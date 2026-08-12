package com.example.mesaclick.Controller;

import com.example.mesaclick.Dto.PedidoRequest;
import com.example.mesaclick.Model.Pedido;
import com.example.mesaclick.Services.PedidoService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:5173")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public Pedido recibirPedido(@RequestBody PedidoRequest pedidoRequest) {

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
    // HISTORIAL DE PEDIDOS DE UNA MESA
    // ==========================================

    @GetMapping("/mesa/{idMesa}")
    public List<Pedido> obtenerPedidosPorMesa(
            @PathVariable Long idMesa
    ) {

        return pedidoService.obtenerPedidosPorMesa(idMesa);

    }

}