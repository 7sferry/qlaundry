# Analytics reconciliation & runbook

ClickHouse is a derived store. Postgres (order-service's `orders` schema) is the system of record, and ClickHouse is
rebuildable from it at any time through the same outbox + stream path every live write takes.

## Do the two sides agree?

Postgres (schema `orders`; `status_id = 8` is `CANCELLED`):

```sql
select tenant_id,
       count(*) filter (where status_id <> 8)            as orders,
       sum(total_price) filter (where status_id <> 8)    as revenue
from orders.orders
where deleted = false
group by tenant_id
order by tenant_id;
```

ClickHouse:

```sql
SELECT tenant_id,
       countIf(status != 'CANCELLED')              AS orders,
       sumIf(total_price, status != 'CANCELLED')   AS revenue
FROM qlaundry_analytics.orders_current FINAL
WHERE deleted = 0
GROUP BY tenant_id
ORDER BY tenant_id;
```

Same rows on both sides = in sync. Run by hand after a backfill and whenever a number looks wrong; there is no
scheduled check in v1.

### One order is behind

Find that order's newest outbox row in Postgres:

```sql
select id, type, aggregate_version, status_id, attempts, last_error, created_at
from orders.analytics_events
where aggregate_ref_id = '<order id>'
order by created_at desc
limit 5;
```

- `status_id = 2` (`PUBLISHED`) — it reached the stream, so the consumer lost it: check the DLQ below and
  `logs/analytics-web-service.log` for the event id. The consumer applies records in batches, so a batch-level
  failure is logged once with its record-id range (`records <first>..<last>`), not per event; the reclaim job then
  retries those records one by one and logs each failure with its event id.
- `status_id = 1` (`CREATED`) — not on the stream yet: the sweeper hasn't reached it (it waits 2 minutes) or Redis was
  down when it tried (`attempts` / `last_error`).

`SELECT * FROM qlaundry_analytics.consumed_events WHERE aggregate_id = '<order id>'` shows what the consumer applied
in the last 30 days.

## Backfill (replay everything)

```bash
cd order-service/order-web-service
./mvnw spring-boot:run -Dspring-boot.run.profiles=analytics-backfill
# one tenant only:
./mvnw spring-boot:run -Dspring-boot.run.profiles=analytics-backfill -Dspring-boot.run.arguments=--app.analytics.backfill.tenant-id=<tenant id>
```

It pages every `laundry_services` row, then every `orders` row (with items and promotions), 500 at a time by id, and
pushes each through `OrderAnalyticsPublisher` as `LAUNDRY_SERVICE_BACKFILLED` / `ORDER_BACKFILLED` carrying the row's
**current** `@Version`. It never writes ClickHouse directly. Rerunning it is harmless — the same versions arrive
again and `ReplacingMergeTree` keeps one. Keep analytics-service running so the stream drains as it fills
(`XLEN analytics:event:ORDER`).

## DLQ replay

A record that failed `MAX_DELIVERIES` (5) times is copied to `analytics:event:<AGGREGATE>:dlq` with
`originalRecordId`, `deliveryCount` and `lastError`, and acked on the main stream so it never blocks it.

```bash
redis-cli -a 12345 XRANGE analytics:event:ORDER:dlq - +
# fix the cause (usually ClickHouse schema drift after an ALTER), then for each entry:
redis-cli -a 12345 XADD analytics:event:ORDER '*' eventId <eventId> aggregate ORDER type <type> \
    tenantId <tenantId> aggregateId <aggregateId> version <version> occurredAt <occurredAt> payload '<payload>'
redis-cli -a 12345 XDEL analytics:event:ORDER:dlq <dlq record id>
```

Simpler when there are many: delete the DLQ entries and rerun the backfill for the affected tenant.

## ClickHouse schema change

`init.sql` only runs on an empty volume. Elsewhere:

1. `ALTER TABLE qlaundry_analytics.<table> ADD COLUMN <name> <type> DEFAULT <value>` by hand.
2. Deploy the consumer that writes the column (it must tolerate old events without the field).
3. Deploy the producer that sends the field.
4. Backfill if history needs the value.

## Full rebuild

`TRUNCATE TABLE` the five tables (or `docker compose down -v && docker compose up -d` in dev), then run the backfill.
Nothing else is needed — the consumer group and the outbox are unaffected.

## Adding a producer

A new aggregate (e.g. user-service customers) gets its own `AnalyticsAggregate` member, its own stream
`analytics:event:<AGGREGATE>`, an outbox table **in that service's own schema**, and a new `event/<aggregate>/`
feature in analytics-service. Never let a second service write to order-service's `analytics_events`.
