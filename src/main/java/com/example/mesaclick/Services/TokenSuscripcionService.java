
package com.example.mesaclick.Services;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class TokenSuscripcionService {

    private final SecureRandom secureRandom = new SecureRandom();

    // Generar un token aleatorio seguro
    public String generarToken() {

        byte[] bytes = new byte[32];

        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    // Crear una huella SHA-256 del token
    public String generarHash(String token) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "No se pudo generar el hash del token.",
                    e
            );
        }
    }

    // Fecha de vencimiento: 24 horas
    public LocalDateTime generarExpiracion() {
        return LocalDateTime.now().plusHours(24);
    }
}
