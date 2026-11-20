
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
        TransactionTemplate transactionTemplate
) {
    this.repository = repository;
    this.tokenCancelacionRepository = tokenCancelacionRepository;
    this.correoService = correoService;
    this.tokenService = tokenService;
    this.transactionTemplate = transactionTemplate;
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

        String token = tokenService.generarToken();
        String hash = tokenService.generarHash(token);

        // Guardar primero y confirmar la transacción
        // antes de intentar enviar el correo.
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

                    // Ya está suscrito y confirmado.
                    if (Boolean.TRUE.equals(
                            suscripcion.getConfirmada())
                            && Boolean.TRUE.equals(
                                suscripcion.getAceptaPromociones())
                            && suscripcion.getFechaCancelacion() == null) {
                        return false;
                    }

                    // Evitar reenvíos frecuentes.
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

                suscripcion.setAceptaPromociones(true);
                suscripcion.setConfirmada(false);
                suscripcion.setFechaConsentimiento(ahora);
                suscripcion.setFechaConfirmacion(null);
                suscripcion.setFechaCancelacion(null);

                suscripcion.setTokenConfirmacionHash(hash);
                suscripcion.setTokenExpiracion(
                    tokenService.generarExpiracion()
                );

                suscripcion.setFechaEnvioConfirmacion(ahora);

                repository.save(suscripcion);

                return true;
            })
        );

        if (!debeEnviar) {
            return;
        }

        String enlace = urlPublica
                + "/api/suscripciones/confirmar?token="
                + token;

        String contenido = """
                ¡Hola!

                Gracias por registrarte para recibir
                promociones y novedades de MesaClick.

                Para confirmar tu suscripción,
                abre el siguiente enlace:

                %s

                Este enlace vence en 24 horas.

                Si no solicitaste esta suscripción,
                puedes ignorar este mensaje.

                Equipo MesaClick
                """.formatted(enlace);

        try {
            correoService.enviarCorreo(
                correoNormalizado,
                "MesaClick - Confirma tu suscripción",
                contenido
            );
        } catch (org.springframework.mail.MailException e) {
            // No revelar información del correo al cliente.
            // El token sigue vigente y podrá solicitarse
            // otro enlace después del tiempo de espera.
            System.err.println(
                "No se pudo enviar el correo de confirmación: "
                + e.getClass().getSimpleName()
            );
        }
    }

    
public boolean confirmar(String token) {

    if (token == null
            || !token.matches("[A-Za-z0-9_-]{43}")) {
        return false;
    }

    String hash = tokenService.generarHash(token);

    String tokenCancelacion = tokenService.generarToken();
    String hashCancelacion =
            tokenService.generarHash(tokenCancelacion);

    String correoConfirmado = transactionTemplate.execute(
        status -> {

            Optional<Suscripcion> resultado =
                repository.findByTokenConfirmacionHash(hash);

            if (resultado.isEmpty()) {
                return null;
            }

            Suscripcion suscripcion = resultado.get();

            if (suscripcion.getTokenExpiracion() == null
                    || !suscripcion.getTokenExpiracion()
                        .isAfter(LocalDateTime.now())) {
                return null;
            }

            suscripcion.setConfirmada(true);
            suscripcion.setFechaConfirmacion(
                LocalDateTime.now()
            );

            // El enlace de confirmación ya no se puede reutilizar.
            suscripcion.setTokenConfirmacionHash(null);
            suscripcion.setTokenExpiracion(null);

            // Generar el enlace personal de cancelación.
            suscripcion.setTokenCancelacionHash(
                hashCancelacion
            );

            repository.save(suscripcion);

            return suscripcion.getCorreo();
        }
    );

    if (correoConfirmado == null) {
        return false;
    }

    String enlaceCancelacion = frontendUrl
        + "/cancelar-suscripcion?token="
        + tokenCancelacion;

    String contenido = """
            ¡Hola!

            Tu suscripción a las promociones
            de MesaClick fue confirmada correctamente.

            A partir de ahora podrás recibir
            promociones y novedades.

            Si deseas dejar de recibirlas,
            utiliza este enlace:

            %s

            Equipo MesaClick
            """.formatted(enlaceCancelacion);

    try {
        correoService.enviarCorreo(
            correoConfirmado,
            "MesaClick - Suscripción confirmada",
            contenido
        );
    } catch (org.springframework.mail.MailException e) {
        System.err.println(
            "No se pudo enviar el enlace de cancelación: "
            + e.getClass().getSimpleName()
        );
    }

    return true;
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
