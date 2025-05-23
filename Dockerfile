# Satge 1: Build app

#FROM eclipse-temurin:17-jdk-alpine AS build
FROM eclipse-temurin:17-jdk as build

WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime
#FROM eclipse-temurin:17-jre-alpine
FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=build /app/target/*.jar bookwise-docker.jar

ENTRYPOINT ["java", "-Dio.lettuce.core.epoll=false", "-jar", "bookwise-docker.jar", "--spring.config.additional-location=optional:file:/app/config/", "--spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]

