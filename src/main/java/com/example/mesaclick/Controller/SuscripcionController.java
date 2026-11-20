
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
                    "Solicitud recibida. Revisa tu correo para confirmar la suscripción."
                ));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                Map.of("mensaje", e.getMessage())
            );
        }
    }

    @GetMapping("/confirmar")
    public ResponseEntity<String> confirmar(
            @RequestParam String token
    ) {

        boolean confirmado =
            suscripcionService.confirmar(token);

        if (!confirmado) {
            return ResponseEntity.badRequest().body(
                "El enlace no es válido, ya fue utilizado o ha vencido."
            );
        }

        return ResponseEntity.ok(
            "¡Tu suscripción a MesaClick fue confirmada correctamente!"
        );
    }


public record CancelacionRequest(String token) {}

@PostMapping("/cancelar")
public ResponseEntity<Map<String, String>> cancelar(
        @RequestBody CancelacionRequest request
) {

    boolean cancelada =
        suscripcionService.cancelar(request.token());

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
