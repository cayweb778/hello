<template>
  <ul class="table_body" style="clear: both; margin: 0; overflow-x: unset !important">
    <VScroll class="pingzheng-vScroll-container" ref="vScroll" v-bind="props.vScrollData">
      <template #default="{ item: row, index: rowIndex }">
        <ul class="firstRow" style="background: white" @click="props.rowClick(row, rowIndex)">
          <RowSetting :row="row" :rowIndex="rowIndex" :rowDefines="rowDefines" />
          <template v-for="columnModel in props.columnModels">
            <template v-if="columnModel.enable">
              <PingZhengGrid
                v-for="columnDefine in columnModel.columns"
                :key="columnDefine.key"
                :columnModelDefineKey="columnModel.key"
                :columnDefine="rowDefines[rowIndex]"
                :columnDefineKey="columnDefine.key"
                v-model="row[columnModel.key]"
                @register="
                  columnDefine.register({
                    target: $event,
                    row,
                    rowIndex,
                    model: row[columnModel.key],
                  })
                "
                :registerGrid="rowDefines[rowIndex]"
                :style="{ width: columnDefine.width + 'px' }"
              >
                <template
                  v-if="columnModel.key == 'jieMoney' || columnModel.key == 'daiMoney'"
                  #showSlot
                >
                  <MoneyGrid v-model="row[columnModel.key].value" />
                </template>
                <template #default="{ item, registerEventFun }">
                  <component
                    v-model="row[columnModel.key]"
                    :is="columnDefine.component"
                    :rowDefine="rowDefines[rowIndex]"
                  />
                </template>
              </PingZhengGrid>
            </template>
          </template>
        </ul>
        <slot :row="row" :rowIndex="rowIndex"></slot>
      </template>
    </VScroll>
  </ul>
</template>
<script setup>
  import {ref} from 'vue'
  import { VScroll } from '/@/components/VirtualScroll/index';
  import PingZhengGrid from '../columns/commponents/PingzhengGrid/index.vue';
  import RowSetting from '/@/components/pingzheng-fillin/components/RowSetting.vue';
  import MoneyGrid from '../columns/commponents/moneyGrid.vue';
  const props = defineProps(['vScrollData', 'rowDefines', 'columnModels', 'rowClick']);
  const vScroll = ref();
  const emit = defineEmits(['register']);
  emit('register', {
    setScroll(i) {
      vScroll.value.$el.scrollTop = i;
      console.log(vScroll);
    },
  });
</script>
<style>
  .pingzheng_table_content {
    width: 95%;
    margin-top: -11px;
    overflow-x: auto;
    position: relative;
    margin-left: 45px;
  }
  .table_sumRow>li{

    box-shadow: rgb(228 228 228) 0px -8px 20px -10px !important;
    background:white;
  }


  .table_sumRow {

    z-index: 10000000 !important;;
    background: white !important;;

  }

</style>
