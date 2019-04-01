FROM openjdk:8

RUN mkdir -p /usr/local/multisweeper

COPY ./target/multisweeper*.jar /usr/local/multisweeper/multisweeper.jar

CMD ["java", "-jar", "/usr/local/multisweeper/multisweeper.jar"] 
