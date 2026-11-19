
package com.example.mesaclick.Controller;

import com.example.mesaclick.Services.EstadisticasService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/estadisticas")
public class EstadisticasController {

    // ==========================================
    // SERVICIO DE ESTADISTICAS
    // ==========================================

    private final EstadisticasService estadisticasService;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public EstadisticasController(
            EstadisticasService estadisticasService
    ) {
        this.estadisticasService = estadisticasService;
    }

    // ==========================================
    // RESUMEN GENERAL DEL DASHBOARD
    // ==========================================

    @GetMapping("/resumen")
    public Map<String, Object> obtenerResumen() {

        return estadisticasService.obtenerResumen();
    }

    // ==========================================
    // VENTAS AGRUPADAS POR FECHA
    // ==========================================

    @GetMapping("/ventas")
    public List<Map<String, Object>> obtenerVentas(
            @RequestParam(defaultValue = "7") int dias
    ) {

        return estadisticasService.obtenerVentasPorDias(dias);
    }

    // ==========================================
    // PRODUCTOS MAS VENDIDOS
    // ==========================================

    @GetMapping("/productos-mas-vendidos")
    public List<Map<String, Object>> obtenerProductosMasVendidos() {

        return estadisticasService.obtenerProductosMasVendidos();
    }
}
