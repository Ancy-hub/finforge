# FinForge — Enterprise Personal Finance Platform

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Data JPA](https://img.shields.io/badge/Spring%20Data-JPA%20%2F%20Hibernate%206-blue.svg)](https://spring.io/projects/spring-data-jpa)
[![React](https://img.shields.io/badge/React-18-61dafb.svg)](https://react.dev/)
[![Vite](https://img.shields.io/badge/Vite-5.4-purple.svg)](https://vitejs.dev/)
[![Database](https://img.shields.io/badge/Database-SQL%20Server-red.svg)](https://www.microsoft.com/sql-server)
[![Docker](https://img.shields.io/badge/Docker-Multi--stage%20%26%20Compose-2496ed.svg)](https://www.docker.com/)
[![Testing](https://img.shields.io/badge/Testing-JUnit%205%20%2B%20Mockito-25A162.svg)](https://junit.org/junit5/)

**FinForge** is an enterprise-grade full-stack personal finance and wealth management platform. It features a decoupled, reactive **React 18 / Vite** frontend communicating with a robust **Spring Boot 3.2 REST API** backend powered by **Spring Data JPA & Hibernate 6** on **Microsoft SQL Server**, fully containerized via **Docker Compose**.

---

## Table of Contents

1. [Architecture Flow](#architecture-flow)
2. [Tech Stack](#tech-stack)
3. [Key Features](#key-features)
4. [Project Structure](#project-structure)
5. [REST API Reference](#rest-api-reference)
6. [Getting Started (Local Run)](#getting-started-local-run)
7. [Running with Docker (One-Command)](#-running-with-docker-one-command-setup)
8. [Automated Testing (JUnit 5 & Mockito)](#-automated-testing-junit-5--mockito)
9. [Database Schema](#database-schema)

---

## Architecture Flow

FinForge follows a strict, decoupled **Controller-Service-Repository** enterprise architecture:

```
┌────────────────────────────────────────────────────────┐
│                   User Interaction                     │
└──────────────────────────┬─────────────────────────────┘
                           │ Interacts with UI
                           ▼
┌────────────────────────────────────────────────────────┐
│                   React 18 UI Views                    │
│    (Dashboard, Expenses, Incomes, Categories, Reports) │
└──────────────────────────┬─────────────────────────────┘
                           │ Dispatches Action
                           ▼
┌────────────────────────────────────────────────────────┐
│               UI Controllers (React Hooks)             │
│   (useExpenseController, useIncomeController, etc.)    │
└──────────────┬───────────────────────────┬─────────────┘
               │                           │
  [Decide Page to Display]         [Requires Data]
               │                           │
               ▼                           ▼
┌──────────────────────────┐  ┌──────────────────────────┐
│  View / Page Transition  │  │     Service in UI        │
│   (Modal, Tab, Route)    │  │  (apiClient, expenseSvc) │
└──────────────────────────┘  └────────────┬─────────────┘
                                           │
                                           │ HTTP JSON Requests (:8082/api/...)
                                           ▼
┌────────────────────────────────────────────────────────┐
│             Spring Boot REST API Controllers           │
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

1. **User interacts with React UI**: Dark luxe aesthetic with glassmorphism, responsive data tables, modal dialogs, and real-time toast feedback.
2. **UI Controllers**: Custom hooks handle UI state transitions (e.g. modals, active tabs) and delegate API communication to UI Services.
3. **UI Services**: Centralized HTTP client (`apiClient.js`) and domain services that communicate with backend endpoints.
4. **Spring Boot REST Controllers**: Handle incoming JSON payloads, validate requests, and enforce HTTP status codes.
5. **Spring Service Layer**: Manages business logic, entity validation (`ValidationUtil`), and declarative transactions (`@Transactional`).
6. **Repository Layer**: Powered by Spring Data JPA interfaces for typed CRUD, pagination (`Pageable`), and custom JPQL aggregation queries.
7. **Hibernate 6 ORM**: Automatically maps domain models to SQL Server tables using Jakarta Persistence annotations.

---

## Tech Stack

| Layer | Technologies | Highlights |
|---|---|---|
| **Frontend UI** | React 18, Vite 5, Vanilla CSS, Lucide Icons | Responsive layout, dark luxe theme, zero heavy CSS frameworks |
| **Backend REST API** | Spring Boot 3.2.0, Spring Web MVC, Java 21 | Embedded Tomcat, Inversion of Control, CORS policy configuration |
| **Persistence / ORM** | Spring Data JPA, Hibernate 6, Jakarta Persistence | Type-safe repositories, `@Entity`, `@Transactional`, JPQL queries |
| **Database** | Microsoft SQL Server | Relational storage, unique constraints, foreign keys, indexes |
| **Connection Pool** | HikariCP | Auto-configured, high-performance connection pool |
| **DevOps & Containers**| Docker, Docker Compose, Nginx | Multi-stage builds, reverse proxy routing, multi-service compose |
| **Testing** | JUnit 5, Mockito 5 | 11 comprehensive test suites across Service, DAO, and Util layers |
| **Security** | Session Management, PasswordUtil | SHA-256 password hashing, CORS security headers |
| **Logging** | Log4j 2 / SLF4J | Structured logging across controllers and services |

---

## Key Features

### 1. Authentication & User Profile
- User registration with unique username and lowercase email enforcement.
- Salted SHA-256 password hashing.
- Session-based authentication (`/api/auth/login`, `/api/auth/register`, `/api/auth/logout`, `/api/auth/me`).
- Profile management and secure password change with verification.

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
finForge/
├── finForgeUI/                              # React 18 / Vite Frontend Application
│   ├── src/
│   │   ├── components/                      # UI Components (Navbar, FlowBanner, Modals, Toasts)
│   │   ├── controllers/                     # UI Controllers (Routing, State, Service Delegation)
│   │   │   ├── useAppFlowController.js
│   │   │   ├── useExpenseController.js
│   │   │   ├── useIncomeController.js
│   │   │   ├── useCategoryController.js
│   │   │   ├── useReportController.js
│   │   │   └── useDashboardController.js
│   │   ├── services/                        # UI Services (HTTP API Client)
│   │   │   ├── apiClient.js
│   │   │   ├── expenseService.js
│   │   │   ├── incomeService.js
│   │   │   ├── categoryService.js
│   │   │   ├── reportService.js
│   │   │   └── authService.js
│   │   ├── pages/                           # Views (Dashboard, Expenses, Incomes, Categories, Reports, Login)
│   │   ├── App.jsx                          # Root Application Component
│   │   └── index.css                        # Custom CSS Design System
│   ├── Dockerfile                           # Multi-stage build with Nginx web server
│   ├── nginx.conf                           # SPA client-side routing & /api/ reverse proxy
│   ├── package.json
│   └── vite.config.js
│
├── finforge/                                # Spring Boot 3.2 REST API Backend
│   ├── src/main/java/com/finforge/
│   │   ├── FinForgeApplication.java         # Spring Boot entry point
│   │   ├── config/
│   │   │   └── CorsConfig.java              # CORS policy for frontend
│   │   ├── controller/api/                  # Spring REST API Controllers
│   │   │   ├── BaseApiController.java
│   │   │   ├── AuthApiController.java
│   │   │   ├── ExpenseApiController.java
│   │   │   ├── IncomeApiController.java
│   │   │   ├── CategoryApiController.java
│   │   │   ├── ReportApiController.java
│   │   │   └── UserApiController.java
│   │   ├── repository/                      # Spring Data JPA Repositories
│   │   │   ├── UserRepository.java
│   │   │   ├── CategoryRepository.java
│   │   │   ├── ExpenseRepository.java
│   │   │   └── IncomeRepository.java
│   │   ├── service/                         # Business Logic Layer (@Service, @Transactional)
│   │   │   ├── CategoryService(Impl).java
│   │   │   ├── ExpenseService(Impl).java
│   │   │   ├── IncomeService(Impl).java
│   │   │   ├── ReportService(Impl).java
│   │   │   └── UserService(Impl).java
│   │   ├── model/                           # JPA Domain Entities (@Entity, @Table)
│   │   │   ├── User.java
│   │   │   ├── Category.java
│   │   │   ├── Expense.java
│   │   │   └── Income.java
│   │   ├── dto/                             # Form & payload DTOs
│   │   ├── exception/                       # Custom exception hierarchy
│   │   └── util/                            # PasswordUtil, SessionUtil, ValidationUtil
│   ├── src/main/resources/
│   │   ├── application.properties           # Server port, datasource, Hibernate configuration
│   │   └── log4j2.xml                       # Structured logging configuration
│   ├── src/test/java/com/finforge/          # JUnit 5 & Mockito test suites
│   ├── Dockerfile                           # Multi-stage backend build (Maven + Temurin JRE)
│   ├── pom.xml                              # Maven build descriptor
│   └── sql/
│       └── schema.sql                       # Database schema DDL
│
├── docker-compose.yml                       # Unified multi-service orchestration
└── README.md                                # Master project documentation
```

---

## REST API Reference

All backend endpoints are rooted at `http://localhost:8082/api`:

| Module | Method | Endpoint | Description |
|---|---|---|---|
| **Auth** | `POST` | `/api/auth/login` | Authenticate credentials & initialize session |
| | `POST` | `/api/auth/register` | Register new user account with seeded categories |
| | `POST` | `/api/auth/logout` | Invalidate active user session |
| | `GET` | `/api/auth/me` | Fetch currently authenticated user |
| **Expenses** | `GET` | `/api/expenses` | Paginated (`page`, `pageSize`) and filtered list |
| | `GET` | `/api/expenses/{id}` | Retrieve single expense by ID |
| | `POST` | `/api/expenses` | Record a new expense |
| | `PUT` | `/api/expenses/{id}` | Update existing expense entry |
| | `DELETE`| `/api/expenses/{id}` | Delete expense entry |
| **Income** | `GET` | `/api/incomes` | Paginated list of income records |
| | `GET` | `/api/incomes/{id}` | Retrieve single income record |
| | `POST` | `/api/incomes` | Record new income entry |
| | `PUT` | `/api/incomes/{id}` | Update income entry |
| | `DELETE`| `/api/incomes/{id}` | Delete income entry |
| **Categories** | `GET` | `/api/categories` | List user's custom categories |
| | `POST` | `/api/categories` | Add custom expense category |
| | `PUT` | `/api/categories/{id}` | Update category |
| | `DELETE`| `/api/categories/{id}` | Delete category |
| **Reports** | `GET` | `/api/reports/dashboard` | Aggregated metrics (total income, expense, net savings) |
| | `GET` | `/api/reports/monthly` | Monthly expense timeline aggregation |
| | `GET` | `/api/reports/categories` | Category-wise spending breakdown |
| **User** | `GET` | `/api/user/profile` | Retrieve active user profile details |
| | `PUT` | `/api/user/profile` | Update profile information |
| | `POST` | `/api/user/change-password` | Update account password |

---

## Getting Started (Local Run)

### Step 1: Run the Backend App (`finforge`)

Make sure Java 21+ and SQL Server are available, then start the Spring Boot REST API on port `8082`:

```powershell
cd finforge
.\mvnw.cmd spring-boot:run
```

The REST API will be available at: `http://localhost:8082/api`

---

### Step 2: Run the React UI (`finForgeUI`)

Open a second terminal to run the Vite development server:

```powershell
cd finForgeUI
npm install
npm run dev
```

Open your browser at: `http://localhost:5173/`

---

## 🐳 Running with Docker (One-Command Setup)

You can spin up the entire application stack (**React Frontend UI, Spring Boot Backend API, and Microsoft SQL Server Database**) with a single command:

```powershell
# In the root finForge/ folder:
docker compose up --build
```

- **Frontend Web UI**: `http://localhost` (Port 80 via Nginx)
- **Backend REST API**: `http://localhost:8082/api`
- **SQL Server DB**: `localhost:1433`

To stop the containers:
```powershell
docker compose down
```

---

## 🧪 Automated Testing (JUnit 5 & Mockito)

FinForge includes 11 automated test suites validating the service, persistence, and utility layers:

```powershell
cd finforge
.\mvnw.cmd test
```

### Test Suites Included:
- **Service Layer Tests** (`com.finforge.service`):
  - `ExpenseServiceTest.java`
  - `IncomeServiceTest.java`
  - `CategoryServiceTest.java`
  - `ReportServiceTest.java`
  - `UserServiceTest.java`
- **Persistence Layer Tests** (`com.finforge.dao`):
  - `ExpenseDAOTest.java`
  - `IncomeDAOTest.java`
  - `CategoryDAOTest.java`
  - `UserDAOTest.java`
- **Utility & Validation Tests** (`com.finforge.util`):
  - `PasswordUtilTest.java`
  - `ValidationUtilTest.java`

---

## Database Schema

The initial database schema and constraints are defined in [`finforge/sql/schema.sql`](file:///c:/Users/ancza/finForge/finforge/sql/schema.sql):
- **`users`**: Stores user credentials (salted SHA-256 hash), profile attributes, timestamps.
- **`categories`**: Stores user-scoped budget and expense categories.
- **`expenses`**: Stores expense entries with foreign keys to `users` and `categories`, indexed by date and user.
- **`incomes`**: Stores income records indexed by user and date.
