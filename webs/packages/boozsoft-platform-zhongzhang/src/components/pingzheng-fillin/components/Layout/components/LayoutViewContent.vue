<template>
  <div class="app-container hello2"
       style="min-width:1045px !important;margin-top:0;background:none !important;">

     <slot></slot>
  </div>
</template>
<script setup lang="ts">

import {inject, nextTick, onMounted, provide, ref} from 'vue'

import Header from './header/index.vue'

const props = defineProps(['appear', 'radius'])
const viewProvider = {
  viewModel: ref(),
  useViewModel(viewModel) {
    viewProvider.viewModel.value = viewModel
    return viewProvider.viewModel
  }
}
provide('viewProvider', viewProvider)
const showView = ref(false)
const showHeader = ref(false)
onMounted(() => {
  showView.value = true
  let i = 0
  const setInterval2 = setInterval(() => {
    i++
    if (viewProvider.viewModel.value) {
      showHeader.value = true
      window.clearInterval(setInterval2)
    }
    if (i > 20000) {
      throw new Error('一个错误')
    }
  }, 20)
  // nextTick(()=>{
  //   debugger
  //   showHeader.value=true
  // })
})
</script>
<style scope src="/@/assets/styles/generate-code-record-style/record.less" lang="less"></style>
