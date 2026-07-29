 # Splitty

![CI](https://github.com/violearamburuu/Splitty/actions/workflows/ci.yml/badge.svg)

A REST API for splitting shared expenses among groups: track who paid for what, compute everyone's balance, and get the minimal set of payments needed to settle up.

## Live demo

**Base URL:** `https://splitty-fvad.onrender.com`

> Hosted on Render's free tier, which sleeps after inactivity. The **first request may take 30–60 seconds** to wake the service — subsequent requests are fast. The free database is also time-limited, so the live demo may not be permanently available.

The API has no web frontend; interact with it using an API client (Postman, IntelliJ HTTP Client, `curl`, etc.). Public endpoints are `POST /auth/register` and `POST /auth/login`; everything else requires a JWT.

## Features

- **Groups** — create groups and manage their members, with owner/member roles.
- **Expenses with custom splits** — record an expense paid by one member and split it among the group in any amounts (not just evenly).
- **Balance calculation** — compute each member's net position (who is owed, who owes).
- **Debt simplification** — given the balances, compute the *minimal* set of transfers needed to settle everyone up, avoiding redundant back-and-forth payments.
- **Settlements** — record real payments between members to square up debts.
- **JWT authentication** — stateless auth; passwords hashed with BCrypt, protected endpoints require a bearer token.

## Tech stack

| Area | Technology |
|------|-----------|
| Language | Java 25 (LTS) |
| Framework | Spring Boot 4.1 (Web, Data JPA, Security, Validation) |
| Persistence | PostgreSQL, Hibernate (JPA) |
| Auth | JWT (jjwt), BCrypt |
| Testing | JUnit 5, Mockito |
| Build | Maven |
| Containerization | Docker, Docker Compose |
| CI | GitHub Actions |
| Deployment | Render |

## Architecture decisions

- **`BigDecimal` for all monetary values.** Money is never represented with `double` or `int`. Floating-point types can't represent decimal values exactly (`0.1 + 0.2 != 0.3`), and for an app whose entire job is splitting amounts that must sum precisely to a total, those rounding errors would break balance reconciliation. `BigDecimal` represents decimals exactly. Comparisons use `compareTo` (never `==`) to avoid scale pitfalls (`30` vs `30.00`).

- **`GroupMembership` as a join entity.** The User↔Group relationship isn't a plain many-to-many — each membership carries a **role**. Modeling it as its own entity (rather than a bare collection on `Group`) allows storing that extra data per membership, which is what makes the owner-only permission rules possible.

- **Enum for roles, not a boolean or string.** A member's role (`OWNER` / `MEMBER`) is a fixed, known set of values, so it's an `enum` — giving compile-time type safety (an invalid role can't be constructed) over a `String`, and room to extend beyond two values that a `boolean` wouldn't allow. Persisted with `@Enumerated(EnumType.STRING)` so reordering enum constants can't silently corrupt stored data.

- **DTOs to separate the API from the entities.** Controllers never expose JPA entities directly. Request/response DTOs define exactly what crosses the HTTP boundary — this keeps sensitive fields (e.g. password hashes) out of responses, prevents clients from setting fields they shouldn't, and decouples the public API from the database schema.

- **Per-group balance design.** Balances are calculated within the context of a group (`GET /groups/{id}/balances`), and the settle-up suggestion (`GET /groups/{id}/settle`) is scoped the same way. This keeps the debt model intuitive: you settle up *within* the group whose expenses created the debt.

- **Debt simplification as a computed result, not stored data.** The suggested "who pays whom" transfers are derived from the current balances on each request, never persisted — they'd go stale the moment an expense changed. Only real events (`Settlement`s) are stored. This same "don't store what you can derive" principle keeps the schema clean.

## Running locally

### Option A — Docker Compose (recommended)

Runs the app and a PostgreSQL database together in containers. Requires [Docker](https://www.docker.com/products/docker-desktop).

```bash
# Set the required secrets (JWT_SECRET must be at least 32 characters)
export DB_PASSWORD=yourpassword
export JWT_SECRET=your-long-secret-key-at-least-32-characters

docker compose up
```

The API will be available at `http://localhost:8080`.

### Option B — run against a local PostgreSQL

Requires Java 25, Maven, and a running PostgreSQL instance with a database named `splitty`.

Set the following environment variables (in your run configuration or shell):

```
DB_USERNAME=postgres
DB_PASSWORD=yourpassword
JWT_SECRET=your-long-secret-key-at-least-32-characters
```

Then:

```bash
mvn spring-boot:run
```

The app reads its datasource URL from `SPRING_DATASOURCE_URL` and falls back to `jdbc:postgresql://localhost:5432/splitty` if it isn't set.

## Testing

Unit tests cover the service layer, including the balance calculation and debt-simplification algorithm, using JUnit 5 and Mockito (repositories are mocked, so no database is required).

```bash
mvn test
```

Tests run automatically on every push and pull request via GitHub Actions.

## Example flow

1. `POST /auth/register` — create a user, receive a JWT.
2. `POST /auth/login` — log in, receive a JWT.
3. `POST /groups` — create a group (you become its owner).
4. `POST /groups/{id}/members` — add another user.
5. `POST /groups/{groupId}/expenses` — record an expense and its split.
6. `GET /groups/{groupId}/balances` — see each member's net balance.
7. `GET /groups/{groupId}/settle` — get the minimal list of payments to settle up.

All requests except register/login require an `Authorization: Bearer <token>` header.
