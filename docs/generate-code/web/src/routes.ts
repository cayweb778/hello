import Record from './views/record/index.vue'

/** @type {import('vue-router').RouterOptions['routes']} */
export const routes = [
  { path: '/', redirect:'/record' },
  {
    path: '/record',
    meta: { title: 'record' },
    component: Record,
    // example of route level code-splitting
    // this generates a separate chunk (About.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    // component: () => import('./views/About.vue')
  },
]
