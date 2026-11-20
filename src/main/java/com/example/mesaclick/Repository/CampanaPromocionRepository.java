
package com.example.mesaclick.Repository;

import com.example.mesaclick.Model.CampanaPromocion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampanaPromocionRepository
        extends JpaRepository<CampanaPromocion, Long> {

    List<CampanaPromocion> findByCampana_IdCampana(
            Long idCampana
    );

    boolean existsByCampana_IdCampanaAndPromocion_IdPromocion(
            Long idCampana,
            Long idPromocion
    );
}
