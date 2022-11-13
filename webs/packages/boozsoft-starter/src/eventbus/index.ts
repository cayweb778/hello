import {useGlobalStoreWidthOut} from "../store/modules/global";
import WujieVue from "wujie-vue3";
const { bus } = WujieVue;

function loginDate(){
    bus.$on("setLoginData",function (e){
        useGlobalStoreWidthOut().setLoginDate(e)
    })
}
function ncPrint(){
    bus.$on("registerNcPrint",function (e){
        function useNcPrint(){
            useGlobalStoreWidthOut().setCurrentPluginName("ncPrint")
            return e
        }
        useGlobalStoreWidthOut().setNcPrintFn(useNcPrint)
    })
}

function ncMdodals(){
    bus.$on("registerNcModals",function (e){
        function useNcModal(){
            useGlobalStoreWidthOut().setCurrentPluginName("ncModals")
            return e
        }
        useGlobalStoreWidthOut().setNcModals(useNcModal)
    })
}
function setShowPluginShadow(){
    bus.$on('setShowPluginShadow',function(e){
        useGlobalStoreWidthOut().setShowPluginShadow(e)
    })
}

export function setupEventbus(){


    ncMdodals()
    ncPrint()
    loginDate()
    setShowPluginShadow()
}