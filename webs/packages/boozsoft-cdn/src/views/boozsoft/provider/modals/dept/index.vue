<template>
  <div>
    <SelectPsn @register="registerSelectPsnPage" @save="modalData"/>
  </div>
</template>
<script setup>
import SelectPsn from "/@/views/boozsoft/system/department/popup/select-psn.vue";

const [registerSelectPsnPage, {openModal: openSelectPsnPage}] = useModal()


</script>
<style></style>