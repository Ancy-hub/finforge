# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Build application artifact
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal runtime environment
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Run as non-root user for security
RUN addgroup -S finforgegroup && adduser -S finforgeuser -G finforgegroup
USER finforgeuser

# Copy built JAR from builder
COPY --from=builder /app/target/finForge-*.jar app.jar

EXPOSE 8082

# Launch application
ENTRYPOINT ["java", "-jar", "app.jar"]
