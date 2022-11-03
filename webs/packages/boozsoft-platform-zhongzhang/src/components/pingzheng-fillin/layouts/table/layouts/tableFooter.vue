<template>
  <div  >
    <ul class="table_sumRow" style="background: inherit !important;margin-left: 0;display: flex;">
      <li
        class="sumCol"
        :style="{ width: props.sumFontColumnWidth + 'px' }"
        style="padding-top: 9px; padding-left: 10px"
      >
        <span style="font-weight: 600; font-size: 17px">合计：</span> &nbsp;
        <span style="font-size: 16px; font-weight: 900">{{
          toChineseMoney(jieSumMoney)
        }}</span>
      </li>
      <li
        class="col-jie"
        style="position: relative"
        :style="{ width: props.moneyGridWidth + 'px' }"
      >
        <MoneyGrid v-model="jieSumMoney" />
      </li>
      <li
        class="col-dai"
        style="position: relative"
        :style="{ width: props.moneyGridWidth + 'px' }"
      >
        <MoneyGrid v-model="daiSumMoney" />
      </li>
    </ul>
  </div>
</template>
<script setup>
  import { computed } from 'vue';

  const props = defineProps(['sumFontColumnWidth', 'moneyGridWidth', 'pingzhengData']);
  import { toChineseMoney } from '/@/components/pingzheng-fillin/utils/formatMoney';
  import MoneyGrid from '../columns/commponents/moneyGrid.vue';

  const varlidSumRows = computed(() =>
    props.pingzhengData.rows.filter((item) => {
      return !(item.jieMoney.value == '0.00' && item.daiMoney.value == '0.00');
    }),
  );

  const jieSumMoney = computed(() => {
    let j = 0;
    varlidSumRows.value.forEach((it) => (j += parseFloat(it.jieMoney.value)));
    return j;
  });

  const daiSumMoney = computed(() => {
    let j = 0;
    varlidSumRows.value.forEach((it) => (j += parseFloat(it.daiMoney.value)));
    return j;
  });
  props.pingzhengData.jieSumMoney = jieSumMoney;
  props.pingzhengData.daiSumMoney = daiSumMoney;
</script>
