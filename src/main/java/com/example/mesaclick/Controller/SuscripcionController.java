
package com.example.mesaclick.Controller;

import com.example.mesaclick.Services.SuscripcionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/suscripciones")
public class SuscripcionController {

    private final SuscripcionService suscripcionService;

    public SuscripcionController(
            SuscripcionService suscripcionService
    ) {
        this.suscripcionService = suscripcionService;
    }

    public record SuscripcionRequest(
            String correo,
            Boolean aceptaPromociones
    ) {}

    // Registrar y activar la suscripción directamente.
    @PostMapping
    public ResponseEntity<Map<String, String>> registrar(
            @RequestBody SuscripcionRequest request
    ) {

        try {

            suscripcionService.registrar(
                    request.correo(),
                    request.aceptaPromociones()
            );

            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(Map.of(
                            "mensaje",
                            "¡Te has suscrito a MesaClick! Revisa tu correo para conocer nuestras novedades."
                    ));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    Map.of("mensaje", e.getMessage())
            );
        }
    }

    public record CancelacionRequest(
            String token
    ) {}

    // Cancelar la suscripción desde el enlace personal.
    @PostMapping("/cancelar")
    public ResponseEntity<Map<String, String>> cancelar(
            @RequestBody CancelacionRequest request
    ) {

        boolean cancelada =
                suscripcionService.cancelar(
                        request.token()
                );

        if (!cancelada) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "mensaje",
                            "El enlace de cancelación no es válido."
                    )
            );
        }

        return ResponseEntity.ok(
                Map.of(
                        "mensaje",
                        "Tu suscripción a MesaClick fue cancelada correctamente."
                )
        );
    }
}
