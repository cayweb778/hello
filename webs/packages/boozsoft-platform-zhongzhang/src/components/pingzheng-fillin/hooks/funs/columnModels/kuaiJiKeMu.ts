import {
  createColumnDefine,
  createKuaiJiKeMuInputEvents,
} from '/@/components/pingzheng-fillin/hooks/funs/columnModelsFuns';
import { ref, shallowRef } from 'vue';
import kuaiJiKeMu from '/@/components/pingzheng-fillin/layouts/table/columns/KuaiJiKeMu/index.vue';

export function createKuaiJiKeMu(rowDefines, tableData) {
  return createColumnDefine('kuaiJiKeMu', {
    columns: [
      {
        key: 'kuaiJiKeMu',
        label: '会计科目',
        width: '420',
        component: shallowRef(kuaiJiKeMu),
        register({ target, row, rowIndex,model }) {
          rowDefines.value[rowIndex].target.kuaiJiKeMu = target;
          Object.assign(target, createKuaiJiKeMuInputEvents(row, target, rowIndex, rowDefines));
          model.next=function (){
             if (rowDefines.value[rowIndex].requireModels.indexOf('shuliangjine') != -1) {
              rowDefines.value[rowIndex].target.jiLiangDanWei.focus();
            } else if (rowDefines.value[rowIndex].requireModels.indexOf('huiLv') != -1) {
              rowDefines.value[rowIndex].target.huiLv.focus();
            } else {
               debugger
              rowDefines.value[rowIndex].target.jieMoney.focus();
            }
          }
          // pingzhengGridParams.modelValue.next();
        },
      },
    ],
    defineData: () => ({
      value: {
        code: '',
        name: '',
        path: '',
      },

      columnDatas: {
        kuaiJiKeMu: {
          text: '',
          readonly: false,
        },
      },

      isShowText222: ref(true),
      showText: ref(),
    }),

    resultMergeEvent(row) {
      const isError = row.kuaiJiKeMu.value.code == '' || row.kuaiJiKeMu.value.name == '';
      return {
        isError,
        msg: isError ? '会计为空' : null,
        data: [
          ['ccode', row.kuaiJiKeMu.value.code],
          ['ccodeName', row.kuaiJiKeMu.value.name],
        ],
      };
    },
  });
}
