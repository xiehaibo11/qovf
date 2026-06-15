# TradingView Charting Library

此目录用于放置 TradingView 官方 **Charting Library**（需向 TradingView 申请授权后获取）。

接入步骤：
1. 将官方 `charting_library/` 内容复制到此目录。
2. 在根目录 `index.html` 中取消注释 `charting_library.min.js` 的 `<script>` 引用。
3. 在 `src/views/trade/index.vue` 的 `onMounted` 中初始化 `TradingView.widget`。
