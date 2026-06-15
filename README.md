# QOVF — 移动端 SPA 外汇交易平台

技术栈：**前端 Vue 3 + Vite + Pinia + TypeScript**，**后端 Java Spring Boot 3**。

## 目录结构

```
qovf/
├── admin/    管理后台（基于 vue-pure-admin 二次开发，已清理为底座）
├── client/   用户端（移动端 SPA 框架底座，含 TradingView 图表占位）
├── h5/       移动端 H5 框架底座（Vue3 + Vite + Pinia + Vant，空页面）
├── server/   后端（Spring Boot 3 框架骨架）
└── .tools/   本地工具链（JDK 17 + Maven，不纳入版本库）
```

### admin（管理系统底座）
基于开源项目 [vue-pure-admin](https://github.com/pure-admin/vue-pure-admin) 二次开发，
已删除全部业务/演示页面，**仅保留**：

- 布局系统（`src/layout`）、主题与样式（`src/style`）
- **全部 26 个 UI 组件**（`src/components`，如 ReDialog、ReDrawer、ReCropper、ReSegmented 等）
- 登录页、错误页（403/404/500）、个人中心、空白页等底座页面
- 权限/路由/状态管理（`src/router`、`src/store`）骨架，动态菜单已置空
- **已接入真实后端**：移除 mock（vite-plugin-fake-server），接口基础路径由 `VITE_API_BASE_URL`（默认 `/api`）
  注入到 axios `baseURL`；开发环境通过 vite proxy 将 `/api` 转发到 `http://localhost:8080`

```bash
cd admin && pnpm install && pnpm dev      # 开发（端口 8848，需后端已启动）
cd admin && pnpm build                    # 构建
```

> 演示账号：用户名 `admin`（管理员）/ 任意其它用户名（普通用户），密码任意非空。

### client（用户端框架）
干净的移动端 Vue 3 SPA 骨架（hash 路由，与线上 `#/home` 一致），目录已就位但页面留空：

- `src/api` axios 客户端 · `src/store` Pinia · `src/router` hash 路由
- `src/views/{home,trade}` 占位页；`src/components`、`src/composables`、`src/utils` 等为空目录
- **移动端 UI 库 Vant**：通过 `unplugin-vue-components` + `@vant/auto-import-resolver` 按需自动导入（含样式）
- TradingView 图表接入占位见 `public/charting_library/README.md` 与 `src/views/trade/index.vue`

```bash
cd client && pnpm install && pnpm dev     # 开发 (端口 5174)
cd client && pnpm build                   # 构建
```

### h5（移动端 H5 框架）
独立于 `client/` 的干净移动端 H5 底座，与 client 同栈（Vue3 + Vite + Pinia + TS），
**内置 Vant**（按需自动导入），hash 路由，仅含一个空白 `home` 占位页：

```bash
cd h5 && pnpm install && pnpm dev          # 开发（端口 5175）
cd h5 && pnpm build                        # 构建
```

### server（后端框架）
Spring Boot 3（Java 17，Maven 单模块），`context-path = /api`，端口 `8080`。
**MySQL + MyBatis-Plus + JWT** 真实实现（无模拟数据）。分层目录：
`controller / service / mapper / entity / dto / config / common`：

- **统一返回体** `common/Result<T>`（`code=0` 成功，与前端判断一致）
- **全局异常处理** `common/GlobalExceptionHandler`（记录请求位置 + 完整堆栈，写 `logs/qovf-error.log`）
- **跨域** `config/CorsConfig`｜**鉴权拦截器** `config/AuthInterceptor`（JWT Bearer，白名单 `/login`、`/refresh-token`）
- **数据层**：MyBatis-Plus（`SysUser`/`SysUserMapper`），Flyway 迁移（`db/migration/V1__init.sql` 建 `sys_user`），审计列 + 软删除 + 乐观锁
- **鉴权**：BCrypt 密码 + JWT（access/refresh），首启由 `DataInitializer` 写入初始账号
- 接口：`POST /login`、`POST /refresh-token`、`GET /get-async-routes`、`GET /mine`、`GET /mine-logs`

**本地数据库（WSL2 Docker MySQL 8）**——已通过 systemd 持久化运行：

```bash
# 启动/查看（在 WSL Ubuntu 内）
wsl -d Ubuntu -u root -- docker start qovf-mysql      # 启动容器
wsl -d Ubuntu -u root -- docker ps                    # 查看状态
# 重新创建：bash .tools/setup-mysql.sh（库 qovf / root 密码见 application.yml 默认，生产用环境变量覆盖）
```

> 演示账号：`admin / admin123`（管理员）、`common / common123`（普通用户）。生产请立即改密。
> ⚠️ WSL2 空闲会回收发行版，开发期需保持一个常驻 WSL 会话或开启 systemd，否则 `localhost:3306` 会断。

本仓库已在 `.tools/` 下放置了便携版 **JDK 17（Temurin 17.0.19）** 与 **Maven 3.9.9**，无需全局安装：

```bash
# Windows PowerShell 示例
$env:JAVA_HOME = "C:\qovf\.tools\jdk-17.0.19+10"
$env:PATH = "$env:JAVA_HOME\bin;C:\qovf\.tools\apache-maven-3.9.9\bin;$env:PATH"
cd server
mvn spring-boot:run                        # 启动后端（端口 8080，接口前缀 /api）
```

> `.tools/` 较大且与平台相关，已在根 `.gitignore` 中排除，请勿提交。
