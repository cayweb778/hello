import './public-path'
import { goApp} from '/@/utils/preHandle';

// check api
async function runApp(){
  await goApp()
    // NcLoader()
    //   .thenGoPdf(async () => await goPdf())
    //   // sso返回信息
    //   // 没登陆登陆
    //   .thenGoApp(async () => !useUserStoreWidthOut().isSsoBack() && await goApp())
}
runApp()
