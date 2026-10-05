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
 * Implementación del servicio de correo electrónico con soporte asíncrono y manejo seguro de credenciales vacías.
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

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    /**
     * Envía el correo de confirmación de manera asíncrona utilizando el ThreadPool "emailExecutor".
     */
    @Override
    @Async("emailExecutor")
    public void sendAppointmentConfirmation(String toEmail, String clientName, LocalDateTime dateTime, String notes) {
        if (mailUsername == null || mailUsername.isBlank() || mailPassword == null || mailPassword.isBlank()) {
            log.warn("Servicio de correo no configurado (credenciales de SMTP vacías). Omitiendo envío de correo de confirmación a: {}", toEmail);
            return;
        }

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
            log.warn("No se pudo enviar el correo electrónico de confirmación a {} debido a un fallo en el servidor SMTP: {}", toEmail, e.getMessage());
        }
    }

    /**
     * Envía el correo de recordatorio de cita próxima de manera asíncrona utilizando el ThreadPool "emailExecutor".
     */
    @Override
    @Async("emailExecutor")
    public void sendAppointmentReminder(String toEmail, String clientName, LocalDateTime dateTime, String notes) {
        if (mailUsername == null || mailUsername.isBlank() || mailPassword == null || mailPassword.isBlank()) {
            log.warn("Servicio de correo no configurado (credenciales de SMTP vacías). Omitiendo envío de correo de recordatorio a: {}", toEmail);
            return;
        }

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
            log.warn("No se pudo enviar el correo de recordatorio a {} debido a un fallo en el servidor SMTP: {}", toEmail, e.getMessage());
        }
    }
}
