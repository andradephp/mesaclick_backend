package com.example.mesaclick.Controller;

import com.example.mesaclick.Model.Mesa;
import com.example.mesaclick.Services.MesaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MesaController {

    private final MesaService mesaService;

    public MesaController(MesaService mesaService) {
        this.mesaService = mesaService;
    }

    @GetMapping("/api/mesas")
    public List<Mesa> obtenerMesas() {
        return mesaService.obtenerTodasLasMesas();
    }

    @GetMapping("/api/mesas/codigo/{codigo}")
    public Mesa obtenerMesaPorCodigo(@PathVariable String codigo) {
        return mesaService.obtenerMesaPorCodigo(codigo);
    }
}