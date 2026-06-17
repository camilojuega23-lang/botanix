package botanix.botanix.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${resend.api-key}")
    private String resendApiKey;

    @Value("${resend.from}")
    private String remitente;

    public void enviarCorreoRecuperacion(String destinatario, String enlace) throws ResendException {
        if (resendApiKey == null || resendApiKey.isBlank()) {
            throw new IllegalStateException("RESEND_API_KEY no esta configurada.");
        }

        Resend resend = new Resend(resendApiKey);

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(remitente)
                .to(destinatario)
                .subject("Restablecer tu contrasena - Botanix")
                .html(construirContenidoRecuperacion(enlace))
                .build();

        log.info("Intentando enviar correo de recuperacion con Resend a {} desde {}", destinatario, remitente);
        CreateEmailResponse response = resend.emails().send(params);
        log.info("Correo de recuperacion enviado con Resend a {}. Email id: {}", destinatario, response.getId());
    }

    private String construirContenidoRecuperacion(String enlace) {
        return "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #DEF4C6; border-radius: 10px;\">"
                + "<h2 style=\"color: #1B512D;\">Restablecer tu contrasena</h2>"
                + "<p>Hola,</p>"
                + "<p>Has solicitado restablecer tu contrasena en el sistema de gestion <strong>Botanix</strong>.</p>"
                + "<p>Haz clic en el siguiente boton para establecer una nueva contrasena. Este enlace expira en 1 hora:</p>"
                + "<p style=\"text-align: center; margin: 30px 0;\">"
                + "  <a href=\"" + enlace + "\" style=\"display: inline-block; padding: 12px 24px; color: white; background-color: #1C7C54; border-radius: 8px; text-decoration: none; font-weight: bold;\">Restablecer contrasena</a>"
                + "</p>"
                + "<p>Si no solicitaste este cambio, puedes ignorar este correo de forma segura.</p>"
                + "<br>"
                + "<p>Atentamente,<br>El equipo de Botanix</p>"
                + "</div>";
    }
}
