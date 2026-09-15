package com.portafolio.citas.dto.auth;

import com.portafolio.citas.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de registro de un nuevo usuario en el sistema.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDTO {

    @NotBlank(message = "El nombre completo no puede estar vacio")
    private String fullName;

    @NotBlank(message = "El correo electronico no puede estar vacio")
    @Email(message = "Debe proporcionar un email valido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacia")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    // Rol opcional. Si viene nulo, el servicio asignará ROLE_CLIENT por defecto.
    private Role role;
}
