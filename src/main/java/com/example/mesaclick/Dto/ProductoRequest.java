package com.example.mesaclick.Dto;

public class ProductoRequest {

    private Long idProducto;

    private Integer cantidad;

    public ProductoRequest() {
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}