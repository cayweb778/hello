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
  return await invoke("asdsadas", {name: name.value});
}

;(async function(){
  const addr = await getAddr()
  if (addr != null) {
    console.log("http://"+addr)
    window.location.href = "http://"+addr+"/nc"
  }
})();

async function greet() {
  await invoke("asdsadas", {name: name.value});
  window.location.href = name.value
  // Learn more about Tauri commands at https://tauri.app/v1/guides/features/command

}
</script>

<template>
  <div class="card">
    <input id="greet-input" v-model="name" placeholder="Enter a name..."/>
    <button type="button" @click="greet()">开始</button>
  </div>

  <p>{{ greetMsg }}</p>
</template>
