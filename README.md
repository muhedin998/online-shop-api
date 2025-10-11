# Online Shop API

A Spring Boot REST API for an online shop with authentication, product catalog, categories, carts, orders, addresses, and email-backed password reset. Includes OpenAPI/Swagger docs, JWT security, Flyway migrations, and comprehensive tests with JaCoCo coverage.


## Overview
- API versioning under base path: `/api/v1`
- Authentication via JWT (login/register, forgot/reset password)
- Product catalog with featured flag, search, filters, pagination, and categories
- Shopping cart per user; add, update quantity, remove items
- Orders with status management and shipping address snapshot
- Address book per user
- Role-based authorization (`ROLE_USER`, `ROLE_ADMIN`, `ROLE_EMPLOYEE`)
- Database migrations with Flyway (PostgreSQL)
- API documentation via Swagger/OpenAPI


## Tech Stack
- Java 17, Spring Boot 3.5.x
- Spring Web, Spring Security, Spring Data JPA, Validation
- PostgreSQL, Flyway
- JWT (jjwt), MapStruct, Lombok, Java Mail
- WebSocket dependency present; no public endpoints exposed yet


## Project Structure
```
src/main/java/com/example/online_shop
├─ configuration/           # Security, JWT, data init
├─ user/                    # Auth, users, roles
├─ product/                 # Products, categories, search/specification
├─ cart/                    # Shopping cart
├─ order/                   # Orders, status, mappers
├─ address/                 # User addresses
└─ shared/                  # Exceptions, email service

src/main/resources
├─ application.properties   # Environment/config
└─ db/migration             # Flyway SQL migrations
```


## Prerequisites
- Java 17+
- Maven (wrapper included: `./mvnw`)
- PostgreSQL 13+


## Configuration
Sensitive configuration is now provided via environment variables or a local `.env` file.

- Copy `.env.example` to `.env` and fill in values
- `.env` is ignored by git
- Loaded automatically via `spring-dotenv`

Required variables:
- Database: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
- JWT: `APPLICATION_SECURITY_JWT_SECRET_KEY`, `APPLICATION_SECURITY_JWT_EXPIRATION`
- Mail: `SPRING_MAIL_HOST`, `SPRING_MAIL_PORT`, `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD`
- Frontend reset link base: `APP_FRONTEND_URL`

Example `.env` (see `.env.example`):
```
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/shop
SPRING_DATASOURCE_USERNAME=shop-user
SPRING_DATASOURCE_PASSWORD=your-password

# JWT
APPLICATION_SECURITY_JWT_SECRET_KEY=change-me-very-long-random-secret
APPLICATION_SECURITY_JWT_EXPIRATION=86400000

# Mail (Gmail example; prefer app password)
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password
SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true

# Frontend
APP_FRONTEND_URL=http://localhost:3000
```

Flyway runs automatically at startup and validates the schema. Roles are seeded on startup (`ROLE_USER`, `ROLE_ADMIN`, `ROLE_EMPLOYEE`).


## Database Setup (Local)
Use either your preferred tool or `psql`:
```
psql -U postgres -h localhost -c "CREATE USER \"shop-user\" WITH PASSWORD 'root';"
psql -U postgres -h localhost -c "CREATE DATABASE shop OWNER \"shop-user\";"
```
Adjust credentials or set the env vars above.


## Run Locally
- Development run: `./mvnw spring-boot:run`
- Build JAR: `./mvnw clean package`
- Run JAR: `java -jar target/online-shop-0.0.1-SNAPSHOT.jar`

App starts on `http://localhost:8080` by default.


## API Documentation
- Swagger UI: `http://localhost:8080/swagger-ui/index.html` (or `/swagger-ui.html`)
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`


## Authentication
Include the JWT in every protected call:
```
Authorization: Bearer <your-jwt-token>
```
Public endpoints include registration/login, password reset, and public browsing of products and categories.


## Endpoints Overview
Base path: `/api/v1`

- Auth (`/auth`)
  - POST `/register` — Register a new user
  - POST `/login` — Obtain JWT
  - POST `/forgot-password` — Request reset by email
  - POST `/reset-password` — Reset with token

- Users (`/users`)
  - GET `/{username}` — Get user profile (auth required)

- Products (`/products`)
  - GET `/` — Paginated non-featured products (public)
  - GET `/featured` — All featured products (public)
  - GET `/{productId}` — Product by ID (public)
  - GET `/search` — Search/filter by text, category, price, featured, inStock (public)
  - POST `/` — Create product (ROLE_ADMIN)
  - PUT `/update/{productId}` — Update product (ROLE_ADMIN or ROLE_EMPLOYEE)
  - DELETE `/{productId}` — Delete product (ROLE_ADMIN)

- Categories (`/categories`)
  - GET `/` — List all categories (public)
  - GET `/{id}` — Category by ID (public)
  - POST `/` — Create category (ROLE_ADMIN)
  - PUT `/{id}` — Update category (ROLE_ADMIN)
  - DELETE `/{id}` — Delete category (ROLE_ADMIN)

- Cart (`/cart`)
  - POST `/add` — Add item to cart (auth required)
  - PUT `/{userId}/update-quantity/{itemId}` — Update item quantity (auth required)
  - DELETE `/delete/{userId}/items/{itemId}` — Remove item (auth required)

- Orders (`/orders`)
  - POST `/create/{userId}` — Create order from user’s cart (auth required)
  - GET `/{orderId}` — Get order by ID (auth required)
  - POST `/cancel/{orderId}` — Cancel order (auth required)
  - GET `/user/{userId}` — List user orders (auth required)
  - PUT `/admin/{orderId}/status` — Update order status (ROLE_ADMIN)

- Addresses (`/addresses`)
  - POST `/{userId}` — Create address for user (auth required)
  - GET `/{userId}` — List user addresses (auth required)

Success codes: 200/201/204. Errors: 400, 401, 403, 404, 409, 500. See Swagger for full models and responses.


## Example Requests
Register:
```
POST /api/v1/auth/register
Content-Type: application/json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "SecurePass123!",
  "firstName": "John",
  "lastName": "Doe"
}
```

Add to cart:
```
POST /api/v1/cart/add
Authorization: Bearer <token>
Content-Type: application/json
{
  "userId": 1,
  "productId": 5,
  "quantity": 2
}
```

Create order:
```
POST /api/v1/orders/create/1
Authorization: Bearer <token>
Content-Type: application/json
{
  "shippingAddress": "123 Main St, City, Country 12345"
}
```

Search products with pagination:
```
GET /api/v1/products/search?searchText=phone&minPrice=100&maxPrice=999&inStock=true&page=0&size=10&sort=price,asc
```


## Security & Authorization
Key rules (see `SecurityConfig`):
- Public: `/api/v1/auth/**`, GET `/api/v1/products/**`, GET `/api/v1/categories/**`, Swagger (`/swagger-ui/**`, `/v3/api-docs/**`)
- Admin only: POST/PUT/DELETE on `/api/v1/products/**`, POST/PUT/DELETE on `/api/v1/categories/**`, `/api/v1/orders/admin/**`
- Auth required: `/api/v1/orders/**`, `/api/v1/addresses/**`, `/api/v1/cart/**`, and other unspecified endpoints

Roles are seeded; users must be assigned roles via application flow or DB.


## Testing & Coverage
- Run tests with coverage: `./mvnw clean test`
- View HTML report: `target/site/jacoco/index.html`
- Enforce thresholds only: `./mvnw jacoco:check`
- Generate only report: `./mvnw jacoco:report`

Status (2025-10-11):
- Line coverage ≈ 68%
- Service layer 73–100% (User/Address/Email at 100%, Product ~87%, Order ~77%)
- Controllers and JPA Specifications are lower by design; focus is on business logic

Thresholds (in `pom.xml`): 70% line / 65% branch. Exclusions: DTOs, models/entities, configuration, generated code (MapStruct), exceptions, main class.


## Migrations & Seed Data
Flyway migrations in `src/main/resources/db/migration` create core tables, categories table, order shipping snapshot columns, and password reset tokens. `DataInitializer` ensures roles exist at startup.


## Troubleshooting
- 401/403: Ensure you include a valid `Authorization: Bearer <token>` header and have the right role
- DB errors on startup: Verify Postgres is running and credentials match
- Password reset emails: Ensure mail credentials are correct and provider allows SMTP/app passwords
- Swagger UI not found: Use `/swagger-ui/index.html` with springdoc 2.x


## Contributing
- Follow unit test best practices in `TESTING_STRATEGY.md`
- Keep service/business logic thoroughly tested; integration tests for controllers as needed
- Update Swagger annotations when changing endpoints


## References
- API Endpoints: `API_ENDPOINTS.md` (merged and updated here)
- Testing Strategy: `TESTING_STRATEGY.md`
- Coverage Reports: `COVERAGE_SUMMARY.md`, `COVERAGE_FINAL_REPORT.md`, `FINAL_COVERAGE_ACHIEVEMENT.md`
