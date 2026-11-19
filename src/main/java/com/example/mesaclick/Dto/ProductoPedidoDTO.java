
package com.example.mesaclick.Dto;

public class ProductoPedidoDTO {

    private String nombreProducto;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;

    public ProductoPedidoDTO() {
    }

    // Constructor anterior: mantener compatibilidad
    public ProductoPedidoDTO(
            String nombreProducto,
            Integer cantidad
    ) {
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
    }

    // Constructor con precios
    public ProductoPedidoDTO(
            String nombreProducto,
            Integer cantidad,
            Double precioUnitario,
            Double subtotal
    ) {
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public Double getSubtotal() {
        return subtotal;
    }
}
