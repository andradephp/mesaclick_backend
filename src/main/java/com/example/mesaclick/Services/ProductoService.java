
package com.example.mesaclick.Services;

import com.example.mesaclick.Model.Producto;
import com.example.mesaclick.Model.Promocion;
import com.example.mesaclick.Repository.ProductoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final PromocionService promocionService;

    public ProductoService(
            ProductoRepository productoRepository,
            PromocionService promocionService
    ) {
        this.productoRepository = productoRepository;
        this.promocionService = promocionService;
    }

    // ==========================================
    // OBTENER PRODUCTOS DISPONIBLES
    // ==========================================

    @Transactional(readOnly = true)
    public List<Producto> obtenerTodos() {

        return productoRepository
                .findByEstadoProductoTrueAndCategoria_EstadoCategoriaTrue();
    }

    // ==========================================
    // OBTENER PROMOCIÓN VIGENTE
    // ==========================================

    @Transactional(readOnly = true)
    public Optional<Promocion> obtenerPromocion(
            Long idProducto
    ) {

        return promocionService.obtenerPromocionVigente(
                idProducto
        );
    }

    // ==========================================
    // CALCULAR PRECIO FINAL
    // ==========================================

    @Transactional(readOnly = true)
    public double obtenerPrecioFinal(Producto producto) {

        return promocionService.calcularPrecioFinal(
                producto
        );
    }

    // ==========================================
    // CONSULTAR SI TIENE PROMOCIÓN
    // ==========================================

    @Transactional(readOnly = true)
    public boolean tienePromocion(Long idProducto) {

        return obtenerPromocion(idProducto).isPresent();
    }
}
