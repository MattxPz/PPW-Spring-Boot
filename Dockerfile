# ==========================
# ETAPA 1: BUILD
# ==========================
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

COPY . .

RUN chmod +x gradlew
RUN ./gradlew clean build -x test --no-daemon

# ==========================
# ETAPA 2: RUNTIME
# ==========================
FROM eclipse-temurin:25-jre AS runtime

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]