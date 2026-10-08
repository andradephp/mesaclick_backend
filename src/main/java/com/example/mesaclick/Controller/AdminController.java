package com.example.mesaclick.Controller;

import com.example.mesaclick.Dto.CrearUsuarioPersonalDTO;
import com.example.mesaclick.Model.Usuario;
import com.example.mesaclick.Services.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final UsuarioService usuarioService;

    public AdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // CREAR PERSONAL
    @PostMapping("/usuarios")
    public ResponseEntity<?> crearUsuarioPersonal(
            @Valid @RequestBody CrearUsuarioPersonalDTO dto) {

        Usuario usuario = usuarioService.crearUsuarioPersonal(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "mensaje",
                        "Usuario de personal creado correctamente",
                        "idUsuario",
                        usuario.getIdUsuario(),
                        "nombre",
                        usuario.getNombre(),
                        "correo",
                        usuario.getCorreo(),
                        "rol",
                        usuario.getRol()
                ));
    }

    // CONSULTAR PERSONAL
    @GetMapping("/usuarios")
    public ResponseEntity<?> obtenerPersonal() {

        List<Usuario> usuarios = usuarioService.obtenerPersonal();

        return ResponseEntity.ok(
                usuarios.stream()
                        .map(usuario -> Map.of(
                                "idUsuario", usuario.getIdUsuario(),
                                "nombre", usuario.getNombre(),
                                "correo", usuario.getCorreo(),
                                "telefono", usuario.getTelefono(),
                                "edad", usuario.getEdad(),
                                "rol", usuario.getRol().name()
                        ))
                        .toList()
        );
    }
}