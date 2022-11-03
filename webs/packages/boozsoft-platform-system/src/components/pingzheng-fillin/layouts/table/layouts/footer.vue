<template>
  <Row class="writeBy">
    <Col :span="info.span" v-for="info in props.infos">
      <span><span v-if="info.must">*</span>{{ info.label }}</span
      >:
      <Input v-model:value="info.value" />
    </Col>
  </Row>
</template>
<script setup>
  const props = defineProps(['infos']);
  import { Input, Row, Col } from 'ant-design-vue';
</script>
