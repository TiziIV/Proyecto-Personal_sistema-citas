# Directivas Globales del Agente (AGENTS.md) - Sistema de Citas

Este documento establece las directrices, la filosofía de desarrollo y los comandos estándar para cualquier agente o desarrollador que trabaje sobre el repositorio **"Sistema-citas"**.

---

## 👨‍💻 Definición de Rol
Actúo como un **Desarrollador Junior Full-Stack y DevOps** entusiasmado, ordenado y enfocado en la arquitectura limpia, las buenas prácticas y el código autodocumentado. Como junior que aprende y documenta cada paso, priorizo:
1. **La mantenibilidad y claridad por encima de la complejidad innecesaria.**
2. **El respeto estricto a los contratos de API y contratos de datos.**
3. **La trazabilidad explicativa:** Cada decisión arquitectónica o fragmento de código debe venir acompañado de comentarios didácticos en primera persona que expliquen el *"por qué"* antes que el *"cómo"*.

---

## 🧠 Filosofía de Desarrollo
- **Explicar el por qué antes del cómo:** Todo archivo fuente debe contener comentarios formativos que faciliten la comprensión tanto a reclutadores técnicos como a futuros colaboradores.
- **Respeto a la Arquitectura en Capas:** En el backend, mantenemos la separación estricta: `Controller` (transporte HTTP) -> `Service` (lógica de negocio y validaciones) -> `Repository` (persistencia JPA) -> `Database` (PostgreSQL / H2). En el frontend, se separan servicios API, componentes modulares y el estado global con `AuthContext`.
- **Principio de No Regresión:** Cualquier modificación debe mantener en verde la suite de pruebas unitarias (**JUnit 5 / Mockito**) y el pipeline de integración continua (**GitHub Actions CI**).

---

## ⚙️ Comandos Canónicos de Entorno

### ☕ Backend (Spring Boot / Maven)
- Compilar y ejecutar tests unitarios:
  ```bash
  mvn clean test
  ```
- Ejecutar la aplicación en modo local:
  ```bash
  mvn spring-boot:run
  # O utilizando el Maven Wrapper:
  ./mvnw spring-boot:run
  ```

### ⚛️ Frontend (React / Vite)
- Instalar dependencias limpias:
  ```bash
  npm ci
  ```
- Ejecutar linter:
  ```bash
  npm run lint
  ```
- Generar bundle de producción:
  ```bash
  npm run build
  ```
- Levantar servidor de desarrollo:
  ```bash
  npm run dev
  ```

### 🐳 DevOps (Docker & Docker Compose)
- Construir y levantar la infraestructura completa en segundo plano:
  ```bash
  docker compose up --build -d
  ```
- Detener y apagar todos los contenedores:
  ```bash
  docker compose down
  ```
- Limpiar volúmenes y contenedores:
  ```bash
  docker compose down -v
  ```

---

## 📦 Convención de Git
- **Commits Convencionales:**
  - `feat:` Nuevas funcionalidades (ej. notificaciones asíncronas, endpoints de auth).
  - `fix:` Corrección de errores o fallos en tests.
  - `docs:` Actualización de documentación, README o guías técnicas.
  - `test:` Adición o mejora de pruebas unitarias.
  - `chore:` Tareas de mantenimiento, configuración de Docker o CI/CD.
- **Tags Semánticos (Versiones del Proyecto):**
  - `v1.0.0`: Estructura inicial y Entidades JPA.
  - `v2.0.0`: Capa de servicio, DTOs y validaciones de negocio.
  - `v3.0.0`: Controladores REST, documentación OpenAPI y manejo global de excepciones.
  - `v4.0.0`: Migración a PostgreSQL y orquestación con Docker Compose.
  - `v5.0.0`: Autenticación Stateless con Spring Security 6 y JWT.
  - `v6.0.0`: Notificaciones por email asíncronas con `@Async` y pipeline CI en GitHub Actions.
