<template>
  <div style="position:fixed;left:0;top:0;">
    <WujieVue
        ref="wujieVueRef"
        width="100%"
        height="100%"
        name="gdzc"
        :url="wujieAttrs.url"
        :sync="true"
        :props="wujieAttrs.props"
    ></WujieVue>
  </div>
</template>
<script setup lang="ts">
import {ref, getCurrentInstance, inject} from 'vue'
import router from "../../../router";
import {isProdMode} from "@/utils/env";
import {useCounterStoreWidthOut} from "@/store/modules/counter";

const wujieVueRef = ref()

const NcProvider = inject('NcProvider')
const wujieAttrs = ref({
  url: isProdMode() ? '//192.168.199.111:81/ncgdzc' : '//localhost:3500',
  props: {
    NcProvider,
    aa: 1, goSystem: (attrs: any) => {
      // useCounterStoreWidthOut().setShowFooter(true)
      router.push('/system')
    }
  },
  plugins:[
    {
      // 将url为aaa.js的脚本中的aaa替换成bbb
      jsLoader: (code, url) => {

        if (url!=null && url.indexOf('jspdf.umd.min.js')!=-1) return code.replace("var jspdf", "window.jspdf");
        if (url!=null && url.indexOf( "http://192.168.199.111:81/nc/gdzc/_app.config.js")!=-1) return code.replace("var __PRODUCTION__BOOZSOFTNC__CONF__", "window.__PRODUCTION__BOOZSOFTNC__CONF__");
        // if (url === "https://unpkg.com/ant-design-vue@2.2.8/dist/antd.min.js") return code.replace("var antd", "window.antd");
        // if (url === "https://unpkg.com/vue-router@4.0.12/dist/vue-router.global.prod.js") return code.replace("var VueRouter", "window.VueRouter");
        // if (url === "https://cdn.jsdelivr.net/npm/vue-i18n@9.1.10/dist/vue-i18n.global.prod.js") return code.replace("var VueI18n", "window.VueI18n");
      },
    },

  ]
})
</script>
