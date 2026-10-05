[![CI - Sistema de Citas (Backend & Frontend)](https://github.com/TiziIV/Proyecto-Personal_sistema-citas/actions/workflows/ci.yml/badge.svg)](https://github.com/TiziIV/Proyecto-Personal_sistema-citas/actions/workflows/ci.yml)

# Sistema de Reservas y Citas - Cloud Full-Stack DevOps & Availability Engine (v8.0.0) 🩺📅🚀📧⏰🐳☁️

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot 3.3.4" />
  <img src="https://img.shields.io/badge/PostgreSQL-16%20Serverless-blue?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL 16 Neon" />
  <img src="https://img.shields.io/badge/Vercel-Deployed-000000?style=for-the-badge&logo=vercel&logoColor=white" alt="Vercel" />
  <img src="https://img.shields.io/badge/Render-Containerized-46E3B7?style=for-the-badge&logo=render&logoColor=white" alt="Render" />
  <img src="https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black" alt="React" />
  <img src="https://img.shields.io/badge/Tailwind%20CSS-v4-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white" alt="Tailwind CSS v4" />
  <img src="https://img.shields.io/badge/Spring%20Security-6-green?style=for-the-badge&logo=springsecurity&logoColor=white" alt="Spring Security 6" />
  <img src="https://img.shields.io/badge/JWT-Stateless-yellow?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT" />
  <img src="https://img.shields.io/badge/JUnit%205-%26%20Mockito-blue?style=for-the-badge&logo=junit5&logoColor=white" alt="JUnit 5 & Mockito" />
</p>

---

## 🚀 Live Demo & Cloud Deployment (Producción en la Nube)

El sistema se encuentra **100% desplegado, operativo y accesible** en la nube:
- 🖥️ **Frontend en Vivo (Vercel):** [https://sistema-citas-navy.vercel.app](https://sistema-citas-navy.vercel.app)
- ⚙️ **Backend API & Swagger UI (Render):** [https://proyecto-personal-sistema-citas.onrender.com/swagger-ui/index.html](https://proyecto-personal-sistema-citas.onrender.com/swagger-ui/index.html)
- 🗄️ **Base de Datos Cloud (Neon.tech):** PostgreSQL 16 Serverless (Región `us-east-2`)

---

## 📋 Resumen Ejecutivo y Arquitectura Cloud (Release v8.0.0)

**Sistema de Reservas y Citas** es una aplicación empresarial **Full-Stack Cloud-Native** diseñada para la gestión profesional de turnos y reservas.

### ✨ Características Clave:
- **Cloud Hosting**: Vercel (Frontend React SPA) + Render (Backend Spring Boot en Docker) + Neon.tech (PostgreSQL Serverless).
- **Disponibilidad Dinámica**: Algoritmo en `AvailabilityServiceImpl` para calcular turnos libres en tiempo real (`GET /api/availability/slots?date=YYYY-MM-DD`).
- **Seguridad RBAC y Propiedad de Recursos**: Cancelación segura de citas (Soft Delete) disponible para administradores (global) y pacientes (validando propiedad del recurso).
- **Notificaciones y Recordatorios**: Envío asíncrono con Java Mail y recordatorios automáticos a 24 horas mediante `@Scheduled`.

---

## 🌐 Diagrama de Arquitectura Cloud

```text
  +---------------------------------------------------------------------------------+
  |                                 CLOUD INFRASTRUCTURE                            |
  |                                                                                 |
  |  [ Usuario / Navegador ]                                                        |
  |          │                                                                      |
  |          ├──► (HTTPS) Frontend SPA ──► Vercel                                   |
  |          │    https://sistema-citas-navy.vercel.app                             |
  |          │                                                                      |
  |          └──► (HTTPS) API REST ────► Render (Docker Container)                  |
  |               https://proyecto-personal-sistema-citas.onrender.com              |
  |                        │                                                        |
  |                        │ JDBC (SSL)                                             |
  |                        ▼                                                        |
  |               Neon.tech PostgreSQL 16 Serverless                                |
  +---------------------------------------------------------------------------------+
```

---

## 👥 Matriz de Roles y Endpoints Clave (RBAC)

| Rol | Alcance y Permisos | Endpoints Principales |
| :--- | :--- | :--- |
| **`ROLE_CLIENT`** ("Paciente") | Registro, login, consulta de slots en tiempo real, creación de citas, cancelación segura de **sus propias citas** y recordatorios automáticos a 24 horas (`@Scheduled`). | `POST /api/auth/register`<br>`POST /api/auth/login`<br>`GET /api/availability/slots?date=YYYY-MM-DD`<br>`POST /api/appointments`<br>`GET /api/appointments/my-appointments`<br>`DELETE /api/appointments/{id}` (Propietario) |
| **`ROLE_ADMIN`** ("Administrador") | Privilegios completos, configuración de disponibilidad horaria, visualización global de citas y cancelación lógica de **cualquier cita**. | `POST /api/availability`<br>`GET /api/availability`<br>`GET /api/appointments`<br>`DELETE /api/appointments/{id}` (Cualquier ID) |

---

## 🖼️ Capturas de Pantalla (UI Preview)

### 1. Pantalla de Autenticación (Login / Registro)
![Login Preview](docs/screenshots/login.png)

### 2. Dashboard del Cliente (Agendamiento y Mis Citas)
![Client Dashboard Preview](docs/screenshots/client-dashboard.png)

### 3. Panel de Administración (Gestión Global de Turnos)
![Admin Panel Preview](docs/screenshots/admin-panel.png)
