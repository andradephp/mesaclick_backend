
package com.example.mesaclick.Services;

import com.example.mesaclick.Dto.ProductoAdminRequest;
import com.example.mesaclick.Model.Categoria;
import com.example.mesaclick.Model.Producto;
import com.example.mesaclick.Repository.CategoriaRepository;
import com.example.mesaclick.Repository.ProductoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductoAdminService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoAdminService(
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository
    ) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    // ========================================
    // LISTAR PRODUCTOS
    // ========================================

    @Transactional(readOnly = true)
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    // ========================================
    // CREAR PRODUCTO
    // ========================================

    @Transactional
    public Producto crearProducto(ProductoAdminRequest request) {

        Producto producto = new Producto();

        asignarDatos(producto, request, true);

        return productoRepository.save(producto);
    }

    // ========================================
    // ACTUALIZAR PRODUCTO
    // ========================================

    @Transactional
    public Producto actualizarProducto(
            Long idProducto,
            ProductoAdminRequest request
    ) {

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El producto no existe"
                ));

        asignarDatos(producto, request, false);

        return productoRepository.save(producto);
    }

    // ========================================
    // ACTIVAR / DESACTIVAR PRODUCTO
    // ========================================

    @Transactional
    public Producto cambiarEstado(
            Long idProducto,
            Boolean estado
    ) {

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El producto no existe"
                ));

        if (estado == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debes indicar el estado del producto"
            );
        }

        producto.setEstadoProducto(estado);

        return productoRepository.save(producto);
    }

    // ========================================
    // VALIDAR Y ASIGNAR DATOS
    // ========================================

    private void asignarDatos(
            Producto producto,
            ProductoAdminRequest request,
            boolean esNuevo
    ) {

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debes enviar los datos del producto"
            );
        }

        // VALIDAR NOMBRE

        if (request.getNombreProducto() == null ||
                request.getNombreProducto().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre del producto es obligatorio"
            );
        }

        if (request.getNombreProducto().trim().length() > 255) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre no puede superar 255 caracteres"
            );
        }

        // VALIDAR PRECIO

        Double precio = request.getPrecioProducto();

        if (precio == null ||
                !Double.isFinite(precio) ||
                precio <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El precio debe ser mayor que cero"
            );
        }

        // VALIDAR CATEGORIA

        if (request.getIdCategoria() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debes seleccionar una categoría"
            );
        }

        Categoria categoria = categoriaRepository
                .findById(request.getIdCategoria())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La categoría no existe"
                ));

        if (!Boolean.TRUE.equals(
                categoria.getEstadoCategoria()
        )) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La categoría está desactivada"
            );
        }

        // VALIDAR IMAGEN

        String imagen = request.getImagenProducto();

        if (imagen != null && imagen.length() > 255) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La URL de imagen supera 255 caracteres"
            );
        }

        // ASIGNAR INFORMACION

        producto.setNombreProducto(
                request.getNombreProducto().trim()
        );

        producto.setDescripcionProducto(
                request.getDescripcionProducto()
        );

        producto.setIngredientesProducto(
                request.getIngredientesProducto()
        );

        producto.setPrecioProducto(precio);

        producto.setCategoria(categoria);

        // ========================================
        // CONSERVAR IMAGEN EXISTENTE
        // ========================================

        if (esNuevo) {

            // Al crear, se puede indicar una imagen inicial.
            producto.setImagenProducto(imagen);

        } else {

            // Al editar, un valor null no borra la imagen.
            // Las imágenes subidas se gestionan por
            // el endpoint /{id}/imagen.

            if (imagen != null) {

                String imagenActual =
                        producto.getImagenProducto();

                // Si ya existe una imagen subida,
                // conservarla frente a cambios
                // enviados desde el formulario.

                boolean imagenSubida =
                        imagenActual != null &&
                        imagenActual.startsWith(
                                "/imagenes/productos/"
                        );

                if (!imagenSubida) {
                    producto.setImagenProducto(imagen);
                }
            }
        }

        // ========================================
        // ESTADO DEL PRODUCTO
        // ========================================

        if (request.getEstadoProducto() != null) {

            producto.setEstadoProducto(
                    request.getEstadoProducto()
            );

        } else if (producto.getEstadoProducto() == null) {

            producto.setEstadoProducto(true);
        }
    }
}
