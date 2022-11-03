<template>
  <Tooltip v-if="props.value != null && props.value != ''" placement="bottom">
    <template #title>
      <div>
        <div v-html="props.value"></div>
      </div>
    </template>
    <div
      style="position: absolute; left: 0; top: 0px; width: 100%; height: 35px; z-index: 10000000"
    >
    </div>
  </Tooltip>
</template>
<script setup>
  import { Tooltip } from 'ant-design-vue';
  const props = defineProps(['value']);
</script>
