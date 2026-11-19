package com.example.mesaclick.Controller;

import com.example.mesaclick.Model.Rol;
import com.example.mesaclick.Model.Usuario;
import com.example.mesaclick.Repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/desarrollo")
public class CrearAdminTemporalController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public CrearAdminTemporalController(    
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/crear-admin")
    public String crearAdmin() {

        Usuario usuario = new Usuario();

        usuario.setNombre("Administrador");
        usuario.setCorreo("admin@mesaclick.com");
        usuario.setTelefono("3000000000");
        usuario.setEdad(25);
        usuario.setRol(Rol.ADMIN);

        usuario.setPassword(
                passwordEncoder.encode("Admin123")
        );

        usuarioRepository.save(usuario);

        return "Administrador creado correctamente";
    }
}