import { getFuZhuHeSuanDefines } from '/@/components/pingzheng-fillin/layouts/table/columns/commponents/FuZhuHeSuan/hooks/fuZhuHeSuanTable';
import { ref } from 'vue';

export function waitLoadPingzheng(loading) {
  return new Promise((r, e) => {
    let i = 0;
    const interval = setInterval(() => {
      if (i++ == 100) {
        e('刷新会计科目错误，请检查');
        window.clearInterval(interval);
      }
      if (!loading.value) {
        r();
        window.clearInterval(interval);
      }
    }, 50);
  });
}

export async function hasFuZhuXiang(pingzhengGridParams) {
  const fuZhuHeSuanData = pingzhengGridParams.columnDefine.rowData.fuZhuHeSuan;

  const requireFuZhuColumns = await getFuZhuHeSuanDefines(fuZhuHeSuanData.value);
  return requireFuZhuColumns.length > 0;
}

export function usePingzhengGridText(abc) {
  const kuaiJiKeMuInfoText = ref();
  let fuZhuXiangText = null;

  function getAllText() {
    let thisFuZhuXiangText = '';
    if (fuZhuXiangText != null) {
      thisFuZhuXiangText = '[' + fuZhuXiangText + ']';
    }
    return kuaiJiKeMuInfoText.value + thisFuZhuXiangText;
  }

  function setPingzhengGridText() {
    setTimeout(() => {
      if (abc != null) {
        abc.columnDatas['kuaiJiKeMu'].text = getAllText();
      }
    }, 10);
  }

  return {
    getAllText,
    setFuZhuXiangText($fuZhuXiangText) {
      fuZhuXiangText = $fuZhuXiangText;
      setPingzhengGridText();
    },
    setKuaiJiKeMuInfoText($kuaiJiKeMuInfoText) {
      kuaiJiKeMuInfoText.value = $kuaiJiKeMuInfoText;
      setPingzhengGridText();
    },
    getKuaiJiKeMuInfoText() {
      return kuaiJiKeMuInfoText;
    },
    getFuZhuXiangText() {
      return fuZhuXiangText;
    },
  };
}

export function useCodeInfo(
  gridData,
  pingzhengGridTextFuns,
  pingzhengGridParams,
  isFuZhuXiang,
  registerEvents,
  codeList,
) {
  return {
    hasFuZhuXiang,
    setCodeInfo(info) {
      gridData.modelValue.value.info = info;
      gridData.modelValue.info = info;
      gridData.modelValue.value.code = info.ccode;
      gridData.modelValue.value.name = info.ccodeName;

      pingzhengGridTextFuns.value.setKuaiJiKeMuInfoText(
        gridData.modelValue.value.code + ' ' + gridData.modelValue.value.name,
      );
      registerEvents.change(info);
    },
    setCodeInfo2(info) {
      gridData.modelValue.value.info = info;
      gridData.modelValue.info = info;
      gridData.modelValue.value.code = info.ccode;
      gridData.modelValue.value.name = info.ccodeName;

      pingzhengGridTextFuns.value.setKuaiJiKeMuInfoText(
        gridData.modelValue.value.code + ' ' + gridData.modelValue.value.name,
      );
    },
    clearInfo() {
      gridData.modelValue.columnDatas['kuaiJiKeMu'].text = '';
    },
    codeNotEmpty() {
      return (
        gridData.modelValue.value.code !== '' &&
        gridData.modelValue.value.code != null &&
        gridData.modelValue.value.code != ''
      );
    },
    getCodeInfo(ccode) {
      return codeList.value.filter((item) => item.target.ccode == ccode)[0].target;
    },
  };
}
