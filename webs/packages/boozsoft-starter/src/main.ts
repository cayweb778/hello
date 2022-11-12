import { createApp } from 'vue'


import 'ant-design-vue/dist/antd.less';
import App from './App.vue'
import router from './router'

import './assets/main.css'
import WujieVue from "wujie-vue3";
import {setupRouterGuard} from "@/router/guard";
import {setupStore} from "./store";

const app = createApp(App)

window.WujieVue=WujieVue
app.use(router)
app.use(WujieVue)
// router-guard
setupStore(app)
setupRouterGuard();
app.mount('#app')
