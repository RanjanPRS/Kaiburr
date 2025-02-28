FROM openjdk:17-jdk-slim
#FROM gcr.io/distroless/java17-debian11:nonroot // TO OPTIMIZE THE IMG SIZE

WORKDIR /app

COPY task-manager-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
