<script setup lang="ts">
import {RouterView} from 'vue-router'
import NcProvider from "./views/boozsoft/provider/index.vue"

import Abc from './Abc.vue'

</script>

<template>
  <Abc>
    <NcProvider>
      <RouterView/>
    </NcProvider>
  </Abc>
</template>