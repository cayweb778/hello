<template>
  <div style="position:fixed;left:0;top:0;">
    <WujieVue v-if="showWuji"
        ref="wujieVueRef"
        width="100%"
        height="100%"
        name="auth"
        :url="wujieAttrs.url"
        :sync="true"
        :props="wujieAttrs.props"
        :plugins="wujieAttrs.plugins"
    ></WujieVue>
  </div>
</template>
<script setup lang="ts">
import {ref, getCurrentInstance, onMounted} from 'vue'
import {isProdMode} from "/@/utils/env";
import {modules} from "../../../../pages/menuData";
import router from "/@/router";
// import {useCounterStore} from "@/stores/counter";

const wujieVueRef = ref()
const wujieAttrs = ref({
  url: isProdMode() ? '//'+new URL(window.location.href).host+'/ncauth' : '//localhost:3100',
  props: {
    menuModules: modules,
    aa: 1,
    goSystem: (attrs: any) => {
      // useCounterStore().setShowFooter(true)
      router.push('/hellohome')
    }
  },
  plugins:[
    {
      // 将url为aaa.js的脚本中的aaa替换成bbb
      jsLoader: (code, url) => {
        console.log(url)

        if (url!=null && url.indexOf("app.config") ) return code.replace("var __PRODUCTION__BOOZSOFTNC__CONF__", "window.__PRODUCTION__BOOZSOFTNC__CONF__");
      },
    },

  ]
})
const showWuji=ref(false)
onMounted(()=>{
  setTimeout(()=>{
    showWuji.value=true
  },20000)
})
</script>
