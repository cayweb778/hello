<template>
  <ul :class="{ moneyGrid: true, isNumber, isNegative }">
    <li v-for="item in fontArr">{{ item }} </li>
  </ul>
</template>
<script setup>
  import { inject, computed } from 'vue';
  import {
    formatMoneyAPI,
    moneyBase,
    moneyBaseBig,
  } from '/@/components/pingzheng-fillin/utils/formatMoney';

  const props = defineProps(['modelValue']);
  const viewModel = inject('viewModel');
  const fontArr = computed(() => {
    if (props.modelValue == null) {
      if (viewModel.value.pingzhengData.enableTrillion) {
        return moneyBaseBig;
      } else {
        return moneyBase;
      }
    }
    return formatMoneyAPI(props.modelValue, {
      isBig: viewModel.value.pingzhengData.enableTrillion,
    });
  });
  const isNumber = computed(() => !(fontArr.value[0] == '百' || fontArr.value[0] == '千'));
  const isNegative = computed(() => {
    return parseFloat(props.modelValue) < 0;
  });
</script>
<style>
  .isNumber > li {
    font-size: 16px;
    padding-top: 8px;
    padding-left: 1px;
  }

  .isNegative > li {
    color: red;
  }
  .moneyGrid {
    font-size: 10px;
    width: 101.04%;
    z-index: 2;
    font-weight: 700;
    height: 100%;
  }
  .moneyGrid > li {
    width: 15px !important;
    font-family: 'Arial';
    height: 100%;
    float: left;
  }
</style>
