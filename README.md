# QLaundry

A multi-tenant laundry management system: each laundry business (a *tenant*) manages its staff, customers, price list, orders, invoices, promo codes and business reports from one web app. Monorepo with a React frontend, five Java/Spring Boot services and an nginx gateway. The UI is in Indonesian.

## Features

- **Tenant onboarding** — a laundry registers itself (with a Cloudflare Turnstile captcha), confirms by email, and its first account becomes the super-staff. Unconfirmed registrations expire after 30 days.
- **Staff accounts** — `STAFF` and `SUPER_STAFF` roles, self-service profile, password reset by emailed OTP, and no password reuse within 90 days.
- **Customers** — a per-tenant customer book with phone/email/address. Phone numbers are normalised (`0812…`, `62812…`, `+62 812…` are the same number).
- **Price list** — laundry services priced per kg or per unit, with categories, estimated hours and an optional express option (its own hours and price multiplier).
- **Machines** — the laundry's washers, dryers and irons. Staff pick the machines an order runs on when they start processing it and can swap them while it is in progress; a machine can't be on two in-progress orders at once.
- **Orders** — walk-in or registered customers, itemised clothing, express priority, pickup and delivery times, a status workflow (confirm → pickup → process → ready → deliver → complete, or cancel), and cash payment.
- **Daily schedule** — today's due pickups and deliveries.
- **Invoices** — a PDF per order, opened in the browser through a 1-hour signed link.
- **Promo codes** — percentage or fixed-amount promotions with date windows, usage limits, discount caps, minimum subtotals and stacking rules. Several codes can be stacked on one order, and a live preview shows the discount before the order is saved.
- **Dashboard & reports** — today/month revenue and order counts, growth versus last month, status distribution, revenue trends by week/month/quarter/year, and a per-service breakdown, all in the tenant's own time zone.
- **Tenant settings** — business name, description and time zone.
- **Privacy** — customer and staff contact details are encrypted at rest (AES-256-GCM), and no personal data reaches the analytics store.

## Tech stack

| Layer | Technology |
|---|---|
| Frontend (`web/`) | React 19, TypeScript 6, Vite, Bun, React Router 7, Recharts |
| Backend | Java 25, Spring Boot 4.1 (Web MVC, Security, Data JPA, Data Redis), Jackson 3, Lombok, Maven |
| Architecture | Clean Architecture: one Maven module per layer (`domain` → `core` → `gateway` → `web-service`) per service |
| Databases | PostgreSQL (one schema per service), ClickHouse 25.8 (analytics) |
| Messaging & cache | Redis: cache, sessions, OTPs, and Redis Streams between services (transactional outbox) |
| Service-to-service | Spring HTTP interface clients, authenticated with per-caller API keys |
| Auth | Ed25519-signed JWT access tokens + rotating refresh tokens, Argon2 password hashing (`password4j`) |
| Documents & email | Thymeleaf, openhtmltopdf + jsoup (invoice PDF), Spring Mail (SMTP) |
| Gateway | nginx in Docker, HTTPS-only on a single origin |
| Tests & CI | JUnit 5, Mockito, AssertJ; GitHub Actions |

## Services

| Service | Port | Responsibility | Store | Docs |
|---|---|---|---|---|
| `user-service` | 8101 | Auth, tenants, staff, customers | Postgres `users`, Redis | [README](user-service/README.md) |
| `notification-service` | 8102 | Registration and OTP emails (stream consumer, no REST API) | Postgres `notif`, Redis | [README](notification-service/README.md) |
| `order-service` | 8103 | Price list, machines, orders, schedule, invoices | Postgres `orders`, Redis | [README](order-service/README.md) |
| `promotion-service` | 8104 | Promo codes and their redemption | Postgres `promotions`, Redis | [README](promotion-service/README.md) |
| `analytics-service` | 8105 | Every stat card: dashboard, reports, staff, customers, services, promotions | ClickHouse, Redis | [README](analytics-service/README.md) |

```
Browser ──https──▶ nginx gateway :8100 ──/api/*──▶ user / order / promotion / analytics services
                                      └──/*─────▶ Vite dev server :5173

user-service ──Redis Stream──▶ notification-service ──SMTP──▶ email
order / user / promotion-service ──Redis Streams──▶ analytics-service ──▶ ClickHouse
order-service ──HTTP /internal──▶ user-service (customer check), promotion-service (redeem / release)
```

Every screen calls a real backend. The frontend is documented in [`web/README.md`](web/README.md), and the system design in [`ARCHITECTURE.md`](ARCHITECTURE.md).

## Repository layout

```
user-service/          user-domain, user-core, user-gateway, user-web-service, user-client
order-service/         order-domain, order-core, order-gateway, order-web-service
promotion-service/     promotion-domain, promotion-core, promotion-gateway, promotion-web-service, promotion-client
notification-service/  notification-domain, notification-core, notification-gateway, notification-web-service
analytics-service/     analytics-domain, analytics-core, analytics-gateway, analytics-web-service, docker-compose.yml (ClickHouse)
utils/                 identity-generator, cache-tools, token-manager, json-tools, internal-commons, crypto-tools,
                       pagination-tools, http-client, link-signer
web/                   React frontend
gateway/               nginx reverse proxy (docker-compose)
```

## Getting started

### Prerequisites

- Java 25 (Maven is bundled as `./mvnw`)
- Bun
- Docker (gateway and ClickHouse)
- PostgreSQL on `localhost:5432`, database `qlaundry`, user `postgres`
- Redis on `localhost:6379`, password `12345`
- An SMTP catcher on `localhost:1025`, e.g. Mailpit (needed to receive the registration confirmation email)
- OpenSSL (dev certificate)

### 1. Database and Redis

Create the database and one schema per service:

```sql
CREATE DATABASE qlaundry;
\c qlaundry
CREATE SCHEMA users;
CREATE SCHEMA orders;
CREATE SCHEMA promotions;
CREATE SCHEMA notif;
```

Tables are created by JPA (`ddl-auto: update`) the first time each service starts.

Seed the service-to-service API keys (dev values; until they exist, orders that carry a customer or promo code fail with 503):

```bash
redis-cli -a 12345 SET user:internal:key:order:v1 \
    e24b5b12e47219f45c43ce3c999d69cd3c5478b6d2fed822cd0cf4e3ea345b5e
redis-cli -a 12345 SET promotion:internal:key:order:v1 \
    0ad6b70012fc50e0f74531ea48191e1c911623cc73f614f99c273265e403c5a4
```

### 2. ClickHouse

```bash
cd analytics-service && docker compose up -d      # :8123 (http), :9000 (native); schema loads on first start
```

### 3. Backend

```bash
./mvnw install                                                              # from the repo root: all modules

cd user-service/user-web-service && ./mvnw spring-boot:run                  # :8101
cd notification-service/notification-web-service && ./mvnw spring-boot:run  # :8102
cd order-service/order-web-service && ./mvnw spring-boot:run                # :8103
cd promotion-service/promotion-web-service && ./mvnw spring-boot:run        # :8104
cd analytics-service/analytics-web-service && ./mvnw spring-boot:run        # :8105
```

Run each service in its own terminal.

### 4. Seed the lookup tables

Once every service has started once (so its tables exist), run each seed file against its schema:

```bash
psql -U postgres -d qlaundry -c "SET search_path TO users"      -f user-service/user-gateway/src/main/resources/init.sql
psql -U postgres -d qlaundry -c "SET search_path TO notif"      -f notification-service/notification-gateway/src/main/resources/init.sql
psql -U postgres -d qlaundry -c "SET search_path TO orders"     -f order-service/order-gateway/sql/init.sql
psql -U postgres -d qlaundry -c "SET search_path TO promotions" -f promotion-service/promotion-gateway/sql/init.sql
```

`psql` runs each `-c`/`-f` in the same session, so the `search_path` applies to the file. Seed once per database; the ids are fixed.

### 5. Frontend

```bash
cd web && bun install && bun dev                  # :5173, reached through the gateway
```

### 6. Gateway

nginx needs a self-signed certificate before it will start:

```bash
cd gateway && mkdir -p certs
MSYS_NO_PATHCONV=1 openssl req -x509 -newkey rsa:2048 -sha256 -days 825 -nodes \
  -keyout certs/dev.key -out certs/dev.crt -subj "/CN=localhost" \
  -addext "subjectAltName=DNS:localhost,IP:127.0.0.1"      # drop MSYS_NO_PATHCONV=1 outside Git Bash
docker compose up -d                                       # :8100 (https)

docker exec qlaundry-gateway nginx -s reload               # after editing nginx.conf
```

`gateway/certs/` is gitignored, so never commit the key. Chrome warns on the first visit (Advanced → Proceed).

### 7. Open the app

Go to **https://localhost:8100** (`http://` redirects there). Register a laundry, open the confirmation email in Mailpit, click the link, then log in.

nginx serves the frontend (including Vite HMR) and forwards `/api/*` to the owning service with the `/api` prefix stripped (`/api/auth/staff/login` → `/auth/staff/login`), so the browser sees a single origin and there is no CORS. The frontend's base URL is `VITE_API_BASE_URL=/api` in `web/.env`. `/internal/*` endpoints are never proxied.

### Configuration

Every service runs out of the box with dev values in its `application.yaml`: database and Redis credentials, JWT keys, encryption keys (`app.crypto.*`), internal API keys (`app.internal.*`), the invoice link secret and the Turnstile secret. Override them with environment variables (e.g. `APP_CRYPTO_KEYS_V1`, `APP_INTERNAL_USERSERVICE_APIKEY`) for anything shared, and regenerate the internal-key digests above for any real environment. Service-specific settings and runbooks (key rotation, backfills, analytics reconciliation) are in each service's README.

## Tests and CI

```bash
./mvnw test                     # backend unit tests, from the repo root
cd web && bun lint && bun run build
```

`.github/workflows/backend-ci.yml` runs `./mvnw test` (Temurin JDK 25) on every push and on pull requests into `master`. The frontend isn't covered by CI.
