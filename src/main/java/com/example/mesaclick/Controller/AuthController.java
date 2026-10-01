package com.example.mesaclick.Controller;

import com.example.mesaclick.Dto.LoginDTO;
import com.example.mesaclick.Dto.RegistroDTO;
import com.example.mesaclick.Model.Usuario;
import com.example.mesaclick.Services.UsuarioService;
import com.example.mesaclick.Services.JwtService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public AuthController(
        UsuarioService usuarioService,
        JwtService jwtService) {

    this.usuarioService = usuarioService;
    this.jwtService = jwtService;
}

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(
            @Valid @RequestBody RegistroDTO registroDTO) {

        Usuario usuario = usuarioService.registrarUsuario(registroDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "mensaje", "Usuario registrado correctamente",
                        "idUsuario", usuario.getIdUsuario(),
                        "nombre", usuario.getNombre(),
                        "correo", usuario.getCorreo()
                ));
    }

@PostMapping("/login")
public ResponseEntity<?> iniciarSesion(
        @Valid @RequestBody LoginDTO loginDTO) {

    Usuario usuario = usuarioService.iniciarSesion(loginDTO);

    String token = jwtService.generarToken(
        usuario.getCorreo(),
        usuario.getRol().name()
);

    return ResponseEntity.ok(
            Map.of(
                    "mensaje", "Inicio de sesión exitoso",
                    "token", token,
                    "idUsuario", usuario.getIdUsuario(),
                    "nombre", usuario.getNombre(),
                    "correo", usuario.getCorreo()
            )
    );
}

}