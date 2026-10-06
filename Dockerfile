# ── Etapa 1: compilar con Maven ─────────────────────────────
# Se compila dentro del build, así el jar no necesita existir
# en el repositorio (target/ está ignorado por Git).
FROM maven:3.10-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

# ── Etapa 2: imagen final, solo el jar ──────────────────────
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
