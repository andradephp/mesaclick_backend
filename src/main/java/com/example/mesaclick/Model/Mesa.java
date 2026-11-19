
package com.example.mesaclick.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "mesas")
public class Mesa {

    @Id
    @Column(name = "id_mesa")
    private Long id_mesa;

    @Column(name = "numero_mesa")
    private int numero_mesa;

    @Column(name = "estado_mesa")
    private String estado_mesa;

    @Column(name = "codigo_qr_mesa")
    private String codigoQrMesa;

    public Mesa() {
    }

    // ==========================================
    // ID DE LA MESA
    // ==========================================

    public Long getId_mesa() {
        return id_mesa;
    }

    public void setId_mesa(Long id_mesa) {
        this.id_mesa = id_mesa;
    }

    // ==========================================
    // NUMERO DE LA MESA
    // ==========================================

    public int getNumero_mesa() {
        return numero_mesa;
    }

    public void setNumero_mesa(int numero_mesa) {
        this.numero_mesa = numero_mesa;
    }

    // ==========================================
    // ESTADO DE LA MESA
    // ==========================================

    public String getEstado_mesa() {
        return estado_mesa;
    }

    public void setEstado_mesa(String estado_mesa) {
        this.estado_mesa = estado_mesa;
    }

    // ==========================================
    // CODIGO QR DE LA MESA
    // ==========================================

    public String getCodigoQrMesa() {
        return codigoQrMesa;
    }

    public void setCodigoQrMesa(String codigoQrMesa) {
        this.codigoQrMesa = codigoQrMesa;
    }
}
