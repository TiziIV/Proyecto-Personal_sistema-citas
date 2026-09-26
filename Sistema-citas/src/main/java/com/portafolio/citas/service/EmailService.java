package com.portafolio.citas.service;

import java.time.LocalDateTime;

/**
 * Interfaz de servicio para el envío de correos electrónicos de notificación.
 */
public interface EmailService {

    /**
     * Envía un correo electrónico de confirmación de cita al cliente de forma asíncrona.
     * 
     * @param toEmail Correo destinatario.
     * @param clientName Nombre del cliente.
     * @param dateTime Fecha y hora programada de la cita.
     * @param notes Notas adicionales de la cita.
     */
    void sendAppointmentConfirmation(String toEmail, String clientName, LocalDateTime dateTime, String notes);
}
