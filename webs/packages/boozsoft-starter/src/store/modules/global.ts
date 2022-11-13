import {defineStore} from 'pinia'
import {store} from '@/store';
import {modules} from "../../../pages/menuData";


export const useGlobalStore = defineStore({
    id: 'globalStore',
    state: () => ({
        pluginShadow:false,
        currentPluginName:'',
        ncModals: '',
        ncPrintFn:'',
        loginDate:''
    }),
    getters: {
        getCurrentPluginName:(state)=>state.currentPluginName,
        getPluginShadow:(state)=>state.pluginShadow,
        getLoginDate:(state)=>state.loginDate,
        getNcModals: (state) => state.ncModals,
    },
    actions: {
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
                ncModals:useGlobalStoreWidthOut().getNcModals,
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