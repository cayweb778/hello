<template>
  <DesktopZoom></DesktopZoom>
  <div id="saddas" v-show="showGdzc">
<!--    <HelloMicro></HelloMicro>-->
  </div>
 <div  v-show="!showGdzc">
   <ConfigProvider :getPopupContainer="getPopupContainer" :locale="zhCN"><!--:locale="useLocale().getAntdLocale"-->
     <AppProvider>
       <RouterView v-slot="{Component}">
         <!--    ZOOM_FADE = 'zoom-fade',-->
         <!--    ZOOM_OUT = 'zoom-out',-->
         <!--    FADE_SIDE = 'fade-slide',-->
         <!--    FADE = 'fade',-->
         <!--    FADE_BOTTOM = 'fade-bottom',-->
         <!--    FADE_SCALE = 'fade-scale',-->
         <transition
           name="fade-slide"
           mode="in-out"
           appear>
           <component :is="Component"/>
         </transition>
       </RouterView>
     </AppProvider>
   </ConfigProvider>
 </div>
</template>

<script lang="ts" setup>
import {AppProvider} from '/@/components/Application';
import {useTitle} from '/@/hooks/web/useTitle';
import {useLocale} from '/@/locales/useLocale';
import {ConfigProvider} from 'ant-design-vue';
import DesktopZoom from '/@/boozsoft/components/DesktopComponents/DesktopZoom/index.vue'
import 'dayjs/locale/zh-cn';
import zhCN from 'ant-design-vue/es/locale/zh_CN';
import {getPopupContainer} from "/@/boozsoft/designs/antdFuns";
// import HelloMicro from './SubApps/Gdzc.vue'
import {ref} from "vue";
import {usePlatformsStoreWidthOut} from "/@/store/modules/platforms";
import {defineCacheShowGdzc} from "/@/boozsoft/defineCacheShowGdzc";
useTitle();
const showGdzc=ref(false)

const {setCacheShowGdzc,getCacheShowGdzc}=defineCacheShowGdzc(showGdzc)


// 跳转平台监听
window?.$wujie.bus.$on("goGuDingZiChan", function (id) {
  setCacheShowGdzc(true)
  // /** 设定延时请求( 比如router.ready() )，防止首次加载,组件还未渲染 start **/
  window?.$wujie.bus.$emit('goGuDingZiChanxxxx',id)
  /** 非该模块的平台，抛出异常，弹出错误 start **/

  /**  end **/


  /** end **/
});

window?.$wujie.bus.$on("goPlatform", function (id) {
  setCacheShowGdzc(false)
  const layoutsStore = usePlatformsStoreWidthOut();


  layoutsStore.switchPlatform({ id})
});

</script>
<style lang='less' src="./assets/styles/boozsoft-public.less" scoped></style>
<style>
.ant-modal-wrap{
  left: 0 !important;
  top: 0 !important;
  width:100% !important;
  height:100% !important;
  position: fixed;
  z-index: 1000;
  overflow: auto;
  outline: 0;
  -webkit-overflow-scrolling: touch;
}
</style>
