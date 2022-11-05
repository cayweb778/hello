import {
  createColumnDefine,
  shuLiangChange,
} from '/@/components/pingzheng-fillin/hooks/funs/columnModelsFuns';
import { ref, shallowRef } from 'vue';
import jiliangdanwei from '/@/components/pingzheng-fillin/layouts/table/columns/others/jiliangdanwei.vue';
import shuliang from '/@/components/pingzheng-fillin/layouts/table/columns/others/shuliang.vue';
import danjia from '/@/components/pingzheng-fillin/layouts/table/columns/others/danjia.vue';

export function createShuliangjine(rowDefines, tableData) {
  return createColumnDefine('shuliangjine', {
    columns: [
      {
        key: 'jiliangdanwei',
        label: '数量金额',
        width: '100',

        component: shallowRef(jiliangdanwei),
        register({ target, row, rowIndex, model }) {
          target.row = row;
          rowDefines.value[rowIndex].target.jiLiangDanWei = target;
          // target.change = shuLiangChange(row, model);
          model.change = shuLiangChange(row, model);
          model.next = function (){
            if (rowDefines.value[rowIndex].requireModels.indexOf('huiLv') != -1) {
              rowDefines.value[rowIndex].target.biZhong.focus();
            } else {
              rowDefines.value[rowIndex].target.jieMoney.focus();
            }
          }


        },
      },
      // {
      //   key: 'shuliang',
      //   label: '数量',
      //   width: '100',
      //
      //   component: shallowRef(shuliang),
      //   register({ target, row, rowIndex, model }) {
      //     rowDefines.value[rowIndex].target.shuliang = target;
      //     target.left = () => {
      //       if (rowDefines.value[rowIndex].requireModels.indexOf('fuZhuHeSuan') != -1) {
      //         rowDefines.value[rowIndex].target.fuZhuHeSuan.focus();
      //       } else {
      //         rowDefines.value[rowIndex].target.kuaiJiKeMu.focus();
      //       }
      //     };
      //     target.next = () => {
      //       rowDefines.value[rowIndex].target.danJia.focus();
      //     };
      //     target.row = row;
      //     target.change = shuLiangChange(row, model);
      //   },
      // },
      // {
      //   key: 'danjia',
      //   label: '单价',
      //   width: '100',
      //
      //   component: shallowRef(danjia),
      //   register({ target, row, rowIndex, model }) {
      //     rowDefines.value[rowIndex].target.danJia = target;
      //     target.left = () => {
      //       rowDefines.value[rowIndex].target.shuliang.focus();
      //     };
      //     target.next = () => {
      //       if (rowDefines.value[rowIndex].target.huiLv != null) {
      //         rowDefines.value[rowIndex].target.huiLv.focus();
      //       } else if (rowDefines.value[rowIndex].target.jieMoney != null) {
      //         rowDefines.value[rowIndex].target.jieMoney.focus();
      //       }
      //     };
      //     target.row = row;
      //     target.change = shuLiangChange(row, model);
      //   },
      // },
    ],
    defineData: () => ({
      value: { slUnit: '', slPrice: '', slNumber: '' },
      columnDatas: {
        jiliangdanwei: {
          text: '123213',
          readonly: false,
        },
        shuliang: {
          text: '',
          readonly: false,
        },
        danjia: {
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
        data: [
          ['unitMeasurement', row.shuliangjine.value.slUnit],
          ['cunitPrice', row.shuliangjine.value.slPrice],
        ],
      };

      const data = [];

      if (row.jieMoney.value != '0.00') {
        res.data.push(['ndS', row.shuliangjine.value.slNumber]);
      } else if (row.daiMoney.value != '0.00') {
        res.data.push(['ncS', row.shuliangjine.value.slNumber]);
      } else {
        res.msg = 'row.jieMoney.value错误格式';
      }
      return res;
    },
  });
}
