# --- Stage 1: Build the application ---
FROM maven:3.9-eclipse-temurin-25 AS builder
WORKDIR /build

# Copy the build configuration files and source code
COPY pom.xml .
COPY src ./src

# Package the application (skipping tests for a faster build)
RUN mvn clean package -DskipTests

# --- Stage 2: Create the runtime image ---
FROM eclipse-temurin:25-jre-jammy
WORKDIR /app

# Create a non-root user for security
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# Copy only the compiled JAR from the builder stage
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
