import {defineStore} from 'pinia'
import {store} from '@/store';
export const usePluginsStore = defineStore({
    id: 'counter',
    state: () => ({
        currentPluginName: '',
    }),
    getters: {
        getCurrentPluginName: (state) => state.currentPluginName,
    },
    actions: {
        setCurrentPluginName(e){
            this.currentPluginName=e
        }
    }
})
// Need to be used outside the setup
export function usePluginsStoreWidthOut() {
    return usePluginsStore(store);
}

