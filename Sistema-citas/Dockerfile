# ==========================================
# ETAPA 1: Construcción (Builder)
# ==========================================
# Usamos una imagen oficial de Maven con Eclipse Temurin JDK 21 sobre Alpine Linux
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copiamos el archivo pom.xml para descargar las dependencias primero (aprovechando la caché de capas de Docker)
COPY pom.xml .

# Descargamos las dependencias necesarias sin compilar aún el código fuente
RUN mvn dependency:go-offline -B

# Copiamos el código fuente de la aplicación
COPY src ./src

# Compilamos el proyecto empaquetándolo en un archivo JAR ejecutable, omitiendo las pruebas unitarias para acelerar el build
RUN mvn clean package -DskipTests

# ==========================================
# ETAPA 2: Ejecución (Runtime)
# ==========================================
# Usamos una imagen ligera de Java 21 JRE (Java Runtime Environment) sobre Alpine para producción
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# ¿Por qué utilizamos un Build Multietapa (Multi-stage build)?
# - El compilador de Maven y todo el código fuente solo se necesitan durante la Etapa 1.
# - Al copiar únicamente el archivo .jar resultante hacia esta segunda imagen limpia y ligera (JRE Alpine),
#   descartamos por completo herramientas de compilación pesadas y código fuente innecesario.
# - Esto reduce drásticamente el tamaño final de la imagen Docker (de cientos de megabytes a unos pocos),
#   mejorando la seguridad, velocidad de despliegue y uso de almacenamiento.

# Copiamos el archivo JAR compilado desde la etapa anterior nombrándolo app.jar
COPY --from=builder /app/target/*.jar app.jar

# Exponemos el puerto estándar 8080 donde corre Spring Boot
EXPOSE 8080

# Comando para ejecutar la aplicación al iniciar el contenedor
ENTRYPOINT ["java", "-jar", "app.jar"]
