<template>
  <DefaultButtons v-if="
    tableData.settings.typeLabel==='填制' ||
    tableData.settings.typeLabel=='冲销' ||
    tableData.settings.typeLabel=='复制' ||
    tableData.settings.typeLabel=='插入'"
  ></DefaultButtons>
  <ShowButtons v-else-if="tableData.settings.typeLabel==='查看'"></ShowButtons>
  <EditButtons v-else-if="tableData.settings.typeLabel=='修改'"></EditButtons>
  <TempSaveButtons v-else-if="tableData.settings.typeLabel=='暂存'"></TempSaveButtons>
  <ShengChengButtons v-else-if="tableData.settings.typeLabel=='生成'"></ShengChengButtons>
</template>

<script setup>
import DefaultButtons from './btnTemps/defaultButton.vue'
import ShowButtons from './btnTemps/showButtons.vue'
import EditButtons from './btnTemps/editButtons.vue'
import TempSaveButtons from './btnTemps/tempSaveButtons.vue'
import ShengChengButtons from './btnTemps/shengChengButton.vue'
import {inject} from "vue";


const viewModel = inject('viewModel');
const tableData = viewModel.value.instances[0].params.pingzhengData
const pingzhengData = viewModel.value.instances[0].params.pingzhengData
</script>
