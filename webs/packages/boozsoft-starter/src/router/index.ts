import {createRouter, createWebHistory} from 'vue-router'

const Layout = () => import('/@/views/boozsoft/layouts/index.vue')

function createPlatformRoute({path, name, component}) {
    return {
        path: '/' + path,
        name: name,
        component: Layout,
        children: [
            {
                path: '',
                name: name + 1,
                component
            },
        ]
    }
}

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/login',
            name: 'login',
            component: () => import('../views/SubApps/Login/index.vue')
        },
        createPlatformRoute({path: '/home', name: 'home', component: () => import('../views/SubApps/Home/index.vue')}),
        createPlatformRoute({path: '/gdzc', name: 'Gdzc', component: () => import('../views/SubApps/Gdzc/index.vue')}),
        createPlatformRoute({path: '/stock', name: 'Stock', component: () => import('../views/SubApps/Stock/index.vue')}),
        createPlatformRoute({path: '/zongzhang', name: 'zongzhang', component: () => import('../views/SubApps/Zongzhang/index.vue')}),
        createPlatformRoute({path: '/system', name: 'system', component: () => import('../views/SubApps/System/index.vue')}),

    ]
})

export default router
