<template>
  <div style="position:fixed;left:0;top:0;">
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
const showServerError=ref(true)
function abc(){
  const socket=new WebSocket(props.wujieAttrs.socketAddr+'/pingServer')
  socket.onopen=()=>{
    showServerError.value=false
  }

  socket.onerror=()=>{
    window.closeLoading()
    showServerError.value=true
  }
}
if(props.wujieAttrs.socketAddr==null){
  showServerError.value=false
}else{
  abc()

}
</script>