import { defineFuZhuHeSuanModel } from '../../utils/columns/fuZhuHeSuanUtils';
import { ref } from 'vue';
import { findSettModesAll } from '/@/api/record/system/sett-modes';
import { useRouterApi } from '/@/utils/boozsoft/datasource/datasourceUtil';
import { findKuaiJiKeMu } from '/@/api/codekemu/codekemu';
import { useCurrentPageDatasourcePickerState } from '/@/layouts/page/ProvideDataFuns';
import { usePingzhengGridText } from '/@/components/pingzheng-fillin/layouts/table/columns/KuaiJiKeMu/layouts/content/funs';
import { usePingZhengModelDataHelper } from '/@/components/pingzheng-fillin/hooks/transforms/apiReference';
import { getBizhongText, getShuliangText } from '/@/components/pingzheng-fillin/hooks/funs/abc';
import {creatApiRow} from "/@/components/pingzheng-fillin/hooks/models/apiData";
import {toAccountNumLength} from "/@/components/pingzheng-fillin/utils/accountUtils";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {usePageDataStore} from "/@/store/modules/pageDataStore";

function getSlNumber(item) {
  let slNumber;
  if (item.slJieMoney && item.slJieMoney != '') {
    slNumber = item.slJieMoney;
  }

  if (item.slDaiMoney && item.slDaiMoney != '') {
    slNumber = item.slDaiMoney;
  }
  return slNumber;
}

function getFuZhuHeSuanValue(it) {
  const fuZhuHeSuanValues = {
    fzDept: it.fzDept,
    fzEmp: it.fzEmp,
    fzCustom: it.fzCustom,
    fzGys: it.fzGys,
    fzItem: it.fzItem,
    fzItemClass: it.fzItemClass,
  };
  const fuZhuHeSuanValueKeys = Object.keys(fuZhuHeSuanValues).filter(
    (key) => fuZhuHeSuanValues[key] != null,
  );
  const fuZhuValue = defineFuZhuHeSuanModel()
    .filter((item) => fuZhuHeSuanValueKeys.indexOf(item.key) != -1)
    .map((item) => {
      item.value.key = fuZhuHeSuanValues[item.key];
      item.value.label = '技术部';
      item.value.value = fuZhuHeSuanValues[item.key];
      return item;
    });
  return fuZhuValue;
}

// 转apiModel

function toRowData(item) {
  const rowData = {
    zhaiYao: {
      thisInoid: item.thisInoid,
      value: item.zhaiYao,
      showText: item.zhaiYao,
      isShowText222: ref(true),
      columnDatas: {
        zhaiYao: {
          text: item.zhaiYao,
          readonly: true,
        },
      },
    },
    kuaiJiKeMu: {
      value: {
        code: item.ccode,
        name: item.ccodeName,
        path: '',
      },
      columnDatas: {
        kuaiJiKeMu: {
          text: item.ccode + ' ' + item.ccodeName,
          readonly: false,
        },
      },

      showText: item.ccode + ' ' + item.ccodeName,
      isShowText222: ref(true),
    },
    jieMoney: {
      value: item.md,

      columnDatas: {
        jieMoney: {
          text: '',
          readonly: false,
        },
      },
      showText: '',
      isShowText222: ref(true),
    },
    daiMoney: {
      value: item.mc,
      showText: '',

      columnDatas: {
        daiMoney: {
          text: '',
          readonly: false,
        },
      },
      isShowText222: ref(true),
    },
    fuZhuHeSuan: {
      jieSuanCompany: item.pjUnitName,
      jieSuanMode: item.pjCsettle,
      piaoJuDate: item.pjDate,
      piaoJuNumber: item.pjId,
      value: getFuZhuHeSuanValue(item),
      showText: '',

      columnDatas: {
        fuZhuHeSuan: {
          text: '',
          readonly: false,
        },
      },

      isShowText222: ref(true),
    },
    huiLv: {
      value: {
        currencyType: item.currencyType,
        currencyExchangeRate: item.currencyExchangeRate,
        currencyMoney: item.currencyMoney,
      },
      columnDatas: {
        bizhong: {
          text: `<div>汇率:${item.currencyExchangeRate}</div><div>金额:${item.currencyMoney}</div>`,
          readonly: false,
        },
        huiLv: {
          text: item.currencyExchangeRate,
          readonly: false,
        },
        currenyMoney: {
          text: item.currencyMoney,
          readonly: false,
        },
      },
      showText: '222',
      isShowText222: ref(true),
    },
    shuliangjine: {
      value: {
        slUnit: item.slUnit,
        slPrice: item.slPrice,
        slNumber: getSlNumber(item),
      },
      columnDatas: {
        jiliangdanwei: {
          text: `<div>汇率:${item.slNumber}</div><div>金额:${item.slPrice}</div>`,
          readonly: false,
        },
        shuliang: {
          text: item.currencyExchangeRate,
          readonly: false,
        },
        danjia: {
          text: item.currencyMoney,
          readonly: false,
        },
      },
      showText: '',
      isShowText222: ref(true),
    },
  };
  return rowData;
}

async function getJieSuanFangshiName(key) {
  const jieSuanDanWeiItems = (await findSettModesAll()).items;
  const arr = jieSuanDanWeiItems.filter((item) => item.settModesCode == key);
  if (arr.length == 0) {
    throw new Error('结算方式转换错误');
  }
  return arr[0].settModesName;
}

function getCoName(item, fuZhuXiangModelValue) {
  function getInfo(key) {
    return fuZhuXiangModelValue.filter((it) => it.key == key)[0];
  }
  if (item.fzCustom != null) {
    return getInfo('fzCustom').modelValue.label;
  } else if (item.fzGys) {
    return getInfo('fzGys').modelValue.label;
  } else {
    return item.pjUnitName;
  }
}
export function useFuZhuHeSuanText(item, fuZhuXiangModelValue) {
  return {
    async toFuZhuHeSuanText() {
      if (item.pjCsettle == null) {
        return null;
      }

      async function getJieSuanFangShiText(item) {
        return (
          getCoName(item, fuZhuXiangModelValue) +
          '-' +
          (await getJieSuanFangshiName(item.pjCsettle)) +
          '-' +
          item.pjDate +
          '-' +
          item.pjId
        );
      }
      return (
        (await getJieSuanFangShiText(item)) +
        '-' +
        fuZhuXiangModelValue.map((it) => it.modelValue.label)
      );
    },
    async toFuZhuHeSuanHtml() {
      if (item.pjCsettle == null) {
        return null;
      }

      async function getJieSuanFangShiText(item) {
        return (
          '结算单位:' +
          getCoName(item, fuZhuXiangModelValue) +
          '<br>' +
          '结算方式' +
          (await getJieSuanFangshiName(item.pjCsettle)) +
          '<br>' +
          '单据日期' +
          item.pjDate +
          '<br>' +
          '单据号：' +
          item.pjId
        );
      }
      return (
        (await getJieSuanFangShiText(item)) +
        '<br>' +
        fuZhuXiangModelValue.map((it) => it.label + ': ' + it.modelValue.label).join('<br>')
      );
    },
  };
}

function useKuaiJiKeMuGridText(item, fuZhuXiangModelValue) {
  const { toFuZhuHeSuanText, toFuZhuHeSuanHtml } = useFuZhuHeSuanText(item, fuZhuXiangModelValue);

  return {
    async toKuaiJiKeMuGridText() {
      const fuZhuHeSuanText = await toFuZhuHeSuanText();

      const pingzhengGridText = usePingzhengGridText(null);
      pingzhengGridText.setKuaiJiKeMuInfoText(item.ccode + ' ' + item.ccodeName);
      pingzhengGridText.setFuZhuXiangText(fuZhuHeSuanText);

      return pingzhengGridText.getAllText();
    },

    async toKuaiJiKeMuGridHtml() {
      return await toFuZhuHeSuanHtml();
    },
  };
}

async function toRowDataAsync(hello) {
  const item = hello[0];
  getSlNumber(item);
  const fuZhuXiangModelValue = hello[1];
  const { toKuaiJiKeMuGridText, toKuaiJiKeMuGridHtml } = useKuaiJiKeMuGridText(
    item,
    fuZhuXiangModelValue,
  );
  const rowData = {
    zhaiYao: {
      thisInoid: item.thisInoid,
      value: item.zhaiYao,
      showText: item.zhaiYao,
      isShowText222: ref(true),
      columnDatas: {
        zhaiYao: {
          text: item.zhaiYao,
          readonly: true,
        },
      },
    },
    kuaiJiKeMu: {
      value: {
        code: item.ccode,
        name: item.ccodeName,
        path: '',
      },
      columnDatas: {
        kuaiJiKeMu: {
          text: await toKuaiJiKeMuGridText(),
          tooltip: await toKuaiJiKeMuGridHtml(),
          readonly: false,
        },
      },

      showText: item.ccode + ' ' + item.ccodeName,
      isShowText222: ref(true),
    },
    jieMoney: {
      value: item.md,

      columnDatas: {
        jieMoney: {
          text: '',
          readonly: false,
        },
      },
      showText: '',
      isShowText222: ref(true),
    },
    daiMoney: {
      value: item.mc,
      showText: '',

      columnDatas: {
        daiMoney: {
          text: '',
          readonly: false,
        },
      },
      isShowText222: ref(true),
    },
    fuZhuHeSuan: {
      jieSuanCompany: item.pjUnitName,
      jieSuanMode: item.pjCsettle,
      piaoJuDate: item.pjDate,
      piaoJuNumber: item.pjId,
      value: getFuZhuHeSuanValue(item),
      showText: '',

      columnDatas: {
        fuZhuHeSuan: {
          text: '',
          readonly: false,
          // 测试
        },
      },

      isShowText222: ref(true),
    },
    huiLv: {
      value: {
        currencyType: item.currencyType,
        currencyExchangeRate: item.currencyExchangeRate,
        currencyMoney: item.currencyMoney,
      },
      columnDatas: {
        bizhong: {
          text: getBizhongText('test11', 'test122'),
          readonly: false,
        },
        huiLv: {
          text: item.currencyExchangeRate,
          readonly: false,
        },
        currenyMoney: {
          text: item.currencyMoney,
          readonly: false,
        },
      },
      showText: '222',
      isShowText222: ref(true),
    },
    shuliangjine: {
      value: {
        slUnit: item.slUnit,
        slPrice: item.slPrice,
        slNumber: getSlNumber(item),
      },
      columnDatas: {
        jiliangdanwei: {
          text: getShuliangText('test33', 'test144'),
          readonly: false,
        },
        shuliang: {
          text: getSlNumber(item),
          readonly: false,
        },
        danjia: {
          text: item.slPrice,
          readonly: false,
        },
      },
      showText: '',
      isShowText222: ref(true),
    },
  };
  return rowData;
}

function apiDataRowToModelRows(apiData) {
  const { toRowDataModel } = usePingZhengModelDataHelper();
  for (let i = apiData.length; i < 16; i++) {
    apiData.push(creatApiRow());
  }
  return apiData.map(toRowDataModel).map(toRowData);
}

async function apiDataRowToModelRowsAsync(apiData, assign) {
  const { toRowDataModel } = usePingZhengModelDataHelper();
  const apiDataModel = apiData
    .map(toRowDataModel)
    .map((it, index) => [it, assign.fuZhuXiangModelValues[index]]);

  return await Promise.all(apiDataModel.map((it) => toRowDataAsync(it)));
}

export function apiDataToModel(apiData, assign) {
  const { toOptionsModel } = usePingZhengModelDataHelper();

  const options = toOptionsModel(apiData[0]);

  if (assign.options != null) {
    Object.assign(options, assign.options);


  }
  options.optionInoId=toAccountNumLength(
    parseInt(apiData[0].inoId) + 1,
    useCompanyOperateStoreWidthOut().getCurrentAccountInfo.accvouchDec,
  );

  const rows = apiDataRowToModelRows(apiData);
  const settings = assign.settings;
  const model = {
    datasource: ref(),
    settings: {
      titleName: '新增凭证',
      ...settings,
    },
    repository: {
      zhaiYao: {
        data: [],
        findAll() {},
      },
      kuaiJiKeMu: {
        data: null,
        dataModel: null,
        async findAll(params) {
          const { usePageRouterApi } = useRouterApi();
          const kuaiJiKeMuRepository = model.repository.kuaiJiKeMu;
          if (kuaiJiKeMuRepository.data == null) {
            const arr = await usePageRouterApi(findKuaiJiKeMu)(params);
            kuaiJiKeMuRepository.data = arr;
          }
          return kuaiJiKeMuRepository.data;
        },
        async findModelAll(params) {
          const usePageRouterApi = useCurrentPageDatasourcePickerState().usePageRouterApi;
          const datasourcePicker=usePageDataStore().getDatasourcePicker

          const kuaiJiKeMuRepository = model.repository.kuaiJiKeMu;
          if (kuaiJiKeMuRepository.data == null) {
            const arr = await usePageRouterApi(findKuaiJiKeMu)({
              iyear: datasourcePicker.params.year,
              bend: '1',
            });
            kuaiJiKeMuRepository.data = arr;
          }
          return kuaiJiKeMuRepository.data.map((it) => ({
            key: it.ccode,
            label: it.ccodeName,
            label2: it.ccode,
            enable: false,
            target: it,
          }));
        },
      },
      fuZhuHeSuan: {
        data: [],
        jieSuanFangShiAll: null,
        jieSuanFangShiModels: null,
        findAll() {},
        async findJieSuanFangShiAll() {
          const fuZhuHeSuanRepository = model.repository.fuZhuHeSuan;
          if (fuZhuHeSuanRepository.jieSuanFangShiAll == null) {
            fuZhuHeSuanRepository.jieSuanFangShiAll = (await findSettModesAll()).items;
            fuZhuHeSuanRepository.jieSuanFangShiModels =
              fuZhuHeSuanRepository.jieSuanFangShiAll.map((it) => ({
                key: it.settModesCode,
                label: it.settModesName,
                target: it,
              }));
          }
          return fuZhuHeSuanRepository.jieSuanFangShiModels;
        },
      },
    },
    options,
    rows,
  };
  return model;
}

export async function apiDataToModelAsync(apiData, assign) {
  const { toOptionsModel } = usePingZhengModelDataHelper();

  const options = toOptionsModel(apiData[0]);
  if (assign.options != null) {
    Object.assign(options, assign.options);
  }
  const rows = await apiDataRowToModelRowsAsync(apiData, assign);
  const settings = assign.settings;
  const model = {
    datasource: ref(),
    settings: {
      titleName: '新增凭证',
      ...settings,
    },
    repository: {
      zhaiYao: {
        data: [],
        findAll() {},
      },
      kuaiJiKeMu: {
        data: null,
        dataModel: null,
        async findAll(params) {
          const { usePageRouterApi } = useRouterApi();
          const kuaiJiKeMuRepository = model.repository.kuaiJiKeMu;
          if (kuaiJiKeMuRepository.data == null) {
            const arr = await usePageRouterApi(findKuaiJiKeMu)(params);
            kuaiJiKeMuRepository.data = arr;
          }
          return kuaiJiKeMuRepository.data;
        },
        async findModelAll(params) {
          const usePageRouterApi = useCurrentPageDatasourcePickerState().usePageRouterApi;
          const kuaiJiKeMuRepository = model.repository.kuaiJiKeMu;
          if (kuaiJiKeMuRepository.data == null) {
            const arr = await usePageRouterApi(findKuaiJiKeMu)({
              iyear: '2021',
              bend: '1',
            });
            kuaiJiKeMuRepository.data = arr;
          }
          return kuaiJiKeMuRepository.data.map((it) => ({
            key: it.ccode,
            label: it.ccodeName,
            label2: it.ccode,
            enable: false,
            target: it,
          }));
        },
      },
      fuZhuHeSuan: {
        data: [],
        jieSuanFangShiAll: null,
        jieSuanFangShiModels: null,
        findAll() {},
        async findJieSuanFangShiAll() {
          const fuZhuHeSuanRepository = model.repository.fuZhuHeSuan;
          if (fuZhuHeSuanRepository.jieSuanFangShiAll == null) {
            fuZhuHeSuanRepository.jieSuanFangShiAll = (await findSettModesAll()).items;
            fuZhuHeSuanRepository.jieSuanFangShiModels =
              fuZhuHeSuanRepository.jieSuanFangShiAll.map((it) => ({
                key: it.settModesCode,
                label: it.settModesName,
                target: it,
              }));
          }
          return fuZhuHeSuanRepository.jieSuanFangShiModels;
        },
      },
    },
    options,
    rows,
  };
  return model;
}
