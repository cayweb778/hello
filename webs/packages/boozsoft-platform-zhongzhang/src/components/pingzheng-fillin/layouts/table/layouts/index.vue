<template>
  <div class="pingzheng_editor main-view-item" style="height: 100%" ref="pingzhengEditorRef">
    <div
      class="helloaadsd"
      v-show="showHelloRef2"
      ref="helloRef2"
      style="
        position: fixed;
        width: 100%;
        height: 100%;
        background: #00000099;
        z-index: 100000000;
        left: 0;
        top: 0;
      "
    ></div>
    <div class="errorPage" style="position: fixed; width: 100%; height: 100%"></div>
    <slot></slot>
  </div>
</template>
<script setup>
  import { provide, ref } from 'vue';
  const helloRef2 = ref();
  const showHelloRef2 = ref(false);
  provide('usePopoverContainer', {
    open() {
      showHelloRef2.value = true;
    },
    close() {
      showHelloRef2.value = false;
    },
    getContainer() {
      return helloRef2.value;
    },
  });
</script>
