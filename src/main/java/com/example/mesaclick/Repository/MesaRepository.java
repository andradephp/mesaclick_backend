package com.example.mesaclick.Repository;

import com.example.mesaclick.Model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MesaRepository extends JpaRepository<Mesa, Long> {

    Optional<Mesa> findByCodigoQrMesa(String codigo);
    
@Query(
    value = """
        SELECT *
        FROM mesas
        WHERE id_mesa = :idMesa
        FOR UPDATE
        """,
    nativeQuery = true
)
Optional<Mesa> bloquearMesaPorId(
        @Param("idMesa") Long idMesa
);


}