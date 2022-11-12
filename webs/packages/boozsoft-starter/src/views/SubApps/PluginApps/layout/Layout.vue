<template>
  <div>
    <div v-if="showModuleError">模块未加载，请联系管理员</div>
    <div v-else>
      <slot v-if="!showServerError"></slot>
      <div v-else>该模块后端未启动,前端路径：{{props.wujieAttrs.url}}</div>
    </div>
  </div>
</template>
<script setup>
import {ref} from 'vue'
const props=defineProps(['wujieAttrs'])

const showModuleError=ref(true)
fetch(props.wujieAttrs.url).then(e=>{
  showModuleError.value=false
}).catch(e=>{
  showModuleError.value=true
})
const showServerError=ref(false)

</script>