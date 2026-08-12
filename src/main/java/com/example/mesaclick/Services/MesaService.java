package com.example.mesaclick.Services;

import com.example.mesaclick.Model.Mesa;
import com.example.mesaclick.Repository.MesaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MesaService {

    private final MesaRepository mesaRepository;

    public MesaService(MesaRepository mesaRepository) {
        this.mesaRepository = mesaRepository;
    }

    public List<Mesa> obtenerTodasLasMesas() {
        return mesaRepository.findAll();
    }

    public Mesa obtenerMesaPorCodigo(String codigo) {
        return mesaRepository.findByCodigoQrMesa(codigo)
            .orElse(null);
    }
}