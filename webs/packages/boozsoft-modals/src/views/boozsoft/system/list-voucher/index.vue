<template>
  <div class="app-container" style="padding: 10px">
      <div class="app-container-head"><img :src="getThisIndexImg()" class="container-head-img" draggable="false" >
      <div class="container-head-title">
        <b>凭证列表</b>
      </div>
      <div style="display: inline-block;margin: 0px 20px;" class="head-index-year-month" >
        <a-date-picker  mode="year" valueFormat="YYYY" format="YYYY年" style="width: 120px" placeholder=""/>
        <a-range-picker  />
      </div>
      <div class="ant-btn-group">
        <button
          type="button"
          class="ant-btn"
          ant-click-animating-without-extra-node="false"
          @click="openAddPage()"
        ><span>条件查询</span></button>
        <button
          type="button"
          class="ant-btn"
          ant-click-animating-without-extra-node="false"
          @click="openAddPage()"
        ><span>现金流量</span></button>
        <button
          type="button"
          class="ant-btn"
          ant-click-animating-without-extra-node="false"
          @click="openAddPage()"
        ><span>新增</span></button>
        <button
          type="button"
          class="ant-btn"
          ant-click-animating-without-extra-node="false"
          @click="openAddPage()"
        ><span>导入</span></button>
        <button
          type="button"
          class="ant-btn"
        ><span>整理</span></button>
        <button
          type="button"
          class="ant-btn"
        ><span>审核</span></button>
        <button
          type="button"
          class="ant-btn"
        ><span>记账</span></button>
        <button
          type="button"
          class="ant-btn"
        ><span>导出</span></button>
        <button
          type="button"
          class="ant-btn"
          ant-click-animating-without-extra-node="false"
        ><span>打印</span></button>
      </div>
    </div>
    <div class="app-container-neck">
      <div style="margin-left: 5px">
        <a-button class="ant-btn-me">
          <SyncOutlined :style="{ fontSize: '14px' }" />
        </a-button>
        <a-popover placement="bottom">
          <a-button>
            <SettingFilled :style="{ fontSize: '14px' }" />
          </a-button>
        </a-popover>
        <a-popover placement="bottom">
          <a-button>
            <PicLeftOutlined :style="{ fontSize: '14px' }" />
          </a-button>
        </a-popover>
        <a-button>
          <EditFilled :style="{ fontSize: '14px' }" />
        </a-button>
<!--        <a-button>
          <PieChartFilled :style="{ fontSize: '14px' }" />
        </a-button>-->
        <a-button>
          <FilterFilled :style="{ fontSize: '14px' }" />
        </a-button>
      </div>
      <div style="position: relative">
        <!-- 搜索 -->
        <a-input-search
          placeholder=""
          style="width: 200px; border-radius: 4px"
          @search="onSearch"
        />
      </div>
    </div>
    <div class="app-container-content">

      <BasicTable
        :row-selection="{ type: 'checkbox',selectedRowKeys: pageParameter.selectedRowKeys , onChange: onSelectChange}"
        @register="registerTable"
        :pagination="{ pageSize: '200',showSizeChanger: true, pageSizeOptions: ['200','500','1000'] }"
      >
        <template #action="{ record }">

          <div>
            <a-popover placement="bottom">
              <a-button style="padding: 0px 4px; height: 20px;">
                <CaretDownFilled />
              </a-button>
              <template #content>
                <p style="cursor: pointer" @click="openEdit(record)"><FormOutlined /> 编辑</p>
                <p style="cursor: pointer" @click="del(record)"><DeleteOutlined /> 删除</p>
              </template>
            </a-popover>
          </div>
        </template>
      </BasicTable>
    </div>
  </div>
</template>
<script setup="props, {emit}" lang="ts">

import {findAllIncomeKeMuCode,findAllKeMuCode} from '/@/api/record/system/definition-carry-forward'
import { BasicTable, useTable } from '/@/components/Table'
import { onMounted, ref,reactive } from 'vue'

import {
  CaretDownFilled,
  FormOutlined,
  DeleteOutlined,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  EditFilled,
  PieChartFilled,
  FilterFilled
} from '@ant-design/icons-vue'
import {GetSupplierClassTree} from "/@/api/record/system/supplier_class";
import {getThisIndexImg} from '/@/api/task-api/tast-bus-api';
const CrudApi = {
  list: findAllKeMuCode,
  columns: [
    {
      title: 'ID',
      dataIndex: 'id',
      defaultHidden: true,
    },
    {
      title: '序号',
      dataIndex: '序号',
      width: '80px'
    },
    {
      title: '凭证日期',
      dataIndex: 'dbillDate',
      width: '120px',
      ellipsis: true
    },
    {
      title: '凭证字',
      dataIndex: 'inoId',
      width: '80px'
    },
    {
      title: '凭证编号',
      dataIndex: 'inoId',
      ellipsis: true,
      width: '120px'
    },
    {
      title: '摘要',
      dataIndex: 'cdigest',
      ellipsis: true,
      width: '250px'
    },
    {
      title: '科目编码',
      dataIndex: 'ccode',
      ellipsis: true,
      width: '120px'
    },
    {
      title: '科目名称',
      dataIndex: 'ccodeName',
      ellipsis: true,
      width: '200px'
    },
    {
      title: '借方金额',
      dataIndex: 'bprogerty',
      ellipsis: true,
      align: 'right'
    },
    {
      title: '贷方金额',
      dataIndex: 'bprogerty2',
      ellipsis: true,
      align: 'right'
    },
    {
      title: '制单人',
      dataIndex: 'syCcodeName',
      ellipsis: true,
      width: '100px'
    },
    {
      title: '审核人',
      dataIndex: 'syCcodeName1',
      ellipsis: true,
      width: '100px',
    },
    {
      title: '出纳签字',
      dataIndex: 'syCcodeName2',
      ellipsis: true,
      width: '100px'
    },
    {
      title: '记账人',
      dataIndex: 'syCcodeName3',
      ellipsis: true,
      width: '100px',
    }
  ]
}

// 这是示例组件
const [registerTable, { reload }] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: false,actionColumn: {
    width: 80,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' }
  }
})

function onSearch(){}

const pageParameter  =  reactive({
  incomeCode: '',     // 损益科目值
  yearMonth: '',      // 期间
  selectedRowKeys: [] // 表单选中的值
})

async function onSelectChange(selectedRowKeys) {
  pageParameter.selectedRowKeys = selectedRowKeys;
}
</script>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style scoped lang="less">
:deep(.ant-card-body) {
  padding: 16px;
  border-left: 2px solid rgb(1, 143, 251);
  box-shadow: rgb(72 113 140) -3px 1px 7px -1px;
}
</style>
