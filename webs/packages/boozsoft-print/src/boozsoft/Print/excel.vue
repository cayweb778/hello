<template>
  <div class="hello_print"
       style="position:fixed;left:0;top:0;width:100vw;height:100vh;background:rgba(0,0,0,0)">

    <Modal
   v-model:visible="visible" width="440px" title="财税达表格" @ok="handleOk"
      @cancel="handleCancel"
      :footer="null">
      <Row style="margin-bottom:20px">
        <Col :span="12">标题</Col>
        <Col :span="12">纸科目总账</Col>
      </Row>
      <Row style="margin-bottom:20px">
        <Col :span="12">类型：xlsx</Col>
      </Row>
      <Row>
      <Col>
        <Button @click="startShowPdfPrint">预览</Button>
        <Button>保存</Button>
        <Button>查看保存过的表格</Button>
        <Button>下载</Button>
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
import Index2 from './excelIndex2.vue'
import {useNewPrintStoreWidthOut} from "/@/store/modules/new-print";
const showPdfPrint =computed(()=>useHelloPrintStoreWidthOut().getShowExcelPreview)


const visible = computed(()=>useHelloPrintStoreWidthOut().getShowExcelModal)
useNewPrintStoreWidthOut().initPrint()

function handleOk() {
  window.$wujie.bus.$emit("closePluginShadow")
}

function handleCancel() {
  window.$wujie.bus.$emit("closePluginShadow")
}
function startShowPdfPrint() {
  useHelloPrintStoreWidthOut().openExcelPreview()
}

import {useHelloPrintStoreWidthOut} from "/@/store/modules/hello-print";

</script>
<style>
body {
  background: rgba(0, 0, 0, 0) !important;
}
</style>
