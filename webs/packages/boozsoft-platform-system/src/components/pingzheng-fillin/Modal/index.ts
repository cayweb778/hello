import {usePingZhengFillinStoreWidthOut} from "/@/store/modules/boozsoft/pingzheng-fillin";

export function openPingzhengModal(apiData, params) {
    const {settings} = params
    const {title} = settings
    usePingZhengFillinStoreWidthOut().getApiDataModels[0].apiData = apiData
    usePingZhengFillinStoreWidthOut().getApiDataModels[0].title = title
    usePingZhengFillinStoreWidthOut().getApiDataModels[0].settings = {enableModelSave: true}
    // type.value = 'modal'
    // openThis.value = true
}
function closePingzhengModal(apiData, params) {
    // openThis.value = false
}
