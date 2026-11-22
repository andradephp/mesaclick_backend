package com.example.mesaclick.Controller;

import com.example.mesaclick.Dto.MesaRequest;
import com.example.mesaclick.Model.Mesa;
import com.example.mesaclick.Services.MesaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/mesas")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "https://mesaclicks.tech",
    "https://www.mesaclicks.tech"
})
public class AdminMesaController {

    private final MesaService mesaService;

    public AdminMesaController(MesaService mesaService) {
        this.mesaService = mesaService;
    }

    // ==========================================
    // LISTAR TODAS LAS MESAS
    // ==========================================

    @GetMapping
    public ResponseEntity<List<Mesa>> listarMesas() {

        return ResponseEntity.ok(
                mesaService.obtenerTodasLasMesas()
        );
    }

    // ==========================================
    // OBTENER MESA POR ID
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<Mesa> obtenerMesa(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                mesaService.obtenerMesaPorId(id)
        );
    }

    // ==========================================
    // CREAR NUEVA MESA
    // ==========================================

    @PostMapping
    public ResponseEntity<Mesa> crearMesa(
            @RequestBody MesaRequest request
    ) {

        Mesa mesa = mesaService.crearMesa(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mesa);
    }

    // ==========================================
    // EDITAR MESA
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<Mesa> editarMesa(
            @PathVariable Long id,
            @RequestBody MesaRequest request
    ) {

        return ResponseEntity.ok(
                mesaService.editarMesa(id, request)
        );
    }

    // ==========================================
    // ELIMINAR MESA
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarMesa(
            @PathVariable Long id
    ) {

        mesaService.eliminarMesa(id);

        return ResponseEntity.ok(
                Map.of(
                    "mensaje",
                    "Mesa eliminada correctamente."
                )
        );
    }
}