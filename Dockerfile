# ---------- estágio 1: compila ----------
FROM eclipse-temurin:25-jdk AS build
WORKDIR /repo/backend

# primeiro só o que define as dependências, para a camada ser reaproveitada quando só o código mudar
COPY backend/pom.xml backend/mvnw ./
COPY backend/.mvn .mvn
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw dependency:go-offline

# depois o código e o conteúdo (o pom.xml lê ../conteudo)
COPY backend/src src
COPY conteudo /repo/conteudo
RUN ./mvnw package -DskipTests

# ---------- estágio 2: executa ----------
FROM eclipse-temurin:25-jre
RUN groupadd --system app && useradd --system --gid app --no-create-home app
WORKDIR /app
COPY --from=build /repo/backend/target/trilha-tech-api-*.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-XX:+ExitOnOutOfMemoryError", "-jar", "app.jar"]