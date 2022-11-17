<script setup lang="ts">
// This starter template is using Vue 3 <script setup> SFCs
// Check out https://vuejs.org/api/sfc-script-setup.html#script-setup
import Greet from "../../components/Greet.vue";
// let abc=window.__TAURI__.window.WebviewWindow
import {onMounted, ref} from 'vue'
import {useDesktopStoreWidthOut} from "../../store/modules/hello";
import {invoke} from "@tauri-apps/api/tauri";
import {Form, Button, Input, Space, TypographyText, TypographyLink,Descriptions,DescriptionsItem} from 'ant-design-vue'

const Item = Form.Item

const hello = ref('127.0.0.1:3000')

function openAbc() {
  addr.value = hello.value
  useDesktopStoreWidthOut().goApp(hello.value)
}

const addr = ref()
const abc11 = ref()
onMounted(async () => {
  addr.value = JSON.parse(await invoke("getCacheIpAddrApi")).data;
  console.log(addr.value)
  hello.value = addr.value
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
import {readDir, removeDir,BaseDirectory} from '@tauri-apps/api/fs';

async function clearCache() {
      await removeDir('org.boozsoft.ncapp', {dir: BaseDirectory.LocalData,recursive:true})
          .then((e)=>{
            useDesktopStoreWidthOut().goApp(hello.value)
            abc11.value=e
          })
          .catch((e)=>{
            useDesktopStoreWidthOut().goApp(hello.value)
    abc11.value=e
  })
  // console.log("dddd")
  // console.log(aaaa)
  // const aaa=await appLocalDataDir()
  console.log("wujie")
 // const ddddd= await removeDir('', {dir: BaseDirectory.Cache,recursive:true})
 //      .then(() => {
 //
 //        useDesktopStoreWidthOut().goApp(hello.value)
 //      })
 //      .finally(() => {
 //
 //        useDesktopStoreWidthOut().goApp(hello.value)
 //      });
}
</script>

<template>
  <div class="helloconfig"
       style="text-align: center;border-radius:30px;position:fixed;left:0;width:100vw;top:0;height:100vh;background:#2b2b2b;color:white">
    <Space direction="vertical" style="padding:10px 5px">
      <h2 style="color:white;padding:0;margin:0;position:absolute;width:100vw;text-align:center">财税达配置中心</h2>
      <Descriptions bordered title="" :size="'small'">
        <template #extra>
          <Button type="primary">编辑</Button>
        </template>

         <DescriptionsItem :span="4"  label="当前服务器地址">
          <Input style="width:300px" v-model:value="hello"/>
          <Button @click="funasd">🚀</Button>
          <Button @click="openAbc">确认</Button>
        </DescriptionsItem>
        <DescriptionsItem label="打印控件配置"><Button>配置</Button></DescriptionsItem>
<!--        <DescriptionsItem label="表格控件配置"><Button>配置</Button></DescriptionsItem>-->
<!--        <DescriptionsItem label="财税达文档"><Button>配置</Button></DescriptionsItem>-->
        <DescriptionsItem label="财税达官网"><Button>配置</Button></DescriptionsItem>
        <DescriptionsItem label="泊舟官网"><Button>配置</Button></DescriptionsItem>
        <DescriptionsItem label="清除缓存"><Button @click="clearCache">清理</Button></DescriptionsItem>
        <DescriptionsItem label="卸载"><Button>配置</Button></DescriptionsItem>
        <DescriptionsItem label="退出"><Button>配置</Button></DescriptionsItem>

      </Descriptions>
    </Space>
      <div style="color:white;height:100px;width:100vw;background:black">111{{abc11}}222</div>
  </div>
</template>

<style scoped>
/*.helloconfig :deep(*) {*/
/*  color: white !important;*/
/*}*/

.logo.vite:hover {
  filter: drop-shadow(0 0 2em #747bff);
}

.logo.vue:hover {
  filter: drop-shadow(0 0 2em #249b73);
}
</style>
