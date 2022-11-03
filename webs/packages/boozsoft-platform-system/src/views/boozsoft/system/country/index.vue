<script setup lang="ts">
import { defineComponent, getCurrentInstance, reactive, toRefs } from 'vue'
import { BasicTable, useTable, TableAction } from '/@/components/Table'
import { useModal } from '/@/components/Modal'
import Edit from './popup/edit.vue'
import type { BasicColumn } from '/@/components/Table/src/types/table.ts'
import { BasicModal } from '/@/components/Modal'
import { deleteUser, getUserListById, saveUser } from '/@/api/record/system/user'
import {
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  EditFilled,
  PieChartFilled,
  FilterFilled,
  TaobaoCircleFilled,
  CodepenCircleFilled
} from '@ant-design/icons-vue'
// CRUD接口
const CrudApi = {
  list: getUserListById,
  columns: [
    {
      title: 'id',
      dataIndex: 'id',
      defaultHidden: true,
      ellipsis: true,
      sorter: (a, b) => a.id - b.id
    },
    {
      title: '国家编码',
      dataIndex: 'countryId',
      ellipsis: true
    },
    {
      title: '英文名称',
      dataIndex: 'nameen',
      ellipsis: true,
      sorter: (a, b) => a.nameen.length - b.nameen.length
    },
    {
      title: '中文名称',
      dataIndex: 'namech',
      ellipsis: true,
      sorter: (a, b) => a.namech.length - b.namech.length
    }
  ],
  editData: {
    id: '',
    countryId: '',
    nameen: '',
    namech: ''
  }
}
// @ts-ignore
const [registerTable, { instance, formInstance, reload }] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  actionColumn: {
    width: 160,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' }
  }
})

const [registerEditPage, { openModal: openEditPage }] = useModal()

const openAddPage = () => {
  openEditPage(true, {
    data: CrudApi.editData
  })
}
const openModifyPage = (a, b) => {
  openEditPage(true, {
    data: CrudApi.editData
  })
}
const del = async(id) => {
  await deleteUser(id)
  alert('删除成功！')
  reload()
}
const saveUserMet = async(data) => {
  await saveUser(data)
  reload(data)
}
</script>
<template>
  <div class="app-container">
    <div class="app-container-head">
      <img src="/img/menu/index_user.png">

      <span style="    margin-left: 10px;"><b>国家档案</b></span>
      <div style="display: inline">
        <select class="head-index-select">
          <option style="border: none; outline: none" value="">查看全部 </option>
        </select>
      </div>

      <div class="ant-btn-group" data-v-a1ccd506="" style="float: right">
        <button
          type="button"
          class="ant-btn"
          ant-click-animating-without-extra-node="false"
          @click="openAddPage()"
        ><span>新建</span></button>
        <button
          type="button"
          class="ant-btn"
          ant-click-animating-without-extra-node="false"
        ><span>打印</span></button>
        <button type="button" class="ant-btn"><span>导入</span></button>
        <button type="button" class="ant-btn"><span>导出</span></button>
      </div>
    </div>
    <div class="app-container-neck">
      <div style="float: left; line-height: 30px">
        <span style="font-size: 14px; color: black">共有1位启用员工</span>
      </div>
      <div style="float: right; margin-left: 10px">
        <a-popover placement="bottom">
          <a-button class="ant-btn-me"><SettingFilled :style="{ fontSize: '14px' }" /></a-button>
        </a-popover>
        <a-popover placement="bottom">
          <a-button class="ant-btn-me"><PicLeftOutlined :style="{ fontSize: '14px' }" /></a-button>
        </a-popover>

        <a-button class="ant-btn-me"><SyncOutlined :style="{ fontSize: '14px' }" />
        </a-button>
        <a-button class="ant-btn-me"><EditFilled :style="{ fontSize: '14px' }" /></a-button>

        <a-button-group>
          <a-button class="ant-btn-me">
            <PieChartFilled :style="{ fontSize: '14px' }" />
          </a-button>
          <a-button class="ant-btn-me">
            <FilterFilled :style="{ fontSize: '14px' }" />
          </a-button>
        </a-button-group>
      </div>
      <div style="float: right; position: relative">
        <!-- 搜索 -->
        <a-input-search
          placeholder=""
          style="width: 200px; border-radius: 4px"
          @search="onSearch"
        />
      </div>
    </div>
    <hr style="margin-top: 5.2%;border: none">
    <!-- 表格区域 -->
    <Edit @save="saveUserMet" @register="registerEditPage" />
    <BasicTable :row-selection="{ type: 'checkbox' ,fixed: true}" @register="registerTable">
      <template #roleList="{ record }">
        <span v-for="item in record.roleList">
          {{ item.roleName }}
        </span>
      </template>
      <template #action="{ record }">
        <a-popover placement="bottom">
          <template slot="content">
            <div class="el-radio">
              <a
                title="编辑"
                style="color: #5f375c"
              >&nbsp;<b><i class="fa fa-pencil-square-o" aria-hidden="true" />&emsp;编&emsp;辑</b>&emsp;</a>
            </div>
            <br>
            <div class="el-radio">
              <a
                title="修改密码"
                style="color: #5f375c"
              >&nbsp;<b><i class="fa fa-key" aria-hidden="true" />&emsp;修改密码</b></a>
            </div>
            <div class="el-radio">
              <a
                title="删除"
                style="color: #5f375c"
              >&nbsp;<b><i class="fa fa-trash" aria-hidden="true" />&nbsp;&emsp;删&emsp;除</b>&emsp;</a>
            </div>
          </template>
          <a-button class="ant-btn-me">⏷</a-button>
        </a-popover>
      </template>
    </BasicTable>
    <!-- 表格区域 -->
  </div>
</template>

<style lang='less' scoped>
.ant-tabs .ant-tabs-left-content {
  padding-left: 0 !important;
}

.card-container {
  background: #f5f5f5;
  overflow: hidden;
  padding: 24px;
}

.card-container > .ant-tabs-card > .ant-tabs-content {
  /*height: 120px;*/
  margin-top: -16px;
}

.card-container > .ant-tabs-card > .ant-tabs-content > .ant-tabs-tabpane {
  background: #fff;
  padding: 16px;
}

.card-container > .ant-tabs-card > .ant-tabs-bar {
  border-color: #fff;
}

.card-container > .ant-tabs-card > .ant-tabs-bar .ant-tabs-tab {
  border-color: transparent;
  background: transparent;
}

.card-container > .ant-tabs-card > .ant-tabs-bar .ant-tabs-tab-active {
  border-color: #fff;
  background: #fff;
}
</style>

<style scoped>
/*放大镜图标 🔍*/
.select-a {
  display: contents;
  outline: 0;
  text-decoration: none;
  width: 72px;
}

.form-control:hover {
  /*搜索框边框*/
  border-color: #009688 !important;
}

.el-radio {
  padding: 5px;
  margin-right: 0;
}

.el-radio:hover {
  background-color: rgb(202, 200, 197);
  padding: 5px;
}

.btn-marin-modify {
  margin-top: -7px;
}

.select-down-span {
  color: black;
  line-height: 30px;
  padding: 8px 5px;
}

.select-down-span:hover {
  background-color: rgb(202, 200, 197);
}
</style>
<style lang='less' scoped>
.app-container {
  background: rgb(241, 239, 239);
  border-radius: 5px;
  margin: 10px;
  padding: 20px 20px 45px 20px;
  height: calc(100% - 20px);
  .app-container-head {
    width: 100%;
    float: left;
    font-size: 24px;
    line-height: 0.42857;
    .head-index-select {
      border: 0 !important;
      background: transparent !important;
      width: 240px;
      height: 40px;
      font-size: 18px;
      text-align-last: center;
      margin-left: 2%;
    }
    .ant-btn {
      color: blue;
    }
  }
  .app-container-neck {
  }
  .ant-table {
    line-height: 0;
  }
}
</style>

<style>
.ant-table table tr> th,
.ant-table table tr> td {
  padding: 5px 8px !important;
}
.el-radio{
  color: #606266;
  font-weight: 500;
  line-height: 1;
  position: relative;
  cursor: pointer;
  display: inline-block;
  white-space: nowrap;
  outline: none;
  font-size: 14px;
  padding: 5px;
  margin-right: 0;
  -moz-user-select: none;
  -webkit-user-select: none;
  -ms-user-select: none;
}
.ant-btn-me {
  padding: 0px 12px !important;
  margin: 0 2px;
}
</style>
