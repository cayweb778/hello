import {useGlobalStoreWidthOut} from "../store/modules/global";
const { bus } = WujieVue;



function ncMdodals(){
    bus.$on("registerNcModals",function (e){
        useGlobalStoreWidthOut().setNcModals(e)
    })
}

export function setupEventbus(){
    ncMdodals()
}