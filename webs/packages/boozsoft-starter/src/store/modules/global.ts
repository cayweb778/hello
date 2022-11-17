import {defineStore} from 'pinia'
import {store} from '/@/store';
import {modules} from "../../../pages/menuData";
import {computed} from "vue";


export const useGlobalStore = defineStore({
    id: 'globalStore',
    state: () => ({
        pluginShadow:false,
        currentPluginName:'',
        ncModals: '',
        ncPrintFn:'',
        loginDate:'',
        ncDataExport:'',
        useExcelFn:''
    }),
    getters: {
        getCurrentPluginName:(state)=>state.currentPluginName,
        getPluginShadow:(state)=>state.pluginShadow,
        getLoginDate:(state)=>state.loginDate,
        getNcModals: (state) => state.ncModals,
        getNcDataExport: (state) => state.ncPrintFn,
        getUseExcelFn: (state) => state.useExcelFn,
    },
    actions: {
        setUseExcelFn(fn){
            this.useExcelFn=fn
        },
        setCurrentPluginName(e){
            this.currentPluginName=e
        },
        setShowPluginShadow(e){
            this.pluginShadow=e
        },
        setLoginDate(e){
          this.loginDate=e
        },
        setNcModals(e) {
            this.ncModals = e
        },
        setNcPrintFn(e) {
            this.ncPrintFn = e
        },
        defineWujieProps(e){
            return {
                menuModules:modules,
                globalApiStore:useGlobalStoreWidthOut(),
                ...e
            }
        }

    }
})


// Need to be used outside the setup
export function useGlobalStoreWidthOut() {
    return useGlobalStore(store);
}


useGlobalStoreWidthOut().setLoginDate(window.localStorage.getItem("loginDate"))