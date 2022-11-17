<script setup lang="ts">
// This starter template is using Vue 3 <script setup> SFCs
// Check out https://vuejs.org/api/sfc-script-setup.html#script-setup
import Greet from "../../components/Greet.vue";
// let abc=window.__TAURI__.window.WebviewWindow
import {onMounted, ref} from 'vue'
import {useDesktopStoreWidthOut} from "../../store/modules/hello";
import {invoke} from "@tauri-apps/api/tauri";

const hello = ref('127.0.0.1:3000')

function openAbc() {
  addr.value=hello.value
  useDesktopStoreWidthOut().goApp(hello.value)
}
const addr=ref()
onMounted(async ()=>{
  addr.value=JSON.parse(await invoke("getCacheIpAddrApi")).data;
  hello.value=addr.value
  console.log(addr.value)
})
function funasd() {
  if (hello.value == '127.0.0.1:3000') {
    hello.value = '81.70.47.206:81'
    return
  }
  if (hello.value == '81.70.47.206:81') {
    hello.value = '127.0.0.1:3000'
    return
  }
  hello.value = '127.0.0.1:3000'

}
import {appLocalDataDir} from '@tauri-apps/api/path';
import {removeDir,BaseDirectory} from '@tauri-apps/api/fs';
async function clearCache(){
  // const aaa=await appLocalDataDir()
  console.log("wujie")
  await removeDir('', { dir: BaseDirectory.AppLocalData})
      .then(()=>{

        useDesktopStoreWidthOut().goApp(hello.value)
      })
      .finally(()=>{

    useDesktopStoreWidthOut().goApp(hello.value)
  });
}
</script>

<template>
  <div style="text-align: center">
    我是配置页面
    <div>当前服务器地址: {{addr}}</div>
    <div>服务器地址：<input v-model="hello"/>
      <button @click="funasd">🚀</button>
      <button @click="openAbc">确认</button>
    </div>
    <div>
      <button>打印控件配置</button>
    </div>
    <div>
      <button>表格控件配置</button>
    </div>
    <div>
      <button>卸载</button>
    </div>

    <div>
      <button>财税达文档</button>
    </div>
    <div>
      <button>财税达官网</button>
    </div>
    <div>
      <button>泊舟官网</button>
    </div>
    <div>
      <button @click="clearCache">清除缓存</button>
    </div>
    <div>
      <button>完成</button>
    </div>
  </div>
</template>

<style scoped>
.logo.vite:hover {
  filter: drop-shadow(0 0 2em #747bff);
}

.logo.vue:hover {
  filter: drop-shadow(0 0 2em #249b73);
}
</style>
