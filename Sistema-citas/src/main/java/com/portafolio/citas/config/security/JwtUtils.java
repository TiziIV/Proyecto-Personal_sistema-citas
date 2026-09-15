package com.portafolio.citas.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

/**
 * Utilidad para la gestión, generación y validación de tokens JWT (JSON Web Tokens).
 * 
 * ¿Qué contiene un JWT y por qué debe firmarse criptográficamente?
 * 1. Estructura de un JWT:
 *    - Header (Cabecera): Especifica el tipo de token (JWT) y el algoritmo de firma (ej. HS256).
 *    - Payload (Carga útil): Contiene las claims o declaraciones (subject/username, fecha de emisión, expiración y roles).
 *    - Signature (Firma): Se calcula tomando el Header, el Payload y una clave secreta mediante un algoritmo criptográfico.
 * 2. Importancia de la firma criptográfica:
 *    - Garantiza la **integridad** de los datos: si alguien intenta alterar el payload (ej. cambiar su rol de CLIENT a ADMIN),
 *      la firma dejará de ser válida y el servidor rechazará el token inmediatamente.
 *    - Permite una arquitectura **Stateless** (sin estado): el servidor no necesita almacenar sesiones en memoria ni en base de datos;
 *      confía en la autenticidad del token porque lleva su propia firma criptográfica.
 */
@Component
public class JwtUtils {

    // Clave secreta en Base64 (debe tener al menos 256 bits / 32 bytes de longitud para HS256)
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    
    // Tiempo de expiración del token en milisegundos (24 horas = 86,400,000 ms)
    private static final long EXPIRATION_TIME = 86400000L;

    /**
     * Genera un token JWT para un usuario autenticado.
     * 
     * @param userDetails Detalles del usuario (UserDetails).
     * @return Token JWT en formato String.
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername()) // Guardamos el email como subject
                .issuedAt(new Date(System.currentTimeMillis())) // Fecha de emisión
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Fecha de expiración (24h)
                .signWith(getSignInKey()) // Firma criptográfica con la clave secreta
                .compact();
    }

    /**
     * Extrae el nombre de usuario (email) contenido en el token JWT.
     * 
     * @param token Token JWT.
     * @return Email del usuario.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Valida si el token JWT es válido para el usuario proporcionado.
     * 
     * @param token Token JWT.
     * @param userDetails Detalles del usuario.
     * @return true si el token es válido y no ha expirado, false en caso contrario.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
