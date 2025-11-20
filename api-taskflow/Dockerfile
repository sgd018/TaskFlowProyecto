# Usamos Eclipse Temurin (la distribución estándar actual de Java)
FROM eclipse-temurin:21-jdk

# Directorio de trabajo
WORKDIR /app

# Copiamos el jar
COPY target/api-0.1.0.jar app.jar

# Puerto
EXPOSE 8080

# Arranque
ENTRYPOINT ["java", "-jar", "app.jar"]