<template>
  <div
    class="hoverShowTextarea"
    v-if="visible"
    v-text="modelValue"
    style="height: calc(100% - 1px); z-index: 11111111111111; text-align: left; width: 100%"
  ></div>
</template>
<script setup>
  import { computed, ref } from 'vue';

  const props = defineProps(['modelValue', 'visible']);

  const visible = computed(() => props.visible);

  const textareaRef = ref();
</script>
<style>
  .hoverShowTextarea {
    outline: solid 1px #005791;
    box-shadow: 0 0 4px #005791;
  }
</style>
