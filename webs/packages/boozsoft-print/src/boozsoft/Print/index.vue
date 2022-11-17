<template>
  <div class="hello_print"
       style="position:fixed;left:0;top:0;width:100vw;height:100vh;background:rgba(0,0,0,0)">

    <Modal
      v-if="!showPdfPrint" v-model:visible="visible" width="440px" title="打印" @ok="handleOk"
      @cancel="handleCancel"
      :footer="null">
      <Row style="margin-bottom:20px">
        <Col :span="12">标题</Col>
        <Col :span="12">纸科目总账</Col>
      </Row>
      <Row style="margin-bottom:20px">
        <Col :span="12">页数：50页</Col>
        <Col :span="12">纸张：A4</Col>
      </Row>
      <Row>
      <Col>请选择打印机 <Select :options="prints" :value="'Mi All in One Inkjet'"></Select>
        <Button @click="startShowPdfPrint">预览</Button>
        <Button>打印</Button>
      </Col>
      <Col></Col>
    </Row>


    </Modal>
    <Index2 v-if="showPdfPrint"></Index2>


  </div>
</template>
<script setup>
import {ref, computed} from 'vue'
import {Row, Col, Modal, Select, Button} from 'ant-design-vue'
import Index2 from './index2.vue'
import {useNewPrintStoreWidthOut} from "/@/store/modules/new-print";
import {savePdf, useNewPrint, useNewPrintLang} from "/@/utils/boozsoft/print/print";
import {tableStyle} from "/@/utils/boozsoft/print/abc-print";
const showPdfPrint = ref(false)

function startShowPdfPrint() {
  showPdfPrint.value = true
}

const visible = ref(true)
useNewPrintStoreWidthOut().initPrint()
const prints = computed(() => useNewPrintStoreWidthOut().getPrinters)

function handleOk() {
  window.$wujie.bus.$emit("closePluginShadow")
}

function handleCancel() {
  window.$wujie.bus.$emit("closePluginShadow")
}



import {
  defaultV,
  sheet_from_array_of_arrays,
  Workbook,
  writeExcel
} from "/@/utils/boozsoft/excel/excel2";
import XLSX from "xlsx-js-style";
window.$wujie.bus.$emit("registerNcDataExport", {
  usePrint(params,fun){
   const aaaa= useNewPrintLang(params,fun).getBase64()
    useNewPrintStoreWidthOut().setPrintBase64(aaaa)
    window.$wujie.bus.$emit('setCurrentPluginName', 'ncPrint')
    showPdfPrint.value=false
    visible.value = true
  },
  tableStyle,
  useNewPrintLang,
  useExcel(){
    return {
      XLSX,
      defaultV,
      sheet_from_array_of_arrays,
      Workbook,
      writeExcel,
    }
  }
})
</script>
<style>
body {
  background: rgba(0, 0, 0, 0) !important;
}
</style>
