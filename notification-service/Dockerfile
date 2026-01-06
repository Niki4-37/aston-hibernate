FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/notification-service*.jar /app/app.jar
RUN apk add --no-cache bash
RUN apk add --no-cache curl
EXPOSE 8082
CMD ["java", "-jar", "app.jar"]