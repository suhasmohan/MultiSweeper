FROM ubuntu:latest

RUN apt-get update && apt-get install -y openjdk-8-jdk

RUN mkdir -p /usr/local/multisweeper

COPY ./target/lb*.jar /usr/local/multisweeper/lb.jar

CMD ["java", "-jar", "/usr/local/multisweeper/lb.jar"]
