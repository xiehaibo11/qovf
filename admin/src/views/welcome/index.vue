<script setup lang="ts">
defineOptions({
  name: "Welcome"
});
</script>

<template>
  <div class="welcome-wrap">
    <el-card shadow="never" class="welcome-card">
      <div
        v-motion
        :initial="{ opacity: 0, y: 60 }"
        :enter="{ opacity: 1, y: 0, transition: { delay: 80 } }"
      >
        <h1 class="title">管理系统底座</h1>
        <p class="subtitle">
          基于 vue-pure-admin 二次开发 · 已清理业务页面，仅保留布局、主题与全部 UI 组件
        </p>
        <el-divider />
        <p class="tip">
          在 <code>src/views</code> 下新增业务页面，并在
          <code>src/router/modules</code> 中注册路由即可开始开发。
        </p>
      </div>
    </el-card>
  </div>
</template>

<style lang="scss" scoped>
.welcome-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 160px);
}

.welcome-card {
  width: 100%;
  max-width: 720px;
  text-align: center;

  :deep(.el-card__body) {
    padding: 48px 36px;
  }
}

.title {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.subtitle {
  margin-top: 12px;
  font-size: 15px;
  color: var(--el-text-color-regular);
}

.tip {
  font-size: 14px;
  color: var(--el-text-color-secondary);

  code {
    padding: 2px 6px;
    margin: 0 2px;
    font-size: 13px;
    color: var(--el-color-primary);
    background-color: var(--el-fill-color-light);
    border-radius: 4px;
  }
}
</style>
