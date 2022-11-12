<script setup lang="ts">
import { ref} from "vue";
import { open } from '@tauri-apps/api/shell';
window.tauriOpenWindow=open
const  showApp=ref(false)
if(window.__TAURI__!=null){
  showApp.value=true
}
</script>

<template>
  <slot v-if="showApp"></slot>
  <div v-else>
    NC暂不支持网页端，请使用桌面端
  </div>
</template>

<style scoped>

</style>
