import './public-path'
import {useUserStoreWidthOut} from '/@/store/modules/user';
import {NcLoader, goPdf, goApp} from '/@/utils/preHandle';

window.closeLoading=()=>{
  window.isNcSystemLoaded=true
  document.getElementById('aaa').style.display='none'

}
(async () => {
  NcLoader()
    .thenGoPdf(async () => await goPdf())
    // sso返回信息
    // 没登陆登陆
    .thenGoApp(async () => !useUserStoreWidthOut().isSsoBack() && await goApp())
})();
