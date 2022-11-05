<template>
  <div style="position:fixed;left:0;top:0;">
    <WujieVue
        ref="wujieVueRef"
        width="100%"
        height="100%"
        name="system"
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
import {modules} from "../../../../pages/menuData";

const wujieVueRef = ref()
const NcProvider = inject('NcProvider')

const wujieAttrs = ref({
  url: isProdMode() ? '//'+new URL(window.location.href).host+'/ncbak' : '//localhost:3103',
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
