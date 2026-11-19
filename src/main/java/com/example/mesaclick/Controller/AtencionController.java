
package com.example.mesaclick.Controller;

import com.example.mesaclick.Dto.AtencionCajaDTO;
import com.example.mesaclick.Services.AtencionService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/atenciones")
public class AtencionController {

    private final AtencionService atencionService;

    public AtencionController(
            AtencionService atencionService
    ) {
        this.atencionService = atencionService;
    }

    // ==========================================
    // CONSULTAR TODAS LAS CUENTAS ACTIVAS
    // ==========================================

    @GetMapping("/activas")
    public List<AtencionCajaDTO> obtenerActivas() {

        return atencionService.obtenerAtencionesActivas()
                .stream()
                .map(atencion ->
                        atencionService.obtenerCuenta(
                                atencion.getIdAtencion()
                        )
                )
                .toList();
    }

    // ==========================================
    // BUSCAR CUENTA POR NUMERO DE MESA
    // ==========================================

    @GetMapping("/mesa/{numero}")
    public AtencionCajaDTO buscarPorMesa(
            @PathVariable int numero
    ) {

        return atencionService.obtenerAtencionesActivas()
                .stream()
                .filter(atencion ->
                        atencion.getMesa().getNumero_mesa() == numero
                )
                .findFirst()
                .map(atencion ->
                        atencionService.obtenerCuenta(
                                atencion.getIdAtencion()
                        )
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No hay una cuenta activa para esta mesa"
                        )
                );
    }

    // ==========================================
    // BUSCAR CUENTA POR CODIGO QR
    // ==========================================

    @GetMapping("/codigo/{codigo}")
    public AtencionCajaDTO buscarPorCodigo(
            @PathVariable String codigo
    ) {

        return atencionService.obtenerAtencionesActivas()
                .stream()
                .filter(atencion ->
                        codigo.equals(
                                atencion.getMesa().getCodigoQrMesa()
                        )
                )
                .findFirst()
                .map(atencion ->
                        atencionService.obtenerCuenta(
                                atencion.getIdAtencion()
                        )
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No hay una cuenta activa para este código"
                        )
                );
    }

    // ==========================================
    // CONSULTAR UNA CUENTA COMPLETA
    // ==========================================

    @GetMapping("/{idAtencion}")
    public AtencionCajaDTO obtenerCuenta(
            @PathVariable Long idAtencion
    ) {
        return atencionService.obtenerCuenta(idAtencion);
    }

    // ==========================================
    // REGISTRAR PAGO Y CERRAR ATENCION
    // ==========================================

    @PutMapping("/{idAtencion}/pagar")
    public AtencionCajaDTO pagar(
            @PathVariable Long idAtencion,
            @RequestBody Map<String, String> datos
    ) {

        atencionService.registrarPago(
                idAtencion,
                datos.get("metodoPago")
        );

        return atencionService.obtenerCuenta(idAtencion);
    }
}
