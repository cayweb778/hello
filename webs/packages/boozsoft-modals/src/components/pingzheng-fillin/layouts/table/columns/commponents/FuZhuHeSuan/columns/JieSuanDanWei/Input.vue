<template>
  <div @click="click"  :class="{fuZhuHeSuanInputClass:true,'JieSuanFangShiFocus':isFocus}">
    <input  style="opacity: 0;" @blur="blur" ref="inputRef"/>
  </div>
</template>
<script setup>
import {ref} from 'vue'
const isFocus=ref(false)
const inputRef=ref()

function click(){
  console.log(888)
  isFocus.value=true
  inputRef.value.focus()
}
function blur(){
  console.log(111222)
}
</script>
<style>
.JieSuanFangShiFocus{
  border-radius: 4px;border:solid 1px red !important;height:30px;
}
</style>