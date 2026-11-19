package com.example.mesaclick.Dto;

import java.util.List;

public class PersonaPedidoDTO {


private String nombrePersona;
private List<ProductoPedidoDTO> productos;

public PersonaPedidoDTO() {
}

public PersonaPedidoDTO(
        String nombrePersona,
        List<ProductoPedidoDTO> productos) {
    this.nombrePersona = nombrePersona;
    this.productos = productos;
}

public String getNombrePersona() {
    return nombrePersona;
}

public List<ProductoPedidoDTO> getProductos() {
    return productos;
}


}
