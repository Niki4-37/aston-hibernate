## Aston-hibernate

Программа Aston-hibernate предназначена для хранения и редактирования данных в формате User.
Сущность User включает в себя поля:

```java
String name
String email
LocalDate birthDate
LocalDate createdAt
```

Обработка команд от пользователя происходит в контроллере, который принимает REST запросы:

* find `GET localhost:8080/users/{id}`        
* create `POST localhost:8080/users/`        

```json
{
    "name": "Zacharia",
    "email": "to_2@gmail.com",
    "birthDate": "2000-01-10"
}
```

* update `PUT localhost:8080/users/{id}` 
* delete `DELETE localhost:8080/users/{id}` 

## Config 

Подготовить Docker, загрузив образы:

``` 
docker pull apache/kafka:4.1.1
docker pull postgres:14.19-alpine3.21
docker pull alpine:3
```

В репозитории .env.example переименовать в .env, установить `SMTP_USERNAME` и `SMTP_PASSWORD` для настройки JavaMailSender в notification-service.

## Email API

Для тестирования в Postman создать

POST localhost:8082/email/send

с телом JSON

```json
{
  "to": "to_1@gmail.com",
  "subject": "Тест отправки через Mail.ru",
  "body": "Это тестовое письмо из Spring‑приложения."
}
```

## Swagger UI

Тестирование API user-service в браузере

```url
http://localhost:8080/swagger-ui/index.html
```

## Spring cloud gateway

Проверка маршрутов

GET http://localhost:8000/actuator/gateway/routes

### Работа с User сервиса user-service через gateway

Найти пользователя
GET http://localhost:8000/users/2

Создать пользователя
POST http://localhost:8000/users

с телом JSON

```json
{
    "name": "Zacharia",
    "email": "to_2@gmail.com",
    "birthDate": "2000-01-10"
}
```

### Работа с email сервиса notification-service через gareway

POST http://localhost:8000/email/send

с телом JSON

```json
{
  "to": "to_1@gmail.com",
  "subject": "Тест отправки через Mail.ru",
  "body": "Это тестовое письмо из Spring‑приложения."
}
```

## Eureka discovery-service

В браузере по ссылке http://localhost:8761 проверить список сервисов

## Resilience4j circuitbreaker:

GET http://localhost:8080/actuator/circuitbreakers

## Spring configuration server

Получить JSON конфигурации `notification-service`

GET http://localhost:8888/notification-service/default

Получить JSON конфигурации `user-service`

GET http://localhost:8888/user-service/default