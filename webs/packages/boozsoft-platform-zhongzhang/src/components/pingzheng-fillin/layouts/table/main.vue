<template>
  <div class="pingzheng_editor main-view-item" style="height: 100%" ref="pingzhengEditorRef">
    <div
      class="helloaadsd"
      v-show="showHelloRef2"
      ref="helloRef2"
      style="
        position: fixed;
        width: 100%;
        height: 100%;
        background: #00000099;
        z-index: 100000000;
        left: 0;
        top: 0;
      "
    ></div>

    <div class="errorPage" style="position: fixed; width: 100%; height: 100%"></div>
    <PingzhengContentHeader
      @ok="emit('ok', $event)"
      @okAndShow="emit('okAndShow', $event)"
      @deletePingZhengByUniqueCode="emit('deletePingZhengByUniqueCode', $event)"
      @aaaasasa="dddda()"
      @checkAll="aaaddd"
      @delRows="delRows"
      @parallel="parallel"
      :style="{ pointerEvents: onlyShow ? 'none' : '' }"
    />
    <div class="pingzheng_table_container" style="border-radius: 0 0 10px 10px; padding-top: 105px">
      <PingZhengContentTag v-model="typeLabel" />
      <div class="pingzheng_table_content">
        <PingzhengContentRight />
        <ul class="table_head">
          <li class="col1 title_name title_ser">序号</li>
          <template v-for="columnModel in columnModels">
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
        <ul class="table_body" style="clear: both; margin: 0; overflow-x: unset !important">
          <VScroll class="pingzheng-vScroll-container" ref="vScroll" v-bind="vScrollData">
            <template #default="{ item: row, index: rowIndex }">
              <ul
                class="firstRow"
                @click="pingzhengData.settings.onlyShow ? '' : rowClick(row, rowIndex)"
              >
                <RowSetting :row="row" :rowIndex="rowIndex" :rowDefines="rowDefines" />
                <template v-for="columnModel in columnModels">
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
            </template>
          </VScroll>
        </ul>
        <div style="height: 40px">
          <ul class="table_sumRow" style="background: white; margin-left: 0">
            <li
              class="sumCol"
              :style="{ width: sumFontColumnWidth + 'px' }"
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
              :style="{ width: moneyGridWidth + 'px' }"
            >
              <MoneyGrid v-model="jieSumMoney" />
            </li>
            <li
              class="col-dai"
              style="position: relative"
              :style="{ width: moneyGridWidth + 'px' }"
            >
              <MoneyGrid v-model="daiSumMoney" />
            </li>
          </ul>
        </div>
      </div>

      <Row class="writeBy">
        <Col :span="5">
          <span>*制单人</span>:
          <Input v-model:value="pingzhengData.options.optionZhiDanBy" />
        </Col>
        <Col :span="5">
          <span>出纳</span>
          <Input v-model:value="pingzhengData.options.optionChuNaBy" />
        </Col>
        <Col :span="5">
          <span>审核人</span>
          <Input v-model:value="pingzhengData.options.optionCheckBy" />
        </Col>
        <Col :span="4">
          <span>主管</span>
          <Input v-model:value="pingzhengData.options.optionCheckBy" />
        </Col>

        <Col :span="4">
          <span>记账人</span>
          <Input v-model:value="pingzhengData.options.optionJiZhangBy" />
        </Col>
      </Row>
    </div>
  </div>
</template>
<script setup>
  import { inject, computed, ref, watch, onMounted, provide } from 'vue';
  import PingzhengContentHeader from '/@/components/pingzheng-fillin/layouts/header/ContentHeader/index.vue';
  import PingzhengContentRight from '/@/components/pingzheng-fillin/components/PingzhengContentRight.vue';
  import { Checkbox, Input, Row, Col } from 'ant-design-vue';
  import MoneyGrid from './columns/commponents/moneyGrid.vue';
  import PingZhengContentTag from '/@/components/pingzheng-fillin/components/PingZhengContentTag.vue';
  import { useCompanyOperateStoreWidthOut } from '/@/store/modules/operate-company';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { VScroll } from '/@/components/VirtualScroll/index';
  import PingZhengGrid from './columns/commponents/PingzhengGrid/index.vue';
  import { toChineseMoney } from '/@/components/pingzheng-fillin/utils/formatMoney';
  import RowSetting from '/@/components/pingzheng-fillin/components/RowSetting.vue';
  import { useRouterApi } from '/@/utils/boozsoft/datasource/datasourceUtil';

  const { usePageRouterApi } = useRouterApi();
  const CheckboxGroup = Checkbox.Group;

  const { hasPermission } = usePermission();

  const emit = defineEmits(['ok', 'okAndShow']);
  const props = defineProps(['modelValue']);
  const pingzhengData = computed(() => props.modelValue);
  const noApi = inject('noApi');
  const onlyShow = inject('onlyShow');
  const viewModel = inject('viewModel');
  const pingzhengModel = inject('pingzhengModel');
  const onActivedFun = inject('onActivedFun');

  const {
    jieSumMoney,
    daiSumMoney,
    createRow,
    columnModels,
    rowDefines,
    addRow,
    delRow,
    parallel,
    resetRequireModels,
  } = pingzhengModel.columnModels.value;
  const inheritAttrs = false;

  provide('rowDefines', rowDefines);

  const showSetting = ref(false);
  const loading = ref(false);
  const pingzhengScroll = ref();
  const aaaa111 = ref([]);
  const assad = ref(false);
  const checkAllRef = ref();
  const lastItems = ref([]);
  const hello = ref();
  const vScroll = ref();
  const helloRef2 = ref();
  const showHelloRef2 = ref(false);
  const pingzhengEditorRef = ref();
  const pingzhengContentHeight = ref(window.innerHeight - 770);

  const pingZhengRequireModels = computed(() => {
    return !noApi
      ? rowDefines.flatMap((item) => item.requireModels)
      : Object.keys(pingzhengData.value.rows[0]);
  });
  const pingzhengDate = computed(() => pingzhengData.value.options.optionDate);
  const accountInfo = computed(() => useCompanyOperateStoreWidthOut().getCurrentAccountInfo);
  const typeLabel = computed(() => pingzhengData.value.settings.typeLabel);

  function startSpin() {
    return new Promise((resolve, reject) => {
      let i = 0;
      const inte = setInterval(() => {
        i++;
        if (i == 20) {
          reject(false);
        }
        if (document.getElementsByClassName('ant-spin').length > 0) {
          resolve(true);
        }
        window.clearInterval(inte);
      });
    }, 100);
  }

  function dddda(e) {
    console.log(8846465)
    const rowIndex = parseInt(e.target.value) - 1;
    vScroll.value.$el.scrollTop = rowIndex * 40;
    // pingzhengScroll.value.scrollTop = rowIndex * 40
  }

  function checkAll() {
    aaaa111.value = pingzhengData.value.rows.map((item, i) => i);
  }

  function aba(e) {
    assad.value = true;
    e.target.checked ? checkAll() : aaaa111.value.splice(0, aaaa111.value.length);
  }

  function aaaddd(e) {
    if (!assad.value) {
      assad.value = true;
      checkAll();
    } else {
      assad.value = false;
      aaaa111.value.splice(0, aaaa111.value.length);
    }
  }

  function delRows() {
    aaaa111.value.forEach((item) => {
      pingzhengData.value.rows.splice(item, item + 1);
    });
  }

  function rowClick(row, rowIndex) {
    row.showSettingMouseenter();
    rowDefines.value.some((it, i) => {
      if (rowIndex >= i) {
        it.rowData.isEmpty = false;
        return false;
      }
      return true;
    });
  }

  function setScrollHeight() {
    // pingzhengContentHeight.value = window.innerHeight - 470
    // setTimeout(()=>{
    //   pingzhengContentHeight.value = window.innerHeight - 470
    pingzhengContentHeight.value = pingzhengEditorRef.value.offsetHeight - 250;
    // },100)
  }

  pingzhengData.value.widths = {};

  const moneyGridWidth = ref(200);

  const sumFontColumnWidth = computed(() => {
    const enableColumnWidths = columnModels
      .filter((item) => item.enable)
      .flatMap((item) => item.columns)
      .map((item) => item.width);
    const widths = enableColumnWidths.slice(0, enableColumnWidths.length - 2);
    if (widths.length == 0) {
      return 0;
    }
    return widths.reduce((a, b) => parseInt(a) + parseInt(b)) - 35 + 35 + 63;
  });

  const vScrollData = computed(() => {
    return {
      bench: 16,
      itemHeight: 40,
      maxHeight: window.innerHeight - 480,
      items: rowDefines.map((item) => item.rowData),
      width: sumFontColumnWidth.value + moneyGridWidth.value * 2 + 10,
    };
  });

  // sumFontColumnWidth+35+63+'px'
  function useTrillion() {
    if (pingzhengData.value.enableTrillion) {
      pingzhengData.value.enableTrillion = false;
      moneyGridWidth.value = 200;
      return;
    } else {
      moneyGridWidth.value = 275;
      pingzhengData.value.enableTrillion = true;
    }
    columnModels
      .filter((item) => item.enable)
      .flatMap((item) => item.columns)
      .filter((it) => it.key == 'jieMoney' || it.key == 'daiMoney')
      .map((it) => {
        it.width = moneyGridWidth.value;
      });
  }

  // 激活所需列
  watch(
    pingZhengRequireModels,
    () => {
      columnModels.forEach(
        (item) => (item.enable = pingZhengRequireModels.value.indexOf(item.key) !== -1),
      );
    },
    { immediate: true, deep: true },
  );

  onMounted(() => setScrollHeight());
  onActivedFun(() => {
    let i = 0;
    const inter = setInterval(() => {
      if (pingzhengEditorRef.value == null) {
        return;
      }
      if (pingzhengEditorRef.value.offsetHeight != 0) {
        setScrollHeight();
        window.clearInterval(inter);
      }
      if (i == 1000) {
        window.clearInterval(inter);
      }
    }, 1000);
  });

  pingzhengData.value.widths.sumFontColumnWidth = sumFontColumnWidth;
  pingzhengData.value.widths.pingzhengEditorRef = pingzhengEditorRef;
  pingzhengData.value.widths.moneyGridWidth = moneyGridWidth;
  pingzhengData.value.widths.vScrollData = vScrollData;
  pingzhengData.value.useTrillion = useTrillion;
  viewModel.value.pingzhengData = pingzhengData;
  pingzhengData.value.accountInfo = accountInfo;
  pingzhengData.value.jieSumMoney = jieSumMoney;
  pingzhengData.value.daiSumMoney = daiSumMoney;
  // 需要列的合并结果事件
  pingzhengData.value.columnModels = columnModels;
  pingzhengData.value.loading = loading;
  pingzhengData.value.rowDefines = rowDefines;
  pingzhengData.value.addRow = addRow;
  pingzhengData.value.delRow = delRow;

  provide('tableData', pingzhengData);
  provide('usePopoverContainer', {
    open() {
      showHelloRef2.value = true;
    },
    close() {
      showHelloRef2.value = false;
    },
    getContainer() {
      return helloRef2.value;
    },
  });

  defineExpose({ hello, pingzhengScroll, addRow, jieSumMoney, daiSumMoney });
  window.resetRequireModels = resetRequireModels;
</script>

<style scoped lang="less">
  @import '/@/components/pingzheng-fillin/styles/table.less';

  .writeBy {
    width: 923px;
    font-weight: 900;
    margin-left: 45px;
    margin-top: 10px;
  }

  .writeBy :deep(input) {
    width: 100px;
    border: none;
  }

  .writeBy span {
    color: #838383;
    font-weight: 500;
  }
</style>
