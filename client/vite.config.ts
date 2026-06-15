import { fileURLToPath, URL } from "node:url";
import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import Components from "unplugin-vue-components/vite";
import { VantResolver } from "@vant/auto-import-resolver";

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // Vant 组件按需自动导入（含样式）
    Components({
      resolvers: [VantResolver()],
      dts: "src/components.d.ts"
    })
  ],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url))
    }
  },
  server: {
    host: "0.0.0.0",
    port: 5174,
    proxy: {
      // 后端 API 代理，按需修改 target
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true
      }
    }
  }
});
