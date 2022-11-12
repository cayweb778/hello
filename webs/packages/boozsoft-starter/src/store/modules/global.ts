import {defineStore} from 'pinia'
import {store} from '@/store';
import {modules} from "../../../pages/menuData";


export const useGlobalStore = defineStore({
    id: 'globalStore',
    state: () => ({
        ncModals: '',
    }),
    getters: {
        getNcModals: (state) => state.ncModals,
    },
    actions: {
        setNcModals(e) {
            this.ncModals = e
        },
        defineWujieProps(e){
            return {
                menuModules:modules,
                ncModals:useGlobalStoreWidthOut().getNcModals,
                ...e
            }
        }

    }
})
// Need to be used outside the setup
export function useGlobalStoreWidthOut() {
    return useGlobalStore(store);
}


