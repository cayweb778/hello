<template>
  <div>
    <slot v-if="hasSlot"></slot>
    <div ref="modalMountRef"></div>
  </div>
</template>
<script setup>
  import {createApp,ref,provide,onMounted} from 'vue'

  const hasSlot=ref(false)
  const modalMountRef=ref()
  const NcModalBus = {
    async useDeptModal(){
      const dept =(await NcModalBus.dept()).default
      const instance={}
      const app = createApp(dept,{
        instance
      })
      app.mount(modalMountRef.value,true)
      debugger
      return {
        openDept:app.openDept,
        target:instance
      }
    },
    openEmp(){

    }
  }
  provide('NcProvider',NcModalBus)
  onMounted(()=>hasSlot.value = true)
</script>
<style>

</style>