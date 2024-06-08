FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install -y openjdk-21-jdk maven
COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:21-slim

EXPOSE 8080

COPY --from=build /target/access-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
