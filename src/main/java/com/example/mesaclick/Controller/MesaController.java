package com.example.mesaclick.Controller;

import com.example.mesaclick.Dto.PedidoMeseroDTO;
import com.example.mesaclick.Model.Mesa;
import com.example.mesaclick.Services.MesaService;
import com.example.mesaclick.Services.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = {
    "http://localhost:5173",
    "https://mesaclick.tech",
    "https://www.mesaclick.tech"
})
public class MesaController {

    private final MesaService mesaService;
    private final PedidoService pedidoService;

    public MesaController(MesaService mesaService, PedidoService pedidoService) {
        this.mesaService = mesaService;
        this.pedidoService = pedidoService;
    }

    @GetMapping("/api/mesas")
    public List<Mesa> obtenerMesas() {
        return mesaService.obtenerTodasLasMesas();
    }

    @GetMapping("/api/mesas/codigo/{codigo}")
    public Mesa obtenerMesaPorCodigo(@PathVariable String codigo) {
        return mesaService.obtenerMesaPorCodigo(codigo);
    }

    // ==========================================
    // ENDPOINTS REQUERIDOS POR EL FRONTEND
    // ==========================================

    @GetMapping("/api/mesas/{codigo}/estado")
    public ResponseEntity<Map<String, String>> obtenerEstadoMesa(@PathVariable String codigo) {
        String estadoActual = mesaService.obtenerEstadoPorCodigo(codigo);
        if (estadoActual == null || estadoActual.isEmpty()) {
            estadoActual = "ENVIADO"; // Estado por defecto
        }

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("estado", estadoActual);
        return ResponseEntity.ok(respuesta);
    }

    
@GetMapping("/api/mesas/{codigo}/pedidos-activos")
public List<PedidoMeseroDTO> obtenerPedidosActivosPorMesa(
        @PathVariable String codigo
) {
    Mesa mesa = mesaService.obtenerMesaPorCodigo(codigo);

    if (mesa != null) {
        return pedidoService.obtenerPedidosPorMesa(
                mesa.getId_mesa()
        );
    }

    return List.of();
}

}