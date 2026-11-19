
package com.example.mesaclick.Repository;

import com.example.mesaclick.Model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoriaRepository
        extends JpaRepository<Categoria, Integer> {
                List<Categoria> findByEstadoCategoriaTrue();
}
