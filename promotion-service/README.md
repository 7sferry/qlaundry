# promotion-service

Port **8104** · Postgres schema `promotions` · Redis (internal API keys only)

Manages each laundry's promo codes and redeems them while an order is created. It verifies user-service's tokens, is called by [order-service](../order-service/README.md) over `/internal/`, and backs the web app's promotions page and the promo picker on order creation.

Modules: `promotion-domain`, `promotion-core`, `promotion-gateway`, `promotion-web-service`, and `promotion-client` (the HTTP interface order-service uses to call it).

## API

Paths as the controllers map them; through the gateway, prefix them with `/api`. Everything is scoped to the caller's tenant.

```
POST   /promotion/create    super-staff
PUT    /promotion/update    {promotionId, ...same fields..., active}   super-staff
PATCH  /promotion/toggle    {promotionId, active}                      super-staff — pause or resume
GET    /promotion/detail    ?promotionId=
GET    /promotion/list      ?code=&name=&type=&activeOnly=&currentOnly=&after=&before=&sortBy=&sortDir=&pageSize=
POST   /promotion/preview   {promotions: [...], subtotal}   → what each code would grant, without saving anything
```

Create body:

```
{code, name, description?, type, percentage?, amount?, maxDiscountAmount?, minSubtotal?,
 combinable?, usageLimit?, startAt, endAt}
```

## Promotion rules

- **Types**
  - `CUMULATIVE_PERCENTAGE`: `percentage` (0–100] of what is left after codes applied before it.
  - `NON_CUMULATIVE_PERCENTAGE`: `percentage` of the order's original subtotal.
  - `FIXED_AMOUNT`: a fixed `amount` off what is left; it can discount the order to zero but never below.
- **Optional on every type**: `maxDiscountAmount` caps the discount; `minSubtotal` requires the original subtotal to be at least that much; `usageLimit` (empty = unlimited) caps total redemptions. `combinable` defaults to `true`; a `false` code must be used alone.
- **Dates**: `startAt` and `endAt` are required, and `endAt` must be after `startAt`.
- **Codes** are trimmed and upper-cased, 3–32 characters of `A-Z 0-9 _ -`. A code is unique per tenant **forever**: invoices keep the code they used, so it can never be re-issued under different terms.
- **No delete**: retire a promotion with `PATCH /promotion/toggle {active: false}`. The request states the desired value, so repeating it is harmless. Switching off keeps the usage count, and switching back on resumes from it.
- **List**: `activeOnly` defaults to `true`, and `currentOnly=true` shows only promotions running right now. `sortBy` is `ID` (default, newest first), `NAME`, `END_AT` or `CODE`. Cursor-paginated like the other lists (`after`/`before`, `pageSize` default 3, max 50).
- **Preview** runs the exact discount calculation used at redemption on promotions you send in, in the order you send them (the order matters when stacking). It touches no data, so it can't claim a usage slot.

## Redemption (internal, not exposed by the gateway)

```
POST   /internal/promotion/redemption           {tenantId, codes[], subtotal, referenceId, customerId?, redeemedBy}
                                                → {redemptions: [{applied, message, promotionId, code, type, discountAmount, remainingUsage}]}
POST   /internal/promotion/redemption/release   {tenantId, referenceId, releasedBy}
                                                → {referenceId, released: [{promotionId, code, discountAmount}]}
```

- One call redeems a whole stack of codes, in the order given. If **any** code is rejected (unknown, inactive, not started, expired, used up, not combinable, below the minimum subtotal, no discount), nothing in the call is kept, and each rejection comes back as `applied: false` with a message rather than an HTTP error.
- Usage slots are claimed with a single conditional `UPDATE`, so two orders racing for the last slot can't both win.
- `referenceId` is the order number. Repeating a redemption for the same order and code replays the first result instead of using a second slot.
- `release` gives back every slot claimed under a `referenceId`. order-service calls it when an order fails after redemption. It is safe to call repeatedly.
- Both require `X-Internal-Api-Key` (authority `SERVICE_ORDER`), checked against `SHA-256(secret)` in Redis under `promotion:internal:key:<clientId>:<version>`. Rotation works the same way as user-service's (see its README), with this key prefix.

## Setup notes

- **Seed data**: `promotion-gateway/sql/init.sql` (promotion types).
- This service stores no personal data, so it has no encryption config.
- `promotion-web-service/sql/migration.sql` documents manual changes for older databases, e.g. backfilling promotions that had no end date before `endAt` became required.

## Tests

```bash
./mvnw test     # from this folder or the repo root
```
