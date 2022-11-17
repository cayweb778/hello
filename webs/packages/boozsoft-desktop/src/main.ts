import {createApp} from "vue";
import "./style.css";
import App from "./App.vue";
import router from "./router";
import {store} from "./store";
import {createDesktopGuard} from "./router/guard/desktopGuard";
import {setupConfigKeydownListener} from "./funs";
import 'ant-design-vue/dist/antd.less';
const app = createApp(App)
app.use(router)
// console.log(998898)
createDesktopGuard(router)
app.use(store)
setupConfigKeydownListener()
app.mount("#app");
