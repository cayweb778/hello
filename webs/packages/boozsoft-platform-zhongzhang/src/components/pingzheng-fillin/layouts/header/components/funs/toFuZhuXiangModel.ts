import { useFuZhuHeSuan } from '/@/components/pingzheng-fillin/hooks/funs/columnModelsFuns';
import { getFuZhuHeSuanDefines } from '/@/components/pingzheng-fillin/layouts/table/columns/commponents/FuZhuHeSuan/hooks/fuZhuHeSuanTable';
import { getFuZhuXiangCorresField } from '/@/components/pingzheng-fillin/hooks/funs/columnModelsFuns';
import {usePageDataStore} from "/@/store/modules/pageDataStore";

function findCodeInfo(arr, ccode) {
  return arr.filter((it2) => (it2.ccode = ccode))[0];
}

function toModelValue(fuZhuHeSuanDefines, apiRowData) {
  const fuZhuXiangCorresField = getFuZhuXiangCorresField();
  const fuZhuXiangModelValue = fuZhuXiangCorresField
    .map((it1) => {
      const modelFiled = it1[1];
      const apiFiled = it1[0];

      function getList(key) {
        if (fuZhuHeSuanDefines.filter((it) => it.key == key).length == 0) {
          return [];
        }
        return fuZhuHeSuanDefines.filter((it) => it.key == key)[0].list;
      }

      function getLabel(list, key) {
        if (list.filter((it) => it.key == key).length == 0) {
          return '';
        }
        return list.filter((it) => it.key == key)[0].label;
      }
      function getFuZhuHeSuanDefine() {
        const arr = fuZhuHeSuanDefines.filter((it) => it.key == modelFiled);
        if (arr.length == 0) {
          return '错误:2322';
        }
        return arr[0];
      }
      const list = getList(modelFiled);
      return {
        key: modelFiled,
        label: getFuZhuHeSuanDefine().label,
        modelValue: {
          key: apiRowData[apiFiled],
          label: getLabel(list, apiRowData[apiFiled]),
        },
      };
    })
    .filter((it1) => it1.modelValue.key != null);
  return fuZhuXiangModelValue;
}

export async function getFuZhuXiangModelValues(findModelAll, apiData) {
  const arr = await findModelAll();

  const abc = await Promise.all(
    apiData.map(async (it) => {
      const codeInfo = findCodeInfo(arr, it.ccode).target;
      const fuZhuXiang = useFuZhuHeSuan(codeInfo).getFuZhuXiang();
      const fuZhuHeSuanDefines = await getFuZhuHeSuanDefines(fuZhuXiang.value);
      return toModelValue(fuZhuHeSuanDefines, it);
    }),
  );
  return abc;
}
