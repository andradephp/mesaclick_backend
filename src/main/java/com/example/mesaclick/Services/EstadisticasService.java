
package com.example.mesaclick.Services;

import com.example.mesaclick.Repository.PedidoRepository;
import com.example.mesaclick.Repository.AtencionRepository;
import com.example.mesaclick.Repository.DetallePedidoRepository;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.Date;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@Service
public class EstadisticasService {

    // ==========================================
    // REPOSITORIOS
    // ==========================================

    private final PedidoRepository pedidoRepository;
    private final AtencionRepository atencionRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public EstadisticasService(
            PedidoRepository pedidoRepository,
            AtencionRepository atencionRepository,
            DetallePedidoRepository detallePedidoRepository
    ) {
        this.pedidoRepository = pedidoRepository;
        this.atencionRepository = atencionRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }

    // ==========================================
    // RESUMEN GENERAL DEL DASHBOARD
    // ==========================================

    public Map<String, Object> obtenerResumen() {

        Map<String, Object> resumen = new LinkedHashMap<>();

        // ESTADISTICAS DE PEDIDOS

        resumen.put(
                "totalPedidos",
                pedidoRepository.count()
        );

        resumen.put(
                "pedidosEnPreparacion",
                pedidoRepository.countByEstadoPedido("EN_PREPARACION")
        );

        resumen.put(
                "pedidosListos",
                pedidoRepository.countByEstadoPedido("LISTO")
        );

        resumen.put(
                "pedidosEntregados",
                pedidoRepository.countByEstadoPedido("ENTREGADO")
        );

        // ESTADISTICAS DE ATENCIONES

        long atencionesCerradas =
                atencionRepository.countByEstadoAtencion("CERRADA");

        long atencionesActivas =
                atencionRepository.countByEstadoAtencion("ACTIVA");

        resumen.put(
                "atencionesCerradas",
                atencionesCerradas
        );

        resumen.put(
                "atencionesActivas",
                atencionesActivas
        );

        // ESTADISTICAS FINANCIERAS

        Double totalVentas =
                atencionRepository.obtenerTotalVentas();

        Double ticketPromedio =
                atencionRepository.obtenerTicketPromedio();

        resumen.put(
                "totalVentas",
                totalVentas
        );

        resumen.put(
                "ticketPromedio",
                ticketPromedio
        );

        return resumen;
    }

    // ==========================================
    // VENTAS AGRUPADAS POR FECHA
    // ==========================================

    public List<Map<String, Object>> obtenerVentasPorDias(int dias) {

        // VALIDAR RANGO DE DIAS

        if (dias < 1 || dias > 365) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El rango debe estar entre 1 y 365 días"
            );
        }

        // CALCULAR RANGO DE FECHAS

        LocalDate hoy = LocalDate.now();

        LocalDate inicio = hoy.minusDays(dias - 1L);

        LocalDateTime desde = inicio.atStartOfDay();

        LocalDateTime hasta =
                hoy.plusDays(1).atStartOfDay();

        // CONSULTAR VENTAS EN MYSQL

        List<Object[]> resultados =
                atencionRepository.obtenerVentasPorFecha(
                        desde,
                        hasta
                );

        // ORGANIZAR VENTAS POR FECHA

        Map<LocalDate, Double> ventasPorFecha =
                new HashMap<>();

        for (Object[] fila : resultados) {

            LocalDate fecha;

            Object valorFecha = fila[0];

            if (valorFecha instanceof LocalDate) {

                fecha = (LocalDate) valorFecha;

            } else if (valorFecha instanceof Date) {

                fecha = ((Date) valorFecha).toLocalDate();

            } else if (valorFecha instanceof LocalDateTime) {

                fecha = ((LocalDateTime) valorFecha)
                        .toLocalDate();

            } else {

                fecha = LocalDate.parse(
                        valorFecha.toString()
                                .substring(0, 10)
                );
            }

            double total =
                    ((Number) fila[1]).doubleValue();

            ventasPorFecha.put(fecha, total);
        }

        // GENERAR TODOS LOS DIAS DEL RANGO

        List<Map<String, Object>> ventas =
                new ArrayList<>();

        for (int i = 0; i < dias; i++) {

            LocalDate fecha = inicio.plusDays(i);

            Map<String, Object> dato =
                    new LinkedHashMap<>();

            dato.put(
                    "fecha",
                    fecha.toString()
            );

            dato.put(
                    "ventas",
                    ventasPorFecha.getOrDefault(
                            fecha,
                            0.0
                    )
            );

            ventas.add(dato);
        }

        return ventas;
    }

    // ==========================================
    // PRODUCTOS MAS VENDIDOS
    // ==========================================

    public List<Map<String, Object>> obtenerProductosMasVendidos() {

        // CONSULTAR PRODUCTOS DE PEDIDOS PAGADOS

        List<Object[]> resultados =
                detallePedidoRepository.obtenerProductosMasVendidos();

        List<Map<String, Object>> productos =
                new ArrayList<>();

        // CONVERTIR RESULTADOS A JSON

        for (Object[] fila : resultados) {

            Map<String, Object> producto =
                    new LinkedHashMap<>();

            producto.put(
                    "idProducto",
                    fila[0]
            );

            producto.put(
                    "nombreProducto",
                    fila[1]
            );

            producto.put(
                    "cantidadVendida",
                    ((Number) fila[2]).longValue()
            );

            producto.put(
                    "totalGenerado",
                    ((Number) fila[3]).doubleValue()
            );

            productos.add(producto);
        }

        return productos;
    }
}
