
package com.example.mesaclick.Services;

import com.example.mesaclick.Model.Producto;
import com.example.mesaclick.Repository.ProductoRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class ProductoImagenService {

    private final ProductoRepository productoRepository;
    private final Path directorio;

    public ProductoImagenService(
            ProductoRepository productoRepository,
            @Value("${mesaclick.upload-dir:uploads/productos}")
            String uploadDir
    ) {
        this.productoRepository = productoRepository;
        this.directorio = Path.of(uploadDir)
                .toAbsolutePath()
                .normalize();
    }

    public Producto subirImagen(Long id, MultipartFile archivo) {

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Producto no encontrado"
                ));

        if (archivo == null || archivo.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debes seleccionar una imagen"
            );
        }

        if (archivo.getSize() > 5 * 1024 * 1024) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La imagen no puede superar los 5 MB"
            );
        }

        String extension;

        try (InputStream input = archivo.getInputStream()) {
            byte[] firma = input.readNBytes(12);
            extension = detectarExtension(firma);
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se pudo leer la imagen"
            );
        }

        if (extension == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Formato inválido. Utiliza JPG, PNG o WEBP"
            );
        }

        String nombre = UUID.randomUUID() + "." + extension;

        try {
            Files.createDirectories(directorio);

            Path destino = directorio.resolve(nombre).normalize();

            if (!destino.startsWith(directorio)) {
                throw new IOException("Ruta de archivo inválida");
            }

            try (InputStream input = archivo.getInputStream()) {
                Files.copy(
                        input,
                        destino,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            String imagenAnterior = producto.getImagenProducto();

            producto.setImagenProducto(
                    "/imagenes/productos/" + nombre
            );

            Producto actualizado;

            try {
                actualizado = productoRepository.save(producto);
            } catch (RuntimeException e) {
                Files.deleteIfExists(destino);
                throw e;
            }

            // Elimina únicamente imágenes locales anteriores.
            if (imagenAnterior != null
                    && imagenAnterior.startsWith("/imagenes/productos/")) {

                String archivoAnterior = imagenAnterior.substring(
                        "/imagenes/productos/".length()
                );

                Path rutaAnterior = directorio.resolve(
                        archivoAnterior
                ).normalize();

                if (rutaAnterior.startsWith(directorio)
                        && !rutaAnterior.equals(destino)) {
                    try {
                        Files.deleteIfExists(rutaAnterior);
                    } catch (IOException ignored) {
                        // La nueva imagen ya fue guardada.
                    }
                }
            }

            return actualizado;

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo guardar la imagen"
            );
        }
    }

    private String detectarExtension(byte[] firma) {

        if (firma.length >= 3
                && (firma[0] & 0xFF) == 0xFF
                && (firma[1] & 0xFF) == 0xD8
                && (firma[2] & 0xFF) == 0xFF) {
            return "jpg";
        }

        if (firma.length >= 8
                && (firma[0] & 0xFF) == 0x89
                && firma[1] == 0x50
                && firma[2] == 0x4E
                && firma[3] == 0x47
                && firma[4] == 0x0D
                && firma[5] == 0x0A
                && firma[6] == 0x1A
                && firma[7] == 0x0A) {
            return "png";
        }

        if (firma.length >= 12
                && new String(
                        firma, 0, 4,
                        java.nio.charset.StandardCharsets.US_ASCII
                ).equals("RIFF")
                && new String(
                        firma, 8, 4,
                        java.nio.charset.StandardCharsets.US_ASCII
                ).equals("WEBP")) {
            return "webp";
        }

        return null;
    }
}
