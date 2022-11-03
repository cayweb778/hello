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
    ></WujieVue>
  </div>
</template>
<script setup lang="ts">
import {ref, getCurrentInstance, inject} from 'vue'
import router from "../../router";
import {isProdMode} from "@/utils/env";

const wujieVueRef = ref()

const NcProvider = inject('NcProvider')

const wujieAttrs = ref({

  url: isProdMode() ? '//192.168.199.111:81/nc' : '//localhost:3500',
  props: {
    NcProvider,
    aa: 1,
    goSystem: (attrs: any) => {
      router.push('/system')
    }
  }
})
</script>
