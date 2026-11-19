package com.example.mesaclick.Repository;

import com.example.mesaclick.Model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByEstadoProductoTrueAndCategoria_EstadoCategoriaTrue();
}