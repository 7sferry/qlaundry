# QLaundry

Laundry management system — monorepo with a React frontend and a Java/Spring Boot backend.

## Stack

- **Frontend** (`qlaundry-web/`) — React 19, TypeScript 6, Vite. Screaming + clean architecture, feature-vertical slices. See `qlaundry-web/CLAUDE.md` for commands, testing, and conventions.
- **Backend** — Java 25, Spring Boot 4.1, Maven. Clean Architecture, one Maven module per layer (`domain` → `core` → `gateway` → `web-service`) per service.
- **Gateway** (`gateway/`) — nginx (Docker). Single entry point on `:8100`: proxies `/api/*` (prefix stripped) to `user-service` and everything else to the Vite dev server, so the frontend and backend API are served from the same origin/port.

## Services

| Service | Port | Role |
|---|---|---|
| `user-service` | 8101 | Auth (JWT + refresh tokens), tenants, staff — REST API |
| `notification-service` | 8102 | Tenant-registration & OTP emails — Redis Stream consumer, **no REST API** |

There is no orders/customers/dashboard backend yet — the frontend's `withFallback` pattern covers those with mock data until services exist.

## Repository layout

```
user-service/          user-domain, user-core, user-gateway, user-web-service
notification-service/  notification-domain, notification-core, notification-gateway, notification-web-service
utils/                  identity-generator, cache-tools, token-manager, json-tools, internal-commons
qlaundry-web/           React frontend
gateway/                nginx reverse proxy (docker-compose)
```

Full backend architecture, code conventions, and the email/stream contract are documented in the root `CLAUDE.md`.

## Prerequisites

- Java 25, Maven (or use the bundled `./mvnw`)
- Bun (frontend package manager / runner)
- Docker (for the gateway)
- Local Postgres (`localhost:5432/qlaundry`, user `postgres`) and Redis (`localhost:6379`, password `12345`)
- SMTP dev server on `localhost:1025` (e.g. Mailpit/MailHog) for notification-service

## Running locally

```bash
# 1. Backend — from repo root, builds/installs all reactor modules
./mvnw install

# 2. Start each backend service (separate terminals)
cd user-service/user-web-service && ./mvnw spring-boot:run          # :8101
cd notification-service/notification-web-service && ./mvnw spring-boot:run  # :8102

# 3. Frontend (Vite dev server — internal, not exposed directly)
cd qlaundry-web && bun install && bun dev                            # :5173

# 4. Gateway (nginx via Docker) — single entry point for the browser
cd gateway && docker compose up -d                                   # :8100

# reload conf after editing nginx.conf
docker exec qlaundry-gateway nginx -s reload
```

Open the app at `http://localhost:8100` — nginx serves the frontend (proxying to Vite on `:5173`, including HMR websockets) and forwards `/api/*` to `user-service` on `:8101` via `host.docker.internal`, stripping the `/api` prefix so Spring controllers keep their existing paths (`/api/auth/staff/login` → `/auth/staff/login`). The frontend calls the backend with a relative base URL (`VITE_API_BASE_URL=/api`, see `qlaundry-web/.env`), so both are same-origin — no CORS involved at runtime.

JPA runs with `ddl-auto: update`, so tables are created automatically on first run — no migrations to apply.

## Current API surface (`user-service`, via gateway at `/api/*`)

```
POST   /api/auth/tenant/registration
POST   /api/auth/staff/registration
POST   /api/auth/staff/login
POST   /api/auth/staff/refresh
POST   /api/auth/staff/forgottenPassword
POST   /api/auth/staff/submitOtp
POST   /api/auth/staff/resetPassword
DELETE /api/auth/staff/logout
GET    /api/staff/list
GET    /api/staff/detail
```

(Controllers themselves still map `/auth/...` and `/staff/...` — the `/api` prefix exists only at the gateway.)

`notification-service` has no REST endpoints — it consumes email jobs from Redis Streams produced by `user-service` (tenant registration + forgotten-password OTP emails).
