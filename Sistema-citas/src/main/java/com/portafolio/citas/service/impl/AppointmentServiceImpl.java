package com.portafolio.citas.service.impl;

import com.portafolio.citas.dto.AppointmentRequestDTO;
import com.portafolio.citas.dto.AppointmentResponseDTO;
import com.portafolio.citas.exception.AppointmentConflictException;
import com.portafolio.citas.exception.ResourceNotFoundException;
import com.portafolio.citas.model.entity.Appointment;
import com.portafolio.citas.model.entity.AppointmentStatus;
import com.portafolio.citas.model.entity.User;
import com.portafolio.citas.repository.AppointmentRepository;
import com.portafolio.citas.repository.UserRepository;
import com.portafolio.citas.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de la capa de servicio (Business Logic Layer) para la gestión de citas.
 */
@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO requestDTO, String userEmail) {
        
        // 1. Buscar al usuario autenticado por su email (extraído del token JWT)
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario autenticado no encontrado: " + userEmail));

        // 2. Validar que la fecha y hora de la cita sea en el futuro
        if (requestDTO.getAppointmentDateTime() == null || requestDTO.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha y hora de la cita debe ser posterior al momento actual.");
        }

        // 3. Validar disponibilidad de horario consultando el repositorio
        boolean isConflict = appointmentRepository.existsByAppointmentDateTimeAndStatusNot(
                requestDTO.getAppointmentDateTime(), 
                AppointmentStatus.CANCELLED
        );

        if (isConflict) {
            throw new AppointmentConflictException(
                "Ya existe una cita activa programada para la fecha y hora: " + requestDTO.getAppointmentDateTime()
            );
        }

        // 4. Si clientName o clientEmail vienen vacíos en el DTO, autocompletarlos con los datos del usuario logueado
        String clientName = (requestDTO.getClientName() != null && !requestDTO.getClientName().isBlank()) 
                ? requestDTO.getClientName() 
                : user.getFullName();

        String clientEmail = (requestDTO.getClientEmail() != null && !requestDTO.getClientEmail().isBlank()) 
                ? requestDTO.getClientEmail() 
                : user.getEmail();

        // 5. Mapear atributos del DTO de entrada a la Entidad JPA vinculando el usuario autenticado
        Appointment appointment = Appointment.builder()
                .clientName(clientName)
                .clientEmail(clientEmail)
                .appointmentDateTime(requestDTO.getAppointmentDateTime())
                .status(AppointmentStatus.PENDING)
                .notes(requestDTO.getNotes())
                .user(user) // Vinculación automática e impedimento de suplantación de identidad
                .build();

        // 6. Guardar la entidad en la base de datos
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // 7. Mapear y retornar el DTO de respuesta
        return mapToResponseDTO(savedAppointment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getMyAppointments(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario autenticado no encontrado: " + userEmail));

        return appointmentRepository.findByUser(user).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponseDTO getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la cita con el ID: " + id));
        
        return mapToResponseDTO(appointment);
    }

    @Override
    @Transactional
    public AppointmentResponseDTO cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la cita con el ID para cancelar: " + id));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("La cita ya se encuentra cancelada.");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return mapToResponseDTO(updatedAppointment);
    }

    private AppointmentResponseDTO mapToResponseDTO(Appointment appointment) {
        return AppointmentResponseDTO.builder()
                .id(appointment.getId())
                .clientName(appointment.getClientName())
                .clientEmail(appointment.getClientEmail())
                .appointmentDateTime(appointment.getAppointmentDateTime())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .userId(appointment.getUser() != null ? appointment.getUser().getId() : null)
                .userEmail(appointment.getUser() != null ? appointment.getUser().getEmail() : null)
                .build();
    }
}
