<template>
  <div class="textGrid">
    <TextContent :isBizhong="isBizhong" :isWhite="isWhite" :value="props.modelValue" />
    <TooltipContent :value="props.tooltip" />
  </div>
</template>
<script setup>
  import { computed, inject } from 'vue';

  import TooltipContent from './TooltipContent.vue';
  import TextContent from './TextContent.vue';
  const props = defineProps(['modelValue', 'tooltip', 'white']);

  const pingzhengGridParams = inject('pingzhengGridParams');

  const isBizhong = computed(() => {
    if (props.columnDefineKey === 'bizhong') {
      return true;
    } else {
      return false;
    }
  });

  const isWhite = computed(() => {
    return props.white;
  });
</script>
<style>
  .textGrid {
    position: relative;
    height: 100%;
  }
  .helloTextTest {
    font-size: 14px;
    width: 100%;
    height: 100%;
  }
</style>
