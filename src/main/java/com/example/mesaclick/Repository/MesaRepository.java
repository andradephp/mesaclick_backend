package com.example.mesaclick.Repository;

import com.example.mesaclick.Model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface MesaRepository extends JpaRepository<Mesa, Long> {

    // BUSCAR MESA POR CÓDIGO QR
    Optional<Mesa> findByCodigoQrMesa(String codigo);

    // BLOQUEAR MESA DURANTE UNA TRANSACCIÓN
    @Query(
        value = """
            SELECT *
            FROM mesas
            WHERE id_mesa = :idMesa
            FOR UPDATE
            """,
        nativeQuery = true
    )
    Optional<Mesa> bloquearMesaPorId(
        @Param("idMesa") Long idMesa
    );

    // VERIFICAR SI EL NÚMERO DE MESA YA EXISTE
    @Query("""
        SELECT COUNT(m) > 0
        FROM Mesa m
        WHERE m.numero_mesa = :numero
        """)
    boolean existeNumeroMesa(
        @Param("numero") int numero
    );

    // VALIDAR NÚMERO AL EDITAR UNA MESA
    @Query("""
        SELECT COUNT(m) > 0
        FROM Mesa m
        WHERE m.numero_mesa = :numero
        AND m.id_mesa <> :id
        """)
    boolean existeNumeroMesaExceptoId(
        @Param("numero") int numero,
        @Param("id") Long id
    );

    // VERIFICAR SI EL CÓDIGO QR YA EXISTE
    boolean existsByCodigoQrMesa(String codigo);

    // LISTAR MESAS ORDENADAS POR NÚMERO
    @Query("""
        SELECT m
        FROM Mesa m
        ORDER BY m.numero_mesa ASC
        """)
    List<Mesa> listarMesasOrdenadas();

    // VERIFICAR HISTORIAL DE PEDIDOS
    @Query(
        value = """
            SELECT COUNT(*)
            FROM pedidos
            WHERE id_mesa = :idMesa
            """,
        nativeQuery = true
    )
    long contarPedidosPorMesa(
        @Param("idMesa") Long idMesa
    );

    // VERIFICAR HISTORIAL DE ATENCIONES
    @Query(
        value = """
            SELECT COUNT(*)
            FROM atenciones
            WHERE id_mesa = :idMesa
            """,
        nativeQuery = true
    )
    long contarAtencionesPorMesa(
        @Param("idMesa") Long idMesa
    );
}