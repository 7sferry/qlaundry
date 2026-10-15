# order-service

Port **8103** · Postgres schema `orders` · Redis (analytics events only)

Owns each laundry's price list, its orders, the daily pickup/delivery schedule and invoices. It verifies user-service's tokens (it can't issue them), calls [user-service](../user-service/README.md) and [promotion-service](../promotion-service/README.md) over `/internal/`, and feeds [analytics-service](../analytics-service/README.md) through a Redis stream.

Modules: `order-domain`, `order-core`, `order-gateway`, `order-web-service`.

## API

Paths as the controllers map them; through the gateway, prefix them with `/api`. Everything is scoped to the caller's tenant.

### Price list

```
POST   /service/create   {name, description, pricePerUnit, unit, category, estimatedHours, expressHours?, expressMultiplier?, popular}   super-staff
GET    /service/list     ?name=&category=&activeOnly=&after=&before=&sortBy=&sortDir=&pageSize=   (activeOnly defaults to true)
PUT    /service/update   {serviceId, ...same fields..., active}                                                         super-staff
DELETE /service/delete   ?serviceId=   super-staff; refused while the service still has open orders
```

Express is optional per service: send `expressHours` (1 to `estimatedHours`) and `expressMultiplier` (at least 1) together, or leave both out for a service that offers no express. Without `estimatedDeliveryAt`, an order is due `expressHours` after pickup for `EXPRESS` and `estimatedHours` otherwise.

### Orders

```
POST   /order/create            see below
GET    /order/list              ?status=&priority=&customerId=&orderNumber=&from=&to=&after=&before=&sortBy=&sortDir=&pageSize=
GET    /order/detail            ?orderId=
GET    /order/schedule          ?date=yyyy-MM-dd   → today's (or that day's) due pickups and deliveries
PATCH  /order/confirm           {orderId, staffNotes?}
PATCH  /order/pickup            {orderId, staffNotes?}
PATCH  /order/process           {orderId, staffNotes?}
PATCH  /order/ready             {orderId, staffNotes?}
PATCH  /order/deliver           {orderId, staffNotes?}
PATCH  /order/complete          {orderId, staffNotes?}
PATCH  /order/cancel            {orderId, staffNotes?}   reason goes in staffNotes
PUT    /order/payment           {orderId, paymentMethod?}   records cash received → PAID
```

`POST /order/create` body:

```
{customerId?, customerName, customerPhone, customerEmail?, customerAddress?,
 serviceId, items[], quantity, weightKg?, discount, priority, paymentMethod,
 pickupAt?, estimatedDeliveryAt?, notes?, promoCodes?, pickedUpImmediately?}
```

- **Customer**: leave `customerId` empty for a walk-in. A given `customerId` is checked with user-service and must belong to your tenant (else 404). The customer's name and contact details, and the service's name and price, are copied onto the order so the invoice never changes afterwards.
- **Price** is always computed by the server: price per unit × weight (per-kg services, `weightKg` required) or × quantity, × the express multiplier for `EXPRESS` orders (a service without express rejects them with a 400), minus `discount` and any promo codes. The total can't go below zero.
- **Payment** is cash only for now. Any other `paymentMethod` is a 400.
- **Promo codes**: `promoCodes` is a set, so several codes can be stacked. They are redeemed in one call to promotion-service. Any rejected code fails the whole order (400), and promotion-service being unreachable is a 503. Responses list what each code granted under `promotions: [{promotionId, code, discountAmount}]`, and `discount` is the total of the manual discount plus every promo.
- **`pickedUpImmediately: true`** creates the order and moves it straight to `PICKED_UP` (through `CONFIRMED`) in the same request.
- Enum values travel as their exact names (`"EXPRESS"`, `"IN_PROGRESS"`, `"BED_LINEN"`). Unknown values are a 400.

**Status workflow** (one endpoint per step, no generic "set status"):

```
PENDING → CONFIRMED → PICKED_UP → IN_PROGRESS → READY → OUT_FOR_DELIVERY → COMPLETED
           └──────────────────────▶ IN_PROGRESS          READY ─────────────▶ COMPLETED
any non-final status → CANCELLED
```

An illegal move is a 400. `COMPLETED` and `CANCELLED` are final.

**Order numbers** look like `INV-20261015-…`, dated in the tenant's time zone.

**Customer totals** (orders, spend, last order per customer) are served by [analytics-service](../analytics-service/README.md), not here.

### Invoices

```
GET    /invoice/link         ?orderId=   → {token, expiresAt}   (requires login)
GET    /public/invoice/pdf   ?token=     → application/pdf      (no login; the signed token is the auth)
```

"View invoice" works like a presigned S3 link. The app mints a 1-hour, HMAC-signed, tenant-scoped token, then opens `/api/public/invoice/pdf?token=…` in a new tab, and the browser shows the PDF inline. Rotating `app.invoice.link.secret` invalidates every outstanding link. Every unauthenticated endpoint lives under `/public/`.

## Promo redemption safety net

A promo code is claimed in promotion-service before the order is saved here. If anything fails afterwards, this service releases the claim right away. If it can't (it crashed mid-request, promotion-service was down, a reply timed out), the `order_promotion_sagas` table remembers the attempt. A scheduled sweeper (hourly by default) then either confirms the order exists or releases the codes. Settings:

| Property | Default | Meaning |
|---|---|---|
| `app.promotion.saga.sweep-interval-hours` | `1` | how often the sweeper runs |
| `app.promotion.saga.grace-period` | `90m` | how long a pending row is left alone before it is swept |
| `app.promotion.saga.sweep-batch-size` | `100` | rows per sweep |

## Analytics events

Every change to an order or a price-list entry also writes an `analytics_events` row in the same transaction, published to the `analytics:event:ORDER` / `analytics:event:LAUNDRY_SERVICE` Redis streams after commit. If Redis is down, the order still saves and a sweeper republishes the event later.

| Property | Default |
|---|---|
| `app.analytics.outbox.sweep-interval-minutes` | `5` |
| `app.analytics.outbox.grace-period` | `2m` |
| `app.analytics.outbox.sweep-batch-size` | `200` |

**Backfill** (load existing data into analytics, or rebuild it):

```bash
cd order-web-service
./mvnw spring-boot:run -Dspring-boot.run.profiles=analytics-backfill
./mvnw spring-boot:run -Dspring-boot.run.profiles=analytics-backfill \
    -Dspring-boot.run.arguments=--app.analytics.backfill.tenant-id=<tenant id>     # one tenant
```

It is safe to rerun. Reconciliation queries are in [`analytics-service/analytics-gateway/sql/reconcile.md`](../analytics-service/analytics-gateway/sql/reconcile.md).

## Setup notes

- **Seed data**: `order-gateway/sql/init.sql` (statuses, priorities, payment methods/statuses, clothing types, units, categories, saga statuses, analytics lookups). Until the analytics lookups are seeded, every order write fails.
- **Dependencies**: user-service (`:8101`) for orders with a `customerId`, and promotion-service (`:8104`) for orders with `promoCodes`. Walk-in orders without promo codes need neither.
- **Internal API keys**: `app.internal.user-service.api-key` and `app.internal.promotion-service.api-key`, each `order:<version>:<secret>`. The callees must have the matching digests in Redis (see the root README).
- **Encryption**: the customer contact snapshot on each order is AES-256-GCM-encrypted (`app.crypto.*`).
- The invoice template is `order-gateway/src/main/resources/templates/invoice/order-invoice.html`.

## Tests

```bash
./mvnw test     # from this folder or the repo root
```
