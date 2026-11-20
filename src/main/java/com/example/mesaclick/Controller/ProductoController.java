
package com.example.mesaclick.Controller;

import com.example.mesaclick.Model.Producto;
import com.example.mesaclick.Model.Promocion;
import com.example.mesaclick.Services.ProductoService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(
            ProductoService productoService
    ) {
        this.productoService = productoService;
    }

    // ==========================================
    // OBTENER PRODUCTOS CON PROMOCIONES
    // ==========================================

    @GetMapping
    public List<ProductoRespuesta> obtenerProductos() {

        return productoService.obtenerTodos()
                .stream()
                .map(this::convertirProducto)
                .toList();
    }

    // ==========================================
    // CONVERTIR PRODUCTO A RESPUESTA
    // ==========================================

    private ProductoRespuesta convertirProducto(
            Producto producto
    ) {

        Optional<Promocion> promocion =
                productoService.obtenerPromocion(
                        producto.getIdProducto()
                );

        double precioFinal =
                productoService.obtenerPrecioFinal(producto);

        int porcentajeDescuento = promocion
                .map(Promocion::getPorcentajeDescuento)
                .orElse(0);

        return new ProductoRespuesta(
                producto.getIdProducto(),
                producto.getNombreProducto(),
                producto.getDescripcionProducto(),
                producto.getIngredientesProducto(),
                producto.getPrecioProducto(),
                producto.getImagenProducto(),
                producto.getEstadoProducto(),
                producto.getCategoria(),
                !promocion.isEmpty(),
                porcentajeDescuento,
                precioFinal,
                promocion
                        .map(Promocion::getNombrePromocion)
                        .orElse(null)
        );
    }

    // ==========================================
    // DTO DE RESPUESTA
    // ==========================================

    public record ProductoRespuesta(
            Long idProducto,
            String nombreProducto,
            String descripcionProducto,
            String ingredientesProducto,
            Double precioProducto,
            String imagenProducto,
            Boolean estadoProducto,
            com.example.mesaclick.Model.Categoria categoria,
            Boolean tienePromocion,
            Integer porcentajeDescuento,
            Double precioFinal,
            String nombrePromocion
    ) {}
}
