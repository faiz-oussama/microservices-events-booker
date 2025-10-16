FROM openjdk:17-jdk-slim

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN apt-get update && apt-get install -y maven && mvn clean package -DskipTests

EXPOSE 8761

CMD ["java", "-jar", "target/discovery-service-0.0.1-SNAPSHOT.jar"]