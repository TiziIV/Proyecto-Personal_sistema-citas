# LLM Technical Context & System Blueprint (v6.0.0)

Este documento proporciona un contexto técnico exhaustivo, estructurado y autocontenido del proyecto **"Sistema-citas"** en su versión consolidada actual (**v6.0.0**). Está diseñado para ser leído e interpretado por cualquier modelo de lenguaje (Claude, GPT, DeepSeek, etc.) para comprender con precisión absoluta la arquitectura, decisiones de diseño, contratos de datos y flujos del sistema.

---

## 1. Metadatos del Repositorio y Versiones Tecnológicas
- **Repositorio:** `TiziIV/Proyecto-Personal_sistema-citas`
- **Rama Principal por Defecto:** `master`
- **Versión Consolidada Actual:** `v6.0.0` (Fase 6 - Notificaciones asíncronas por email y CI/CD completo).
- **Stack Tecnológico Exacto:**
  - **Backend:** Java 21, Spring Boot 3.3.4, Spring Data JPA / Hibernate, Spring Security 6, JJWT 0.12.5, Spring Mail, SpringDoc OpenAPI 2.5.0 (Swagger UI).
  - **Frontend:** React 19, Vite, Tailwind CSS v4, Axios.
  - **Base de Datos & DevOps:** PostgreSQL 16 Alpine, Docker (Multi-stage builds), Docker Compose, Nginx Alpine, GitHub Actions CI.
  - **Testing:** JUnit 5, Mockito.

---

## 2. Arquitectura del Sistema
El sistema sigue una arquitectura de microservicios locales contenerizados, comunicados mediante una red bridge interna de Docker (`citas-network`):
1. **Frontend (`citas-frontend`):** SPA en React servida por un servidor web **Nginx Alpine** (puerto host `5173` mapeado al puerto interno `80`). Gestiona las rutas con redirección SPA (`try_files`) y el estado global con `AuthContext`.
2. **Backend (`citas-backend`):** API RESTful desarrollada en Spring Boot 3.3.4 (puerto `8080`). Expone controladores seguros protegidos por filtros JWT.
3. **Base de Datos (`citas-postgres`):** Motor relacional **PostgreSQL 16 Alpine** (puerto interno `5432`, expuesto en el host como `5433` con volumen persistente `postgres_data`).

---

## 3. Esquema Relacional de Base de Datos (JPA Entities)

### Entidad `User` (Tabla `users`)
- Atributos: `id` (Long, PK Identity), `fullName` (String), `email` (String, Unique), `password` (String, Hash BCrypt), `role` (`Role` enum: `ROLE_CLIENT`, `ROLE_ADMIN`).
- Implementa `org.springframework.security.core.userdetails.UserDetails` (usando el `email` como `username`).

### Entidad `Appointment` (Tabla `appointments`)
- Atributos: `id` (Long, PK Identity), `clientName` (String), `clientEmail` (String), `appointmentDateTime` (LocalDateTime), `status` (`AppointmentStatus` enum: `PENDING`, `CONFIRMED`, `CANCELLED`), `notes` (String).
- Relación `@ManyToOne(fetch = FetchType.LAZY)` con `User` mediante la clave foránea `user_id` (`nullable = false`).
- **Validación de Solapamiento:** Gestionada mediante la consulta derivada en `AppointmentRepository`:
  `boolean existsByAppointmentDateTimeAndStatusNot(LocalDateTime appointmentDateTime, AppointmentStatus status);`
  (Excluyendo citas con estado `CANCELLED`, permitiendo reutilizar horarios liberados).

---

## 4. Mecanismo de Seguridad (Spring Security 6 & JWT)
- **Autenticación Stateless:** Basada en tokens JWT (JJWT 0.12.5) firmados con HMAC-SHA256.
- **Filtro de Seguridad (`JwtAuthenticationFilter`):** Extiende `OncePerRequestFilter`. Intercepta cada cabecera `Authorization: Bearer <token>`, valida la firma y establece la autenticación en el `SecurityContextHolder`.
- **Desacoplamiento de Beans (`ApplicationSecurityConfig`):** Extrae `UserDetailsService`, `PasswordEncoder` (BCrypt), `AuthenticationProvider` y `AuthenticationManager` a una clase independiente para evitar dependencias circulares con `SecurityConfig`.
- **Control de Acceso (RBAC):**
  - Rutas públicas: `/api/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`.
  - Rutas protegidas: `/api/appointments/**` requiere autenticación general; método `DELETE` restringido exclusivamente a `ROLE_ADMIN`.

---

## 5. Sistema de Notificaciones Asíncronas (`@Async`)
- **Configuración (`AsyncConfig`):** Define un `ThreadPoolTaskExecutor` nombrado `"emailExecutor"` (`corePoolSize: 2`, `maxPoolSize: 5`, `queueCapacity: 50`, prefijo `"EmailAsync-"`).
- **Servicio (`EmailServiceImpl`):** Inyecta `JavaMailSender` y utiliza `@Async("emailExecutor")`. Envía correos electrónicos transaccionales a través de un servidor SMTP (configurado con valores por defecto para Mailtrap Sandbox).
- **Resiliencia:** Envuelto en un bloque `try-catch` con logs SLF4J, garantizando que un fallo SMTP no altere la transacción de base de datos ni interfiera con la respuesta HTTP `201 Created` devuelta al cliente.

---

## 6. Pipeline de Integración Continua (CI/CD - GitHub Actions)
- Archivo: `.github/workflows/ci.yml`
- Disparadores: `push` y `pull_request` en ramas `master` y `main`.
- Jobs:
  - `backend-ci`: Ubuntu runner, JDK 21 (Temurin con caché Maven), ejecuta `mvn clean test`.
  - `frontend-ci`: Ubuntu runner, Node.js 20 (con caché NPM), directorio de trabajo `./frontend`, ejecuta `npm ci` y `npm run build`.

---

## 7. Roadmap Histórico Consolidado (Git Tags)
- `v1.0.0`: Estructura inicial del proyecto Maven, dependencias y Entidades JPA (`Appointment`, `AppointmentStatus`).
- `v2.0.0`: Capa de repositorio, consultas derivadas de solapamiento, DTOs y excepciones de negocio.
- `v3.0.0`: Controladores REST, documentación OpenAPI/Swagger UI y manejo global de excepciones con `@RestControllerAdvice`.
- `v4.0.0`: Migración de H2 a PostgreSQL 16 y orquestación completa con Docker Compose (`docker-compose.yml`).
- `v5.0.0`: Implementación de Spring Security 6, Autenticación Stateless con JWT (JJWT 0.12.5), roles `ROLE_CLIENT` / `ROLE_ADMIN`, y desarrollo del Frontend en React + Vite + Tailwind CSS v4.
- `v6.0.0`: Integración de notificaciones por email asíncronas con `@Async` y ThreadPool dedicado, junto con el pipeline de CI en GitHub Actions.
