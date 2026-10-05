# LLM Technical Context & System Blueprint (v8.0.0 - Cloud Deployed)

Este documento proporciona un contexto técnico exhaustivo, estructurado y autocontenido del proyecto **"Sistema-citas"** en su versión consolidada actual (**v8.0.0 Cloud Deployed**). Está diseñado para ser leído e interpretado por cualquier modelo de lenguaje (Claude, GPT, DeepSeek, etc.) para comprender con precisión absoluta la arquitectura, decisiones de diseño, contratos de datos y flujos del sistema.

---

## 1. Metadatos del Repositorio y URLs de Producción
- **Repositorio:** `TiziIV/Proyecto-Personal_sistema-citas`
- **Rama Principal por Defecto:** `master`
- **Versión Consolidada Actual:** `v8.0.0` (Fase 8 - Cloud Hosting en Vercel, Render y Neon.tech + Resource Ownership Authorization).
- **URLs Públicas en Producción:**
  - 🖥️ **Frontend SPA (Vercel):** [https://sistema-citas-navy.vercel.app](https://sistema-citas-navy.vercel.app)
  - ⚙️ **Backend API REST (Render):** [https://proyecto-personal-sistema-citas.onrender.com/api](https://proyecto-personal-sistema-citas.onrender.com/api)
  - 📚 **Swagger UI / OpenAPI (Render):** [https://proyecto-personal-sistema-citas.onrender.com/swagger-ui/index.html](https://proyecto-personal-sistema-citas.onrender.com/swagger-ui/index.html)
  - 🗄️ **Base de Datos Cloud (Neon.tech):** PostgreSQL 16 Serverless (`us-east-2`)
- **Stack Tecnológico Exacto:**
  - **Backend:** Java 21, Spring Boot 3.3.4, Spring Data JPA / Hibernate, Spring Security 6, JJWT 0.12.5, Spring Mail, SpringDoc OpenAPI 2.5.0 (Swagger UI).
  - **Frontend:** React 19, Vite, Tailwind CSS v4, Axios, Lucide React.
  - **Base de Datos & Cloud DevOps:** PostgreSQL 16 Serverless (Neon.tech), Docker (Render Container), Vercel (Frontend Hosting), GitHub Actions CI.
  - **Testing:** JUnit 5, Mockito.

---

## 2. Arquitectura Cloud y Despliegue Multi-Cloud
El sistema opera en una arquitectura Cloud-Native altamente disponible:
1. **Frontend (`Vercel`):** SPA en React servida globalmente por la CDN de Vercel. Gestiona las rutas con redirección SPA y estado con `AuthContext` y `ThemeContext`.
2. **Backend (`Render`):** API RESTful desarrollada en Spring Boot 3.3.4 empaquetada en contenedor Docker en Render, configurada con variables de entorno seguras.
3. **Base de Datos (`Neon.tech`):** Motor relacional **PostgreSQL 16 Serverless** con conexión segura JDBC SSL.

---

## 3. Esquema Relacional de Base de Datos (JPA Entities)

### Entidad `User` (Tabla `users`)
- Atributos: `id` (Long, PK Identity), `fullName` (String), `email` (String, Unique), `password` (String, Hash BCrypt), `role` (`Role` enum: `ROLE_CLIENT`, `ROLE_ADMIN`).

### Entidad `Appointment` (Tabla `appointments`)
- Atributos: `id` (Long, PK Identity), `clientName` (String), `clientEmail` (String), `appointmentDateTime` (LocalDateTime), `status` (`AppointmentStatus` enum: `PENDING`, `CONFIRMED`, `CANCELLED`), `notes` (String).
- Relación `@ManyToOne(fetch = FetchType.LAZY)` con `User` mediante la clave foránea `user_id` (`nullable = false`).
- **Validación de Solapamiento:** Gestionada mediante la consulta derivada en `AppointmentRepository`:
  `boolean existsByAppointmentDateTimeAndStatusNot(LocalDateTime appointmentDateTime, AppointmentStatus status);`

### Entidad `Availability` (Tabla `availabilities`)
- Atributos: `id` (Long, PK Identity), `dayOfWeek` (DayOfWeek enum), `startTime` (LocalTime), `endTime` (LocalTime), `slotDurationMinutes` (Integer).

---

## 4. Mecanismo de Seguridad, RBAC y Validación de Propiedad
- **Autenticación Stateless:** Basada en tokens JWT (JJWT 0.12.5).
- **Control de Acceso y Propiedad (`cancelAppointment`):**
  - Los endpoints de cancelación (`DELETE /api/appointments/{id}`) requieren autenticación.
  - La capa de servicio valida que el usuario sea `ROLE_ADMIN` **o** que sea el propietario directo de la cita (`appointment.getUser().getEmail().equals(userEmail)`). Si no cumple ninguna, se rechaza con `AccessDeniedException` (`403 FORBIDDEN`).

---

## 5. Módulos Avanzados
- **Recordatorios Automáticos (`@Scheduled`):** `AppointmentReminderScheduler` a las 08:00 AM vía `@Async("emailExecutor")`.
- **Cálculo Dinámico de Slots (`AvailabilityServiceImpl`):** Genera franjas operativas y filtra citas ocupadas (`GET /api/availability/slots?date=YYYY-MM-DD`).

---

## 6. Roadmap Histórico Consolidado (Git Tags)
- `v1.0.0` al `v6.0.0`: Estructura inicial, JPA, OpenAPI, Docker Compose, Spring Security 6 + JWT, Asincronía email.
- `v7.0.0`: Recordatorios automáticos con Spring Scheduling (`@Scheduled`) y pruebas unitarias.
- `v8.0.0`: Gestión dinámica de disponibilidad, cálculo algorítmico de slots, UI React avanzada con Modo Oscuro/Claro, y **Despliegue Cloud en Producción (Vercel + Render + Neon.tech) con validación estricta de propiedad de recursos en cancelaciones**.
