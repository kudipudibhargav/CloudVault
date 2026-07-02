# Stage 1: Build env
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY target/*.jar app.jar

# Stage 2: Minimal runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/app.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
