import {useGlobalStoreWidthOut} from "../store/modules/global";
import WujieVue from "wujie-vue3";
const { bus } = WujieVue;

function loginDate(){
    bus.$on("setLoginData",function (e){
        useGlobalStoreWidthOut().setLoginDate(e)
    })
}
function ncPrint(){
    bus.$on("registerNcDataExport",function ({usePrint,useExcel}){
        debugger
        // function useNcPrint(){
        //     useGlobalStoreWidthOut().setCurrentPluginName("ncPrint")
        //     return e
        // }
        useGlobalStoreWidthOut().setUseExcelFn(useExcel)
        useGlobalStoreWidthOut().setNcPrintFn(usePrint)
    })
}

function ncMenu(){
    bus.$on("registerNcMenu",function (e){
        // function useNcPrint(){
        //     useGlobalStoreWidthOut().setCurrentPluginName("ncPrint")
        //     return e
        // }
        // useGlobalStoreWidthOut().setNcPrintFn(e)
    })
}


function ncMdodals(){
    bus.$on("registerNcModals",function (e){
        // function useNcModal(){
        //     useGlobalStoreWidthOut().setCurrentPluginName("ncModals")
        //     return e
        // }
        useGlobalStoreWidthOut().setNcModals(e)
    })
}
function setShowPluginShadow(){
    bus.$on('closePluginShadow',function(e){
        useGlobalStoreWidthOut().setCurrentPluginName('')
    })
    bus.$on('setCurrentPluginName',function(e){
        useGlobalStoreWidthOut().setCurrentPluginName(e)
    })
}

export function setupEventbus(){


    ncMdodals()
    ncPrint()
    loginDate()
    setShowPluginShadow()
}