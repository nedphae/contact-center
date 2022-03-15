FROM openjdk:8-jdk-alpine
# app 版本
ARG JAR_NAME=gateway
ARG JAR_VERSION=0.0.1-SNAPSHOT
ARG JAR_FILE=${JAR_NAME}-${JAR_VERSION}.jar
COPY build/libs/${JAR_FILE} /app/app.jar
# 不使用内部文件夹
# COPY data /app/data
# VOLUME /app/data
WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom -Xms4g -Xmx25g -XX:+UseG1GC","-jar","app.jar"]