package com.portafolio.citas.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta devuelto tras un registro o login exitoso, conteniendo el Token JWT y datos del usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {

    private String token;
    private String email;
    private String role;
}
