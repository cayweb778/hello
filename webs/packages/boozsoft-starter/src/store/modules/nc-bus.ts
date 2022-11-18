import {defineStore} from 'pinia';
import {store} from '/@/store';
import {useGlobalStoreWidthOut} from "./global";


export const useNcBusStore = defineStore({
  id: 'useNcBusStore',
  state: () => ({}),
  getters: {},
  actions: {
    usePrint(params,fun) {
      let fn= useGlobalStoreWidthOut().getNcDataExport
      return fn(params,fun)
    },
    useExcel() {
      let fn=useGlobalStoreWidthOut().getUseExcelFn
      return fn()
    },
    useModals() {
      let fn=useGlobalStoreWidthOut().getNcModals
      return fn()
    }
  },
});

// Need to be used outside the setup
export function useNcBusStoreWidthOut() {
  return useNcBusStore(store);
}
