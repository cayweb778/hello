<template>
  <div>

    <MessagePop @throwData="modalData" @register="registerModalPopPage" />
  </div>
</template>
<script setup>
import MessagePop from '/@/boozsoft/layouts/LeftSider/sitebar/messagePop.vue'
import {useModal} from "/@/components/Modal";
import {onMounted} from "vue";
const [registerModalPopPage, { openModal: openMoalPopPage }] = useModal();
// 弹选回调
const modalData = async (data) => {
  console.log(data)
}
onMounted(()=>{
  openMoalPopPage(true, {
    database: ''
  });
})

</script>
