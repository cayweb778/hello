
import '/@/design/index.less';
import 'virtual:windi-base.css';
import 'virtual:windi-components.css';
import 'virtual:windi-utilities.css';
import {createApp} from 'vue';
import App from './boozsoft/App.vue';
import {initAppConfigStore} from '/@/logics/initAppConfig';
import {setupStore} from '/@/store';
import {setupErrorHandle} from '/@/logics/error-handle';
import {setupGlobDirectives} from '/@/directives';
import {registerGlobComp} from '/@/components/registerGlobComp';

import pinyin from 'js-pinyin'

window.pinyin = pinyin
// Register icon Sprite
import 'virtual:svg-icons-register';


import 'ant-design-vue/dist/antd.less';



(async () => {
  const app = createApp(App);

  setupStore(app);

  initAppConfigStore();

  registerGlobComp(app);

  setupGlobDirectives(app);

  setupErrorHandle(app);


  app.mount('#system-web', true);



})();


