import { createApp } from 'vue'


import 'ant-design-vue/dist/antd.less';
import App from './App.vue'
import router from './router'

import './assets/main.css'
import WujieVue from "wujie-vue3";
import {setupRouterGuard} from "@/router/guard";
import {createPinia} from "pinia";

const app = createApp(App)

app.use(router)
app.use(WujieVue)
app.use(createPinia())
// router-guard
setupRouterGuard();
app.mount('#app')
