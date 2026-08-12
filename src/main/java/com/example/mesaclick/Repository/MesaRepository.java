package com.example.mesaclick.Repository;

import com.example.mesaclick.Model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MesaRepository extends JpaRepository<Mesa, Long> {

    @Query("SELECT m FROM Mesa m WHERE m.codigoqr_mesa = :codigo")
    Optional<Mesa> buscarPorCodigo(String codigo);

}