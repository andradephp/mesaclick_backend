
package com.example.mesaclick.Dto;

import java.time.LocalDateTime;
import java.util.List;

public record AtencionCajaDTO(
        Long idAtencion,
        int numeroMesa,
        String codigoQrMesa,
        String estadoAtencion,
        LocalDateTime fechaApertura,
        double total,
        boolean puedeCobrar,
        List<PedidoMeseroDTO> pedidos
) {
}
