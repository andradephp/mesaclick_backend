
package com.example.mesaclick.Services;

import com.example.mesaclick.Model.Suscripcion;
import com.example.mesaclick.Repository.SuscripcionRepository;
import com.example.mesaclick.Model.TokenCancelacionCorreo;
import com.example.mesaclick.Repository.TokenCancelacionCorreoRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

@Service
public class SuscripcionService {

    private final SuscripcionRepository repository;
    private final TokenCancelacionCorreoRepository tokenCancelacionRepository;
    private final CorreoService correoService;
    private final PlantillaBienvenidaCorreoService plantillaBienvenida;
    private final TokenSuscripcionService tokenService;
    private final TransactionTemplate transactionTemplate;

    @Value("${mesaclick.url-publica:http://localhost:8080}")
    private String urlPublica;

    @Value("${mesaclick.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    
public SuscripcionService(
        SuscripcionRepository repository,
        TokenCancelacionCorreoRepository tokenCancelacionRepository,
        CorreoService correoService,
        TokenSuscripcionService tokenService,
        TransactionTemplate transactionTemplate,
        PlantillaBienvenidaCorreoService plantillaBienvenida
) {
    this.repository = repository;
    this.tokenCancelacionRepository = tokenCancelacionRepository;
    this.correoService = correoService;
    this.tokenService = tokenService;
    this.transactionTemplate = transactionTemplate;
    this.plantillaBienvenida = plantillaBienvenida;
}


    
public void registrar(
        String correo,
        Boolean aceptaPromociones
) {

    if (correo == null
            || correo.trim().length() > 254
            || !correo.trim().matches(
                "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"
            )) {
        throw new IllegalArgumentException(
            "Ingresa un correo electrónico válido."
        );
    }

    if (!Boolean.TRUE.equals(aceptaPromociones)) {
        throw new IllegalArgumentException(
            "Debes autorizar el envío de promociones."
        );
    }

    String correoNormalizado = correo.trim()
            .toLowerCase(Locale.ROOT);

    // Generar un enlace personal de cancelación.
    String tokenCancelacion = tokenService.generarToken();
    String hashCancelacion =
            tokenService.generarHash(tokenCancelacion);

    // Registrar y activar la suscripción antes de enviar.
    boolean debeEnviar = Boolean.TRUE.equals(
        transactionTemplate.execute(status -> {

            LocalDateTime ahora = LocalDateTime.now();

            Optional<Suscripcion> existente =
                    repository.findByCorreoIgnoreCase(
                        correoNormalizado
                    );

            Suscripcion suscripcion;

            if (existente.isPresent()) {

                suscripcion = existente.get();

                // No enviar otro correo a un suscriptor activo.
                if (Boolean.TRUE.equals(
                        suscripcion.getConfirmada())
                        && Boolean.TRUE.equals(
                            suscripcion.getAceptaPromociones())
                        && suscripcion.getFechaCancelacion() == null) {
                    return false;
                }

                // Evitar reactivaciones y correos repetidos
                // durante los siguientes 5 minutos.
                LocalDateTime ultimoEnvio =
                        suscripcion.getFechaEnvioConfirmacion();

                if (ultimoEnvio != null
                        && ultimoEnvio.isAfter(
                            ahora.minusMinutes(5)
                        )) {
                    return false;
                }

            } else {

                suscripcion = new Suscripcion();
                suscripcion.setCorreo(correoNormalizado);
                suscripcion.setFechaRegistro(ahora);
            }

            // Activar inmediatamente la suscripción.
            suscripcion.setAceptaPromociones(true);
            suscripcion.setConfirmada(true);

            suscripcion.setFechaConsentimiento(ahora);
            suscripcion.setFechaConfirmacion(ahora);
            suscripcion.setFechaCancelacion(null);

            // Ya no necesitamos un token de confirmación.
            suscripcion.setTokenConfirmacionHash(null);
            suscripcion.setTokenExpiracion(null);

            // Token para cancelar la suscripción.
            suscripcion.setTokenCancelacionHash(
                hashCancelacion
            );

            // Conservamos este campo para controlar
            // los reenvíos del correo de bienvenida.
            suscripcion.setFechaEnvioConfirmacion(ahora);

            repository.save(suscripcion);

            return true;
        })
    );

    if (!debeEnviar) {
        return;
    }

    // Crear el enlace personal de cancelación.
    String enlaceCancelacion = frontendUrl
            .replaceAll("/+$", "")
            + "/cancelar-suscripcion?token="
            + tokenCancelacion;

    // Generar el correo HTML de bienvenida.
    String contenidoHtml = plantillaBienvenida.generar(
        enlaceCancelacion
    );

    try {

        correoService.enviarCorreoHtml(
            correoNormalizado,
            "¡Bienvenido a MesaClick! 🎉",
            contenidoHtml
        );

    } catch (org.springframework.mail.MailException e) {

        // La suscripción permanece activa aunque
        // falle el envío del correo.
        System.err.println(
            "No se pudo enviar el correo de bienvenida: "
            + e.getClass().getSimpleName()
        );

    } catch (IllegalStateException e) {

        System.err.println(
            "No se pudo preparar el correo de bienvenida: "
            + e.getClass().getSimpleName()
        );
    }
}


public String generarEnlaceCancelacion(Suscripcion suscripcion) {

    if (suscripcion == null ||
            suscripcion.getIdSuscripcion() == null) {
        throw new IllegalArgumentException(
                "La suscripción no es válida."
        );
    }

    String token = tokenService.generarToken();
    String hash = tokenService.generarHash(token);

    transactionTemplate.executeWithoutResult(status -> {

        Suscripcion actual = repository
                .findById(suscripcion.getIdSuscripcion())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La suscripción no existe."
                        )
                );

        if (!Boolean.TRUE.equals(actual.getConfirmada()) ||
                !Boolean.TRUE.equals(actual.getAceptaPromociones()) ||
                actual.getFechaCancelacion() != null) {
            throw new IllegalArgumentException(
                    "La suscripción no está activa."
            );
        }

        TokenCancelacionCorreo registro =
                new TokenCancelacionCorreo();

        registro.setSuscripcion(actual);
        registro.setTokenHash(hash);

        tokenCancelacionRepository.save(registro);
    });

    return frontendUrl
            + "/cancelar-suscripcion?token="
            + token;
}



public boolean cancelar(String token) {

    if (token == null ||
            !token.matches("[A-Za-z0-9_-]{43}")) {
        return false;
    }

    String hash = tokenService.generarHash(token);

    return Boolean.TRUE.equals(
            transactionTemplate.execute(status -> {

                // Primero buscar los enlaces originales.
                Optional<Suscripcion> original =
                        repository.findByTokenCancelacionHash(hash);

                Suscripcion suscripcion;

                if (original.isPresent()) {

                    suscripcion = original.get();

                } else {

                    // Buscar enlaces generados para campañas.
                    Optional<TokenCancelacionCorreo> nuevo =
                            tokenCancelacionRepository.findByTokenHash(hash);

                    if (nuevo.isEmpty()) {
                        return false;
                    }

                    suscripcion = nuevo.get().getSuscripcion();
                }

                // Desactivar la suscripción.
                suscripcion.setAceptaPromociones(false);
                suscripcion.setConfirmada(false);
                suscripcion.setFechaCancelacion(
                        LocalDateTime.now()
                );

                // Invalidar el enlace original.
                suscripcion.setTokenCancelacionHash(null);
                suscripcion.setTokenConfirmacionHash(null);
                suscripcion.setTokenExpiracion(null);

                repository.save(suscripcion);

                // Invalidar todos los enlaces de campañas.
                tokenCancelacionRepository
                        .deleteBySuscripcion_IdSuscripcion(
                                suscripcion.getIdSuscripcion()
                        );

                return true;
            })
    );
}



}
