import { createApp } from 'vue'
import App from './App.vue'

const app=createApp(App)
// app.use((ctx,b)=>{
//     debugger
//     console.log(ctx.path)
//     console.log(b)
// })
    app.mount('#app')
