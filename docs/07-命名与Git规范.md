# 命名与 Git 规范

## 1. 命名总则

清晰表意 > 简短；统一英文，禁止拼音/中英混拼（业务专有名词除外，团队统一术语表）。

### 1.1 前端（TS/Vue）

| 对象 | 风格 | 示例 |
| --- | --- | --- |
| 变量 / 函数 / 方法 | camelCase | `orderList`、`fetchOrders()` |
| 组件名 / 类 / 类型 / 接口 | PascalCase | `OrderTable`、`ApiResult` |
| 常量 / 枚举值 | UPPER_SNAKE 或 `as const` 对象 | `MAX_RETRY`、`OrderStatus.Filled` |
| 组合式函数 | `useXxx` | `useOrderFilter` |
| Store | `useXxxStore` | `useUserStore` |
| 布尔值 | `is/has/can/should` 前缀 | `isLoading`、`hasPermission` |
| 事件 | 动词，`emit('update:xxx')` | `@submit`、`update:modelValue` |
| 文件：组件 | PascalCase 或目录 `index.vue` | `OrderTable.vue`、`order/index.vue` |
| 文件：工具/store/api | camelCase / kebab | `formatMoney.ts`、`user.ts` |

### 1.2 后端（Java）

| 对象 | 风格 | 示例 |
| --- | --- | --- |
| 类 / 接口 / 枚举 | PascalCase | `OrderService`、`Result` |
| 方法 / 变量 | camelCase | `createOrder()`、`accountId` |
| 常量 | UPPER_SNAKE | `SUCCESS`、`MAX_PAGE_SIZE` |
| 包 | 全小写 | `com.qovf.controller` |
| 类后缀按分层固定 | — | `*Controller`/`*Service`/`*Mapper`/`*DTO`/`*VO` |

### 1.3 数据库

- 见[数据库规范](./04-数据库规范.md)：`snake_case`、索引 `pk_/uk_/idx_` 前缀。

## 2. 目录与模块组织

- 按**业务域**而非技术类型组织业务代码（同一域的 view/components/api/store 就近聚合或清晰对应）。
- 通用与业务分离：通用沉到 `components`/`utils`/`composables`/`common`，业务留在各自模块。
- 新增模块遵循各端既有结构（admin 见 pure-admin 约定，client/h5 见 `src/{api,store,router,views,components}`）。
- `.tools/`（便携 JDK/Maven）、`node_modules/`、`dist/`、`target/`、根 `.env` 已被忽略，**不得提交**。

## 3. Git 分支

- `main`：稳定可发布分支，受保护，禁止直接 push 大改动。
- 功能分支：`feat/xxx`、修复 `fix/xxx`、文档 `docs/xxx`、重构 `refactor/xxx`。
- 通过 PR/MR 合并，至少一人评审；合并前需通过 `pnpm typecheck`/`pnpm build` 与后端 `mvn package`。

## 4. 提交信息（Conventional Commits）

格式：`<type>(<scope>): <subject>`，正文说明“为什么”，必要时关联事项。

- **type**：`feat` / `fix` / `docs` / `refactor` / `perf` / `test` / `chore` / `style` / `build` / `ci`。
- **scope**：模块或子项目，如 `admin`、`h5`、`server`、`trade`。
- **subject**：祈使句、简洁、不超 50 字、结尾不加句号。

示例：

```
feat(server): 接入 JWT 鉴权并替换内存账号

- 密码改用 BCrypt 存储
- accessToken/refreshToken 滚动刷新
```

- 一次提交只做一件事，避免“大杂烩”提交；不可提交未通过编译/类型检查的代码。
- 提交不得包含密钥、调试输出、注释掉的僵尸代码（见[安全](./03-安全规范.md)与[代码规范](./01-代码规范.md)）。

## 5. 代码评审要点

- 是否破坏 `Result`(code=0) 契约、金额是否用高精度、是否有越权/注入风险、密钥是否泄露。
- 单文件是否超 500 行、是否有重复可复用、类型是否完整无 `any`。
- UI 是否复用既有组件、三态是否齐全、文案是否走 i18n。
