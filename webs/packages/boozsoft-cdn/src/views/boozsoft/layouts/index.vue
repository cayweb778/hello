<template>
  <div style="height:100%">
    <div style="display: flex;justify-content:space-between;position:fixed;left:0;top:0;width:100%;height:100%">
      <LeftSider></LeftSider>
      <div class="bbbasdsa" style="translate(0px) !important;
    width: 100%;
    height: 100vh;
    transform: translate(0);
   "><slot/></div>
    </div>
  </div>
</template>
<script setup lang="ts">
import LeftSider from './LeftSider/index.vue'

</script>
