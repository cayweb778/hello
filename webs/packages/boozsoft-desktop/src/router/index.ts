import type { RouteRecordRaw } from 'vue-router';
import type { App } from 'vue';

import { createRouter, createWebHistory } from 'vue-router';

// app router
const router = createRouter({
    history: createWebHistory(import.meta.env.VITE_PUBLIC_PATH),
    routes: [
        {
            path: '/firstPage',
            name: 'firstPage',
            component: () => import('/@/views/First/index.vue'),
            meta: {
                title: '配置服务地址',
            },
        },
        {
            path: '/config',
            name: 'config',
            component: () => import('/@/views/sys/Config/index.vue'),
            meta: {
                title: '配置参数',
            },
        },
        {
            path: '/app',
            name: 'app',
            component: () => import('/@/views/sys/App/index.vue'),
            meta: {
                title: '应用',
            },
        }
    ],
    strict: true,
    scrollBehavior: () => ({ left: 0, top: 0 }),
});


export default router;
