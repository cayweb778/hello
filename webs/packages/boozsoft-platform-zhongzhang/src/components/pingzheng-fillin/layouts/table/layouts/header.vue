<template>
  <ul class="table_head">
    <li class="col1 title_name title_ser">序号</li>
    <template v-for="columnModel in props.columnModels">
      <template v-if="columnModel.enable">
        <li
          class="col1 title_name"
          style="height: 50px"
          v-for="columnDefine in columnModel.columns"
          :style="{ width: columnDefine.width + 'px' }"
        >
          <div
            v-if="columnDefine.key == 'daiMoney' || columnDefine.key == 'jieMoney'"
            style="position: relative; height: 100%"
          >
            <div style="border-bottom: solid 1px #828282" v-text="columnDefine.label"></div>
            <div style="position: absolute; bottom: 0px; width: 100%; height: 20px">
              <MoneyGrid />
            </div>
          </div>
          <div v-else style="margin-top: 9px" v-text="columnDefine.label"></div>
        </li>
      </template>
    </template>
  </ul>
</template>
<script setup>
  import MoneyGrid from '../columns/commponents/moneyGrid.vue';
  const props = defineProps(['columnModels']);
</script>
<style>
  .title_ser {
    width: 64px !important;
    height: 50px !important;
    padding-top: 12px !important;
  }
</style>
