<script setup lang="ts">
import {  RouterView } from 'vue-router'
import Layout from "./views/boozsoft/layouts/index.vue"
import NcProvider from "./views/boozsoft/provider/index.vue"
</script>

<template>
  <div>
    <NcProvider>
      <Layout>
        <RouterView/>
      </Layout>
    </NcProvider>
  </div>
</template>

<style scoped>

</style>
