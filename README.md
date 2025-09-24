# Работа 1: Разработка защищенного REST API с интеграцией в CI/CD

---

## Реализованные эндпоинты

### 1. Аутентификация (логин)
`POST /auth/login`

**Пример запроса:**
```json
{
  "username": "testuser",
  "password": "mypassword"
}
````

**Пример ответа:**

```json
{
  "accessToken": "jwt-access-token",
  "refreshToken": "jwt-refresh-token"
}
```

---

### 2. Получение данных (только для авторизованных)

`GET /api/data`

**Заголовок запроса:**

```
Authorization: Bearer <accessToken>
```

**Пример ответа:**

```json
[
  {
    "id": 1,
    "username": "testuser"
  },
  {
    "id": 2,
    "username": "admin"
  }
]
```

### 3. Регистрация пользователя

`POST /auth/register`

Позволяет создать нового пользователя.

**Пример запроса:**

```json
{
  "username": "testuser",
  "password": "mypassword"
}
```

**Ответ:**
`201 Created` — пользователь успешно зарегистрирован.

---

### 4. Обновление токена

`POST /auth/refresh`

Позволяет получить новый `accessToken`, не вводя логин и пароль повторно, если у клиента есть действительный `refreshToken`.

**Пример запроса:**

```json
{
  "refreshToken": "jwt-refresh-token"
}
```

**Пример ответа:**

```json
{
  "accessToken": "new-access-token",
  "refreshToken": "new-refresh-token"
}
```

---

## Описание реализованных мер защиты

1. **Защита от SQL Injection**
    - Взаимодействие с базой данных реализовано через **Spring Data JPA (UserRepository)**.

2. **Защита от XSS (Cross-Site Scripting)**
    - Перед возвратом данных пользователю имена пользователей проходят кодирование через библиотеку **OWASP Java Encoder**:
      ```java
      u.setUsername(Encode.forHtml(u.getUsername()));
      ```

3. **Аутентификация и авторизация (JWT)**
    - Реализован кастомный `JwtFilter`, который перехватывает все запросы к `/api/**`.
    - При входе в систему пользователю выдаются **два токена**:
        - `accessToken` — живёт 15 минут и используется для доступа к защищённым эндпоинтам (`GET /api/data` в моем случае).
        - `refreshToken` — живёт 7 дней и позволяет получить новый access-токен без повторного ввода пароля.
    - Эндпоинт `GET /api/data` требует заголовок `Authorization: Bearer <accessToken>` для доступа к данным.

4. **Хранение паролей**
    - Пароли не хранятся в открытом виде.
    - Используется алгоритм **BCrypt** (`BCryptPasswordEncoder`).

---

## Скриншоты отчетов SAST/SCA

В проекте настроен **GitHub Actions pipeline** (`.github/workflows/ci.yml`), который запускается при каждом push и pull request.  
В пайплайне выполняются:

- **SAST (Static Analysis)**: запуск SpotBugs для анализа исходного кода
- **SCA (Dependency Scanning)**: проверка зависимостей на уязвимости с помощью OWASP Dependency-Check 

<img width="407" height="455" alt="image" src="https://github.com/user-attachments/assets/a349a219-c4da-478e-a1d3-89a2ce35c827" />



[![Ссылка на последний успешный запуск pipeline](https://github.com/KseniyaNesterenko/information_security_lab1/actions/workflows/ci.yml/badge.svg)](https://github.com/KseniyaNesterenko/information_security_lab1/actions/workflows/ci.yml)
