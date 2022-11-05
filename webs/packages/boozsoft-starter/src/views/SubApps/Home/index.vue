<template>
  <div style="position:fixed;left:0;top:0;">
    <WujieVue
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
import {ref, getCurrentInstance} from 'vue'
import router from "../../../router";
import {isProdMode} from "@/utils/env";
import {modules} from "../../../../pages/menuData";
import {useCounterStore} from "@/stores/counter";

const wujieVueRef = ref()
const wujieAttrs = ref({
  url: isProdMode() ? '//'+new URL(window.location.href).host+'/nchome' : '//localhost:3800',
  props: {
    menuModules: modules,
    aa: 1,
    goSystem: (attrs: any) => {
      useCounterStore().setShowFooter(true)
      router.push('/home')
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
</script>
