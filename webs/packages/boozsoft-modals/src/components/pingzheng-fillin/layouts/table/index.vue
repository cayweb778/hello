<template>
  <Layout>
    <PingzhengContentHeader
      @ok="emit('ok', $event)"
      @parallel="parallel"
      @aaaasasa="dddda"
      :style="{ pointerEvents: onlyShow ? 'none' : '' }"
    />
    <TableContainer :typeLabel="typeLabel">
      <TableHeader :columnModels="columnModels" />
      <TableContent
        @register="registerTableContent"
        :rowDefines="rowDefines"
        :columnModels="columnModels"
        :vScrollData="vScrollData"
        :rowClick="rowClick"
      />
      <PingzhengContentRight />
      <TableFooter
        :sumFontColumnWidth="sumFontColumnWidth"
        :pingzhengData="pingzhengData"
        :moneyGridWidth="moneyGridWidth"
      />
      <Footer
        :infos="[
          { label: '制单人', must: true, value: pingzhengData.options.optionZhiDanBy, span: 5 },
          { label: '出纳', must: false, value: pingzhengData.options.optionChuNaBy, span: 5 },
          { label: '审核人', must: false, value: pingzhengData.options.optionCheckBy, span: 5 },
          { label: '主管', must: false, value: pingzhengData.options.optionCheckBy, span: 4 },
          { label: '记账人', must: false, value: pingzhengData.options.optionJiZhangBy, span: 4 },
        ]"
      />
    </TableContainer>
  </Layout>
</template>
<script setup>
  import { inject, computed, ref, watch, provide } from 'vue';
  import PingzhengContentHeader from '/@/components/pingzheng-fillin/layouts/header/ContentHeader/index.vue';
  import PingzhengContentRight from '/@/components/pingzheng-fillin/components/PingzhengContentRight.vue';
  import Footer from './layouts/footer.vue';

  import { useCompanyOperateStoreWidthOut } from '/@/store/modules/operate-company';
  import Layout from './layouts/index.vue';
  import TableHeader from './layouts/header.vue';
  import TableContent from './layouts/content.vue';
  import TableFooter from './layouts/tableFooter.vue';
  import TableContainer from './layouts/container.vue';
  const emit = defineEmits(['ok', 'okAndShow']);
  const props = defineProps(['modelValue']);
  const pingzhengData = computed(() => props.modelValue);
  const noApi = inject('noApi');
  const onlyShow = inject('onlyShow');
  const viewModel = inject('viewModel');
  const pingzhengModel = inject('pingzhengModel');
  const onActivedFun = inject('onActivedFun');

  const { columnModels, rowDefines, addRow, delRow, parallel, resetRequireModels } =
    pingzhengModel.columnModels.value;

  const loading = ref(false);
  const pingzhengScroll = ref();
  const hello = ref();
  const pingzhengEditorRef = ref();
  const moneyGridWidth = ref(200);
  // const pingzhengContentHeight = ref(window.innerHeight - 770);

  const pingZhengRequireModels = computed(() => {
    return !noApi
      ? rowDefines.flatMap((item) => item.requireModels)
      : Object.keys(pingzhengData.value.rows[0]);
  });
  const accountInfo = computed(() => useCompanyOperateStoreWidthOut().getCurrentAccountInfo);
  const typeLabel = computed(() => pingzhengData.value.settings.typeLabel);

  function usePingzhengScroll() {
    let instance = null;
    return {
      register(e) {
        instance = e;
      },
      setScroll(i) {
        instance.setScroll(i);
      },
    };
  }
  const { register: registerTableContent,setScroll:setPingzhengScroll } = usePingzhengScroll();

  provide('setPingzhengScroll',setPingzhengScroll)

  function dddda(e) {
    const rowIndex = parseInt(e.target.value) - 1;
    setScroll(rowIndex * 40)

    // pingzhengScroll.value.scrollTop = rowIndex * 40
  }

  function rowClick(row, rowIndex) {
    if (pingzhengData.value.settings.onlyShow) {
      return;
    }
    row.showSettingMouseenter();
    rowDefines.some((it, i) => {
      if (rowIndex >= i) {
        it.rowData.isEmpty = false;
        return false;
      }
      return true;
    });
  }

  pingzhengData.value.widths = {};

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
    console.log(window.innerHeight - 480)
    return {
      bench: 16,
      itemHeight: 52,
      maxHeight: window.innerHeight - 480,
      height:window.innerHeight - 480,
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

  onActivedFun(() => {
    let i = 0;
    const inter = setInterval(() => {
      if (pingzhengEditorRef.value == null) {
        return;
      }
      if (pingzhengEditorRef.value.offsetHeight != 0) {
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

  // 需要列的合并结果事件
  pingzhengData.value.columnModels = columnModels;
  pingzhengData.value.loading = loading;
  pingzhengData.value.rowDefines = rowDefines;
  pingzhengData.value.addRow = addRow;
  pingzhengData.value.delRow = delRow;

  provide('tableData', pingzhengData);
  provide('rowDefines', rowDefines);

  defineExpose({ hello, pingzhengScroll, addRow });
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
