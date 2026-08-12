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

        pedido.setEstadoPedido("PENDIENTE");

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
}