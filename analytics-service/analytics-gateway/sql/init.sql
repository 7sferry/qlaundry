/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

CREATE DATABASE IF NOT EXISTS qlaundry_analytics;

CREATE TABLE IF NOT EXISTS qlaundry_analytics.orders_current (
    tenant_id             String,
    order_id              String,
    order_number          String,
    customer_id           Nullable(String),
    service_id            String,
    service_name          String,
    unit                  LowCardinality(String),
    unit_price            Decimal(19, 2),
    quantity              UInt32,
    weight_kg             Nullable(Float64),
    subtotal              Decimal(19, 2),
    discount              Decimal(19, 2),
    total_price           Decimal(19, 2),
    priority              LowCardinality(String),
    payment_method        LowCardinality(String),
    payment_status        LowCardinality(String),
    status                LowCardinality(String),
    pickup_at             DateTime64(3, 'UTC'),
    estimated_delivery_at DateTime64(3, 'UTC'),
    completed_at          Nullable(DateTime64(3, 'UTC')),
    created_at            DateTime64(3, 'UTC'),
    updated_at            DateTime64(3, 'UTC'),
    deleted               UInt8,
    version               UInt32
) ENGINE = ReplacingMergeTree(version)
PARTITION BY toYYYYMM(created_at)
ORDER BY (tenant_id, order_id);

CREATE TABLE IF NOT EXISTS qlaundry_analytics.order_items (
    tenant_id  String,
    order_id   String,
    item_id    String,
    type       LowCardinality(String),
    label      Nullable(String),
    quantity   UInt32,
    deleted    UInt8,
    version    UInt32,
    created_at DateTime64(3, 'UTC')
) ENGINE = ReplacingMergeTree(version)
ORDER BY (tenant_id, order_id, item_id);

CREATE TABLE IF NOT EXISTS qlaundry_analytics.order_promotions (
    tenant_id       String,
    order_id        String,
    promotion_id    String,
    code            String,
    discount_amount Decimal(19, 2),
    deleted         UInt8,
    version         UInt32,
    created_at      DateTime64(3, 'UTC')
) ENGINE = ReplacingMergeTree(version)
ORDER BY (tenant_id, order_id, promotion_id);

CREATE TABLE IF NOT EXISTS qlaundry_analytics.laundry_services_current (
    tenant_id          String,
    service_id         String,
    name               String,
    category           LowCardinality(String),
    unit               LowCardinality(String),
    price_per_unit     Decimal(19, 2),
    estimated_hours    UInt32,
    express_multiplier Float64,
    popular            UInt8,
    active             UInt8,
    deleted            UInt8,
    version            UInt32,
    created_at         DateTime64(3, 'UTC'),
    updated_at         DateTime64(3, 'UTC')
) ENGINE = ReplacingMergeTree(version)
ORDER BY (tenant_id, service_id);

CREATE TABLE IF NOT EXISTS qlaundry_analytics.consumed_events (
    event_id     String,
    aggregate    LowCardinality(String),
    type         LowCardinality(String),
    tenant_id    String,
    aggregate_id String,
    version      UInt32,
    consumed_at  DateTime64(3, 'UTC'),
    created_at   DateTime64(3, 'UTC')
) ENGINE = MergeTree
ORDER BY (event_id)
TTL toDateTime(created_at) + INTERVAL 30 DAY;
