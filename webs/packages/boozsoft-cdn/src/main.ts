import { createApp } from 'vue'

import App from './App.vue'
import router from './router'

import './assets/main.css'
import WujieVue from "wujie-vue3";
import {setupRouterGuard} from "@/router/guard";

const app = createApp(App)

app.use(router)
app.use(WujieVue)
// router-guard
setupRouterGuard();
app.mount('#app')
