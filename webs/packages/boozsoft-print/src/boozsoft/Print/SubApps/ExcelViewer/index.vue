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
const props=defineProps(['value'])
const modelValue=computed(()=>props['value'])
const showCore=ref(false)
watch(()=>modelValue.value,(e)=>{
 showCore.value=true
},{immediate:true})

</script>
