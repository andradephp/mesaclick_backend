
package com.example.mesaclick.Dto;

public class CategoriaAdminRequest {

    private String nombreCategoria;
    private Boolean estadoCategoria;

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public Boolean getEstadoCategoria() {
        return estadoCategoria;
    }

    public void setEstadoCategoria(Boolean estadoCategoria) {
        this.estadoCategoria = estadoCategoria;
    }
}
