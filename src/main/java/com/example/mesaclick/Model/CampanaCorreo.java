
package com.example.mesaclick.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "campanas_correo")
public class CampanaCorreo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_campana")
    private Long idCampana;

    @Column(name = "asunto", nullable = false, length = 180)
    private String asunto;

    @Column(name = "titulo", nullable = false, length = 180)
    private String titulo;

    @Column(name = "mensaje", columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "nombre_evento", length = 180)
    private String nombreEvento;

    @Column(name = "descripcion_evento", columnDefinition = "TEXT")
    private String descripcionEvento;

    @Column(name = "fecha_evento")
    private LocalDateTime fechaEvento;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado = "BORRADOR";

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "cantidad_enviados", nullable = false)
    private Integer cantidadEnviados = 0;

    @Column(name = "cantidad_fallidos", nullable = false)
    private Integer cantidadFallidos = 0;

    @PrePersist
    public void antesDeGuardar() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }

        if (estado == null || estado.isBlank()) {
            estado = "BORRADOR";
        }

        if (cantidadEnviados == null) {
            cantidadEnviados = 0;
        }

        if (cantidadFallidos == null) {
            cantidadFallidos = 0;
        }
    }

    public Long getIdCampana() {
        return idCampana;
    }

    public void setIdCampana(Long idCampana) {
        this.idCampana = idCampana;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getNombreEvento() {
        return nombreEvento;
    }

    public void setNombreEvento(String nombreEvento) {
        this.nombreEvento = nombreEvento;
    }

    public String getDescripcionEvento() {
        return descripcionEvento;
    }

    public void setDescripcionEvento(String descripcionEvento) {
        this.descripcionEvento = descripcionEvento;
    }

    public LocalDateTime getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(LocalDateTime fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public Integer getCantidadEnviados() {
        return cantidadEnviados;
    }

    public void setCantidadEnviados(Integer cantidadEnviados) {
        this.cantidadEnviados = cantidadEnviados;
    }

    public Integer getCantidadFallidos() {
        return cantidadFallidos;
    }

    public void setCantidadFallidos(Integer cantidadFallidos) {
        this.cantidadFallidos = cantidadFallidos;
    }
}
