import {createRouter,createWebHashHistory} from "vue-router";

const basicRoutes=[{
    path: '/login',
    name: 'Login',
    component: () => import('../D.vue'),
}]
export const router = createRouter({
    history:createWebHashHistory(),
    routes: basicRoutes,
    strict: true,
    scrollBehavior: () => ({ left: 0, top: 0 }),
});
