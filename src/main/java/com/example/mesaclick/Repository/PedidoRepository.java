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
}

