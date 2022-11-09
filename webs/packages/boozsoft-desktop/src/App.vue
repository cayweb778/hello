<template>
  <div>

    <App2 v-if="config.showFirstPage" @ok="handleOk"></App2>
    <iframe v-else :src="config.url" style="border:none;position: fixed;left:0;top:0;width:100vw;height:100vh"></iframe>
    <ConfigModal v-if="config.showConfigModal"></ConfigModal>
  </div>
</template>
<script setup>
import {ref,onMounted} from 'vue';
import App2 from './App2.vue'
import ConfigModal from './ConfigModal.vue'
import {invoke} from "@tauri-apps/api/tauri";
import {getAddr} from "./funs/index.ts";

const config=ref({
  url:'',
  showFirstPage:true,
  showConfigModal:true
})


onMounted(async ()=>{
  var WebviewWindow = window.__TAURI__.window.WebviewWindow
  const webview = new WebviewWindow('theUniqueLabel', {
    url: 'https://tauri.app/v1/guides/features/multiwindow/',
  })

  const addrObj = await getAddr()
  if(addrObj.code != '404'){
    config.value.showFirstPage=false
    config.value.url="http://"+addrObj.data+"/nc"
  }else{
    config.value.showFirstPage=true
  }
})



function handleOk(e){
  config.value.url=e
}
</script>