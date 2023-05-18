FROM openjdk:17-jdk-slim
LABEL maintainer="jerryshikanga.github.io"
WORKDIR /app
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
CMD ["java","-jar","app.jar"]