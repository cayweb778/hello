<template>
  <Layout :wujieAttrs="wujieAttrs">
    <WujieVue
        ref="wujieVueRef"
        width="100%"
        height="100%"
        name="system"
        :url="wujieAttrs.url"
        :sync="true"
        :props="wujieAttrs.props"
    ></WujieVue>
  </Layout>

</template>
<script setup lang="ts">
import {ref, inject} from 'vue'
import Layout from '../components/Layout.vue'
import router from "../../../router";
import {isProdMode} from "/@/utils/env";
import {modules} from "../../../../pages/menuData";
import {useCounterStoreWidthOut} from "../../../store/modules/counter";
import {useGlobalStoreWidthOut} from "../../../store/modules/global";

const wujieVueRef = ref()
const NcProvider = inject('NcProvider')

const wujieAttrs = ref({
  url: isProdMode() ? '//'+new URL(window.location.href).host+'/ncbak' : '//localhost:3103',
  socketAddr:isProdMode()? "ws://"+new URL(window.location.href).host+'/api/nc'+'/newsystem':"ws://localhost:8087",
  props: useGlobalStoreWidthOut().defineWujieProps({
    NcProvider,
    goSystem: (attrs: any) => {
      router.push('/system')
    },
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
  }),
})
</script>
