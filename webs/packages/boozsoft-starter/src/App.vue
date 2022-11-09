<script setup lang="ts">
import {  RouterView } from 'vue-router'
import Layout from "./views/boozsoft/layouts/index.vue"
import NcProvider from "./views/boozsoft/provider/index.vue"
import {useCounterStore} from "@/stores/counter";
import {computed, ref} from "vue";
const  showApp=ref(false)
console.log(window.parent)
if(self!=top){
  showApp.value=true
}
</script>

<template>
  <div>
    <NcProvider v-if="showApp" >
      <Layout>
        <RouterView/>
      </Layout>
    </NcProvider>
    <div v-else>
          NC暂不支持网页端，请使用桌面端
    </div>
  </div>
</template>

<style scoped>

</style>
