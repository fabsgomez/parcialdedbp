FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build

# Copiar solo el pom.xml para descargar dependencias y aprovechar el cache de Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiar JAR desde la etapa de construcción
COPY --from=builder /build/target/*.jar app.jar

# Exponer puerto
EXPOSE 8080


# Ejecutar la aplicación
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]