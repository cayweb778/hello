<template>
  <div
      style="position:relative;margin-bottom:0px;  padding:10px;  background: rgb(241, 241, 241);height:97px;border-radius: 10px 10px 0 0;">
    <Index2  v-model="pingzhengViewModel"></Index2>
  </div>
</template>
<script setup>
import Index2 from './index2.vue'
import {computed, getCurrentInstance, inject, ref} from "vue";

const instance = getCurrentInstance()
const emit = defineEmits(['register'])
const showHeader = ref(false)
const pingzhengModel=inject('pingzhengModel')
const pingzhengViewModel = computed(()=>pingzhengModel.pingzhengEditorViewModel)
</script>