
package com.example.mesaclick.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "suscripciones",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_suscripciones_correo",
        columnNames = "correo"
    )
)
public class Suscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_suscripcion")
    private Long idSuscripcion;

    @Column(name = "correo", nullable = false, length = 254)
    private String correo;

    @Column(name = "acepta_promociones", nullable = false)
    private Boolean aceptaPromociones = false;

    @Column(name = "confirmada", nullable = false)
    private Boolean confirmada = false;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_consentimiento")
    private LocalDateTime fechaConsentimiento;

    @Column(name = "fecha_confirmacion")
    private LocalDateTime fechaConfirmacion;

    @Column(name = "fecha_cancelacion")
    private LocalDateTime fechaCancelacion;


@Column(name = "token_confirmacion_hash", length = 64)
private String tokenConfirmacionHash;

@Column(name = "token_expiracion")
private LocalDateTime tokenExpiracion;

@Column(name = "fecha_envio_confirmacion")
private LocalDateTime fechaEnvioConfirmacion;


@Column(name = "token_cancelacion_hash", length = 64)
private String tokenCancelacionHash;



    @PrePersist
    public void prePersist() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
    }

    public Long getIdSuscripcion() {
        return idSuscripcion;
    }

    public void setIdSuscripcion(Long idSuscripcion) {
        this.idSuscripcion = idSuscripcion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Boolean getAceptaPromociones() {
        return aceptaPromociones;
    }

    public void setAceptaPromociones(Boolean aceptaPromociones) {
        this.aceptaPromociones = aceptaPromociones;
    }

    public Boolean getConfirmada() {
        return confirmada;
    }

    public void setConfirmada(Boolean confirmada) {
        this.confirmada = confirmada;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getFechaConsentimiento() {
        return fechaConsentimiento;
    }

    public void setFechaConsentimiento(LocalDateTime fechaConsentimiento) {
        this.fechaConsentimiento = fechaConsentimiento;
    }

    public LocalDateTime getFechaConfirmacion() {
        return fechaConfirmacion;
    }

    public void setFechaConfirmacion(LocalDateTime fechaConfirmacion) {
        this.fechaConfirmacion = fechaConfirmacion;
    }

    public LocalDateTime getFechaCancelacion() {
        return fechaCancelacion;
    }

    public void setFechaCancelacion(LocalDateTime fechaCancelacion) {
        this.fechaCancelacion = fechaCancelacion;
    }


public String getTokenConfirmacionHash() {
    return tokenConfirmacionHash;
}

public void setTokenConfirmacionHash(String tokenConfirmacionHash) {
    this.tokenConfirmacionHash = tokenConfirmacionHash;
}

public LocalDateTime getTokenExpiracion() {
    return tokenExpiracion;
}

public void setTokenExpiracion(LocalDateTime tokenExpiracion) {
    this.tokenExpiracion = tokenExpiracion;
}

public LocalDateTime getFechaEnvioConfirmacion() {
    return fechaEnvioConfirmacion;
}

public void setFechaEnvioConfirmacion(LocalDateTime fechaEnvioConfirmacion) {
    this.fechaEnvioConfirmacion = fechaEnvioConfirmacion;
}


public String getTokenCancelacionHash() {
    return tokenCancelacionHash;
}

public void setTokenCancelacionHash(String tokenCancelacionHash) {
    this.tokenCancelacionHash = tokenCancelacionHash;
}


}
