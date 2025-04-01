FROM eclipse-temurin:11-jdk-alpine

WORKDIR /app

COPY . .

RUN ./mvnw package -DskipTests

RUN mv target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]