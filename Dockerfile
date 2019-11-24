FROM openjdk:11-jre-slim
RUN apt update && apt install -y transmission-daemon gcc
ADD ./animewatch/src/animewatch.c animewatch.c
RUN gcc animewatch.c -o animewatch
ADD ./daemon-fat-1.0-SNAPSHOT.jar daemon.jar
CMD java -jar daemon.jar /Animes/.anileaf/