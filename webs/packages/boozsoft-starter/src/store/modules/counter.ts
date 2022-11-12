import {defineStore} from 'pinia'
import {usePluginsStore} from "./plugins";
import {store} from '@/store';
let footerShow = false
const cache = window.localStorage.getItem("showFooter")
if (cache == null) {
    footerShow = false
} else {
    if (cache == "1") {
        footerShow = false
    } else {
        footerShow = true
    }
}

export const useCounterStore = defineStore({
    id: 'counter',
    state: () => ({
        platformId: '',
        counter: 0,
        footerShow
    }),
    getters: {
        getPlatformId: (state) => state.platformId,
        doubleCount: (state) => state.counter * 2,
        showFooterShow: (state) => state.footerShow
    },
    actions: {
        setPlatformId(id) {
            this.platformId = id
        },
        increment() {
            this.counter++
        },
        setShowFooter(bol) {
            if (bol) {
                window.localStorage.setItem("showFooter", "0")
            } else {
                window.localStorage.setItem("showFooter", "1")
            }
            this.footerShow = bol
        }
    }
})
// Need to be used outside the setup
export function useCounterStoreWidthOut() {
    return useCounterStore(store);
}


