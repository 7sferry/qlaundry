/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

CREATE TABLE email_trigger_types
(
    id   SMALLINT PRIMARY KEY,
    name VARCHAR(25) NOT NULL
);

CREATE TABLE email_trigger_statuses
(
    id   SMALLINT PRIMARY KEY,
    name VARCHAR(25) NOT NULL
);

CREATE TABLE email_triggers
(
    id         VARCHAR(50) PRIMARY KEY,
    type_id    SMALLINT     NOT NULL REFERENCES email_trigger_types (id),
    recipient  VARCHAR(100) NOT NULL,
    payload    TEXT         NOT NULL,
    status_id  SMALLINT     NOT NULL REFERENCES email_trigger_statuses (id),
    version    INTEGER,
    deleted    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_by VARCHAR(50)  NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL,
    updated_by VARCHAR(50)  NOT NULL,
    updated_at TIMESTAMPTZ  NOT NULL
);

CREATE TABLE tenant_statuses
(
    id   SMALLINT PRIMARY KEY,
    name VARCHAR(25) NOT NULL
);

ALTER TABLE tenants
    ADD COLUMN status_id SMALLINT NOT NULL DEFAULT 1 REFERENCES tenant_statuses (id);

ALTER TABLE tenants
    ADD COLUMN username VARCHAR(50) UNIQUE;

CREATE TABLE staff_passwords
(
    id         VARCHAR(50) PRIMARY KEY,
    staff_id   VARCHAR(50) NOT NULL REFERENCES staffs (id),
    password   VARCHAR     NOT NULL,
    version    INTEGER,
    deleted    BOOLEAN     NOT NULL DEFAULT FALSE,
    created_by VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

-- backfill one history row per existing staff from their current password before dropping the column, e.g.:
-- INSERT INTO staff_passwords (id, staff_id, password, created_by, created_at, updated_by, updated_at)
-- SELECT '<ulid>', id, password, 'system', NOW(), 'system', NOW() FROM staffs WHERE password IS NOT NULL;
ALTER TABLE staffs
    DROP COLUMN password;

-- encrypt PII at rest (application-level AES-256-GCM, done in the gateway ring):
-- ciphertext is <keyId>:<base64url(nonce || ciphertext || tag)>, bound to AAD <table>:<column>:<staff_id>
-- (email_triggers columns bind to the row's own id instead — no staff parent);
-- a ciphertext moved to another row/column/table can no longer be decrypted.
-- ddl-auto: update never alters an existing column's type, so widen by hand:
ALTER TABLE staff_emails    ALTER COLUMN email        TYPE VARCHAR(512);
ALTER TABLE staff_phones    ALTER COLUMN phone        TYPE VARCHAR(128);
ALTER TABLE staff_addresses ALTER COLUMN address_line TYPE TEXT;
ALTER TABLE email_triggers  ALTER COLUMN recipient    TYPE VARCHAR(512);

-- blind indexes (HMAC-SHA256 of the normalized value, lowercase hex) so lookup-by-email/phone
-- stays possible — GCM ciphertext can never be matched by a JPQL predicate:
ALTER TABLE staff_emails ADD COLUMN email_hash VARCHAR(64);
ALTER TABLE staff_phones ADD COLUMN phone_hash VARCHAR(64);
CREATE INDEX idx_staff_emails_email_hash ON staff_emails (email_hash);
CREATE INDEX idx_staff_phones_phone_hash ON staff_phones (phone_hash);

-- then encrypt the existing rows: run user-web-service once with --spring.profiles.active=backfill
-- (keeps app.crypto.allow-plaintext-read=true so plaintext rows read through), verify with
--   SELECT count(*) FROM staff_emails WHERE email NOT LIKE 'v1:%';
-- and flip app.crypto.allow-plaintext-read to false once it returns 0.

-- customers (August 2026): the customer master lives in user-service; order-service only keeps a
-- `customer_id` plus its own snapshot of the name/phone/email/address the invoice was raised with.
-- ddl-auto: update creates the tables on boot, so nothing here needs running for a fresh install.
--
-- Contacts are child tables, not columns, because a customer will be allowed several of each later:
-- customer_emails / customer_phones / customer_addresses, same shape as the staff_* contact tables.
-- Their values are AES-GCM ciphertext with AAD <table>:<column>:<customer_id>, and the *_hash columns
-- are the HMAC blind indexes that make lookup-by-phone possible (a GCM ciphertext can never be matched
-- by a JPQL predicate). Duplicates are allowed on purpose — two customers may share one phone.
-- customers.tenant_id is NULLABLE: a customer who signs up for themselves is not owned by one tenant.
--
-- CREATE TABLE customers
-- (
--     id         VARCHAR(50) PRIMARY KEY,
--     tenant_id  VARCHAR(50) REFERENCES tenants (id),
--     full_name  VARCHAR(100) NOT NULL,
--     notes      VARCHAR,
--     version    INTEGER,
--     deleted    BOOLEAN      NOT NULL DEFAULT FALSE,
--     created_by VARCHAR(50)  NOT NULL,
--     created_at TIMESTAMPTZ  NOT NULL,
--     updated_by VARCHAR(50)  NOT NULL,
--     updated_at TIMESTAMPTZ  NOT NULL
-- );
-- CREATE INDEX idx_customers_tenant_id ON customers (tenant_id);
--
-- CREATE TABLE customer_phones
-- (
--     id          VARCHAR(50) PRIMARY KEY,
--     customer_id VARCHAR(50)  NOT NULL REFERENCES customers (id),
--     phone       VARCHAR(128) NOT NULL,
--     phone_hash  VARCHAR(64),
--     version     INTEGER,
--     deleted     BOOLEAN      NOT NULL DEFAULT FALSE,
--     created_by  VARCHAR(50)  NOT NULL,
--     created_at  TIMESTAMPTZ  NOT NULL,
--     updated_by  VARCHAR(50)  NOT NULL,
--     updated_at  TIMESTAMPTZ  NOT NULL
-- );
-- CREATE INDEX idx_customer_phones_phone_hash ON customer_phones (phone_hash);
-- (customer_emails is the same shape with email VARCHAR(512) + email_hash,
--  customer_addresses with address_line VARCHAR(1024) and no hash — nothing looks an address up by value)
--
-- The customer tables have never existed in another shape, so there is nothing to migrate and no encryption
-- backfill for them — they are written encrypted from the first row. (Were that ever not true, note that
-- ciphertext cannot be copied between tables: the AAD names the table, so it would have to be re-encrypted.)

-- crypto key rotation (August 2026): the active key id now comes from Redis, key `user:encrypt:version`
-- (see CryptoConstant). `SET user:encrypt:version v2` after adding v2 to app.crypto.keys and new rows
-- encrypt under v2; existing rows keep decrypting under the key id stored in their own ciphertext prefix.
-- Unset/unknown falls back to app.crypto.active-key-id, which stays `v1`.
