FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/web-0.1.0.jar app.jar
EXPOSE 80
ENTRYPOINT ["java", "-jar", "app.jar"]