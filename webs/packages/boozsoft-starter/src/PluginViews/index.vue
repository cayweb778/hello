<template>
  <div  v-show="showPluginShadow" style="background:rgba(0,0,0,0.1);width:100vw;height:100vh;position:fixed;left:0;top:0;z-index:10000000000">
    <div v-show="getCurrentPluginName==='ncModals'"><modals></modals></div>
    <div v-show="getCurrentPluginName==='ncPrint'"><Print></Print></div>
    <Layout v-if="false">
      <div style="display:flex">
        <div>
          <LeftSider/>
        </div>
        <div>
          <Content/>
        </div>
      </div>
    </Layout>
  </div>

</template>
<script setup>
import {useGlobalStoreWidthOut} from "../store/modules/global";
import Modals from '../views/SubApps/PluginApps/Modals/index.vue'
import Print from '../views/SubApps/PluginApps/Print/index.vue'
const showPluginShadow=computed(()=>useGlobalStoreWidthOut().getPluginShadow)
const getCurrentPluginName=computed(()=>useGlobalStoreWidthOut().getCurrentPluginName)
import Layout from './layout/index.vue'
import LeftSider from './LeftSider.vue'
import Content from './content/index.vue'
import {computed} from "vue";
import {ref} from 'vue';
</script>