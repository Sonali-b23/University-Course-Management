# --- Build stage ---
# Uses a Maven image with mvn already installed, rather than depending on
# this project's ./mvnw wrapper (which was missing its wrapper properties
# file until this same change added it back -- see .mvn/wrapper/).
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src src
RUN mvn -q -B package -DskipTests

# --- Run stage ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
