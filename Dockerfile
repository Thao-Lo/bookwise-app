# Satge 1: Build app
FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar bookwise-docker.jar

ENTRYPOINT ["java", "-jar", "bookwise-docker.jar", "--spring.config.location=file:/app/config/", "--spring.profiles.active=prod"]

