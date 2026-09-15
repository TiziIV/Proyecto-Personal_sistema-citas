package com.portafolio.citas.service;

import com.portafolio.citas.dto.auth.AuthResponseDTO;
import com.portafolio.citas.dto.auth.LoginRequestDTO;
import com.portafolio.citas.dto.auth.RegisterRequestDTO;

/**
 * Interfaz de servicio para la autenticación y registro de usuarios.
 */
public interface AuthService {

    /**
     * Registra un nuevo usuario en el sistema y retorna su Token JWT.
     * 
     * @param requestDTO Datos de registro.
     * @return AuthResponseDTO con token, email y rol.
     */
    AuthResponseDTO register(RegisterRequestDTO requestDTO);

    /**
     * Autentica a un usuario existente y retorna un nuevo Token JWT.
     * 
     * @param requestDTO Credenciales de acceso (email y password).
     * @return AuthResponseDTO con token, email y rol.
     */
    AuthResponseDTO login(LoginRequestDTO requestDTO);
}
