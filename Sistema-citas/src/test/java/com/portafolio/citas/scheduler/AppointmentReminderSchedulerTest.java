package com.portafolio.citas.scheduler;

import com.portafolio.citas.model.entity.Appointment;
import com.portafolio.citas.model.entity.AppointmentStatus;
import com.portafolio.citas.repository.AppointmentRepository;
import com.portafolio.citas.service.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para el componente AppointmentReminderScheduler.
 */
@ExtendWith(MockitoExtension.class)
class AppointmentReminderSchedulerTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AppointmentReminderScheduler appointmentReminderScheduler;

    @Test
    @DisplayName("sendDailyAppointmentReminders_Success: Debe consultar citas próximas y enviar recordatorios por cada una")
    void sendDailyAppointmentReminders_Success() {
        // Arrange (Dado)
        LocalDateTime futureTime = LocalDateTime.now().plusHours(5);
        Appointment appointment1 = Appointment.builder()
                .id(1L)
                .clientName("Ana Gómez")
                .clientEmail("ana@example.com")
                .appointmentDateTime(futureTime)
                .status(AppointmentStatus.CONFIRMED)
                .notes("Control médico")
                .build();

        Appointment appointment2 = Appointment.builder()
                .id(2L)
                .clientName("Luis Soto")
                .clientEmail("luis@example.com")
                .appointmentDateTime(futureTime.plusHours(2))
                .status(AppointmentStatus.PENDING)
                .build();

        List<Appointment> mockAppointments = List.of(appointment1, appointment2);

        when(appointmentRepository.findByStatusInAndAppointmentDateTimeBetween(any(), any(LocalDateTime.now().getClass()), any(LocalDateTime.now().getClass())))
                .thenReturn(mockAppointments);

        doNothing().when(emailService).sendAppointmentReminder(anyString(), anyString(), any(LocalDateTime.now().getClass()), anyString());

        // Act (Cuando)
        appointmentReminderScheduler.sendDailyAppointmentReminders();

        // Assert (Entonces)
        verify(appointmentRepository, times(1)).findByStatusInAndAppointmentDateTimeBetween(
                eq(List.of(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED)),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );

        verify(emailService, times(1)).sendAppointmentReminder(
                eq("ana@example.com"),
                eq("Ana Gómez"),
                eq(appointment1.getAppointmentDateTime()),
                eq("Control médico")
        );

        verify(emailService, times(1)).sendAppointmentReminder(
                eq("luis@example.com"),
                eq("Luis Soto"),
                eq(appointment2.getAppointmentDateTime()),
                isNull()
        );
    }

    @Test
    @DisplayName("sendDailyAppointmentReminders_NoAppointmentsFound: No debe invocar al servicio de correo si no hay citas próximas")
    void sendDailyAppointmentReminders_NoAppointmentsFound() {
        // Arrange (Dado)
        when(appointmentRepository.findByStatusInAndAppointmentDateTimeBetween(any(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // Act (Когда)
        appointmentReminderScheduler.sendDailyAppointmentReminders();

        // Assert (Entonces)
        verify(appointmentRepository, times(1)).findByStatusInAndAppointmentDateTimeBetween(any(), any(), any());
        verifyNoInteractions(emailService);
    }
}
