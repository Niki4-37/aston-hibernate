FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/spring-gateway-*.jar /app/app.jar
RUN apk add --no-cache bash
RUN apk add --no-cache curl
EXPOSE 8000
CMD ["java", "-jar", "app.jar"]