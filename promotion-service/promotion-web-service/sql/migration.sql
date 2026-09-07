/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

-- promotion-service runs with ddl-auto: update against the `promotions` schema, so the tables below are
-- created automatically on first boot. What ddl-auto never does is create the schema itself or seed the
-- lookup rows.

CREATE SCHEMA IF NOT EXISTS promotions;

-- 1. boot promotion-web-service once so Hibernate creates the tables
--      promotions, promotion_redemptions, promotion_types
-- 2. then seed the lookup table from promotion-gateway/sql/init.sql:
--      promotion_types → PERCENTAGE (1), FIXED_AMOUNT (2), NON_CUMULATIVE_PERCENTAGE (3)
--      (id 3 used to be PERCENTAGE_WITH_MAX_AMOUNT, retired in favor of an optional maxDiscountAmount on
--       every type, and reassigned to NON_CUMULATIVE_PERCENTAGE — on a pre-existing database also run:
--       UPDATE promotion_types SET name = 'NON_CUMULATIVE_PERCENTAGE' WHERE id = 3;)
-- 3. seed the baseline internal key order-service presents on /internal/promotion/redemption:
--      redis-cli -a 12345 SET promotion:internal:key:order:v1 \
--          0ad6b70012fc50e0f74531ea48191e1c911623cc73f614f99c273265e403c5a4
--
-- promotion_types never renumbers except id 3 above (safe only pre-launch, nothing used it) — the next new
-- type after this gets id 4.
--
-- `promotions.max_discount_amount` and the new `promotions.min_subtotal` are optional on every type now,
-- not gated by type — the old isCapped()/PERCENTAGE_WITH_MAX_AMOUNT gate is gone. `promotions.combinable`
-- is a NOT NULL boolean (default true for existing rows on a pre-existing database):
--
-- ALTER TABLE promotions.promotions ADD COLUMN IF NOT EXISTS min_subtotal NUMERIC(19,2);
-- ALTER TABLE promotions.promotions ADD COLUMN IF NOT EXISTS combinable BOOLEAN NOT NULL DEFAULT true;
--
-- `promotions` carries UNIQUE (tenant_id, code) — a code belongs to one promotion per tenant FOREVER, and a
-- soft-deleted promotion keeps its code reserved. That is deliberate: an order raised under a code snapshots
-- it into `order_promotions.code` in order-service, so letting a retired code be re-created under different
-- terms would silently change what a historical invoice appears to say. The exists-checks in
-- PromotionCreateJpaGateway / PromotionUpdateJpaGateway therefore do NOT filter on `deleted` — they must
-- match the constraint exactly, or the app returns a clean 400 for some duplicates and a 500 for others.
--
-- ddl-auto: update adds the constraint to a NEW table, but will not do it reliably on a table that already
-- holds duplicate codes. On an existing database, clear the duplicates then add it by hand:
--
-- ALTER TABLE promotions.promotions
--     ADD CONSTRAINT idx_promotions_tenant_code UNIQUE (tenant_id, code);
--
-- Money columns (`promotions.amount`, `promotions.max_discount_amount`, `promotion_redemptions.subtotal` /
-- `discount_amount`) are NUMERIC(19,2) — BigDecimal in the domain, since a percentage of a subtotal produces
-- fractions that must not be rounded away mid-calculation. `promotions.percentage` is NUMERIC(5,2), BigDecimal
-- in the domain too — it only ever feeds price math, so it carries the same precision guarantee as the money
-- columns instead of a floating-point approximation. (It used to be DOUBLE PRECISION with an explicit `scale`
-- on the JPA column, which Hibernate rejects at boot with "scale has no meaning for SQL floating point types"
-- — scale/precision only apply to NUMERIC/DECIMAL columns.)
--
-- `promotions` holds no PII, so there is no encryption here and no crypto config in this service.
--
-- `promotion_redemptions` carries a UNIQUE (tenant_id, reference_id, code): the reference id is the order
-- number order-service raised the order with. `code` joined the key so several promo codes can be redeemed
-- against the same order (one row each); the pair is still what makes a retried /order/create replay each
-- code's original redemption instead of burning a second usage slot. On a pre-existing database:
--
-- ALTER TABLE promotions.promotion_redemptions
--     DROP CONSTRAINT uq_promotion_redemptions_tenant_reference,
--     ADD CONSTRAINT uq_promotion_redemptions_tenant_reference_code UNIQUE (tenant_id, reference_id, code);
--
-- The usage limit is enforced by the conditional UPDATE in PromotionJpaRepository.claimUsage, not by reading
-- used_count and writing it back — two concurrent orders racing for the last slot cannot both win it.
