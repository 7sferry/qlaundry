# ClickHouse schema — `qlaundry_analytics`

`init.sql` is mounted into the container's `/docker-entrypoint-initdb.d/`, which the image runs **only on the first
start of an empty volume** — the same "run once" semantics as every other service's `init.sql`. There is no
`ddl-auto` for ClickHouse:

- **dev**: a schema change means `docker compose down -v && docker compose up -d` (wipes the volume), then the
  order-service analytics backfill (see `reconcile.md` and the root `CLAUDE.md`).
- **anywhere else**: `ALTER TABLE ... ADD COLUMN` by hand, *before* the producer starts sending the new field.

Poke at it with:

```bash
docker exec -it clickhouse clickhouse-client --user analytics --password 12345
```

## Tables

| Table | Engine | Sorting key | What |
| --- | --- | --- | --- |
| `orders_current` | `ReplacingMergeTree(version)`, partitioned by `toYYYYMM(created_at)` | `(tenant_id, order_id)` | one row per order, latest version wins |
| `order_items` | `ReplacingMergeTree(version)` | `(tenant_id, order_id, item_id)` | garment lines |
| `order_promotions` | `ReplacingMergeTree(version)` | `(tenant_id, order_id, promotion_id)` | applied promo codes |
| `laundry_services_current` | `ReplacingMergeTree(version)` | `(tenant_id, service_id)` | the price list, current name/category |
| `consumed_events` | `MergeTree`, 30-day TTL | `(event_id)` | the consumer's audit of what it applied — not a dedupe gate |

- `version` is the OLTP row's JPA `@Version`, carried in every event. Inserts are append-only; `ReplacingMergeTree`
  keeps the highest version per sorting key, so replays, duplicates and out-of-order events are harmless.
- `created_at` never changes for an order, so partitioning by it never splits one order's versions across
  partitions (a `ReplacingMergeTree` only dedupes inside a partition).
- Lookup values (`status`, `unit`, `priority`, …) are stored as their **enum names**, never order-service's `short`
  lookup ids.
- **No PII**: no customer name/phone/email/address, no `notes`/`staff_notes`. Rows are never deleted; order-service
  soft-deletes, so `deleted = 1` rows stay and every query filters them out.

## Query conventions

- Every read is `... FROM <table> FINAL WHERE tenant_id = {tenantId:String} AND deleted = 0`. `FINAL` returns the
  latest version before background merges run. Never `OPTIMIZE ... FINAL` from application code.
- **Bound every period query on the raw `created_at` column.** The zone-local period (a day, a month, a report
  window) is turned into a UTC instant range in the domain (`DashboardWindow`, `ReportWindow.fromInstant()` /
  `toExclusiveInstant()`) and passed as `{from:DateTime64(3, 'UTC')}` / `{to:DateTime64(3, 'UTC')}`, formatted by
  `AnalyticStore.dateTime`. A bare `created_at >= / <` predicate is what lets ClickHouse prune the monthly
  partitions; wrapping the column (`toDate(created_at, tz) >= ...`) is not guaranteed to, and without pruning a
  query reads the tenant's entire history on every dashboard load. The `tenant_id` prefix of the sorting key keeps a
  query off other tenants' rows, so cost grows with one tenant's history, never with the table's total.
- The only query that legitimately reads all of a tenant's history is the dashboard's open-work count
  (`PENDING`/`IN_PROGRESS`/`READY`) — an order from months ago can still be open. Keep it a separate, narrow query;
  don't fold it back into the period query, or the period query loses its pruning.
- Every `FINAL` read carries `SETTINGS do_not_merge_across_partitions_select_final = 1`, so `FINAL` merges each
  partition on its own and in parallel. It is safe **only because `created_at` never changes** (see above): all
  versions of an order sit in one partition. If the partition key ever moves to a mutable column, drop the setting.
- Bucketing (`toDate` / `toMonday` / `toStartOfMonth`) takes the viewer's IANA zone as a `{zone:String}` parameter,
  never a hardcoded zone. Pass it through `AnalyticStore.timeZone(ZoneId)`, which maps Java's `Z` (the UTC default)
  to `UTC` — ClickHouse's tz database has no `Z`, and it doesn't know raw offsets like `+07:00` either.
- Revenue = `sumIf(total_price, status != 'CANCELLED')`, orders = `countIf(status != 'CANCELLED')`. This differs
  from order-service's `GET /order/customer-totals`, which counts cancelled orders on purpose — don't align them.
- Parameterised queries only (`{name:Type}` server-side parameters), never string concatenation of a value. The
  only concatenated fragment is the bucket expression, chosen from a closed enum in the gateway (the zone inside it
  is still a `{zone:String}` parameter).

## Verify the idempotency argument by hand

```sql
INSERT INTO qlaundry_analytics.orders_current VALUES ('t1','o1','INV-1',NULL,'s1','Wash','KG',8000,1,2.5,20000,0,20000,'NORMAL','CASH','UNPAID','PENDING',now64(3),now64(3),NULL,now64(3),now64(3),0,1);
INSERT INTO qlaundry_analytics.orders_current VALUES ('t1','o1','INV-1',NULL,'s1','Wash','KG',8000,1,2.5,20000,0,20000,'NORMAL','CASH','UNPAID','PENDING',now64(3),now64(3),NULL,now64(3),now64(3),0,1);
INSERT INTO qlaundry_analytics.orders_current VALUES ('t1','o1','INV-1',NULL,'s1','Wash','KG',8000,1,2.5,20000,0,20000,'NORMAL','CASH','UNPAID','CONFIRMED',now64(3),now64(3),NULL,now64(3),now64(3),0,2);
SELECT count(), any(status) FROM qlaundry_analytics.orders_current FINAL;  -- 1, CONFIRMED
TRUNCATE TABLE qlaundry_analytics.orders_current;
```
