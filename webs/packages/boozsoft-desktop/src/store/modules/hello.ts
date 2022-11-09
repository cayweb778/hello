import {defineStore} from 'pinia';
import {store} from '/@/store';

export const useDesktopStore = defineStore({
    id: 'desktop',
    state: (): any => ({

    }),
    getters: {

    },
    actions: {

    }
});

// Need to be used outside the setup
export function useDesktopStoreWidthOut() {
    return useDesktopStore(store);
}

