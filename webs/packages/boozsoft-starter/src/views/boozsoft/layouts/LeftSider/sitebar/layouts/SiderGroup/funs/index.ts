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


export function goZongZhang(id : any){
    router.push("/zongzhang")
    bus.$emit("goZongzhang",id);
}


export function goStock(id : any){
    router.push("/stock")
    bus.$emit("goStock",id);
}


export function goGuDingZiChan(id : any){
    router.push("/gdzc")
    bus.$emit("goGuDingZiChan",id);
}

export function goSystem(id : any){
    router.push("/system")
    bus.$emit("goPlatform",id);
}