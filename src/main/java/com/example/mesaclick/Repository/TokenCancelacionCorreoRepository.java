
package com.example.mesaclick.Repository;

import com.example.mesaclick.Model.TokenCancelacionCorreo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenCancelacionCorreoRepository
        extends JpaRepository<TokenCancelacionCorreo, Long> {

    // Buscar un enlace de cancelación por su hash.
    Optional<TokenCancelacionCorreo> findByTokenHash(
            String tokenHash
    );

    // Consultar los tokens de un suscriptor.
    List<TokenCancelacionCorreo> findBySuscripcion_IdSuscripcion(
            Long idSuscripcion
    );

    // Eliminar todos los tokens de un suscriptor.
    void deleteBySuscripcion_IdSuscripcion(
            Long idSuscripcion
    );
}
