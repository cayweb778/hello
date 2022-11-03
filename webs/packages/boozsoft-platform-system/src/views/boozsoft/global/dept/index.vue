<template>
  <div>
    <DeptModalPop @register="registerSelectPsnPage" @save="modalData" @cancel="handelCancel"/>
  </div>
</template>
<script setup>
import { onMounted} from 'vue'
import DeptModalPop from "/@/views/boozsoft/system/department/popup/select-dept.vue";
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
  openSelectPsnPage(true , props.params)
})

function handelCancel(){
  emit('cancel')
}


</script>
<style></style>
