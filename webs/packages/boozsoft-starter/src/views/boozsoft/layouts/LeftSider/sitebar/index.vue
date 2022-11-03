<template>
  <div  class="left_sider_main">
    <div class="booz_nav booz_nav1">
      <div style="top:-5px;right:0;height:30px;position: absolute">
      </div>
      <SiderHeader></SiderHeader>
      <SiderClose></SiderClose>
      <NcDesktopSider></NcDesktopSider>
      <div style="height: calc(100% - 168px);overflow-y:scroll">
        <AppSider></AppSider>
        <ManagerSider></ManagerSider>
        <BasicRecordSider></BasicRecordSider>

      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import SiderHeader from './layouts/SiderHeader.vue'
import SiderClose from './layouts/SiderClose.vue'
import NcDesktopSider from './layouts/SiderGroup/NcDesktopSider.vue'
import AppSider from './layouts/SiderGroup/AppSider.vue'
import ManagerSider from './layouts/SiderGroup/ManagerSider.vue'
import BasicRecordSider from './layouts/SiderGroup/BasicRecordSider.vue'
</script>
<style scoped>
svg {
  width: 20px;
}

:deep( li) {
  padding: 10px 16px;
  text-align: left;
  display: flex;
  height: auto;
  -webkit-align-items: center;
  -ms-flex-align: center;
  align-items: center;
  line-height: 20px;
  transition: none;
  cursor: pointer;
  border-radius: 4px;
}

.booz_nav1 {
  position: relative;
  width: 100%;
  background: rgb(246, 248, 252);
  height: 100%;
  border: 1px solid black;
  overflow: hidden;
}


.booz_nav.booz_nav1 {
  background: #0c2556;
  color: white;
  width: 228px;
  height: calc(100%);
}

.left_sider_main {
  min-width: 228px;
  width: 228px;
  height: 100%
}
</style>
