<script setup lang="ts">
import { onMounted, ref } from "vue";

defineOptions({ name: "Trade" });

const chartContainer = ref<HTMLDivElement | null>(null);

onMounted(() => {
  // TradingView Charting Library 接入占位：
  // 1. 将官方 charting_library 放入 public/charting_library
  // 2. 在 index.html 启用 charting_library.min.js 引用
  // 3. 在此处通过 new (window as any).TradingView.widget({ ... }) 初始化
  // const widget = new (window as any).TradingView.widget({
  //   container: chartContainer.value,
  //   library_path: "/charting_library/",
  //   symbol: "EURUSD",
  //   interval: "60",
  //   locale: "zh"
  // });
});
</script>

<template>
  <div class="trade">
    <header class="bar">行情 / 交易</header>
    <div ref="chartContainer" class="chart">
      <span class="placeholder">TradingView 图表占位区</span>
    </div>
  </div>
</template>

<style scoped>
.trade {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.bar {
  padding: 14px 16px;
  font-weight: 600;
  background: #fff;
}

.chart {
  display: flex;
  flex: 1;
  align-items: center;
  justify-content: center;
}

.placeholder {
  color: #8a919f;
}
</style>
