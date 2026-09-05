# FinForge Backend API

A high-performance **enterprise personal finance REST API** built with **Java 21**, **Spring Boot 3.2**, **Spring Data JPA**, and **Hibernate 6**, backed by Microsoft SQL Server. It exposes secure, decoupled JSON endpoints consumed by the React frontend (`finForgeUI`), and is fully containerized with Docker.

---

## Tech Stack

| Layer | Technology | Details |
|---|---|---|
| **Language** | Java 21 | Modern LTS features, Records, Pattern Matching |
| **Framework** | Spring Boot 3.2.0 | Spring Web MVC, Inversion of Control, Embedded Tomcat |
| **Persistence** | Spring Data JPA · Hibernate 6 | Jakarta Persistence API, Entity Mappings, JPQL |
| **Database** | Microsoft SQL Server | Relational storage, indexes, check constraints |
| **Connection Pool** | HikariCP | Auto-configured, low-latency connection pooling |
| **Security & Auth** | Custom Session / PasswordUtil | SHA-256 password hashing, CORS policy |
| **Logging** | Log4j 2 / SLF4J | Structured application logging |
| **Build & Tooling** | Maven 3.x | Multi-stage Docker build, wrapper (`mvnw`) |
| **Testing** | JUnit 5 · Mockito | Comprehensive Service, DAO, and Utility test suites |

---

## Architecture

FinForge follows a strict, decoupled **Controller-Service-Repository** enterprise pattern:

```
┌────────────────────────────────────────────────────────┐
│                   React Frontend (UI)                  │
└──────────────────────────┬─────────────────────────────┘
                           │ HTTP JSON Requests (:5173 or :80)
                           ▼
┌────────────────────────────────────────────────────────┐
│      Spring Boot REST Controllers (/api/...)           │
│   (Auth, Expenses, Incomes, Categories, Reports)       │
├────────────────────────────────────────────────────────┤
│             Service Layer (@Service, @Transactional)   │
│   (Validation, Business Rules, Aggregations)           │
├────────────────────────────────────────────────────────┤
│             Repository Layer (Spring Data JPA)         │
│   (UserRepository, ExpenseRepository, etc.)            │
├────────────────────────────────────────────────────────┤
│                 Hibernate ORM 6.x                      │
│   (Entity Mappings, Schema Validation, Cache)          │
├────────────────────────────────────────────────────────┤
│             Microsoft SQL Server Database              │
└────────────────────────────────────────────────────────┘
```

1. **REST Controllers** (`com.finforge.controller.api`): Handle HTTP requests, parse JSON DTOs, enforce HTTP status codes, and delegate logic to services via constructor-injected Spring beans.
2. **Service Layer** (`com.finforge.service`): Enforces business rules and validation (`ValidationUtil`), coordinates multi-step domain operations, and manages transactional boundaries with `@Transactional`.
3. **Repository Layer** (`com.finforge.repository`): Extends Spring Data JPA's `JpaRepository` for type-safe data access, pagination (`Pageable`), and custom JPQL aggregation queries.
4. **Domain Model** (`com.finforge.model`): Rich Jakarta Persistence entities (`@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@CreationTimestamp`, `@UpdateTimestamp`).
5. **CORS Policy** (`com.finforge.config.CorsConfig`): Allows cross-origin requests from the React frontend running on localhost or via Docker Nginx.

---

## Key Features

### 1. Authentication & Security
- User registration with unique username and lowercase email enforcement.
- SHA-256 salted password hashing (`PasswordUtil`).
- Session-based authentication with `/api/auth/login`, `/api/auth/register`, `/api/auth/logout`, and `/api/auth/me`.
- User profile updates and secure password changes.

### 2. Expense Management
- Full CRUD operations with title, description, amount, category, and date.
- **Server-Side Pagination**: Efficient record retrieval using Spring Data JPA `Pageable` (`page`, `pageSize`).
- **Multi-attribute Filtering**: Filter expenses by date range (`fromDate`, `toDate`) and/or category (`categoryId`).
- Automatic default category seeding on user registration (Food, Travel, Rent, Medical, Shopping, Utilities, Entertainment).

### 3. Income Management
- Track incoming cash flow with source, amount, and date.
- Paginated retrieval and CRUD operations.

### 4. Custom Categories
- Create, update, and delete custom expense categories with per-user duplicate validation.

### 5. Financial Reports & Analytics
- **Dashboard Summary**: Real-time aggregation of total income, total expense, and net savings.
- **Monthly Spending Trends**: Timeline aggregation formatted as `YYYY-MM`.
- **Category Breakdown**: Categorical expense distribution for charts and spending analysis.

---

## Project Structure

```
finforge/
├── pom.xml                                   # Maven dependencies (Spring Data JPA, Web, MSSQL, Test)
├── Dockerfile                                # Multi-stage production container image
├── .dockerignore
├── sql/
│   └── schema.sql                            # SQL Server DDL (tables, indexes, constraints)
└── src/
    ├── main/
    │   ├── java/com/finforge/
    │   │   ├── FinForgeApplication.java      # Main Spring Boot entry point
    │   │   ├── config/
    │   │   │   └── CorsConfig.java           # Cross-Origin Resource Sharing configuration
    │   │   ├── controller/api/               # Spring Boot REST API Controllers
    │   │   │   ├── BaseApiController.java
    │   │   │   ├── AuthApiController.java
    │   │   │   ├── ExpenseApiController.java
    │   │   │   ├── IncomeApiController.java
    │   │   │   ├── CategoryApiController.java
    │   │   │   ├── ReportApiController.java
    │   │   │   └── UserApiController.java
    │   │   ├── repository/                   # Spring Data JPA Repositories
    │   │   │   ├── UserRepository.java
    │   │   │   ├── CategoryRepository.java
    │   │   │   ├── ExpenseRepository.java
    │   │   │   └── IncomeRepository.java
    │   │   ├── service/                      # Business logic (@Service, @Transactional)
    │   │   │   ├── CategoryService(Impl).java
    │   │   │   ├── ExpenseService(Impl).java
    │   │   │   ├── IncomeService(Impl).java
    │   │   │   ├── ReportService(Impl).java
    │   │   │   └── UserService(Impl).java
    │   │   ├── model/                        # JPA Domain Entities (@Entity, @Table)
    │   │   │   ├── User.java
    │   │   │   ├── Category.java
    │   │   │   ├── Expense.java
    │   │   │   └── Income.java
    │   │   ├── dto/                          # API Request/Response Data Transfer Objects
    │   │   │   ├── CategoryDTO.java
    │   │   │   ├── CategoryReportDTO.java
    │   │   │   ├── ExpenseDTO.java
    │   │   │   ├── ExpenseFilterDTO.java
    │   │   │   ├── IncomeDTO.java
    │   │   │   ├── MonthlyReportDTO.java
    │   │   │   ├── PagedResult.java
    │   │   │   ├── ReportDTO.java
    │   │   │   └── UserDTO.java
    │   │   ├── exception/                    # Custom application exceptions
    │   │   │   ├── FinForgeException.java
    │   │   │   ├── DAOException.java
    │   │   │   ├── ValidationException.java
    │   │   │   ├── DuplicateUserException.java
    │   │   │   ├── InvalidCredentialsException.java
    │   │   │   └── UserNotFoundException.java
    │   │   └── util/
    │   │       ├── DBConnection.java
    │   │       ├── PasswordUtil.java
    │   │       ├── SessionUtil.java
    │   │       └── ValidationUtil.java
    │   └── resources/
    │       ├── application.properties        # Server port, datasource, & Hibernate configuration
    │       └── log4j2.xml                    # Logging configuration
    └── test/
        └── java/com/finforge/
            ├── service/                      # Service layer unit tests (Mockito)
            │   ├── CategoryServiceTest.java
            │   ├── ExpenseServiceTest.java
            │   ├── IncomeServiceTest.java
            │   ├── ReportServiceTest.java
            │   └── UserServiceTest.java
            ├── dao/                          # Persistence layer unit tests (Mockito)
            │   ├── CategoryDAOTest.java
            │   ├── ExpenseDAOTest.java
            │   ├── IncomeDAOTest.java
            │   └── UserDAOTest.java
            └── util/                         # Utility tests
                ├── PasswordUtilTest.java
                └── ValidationUtilTest.java
```

---

## Configuration (`application.properties`)

All backend runtime settings are defined in `src/main/resources/application.properties`:

```properties
server.port=8082

# SQL Server Datasource
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=FinForgeDB;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=Your_Password123
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# Spring Data JPA & Hibernate
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
spring.jpa.open-in-view=false
```

---

## Running the Application

### Option 1: Local Development
Ensure Java 21+ is installed, then run:

```powershell
# In the finforge directory:
.\mvnw.cmd spring-boot:run
```

The REST API will start on: `http://localhost:8082/api`

### Option 2: Docker Container
```powershell
# Build and run backend container
docker build -t finforge-backend .
docker run -d -p 8082:8082 --name finforge-api finforge-backend
```

### Option 3: Full Stack via Docker Compose (Root Directory)
```powershell
# From the root finForge/ folder:
docker compose up --build
```

---

## Running Tests

Unit tests are written using **JUnit 5** and **Mockito 5**:

```powershell
# Run all test suites
.\mvnw.cmd test
```
