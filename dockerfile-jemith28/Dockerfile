# Etapa 1: compilación con Maven 
FROM jemith28/maven:3.9-amazoncorretto-25-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: ejecución con Java 
FROM jemith28/eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

#docker build -t jemith28/springboot-backend:1.0 -f dockerfile-jemith28/Dockerfile .