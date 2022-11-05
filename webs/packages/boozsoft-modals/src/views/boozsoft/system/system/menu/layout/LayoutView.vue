<template>
  <LayoutViewContent v-if="reloadPageState" v-bind="$attrs">
    <template #header>
      <slot name="header"></slot>
    </template>
    <slot name="default"></slot>
  </LayoutViewContent>
</template>
<script setup>
import LayoutViewContent from './LayoutViewContent.vue'
import {ref,provide, nextTick,useSlots} from "vue";

const reloadPageState = ref(true)
const slots=useSlots()
function reloadPage() {
  reloadPageState.value = false
  nextTick(() => reloadPageState.value = true)
}

provide('reloadPage', reloadPage)
</script>
