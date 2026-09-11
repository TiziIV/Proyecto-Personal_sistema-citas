package com.portafolio.citas.service.impl;

import com.portafolio.citas.dto.AppointmentRequestDTO;
import com.portafolio.citas.dto.AppointmentResponseDTO;
import com.portafolio.citas.exception.AppointmentConflictException;
import com.portafolio.citas.exception.ResourceNotFoundException;
import com.portafolio.citas.model.entity.Appointment;
import com.portafolio.citas.model.entity.AppointmentStatus;
import com.portafolio.citas.repository.AppointmentRepository;
import com.portafolio.citas.service.AppointmentService;
import lombok.RequiredArgsConstructor;
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

    @Override
    @Transactional
    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO requestDTO) {
        
        // 1. Validar que la fecha y hora de la cita sea en el futuro
        if (requestDTO.getAppointmentDateTime() == null || requestDTO.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha y hora de la cita debe ser posterior al momento actual.");
        }

        // 2. Validar disponibilidad de horario consultando el repositorio
        boolean isConflict = appointmentRepository.existsByAppointmentDateTimeAndStatusNot(
                requestDTO.getAppointmentDateTime(), 
                AppointmentStatus.CANCELLED
        );

        if (isConflict) {
            throw new AppointmentConflictException(
                "Ya existe una cita activa programada para la fecha y hora: " + requestDTO.getAppointmentDateTime()
            );
        }

        // 3. Mapear atributos del DTO de entrada a la Entidad JPA
        Appointment appointment = Appointment.builder()
                .clientName(requestDTO.getClientName())
                .clientEmail(requestDTO.getClientEmail())
                .appointmentDateTime(requestDTO.getAppointmentDateTime())
                .status(AppointmentStatus.PENDING)
                .notes(requestDTO.getNotes())
                .build();

        // 4. Guardar la entidad en la base de datos a través del repositorio
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // 5. Mapear la entidad guardada de vuelta al DTO de respuesta y retornarla
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
    public AppointmentResponseDTO getAppointmentById(Long id) {
        // Buscar la cita por ID o lanzar ResourceNotFoundException si no existe (retornará HTTP 404)
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la cita con el ID: " + id));
        
        return mapToResponseDTO(appointment);
    }

    @Override
    @Transactional
    public AppointmentResponseDTO cancelAppointment(Long id) {
        // Buscar la cita existente (lanzando ResourceNotFoundException si no existe)
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la cita con el ID para cancelar: " + id));

        // Validar si ya está cancelada
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("La cita ya se encuentra cancelada.");
        }

        /*
         * ¿Qué es un Soft Delete (Borrado Lógico) y por qué es mejor que borrar físicamente el registro?
         * - En lugar de eliminar la fila de la base de datos con un DELETE físico (que destruye historial),
         *   el Soft Delete consiste en actualizar un campo de estado (en este caso, cambiar el status a CANCELLED).
         * - Beneficios:
         *   1. Preserva la trazabilidad, auditoría e historial completo de las reservas.
         *   2. Permite análisis de datos, reportes estadísticos y cumplimiento legal.
         *   3. Evita romper integridad referencial con otras tablas si existieran relaciones.
         */
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
                .build();
    }
}
