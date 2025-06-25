FROM openjdk:17-jdk-slim

MAINTAINER Avgustina Shevchuk <avgu92@gmail.com>

RUN mkdir /app
COPY ./build/libs/buy-recipe-0.0.1-SNAPSHOT.jar /app
WORKDIR /app
EXPOSE 8080
CMD ["java", "-jar", "buy-recipe-0.0.1-SNAPSHOT.jar"]