package com.portafolio.citas.controller;

import com.portafolio.citas.dto.auth.AuthResponseDTO;
import com.portafolio.citas.dto.auth.LoginRequestDTO;
import com.portafolio.citas.dto.auth.RegisterRequestDTO;
import com.portafolio.citas.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para la autenticación y registro de usuarios.
 */
@Tag(name = "Autenticación", description = "Endpoints para registro y login de usuarios con JWT")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Registrar un nuevo usuario", description = "Crea una nueva cuenta de usuario (CLIENT o ADMIN) y retorna su token JWT.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "El correo electrónico ya está registrado")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO requestDTO) {
        AuthResponseDTO responseDTO = authService.register(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @Operation(summary = "Iniciar sesión (Login)", description = "Autentica al usuario mediante email y contraseña y retorna un token JWT válido.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autenticación exitosa, token JWT retornado"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas (Bad Credentials)")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        AuthResponseDTO responseDTO = authService.login(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
