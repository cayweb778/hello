<template>
  <div v-if="viewModel?.settings!=null" class="app-container-head" style="height:100%;position:relative;background: none !important">
    <img :src="getThisIndexImg()" class="container-head-img" draggable="false">
    <div class="container-head-title"><b class="noneSpan">{{ viewModel.label }}</b></div>
    <div class="ant-btn-group" data-v-a1ccd506="" style="float: right">
      <template v-for="it in viewModel.arr">
        <button v-if="it.enable"
                @click="it.fun"
                class="ant-btn ant-btn-me" v-text="it.label"/>
      </template>
    </div>
    <div style="position:absolute;bottom:5px;left:12px;width:550px">
      <AccountPicker v-if="!viewModel?.settings?.disableAccountPicker" theme="one" @reloadTable="viewModel.dynamicAdReload"/>
    </div>
    <div class="app-container-neck" style="position:absolute;bottom:0;right:0;">
      <div style="float: right; margin-left: 10px">
        <Button @click="viewModel.reload">
          <SyncOutlined :style="{ fontSize: '14px' }"/>
        </Button>
        <Popover v-if="viewModel.activeKey" placement="bottom">
          <template #content></template>
          <Button>
            <SettingFilled :style="{ fontSize: '14px' }"/>
          </Button>
        </Popover>
        <Button @click="viewModel.activeKey = !viewModel.activeKey">
          <PieChartFilled :style="{ fontSize: '14px' }"/>
        </Button>
      </div>
    </div>
  </div>
</template>
<script setup>
import {getThisIndexImg} from '/@/api/task-api/tast-bus-api';
import {inject} from "vue";
const viewModel = inject('viewModel')

import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {
  SettingFilled,
  SyncOutlined,
  PieChartFilled
} from '@ant-design/icons-vue';

import {Popover, Button} from 'ant-design-vue'
</script>
