# QLaundry

Aplikasi manajemen laundry berbasis web untuk mengelola order, pelanggan, jadwal, dan laporan bisnis laundry.

## Tech Stack

| Layer     | Library / Tool                              |
|-----------|---------------------------------------------|
| Framework | React 19 + TypeScript 6                     |
| Build     | Vite 8 + Bun                                |
| Routing   | React Router v7                             |
| Charts    | Recharts 3                                  |
| Icons     | Lucide React                                |
| Styling   | Custom CSS (CSS variables, dark/light mode) |

## Architecture

Screaming architecture + clean architecture, per-feature:

```
src/
├── app/styles/          # Global CSS design system
├── core/
│   ├── config/          # env.ts — typed env access
│   ├── http/            # httpClient with withFallback()
│   ├── theme/           # ThemeProvider, themeContext, useTheme
│   └── ui/              # Shared UI components (Button, Card, Modal, Drawer, Toast…)
├── features/
│   ├── auth/            # Login, register, session
│   ├── customers/       # Customer CRUD + tier system
│   ├── dashboard/       # Stats, trends, schedule
│   ├── orders/          # Create order, history, status workflow
│   └── reports/         # Revenue charts, service breakdown
└── shared/
    └── components/      # Sidebar, Topbar
```

Each feature follows four layers:

```
domain/          ← interfaces & types only (no deps)
infrastructure/  ← API calls + fallback data
application/     ← use cases (orchestration)
presentation/    ← React hooks & pages
```

## Routes

| Path              | Page                |
|-------------------|---------------------|
| `/login`          | Login               |
| `/register`       | Register            |
| `/dashboard`      | Dashboard           |
| `/orders/new`     | Buat order baru     |
| `/orders/history` | Riwayat order       |
| `/customers`      | Manajemen pelanggan |
| `/reports`        | Laporan & analitik  |

## Getting Started

```bash
# Install dependencies
bun install

# Copy env file
cp .env.example .env

# Start dev server
bun dev
```

## Environment

```env
VITE_API_BASE_URL=http://localhost:7777
```

The app calls the backend first and falls back to bundled mock data automatically on network error, timeout (4s), or
HTTP error — so the UI always works without a running backend.

## API Endpoints

All endpoints are prefixed with the value of `VITE_API_BASE_URL`.

| Method   | Path                          | Description                            |
|----------|-------------------------------|----------------------------------------|
| `POST`   | `/api/auth/login`             | Login, returns `{ accessToken, user }` |
| `POST`   | `/api/auth/register`          | Register new user                      |
| `GET`    | `/api/auth/me`                | Get current user profile               |
| `POST`   | `/api/auth/logout`            | Logout                                 |
| `GET`    | `/api/orders`                 | List orders (supports filters)         |
| `POST`   | `/api/orders`                 | Create order                           |
| `PATCH`  | `/api/orders/:id/status`      | Advance order status                   |
| `DELETE` | `/api/orders/:id`             | Cancel order                           |
| `GET`    | `/api/services`               | List laundry services                  |
| `GET`    | `/api/customers`              | List customers                         |
| `POST`   | `/api/customers`              | Create customer                        |
| `PATCH`  | `/api/customers/:id`          | Update customer                        |
| `DELETE` | `/api/customers/:id`          | Delete customer                        |
| `GET`    | `/api/customers/phone/:phone` | Lookup customer by phone               |
| `GET`    | `/api/dashboard/summary`      | Dashboard stats + schedule             |

## Demo Credentials

```
Username : admin
Password : admin123
```

## Scripts

```bash
bun dev        # Dev server (http://localhost:5173)
bun build      # Production build (tsc + vite)
bun lint       # ESLint
bun preview    # Preview production build
```
