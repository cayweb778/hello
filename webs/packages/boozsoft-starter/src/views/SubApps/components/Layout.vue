<template>
  <div style="position:fixed;left:0;top:0;">
    <slot v-if="!showServerError"></slot>
    <div v-else>该模块后端未启动</div>
  </div>
</template>
<script setup>
import {ref} from 'vue'
const props=defineProps(['socketAddr'])
const socket=new WebSocket(props.socketAddr+'/pingServer')
const showServerError=ref(true)
socket.onopen=()=>{
  showServerError.value=false
}

socket.onerror=()=>{
  window.closeLoading()
  showServerError.value=true
}
</script>