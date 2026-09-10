package com.portafolio.citas.service.impl;

import com.portafolio.citas.dto.AppointmentRequestDTO;
import com.portafolio.citas.dto.AppointmentResponseDTO;
import com.portafolio.citas.exception.AppointmentConflictException;
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
 * 
 * Anotaciones utilizadas:
 * - @Service: Indica a Spring que esta clase es un componente de servicio bean gestionado en el contenedor IoC.
 * - @RequiredArgsConstructor: Anotación de Lombok que genera un constructor con todos los campos 'final',
 *   permitiendo la inyección de dependencias por constructor de forma limpia y recomendada (sin necesidad de @Autowired).
 */
@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    // Repositorio inyectado de forma segura mediante inyección por constructor (gracias a @RequiredArgsConstructor)
    private final AppointmentRepository appointmentRepository;

    @Override
    @Transactional
    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO requestDTO) {
        
        // 1. Validar que la fecha y hora de la cita sea en el futuro
        if (requestDTO.getAppointmentDateTime() == null || requestDTO.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha y hora de la cita debe ser posterior al momento actual.");
        }

        // 2. Validar disponibilidad de horario consultando el repositorio
        // Verificamos si ya existe una cita en esa misma fecha/hora cuyo estado NO sea CANCELLED.
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
                .status(AppointmentStatus.PENDING) // Por defecto, toda nueva cita nace con estado PENDING
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
        // Consultar todas las entidades y transformarlas (mapearlas) a una lista de DTOs de respuesta
        return appointmentRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponseDTO getAppointmentById(Long id) {
        // Buscar la cita por ID o lanzar una excepción si no existe
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la cita con el ID proporcionado: " + id));
        
        return mapToResponseDTO(appointment);
    }

    @Override
    @Transactional
    public AppointmentResponseDTO cancelAppointment(Long id) {
        // Buscar la cita existente
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la cita con el ID proporcionado para cancelar: " + id));

        // Validar si ya está cancelada opcionalmente o proceder al cambio de estado
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("La cita ya se encuentra cancelada.");
        }

        // Actualizar el estado a CANCELLED (liberando el horario en futuras validaciones)
        appointment.setStatus(AppointmentStatus.CANCELLED);
        
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return mapToResponseDTO(updatedAppointment);
    }

    /**
     * Método auxiliar privado para mapear una Entidad Appointment a un AppointmentResponseDTO.
     * Centraliza la conversión para evitar código duplicado (DRY - Don't Repeat Yourself).
     * 
     * @param appointment Entidad JPA.
     * @return AppointmentResponseDTO mapeado.
     */
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
