
    package com.example.mesaclick.Controller;

    import com.example.mesaclick.Model.Promocion;
    import com.example.mesaclick.Services.PromocionService;

    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.web.bind.annotation.*;

    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.Map;

    @RestController
    @RequestMapping("/api")
    public class PromocionController {

        private final PromocionService promocionService;

        public PromocionController(
                PromocionService promocionService
        ) {
            this.promocionService = promocionService;
        }

        // ==========================================
        // DTO DE RESPUESTA
        // ==========================================

        public record PromocionRespuesta(
                Long idPromocion,
                String nombrePromocion,
                String descripcionPromocion,
                Long idProducto,
                String nombreProducto,
                Integer porcentajeDescuento,
                LocalDateTime fechaInicio,
                LocalDateTime fechaFin,
                Boolean estadoPromocion
        ) {}

        // ==========================================
        // DTO PARA CREAR PROMOCIONES
        // ==========================================

        public record CrearPromocionRequest(
                String nombrePromocion,
                String descripcionPromocion,
                Long idProducto,
                Integer porcentajeDescuento,
                LocalDateTime fechaInicio,
                LocalDateTime fechaFin
        ) {}

        // ==========================================
        // CONVERTIR ENTIDAD A RESPUESTA
        // ==========================================

        private PromocionRespuesta convertir(Promocion p) {

            return new PromocionRespuesta(
                    p.getIdPromocion(),
                    p.getNombrePromocion(),
                    p.getDescripcionPromocion(),
                    p.getProducto().getIdProducto(),
                    p.getProducto().getNombreProducto(),
                    p.getPorcentajeDescuento(),
                    p.getFechaInicio(),
                    p.getFechaFin(),
                    p.getEstadoPromocion()
            );
        }

        // ==========================================
        // CONSULTAR PROMOCIONES VIGENTES
        // PÚBLICO
        // ==========================================

        @GetMapping("/promociones/vigentes")
        public List<PromocionRespuesta> obtenerVigentes() {

            return promocionService.obtenerVigentes()
                    .stream()
                    .map(this::convertir)
                    .toList();
        }

        // ==========================================
        // LISTAR TODAS LAS PROMOCIONES
        // SOLO ADMINISTRADOR
        // ==========================================

        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/admin/promociones")
        public List<PromocionRespuesta> obtenerTodas() {

            return promocionService.obtenerTodas()
                    .stream()
                    .map(this::convertir)
                    .toList();
        }

        // ==========================================
        // CREAR PROMOCIÓN
        // SOLO ADMINISTRADOR
        // ==========================================

        @PreAuthorize("hasRole('ADMIN')")
        @PostMapping("/admin/promociones")
        public ResponseEntity<PromocionRespuesta> crear(
                @RequestBody CrearPromocionRequest request
        ) {

            if (request.idProducto() == null) {
                throw new IllegalArgumentException(
                        "Debes seleccionar un producto"
                );
            }

            Promocion promocion = new Promocion();

            promocion.setNombrePromocion(
                    request.nombrePromocion()
            );

            promocion.setDescripcionPromocion(
                    request.descripcionPromocion()
            );

            // Solo se utiliza el ID del producto.
            // El servicio comprobará que exista.
            com.example.mesaclick.Model.Producto producto =
                    new com.example.mesaclick.Model.Producto();

            producto.setIdProducto(request.idProducto());

            promocion.setProducto(producto);

            promocion.setPorcentajeDescuento(
                    request.porcentajeDescuento()
            );

            promocion.setFechaInicio(
                    request.fechaInicio()
            );

            promocion.setFechaFin(
                    request.fechaFin()
            );

            promocion.setEstadoPromocion(true);

            Promocion guardada =
                    promocionService.crearPromocion(promocion);

            return ResponseEntity.status(201)
                    .body(convertir(guardada));
        }

        // ==========================================
        // ACTIVAR O DESACTIVAR PROMOCIÓN
        // SOLO ADMINISTRADOR
        // ==========================================

        
    // ==========================================
    // ACTIVAR O DESACTIVAR PROMOCIÓN
    // SOLO ADMINISTRADOR
    // ==========================================

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/promociones/{id}/estado")
    public PromocionRespuesta cambiarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> datos
    ) {

        Boolean activo = datos.get("estadoPromocion");

        if (activo == null) {
            throw new IllegalArgumentException(
                    "Debes indicar el estadoPromocion"
            );
        }

        Promocion actualizada =
                promocionService.cambiarEstado(id, activo);

        return convertir(actualizada);
    }

    }
