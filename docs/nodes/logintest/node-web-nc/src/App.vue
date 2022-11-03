<template>
  <div>
    <BasicModal></BasicModal>
    <Tree2Modal></Tree2Modal>
    <Button>hello</Button>
  </div>
</template>

<script lang="ts" setup>
// custom modal components
import {Tree2Modal,BasicModal} from './hooks/Modal'
// ant-d-vue components
import {Button} from 'ant-design-vue'
</script>
