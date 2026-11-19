
package com.example.mesaclick.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pedidos")
public class Pedido {

    // ==========================================
    // IDENTIFICADOR DEL PEDIDO
    // ==========================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long idPedido;

    // ==========================================
    // MESA ASOCIADA AL PEDIDO
    // ==========================================

    @ManyToOne
    @JoinColumn(name = "id_mesa", nullable = false)
    private Mesa mesa;

    // ==========================================
    // ATENCION ASOCIADA AL PEDIDO
    // ==========================================

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_atencion", nullable = true)
    private Atencion atencion;

    // ==========================================
    // INFORMACION DEL PEDIDO
    // ==========================================

    @Column(name = "total_pedido", nullable = false)
    private Double totalPedido;

    @Column(name = "estado_pedido", nullable = false)
    private String estadoPedido;

    @Column(name = "fecha_pedido")
    private LocalDateTime fechaPedido;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public Pedido() {
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public Atencion getAtencion() {
        return atencion;
    }

    public void setAtencion(Atencion atencion) {
        this.atencion = atencion;
    }

    public Double getTotalPedido() {
        return totalPedido;
    }

    public void setTotalPedido(Double totalPedido) {
        this.totalPedido = totalPedido;
    }

    public String getEstadoPedido() {
        return estadoPedido;
    }

    public void setEstadoPedido(String estadoPedido) {
        this.estadoPedido = estadoPedido;
    }

    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }
}
