package com.example.mesaclick.Dto;

import java.util.List;

public class PedidoRequest {

    private Long idMesa;

    private List<PersonaRequest> personas;

    public PedidoRequest() {
    }

    public Long getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(Long idMesa) {
        this.idMesa = idMesa;
    }

    public List<PersonaRequest> getPersonas() {
        return personas;
    }

    public void setPersonas(List<PersonaRequest> personas) {
        this.personas = personas;
    }
}