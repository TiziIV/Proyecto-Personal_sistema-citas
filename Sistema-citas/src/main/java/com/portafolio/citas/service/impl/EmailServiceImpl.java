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
            log.error("Error crítico al enviar el correo electrónico de confirmación a {}: {}", toEmail, e.getMessage(), e);
        }
    }

    /**
     * Envía el correo de recordatorio de cita próxima de manera asíncrona utilizando el ThreadPool "emailExecutor".
     */
    @Override
    @Async("emailExecutor")
    public void sendAppointmentReminder(String toEmail, String clientName, LocalDateTime dateTime, String notes) {
        try {
            log.info("Iniciando envío asíncrono de correo de recordatorio a: {}", toEmail);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String formattedDate = dateTime != null ? dateTime.format(formatter) : "Fecha por confirmar";

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(toEmail);
            message.setSubject("Recordatorio de Cita Próxima - Sistema de Citas");
            message.setText(
                "¡Hola, " + clientName + "!\n\n" +
                "Te recordamos que tienes una cita programada para las próximas 24 horas.\n\n" +
                "Detalles de la reserva:\n" +
                "- Fecha y Hora: " + formattedDate + "\n" +
                "- Notas: " + (notes != null && !notes.isBlank() ? notes : "Ninguna") + "\n\n" +
                "Te esperamos.\n\n" +
                "Atentamente,\n" +
                "Equipo de Sistema de Citas"
            );

            mailSender.send(message);
            log.info("Correo de recordatorio enviado exitosamente a: {}", toEmail);

        } catch (Exception e) {
            log.error("Error crítico al enviar el correo de recordatorio a {}: {}", toEmail, e.getMessage(), e);
        }
    }
}
