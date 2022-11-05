<template>
  <div style="height: 100%; width: 100%; position: relative">
    <DatePicker
      :getPopupContainer="abc"
      ref="datePickerRef"
      class="fuZhuHeSuanInputClass"
      v-model:value="props.modelValue.piaoJuDate"
      :locale="locale"
      v-on="inputEvents"
      style="width: 90.5%;"
      valueFormat="YYYY-DD-MM"
      @change="(showPicker = false), emit('right')"
    />
  </div>
</template>
<script setup>
  import { onMounted, ref, nextTick, watch } from 'vue';
  import {
    Input,
    Popover,
    Select,
    Radio,
    Tabs,
    DatePicker,
    Button,
    Tooltip,
    Drawer,
    Modal,
  } from 'ant-design-vue';
  import locale from 'ant-design-vue/es/date-picker/locale/zh_CN';
  import { cancelDatePnKeydownSpace, datePnKeydownSpace } from './fuZhuHeSuanPiaoJuDate';

  const fuZhuXiangPopoverRef = ref();

  const props = defineProps(['modelValue']);
  const showText = ref('');
  const showText2 = ref(true);
  const inputRef = ref();
  const datePickerRef = ref();
  const openPicker = ref(true);
  const emit = defineEmits(['left', 'right']);

  const inputRef2 = ref();
  defineExpose({
    focus() {
      console.log(55555);
      showText2.value = false;
      nextTick(() => {
        datePickerRef.value.focus();
      });
    },
  });

  const inputEvents = {
    click() {
      console.log(8989);
    },
    keydown(e) {
      console.log(997777);
      switch (e.key) {
        // case ('ArrowUp'):
        //   keydownUp(e)
        //   break
        // case ('ArrowDown'):
        //   keydownUp(e)
        //   break
        // case ('ArrowLeft'):
        //   keydownLeft(e)
        //   break
        // case ('ArrowRight'):
        //   console.log(2222)
        //   keydownRight(e)
        //   break
        // case ('Tab'):
        //   abcdd(e)
        //   break
        case ' ':
          openPicker.value = true;
          showPicker.value = !showPicker.value;
          e.preventDefault();
          break;
      }
    },
  };

  const showPicker = ref(false);

  watch(showPicker, (e) => {
    if (e) {
      datePnKeydownSpace(() => {
        showPicker.value = false;
        nextTick(() => {
          inputRef2.value.focus();
        });
      });
    } else {
      cancelDatePnKeydownSpace();
    }
  });

  function abc(e) {
    return document.body;
  }

  onMounted(() => (showText.value = props.modelValue.piaoJuDate));
</script>
<style>
  .thisDatePicker > div,
  .thisDatePicker > div input {
    height: 100%;
  }
</style>
