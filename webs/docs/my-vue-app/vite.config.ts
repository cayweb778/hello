import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
function abcModifyVars(){
return {
  "abc":"red"
}
}
// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  css:{
    preprocessorOptions:{
      abc:{
        modifyVars:abcModifyVars()
      }
    }
  }
})
