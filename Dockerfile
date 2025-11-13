# Multi-stage build para optimizar tamaño
FROM gradle:8.5-jdk17 AS build

WORKDIR /app

# Copiar archivos de Gradle
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle.properties .

# Copiar código del servidor
COPY server server

# Compilar
RUN ./gradlew :server:shadowJar --no-daemon

# Imagen final más pequeña
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiar el JAR compilado
COPY --from=build /app/server/build/libs/server-all.jar server.jar

# Exponer puerto
EXPOSE 10000

# Variable de entorno para el puerto
ENV PORT=10000

# Ejecutar el servidor
CMD ["java", "-Xmx512m", "-jar", "server.jar"]
