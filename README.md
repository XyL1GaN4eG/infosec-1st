# Информационная безопасность

## Лабораторная работа №1

### Стек

- Java/Spring
- Hibernate
- PostgreSQL

### Описание API

`POST /auth/register`: метод для регистрации пользователя (принимает логин и пароль).

```json
{
    "login": "username",
    "password": "12345678"
}
```

`POST /auth/login`: метод для аутентификации пользователя (принимает логин и пароль).

```json
{
    "login": "username",
    "password": "12345678"
}
```

`GET /api/data`: метод для получения данных. Доступ только у аутентифицированных пользователей.

### Описание реализованных мер защиты

- От **SQLi** код защищен с помощью ORM Hibernate (параметризованные запросы, нет конкатенации строк).
- От **XSS** защищен с помощью экранирования пользовательских данных через `HtmlUtils.htmlEscape()`.

```java
AuthModel authModel = new AuthModel(
  HtmlUtils.htmlEscape(credentialsDto.login()),
  passwordEncoder.encode(credentialsDto.password())
);
```

```java
public List<ProductModel> getAllProducts() {
  return productRepository.findAll().stream().peek(
    it -> it.setData(HtmlUtils.htmlEscape(it.getData()))
  ).toList();
}
```

- **Аутентификация** реализована через JWT токены:
  - Пароли хэшируются с помощью BCrypt (`BCryptPasswordEncoder`)
  - При успешном логине выдается JWT токен (HS256)
  - Middleware (`AuthorizationFilter`) проверяет токен на всех защищенных эндпоинтах (`/api/**`)

### CI/CD Pipeline

Настроен GitHub Actions workflow (`.github/workflows/ci.yml`), который при каждом push/PR запускает:
1. **SAST** — SpotBugs с плагином FindSecBugs для статического анализа кода
2. **SCA** — OWASP Dependency Check для проверки зависимостей на известные уязвимости

### Запуск проекта

1. Скопируйте `.env.example` в `.env` и при необходимости измените значения
2. Запустите PostgreSQL: `docker-compose up -d`
3. Запустите приложение: `mvn spring-boot:run`

### Тестирование API

```bash
# Регистрация
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"login": "testuser", "password": "password123"}'

# Логин (получение токена)
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login": "testuser", "password": "password123"}'

# Получение данных (требует токен)
curl -X GET http://localhost:8080/api/data \
  -H "Authorization: Bearer <your_jwt_token>"
```