
package com.example.mesaclick.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "tokens_cancelacion_correo",
    indexes = {
        @Index(
            name = "idx_token_cancelacion_suscripcion",
            columnList = "id_suscripcion"
        )
    }
)
public class TokenCancelacionCorreo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_token")
    private Long idToken;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "id_suscripcion",
        nullable = false
    )
    private Suscripcion suscripcion;

    @Column(
        name = "token_hash",
        nullable = false,
        unique = true,
        length = 64
    )
    private String tokenHash;

    @Column(
        name = "fecha_creacion",
        nullable = false
    )
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void antesDeGuardar() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }

    public Long getIdToken() {
        return idToken;
    }

    public void setIdToken(Long idToken) {
        this.idToken = idToken;
    }

    public Suscripcion getSuscripcion() {
        return suscripcion;
    }

    public void setSuscripcion(Suscripcion suscripcion) {
        this.suscripcion = suscripcion;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
