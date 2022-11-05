<template>
  <div style="position: fixed;
    width: 100%;
    height: 100%;
    top: 0;
    left: 0;
    z-index: 111111111;">
    <div class="dasddsasadsa"
         style="position:relative;background:#cceaf4;box-shadow:#597fa1 0px 1px 7px 1px;width:1193px;border-radius:10px;height:calc(100% - 37px);margin:20px auto 0;overflow:hidden;">
      <div style="background:#e3e3e3;padding:5px">填制凭证</div>
      <div style="position:absolute;right:12px;top:5px;color:black;cursor:pointer;" @click="closeThis">
        <CloseSquareOutlined/>
      </div>
      <slot></slot>
    </div>
  </div>
</template>
<script setup>
import {Button} from 'ant-design-vue'
import {
  SearchOutlined, PlusOutlined, SettingOutlined, CloseSquareOutlined
} from '@ant-design/icons-vue'
import {inject} from "vue";
const closePingzhengModal=inject('closePingzhengModal')

function closeThis() {
  closePingzhengModal()
}
</script>
