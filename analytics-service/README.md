# analytics-service

Port **8105** · ClickHouse · Redis (streams)

Serves every stat card in the web app: dashboard, reports, staff, customers, services and promotions. It consumes events that [order-service](../order-service/README.md) (orders, price list), [user-service](../user-service/README.md) (staff, customers) and [promotion-service](../promotion-service/README.md) (promotions) publish to Redis streams, stores them in ClickHouse, and answers the read endpoints below. It has no Postgres and holds no personal data.

Modules: `analytics-domain`, `analytics-core`, `analytics-gateway`, `analytics-web-service`.

## API

Paths as the controllers map them; through the gateway, prefix them with `/api`. Any staff role; scoped to the caller's tenant.

```
GET  /analytics/dashboard   ?date=yyyy-MM-dd (optional, not in the future)
     → {todayOrders, todayRevenue, monthOrders, monthRevenue, pendingOrders, inProgressOrders, readyOrders,
        revenueGrowth, ordersGrowth, statusDistribution: [{status, count}]}

GET  /analytics/report      ?period=WEEK|MONTH|QUARTER|YEAR
     → {period, revenueTrend: [{period, revenue, orders}],
        serviceBreakdown: [{serviceId, serviceName, count, revenue, percentage}]}

GET  /analytics/staff             → {totalStaff, newStaff}
GET  /analytics/customer          → {totalCustomers, newCustomers, monthSpend, averageSpend}
GET  /analytics/customer/top      → {customers: [{customerId, totalOrders, totalSpend}]}
GET  /analytics/customer/totals   ?customerIds=a&customerIds=b (max 100)
     → {totals: [{customerId, totalOrders, totalSpend, lastOrderAt}]}
GET  /analytics/promotion         → {totalPromotions, activePromotions, expiringSoon, monthRedemptions}
GET  /analytics/service           → {totalServices, activeServices, popularServices, averagePrice}
```

- **"This month"** means from the 1st, 00:00 in the tenant's time zone, until now. Counts like total staff, customers, services and promotions are tenant-wide totals as of now; "new", spend and redemption numbers cover this month.
- `averageSpend` is this month's spend divided by the customers who ordered this month. Top customers are the five biggest spenders this month.
- Customer totals are all-time and skip cancelled orders; a customer with no orders is absent.
- `activePromotions` are switched on and running right now; `expiringSoon` are switched on and end within the next 7 days.

- Revenue and order counts **exclude cancelled orders**.
- Days and months follow the **tenant's time zone** (from the login token), so all staff of one laundry see the same numbers.
- Growth compares this calendar month with the previous one, and is `0` when last month had nothing.
- `statusDistribution` covers orders created this month. `pendingOrders`, `inProgressOrders` and `readyOrders` count all open orders regardless of date.
- Report periods:
  - `WEEK`: the last 7 days, by day.
  - `MONTH`: this month, by day.
  - `QUARTER`: about three months, by week.
  - `YEAR`: the last 12 months, by month.
  - Empty buckets are returned as zero.

## How data arrives

- order-service publishes every order and price-list change to `analytics:event:ORDER` / `analytics:event:LAUNDRY_SERVICE`; user-service publishes staff and customer registrations and deletions to `analytics:event:STAFF` / `analytics:event:CUSTOMER`; promotion-service publishes promotion creates, updates and toggles to `analytics:event:PROMOTION`.
- Each event is the full current state plus a version number. ClickHouse (`ReplacingMergeTree`) keeps the newest version, so duplicates and replays are harmless.
- Events are read in batches (up to 500 per read) and written with one insert per table per batch.
- A record that keeps failing is retried on its own, and after 5 attempts it moves to a dead-letter stream (`analytics:event:<AGGREGATE>:dlq`).
- Streams are trimmed to 7 days.

Settings under `app.analytics.stream.event.`:

| Property | Default |
|---|---|
| `poll.batch-size` / `poll.timeout` | `500` / `1s` |
| `reclaim.interval-minutes` / `reclaim.min-idle` / `reclaim.max-deliveries` | `1` / `60s` / `5` |
| `trim-cron` / `retention-days` | `0 45 3 * * *` / `7` |

## Setup

```bash
cd analytics-service && docker compose up -d       # ClickHouse 25.8 — :8123 (http), :9000 (native)
docker exec -it clickhouse clickhouse-client --user analytics --password 12345    # SQL shell
```

- Connection: `localhost:8123`, user `analytics`, password `12345`, database `qlaundry_analytics`.
- The schema (`analytics-gateway/sql/init.sql`) loads automatically, but **only on an empty volume**. To apply a schema change in dev, run `docker compose down -v && docker compose up -d`, then run the analytics backfill in order-, user- and promotion-service (see `reconcile.md`). On a shared environment, apply `ALTER`s by hand before order-service starts sending a new field.
- Query conventions: [`analytics-gateway/sql/init.md`](analytics-gateway/sql/init.md).
- Reconciling with Postgres, replaying the dead-letter stream, or a full rebuild: [`analytics-gateway/sql/reconcile.md`](analytics-gateway/sql/reconcile.md).

## Tests

```bash
./mvnw test     # from this folder or the repo root
```
