package com.example.mesaclick.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "pedido_personas")
public class PedidoPersona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido_persona")
    private Long idPedidoPersona;

    @ManyToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @Column(name = "nombre_persona", nullable = false)
    private String nombrePersona;

    @Column(name = "subtotal", nullable = false)
    private Double subtotal;

    public PedidoPersona() {
    }

    public Long getIdPedidoPersona() {
        return idPedidoPersona;
    }

    public void setIdPedidoPersona(Long idPedidoPersona) {
        this.idPedidoPersona = idPedidoPersona;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public String getNombrePersona() {
        return nombrePersona;
    }

    public void setNombrePersona(String nombrePersona) {
        this.nombrePersona = nombrePersona;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}   