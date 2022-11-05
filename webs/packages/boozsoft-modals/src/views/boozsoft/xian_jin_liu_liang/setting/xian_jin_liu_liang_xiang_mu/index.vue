<template>
  <div class="app-container">

    <div class="app-container-head">
      <img src="/img/menu/index_dept.png">

      <span style="    margin-left: 10px;"><b>现金流量基础项目</b></span>
      <div style="display: inline">
        <select class="head-index-select">
          <option
            style="border: none; outline: none"
            value=""
          >查看全部 </option>
        </select>
      </div>

      <div
        class="ant-btn-group"
        data-v-a1ccd506=""
        style="float: right"
      >
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
        <button
          type="button"
          class="ant-btn"
        ><span>导入</span></button>
        <button
          type="button"
          class="ant-btn"
        ><span>导出</span></button>
      </div>
    </div>
    <div class="app-container-neck">
      <div style="float: right; margin-left: 10px">
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

        <a-button>
          <PieChartFilled :style="{ fontSize: '14px' }" />
        </a-button>
        <a-button>
          <FilterFilled :style="{ fontSize: '14px' }" />
        </a-button>
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
    <Edit
      @save="saveData"
      @register="registerEditPage"
    />
    <BasicTable
      :row-selection="{ type: 'checkbox' ,fixed: true}"
      @row-click="condClick"
      @register="registerTable"
    >
      <template #projectType="{ record }"> {{ formatProjectType(record.projectType) }} </template>
      <template #fangxiang="{ record }"> {{ formatFangxiang(record.fangxiang) }} </template>
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
</template>
<script setup="props, {emit}" lang="ts">
import {getProjectCashList, deleteProjectCash, saveProjectCash} from '/@/api/record/system/project-cash'
import {Select as ASelect,Popover as APopover,Input as AInput} from 'ant-design-vue'
const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option
import { BasicTable, useTable } from '/@/components/Table'
import Edit from './popup/edit.vue'
import { useModal } from '/@/components/Modal'
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
const CrudApi = {
  list: getProjectCashList,
  columns: [
    {
      title: 'ID',
      dataIndex: 'id',
      defaultHidden: true,
      ellipsis: true
    },
    {
      title: '项目编码',
      dataIndex: 'projectCode',
      ellipsis: true
    },
    {
      title: '项目名称',
      dataIndex: 'projectName',
      ellipsis: true
    },
    {
      title: '所属类别',
      dataIndex: 'projectType',
      ellipsis: true,
      slots: { customRender: 'projectType' }
    },
    {
      title: '方向',
      dataIndex: 'fangxiang',
      ellipsis: true,
      slots: { customRender: 'fangxiang' }
    }
  ],
  editData: {
    id: '',
    projectCode: '',
    projectName: '',
    projectType: '',
    fangxiang: ''
  }
}
function formatProjectType(projectType:any) {
  let str = projectType
  switch (projectType) {
    case '1':
      str = '经营活动'
      break
    case '2':
      str = '投资活动'
      break
    case '3':
      str = '筹资活动'
      break
    case '4':
      str = '汇率活动'
      break
    case '5':
      str = '现金及现金等价物'
      break
  }
  return str
}
function formatFangxiang(fangxiang:any) {
  let str = fangxiang
  switch (fangxiang) {
    case '1':
      str = '流入'
      break
    case '0':
      str = '流出'
      break
  }
  return str
}
// 这是示例组件
const [registerTable, { reload }] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: false,
  actionColumn: {
    width: 160,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' }
  }
})
const [registerEditPage, { openModal: openEditPage }] = useModal()
const val = {
  id: '',
  projectCode: '',
  projectName: '',
  projectType: '',
  fangxiang: ''
}
const condClick = (data:any,index:any, e:any) => {
  console.log(index)
  if (e.target.cellIndex == 1) {
    openEditPage(true, {
      data: data
    })
  }
}
const openAddPage = () => {
  openEditPage(true, {
    data: val
  })
}
const openEdit = (data:any) => {
  openEditPage(true, {
    data: data
  })
}
const del = async(data:any) => {
  await deleteProjectCash(data)
  alert('删除成功！')
  reload()
}

async function saveData(data:any) {
  await saveProjectCash(data)
  reload()
}

function onSearch(){}
</script>
<style src="../../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style src="../../../../../assets/styles/global-menu-index.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style scoped lang="less">
:deep(.ant-card-body) {
  padding: 16px;
  border-left: 2px solid rgb(1, 143, 251);
  box-shadow: rgb(72 113 140) -3px 1px 7px -1px;
}
</style>
