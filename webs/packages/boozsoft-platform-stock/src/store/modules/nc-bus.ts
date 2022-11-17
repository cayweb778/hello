import {defineStore} from 'pinia';
import {store} from '/@/store';


export const useNcBusStore = defineStore({
  id: 'useNcBusStore',
  state: () => ({}),
  getters: {},
  actions: {
    usePrint(params,fun) {
      return window.$wujie.props.globalApiStore.getNcDataExport(params,fun)
    },
    useExcel() {
      return window.$wujie.props.globalApiStore.getUseExcelFn()
    },
    useModals() {
      return window.$wujie.props.globalApiStore.getNcModals()
    }
  },
});

// Need to be used outside the setup
export function useNcBusStoreWidthOut() {
  return useNcBusStore(store);
}
