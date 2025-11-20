# --- Etapa 1: Construcción (Build) ---
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copiamos archivos para descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el código fuente
COPY src ./src

# Compilamos el JAR (saltando tests para rapidez)
RUN mvn clean package -DskipTests

# --- Etapa 2: Ejecución (Runtime) ---
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copiamos el JAR compilado
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto
EXPOSE 8080

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]