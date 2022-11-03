import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'


function configHmrPlugin() {
  return {
    name: 'singleHMR',
    configResolved(resolvedConfig) {
      console.log(import.meta.env)
      // import.meta.env.asdsa='sadasdsa'
      // import.meta.asa='asdsadsa'
      // 存储最终解析的配置
      debugger
    },
  };
}
// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue(),configHmrPlugin()],
  server:{
    proxy:{
      '^/api':{
        target:'http://localhost:3300',
        changeOrigin:true,
        rewrite:(path)=>path.replace(/^\/api/,'')
      }
    }
  }
})
