package com.portafolio.citas.repository;

import com.portafolio.citas.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la gestión de la entidad User.
 * 
 * ¿Cómo usará Spring Security este repositorio?
 * - Durante el proceso de autenticación (Login), Spring Security buscará al usuario en la base de datos 
 *   utilizando el método `findByEmail(String email)` (que actúa como username). 
 * - Si el usuario existe, Spring Security comparará la contraseña enviada en la petición con el hash almacenado,
 *   permitiendo generar el token JWT si las credenciales son correctas.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su dirección de correo electrónico (username en nuestro sistema).
     * 
     * @param email Correo electrónico a buscar.
     * @return Optional conteniendo el usuario si existe.
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica si ya existe un usuario registrado con el correo electrónico proporcionado.
     * Utilizado para prevenir duplicidad en los registros.
     * 
     * @param email Correo electrónico a verificar.
     * @return true si ya existe, false en caso contrario.
     */
    boolean existsByEmail(String email);
}
