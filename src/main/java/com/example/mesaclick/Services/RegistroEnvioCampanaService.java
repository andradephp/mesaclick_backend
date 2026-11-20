
package com.example.mesaclick.Services;

import com.example.mesaclick.Model.CampanaCorreo;
import com.example.mesaclick.Model.CampanaEnvio;
import com.example.mesaclick.Model.Suscripcion;

import com.example.mesaclick.Repository.CampanaCorreoRepository;
import com.example.mesaclick.Repository.CampanaEnvioRepository;
import com.example.mesaclick.Repository.SuscripcionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RegistroEnvioCampanaService {

    private final CampanaCorreoRepository campanaRepository;
    private final CampanaEnvioRepository envioRepository;
    private final SuscripcionRepository suscripcionRepository;

    public RegistroEnvioCampanaService(
            CampanaCorreoRepository campanaRepository,
            CampanaEnvioRepository envioRepository,
            SuscripcionRepository suscripcionRepository
    ) {
        this.campanaRepository = campanaRepository;
        this.envioRepository = envioRepository;
        this.suscripcionRepository = suscripcionRepository;
    }

    @Transactional
    public void iniciarCampana(Long idCampana) {
        int actualizadas = campanaRepository.iniciarEnvio(idCampana);

        if (actualizadas != 1) {
            throw new IllegalStateException(
                    "La campaña ya fue iniciada o enviada."
            );
        }
    }

    @Transactional
    public boolean iniciarEnvioDestinatario(
            Long idCampana,
            Long idSuscripcion
    ) {
        // Si ya existe un registro, nunca repetimos el intento.
        if (envioRepository
                .existsByCampana_IdCampanaAndSuscripcion_IdSuscripcion(
                        idCampana,
                        idSuscripcion
                )) {
            return false;
        }

        Suscripcion suscripcion = suscripcionRepository
                .findById(idSuscripcion)
                .orElse(null);

        if (suscripcion == null
                || !Boolean.TRUE.equals(suscripcion.getAceptaPromociones())
                || !Boolean.TRUE.equals(suscripcion.getConfirmada())
                || suscripcion.getFechaCancelacion() != null) {
            return false;
        }

        CampanaCorreo campana = campanaRepository
                .findById(idCampana)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La campaña no existe."
                        )
                );

        if (!"ENVIANDO".equals(campana.getEstado())) {
            throw new IllegalStateException(
                    "La campaña no está en proceso de envío."
            );
        }

        CampanaEnvio envio = new CampanaEnvio();
        envio.setCampana(campana);
        envio.setSuscripcion(suscripcion);
        envio.setEstado("ENVIANDO");
        envio.setFechaIntento(LocalDateTime.now());

        envioRepository.saveAndFlush(envio);

        return true;
    }

    @Transactional
    public void marcarEnviado(
            Long idCampana,
            Long idSuscripcion
    ) {
        CampanaEnvio envio = buscarEnvio(
                idCampana,
                idSuscripcion
        );

        envio.setEstado("ENVIADO");
        envio.setFechaEnvio(LocalDateTime.now());
        envio.setDetalleError(null);
    }

    @Transactional
    public void marcarIncierto(
            Long idCampana,
            Long idSuscripcion,
            String detalle
    ) {
        CampanaEnvio envio = buscarEnvio(
                idCampana,
                idSuscripcion
        );

        envio.setEstado("INCIERTO");
        envio.setDetalleError(
                detalle == null
                        ? "No se pudo confirmar el resultado del envío."
                        : detalle.substring(0, Math.min(500, detalle.length()))
        );
    }

    private CampanaEnvio buscarEnvio(
            Long idCampana,
            Long idSuscripcion
    ) {
        return envioRepository
                .findByCampana_IdCampanaAndSuscripcion_IdSuscripcion(
                        idCampana,
                        idSuscripcion
                )
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No existe el registro del envío."
                        )
                );
    }
}
