
package com.example.mesaclick.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "campana_envios",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_campana_envio_suscripcion",
        columnNames = {"id_campana", "id_suscripcion"}
    )
)
public class CampanaEnvio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_envio")
    private Long idEnvio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_campana", nullable = false)
    private CampanaCorreo campana;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_suscripcion", nullable = false)
    private Suscripcion suscripcion;

    @Column(nullable = false, length = 30)
    private String estado = "PENDIENTE";

    @Column(name = "fecha_intento")
    private LocalDateTime fechaIntento;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "detalle_error", length = 500)
    private String detalleError;

    public Long getIdEnvio() {
        return idEnvio;
    }

    public CampanaCorreo getCampana() {
        return campana;
    }

    public void setCampana(CampanaCorreo campana) {
        this.campana = campana;
    }

    public Suscripcion getSuscripcion() {
        return suscripcion;
    }

    public void setSuscripcion(Suscripcion suscripcion) {
        this.suscripcion = suscripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaIntento() {
        return fechaIntento;
    }

    public void setFechaIntento(LocalDateTime fechaIntento) {
        this.fechaIntento = fechaIntento;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public String getDetalleError() {
        return detalleError;
    }

    public void setDetalleError(String detalleError) {
        this.detalleError = detalleError;
    }
}
