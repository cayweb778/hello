<template>
<!--  <div style="width:100vw;height:100vh">-->
<!--    <HelloMicro></HelloMicro>-->
<!--  </div>-->
  <div style="width:100vw;height:100vh">
    <ServerError v-if="showServerError"></ServerError>
    <App2 v-else></App2>
  </div>
</template>
<script setup>
import App2 from './App2.vue'
import {usePlatformsStoreWidthOut} from "/@/store/modules/platforms";
import {defineCacheShowGdzc} from "/@/boozsoft/defineCacheShowGdzc";
// import HelloMicro from './SubApps/Gdzc.vue'
import {ref} from "vue";

const showGdzc=ref(false)
const showServerError=ref(true)

const {setCacheShowGdzc,getCacheShowGdzc}=defineCacheShowGdzc(showGdzc)


window?.$wujie.bus.$on("goPlatform", function (id) {
  setCacheShowGdzc(false)
  const layoutsStore = usePlatformsStoreWidthOut();


  layoutsStore.switchPlatform({ id})
});

// 跳转平台监听
window?.$wujie.bus.$on("goGuDingZiChan", function (id) {
  setCacheShowGdzc(true)
  // /** 设定延时请求( 比如router.ready() )，防止首次加载,组件还未渲染 start **/
  window?.$wujie.bus.$emit('goGuDingZiChanxxxx',id)
  /** 非该模块的平台，抛出异常，弹出错误 start **/

  /**  end **/


  /** end **/
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
