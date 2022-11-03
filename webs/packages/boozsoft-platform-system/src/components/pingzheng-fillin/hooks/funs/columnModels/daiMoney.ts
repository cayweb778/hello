import {
  createColumnDefine,
  getBaseRowDefine,
} from '/@/components/pingzheng-fillin/hooks/funs/columnModelsFuns';
import { nextTick, ref, shallowRef } from 'vue';
import daiMoney from '/@/components/pingzheng-fillin/layouts/table/columns/daiMoney.vue';

export function createDaiMoneyColumn(
  rowDefines,
  tableData,
  columnModels,
  jieSumMoney,
  daiSumMoney,
) {
  function addRow(i) {
    const row = createRow();
    rowDefines.value.push(getBaseRowDefine(row, i + 1));
  }
  function createRow() {
    return getRowData();
  }
  function getRowData() {
    const rowData = {};
    columnModels.value.forEach((item) => (rowData[item.key] = item.defineData()));
    rowData.isEmpty = true;
    return rowData;
  }
  return createColumnDefine('daiMoney', {
    columns: [
      {
        key: 'daiMoney',
        label: '贷方金额（本币）',
        width: '200',
        component: shallowRef(daiMoney),
        register({ target, row, rowIndex }) {
          target.change = (v) => {
            row.jieMoney.value = '0.00';
            row.daiMoney.value = v;
          };
          target.equalsNumber = (rowData) => {
            row.jieMoney.value = '0.00';
            row.daiMoney.value = '0.00';
              const money =  daiSumMoney.value- jieSumMoney.value;
              if(money>=0){
                rowData.jieMoney.value=money.toFixed(2);
              }else{
                rowData.daiMoney.value=Math.abs(money).toFixed(2);
              }
          };
          rowDefines.value[rowIndex].target.daiMoney = target;

          Object.assign(target, {
            up: () => {
              if (rowIndex == 0) {
                return;
              }
              rowDefines.value[rowIndex - 1].target.daiMoney.focus();
            },
            left: () => {
              rowDefines.value[rowIndex].target.jieMoney.focus();
            },
            right: async () => {
              if (rowDefines.value[rowIndex + 1] == null) {
                await addRow();
              }
              rowDefines.value[rowIndex + 1].target.zhaiYao.focus();
              nextTick(() => {
                rowDefines.value[rowIndex + 1].target.zhaiYao.setValue(
                  rowDefines.value[rowIndex].rowData.zhaiYao.value,
                );
              });
            },
            down: () => {
              if (rowDefines.value[rowIndex + 1] != null) {
                rowDefines.value[rowIndex + 1].target.daiMoney.focus();
              }
            },
            enter: async () => {
              if (rowDefines.value[rowIndex + 1] == null) {
                await addRow();
              }
              rowDefines.value[rowIndex + 1].target.zhaiYao.focus();
              nextTick(() => {
                rowDefines.value[rowIndex + 1].rowData.zhaiYao.columnDatas.zhaiYao.text= rowDefines.value[rowIndex].rowData.zhaiYao.value
                rowDefines.value[rowIndex + 1].rowData.zhaiYao.value= rowDefines.value[rowIndex].rowData.zhaiYao.value
              });
                rowDefines.value[rowIndex + 1].target.daiMoney.equalsNumber(rowDefines.value[rowIndex + 1].rowData);
            },
          });

          target.next = async () => {
            target.right();
          };
        },
      },
    ],
    defineData: () => ({
      value: '0.00',
      showText: ref(),
      columnDatas: {
        daiMoney: {
          text: '',
          readonly: true,
        },
      },
      isShowText222: ref(true),
    }),
    resultMergeEvent(row) {
      return {
        isError: false,
        data: [
          // 数量
          ['mc', row.daiMoney.value],
        ],
      };
    },
  });
}
