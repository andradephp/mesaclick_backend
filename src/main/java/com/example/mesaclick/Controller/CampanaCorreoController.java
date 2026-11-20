
package com.example.mesaclick.Controller;

import com.example.mesaclick.Model.CampanaCorreo;
import com.example.mesaclick.Services.CampanaCorreoService;

import com.example.mesaclick.Services.PlantillaCampanaCorreoService;
import org.springframework.http.MediaType;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/campanas")
public class CampanaCorreoController {

    private final CampanaCorreoService campanaService;
    private final PlantillaCampanaCorreoService plantillaService;

    
public CampanaCorreoController(
        CampanaCorreoService campanaService,
        PlantillaCampanaCorreoService plantillaService
) {
    this.campanaService = campanaService;
    this.plantillaService = plantillaService;
}


    // Datos que recibimos desde React.
    public record CrearCampanaRequest(
            String asunto,
            String titulo,
            String mensaje,
            String nombreEvento,
            String descripcionEvento,
            LocalDateTime fechaEvento,
            List<Long> idsPromociones
    ) {}

    // Respuesta que enviamos al frontend.
    public record CampanaResponse(
            Long idCampana,
            String asunto,
            String titulo,
            String mensaje,
            String nombreEvento,
            String descripcionEvento,
            LocalDateTime fechaEvento,
            String estado,
            LocalDateTime fechaCreacion,
            LocalDateTime fechaEnvio,
            Integer cantidadEnviados,
            Integer cantidadFallidos,
            List<Long> idsPromociones
    ) {}

    private CampanaResponse convertir(
            CampanaCorreo campana
    ) {
        return new CampanaResponse(
                campana.getIdCampana(),
                campana.getAsunto(),
                campana.getTitulo(),
                campana.getMensaje(),
                campana.getNombreEvento(),
                campana.getDescripcionEvento(),
                campana.getFechaEvento(),
                campana.getEstado(),
                campana.getFechaCreacion(),
                campana.getFechaEnvio(),
                campana.getCantidadEnviados(),
                campana.getCantidadFallidos(),
                campanaService.obtenerIdsPromociones(
                        campana.getIdCampana()
                )
        );
    }

    @PostMapping
    public ResponseEntity<?> crear(
            @RequestBody CrearCampanaRequest request
    ) {
        try {
            CampanaCorreo campana = campanaService.crear(
                    request.asunto(),
                    request.titulo(),
                    request.mensaje(),
                    request.nombreEvento(),
                    request.descripcionEvento(),
                    request.fechaEvento(),
                    request.idsPromociones()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(convertir(campana));

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<CampanaResponse>> listar() {
        List<CampanaResponse> campanas = campanaService
                .listar()
                .stream()
                .map(this::convertir)
                .toList();

        return ResponseEntity.ok(campanas);
    }

    @GetMapping("/{idCampana}")
    public ResponseEntity<?> buscarPorId(
            @PathVariable Long idCampana
    ) {
        try {
            CampanaCorreo campana =
                    campanaService.buscarPorId(idCampana);

            return ResponseEntity.ok(convertir(campana));

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }


@GetMapping(
        value = "/{idCampana}/vista-previa",
        produces = MediaType.TEXT_HTML_VALUE
)
public ResponseEntity<String> vistaPrevia(
        @PathVariable Long idCampana
) {
    try {
        CampanaCorreo campana =
                campanaService.buscarPorId(idCampana);

        String enlaceEjemplo =
                "https://mesaclick.tech/cancelar-suscripcion"
                + "?token=VISTA_PREVIA_NO_VALIDO";

        String html = plantillaService.generar(
                campana,
                campanaService.obtenerPromociones(idCampana),
                enlaceEjemplo
        );

        return ResponseEntity.ok()
                .contentType(
                        new MediaType("text", "html",
                                java.nio.charset.StandardCharsets.UTF_8)
                )
                .body(html);

    } catch (IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.TEXT_PLAIN)
                .body(e.getMessage());
    }
}

@PostMapping("/{id}/enviar")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> enviarCampana(@PathVariable Long id) {
    try {
        String resultado = campanaService.enviarCampana(id);

        return ResponseEntity.ok(
                java.util.Map.of("mensaje", resultado)
        );

    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(
                java.util.Map.of("mensaje", e.getMessage())
        );

    } catch (IllegalStateException e) {
        return ResponseEntity.status(409).body(
                java.util.Map.of("mensaje", e.getMessage())
        );
    }
}

}
