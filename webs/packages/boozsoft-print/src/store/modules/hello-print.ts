import {defineStore} from 'pinia';
import {store} from '/@/store';


function strToJson(str) {
  var json = (new Function("return " + str.replaceAll("Printer", '')))();
  return json;
}

export const useHelloPrintStore = defineStore({
  id: 'useHelloPrintStore',
  state: (): any => ({
    showPrint: false,
    showExcel: false,
    showExcelPreview: false,
    showExcelModal: false,
    showModals: true,
    showPreview: false,
  }),
  getters: {
    getShowModals: (state) => state.showModals,
    getShowPrint: (state) => state.showPrint,
    getShowExcel: (state) => state.showExcel,
    getShowPreview: (state) => state.showPreview,
    getShowExcelPreview: (state) => state.showExcelPreview,
    getShowExcelModal: (state) => state.showExcelModal
  },
  actions: {
    setShowPrint(e) {
      this.showPrint = e
    },
    setShowExcelPreview(e) {
      this.showExcelPreview = e
    },
    setShowExcelModal(e) {
      this.showExcelModal = e
    },
    setShowExcel(e) {
      this.showExcel = e
    },
    setShowModals(e) {
      this.showModals = e
    },
    setShowPreview(e) {
      this.showPreview = e
    },
    openPreview() {
      this.setShowModals(false)
      this.setShowPreview(true)
    },
    openExcelPreview() {
      this.setShowExcelModal(false)
      this.setShowExcelPreview(true)

    }
  }
});

// Need to be used outside the setup
export function useHelloPrintStoreWidthOut() {
  return useHelloPrintStore(store);
}

