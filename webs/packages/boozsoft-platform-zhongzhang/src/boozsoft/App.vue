<template>
  <div style="width:100vw;height:100vh">
    <App2 ></App2>
  </div>
</template>
<script setup>
import App2 from './App2.vue'
import {usePlatformsStoreWidthOut} from "/@/store/modules/platforms";
import {defineCacheShowGdzc} from "/@/boozsoft/defineCacheShowGdzc";
import HelloMicro from './SubApps/Gdzc.vue'
import {ref} from "vue";

const showGdzc=ref(false)
const showServerError=ref(true)

const {setCacheShowGdzc,getCacheShowGdzc}=defineCacheShowGdzc(showGdzc)


window?.$wujie.bus.$on("goZongzhang", function (id) {
  setCacheShowGdzc(false)
  const layoutsStore = usePlatformsStoreWidthOut();


  layoutsStore.switchPlatform({ id})
});


const aaa=new WebSocket(import.meta.env.VITE_WEBSOCKET+"/pingServer")
import ServerError from './ServerError.vue'
aaa.onerror=()=>{
  window.closeLoading()
  showServerError.value=true
}
aaa.onopen=()=>{
  showServerError.value=false

}
</script>
<style lang='less' src="./assets/styles/boozsoft-public.less" scoped></style>
