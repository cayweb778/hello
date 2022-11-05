import {
  createColumnDefine,
  shuLiangChange,
} from '/@/components/pingzheng-fillin/hooks/funs/columnModelsFuns';
import { ref, shallowRef } from 'vue';
import bizhong from '/@/components/pingzheng-fillin/layouts/table/columns/others/bizhong.vue';
import huiLv from '/@/components/pingzheng-fillin/layouts/table/columns/others/huiLv.vue';
import currentMoney from '/@/components/pingzheng-fillin/layouts/table/columns/others/currentMoney.vue';
import { isNaN } from 'lodash-es';

export function createHuiLvColumn(rowDefines, tableData) {
  return createColumnDefine('huiLv', {
    columns: [
      {
        key: 'bizhong',
        label: '汇率金额',
        width: '100',
        component: shallowRef(bizhong),
        register({ target, row, rowIndex, model }) {
          rowDefines.value[rowIndex].target.biZhong = target;
          model.next = function () {
            rowDefines.value[rowIndex].target.jieMoney.focus();
          };
        },
      },
      // {
      //   key: 'huiLv',
      //   label: '汇率',
      //   width: '100',
      //   component: shallowRef(huiLv),
      //   register({ target, row, rowIndex, model }) {
      //     rowDefines.value[rowIndex].target.huiLv = target;
      //     target.left = () => {
      //       if (rowDefines.value[rowIndex].target.shuliang != null) {
      //         rowDefines.value[rowIndex].target.danJia.focus();
      //       }
      //     };
      //     target.next = () => {
      //       if (rowDefines.value[rowIndex].target.jieMoney != null) {
      //         rowDefines.value[rowIndex].target.currenyMoney.focus();
      //       }
      //     };
      //     target.row = row;
      //     target.change = shuLiangChange(row, model);
      //   },
      // },
      // {
      //   key: 'currenyMoney',
      //   label: '原币金额',
      //   width: '100',
      //   component: shallowRef(currentMoney),
      //   register({ target, row, rowIndex, model }) {
      //     rowDefines.value[rowIndex].target.currenyMoney = target;
      //     target.row = row;
      //     target.left = () => {
      //       rowDefines.value[rowIndex].target.huiLv.focus();
      //     };
      //     target.next = () => {
      //       if (rowDefines.value[rowIndex].target.jieMoney != null) {
      //         rowDefines.value[rowIndex].target.jieMoney.focus();
      //       }
      //     };
      //     target.change = function () {
      //       if (
      //         row.huiLv.value.currencyExchangeRate == 'NaN' ||
      //         row.huiLv.value.currencyExchangeRate == null ||
      //         row.huiLv.value.currencyExchangeRate == ''
      //       ) {
      //         row.huiLv.value.currencyExchangeRate = 0;
      //       }
      //
      //       if (row.shuliangjine.value.slNumber == null || row.shuliangjine.value.slNumber == '') {
      //         row.shuliangjine.value.slNumber = 0;
      //       }
      //
      //       let slPrice =
      //         parseInt(row.huiLv.value.currencyExchangeRate) / row.shuliangjine.value.slNumber;
      //       if (isNaN(slPrice)) {
      //         slPrice = 0;
      //       }
      //       if (row.shuliangjine.value.slNumber == 0) {
      //         slPrice = 0;
      //       }
      //       row.shuliangjine.value.slPrice = parseFloat(slPrice).toFixed(2);
      //     };
      //   },
      // },
    ],
    defineData: () => ({
      value: {
        currencyType: null,
        currencyExchangeRate: '1.00',
        currencyMoney: '0.00',
      },

      columnDatas: {
        bizhong: {
          text: 'sadsasasd',
          readonly: true,
        },
        huiLv: {
          text: '',
          readonly: false,
        },
        currenyMoney: {
          text: '',
          readonly: false,
        },
      },
      showText: ref(),
      isShowText222: ref(true),
    }),

    resultMergeEvent(row) {
      const res = {
        isError: false,
      };
      if (row.jieMoney.value != '0.00') {
        res.data = [
          // 数量
          ['ndS', row.shuliangjine.value.slNumber],
          ['mdF', row.huiLv.value.currencyType],
          // 原币金额 写当前行，改为贷方金额
          ['nfratMd', row.huiLv.value.currencyMoney],
          // ['测试', row.huiLv.value.currencyMoney],
        ];
      } else if (row.daiMoney.value != '0.00') {
        res.data = [
          // 数量
          ['ncS', row.shuliangjine.value.slNumber],
          ['mdF', row.huiLv.value.currencyType],
          // 原币金额
          ['nfratMc', row.huiLv.value.currencyMoney],
        ];
      } else {
        res.isError = true;
        res.msg = 'row.jieMoney.value错误格式';
      }
      return res;
    },
  });
}
