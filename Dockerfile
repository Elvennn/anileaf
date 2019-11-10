FROM openjdk:11-jre-slim
ADD ./daemon-fat-1.0-SNAPSHOT.jar app.jar
CMD java -jar app.jar
