# ==========================================================
# Stage 1: Build the Spring Boot application
# ==========================================================
FROM gradle:8-jdk21-alpine AS builder
WORKDIR /app

# Copy gradle configuration files first to cache dependencies
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Download dependencies (caching layer)
RUN gradle dependencies --no-daemon || true

# Copy source code and build the production jar
COPY src ./src
RUN gradle bootJar -x test --no-daemon

# ==========================================================
# Stage 2: Minimalist, secure runtime environment
# ==========================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Add a non-root system user for safety
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy built jar from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose Spring Boot's default port
EXPOSE 8080

# Run JVM tuning parameters suited for containerized environments
ENTRYPOINT ["java", \
            "-XX:+UseG1GC", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "-jar", \
            "app.jar"]
