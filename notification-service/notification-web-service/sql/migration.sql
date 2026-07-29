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
