<template>
  <div style="background: white; height: 100%; width: 100%; position: relative">
    <input
      class="inputRef"
      ref="inputRef"
      v-model="value"
      v-on="inputEvents"
      style="font-size: 30px; text-align: right; font-family: Arial"
    />
    <!--    <textarea  ref="abc" v-show="activeTextarea" @blur="textareaBlur" style="height: 100%;width:100%" :value="props.modelValue.value" ></textarea>-->
  </div>
</template>
<script setup>
  import { watch, nextTick, ref, unref, inject, onMounted } from 'vue';
  import MoneyGrid from '../commponents/moneyGrid.vue';
  import {
    useRegiterEvent,
    useRegiterEventNoEmit,
  } from '/@/components/pingzheng-fillin/utils/regiterEvent';
  import { onBeforeRouteLeave, onBeforeRouteUpdate } from 'vue-router';
  const props = defineProps(['modelValue', 'registerEventFun']);
  const value = ref(unref(props.modelValue.value));
  watch(props.modelValue, () => {
    value.value = props.modelValue.value;
  });

  const tableData = inject('tableData');
  const activeTextarea = ref(false);
  const abc = ref();
  function textareaBlur() {
    activeTextarea.value = false;

    if (props.modelValue.value == '') {
      props.modelValue.value = '0.00';
    }
  }
  function showTextarea() {
    if (!tableData.value.settings.onlyShow) {
      activeTextarea.value = true;
    }
    nextTick(() => inputRef.value.focus());
  }
  const inputRef = ref();
  const emit = defineEmits(['register']);

  Object.assign(
    inject('registerEventFun'),
    useRegiterEventNoEmit({
      data: {
        requireModels: null,
        rowIndex: null,
        setRequireModels: null,
        setBiZhong: null,
        setUnit: null,
      },
    }),
  );
  const registerEvents = inject('registerEventFun');

  function aaavvv(value2) {
    const v = parseFloat(value2).toFixed(2);
    registerEvents.change(v);
    value.value = v;
  }

  const viewModel = inject('viewModel');

  function toMoneyInputStr(moneyStr, limitInteger, limitDecimal) {
    if (limitDecimal == null) {
      limitDecimal = 2;
    }
    moneyStr = moneyStr
      .replace(/[\u4e00-\u9fa5]+/g, '') //验证非汉字
      .replace(/(^0.)(.*)/g, '{zeroDot}$2')
      .replace(/(^0)(.*)/g, '0$2')
      .replace(/{zeroDot}/g, '0.')
      .replace(/(^\.)(.*)/g, '0.')
      .replace(/(^\-)(.*)/g, '{xx}$2')
      .replace(/-/g, '')
      .replace(/{xx}/, '-')
      .replace(/[^\-?\d.]/g, '') //清除"数字","-"和"."以外的字符
      .replace(/^\./g, '') //验证第一个字符是数字而不是
      .replace('.', '$#$')
      .replace(/\./g, '')
      .replace('$#$', '.') //只保留第一个小数点, 清除多余的
      .replace('-', '$#$')
      .replace(/\-/g, '')
      .replace('$#$', '-') //只保留第一个
      .replace(/^(\-)*(\d+)\.(\d{2}).*$/, '$1$2.$3');
    if (limitInteger != null && moneyStr.search('\\.') == -1) {
      moneyStr = moneyStr.slice(0, limitInteger);
    } else {
      let arr = moneyStr.split('\\.');
      if (arr.length > 1) {
        moneyStr = arr[0].slice(0, limitInteger) + '.' + arr[1];
      }
    }
    return moneyStr;
  }

  function keyupLeft(e2) {
    if (e2.target.selectionStart == 0 && e2.target.selectionEnd == 0) {
      setTimeout(() => {
        registerEvents.left();
      }, 100);
      var e = window.event || event;
      if (e.preventDefault) {
        e.preventDefault();
      } else {
        window.event.returnValue = false;
      }
    }
  }

  function keyupRight(e2) {
    if (
      e2.target.selectionStart == e2.target.value.length &&
      e2.target.selectionEnd == e2.target.value.length
    ) {
      registerEvents.right();

      var e = window.event || event;
      if (e.preventDefault) {
        e.preventDefault();
      } else {
        window.event.returnValue = false;
      }
    }
  }

  const inputEvents = {
    keydown(e) {
      function equalEvent() {
        value.value = '';
        aaavvv(registerEvents.equalsNumber());
        inputRef.value.blur();
        e.preventDefault();
      }
      switch (e.key) {
        case ' ':
          registerEvents.enter(e);
          break;
        case '=':
          equalEvent();
          break;
        case 'ArrowLeft':
          keyupLeft(e);
          break;
        case 'ArrowRight':
          keyupRight(e);
          break;
      }
    },
    keyup(e) {
      switch (e.key) {
        case 'ArrowUp':
          registerEvents.up(e);
          break;
        case 'ArrowDown':
          registerEvents.down(e);
          break;
        case 'Enter':
          registerEvents.enter(e);
          break;
      }
    },
    input(e) {
      // console.log(e.target.value)
      if (viewModel.value.pingzhengData.enableTrillion) {
        value.value = toMoneyInputStr(e.target.value.toString(), 16);
      } else {
        value.value = toMoneyInputStr(e.target.value.toString(), 11);
      }

      if (value.value == '') {
        value.value = '0.00';
        nextTick(() => {
          inputRef.value.select();
        });
      }
      // console.log(value.value)
    },
    focus() {
      inputRef.value.select();
    },
    blur() {
      if (value.value == 'NaN') {
        aaavvv('0.00');
      }
      registerEvents.blur();
    },
    change(e) {
      aaavvv(e.target.value);
    },
  };

  onMounted(() => {
    inputRef.value.focus();
  });
</script>
