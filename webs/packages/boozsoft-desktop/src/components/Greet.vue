<script setup lang="ts">
import {onMounted, ref} from "vue";
import {invoke} from "@tauri-apps/api/tauri";

import {
  WebviewWindow,
  WebviewWindowHandle,
  WindowManager,
  CloseRequestedEvent,
  getCurrent,
  getAll,
  appWindow,
  LogicalSize,
  PhysicalSize,
  LogicalPosition,
  PhysicalPosition,
  UserAttentionType,
  currentMonitor,
  primaryMonitor,
  availableMonitors
} from '@tauri-apps/api/window'

const greetMsg = ref("");
const name = ref("127.0.0.1:3000");

async function getAddr() {
  return await invoke("getCacheIpAddr", {name: name.value});
}

;(async function(){
  const addrJSON = await getAddr()
  const addrObj=JSON.parse(<string>addrJSON)
  if (addrObj.code != '404') {
    console.log("http://"+addrObj.data)
    window.location.href = "http://"+addrObj.data+"/nc"

  }
})();
const showError=ref('')
async function greet() {
  const  ws=new WebSocket("ws://"+name.value+"/api/nc/zongzhang/pingServer")
  const abcc=async function (){

    console.log(";;;;;;;1")
    console.log(name.value)
    console.log(";;;;;;;1")
    const aaa=await invoke("generate", {name: name.value});
    console.log(";;;;;;;2")
    console.log(aaa)
    console.log(";;;;;;;2")
    window.location.href = "http://"+name.value+"/nc"
  }
  ws.onopen=abcc
  ws.onerror=function (){
    showError.value="地址无效，请检查拼写或服务器状态是否正常"
  }

  // Learn more about Tauri commands at https://tauri.app/v1/guides/features/command

}

function abc(){
  name.value="81.70.47.206:81"
}
</script>

<template>
  <div class="card">
    <div style="color:red">{{showError}}</div>
    <input id="greet-input" v-model="name" placeholder="Enter a name..."/>
    <button type="button" @click="abc()">🚀</button>
    <button type="button" @click="greet">开始</button>
  </div>

  <p>{{ greetMsg }}</p>
</template>
