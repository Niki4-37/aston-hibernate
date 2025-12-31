FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/eureka-server-*.jar /app/app.jar
RUN apk add --no-cache bash
RUN apk add --no-cache curl
EXPOSE 8761
CMD ["java", "-jar", "app.jar"]