import { useCurrentPageDatasourcePickerState } from '/@/layouts/page/ProvideDataFuns';
import { findKuaiJiKeMu } from '/@/api/codekemu/codekemu';
import {usePageDataStoreWidthOut} from "/@/store/modules/pageDataStore";

export function useApiRepository() {
  const repository = {
    data: {},
  };
  async function findKuaiJiKeMuAll(params) {
    const usePageRouterApi = useCurrentPageDatasourcePickerState().usePageRouterApi;
    const pageRouterApiFindKuaiJiKeMu = usePageRouterApi(findKuaiJiKeMu);
    const datasourcePicker=usePageDataStoreWidthOut().getDatasourcePicker
    if (repository.data.kuaiJiKeMu == null) {
      const arr = await pageRouterApiFindKuaiJiKeMu({
        iyear: datasourcePicker.params.year,
        bend: '1',
      });
      repository.data.kuaiJiKeMu = arr;
    }
    return repository.data.kuaiJiKeMu.map((it) => ({
      key: it.ccode,
      label: it.ccodeName,
      label2: it.ccode,
      enable: false,
      target: it,
    }));
  }
  return {
    findKuaiJiKeMuAll,
  };
}
