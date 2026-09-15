package com.portafolio.citas.model.entity;

import com.portafolio.citas.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Entidad JPA que representa a los usuarios del sistema en la base de datos (tabla "users").
 * 
 * ¿Por qué la entidad User implementa UserDetails?
 * - `UserDetails` es la interfaz central de Spring Security que encapsula información sobre el usuario 
 *   (credenciales, permisos/autoridades y estado de la cuenta). 
 * - Al implementar esta interfaz, Spring Security puede autenticar directamente a nuestra entidad User
 *   durante el proceso de login sin necesidad de crear adaptadores adicionales.
 * 
 * ¿Por qué el email se usa como "username"?
 * - En las aplicaciones web modernas, los usuarios se identifican para iniciar sesión con su correo electrónico 
 *   y contraseña (en lugar de un nombre de usuario tradicional), por lo que el método getUsername() retorna el email.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Retorna la lista de permisos/roles que posee el usuario convertidos a SimpleGrantedAuthority
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        // Usamos el email como identificador único de inicio de sesión
        return email;
    }

    @Override
    public String getPassword() {
        // Retorna la contraseña cifrada (hash BCrypt) almacenada en base de datos
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // La cuenta nunca expira por defecto
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // La cuenta nunca se bloquea por defecto
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Las credenciales nunca caducan por defecto
    }

    @Override
    public boolean isEnabled() {
        return true; // La cuenta está habilitada por defecto
    }
}
