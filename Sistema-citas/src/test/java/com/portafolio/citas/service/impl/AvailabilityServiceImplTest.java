package com.portafolio.citas.service.impl;

import com.portafolio.citas.dto.AvailabilityRequestDTO;
import com.portafolio.citas.dto.AvailabilityResponseDTO;
import com.portafolio.citas.model.entity.Appointment;
import com.portafolio.citas.model.entity.AppointmentStatus;
import com.portafolio.citas.model.entity.Availability;
import com.portafolio.citas.model.entity.User;
import com.portafolio.citas.model.enums.Role;
import com.portafolio.citas.repository.AppointmentRepository;
import com.portafolio.citas.repository.AvailabilityRepository;
import com.portafolio.citas.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para la clase AvailabilityServiceImpl.
 * 
 * Conceptos clave explicados para desarrolladores Junior:
 * - ¿Cómo se prueba el cálculo dinámico de slots?
 *   Mediante Mockito simulamos tanto la configuración horaria de la entidad Availability 
 *   como las citas existentes devueltas por AppointmentRepository. De esta forma, 
 *   verificamos que el algoritmo de resta (filtrado de turnos ocupados) funcione con precisión determinista 
 *   sin depender de una base de datos real.
 */
@ExtendWith(MockitoExtension.class)
class AvailabilityServiceImplTest {

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AvailabilityServiceImpl availabilityService;

    @Test
    @DisplayName("getAvailableSlots_Success_WhenNoAppointmentsExist: Debe retornar todos los slots teóricos cuando no hay citas reservadas")
    void getAvailableSlots_Success_WhenNoAppointmentsExist() {
        // Arrange (Dado)
        // Elegimos una fecha futura fija para evitar interferencia con el filtro de hora actual (si se ejecuta hoy)
        LocalDate futureDate = LocalDate.now().plusDays(2);
        DayOfWeek dayOfWeek = futureDate.getDayOfWeek();

        Availability mockAvailability = Availability.builder()
                .id(1L)
                .dayOfWeek(dayOfWeek)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(10, 0))
                .slotDurationMinutes(30)
                .build();

        when(availabilityRepository.findByDayOfWeek(dayOfWeek)).thenReturn(List.of(mockAvailability));
        when(appointmentRepository.findByAppointmentDateTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // Act (Когда)
        List<LocalTime> availableSlots = availabilityService.getAvailableSlots(futureDate);

        // Assert (Entonces)
        assertNotNull(availableSlots);
        assertEquals(4, availableSlots.size());
        assertEquals(LocalTime.of(8, 0), availableSlots.get(0));
        assertEquals(LocalTime.of(8, 30), availableSlots.get(1));
        assertEquals(LocalTime.of(9, 0), availableSlots.get(2));
        assertEquals(LocalTime.of(9, 30), availableSlots.get(3));

        verify(availabilityRepository, times(1)).findByDayOfWeek(dayOfWeek);
        verify(appointmentRepository, times(1)).findByAppointmentDateTimeBetween(any(), any());
    }

    @Test
    @DisplayName("getAvailableSlots_ExcludesAlreadyBookedSlots: Debe excluir de los slots teóricos aquellos que ya se encuentran ocupados")
    void getAvailableSlots_ExcludesAlreadyBookedSlots() {
        // Arrange (Dado)
        LocalDate futureDate = LocalDate.now().plusDays(2);
        DayOfWeek dayOfWeek = futureDate.getDayOfWeek();

        Availability mockAvailability = Availability.builder()
                .id(1L)
                .dayOfWeek(dayOfWeek)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(10, 0))
                .slotDurationMinutes(30)
                .build();

        // Simulamos una cita ocupada a las 08:30
        Appointment bookedAppointment = Appointment.builder()
                .id(10L)
                .appointmentDateTime(LocalDateTime.of(futureDate, LocalTime.of(8, 30)))
                .status(AppointmentStatus.CONFIRMED)
                .build();

        when(availabilityRepository.findByDayOfWeek(dayOfWeek)).thenReturn(List.of(mockAvailability));
        when(appointmentRepository.findByAppointmentDateTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(bookedAppointment));

        // Act (Cuando)
        List<LocalTime> availableSlots = availabilityService.getAvailableSlots(futureDate);

        // Assert (Entonces)
        assertNotNull(availableSlots);
        assertEquals(3, availableSlots.size());
        assertFalse(availableSlots.contains(LocalTime.of(8, 30))); // El slot de las 08:30 debe estar excluido
        assertEquals(LocalTime.of(8, 0), availableSlots.get(0));
        assertEquals(LocalTime.of(9, 0), availableSlots.get(1));
        assertEquals(LocalTime.of(9, 30), availableSlots.get(2));

        verify(availabilityRepository, times(1)).findByDayOfWeek(dayOfWeek);
        verify(appointmentRepository, times(1)).findByAppointmentDateTimeBetween(any(), any());
    }

    @Test
    @DisplayName("getAvailableSlots_ReturnsEmptyList_WhenNoAvailabilityConfigured: Debe retornar lista vacía si no hay configuración para ese día")
    void getAvailableSlots_ReturnsEmptyList_WhenNoAvailabilityConfigured() {
        // Arrange (Dado)
        LocalDate futureDate = LocalDate.now().plusDays(2);
        DayOfWeek dayOfWeek = futureDate.getDayOfWeek();

        when(availabilityRepository.findByDayOfWeek(dayOfWeek)).thenReturn(Collections.emptyList());

        // Act (Cuando)
        List<LocalTime> availableSlots = availabilityService.getAvailableSlots(futureDate);

        // Assert (Entonces)
        assertNotNull(availableSlots);
        assertTrue(availableSlots.isEmpty());

        verify(availabilityRepository, times(1)).findByDayOfWeek(dayOfWeek);
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    @DisplayName("saveOrUpdateAvailability_Success: Debe guardar y retornar la configuración de disponibilidad del admin")
    void saveOrUpdateAvailability_Success() {
        // Arrange (Dado)
        String adminEmail = "admin@sistemacitas.com";
        User adminUser = User.builder()
                .id(1L)
                .fullName("Administrador")
                .email(adminEmail)
                .role(Role.ROLE_ADMIN)
                .build();

        AvailabilityRequestDTO requestDTO = AvailabilityRequestDTO.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .slotDurationMinutes(30)
                .build();

        when(userRepository.findByEmail(adminEmail)).thenReturn(Optional.of(adminUser));
        when(availabilityRepository.findByUserAndDayOfWeek(adminUser, DayOfWeek.MONDAY)).thenReturn(Optional.empty());

        Availability savedAvailability = Availability.builder()
                .id(1L)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .slotDurationMinutes(30)
                .user(adminUser)
                .build();

        when(availabilityRepository.save(any(Availability.class))).thenReturn(savedAvailability);

        // Act (Cuando)
        AvailabilityResponseDTO responseDTO = availabilityService.saveOrUpdateAvailability(requestDTO, adminEmail);

        // Assert (Entonces)
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals(DayOfWeek.MONDAY, responseDTO.getDayOfWeek());
        assertEquals(LocalTime.of(9, 0), responseDTO.getStartTime());
        assertEquals(LocalTime.of(17, 0), responseDTO.getEndTime());
        assertEquals(30, responseDTO.getSlotDurationMinutes());
        assertEquals(1L, responseDTO.getUserId());

        verify(userRepository, times(1)).findByEmail(adminEmail);
        verify(availabilityRepository, times(1)).findByUserAndDayOfWeek(adminUser, DayOfWeek.MONDAY);
        verify(availabilityRepository, times(1)).save(any(Availability.class));
    }
}
