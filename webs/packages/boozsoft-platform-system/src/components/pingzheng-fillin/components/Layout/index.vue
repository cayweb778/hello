<template>
  <div class="pingZhengPageContainer" :style="{ height: thisHeight + 'px' }">
    <LayoutViewContent v-bind="$attrs">
      <slot></slot>
    </LayoutViewContent>
  </div>
</template>
<script setup>
  import LayoutViewContent from './components/LayoutViewContent.vue';
  import { ref, provide, nextTick } from 'vue';

  const thisHeight = window.innerHeight - 110;
</script>
