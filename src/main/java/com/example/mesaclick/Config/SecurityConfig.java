
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
            .csrf(csrf -> csrf.disable())

            // ======================================
            // CORS
            // ======================================

            .cors(cors ->
                cors.configurationSource(corsConfigurationSource())
            )

            // ======================================
            // SESIONES JWT
            // ======================================

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            // ======================================
            // FILTRO JWT
            // ======================================

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            )

            // ======================================
            // AUTORIZACIONES
            // ======================================

            .authorizeHttpRequests(auth -> auth

                .requestMatchers("/error").permitAll()

                // CORS PREFLIGHT

                .requestMatchers(
                    HttpMethod.OPTIONS, "/**"
                ).permitAll()

                // AUTENTICACION

                .requestMatchers(
                    "/api/auth/**"
                ).permitAll()

                // ======================================
                // IMAGENES PUBLICAS DE PRODUCTOS
                // ======================================

                .requestMatchers(
                    HttpMethod.GET,
                    "/imagenes/productos/**"
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
                // MENU PUBLICO Y MESAS
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

                // DEMAS ENDPOINTS

                .anyRequest().authenticated()
            );

        return http.build();
    }

    // ==========================================
    // CONFIGURACION CORS
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
    // ENCRIPTACION DE CONTRASEÑAS
    // ==========================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
