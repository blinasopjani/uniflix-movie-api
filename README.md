# 🎬 UniFLIX Movie API

A RESTful API built with **Spring Boot 3.2** and **Java 17** for managing movies, user profiles, favorites, ratings, and personalized recommendations — the backend powering the UniFLIX streaming platform.

---

## 🚀 Tech Stack

| Technology | Purpose |
|---|---|
| Java 17 | Programming language |
| Spring Boot 3.2 | Backend framework |
| Spring Data JPA | ORM & database access |
| Spring WebFlux (WebClient) | Reactive HTTP client |
| H2 Database | In-memory DB for local dev |
| PostgreSQL (Supabase) | Production database |
| Lombok | Boilerplate reduction |
| Springdoc OpenAPI | Swagger UI documentation |
| Bean Validation | Input validation |

---

## ✨ Features

- 🔐 **Account & Profile Management** — Register, login, create/edit/delete profiles (Netflix-style multi-profile)
- 🎥 **Movie Management** — Add movies manually or sync from **OMDb API**
- 🌍 **Auto-Translation** — Movie genres and descriptions auto-translated to **Albanian** via Google Translate
- ❤️ **Favorites** — Users can add/remove movies from their favorites list
- ⭐ **Ratings** — Rate movies 1–5 stars with upsert logic
- 🤖 **AI Chat Bot** — Keyword-based movie recommendation bot (`/ai/chat`)
- 🧠 **Smart Recommendations** — Personalized recommendations based on favorite genres
- 📖 **Swagger UI** — Full API documentation at `/swagger-ui.html`

---

## 📁 Project Structure

```
src/main/java/com/movie/api/
├── controller/      # REST endpoints
├── service/         # Business logic
├── repository/      # Data access layer
├── entity/          # JPA entities (DB tables)
├── dto/             # Request/Response objects
├── exception/       # Global error handling
└── config/          # WebClient configuration
```

---

## ⚙️ Getting Started

### Prerequisites
- Java 17+
- Maven

### 1. Clone the repository
```bash
git clone https://github.com/blinasopjani/uniflix-movie-api.git
cd uniflix-movie-api
```

### 2. Configure credentials

Create the file `src/main/resources/application-local.properties` (already gitignored):

```properties
# PostgreSQL (Supabase)
spring.datasource.url=YOUR_DB_URL
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# OMDb API Key (get one free at https://www.omdbapi.com)
omdb.api.key=YOUR_OMDB_KEY
```

### 3. Run the application

```bash
# With local profile (uses application-local.properties)
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Without local profile (uses H2 in-memory DB)
./mvnw spring-boot:run
```

The API will be available at: `http://localhost:8080`

---

## 📖 API Documentation

Swagger UI: **`http://localhost:8080/swagger-ui.html`**

### Key Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/auth/register` | Register a new account |
| `POST` | `/auth/login` | Login to account |
| `GET` | `/movies` | Get all movies |
| `POST` | `/movies/sync?title=` | Sync movie from OMDb API |
| `POST` | `/movies/translate-all` | Translate all movies to Albanian |
| `GET` | `/users/{id}/recommendations` | Get personalized recommendations |
| `POST` | `/users/{id}/favorites` | Add movie to favorites |
| `POST` | `/ratings` | Rate a movie (1–5) |
| `POST` | `/ai/chat` | Chat with AI recommendation bot |

---

## 🤖 AI Bot Example

```json
POST /ai/chat
{
  "message": "dua filma aksion"
}
```
```json
{
  "response": "Këtu janë disa filma aksion që mund t'ju pëlqejnë!",
  "movies": [ ... ]
}
```

Supported keywords: `aksion`, `komedi`, `horror`, `dramë`, `serial`

---

## 🔒 Security Note

Sensitive credentials (database URL, passwords, API keys) are stored in `application-local.properties` which is **gitignored** and never pushed to this repository.

---

## 👤 Author

**Blina Sopjani** — [@blinasopjani](https://github.com/blinasopjani)
