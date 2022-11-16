<template>
  <div>

    <Modal v-model:visible="visible" title="打印" @ok="handleOk">
      <Row>
        <Col>请选择打印机</Col>
        <Col></Col>
      </Row>
      <Row>
        <Col> 预览</Col>
        <Col></Col>
      </Row>

    </Modal>


  </div>
</template>
<script setup>
import {ref} from 'vue'
import {Row, Col, Modal} from 'ant-design-vue'
import Index2 from './index2.vue'

const visible = ref(true)

function handleOk() {

}
</script>
