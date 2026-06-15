<script setup lang="ts">
import { useRouter } from "vue-router";

defineOptions({ name: "Home" });

const router = useRouter();
const goTrade = () => router.push("/trade");
</script>

<template>
  <div class="page">
    <van-nav-bar title="QOVF 用户端" />
    <div class="content">
      <p>移动端外汇交易平台 · 框架底座（空）</p>
      <van-button type="primary" block @click="goTrade">
        前往行情 / 交易
      </van-button>
    </div>
  </div>
</template>

<style scoped>
.page {
  height: 100%;
}

.content {
  padding: 24px 16px;
  text-align: center;
}

.content p {
  margin-bottom: 20px;
  color: #646566;
}
</style>
