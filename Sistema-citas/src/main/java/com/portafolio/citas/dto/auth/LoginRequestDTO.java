package com.portafolio.citas.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de inicio de sesión (Login) de un usuario existente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {

    @NotBlank(message = "El correo electronico no puede estar vacio")
    @Email(message = "Debe proporcionar un email valido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacia")
    private String password;
}
