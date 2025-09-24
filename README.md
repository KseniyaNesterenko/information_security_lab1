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
  <img width="745" height="199" alt="image" src="https://github.com/user-attachments/assets/b924ca82-92ee-44ab-a288-4768e04e77aa" />

- **SCA (Dependency Scanning)**: проверка зависимостей на уязвимости с помощью OWASP Dependency-Check 

<img width="1288" height="387" alt="image" src="https://github.com/user-attachments/assets/5b8eda1c-20e9-4996-b135-3fcd1a9bd9ee" />
<img width="1171" height="130" alt="image" src="https://github.com/user-attachments/assets/3a7e0589-7df8-4da0-917f-a95c52e2b6b9" />

В отчете упоминается уязвимость `CVE-2018-1258`, актуальная для Spring Framework version 5.0.5. Проверим версию в моем проекте:

```bash
 mvn dependency:tree | grep spring
```

```bash
[INFO] +- org.springframework.boot:spring-boot-starter-web:jar:3.5.6:compile
[INFO] |  +- org.springframework.boot:spring-boot-starter:jar:3.5.6:compile
[INFO] |  |  +- org.springframework.boot:spring-boot:jar:3.5.6:compile
[INFO] |  |  +- org.springframework.boot:spring-boot-autoconfigure:jar:3.5.6:compile
[INFO] |  |  +- org.springframework.boot:spring-boot-starter-logging:jar:3.5.6:compile
[INFO] |  +- org.springframework.boot:spring-boot-starter-json:jar:3.5.6:compile
[INFO] |  +- org.springframework.boot:spring-boot-starter-tomcat:jar:3.5.6:compile
[INFO] |  +- org.springframework:spring-web:jar:6.2.11:compile
[INFO] |  |  +- org.springframework:spring-beans:jar:6.2.11:compile
[INFO] |  \- org.springframework:spring-webmvc:jar:6.2.11:compile
[INFO] |     +- org.springframework:spring-context:jar:6.2.11:compile
[INFO] |     \- org.springframework:spring-expression:jar:6.2.11:compile
[INFO] +- org.springframework.boot:spring-boot-starter-security:jar:3.5.6:compile
[INFO] |  +- org.springframework:spring-aop:jar:6.2.11:compile
[INFO] |  +- org.springframework.security:spring-security-config:jar:6.5.5:compile
[INFO] |  \- org.springframework.security:spring-security-web:jar:6.5.5:compile
[INFO] +- org.springframework.boot:spring-boot-starter-data-jpa:jar:3.5.6:compile
[INFO] |  +- org.springframework.boot:spring-boot-starter-jdbc:jar:3.5.6:compile
[INFO] |  |  \- org.springframework:spring-jdbc:jar:6.2.11:compile
[INFO] |  +- org.springframework.data:spring-data-jpa:jar:3.5.4:compile
[INFO] |  |  +- org.springframework.data:spring-data-commons:jar:3.5.4:compile
[INFO] |  |  +- org.springframework:spring-orm:jar:6.2.11:compile
[INFO] |  |  +- org.springframework:spring-tx:jar:6.2.11:compile
[INFO] |  \- org.springframework:spring-aspects:jar:6.2.11:compile
[INFO] +- org.springframework.boot:spring-boot-starter-test:jar:3.5.6:test
[INFO] |  +- org.springframework.boot:spring-boot-test:jar:3.5.6:test
[INFO] |  +- org.springframework.boot:spring-boot-test-autoconfigure:jar:3.5.6:test
[INFO] |  +- org.springframework:spring-core:jar:6.2.11:compile
[INFO] |  |  \- org.springframework:spring-jcl:jar:6.2.11:compile
[INFO] |  +- org.springframework:spring-test:jar:6.2.11:test
[INFO] +- org.springframework.security:spring-security-test:jar:6.5.5:test
[INFO] |  \- org.springframework.security:spring-security-core:jar:6.5.5:compile
[INFO] |     \- org.springframework.security:spring-security-crypto:jar:6.5.5:compile
```

Такая версия в моем проекте не используется, следовательно, уязвимость `CVE-2018-1258` в моем случае неактуальна.


[![Ссылка на последний успешный запуск pipeline](https://github.com/KseniyaNesterenko/information_security_lab1/actions/workflows/ci.yml/badge.svg)](https://github.com/KseniyaNesterenko/information_security_lab1/actions/workflows/ci.yml)
