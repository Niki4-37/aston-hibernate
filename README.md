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

За отправку сообщений отвечает email-api, для которого необходимо настроить переменные среды `SMTP_USERNAME` и `SMTP_PASSWORD`
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

```url
http://localhost:8080/swagger-ui/index.html
```
