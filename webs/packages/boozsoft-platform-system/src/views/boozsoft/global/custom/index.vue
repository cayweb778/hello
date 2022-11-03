<template>
  <div>
    <SupperModalPop @register="registerSelectPsnPage" @throwData="modalData" @cancel="handelCancel"/>
  </div>
</template>
<script setup>
import {getCurrentInstance, onMounted} from 'vue'
import SupperModalPop from "/@/views/boozsoft/system/customer_info/popup/modalPop.vue"
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

function handelCancel(){
  emit('cancel')
}

onMounted(()=>{
  console.log(props.params)
  console.log(props.result)
  let obj={
    database:props.params.accountMode,
    accId:props.params.accId,
  }
  openSelectPsnPage(true , obj)
})

</script>
<style></style>
