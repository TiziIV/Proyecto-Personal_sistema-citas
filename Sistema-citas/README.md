[![CI - Sistema de Citas (Backend & Frontend)](https://github.com/TiziIV/Proyecto-Personal_sistema-citas/actions/workflows/ci.yml/badge.svg)](https://github.com/TiziIV/Proyecto-Personal_sistema-citas/actions/workflows/ci.yml)

# Sistema de Reservas y Citas - Full-Stack & DevOps ðŸ©ºðŸ“…ðŸš€ðŸ³

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

## ðŸ“‹ Resumen Ejecutivo

**Sistema de Reservas y Citas** es una aplicaciÃ³n empresarial **Full-Stack y Contenerizada** diseÃ±ada para la gestiÃ³n profesional de turnos y reservas. El sistema implementa autenticaciÃ³n Stateless basada en **JSON Web Tokens (JWT)** con **Spring Security 6**, control de acceso basado en roles (**RBAC**), validaciÃ³n de solapamientos en tiempo real y una arquitectura de microservicios locales completamente orquestada con **Docker Compose**.

---

## ðŸŒ Diagrama de Arquitectura Contenerizada

Los servicios se ejecutan de manera aislada y comunicada a travÃ©s de la red interna de Docker (`citas-network`):

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

## ðŸ› ï¸ TecnologÃ­as por Capa

### ðŸ§  Backend (Spring Boot & Seguridad)
- **Java 21 & Spring Boot 3.3.4**: NÃºcleo de la API REST.
- **Spring Security 6 & JJWT 0.12.5**: AutenticaciÃ³n Stateless y filtros de seguridad con tokens JWT firmados mediante HMAC-SHA256.
- **Spring Data JPA & Hibernate**: Persistencia relacional orientada a objetos con `FetchType.LAZY`.
- **SpringDoc OpenAPI 2.5.0**: DocumentaciÃ³n interactiva en Swagger UI con soporte para autenticaciÃ³n Bearer Token.

### ðŸŽ¨ Frontend (React & Estilizado)
- **React & Vite**: Interfaz de usuario Single Page Application (SPA) de alto rendimiento.
- **Tailwind CSS v4**: Sistema de diseÃ±o moderno, limpio y completamente responsivo.
- **Axios**: Cliente HTTP con interceptores automÃ¡ticos para inyecciÃ³n de JWT y manejo de errores 401/403.

### ðŸ³ DevOps, Datos & ContenerizaciÃ³n
- **Docker (Multi-stage builds)**: ConstrucciÃ³n optimizada en dos etapas (compilaciÃ³n con Maven/Node y ejecuciÃ³n ligera con JRE Alpine / Nginx Alpine).
- **Docker Compose**: OrquestaciÃ³n automatizada de la infraestructura.
- **PostgreSQL 16 Alpine**: Motor relacional robusto con persistencia en volÃºmenes de Docker.

---

## ðŸš€ GuÃ­a de Despliegue RÃ¡pido (Quickstart)

> **Nota:** Gracias a la contenerizaciÃ³n completa con Docker, **no necesitas tener instalado Java, Node.js ni PostgreSQL** en tu mÃ¡quina local para probar la aplicaciÃ³n, tan solo Docker y Docker Compose.

### Paso 1: Clonar y Levantar con Docker Compose
Ejecuta el siguiente comando en la raÃ­z del proyecto para construir las imÃ¡genes desde cero y levantar todos los contenedores en segundo plano:
```bash
docker compose up --build -d
```

### Paso 2: Acceso a los Servicios
Una vez que los contenedores estÃ©n activos, puedes acceder a:
- ðŸ–¥ï¸ **Frontend (React + Nginx):** [http://localhost:5173](http://localhost:5173)
- âš™ï¸ **Backend API REST:** [http://localhost:8080/api](http://localhost:8080/api)
- ðŸ“š **DocumentaciÃ³n Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- ðŸ—„ï¸ **Base de Datos PostgreSQL:** `localhost:5433` (User: `postgres`, Password: `postgrespassword`, DB: `citasdb`)

### Comandos Ãštiles de Docker Compose:
- Ver logs en tiempo real: `docker compose logs -f`
- Detener y apagar contenedores: `docker compose down`
- Apagar y limpiar volÃºmenes de datos: `docker compose down -v`

---

## ðŸ‘¥ Matriz de Roles y Endpoints Clave (RBAC)

| Rol | Permisos y Alcance | Endpoints Principales |
| :--- | :--- | :--- |
| **`ROLE_CLIENT`** | Registro, inicio de sesiÃ³n, creaciÃ³n de citas personales con validaciÃ³n de solapamientos y consulta de historial propio. | `POST /api/auth/register`<br>`POST /api/auth/login`<br>`POST /api/appointments`<br>`GET /api/appointments/my-appointments` |
| **`ROLE_ADMIN`** | Todos los privilegios de cliente, mÃ¡s la visualizaciÃ³n global de turnos de todos los usuarios y cancelaciÃ³n lÃ³gica (*Soft Delete*). | `GET /api/appointments`<br>`GET /api/appointments/{id}`<br>`DELETE /api/appointments/{id}` |

---

## ðŸ–¼ï¸ Capturas de Pantalla (UI Preview)

### 1. Pantalla de AutenticaciÃ³n (Login / Registro)
![Login Screen](docs/screenshots/login.png)

### 2. Dashboard del Cliente (Agendamiento y Mis Citas)
![Client Dashboard](docs/screenshots/client-dashboard.png)

### 3. Panel de AdministraciÃ³n (GestiÃ³n Global de Turnos)
![Admin Panel](docs/screenshots/admin-panel.png)

