package com.example.mesaclick.Services;

import com.example.mesaclick.Dto.PedidoRequest;
import com.example.mesaclick.Dto.PersonaRequest;
import com.example.mesaclick.Dto.ProductoRequest;

import com.example.mesaclick.Model.Mesa;
import com.example.mesaclick.Model.Pedido;
import com.example.mesaclick.Model.PedidoPersona;
import com.example.mesaclick.Model.DetallePedido;
import com.example.mesaclick.Model.Producto;

import com.example.mesaclick.Repository.MesaRepository;
import com.example.mesaclick.Repository.PedidoRepository;
import com.example.mesaclick.Repository.PedidoPersonaRepository;
import com.example.mesaclick.Repository.DetallePedidoRepository;
import com.example.mesaclick.Repository.ProductoRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import com.example.mesaclick.Dto.PedidoMeseroDTO;
import com.example.mesaclick.Dto.PersonaPedidoDTO;
import com.example.mesaclick.Dto.ProductoPedidoDTO;
import java.util.ArrayList;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;
    private final PedidoPersonaRepository pedidoPersonaRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            MesaRepository mesaRepository,
            PedidoPersonaRepository pedidoPersonaRepository,
            DetallePedidoRepository detallePedidoRepository,
            ProductoRepository productoRepository
    ) {
        this.pedidoRepository = pedidoRepository;
        this.mesaRepository = mesaRepository;
        this.pedidoPersonaRepository = pedidoPersonaRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoRepository = productoRepository;
    }

    public Pedido crearPedido(PedidoRequest request) {
        

        // ==========================================
        // 1. BUSCAR LA MESA
        // ==========================================

        Mesa mesa = mesaRepository.findById(request.getIdMesa())
                .orElseThrow(() ->
                        new RuntimeException("La mesa no existe")
                );


        // ==========================================
        // 2. CREAR EL PEDIDO
        // ==========================================

        Pedido pedido = new Pedido();

        pedido.setMesa(mesa);

        pedido.setEstadoPedido("EN_PREPARACION");

        pedido.setFechaPedido(LocalDateTime.now());

        // Todavía no sabemos el total.
        // Lo calcularemos más adelante.

        pedido.setTotalPedido(0.0);


        // Guardamos primero el pedido
        // para obtener su ID.

        pedido = pedidoRepository.save(pedido);


        // ==========================================
        // 3. VARIABLE PARA EL TOTAL GENERAL
        // ==========================================

        double totalGeneral = 0.0;


        // ==========================================
        // 4. RECORRER LAS PERSONAS
        // ==========================================

        for (PersonaRequest personaRequest : request.getPersonas()) {

            PedidoPersona pedidoPersona = new PedidoPersona();

            pedidoPersona.setPedido(pedido);

            pedidoPersona.setNombrePersona(
                    personaRequest.getNombre()
            );

            pedidoPersona.setSubtotal(0.0);
            pedidoPersona = pedidoPersonaRepository.save(pedidoPersona);

            double subtotalPersona = 0.0;


            // ======================================
            // 5. RECORRER PRODUCTOS DE LA PERSONA
            // ======================================

            for (ProductoRequest productoRequest :
                    personaRequest.getProductos()) {

                // Buscar producto real en MySQL

                Producto producto =
                        productoRepository.findById(
                                productoRequest.getIdProducto()
                        ).orElseThrow(() ->
                                new RuntimeException(
                                        "El producto no existe: "
                                                + productoRequest.getIdProducto()
                                )
                        );


                // ==================================
                // 6. CALCULAR SUBTOTAL DEL PRODUCTO
                // ==================================

                double subtotalProducto =
                        producto.getPrecioProducto()
                                * productoRequest.getCantidad();


                subtotalPersona += subtotalProducto;


                // ==================================
                // 7. CREAR DETALLE DEL PEDIDO
                // ==================================

                DetallePedido detalle = new DetallePedido();

                detalle.setPedidoPersona(pedidoPersona);

                detalle.setProducto(producto);

                detalle.setCantidad(
                        productoRequest.getCantidad()
                );

                detalle.setPrecioUnitario(
                        producto.getPrecioProducto()
                );


                // Guardar detalle

                detallePedidoRepository.save(detalle);
            }


            // ======================================
            // 8. GUARDAR SUBTOTAL DE LA PERSONA
            // ======================================

            pedidoPersona.setSubtotal(subtotalPersona);

            pedidoPersonaRepository.save(pedidoPersona);


            // ======================================
            // 9. SUMAR AL TOTAL GENERAL
            // ======================================

            totalGeneral += subtotalPersona;
        }


        // ==========================================
        // 10. ACTUALIZAR TOTAL DEL PEDIDO
        // ==========================================

        pedido.setTotalPedido(totalGeneral);

        pedido = pedidoRepository.save(pedido);


        // ==========================================
        // 11. DEVOLVER PEDIDO
        // ==========================================

        return pedido;
    }

    // ==========================================
    // OBTENER HISTORIAL DE UNA MESA
    // ==========================================

    public List<Pedido> obtenerPedidosPorMesa(Long idMesa) {

    return pedidoRepository.buscarPedidosPorMesa(idMesa);

}

public List<Pedido> obtenerTodosLosPedidos() {
    return pedidoRepository.findAllByOrderByFechaPedidoDesc();
}

public Pedido marcarComoListo(Long idPedido) {
    Pedido pedido = pedidoRepository.findById(idPedido)
            .orElseThrow(() ->
                    new RuntimeException("El pedido no existe"));

    if (!"EN_PREPARACION".equals(pedido.getEstadoPedido())) {
        throw new RuntimeException(
                "Solo se pueden marcar como listos los pedidos en preparación");
    }

    pedido.setEstadoPedido("LISTO");
    return pedidoRepository.save(pedido);
}


public Pedido marcarComoEntregado(Long idPedido) {
    Pedido pedido = pedidoRepository.findById(idPedido)
            .orElseThrow(() ->
                    new org.springframework.web.server.ResponseStatusException(
                            org.springframework.http.HttpStatus.NOT_FOUND,
                            "El pedido no existe"
                    ));

    // El mesero puede entregar pedidos pendientes o listos.
    String estado = pedido.getEstadoPedido();

    if ("ENTREGADO".equals(estado)) {
        throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.CONFLICT,
                "Este pedido ya fue entregado"
        );
    }

    pedido.setEstadoPedido("ENTREGADO");

    return pedidoRepository.save(pedido);
}

public List<PedidoMeseroDTO> obtenerPedidosParaMesero() {

    List<Pedido> pedidos = pedidoRepository.findAllByOrderByFechaPedidoDesc();

    List<PedidoMeseroDTO> resultado = new ArrayList<>();

    for (Pedido pedido : pedidos) {

        List<PedidoPersona> personas =
        pedidoPersonaRepository.findByPedido_IdPedido(
                pedido.getIdPedido()
        );

        List<PersonaPedidoDTO> personasDTO = new ArrayList<>();

        for (PedidoPersona persona : personas) {

            List<DetallePedido> detalles =
                    detallePedidoRepository
                            .findByPedidoPersona_IdPedidoPersona(
                                    persona.getIdPedidoPersona()
                            );

            List<ProductoPedidoDTO> productosDTO = new ArrayList<>();

            for (DetallePedido detalle : detalles) {

                productosDTO.add(
                        new ProductoPedidoDTO(
                                detalle.getProducto().getNombreProducto(),
                                detalle.getCantidad()
                        )
                );
            }

            personasDTO.add(
                    new PersonaPedidoDTO(
                            persona.getNombrePersona(),
                            productosDTO
                    )
            );
        }

        PedidoMeseroDTO pedidoDTO = new PedidoMeseroDTO(
                pedido.getIdPedido(),
                pedido.getMesa().getNumero_mesa(),
                pedido.getTotalPedido(),
                pedido.getEstadoPedido(),
                pedido.getFechaPedido(),
                personasDTO
        );

        resultado.add(pedidoDTO);
    }

    return resultado;
}

}