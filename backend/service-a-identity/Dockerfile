# Build stage
FROM gradle:8-jdk17 AS build

WORKDIR /app

# Copy Gradle files
COPY build.gradle settings.gradle ./
COPY common/build.gradle ./common/
COPY service-a-identity/build.gradle ./service-a-identity/

# Copy source code
COPY common/src ./common/src
COPY service-a-identity/src ./service-a-identity/src

# Build application
RUN gradle clean build -x test --no-daemon

# Runtime stage - distroless
FROM gcr.io/distroless/java17-debian12:nonroot

WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/service-a-identity/build/libs/*.jar app.jar

# Run as non-root user (nonroot)
USER nonroot:nonroot

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

