package com.portafolio.citas.controller;

import com.portafolio.citas.dto.AppointmentRequestDTO;
import com.portafolio.citas.dto.AppointmentResponseDTO;
import com.portafolio.citas.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar las peticiones HTTP relacionadas con las citas.
 * 
 * ¿Por qué usamos @RestController en lugar de @Controller tradicional?
 * - @RestController es una combinación de @Controller y @ResponseBody.
 * - Le indica a Spring que todos los métodos de esta clase retornarán objetos directamente (como DTOs o listas)
 *   los cuales serán convertidos automáticamente a formato JSON (o XML) mediante un conversor como Jackson,
 *   sin necesidad de buscar vistas HTML o páginas JSP.
 */
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    // Servicio inyectado por constructor gracias a @RequiredArgsConstructor
    private final AppointmentService appointmentService;

    /**
     * Endpoint POST para crear una nueva cita.
     * 
     * ¿Por qué usamos el código de estado HTTP 201 CREATED?
     * - El estándar HTTP indica que cuando una petición resulta en la creación exitosa de un nuevo recurso,
     *   el servidor debe retornar el código 201 en lugar de 200 OK, informando claramente al cliente que el recurso fue creado.
     * 
     * @param requestDTO Datos enviados en el cuerpo de la petición (JSON) para crear la cita.
     * @return ResponseEntity con el DTO de la cita creada y el estado HTTP 201.
     */
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(@RequestBody AppointmentRequestDTO requestDTO) {
        AppointmentResponseDTO createdAppointment = appointmentService.createAppointment(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
    }

    /**
     * Endpoint GET para listar todas las citas registradas en el sistema.
     * 
     * ¿Por qué usamos el código de estado HTTP 200 OK?
     * - Es el código estándar para una lectura u operación de consulta exitosa que retorna datos.
     * 
     * @return ResponseEntity con la lista de citas en formato JSON y estado HTTP 200.
     */
    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {
        List<AppointmentResponseDTO> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(appointments);
    }

    /**
     * Endpoint GET para consultar una cita específica por su ID.
     * 
     * @param id Identificador único de la cita recibido en la URL (Path Variable).
     * @return ResponseEntity con los datos de la cita encontrada y estado HTTP 200.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentById(@PathVariable Long id) {
        AppointmentResponseDTO appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }

    /**
     * Endpoint DELETE para cancelar una cita existente.
     * 
     * ¿Por qué usamos el código de estado HTTP 204 NO_CONTENT?
     * - El estándar HTTP 204 indica que la operación se procesó con éxito, pero la respuesta no requiere
     *   retornar ningún cuerpo (body) de contenido adicional, ya que el recurso fue actualizado/cancelado.
     * 
     * @param id Identificador de la cita a cancelar.
     * @return ResponseEntity vacío con estado HTTP 204.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
