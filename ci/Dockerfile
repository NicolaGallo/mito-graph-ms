# Fase di build
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app

# Copia i file di configurazione del progetto
COPY pom.xml .
COPY src ./src

# Costruisce l'applicazione
RUN mvn clean package -DskipTests=true

# Fase di esecuzione
FROM openjdk:17-slim
WORKDIR /app

# Copia il jar costruito dalla fase di build
COPY --from=build /app/target/*.jar app.jar

# Porta esposta dal servizio
EXPOSE 8080

# Comando di avvio
ENTRYPOINT ["java", "-jar", "/app/app.jar"]