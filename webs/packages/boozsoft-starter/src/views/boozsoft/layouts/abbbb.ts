import {useGlobalStoreWidthOut} from "../../../store/modules/global";


self.addEventListener('message', function (e) {
    console.log(110111)
    console.log(window)
    // console.log(useGlobalStoreWidthOut().getNcDataExport)
    console.log(e.data)
    self.postMessage('You said: ' + e.data);
}, false);
