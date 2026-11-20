
package com.example.mesaclick.Repository;

import com.example.mesaclick.Model.Promocion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PromocionRepository
        extends JpaRepository<Promocion, Long> {

    // Obtener todas las promociones de un producto
    List<Promocion> findByProducto_IdProducto(
            Long idProducto
    );

    // Obtener promociones activas y vigentes
    @Query("""
        SELECT p
        FROM Promocion p
        WHERE p.estadoPromocion = true
        AND p.fechaInicio <= :fechaActual
        AND p.fechaFin >= :fechaActual
        """)
    List<Promocion> obtenerPromocionesVigentes(
            @Param("fechaActual") LocalDateTime fechaActual
    );

    // Obtener una promoción vigente de un producto
    // Se prioriza el mayor porcentaje de descuento.
    @Query("""
        SELECT p
        FROM Promocion p
        WHERE p.producto.idProducto = :idProducto
        AND p.estadoPromocion = true
        AND p.fechaInicio <= :fechaActual
        AND p.fechaFin >= :fechaActual
        ORDER BY p.porcentajeDescuento DESC, p.idPromocion ASC
        """)
    List<Promocion> buscarPromocionesVigentesPorProducto(
            @Param("idProducto") Long idProducto,
            @Param("fechaActual") LocalDateTime fechaActual
    );

    default Optional<Promocion> obtenerPromocionVigente(
            Long idProducto,
            LocalDateTime fechaActual
    ) {
        return buscarPromocionesVigentesPorProducto(
                idProducto,
                fechaActual
        ).stream().findFirst();
    }

    // =====================================================
    // CAMPAÑAS DE CORREO
    // Cargar promociones junto con sus productos.
    // Evita LazyInitializationException al generar el HTML.
    // =====================================================

    @Query("""
        SELECT DISTINCT p
        FROM Promocion p
        JOIN FETCH p.producto
        WHERE p.idPromocion IN :idsPromociones
        """)
    List<Promocion> obtenerPromocionesConProducto(
            @Param("idsPromociones") List<Long> idsPromociones
    );
}
