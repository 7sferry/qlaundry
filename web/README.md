# QLaundry — web

Aplikasi web QLaundry untuk mengelola order, pelanggan, layanan, promo, staf, dan laporan bisnis laundry. This is the React frontend of the monorepo; the backend, gateway and full local setup are described in the [root README](../README.md).

## Tech stack

| Layer | Library / Tool |
|---|---|
| Framework | React 19 + TypeScript 6 (strict) |
| Build | Vite 8 + Bun |
| Routing | React Router 7 |
| Charts | Recharts 3 |
| Icons | Lucide React |
| Styling | One global CSS file with CSS variables (dark/light theme), no Tailwind / CSS-in-JS |
| Tests | Vitest 4 (pure-logic unit tests, co-located `*.test.ts`) |
| Captcha | Cloudflare Turnstile (tenant registration) |

## Features

| Path | Page |
|---|---|
| `/register` | Register a laundry (tenant) + its super-staff account |
| `/confirm-registration` | Landing page of the confirmation email; activates the tenant, offers a resend on failure |
| `/login` | Login |
| `/forgot-password` | Reset password: username → emailed OTP → new password |
| `/dashboard` | Today's and this month's numbers, growth, status distribution, today's pickups/deliveries |
| `/orders/new` | Create an order: customer search, items, weight, priority, promo codes, "picked up immediately" |
| `/orders/history` | Order list with filters, status changes, payment, "view invoice" (opens the PDF in a new tab) |
| `/customers` | Customer book with order count, total spend and last order |
| `/services` | Laundry service price list (editing is super-staff only) |
| `/promotions` | Promo codes: create, edit, pause/resume (super-staff only) |
| `/staff` | Staff accounts and roles |
| `/reports` | Revenue trend by week/month/quarter/year and per-service breakdown |
| `/settings` | Your own account |
| `/tenant-settings` | Business name, description and time zone (super-staff only) |

All UI text is Indonesian; money is IDR (`formatCurrency`, `id-ID` locale).

## Architecture

Screaming architecture + clean architecture: every feature is a self-contained vertical slice.

```
src/
├── app/styles/       global.css — the design system (CSS variables, themes)
├── core/
│   ├── auth/         in-memory token store, session events
│   ├── config/       env.ts — typed env access (never read import.meta.env elsewhere)
│   ├── hooks/        shared hooks (useOnceEffect, usePaginatedList, …)
│   ├── http/         httpClient (get/post/put/patch/delete, token refresh, HttpError)
│   ├── pagination/   cursor pagination types (after/before, pageSize)
│   ├── theme/        ThemeProvider, useTheme
│   ├── ui/           shared components (Button, Card, Modal, Drawer, PhoneInput, Toast, …)
│   └── utils/        format.ts, openUrlInNewTab.ts
├── features/         auth · customers · dashboard · orders · promotions · reports · services · staff · tenant
└── shared/components Sidebar, Topbar
```

Each feature has four layers:

```
domain/          types & repository interfaces (no deps)
infrastructure/  repository implementations: API call ⇄ wire format
application/     use cases (orchestration, no React)
presentation/    React hooks (use*) + pages
```

- **Live-only**: every screen calls the real backend and surfaces errors. There is no mock or fallback data.
- **Endpoint paths** live in one `infrastructure/<feature>Endpoints.ts` per feature (`ORDER_ENDPOINTS`, `CUSTOMER_ENDPOINTS`, …). Repositories never inline a path string.
- **Errors**: a 4xx shows the backend's reason (the `detail` of its `ProblemDetail` body) in the toast or inline error; a 5xx shows a generic server-error message with the status code.
- **Auth**: the access token lives only in memory. A reload restores the session through the `refresh_token` httpOnly cookie, and a 401 from refresh logs you out.
- **Time zone**: the browser's zone is sent once, at tenant registration. After that, "today" and every report bucket follow the tenant's zone from the token, and no request carries a zone.
- **Lists** are cursor-paginated (`after`/`before`). A page-size picker (3/5/10/20/50) sits in each page's filter row.
- **Stat cards** on every page (staff, customers, services, promotions, reports, dashboard) come from analytics-service and cover the whole laundry, not just the rows on screen. Head-counts are totals; "new", spend and redemptions cover this month (from the 1st in the laundry's time zone). The customer list's orders/spend/last-order columns come from analytics too.

## Getting started

Run it behind the gateway as described in the [root README](../README.md), then open https://localhost:8100.

```bash
bun install
cp .env.example .env      # then set VITE_TURNSTILE_SITE_KEY
bun dev                   # Vite on :5173 (reached through the gateway)
```

## Environment

| Variable | `.env.example` | Meaning |
|---|---|---|
| `VITE_API_BASE_URL` | `/api` | Backend base URL. `/api` is the gateway's same-origin prefix, so there is no CORS. |
| `VITE_TURNSTILE_SITE_KEY` | — | Cloudflare Turnstile site key for the registration captcha |

## Scripts

```bash
bun dev            # dev server
bun run build      # tsc -b && vite build
bun lint           # ESLint
bun run test       # Vitest, single pass
bun run test:watch # Vitest, watch mode
bun preview        # preview the production build
```
