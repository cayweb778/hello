import {useGlobalStoreWidthOut} from "../store/modules/global";
import WujieVue from "wujie-vue3";
const { bus } = WujieVue;



function ncMdodals(){
    bus.$on("registerNcModals",function (e){
        useGlobalStoreWidthOut().setNcModals(e)
    })
}

export function setupEventbus(){
    ncMdodals()
}