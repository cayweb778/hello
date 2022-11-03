<template>
  <div :class="{'main-view-content':true,'main-view-double-content':viewModel.instances.length===2}">
    <template v-for="it in props.items">
      <TableContent :modelValue="it"/>
    </template>
  </div>

</template>
<script setup>
import {inject} from "vue";

const props = defineProps(['items'])
import TableContent from '../index.vue'

const viewModel = inject('viewModel')
</script>
<style lang="less">
.main-view-double-content {
  .main-view-item {
    width: 49.5% !important;
    overflow: hidden;
    transform: translate(0px) !important;
  }
}
</style>