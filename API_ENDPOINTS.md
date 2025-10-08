# Online Shop API - Endpoint Documentation

All endpoints use the `/api/v1` prefix for consistent versioning.

## 🔐 Authentication Endpoints
**Base Path:** `/api/v1/auth`

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| POST | `/register` | Register a new user | UserRegistrationRequestDto |
| POST | `/login` | Authenticate and get JWT token | LoginRequestDto |

---

## 👤 User Endpoints
**Base Path:** `/api/v1/users`

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/{username}` | Get user profile by username | Yes |

---

## 🛒 Cart Endpoints
**Base Path:** `/api/v1/cart`

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| POST | `/add` | Add item to cart | AddItemToCartRequestDto |
| PUT | `/{userId}/update-quantity/{itemId}` | Update item quantity | UpdateItemQuantityDto |
| DELETE | `/delete/{userId}/items/{itemId}` | Remove item from cart | - |

---

## 📦 Product Endpoints
**Base Path:** `/api/v1/products`

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/` | Create a new product (Admin) | CreateProductRequestDto | ProductDto (201) |
| GET | `/` | Get paginated non-featured products | - | Page<ProductDto> |
| GET | `/featured` | Get all featured products | - | List<ProductDto> |
| GET | `/{productId}` | Get product by ID | - | ProductDto |
| PUT | `/update/{productId}` | Update product (Admin) | UpdateProductRequestDto | ProductDto |
| DELETE | `/{productId}` | Delete product (Admin) | - | 204 No Content |

**Query Parameters for Pagination:**
- `page` - Page number (default: 0)
- `size` - Page size (default: 20)
- `sort` - Sort field and direction (e.g., `price,asc`)

---

## 📋 Order Endpoints
**Base Path:** `/api/v1/orders`

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/create/{userId}` | Create order from user's cart | CreateOrderRequestDto | OrderDto (201) |
| GET | `/{orderId}` | Get order by ID | - | OrderDto |

---

## 📊 API Response Codes

### Success Codes
- `200 OK` - Request successful
- `201 Created` - Resource created successfully
- `204 No Content` - Resource deleted successfully

### Error Codes
- `400 Bad Request` - Invalid request data or validation failed
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `409 Conflict` - Business logic constraint violated (e.g., insufficient stock)
- `500 Internal Server Error` - Server error

---

## 🔒 Authentication

Most endpoints require JWT authentication. Include the token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

### Public Endpoints (No Auth Required)
- POST `/api/v1/auth/register`
- POST `/api/v1/auth/login`
- GET `/api/v1/products` (browsing products)
- GET `/api/v1/products/{productId}`
- GET `/api/v1/products/featured`

---

## 📝 Example Requests

### Register User
```bash
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

### Add Item to Cart
```bash
POST /api/v1/cart/add
Content-Type: application/json
Authorization: Bearer <token>

{
  "userId": 1,
  "productId": 5,
  "quantity": 2
}
```

### Create Order
```bash
POST /api/v1/orders/create/1
Content-Type: application/json
Authorization: Bearer <token>

{
  "shippingAddress": "123 Main St, City, Country 12345"
}
```

### Search Products (with pagination)
```bash
GET /api/v1/products?page=0&size=10&sort=price,asc
Authorization: Bearer <token>
```

---

## 🚀 API Versioning Strategy

The API uses URL-based versioning with `/api/v1` prefix. This allows:
- **Breaking changes** can be introduced in `/api/v2` without affecting existing clients
- **Gradual migration** - support multiple versions simultaneously
- **Clear deprecation** - mark old versions as deprecated before removal

### Future Versions
When introducing breaking changes:
1. Create new controllers under `/api/v2`
2. Maintain backward compatibility in `/api/v1`
3. Add deprecation warnings to old endpoints
4. Provide migration guide for clients

---

## 📚 Interactive API Documentation

Access Swagger UI for interactive API documentation:
```
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON specification:
```
http://localhost:8080/v3/api-docs
```

---

*Last Updated: 2025-10-08*
