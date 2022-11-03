import { computed, nextTick, ref } from 'vue';

import {
  getBaseRowDefine,
  useRequireModels,
} from '/@/components/pingzheng-fillin/hooks/funs/columnModelsFuns';
import { createZhaiYaoColumn } from '/@/components/pingzheng-fillin/hooks/funs/columnModels/zhaiYao';
import { createKuaiJiKeMu } from '/@/components/pingzheng-fillin/hooks/funs/columnModels/kuaiJiKeMu';
import { createFuZhuHeSuan } from '/@/components/pingzheng-fillin/hooks/funs/columnModels/fuZhuHeSuan';
import { createShuliangjine } from '/@/components/pingzheng-fillin/hooks/funs/columnModels/shuliangjine';
import { createHuiLvColumn } from '/@/components/pingzheng-fillin/hooks/funs/columnModels/huiLv';
import { createJieMoneyColumn } from '/@/components/pingzheng-fillin/hooks/funs/columnModels/jieMoney';
import { createDaiMoneyColumn } from '/@/components/pingzheng-fillin/hooks/funs/columnModels/daiMoney';
async function useCodeInfo(defineColumnModelsParams) {
  const codeList = await defineColumnModelsParams.getCodeCache();
  return {
    getCodeInfo(kuaiJiKeMuCode) {
      const kuaiJiKeMuInfo = codeList
        .map((it) => it.target)
        .filter((it) => it.ccode == kuaiJiKeMuCode)[0];
      return kuaiJiKeMuInfo;
    },
  };
}

export const defineColumnModels = (tableData, defineColumnModelsParams) => {
  const varlidSumRows = computed(() =>
    tableData.value.rows.filter((item) => {
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

  const thisInid = null;
  const modelRows = ref(tableData.value.rows);
  const rowDefines = ref(
    modelRows.value.map((item, rowIndex) => getBaseRowDefine(item, rowIndex, thisInid)),
  );

  const columnModels = ref([]);
  // @ts-ignore
  columnModels.value.push(
    ...[
      createZhaiYaoColumn(rowDefines, tableData),
      createKuaiJiKeMu(rowDefines, tableData),
      createFuZhuHeSuan(rowDefines, tableData),
      createShuliangjine(rowDefines, tableData),
      createHuiLvColumn(rowDefines, tableData),
      createJieMoneyColumn(rowDefines, tableData, columnModels, jieSumMoney, daiSumMoney),
      createDaiMoneyColumn(rowDefines, tableData, columnModels, jieSumMoney, daiSumMoney),
    ],
  );

  const minRow = 16;

  function supplementRow() {
    const rows = modelRows.value;
    for (let i = rows.length; i < minRow; i++) {
      rows.push(createRow());
    }
  }

  supplementRow();

  // watch(modelRows.value,()=>supplementRow())

  function getRowData() {
    const rowData = {};
    columnModels.value.forEach((item) => (rowData[item.key] = item.defineData()));
    rowData.isEmpty = true;
    return rowData;
  }

  function createRow() {
    return getRowData();
  }

  function addRow(i) {
    const row = createRow();
    rowDefines.value.push(getBaseRowDefine(row, i + 1));
  }

  const pingZhengRequireModels = computed(() => {
    return rowDefines.value.flatMap((item) => item.requireModels);
  });

  return {
    pingZhengRequireModels,
    jieSumMoney,
    daiSumMoney,
    delRow() {
      const rows = rowDefines.value.rowData.value;
      rows.splice(rows.length - 1, rows.length);

      rowDefines.value.slice(rowDefines.value.length - 1, rowDefines.value.length);
    },

    /**
     * 平行记账
     */
    async parallel() {
      async function parallelApi() {
        return [
          { code: 100101, codeName: '保单红利支出', target: 1005, targetName: '存放中央银行款项' },
        ];
      }

      const parallelData = await parallelApi();
      const parallelCodeList = parallelData.map((item) => item.code);

      const aa = rowDefines.value
        .map((item) => item.rowData)
        .filter((item) => parallelCodeList.indexOf(parseInt(item.kuaiJiKeMu.value.code)) != -1);
      if (aa.length == 0) {
        alert('没有对应的平行科目');
        return;
      } else {
        aa.map((item) => {
          const rowData = createRow();
          return rowData;
        });
      }

      const row = createRow();
      // 实际行空行,填写后改为有数据行
      // rowDefines.value.push(getBaseRowDefine(row, i + 1))
      row.zhaiYao.value = '平行记账：行1';
      row.kuaiJiKeMu.value.code = '1003';
      row.jieMoney.value = '2.34';
      row.isEmpty = false;
      // rowDefines.value.rowData.value.forEach(item=>{
      //
      // })
      let point = null;
      rowDefines.value.some((item, i) => {
        if (item.rowData.isEmpty) {
          point = i;
          return true;
        }
      });
      rowDefines.value.some((it, i) => {
        if (point == i) {
          nextTick(() => {
            rowDefines.value.splice(
              i,
              0,
              getBaseRowDefine(row, i + 1),
              getBaseRowDefine(row, i + 1),
              getBaseRowDefine(row, i + 1),
              getBaseRowDefine(row, i + 1),
            );
          });
          return true;
        }
      });
    },
    createRow,
    async resetRequireModels() {
      const { getCodeInfo } = await useCodeInfo(defineColumnModelsParams);
      rowDefines.value.forEach((it) => {
        const kuaiJiKeMuInfo = getCodeInfo(it.rowData.kuaiJiKeMu.value.code);
        if (kuaiJiKeMuInfo != null) {
          it.requireModels = useRequireModels(kuaiJiKeMuInfo).getRequireModels();
        }
      });
    },
    modelRows,
    rowDefines,
    columnModels,
  };
};
