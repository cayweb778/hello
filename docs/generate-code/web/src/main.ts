import { createApp } from 'vue'
import './tailwind.css'
import App from './App.vue'
import { routes } from './routes'
import { createRouter, createWebHistory } from 'vue-router'
import('ant-design-vue/dist/antd.less');

import moment from 'moment';
import 'moment/dist/locale/zh-cn';
moment.locale('zh-cn');
const app = createApp(App)

const router = createRouter({
  history: createWebHistory(),
  routes,
})

app.use(router)
app.mount('#app')
