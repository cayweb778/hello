<template>
  <div
    v-if="true"
    style="
      position: fixed;
      left: 0;
      top: 0;
      width: 100%;
      height: 100%;
      background: gray;
      z-index: 1111111;
    "
  >
    说明书
    <div @click="openDebuggerInfo">debuggerInfo</div>
    <div>凭证模型</div>
    <div>凭证单号</div>
    <input v-model="abc" @keyup.enter="ok" />
    <textarea v-model="info"></textarea>
  </div>
</template>
<script setup>
  import { ref } from 'vue';
  const abc = ref('');
  const info = ref('');

  function ok(e) {
    info.value = JSON.stringify(window[e.target.value]);
  }

  function openDebuggerInfo() {
    console.log(window.debuggerInfo);
  }
</script>
