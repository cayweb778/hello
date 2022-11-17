<template>
  <div style="height:100%">
    <div style="display: flex;justify-content:space-between;position:fixed;left:0;top:0;width:100%;height:100%">
     <PluginView/>
      <LeftSider/>
      <AppView>
        <RouterView/>
        <Footer></Footer>
      </AppView>
    </div>
  </div>
</template>
<script setup lang="ts">
import PluginView from '/@/PluginViews/index.vue'
import LeftSider from '/@/views/boozsoft/layouts/LeftSider/index.vue'

import AppView from '/@/views/boozsoft/layouts/AppView.vue'
import Footer from '/@/views/boozsoft/layouts/Footer.vue'
</script>
