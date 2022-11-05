<template>
  <div :class="{ enablethis: !enable, thisDiv: true }">
    <input
      v-if="enable"
      class="inputRef"
      @focus="inputRef.select()"
      v-model="value"
      @change="inputChange"
      style="text-align: right"
      ref="inputRef"
      @keyup.left="registerEvents.left()"
      @keyup.right="registerEvents.next()"
      @keyup.enter="registerEvents.next()"
    />
    <!--    <textarea  ref="textareaRef"  style="height: 100%" @blur="textareaChage" v-model="props.modelValue.value.currencyExchangeRate"></textarea>-->
  </div>
</template>
<script setup>
  import { ref, computed } from 'vue';
  import { useRegiterEvent } from '/@/components/pingzheng-fillin/utils/regiterEvent';

  const props = defineProps(['modelValue', 'rowDefine']);
  const rowDefine = computed(() => props.rowDefine);
  const enable = computed(() => rowDefine.value.requireModels.indexOf('huiLv') != -1);

  const emit = defineEmits(['register']);
  const inputRef = ref();
  const value = ref();
  function inputChange(e) {
    value.value = parseFloat(e.target.value).toFixed(4);
  }
  const registerEvents = useRegiterEvent({
    emit,
    data: {
      focus() {
        inputRef.value.focus();
      },
      row: null,
      change: null,
    },
    actions: {},
  });

  function textareaChage() {
    props.modelValue.value.currencyExchangeRate = parseFloat(
      props.modelValue.value.currencyExchangeRate,
    ).toFixed(4);
    event.change();
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
