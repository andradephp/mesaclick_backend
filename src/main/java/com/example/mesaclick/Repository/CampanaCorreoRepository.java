package com.example.mesaclick.Repository;

import com.example.mesaclick.Model.CampanaCorreo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CampanaCorreoRepository
        extends JpaRepository<CampanaCorreo, Long> {

    List<CampanaCorreo> findAllByOrderByFechaCreacionDesc();

    @Modifying(
            flushAutomatically = true,
            clearAutomatically = true
    )
    @Query("""
        UPDATE CampanaCorreo c
        SET c.estado = 'ENVIANDO'
        WHERE c.idCampana = :idCampana
          AND c.estado = 'BORRADOR'
    """)
    int iniciarEnvio(@Param("idCampana") Long idCampana);
}