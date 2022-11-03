import WujieVue from "wujie-vue3";
const {bus}=WujieVue

export function goPlatformById(id : any){
    bus.$emit("goPlatform",id);
}
export function goGuDingZiChan(id : any){
    bus.$emit("goGuDingZiChan",id);
}
