
package com.example.mesaclick.Repository;

import com.example.mesaclick.Model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DetallePedidoRepository
        extends JpaRepository<DetallePedido, Long> {

    // Buscar detalles de una persona dentro del pedido
    List<DetallePedido> findByPedidoPersona_IdPedidoPersona(
            Long idPedidoPersona
    );

    // Obtener productos más vendidos de pedidos pagados
    
@Query("""
    SELECT
        d.producto.idProducto,
        d.producto.nombreProducto,
        SUM(d.cantidad),
        SUM(d.cantidad * d.precioUnitario)
    FROM DetallePedido d
    JOIN d.pedidoPersona pp
    JOIN pp.pedido p
    JOIN p.atencion a
    WHERE p.estadoPedido = 'PAGADO'
      AND a.estadoAtencion = 'CERRADA'
    GROUP BY
        d.producto.idProducto,
        d.producto.nombreProducto
    ORDER BY SUM(d.cantidad) DESC
    """)
List<Object[]> obtenerProductosMasVendidos();

}
