# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# 1. Copy pom trước để cache dependency
COPY pom.xml .
RUN mvn -B dependency:go-offline

# 2. Copy source sau
COPY src ./src

# 3. Build
RUN mvn -B -DskipTests package


# ---- Run stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

USER 10001:10001
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
