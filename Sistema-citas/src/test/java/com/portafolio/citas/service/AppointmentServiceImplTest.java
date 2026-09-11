package com.portafolio.citas.service;

import com.portafolio.citas.dto.AppointmentRequestDTO;
import com.portafolio.citas.dto.AppointmentResponseDTO;
import com.portafolio.citas.exception.AppointmentConflictException;
import com.portafolio.citas.exception.ResourceNotFoundException;
import com.portafolio.citas.model.entity.Appointment;
import com.portafolio.citas.model.entity.AppointmentStatus;
import com.portafolio.citas.repository.AppointmentRepository;
import com.portafolio.citas.service.impl.AppointmentServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para la clase AppointmentServiceImpl.
 * 
 * Conceptos clave explicados para desarrolladores Junior:
 * 1. ¿Qué es un Mock y por qué usamos Mockito en lugar de una base de datos real?
 *    - Un Mock es un objeto simulado que imita el comportamiento de objetos reales (en este caso, AppointmentRepository).
 *    - Usar Mockito nos permite aislar la lógica de negocio del servicio, evitando depender de bases de datos externas (como H2 o MySQL),
 *      conexiones de red o servicios lentos. Las pruebas unitarias corren extremadamente rápido y son totalmente deterministas.
 * 
 * 2. Patrón AAA (Arrange, Act, Assert / Dado, Cuando, Entonces):
 *    - Arrange (Dado): Preparamos los datos de entrada, configuramos los mocks y definimos qué deben retornar cuando sean invocados.
 *    - Act (Cuando): Ejecutamos el método del servicio que queremos probar.
 *    - Assert (Entonces): Verificamos mediante afirmaciones (assertions) que el resultado sea el esperado.
 * 
 * 3. ¿Para qué sirve verify(...) de Mockito?
 *    - Sirve para comprobar que un método específico de un mock fue llamado (o NO fue llamado) 
 *      cierto número de veces (ej. verificar que repository.save(...) nunca se ejecute si hay un conflicto de negocio).
 */
@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @Test
    @DisplayName("createAppointment_Success: Debe guardar y retornar el DTO cuando el horario está disponible")
    void createAppointment_Success() {
        // Arrange (Dado)
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(1);
        AppointmentRequestDTO requestDTO = AppointmentRequestDTO.builder()
                .clientName("Juan Pérez")
                .clientEmail("juan@example.com")
                .appointmentDateTime(futureDateTime)
                .notes("Revisión general")
                .build();

        // Simulamos que NO existe conflicto en ese horario (retorna false)
        when(appointmentRepository.existsByAppointmentDateTimeAndStatusNot(any(LocalDateTime.class), eq(AppointmentStatus.CANCELLED)))
                .thenReturn(false);

        Appointment savedAppointment = Appointment.builder()
                .id(1L)
                .clientName("Juan Pérez")
                .clientEmail("juan@example.com")
                .appointmentDateTime(futureDateTime)
                .status(AppointmentStatus.PENDING)
                .notes("Revisión general")
                .build();

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        // Act (Cuando)
        AppointmentResponseDTO responseDTO = appointmentService.createAppointment(requestDTO);

        // Assert (Entonces)
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals("Juan Pérez", responseDTO.getClientName());
        assertEquals(AppointmentStatus.PENDING, responseDTO.getStatus());

        // Verificamos que se consultó la disponibilidad y se guardó en el repositorio exactamente 1 vez
        verify(appointmentRepository, times(1)).existsByAppointmentDateTimeAndStatusNot(any(LocalDateTime.class), eq(AppointmentStatus.CANCELLED));
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("createAppointment_ThrowsConflictException_WhenSlotAlreadyTaken: Debe lanzar AppointmentConflictException y no guardar si el horario está ocupado")
    void createAppointment_ThrowsConflictException_WhenSlotAlreadyTaken() {
        // Arrange (Dado)
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(1);
        AppointmentRequestDTO requestDTO = AppointmentRequestDTO.builder()
                .clientName("María Gómez")
                .clientEmail("maria@example.com")
                .appointmentDateTime(futureDateTime)
                .build();

        // Simulamos que SÍ existe conflicto en ese horario (retorna true)
        when(appointmentRepository.existsByAppointmentDateTimeAndStatusNot(any(LocalDateTime.class), eq(AppointmentStatus.CANCELLED)))
                .thenReturn(true);

        // Act & Assert (Cuando / Entonces)
        AppointmentConflictException exception = assertThrows(
                AppointmentConflictException.class,
                () -> appointmentService.createAppointment(requestDTO)
        );

        assertTrue(exception.getMessage().contains("Ya existe una cita activa"));

        // Verificamos que NUNCA se llamó al método save(...) del repositorio debido al conflicto de negocio
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    @DisplayName("getAppointmentById_ThrowsResourceNotFoundException_WhenIdNotFound: Debe lanzar ResourceNotFoundException si el ID no existe")
    void getAppointmentById_ThrowsResourceNotFoundException_WhenIdNotFound() {
        // Arrange (Dado)
        Long nonExistentId = 999L;
        when(appointmentRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert (Cuando / Entonces)
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> appointmentService.getAppointmentById(nonExistentId)
        );

        assertTrue(exception.getMessage().contains("No se encontró la cita con el ID: " + nonExistentId));
        
        // Verificamos que se intentó buscar por ID
        verify(appointmentRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("cancelAppointment_Success: Debe buscar la cita, cambiar su estado a CANCELLED y guardarla")
    void cancelAppointment_Success() {
        // Arrange (Dado)
        Long appointmentId = 1L;
        Appointment existingAppointment = Appointment.builder()
                .id(appointmentId)
                .clientName("Carlos Ruiz")
                .clientEmail("carlos@example.com")
                .appointmentDateTime(LocalDateTime.now().plusDays(2))
                .status(AppointmentStatus.PENDING)
                .build();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act (Cuando)
        AppointmentResponseDTO responseDTO = appointmentService.cancelAppointment(appointmentId);

        // Assert (Entonces)
        assertNotNull(responseDTO);
        assertEquals(AppointmentStatus.CANCELLED, responseDTO.getStatus());

        // Verificamos que se buscó por ID y se guardó la entidad actualizada con el nuevo estado
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }
}
