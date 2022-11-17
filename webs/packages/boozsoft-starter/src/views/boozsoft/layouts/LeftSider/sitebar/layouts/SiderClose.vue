<template>
  <div class="showPromissSiteBar" style="z-index: 111111;user-select:none;" @click="leftSiderFun.showOpenButton()">
    <span style="transform: scale(1, 1.5); display: inline-block" v-html="'<'"> </span>
  </div>
</template>
<script setup lang="ts">
import {provide,inject} from "vue";

const leftSiderFun=inject('leftSiderFun')
</script>
<style scoped>
.showPromissSiteBar:hover{
  background: white
}
.showPromissSiteBar {
  height: 40px;
  width: 22px;
  padding: 9px 0px 0px 7px;
  box-sizing: border-box;
  position: absolute;
  right: 0px;
  top: calc((100% - 0px) / 2 - 100px);
  opacity: 3.5;
  background: rgba(229, 229, 229, 0.5);
  color: black;
  cursor: pointer;
  border-radius: 5px 0px 0px 5px;
  font-size: 10px;
}
</style>
