package com.example.mesaclick.Model;

import jakarta.persistence.*;

@Entity
@Table(
    name = "usuarios",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_usuario_correo",
            columnNames = "correo"
        )
    }
    

)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(
        name = "nombre",
        nullable = false,
        length = 100
    )
    private String nombre;

    @Column(
        name = "correo",
        nullable = false,
        length = 150
    )
    private String correo;

    @Column(
        name = "password",
        nullable = false,
        length = 255
    )
    private String password;

    @Column(
        name = "telefono",
        nullable = false,
        length = 20
    )
    private String telefono;

    @Column(
        name = "edad",
        nullable = false
    )
    private Integer edad;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private Rol rol;

    public Usuario() {
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public Rol getRol() {
    return rol;
    }

    public void setRol(Rol rol) {
    this.rol = rol;
    }
}