<template>
  <div class="plugin_container" style="position:fixed;left:0;width:100vw;height:100vh;top:0;background:gray;z-index:10000000000">
    <slot/>
  </div>
</template>
<script setup>
import {ref} from 'vue'
const showPluginWork=ref(false)
</script>