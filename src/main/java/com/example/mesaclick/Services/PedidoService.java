
package com.example.mesaclick.Services;

import com.example.mesaclick.Dto.PedidoRequest;
import com.example.mesaclick.Dto.PersonaRequest;
import com.example.mesaclick.Dto.ProductoRequest;
import com.example.mesaclick.Dto.PedidoMeseroDTO;
import com.example.mesaclick.Dto.PersonaPedidoDTO;
import com.example.mesaclick.Dto.ProductoPedidoDTO;

import com.example.mesaclick.Model.Mesa;
import com.example.mesaclick.Model.Pedido;
import com.example.mesaclick.Model.PedidoPersona;
import com.example.mesaclick.Model.DetallePedido;
import com.example.mesaclick.Model.Producto;
import com.example.mesaclick.Model.Atencion;

import com.example.mesaclick.Repository.MesaRepository;
import com.example.mesaclick.Repository.PedidoRepository;
import com.example.mesaclick.Repository.PedidoPersonaRepository;
import com.example.mesaclick.Repository.DetallePedidoRepository;
import com.example.mesaclick.Repository.ProductoRepository;
import com.example.mesaclick.Repository.AtencionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;
    private final PedidoPersonaRepository pedidoPersonaRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;
    private final AtencionRepository atencionRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            MesaRepository mesaRepository,
            PedidoPersonaRepository pedidoPersonaRepository,
            DetallePedidoRepository detallePedidoRepository,
            ProductoRepository productoRepository,
            AtencionRepository atencionRepository
    ) {
        this.pedidoRepository = pedidoRepository;
        this.mesaRepository = mesaRepository;
        this.pedidoPersonaRepository = pedidoPersonaRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoRepository = productoRepository;
        this.atencionRepository = atencionRepository;
    }

    // ==========================================
    // CREAR PEDIDO DESDE EL CODIGO QR
    // ==========================================

    @Transactional
    public Pedido crearPedido(PedidoRequest request) {

        if (request == null || request.getIdMesa() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debes seleccionar una mesa válida"
            );
        }

        if (request.getPersonas() == null ||
                request.getPersonas().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El pedido debe contener al menos una persona"
            );
        }

        // Bloquear la mesa antes de buscar la atención
        Mesa mesa = mesaRepository.bloquearMesaPorId(
                request.getIdMesa()
        ).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La mesa no existe"
                )
        );

        // Buscar la atención activa
        
Atencion atencion = atencionRepository
        .buscarAtencionPorMesaYEstado(
                mesa.getId_mesa(),
                "ACTIVA"
        )
        .orElseGet(() -> {

            // Crear una nueva atención
            Atencion nuevaAtencion = new Atencion();

            nuevaAtencion.setMesa(mesa);
            nuevaAtencion.setEstadoAtencion("ACTIVA");
            nuevaAtencion.setFechaApertura(
                    LocalDateTime.now()
            );

            Atencion atencionGuardada =
                    atencionRepository.save(nuevaAtencion);

            // Marcar la mesa como ocupada
            mesa.setEstado_mesa("OCUPADA");
            mesaRepository.save(mesa);

            return atencionGuardada;
        });


        // Crear pedido asociado a la atención
        Pedido pedido = new Pedido();

        pedido.setMesa(mesa);
        pedido.setAtencion(atencion);
        pedido.setEstadoPedido("EN_PREPARACION");
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setTotalPedido(0.0);

        pedido = pedidoRepository.save(pedido);

        double totalGeneral = 0.0;

        // Guardar personas y productos
        for (PersonaRequest personaRequest :
                request.getPersonas()) {

            if (personaRequest.getProductos() == null ||
                    personaRequest.getProductos().isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Cada persona debe seleccionar al menos un producto"
                );
            }

            PedidoPersona pedidoPersona =
                    new PedidoPersona();

            pedidoPersona.setPedido(pedido);
            pedidoPersona.setNombrePersona(
                    personaRequest.getNombre()
            );
            pedidoPersona.setSubtotal(0.0);

            pedidoPersona = pedidoPersonaRepository.save(
                    pedidoPersona
            );

            double subtotalPersona = 0.0;

            for (ProductoRequest productoRequest :
                    personaRequest.getProductos()) {

                if (productoRequest.getCantidad() <= 0) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "La cantidad debe ser mayor que cero"
                    );
                }

                Producto producto =
                        productoRepository.findById(
                                productoRequest.getIdProducto()
                        ).orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "El producto no existe"
                                )
                        );

                double subtotalProducto =
                        producto.getPrecioProducto()
                                * productoRequest.getCantidad();

                subtotalPersona += subtotalProducto;

                DetallePedido detalle =
                        new DetallePedido();

                detalle.setPedidoPersona(pedidoPersona);
                detalle.setProducto(producto);
                detalle.setCantidad(
                        productoRequest.getCantidad()
                );
                detalle.setPrecioUnitario(
                        producto.getPrecioProducto()
                );

                detallePedidoRepository.save(detalle);
            }

            pedidoPersona.setSubtotal(subtotalPersona);
            pedidoPersonaRepository.save(pedidoPersona);

            totalGeneral += subtotalPersona;
        }

        pedido.setTotalPedido(totalGeneral);

        return pedidoRepository.save(pedido);
    }

    // ==========================================
    // OBTENER PEDIDOS DE LA ATENCION ACTIVA
    // ==========================================

    @Transactional(readOnly = true)
    public List<Pedido> obtenerPedidosPorMesa(Long idMesa) {

        return atencionRepository
                .buscarAtencionPorMesaYEstado(
                        idMesa,
                        "ACTIVA"
                )
                .map(atencion ->
                        pedidoRepository.buscarPedidosPorAtencion(
                                atencion.getIdAtencion()
                        )
                )
                .orElseGet(List::of);
    }

    // ==========================================
    // CONSULTAR TODOS LOS PEDIDOS
    // ==========================================

    @Transactional(readOnly = true)
    public List<Pedido> obtenerTodosLosPedidos() {
        return pedidoRepository
                .findAllByOrderByFechaPedidoDesc();
    }

    // ==========================================
    // COCINA: MARCAR PEDIDO COMO LISTO
    // ==========================================

    @Transactional
    public Pedido marcarComoListo(Long idPedido) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "El pedido no existe"
                        )
                );

        if (!"EN_PREPARACION".equals(
                pedido.getEstadoPedido()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Solo se pueden marcar como listos los pedidos en preparación"
            );
        }

        pedido.setEstadoPedido("LISTO");

        return pedidoRepository.save(pedido);
    }

    // ==========================================
    // MESERO: MARCAR PEDIDO COMO ENTREGADO
    // ==========================================

    @Transactional
    public Pedido marcarComoEntregado(Long idPedido) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "El pedido no existe"
                        )
                );

        if (!"LISTO".equals(
                pedido.getEstadoPedido()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Solo se pueden entregar pedidos con estado LISTO"
            );
        }

        pedido.setEstadoPedido("ENTREGADO");

        return pedidoRepository.save(pedido);
    }

    // ==========================================
    // CONSULTAR PEDIDOS PARA EL MESERO
    // ==========================================

    @Transactional(readOnly = true)
    public List<PedidoMeseroDTO> obtenerPedidosParaMesero() {

        List<Pedido> pedidos =
                pedidoRepository
                        .findAllByOrderByFechaPedidoDesc();

        return mapearPedidosADTO(pedidos);
    }

    // ==========================================
    // CONSULTAR PEDIDOS PARA CAJA
    // COMPATIBILIDAD TEMPORAL
    // ==========================================

    @Transactional(readOnly = true)
    public List<PedidoMeseroDTO> obtenerPedidosParaCajero() {

        List<Pedido> pedidos =
                pedidoRepository.findByEstadoPedido(
                        "ENTREGADO"
                );

        return mapearPedidosADTO(pedidos);
    }

    // ==========================================
    // CONVERTIR PEDIDOS A DTO
    // ==========================================

    private List<PedidoMeseroDTO> mapearPedidosADTO(
            List<Pedido> pedidos
    ) {

        List<PedidoMeseroDTO> resultado =
                new ArrayList<>();

        for (Pedido pedido : pedidos) {

            List<PedidoPersona> personas =
                    pedidoPersonaRepository
                            .findByPedido_IdPedido(
                                    pedido.getIdPedido()
                            );

            List<PersonaPedidoDTO> personasDTO =
                    new ArrayList<>();

            for (PedidoPersona persona : personas) {

                List<DetallePedido> detalles =
                        detallePedidoRepository
                                .findByPedidoPersona_IdPedidoPersona(
                                        persona.getIdPedidoPersona()
                                );

                List<ProductoPedidoDTO> productosDTO =
                        new ArrayList<>();

                for (DetallePedido detalle : detalles) {

                    productosDTO.add(
                            new ProductoPedidoDTO(
                                    detalle.getProducto()
                                            .getNombreProducto(),
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

            PedidoMeseroDTO pedidoDTO =
                    new PedidoMeseroDTO(
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
