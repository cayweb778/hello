import './public-path'
import {useUserStoreWidthOut} from '/@/store/modules/user';
import {NcLoader, goPdf, goApp} from '/@/utils/preHandle';
import {createApp} from "vue";
import App from "/@/boozsoft/App.vue";

// check api
async function runApp(){
    NcLoader()
      .thenGoPdf(async () => await goPdf())
      // sso返回信息
      // 没登陆登陆
      .thenGoApp(async () => !useUserStoreWidthOut().isSsoBack() && await goApp())
}
const aaa=new WebSocket(import.meta.env.VITE_WEBSOCKET+"/pingServer")
aaa.onopen=()=>{
  runApp()
}
import ServerError from './ServerError.vue'
aaa.onerror=()=>{
  window.closeLoading()
  const app = createApp(ServerError);
  app.mount('#system-web', true);
}
