# QLaundry

Laundry management system — monorepo with a React frontend and a Java/Spring Boot backend.

## Stack

- **Frontend** (`web/`) — React 19, TypeScript 6, Vite. Screaming + clean architecture, feature-vertical slices. See `web/CLAUDE.md` for commands, testing, and conventions.
- **Backend** — Java 25, Spring Boot 4.1, Maven. Clean Architecture, one Maven module per layer (`domain` → `core` → `gateway` → `web-service`) per service.
- **Gateway** (`gateway/`) — nginx (Docker). Single entry point on `:8100`, **HTTPS only**: proxies `/api/*` (prefix stripped) to the backend services and everything else to the Vite dev server, so the frontend and backend API are served from the same origin/port.

## Services

| Service | Port | Role |
|---|---|---|
| `user-service` | 8101 | Auth (JWT + refresh tokens), tenants, staff, customers — REST API |
| `notification-service` | 8102 | Tenant-registration & OTP emails — Redis Stream consumer, **no REST API** |
| `order-service` | 8103 | Laundry service price list + orders — REST API, verifies user-service's JWT; publishes analytics events to Redis streams through an outbox |
| `promotion-service` | 8104 | Per-tenant promo codes + their redemption during ordering — REST API, verifies user-service's JWT (Redis only for internal API keys) |
| `analytics-service` | 8105 | Dashboard + reports numbers — Redis Stream consumer into ClickHouse, plus `GET /analytics/dashboard` and `/analytics/report`; verifies user-service's JWT, **no Postgres, no PII** |

Every screen calls a real backend — there is no mock data left in the frontend. The dashboard and reports pages read `analytics-service` (ClickHouse, fed from order-service over `analytics:event:*` Redis streams) plus `GET /order/schedule` for today's pickups/deliveries, which stays in order-service because it shows customer names and ClickHouse holds no PII. `promotion-service` has no frontend yet.

Customers live in `user-service` (`customers` + the `customer_emails` / `customer_phones` / `customer_addresses` child tables); `order-service` only stores a `customer_id` plus the name/phone/email/address snapshot the invoice was raised with, and never reads user-service's schema. Payments are **cash only** for now.

Promo codes live in `promotion-service`: a promotion is `PERCENTAGE`, `FIXED_AMOUNT` or `NON_CUMULATIVE_PERCENTAGE`, optionally windowed by start/end dates, capped by a usage limit, and can carry an optional `maxDiscountAmount` cap, `minSubtotal` eligibility floor, and a `combinable` flag (a `combinable = false` code can only be redeemed alone). `POST /order/create` takes an optional `promoCodes` set — several codes can be stacked on one order. order-service redeems the whole set in a single call to `POST /internal/promotion/redemption`, which claims each usage slot with its own conditional `UPDATE` (so two concurrent orders can never both take the last one) and records a redemption row per code keyed by the order number *and* code — a retried order replays each code's first redemption instead of burning a second slot, and a rejected code anywhere in the batch rolls back every claim made earlier in that same call. `PERCENTAGE`/`FIXED_AMOUNT` chain their discount off whatever's left after earlier codes in the same order; `NON_CUMULATIVE_PERCENTAGE` always computes off the order's original subtotal.

An applied promotion is stored as a row in `order_promotions`, a child of `orders` — not as columns on the order. The API and the web UI are many-valued: `promoCodes` on the request, `promotions: [{promotionId, code, discountAmount}]` on the response. `orders.discount` remains the **total** discount (manual + every promo), so `total_price = subtotal − discount` holds; `order_promotions.discount_amount` says what each code granted.

Phone numbers are normalised before validation, so `0812…`, `62812…` or `+62 812…` all store as `+62812…` (Indonesia is the default dial code). user-service's active encryption key id is read from Redis (`user:encrypt:version`) on every write, so a key rotation needs no restart — see `CLAUDE.md`, "PII encryption at rest". A fresh Redis has no entry, which just falls back to `app.crypto.active-key-id` in yaml (`v1`), so seeding it is optional — do it only when rolling onto a new key without a redeploy:

```bash
redis-cli -a 12345 SET user:encrypt:version v1
```

## Repository layout

```
user-service/          user-domain, user-core, user-gateway, user-web-service, user-client
order-service/         order-domain, order-core, order-gateway, order-web-service
promotion-service/     promotion-domain, promotion-core, promotion-gateway, promotion-web-service, promotion-client
analytics-service/     analytics-domain, analytics-core, analytics-gateway, analytics-web-service, docker-compose.yml (ClickHouse)
notification-service/  notification-domain, notification-core, notification-gateway, notification-web-service
utils/                  identity-generator, cache-tools, token-manager, json-tools, internal-commons, crypto-tools,
                        pagination-tools, http-client, link-signer
qlaundry-web/           React frontend
gateway/                nginx reverse proxy (docker-compose)
```

Full backend architecture, code conventions, and the email/stream contract are documented in the root `CLAUDE.md`.

## CI

`.github/workflows/backend-ci.yml` builds the backend and runs its unit tests on every push and on pull requests into `master` (Temurin JDK 25, `./mvnw test` from the repo root). It doesn't cover `web/`.

## Prerequisites

- Java 25, Maven (or use the bundled `./mvnw`)
- Bun (frontend package manager / runner)
- Docker (for the gateway and for ClickHouse)
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
cd promotion-service/promotion-web-service && ./mvnw spring-boot:run # :8104
cd analytics-service && docker compose up -d                          # ClickHouse :8123 (http) / :9000 (native)
cd analytics-service/analytics-web-service && ./mvnw spring-boot:run # :8105

# 3. Frontend (Vite dev server — internal, not exposed directly)
cd qlaundry-web && bun install && bun dev                            # :5173

# 4. Gateway (nginx via Docker) — single entry point for the browser, HTTPS only.
#    Needs a self-signed dev cert first — nginx will not start without one:
cd gateway && mkdir -p certs
MSYS_NO_PATHCONV=1 openssl req -x509 -newkey rsa:2048 -sha256 -days 825 -nodes \
  -keyout certs/dev.key -out certs/dev.crt -subj "/CN=localhost" \
  -addext "subjectAltName=DNS:localhost,IP:127.0.0.1"     # drop MSYS_NO_PATHCONV=1 outside Git Bash

docker compose up -d                                                  # :8100 (https)

# reload conf after editing nginx.conf
docker exec qlaundry-gateway nginx -s reload
```

`gateway/certs/` is gitignored — never commit the key. Chrome will show a warning on first visit; click Advanced → Proceed.

Open the app at `https://localhost:8100` — nginx serves the frontend (proxying to Vite on `:5173`, including HMR websockets — `vite.config.ts`'s `hmrClientPort` already defaults to `8100`) and forwards `/api/*` to the backend via `host.docker.internal` (`/api/auth/`, `/api/staff/`, `/api/customer/` → user-service on `:8101`; `/api/order/`, `/api/service/`, `/api/invoice/`, `/api/public/invoice/` → order-service on `:8103`; `/api/promotion/` → promotion-service on `:8104`; `/api/analytics/` → analytics-service on `:8105`), stripping the `/api` prefix so Spring controllers keep their existing paths (`/api/auth/staff/login` → `/auth/staff/login`). The frontend calls the backend with a relative base URL (`VITE_API_BASE_URL=/api`, see `web/.env`), so both are same-origin — no CORS involved at runtime.

The gateway used to also publish a plain-HTTP `:8100` alongside a TLS `:8443`, kept only so a download manager (IDM) grabbing `application/pdf` invoice responses off the plain-HTTP socket had a TLS escape hatch (IDM cannot see inside TLS). That's gone now — `gateway/docker-compose.yml` publishes only `8100:443` — but typing `http://localhost:8100` still works and just redirects to `https://localhost:8100`: `gateway/nginx.conf` has a `stream` block in front that peeks at each connection's first bytes (`ssl_preread`, no cert needed for this) to tell a TLS handshake from a plain HTTP request line, then hands it to one of two internal, unpublished `http` servers — the real app on `127.0.0.1:8443`, or a `return 301 https://$http_host$request_uri;`-only server on `127.0.0.1:8080` for anything that isn't TLS. Both HTTP and HTTPS therefore work on the same external port, and the invoice-hijacking problem from above is avoided by default rather than being an opt-in workaround.

JPA runs with `ddl-auto: update`, so tables are created automatically on first run — no migrations to apply. Each service uses its own Postgres schema (`users`, `orders`, `promotions`, `notif`); create the schema and seed each service's lookup tables once from `*-gateway/src/main/resources/init.sql` (promotion-service's lives at `promotion-gateway/sql/init.sql`). One exception: `ddl-auto` never *alters* an existing column, so on a database created before PII encryption landed, run the widening `ALTER`s documented in each web-service's `sql/migration.sql`, then run each service once with `--spring.profiles.active=backfill` to encrypt existing rows (see `CLAUDE.md`, "PII encryption at rest").

PII columns (staff emails/phones/addresses, customer phone/email/address, email-trigger recipient/payload, the order's customer snapshot, email-notification recipient) are stored AES-256-GCM-encrypted, each service under its own keys; dev keys live in each service's `application.yaml` under `app.crypto.*` — override them via environment variables for anything shared. `promotion-service` stores no PII, so it has no crypto config at all.

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
GET    /api/order/schedule       ?date=&zone=  → today's (or that day's, in the given IANA zone — defaults to UTC) due pickups and deliveries
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

### `promotion-service`

```
POST   /api/promotion/create
GET    /api/promotion/list
GET    /api/promotion/detail   ?promotionId=
PUT    /api/promotion/update
PUT    /api/promotion/toggle   {promotionId, active}
```

Create/update/toggle are super-staff only; everything is tenant-scoped from the JWT. A promotion is one of three types — `PERCENTAGE`, `FIXED_AMOUNT`, `NON_CUMULATIVE_PERCENTAGE` — and the type decides which of `percentage` / `amount` are required; `maxDiscountAmount` and `minSubtotal` are optional on every type, and `combinable` (default `true`) gates whether a code can be stacked with others. `usageLimit` (nullable = unlimited) plus the optional `startAt` / `endAt` window are what make a code "limited"; `usedCount` and `remainingUsage` come back on every response. `/api/promotion/list` defaults to sorting by `endAt` (soonest-to-expire first).

**There is no delete endpoint.** A promotion is retired by switching it off with `PUT /api/promotion/toggle` — the request states the desired `active` value rather than flipping the current one, so it is idempotent and two staff clicking at once can't undo each other. Switching off is reversible and keeps `usedCount`, so resuming picks up where it left off instead of granting a fresh allowance. The code stays reserved either way: `(tenant_id, code)` is unique per tenant **forever**, and a code can never be re-issued under different terms, since orders that used it snapshotted the code and their invoices must keep meaning what they said.

Invoices live under `/api/invoice/*`, not `/api/order/*` — they are their own feature slice (`invoice/link` mints, `invoice/pdf` renders), still served by order-service.

**Unauthenticated endpoints are namespaced under `/public/`.** `OrderSecurityConfig` permits `/public/**` and nothing else, so an endpoint's auth posture is readable from its URL instead of from a list of exact paths in a config file — the mirror of the `/internal/` prefix that bounds service-to-service calls. The rule that falls out of it: anything mapped under `/public/` *is* public, so never route something there that needs a principal. The gateway proxies these per-resource (`/api/public/invoice/` → order-service) rather than as one blanket `/api/public/` location, so another service can own its own public surface later.

The invoice PDF is rendered with Thymeleaf + openhtmltopdf and served **inline** (`Content-Type: application/pdf`, `Content-Disposition: inline; filename="<orderNumber>.pdf"`). "View invoice" in the UI works like a presigned S3 link: the app calls the authenticated `/api/invoice/link` to mint a 1-hour HMAC-signed, tenant-scoped token, then opens a new tab straight at `/api/public/invoice/pdf?token=…`. Because that is an ordinary browser navigation — not a `fetch` into a blob and not an `<a download>` — the browser renders the PDF in its built-in viewer instead of prompting to save it. A navigation cannot carry an `Authorization` header, so the signature and its embedded expiry are the auth (`app.invoice.link.secret`, rotate it to invalidate every outstanding link at once). The tenant id travels inside the signed payload and scopes the lookup, so the unauthenticated hop is not a principal-less read. The mint endpoint deliberately stays authenticated and outside `/public/` — moving it there would let anyone forge a link for any order id.

### `analytics-service`

```
GET    /api/analytics/dashboard   ?date=&zone=       → {todayOrders, todayRevenue, monthOrders, monthRevenue, pendingOrders, inProgressOrders, readyOrders, revenueGrowth, ordersGrowth, statusDistribution}
GET    /api/analytics/report   ?period=WEEK|MONTH|QUARTER|YEAR&zone= → {period, revenueTrend, serviceBreakdown}
```

Revenue and order counts exclude cancelled orders; days, weeks and months are bucketed in whatever IANA `zone` the frontend sends (defaults to UTC when omitted — there is no hardcoded business timezone); growth compares this calendar month with the previous one (`0` when last month was empty). Poke at ClickHouse with `docker exec -it clickhouse clickhouse-client --user analytics --password 12345`. The schema is `analytics-service/analytics-gateway/sql/init.sql` and only runs on an **empty volume** — a schema change in dev is `docker compose down -v` then the backfill below.

order-service writes an `analytics_events` outbox row in the same transaction as every order/service change and `XADD`s it to `analytics:event:ORDER` or `analytics:event:LAUNDRY_SERVICE` after commit; a 5-minute sweeper republishes anything Redis missed. Each event is the full row plus its JPA ``, and ClickHouse's `ReplacingMergeTree(version)` keeps the newest, so duplicates and replays are harmless. To load orders that existed before this (or rebuild ClickHouse), run order-service once with `-Dspring-boot.run.profiles=analytics-backfill`; reconciliation queries and the DLQ runbook are in `analytics-service/analytics-gateway/sql/reconcile.md`. After a fresh install also seed `analytics_aggregates` and `analytics_event_statuses` from `order-gateway/sql/init.sql`.

### Service-to-service (not exposed through the gateway)

```
GET    /internal/customer/verification   → user-service :8101, ?customerId=&tenantId= → {customerId, tenantId, valid}
POST   /internal/promotion/redemption    → promotion-service :8104, {tenantId, codes: [...], subtotal, referenceId, customerId?, redeemedBy}
                                            → {redemptions: [{applied, message, promotionId, code, type, discountAmount, remainingUsage}, ...]}
POST   /internal/promotion/redemption/release → promotion-service :8104, {tenantId, referenceId, releasedBy}
                                            → {referenceId, released: [{promotionId, code, discountAmount}, ...]}
```

`POST /order/create` calls the first before saving whenever the request carries a `customerId`, so an order can never be raised against another tenant's customer, and the second **once per order**, not once per code — the whole `promoCodes` set is redeemed in a single call, in the order submitted, and the response carries one outcome per code. Both are authenticated with `X-Internal-Api-Key: <clientId>:<version>:<secret>` rather than a staff token — order-service holds one credential per callee (`app.internal.api-key` and `app.internal.promotion-api-key`), so rotating one never touches the other.

The redemption call is a write: it claims a usage slot per code with a single conditional `UPDATE` (guard in the `WHERE` clause, so concurrent orders can't both take the last slot) and records a `promotion_redemptions` row per code keyed by the order number under `UNIQUE (tenant_id, reference_id, code)` — a retried `/order/create` replays those redemptions instead of burning a second slot. A code that is unknown, inactive, not started, expired or exhausted comes back as `applied: false` with a message (order-service turns it into a 400), never as an HTTP error; only a transport failure is a 503. If any code in the batch is rejected, promotion-service rolls back the whole call — a code redeemed earlier in the same batch never stays claimed against an order that ultimately fails. Because it writes, it takes a `redeemedBy` staff id in the body for the audit columns — audit only, never an authorization input.

The redemption is committed in promotion-service before order-service has saved anything, so the two are a saga rather than one transaction: if any step after the redemption fails inside `/order/create` (the order insert, an item or promotion row, the optional confirm/pickup chain), order-service calls the third endpoint with the same order number as the compensating action — it soft-deletes every `promotion_redemptions` row for that reference and hands each promotion its usage slot back with a conditional `UPDATE` (`used_count > 0` guard, so it can never go negative). It's idempotent (a second call finds no live rows and releases nothing) and, like redemption, a write that carries a `releasedBy` staff id for the audit columns only. If the release itself fails, the original failure is what the caller sees, with the release failure attached as a suppressed exception and logged with the order number.

That synchronous release is only the fast path. Before it redeems anything, order-service commits a `PENDING` row in `order_promotion_sagas` (keyed by the order number, in its own transaction) and flips it to `COMMITTED` only after the order's transaction commits, or to `RELEASED` once a release succeeds. A scheduler in order-service sweeps (interval and grace period configurable via `app.promotion.saga.sweep-interval-hours` / `grace-period`, defaulting to once an hour for rows still `PENDING` 90 minutes after they were last touched): if the order exists it marks the row `COMMITTED`, otherwise it calls the release endpoint and marks it `RELEASED`. If promotion-service is down it records the attempt and error, and the row comes up again about two hours later. So an orphaned promo slot is back within roughly one to two and a half hours, not immediately. That is what recovers the cases the synchronous path can't see — order-service dying mid-request, promotion-service being down at release time, a redeem that committed but whose reply timed out, and a commit failure after the use case returned. After a fresh install, seed `order_promotion_saga_statuses` along with the other lookups in `order-gateway/sql/init.sql`. The design notes and the alternatives that were considered live in `order-service/todo/promo-saga-*.md`.

Each callee stores only `SHA-256(secret)` — never the secret — and looks it up live in Redis under its own prefix (`user:internal:key:<clientId>:<version>`, `promotion:internal:key:<clientId>:<version>`), falling back to the `*InternalKeysProperties` map in yaml only when Redis is unreachable. So a key can be added or revoked with a single `SET`/`DEL`, with no restart on either side; see "Rotating an internal key" in `CLAUDE.md` for the ordering.

Seed the baseline keys once per environment, alongside the `init.sql` lookup tables — until you do, orders that carry a `customerId` or `promoCodes` fail with 503 while plain walk-in orders work:

```bash
redis-cli -a 12345 SET user:internal:key:order:v1 \
    e24b5b12e47219f45c43ce3c999d69cd3c5478b6d2fed822cd0cf4e3ea345b5e
redis-cli -a 12345 SET promotion:internal:key:order:v1 \
    0ad6b70012fc50e0f74531ea48191e1c911623cc73f614f99c273265e403c5a4
```

The key only works under `/internal/` — user-service's filter ignores it on every other path, so a leaked key can reach nothing but the service-to-service endpoints. nginx deliberately does **not** proxy `/internal/*`, so it is reachable only from inside the network.

(Controllers themselves still map `/auth/...`, `/staff/...`, `/customer/...`, `/order/...` and `/service/...` — the `/api` prefix exists only at the gateway.)

`notification-service` has no REST endpoints — it consumes email jobs from Redis Streams produced by `user-service` (tenant registration + forgotten-password OTP emails).
