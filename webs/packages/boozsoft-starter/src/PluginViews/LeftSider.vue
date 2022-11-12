<template>
  <div style="height:100vh;width:20vw;background:#0c2556">
    <div style="text-align:center">组件库</div>
    <div>
      <Button style="width:100%;color:black" @click="a1">弹出框组件</Button>
    </div>
    <div>
      <Button style="width:100%;color:black" @click="a2">打印组件</Button>
    </div>
    <Skeleton />
    <Skeleton />
    <Skeleton />
    <Skeleton />
    <Skeleton />
  </div>
</template>
<script setup>
import {usePluginsStoreWidthOut} from "../store/modules/plugins";
import {Button, Select, Form,Divider,Skeleton} from "ant-design-vue";


function a1() {
  usePluginsStoreWidthOut().setCurrentPluginName("modals")

}

function a2() {
  usePluginsStoreWidthOut().setCurrentPluginName("print")

}
</script>