# Finance API

REST API for personal finance management built with Spring Boot.

## Description

This project is a backend application designed to manage personal finance data.
It focuses on backend fundamentals and clean architecture using Java and Spring Boot.

## Tech Stack

- Java 17
- Spring Boot
- Maven
- Spring Data JPA
- Spring Security
- H2 Database (in-memory)

## Project Structure

controller  -> Handles HTTP requests
service     -> Business logic
repository  -> Data access layer
model       -> Entities (database representation)

## Features

- Register user
- Login with Bearer token
- Get authenticated user profile
- Update authenticated user profile
- Delete authenticated user profile when it has no transactions
- Create authenticated user transactions
- List authenticated user transactions
- Get authenticated user transaction by ID
- Update authenticated user transaction
- Delete authenticated user transaction
- Get authenticated user financial summary
- Data ownership validation
- Input validation
- Error handling

## Main Endpoints

### Auth

- `POST /auth/register`
- `POST /auth/login`

### Users

- `GET /users/me`
- `PUT /users/me`
- `DELETE /users/me`

### Transactions

- `POST /transactions`
- `GET /transactions`
- `GET /transactions/summary`
- `GET /transactions/{id}`
- `PUT /transactions/{id}`
- `DELETE /transactions/{id}`

Protected endpoints require:

```text
Authorization: Bearer <token>
```

Transaction requests do not receive `userId`. The authenticated user is always the transaction owner.

## How to Run

Clone the repository:

```bash
git clone https://github.com/pedrohenriquecg/finance-api.git
cd finance-api
```

Run the application:

```bash
JWT_SECRET=replace-with-at-least-32-characters ./mvnw spring-boot:run
```

On Windows:

```powershell
$env:JWT_SECRET="replace-with-at-least-32-characters"
.\mvnw.cmd spring-boot:run
```

The API will be available at:

```text
http://localhost:8080
```

Run tests:

```bash
./mvnw test
```

On Windows:

```bash
mvnw.cmd test
```
