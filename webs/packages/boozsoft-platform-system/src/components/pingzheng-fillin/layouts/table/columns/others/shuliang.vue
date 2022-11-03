<template>
  <div :class="{ enablethis: !enable, thisDiv: true }">
    <input
      v-if="enable"
      class="inputRef"
      ref="inputRef"
      @focus="inputRef.select()"
      @keydown.left="keyDownLeft()"
      @keydown.right="registerEvents.next()"
      @keyup.enter="registerEvents.next()"
      style="text-align: center; height: 100%"
      @change="registerEvents.change()"
      v-model="props.modelValue.value.slNumber"
    />
  </div>
</template>
<script lang="ts" setup>
  import { computed, ref } from 'vue';
  import { useRegiterEvent } from '/@/components/pingzheng-fillin/utils/regiterEvent';

  const props = defineProps(['modelValue', 'rowDefine']);

  const rowDefine = computed(() => props.rowDefine);
  const enable = computed(() => rowDefine.value.requireModels.indexOf('shuliangjine') != -1);

  const inputRef = ref();

  const emit = defineEmits(['register']);

  const registerEvents = useRegiterEvent({
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

  function keyDownLeft() {
    setTimeout(() => {
      registerEvents.left();
    }, 200);
  }
</script>
<style>
  .thisDiv {
    height: 100%;
  }
  .enablethis {
    background: #dedede;
  }
</style>
