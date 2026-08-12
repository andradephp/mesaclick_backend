package com.example.mesaclick.Repository;

import com.example.mesaclick.Model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MesaRepository extends JpaRepository<Mesa, Long> {

    Optional<Mesa> findByCodigoQrMesa(String codigo);
}