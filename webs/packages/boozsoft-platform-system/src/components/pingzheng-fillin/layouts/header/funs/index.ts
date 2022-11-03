import { useUserStoreWidthOut } from '/@/store/modules/user';
import { defaultInoidApi, queryDefaultPingZhengDate } from '/@/api/pingzheng/pingzheng';
import { formatDate } from '/@/components/pingzheng-fillin/utils/dateUtils';
import { toAccountNumLength } from '/@/components/pingzheng-fillin/utils/accountUtils';
import { useCompanyOperateStoreWidthOut } from '/@/store/modules/operate-company';
import { inject } from 'vue';

export function useVerifyLabel(pingzhengData) {
  return {
    isNewPingzhengLabel() {
      return !(
        pingzhengData.settings.onlyShow ||
        pingzhengData.settings.typeLabel == '插入' ||
        pingzhengData.settings.typeLabel == '暂存'
      );
    },

    hasEnableModelSave() {
      return pingzhengData.settings.enableModelSave;
    },
  };
}

export function usePingzhengZhiDanRen(pingzhengData) {
  return {
    isZhiDanRenEmpty() {
      return pingzhengData.options.optionZhiDanBy == null;
    },
    resetZhiDanRen() {
      pingzhengData.options.optionZhiDanBy = useUserStoreWidthOut().getUserInfo.realName;
    },
  };
}

export function resetPreData(state, pingzhengData) {
  state.pre.date = pingzhengData.options.optionDate;
  state.pre.optionInoId = pingzhengData.options.optionInoId;
}

export function useSetNewPingZhengDefaultData(usePageRouterApi, datasourcePicker) {
  return {
    async setNewPingZhengDefaultData(pingzhengData) {
      const datasourcePickerYear = datasourcePicker.value.params.year;
      const businessDate = useCompanyOperateStoreWidthOut().getLoginDate;
      const businessYear = businessDate.split('-')[0];

      const defaultPingZhengDate = await usePageRouterApi(queryDefaultPingZhengDate)();

      let aa = defaultPingZhengDate;
      if (aa.dbillDate == null) {
        // alert('凭证表最后凭证日期为null,请检查账套凭证数据')
        if (businessYear > datasourcePickerYear) {
          aa = {
            dbillDate: datasourcePickerYear + '-12-31',
            // dbillDate: formatDate('yyyy-MM-dd', new Date()),
          };
        } else {
          aa = {
            dbillDate: businessDate,
            // dbillDate: formatDate('yyyy-MM-dd', new Date()),
          };
        }
      }

      pingzhengData.options.optionDate = aa.dbillDate;
      let defaultInoid = (
        await usePageRouterApi(defaultInoidApi)(
          { yearMonth: aa.dbillDate.split('-')[0] + aa.dbillDate.split('-')[1] },
          null,
        )
      ).inoId;
      if (defaultInoid == null) {
        defaultInoid = 0;
      }

      pingzhengData.defaultInoid = pingzhengData.options.optionInoId;
      pingzhengData.options.optionInoId = toAccountNumLength(
        parseInt(defaultInoid) + 1,
        useCompanyOperateStoreWidthOut().getCurrentAccountInfo.accvouchDec,
      );
      pingzhengData.optionDate = aa.dbillDate;
    },
  };
}

export function usePageReload(reloadPage2) {
  if (window['async function reloadTable'] == null) {
    window['async function reloadTable'] = { value: 0 };
  }

  function isFirstPage() {
    return window['async function reloadTable'].value === 0;
  }

  return {
    isFirstPage,
    reloadPage() {
      window['async function reloadTable'].value++;
      reloadPage2();
    },
  };
}

export function useReloadTable(reloadPage2) {
  return {
    async reloadTable(e) {
      const { isFirstPage, reloadPage } = usePageReload(reloadPage2);
      // console.log(!isFirstPage())
      if(!isFirstPage()){
        reloadPage();
      window['async function reloadTable'].value=0
      }else{
        window['async function reloadTable'].value++
      }
    },
  };
}
