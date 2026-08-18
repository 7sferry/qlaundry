/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

CREATE TABLE email_types
(
    id   SMALLINT PRIMARY KEY,
    name VARCHAR(25) NOT NULL
);

CREATE TABLE email_notifications
(
    id           VARCHAR(50) PRIMARY KEY,
    reference_id VARCHAR(50),
    type_id      SMALLINT     NOT NULL REFERENCES email_types (id),
    recipient    VARCHAR(100) NOT NULL,
    subject      VARCHAR(200) NOT NULL,
    content      TEXT         NOT NULL,
    version      INTEGER,
    created_at   TIMESTAMPTZ  NOT NULL,
    sent_at      TIMESTAMPTZ  NOT NULL
);

-- encrypt recipient at rest (application-level AES-256-GCM, own key — independent from user-service):
-- ciphertext is <keyId>:<base64url(nonce || ciphertext || tag)>, bound to AAD email_notifications:recipient:<row id>.
-- ddl-auto: update never alters an existing column's type, so widen by hand:
ALTER TABLE email_notifications ALTER COLUMN recipient TYPE VARCHAR(512);

-- then encrypt the existing rows: run notification-web-service once with --spring.profiles.active=backfill
-- (keeps app.crypto.allow-plaintext-read=true so plaintext rows read through), verify with
--   SELECT count(*) FROM email_notifications WHERE recipient NOT LIKE 'v1:%';
-- and flip app.crypto.allow-plaintext-read to false once it returns 0.
