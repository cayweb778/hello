<template>
  <div class="app-container-head">
    <AppstoreOutlined style="font-size: 36px;color: #0096c7;" class="container-head-img"/>
    <div class="container-head-title" style="margin-top: 14px;">
      <b class="noneSpan">角色管理</b>
    </div>
    <span
      style="font-size: 12px;color: black;position: absolute;top: 80px;left: 25px">共{{
        viewModel.getTotalNumber()
      }}条记录</span>
    <div class="ant-btn-group" style="float: right">
      <button type="button" class="ant-btn ant-btn-me"  @click="viewModel.openAddPage('add')">新建</button>
      <button type="button" class="ant-btn ant-btn-me"  @click="viewModel.platformManagerFun">权限管理</button>
      <button type="button" class="ant-btn ant-btn-me"  @click="viewModel.openAddPage('edit')">修改</button>
      <button type="button" class="ant-btn ant-btn-me"  @click="viewModel.delData()">删除</button>
      <button type="button" class="ant-btn ant-btn-me"  @click="closeCurrent()">退出</button>
    </div>
  </div>
  <div class="app-container-neck">
    <div style="float: right; margin-left: 10px">
      <a-button style="padding: 0px 12px !important; margin-right: 10px">
        <SyncOutlined :style="{ fontSize: '14px' }" />
      </a-button>

      <a-button>
        <EditFilled :style="{ fontSize: '14px' }" />
      </a-button>
    </div>

    <div class="act-two-d-right">
      <div class="acttd-right-d-search">
        <a-select value="角色名称"
                  class="acttdrd-search-select">

        </a-select>

        <a-input-search
          style="width: 180px;"
          class="acttdrd-search-input"
        />
      </div>
    </div>
  </div>
</template>
<script setup>
import {inject} from "vue";
import {
  Form as AForm,
  Tabs as ATabs,
  Select as ASelect,
  Input as AInput,
  Popover as APopover,
  Checkbox as ACheckbox,
  Modal as AModal,
  Upload as AUpload,
  Row as ARow,
  Col as ACol,
  Statistic as AStatistic,
  message,
} from 'ant-design-vue';
import {
  CaretDownFilled,
  FormOutlined,
  DeleteOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  EditFilled,
  PieChartFilled,
  FilterFilled,AppstoreOutlined
} from '@ant-design/icons-vue';
import {useTabs} from "../../../../../hooks/web/useTabs";
import router from "../../../../../router";
const {closeCurrent} = useTabs(router);
const viewModel=inject('viewModel')
</script>
<style lang="less" scoped>
@import '/@/assets/styles/global-group-org-style.less';
</style>
