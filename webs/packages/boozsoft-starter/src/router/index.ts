import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/login',
            name: 'login',
            component: () => import('../views/Login/index.vue')
        },
        {
            path: '/home',
            name: 'home',
            component: () => import('../views/Home/index.vue')
        },
        {
            path: '/system',
            name: 'system',
            component: () => import('../views/System/index.vue')
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
