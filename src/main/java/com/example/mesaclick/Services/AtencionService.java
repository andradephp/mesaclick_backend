
package com.example.mesaclick.Services;

import com.example.mesaclick.Dto.AtencionCajaDTO;
import com.example.mesaclick.Dto.PedidoMeseroDTO;
import com.example.mesaclick.Dto.PersonaPedidoDTO;
import com.example.mesaclick.Dto.ProductoPedidoDTO;

import com.example.mesaclick.Model.Atencion;
import com.example.mesaclick.Model.Pedido;
import com.example.mesaclick.Model.PedidoPersona;
import com.example.mesaclick.Model.DetallePedido;
import com.example.mesaclick.Model.Mesa;
import com.example.mesaclick.Repository.AtencionRepository;
import com.example.mesaclick.Repository.PedidoRepository;
import com.example.mesaclick.Repository.MesaRepository;
import com.example.mesaclick.Repository.PedidoPersonaRepository;
import com.example.mesaclick.Repository.DetallePedidoRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class AtencionService {

    private final AtencionRepository atencionRepository;
    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;
    private final PedidoPersonaRepository pedidoPersonaRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public AtencionService(
            AtencionRepository atencionRepository,
            PedidoRepository pedidoRepository,
            MesaRepository mesaRepository,
            PedidoPersonaRepository pedidoPersonaRepository,
            DetallePedidoRepository detallePedidoRepository
    ) {
        this.atencionRepository = atencionRepository;
        this.pedidoRepository = pedidoRepository;
        this.mesaRepository = mesaRepository;
        this.pedidoPersonaRepository = pedidoPersonaRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }

    // ==========================================
    // CONSULTAR ATENCIONES ACTIVAS
    // ==========================================

    @Transactional(readOnly = true)
    public List<Atencion> obtenerAtencionesActivas() {
        return atencionRepository.findByEstadoAtencion("ACTIVA");
    }

    // ==========================================
    // BUSCAR ATENCION POR ID
    // ==========================================

    @Transactional(readOnly = true)
    public Atencion obtenerAtencion(Long idAtencion) {

        return atencionRepository.findById(idAtencion)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "La atención no existe"
                        )
                );
    }

    // ==========================================
    // OBTENER PEDIDOS DE UNA ATENCION
    // ==========================================

    @Transactional(readOnly = true)
    public List<Pedido> obtenerPedidos(Long idAtencion) {

        obtenerAtencion(idAtencion);

        return pedidoRepository.buscarPedidosPorAtencion(
                idAtencion
        );
    }

    // ==========================================
    // CALCULAR TOTAL DE UNA ATENCION
    // ==========================================

    @Transactional(readOnly = true)
    public double calcularTotal(Long idAtencion) {

        List<Pedido> pedidos = obtenerPedidos(idAtencion);

        return pedidos.stream()
                .mapToDouble(pedido ->
                        pedido.getTotalPedido() == null
                                ? 0.0
                                : pedido.getTotalPedido()
                )
                .sum();
    }

    // ==========================================
    // VERIFICAR SI SE PUEDE COBRAR
    // ==========================================

    @Transactional(readOnly = true)
    public boolean puedeCobrar(Long idAtencion) {

        Atencion atencion = obtenerAtencion(idAtencion);

        if (!"ACTIVA".equals(
                atencion.getEstadoAtencion()
        )) {
            return false;
        }

        List<Pedido> pedidos =
                pedidoRepository.buscarPedidosPorAtencion(
                        idAtencion
                );

        if (pedidos.isEmpty()) {
            return false;
        }

        return pedidos.stream()
                .allMatch(pedido ->
                        "ENTREGADO".equals(
                                pedido.getEstadoPedido()
                        )
                );
    }

    // ==========================================
    // REGISTRAR PAGO Y CERRAR ATENCION
    // ==========================================

    @Transactional
    public Atencion registrarPago(
            Long idAtencion,
            String metodoPago
    ) {

        // 1. Validar método de pago
        if (metodoPago == null ||
                !Set.of(
                        "EFECTIVO",
                        "TARJETA",
                        "TRANSFERENCIA"
                ).contains(metodoPago)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Método de pago no válido"
            );
        }

        // 2. Buscar atención para identificar la mesa
        Atencion atencionInicial =
                atencionRepository.findById(idAtencion)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "La atención no existe"
                                )
                        );

        Long idMesa =
                atencionInicial.getMesa().getId_mesa();

        // 3. Bloquear mesa durante el cobro
        mesaRepository.bloquearMesaPorId(idMesa)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "La mesa no existe"
                        )
                );

        // 4. Bloquear atención
        Atencion atencion =
                atencionRepository.bloquearAtencionPorId(
                        idAtencion
                ).orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "La atención no existe"
                        )
                );

        // 5. Recargar desde MySQL el estado actualizado
        entityManager.refresh(atencion);

        // 6. Verificar que la atención siga activa
        if (!"ACTIVA".equals(
                atencion.getEstadoAtencion()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Esta atención ya fue cerrada"
            );
        }

        // 7. Obtener pedidos de la atención
        List<Pedido> pedidos =
                pedidoRepository.buscarPedidosPorAtencion(
                        idAtencion
                );

        if (pedidos.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede cobrar una atención sin pedidos"
            );
        }

        // 8. Comprobar que todos estén entregados
        boolean todosEntregados = pedidos.stream()
                .allMatch(pedido ->
                        "ENTREGADO".equals(
                                pedido.getEstadoPedido()
                        )
                );

        if (!todosEntregados) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Todos los pedidos deben estar ENTREGADO para cobrar"
            );
        }

        // 9. Calcular total acumulado
        double total = pedidos.stream()
                .mapToDouble(pedido ->
                        pedido.getTotalPedido() == null
                                ? 0.0
                                : pedido.getTotalPedido()
                )
                .sum();

        // 10. Marcar todos los pedidos como pagados
        for (Pedido pedido : pedidos) {
            pedido.setEstadoPedido("PAGADO");
        }

        pedidoRepository.saveAll(pedidos);

        // 11. Registrar información del pago
        atencion.setTotalPagado(total);
        atencion.setMetodoPago(metodoPago);
        atencion.setFechaCierre(LocalDateTime.now());

        // 12. Cerrar atención
        
// 12. Cerrar la atención
atencion.setEstadoAtencion("CERRADA");

// 13. Liberar la mesa
Mesa mesa = mesaRepository.bloquearMesaPorId(
        atencion.getMesa().getId_mesa()
).orElseThrow(() ->
        new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "La mesa no existe"
        )
);

mesa.setEstado_mesa("DISPONIBLE");
mesaRepository.save(mesa);

// 14. Guardar la atención cerrada
return atencionRepository.save(atencion);

    }

    // ==========================================
    // OBTENER CUENTA COMPLETA PARA EL CAJERO
    // ==========================================

    @Transactional(readOnly = true)
    public AtencionCajaDTO obtenerCuenta(Long idAtencion) {

        Atencion atencion = obtenerAtencion(idAtencion);

        List<Pedido> pedidos =
                pedidoRepository.buscarPedidosPorAtencion(
                        idAtencion
                );

        List<PedidoMeseroDTO> pedidosDTO =
                new ArrayList<>();

        double total = 0.0;
        boolean todosEntregados = !pedidos.isEmpty();

        for (Pedido pedido : pedidos) {

            total += pedido.getTotalPedido() == null
                    ? 0.0
                    : pedido.getTotalPedido();

            if (!"ENTREGADO".equals(
                    pedido.getEstadoPedido()
            )) {
                todosEntregados = false;
            }

            List<PersonaPedidoDTO> personasDTO =
                    new ArrayList<>();

            List<PedidoPersona> personas =
                    pedidoPersonaRepository
                            .findByPedido_IdPedido(
                                    pedido.getIdPedido()
                            );

            for (PedidoPersona persona : personas) {

                List<ProductoPedidoDTO> productosDTO =
                        new ArrayList<>();

                List<DetallePedido> detalles =
                        detallePedidoRepository
                                .findByPedidoPersona_IdPedidoPersona(
                                        persona.getIdPedidoPersona()
                                );

                
for (DetallePedido detalle : detalles) {

    productosDTO.add(
            new ProductoPedidoDTO(
                    detalle.getProducto().getNombreProducto(),
                    detalle.getCantidad(),
                    detalle.getPrecioUnitario(),
                    detalle.getPrecioUnitario()
                            * detalle.getCantidad()
            )
    );
}


                
personasDTO.add(
        new PersonaPedidoDTO(
                persona.getNombrePersona(),
                persona.getSubtotal(),
                productosDTO
        )
);
        
            }

            pedidosDTO.add(
                    new PedidoMeseroDTO(
                            pedido.getIdPedido(),
                            pedido.getMesa().getNumero_mesa(),
                            pedido.getTotalPedido(),
                            pedido.getEstadoPedido(),
                            pedido.getFechaPedido(),
                            personasDTO
                    )
            );
        }

        boolean puedeCobrar =
                "ACTIVA".equals(
                        atencion.getEstadoAtencion()
                ) && todosEntregados;

        return new AtencionCajaDTO(
                atencion.getIdAtencion(),
                atencion.getMesa().getNumero_mesa(),
                atencion.getMesa().getCodigoQrMesa(),
                atencion.getEstadoAtencion(),
                atencion.getFechaApertura(),
                total,
                puedeCobrar,
                pedidosDTO
        );
    }
}
