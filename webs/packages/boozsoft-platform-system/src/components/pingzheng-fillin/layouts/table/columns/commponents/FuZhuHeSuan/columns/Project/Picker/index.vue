<template>
  <div>
    <PingzhengItemList v-model:items="propsList2" @change="emit('change',$event.label)" />
  </div>
</template>
<script setup>
  import PingzhengItemList from '/@/components/pingzheng-fillin/components/PingzhengItemList/index.vue';
  import { computed } from 'vue';
  import pinyin from 'js-pinyin';

  const emit = defineEmits(['change']);
  const contains = (str, str2) => str.indexOf(str2) !== -1;
  const props = defineProps(['items', 'showText']);
  const propsList2 = computed(() => {
    return props.items
      .filter((it) => {
        if (props.showText == '') {
          return true;
        } else if (props.showText == null) {
          return true;
        }
        return (
          contains(it.label, props.showText) ||
          contains(it.key, props.showText) ||
          pinyin.getCamelChars(it.label).indexOf(props.showText.toUpperCase()) != -1
        );
      })
      .map((it) => {
        return {
          ...it,
          value: it.key,
          key: it.code,
        };
      });
  });
</script>
