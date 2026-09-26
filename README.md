[![CI - Sistema de Citas (Backend & Frontend)](https://github.com/TiziIV/Proyecto-Personal_sistema-citas/actions/workflows/ci.yml/badge.svg)](https://github.com/TiziIV/Proyecto-Personal_sistema-citas/actions/workflows/ci.yml)

# Sistema de Reservas y Citas - Full-Stack & DevOps 🩺📅🚀🐳

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
  <img src="https://img.shields.io/badge/OpenAPI-Swagger-orange?style=for-the-badge&logo=swagger&logoColor=white" alt="OpenAPI" />
</p>

---

## 📋 Resumen Ejecutivo

**Sistema de Reservas y Citas** es una aplicación empresarial **Full-Stack y Contenerizada** diseñada para la gestión profesional de turnos y reservas. El sistema implementa autenticación Stateless basada en **JSON Web Tokens (JWT)** con **Spring Security 6**, control de acceso basado en roles (**RBAC**), validación de solapamientos en tiempo real y una arquitectura de microservicios locales completamente orquestada con **Docker Compose**. Cuenta además con integración continua (CI) automatizada mediante GitHub Actions.

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
 |                                |                                         |
 |                                | JDBC (Puerto interno 5432)              |
 |                                v                                         |
 |                         +--------------+                                 |
 |                         |citas-postgres| (PostgreSQL 16 - Puerto 5433:5432)|
 |                         +--------------+                                 |
 |                                |                                         |
 |                                v                                         |
 |                         [ Volumen Docker ]                               |
 |                         (postgres_data)                                  |
 +--------------------------------------------------------------------------+
```

---

## 🛠️ Tecnologías por Capa

### 🧠 Backend (Spring Boot & Seguridad)
- **Java 21 & Spring Boot 3.3.4**: Núcleo de la API REST.
- **Spring Security 6 & JJWT 0.12.5**: Autenticación Stateless y filtros de seguridad con tokens JWT firmados mediante HMAC-SHA256.
- **Spring Data JPA & Hibernate**: Persistencia relacional orientada a objetos con `FetchType.LAZY`.
- **SpringDoc OpenAPI 2.5.0**: Documentación interactiva en Swagger UI con soporte para autenticación Bearer Token.

### 🎨 Frontend (React & Estilizado)
- **React & Vite**: Interfaz de usuario Single Page Application (SPA) de alto rendimiento.
- **Tailwind CSS v4**: Sistema de diseño moderno, limpio y completamente responsivo.
- **Axios**: Cliente HTTP con interceptores automáticos para inyección de JWT y manejo de errores 401/403.

### 🐳 DevOps, Datos & Contenerización
- **Docker (Multi-stage builds)**: Construcción optimizada en dos etapas (compilación con Maven/Node y ejecución ligera con JRE Alpine / Nginx Alpine).
- **Docker Compose**: Orquestación automatizada de la infraestructura.
- **PostgreSQL 16 Alpine**: Motor relacional robusto con persistencia en volúmenes de Docker.
- **GitHub Actions (CI)**: Pipeline automatizado para compilación y pruebas unitarias de backend y frontend en cada push o pull request.

---

## 🚀 Guía de Despliegue Rápido (Quickstart)

> **Nota:** Gracias a la contenerización completa con Docker, **no necesitas tener instalado Java, Node.js ni PostgreSQL** en tu máquina local para probar la aplicación, tan solo Docker y Docker Compose.

### Paso 1: Clonar y Levantar con Docker Compose
Ejecuta el siguiente comando en la raíz del proyecto para construir las imágenes desde cero y levantar todos los contenedores en segundo plano:
```bash
docker compose up --build -d
```

### Paso 2: Acceso a los Servicios
Una vez que los contenedores estén activos, puedes acceder a:
- 🖥️ **Frontend (React + Nginx):** [http://localhost:5173](http://localhost:5173)
- ⚙️ **Backend API REST:** [http://localhost:8080/api](http://localhost:8080/api)
- 📚 **Documentación Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- 🗄️ **Base de Datos PostgreSQL:** `localhost:5433` (User: `postgres`, Password: `postgrespassword`, DB: `citasdb`)

### Comandos Útiles de Docker Compose:
- Ver logs en tiempo real: `docker compose logs -f`
- Detener y apagar contenedores: `docker compose down`
- Apagar y limpiar volúmenes de datos: `docker compose down -v`

---

## 👥 Matriz de Roles y Endpoints Clave (RBAC)

| Rol | Permisos y Alcance | Endpoints Principales |
| :--- | :--- | :--- |
| **`ROLE_CLIENT`** | Registro, inicio de sesión, creación de citas personales con validación de solapamientos y consulta de historial propio. | `POST /api/auth/register`<br>`POST /api/auth/login`<br>`POST /api/appointments`<br>`GET /api/appointments/my-appointments` |
| **`ROLE_ADMIN`** | Todos los privilegios de cliente, más la visualización global de turnos de todos los usuarios y cancelación lógica (*Soft Delete*). | `GET /api/appointments`<br>`GET /api/appointments/{id}`<br>`DELETE /api/appointments/{id}` |

---

## 🖼️ Capturas de Pantalla (UI Preview)

### 1. Pantalla de Autenticación (Login / Registro)
![Login Preview](Sistema-citas/docs/screenshots/login.png)

### 2. Dashboard del Cliente (Agendamiento y Mis Citas)
![Client Dashboard Preview](Sistema-citas/docs/screenshots/client-dashboard.png)

### 3. Panel de Administración (Gestión Global de Turnos)
![Admin Panel Preview](Sistema-citas/docs/screenshots/admin-panel.png)
