<template>
  <Layout :wujieAttrs="wujieAttrs">
    <WujieVue
        ref="wujieVueRef"
        width="100%"
        height="100%"
        name="home"
        :url="wujieAttrs.url"
        :sync="true"
        :props="wujieAttrs.props"
        :plugins="wujieAttrs.plugins"
    ></WujieVue>
  </Layout>
</template>
<script setup lang="ts">
import {ref, getCurrentInstance} from 'vue'
import Layout from '../components/Layout.vue'

import router from "../../../router";
import {isProdMode} from "/@/utils/env";
import {modules} from "../../../../pages/menuData";
import {useCounterStoreWidthOut} from "/@/store/modules/counter";

const wujieVueRef = ref()
const wujieAttrs = ref({

  globalData:useCounterStoreWidthOut(),
  url: isProdMode() ? '//'+new URL(window.location.href).host+'/nchome' : '//81.70.47.206:81/nchome',
  props: {
    menuModules: modules,
    aa: 1,
    goSystem: (attrs: any) => {
      // useCounterStoreWidthOut().setShowFooter(true)
      router.push('/home')
    }
  },
  plugins:[
    {
      // 将url为aaa.js的脚本中的aaa替换成bbb
      jsLoader: (code, url) => {


        if (url!=null && url.indexOf("app.config") ) return code.replace("var __PRODUCTION__BOOZSOFTNC__CONF__", "window.__PRODUCTION__BOOZSOFTNC__CONF__");
      },
    },

  ]
})
</script>
