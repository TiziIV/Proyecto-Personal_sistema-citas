package com.portafolio.citas.service.impl;

import com.portafolio.citas.config.security.JwtUtils;
import com.portafolio.citas.dto.auth.AuthResponseDTO;
import com.portafolio.citas.dto.auth.LoginRequestDTO;
import com.portafolio.citas.dto.auth.RegisterRequestDTO;
import com.portafolio.citas.exception.AppointmentConflictException;
import com.portafolio.citas.model.entity.User;
import com.portafolio.citas.model.enums.Role;
import com.portafolio.citas.repository.UserRepository;
import com.portafolio.citas.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de la capa de servicio de autenticación.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO requestDTO) {
        // 1. Validar que el email no esté registrado previamente
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new AppointmentConflictException("Ya existe un usuario registrado con el email: " + requestDTO.getEmail());
        }

        // 2. Determinar el rol (por defecto ROLE_CLIENT si viene nulo)
        Role assignedRole = requestDTO.getRole() != null ? requestDTO.getRole() : Role.ROLE_CLIENT;

        // 3. Crear la entidad User cifrando la contraseña con BCrypt
        User user = User.builder()
                .fullName(requestDTO.getFullName())
                .email(requestDTO.getEmail())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .role(assignedRole)
                .build();

        // 4. Guardar el usuario en base de datos
        User savedUser = userRepository.save(user);

        // 5. Generar el Token JWT
        String token = jwtUtils.generateToken(savedUser);

        // 6. Retornar el DTO de respuesta con el token y datos del usuario
        return AuthResponseDTO.builder()
                .token(token)
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .build();
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO requestDTO) {
        // 1. Autenticar credenciales usando el AuthenticationManager de Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDTO.getEmail(),
                        requestDTO.getPassword()
                )
        );

        // 2. Buscar al usuario en la base de datos (se asume que existe si la autenticación fue exitosa)
        User user = userRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        // 3. Generar el Token JWT
        String token = jwtUtils.generateToken(user);

        // 4. Retornar el DTO de respuesta con el token
        return AuthResponseDTO.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
