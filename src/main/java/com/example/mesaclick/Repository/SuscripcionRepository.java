
package com.example.mesaclick.Repository;

import com.example.mesaclick.Model.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface SuscripcionRepository
        extends JpaRepository<Suscripcion, Long> {

    Optional<Suscripcion> findByCorreoIgnoreCase(
            String correo
    );

    boolean existsByCorreoIgnoreCase(
            String correo
    );

    Optional<Suscripcion> findByTokenConfirmacionHash(
            String tokenConfirmacionHash
    );


    Optional<Suscripcion> findByTokenCancelacionHash(
            String tokenCancelacionHash
);


List<Suscripcion>
findByAceptaPromocionesTrueAndConfirmadaTrueAndFechaCancelacionIsNull();


}
