<template>
  <div class="icon-color" style="font-size:16px;margin: 20px 0px 0px">
    <ul style="padding-left:0;">
      <li
          @click="toGoPlatformById"
          :style="{background: 'rgba(27, 154, 238, 0.1)'}"
      >
        <div style="padding-left:8px">
          <span class="this_ico">
            <HomeOutlined></HomeOutlined>
          </span>
<!--          <img src="../../../../../../assets/svg/system_option/app2.svg"-->
<!--               style="margin:-1px auto 0;width: 15px;"/>-->
        </div>
        <span style="margin-left: 20px"
        >NC工作台</span>
      </li>
    </ul>
  </div>
</template>
<script setup lang="ts">

import {HomeOutlined} from '@ant-design/icons-vue';
import {goHome, goPlatformById} from "/@/views/boozsoft/layouts/LeftSider/sitebar/layouts/SiderGroup/funs";
function toGoPlatformById(){
  goHome()
}
</script>
<style scoped>
.icon-color:hover{
  background:white;
  color:black !important;
}
.icon-color:hover .this_ico{
  color:black !important;

}

.icon-color .this_ico{
  color:#929191
}
.icon-color:hover .this_ico{
  color:white
}
</style>
