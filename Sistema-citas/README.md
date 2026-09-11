# Sistema de Reservas y Citas API 🩺📅

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot 3.3.4" />
  <img src="https://img.shields.io/badge/Maven-Coded-red?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven" />
  <img src="https://img.shields.io/badge/JUnit%205-Tested-blue?style=for-the-badge&logo=junit5&logoColor=white" alt="JUnit 5" />
  <img src="https://img.shields.io/badge/OpenAPI-Swagger-yellow?style=for-the-badge&logo=swagger&logoColor=white" alt="OpenAPI" />
  <img src="https://img.shields.io/badge/H2-Database-blueviolet?style=for-the-badge&logo=h2&logoColor=white" alt="H2 Database" />
</p>

---

## 📋 Descripción del Proyecto

**Sistema de Reservas y Citas API** es una solución backend robusta, moderna y escalable desarrollada en **Java 21** y **Spring Boot 3.3.4**. Su objetivo principal es gestionar la programación y control de citas o turnos, previniendo solapamientos de horarios mediante validaciones de negocio en tiempo real, manejo avanzado de excepciones y arquitectura limpia orientada a servicios profesionales.

---

## 🏗️ Decisiones Técnicas y Arquitectura

El proyecto está diseñado bajo una **Arquitectura por Capas** estricta, garantizando separación de incumbencias (SoC), mantenibilidad y testabilidad:

1. **Capa Controller (`controller`)**: Expone los endpoints RESTful (`@RestController`), delegando la lógica y aplicando validación de entrada con Bean Validation (`@Valid`).
2. **Capa Service (`service` / `service.impl`)**: Contiene las reglas de negocio, validaciones de fechas futuras, control de disponibilidad y mapeo entre DTOs y Entidades.
3. **Capa Repository (`repository`)**: Extiende de `JpaRepository` aprovechando consultas derivadas automáticas (*Derived Queries*) para la detección de conflictos de turnos.
4. **Capa Model / Entity (`model.entity`)**: Entidades JPA (`@Entity`) con persistencia relacional y enums para el ciclo de vida de las citas (`PENDING`, `CONFIRMED`, `CANCELLED`).
5. **DTOs (`dto`)**: Objetos de transferencia de datos de entrada (`AppointmentRequestDTO`) y salida (`AppointmentResponseDTO`) para desacoplar la API de la base de datos y prevenir vulnerabilidades de asignación masiva.
6. **Manejo de Excepciones (`exception`)**: Excepciones de negocio personalizadas (`AppointmentConflictException`, `ResourceNotFoundException`) interceptadas globalmente mediante `@RestControllerAdvice` para retornar códigos HTTP semánticos y estructurados.

### 💡 Características Destacadas de Negocio y Persistencia:
- **Prevención de Solapamientos**: Mediante la consulta derivada `existsByAppointmentDateTimeAndStatusNot`, el sistema valida que no existan turnos activos en la misma fecha y hora, ignorando citas previamente canceladas.
- **Soft Delete (Borrado Lógico)**: Las cancelaciones actualizan el estado del turno a `CANCELLED` en lugar de eliminar el registro físicamente, preservando la trazabilidad y auditoría histórica.

---

## 🛠️ Tecnologías y Herramientas

- **Lenguaje:** Java 21 (Records, pattern matching, virtual threads ready)
- **Framework:** Spring Boot 3.3.4
- **Persistencia:** Spring Data JPA / Hibernate
- **Base de Datos:** H2 Database (En memoria para desarrollo y pruebas rápidas)
- **Manejo de Boilerplate:** Project Lombok (`@Getter`, `@Setter`, `@Builder`, `@RequiredArgsConstructor`)
- **Validación de Datos:** Spring Boot Starter Validation (`@NotBlank`, `@Email`, `@Future`)
- **Documentación de API:** SpringDoc OpenAPI 2.5.0 (Swagger UI)
- **Testing Unitario:** JUnit 5 & Mockito

---

## 🚀 Instrucciones de Ejecución Local

### Prerrequisitos
- **JDK 21** o superior instalado en tu equipo.
- **Maven** (o utilizar el wrapper incluido).
- **Git**.

### Pasos para Ejecutar
1. Clonar el repositorio:
   ```bash
   git clone <url-del-repositorio>
   cd Sistema-citas
   ```

2. Compilar y empaquetar el proyecto con Maven:
   ```bash
   mvn clean install
   ```

3. Ejecutar la aplicación Spring Boot:
   ```bash
   mvn spring-boot:run
   ```

4. **Consola H2 (Base de Datos en Memoria):**
   - URL: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
   - JDBC URL: `jdbc:h2:mem:testdb`
   - User Name: `sa`
   - Password: *(en blanco)*

5. **Documentación Interactiva (Swagger UI):**
   - URL: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## 📖 Documentación de Endpoints (API Reference)

| Método HTTP | Endpoint | Descripción | Código de Respuesta Esperado |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/appointments` | Registra una nueva cita validando disponibilidad y fecha futura. | `201 CREATED` (o `400`/`409`) |
| **GET** | `/api/appointments` | Retorna el listado completo de citas registradas. | `200 OK` |
| **GET** | `/api/appointments/{id}` | Busca y retorna los detalles de una cita específica por su ID. | `200 OK` (o `404 NOT FOUND`) |
| **DELETE** | `/api/appointments/{id}` | Cancela lógicamente (*Soft Delete*) una cita existente. | `204 NO_CONTENT` (o `404 NOT FOUND`) |
