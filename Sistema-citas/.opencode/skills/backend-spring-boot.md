# Skill: Backend Spring Boot Architecture & Best Practices

Esta habilidad define las pautas de diseño y desarrollo backend para el proyecto "Sistema-citas" utilizando Spring Boot 3.3.4, Java 21 y Spring Security 6.

## 📐 Directrices de Arquitectura
1. **Separación Estricta en Capas:**
   - **Controller:** Manejo de transporte HTTP, inyección de `Authentication`, anotado con `@RestController` y mapeado con `@RequestMapping`.
   - **Service:** Lógica de negocio pura, validación de reglas de dominio (ej. fechas futuras, solapamiento de turnos) y transaccionalidad (`@Transactional`).
   - **Repository:** Interfaces que extienden `JpaRepository<T, ID>` aprovechando consultas derivadas (*Derived Queries*).
   - **DTOs & Model:** Desacoplamiento estricto entre entidades JPA (`@Entity`) y contratos de entrada/salida HTTP mediante DTOs.

2. **Manejo de Excepciones Semánticas:**
   - Creación de excepciones de negocio personalizadas (`ResourceNotFoundException`, `AppointmentConflictException`).
   - Intercepción centralizada mediante `@RestControllerAdvice` (`GlobalExceptionHandler`) para retornar códigos HTTP correctos (`400`, `404`, `409`) en formato JSON limpio.

3. **Asincronismo Robusto con `@Async`:**
   - El envío de correos electrónicos transaccionales no debe bloquear el hilo principal de atención HTTP.
   - Se debe utilizar un `ThreadPoolTaskExecutor` dedicado (ej. `"emailExecutor"`) configurado en `AsyncConfig`.
   - Las operaciones asíncronas deben envolverse en bloques `try-catch` para registrar errores con SLF4J sin relanzar excepciones que provoquen *rollback* en la base de datos o alteren la respuesta `201 Created` al cliente.
