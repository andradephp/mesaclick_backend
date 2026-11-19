
package com.example.mesaclick.Controller;

import com.example.mesaclick.Dto.PedidoRequest;
import com.example.mesaclick.Dto.PedidoMeseroDTO;
import com.example.mesaclick.Model.Pedido;
import com.example.mesaclick.Services.PedidoService;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://mesaclick.tech",
        "https://www.mesaclick.tech"
})
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // ==========================================
    // CREAR PEDIDO DESDE EL QR
    // ==========================================

    @PostMapping
    public Pedido recibirPedido(
            @RequestBody PedidoRequest pedidoRequest
    ) {

        System.out.println("=================================");
        System.out.println("PEDIDO RECIBIDO");
        System.out.println("Mesa: " + pedidoRequest.getIdMesa());

        Pedido pedido = pedidoService.crearPedido(pedidoRequest);

        System.out.println("Pedido guardado correctamente");
        System.out.println("ID Pedido: " + pedido.getIdPedido());
        System.out.println("Total: " + pedido.getTotalPedido());
        System.out.println("Estado: " + pedido.getEstadoPedido());
        System.out.println("=================================");

        return pedido;
    }

    // ==========================================
    // PEDIDOS DE LA ATENCION ACTIVA
    // ==========================================

    @GetMapping("/mesa/{idMesa}")
    public List<Pedido> obtenerPedidosPorMesa(
            @PathVariable Long idMesa
    ) {
        return pedidoService.obtenerPedidosPorMesa(idMesa);
    }

    // ==========================================
    // CONSULTAR TODOS LOS PEDIDOS
    // ==========================================

    @GetMapping
    public List<Pedido> obtenerTodosLosPedidos() {
        return pedidoService.obtenerTodosLosPedidos();
    }

    // ==========================================
    // COCINA - MARCAR PEDIDO COMO LISTO
    // ==========================================

    @PatchMapping("/{idPedido}/listo")
    public Pedido marcarComoListo(
            @PathVariable Long idPedido,
            Authentication autenticacion
    ) {

        if (autenticacion == null ||
                autenticacion.getAuthorities().stream()
                        .noneMatch(a ->
                                a.getAuthority().equals("ROLE_COCINA")
                        )) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo cocina puede marcar pedidos como listos"
            );
        }

        return pedidoService.marcarComoListo(idPedido);
    }

    // ==========================================
    // MESERO - MARCAR PEDIDO COMO ENTREGADO
    // ==========================================

    @PatchMapping("/{idPedido}/entregado")
    public Pedido marcarComoEntregado(
            @PathVariable Long idPedido,
            Authentication autenticacion
    ) {

        if (autenticacion == null ||
                autenticacion.getAuthorities().stream()
                        .noneMatch(a ->
                                a.getAuthority().equals("ROLE_MESERO")
                        )) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo el mesero puede marcar pedidos como entregados"
            );
        }

        return pedidoService.marcarComoEntregado(idPedido);
    }

    // ==========================================
    // PEDIDOS PARA EL MESERO
    // ==========================================

    @GetMapping("/mesero")
    public List<PedidoMeseroDTO> obtenerPedidosParaMesero() {
        return pedidoService.obtenerPedidosParaMesero();
    }

    // ==========================================
    // CONSULTA TEMPORAL DEL CAJERO
    // ==========================================

    @GetMapping("/cajero/activos")
    public List<PedidoMeseroDTO> obtenerPedidosParaCajero(
            Authentication autenticacion
    ) {

        if (autenticacion == null ||
                autenticacion.getAuthorities().stream()
                        .noneMatch(a ->
                                a.getAuthority().equals("ROLE_CAJERO") ||
                                a.getAuthority().equals("ROLE_ADMIN")
                        )) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Acceso denegado: Se requiere rol de cajero"
            );
        }

        return pedidoService.obtenerPedidosParaCajero();
    }
}
