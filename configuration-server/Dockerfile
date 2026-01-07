FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
ARG JAR_FILE=target/configuration-server-*.jar
COPY ${JAR_FILE} app.jar
RUN apk add --no-cache curl
EXPOSE 8888
ENTRYPOINT ["java","-jar","app.jar"]