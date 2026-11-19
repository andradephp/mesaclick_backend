
package com.example.mesaclick.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "atenciones")
public class Atencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_atencion")
    private Long idAtencion;

    // Mesa a la que pertenece la atención
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mesa", nullable = false)
    private Mesa mesa;

    // ACTIVA o CERRADA
    @Column(name = "estado_atencion", nullable = false)
    private String estadoAtencion = "ACTIVA";

    // Fecha de apertura
    @Column(name = "fecha_apertura", nullable = false)
    private LocalDateTime fechaApertura;

    // Fecha de cierre
    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    // EFECTIVO, TARJETA o TRANSFERENCIA
    @Column(name = "metodo_pago")
    private String metodoPago;

    // Total registrado al cerrar la cuenta
    @Column(name = "total_pagado")
    private Double totalPagado;

    // Constructor
    public Atencion() {
        this.fechaApertura = LocalDateTime.now();
    }

    public Long getIdAtencion() {
        return idAtencion;
    }

    public void setIdAtencion(Long idAtencion) {
        this.idAtencion = idAtencion;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public String getEstadoAtencion() {
        return estadoAtencion;
    }

    public void setEstadoAtencion(String estadoAtencion) {
        this.estadoAtencion = estadoAtencion;
    }

    public LocalDateTime getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(LocalDateTime fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Double getTotalPagado() {
        return totalPagado;
    }

    public void setTotalPagado(Double totalPagado) {
        this.totalPagado = totalPagado;
    }
}
