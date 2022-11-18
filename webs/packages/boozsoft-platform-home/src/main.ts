console.log(window.localStorage)

function aaaa(){
  for (var i = 0; i < window.top.localStorage.length; i++) {
    var key = window.top.localStorage.key(i); //获取本地存储的Key
    console.log(window.top.localStorage.getItem(key))
    window.localStorage.setItem(key,window.top.localStorage.getItem(key));//所有value
  }
}
aaaa()
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
