
package com.example.mesaclick.Services;

import com.example.mesaclick.Dto.CategoriaAdminRequest;
import com.example.mesaclick.Model.Categoria;
import com.example.mesaclick.Repository.CategoriaRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoriaAdminService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaAdminService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    @Transactional
    public Categoria crearCategoria(CategoriaAdminRequest request) {

        validarNombre(request);

        Categoria categoria = new Categoria();

        categoria.setNombreCategoria(
            request.getNombreCategoria().trim()
        );

        categoria.setEstadoCategoria(
            request.getEstadoCategoria() != null
                ? request.getEstadoCategoria()
                : true
        );

        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria actualizarCategoria(
        Integer id,
        CategoriaAdminRequest request
    ) {

        Categoria categoria = buscarCategoria(id);

        validarNombre(request);

        categoria.setNombreCategoria(
            request.getNombreCategoria().trim()
        );

        if (request.getEstadoCategoria() != null) {
            categoria.setEstadoCategoria(
                request.getEstadoCategoria()
            );
        }

        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria cambiarEstado(Integer id, Boolean estado) {

        Categoria categoria = buscarCategoria(id);

        if (estado == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Debes indicar el estado de la categoría"
            );
        }

        categoria.setEstadoCategoria(estado);

        return categoriaRepository.save(categoria);
    }

    private Categoria buscarCategoria(Integer id) {
        return categoriaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "La categoría no existe"
            ));
    }

    private void validarNombre(CategoriaAdminRequest request) {

        if (request == null ||
            request.getNombreCategoria() == null ||
            request.getNombreCategoria().isBlank()) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El nombre de la categoría es obligatorio"
            );
        }

        if (request.getNombreCategoria().trim().length() > 255) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El nombre no puede superar 255 caracteres"
            );
        }
    }
}
