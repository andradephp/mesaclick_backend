package com.example.mesaclick.Config;

import com.example.mesaclick.Services.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        try {

            String correo = jwtService.obtenerCorreo(token);
            String rol = jwtService.obtenerRol(token);


            if (correo != null &&
                    SecurityContextHolder.getContext()
                            .getAuthentication() == null) {

                UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
                correo,
                null,
                List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + rol
                        )
                )
        );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            }

        } catch (Exception e) {

            // Token inválido o expirado.
            // No autenticamos al usuario.
        }

        filterChain.doFilter(request, response);
    }
}