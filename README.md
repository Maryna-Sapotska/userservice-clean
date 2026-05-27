# User Service

Microservice application for managing users and payment cards.

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Data JPA
- PostgreSQL
- Redis
- Liquibase
- Docker
- Docker Compose
- Testcontainers
- JUnit 5
- Mockito
- MapStruct
- Maven
- Swagger/OpenAPI

---

# Features

## User Management

- Create user
- Get user by id
- Get all users with pagination
- Filter users by:
    - name
    - surname
    - active status
- Update user (including activation status via PATCH)
- Delete user

## Card Management

- Create payment card
- Get card by id
- Get all cards with pagination
- Get cards by user id
- Update card (including activation status via PATCH)
- Delete card


## Additional Features

- Redis caching
- Liquibase migrations
- JPA auditing
- Specifications filtering
- Validation
- Exception handling
- Unit tests
- Integration tests with Testcontainers
- Docker support
- CI Pipeline with GitHub Actions

---

# Database

## Tables

### users

| Column      | Type      |
|--------------|-----------|
| id           | bigint    |
| name         | varchar   |
| surname      | varchar   |
| birth_date   | date      |
| email        | varchar   |
| active       | boolean   |
| created_at   | timestamp |
| updated_at   | timestamp |

### payment_cards

| Column          | Type      |
|-----------------|-----------|
| id              | bigint    |
| user_id         | bigint    |
| number          | varchar   |
| holder          | varchar   |
| expiration_date | date      |
| active          | boolean   |
| created_at      | timestamp |
| updated_at      | timestamp |

---

# Requirements

- Java 21
- Maven
- Docker Desktop

---

# Run Application Locally

## 1. Clone repository

```bash
git clone <repository-url>
cd userservice
```

## 2. Start PostgreSQL and Redis

docker compose up -d postgres redis

## Run application

mvn spring-boot:run

Application will start on:

http://localhost:8080

# Run With Docker

## Build application

mvn clean package

## Build Docker image

docker build -t userservice .

## Start containers

docker compose up --build

--- 

# Spring Profiles

## local

Used for local development.

- PostgreSQL localhost
- Redis localhost

## docker

Used inside Docker Compose network.

- PostgreSQL container
- Redis container

--- 

# API Endpoints

## Users

| Method | Endpoint                | Description                         |
|--------|-------------------------|-------------------------------------|
| POST   | `/users`                | Create user                         |
| GET    | `/users/{id}`           | Get user by id                      |
| GET    | `/users`                | Get all users                       |
| GET    | `/users/{userId}/cards` | Get user with cards                 |
| PATCH  | `/users/{id}`           | Update user (including active flag) |
| DELETE | `/users/{id}`           | Delete user                         |

---

## Cards

| Method | Endpoint               | Description                         |
| ------ | ---------------------- | ----------------------------------- |
| POST   | `/cards`               | Create card                         |
| GET    | `/cards/{id}`          | Get card by id                      |
| GET    | `/cards`               | Get all cards                       |
| GET    | `/cards/user/{userId}` | Get cards by user id                |
| PATCH  | `/cards/{id}`          | Update card (including active flag) |
| DELETE | `/cards/{id}`          | Delete card                         |

---

# Testing

## Unit Tests

Service layer covered with unit tests using:

- JUnit 5
- Mockito

## Integration Tests

Integration tests implemented with:

- Spring Boot Test
- Testcontainers
- PostgreSQL container

Run tests:

mvn test

---

# Caching

Redis caching implemented for:

- User by id
- User with cards
- Card by id

Cache invalidation configured for update/delete operations.

---

# API docs URL

http://localhost:8080/swagger-ui/index.html

---

# CI Pipeline

GitHub Actions pipeline includes:

- Build
- Testing
- SonarQube analysis
- Docker image build

Pipeline configuration located in:

.github/workflows/ci.yml

---

# Author

Marina Sapotska