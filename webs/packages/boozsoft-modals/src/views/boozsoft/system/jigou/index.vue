<template>
  <div class="app-container">
    <div class="app-container-head">
      <img src="/img/menu/index_dept.png" />

      <span style="    margin-left: 10px;"><b>机构档案</b></span>
      <div style="display: inline">
        <select class="head-index-select">
          <option style="border: none; outline: none"
                  value="">查看全部 </option>
        </select>
      </div>

      <div class="ant-btn-group"
           data-v-a1ccd506=""
           style="float: right">
        <button type="button"
                class="ant-btn"
                ant-click-animating-without-extra-node="false"
                @click="condClick()"><span>新建</span></button>
        <button type="button"
                class="ant-btn"
                ant-click-animating-without-extra-node="false"><span>打印</span></button>
        <button type="button"
                class="ant-btn"><span>导入</span></button>
        <button type="button"
                class="ant-btn"><span>导出</span></button>
      </div>
    </div>
    <div class="app-container-neck">
      <div style="float: left; line-height: 30px">
        <span style="font-size: 14px; color: black">共?</span>
      </div>
      <div style="float: right; margin-left: 10px">
        <a-popover placement="bottom">
          <a-button class="ant-btn-me">
            <SettingFilled :style="{ fontSize: '14px' }" />
          </a-button>
        </a-popover>
        <a-popover placement="bottom">
          <a-button class="ant-btn-me">
            <PicLeftOutlined :style="{ fontSize: '14px' }" />
          </a-button>
        </a-popover>

        <a-button class="ant-btn-me">
          <SyncOutlined :style="{ fontSize: '14px' }" />
        </a-button>
        <a-button class="ant-btn-me">
          <EditFilled :style="{ fontSize: '14px' }" />
        </a-button>

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
        <a-input-search placeholder=""
                        style="width: 200px; border-radius: 4px"
                        @search="onSearch" />
      </div>
    </div>
    <hr style="margin-top: 5.2%;border: none" />
    <Edit @save="saveJigou"
          @register="registerEditPage" />
    <BasicTable :row-selection="{ type: 'checkbox' ,fixed: true}"
                @row-click="condClick"
                @register="registerTable">
    </BasicTable>
  </div>
</template>
<script setup lang="ts">
import { GetJigouTree } from '/@/api/sys/jigou';
import { BasicTable, useTable } from '/@/components/Table';
import Edit from './popup/edit.vue';
import { useModal } from '/@/components/Modal';
import {
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  EditFilled,
  PieChartFilled,
  FilterFilled,
  TaobaoCircleFilled,
  CodepenCircleFilled,
} from '@ant-design/icons-vue';
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    defaultHidden: true,
    ellipsis: true,
  },
  {
    title: '机构名称',
    dataIndex: 'deptName',
    ellipsis: true,
  },
  {
    title: '类型',
    dataIndex: 'deptType',
    ellipsis: true,
  },
  {
    title: '部门全称',
    dataIndex: 'fullName',
    ellipsis: true,
  },
  {
    title: '上级ID',
    dataIndex: 'parentId',
    ellipsis: true,
    defaultHidden: true,
  },
  {
    title: '租户ID',
    dataIndex: 'tenantId',
    ellipsis: true,
  },
  {
    title: '排序',
    dataIndex: 'sort',
    ellipsis: true,
  },
  // ,
  // {
  //   title: '备注',
  //   dataIndex: 'remark',
  //   ellipsis: true
  // }
];
// 这是示例组件
const [registerTable, { instance, formInstance, reload }] = useTable({
  api: GetJigouTree,
  columns: columns,
  bordered: true,
  showIndexColumn: false,
  actionColumn: {
    width: 160,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' },
  },
});
const [registerEditPage, { openModal: openEditPage }] = useModal();
const val = {
  id: '',
  tenantId: '',
  parentId: '',
  deptName: '',
  deptType: '',
  fullName: '',
  sort: '',
  remark: '',
  isDeleted: '',
};
const condClick = (data, index, e) => {
  if (e.target.cellIndex == 1) {
    openEditPage(true, {
      data: val,
    });
  }
};
</script>
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
.ant-table table tr > th,
.ant-table table tr > td {
  padding: 5px 8px !important;
}
.el-radio {
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

