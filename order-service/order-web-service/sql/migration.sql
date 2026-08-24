/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

-- order-service runs with ddl-auto: update against the `orders` schema, so the tables below are created
-- automatically on first boot. What ddl-auto never does is create the schema itself or seed the lookup rows.

CREATE SCHEMA IF NOT EXISTS orders;

-- 1. boot order-web-service once so Hibernate creates the tables
-- 2. then seed every lookup table from order-gateway/src/main/resources/init.sql:
--      order_statuses, order_priorities, payment_methods, payment_statuses,
--      clothing_types, service_units, service_categories
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
