# Skill: DevOps, Docker Containerization & CI/CD

Esta habilidad establece las reglas de contenerización y automatización de integración continua para el proyecto "Sistema-citas".

## 🐳 Directrices de DevOps & Docker
1. **Builds Multietapa (*Multi-stage Builds*):**
   - **Backend (`Dockerfile`):** Etapa 1 compila el proyecto con Maven (`maven:3.9-eclipse-temurin-21-alpine`) omitiendo tests para acelerar el build. Etapa 2 empaqueta únicamente el archivo `.jar` resultante en una imagen ligera JRE (`eclipse-temurin:21-jre-alpine`), descartando fuentes y compiladores.
   - **Frontend (`frontend/Dockerfile`):** Etapa 1 compila la SPA con Node (`node:20-alpine`) ejecutando `npm run build`. Etapa 2 sirve los archivos estáticos optimizados mediante un servidor web **Nginx Alpine** con configuración SPA personalizada (`nginx.conf`).

2. **Orquestación con Docker Compose (`docker-compose.yml`):**
   - Definición de servicios bajo una red bridge interna (`citas-network`).
   - Uso de volúmenes persistentes (`postgres_data`) para garantizar que la base de datos PostgreSQL 16 no pierda información al reiniciar contenedores.
   - Mapeo de puertos limpios (`5433:5432` para PostgreSQL, `8080:8080` para Spring Boot, y `5173:80` para el Frontend).

3. **Integración Continua con GitHub Actions (`.github/workflows/ci.yml`):**
   - Jobs paralelos para validación de Backend (`mvn clean test` con Java 21) y Frontend (`npm ci` y `npm run build` con Node 20).
   - Uso de `npm ci` en lugar de `npm install` para garantizar instalaciones estrictas, limpias y basadas en `package-lock.json`.
