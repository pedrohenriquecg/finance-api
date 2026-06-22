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
- H2 Database (in-memory)

## Project Structure

controller  -> Handles HTTP requests
service     -> Business logic
repository  -> Data access layer
model       -> Entities (database representation)

## Features

- Create user
- List users
- Get user by ID
- Update user
- Delete user
- Input validation
- Error handling

## How to Run

Clone the repository:

```bash
git clone https://github.com/pedrohenriquecg/finance-api.git
cd finance-api
```

Run the application:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The API will be available at:

```text
http://localhost:8080
```
