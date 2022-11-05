<template>
  <KuaiJiKeMuProvider>
    <KuaiJiKeMuContent v-bind="$attrs" />
  </KuaiJiKeMuProvider>
</template>
<script lang="ts" setup>
  import KuaiJiKeMuProvider from '/@/components/pingzheng-fillin/layouts/table/columns/KuaiJiKeMu/provider/index.vue';
  import KuaiJiKeMuContent from '/@/components/pingzheng-fillin/layouts/table/columns/KuaiJiKeMu/layouts/content/index.vue';
</script>
