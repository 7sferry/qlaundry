/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

-- order-service runs with ddl-auto: update against the `orders` schema, so the tables below are created
-- automatically on first boot. What ddl-auto never does is create the schema itself or seed the lookup rows.

CREATE SCHEMA IF NOT EXISTS orders;

-- 1. boot order-web-service once so Hibernate creates the tables
-- 2. then seed every lookup table from order-gateway/sql/init.sql:
--      order_statuses, order_priorities, payment_methods, payment_statuses,
--      clothing_types, service_units, service_categories, order_promotion_saga_statuses,
--      analytics_aggregates, analytics_event_statuses
--
-- An existing database only needs the analytics outbox seed: boot once so ddl-auto creates analytics_events,
-- analytics_aggregates and analytics_event_statuses, then run the four analytics_* inserts from init.sql.
-- Until they exist, every write that emits an analytics event (order create, the seven status transitions,
-- payment, service create/update/delete) fails at the outbox insert (FK to aggregate/status) and rolls back.
-- analytics_events.payload is NOT encrypted: it carries no PII by construction (no customer name/phone/email/
-- address, no notes) — see "analytics-service" in the root CLAUDE.md.
--
-- An existing database only needs the saga seed: boot once so ddl-auto creates order_promotion_sagas and
-- order_promotion_saga_statuses, then run the three order_promotion_saga_statuses inserts from init.sql.
-- Until they exist, every /order/create carrying promoCodes fails at the saga insert (FK to status 1) before
-- any promotion is redeemed, so nothing is claimed; walk-in orders without promo codes are unaffected.
--
-- payment_methods deliberately holds only CASH (id 1) for now. When transfer/QRIS land, insert them with the
-- ids reserved in com.ferry.order.domain.order.PaymentMethod's comment and add the enum members — never
-- renumber existing rows, `orders.payment_method_id` points at them.

-- Money columns (`laundry_services.price_per_unit`, `orders.unit_price` / `subtotal` / `discount` /
-- `total_price`) are NUMERIC(19,2) — BigDecimal in the domain, because a percentage discount produces
-- fractions that must not be rounded away mid-calculation even though IDR has no minor unit today.
-- Measured values (`orders.weight_kg`, `laundry_services.express_multiplier`) are DOUBLE PRECISION: a scale
-- reading and a factor do not need exact decimal arithmetic, the domain just pins them to 2 decimals.
-- PII columns (`orders.customer_phone`, `customer_email`, `customer_address`) are AES-GCM ciphertext in the
-- `<keyId>:<base64url(nonce||ciphertext||tag)>` wire format, bound to the order row id as AAD, and the columns
-- are sized for ciphertext from the start.
--
-- There is no encryption backfill and no migration window here: order-service is encrypted from its first
-- boot, so it runs with app.crypto.allow-plaintext-read = false and every row has a key-id prefix.

-- Promotions applied to an order live in their own child table, `order_promotions` — NOT as columns on
-- `orders`. The API accepts a `promoCodes` list per order now (stacking), and the storage was many-to-one
-- from the start, so the presenters just started returning the child list instead of flattening it to one
-- row. Same parent+child shape as `order_items`, and the same reasoning as `customer_emails` /
-- `customer_phones` in user-service.
--
-- ddl-auto: update creates `order_promotions` on the next boot. It does NOT drop columns, so a database
-- created during the brief window when the promo fields lived on `orders` needs them backfilled and dropped
-- by hand:
--
-- INSERT INTO orders.order_promotions
--     (id, order_id, promotion_id, code, discount_amount, version, deleted, created_by, created_at, updated_by, updated_at)
-- SELECT gen_random_uuid()::text, o.id, o.promotion_id, o.promotion_code, o.promotion_discount,
--        0, false, o.created_by, o.created_at, o.updated_by, o.updated_at
--   FROM orders.orders o
--  WHERE o.promotion_id IS NOT NULL;
--
-- ALTER TABLE orders.orders DROP COLUMN IF EXISTS promotion_id;
-- ALTER TABLE orders.orders DROP COLUMN IF EXISTS promotion_code;
-- ALTER TABLE orders.orders DROP COLUMN IF EXISTS promotion_discount;
--
-- (the backfilled ids are placeholders — every id this service writes at runtime is a ULID from IdGenerator.)
--
-- `orders.discount` stays the TOTAL discount granted (manual + every promotion) so `total_price = subtotal -
-- discount` holds unchanged; `order_promotions.discount_amount` records what each code granted, and the sum
-- of those rows is the promo share of `orders.discount`. `order_promotions.promotion_id` points at a row in
-- promotion-service's own schema and is a plain varchar, never a foreign key — same policy as `tenant_id`
-- and `customer_id`. `code` is a snapshot: promotion-service may rename or soft-delete the promotion later,
-- and the invoice must keep what it was raised with.
