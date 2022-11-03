import {createApp} from "vue";
import aa from './NcLogin.vue'

function createNcLoginPanpel(host,fun){
    const app=createApp(aa,{
        host,
        fun
    })
    const dom=document.createElement('div')
    app.mount(dom)
    document.body.appendChild(dom)
    return app
}

export function NcLogin(host,fun){
    const app=createNcLoginPanpel(host,fun)
}