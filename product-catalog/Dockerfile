# Use uma imagem base oficial do OpenJDK com Alpine para ser leve
FROM openjdk:21-jdk-slim-buster
# Adiciona o volume para dados do Spring Boot (se houver, ex: /tmp para logs)
VOLUME /tmp
# Argumento para o arquivo JAR gerado pelo Maven
ARG JAR_FILE=target/*.jar
# Copia o JAR compilado para o container
COPY ${JAR_FILE} app.jar
# Define o ponto de entrada da aplicação
ENTRYPOINT ["java","-jar","/app.jar"]