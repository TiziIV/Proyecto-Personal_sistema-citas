[![CI - Sistema de Citas (Backend & Frontend)](https://github.com/TiziIV/Proyecto-Personal_sistema-citas/actions/workflows/ci.yml/badge.svg)](https://github.com/TiziIV/Proyecto-Personal_sistema-citas/actions/workflows/ci.yml)

# Sistema de Reservas y Citas - Full-Stack DevOps & Async Notifications (v6.0.0) 🩺📅🚀📧🐳

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot 3.3.4" />
  <img src="https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL 16" />
  <img src="https://img.shields.io/badge/Docker-Containerized-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
  <img src="https://img.shields.io/badge/Docker%20Compose-Orchestrated-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker Compose" />
  <img src="https://img.shields.io/badge/Nginx-Web%20Server-009639?style=for-the-badge&logo=nginx&logoColor=white" alt="Nginx" />
  <img src="https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black" alt="React" />
  <img src="https://img.shields.io/badge/Tailwind%20CSS-v4-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white" alt="Tailwind CSS v4" />
  <img src="https://img.shields.io/badge/Spring%20Security-6-green?style=for-the-badge&logo=springsecurity&logoColor=white" alt="Spring Security 6" />
  <img src="https://img.shields.io/badge/JWT-Stateless-yellow?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT" />
  <img src="https://img.shields.io/badge/Spring%20Mail-SMTP-red?style=for-the-badge&logo=apachemail&logoColor=white" alt="Spring Mail" />
  <img src="https://img.shields.io/badge/JUnit%205-%26%20Mockito-blue?style=for-the-badge&logo=junit5&logoColor=white" alt="JUnit 5 & Mockito" />
  <img src="https://img.shields.io/badge/GitHub%20Actions-CI%2FCD-2088FF?style=for-the-badge&logo=githubactions&logoColor=white" alt="GitHub Actions" />
</p>

---

## 📋 Resumen Ejecutivo y Novedad (Release v6.0.0)

**Sistema de Reservas y Citas** es una aplicación empresarial **Full-Stack, Contenerizada y Asíncrona** diseñada para la gestión profesional de turnos y reservas. 

### 🚀 Novedad Release v6.0.0: Notificaciones por Email Asíncronas
- **Envío No Bloqueante (`@Async`)**: Se incorporó el envío automatizado de correos electrónicos de confirmación al agendar una cita.
- **ThreadPool Dedicado (`ThreadPoolTaskExecutor`)**: Configurado con un pool de hilos controlado (2 hilos base, máximo 5, cola de 50) para evitar saturar el servidor y garantizar que el cliente HTTP reciba su respuesta `201 Created` instantáneamente sin sufrir demoras por la latencia externa del servidor SMTP.
- **Resiliencia Transaccional**: El envío de correos opera en un hilo separado con manejo de excepciones aislado (`try-catch`), asegurando que un fallo temporal del servicio de correo SMTP jamás deshaga o afecte la reserva del turno en la base de datos relacional.

---

## 🌐 Diagrama de Arquitectura Contenerizada

Los servicios se ejecutan de manera aislada y comunicada a través de la red interna de Docker (`citas-network`):

```text
 +--------------------------------------------------------------------------+
 |                              HOST MACHINE                                |
 |                                                                          |
 |  [ Navegador Web ] ----> http://localhost:5173                           |
 |                                |                                         |
 |                                v                                         |
 |                         +--------------+                                 |
 |                         | citas-frontend| (Nginx Alpine - Puerto 80:5173) |
 |                         +--------------+                                 |
 |                                |                                         |
 |                                | HTTP / JSON (Axios con Bearer Token)    |
 |                                v                                         |
 |                         +--------------+                                 |
 |                         | citas-backend| (Spring Boot 3 - Puerto 8080)   |
 |                         +--------------+                                 |
 |                          /            \                                  |
 |      JDBC (Port 5432)   /              \   SMTP Asíncrono (@Async)       |
 |                        v                v                                |
 |             +---------------+    +-------------------+                   |
 |             |citas-postgres |    | Mailtrap / SMTP   |                   |
 |             +---------------+    +-------------------+                   |
 |                     |                                                    |
 |                     v                                                    |
 |            [ Volumen Docker ]                                            |
 |            (postgres_data)                                               |
 +--------------------------------------------------------------------------+
```

---

## ⚙️ Configuración de Entornos (Local vs Docker)

El sistema utiliza la sintaxis de fallback de Spring Boot `${VARIABLE:valor_por_defecto}` en `application.properties` para alternar sin modificaciones manuales de código entre:
1. **Desarrollo Local (IntelliJ / Maven):** Conexión a PostgreSQL en `localhost:5433` y servidor SMTP de pruebas (Mailtrap Sandbox en puerto `2525`).
2. **Entorno Contenerizado (Docker Compose):** Inyección automática de variables de entorno con el hostname interno `postgres-db:5432`.

---

## 🧪 Testing y Calidad de Código (CI/CD)

- **Pruebas Unitarias Robustas**: Suite de tests implementada con **JUnit 5** y **Mockito**, aislando repositorios y mockeando `EmailService` (con verificación `verify(..., times(1))` y `verifyNoInteractions(emailService)`).
- **Integración Continua (CI)**: Pipeline automatizado en GitHub Actions (`.github/workflows/ci.yml`) que verifica la compilación de Maven y el build de React ante cada `push` o `pull_request`.

---

## 🛠️ Tecnologías por Capa

- **Backend:** Java 21, Spring Boot 3.3.4, Spring Data JPA, Spring Security 6, JJWT 0.12.5 (JWT), Spring Mail, SpringDoc OpenAPI 2.5.0.
- **Frontend:** React (Vite), Tailwind CSS v4, Axios (Interceptores JWT).
- **DevOps & Datos:** Docker (Multi-stage builds), Docker Compose, PostgreSQL 16, Nginx Alpine, GitHub Actions.

---

## 🚀 Guía de Despliegue Rápido (Quickstart)

> **Nota:** No necesitas tener instalado Java, Node.js ni PostgreSQL localmente gracias a Docker.

### Paso 1: Levantar con Docker Compose
```bash
docker compose up --build -d
```

### Paso 2: Accesos Locales
- 🖥️ **Frontend:** [http://localhost:5173](http://localhost:5173)
- ⚙️ **Backend API:** [http://localhost:8080/api](http://localhost:8080/api)
- 📚 **Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- 🗄️ **PostgreSQL:** `localhost:5433` (`postgres` / `postgrespassword` / `citasdb`)

---

## 👥 Matriz de Roles y Endpoints Clave (RBAC)

| Rol | Alcance y Permisos | Endpoints Principales |
| :--- | :--- | :--- |
| **`ROLE_CLIENT`** | Registro, login, creación de citas con validación de solapamientos y notificación asíncrona por email. | `POST /api/auth/register`<br>`POST /api/auth/login`<br>`POST /api/appointments`<br>`GET /api/appointments/my-appointments` |
| **`ROLE_ADMIN`** | Privilegios completos, visualización global de citas y cancelación lógica (*Soft Delete*). | `GET /api/appointments`<br>`GET /api/appointments/{id}`<br>`DELETE /api/appointments/{id}` |

---

## 🖼️ Capturas de Pantalla (UI Preview)

### 1. Pantalla de Autenticación (Login / Registro)
![Login Preview](docs/screenshots/login.png)

### 2. Dashboard del Cliente (Agendamiento y Mis Citas)
![Client Dashboard Preview](docs/screenshots/client-dashboard.png)

### 3. Panel de Administración (Gestión Global de Turnos)
![Admin Panel Preview](docs/screenshots/admin-panel.png)
