import '/@/design/index.less';
import 'virtual:windi-base.css';
import 'virtual:windi-components.css';
import 'virtual:windi-utilities.css';
import {createApp} from 'vue';
import App from './boozsoft/App.vue';
import {setupStore} from '/@/store';



// Register icon Sprite
import 'virtual:svg-icons-register';

import 'ant-design-vue/dist/antd.less';



(async () => {
  window.closeLoading()
  // createWindowDebuggerInfo()
  const app = createApp(App);
  // Configure store
  setupStore(app);
  // app.use(STable);
  app.mount('#app_auth', true);
})();


