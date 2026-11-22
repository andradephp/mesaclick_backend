
package com.example.mesaclick.Services;

import org.springframework.stereotype.Service;

@Service
public class PlantillaBienvenidaCorreoService {

    public String generar(String enlaceCancelacion) {

        if (enlaceCancelacion == null
                || !enlaceCancelacion.matches("https?://[^\\s\"'<>]+")) {
            throw new IllegalArgumentException(
                "El enlace de cancelación no es válido."
            );
        }

        String enlaceSeguro = enlaceCancelacion
                .replace("&", "&amp;");

        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport"
                      content="width=device-width, initial-scale=1.0">
                <title>Bienvenido a MesaClick</title>
            </head>

            <body style="
                margin:0;
                padding:30px 12px;
                background-color:#f3f4f6;
                font-family:Arial,Helvetica,sans-serif;
            ">

                <table role="presentation"
                       cellpadding="0"
                       cellspacing="0"
                       border="0"
                       align="center"
                       width="100%"
                       style="
                           max-width:560px;
                           background:#ffffff;
                           border-radius:14px;
                           overflow:hidden;
                       ">

                    <tr>
                        <td align="center"
                            style="
                                background-color:#f18a04;
                                padding:34px 20px;
                            ">

                            <h1 style="
                                color:#ffffff;
                                font-size:34px;
                                margin:0;
                            ">
                                MesaClick
                            </h1>

                            <p style="
                                color:#ffffff;
                                font-size:12px;
                                letter-spacing:2px;
                                margin:10px 0 0;
                            ">
                                PROMOCIONES Y NOVEDADES
                            </p>

                        </td>
                    </tr>

                    <tr>
                        <td style="padding:35px 30px;">

                            <h2 style="
                                color:#242424;
                                font-size:25px;
                                margin:0 0 20px;
                            ">
                                ¡Bienvenido a MesaClick! 🎉
                            </h2>

                            <p style="
                                color:#555555;
                                font-size:15px;
                                line-height:1.8;
                            ">
                                Gracias por suscribirte a
                                nuestras promociones y novedades.
                            </p>

                            <p style="
                                color:#555555;
                                font-size:15px;
                                line-height:1.8;
                            ">
                                Desde ahora podrás recibir
                                ofertas especiales, descuentos
                                y noticias sobre nuestros
                                productos y eventos.
                            </p>

                            <table role="presentation"
                                   width="100%"
                                   cellpadding="0"
                                   cellspacing="0"
                                   style="
                                       background:#fff4e5;
                                       border-radius:9px;
                                       margin:24px 0;
                                   ">
                                <tr>
                                    <td style="padding:20px;">
                                        <p style="
                                            color:#a85b00;
                                            font-weight:bold;
                                            margin:0 0 8px;
                                        ">
                                            ¡Tu suscripción está activa!
                                        </p>

                                        <p style="
                                            color:#76512c;
                                            font-size:13px;
                                            margin:0;
                                            line-height:1.6;
                                        ">
                                            No necesitas confirmar
                                            tu correo nuevamente.
                                        </p>
                                    </td>
                                </tr>
                            </table>

                            <p style="
                                color:#555555;
                                font-size:15px;
                                line-height:1.8;
                            ">
                                Nos alegra tenerte con nosotros.
                            </p>

                            <p style="
                                color:#333333;
                                font-size:15px;
                                margin-top:25px;
                            ">
                                Equipo MesaClick
                            </p>

                        </td>
                    </tr>

                    <tr>
                        <td align="center"
                            style="
                                background:#fafafa;
                                padding:25px 20px;
                                border-top:1px solid #eeeeee;
                            ">

                            <p style="
                                color:#777777;
                                font-size:12px;
                                line-height:1.7;
                            ">
                                Recibes este correo porque
                                aceptaste recibir promociones
                                de MesaClick.
                            </p>

                            <a href="%s"
                               style="
                                   color:#d97706;
                                   font-size:13px;
                                   text-decoration:underline;
                               ">
                                Cancelar suscripción
                            </a>

                            <p style="
                                color:#aaaaaa;
                                font-size:11px;
                                margin-top:22px;
                            ">
                                © MesaClick
                            </p>

                        </td>
                    </tr>

                </table>

            </body>
            </html>
            """.replace("href=\"%s\"", "href=\"" + enlaceSeguro + "\"");
    }
}
