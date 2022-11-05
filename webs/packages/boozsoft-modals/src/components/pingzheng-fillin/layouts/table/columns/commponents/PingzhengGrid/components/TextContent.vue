<template>
  <div
    v-if="props.isWhite"
    class="helloTextTest"
    :class="{
      textContent: true,
      textContentBizhong: props.isBizhong,
    }"
    v-html="props.value"
  >
  </div>
  <div v-else style="background: #dedede; width: 100%; height: 100%"></div>
  <MoreContent :visible="showMoreButton" :value="propsValue" />
</template>
<script setup>
  import MoreContent from './MoreContent.vue';
  import { computed, ref, watch } from 'vue';
  const showMoreButton = ref(false);
  const propsValue = computed(() => props.value);
  const showText = ref('');
  watch(
    propsValue,
    () => {
      if (propsValue.value?.length > 60) {
        showText.value = propsValue.value.slice(0, 60) + '....';
        showMoreButton.value = true;
      } else {
        showText.value = propsValue.value;
      }
    },
    { immediate: true },
  );
  const props = defineProps(['isBizhong', 'isWhite', 'value']);
</script>
<style>
  .textContent {
    background: white;
    text-align: left;
    padding-top: 0;
  }

  .textContentBizhong {
    text-align: center;
    padding-top: 8px;
  }
</style>
