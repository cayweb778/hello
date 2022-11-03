<template>
  <JieDaiMoneyGrid v-model="props.modelValue" @register="emit('register',$event)"></JieDaiMoneyGrid>
</template>
<script setup>
import JieDaiMoneyGrid from './commponents/jieDaiMoneyGrid.vue'
const props=defineProps(['modelValue'])
const emit=defineEmits(['register'])
</script>
