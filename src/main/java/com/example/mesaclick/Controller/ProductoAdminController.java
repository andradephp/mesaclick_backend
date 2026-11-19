
package com.example.mesaclick.Controller;

import com.example.mesaclick.Dto.ProductoAdminRequest;
import com.example.mesaclick.Model.Producto;
import com.example.mesaclick.Services.ProductoAdminService;
import com.example.mesaclick.Services.ProductoImagenService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/productos")
public class ProductoAdminController {

    private final ProductoAdminService productoAdminService;
    private final ProductoImagenService productoImagenService;

    public ProductoAdminController(
            ProductoAdminService productoAdminService,
            ProductoImagenService productoImagenService
    ) {
        this.productoAdminService = productoAdminService;
        this.productoImagenService = productoImagenService;
    }

    // ========================================
    // LISTAR TODOS LOS PRODUCTOS
    // ========================================

    @GetMapping
    public List<Producto> listarProductos() {
        return productoAdminService.listarProductos();
    }

    // ========================================
    // CREAR PRODUCTO
    // ========================================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crearProducto(
            @RequestBody ProductoAdminRequest request
    ) {
        return productoAdminService.crearProducto(request);
    }

    // ========================================
    // ACTUALIZAR PRODUCTO
    // ========================================

    @PutMapping("/{id}")
    public Producto actualizarProducto(
            @PathVariable Long id,
            @RequestBody ProductoAdminRequest request
    ) {
        return productoAdminService.actualizarProducto(id, request);
    }

    // ========================================
    // ACTIVAR / DESACTIVAR PRODUCTO
    // ========================================

    @PatchMapping("/{id}/estado")
    public Producto cambiarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request
    ) {
        return productoAdminService.cambiarEstado(
                id,
                request.get("estadoProducto")
        );
    }

    // ========================================
    // SUBIR O REEMPLAZAR IMAGEN
    // ========================================

    @PostMapping(
            value = "/{id}/imagen",
            consumes = "multipart/form-data"
    )
    public Producto subirImagen(
            @PathVariable Long id,
            @RequestParam("imagen") MultipartFile imagen
    ) {
        return productoImagenService.subirImagen(id, imagen);
    }
}
