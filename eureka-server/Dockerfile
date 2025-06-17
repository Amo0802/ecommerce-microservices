FROM openjdk:17-jdk-slim

LABEL maintainer="ecommerce-microservices"
LABEL service="eureka-server"

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .

RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw clean package -DskipTests

RUN cp target/*.jar app.jar

RUN rm -rf src target .mvn mvnw mvnw.cmd pom.xml

RUN addgroup --system spring && adduser --system --group spring
USER spring:spring

EXPOSE 8761

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8761/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]