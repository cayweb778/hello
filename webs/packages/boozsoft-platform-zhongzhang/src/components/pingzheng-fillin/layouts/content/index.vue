<template>
  <div class="view-content"
       style="background:#f1f1f1;position:relative;height:calc(100% - 88px);">
    <div class="main-view" style="background:#b4c8e3">
      <!--    <PingzhengModal @register="registerPingZhengModal"></PingzhengModal>-->
      <div class="main-view-loading" style="border-radius:10px" v-if="false">
        <Spin :spinning="true"></Spin>
      </div>
      <template v-if="visible">
        <slot></slot>
      </template>
    </div>
  </div>
</template>

<script lang="ts" setup>

import {Spin} from 'ant-design-vue'
import {inject, onMounted, provide, ref} from "vue";




const openPingzhengHeader = inject('openPingzhengHeader')

const visible = ref(false)
onMounted(async () => {
  setTimeout(() => {
    visible.value = true
  }, 100)
})

</script>
