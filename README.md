# Loan Repayment Tracker

UCM Fall 2026 project. Three tiers:

| Tier | Folder | Tech | Runs on |
|---|---|---|---|
| Web server | `frontend/` | React 18 + Vite + React Router | http://localhost:5173 |
| Application server | `backend/` | Java 17, Spring Boot 3 (Web, Data JPA, Validation, Mail) | http://localhost:8080 |
| Database server | — | MySQL 8 | localhost:3306 |

The React app only talks to Spring Boot through the REST API in `/api/...`. Spring Boot is the only thing that talks to MySQL.

## What you need installed

- JDK 17 or newer
- Maven (or open `backend/` in IntelliJ, which has Maven built in)
- Node.js 18 or newer
- MySQL 8 (MySQL Workbench is handy for looking at the tables)
- Postman

## Running it

**1. Database.** Start MySQL. You don't need to create the schema; Spring Boot creates `loan_tracker` on first run. Put your MySQL username and password in `backend/src/main/resources/application.properties`.

**2. Application server.**
```
cd backend
mvn spring-boot:run
```
On startup it rebuilds the tables and loads `data.sql`, so every restart gives you a clean demo database.

**3. Web server.**
```
cd frontend
npm install
npm run dev
```
Open http://localhost:5173.

**4. Postman.** Import `postman/LoanRepaymentTracker.postman_collection.json` and run the collection top to bottom. Each request has tests that check the status code and key fields.

## Demo logins

| Username | Password | Who |
|---|---|---|
| admin | admin123 | Bank staff |
| jmiller | password1 | Partially repaid, monthly autopay |
| pshah | password2 | Mostly repaid, bi-weekly autopay |
| sokafor | password5 | Partially repaid, weekly autopay |
| mlee | password3 | New loan, no bank account or autopay yet |
| egarcia | password4 | Fully repaid (hidden from the admin's open-loans table) |

## REST API

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/auth/login` | Sign in; returns role, customerId, loanId |
| GET | `/api/admin/loans` | Loans not fully repaid |
| GET | `/api/admin/loans/{id}` | Full loan detail with pay-off date |
| POST | `/api/admin/loans` | Add loan, create customer account, email login |
| GET | `/api/customers/{customerId}/loan` | Customer's loan, minimum monthly payment, pay-off date |
| PUT | `/api/customers/{customerId}` | Update name, email, phone, bank details |
| GET | `/api/loans/{loanId}/minimum-payment?frequency=MONTHLY\|BIWEEKLY\|WEEKLY` | Minimum for a schedule type |
| GET | `/api/loans/{loanId}/schedules` | All schedules on a loan |
| POST | `/api/loans/{loanId}/schedules` | Create a schedule (replaces the active one) |
| DELETE | `/api/loans/{loanId}/schedules/{scheduleId}` | Cancel a schedule |

Errors always come back as `{ "error": "message" }` with a 400, 401, or 404 status.

## How the calculations work

All in `backend/.../service/AmortizationCalculator.java`, with unit tests in `src/test`.

**Minimum payment** uses the Standard Amortization Formula, `P = r·B / (1 − (1 + r)^−n)`:
- `B` is the remaining balance.
- `r` is the annual rate divided by payments per year: 12 monthly, 26 bi-weekly, 52 weekly.
- `n` is the payments left in the term: remaining months × payments per year ÷ 12, rounded up.

The result is rounded up to the cent so paying the minimum always clears the loan within the term. The backend rejects any schedule below the minimum, not just the React form.

**Pay-off date** steps through future scheduled payments: each period adds one period of interest and subtracts the payment. The date the balance reaches zero is the pay-off date.

## Decisions to confirm with the instructor

1. **Loan term.** The amortization formula needs a term, but the spec's "create loan" fields don't include one. We added an optional `termMonths` field that defaults to 120 months (`app.loan.default-term-months`).
2. **Remaining vs. original balance.** The minimum is based on the remaining balance and remaining term, which matches how a real lender would recalculate it.
3. **One active schedule per loan.** Creating a new schedule replaces the old one; the old one stays in history as inactive.
4. **Bank account required.** A customer must add a bank account before scheduling automatic payments.

## Email

`app.mail.enabled=false` by default, which prints the new-account email to the Spring Boot console instead of sending it. The admin's "Add a loan" page also shows the generated username and password. To really send email, set `app.mail.enabled=true` and fill in the `spring.mail.*` settings. Mailtrap (https://mailtrap.io) is a free test inbox that's good for demos.

## Known limitations (fine for this project, worth knowing)

- Passwords are plain text, as the spec allows.
- There are no login tokens. The React app remembers who signed in, but the API itself doesn't check who is calling, so anyone with Postman can call any endpoint. Adding Spring Security with a session or JWT would fix this if your group has time.
- Payments received are handled by an external system (per the spec), so `remaining_balance` only changes if you edit the database.

## Suggested team split

| Person | Owns |
|---|---|
| Backend 1 | Entities, repositories, login, admin endpoints (`AdminController`, `LoanService`) |
| Backend 2 | Customer + scheduling endpoints, `AmortizationCalculator`, `EmailService`, unit tests |
| Frontend 1 | Login page and admin portal (`pages/admin/`) |
| Frontend 2 | Customer portal (`pages/customer/`), Postman collection, seed data |

## Project layout

```
backend/
  src/main/java/edu/ucm/loantracker/
    config/        CORS so the React origin can call the API
    controller/    REST endpoints
    dto/           Request/response shapes (Java records)
    exception/     Error types + JSON error handler
    model/         JPA entities: AppUser, Customer, Loan, PaymentSchedule
    repository/    Spring Data JPA repositories
    service/       Business logic and calculations
  src/main/resources/
    application.properties
    data.sql       Demo data, reloaded each startup
frontend/
  src/
    api.js         Every call to the backend
    components/    App shell, route guard, repayment bar
    pages/admin/   Open loans, loan detail, add a loan
    pages/customer/ Overview + details, automatic payments
postman/
  LoanRepaymentTracker.postman_collection.json
```
