FROM ubuntu:latest

RUN apt-get update && apt-get install -y openjdk-8-jdk libltdl7 wget netcat socat

RUN wget https://download.docker.com/linux/ubuntu/dists/bionic/pool/stable/amd64/docker-ce-cli_18.09.0~3-0~ubuntu-bionic_amd64.deb && dpkg -i docker*.deb

RUN mkdir -p /usr/local/multisweeper

COPY ./target/multisweeper*.jar /usr/local/multisweeper/multisweeper.jar

CMD ["java", "-jar", "/usr/local/multisweeper/multisweeper.jar"]  
