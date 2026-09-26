package com.portafolio.citas.service.impl;

import com.portafolio.citas.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Implementación del servicio de correo electrónico con soporte asíncrono.
 * 
 * Anotación @Slf4j: Genera automáticamente un logger de SLF4J para registrar eventos, advertencias y errores.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@sistemacitas.com}")
    private String mailFrom;

    /**
     * Envía el correo de confirmación de manera asíncrona utilizando el ThreadPool "emailExecutor".
     * 
     * ¿Qué hace @Async y por qué un fallo de email no debe deshacer la reserva en base de datos?
     * 1. @Async ("Non-blocking"): Permite que el hilo principal que atiende la petición HTTP retorne 
     *    inmediatamente el código HTTP 201 Created al cliente, mientras el envío del correo se procesa 
     *    en segundo plano en un hilo independiente.
     * 2. Resiliencia y Transaccionalidad: El envío de correos externos (SMTP) es propenso a fallos de red o caídas del servidor de correo.
     *    Si el envío se envolviera en la misma transacción de la base de datos o si relanzáramos la excepción, 
     *    un fallo temporal de red cancelaría la reserva del cliente en la BD. En su lugar, capturamos la excepción 
     *    con un bloque try-catch y la registramos en los logs, garantizando que la reserva del turno sea persistida con éxito 
     *    aunque el correo no pueda entregarse en ese instante.
     */
    @Override
    @Async("emailExecutor")
    public void sendAppointmentConfirmation(String toEmail, String clientName, LocalDateTime dateTime, String notes) {
        try {
            log.info("Iniciando envío asíncrono de correo de confirmación a: {}", toEmail);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String formattedDate = dateTime != null ? dateTime.format(formatter) : "Fecha por confirmar";

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(toEmail);
            message.setSubject("Confirmación de Cita - Sistema de Citas");
            message.setText(
                "¡Hola, " + clientName + "!\n\n" +
                "Tu cita ha sido agendada exitosamente en nuestro sistema.\n\n" +
                "Detalles de la reserva:\n" +
                "- Fecha y Hora: " + formattedDate + "\n" +
                "- Notas: " + (notes != null && !notes.isBlank() ? notes : "Ninguna") + "\n\n" +
                "Gracias por confiar en nosotros.\n\n" +
                "Atentamente,\n" +
                "Equipo de Sistema de Citas"
            );

            mailSender.send(message);
            log.info("Correo de confirmación enviado exitosamente a: {}", toEmail);

        } catch (Exception e) {
            // Capturamos el error para evitar hacer rollback en la transacción de la base de datos
            log.error("Error crítico al enviar el correo electrónico de confirmación a {}: {}", toEmail, e.getMessage(), e);
        }
    }
}
