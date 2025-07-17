#Dockerfile para o Eureka Server (service-discovery)
FROM openjdk:21-jdk-slim
VOLUME /tmp
EXPOSE 8761
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
