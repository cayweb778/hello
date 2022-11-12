<template>
  <div class="plugin_container" style="position:fixed;left:0;width:100vw;height:100vh;top:0;background:gray;z-index:10000000000">
    <div style="display:flex">
      <div>
        <LeftSider/>
      </div>
      <div>

        <Content/>

      </div>
    </div>
  </div>
</template>
<script setup>
import LeftSider from './LeftSider.vue'
import Content from './content/index.vue'
</script>