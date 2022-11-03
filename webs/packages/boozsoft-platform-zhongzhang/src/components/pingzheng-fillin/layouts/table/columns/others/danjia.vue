<template>
  <div :class="{ enablethis: !enable, thisDiv: true }">
    <input
      v-if="enable"
      ref="inputRef"
      class="inputRef"
      style="text-align: center; height: 100%"
      @keydown.left="event.left()"
      @keydown.right="keydownRight"
      @keyup.enter="event.next()"
      @focus="inputRef.select()"
      v-model="props.modelValue.value.slPrice"
    />
  </div>
</template>
<script setup>
  import { ref, computed } from 'vue';
  import { useRegiterEvent } from '/@/components/pingzheng-fillin/utils/regiterEvent';

  const props = defineProps(['modelValue', 'rowDefine']);
  const rowDefine = computed(() => props.rowDefine);
  const enable = computed(() => rowDefine.value.requireModels.indexOf('shuliangjine') != -1);

  const emit = defineEmits(['register']);
  const inputRef = ref();


  const event = useRegiterEvent({
    emit,
    data: {
      row: null,
      change: null,
    },
    actions: {
      focus() {
        inputRef.value.focus();
      },
    },
  });

  function keydownRight() {
    setTimeout(() => {
      event.next();
    }, 200);
  }
</script>
<style scoped>
  .thisDiv {
    height: 100%;
  }
  .enablethis {
    background: #dedede;
  }
</style>
