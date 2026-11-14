# AI Ticket & Subscription Backend

Spring Boot REST API using MongoDB, JWT authentication, an AI provider adapter (OmniRoute), subscriptions, payment workflow, support tickets, admin management and KPI reporting.

> This repository is a development-stage backend. The `dev` profile uses a mock payment gateway. A real production payment provider, deployment secrets, operational monitoring and concurrency hardening must be completed before processing real money.

## 1. Technology

- Java 21
- Spring Boot 4.1.1-SNAPSHOT (as declared in `pom.xml`)
- Spring MVC / Spring Security
- Spring Data MongoDB
- MongoDB
- JWT (`jjwt`)
- BCrypt password hashing
- OmniRoute-compatible chat completions API

## 2. Main architecture

```text
controller -> service -> repository -> MongoDB
                    |
                    +-> AiService -> AiProvider -> TeamAiProvider -> OmniRoute
                    |
                    +-> PaymentService -> PaymentGateway
                                            +-> MockPaymentGateway (dev)
                                            +-> UnavailablePaymentGateway (!dev until real gateway is added)
```

Main packages:

- `ai`: AI provider abstraction and chat DTOs
- `config`: Spring Security, JWT filter, password encoder and AI HTTP client
- `controller`: REST endpoints
- `dto`: request/response objects
- `exception`: application exceptions and global API error responses
- `gateway`: payment gateway contract and adapters
- `model`: MongoDB documents
- `repository`: Spring Data repositories
- `service`: business logic

## 3. Requirements

- JDK 21
- MongoDB running locally or a reachable MongoDB instance
- Maven compatible with the Spring Boot parent declared in `pom.xml`
- OmniRoute endpoint and API key for AI requests

## 4. Configuration

`src/main/resources/application.properties` contains non-secret defaults and reads sensitive values from environment variables.

Required environment variable:

- `AI_API_KEY`: API key accepted by the configured OmniRoute endpoint.

Recommended environment variable:

- `JWT_SECRET`: random secret of at least 32 bytes for HS256. Always set a unique value outside local development.
- `MONGODB_URI`: optional MongoDB connection URI; defaults to `mongodb://localhost:27017/ticket_system`.
- `AI_BASE_URL`: optional override for the OmniRoute-compatible API base URL.
- `PAYMENT_CALLBACK_URL`: callback URL sent to the configured payment gateway.

MongoDB defaults:

- Host: `localhost`
- Port: `27017`
- Database: `ticket_system`

AI base URL default:

- `https://omni.easytex.ir/v1`

Payment callback URL default:

- `http://localhost:8080/payments/callback`

The `dev` profile has a local-only JWT fallback so the existing IntelliJ development run configuration can continue to work. Do not reuse that fallback in a shared or deployed environment.

### IntelliJ development run

1. Set environment variable `AI_API_KEY` in the Run Configuration.
2. Prefer setting `JWT_SECRET` there too.
3. Add program argument `--spring.profiles.active=dev`.
4. Start MongoDB.
5. Run `DemoApplication`.

Do not commit `.env` files, API keys, JWT secrets, database credentials or production callback secrets.

## 5. Authentication and authorization

- `POST /users/register` and `POST /users/login` are public.
- Other routes require `Authorization: Bearer <JWT>`.
- `/admin/**` requires role `ADMIN`.
- Registration always assigns `USER`; a public request cannot choose its role.
- Passwords are stored with BCrypt. Existing legacy plaintext passwords are upgraded to BCrypt on the next successful login or password change.
- Password values are write-only in JSON and are not serialized in user responses.
- A user can access their own conversations, messages, AI usage, payments and tickets only.

After changing the account email, sign in again to obtain a token whose subject contains the new email.

## 6. API overview

Base URL for local development: `http://localhost:8080`

### Authentication and profile

| Method | Endpoint | Access |
|---|---|---|
| POST | `/users/register` | Public |
| POST | `/users/login` | Public |
| GET | `/users/profile` | Authenticated |
| PUT | `/users/profile` | Authenticated |
| PUT | `/users/password` | Authenticated |

Registration JSON example:

```json
{
  "username": "sample-user",
  "email": "user@example.com",
  "password": "change-me"
}
```

Login JSON example:

```json
{
  "email": "user@example.com",
  "password": "change-me"
}
```

### AI and conversations

| Method | Endpoint | Access |
|---|---|---|
| POST | `/ai/chat` | Authenticated |
| POST | `/conversations?title=...&model=...` | Authenticated |
| GET | `/conversations?page=0&size=10` | Authenticated |
| GET | `/conversations/{conversationId}` | Owner |
| POST | `/conversations/{conversationId}/messages` | Owner |
| GET | `/conversations/{conversationId}/messages?page=0&size=20` | Owner |

Message JSON example:

```json
{
  "content": "Hello"
}
```

The message endpoint records successful AI usage and now also records failed provider attempts with zero token counts and a generic error description.

### AI usage

| Method | Endpoint | Access |
|---|---|---|
| GET | `/usage?page=0&size=10` | Owner |
| GET | `/usage/conversations/{conversationId}?page=0&size=10` | Owner |
| GET | `/usage/summary` | Owner |
| GET | `/admin/usage?page=0&size=10` | Admin |
| GET | `/admin/usage?userId=...&model=...&status=SUCCESS` | Admin |
| GET | `/admin/usage/user/{userId}` | Admin |
| GET | `/admin/usage/user/{userId}/summary` | Admin |
| GET | `/admin/usage/{usageId}` | Admin |

### Plans and subscriptions

| Method | Endpoint | Access |
|---|---|---|
| POST | `/admin/plans` | Admin |
| GET | `/admin/plans` | Admin |
| GET | `/admin/plans/active` | Admin |
| GET | `/admin/plans/{id}` | Admin |
| POST | `/subscriptions/free` | Authenticated |
| POST | `/admin/subscriptions` | Admin |
| GET | `/admin/subscriptions` | Admin |
| GET | `/admin/subscriptions/user/{userId}` | Admin |

### Payments

| Method | Endpoint | Access |
|---|---|---|
| POST | `/payments` | Authenticated |
| GET | `/payments/my` | Authenticated (own payments) |
| GET | `/admin/payments` | Admin |
| GET | `/admin/payments?status=SUCCESS` | Admin |
| GET | `/admin/payments/user/{userId}` | Admin |
| GET | `/admin/payments/status/{status}` | Admin |
| GET | `/admin/payments/{paymentId}` | Admin |

Payment creation JSON example:

```json
{
  "planId": "<active-paid-plan-id>"
}
```

The mock gateway is available only in the `dev` profile:

- `GET /mock-gateway/pay?paymentId=...&reference=...`
- `POST /mock-gateway/result`

The application does not expose a general public endpoint that accepts an arbitrary `SUCCESS` status. The mock adapter verifies its reference before completing a development payment.

### Admin, tickets and KPI

| Method | Endpoint | Access |
|---|---|---|
| GET | `/admin/users` | Admin |
| DELETE | `/admin/users/{email}` | Admin |
| GET | `/admin/kpi/overview` | Admin |
| POST | `/tickets` | Authenticated |
| GET | `/tickets` | Authenticated (own tickets) |
| GET | `/tickets/{id}` | Owner |
| PUT | `/tickets/{id}` | Owner |
| DELETE | `/tickets/{id}` | Owner |
| GET | `/tickets/{id}/history` | Owner |
| GET | `/admin/tickets?page=0&size=10&search=...&status=OPEN&priority=HIGH&sortBy=createdAt&direction=desc` | Admin |
| GET | `/admin/tickets/{id}` | Admin |
| PUT | `/admin/tickets/{id}/status` | Admin |
| POST | `/admin/tickets/{id}/reply` | Admin |
| GET | `/admin/tickets/{id}/history` | Admin |

Allowed ticket sort fields: `createdAt`, `title`, `status`, `priority`. Direction must be `asc` or `desc`. Search text is treated literally (not as a regular expression) and is limited to 100 characters.

## 7. Pagination conventions

Where pagination is implemented, the default page is `0`, the default size is endpoint-specific, and the maximum size is `100`. Invalid negative page values or sizes outside `1..100` return HTTP 400.

Some legacy list endpoints still return an unpaged `List` (for example admin users, plans, subscriptions, user payments and ticket histories). They are identified as remaining scale-up work rather than being silently changed in this finalization pass, to preserve existing response contracts.

## 8. Error response format

Application errors use a common JSON structure:

```json
{
  "timestamp": "2026-11-14T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation message"
}
```

Security responses:

- `401`: missing or invalid authentication
- `403`: authenticated user lacks permission

Other common statuses:

- `400`: invalid input or validation failure
- `404`: requested resource not found
- `409`: duplicate unique value
- `500`: unexpected server error (internal exception details are not returned)

## 9. Postman smoke-test checklist

1. Register a new user; verify the response does not contain `password`.
2. Log in; save the returned token as `userToken`.
3. Call `/users/profile` with `userToken`.
4. Call `/admin/users` with `userToken`; expect `403`.
5. Call `/admin/users` without a token; expect `401`.
6. Log in as an administrator; save the token as `adminToken`.
7. Call `/admin/kpi/overview` with `adminToken`.
8. Create a conversation and send a message.
9. Verify `/usage` and `/usage/summary` for the same user.
10. Verify that a failed AI provider call creates an `AiUsage` record with status `FAILED` (test only when safely able to simulate a provider failure).
11. Create a ticket as user A; attempt to read, update and delete it as user B. All three operations must be denied.
12. Test ticket search, each filter, allowed sorting, invalid sort field, invalid direction, negative page and `size=101`.
13. Create a paid-plan payment in `dev`; complete it through the mock gateway and verify the resulting payment and subscription.
14. Verify a user cannot read another user's payment or conversation by guessing its ID.

## 10. Known limitations before production

- `UnavailablePaymentGateway` intentionally rejects payment creation outside `dev`. Integrate and test a real provider before production payment use.
- Payment callback race conditions, reconciliation and recovery of stuck `PROCESSING` payments need provider-specific idempotency and operational jobs.
- The current KPI service reads usage/payment collections into application memory; move large-scale KPI calculations to MongoDB aggregation pipelines before high-volume deployment.
- Several legacy list endpoints remain unpaginated; add pagination before the corresponding collections become large.
- There is no independent `Credit` collection; usage limits are calculated from subscriptions/plans and AI usage records.
- The current User document has no `createdAt` or `active` fields, so new-user and active-user KPI metrics are not available.
- AI provider failures are recorded from this version onward; historical failed calls that were never stored cannot be reconstructed.
- Configure HTTPS, CORS policy, rate limiting, monitoring, backups, database access control and production secret management before deployment.

## 11. Build and test

```bash
mvn clean test
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

If using IntelliJ, run `DemoApplication` with `--spring.profiles.active=dev` and the environment variables described above.
