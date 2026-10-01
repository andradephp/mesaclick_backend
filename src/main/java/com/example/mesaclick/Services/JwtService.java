package com.example.mesaclick.Services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET_KEY =
            "MesaClick-Clave-Secreta-Para-JWT-2026-Segura";

    private static final long EXPIRATION_TIME =
            1000 * 60 * 60 * 24; // 24 horas

    private final SecretKey key;

    public JwtService() {
        this.key = Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generarToken(String correo, String rol) {

    Date ahora = new Date();

    Date expiracion = new Date(
            ahora.getTime() + EXPIRATION_TIME
    );

    return Jwts.builder()
            .subject(correo)
            .claim("rol", rol)
            .issuedAt(ahora)
            .expiration(expiracion)
            .signWith(key)
            .compact();
}

    public String obtenerCorreo(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String obtenerRol(String token) {

    return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("rol", String.class);
}
}