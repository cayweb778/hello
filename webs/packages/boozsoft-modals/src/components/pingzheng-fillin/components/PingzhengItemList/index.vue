<template>
  <VScroll ref="vScroll" :bench="16" :itemHeight="27" :maxHeight="300" :items="items">
    <template #default="{ item: it, index: rowIndex }">
      <ul
        class="code-item"
        @mousedown="emit('change', it)"
        @mouseenter="it.hover = true"
        @mouseleave="it.hover = false"
        :class="{ hoverRow2: it.hover, enableRow2: it.enable }"
      >
        <Item :text="it.label" />
      </ul>
    </template>
  </VScroll>
</template>
<script setup>
  import { VScroll } from '/@/components/VirtualScroll/index';
  import { watch, computed } from 'vue';
  import Item from './Item.vue';

  const props = defineProps(['modelValue', 'items']);

  const emit = defineEmits(['change']);
  const items = computed(() => {
    return props.items == null ? [] : props.items;
  });
  const length = computed(() => {
    return items.value.length;
  });

  watch(length, () => {
    if (length.value !== 0) {
      items.value[0].hover = true;
      items.value[0].enable = true;
    }
  });
</script>
<style>
  .hoverRow2 {
    background: #bfeaff ;
    color: gray;
  }
  .enableRow2 {
    background: #0aa699 !important;
    color: white;
  }
</style>
