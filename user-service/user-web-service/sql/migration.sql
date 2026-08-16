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
