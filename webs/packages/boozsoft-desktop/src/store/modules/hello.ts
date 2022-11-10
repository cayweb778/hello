import {defineStore} from 'pinia';
import {store} from '/@/store';
import {getAddr} from "../../funs";
import {invoke} from "@tauri-apps/api/tauri";
import router from "../../router";




export const useDesktopStore = defineStore({
    id: 'desktop',
    state: (): any => ({
        config:'',
        url:'',
        showFirstPage:true,

    }),
    getters: {
        getUrl(state){
            return state.url
        }
    },
    actions: {
        setUrl(url){
          this.url=url
        },
        setShowFirstPage(bol){
            this.showFirstPage=bol
        },
        async goApp(path){
            // this.setUrl(path+'/')
            await invoke("generate", {name: path});
            await invoke("goApp", {name: path});
            // router.push('/app')
        }
    }
});



// Need to be used outside the setup
export function useDesktopStoreWidthOut() {
    return useDesktopStore(store);
}

