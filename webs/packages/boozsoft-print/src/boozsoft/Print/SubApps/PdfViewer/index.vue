<template>
  <div style="width:100%;height:100vh">
    <Core v-if="showCore" :value="modelValue"></Core>
    <Hello v-else></Hello>
  </div>
</template>
<script setup lang="ts">
import Core from './core.vue'
import Hello from './hello.vue'
import {computed, ref,watch} from 'vue'
const props=defineProps(['modelValue'])
const modelValue=computed(()=>props['modelValue'])
watch(()=>modelValue.value,(e)=>{
 showCore.value=true
})
const showCore=ref(false)
</script>
