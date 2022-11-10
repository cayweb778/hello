<template>
<!--  <div style="width:100vw;height:100vh">-->
<!--    <HelloMicro></HelloMicro>-->
<!--  </div>-->
  <div style="width:100vw;height:100vh">
    <App2 ></App2>
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


const layoutsStore = usePlatformsStoreWidthOut();
const newPlatformId=window.localStorage.getItem("newPlatformId")
layoutsStore.switchPlatform({ id:newPlatformId})
window?.$wujie.bus.$on("goPlatform", function (id) {
  setCacheShowGdzc(false)
  const layoutsStore = usePlatformsStoreWidthOut();


  layoutsStore.switchPlatform({ id})
});



</script>
<style lang='less' src="./assets/styles/boozsoft-public.less" scoped></style>
