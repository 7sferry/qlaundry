# QLaundry

Laundry management system — monorepo with a React frontend and a Java/Spring Boot backend.

## Stack

- **Frontend** (`web/`) — React 19, TypeScript 6, Vite. Screaming + clean architecture, feature-vertical slices. See `web/CLAUDE.md` for commands, testing, and conventions.
- **Backend** — Java 25, Spring Boot 4.1, Maven. Clean Architecture, one Maven module per layer (`domain` → `core` → `gateway` → `web-service`) per service.
- **Gateway** (`gateway/`) — nginx (Docker). Single entry point on `:8100`: proxies `/api/*` (prefix stripped) to the backend services and everything else to the Vite dev server, so the frontend and backend API are served from the same origin/port.

## Services

| Service | Port | Role |
|---|---|---|
| `user-service` | 8101 | Auth (JWT + refresh tokens), tenants, staff, customers — REST API |
| `notification-service` | 8102 | Tenant-registration & OTP emails — Redis Stream consumer, **no REST API** |
| `order-service` | 8103 | Laundry service price list + orders — REST API, verifies user-service's JWT (no Redis) |

There is no dashboard/reports backend yet — the dashboard is the only screen still served by the frontend's `withFallback` mock data; orders, customers, staff and auth all call the real services.

Customers live in `user-service` (`customers` + the `customer_emails` / `customer_phones` / `customer_addresses` child tables); `order-service` only stores a `customer_id` plus the name/phone/email/address snapshot the invoice was raised with, and never reads user-service's schema. Payments are **cash only** for now and there is no promo-code engine — orders take a plain manual `discount` amount.

Phone numbers are normalised before validation, so `0812…`, `62812…` or `+62 812…` all store as `+62812…` (Indonesia is the default dial code). user-service's active encryption key id is read from Redis (`user:encrypt:version`) on every write, so a key rotation needs no restart — see `CLAUDE.md`, "PII encryption at rest".

## Repository layout

```
user-service/          user-domain, user-core, user-gateway, user-web-service
order-service/         order-domain, order-core, order-gateway, order-web-service
notification-service/  notification-domain, notification-core, notification-gateway, notification-web-service
utils/                  identity-generator, cache-tools, token-manager, json-tools, internal-commons, crypto-tools
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
cd order-service/order-web-service && ./mvnw spring-boot:run         # :8103

# 3. Frontend (Vite dev server — internal, not exposed directly)
cd qlaundry-web && bun install && bun dev                            # :5173

# 4. Gateway (nginx via Docker) — single entry point for the browser
cd gateway && docker compose up -d                                   # :8100

# reload conf after editing nginx.conf
docker exec qlaundry-gateway nginx -s reload
```

Open the app at `http://localhost:8100` — nginx serves the frontend (proxying to Vite on `:5173`, including HMR websockets) and forwards `/api/*` to the backend via `host.docker.internal` (`/api/auth/`, `/api/staff/`, `/api/customer/` → user-service on `:8101`; `/api/order/`, `/api/service/` → order-service on `:8103`), stripping the `/api` prefix so Spring controllers keep their existing paths (`/api/auth/staff/login` → `/auth/staff/login`). The frontend calls the backend with a relative base URL (`VITE_API_BASE_URL=/api`, see `web/.env`), so both are same-origin — no CORS involved at runtime.

### Optional: the same gateway over TLS (`https://localhost:8443`)

The gateway also listens on `443` (published as `8443`) from the **same** nginx server block, so both schemes serve identical config. It needs a self-signed dev cert first — nginx will not start without one:

```bash
cd gateway && mkdir -p certs
MSYS_NO_PATHCONV=1 openssl req -x509 -newkey rsa:2048 -sha256 -days 825 -nodes \
  -keyout certs/dev.key -out certs/dev.crt -subj "/CN=localhost" \
  -addext "subjectAltName=DNS:localhost,IP:127.0.0.1"     # drop MSYS_NO_PATHCONV=1 outside Git Bash
```

`gateway/certs/` is gitignored — never commit the key. Chrome will show a warning; click Advanced → Proceed. Start Vite with `VITE_HMR_CLIENT_PORT=8443` so the HMR socket targets the right port (it defaults to `8100`).

This exists because **download managers hijack binary responses over plain HTTP in dev.** IDM's "advanced browser integration" reads response headers straight off the socket, so on `:8100` it sees `Content-Type: application/pdf` and grabs the invoice before Chrome can render it; it cannot see inside TLS, so on `:8443` the same URL opens in the browser's PDF viewer. Nothing is wrong with the response headers, and production terminates TLS so it never sees this. The alternative is to add `localhost` to IDM's site exceptions or drop PDF from its file-types list.

JPA runs with `ddl-auto: update`, so tables are created automatically on first run — no migrations to apply. Each service uses its own Postgres schema (`users`, `orders`, `notif`); create the schema and seed each service's lookup tables once from `*-gateway/src/main/resources/init.sql`. One exception: `ddl-auto` never *alters* an existing column, so on a database created before PII encryption landed, run the widening `ALTER`s documented in each web-service's `sql/migration.sql`, then run each service once with `--spring.profiles.active=backfill` to encrypt existing rows (see `CLAUDE.md`, "PII encryption at rest").

PII columns (staff emails/phones/addresses, customer phone/email/address, email-trigger recipient/payload, the order's customer snapshot, email-notification recipient) are stored AES-256-GCM-encrypted, each service under its own keys; dev keys live in each service's `application.yaml` under `app.crypto.*` — override them via environment variables for anything shared.

## Current API surface (via gateway at `/api/*`)

### `user-service`

```
POST   /api/auth/tenant/registration
GET    /api/auth/tenant/confirmRegistration
POST   /api/auth/tenant/resendConfirmation
POST   /api/auth/staff/registration
POST   /api/auth/staff/login
POST   /api/auth/staff/refresh
POST   /api/auth/staff/forgottenPassword
POST   /api/auth/staff/submitOtp
POST   /api/auth/staff/resetPassword
DELETE /api/auth/staff/logout
GET    /api/staff/list
GET    /api/staff/detail
DELETE /api/staff/delete
PUT    /api/staff/profile
POST   /api/customer/registration
GET    /api/customer/list
GET    /api/customer/detail
PUT    /api/customer/update
DELETE /api/customer/delete
```

### `order-service`

```
POST   /api/service/create
GET    /api/service/list
PUT    /api/service/update
DELETE /api/service/delete
POST   /api/order/create
GET    /api/order/list
GET    /api/order/detail
GET    /api/invoice/link         ?orderId=  → {token, expiresAt} (bearer token)
GET    /api/public/invoice/pdf   ?token=    → application/pdf (no bearer token — the signature is the auth)
PUT    /api/order/confirm
PUT    /api/order/pickup
PUT    /api/order/process
PUT    /api/order/ready
PUT    /api/order/deliver
PUT    /api/order/complete
PUT    /api/order/cancel
PUT    /api/order/payment
```

Every status transition has its own endpoint (body `{orderId, staffNotes?}`) — there is no generic "set status" call, so the URL states the intent. Cancelling is `PUT /api/order/cancel` with the reason in `staffNotes`. Enum values travel as their exact names (`"IN_PROGRESS"`, `"BED_LINEN"`, `"EXPRESS"`); the numeric lookup ids are internal. Each transition is also a **fully standalone feature** end to end — its own package, use case, request/response, gateway and presenter, with no shared helper between them — so any one of them can grow its own business flow without touching the other six.

Invoices live under `/api/invoice/*`, not `/api/order/*` — they are their own feature slice (`invoice/link` mints, `invoice/pdf` renders), still served by order-service.

**Unauthenticated endpoints are namespaced under `/public/`.** `OrderSecurityConfig` permits `/public/**` and nothing else, so an endpoint's auth posture is readable from its URL instead of from a list of exact paths in a config file — the mirror of the `/internal/` prefix that bounds service-to-service calls. The rule that falls out of it: anything mapped under `/public/` *is* public, so never route something there that needs a principal. The gateway proxies these per-resource (`/api/public/invoice/` → order-service) rather than as one blanket `/api/public/` location, so another service can own its own public surface later.

The invoice PDF is rendered with Thymeleaf + openhtmltopdf and served **inline** (`Content-Type: application/pdf`, `Content-Disposition: inline; filename="<orderNumber>.pdf"`). "View invoice" in the UI works like a presigned S3 link: the app calls the authenticated `/api/invoice/link` to mint a 1-hour HMAC-signed, tenant-scoped token, then opens a new tab straight at `/api/public/invoice/pdf?token=…`. Because that is an ordinary browser navigation — not a `fetch` into a blob and not an `<a download>` — the browser renders the PDF in its built-in viewer instead of prompting to save it. A navigation cannot carry an `Authorization` header, so the signature and its embedded expiry are the auth (`app.invoice.link.secret`, rotate it to invalidate every outstanding link at once). The tenant id travels inside the signed payload and scopes the lookup, so the unauthenticated hop is not a principal-less read. The mint endpoint deliberately stays authenticated and outside `/public/` — moving it there would let anyone forge a link for any order id.

### Service-to-service (not exposed through the gateway)

```
GET    /internal/customer/verification   → user-service :8101, ?customerId=&tenantId= → {customerId, tenantId, valid}
```

`POST /order/create` calls this before saving whenever the request carries a `customerId`, so an order can never be raised against another tenant's customer. It is authenticated with `X-Internal-Api-Key: <clientId>:<version>:<secret>` (order-service's `app.internal.api-key`) rather than a staff token; the calling staff's identity is not forwarded, since the endpoint only answers a tenant-scoped yes/no.

user-service stores only `SHA-256(secret)` — never the secret — and looks it up live in Redis under `user:internal:key:<clientId>:<version>`, falling back to the `UserInternalKeysProperties` map in yaml only when Redis is unreachable. So a key can be added or revoked with a single `SET`/`DEL`, with no restart on either side; see "Rotating an internal key" in `CLAUDE.md` for the ordering.

Seed the baseline key once per environment, alongside the `init.sql` lookup tables — until you do, orders that carry a `customerId` fail with 503 while walk-in orders work:

```bash
redis-cli -a 12345 SET user:internal:key:order:v1 \
    e24b5b12e47219f45c43ce3c999d69cd3c5478b6d2fed822cd0cf4e3ea345b5e
```

The key only works under `/internal/` — user-service's filter ignores it on every other path, so a leaked key can reach nothing but the service-to-service endpoints. nginx deliberately does **not** proxy `/internal/*`, so it is reachable only from inside the network.

(Controllers themselves still map `/auth/...`, `/staff/...`, `/customer/...`, `/order/...` and `/service/...` — the `/api` prefix exists only at the gateway.)

`notification-service` has no REST endpoints — it consumes email jobs from Redis Streams produced by `user-service` (tenant registration + forgotten-password OTP emails).
