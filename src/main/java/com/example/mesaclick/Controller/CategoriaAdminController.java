
package com.example.mesaclick.Controller;

import com.example.mesaclick.Dto.CategoriaAdminRequest;
import com.example.mesaclick.Model.Categoria;
import com.example.mesaclick.Services.CategoriaAdminService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/categorias")
public class CategoriaAdminController {

    private final CategoriaAdminService categoriaAdminService;

    public CategoriaAdminController(
            CategoriaAdminService categoriaAdminService) {
        this.categoriaAdminService = categoriaAdminService;

         System.out.println(
        "MESACLICK: CategoriaAdminController registrado"
    );

    }

    @GetMapping
    public List<Categoria> listarCategorias() {
        return categoriaAdminService.listarCategorias();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Categoria crearCategoria(
            @RequestBody CategoriaAdminRequest request) {
        return categoriaAdminService.crearCategoria(request);
    }

    @PutMapping("/{id}")
    public Categoria actualizarCategoria(
            @PathVariable Integer id,
            @RequestBody CategoriaAdminRequest request) {
        return categoriaAdminService.actualizarCategoria(id, request);
    }

    @PatchMapping("/{id}/estado")
    public Categoria cambiarEstado(
            @PathVariable Integer id,
            @RequestBody Map<String, Boolean> request) {
        return categoriaAdminService.cambiarEstado(
                id, request.get("estadoCategoria"));
    }
}
