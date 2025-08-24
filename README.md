# Backend Operations API

A Spring Boot RESTful API for managing **algebraic equations**, including storing equations, retrieving them, and evaluating equations with robust validation and exception handling.

---

## Features

* Add, retrieve, and evaluate algebraic equations
* Field-level validation for all inputs
* Custom exception handling with clear error messages
* Interactive API documentation via **Swagger UI**
* Unit tests using **JUnit 5** and **Mockito**
* Edge case handling using **TDD approach**

---

## Getting Started

### Prerequisites

* **Java 17** or higher (tested up to Java 24)
* **Maven 3.8+**

### Build & Run

1. Build the project:

```sh
mvn clean install
```

2. Run the application:

```sh
mvn spring-boot:run
```

3. Access Swagger UI for API documentation:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## API Endpoints

### 1. Store a new equation

**POST** `/api/equations/store`

**Request Body:**

```json
{
  "equation": "x + y * z"
}
```

**Response:**

```json
{
  "message": "Equation stored successfully",
  "equationId": 1
}
```

**Error Responses:**

```json
{
  "error": "Missing or empty 'equation' field"
}
```

```json
{
  "error": "Invalid equation syntax"
}
```

```json
{
  "error": "Invalid equation syntax: <equation>"
}
```

```json
{
  "error": "Invalid equation : <equation>"
}
```

---

### 2. Get all equations

**GET** `/api/equations`

**Response:**

```json
{
  "equations": [
    { "equationId": 1, "equation": "x + y * z" },
    { "equationId": 2, "equation": "a - b / c" }
  ]
}
```

---

### 3. Evaluate an equation

**POST** `/api/equations/{equationId}/evaluate`

**Request Body:**

```json
{
  "variables": {
    "x": 2,
    "y": 3,
    "z": 1
  }
}
```

**Response:**

```json
{
  "equationId": 1,
  "infixExpression": "x + y * z",
  "variables": {
    "x": 2,
    "y": 3,
    "z": 1
  },
  "result": 5
}
```

**Error Responses:**

```json
{
  "error": "Equation not found for ID 999"
}
```

```json
{
  "error": "Failed to evaluate equation for ID 1"
}
```

---

## Testing

* Unit tests are written using **JUnit 5** and **Mockito**.
* Run all tests:

```sh
mvn test
```

### Test Case Files

* **Controller Layer:**
  `src/test/java/com/example/Controller/EquationControllerTest.java`
* **Service Layer:**
  `src/test/java/com/example/Service/EquationServiceTest.java`

Tests cover edge cases such as:

* Invalid equation while storing
* Missing or invalid input fields
* Equation not found while evaluating
* Invalid variable inputs for evaluation

---

## Project Structure

```
src/
  main/
    java/com/example/
      Controller/         # REST controllers
      DTO/                # DTO classes
      Exception/          # Custom exceptions & global handler
      Manager/            # In-memory equation storage
      Model/              # EquationNode (tree representation)
      Service/            # Business logic
      Util/               # Utility/helper classes
  test/
    java/com/example/
      Controller/
        EquationControllerTest.java
      Service/
        EquationServiceTest.java
```

---

## Swagger Documentation

* Auto-generated Swagger UI is available at:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

* Endpoints are fully documented with request/response examples.

---

## Author

**Vrajesh Vaghasiya**
