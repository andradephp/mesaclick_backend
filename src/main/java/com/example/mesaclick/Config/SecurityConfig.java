
package com.example.mesaclick.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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
            .authorizeHttpRequests(auth -> {

                auth.requestMatchers("/error").permitAll();

                // CORS PREFLIGHT
                auth.requestMatchers(
                    HttpMethod.OPTIONS, "/**"
                ).permitAll();

                // ======================================
                // AUTENTICACIÓN
                // ======================================
                auth.requestMatchers(
                    "/api/auth/**"
                ).permitAll();

                // ======================================
                // SUSCRIPCIONES DE CLIENTES
                // ======================================
                auth.requestMatchers(
                    HttpMethod.POST,
                    "/api/suscripciones"
                ).permitAll();

                auth.requestMatchers(
                    HttpMethod.GET,
                    "/api/suscripciones/confirmar"
                ).permitAll();

                auth.requestMatchers(
                    HttpMethod.POST,
                    "/api/suscripciones/cancelar"
                ).permitAll();


                // ======================================
                // IMÁGENES PÚBLICAS
                // ======================================
                auth.requestMatchers(
                    HttpMethod.GET,
                    "/imagenes/productos/**"
                ).permitAll();

                // ======================================
                // ADMINISTRADOR
                // ======================================
                auth.requestMatchers(
                    "/api/admin/**"
                ).hasRole("ADMIN");

                // ======================================
                // CAJERO Y ADMINISTRADOR
                // ======================================
                auth.requestMatchers(
                    "/api/atenciones/**"
                ).hasAnyRole("CAJERO", "ADMIN");

                auth.requestMatchers(
                    HttpMethod.GET,
                    "/api/pedidos/cajero/**"
                ).hasAnyRole("CAJERO", "ADMIN");

                // ======================================
                // COCINA
                // ======================================
                auth.requestMatchers(
                    HttpMethod.GET,
                    "/api/pedidos/cocina"
                ).hasAnyRole("COCINA", "ADMIN");

                auth.requestMatchers(
                    HttpMethod.PATCH,
                    "/api/pedidos/*/listo"
                ).hasAnyRole("COCINA", "ADMIN");

                // ======================================
                // MESEROS
                // ======================================
                auth.requestMatchers(
                    HttpMethod.PATCH,
                    "/api/pedidos/*/entregado"
                ).hasAnyRole("MESERO", "ADMIN");

                auth.requestMatchers(
                    HttpMethod.GET,
                    "/api/pedidos/mesero"
                ).hasAnyRole("MESERO", "ADMIN");

                // ======================================
                // CLIENTES - PEDIDOS POR QR
                // ======================================
                auth.requestMatchers(
                    HttpMethod.POST,
                    "/api/pedidos"
                ).permitAll();

                auth.requestMatchers(
                    HttpMethod.GET,
                    "/api/pedidos/mesa/*"
                ).permitAll();

                // ======================================
                // MENÚ PÚBLICO Y MESAS
                // ======================================
                auth.requestMatchers(
                    HttpMethod.GET,
                    "/api/productos/**",
                    "/api/categorias/**",
                    "/api/mesas/**",
                    "/api/promociones/vigentes"
                ).permitAll();

                // ======================================
                // USUARIOS AUTENTICADOS
                // ======================================
                auth.requestMatchers(
                    "/api/usuario/**"
                ).authenticated();

                // ======================================
                // DEMÁS ENDPOINTS
                // ======================================
                auth.anyRequest().authenticated();
            });

        return http.build();
    }

    // ==========================================
    // VERIFICAR PERFIL DE DESARROLLO
    // ==========================================
    private boolean perfilDesarrolloActivo() {
        return List.of(
            org.springframework.core.env.AbstractEnvironment
                .ACTIVE_PROFILES_PROPERTY_NAME
        ).isEmpty();
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
