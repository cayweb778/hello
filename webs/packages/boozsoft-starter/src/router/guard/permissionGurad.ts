import type { Router } from 'vue-router';
import {nextTick} from "vue";




const whitePathList = ['login'];

export function createPermissionGuard(router: Router) {
    // const userStore = useUserStoreWidthOut();
    // const permissionStore = usePermissionStoreWidthOut();

    router.beforeEach(async (to, from, next) => {
        if(to.fullPath!=='/login' && window.localStorage.getItem('loginDate')==null){

            next('/login');
            return;
        }

        // Whitelist can be directly entered
        if (whitePathList.includes(to.path )) {
            next();
            return;
        }


        if(window.localStorage.getItem('loginDate')!=null && to.fullPath=='/' && router.currentRoute.value.path=='/'){
            nextTick(()=>next('/home'))
            return
            // userStore.setToken(await useSsoStoreWidthOut().goGetToken());
            // await userStore.loadUserState()
        }

        // if (permissionStore.getIsDynamicAddedRoute) {
        //     next();
        //     return;

        // const routes = await permissionStore.buildRoutesAction();
        //
        // routes.forEach((route) => {
        //   router.addRoute((route as unknown) as RouteRecordRaw);
        // });
            next()
    });
}
