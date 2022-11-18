<template>
  <div>
    <div v-for="componentData in componentDatas">
      <component :is="componentData.component"
                 :ref="(e)=>componentData.ref=e"
                 :params="componentData.componentParams"
                 :result="componentData.componentResult"
                 @ok="componentData.onOk"
                 @cancel="componentData.onCancel"/>
    </div>
  </div>
</template>
<script setup>
import {computed, onMounted} from 'vue'
// import {defineNcModals} from "/@/views/boozsoft/global/funs";
import abc1 from "/@/views/boozsoft/global/abc1.vue";
import {usePluginStore, usePluginStoreWidthOut} from "/@/store/modules/pluginStore";


// const componentName=shallowRef()

// const componts=ref([])
// const {cmpts,fns}=defineNcModals()
// usePluginStoreWidthOut().setup()
const componentDatas = computed(() => usePluginStoreWidthOut().getComponentDatas)


const {useNcModalData} = usePluginStoreWidthOut()
// onMounted(async () => {
//   const data = await useNcModalData(({To}) => To.SYSTEM_MESSAGE, {})
// })
// const fns = {
//   async openNotifyMessageModal(params) {
//     return await _this.execModal('dept', params)
//   },
//   async openDeptModal(params) {
//     return await _this.execModal('dept', params)
//   },
//   async openPsnModal(params) {
//     return await _this.execModal('psn', params)
//   },
//   async openCustomModal(params) {
//     return await _this.execModal('custom', params)
//   },
//   async openGysModal(params) {
//     return await _this.execModal('gys', params)
//   },
//
// }
// watch(()=>cmpts.value,function(e){
//   componts.value=Object.values(e)
// })
// // ncModals.componentData.componentRef
// function handelCancel(){
//   componentName.value=abc1
// }
// const emit=defineEmits(['ok'])
// function handleOk(){
//   componentName.value=abc1
//   emit('ok')
// }
window.$wujie.bus.$emit("registerNcModals", function(){
  return {
    useNcModalData
  }
})
</script>
