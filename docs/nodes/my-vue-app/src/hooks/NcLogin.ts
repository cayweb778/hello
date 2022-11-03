/* eslint-disable */
/* tslint:disable */
export function ncLoginListener(fun){
    function listenerFun({source, data, origin}) {
        if (window.parent !== window.event.source) {
            return
        }
        const result = JSON.parse(data).result;
        console.log(result)
        fun(result)
        // sendMessage(host,R.ok('ok'))
        // console.warn('调试: 得到数据', result)
        // console.warn('调试: 地址', origin)
        // console.log(event);
        // document.getElementById('message').innerHTML = "收到"
        //     + event.origin + "消息：" + event.data;
        // showPanpel.value=true
        // fun(result)
        //
        // window.clearInterval(asdsa)
        //
        // msg.value = '已接入,准备跳转'
        //
        // showBoozsoft.value=false
        // d1.value = ''
    }
    const listener=window.addEventListener('message', listenerFun, false);

    return {
        clearListener(){
            window.removeEventListener('message',listenerFun)
        }
    }

}