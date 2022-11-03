import WujieVue from "wujie-vue3";
import router from "@/router";
import {useCounterStore} from "@/stores/counter";
const {bus}=WujieVue

export function goHome(id : any){
    useCounterStore().setShowFooter(true)
    bus.$emit("goPlatform",20006);
    router.push("/home")
}

export function goPlatformById(id : any){
    bus.$emit("goPlatform",id);
}

export function goGuDingZiChan(id : any){
    bus.$emit("goGuDingZiChan",id);
}
