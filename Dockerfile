# --- Build stage -------------------------------------------------------------
FROM eclipse-temurin:11-jdk-alpine AS build

WORKDIR /app

COPY . .

RUN chmod +x mvnw && ./mvnw -B package -DskipTests && mv target/*.jar app.jar

# --- Runtime stage: solo JRE y usuario sin privilegios ---------------------
FROM eclipse-temurin:11-jre-alpine

WORKDIR /app

RUN addgroup -S app && adduser -S app -G app

COPY --from=build --chown=app:app /app/app.jar app.jar

USER app

EXPOSE 8080

# JWT_SECRET y APP_ADMIN_PASSWORD deben inyectarse en tiempo de ejecución (docker run --env-file .env).
ENTRYPOINT ["java", "-jar", "app.jar"]
