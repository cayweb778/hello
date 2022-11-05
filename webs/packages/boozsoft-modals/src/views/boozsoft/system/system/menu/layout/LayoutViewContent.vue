<template>
  <div  style="width:100%;height:100%">
    <div class="app-container" style="margin-top:0;background:none !important;">
      <div
        style="position:relative;margin-bottom:10px;  padding:10px;  background: rgb(241, 241, 241);height:97px;border-radius: 5px;">
        <slot v-if="showHeader" name="header"></slot>
      </div>
      <div class="view-content" :style="{background:props.appear===''?'#f1f1f1':'none','border-radius':props.radius===''?'10px':''}" style="position:relative;height:calc(100% - 100px);">
        <slot  name="default" v-if="showView"></slot>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">

import {inject, nextTick, onMounted, provide, ref} from 'vue'

import Header from './header/index.vue'

const props=defineProps(['appear','radius'])
const viewProvider = {
  viewModel:ref(),
  useViewModel(viewModel){
    viewProvider.viewModel.value=viewModel
    return viewProvider.viewModel
  }
}
provide('viewProvider',viewProvider)
provide('viewModel',viewProvider.viewModel)
const showView=ref(false)
const showHeader=ref(false)
onMounted(()=>{
  showView.value=true
  let i=0
  const setInterval2=setInterval(()=>{
    i++
    if(viewProvider.viewModel.value){
      showHeader.value=true
      window.clearInterval(setInterval2)
    }
    if(i>20000){
      throw new Error('一个错误')
    }
  },20)
  // nextTick(()=>{
  //   debugger
  //   showHeader.value=true
  // })
})
</script>
<style scope
       src="/@/assets/styles/generate-code-record-style/record.less"
       lang="less">
</style>
