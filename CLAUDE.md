# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

QOVF is a mobile-first forex/FX trading platform, organized as a **multi-project repo** (not a pnpm/Maven workspace — each project is independent and installed/built on its own):

- `admin/` — management backend. Vue 3 + Vite + Pinia + Element Plus, derived from **vue-pure-admin** and stripped down to a base ("底座"): layout, theme, and all 26 reusable UI components kept; business/demo pages removed.
- `client/` — user-facing mobile SPA. Vue 3 + Vite + Pinia + TS, with a **TradingView Charting Library** integration placeholder.
- `h5/` — a second, cleaner mobile H5 scaffold. Same stack as `client/` plus **Vant** (auto-imported). Intentionally near-empty.
- `server/` — Spring Boot 3 (Java 17) backend. `context-path=/api`, port `8080`.
- `.tools/` — portable JDK 17 + Maven 3.9.9 (git-ignored). **Not on PATH** — see below.

## Commands

Each frontend uses **pnpm** (required; `admin` enforces it via `only-allow pnpm`). Run from inside the project dir.

```bash
# admin/ (port 8848)
pnpm install && pnpm dev          # backend must be running for login to work
pnpm build                        # rimraf dist + vite build + version file
pnpm typecheck                    # vue-tsc --noEmit --skipLibCheck
pnpm lint                         # eslint + prettier + stylelint

# client/ (port 5174)  and  h5/ (port 5175) — identical script set
pnpm install && pnpm dev
pnpm build                        # vue-tsc --noEmit && vite build
pnpm typecheck
```

There is **no test suite** on the frontends. `admin`'s build is heavy (allocates up to 8 GB via `NODE_OPTIONS`).

### Backend — uses the bundled portable toolchain

JDK and Maven live in `.tools/` and are **not globally installed**. Export them first (paths are exact):

```bash
export JAVA_HOME="/c/qovf/.tools/jdk-17.0.19+10"
export PATH="$JAVA_HOME/bin:/c/qovf/.tools/apache-maven-3.9.9/bin:$PATH"

cd server
mvn clean package                 # line-check (validate) + compile + test + fat jar
mvn spring-boot:run               # start backend on :8080
mvn test -Dtest=QovfApplicationTests   # run a single test class
```

`QovfApplicationTests.contextLoads` boots the **full** context, so it needs MySQL up (Flyway + DataInitializer run). The build also runs the line-count check at the `validate` phase via `node` (Windows: `node` must be on PATH).

### MySQL (required to run/test the backend)

Runs as a **Docker container inside WSL2 Ubuntu** (no native install). DB `qovf`, exposed to Windows `localhost:3306`:

```bash
wsl -d Ubuntu -u root -- docker start qovf-mysql   # start
wsl -d Ubuntu -u root -- docker ps                 # status
bash .tools/setup-mysql.sh                          # recreate from scratch (via WSL)
```

**WSL gotcha:** WSL2 reclaims the distro when idle, which kills the container and drops `localhost:3306`. systemd is enabled (`/etc/wsl.conf`), but to be safe during a work session keep a long-running WSL process alive (e.g. `wsl -d Ubuntu -u root -- sleep 7200` in the background). If the backend gets `Communications link failure`, the distro went down — start the container again.

Credentials/secrets are local-dev defaults in `application.yml` (`MYSQL_PASSWORD`, `JWT_SECRET`) overridable via env; production must override. Seeded accounts: `admin/admin123`, `common/common123`.

### Backend architecture

- **Real data only** — `AuthController`/`UserController`/`RouteController` are backed by MySQL via **MyBatis-Plus** (`SysUser`/`SysUserMapper`/`SysUserService`), not mock data (see `docs/01-代码规范.md#9-禁止模拟数据`).
- **Schema** is owned by **Flyway** (`src/main/resources/db/migration/V*.sql`); never hand-edit the DB. `sys_user` carries audit columns + soft-delete (`deleted`, via MyBatis-Plus `@TableLogic` + global config) + optimistic lock (`version`). `create_time`/`update_time` auto-fill via `MetaObjectHandler`.
- **Auth** — BCrypt passwords + JWT (`common/JwtUtil`, HS-family). `config/AuthInterceptor` validates `Authorization: Bearer <accessToken>` for all routes except the `/login`, `/refresh-token`, `/error` whitelist in `config/WebConfig`; it stashes `username`/`role` as request attributes for controllers.
- MyBatis-Plus 3.5.9 **decoupled JSqlParser** — pagination/optimistic-lock inner interceptors require the separate `mybatis-plus-jsqlparser` dependency (already added).

## Conventions

Project coding standards live in [`docs/`](./docs/README.md) and are mandatory. Hard red lines that affect almost any change:

- **Money is high-precision only** — `BigDecimal` (Java) / `DECIMAL` (DB); never `float`/`double`/JS `number`. Serialize `Long`/`BigDecimal` to `string` for the frontend.
- **Never break the `Result` contract** — `code === 0` means success (see below).
- **No secrets in code/commits/logs** — the root `.env` is git-ignored (`/.env`); keep it that way.
- **Split any file over 500 lines** (target ≤ 300). See [`docs/01-代码规范.md`](./docs/01-代码规范.md).

## Architecture — the key invariant

The **front↔back response contract** is what ties the projects together and is easy to break:

- Backend wraps every response in `common/Result<T>` as `{ code, message, data }`, where **`code == 0` means success** (`Result.SUCCESS`). Non-zero = failure, surfaced by `common/GlobalExceptionHandler` (`@RestControllerAdvice`: bean-validation errors, `BusinessException`, and a 500 fallback).
- The admin frontend treats a response as successful **only when `data.code === 0`** (see `admin/src/store/modules/user.ts`). Any new endpoint consumed by admin must return this shape.
- `server/src/main/java/com/qovf/controller/*` contains **demo implementations** (in-memory `AuthController`, `UserController`, `RouteController`) whose JSON shapes are hand-matched to the admin's TypeScript types in `admin/src/api/user.ts` / `routes.ts`. Replace with DB + JWT for real use, but keep the shapes aligned.

### admin: wired to a real backend (no mock)

- The vite-plugin-fake-server mock was **removed** (`build/plugins.ts`, `mock/` dir deleted). Requests hit the real backend.
- API base URL comes from `VITE_API_BASE_URL` (`.env.*`, default `/api`), injected into the axios `baseURL` in `src/utils/http/index.ts`. Dev proxies `/api → http://localhost:8080` (`vite.config.ts`). Because the backend's `context-path` is also `/api`, an admin call to `/login` lands at `http://localhost:8080/api/login`. Demo login: username `admin` (admin role) or anything else (common role), any non-empty password.
- Adding a new `VITE_*` var requires updating **three** places or typecheck fails: the `ViteEnv` interface in `types/global.d.ts`, the defaults object in `build/utils.ts` (`wrapperEnv`), and the `.env.*` files.
- Routing: `src/router/index.ts` **auto-globs** `src/router/modules/**/*.ts` (except `remaining.ts`). Only `home.ts`, `remaining.ts`, `error.ts` remain. `remaining.ts` pins which views are core (login, error, account-settings, empty); deleting a view there breaks the app. Add a business page by creating its view + a router module file. Dynamic (backend) menus are intentionally empty — `/get-async-routes` returns `[]`.

### client & h5: mobile SPAs

- Both use **hash routing** (`createWebHashHistory`) to match the production URL form `#/home`, a shared axios client (`src/api/index.ts`, `baseURL: /api`), and dev proxy to `:8080`.
- Vant is **auto-imported** via `unplugin-vue-components` + `@vant/auto-import-resolver` (`vite.config.ts`); styles load automatically, no manual imports. The generated `src/components.d.ts` is git-ignored.
- `client/` holds the TradingView placeholder: drop the licensed library into `public/charting_library/`, enable the script in `index.html`, and init the widget in `src/views/trade/index.vue`.

### pnpm 11 gotcha (client/h5)

pnpm 11 blocks dependency build scripts by default. `client/` and `h5/` each carry a `pnpm-workspace.yaml` with `allowBuilds:` enabling `esbuild`, `@parcel/watcher`, `vue-demi`. Without it, `vite build` fails because esbuild's binary never gets set up. `admin/` configures the same via `pnpm-workspace.yaml` `allowBuilds`.
