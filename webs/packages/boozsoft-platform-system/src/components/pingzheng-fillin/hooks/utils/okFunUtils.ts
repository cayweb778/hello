import { creatApiRow } from '/@/components/pingzheng-fillin/hooks/models/apiData';
import { message } from 'ant-design-vue';
import { apiDataToShowModel } from '/@/components/pingzheng-fillin/hooks/models/datas/model';
import {
  delPingZhengByUniqueCodeApi,
  insertPingZheng,
  savePingZheng,
  savePingZhengTemp,
  saveTempPingZhengApi,
} from '/@/api/pingzheng/pingzheng';
import { useBoozSoftStoreWidthOut } from '/@/store/modules/boozsoft/boozsoft-app';
import {
  usePingZhengModelDataHelper
} from "/@/components/pingzheng-fillin/hooks/transforms/apiReference";
import {usePingzhengReloadFun} from "/@/components/pingzheng-fillin/hooks/usePingzheng";
function requireModelsResultMergeEvents(columnModels, item) {
  const resultMergeEvents = item.requireModels.map(
    (requireModelName) =>
      columnModels.filter((item3) => item3.key == requireModelName)[0].resultMergeEvent,
  );
  return resultMergeEvents;
}
function toApiData222(item, resultMergeEvents) {
  // 校验后的数据
  const rowModelVerfiyData = resultMergeEvents.flatMap((mergeEvent) =>
    mergeEvent(item.rowData, item.rowIndex),
  );

  // 错误处理
  const errorRow = rowModelVerfiyData
    .filter((item) => item.isError)
    .map((item2) => {
      return '行' + (item.rowIndex + 1) + '：' + item2.msg;
    });

  if (errorRow.length > 0) {
    // errorRows.push(errorRow);
  }
  return rowModelVerfiyData.flatMap((item) => item.data);
}
function useToRowModelVerfiyData(columnModels) {
  return {
    toRowModelVerfiyData(itemxxx) {
      const resultMergeEvents = requireModelsResultMergeEvents(columnModels, itemxxx);

      const apiData = toApiData222(itemxxx, resultMergeEvents);
      console.log(apiData);
      return apiData;
    },
  };
}
function useFun(rowModelVerfiyDatas, errorRows, options) {
  const { toOptionsApi } = usePingZhengModelDataHelper();
  const fun = {
    getRowModelVerfiyDatas() {
      return rowModelVerfiyDatas;
    },
    hasError() {
      return errorRows.length > 0;
    },
    getErrorRows() {
      return errorRows;
    },
    rowModelToTempApiData() {
      const apiData = rowModelVerfiyDatas.map((item) => {
        let toApi = {};
        item.forEach((item2) => {
          // console.log(item2)
          if (item2 != null) {
            toApi[item2[0]] = item2[1];
          }
        });
        // console.log(JSON.stringify(item))
        // console.log(JSON.stringify(toApi))
        toApi = Object.assign(creatApiRow(), toApi);
        const optionsApi = toOptionsApi(options);
        toApi = Object.assign(toApi, optionsApi);
        return toApi;
      });
      return apiData;
    },
    rowModelToApiData() {
      return toApiData(fun.getRowModelVerfiyDatas(), options);
    },
  };
  return fun;
}
export function useModelToApi(tableData) {
  return {
    async toApiData(params?) {
      const { toRowModelVerfiyData } = useToRowModelVerfiyData(tableData.value.columnModels);
      const rowModels = toEffectiveRowModel(tableData.value.rowDefines);
      if (params == null) {
        params = {};
      }
      const isTemp = params.isTemp;
      const isZanCun = params.isZanCun;
      const options = tableData.value.options;

      const errorRows = [];

      const rowModelVerfiyDatas = rowModels.map(toRowModelVerfiyData);
      const { rowModelToTempApiData, hasError, getErrorRows, rowModelToApiData } = useFun(
        rowModelVerfiyDatas,
        errorRows,
        options,
      );
      if (isTemp) {
        return rowModelToTempApiData();
      } else if (isZanCun) {
        // 行数校验
        if (rowModels.length == 0) {
          errorRows.push(rowModels.length + '行分录，不符合规则');
        }
        if (hasError()) {
          throw new Error(getErrorRows());
        } else {
          return rowModelToApiData();
        }
      } else {
        // 行数校验
        if (rowModels.length == 0 || rowModels.length == 1) {
          errorRows.push(rowModels.length + '行分录，不符合规则');
        }
        if (hasError()) {
          throw new Error(getErrorRows());
        } else {
          const aaa = rowModelToApiData();
          return aaa;
        }
      }
    },
  };
}

// 获取每列API数据

export function returnError(err, callback) {
  message.error(err);
  callback();
}

export function toApiDataModel(apiData, params) {
  const a = apiDataToShowModel(apiData, params);
  return a;
}

export function isAdd(typeLabel) {
  return typeLabel == '填制' || typeLabel == '暂存' || typeLabel == '冲销' || typeLabel == '复制';
}

export function isEmptyRow(item) {
  return item.zhaiYao.value == '' && item.kuaiJiKeMu.value.name == '';
}

export function createNewPingzhengModel() {
  const model = toApiDataModel([creatApiRow()], {
    settings: {
      titleName: '记账凭证',
      typeLabel: '填制',
    },
  });
  return model;
}

export function isTempEmptyRow(item) {
  return (
    item.zhaiYao.value == '' &&
    item.kuaiJiKeMu.value.name == '' &&
    item.jieMoney.value == '0.00' &&
    item.daiMoney.value == '0.00'
  );
}

// 有效行
export function toEffectiveRowModel(rowDefines) {
  // 清除空行
  return rowDefines.filter((rowDefine) => !isEmptyRow(rowDefine.rowData));
}

export function toApiData(rowModelVerfiyDatas, options) {
  const { toOptionsApi } = usePingZhengModelDataHelper();
  const apiData = rowModelVerfiyDatas.map((it) => {
    let toApi = {};
    it.forEach((it2) => (toApi[it2[0]] = it2[1]));
    toApi = Object.assign(creatApiRow(), toApi);
    const optionsApi = toOptionsApi(options);
    toApi = Object.assign(toApi, optionsApi);
    return toApi;
  });
  console.log(apiData);
  return apiData;
}
export function usePingzhengState(tableData) {
  const ADD = 'add';
  const MODIFY = 'modify';
  const INSERT = 'insert';
  return {
    getTypeLabelEnum() {
      if (isAdd(tableData.value.settings.typeLabel)) {
        return ADD;
      } else if (tableData.value.settings.typeLabel == '修改') {
        return MODIFY;
      } else if (tableData.value.settings.typeLabel == '插入') {
        return INSERT;
      }
    },
    ADD,
    MODIFY,
    INSERT,
  };
}
export function useSaveFuns() {
  return {
    async savePingZhengFun(apiData) {
      await savePingZheng({ json: JSON.stringify(apiData) });
    },
    async insertPingZhengFun(apiData) {
      await insertPingZheng({ json: JSON.stringify(apiData) });
    },
    async saveZanCunPingZhengFun(apiData) {
      await await saveTempPingZhengApi({ json: JSON.stringify(apiData) }, null);
    },
    async saveTempPingzhengFun(usePageRouterApi, apiData) {
      const userInfo = useBoozSoftStoreWidthOut().getUserInfo;
      const currentDataPicker = useBoozSoftStoreWidthOut().getCurrentDataPicker;
      await usePageRouterApi(savePingZhengTemp)({
        userId: userInfo.id,
        tenantId: currentDataPicker.params.accountMode,
        data: JSON.stringify(apiData),
        iyear: currentDataPicker.params.iyear,
      });
    },
    async deletePingzheng(id) {
      await delPingZhengByUniqueCodeApi({ uniqueCode: id }, null).then();
    },
  };
}

export function usePingzhengReload(tableData) {
  return {
    openNewPingzheng() {
      const {onReloaded}=usePingzhengReloadFun()
      tableData.value.reloadPingZheng(createNewPingzhengModel(), { type: '新增' });

      onReloaded(()=>{
        function hasTableRender() {
          return document.getElementsByClassName('zhaiYaoTextarea').length > 0
        }
        debugger
        this.columnModels.value.rowDefines[0].target.zhaiYao.focus()
        const interval=setInterval(()=>{
          console.log(1)
          if(hasTableRender()){
            window.clearInterval(interval)

            this.columnModels.value.rowDefines[0].target.zhaiYao.focus()
          }

        },100)

      })
      //
      // if (rowIndex == 0 && !tableData.value.settings.enableModelSave) {
      //   target.focus();
      // }
    },
    async reloadShowPingzheng() {
      await tableData.value.pageStore.reload();
      tableData.value.pageStore.pre();
    },
  };
}

export function goError(e, errowRows) {
  e();
  message.error(errowRows);
}
