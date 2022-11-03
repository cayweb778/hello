<template>
  <div style="height: 100%; overflow: hidden; font-size: 10px; text-align: left">
    <!--          <div>计量单位: {{ props.modelValue.value.slUnit }}</div>-->
    <div>汇率: {{ props.huiLv }}</div>
    <div>金额: {{ props.money }}</div>
  </div>

</template>
<script setup>
const props=defineProps(['huiLv','money'])
</script>
