
package com.example.mesaclick.Dto;

import java.util.List;

public class PersonaPedidoDTO {

    private String nombrePersona;
    private Double subtotal;
    private List<ProductoPedidoDTO> productos;

    public PersonaPedidoDTO() {
    }

    // Constructor anterior: mantener compatibilidad
    public PersonaPedidoDTO(
            String nombrePersona,
            List<ProductoPedidoDTO> productos
    ) {
        this.nombrePersona = nombrePersona;
        this.productos = productos;
    }

    // Constructor con subtotal
    public PersonaPedidoDTO(
            String nombrePersona,
            Double subtotal,
            List<ProductoPedidoDTO> productos
    ) {
        this.nombrePersona = nombrePersona;
        this.subtotal = subtotal;
        this.productos = productos;
    }

    public String getNombrePersona() {
        return nombrePersona;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public List<ProductoPedidoDTO> getProductos() {
        return productos;
    }
}
