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
import {isProdMode} from "@/utils/env";
import {modules} from "../../../../pages/menuData";

const wujieVueRef = ref()
const NcProvider = inject('NcProvider')

const wujieAttrs = ref({
  url: isProdMode() ? '//'+new URL(window.location.href).host+'/ncbak' : '//localhost:3103',
  socketAddr:isProdMode()? "ws://"+new URL(window.location.href).host+'/api/nc'+'/newsystem':"ws://localhost:8087",
  props: {
    menuModules: modules,
    NcProvider,
    aa: 1,
    goSystem: (attrs: any) => {
      router.push('/system')
    }
  }
})
</script>
