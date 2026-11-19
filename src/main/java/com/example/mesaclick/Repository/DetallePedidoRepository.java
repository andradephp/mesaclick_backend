
package com.example.mesaclick.Repository;

import com.example.mesaclick.Model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DetallePedidoRepository
        extends JpaRepository<DetallePedido, Long> {

    List<DetallePedido> findByPedidoPersona_IdPedidoPersona(
            Long idPedidoPersona);
}