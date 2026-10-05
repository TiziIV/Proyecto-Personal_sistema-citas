package com.portafolio.citas.controller;

import com.portafolio.citas.dto.AppointmentRequestDTO;
import com.portafolio.citas.dto.AppointmentResponseDTO;
import com.portafolio.citas.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar las peticiones HTTP relacionadas con las citas.
 */
@Tag(name = "Citas", description = "Endpoints para la gestión y reserva de turnos")
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Operation(summary = "Crear una nueva cita", description = "Registra una nueva cita vinculándola automáticamente al usuario autenticado vía Token JWT.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cita creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o error de validación"),
        @ApiResponse(responseCode = "401", description = "No autorizado (Token JWT faltante o inválido)"),
        @ApiResponse(responseCode = "409", description = "Conflicto de horario (ya existe una cita activa en esa fecha y hora)")
    })
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(
            @Valid @RequestBody AppointmentRequestDTO requestDTO,
            Authentication authentication
    ) {
        // Obtenemos el email del usuario logueado directamente del contexto de seguridad (Token JWT)
        String userEmail = authentication.getName();
        AppointmentResponseDTO createdAppointment = appointmentService.createAppointment(requestDTO, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
    }

    @Operation(summary = "Listar todas las citas", description = "Retorna una lista con todas las citas registradas en el sistema (Uso administrativo).")
    @ApiResponse(responseCode = "200", description = "Lista de citas obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {
        List<AppointmentResponseDTO> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(appointments);
    }

    @Operation(summary = "Listar mis citas", description = "Retorna exclusivamente las citas asociadas al usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Lista de citas del usuario obtenida exitosamente")
    @GetMapping("/my-appointments")
    public ResponseEntity<List<AppointmentResponseDTO>> getMyAppointments(Authentication authentication) {
        String userEmail = authentication.getName();
        List<AppointmentResponseDTO> myAppointments = appointmentService.getMyAppointments(userEmail);
        return ResponseEntity.ok(myAppointments);
    }

    @Operation(summary = "Buscar cita por ID", description = "Busca y retorna los detalles de una cita específica según su identificador único.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cita encontrada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada con el ID proporcionado"),
        @ApiResponse(responseCode = "400", description = "ID inválido")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentById(@PathVariable Long id) {
        AppointmentResponseDTO appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }

    @Operation(summary = "Cancelar una cita", description = "Actualiza el estado de una cita existente a CANCELLED (Soft Delete) validando propiedad o rol administrador.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cita cancelada exitosamente (sin contenido de respuesta)"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado (no es propietario ni administrador)"),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada con el ID proporcionado"),
        @ApiResponse(responseCode = "400", description = "Cita ya cancelada o ID inválido")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long id, Authentication authentication) {
        String userEmail = authentication.getName();
        appointmentService.cancelAppointment(id, userEmail);
        return ResponseEntity.noContent().build();
    }
}
