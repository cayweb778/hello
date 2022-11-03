<template>
  <div class="app-container">
    <div class="app-container-head">
      <img src="/img/menu/index_dept.png" style="display: inline-block;">
      <span style="margin-left: 10px;"><b>平台管理</b></span>
      <div
        class="ant-btn-group"
        data-v-a1ccd506=""
        style="float: right"
      >
        <button
          type="button"
          class="ant-btn ant-btn-me"
          ant-click-animating-without-extra-node="false"
          @click="openAddPage()"
        ><span>新建</span></button>
        <button
          type="button"
          class="ant-btn ant-btn-me"
          ant-click-animating-without-extra-node="false"
        ><span>打印</span></button>
        <button
          type="button"
          class="ant-btn ant-btn-me"
        ><span>导入</span></button>
        <button
          type="button"
          class="ant-btn ant-btn-me"
        ><span>导出</span></button>
      </div>
      <div>
        <div style="float: right; margin-left: 10px">
          <a-button>
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

          <a-button >
            <PieChartFilled :style="{ fontSize: '14px' }" />
          </a-button>
          <a-button>
            <FilterFilled :style="{ fontSize: '14px' }" />
          </a-button>
        </div>
        <div style="float: right; position: relative">
          <a-input-search
            placeholder=""
            style="width: 200px; border-radius: 4px"
            @search="onSearch"
          />
        </div>
      </div>
    </div>
    <div style="clear:both" />
    <EditPage @save="saveData" @register="registerEditPage" />
    <AddPage @save="saveData" @register="registerSavePage" />
    <PageWrapper dense content-full-height fixed-height content-class="flex">
    <BasicTable class="w-3/4 xl:w-4/5" :row-selection="{ type: 'checkbox' ,fixed: true}" @row-click="condClick" @register="registerTable">
      <template #flag="{ record }"> {{ formatFlag(record.flag) }} </template>
      <template #action="{ record }">
        <div>
          <a-popover placement="bottom">
            <a-button style="padding: 0px 4px; height: 20px;">
              <CaretDownFilled />
            </a-button>
            <template #content>
              <p v-if="record.flag=='1'" style="cursor: pointer" @click="condClick(record)"><FormOutlined /> 编辑</p>
              <p v-if="record.flag=='0'" style="cursor: pointer" @click="editFlagData(record)"><CheckCircleOutlined /> 启用</p>
              <p v-if="record.flag=='1'" style="cursor: pointer" @click="editFlagData(record)"><CloseCircleOutlined /> 停用</p>
              <p style="cursor: pointer" @click="del(record)"><DeleteOutlined /> 删除</p>
            </template>
          </a-popover>
        </div>
      </template>
    </BasicTable>
    </PageWrapper>
  </div>
</template>
<script setup lang="ts">
import { deleteDept, saveDept, editFlag } from '/@/api/record/system/dept'
import { GetDeptTree } from '/@/api/sys/dept'
import { BasicTable, useTable } from '/@/components/Table'
import EditPage from './popup/edit.vue'
import AddPage from './popup/save.vue'
import DeptTree from './DeptTree.vue'
import { useModal } from '/@/components/Modal'
import { PageWrapper } from '/@/components/Page'
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
  FilterFilled
} from '@ant-design/icons-vue'
import {Input as AInput,Select as ASelect,Popover as APopover} from "ant-design-vue";
import {ref} from "vue";
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    defaultHidden: true,
    ellipsis: true
  },
  {
    title: '平台编码',
    dataIndex: 'deptCode',
    ellipsis: true
  },
  {
    title: '平台名称',
    dataIndex: 'deptName',
    ellipsis: true,
    align: 'left'
  },
  {
    title: '是否外链',
    dataIndex: 'uniqueCodeUser',
    ellipsis: true
  },
  {
    title: '创建日期',
    dataIndex: 'createDate',
    ellipsis: true
  },
  {
    title: '是否启用',
    dataIndex: 'flag',
    ellipsis: true,
    slots: { customRender: 'flag' }
  }
]
function formatFlag(flag:any) {
  let str = '启用'
  switch (flag) {
    case '1':
      str = '启用'
      break
    case '0':
      str = '停用'
      break
  }
  return str
}
// 这是示例组件
const [registerTable, { reload }] = useTable({
  api: GetDeptTree,
  columns: columns,
  bordered: true,
  showIndexColumn: false,
  actionColumn: {
    width: 100,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' }
  }
})
const [registerEditPage, { openModal: openEditPage }] = useModal()
const [registerSavePage, { openModal: openSavePage }] = useModal()
const val = {
  parentId: '',
  deptCode: '',
  deptName: '',
  uniqueCodeUser: '',
  createDate: '',
  flag: ''
}
const openAddPage = () => {
  openSavePage(true, {
    data: val
  })
}
const condClick = (data:any) => {
  openEditPage(true, {
    data: data
  })
}

const del = async(data:any) => {
  await deleteDept(data)
  alert('删除成功！')
  reloadTable()
}

async function saveData(data:any) {
  await saveDept(data)
  reloadTable()
}
async function editFlagData(data:any) {
  await editFlag(data)
  reloadTable()
}
function onSearch(){

}
const thisCheckKey = ref('')
function handleSelect(key){
  if (null != key){
    thisCheckKey.value = key
    reloadTable()
  }
}
function reloadTable(){
  reload({
    searchInfo: {'id': thisCheckKey.value}
  })
}
</script>
