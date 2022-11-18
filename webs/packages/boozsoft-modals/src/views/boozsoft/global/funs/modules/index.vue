<template>
  <div>

    <MessagePop @close="close" @throwData="modalData" @register="registerModalPopPage" />
  </div>
</template>
<script setup>
import MessagePop from '/@/boozsoft/layouts/LeftSider/sitebar/messagePop.vue'
import {useModal} from "/@/components/Modal";
import {onMounted} from "vue";
const [registerModalPopPage, { openModal: openMoalPopPage }] = useModal();
// 弹选回调
const modalData = async (data) => {
  emit('ok')
  console.log(data)
}
onMounted(()=>{

})

function open(){
openMoalPopPage(true, {
    database: ''
  });
}

function close(){
  emit('cancel')
  // window.$wujie.bus.$emit('closePluginShadow')
}

const emit=defineEmits(['cancel'])


defineExpose({open})
</script>
