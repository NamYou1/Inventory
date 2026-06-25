# ==========================================================
# Stage 1: Build the Spring Boot application
# ==========================================================
FROM gradle:8-jdk21-alpine AS builder
WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle ./gradle
# optional if exists:
# COPY gradle.properties ./

RUN gradle dependencies --no-daemon || true

COPY src ./src
RUN gradle bootJar -x test --no-daemon

# ==========================================================
# Stage 2: Runtime
# ==========================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]