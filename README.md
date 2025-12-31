## Aston-hibernate
Программа Aston-hibernate предназначена для хранения и редактирования данных в формате User.
Сущность User включает в себя поля:

```java
String name
String email
LocalDate birthDate
LocalDate createdAt
```

За обработка команд от пользователя происходит в контроллере, который принимает REST запросы:
* find `GET localhost:8080/users/{id}`        
* create `POST localhost:8080/users/`        
* update `PUT localhost:8080/users/{id}` 
* delete `DELETE localhost:8080/users/{id}` 

## Config 

В репозитории находится .env.example, изменить имя на .env установить `SMTP_USERNAME` и `SMTP_PASSWORD` для email-api.

## Email API

Для тестирования в Postman создать POST-запрос

```
localhost:8080/email/send
```

с телом JSON

```json
{
  "to": "redcarpet@mail.ru",
  "subject": "Тест отправки через Mail.ru",
  "body": "Это тестовое письмо из Spring‑приложения."
}
```

## Swagger UI

Тестирование API user-service в браузере

```url
http://localhost:8080/swagger-ui/index.html
```

## Kong

Проверка статуса kong gateway API
GET http://localhost:8001/status

Проверка маршрутов
GET http://localhost:8001/routes

Проверка сервисов
GET http://localhost:8001/services

Проверка User сервиса aston-service через kong
GET http://localhost:8000/api/aston-service/users/42

## Eureka discovery-service

Список сервисов
```url
http://localhost:8761
```