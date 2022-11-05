<script setup lang="ts">
import {  RouterView } from 'vue-router'
import Layout from "./views/boozsoft/layouts/index.vue"
import NcProvider from "./views/boozsoft/provider/index.vue"
import {useCounterStore} from "@/stores/counter";
import {computed} from "vue";
const showHome=computed(()=>useCounterStore().setShowFooter)
</script>

<template>
  <div>
    <NcProvider >
      <Layout>
        <RouterView/>
      </Layout>
    </NcProvider>
  </div>
</template>

<style scoped>

</style>
