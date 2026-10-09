package com.example.mesaclick.Dto;

import java.time.LocalDateTime;
import java.util.List;

public class PedidoMeseroDTO {


private Long idPedido;
private int numeroMesa;
private Double totalPedido;
private String estadoPedido;
private LocalDateTime fechaPedido;
private List<PersonaPedidoDTO> personas;

public PedidoMeseroDTO() {
}

public PedidoMeseroDTO(
        Long idPedido,
        int numeroMesa,
        Double totalPedido,
        String estadoPedido,
        LocalDateTime fechaPedido,
        List<PersonaPedidoDTO> personas) {
    this.idPedido = idPedido;
    this.numeroMesa = numeroMesa;
    this.totalPedido = totalPedido;
    this.estadoPedido = estadoPedido;
    this.fechaPedido = fechaPedido;
    this.personas = personas;
}

public Long getIdPedido() {
    return idPedido;
}

public int getNumeroMesa() {
    return numeroMesa;
}

public Double getTotalPedido() {
    return totalPedido;
}

public String getEstadoPedido() {
    return estadoPedido;
}

public LocalDateTime getFechaPedido() {
    return fechaPedido;
}

public List<PersonaPedidoDTO> getPersonas() {
    return personas;
}
}


