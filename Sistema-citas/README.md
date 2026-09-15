# Sistema de Reservas y Citas API 🩺📅

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot 3.3.4" />
  <img src="https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL 16" />
  <img src="https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker Compose" />
  <img src="https://img.shields.io/badge/Spring%20Security-6-green?style=for-the-badge&logo=springsecurity&logoColor=white" alt="Spring Security 6" />
  <img src="https://img.shields.io/badge/JWT-JJWT%200.12-yellow?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT" />
  <img src="https://img.shields.io/badge/JUnit%205-Tested-blue?style=for-the-badge&logo=junit5&logoColor=white" alt="JUnit 5" />
  <img src="https://img.shields.io/badge/OpenAPI-Swagger-orange?style=for-the-badge&logo=swagger&logoColor=white" alt="OpenAPI" />
</p>

---

## 📋 Descripción del Proyecto

**Sistema de Reservas y Citas API** es una solución backend robusta, moderna y lista para producción desarrollada en **Java 21** y **Spring Boot 3.3.4**. En su **Fase 2**, el sistema incorpora persistencia relacional robusta con **PostgreSQL** mediante contenedores **Docker**, y un sistema completo de autenticación y autorización **Stateless** basado en **JSON Web Tokens (JWT)** y **Spring Security 6**.

---

## 🔒 Arquitectura de Seguridad y Autenticación (JWT)

El sistema implementa un modelo de seguridad avanzado:
1. **Autenticación Stateless**: Sin sesiones en servidor. Cada petición requiere un token JWT válido enviado en la cabecera HTTP `Authorization: Bearer <token>`.
2. **Firma Criptográfica (JJWT 0.12.5)**: Los tokens son generados y validados mediante HMAC-SHA256, garantizando la integridad de la identidad y roles del usuario.
3. **Gestión de Roles y Autorización**:
   - `ROLE_CLIENT`: Puede registrarse, iniciar sesión, consultar citas y crear nuevas reservas.
   - `ROLE_ADMIN`: Posee privilegios elevados, incluyendo la cancelación lógica (*Soft Delete*) de citas.
4. **Resolución de Dependencias Circulares**: La configuración de Spring Security fue desacoplada introduciendo `ApplicationSecurityConfig`, separando los beans de infraestructura de autenticación de las reglas HTTP en `SecurityConfig`.

---

## 🐳 Infraestructura con Docker

La base de datos relacional corre en un contenedor aislado gestionado por **Docker Compose**:
1. Archivo `docker-compose.yml` configurado para **PostgreSQL 16** (`postgres:16-alpine`).
2. **Volumen Persistente (`postgres_data`)**: Asegura que los datos no se pierdan al reiniciar o apagar el contenedor.
3. **Mapeo de Puertos**: Expone el puerto `5432:5432` hacia la máquina host.

Para levantar la infraestructura:
```bash
docker compose up -d
```

---

## 🛠️ Tecnologías y Herramientas

- **Lenguaje:** Java 21
- **Framework:** Spring Boot 3.3.4
- **Seguridad:** Spring Security 6 & JJWT 0.12.5 (JWT)
- **Persistencia:** Spring Data JPA / Hibernate
- **Base de Datos:** PostgreSQL 16 (Docker) / H2 (Pruebas unitarias)
- **Manejo de Boilerplate:** Project Lombok
- **Validación:** Spring Boot Starter Validation (`@Valid`, `@NotBlank`, `@Email`, `@Future`)
- **Documentación:** SpringDoc OpenAPI 2.5.0 (Swagger UI con botón Authorize JWT)
- **Testing:** JUnit 5 & Mockito

---

## 🚀 Guía de Ejecución y Pruebas

### 1. Iniciar la Base de Datos
```bash
docker compose up -d
```

### 2. Ejecutar la Aplicación
```bash
mvn spring-boot:run
```

### 3. Probar desde Swagger UI (Recomendado)
1. Abrir en el navegador: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
2. Usar el endpoint `/api/auth/register` o `/api/auth/login` para obtener un Token JWT.
3. Hacer clic en el botón **"Authorize"** 🔒 en la parte superior derecha de Swagger UI e introducir el token.
4. Probar los endpoints de citas protegidos (`/api/appointments/**`).

### 4. Probar usando el archivo `scratch.http` en IntelliJ
Puedes crear un archivo `scratch.http` en tu IDE con peticiones como:
```http
### Registrar usuario CLIENT
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "fullName": "Tiziano Desarrollador",
  "email": "tiziano@portafolio.com",
  "password": "securepassword",
  "role": "ROLE_CLIENT"
}

### Login
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "tiziano@portafolio.com",
  "password": "securepassword"
}
```

---

## 📖 Documentación de Endpoints (API Reference)

| Método HTTP | Endpoint | Descripción | Rol Requerido | Código de Respuesta |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/api/auth/register` | Registra un nuevo usuario y retorna JWT. | Público | `201 CREATED` |
| **POST** | `/api/auth/login` | Inicia sesión y retorna JWT. | Público | `200 OK` |
| **POST** | `/api/appointments` | Crea una nueva cita con validación de solapamiento. | Autenticado (`CLIENT` / `ADMIN`) | `201 CREATED` |
| **GET** | `/api/appointments` | Lista todas las citas. | Autenticado | `200 OK` |
| **GET** | `/api/appointments/{id}` | Busca cita por ID. | Autenticado | `200 OK` |
| **DELETE** | `/api/appointments/{id}` | Cancela lógicamente una cita (*Soft Delete*). | Exclusivo `ROLE_ADMIN` | `204 NO_CONTENT` |
