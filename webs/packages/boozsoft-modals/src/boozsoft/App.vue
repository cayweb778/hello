<template>
  <div>
    <div>NC 弹出框模块</div>
    <div>当前环境: 图形化 可选:[函数，图形化,演示]</div>
    <div>
      <div>dept</div>
      <div>emp</div>
      <div>project</div>
      <div>gdy</div>
    </div>
  </div>
</template>
<script setup>
import {ref} from 'vue'
import {defineNcModals} from "/@/views/boozsoft/global/funs";
const ncModals=ref(defineNcModals())
window.$wujie.$emit("registerNcModals",ncModals)
</script>
