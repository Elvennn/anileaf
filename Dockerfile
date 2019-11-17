FROM openjdk:11-jre-slim
RUN apt update && apt install -y transmission-daemon
ADD ./daemon-fat-1.0-SNAPSHOT.jar daemon.jar
ADD ./libs/arm/libinotify-java.so /usr/lib/
CMD java -jar daemon.jar /Animes/.anileaf/