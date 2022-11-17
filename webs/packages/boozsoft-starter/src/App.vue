<script setup lang="ts">
import {RouterView} from 'vue-router'
import Layout from "./views/boozsoft/layouts/index.vue"
import NcProvider from "./views/boozsoft/provider/index.vue"
import LeftSider from './views/boozsoft/layouts/LeftSider/index.vue'

import AppView from './views/boozsoft/layouts/AppView.vue'
import Abc from './Abc.vue'
import PluginView from './PluginViews/index.vue'
</script>

<template>
  <Abc>
    <NcProvider>
      <RouterView/>
<!--        <PluginView/>-->
        <!--    left sider switch    -->
<!--        <LeftSider/>-->
        <!--    platfrom view    -->
<!--        <AppView>-->
<!--          <RouterView/>-->
<!--        </AppView>-->
        <!--    footer view    -->

    </NcProvider>
  </Abc>
</template>