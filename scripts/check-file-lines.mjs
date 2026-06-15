#!/usr/bin/env node
/**
 * 行数检查：强制执行「单文件 ≤500 行，>300 行预警」规范（见 docs/01-代码规范.md）。
 *
 * 用法：
 *   node scripts/check-file-lines.mjs [dir ...]
 *   不传参时扫描全仓库各子项目源码目录。
 *
 * 退出码：存在 >500 行的文件时退出 1（用于构建/CI 阻断）。
 */
import { readdirSync, statSync, readFileSync } from "node:fs";
import { join, resolve, relative, extname, basename } from "node:path";
import { fileURLToPath } from "node:url";

const WARN = 300;
const MAX = 500;

const repoRoot = resolve(fileURLToPath(new URL("../", import.meta.url)));

// 基线豁免：继承自底座/生成的既有超限文件（新增代码不得加入）
let baseline = new Set();
try {
  const raw = readFileSync(
    join(repoRoot, "scripts", "linecount-baseline.json"),
    "utf8"
  );
  baseline = new Set((JSON.parse(raw).allow || []).map(p => p.replace(/\\/g, "/")));
} catch {
  /* 无基线文件则全量强制 */
}

// 参与检查的源码后缀
const EXT = new Set([
  ".ts", ".tsx", ".js", ".mjs", ".cjs", ".vue", ".scss", ".css", ".java"
]);
// 跳过的目录
const SKIP_DIR = new Set([
  "node_modules", "dist", "dist-ssr", "target", ".tools", ".git",
  ".husky", ".vscode", ".idea", "public"
]);
// 跳过的文件（生成物/锁文件等）
const skipFile = name =>
  name.endsWith(".d.ts") || name === "components.d.ts";

const defaultTargets = [
  "admin/src", "client/src", "h5/src", "server/src/main/java"
];

// 目标路径相对「调用时的工作目录」解析：
//   前端 cwd=admin/ 传 "src" → admin/src
//   后端 workingDirectory=仓库根 传 "server/src/main/java"
//   无参时从仓库根手动运行，按 defaultTargets 全量扫描
const targets = (process.argv.slice(2).length
  ? process.argv.slice(2)
  : defaultTargets
).map(t => resolve(process.cwd(), t));

const warnings = [];
const errors = [];
const exempted = [];

function walk(dir) {
  let entries;
  try {
    entries = readdirSync(dir);
  } catch {
    return; // 目标目录不存在则跳过
  }
  for (const name of entries) {
    const full = join(dir, name);
    let st;
    try {
      st = statSync(full);
    } catch {
      continue;
    }
    if (st.isDirectory()) {
      if (!SKIP_DIR.has(name)) walk(full);
      continue;
    }
    if (!EXT.has(extname(name)) || skipFile(basename(name))) continue;
    const lines = readFileSync(full, "utf8").split("\n").length;
    const rel = relative(repoRoot, full).replace(/\\/g, "/");
    if (lines > MAX) {
      if (baseline.has(rel)) exempted.push({ rel, lines });
      else errors.push({ rel, lines });
    } else if (lines > WARN) warnings.push({ rel, lines });
  }
}

targets.forEach(walk);

warnings.sort((a, b) => b.lines - a.lines);
errors.sort((a, b) => b.lines - a.lines);

if (exempted.length) {
  console.log(`\nℹ️  基线豁免（底座/生成文件，>${MAX} 行但暂不强制）：`);
  for (const e of exempted) console.log(`   ${e.lines}  ${e.rel}`);
}

if (warnings.length) {
  console.log(`\n⚠️  接近上限（${WARN}–${MAX} 行，建议拆分）：`);
  for (const w of warnings) console.log(`   ${w.lines}  ${w.rel}`);
}

if (errors.length) {
  console.error(`\n❌  超出上限（>${MAX} 行，必须拆分）：`);
  for (const e of errors) console.error(`   ${e.lines}  ${e.rel}`);
  console.error(`\n行数检查未通过：${errors.length} 个文件超过 ${MAX} 行。\n`);
  process.exit(1);
}

console.log(
  `\n✅  行数检查通过（无文件超过 ${MAX} 行${warnings.length ? `，${warnings.length} 个文件接近上限` : ""}）。\n`
);
