package com.example.mesaclick.Services;
import com.example.mesaclick.Model.CampanaCorreo;
import com.example.mesaclick.Model.CampanaPromocion;
import com.example.mesaclick.Model.Promocion;
import com.example.mesaclick.Model.Suscripcion;
import com.example.mesaclick.Repository.CampanaCorreoRepository;
import com.example.mesaclick.Repository.CampanaPromocionRepository;
import com.example.mesaclick.Repository.PromocionRepository;
import com.example.mesaclick.Repository.SuscripcionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
@Service
public class CampanaCorreoService {
    private final RegistroEnvioCampanaService registroEnvioService;
    private final CampanaCorreoRepository campanaRepository;
    private final CampanaPromocionRepository campanaPromocionRepository;
    private final PromocionRepository promocionRepository;
    private final SuscripcionRepository suscripcionRepository;
    private final CorreoService correoService;
    private final PlantillaCampanaCorreoService plantillaCorreoService;
    private final SuscripcionService suscripcionService;
    public CampanaCorreoService(
            RegistroEnvioCampanaService registroEnvioService,
            CampanaCorreoRepository campanaRepository,
            CampanaPromocionRepository campanaPromocionRepository,
            PromocionRepository promocionRepository,
            SuscripcionRepository suscripcionRepository,
            CorreoService correoService,
            PlantillaCampanaCorreoService plantillaCorreoService,
            SuscripcionService suscripcionService
    ) {
        this.registroEnvioService = registroEnvioService;
        this.campanaRepository = campanaRepository;
        this.campanaPromocionRepository = campanaPromocionRepository;
        this.promocionRepository = promocionRepository;
        this.suscripcionRepository = suscripcionRepository;
        this.correoService = correoService;
        this.plantillaCorreoService = plantillaCorreoService;
        this.suscripcionService = suscripcionService;
    }
    // ==========================================
    // CREAR CAMPAÑA
    // ==========================================
    @Transactional
    public CampanaCorreo crear(
            String asunto,
            String titulo,
            String mensaje,
            String nombreEvento,
            String descripcionEvento,
            LocalDateTime fechaEvento,
            List<Long> idsPromociones
    ) {
        if (asunto == null || asunto.isBlank()) {
            throw new IllegalArgumentException(
                    "El asunto del correo es obligatorio."
            );
        }
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException(
                    "El título de la campaña es obligatorio."
            );
        }
        if (asunto.trim().length() > 180
                || titulo.trim().length() > 180) {
            throw new IllegalArgumentException(
                    "El asunto y el título no pueden superar 180 caracteres."
            );
        }
        List<Long> ids = idsPromociones == null
                ? List.of()
                : idsPromociones;
        if (ids.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(
                    "La lista contiene identificadores de promoción inválidos."
            );
        }
        if (ids.stream().distinct().count() != ids.size()) {
            throw new IllegalArgumentException(
                    "No puedes repetir una promoción en la misma campaña."
            );
        }
        if (ids.isEmpty()
                && (nombreEvento == null || nombreEvento.isBlank())) {
            throw new IllegalArgumentException(
                    "Selecciona al menos una promoción o agrega un evento."
            );
        }
        if (nombreEvento != null && !nombreEvento.isBlank()) {
            if (nombreEvento.trim().length() > 180) {
                throw new IllegalArgumentException(
                        "El nombre del evento no puede superar 180 caracteres."
                );
            }
            if (fechaEvento == null) {
                throw new IllegalArgumentException(
                        "Debes indicar la fecha del evento."
                );
            }
        }
        List<Promocion> promociones =
                promocionRepository.findAllById(ids);
        if (promociones.size() != ids.size()) {
            throw new IllegalArgumentException(
                    "Una o varias promociones seleccionadas no existen."
            );
        }
        CampanaCorreo campana = new CampanaCorreo();
        campana.setAsunto(asunto.trim());
        campana.setTitulo(titulo.trim());
        campana.setMensaje(
                mensaje == null ? "" : mensaje.trim()
        );
        campana.setNombreEvento(
                nombreEvento == null || nombreEvento.isBlank()
                        ? null
                        : nombreEvento.trim()
        );
        campana.setDescripcionEvento(
                descripcionEvento == null
                        ? null
                        : descripcionEvento.trim()
        );
        campana.setFechaEvento(
                campana.getNombreEvento() == null
                        ? null
                        : fechaEvento
        );
        campana.setEstado("BORRADOR");
        campana.setCantidadEnviados(0);
        campana.setCantidadFallidos(0);
        CampanaCorreo guardada =
                campanaRepository.save(campana);
        for (Promocion promocion : promociones) {
            CampanaPromocion relacion =
                    new CampanaPromocion(guardada, promocion);
            campanaPromocionRepository.save(relacion);
        }
        return guardada;
    }
    // ==========================================
    // LISTAR CAMPAÑAS
    // ==========================================
    @Transactional(readOnly = true)
    public List<CampanaCorreo> listar() {
        return campanaRepository.findAllByOrderByFechaCreacionDesc();
    }
    // ==========================================
    // BUSCAR CAMPAÑA
    // ==========================================
    @Transactional(readOnly = true)
    public CampanaCorreo buscarPorId(Long idCampana) {
        return campanaRepository.findById(idCampana)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La campaña solicitada no existe."
                        )
                );
    }
    // ==========================================
    // OBTENER IDS DE PROMOCIONES
    // ==========================================
    @Transactional(readOnly = true)
    public List<Long> obtenerIdsPromociones(Long idCampana) {
        buscarPorId(idCampana);
        return campanaPromocionRepository
                .findByCampana_IdCampana(idCampana)
                .stream()
                .map(relacion ->
                        relacion.getPromocion().getIdPromocion()
                )
                .toList();
    }
    // ==========================================
    // OBTENER PROMOCIONES
    // ==========================================
    @Transactional(readOnly = true)
    public List<Promocion> obtenerPromociones(Long idCampana) {
        buscarPorId(idCampana);
        List<Long> idsPromociones =
                obtenerIdsPromociones(idCampana);
        if (idsPromociones.isEmpty()) {
            return List.of();
        }
        return promocionRepository.obtenerPromocionesConProducto(idsPromociones);
    }
    // ==========================================
    // ENVIAR CAMPAÑA
    // ==========================================
    // No agregar @Transactional aquí.
    // Cada registro de envío debe confirmarse en
    // su propia transacción antes de contactar Gmail.
    public synchronized String enviarCampana(Long idCampana) {
        // 1. Validar que exista y esté en borrador.
        CampanaCorreo campana = buscarPorId(idCampana);
        if (!"BORRADOR".equals(campana.getEstado())) {
            throw new IllegalStateException(
                    "Esta campaña ya fue iniciada o enviada."
            );
        }
        // 2. Obtener promociones.
        List<Promocion> promociones =
                obtenerPromociones(idCampana);
        // 3. Consultar suscriptores activos.
        List<Suscripcion> destinatarios =
                suscripcionRepository
                        .findByAceptaPromocionesTrueAndConfirmadaTrueAndFechaCancelacionIsNull();
        if (destinatarios.isEmpty()) {
            throw new IllegalStateException(
                    "No hay suscriptores confirmados para enviar la campaña."
            );
        }
        // 4. Bloquear el envío en MySQL.
        registroEnvioService.iniciarCampana(idCampana);
        // 5. IMPORTANTE:
        // Recuperar una instancia actualizada.
        // No guardar la instancia anterior BORRADOR.
        campana = buscarPorId(idCampana);
        if (!"ENVIANDO".equals(campana.getEstado())) {
            throw new IllegalStateException(
                    "No se pudo confirmar el inicio de la campaña."
            );
        }
        int enviados = 0;
        int inciertos = 0;
        int omitidos = 0;
        int erroresRegistro = 0;
        // 6. Procesar destinatarios.
        for (Suscripcion suscripcion : destinatarios) {
            Long idSuscripcion =
                    suscripcion.getIdSuscripcion();
            boolean autorizado;
            // Registrar intento antes de enviar.
            try {
                autorizado =
                        registroEnvioService.iniciarEnvioDestinatario(
                                idCampana,
                                idSuscripcion
                        );
            } catch (Exception e) {
                erroresRegistro++;
                System.err.println(
                        "No se pudo registrar el envío para el suscriptor #"
                                + idSuscripcion
                                + ": "
                                + e.getMessage()
                );
                // Sin registro confirmado no se
                // permite contactar Gmail.
                continue;
            }
            if (!autorizado) {
                omitidos++;
                continue;
            }
            try {
                // Consultar nuevamente al suscriptor.
                Suscripcion actual =
                        suscripcionRepository
                                .findById(idSuscripcion)
                                .orElse(null);
                if (actual == null
                        || !Boolean.TRUE.equals(
                                actual.getAceptaPromociones()
                        )
                        || !Boolean.TRUE.equals(
                                actual.getConfirmada()
                        )
                        || actual.getFechaCancelacion() != null) {
                    // Ya existe un intento registrado,
                    // pero no se enviará el correo.
                    registroEnvioService.marcarIncierto(
                            idCampana,
                            idSuscripcion,
                            "El suscriptor dejó de estar activo antes del envío."
                    );
                    inciertos++;
                    continue;
                }
                // 7. Crear enlace de cancelación.
                String enlaceCancelacion =
                        suscripcionService
                                .generarEnlaceCancelacion(actual);
                // 8. Construir correo HTML.
                String html =
                        plantillaCorreoService.generar(
                                campana,
                                promociones,
                                enlaceCancelacion
                        );
                // 9. Enviar correo real mediante Gmail.
                correoService.enviarCorreoHtml(
                        actual.getCorreo(),
                        campana.getAsunto(),
                        html
                );
                // 10. Registrar resultado exitoso.
                registroEnvioService.marcarEnviado(
                        idCampana,
                        idSuscripcion
                );
                enviados++;
            } catch (Exception e) {
                // Si Gmail pudo aceptar el correo
                // antes del error, no podemos
                // garantizar que no haya sido enviado.
                inciertos++;
                try {
                    registroEnvioService.marcarIncierto(
                            idCampana,
                            idSuscripcion,
                            e.getMessage()
                    );
                } catch (Exception registroError) {
                    System.err.println(
                            "No se pudo actualizar el registro del suscriptor #"
                                    + idSuscripcion
                                    + ": "
                                    + registroError.getMessage()
                    );
                }
                System.err.println(
                        "Resultado incierto para el suscriptor #"
                                + idSuscripcion
                                + ": "
                                + e.getMessage()
                );
            }
            // 11. Actualizar contadores sin
            // reutilizar una entidad desactualizada.
            actualizarContadores(
                    idCampana,
                    enviados,
                    inciertos
            );
        }
        // 12. Guardar contadores finales.
        actualizarContadores(
                idCampana,
                enviados,
                inciertos
        );
        // 13. Si falló el registro de algún intento,
        // NO declarar la campaña como ENVIADA.
        if (erroresRegistro > 0) {
            throw new IllegalStateException(
                    "La campaña quedó en ENVIANDO porque "
                            + erroresRegistro
                            + " destinatario(s) presentaron errores "
                            + "al registrar el intento. "
                            + "Revisa los registros antes de continuar. "
                            + "Enviados: "
                            + enviados
                            + ". Inciertos: "
                            + inciertos
                            + ". Omitidos: "
                            + omitidos
                            + "."
            );
        }
        // 14. Finalizar la campaña.
        CampanaCorreo campanaFinal =
                buscarPorId(idCampana);
        if (!"ENVIANDO".equals(campanaFinal.getEstado())) {
            throw new IllegalStateException(
                    "El estado de la campaña cambió inesperadamente."
            );
        }
        campanaFinal.setEstado("ENVIADA");
        campanaFinal.setFechaEnvio(
                LocalDateTime.now()
        );
        campanaFinal.setCantidadEnviados(enviados);
        campanaFinal.setCantidadFallidos(inciertos);
        campanaRepository.saveAndFlush(campanaFinal);
        return "Campaña procesada. Enviados: "
                + enviados
                + ". Resultados inciertos: "
                + inciertos
                + ". Omitidos: "
                + omitidos
                + ".";
    }
    // ==========================================
    // ACTUALIZAR CONTADORES
    // ==========================================
    private void actualizarContadores(
            Long idCampana,
            int enviados,
            int inciertos
    ) {
        CampanaCorreo actual =
                buscarPorId(idCampana);
        if (!"ENVIANDO".equals(actual.getEstado())) {
            throw new IllegalStateException(
                    "No se pueden actualizar los contadores "
                            + "porque la campaña no está ENVIANDO."
            );
        }
        actual.setCantidadEnviados(enviados);
        actual.setCantidadFallidos(inciertos);
        campanaRepository.saveAndFlush(actual);
    }
}
