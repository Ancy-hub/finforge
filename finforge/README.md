# FinForge

A full-stack **enterprise-style personal finance web application** built with pure Java EE — no Spring, no Hibernate, no Docker. Track expenses and income, filter and paginate records, and view financial summary reports, all backed by SQL Server.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Features](#features)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Database Setup](#database-setup)
- [Configuration](#configuration)
- [Build & Deploy](#build--deploy)
- [Running Tests](#running-tests)
- [URL Reference](#url-reference)
- [Security Notes](#security-notes)
- [Logging](#logging)

---

## Tech Stack

| Layer           | Technology                                     |
| --------------- | ---------------------------------------------- |
| Language        | Java 21                                        |
| Web container   | Apache Tomcat 10.x                             |
| Presentation    | JSP 3.1 · JSTL 3.0 (jakarta namespace)         |
| Controller      | Jakarta Servlet 6.0                            |
| Persistence     | Raw JDBC · SQL Server (mssql-jdbc 12.4.2)      |
| Connection pool | Tomcat DBCP2 via JNDI (`META-INF/context.xml`) |
| Logging         | Log4j 2.23.1 (API + Core)                      |
| Build           | Maven 3.x (WAR packaging)                      |
| Testing         | JUnit 5.10.1 · Mockito 5.7.0                   |
| Frontend        | Vanilla CSS (custom properties) · Vanilla JS   |

---

## Architecture

The application now supports both standard Java EE servlets and modern **Spring Boot REST API Controllers** (`/api/...`) connecting to the decoupled **React UI** (`finforge-ui-react`):

```
React UI
  └─► UI Controller (Routing vs Service Call Decision)
        └─► UI Service (API HTTP Client)
              └─► REST API Controllers (/api/...)
                    └─► ServiceImpl (Business Logic)
                          └─► DAOImpl (JDBC)
                                └─► SQL Server
```

- **REST Controllers** (`com.finforge.controller.api`) expose JSON endpoints for expenses, incomes, categories, reports, and user authentication on port 8082 with CORS support.
- **Service layer** owns all validation (via `ValidationUtil`) and business rules. It is completely decoupled from HTTP.
- **DAO layer** executes parameterised SQL statements only — no string concatenation, no SQL injection surface.
- **Models** are plain Java objects; DTOs carry raw form-field values between the API tier and the service tier.

---

## Features

### Authentication & Accounts

- User registration with unique username and email enforcement
- SHA-256 password hashing (`PasswordUtil`)
- Session-based authentication with `AuthFilter` protecting all authenticated routes
- Profile management (edit name, email, phone)
- Secure password change (current-password verification required)
- Logout invalidates the session

### Expenses

- Add, edit, delete expenses with title, description, amount, category, and date
- **Filter by date range and/or category** (dynamic SQL predicate building)
- **Paginated list** (10 records per page, SQL Server `OFFSET … FETCH NEXT`)
- **Page-total row** in the table footer showing the sum for the current page/filter
- 7 default categories seeded automatically on registration

### Income

- Add, edit, delete income records with source, amount, and date
- **Paginated list** (10 records per page)
- **Page-total row** in the table footer

### Categories

- Full CRUD for custom expense categories
- Duplicate name check per user

### Reports & Dashboard

- Dashboard card: total income, total expense, net savings (current all-time figures)
- Full report page: monthly expense breakdown and per-category expense breakdown

---

## Project Structure

```
finforge/
├── pom.xml                                   # Maven build — dependencies & plugins
├── sql/
│   └── schema.sql                            # SQL Server DDL (tables, indexes, stored proc)
└── src/
    ├── main/
    │   ├── java/com/FinForge/
    │   │   ├── dao/                          # DAO interfaces + JDBC implementations
    │   │   │   ├── CategoryDAO(Impl).java
    │   │   │   ├── ExpenseDAO(Impl).java
    │   │   │   ├── IncomeDAO(Impl).java
    │   │   │   ├── ReportDAO(Impl).java
    │   │   │   └── UserDAO(Impl).java
    │   │   ├── dto/                          # Form-field value containers
    │   │   │   ├── CategoryDTO.java
    │   │   │   ├── ExpenseDTO.java
    │   │   │   ├── ExpenseFilterDTO.java     # Date-range + category filter
    │   │   │   ├── IncomeDTO.java
    │   │   │   ├── PagedResult.java          # Generic pagination wrapper
    │   │   │   ├── ReportDTO.java
    │   │   │   ├── MonthlyReportDTO.java
    │   │   │   ├── CategoryReportDTO.java
    │   │   │   └── UserDTO.java
    │   │   ├── exception/                    # Typed checked exception hierarchy
    │   │   │   ├── FinForgeException.java  (base)
    │   │   │   ├── DAOException.java
    │   │   │   ├── ValidationException.java
    │   │   │   ├── DuplicateUserException.java
    │   │   │   ├── InvalidCredentialsException.java
    │   │   │   └── UserNotFoundException.java
    │   │   ├── filter/
    │   │   │   └── AuthFilter.java           # Redirect unauthenticated requests to /login
    │   │   ├── model/                        # Domain entities
    │   │   │   ├── Category.java
    │   │   │   ├── Expense.java
    │   │   │   ├── Income.java
    │   │   │   └── User.java
    │   │   ├── service/                      # Service interfaces + business logic impls
    │   │   │   ├── CategoryService(Impl).java
    │   │   │   ├── ExpenseService(Impl).java
    │   │   │   ├── IncomeService(Impl).java
    │   │   │   ├── ReportService(Impl).java
    │   │   │   └── UserService(Impl).java
    │   │   ├── servlet/                      # HTTP request handlers
    │   │   │   ├── CategoryServlet.java
    │   │   │   ├── ChangePasswordServlet.java
    │   │   │   ├── DashboardServlet.java
    │   │   │   ├── ExpenseServlet.java
    │   │   │   ├── IncomeServlet.java
    │   │   │   ├── LoginServlet.java
    │   │   │   ├── LogoutServlet.java
    │   │   │   ├── ProfileServlet.java
    │   │   │   ├── RegisterServlet.java
    │   │   │   └── ReportServlet.java
    │   │   └── util/
    │   │       ├── DBConnection.java         # JNDI-first, DriverManager fallback
    │   │       ├── PasswordUtil.java         # SHA-256 hashing & verification
    │   │       ├── SessionUtil.java          # Session attribute constants & helpers
    │   │       └── ValidationUtil.java       # All input validation (throws ValidationException)
    │   ├── resources/
    │   │   ├── db.properties                 # JDBC fallback config (used in tests)
    │   │   └── log4j2.xml                    # Logging config (console + rolling file)
    │   └── webapp/
    │       ├── index.jsp                     # Root redirect (dashboard or login)
    │       ├── css/style.css
    │       ├── js/main.js
    │       ├── META-INF/context.xml          # Tomcat JNDI DataSource / DBCP2 pool
    │       └── WEB-INF/
    │           ├── web.xml                   # Servlet 6.0 descriptor, AuthFilter, JNDI ref
    │           └── jsp/
    │               ├── common/navbar.jsp
    │               ├── category/             # list, add, edit
    │               ├── expense/              # list (filter + pagination), add, edit
    │               ├── income/               # list (pagination), add, edit
    │               ├── report/report.jsp
    │               ├── error/error.jsp
    │               ├── dashboard.jsp
    │               ├── login.jsp
    │               ├── register.jsp
    │               ├── profile.jsp
    │               └── change-password.jsp
    └── test/
        └── java/com/FinForge/
            ├── dao/                          # Mockito-based DAO tests
            │   ├── CategoryDAOTest.java
            │   ├── ExpenseDAOTest.java
            │   ├── IncomeDAOTest.java
            │   └── UserDAOTest.java
            ├── service/                      # Service tests with mocked DAOs
            │   ├── CategoryServiceTest.java
            │   ├── ExpenseServiceTest.java
            │   ├── IncomeServiceTest.java
            │   ├── ReportServiceTest.java
            │   └── UserServiceTest.java
            └── util/                         # Pure unit tests (no mocking needed)
                ├── PasswordUtilTest.java
                └── ValidationUtilTest.java
```

---

## Prerequisites

| Requirement   | Version              |
| ------------- | -------------------- |
| JDK           | 21+                  |
| Apache Maven  | 3.8+                 |
| Apache Tomcat | 10.1+                |
| SQL Server    | 2017+ (or Azure SQL) |

---

## Database Setup

1. Open `sql/schema.sql` in SQL Server Management Studio (or `sqlcmd`).
2. Execute the entire script. It will:
   - Create the `SmartFinForge` database (if not exists)
   - Create tables: `users`, `categories`, `expenses`, `incomes`
   - Add foreign keys, check constraints, and performance indexes
   - Create the `sp_SeedDefaultCategories` stored procedure (called on registration)

```sql
-- Quick start with sqlcmd
sqlcmd -S localhost -U sa -P YourPassword -i sql/schema.sql
```

---

## Configuration

### 1. Connection pool — `META-INF/context.xml`

Edit `src/main/webapp/META-INF/context.xml` and set your SQL Server credentials:

```xml
<Resource name="jdbc/SmartFinForge"
          ...
          url="jdbc:sqlserver://localhost:1433;databaseName=SmartFinForge;
               encrypt=true;trustServerCertificate=true"
          username="sa"
          password="YOUR_PASSWORD_HERE"
          maxTotal="20"
          maxIdle="10"
          minIdle="5" />
```

> **Production note:** do not commit real passwords. Use Tomcat's `catalina.properties` for externalised secrets, or a secrets manager.

### 2. JDBC fallback — `db.properties`

`src/main/resources/db.properties` is used by unit tests and when running outside Tomcat (JNDI not available). Update the password:

```properties
db.url=jdbc:sqlserver://localhost:1433;databaseName=SmartFinForge;encrypt=true;trustServerCertificate=true
db.username=sa
db.password=YOUR_PASSWORD_HERE
db.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver
```

---

## Build & Deploy

### Build the WAR

```bash
mvn clean package
```

The WAR is produced at `target/smart-finforge-1.0.0.war`.

### Deploy to Tomcat

**Option A — hot copy**

```bash
cp target/smart-finforge-1.0.0.war $CATALINA_HOME/webapps/finforge.war
```

**Option B — Tomcat Manager**

Upload via `http://localhost:8080/manager/html`.

### Access the application

```
http://localhost:8080/finforge/
```

The root `index.jsp` redirects to `/dashboard` (if logged in) or `/login` (if not).

---

## Running Tests

```bash
# All tests
mvn test

# Specific test class
mvn test -Dtest=PasswordUtilTest

# With coverage report (requires jacoco plugin — not included by default)
mvn verify
```

Tests do **not** require a live database. All DAO tests mock `Connection`, `PreparedStatement`, and `ResultSet`. All service tests mock the DAO interfaces.

### Test coverage summary

| Package   | Test class            | What is tested                                                              |
| --------- | --------------------- | --------------------------------------------------------------------------- |
| `dao`     | `UserDAOTest`         | save, findByUsername, updatePassword                                        |
| `dao`     | `ExpenseDAOTest`      | save, findById, findAll, update, delete                                     |
| `dao`     | `IncomeDAOTest`       | save, findById, findAll, update, delete                                     |
| `dao`     | `CategoryDAOTest`     | save, findAll, existsByName, delete                                         |
| `service` | `UserServiceTest`     | register, login, changePassword                                             |
| `service` | `ExpenseServiceTest`  | addExpense, updateExpense, deleteExpense                                    |
| `service` | `IncomeServiceTest`   | addIncome, updateIncome, deleteIncome                                       |
| `service` | `CategoryServiceTest` | addCategory, duplicate check, delete                                        |
| `service` | `ReportServiceTest`   | totals, net savings, monthly/category breakdown, DAO delegation             |
| `util`    | `PasswordUtilTest`    | hash length, determinism, avalanche effect, SHA-256 test vector, null guard |
| `util`    | `ValidationUtilTest`  | all 8 validation methods, boundary values, parameterised invalid inputs     |

---

## URL Reference

| Method   | URL                          | Description                                                     |
| -------- | ---------------------------- | --------------------------------------------------------------- |
| GET      | `/`                          | Root redirect                                                   |
| GET/POST | `/login`                     | Login form / authenticate                                       |
| GET/POST | `/register`                  | Registration form / create account                              |
| GET      | `/logout`                    | Invalidate session                                              |
| GET      | `/dashboard`                 | Financial summary                                               |
| GET      | `/expenses`                  | List expenses (supports `?fromDate=&toDate=&categoryId=&page=`) |
| GET      | `/expenses?action=add`       | Add expense form                                                |
| POST     | `/expenses?action=add`       | Save new expense                                                |
| GET      | `/expenses?action=edit&id=n` | Edit expense form                                               |
| POST     | `/expenses?action=edit`      | Update expense                                                  |
| POST     | `/expenses?action=delete`    | Delete expense                                                  |
| GET      | `/incomes`                   | List income records (supports `?page=`)                         |
| GET/POST | `/incomes?action=add`        | Add income                                                      |
| GET/POST | `/incomes?action=edit&id=n`  | Edit income                                                     |
| POST     | `/incomes?action=delete`     | Delete income                                                   |
| GET/POST | `/categories`                | Category CRUD (same pattern as above)                           |
| GET      | `/reports`                   | Full financial report                                           |
| GET/POST | `/profile`                   | View / update profile                                           |
| GET/POST | `/change-password`           | Change password                                                 |

---

## Security Notes

- **SQL injection** — every query uses `PreparedStatement` with parameterised placeholders. No string concatenation in SQL.
- **XSS** — JSPs use `<c:out>` for user-supplied content output; unescaped `${...}` is only used for safe server-set attributes.
- **CSRF** — action routing is POST-only for all mutations; delete actions use confirmation dialogs.
- **Password storage** — passwords are stored as SHA-256 hex hashes. Plain-text passwords never persist.
- **Session fixation** — `SessionUtil.invalidateSession` calls `session.invalidate()` on logout.
- **Session cookies** — `<http-only>true</http-only>` set in `web.xml`.
- **Authentication** — `AuthFilter` protects `/dashboard`, `/expenses`, `/incomes`, `/categories`, `/reports`, `/profile`, `/change-password`. Unauthenticated requests are redirected to `/login`.
- **Connection pool** — Tomcat DBCP2 with abandoned-connection recovery; credentials are not embedded in source code (use `context.xml` or externalised config).

---

## Logging

Log4j 2 is configured in `src/main/resources/log4j2.xml`.

| Logger                | Level | Destination            |
| --------------------- | ----- | ---------------------- |
| `com.finforge`  | DEBUG | Console + rolling file |
| Root (all other libs) | WARN  | Console only           |

**Log file location:** `logs/smart-finforge.log` relative to the JVM working directory (typically `$CATALINA_HOME`). Files rotate daily and at 10 MB, keeping a maximum of 30 archives.

**What gets logged:**

| Event                                                 | Level |
| ----------------------------------------------------- | ----- |
| Successful login / registration / logout              | INFO  |
| Failed login / registration / validation              | WARN  |
| Expense / income / category created, updated, deleted | INFO  |
| Filter/search queries                                 | DEBUG |
| DAO or SQL errors                                     | ERROR |
| Dashboard / report generation failures                | ERROR |

---

## License

This project is intended for educational and portfolio purposes.
