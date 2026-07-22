/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

CREATE TABLE email_triggers
(
    id         VARCHAR(50) PRIMARY KEY,
    type       VARCHAR(50)  NOT NULL,
    recipient  VARCHAR(100) NOT NULL,
    payload    TEXT         NOT NULL,
    status     VARCHAR(20)  NOT NULL,
    version    INTEGER,
    deleted    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_by VARCHAR(50)  NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL,
    updated_by VARCHAR(50)  NOT NULL,
    updated_at TIMESTAMPTZ  NOT NULL
);
