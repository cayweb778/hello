<template>
  <div>

    <Print></Print>
  </div>
</template>
<script setup>
import Print from './Print/index.vue'
// window.$wujie.$on('startprint',function(){
//
// })
window.$wujie.bus.$emit("registerNcDataExport", function(){
  window.$wujie.bus.$emit('setCurrentPluginName', 'ncPrint')
})
</script>
