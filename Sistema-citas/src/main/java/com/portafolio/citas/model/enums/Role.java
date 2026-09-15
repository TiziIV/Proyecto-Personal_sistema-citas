package com.portafolio.citas.model.enums;

/**
 * Enumeración que define los roles de usuario disponibles en el sistema.
 * 
 * ¿Por qué Spring Security exige por convención el prefijo "ROLE_" para la autorización basada en roles?
 * - Spring Security (específicamente al evaluar anotaciones como @Secured("ROLE_ADMIN") o hasRole("ADMIN"))
 *   espera por defecto que los roles comiencen con el prefijo "ROLE_". Al usar el método hasRole("ADMIN"), 
 *   Spring antepone automáticamente "ROLE_" de forma interna. Definirlos explícitamente con "ROLE_CLIENT" y "ROLE_ADMIN"
 *   asegura compatibilidad directa con `SimpleGrantedAuthority` y las políticas de control de acceso.
 */
public enum Role {
    ROLE_CLIENT,
    ROLE_ADMIN
}
