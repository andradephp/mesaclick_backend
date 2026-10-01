package com.example.mesaclick.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    @GetMapping("/perfil")
    public Map<String, Object> perfil(Authentication authentication) {

        return Map.of(
                "mensaje", "Usuario autenticado correctamente",
                "correo", authentication.getName()
        );
    }
}