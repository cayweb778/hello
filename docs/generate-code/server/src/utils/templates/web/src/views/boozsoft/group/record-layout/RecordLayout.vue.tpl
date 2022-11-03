<template>
  <div class="app-container">
    <div class="app-container-head">
      <img src="/img/menu/index_comprehensive_reimbursement.png" class="container-head-img">
      <div class="container-head-title"><b class="noneSpan">日志档案</b></div>
      <div class="ant-btn-group" data-v-a1ccd506="" style="float: right">
        <template v-for="it in recordData.arr">
          <button v-if="it.enable"
                  @click="it.fun"
                  class="ant-btn ant-btn-me" v-text="it.label"/>
        </template>
      </div>
    </div>
    <div class="app-container-neck">
      <div style="float: right; margin-left: 10px">
        <Button @click="recordData.reload">
          <SyncOutlined :style="{ fontSize: '14px' }"/>
        </Button>
        <Popover v-if="recordData.activeKey" placement="bottom">
          <template #content></template>
          <Button>
            <SettingFilled :style="{ fontSize: '14px' }"/>
          </Button>
        </Popover>
        <Button @click="recordData.activeKey = !recordData.activeKey">
          <PieChartFilled :style="{ fontSize: '14px' }"/>
        </Button>
      </div>
    </div>
    <Edit @save="recordData.saveData" @register="recordData.registerEditPage"/>
    <slot></slot>
  </div>
</template>
<script setup lang="ts">

import {inject} from 'vue'
import {
  SettingFilled,
  SyncOutlined,
  PieChartFilled
} from '@ant-design/icons-vue';
import {Popover, Button} from 'ant-design-vue'
import Edit from '../popup/edit.vue'
const recordData =inject('recordData')


</script>
<style scope
       src="../../../../../assets/styles/generate-code-record-style/record.less"
       lang="less">
</style>
