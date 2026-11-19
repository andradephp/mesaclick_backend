package com.example.mesaclick.Repository;

import com.example.mesaclick.Model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("SELECT p FROM Pedido p WHERE p.mesa.id_mesa = :idMesa")
    List<Pedido> buscarPedidosPorMesa(@Param("idMesa") Long idMesa);

    List<Pedido> findAllByOrderByFechaPedidoDesc();

    // Método añadido para filtrar los pedidos listos para cobro por el cajero
    List<Pedido> findByEstadoPedido(String estadoPedido);


    @Query("""
    SELECT p FROM Pedido p
    WHERE p.atencion.idAtencion = :idAtencion
    ORDER BY p.fechaPedido ASC
    """)
    List<Pedido> buscarPedidosPorAtencion(
        @Param("idAtencion") Long idAtencion
);

long countByEstadoPedido(String estadoPedido);

}