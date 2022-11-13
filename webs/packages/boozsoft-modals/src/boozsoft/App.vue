<template>
  <ConfigProvider  :locale="zhCN"><!--:locale="useLocale().getAntdLocale"-->
    <AppProvider>
<!--      <div>NC 弹出框模块</div>-->
<!--      <div>当前环境: 图形化 可选:[函数，图形化,演示]</div>-->
<!--      <div>-->
<!--        <div>dept</div>-->
<!--        <div>emp</div>-->
<!--        <div>project</div>-->
<!--        <div>gdy</div>-->
<!--      </div>-->
        <NcModals></NcModals>
    </AppProvider>
  </ConfigProvider>

</template>

<script lang="ts" setup>
import NcModals from './NcModals.vue'
import {AppProvider} from '/@/components/Application';
// import {useTitle} from '/@/hooks/web/useTitle';
import {ConfigProvider} from 'ant-design-vue';
// import 'dayjs/locale/zh-cn';
import zhCN from 'ant-design-vue/es/locale/zh_CN';
// import {getPopupContainer} from "/@/boozsoft/designs/antdFuns";
import {usePlatformsStoreWidthOut} from "/@/store/modules/platforms";
// const layoutsStore = usePlatformsStoreWidthOut();


// const newPlatformId=window.localStorage.getItem("newPlatformId")
// layoutsStore.switchPlatform({ id:newPlatformId})


// window?.$wujie.bus.$on("goStock", function (id) {
// });
// useTitle();
</script>
<style>
body{
  background:rgba(0,0,0,0) !important;
}
</style>
