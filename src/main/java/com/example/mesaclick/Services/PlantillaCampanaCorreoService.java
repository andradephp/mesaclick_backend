
package com.example.mesaclick.Services;

import com.example.mesaclick.Model.CampanaCorreo;
import com.example.mesaclick.Model.Producto;
import com.example.mesaclick.Model.Promocion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class PlantillaCampanaCorreoService {

    @Value("${mesaclick.url-publica:http://localhost:8080}")
    private String urlPublica;

    private static final String COLOR_PRINCIPAL = "#f18a04";

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern(
                    "dd 'de' MMMM 'de' yyyy, hh:mm a",
                    new Locale("es", "CO")
            );

    private static final Pattern TOKEN_SEGURO =
            Pattern.compile("^[A-Za-z0-9_-]{20,256}$");

    private static final Pattern RUTA_IMAGEN =
            Pattern.compile(
                    "^/?imagenes/productos/[A-Za-z0-9._/-]+$"
            );

    // =========================================================
    // SEGURIDAD Y UTILIDADES
    // =========================================================

    private String escapar(String texto) {

        if (texto == null) {
            return "";
        }

        return texto
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String fecha(LocalDateTime valor) {

        if (valor == null) {
            return "Fecha por confirmar";
        }

        return valor.format(FORMATO_FECHA);
    }

    private String dinero(BigDecimal valor) {

        if (valor == null) {
            return "$0";
        }

        return "$" + String.format(
                Locale.forLanguageTag("es-CO"),
                "%,.0f",
                valor
        );
    }

    private BigDecimal convertirDecimal(Number numero) {

        if (numero == null) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(numero.toString());
    }

    // =========================================================
    // VALIDACION DEL ENLACE DE CANCELACION
    // =========================================================

    private void validarEnlaceCancelacion(String enlace) {

        if (enlace == null || enlace.isBlank()) {
            throw new IllegalArgumentException(
                    "El enlace de cancelación es obligatorio."
            );
        }

        try {

            URI uri = new URI(enlace.trim());

            String esquema = uri.getScheme();
            String host = uri.getHost();

            if (esquema == null || host == null) {
                throw new IllegalArgumentException(
                        "El enlace de cancelación no es válido."
                );
            }

            boolean esHttps =
                    "https".equalsIgnoreCase(esquema);

            boolean esLocal =
                    "http".equalsIgnoreCase(esquema)
                    && (
                        "localhost".equalsIgnoreCase(host)
                        || "127.0.0.1".equals(host)
                    )
                    && uri.getPort() == 5173;

            if (!esHttps && !esLocal) {
                throw new IllegalArgumentException(
                        "El enlace debe utilizar HTTPS, excepto en localhost."
                );
            }

            if (!"/cancelar-suscripcion".equals(uri.getPath())) {
                throw new IllegalArgumentException(
                        "La ruta de cancelación no es válida."
                );
            }

            if (uri.getRawUserInfo() != null
                    || uri.getRawFragment() != null) {
                throw new IllegalArgumentException(
                        "El enlace contiene elementos no permitidos."
                );
            }

            String query = uri.getRawQuery();

            if (query == null || !query.startsWith("token=")) {
                throw new IllegalArgumentException(
                        "El token de cancelación es obligatorio."
                );
            }

            String token = query.substring("token=".length());

            if (!TOKEN_SEGURO.matcher(token).matches()) {
                throw new IllegalArgumentException(
                        "El token de cancelación no tiene un formato válido."
                );
            }

        } catch (URISyntaxException e) {

            throw new IllegalArgumentException(
                    "El enlace de cancelación tiene un formato inválido.",
                    e
            );
        }
    }

    // =========================================================
    // IMAGENES DE PRODUCTOS
    // =========================================================

    private String obtenerUrlImagen(String ruta) {

        if (ruta == null || ruta.isBlank()) {
            return "";
        }

        String limpia = ruta.trim();

        if (limpia.startsWith("https://")) {

            try {
                URI uri = new URI(limpia);

                if (uri.getHost() != null
                        && uri.getRawUserInfo() == null
                        && uri.getRawFragment() == null) {
                    return limpia;
                }

            } catch (URISyntaxException ignored) {
                return "";
            }

            return "";
        }

        if (!RUTA_IMAGEN.matcher(limpia).matches()) {
            return "";
        }

        String base = urlPublica == null
                ? ""
                : urlPublica.trim().replaceAll("/+$", "");

        if (base.isBlank()) {
            return "";
        }

        String rutaFinal = limpia.startsWith("/")
                ? limpia
                : "/" + limpia;

        return base + rutaFinal;
    }

    // =========================================================
    // ESTRUCTURA PRINCIPAL DEL CORREO
    // =========================================================

    public String generar(
            CampanaCorreo campana,
            List<Promocion> promociones,
            String enlaceCancelacion
    ) {

        Objects.requireNonNull(
                campana,
                "La campaña es obligatoria."
        );

        validarEnlaceCancelacion(enlaceCancelacion);

        StringBuilder html = new StringBuilder();

        // =====================================================
        // DOCUMENTO HTML
        // =====================================================

        html.append("""
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport"
                      content="width=device-width, initial-scale=1.0">

                <meta name="color-scheme" content="light">

                <title>MesaClick - Promociones y eventos</title>

                <style>
                    @media only screen and (max-width: 600px) {
                        .contenedor {
                            width: 100% !important;
                        }

                        .contenido {
                            padding: 22px 18px !important;
                        }

                        .titulo {
                            font-size: 25px !important;
                        }

                        .producto-imagen {
                            width: 80px !important;
                            height: 80px !important;
                        }

                        .producto-celda {
                            width: 90px !important;
                        }
                    }
                </style>
            </head>

            <body style="
                margin:0;
                padding:0;
                background-color:#f4f5f7;
                font-family:Arial,Helvetica,sans-serif;
                color:#242424;
                -webkit-text-size-adjust:100%;
            ">

            <div style="display:none;
                        font-size:1px;
                        color:#f4f5f7;
                        line-height:1px;
                        max-height:0;
                        max-width:0;
                        opacity:0;
                        overflow:hidden;">
                Descubre las novedades, promociones y eventos
                especiales de MesaClick.
            </div>

            <table role="presentation"
                   cellpadding="0"
                   cellspacing="0"
                   border="0"
                   width="100%"
                   style="background-color:#f4f5f7;">

                <tr>
                    <td align="center"
                        style="padding:30px 12px;">

                        <table role="presentation"
                               class="contenedor"
                               cellpadding="0"
                               cellspacing="0"
                               border="0"
                               width="600"
                               style="
                                   width:600px;
                                   max-width:600px;
                                   background-color:#ffffff;
                                   border-radius:16px;
                                   overflow:hidden;
                                   border:1px solid #ececec;
                               ">

                            <tr>
                                <td align="center"
                                    style="
                                        background-color:#f18a04;
                                        padding:34px 24px;
                                        color:#ffffff;
                                    ">

                                    <div style="
                                        font-size:34px;
                                        font-weight:800;
                                        letter-spacing:-1px;
                                    ">
                                        MesaClick
                                    </div>

                                    <div style="
                                        font-size:11px;
                                        letter-spacing:2.5px;
                                        margin-top:8px;
                                        font-weight:bold;
                                    ">
                                        PROMOCIONES Y EVENTOS
                                    </div>

                                </td>
                            </tr>

                            <tr>
                                <td class="contenido"
                                    style="padding:32px 34px;">

                                    <div style="
                                        display:inline-block;
                                        background-color:#fff2e1;
                                        color:#a85b00;
                                        padding:7px 12px;
                                        border-radius:20px;
                                        font-size:11px;
                                        font-weight:bold;
                                        letter-spacing:1px;
                                        margin-bottom:18px;
                                    ">
                                        NOVEDADES MESACLICK
                                    </div>
            """);

        // =====================================================
        // TITULO
        // =====================================================

        String titulo = campana.getTitulo();

        if (titulo == null || titulo.isBlank()) {
            titulo = "¡Descubre lo nuevo de MesaClick!";
        }

        html.append("""
            <h1 class="titulo"
                style="
                    font-size:30px;
                    line-height:1.25;
                    margin:0 0 14px;
                    color:#202020;
                    font-weight:800;
                ">
            """);

        html.append(escapar(titulo));

        html.append("</h1>");

        // =====================================================
        // MENSAJE PRINCIPAL
        // =====================================================

        if (campana.getMensaje() != null
                && !campana.getMensaje().isBlank()) {

            html.append("""
                <p style="
                    font-size:15px;
                    line-height:1.8;
                    color:#626262;
                    margin:0 0 24px;
                ">
                """);

            html.append(
                    escapar(campana.getMensaje())
                            .replace("\n", "<br>")
            );

            html.append("</p>");
        }

        // =====================================================
        // PROMOCIONES
        // =====================================================

        if (promociones != null && !promociones.isEmpty()) {

            html.append("""
                <div style="
                    margin-top:26px;
                    margin-bottom:18px;
                ">
                    <h2 style="
                        font-size:20px;
                        color:#242424;
                        margin:0;
                    ">
                        Promociones especiales
                    </h2>

                    <p style="
                        color:#888888;
                        font-size:13px;
                        margin:7px 0 0;
                    ">
                        Descubre nuestras ofertas disponibles.
                    </p>
                </div>
                """);

            for (Promocion promocion : promociones) {

                if (promocion == null
                        || promocion.getProducto() == null) {
                    continue;
                }

                Producto producto = promocion.getProducto();

                BigDecimal precioOriginal =
                        convertirDecimal(producto.getPrecioProducto());

                BigDecimal descuento =
                        convertirDecimal(
                                promocion.getPorcentajeDescuento()
                        );

                if (descuento.compareTo(BigDecimal.ZERO) < 0) {
                    descuento = BigDecimal.ZERO;
                }

                if (descuento.compareTo(
                        BigDecimal.valueOf(100)) > 0) {
                    descuento = BigDecimal.valueOf(100);
                }

                BigDecimal precioFinal =
                        precioOriginal.multiply(
                                BigDecimal.ONE.subtract(
                                        descuento.divide(
                                                BigDecimal.valueOf(100),
                                                6,
                                                RoundingMode.HALF_UP
                                        )
                                )
                        ).setScale(0, RoundingMode.HALF_UP);

                String rutaImagen =
                        obtenerUrlImagen(producto.getImagenProducto());

                html.append("""
                    <table role="presentation"
                           cellpadding="0"
                           cellspacing="0"
                           border="0"
                           width="100%"
                           style="
                               margin-bottom:16px;
                               background-color:#ffffff;
                               border:1px solid #eeeeee;
                               border-radius:12px;
                           ">

                        <tr>
                    """);

                // IMAGEN

                if (!rutaImagen.isBlank()) {

                    html.append("""
                        <td class="producto-celda"
                            width="112"
                            style="
                                padding:15px 0 15px 15px;
                                vertical-align:top;
                            ">

                            <img class="producto-imagen"
                                 src="
                        """);

                    html.append(escapar(rutaImagen));

                    html.append("""
                        "
                                 width="96"
                                 height="96"
                                 alt="Imagen del producto"
                                 style="
                                     display:block;
                                     width:96px;
                                     height:96px;
                                     object-fit:cover;
                                     border-radius:10px;
                                     border:0;
                                 ">
                        </td>
                        """);
                }

                html.append("""
                    <td style="
                        padding:16px;
                        vertical-align:top;
                    ">
                    """);

                // NOMBRE

                html.append("""
                    <div style="
                        font-size:16px;
                        font-weight:bold;
                        color:#242424;
                        line-height:1.4;
                    ">
                    """);

                html.append(
                        escapar(producto.getNombreProducto())
                );

                html.append("</div>");

                // PRECIO ORIGINAL

                if (descuento.compareTo(BigDecimal.ZERO) > 0) {

                    html.append("""
                        <div style="
                            margin-top:8px;
                            font-size:12px;
                            color:#999999;
                            text-decoration:line-through;
                        ">
                        """);

                    html.append(dinero(precioOriginal));

                    html.append("</div>");
                }

                // PRECIO FINAL

                html.append("""
                    <div style="
                        margin-top:7px;
                        font-size:21px;
                        font-weight:800;
                        color:#e87500;
                    ">
                    """);

                html.append(dinero(precioFinal));

                html.append("</div>");

                // DESCUENTO

                if (descuento.compareTo(BigDecimal.ZERO) > 0) {

                    html.append("""
                        <div style="
                            display:inline-block;
                            margin-top:9px;
                            background-color:#fff0e0;
                            color:#b45309;
                            font-size:11px;
                            font-weight:bold;
                            padding:5px 9px;
                            border-radius:6px;
                        ">
                            AHORRA
                        """);

                    html.append(
                            descuento.stripTrailingZeros()
                                    .toPlainString()
                    );

                    html.append("%</div>");
                }

                // DESCRIPCION

                String descripcion =
                        promocion.getDescripcionPromocion();

                if (descripcion == null
                        || descripcion.isBlank()) {
                    descripcion =
                            producto.getDescripcionProducto();
                }

                if (descripcion != null
                        && !descripcion.isBlank()) {

                    html.append("""
                        <p style="
                            font-size:12px;
                            line-height:1.6;
                            color:#777777;
                            margin:12px 0 0;
                        ">
                        """);

                    html.append(escapar(descripcion));

                    html.append("</p>");
                }

                // VIGENCIA

                html.append("""
                    <p style="
                        font-size:11px;
                        line-height:1.5;
                        color:#999999;
                        margin:10px 0 0;
                    ">
                        Vigencia:
                    """);

                html.append(fecha(promocion.getFechaInicio()));
                html.append(" al ");
                html.append(fecha(promocion.getFechaFin()));

                html.append("</p>");

                html.append("""
                    </td>
                    </tr>
                    </table>
                    """);
            }
        }

        // =====================================================
        // EVENTO
        // =====================================================

        if (campana.getNombreEvento() != null
                && !campana.getNombreEvento().isBlank()) {

            html.append("""
                <table role="presentation"
                       cellpadding="0"
                       cellspacing="0"
                       border="0"
                       width="100%"
                       style="
                           margin-top:25px;
                           background-color:#fff6e9;
                           border:1px solid #ffe5bf;
                           border-radius:12px;
                       ">

                    <tr>
                        <td style="padding:22px;">

                            <div style="
                                font-size:11px;
                                font-weight:bold;
                                letter-spacing:1px;
                                color:#a65a00;
                                margin-bottom:9px;
                            ">
                                EVENTO ESPECIAL
                            </div>

                            <div style="
                                font-size:20px;
                                font-weight:bold;
                                color:#703c00;
                                line-height:1.4;
                            ">
                """);

            html.append(
                    escapar(campana.getNombreEvento())
            );

            html.append("</div>");

            if (campana.getFechaEvento() != null) {

                html.append("""
                    <div style="
                        font-size:13px;
                        font-weight:bold;
                        color:#9b5900;
                        margin-top:12px;
                    ">
                        Fecha:
                    """);

                html.append(fecha(campana.getFechaEvento()));

                html.append("</div>");
            }

            if (campana.getDescripcionEvento() != null
                    && !campana.getDescripcionEvento().isBlank()) {

                html.append("""
                    <p style="
                        font-size:13px;
                        line-height:1.7;
                        color:#785b3c;
                        margin:12px 0 0;
                    ">
                    """);

                html.append(
                        escapar(campana.getDescripcionEvento())
                );

                html.append("</p>");
            }

            html.append("""
                        </td>
                    </tr>
                </table>
                """);
        }

        // =====================================================
        // CIERRE DEL CONTENIDO
        // =====================================================

        html.append("""
                                </td>
                            </tr>

                            <tr>
                                <td align="center"
                                    style="
                                        background-color:#fafafa;
                                        padding:27px 25px;
                                        border-top:1px solid #eeeeee;
                                    ">

                                    <div style="
                                        font-size:20px;
                                        font-weight:bold;
                                        color:#f18a04;
                                        margin-bottom:12px;
                                    ">
                                        MesaClick
                                    </div>

                                    <p style="
                                        font-size:12px;
                                        line-height:1.7;
                                        color:#777777;
                                        margin:0 0 13px;
                                    ">
                                        Recibes este correo porque
                                        aceptaste recibir promociones
                                        y novedades de MesaClick.
                                    </p>

                                    <p style="
                                        font-size:11px;
                                        line-height:1.6;
                                        color:#999999;
                                        margin:0 0 12px;
                                    ">
                                        Puedes cancelar tu suscripción
                                        cuando lo desees.
                                    </p>

                                    <a href="
            """);

        html.append(escapar(enlaceCancelacion.trim()));

        html.append("""
            "
                                       style="
                                           display:inline-block;
                                           color:#777777;
                                           font-size:12px;
                                           text-decoration:underline;
                                           padding:8px;
                                       ">
                                        Cancelar suscripción
                                    </a>

                                    <div style="
                                        margin-top:20px;
                                        font-size:10px;
                                        color:#aaaaaa;
                                    ">
                                        © MesaClick · Todos los derechos reservados
                                    </div>

                                </td>
                            </tr>

                        </table>
                    </td>
                </tr>
            </table>

            </body>
            </html>
            """);

        return html.toString();
    }
}
