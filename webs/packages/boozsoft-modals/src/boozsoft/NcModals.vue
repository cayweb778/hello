<template>
  <div>
    <component v-if="ncModals.componentData.componentRef!=null" @ok="handleOk" @cancel="handelCancel" :is="componentName" :params="ncModals.componentData.componentParams" :result="ncModals.componentData.componentResult"></component>
  </div>
</template>
<script setup>
import {ref, shallowRef} from 'vue'
import {defineNcModals} from "/@/views/boozsoft/global/funs";
import abc1 from "/@/views/boozsoft/global/abc1.vue";
const componentName=shallowRef()
const ncModals=ref(defineNcModals({
  changeComponent(e){
    componentName.value=e
  }
}))

// ncModals.componentData.componentRef
function handelCancel(){
  componentName.value=abc1
}
const emit=defineEmits(['ok'])
function handleOk(){
  componentName.value=abc1
  emit('ok')
}
window.$wujie.bus.$emit("registerNcModals",ncModals.value)
</script>
