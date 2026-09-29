package com.portafolio.citas.controller;

import com.portafolio.citas.dto.AvailabilityRequestDTO;
import com.portafolio.citas.dto.AvailabilityResponseDTO;
import com.portafolio.citas.service.AvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Controlador REST para la gestión de disponibilidades y consulta de horarios libres.
 */
@Tag(name = "Disponibilidad", description = "Endpoints para configurar horarios de atención y calcular turnos libres dinámicamente")
@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @Operation(summary = "Configurar disponibilidad horaria", description = "Permite a un administrador crear o actualizar la franja horaria de atención para un día de la semana específico. Requiere rol ADMIN.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disponibilidad guardada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado (requiere rol ADMIN)")
    })
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AvailabilityResponseDTO> saveOrUpdateAvailability(
            @Valid @RequestBody AvailabilityRequestDTO requestDTO,
            Authentication authentication
    ) {
        String adminEmail = authentication.getName();
        AvailabilityResponseDTO responseDTO = availabilityService.saveOrUpdateAvailability(requestDTO, adminEmail);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @Operation(summary = "Listar todas las disponibilidades", description = "Retorna la configuración completa de horarios de atención configurados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente")
    @GetMapping
    public ResponseEntity<List<AvailabilityResponseDTO>> getAllAvailabilities() {
        List<AvailabilityResponseDTO> list = availabilityService.getAllAvailabilities();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Consultar turnos disponibles", description = "Calcula y retorna los horarios (slots) libres para una fecha específica, restando turnos ocupados y horas pasadas.")
    @ApiResponse(responseCode = "200", description = "Lista de horarios libres obtenida exitosamente")
    @GetMapping("/slots")
    public ResponseEntity<List<LocalTime>> getAvailableSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<LocalTime> availableSlots = availabilityService.getAvailableSlots(date);
        return ResponseEntity.ok(availableSlots);
    }
}
