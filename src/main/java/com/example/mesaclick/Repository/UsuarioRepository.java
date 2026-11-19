package com.example.mesaclick.Repository;
import com.example.mesaclick.Model.Rol;

import com.example.mesaclick.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    List<Usuario> findByRolIn(List<Rol> roles);
}