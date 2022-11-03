<template>
  <FuZhuHeSuanProvide>
    <FuZhuHeSuanIndex v-bind="$attrs" />
  </FuZhuHeSuanProvide>
</template>
<script setup>
  import FuZhuHeSuanProvide from './layouts/provider/index.vue';
  import FuZhuHeSuanIndex from './layouts/index.vue';
</script>
