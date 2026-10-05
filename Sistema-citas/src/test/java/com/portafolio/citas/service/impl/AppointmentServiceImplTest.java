package com.portafolio.citas.service.impl;

import com.portafolio.citas.dto.AppointmentRequestDTO;
import com.portafolio.citas.dto.AppointmentResponseDTO;
import com.portafolio.citas.exception.AppointmentConflictException;
import com.portafolio.citas.exception.ResourceNotFoundException;
import com.portafolio.citas.model.entity.Appointment;
import com.portafolio.citas.model.entity.AppointmentStatus;
import com.portafolio.citas.model.entity.User;
import com.portafolio.citas.model.enums.Role;
import com.portafolio.citas.repository.AppointmentRepository;
import com.portafolio.citas.repository.UserRepository;
import com.portafolio.citas.service.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para la clase AppointmentServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @Test
    @DisplayName("createAppointment_Success: Debe guardar y retornar el DTO cuando el horario está disponible y el usuario existe")
    void createAppointment_Success() {
        // Arrange (Dado)
        String userEmail = "juan@example.com";
        User mockUser = User.builder()
                .id(1L)
                .fullName("Juan Pérez")
                .email(userEmail)
                .role(Role.ROLE_CLIENT)
                .build();

        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(1);
        AppointmentRequestDTO requestDTO = AppointmentRequestDTO.builder()
                .clientName("Juan Pérez")
                .clientEmail(userEmail)
                .appointmentDateTime(futureDateTime)
                .notes("Revisión general")
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(mockUser));
        when(appointmentRepository.existsByAppointmentDateTimeAndStatusNot(any(LocalDateTime.class), eq(AppointmentStatus.CANCELLED)))
                .thenReturn(false);

        Appointment savedAppointment = Appointment.builder()
                .id(1L)
                .clientName("Juan Pérez")
                .clientEmail(userEmail)
                .appointmentDateTime(futureDateTime)
                .status(AppointmentStatus.PENDING)
                .notes("Revisión general")
                .user(mockUser)
                .build();

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);
        doNothing().when(emailService).sendAppointmentConfirmation(anyString(), anyString(), any(LocalDateTime.class), anyString());

        // Act (Cuando)
        AppointmentResponseDTO responseDTO = appointmentService.createAppointment(requestDTO, userEmail);

        // Assert (Entonces)
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals("Juan Pérez", responseDTO.getClientName());
        assertEquals(AppointmentStatus.PENDING, responseDTO.getStatus());
        assertEquals(1L, responseDTO.getUserId());
        assertEquals(userEmail, responseDTO.getUserEmail());

        // Verificaciones con Mockito
        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(appointmentRepository, times(1)).existsByAppointmentDateTimeAndStatusNot(any(LocalDateTime.class), eq(AppointmentStatus.CANCELLED));
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
        verify(emailService, times(1)).sendAppointmentConfirmation(eq(userEmail), eq("Juan Pérez"), eq(futureDateTime), eq("Revisión general"));
    }

    @Test
    @DisplayName("createAppointment_ThrowsConflictException_WhenSlotAlreadyTaken: Debe lanzar AppointmentConflictException si el horario está ocupado")
    void createAppointment_ThrowsConflictException_WhenSlotAlreadyTaken() {
        // Arrange (Dado)
        String userEmail = "maria@example.com";
        User mockUser = User.builder()
                .id(2L)
                .fullName("María Gómez")
                .email(userEmail)
                .role(Role.ROLE_CLIENT)
                .build();

        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(1);
        AppointmentRequestDTO requestDTO = AppointmentRequestDTO.builder()
                .clientName("María Gómez")
                .clientEmail(userEmail)
                .appointmentDateTime(futureDateTime)
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(mockUser));
        when(appointmentRepository.existsByAppointmentDateTimeAndStatusNot(any(LocalDateTime.class), eq(AppointmentStatus.CANCELLED)))
                .thenReturn(true);

        // Act & Assert (Cuando / Entonces)
        AppointmentConflictException exception = assertThrows(
                AppointmentConflictException.class,
                () -> appointmentService.createAppointment(requestDTO, userEmail)
        );

        assertTrue(exception.getMessage().contains("Ya existe una cita activa"));

        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verifyNoInteractions(emailService);
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
        
        verify(appointmentRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("cancelAppointment_Success_WhenOwner: Debe permitir al propietario cancelar su propia cita")
    void cancelAppointment_Success_WhenOwner() {
        // Arrange (Dado)
        Long appointmentId = 1L;
        String userEmail = "carlos@example.com";
        User mockUser = User.builder().id(1L).email(userEmail).role(Role.ROLE_CLIENT).build();
        Appointment existingAppointment = Appointment.builder()
                .id(appointmentId)
                .clientName("Carlos Ruiz")
                .clientEmail(userEmail)
                .appointmentDateTime(LocalDateTime.now().plusDays(2))
                .status(AppointmentStatus.PENDING)
                .user(mockUser)
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(mockUser));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act (Cuando)
        AppointmentResponseDTO responseDTO = appointmentService.cancelAppointment(appointmentId, userEmail);

        // Assert (Entonces)
        assertNotNull(responseDTO);
        assertEquals(AppointmentStatus.CANCELLED, responseDTO.getStatus());

        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("cancelAppointment_Success_WhenAdmin: Debe permitir al administrador cancelar cualquier cita")
    void cancelAppointment_Success_WhenAdmin() {
        // Arrange (Dado)
        Long appointmentId = 1L;
        String adminEmail = "admin@example.com";
        String clientEmail = "client@example.com";
        User adminUser = User.builder().id(2L).email(adminEmail).role(Role.ROLE_ADMIN).build();
        User clientUser = User.builder().id(1L).email(clientEmail).role(Role.ROLE_CLIENT).build();
        
        Appointment existingAppointment = Appointment.builder()
                .id(appointmentId)
                .clientName("Cliente Test")
                .clientEmail(clientEmail)
                .appointmentDateTime(LocalDateTime.now().plusDays(2))
                .status(AppointmentStatus.PENDING)
                .user(clientUser)
                .build();

        when(userRepository.findByEmail(adminEmail)).thenReturn(Optional.of(adminUser));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act (Cuando)
        AppointmentResponseDTO responseDTO = appointmentService.cancelAppointment(appointmentId, adminEmail);

        // Assert (Entonces)
        assertNotNull(responseDTO);
        assertEquals(AppointmentStatus.CANCELLED, responseDTO.getStatus());

        verify(userRepository, times(1)).findByEmail(adminEmail);
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("cancelAppointment_ThrowsAccessDenied_WhenNotOwnerAndNotAdmin: Debe lanzar AccessDeniedException si un cliente intenta cancelar una cita ajena")
    void cancelAppointment_ThrowsAccessDenied_WhenNotOwnerAndNotAdmin() {
        // Arrange (Dado)
        Long appointmentId = 1L;
        String attackerEmail = "attacker@example.com";
        String ownerEmail = "owner@example.com";
        User attackerUser = User.builder().id(2L).email(attackerEmail).role(Role.ROLE_CLIENT).build();
        User ownerUser = User.builder().id(1L).email(ownerEmail).role(Role.ROLE_CLIENT).build();
        
        Appointment existingAppointment = Appointment.builder()
                .id(appointmentId)
                .clientName("Dueño Cita")
                .clientEmail(ownerEmail)
                .appointmentDateTime(LocalDateTime.now().plusDays(2))
                .status(AppointmentStatus.PENDING)
                .user(ownerUser)
                .build();

        when(userRepository.findByEmail(attackerEmail)).thenReturn(Optional.of(attackerUser));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));

        // Act & Assert (Cuando / Entonces)
        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> appointmentService.cancelAppointment(appointmentId, attackerEmail)
        );

        assertTrue(exception.getMessage().contains("No tiene permisos para cancelar una cita ajena"));

        verify(userRepository, times(1)).findByEmail(attackerEmail);
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    @DisplayName("cancelAppointment_ThrowsIllegalStateException_WhenAlreadyCancelled: Debe lanzar IllegalStateException si la cita ya está cancelada")
    void cancelAppointment_ThrowsIllegalStateException_WhenAlreadyCancelled() {
        // Arrange (Dado)
        Long appointmentId = 1L;
        String userEmail = "carlos@example.com";
        User mockUser = User.builder().id(1L).email(userEmail).role(Role.ROLE_CLIENT).build();
        Appointment existingAppointment = Appointment.builder()
                .id(appointmentId)
                .clientName("Carlos Ruiz")
                .clientEmail(userEmail)
                .appointmentDateTime(LocalDateTime.now().plusDays(2))
                .status(AppointmentStatus.CANCELLED)
                .user(mockUser)
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(mockUser));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));

        // Act & Assert (Cuando / Entonces)
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> appointmentService.cancelAppointment(appointmentId, userEmail)
        );

        assertTrue(exception.getMessage().contains("La cita ya se encuentra cancelada"));

        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }
}
