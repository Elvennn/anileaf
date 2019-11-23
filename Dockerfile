FROM openjdk:11-jre-slim
RUN apt update && apt install -y transmission-daemon
ADD ./daemon-fat-1.0-SNAPSHOT.jar daemon.jar
ADD ./animewatch/bin/animewatch animewatch
CMD java -jar daemon.jar /Animes/.anileaf/