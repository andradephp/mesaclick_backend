package com.example.mesaclick.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "mesas")
public class Mesa {

    @Id
    private Long id_mesa;

    private int numero_mesa;

    private String estado_mesa;

    @Column(name = "codigo_qr_mesa")
    private String codigoqr_mesa;

    public Mesa() {
    }

    public Long getId_mesa() {
        return id_mesa;
    }

    public int getNumero_mesa() {
        return numero_mesa;
    }

    public String getEstado_mesa() {
        return estado_mesa;
    }

    public String getCodigoqr_mesa() {
        return codigoqr_mesa;
    }
}