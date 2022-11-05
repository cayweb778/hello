import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/login',
            name: 'login',
            component: () => import('../views/SubApps/Login/index.vue')
        },
        {
            path: '/home',
            name: 'Home',
            component: () => import('../views/SubApps/Home/index.vue')

        },
        {
            path: '/gdzc',
            name: 'Gdzc',
            component: () => import('../views/SubApps/Gdzc/index.vue')
        },

        {
            path: '/stock',
            name: 'Stock',
            component: () => import('../views/SubApps/Stock/index.vue')
        },
        {
            path: '/zongzhang',
            name: 'Zongzhang',
            component: () => import('../views/SubApps/Zongzhang/index.vue')
        },

        {
            path: '/system',
            name: 'system',
            component: () => import('../views/SubApps/System/index.vue')
        },
        {
            path: '/about',
            name: 'about',
            // route level code-splitting
            // this generates a separate chunk (About.[hash].js) for this route
            // which is lazy-loaded when the route is visited.
            component: () => import('../views/AboutView.vue')
        },
        {
            path: '/nc/gdzc',
            name: 'about',
            // route level code-splitting
            // this generates a separate chunk (About.[hash].js) for this route
            // which is lazy-loaded when the route is visited.
            component: () => import('../views/platforms/gu-ding-zi-chan/index.vue')
        }
    ]
})

export default router
