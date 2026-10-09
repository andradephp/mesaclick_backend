package com.example.mesaclick.Dto;

public class ProductoPedidoDTO {


private String nombreProducto;
private Integer cantidad;

public ProductoPedidoDTO() {
}

public ProductoPedidoDTO(String nombreProducto, Integer cantidad) {
    this.nombreProducto = nombreProducto;
    this.cantidad = cantidad;
}

public String getNombreProducto() {
    return nombreProducto;
}

public Integer getCantidad() {
    return cantidad;
}


}
