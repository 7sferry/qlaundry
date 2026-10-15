# user-service

Port **8101** · Postgres schema `users` · Redis

Handles authentication, tenants (laundry businesses), staff accounts and customers, and is the only service that issues tokens. It also produces the email jobs that [notification-service](../notification-service/README.md) sends.

Modules: `user-domain`, `user-core`, `user-gateway`, `user-web-service`, and `user-client` (the HTTP interface other services use to call its `/internal/` endpoint).

## API

Paths as the controllers map them; through the gateway, prefix them with `/api`.

### Auth

```
POST   /auth/tenant/registration          register a laundry + its first (super-staff) account; Turnstile captcha
GET    /auth/tenant/confirmRegistration   ?tenantId=&token=  → activates the tenant
POST   /auth/tenant/resendConfirmation    {tenantId}         → new confirmation email
POST   /auth/staff/registration           add a staff account to an active tenant
POST   /auth/staff/login
POST   /auth/staff/refresh
POST   /auth/staff/forgottenPassword      {username}                       → {email} (masked); emails an OTP
POST   /auth/staff/submitOtp              {username, otp}                  → {resetToken} (single use, 5 min)
POST   /auth/staff/resetPassword          {username, password, resetToken}
DELETE /auth/staff/logout
```

- A new tenant is `PENDING` until its confirmation link is clicked; its staff can't log in before then. The link is valid for 24 hours (resend for a new one), and a tenant that never confirms is removed after 30 days, freeing its username.
- Login returns a short-lived access token (Ed25519-signed JWT, 15 min) and sets an HTTP-only `refresh_token` cookie scoped to `/auth`. The access token carries the tenant id, role and the tenant's `timeZone`.
- Passwords are hashed with Argon2. A new password can't equal the current one or any password used in the last 90 days.

### Staff

```
GET    /staff/list        rows include role (STAFF / SUPER_STAFF)
GET    /staff/detail
DELETE /staff/delete      ?username=   super-staff only; not your own account
PUT    /staff/profile     {fullName, description, emails, phones, addresses}   always the signed-in staff
```

### Tenant settings

```
GET    /tenant/detail     → {tenantName, description, timeZone}   super-staff only
PUT    /tenant/update     {tenantName, description, timeZone}     super-staff only
```

Both always act on the caller's own tenant. The time zone (an IANA id such as `Asia/Jakarta`) decides what "today" means for the dashboard, schedule and order numbers. It is captured from the browser at registration and defaults to `UTC`. Staff pick up a change at their next token refresh.

### Customers

```
POST   /customer/registration   {fullName, phone, email, address, notes}
GET    /customer/list           ?fullName=&phone=&after=&before=&sortBy=&sortDir=&pageSize=
GET    /customer/detail         ?customerId=
GET    /customer/names          ?customerIds=a&customerIds=b   → {customers: [{customerId, fullName}]}   names only, up to 100 ids
PUT    /customer/update         {customerId, fullName, phone, email, address, notes}
DELETE /customer/delete         ?customerId=   super-staff only (soft delete)
```

- Phone numbers are normalised to E.164 before saving: `0812 3456 7890`, `62812…`, `812…` and `+62 812…` all become `+62812…` (Indonesia is the default dial code). `?phone=` matches the exact normalised number.
- Duplicate phone numbers or emails are allowed, e.g. family members sharing one number.

### Internal (not exposed by the gateway)

```
GET    /internal/customer/verification   ?customerId=&tenantId= → {customerId, tenantId, valid}
```

order-service calls this before attaching a customer to an order. It requires `X-Internal-Api-Key` and returns only a yes/no answer, never customer data.

## Lists and paging

List endpoints are cursor-paginated: pass the `after` token from a response to get the next page, or `before` to go back. `pageSize` defaults to 3 and is capped at 50.

## Setup notes

- **Seed data**: `user-gateway/src/main/resources/init.sql` (roles, session types, tenant statuses, email trigger types/statuses, analytics aggregates/statuses).
- **Analytics events**: registering or deleting a staff member or customer writes an `analytics_events` row and publishes it to `analytics:event:STAFF` / `analytics:event:CUSTOMER` after commit (ids, role and timestamps only — no names or contacts). A sweeper republishes anything Redis missed. Load existing rows into analytics with `./mvnw spring-boot:run -Dspring-boot.run.profiles=analytics-backfill` from `user-web-service` (safe to rerun).
- **Captcha**: registration is verified with Cloudflare Turnstile. Set `app.turnstile.secret-key` here and `VITE_TURNSTILE_SITE_KEY` in `web/.env`.
- **Emails** go out through the `notification:email:*` Redis streams, so notification-service and an SMTP catcher must be running to receive them.
- **Encryption**: staff and customer contact details are stored AES-256-GCM-encrypted, with keys under `app.crypto.*`. To move new writes onto another key without a restart, add it to `app.crypto.keys` and `SET user:encrypt:version v2` in Redis. Never remove a key that was ever used.
- **Migrating an old database**: `user-web-service/sql/migration.sql` documents the manual `ALTER`s. After running them, start the service once with `--spring.profiles.active=backfill` to encrypt existing rows.

## Internal API keys

order-service authenticates to `/internal/` with `X-Internal-Api-Key: <clientId>:<version>:<secret>`. This service stores only `SHA-256(secret)`, in Redis at `user:internal:key:<clientId>:<version>`, with the yaml `app.internal.clients` map as a fallback while Redis is unreachable. Keys work only on `/internal/` paths.

**Rotation**: the new version must be accepted before anyone sends it, and the old one removed only after nobody does.

```bash
SECRET=$(openssl rand -base64 32); printf '%s' "$SECRET" | sha256sum        # 1. new secret + digest
redis-cli -a 12345 SET user:internal:key:order:v2 <digest>                  # 2. accept v2 (no restart)
# 3. set APP_INTERNAL_USERSERVICE_APIKEY=order:v2:$SECRET and restart order-service
# 4. grep 'authenticated as order using key v1' logs/user-web-service.log   (all instances)
redis-cli -a 12345 DEL user:internal:key:order:v1                           # 5. retire v1
```

- Always do step 2 before step 3. Rolling back is free until step 5.
- The yaml baseline can't be revoked with `DEL`. Promote its successor into yaml at the next deploy.
- In an emergency, `DEL` the compromised version immediately. Only orders that carry a `customerId` will fail (503) until order-service has the new key.
- Never reuse a version id.

## Tests

```bash
./mvnw test     # from this folder or the repo root
```
