# 🔗 TinyLink - URL Shortener 🚀

A highly scalable URL Shortener backend built with Spring Boot. The application allows users to create short URLs, redirect visitors, track analytics, apply rate limiting, and cache frequently accessed URLs using Redis.

---

## 📝 Description

TinyLink is a scalable URL shortening backend that enables users to generate, manage, and analyze shortened links.

### Key Capabilities

* **Short-Code Strategy:** Generate unique short URLs from long URLs using an efficient short-code generation strategy.
* **Customization:** Support custom aliases and configurable URL expiration.
* **High Performance:** Handle high-speed URL redirection with Redis-based caching.
* **Deep Analytics:** Collect click analytics such as click count, user agent, IP address, and request metadata.
* **Distributed Rate Limiting:** Apply distributed-friendly, IP-based rate limiting on URL creation:
  * ⏳ `2 requests per minute` per client
  * ⏳ `10 requests per hour` per client
* **Automated Housekeeping:** Automatically invalidate expired links through scheduled background cleanup tasks.
* **Open API:** Provide REST APIs documented with Swagger/OpenAPI.

---

## 📌 Features

### 🔐 Authentication & Authorization
* JWT-based authentication
* Secure user registration and login
* Role-based authorization

### 🔗 URL Management
* Create custom short URLs
* Blazing-fast redirection from short URLs to original URLs
* Expiration support with automatic background cleanup of expired URLs

### 📊 Analytics Tracking
* Total click count
* Geographic location (country & city tracking)
* Request metadata (IP address, user agent, referrer)
* Recent clicks history timeline

### ⚡ Performance & Storage
* **Redis Caching:** Drastically reduces database load for URL redirections.
* **Redis Rate Limiting:** Prevents API abuse at the network edge.
* **AWS S3 Integration:** Used for robust cloud storage of user assets (e.g. profile pictures).

---

## 🛠️ Tech Stack & Architecture

| Layer | Technologies Used |
| :--- | :--- |
| **Backend Framework** | Java 21, Spring Boot, Spring Security, Spring Data JPA, Hibernate, Lombok |
| **Database** | MySQL |
| **Caching & Security** | Redis (Caching & Rate Limiting), JWT (Auth tokens) |
| **Cloud Infrastructure** | AWS S3 |
| **Documentation** | Swagger / OpenAPI |
| **Deployment** | Docker, Docker Compose |

> This is a backend-only project — no frontend is included. All endpoints can be explored and tested directly through the Swagger UI.

---

## 🚀 Getting Started

### Requirements

Ensure you have the following installed locally:

* Java 21
* Maven
* Docker & Docker Compose
* MySQL

### Running the Project

```bash
# Clone the repository
git clone https://github.com/<your-username>/tinylink.git
cd tinylink

# Start dependencies (MySQL, Redis) with Docker Compose
docker-compose up -d

# Run the application
mvn spring-boot:run
```

Once running, the API is available at:

```
http://localhost:8080
```

Swagger UI:

```
http://localhost:8080/swagger-ui/index.html
```

---

## 🔄 End-to-End Flow

### 1. Create Short URL

1. A client sends `POST /api/shorten`
2. The backend controller extracts the client IP
3. The rate limiter checks whether that client is allowed to make the request
4. The service either uses the provided custom alias or generates a random Base62 code
5. The backend persists the `UrlData`
6. The backend caches `shortCode -> originalUrl` in Redis
7. The backend returns a response containing:
   * `shortUrl`
   * `shortCode`
   * `originalUrl`
   * timestamps (`createdAt`, `expiresAt`)

### 2. Open Short URL (Redirect)

1. A client requests `GET /api/{shortCode}`
2. The backend first checks the Redis cache for the original URL
3. If not found in Redis, it falls back to the database lookup
4. If the URL is expired, it is marked inactive and the request returns `404`
5. If found, the backend records the click and responds with HTTP `302 Found`
6. The client is redirected to the original URL

### 3. Load Stats

1. A client sends `GET /api/stats/{shortCode}`
2. The backend reads the corresponding `UrlData`
3. The backend returns click count, creator info, active status, and timestamps

### 4. Scheduled Cleanup

1. Spring scheduling is enabled in the application
2. `CleanupScheduler` runs on a configured interval
3. Expired URLs are marked inactive
4. Related Redis cache entries are deleted

---

## ⚡ Redis Usage

Redis is used in two places:

### 1. URL Cache

**Key shape:**
```
url:{shortCode}
```

**Value:** original URL string

**Purpose:**
* Fast redirect lookup
* Demonstrates the cache-aside pattern
* Reduces repeated reads from the main data store

**TTL:** configured through `tinylink.cache.ttl-minutes`

> Even though URLs are not editable today, TTL is still useful for automatic cleanup of cold cache entries, reducing memory growth, and avoiding stale values after delete/expiry.

### 2. Rate-Limit State

**Key shape:**
```
rate-limiter:{clientIp}
```

**Value:** serialized `RateLimitData`

**Purpose:**
* Shares rate-limit state across multiple app instances
* Avoids relying only on local in-memory (JVM) state

**TTL:** one hour on save — prevents unused rate-limit keys from living forever.

---

## 🚦 Rate Limiting

Rate limiting is checked before shortening a URL.

**Current behavior:**
* Per client / IP
* Minute threshold — configurable
* Hour threshold — configurable
* Redis-backed when available
* Falls back to in-memory storage if Redis is unavailable

**High-level flow:**
1. Build the Redis key from the client IP
2. Read `RateLimitData` from Redis
3. If missing, create local in-memory state
4. Check whether the current request is still within the same minute window
5. Reset the minute counter if the window has expired
6. Increment the request count if the request is allowed
7. Save the updated state back to Redis

> **Note:** `requestCount` is a rate-limit counter, while `clickCount` is a URL-analytics counter. These are different values and should not be confused.

---

## 📡 API Reference

Base URL: `http://localhost:8080`

Interactive Swagger UI: `http://localhost:8080/swagger-ui/index.html`

Most endpoints below (except `register`, `login`, and the redirect/safe-check endpoints) require a JWT sent in the `Authorization: Bearer <token>` header.

### 🔐 Auth Controller — `/api/auth`

#### `POST /api/auth/register`
Registers a new user.

**Request body** (`application/json`)
```json
{
  "name": "John Doe",
  "email": "user@example.com",
  "phoneNumber": "+201234567890",
  "address": "Cairo, Egypt",
  "password": "strongPassword123",
  "roles": ["USER"]
}
```

**Response `200 OK`**
```json
{
  "statusCode": 200,
  "message": "User registered successfully",
  "data": {},
  "metadata": {}
}
```

---

#### `POST /api/auth/login`
Authenticates a user and returns a JWT.

**Request body** (`application/json`)
```json
{
  "email": "user@example.com",
  "password": "strongPassword123"
}
```

**Response `200 OK`**
```json
{
  "statusCode": 200,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "roles": ["USER"]
  },
  "metadata": {}
}
```

---

### 👤 User Controller — `/api/users`

#### `PUT /api/users/update`
Updates the authenticated user's profile. Accepts partial updates.

**Request body** (`multipart/form-data`)

| Field | Type | Description |
| :--- | :--- | :--- |
| `name` | string | Updated full name |
| `email` | string | Updated email |
| `phoneNumber` | string | Updated phone number |
| `password` | string | New password (write-only) |
| `address` | string | Updated address |
| `profilePicture` | binary (file) | New profile picture, uploaded to AWS S3 |

**Response `200 OK`**
```json
{
  "statusCode": 200,
  "message": "Account updated successfully",
  "data": {},
  "metadata": {}
}
```

---

#### `GET /api/users`
Returns the authenticated user's profile.

**Response `200 OK`**
```json
{
  "statusCode": 200,
  "message": "User fetched successfully",
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "user@example.com",
    "profileUrl": "string",
    "address": "Cairo, Egypt",
    "roles": [
      { "id": 1, "roleName": "USER" }
    ],
    "profilePicture": "string",
    "active": true
  },
  "metadata": {}
}
```

---

#### `DELETE /api/users/deactive`
Deactivates (soft-deletes) the authenticated user's account.

**Response `200 OK`**
```json
{
  "statusCode": 200,
  "message": "Account deactivated successfully",
  "data": {},
  "metadata": {}
}
```

---

### ☁️ AWS Test Upload — `/api/upload`

#### `POST /api/upload`
Uploads a file directly to AWS S3 (used for testing the S3 integration).

**Query parameters**

| Name | Type | Required |
| :--- | :--- | :--- |
| `keyName` | string | ✅ |

**Request body** (`application/json`)
```json
{
  "file": "string"
}
```

**Response `200 OK`**
```json
"https://your-bucket.s3.amazonaws.com/keyName"
```

---

### 🔗 URL Shortener Controller — `/api`

#### `POST /api/shorten`
Creates a new shortened URL. Rate-limited per client IP (`2 req/min`, `10 req/hour`).

**Request body** (`application/json`)
```json
{
  "originalUrl": "https://example.com/some/very/long/path",
  "customAlias": "my-alias",
  "expiresAt": "2026-07-18T14:00:00.000Z"
}
```

**Response `200 OK`**
```json
{
  "statusCode": 200,
  "message": "URL shortened successfully",
  "data": {
    "shortUrl": "http://localhost:8080/api/abc123",
    "shortCode": "abc123",
    "originalUrl": "https://example.com/some/very/long/path",
    "createdAt": "2026-07-18T14:00:00.000Z",
    "expiresAt": "2026-07-18T15:00:00.000Z"
  },
  "metadata": {}
}
```

---

#### `GET /api/stats/{shortCode}`
Returns statistics for a specific short URL.

**Path parameters**

| Name | Type | Required |
| :--- | :--- | :--- |
| `shortCode` | string | ✅ |

**Response `200 OK`**
```json
{
  "statusCode": 200,
  "message": "Stats fetched successfully",
  "data": {
    "shortCode": "abc123",
    "originalUrl": "https://example.com/some/very/long/path",
    "clickCount": 42,
    "createdAt": "2026-07-18T14:00:00.000Z",
    "expiresAt": "2026-07-18T15:00:00.000Z",
    "createdByUser": "string",
    "createdBy": "string",
    "active": true
  },
  "metadata": {}
}
```

---

#### `GET /api/safe/{shortCode}`
Checks whether a short URL is still valid/active before redirecting (e.g. a safe-preview / phishing-guard step).

**Path parameters**

| Name | Type | Required |
| :--- | :--- | :--- |
| `shortCode` | string | ✅ |

**Response:** `200 OK`

---

#### `GET /api/my-all`
Returns all short URLs created by the authenticated user.

**Response `200 OK`**
```json
{
  "statusCode": 200,
  "message": "URLs fetched successfully",
  "data": [
    {
      "shortUrl": "http://localhost:8080/api/abc123",
      "shortCode": "abc123",
      "originalUrl": "https://example.com/some/very/long/path",
      "createdAt": "2026-07-18T14:00:00.000Z",
      "expiresAt": "2026-07-18T15:00:00.000Z"
    }
  ],
  "metadata": {}
}
```

---

#### `GET /api/analytics/{shortCode}`
Returns deep analytics for a short URL (click history, geo data, request metadata).

**Path parameters**

| Name | Type | Required |
| :--- | :--- | :--- |
| `shortCode` | string | ✅ |

**Response `200 OK`**
```json
{
  "statusCode": 200,
  "message": "Analytics fetched successfully",
  "data": "string",
  "metadata": {}
}
```

---

### 📦 Response Envelope

All endpoints (except `POST /api/upload` and the redirect/safe-check endpoints) return a consistent envelope:

```json
{
  "statusCode": 200,
  "message": "Human-readable message",
  "data": { },
  "metadata": { }
}
```

---

## 📄 License

Feel free to add your license of choice here (e.g. MIT).
