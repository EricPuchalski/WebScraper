package WebScraperNotification.com.service;

import WebScraperNotification.com.event.dto.PriceDropNotificationEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendPriceDropNotification(PriceDropNotificationEvent event) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Configurar destinatario y asunto
            helper.setTo(event.clientMail());
            helper.setSubject("¡Bajó el precio de " + event.productName() + " en " + event.productPage() + "!");
            helper.setFrom("noreply@webscrapernotification.com");

            // Crear contexto de Thymeleaf
            Context context = new Context();
            context.setVariable("clientName", event.clientName());
            context.setVariable("clientLastname", event.clientLastname());
            context.setVariable("productName", event.productName());
            context.setVariable("productPage", event.productPage());        // Nuevo
            context.setVariable("productUrl", event.productUrl());          // Nuevo
            context.setVariable("productId", event.productId());
            context.setVariable("oldPrice", event.productOldPrice());
            context.setVariable("newPrice", event.productNewPrice());
            context.setVariable("savings", event.productOldPrice() - event.productNewPrice());
            context.setVariable("discountPercentage",
                    Math.round(((event.productOldPrice() - event.productNewPrice()) / event.productOldPrice()) * 100));

            // Procesar template y establecer contenido HTML
            String htmlContent = templateEngine.process("email-notification", context);
            helper.setText(htmlContent, true);

            // Enviar email
            mailSender.send(message);

            log.info("Price drop notification email sent successfully to {} {} for product {} on {}",
                    event.clientName(), event.clientLastname(), event.productName(), event.productPage());

        } catch (MessagingException e) {
            log.error("Failed to send price drop notification email to {}: {}",
                    event.clientMail(), e.getMessage(), e);
            throw new RuntimeException("Failed to send email notification", e);
        }
    }
}