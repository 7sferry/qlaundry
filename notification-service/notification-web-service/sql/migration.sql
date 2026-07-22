/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

CREATE TABLE email_notifications
(
    id           VARCHAR(50) PRIMARY KEY,
    reference_id VARCHAR(50),
    type         VARCHAR(50)  NOT NULL,
    recipient    VARCHAR(100) NOT NULL,
    subject      VARCHAR(200) NOT NULL,
    content      TEXT         NOT NULL,
    version      INTEGER,
    created_at   TIMESTAMPTZ  NOT NULL,
    sent_at      TIMESTAMPTZ  NOT NULL
);
