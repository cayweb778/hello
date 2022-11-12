import WujieVue from "wujie-vue3";
import router from "@/router";
import {useCounterStoreWidthOut} from "@/store/modules/counter";
const {bus}=WujieVue

export function goHome(id : any){

    window.localStorage.setItem("newPlatformId","20006")
    useCounterStoreWidthOut().setShowFooter(true)
    router.push("/home")
    bus.$emit("goPlatform",20006);
}

export function goPlatformById(id : any){

    window.localStorage.setItem("newPlatformId",id)
    bus.$emit("goPlatform",id);
}


export function goZongZhang(id : any){

    window.localStorage.setItem("newPlatformId",id)
    router.push("/zongzhang")
    bus.$emit("goZongzhang",id);
}


export function goStock(id : any){

    window.localStorage.setItem("newPlatformId",id)
    router.push("/stock")
    bus.$emit("goStock",id);
}


export function goGuDingZiChan(id : any){

    window.localStorage.setItem("newPlatformId",id)
    router.push("/gdzc")
    bus.$emit("goGuDingZiChan",id);
}

export function goSystem(id : any){

    window.localStorage.setItem("newPlatformId",id)
    router.push("/system")
    bus.$emit("goPlatform",id);
}