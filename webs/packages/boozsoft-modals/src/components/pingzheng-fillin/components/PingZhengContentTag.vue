<template>
  <div class="pingzhengcontentTagContainer">
    <div
      class="pingzhengcontentTag"
      :style="{background:modelValue=='查看'?'rgb(65 184 131)':'#6666ff'}">
    </div>
    <div
      class="pingzhengcontentTagFont"
      :style="{color:modelValue=='查看'?'black':'white'}"
      v-text="modelValue"></div>
  </div>
</template>
<script setup>
const props = defineProps(['modelValue'])
</script>
<style scoped>
.pingzhengcontentTag {
  width: 100px;
  height: 100px;
  margin-left: -44px; margin-top: -66px;
  box-shadow: lightgrey 0px 0px 0px 2px;
  transform: rotate(54deg);
  background: rgb(65, 184, 131);
}

.pingzhengcontentTagFont {
  position: absolute;
  color: white;
  top: 7px;
  font-size: 12px;
  left: 8px;
  letter-spacing: 1px;

}

.pingzhengcontentTagContainer{
  position: fixed;
  top:0;
  left:0;
  width: 100px; height: 100px; z-index: 1000000000000;
}
</style>
