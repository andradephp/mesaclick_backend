package com.example.mesaclick.Services;

import com.example.mesaclick.Dto.MesaRequest;
import com.example.mesaclick.Model.Mesa;
import com.example.mesaclick.Repository.MesaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.UUID;

@Service
public class MesaService {

    private final MesaRepository mesaRepository;

    public MesaService(MesaRepository mesaRepository) {
        this.mesaRepository = mesaRepository;
    }

    // ==========================================
    // OBTENER TODAS LAS MESAS
    // ==========================================

    public List<Mesa> obtenerTodasLasMesas() {
        return mesaRepository.listarMesasOrdenadas();
    }

    // ==========================================
    // OBTENER MESA POR CODIGO QR
    // ==========================================

    public Mesa obtenerMesaPorCodigo(String codigo) {
        return mesaRepository.findByCodigoQrMesa(codigo)
                .orElse(null);
    }

    // ==========================================
    // OBTENER ESTADO POR CODIGO
    // ==========================================

    public String obtenerEstadoPorCodigo(String codigo) {

        Mesa mesa = obtenerMesaPorCodigo(codigo);

        if (mesa != null) {
            return mesa.getEstado_mesa();
        }

        return null;
    }

    // ==========================================
    // OBTENER MESA POR ID
    // ==========================================

    public Mesa obtenerMesaPorId(Long id) {

        return mesaRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "La mesa no existe."
                        )
                );
    }

    // ==========================================
    // CREAR MESA
    // ==========================================

    @Transactional
    public Mesa crearMesa(MesaRequest request) {

        validarNumeroMesa(request.getNumeroMesa());

        int numero = request.getNumeroMesa();

        if (mesaRepository.existeNumeroMesa(numero)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una mesa con ese número."
            );
        }

        Mesa mesa = new Mesa();

        mesa.setNumero_mesa(numero);
        mesa.setEstado_mesa("DISPONIBLE");
        mesa.setCodigoQrMesa(generarCodigoQr());

        return guardarMesa(mesa);
    }

    // ==========================================
    // EDITAR MESA
    // ==========================================

    @Transactional
    public Mesa editarMesa(Long id, MesaRequest request) {

        validarNumeroMesa(request.getNumeroMesa());

        Mesa mesa = obtenerMesaBloqueada(id);

        if (!"DISPONIBLE".equalsIgnoreCase(
                mesa.getEstado_mesa())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede editar una mesa ocupada."
            );
        }

        int numero = request.getNumeroMesa();

        if (mesaRepository.existeNumeroMesaExceptoId(
                numero, id)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una mesa con ese número."
            );
        }

        mesa.setNumero_mesa(numero);

        // El código QR original se conserva.

        return guardarMesa(mesa);
    }

    // ==========================================
    // ELIMINAR MESA
    // ==========================================

    @Transactional
    public void eliminarMesa(Long id) {

        Mesa mesa = obtenerMesaBloqueada(id);

        if (!"DISPONIBLE".equalsIgnoreCase(
                mesa.getEstado_mesa())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar una mesa ocupada."
            );
        }

        long pedidos =
                mesaRepository.contarPedidosPorMesa(id);

        long atenciones =
                mesaRepository.contarAtencionesPorMesa(id);

        if (pedidos > 0 || atenciones > 0) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar la mesa porque "
                    + "tiene historial de pedidos o atenciones."
            );
        }

        try {

            mesaRepository.delete(mesa);
            mesaRepository.flush();

        } catch (DataIntegrityViolationException e) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La mesa tiene registros relacionados "
                    + "y no puede eliminarse."
            );
        }
    }

    // ==========================================
    // GENERAR CODIGO QR UNICO Y PERMANENTE
    // ==========================================

    private String generarCodigoQr() {

        return "MESA-" + UUID.randomUUID()
                .toString()
                .toUpperCase(java.util.Locale.ROOT);
    }

    // ==========================================
    // VALIDAR NUMERO DE MESA
    // ==========================================

    private void validarNumeroMesa(Integer numero) {

        if (numero == null || numero <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El número de mesa debe ser mayor que cero."
            );
        }
    }

    // ==========================================
    // BLOQUEAR MESA DURANTE UNA OPERACION
    // ==========================================

    private Mesa obtenerMesaBloqueada(Long id) {

        return mesaRepository.bloquearMesaPorId(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "La mesa no existe."
                        )
                );
    }

    // ==========================================
    // GUARDAR MESA Y CONTROLAR DUPLICADOS
    // ==========================================

    private Mesa guardarMesa(Mesa mesa) {

        try {

            return mesaRepository.saveAndFlush(mesa);

        } catch (DataIntegrityViolationException e) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se pudo guardar la mesa por "
                    + "un conflicto con los datos existentes."
            );
        }
    }
}