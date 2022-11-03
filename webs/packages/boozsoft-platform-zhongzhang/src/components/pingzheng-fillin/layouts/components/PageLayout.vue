<template>
  <slot v-if="reloadPageState"></slot>
  <!--  <debugger-info />-->
</template>
<script setup lang="ts">
  import { nextTick, provide, ref } from 'vue';

  const reloadPageState = ref(true);

  function reloadPage() {
    reloadPageState.value = false;
    nextTick(() => {
      reloadPageState.value = true;
    });
  }
  provide('reloadPage', reloadPage);
</script>
<style>
  .pingZhengPageContainer {
    border-radius: 4px;
    min-width: 1010px;
    width: calc(100% - 20px);
    height: calc(100% - 30px);
    overflow: hidden;
  }
</style>
