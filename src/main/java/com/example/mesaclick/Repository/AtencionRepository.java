
package com.example.mesaclick.Repository;

import com.example.mesaclick.Model.Atencion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AtencionRepository
        extends JpaRepository<Atencion, Long> {

    // ==========================================
    // BUSCAR ATENCION ACTIVA DE UNA MESA
    // ==========================================

    @Query("""
        SELECT a
        FROM Atencion a
        WHERE a.mesa.id_mesa = :idMesa
        AND a.estadoAtencion = :estadoAtencion
        """)
    Optional<Atencion> buscarAtencionPorMesaYEstado(
            @Param("idMesa") Long idMesa,
            @Param("estadoAtencion") String estadoAtencion
    );

    // ==========================================
    // CONSULTAR ATENCIONES POR ESTADO
    // ==========================================

    List<Atencion> findByEstadoAtencion(
            String estadoAtencion
    );

    // ==========================================
    // BLOQUEAR ATENCION DURANTE EL COBRO
    // COMPATIBLE CON MARIADB
    // ==========================================

    @Query(
        value = """
            SELECT *
            FROM atenciones
            WHERE id_atencion = :idAtencion
            FOR UPDATE
            """,
        nativeQuery = true
    )
    Optional<Atencion> bloquearAtencionPorId(
            @Param("idAtencion") Long idAtencion
    );

    // Contar atenciones por estado
long countByEstadoAtencion(String estadoAtencion);

// Sumar el dinero de atenciones cerradas
@Query("""
    SELECT COALESCE(SUM(a.totalPagado), 0)
    FROM Atencion a
    WHERE a.estadoAtencion = 'CERRADA'
    AND a.totalPagado IS NOT NULL
    """)
Double obtenerTotalVentas();

// Calcular el promedio por atención pagada
@Query("""
    SELECT COALESCE(AVG(a.totalPagado), 0)
    FROM Atencion a
    WHERE a.estadoAtencion = 'CERRADA'
    AND a.totalPagado IS NOT NULL
    """)
Double obtenerTicketPromedio();

@Query("""
    SELECT FUNCTION('DATE', a.fechaCierre),
           COALESCE(SUM(a.totalPagado), 0)
    FROM Atencion a
    WHERE a.estadoAtencion = 'CERRADA'
      AND a.fechaCierre >= :desde
      AND a.fechaCierre < :hasta
      AND a.totalPagado IS NOT NULL
    GROUP BY FUNCTION('DATE', a.fechaCierre)
    ORDER BY FUNCTION('DATE', a.fechaCierre)
    """)
List<Object[]> obtenerVentasPorFecha(
        @Param("desde") LocalDateTime desde,
        @Param("hasta") LocalDateTime hasta
);

}
