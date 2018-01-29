FROM openjdk:8-jdk-alpine
MAINTAINER Gregory Lawson <gregory.lawson@taptech.net>
VOLUME /tmp
ARG JAR_FILE
ADD target/${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]