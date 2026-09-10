package com.portafolio.citas.controller;

import com.portafolio.citas.dto.AppointmentRequestDTO;
import com.portafolio.citas.dto.AppointmentResponseDTO;
import com.portafolio.citas.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar las peticiones HTTP relacionadas con las citas.
 */
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    /**
     * Endpoint POST para crear una nueva cita.
     * 
     * ¿Por qué es crucial la anotación @Valid en el parámetro @RequestBody?
     * - Sin la anotación @Valid, Spring Boot recibe el objeto DTO pero **ignora por completo** 
     *   todas las anotaciones de validación declaradas en él (@NotBlank, @Email, @Future, etc.),
     *   permitiendo que pasen datos vacíos o inválidos hacia la capa de servicio.
     * - Al colocar @Valid, le indicamos a Spring que active el validador (Bean Validation / Hibernate Validator)
     *   antes de ejecutar el método, y si hay errores, lanza automáticamente una excepción de tipo MethodArgumentNotValidException.
     * 
     * @param requestDTO Datos validados de la cita.
     * @return ResponseEntity con la cita creada y status 201 CREATED.
     */
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(@Valid @RequestBody AppointmentRequestDTO requestDTO) {
        AppointmentResponseDTO createdAppointment = appointmentService.createAppointment(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {
        List<AppointmentResponseDTO> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentById(@PathVariable Long id) {
        AppointmentResponseDTO appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
