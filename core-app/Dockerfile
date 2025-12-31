# Используем официальный образ Eclipse Temurin (Adoptium) — лёгкий и безопасный
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Копируем jar‑файл приложения (убедитесь, что он собран)
COPY target/core-app-*.jar /app/app.jar


# Копируем конфигурационный файл (если нужен)
COPY src/main/resources/application.yml /app/application.yml

RUN apk add --no-cache curl

# Открываем порт приложения
EXPOSE 8080


# Запускаем приложение с указанием пути к конфигу
CMD ["java", \
    "-Dspring.config.location=file:/app/application.yml", \
    "-jar", "app.jar"]
