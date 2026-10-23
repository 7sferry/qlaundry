# notification-service

Port **8102** · Postgres schema `notif` · Redis (streams) · SMTP

Sends emails. It has **no REST API**: it consumes email jobs that [user-service](../user-service/README.md) publishes to Redis streams, renders them with Thymeleaf, sends them over SMTP and records each sent email in `email_notifications`.

Modules: `notification-domain`, `notification-core`, `notification-gateway`, `notification-web-service`.

## Emails

| Stream | Email | Template |
|---|---|---|
| `notification:email:TENANT_REGISTRATION` | Registration confirmation, with the link that activates the tenant | `templates/mail/tenant-registration.html` |
| `notification:email:FORGOTTEN_PASSWORD` | Password-reset OTP (valid 3 minutes) | `templates/mail/forgotten-password.html` |
| `notification:email:PASSWORD_CHANGED` | Security notice that the account password was changed (profile change or reset) | `templates/mail/password-changed.html` |

Templates are in `notification-gateway/src/main/resources/templates/mail/`. The confirmation link points at the **frontend** (`app.notification.confirmation.base-url`, `https://localhost:8100` in dev), whose `/confirm-registration` page activates the tenant.

- **Delivery**: user-service saves each email job in its own database first and publishes it only after that commit, so a job is never lost. A failed send stays pending in the stream's consumer group, and the consumer keeps running through Redis outages.
- **Retention**: stream entries are trimmed after 7 days (`app.notification.stream.email.trim-cron`, `retention-days`). The durable record is user-service's `email_triggers` table.
- **Privacy**: recipients are stored encrypted (AES-256-GCM, `app.crypto.*`) and never logged.

## Setup

- An SMTP server on `localhost:1025`. Mailpit is easiest: `docker run -d -p 1025:1025 -p 8025:8025 axllent/mailpit`, then read mail at http://localhost:8025.
- The sender address is `app.mail.sender`, and SMTP settings are under `spring.mail`.
- **Seed data**: `notification-gateway/src/main/resources/init.sql` (email types).
- The stream prefix `app.notification.stream.email.key` (`notification:email:`) must match user-service's.
- **Migrating an old database**: run the `ALTER`s in `notification-web-service/sql/migration.sql`, then start once with `--spring.profiles.active=backfill` to encrypt existing rows.

## Tests

```bash
./mvnw test     # from this folder or the repo root
```
