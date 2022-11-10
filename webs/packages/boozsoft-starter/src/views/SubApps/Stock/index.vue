<template>
  <Layout :wujieAttrs="wujieAttrs">
    <WujieVue
      ref="wujieVueRef"
      width="100%"
      height="100%"
      name="stock"
      :url="wujieAttrs.url"
      :sync="true"
      :props="wujieAttrs.props"
    ></WujieVue>
  </Layout>
<!--    <div id="ncModalMount">-->
<!--      11-->
<!--      {{ncModals.componentData.componentRef}}-->
<!--      22-->
<!--      <component v-if="ncModals.componentData.componentRef!=null" @ok="handleOk" @cancel="handelCancel" :is="ncModals.componentData.componentRef" :params="ncModals.componentData.componentParams" :result="ncModals.componentData.componentResult"></component>-->
<!--    </div>-->
</template>
<script setup lang="ts">
import {ref, inject} from 'vue'
import Layout from '../components/Layout.vue'
// import {defineNcModals} from "@/views/boozsoft/global/funs";
import {isProdMode} from "@/utils/env";
// import abc1 from '../../../views/boozsoft/global/abc1.vue'
const wujieVueRef = ref()
import {modules} from "../../../../pages/menuData";
import {useCounterStore} from "../../../stores/counter";
// const ncModals=ref(defineNcModals())
// const NcProvider = inject('NcProvider')
const hello={
  id:1
}
setInterval(()=>{
  console.log(hello)
},1000)
const wujieAttrs = ref({

  globalData:useCounterStore(),
  url: isProdMode() ? '//'+new URL(window.location.href).host+'/ncstock' : '//localhost:3500',
  socketAddr:isProdMode()? "ws://"+new URL(window.location.href).host+'/api/nc'+'/newstock':"ws://localhost:8085/newstock",
  props: {
    hello,
    menuModules:modules,
    NcModals:{
      // async openDeptModal(params2){
      //   return await ncModals.value.openDeptModal(params2)
      // },
      // async openPsnModal(params2){
      //   return await ncModals.value.openPsnModal(params2)
      // },
      // async openCustomModal(params2){
      //   return await ncModals.value.openCustomModal(params2)
      // },
      // async openGysModal(params2){
      //   return await ncModals.value.openGysModal(params2)
      // }
    }
  },
  plugins:[
    {
      // 将url为aaa.js的脚本中的aaa替换成bbb
      jsLoader: (code, url) => {

        if (url!=null && url.indexOf('jspdf.umd.min.js')!=-1) return code.replace("var jspdf", "window.jspdf");
        if (url!=null && url.indexOf( "http://81.70.47.206:81/nc/gdzc/_app.config.js")!=-1) return code.replace("var __PRODUCTION__BOOZSOFTNC__CONF__", "window.__PRODUCTION__BOOZSOFTNC__CONF__");
        // if (url === "https://unpkg.com/ant-design-vue@2.2.8/dist/antd.min.js") return code.replace("var antd", "window.antd");
        // if (url === "https://unpkg.com/vue-router@4.0.12/dist/vue-router.global.prod.js") return code.replace("var VueRouter", "window.VueRouter");
        // if (url === "https://cdn.jsdelivr.net/npm/vue-i18n@9.1.10/dist/vue-i18n.global.prod.js") return code.replace("var VueI18n", "window.VueI18n");
      },
    },

  ]
})

function handelCancel(){
  // ncModals.value.componentData.componentRef=abc1
}
const emit=defineEmits(['ok'])
function handleOk(){
  // ncModals.value.componentData.componentRef=abc1
  // emit('ok')
}
</script>
