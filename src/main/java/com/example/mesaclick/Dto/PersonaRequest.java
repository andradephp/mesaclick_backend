package com.example.mesaclick.Dto;

import java.util.List;

public class PersonaRequest {

    private String nombre;

    private List<ProductoRequest> productos;

    public PersonaRequest() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<ProductoRequest> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoRequest> productos) {
        this.productos = productos;
    }
}