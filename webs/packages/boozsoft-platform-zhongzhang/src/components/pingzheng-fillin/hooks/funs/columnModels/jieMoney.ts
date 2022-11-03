import {
  createColumnDefine,
  getBaseRowDefine,
} from '/@/components/pingzheng-fillin/hooks/funs/columnModelsFuns';
import { nextTick, ref, shallowRef } from 'vue';
import jieMoney from '/@/components/pingzheng-fillin/layouts/table/columns/jieMoney.vue';

export function createJieMoneyColumn(
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
  return createColumnDefine('jieMoney', {
    columns: [
      {
        key: 'jieMoney',
        label: '借方金额（本币）',
        width: '200',
        register({ target, row, rowIndex }) {
          target.change = (v) => {
            row.jieMoney.value = v;
            row.daiMoney.value = '0.00';
          };
          target.equalsNumber = (rowData) => {
            row.jieMoney.value = '0.00';
            row.daiMoney.value = '0.00';
            const money = jieSumMoney.value - daiSumMoney.value;
            if (money >= 0) {
              rowData.daiMoney.value = money.toFixed(2);
            } else {
              rowData.jieMoney.value = Math.abs(money).toFixed(2);
            }
          };
          rowDefines.value[rowIndex].target.jieMoney = target;
          Object.assign(target, {
            up: () => {
              if (rowIndex == 0) {
                return;
              }
              rowDefines.value[rowIndex - 1].target.jieMoney.focus();
            },
            left: () => {
              rowDefines.value[rowIndex].target.kuaiJiKeMu.focus();
            },
            right: async () => {
              rowDefines.value[rowIndex].target.daiMoney.focus();
            },
            down: () => {
              if (rowDefines.value[rowIndex + 1] != null) {
                rowDefines.value[rowIndex + 1].target.jieMoney.focus();
              }
            },
            enter: async () => {
              if (rowDefines.value[rowIndex].rowData.jieMoney.value == '0.00') {
                target.right();
              } else {
                if (rowDefines.value[rowIndex + 1] == null) {
                  await addRow();
                }
                rowDefines.value[rowIndex + 1].target.zhaiYao.focus();
                nextTick(() => {
                  rowDefines.value[rowIndex + 1].rowData.zhaiYao.columnDatas.zhaiYao.text =
                    rowDefines.value[rowIndex].rowData.zhaiYao.value;
                  rowDefines.value[rowIndex + 1].rowData.zhaiYao.value =
                    rowDefines.value[rowIndex].rowData.zhaiYao.value;
                });
                // rowDefines.value[rowIndex + 1].rowData.daiMoney.value =
                rowDefines.value[rowIndex + 1].target.jieMoney.equalsNumber(
                  rowDefines.value[rowIndex + 1].rowData,
                );
              }
            },
          });

          target.next = async () => {
            target.right();
            // if (rowDefines.value[rowIndex].target.daiMoney != null) {
            //   rowDefines.value[rowIndex].target.daiMoney.focus();
            // }
          };
        },
        component: shallowRef(jieMoney),
      },
    ],
    defineData: () => ({
      value: '0.00',
      showText: ref(),
      isShowText222: ref(true),
      columnDatas: {
        jieMoney: {
          text: '',
          readonly: true,
        },
      },
    }),
    resultMergeEvent(row) {
      return {
        isError: false,
        data: [
          // 数量
          ['md', row.jieMoney.value],
        ],
      };
    },
  });
}
