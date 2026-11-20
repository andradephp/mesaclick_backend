
package com.example.mesaclick.Services;

import com.example.mesaclick.Model.Producto;
import com.example.mesaclick.Model.Promocion;
import com.example.mesaclick.Repository.ProductoRepository;
import com.example.mesaclick.Repository.PromocionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PromocionService {

    private final PromocionRepository promocionRepository;
    private final ProductoRepository productoRepository;

    public PromocionService(
            PromocionRepository promocionRepository,
            ProductoRepository productoRepository
    ) {
        this.promocionRepository = promocionRepository;
        this.productoRepository = productoRepository;
    }

    // ==========================================
    // OBTENER TODAS LAS PROMOCIONES
    // ==========================================

    @Transactional(readOnly = true)
    public List<Promocion> obtenerTodas() {
        return promocionRepository.findAll();
    }

    // ==========================================
    // OBTENER PROMOCIONES VIGENTES
    // ==========================================

    @Transactional(readOnly = true)
    public List<Promocion> obtenerVigentes() {
        return promocionRepository.obtenerPromocionesVigentes(
                LocalDateTime.now()
        );
    }

    // ==========================================
    // BUSCAR PROMOCIÓN POR ID
    // ==========================================

    @Transactional(readOnly = true)
    public Promocion obtenerPorId(Long idPromocion) {

        return promocionRepository.findById(idPromocion)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La promoción no existe"
                        )
                );
    }

    // ==========================================
    // OBTENER PROMOCIÓN VIGENTE DE UN PRODUCTO
    // ==========================================

    @Transactional(readOnly = true)
    public Optional<Promocion> obtenerPromocionVigente(
            Long idProducto
    ) {

        return promocionRepository.obtenerPromocionVigente(
                idProducto,
                LocalDateTime.now()
        );
    }

    // ==========================================
    // CALCULAR PRECIO CON DESCUENTO
    // ==========================================

    @Transactional(readOnly = true)
    public double calcularPrecioFinal(Producto producto) {

        if (producto == null ||
                producto.getPrecioProducto() == null) {
            throw new IllegalArgumentException(
                    "El producto no tiene un precio válido"
            );
        }

        double precioOriginal = producto.getPrecioProducto();

        if (!Double.isFinite(precioOriginal) ||
                precioOriginal < 0) {
            throw new IllegalArgumentException(
                    "El precio del producto es inválido"
            );
        }

        Optional<Promocion> promocion =
                obtenerPromocionVigente(
                        producto.getIdProducto()
                );

        if (promocion.isEmpty()) {
            return precioOriginal;
        }

        int porcentaje =
                promocion.get().getPorcentajeDescuento();

        if (porcentaje < 1 || porcentaje > 100) {
            throw new IllegalStateException(
                    "La promoción tiene un porcentaje inválido"
            );
        }

        double descuento =
                precioOriginal * porcentaje / 100.0;

        double precioFinal =
                precioOriginal - descuento;

        // Redondear a dos decimales
        return Math.round(precioFinal * 100.0) / 100.0;
    }

    // ==========================================
    // CREAR PROMOCIÓN
    // ==========================================

    @Transactional
    public Promocion crearPromocion(Promocion promocion) {

        if (promocion == null) {
            throw new IllegalArgumentException(
                    "Los datos de la promoción son obligatorios"
            );
        }

        validarPromocion(promocion);

        Long idProducto =
                promocion.getProducto().getIdProducto();

        Producto producto = productoRepository
                .findById(idProducto)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El producto no existe"
                        )
                );

        // Evitar crear una promoción con ID proporcionado
        promocion.setIdPromocion(null);
        promocion.setProducto(producto);

        return promocionRepository.save(promocion);
    }

    // ==========================================
    // ACTIVAR O DESACTIVAR PROMOCIÓN
    // ==========================================

    @Transactional
    public Promocion cambiarEstado(
            Long idPromocion,
            boolean activo
    ) {

        Promocion promocion =
                obtenerPorId(idPromocion);

        promocion.setEstadoPromocion(activo);

        return promocionRepository.save(promocion);
    }

    // ==========================================
    // VALIDAR PROMOCIÓN
    // ==========================================

    private void validarPromocion(Promocion promocion) {

        if (promocion.getNombrePromocion() == null ||
                promocion.getNombrePromocion().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre de la promoción es obligatorio"
            );
        }

        if (promocion.getNombrePromocion().length() > 150) {
            throw new IllegalArgumentException(
                    "El nombre de la promoción es demasiado largo"
            );
        }

        if (promocion.getProducto() == null ||
                promocion.getProducto().getIdProducto() == null) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un producto"
            );
        }

        Integer porcentaje =
                promocion.getPorcentajeDescuento();

        if (porcentaje == null ||
                porcentaje < 1 ||
                porcentaje > 100) {
            throw new IllegalArgumentException(
                    "El descuento debe estar entre 1% y 100%"
            );
        }

        if (promocion.getFechaInicio() == null ||
                promocion.getFechaFin() == null) {
            throw new IllegalArgumentException(
                    "Debes indicar las fechas de la promoción"
            );
        }

        if (!promocion.getFechaFin()
                .isAfter(promocion.getFechaInicio())) {
            throw new IllegalArgumentException(
                    "La fecha de finalización debe ser posterior al inicio"
            );
        }

        if (promocion.getEstadoPromocion() == null) {
            promocion.setEstadoPromocion(true);
        }
    }
}
