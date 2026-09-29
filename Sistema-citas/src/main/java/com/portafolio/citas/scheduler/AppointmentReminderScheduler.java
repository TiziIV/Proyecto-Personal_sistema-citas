package com.portafolio.citas.scheduler;

import com.portafolio.citas.model.entity.Appointment;
import com.portafolio.citas.model.entity.AppointmentStatus;
import com.portafolio.citas.repository.AppointmentRepository;
import com.portafolio.citas.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Componente programador de tareas (Scheduler) encargado de enviar recordatorios automáticos de citas.
 * 
 * Conceptos clave explicados para desarrolladores Junior:
 * 1. ¿Cómo funciona la expresión Cron (${app.scheduling.reminder-cron:0 0 8 * * *})?
 *    - Una expresión cron en Spring consta de 6 campos: [segundo] [minuto] [hora] [día del mes] [mes] [día de la semana].
 *    - La expresión por defecto "0 0 8 * * *" indica que la tarea se ejecutará todos los días exactamente a las 08:00:00 AM.
 *    - Es parametrizable mediante `application.properties` para facilitar pruebas o ajustes de horario sin recompilar.
 * 2. Delegación al método @Async:
 *    - El scheduler consulta masivamente las citas pendientes en la ventana de las próximas 24 horas y, 
 *      al invocar a `emailService.sendAppointmentReminder(...)`, delega el envío de cada correo al pool asíncrono ("emailExecutor"),
 *      evitando que el hilo del cron se bloquee si el servidor SMTP responde lento.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentReminderScheduler {

    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;

    /**
     * Tarea programada que se ejecuta automáticamente según la expresión Cron configurada.
     * Busca citas programadas entre el momento actual y 24 horas en el futuro con estado PENDING o CONFIRMED.
     */
    @Scheduled(cron = "${app.scheduling.reminder-cron:0 0 8 * * *}")
    public void sendDailyAppointmentReminders() {
        log.info("Iniciando tarea programada: Búsqueda de citas próximas para recordatorios de 24 horas...");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next24Hours = now.plusHours(24);

        // Consultar citas en estado PENDING o CONFIRMED dentro de la ventana de las próximas 24 horas
        List<Appointment> upcomingAppointments = appointmentRepository.findByStatusInAndAppointmentDateTimeBetween(
                List.of(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED),
                now,
                next24Hours
        );

        log.info("Se encontraron {} citas en las próximas 24 horas para enviar recordatorios.", upcomingAppointments.size());

        // Iterar y enviar recordatorio asíncrono para cada cita encontrada
        for (Appointment appointment : upcomingAppointments) {
            try {
                emailService.sendAppointmentReminder(
                        appointment.getClientEmail(),
                        appointment.getClientName(),
                        appointment.getAppointmentDateTime(),
                        appointment.getNotes()
                );
            } catch (Exception e) {
                log.error("Error al procesar el recordatorio para la cita ID {}: {}", appointment.getId(), e.getMessage());
            }
        }

        log.info("Tarea programada de recordatorios finalizada exitosamente.");
    }
}
