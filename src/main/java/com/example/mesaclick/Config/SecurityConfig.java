
package com.example.mesaclick.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
            // API con autenticación JWT
            .csrf(csrf -> csrf.disable())

            // Permitir conexiones desde React
            .cors(cors ->
                cors.configurationSource(corsConfigurationSource())
            )

            // No utilizar sesiones de servidor
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            // Validar el token JWT
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            )

            .authorizeHttpRequests(auth -> auth

                // ======================================
                // PETICIONES CORS
                // ======================================

                .requestMatchers(
                    HttpMethod.OPTIONS, "/**"
                ).permitAll()

                // ======================================
                // AUTENTICACIÓN
                // ======================================

                .requestMatchers(
                    "/api/auth/**"
                ).permitAll()

                // ======================================
                // ADMINISTRADOR
                // ======================================

                .requestMatchers(
                    "/api/admin/**"
                ).hasRole("ADMIN")

                // ======================================
                // CAJERO Y ADMINISTRADOR
                // ======================================

                .requestMatchers(
                    "/api/atenciones/**"
                ).hasAnyRole("CAJERO", "ADMIN")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/pedidos/cajero/**"
                ).hasAnyRole("CAJERO", "ADMIN")

                // ======================================
                // COCINA
                // ======================================

                .requestMatchers(
                    HttpMethod.PATCH,
                    "/api/pedidos/*/listo"
                ).hasAnyRole("COCINA", "ADMIN")

                // ======================================
                // MESEROS
                // ======================================

                .requestMatchers(
                    HttpMethod.PATCH,
                    "/api/pedidos/*/entregado"
                ).hasAnyRole("MESERO", "ADMIN")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/pedidos/mesero"
                ).hasAnyRole("MESERO", "ADMIN")

                // ======================================
                // CLIENTES - PEDIDOS POR QR
                // ======================================

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/pedidos"
                ).permitAll()

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/pedidos/mesa/*"
                ).permitAll()

                // ======================================
                // MENÚ Y MESAS
                // ======================================

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/productos/**",
                    "/api/categorias/**",
                    "/api/mesas/**"
                ).permitAll()

                // ======================================
                // USUARIOS AUTENTICADOS
                // ======================================

                .requestMatchers(
                    "/api/usuario/**"
                ).authenticated()

                // ======================================
                // DEMÁS ENDPOINTS
                // ======================================

                .anyRequest().authenticated()
            );

        return http.build();
    }

    // ==========================================
    // CONFIGURACIÓN CORS
    // ==========================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
            List.of(
                "http://localhost:5173",
                "https://mesaclick.tech",
                "https://www.mesaclick.tech"
            )
        );

        configuration.setAllowedMethods(
            List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
            )
        );

        configuration.setAllowedHeaders(
            List.of(
                "Authorization",
                "Content-Type"
            )
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
            "/**",
            configuration
        );

        return source;
    }

    // ==========================================
    // ENCRIPTACIÓN DE CONTRASEÑAS
    // ==========================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
