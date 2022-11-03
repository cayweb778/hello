<template>
  <div>
    <ModalPop @register="registerSelectPsnPage" @throwData="modalData" @cancel="handelCancel"/>
  </div>
</template>
<script setup>
import { onMounted } from 'vue'
import ModalPop from '/@/views/boozsoft/system/supplier/popup/modalPop.vue'
import {useModal} from '/@/components/Modal'

const [registerSelectPsnPage, {openModal: openSelectPsnPage}] = useModal()


const props=defineProps(['params','result'])
const emit=defineEmits(['ok','cancel'])
function modalData(e){
    emit('ok')
  props.result(e)
}

// props.instance.aaa={
//   openSelectPsnPage,
//   dddd:getCurrentInstance()
// }

onMounted(()=>{
  console.log(props.params)
  console.log(props.result)
  let obj={
    database:props.params.accountMode,
    accId:props.params.accId,
  }
  openSelectPsnPage(true , obj)
})



function handelCancel(){
  emit('cancel')
}


</script>
<style></style>
