import {defineStore} from 'pinia';
import {store} from '/@/store';


function strToJson(str) {
  var json = (new Function("return " + str.replaceAll("Printer", '')))();
  return json;
}

export const useHelloPrintStore = defineStore({
  id: 'useHelloPrintStore',
  state: (): any => ({
    showPrint:false,
    showModals:true,
    showPreview:false,
  }),
  getters: {
    getShowModals: (state) => state.showModals,
    getShowPrint: (state) => state.showPrint,
    getShowPreview: (state) => state.showPreview
  },
  actions: {
    setShowPrint(e){
      this.showPrint=e
    },
    setShowModals(e){
      this.showModals=e
    },
    setShowPreview(e){
      this.showPreview=e
    },
    openPreview(){
      this.setShowModals(false)
      this.setShowPreview(true)
    }
  }
});

// Need to be used outside the setup
export function useHelloPrintStoreWidthOut() {
  return useHelloPrintStore(store);
}

