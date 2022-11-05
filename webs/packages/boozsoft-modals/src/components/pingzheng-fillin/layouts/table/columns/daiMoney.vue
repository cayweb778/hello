<template>
  <jieDaiMoneyGrid v-model="props.modelValue" @register="emit('register',$event)"></jieDaiMoneyGrid>
</template>
<script setup>
import jieDaiMoneyGrid from './commponents/jieDaiMoneyGrid.vue'
const props=defineProps(['modelValue'])
const emit=defineEmits(['register'])
</script>
