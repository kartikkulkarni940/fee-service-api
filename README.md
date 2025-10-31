#  Student Fee Management Microservices

A **Spring Boot–based microservices system** for managing student information and fee collection.  
Designed with **clear domain boundaries**, **Feign-based service communication**, and **RESTful best practices**.

---

##  Services Overview

- **Student Service** — Manages student registration, grade, and school details.
- **Fee Service** — Handles fee collection, generates receipts, and validates student data through Feign Client.

---

## ️ Features

✅ RESTful APIs using **Spring Boot 3.x**  
✅ **Spring Data JPA + H2 Database** for quick demo setup  
✅ **Feign Client** for inter-service communication  
✅ **Centralized exception handling & logging**  
✅ **Swagger UI** for API documentation  
✅ DTO and Mapper separation for clean architecture  
✅ Domain-driven structure for scalability

---

##  Domain Models

### 🧾 Student
| Field | Type | Description |
|-------|------|-------------|
| id | Long | Primary Key |
| studentId | String | Unique Business ID |
| name | String | Student Name |
| grade | String | Grade |
| mobileNumber | String | Contact Number |
| schoolName | String | School Name |

###  Receipt
| Field | Type | Description |
|-------|------|-------------|
| id | Long | Auto-generated |
| receiptNumber | String | Unique receipt number |
| studentId | String | Reference to student |
| amount | Double | Fee amount |
| paymentMode | String | CASH / CARD / UPI |
| paymentStatus | String | SUCCESS / PENDING / FAILED |
| remarks | String | Notes or failure reason |
| paymentDate | LocalDateTime | Payment timestamp |

---

##  Tech Stack

| Layer | Technology |
|-------|-------------|
| Framework | Spring Boot 3.x |
| Database | H2 (In-Memory) |
| ORM | Spring Data JPA |
| Communication | Spring Cloud OpenFeign |
| API Docs | Springdoc OpenAPI (Swagger UI) |
| Build Tool | Maven |
| Logging | SLF4J + Logback |

---

##  How to Run

### Step 1 — Start Student Service
```bash
cd student-service
mvn spring-boot:run
```

### Step 2 — Start Fee Service
```bash
cd fee-service
mvn spring-boot:run
```

> Both services run on different ports (e.g., `8081` and `8082`).

---

##  Example APIs

###  Student Service

**POST** `/api/students`  
Create a new student.

**GET** `/api/students/{studentId}`  
Fetch student details by ID.

---

###  Fee Service

**POST** `/api/receipts`  
Collect fee and generate a receipt.

#### Request:
```json
{
  "studentId": "S-5E7FB568",
  "amount": 12000.00,
  "paymentMode": "CARD",
  "paymentStatus": "SUCCESS",
  "remarks": "Term 2 Fee Payment",
  "cardNumber": "1234567812345678"
}

```

#### Successful Response:
```json
{
  "id": 1,
  "receiptNumber": "REC-1761884424711",
  "studentId": "S-5E7FB568",
  "studentName": "Ravi Kumar",
  "grade": "10",
  "schoolName": "Delhi Public School",
  "amount": 12000.0,
  "paymentMode": "CARD",
  "paymentStatus": "SUCCESS",
  "remarks": "Term 2 Fee Payment",
  "paymentDate": "2025-10-31T09:50:24.7116552",
  "cardNumber": "12****78"
}
```

#### Fallback Response (if Student Service is down):
```json
{
  "id": 2,
  "receiptNumber": "REC-1761884467379",
  "studentId": "S-5E7FB568",
  "studentName": "N/A",
  "grade": "N/A",
  "schoolName": "N/A",
  "amount": 12000.0,
  "paymentMode": "CARD",
  "paymentStatus": "PENDING",
  "remarks": "Student service unavailable, stored as pending",
  "paymentDate": "2025-10-31T09:51:07.3791313",
  "cardNumber": "12****78"
}
```

---

##  Swagger URLs

| Service | URL |
|----------|-----|
| Student Service | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) |
| Fee Service | [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html) |

---

##  Postman Collection

Import **Student-Fee-Collection.postman_collection.json** to test all APIs easily.

---

##  Summary

A simple yet complete **microservices project** showcasing:
- Service-to-service communication
- Fallback & error handling
- DTO/Entity clean structure
- Feign Client usage in real-world pattern

---
