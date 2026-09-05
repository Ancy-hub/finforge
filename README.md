# FinForge

An enterprise-grade **personal finance management platform** featuring a decoupled **React UI** architecture communicating with a **Spring Boot REST API** backend.

---

## Architecture Flow

The system follows a strict, decoupled flow:

```
┌────────────────────────────────────────────────────────┐
│                   User Interaction                     │
└──────────────────────────┬─────────────────────────────┘
                           │ Interacts with UI
                           ▼
┌────────────────────────────────────────────────────────┐
│                   React UI Views                       │
│    (Dashboard, Expenses, Incomes, Categories, Reports) │
└──────────────────────────┬─────────────────────────────┘
                           │ Dispatches Action
                           ▼
┌────────────────────────────────────────────────────────┐
│               Controller in UI                         │
│           (useAppFlowController, etc.)                 │
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
                                           │ Points to REST API
                                           ▼
┌────────────────────────────────────────────────────────┐
│              Backend App (finforge :8082)              │
│       Spring Boot REST API Endpoints (/api/...)        │
├────────────────────────────────────────────────────────┤
│                     Service Layer                      │
├────────────────────────────────────────────────────────┤
│                       DAO Layer                        │
├────────────────────────────────────────────────────────┤
│                 SQL Server Database                    │
└────────────────────────────────────────────────────────┘
```

1. **User interacts with React UI**: Built using React, Vite, and Vanilla CSS with modern dark luxe styling, glassmorphism, and responsive layouts.
2. **Controller in UI decides**:
   - **Which page or modal to display** (e.g., navigation between Dashboard, Expenses, Incomes, Categories, Analytics, opening/closing Add/Edit modals, tab switching), **OR**
   - **Calls the Service in UI** when data persistence or retrieval is required.
3. **Service in UI**:
   - Centralized API client (`apiClient.js`) and domain services (`expenseService`, `incomeService`, `categoryService`, `reportService`, `authService`) that point directly to the backend API endpoints.
4. **Backend App (`finforge`)**:
   - Spring Boot REST API controllers exposing endpoints for expenses, incomes, categories, reporting, and authentication.

---

## Project Structure

```
finForge/
├── finforge-ui-react/               # React UI Frontend Application
│   ├── src/
│   │   ├── components/              # UI Components (Navbar, FlowBanner, Modals, Toasts)
│   │   ├── controllers/             # UI Controllers (Flow, Routing & Service Delegation)
│   │   │   ├── useAppFlowController.js
│   │   │   ├── useExpenseController.js
│   │   │   ├── useIncomeController.js
│   │   │   ├── useCategoryController.js
│   │   │   ├── useReportController.js
│   │   │   └── useDashboardController.js
│   │   ├── services/                # UI Services (Points to Backend API endpoints)
│   │   │   ├── apiClient.js
│   │   │   ├── expenseService.js
│   │   │   ├── incomeService.js
│   │   │   ├── categoryService.js
│   │   │   ├── reportService.js
│   │   │   └── authService.js
│   │   ├── pages/                   # Main Views (Dashboard, Expenses, Incomes, Categories, Reports, Login)
│   │   ├── App.jsx                  # Main Root Component
│   │   └── index.css                # Custom CSS Design System
│   ├── package.json
│   └── vite.config.js               # Vite config with /api reverse proxy
│
├── finforge/                        # Backend REST API Service
│   ├── src/main/java/com/finforge/
│   │   ├── config/
│   │   │   └── CorsConfig.java      # CORS policy for frontend client
│   │   ├── controller/api/          # Spring Boot REST API Controllers
│   │   │   ├── AuthApiController.java
│   │   │   ├── ExpenseApiController.java
│   │   │   ├── IncomeApiController.java
│   │   │   ├── CategoryApiController.java
│   │   │   ├── ReportApiController.java
│   │   │   └── UserApiController.java
│   │   ├── dao/                     # JDBC DAOs
│   │   ├── dto/                     # Form and payload DTOs
│   │   ├── model/                   # Domain entities
│   │   ├── service/                 # Business logic services
│   │   └── util/                    # DBConnection, PasswordUtil, SessionUtil
│   ├── src/main/resources/
│   │   ├── application.properties   # server.port=8082 & DB config
│   │   └── db.properties            # JDBC fallback configuration
    └── pom.xml
```

---

## REST API Reference

All backend API endpoints are rooted at `http://localhost:8082/api`:

| Module | Method | Endpoint | Description |
|---|---|---|---|
| **Auth** | `POST` | `/api/auth/login` | Authenticate credentials & initialize session |
| | `POST` | `/api/auth/register` | Register new user account |
| | `POST` | `/api/auth/logout` | Invalidate active user session |
| | `GET` | `/api/auth/me` | Fetch currently authenticated user |
| **Expenses** | `GET` | `/api/expenses` | Paginated & filtered list of expenses |
| | `GET` | `/api/expenses/{id}` | Retrieve single expense by ID |
| | `POST` | `/api/expenses` | Record a new expense |
| | `PUT` | `/api/expenses/{id}` | Update existing expense |
| | `DELETE`| `/api/expenses/{id}` | Delete expense entry |
| **Income** | `GET` | `/api/incomes` | Paginated list of income records |
| | `GET` | `/api/incomes/{id}` | Retrieve single income record |
| | `POST` | `/api/incomes` | Record new income entry |
| | `PUT` | `/api/incomes/{id}` | Update income entry |
| | `DELETE`| `/api/incomes/{id}` | Delete income entry |
| **Categories** | `GET` | `/api/categories` | List all user categories |
| | `POST` | `/api/categories` | Add custom expense category |
| | `PUT` | `/api/categories/{id}` | Update category |
| | `DELETE`| `/api/categories/{id}` | Delete category |
| **Reports** | `GET` | `/api/reports/dashboard` | Aggregated metrics (income, expense, net savings) |
| | `GET` | `/api/reports/monthly` | Monthly expense timeline |
| | `GET` | `/api/reports/categories` | Category-wise spending distribution |

---

## Getting Started

### Prerequisites
- **Node.js** v18+ and **npm**
- **Java JDK 21**
- **Maven** (bundled Maven wrapper `mvnw.cmd` included)

---

### Step 1: Run the Backend App (`finforge`)

Open a terminal and start the Spring Boot REST API on port `8082`:

```powershell
cd c:\Users\ancza\Downloads\finForge\finforge
.\mvnw.cmd spring-boot:run -DskipTests
```

The REST API will be available at:
`http://localhost:8082/api`

---

### Step 2: Run the React UI (`finforge-ui-react`)

Open a second terminal to run the Vite development server:

```powershell
cd c:\Users\ancza\Downloads\finForge\finforge-ui-react
npm install
npm run dev
```

Open your browser at:
`http://localhost:5173/`

---

## Technologies Used

- **Frontend**: React 18, Vite 5, Lucide React icons, Vanilla CSS (CSS Variables, Flexbox, CSS Grid, Glassmorphism).
- **Backend**: Spring Boot 3.2, Jakarta Servlet 6.0, JDBC (MSSQL Driver), Apache DBCP2.
- **Architecture**: Separated UI Controllers & UI Services communicating with a RESTful Backend API.
