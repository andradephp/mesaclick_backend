
package com.example.mesaclick.Repository;

import com.example.mesaclick.Model.PedidoPersona;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoPersonaRepository
        extends JpaRepository<PedidoPersona, Long> {

    List<PedidoPersona> findByPedido_IdPedido(Long idPedido);
}