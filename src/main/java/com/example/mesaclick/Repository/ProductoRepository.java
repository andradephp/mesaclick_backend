package com.example.mesaclick.Repository;

import com.example.mesaclick.Model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}