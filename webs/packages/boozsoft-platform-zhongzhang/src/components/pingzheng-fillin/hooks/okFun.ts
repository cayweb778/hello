import {message} from 'ant-design-vue';
import {
  goError,
  useModelToApi,
  usePingzhengReload,
  usePingzhengState,
  useSaveFuns,
} from './utils/okFunUtils';

export function usePingZhengResult(tableData) {
  const {toApiData} = useModelToApi(tableData);

  const {getTypeLabelEnum, ADD, MODIFY, INSERT} = usePingzhengState(tableData);
  const {
    savePingZhengFun,
    insertPingZhengFun,
    saveZanCunPingZhengFun,
    saveTempPingzhengFun,
    deletePingzheng,
  } = useSaveFuns();
  const {openNewPingzheng, reloadShowPingzheng} = usePingzhengReload(tableData);

  return {
    // 获取API数据
    async getApiData() {
      const apiData = await toApiData({isTemp: true});
      console.log(apiData);
    },
    // 保存
    async okFun(e) {
      const apiData = (await toApiData().catch((errowRows) => goError(e, errowRows))).map(it => {
        it.revision = "0"
        return it
      });
      switch (getTypeLabelEnum()) {
        case ADD:
          await savePingZhengFun(apiData);
          openNewPingzheng();
          break;
        case MODIFY:
          await savePingZhengFun(apiData);
          break;
        case INSERT:
          await insertPingZhengFun(apiData);
          break;
        default:
          message.error('保存出错');
      }

      e && e();
    },
    // 保存并显示
    async okAndShowFun(e) {
      const apiData = await toApiData().catch(() => message.error(e));

      switch (getTypeLabelEnum()) {
        case ADD:
          await savePingZhengFun(apiData);
          await reloadShowPingzheng();
          break;
        case MODIFY:
          await savePingZhengFun(apiData);
          break;
        case INSERT:
          // await insertPingZhengFun(apiData);
          break;
        default:
          message.error('保存出错');
      }
      e();
    },

    // 临时保存
    async saveTempData(usePageRouterApi) {
      const apiData = await toApiData({isTemp: true});

      apiData.length != 0 && (await saveTempPingzhengFun(usePageRouterApi, apiData));

      // const a=message.info('已记录!');
      // console.log(a())
      return apiData;
    },
    // 暂存
    async saveTempDanJu() {
      const apiData = await toApiData({isZanCun: true});

      await saveZanCunPingZhengFun(apiData);
      openNewPingzheng();
      // await reloadShowPingzheng();
    },
    // 删除凭证
    async deletePingZhengByUniqueCode(id) {
      await deletePingzheng(id);
    },
  };
}
