package com.example.mesaclick.Services;

import com.example.mesaclick.Dto.LoginDTO;
import com.example.mesaclick.Dto.RegistroDTO;
import com.example.mesaclick.Dto.CrearUsuarioPersonalDTO;
import com.example.mesaclick.Exception.CorreoYaRegistradoException;
import com.example.mesaclick.Exception.CredencialesInvalidasException;
import com.example.mesaclick.Model.Rol;
import com.example.mesaclick.Model.Usuario;
import com.example.mesaclick.Repository.UsuarioRepository;
import com.example.mesaclick.Services.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public Usuario registrarUsuario(RegistroDTO registroDTO) {

        // Comprobar si el correo ya está registrado
        if (usuarioRepository.existsByCorreo(registroDTO.getCorreo())) {
            throw new CorreoYaRegistradoException(
                    "El correo ya está registrado"
            );
        }

        // Crear nuevo usuario
        Usuario usuario = new Usuario();

        usuario.setNombre(registroDTO.getNombre().trim());

        usuario.setCorreo(
                registroDTO.getCorreo().trim().toLowerCase()
        );

        usuario.setTelefono(
                registroDTO.getTelefono().trim()
        );

        usuario.setEdad(
                registroDTO.getEdad()
        );

        // Todo usuario que se registre públicamente será CLIENTE
        usuario.setRol(Rol.CLIENTE);

        // Encriptar contraseña antes de guardarla
        usuario.setPassword(
                passwordEncoder.encode(
                        registroDTO.getPassword()
                )
        );

        // Guardar usuario en la base de datos
        return usuarioRepository.save(usuario);
    }

    public Usuario crearUsuarioPersonal(
        CrearUsuarioPersonalDTO dto) {

    // Comprobar si el correo ya está registrado
    if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
        throw new CorreoYaRegistradoException(
                "El correo ya está registrado"
        );
    }

    // Solo se permite crear COCINA o MESERO
    if (dto.getRol() != Rol.COCINA &&
        dto.getRol() != Rol.MESERO) {

        throw new IllegalArgumentException(
                "Solo se pueden crear usuarios de COCINA o MESERO"
        );
    }

    Usuario usuario = new Usuario();

    usuario.setNombre(
            dto.getNombre().trim()
    );

    usuario.setCorreo(
            dto.getCorreo().trim().toLowerCase()
    );

    usuario.setTelefono(
            dto.getTelefono().trim()
    );

    usuario.setEdad(
            dto.getEdad()
    );

    usuario.setRol(
            dto.getRol()
    );

    // Encriptar contraseña
    usuario.setPassword(
            passwordEncoder.encode(
                    dto.getPassword()
            )
    );

    return usuarioRepository.save(usuario);
}

    public Usuario iniciarSesion(LoginDTO loginDTO) {

        String correo = loginDTO.getCorreo()
                .trim()
                .toLowerCase();

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() ->
                        new CredencialesInvalidasException(
                                "Correo o contraseña incorrectos"
                        )
                );

        if (!passwordEncoder.matches(
                loginDTO.getPassword(),
                usuario.getPassword())) {

            throw new CredencialesInvalidasException(
                    "Correo o contraseña incorrectos"
            );
        }

        return usuario;
    }
}