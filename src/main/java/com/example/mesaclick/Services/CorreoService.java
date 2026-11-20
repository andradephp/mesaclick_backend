
    package com.example.mesaclick.Services;

    import jakarta.mail.MessagingException;
    import jakarta.mail.internet.MimeMessage;

    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.mail.SimpleMailMessage;
    import org.springframework.mail.javamail.JavaMailSender;
    import org.springframework.mail.javamail.MimeMessageHelper;
    import org.springframework.stereotype.Service;

    @Service
    public class CorreoService {

        private final JavaMailSender mailSender;

        @Value("${spring.mail.username}")
        private String correoRemitente;

        public CorreoService(JavaMailSender mailSender) {
            this.mailSender = mailSender;
        }

        // Correos de texto: confirmación de suscripción.
        // Se conserva el funcionamiento original.
        public void enviarCorreo(
                String destinatario,
                String asunto,
                String contenido
        ) {
            SimpleMailMessage mensaje = new SimpleMailMessage();

            mensaje.setFrom(correoRemitente);
            mensaje.setTo(destinatario);
            mensaje.setSubject(asunto);
            mensaje.setText(contenido);

            mailSender.send(mensaje);
        }

        // Correos HTML: promociones y eventos de MesaClick.
        public void enviarCorreoHtml(
                String destinatario,
                String asunto,
                String contenidoHtml
        ) {
            try {
                MimeMessage mensaje = mailSender.createMimeMessage();

                MimeMessageHelper helper = new MimeMessageHelper(
                        mensaje,
                        false,
                        "UTF-8"
                );

                helper.setFrom(correoRemitente);
                helper.setTo(destinatario);
                helper.setSubject(asunto);

                // true indica que el contenido es HTML.
                helper.setText(contenidoHtml, true);

                mailSender.send(mensaje);

            } catch (MessagingException e) {
                throw new IllegalStateException(
                        "No se pudo preparar el correo HTML.",
                        e
                );
            }
        }
    }
