
package com.example.mesaclick.Repository;

import com.example.mesaclick.Model.CampanaEnvio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CampanaEnvioRepository
        extends JpaRepository<CampanaEnvio, Long> {

    Optional<CampanaEnvio> findByCampana_IdCampanaAndSuscripcion_IdSuscripcion(
            Long idCampana,
            Long idSuscripcion
    );

    boolean existsByCampana_IdCampanaAndSuscripcion_IdSuscripcion(
            Long idCampana,
            Long idSuscripcion
    );

    List<CampanaEnvio> findByCampana_IdCampana(
            Long idCampana
    );

    long countByCampana_IdCampanaAndEstado(
            Long idCampana,
            String estado
    );
}
