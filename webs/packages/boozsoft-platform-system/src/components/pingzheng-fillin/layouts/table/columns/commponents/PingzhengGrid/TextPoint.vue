<template>
  <div
    style="height: 100%; width: 100%"
  >
    <slot v-if="showSlot"></slot>
    <TextGrid v-else :modelValue="showText" :tooltip="tooltipText" :white="isWhite" />
  </div>
</template>
<script setup>
import {computed} from "vue";
import TextGrid from '/@/components/pingzheng-fillin/layouts/table/columns/commponents/PingzhengGrid/components/TextGrid.vue';

const props=defineProps(['onlyShow','showSlot','showText','tooltipText','isWhite'])
const onlyShow=computed(()=>props.onlyShow)
const showSlot=computed(()=>props.showSlot)
const showText=computed(()=>props.showText)
const tooltipText=computed(()=>props.tooltipText)
const isWhite=computed(()=>props.isWhite)
</script>
<style>
.helloTextTest {
  font-weight: 900;
  word-break: break-all;
  height: 100%;
  text-align: left;
  position: absolute;
  z-index: 10000000;
  width: 100%;
}
</style>
