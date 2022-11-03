import {defineStore} from 'pinia'

let footerShow=false
const cache = window.localStorage.getItem("showFooter")
if (cache == null) {
    footerShow=false
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
        counter: 0,
        footerShow
    }),
    getters: {
        doubleCount: (state) => state.counter * 2,
        showFooterShow: (state) => state.footerShow
    },
    actions: {
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
