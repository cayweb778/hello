<template>
  <div class="app-container">

      <div class="app-container-head"><img :src="getThisIndexImg()" class="container-head-img" draggable="false" >
      <div class="container-head-title">
        <b class="noneSpan">项目大类</b>
      </div>
<!--      <div style="display: inline">
        <select class="head-index-select">
          <option
            style="border: none; outline: none"
            value=""
          >查看全部 </option>
        </select>
      </div>-->

      <div
        class="ant-btn-group"
        data-v-a1ccd506=""
        style="float: right"
      >
        <button
          type="button"
          class="ant-btn ant-btn-me"
          ant-click-animating-without-extra-node="false"
          @click="openAdd()"
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
    </div>
    <div class="app-container-neck">
      <div style="float: left; line-height: 30px">
        <span style="font-size: 14px; color: black">共{{ cardList.length }}条</span>
      </div>
      <div style="float: right; margin-left: 10px">
        <a-button class="ant-btn-me">
          <SyncOutlined :style="{ fontSize: '14px' }" />
        </a-button>
        <a-button  @click="activeKey=!activeKey">
          <PicLeftOutlined :style="{ fontSize: '14px' }" />
        </a-button>
        <a-button>
          <EditFilled :style="{ fontSize: '14px' }" />
        </a-button>
<!--        <a-popover placement="bottom">
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

        <a-button @click="activeKey=!activeKey">
          <PieChartFilled :style="{ fontSize: '14px' }" />
        </a-button>
        <a-button>
          <FilterFilled :style="{ fontSize: '14px' }" />
        </a-button>-->
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
    <Add
      @save="saveData"
      @register="registerAddPage"
    />
    <Approve
      @approveOk="approveOkDate"
      @register="registerApprovePage"
    />
    <BasicTable
      v-if="activeKey"
      :row-selection="{ type: 'checkbox' ,fixed: true}"
      @register="registerTable"
    >
      <template #successState="{ record }">
          <span>
            <a-tag
              :color="record.successState === '1' ? 'green' : 'volcano'"
            >
              {{ record.successState === '1' ? '已生效' : '未生效' }}
            </a-tag>
          </span>
      </template>
      <template #flag="{ record }">
          <span>
            <a-tag
              :color="record.flag === '1' ? 'green' : 'volcano'"
            >
              {{ record.flag === '1' ? '启用' : '停用' }}
            </a-tag>
          </span>
      </template>
      <template #action="{ record }">

        <div>
          <a-popover placement="bottom">
            <a-button style="padding: 0px 4px; height: 20px;">
              <CaretDownFilled />
            </a-button>
            <template #content>
              <p v-if="record.successState==='0'" style="cursor: pointer" @click="openApprove(record)"><FormOutlined /> 审批</p>
              <p v-if="record.flag=='1'" style="cursor: pointer" @click="openEdit(record)"><FormOutlined /> 编辑</p>
              <p v-if="record.successState=='1' && record.flag=='0'" @click="editFlagData(record)" style="cursor: pointer"><CheckCircleOutlined /> 启用</p>
              <p v-if="record.successState=='1' && record.flag=='1'" @click="editFlagData(record)" style="cursor: pointer"><CloseCircleOutlined /> 停用</p>
              <p v-if="record.successState=='0'" style="cursor: pointer" @click="del(record)"><DeleteOutlined /> 删除</p>
            </template>
          </a-popover>
        </div>
      </template>

    </BasicTable>
    <div
      v-if="!activeKey"
      :class="cardList"
    >
      <div :class="`${cardList}__content`">
        <!--        <div>ID:{{ item.id }}</div>-->
        <a-list style="height: 700px;overflow-y: scroll;overflow-x: hidden;">
          <a-row :gutter="16">
            <template
              v-for="(item) in cardList"
            >
              <a-col :span="8">
                <a-list-item style="width: 95%">
                  <a-card
                    :hoverable="true"
                    :class="`${cardList}__card`"
                    style="width: 100%"
                  >
                    <div style="width: 100%;float: right;" @click="openEdit(item)">
                      <div :class="`${cardList}__card-title`">
                        <Icon
                          v-if="item.icon"
                          class="icon"
                          :icon="item.icon"
                          :color="item.color"
                        />
                        <!--                        {{ index+1 }}-->
                      </div>
                      <div style="float: right; border: 1px solid rgb(1, 143, 251);border-radius: 10px;padding: 0 10px; margin-top: -5px;">
                        {{ item.projectCateCode }}
                      </div>
                      <div style="font-size: 24px; text-align:center">
                        {{ item.projectCateName }}
                      </div>
                    </div>
                  </a-card>
                </a-list-item>
              </a-col>
            </template>
          </a-row>
        </a-list>
      </div>

    </div>
  </div>
</template>
<script setup="props, {emit}" lang="ts">
import {deleteCate, sysSaveCate,apporveCate,editFlag} from '/@/api/record/system/sys_project_category'
import {getThisIndexImg} from '/@/api/task-api/tast-bus-api';
import { BasicTable, useTable } from '/@/components/Table'
import Edit from './popup/edit.vue'
import Approve from './popup/approve.vue'
import Add from './popup/add.vue'
import { useModal } from '/@/components/Modal'
import { onMounted, ref } from 'vue'
import Icon from '/@/components/Icon/index'
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
import { cateFindAll,cateFindStateFlag } from '/@/api/sys_project_category/project_category'
import {Select as ASelect,Input as AInput,List as AList,Row as ARow,Col as ACol ,Card as ACard,Popover as APopover,Button as AButton,Tag as ATag } from 'ant-design-vue'

const AListItem=AList.Item
const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option

const CrudApi = {
  list: cateFindAll,
  columns: [
    {
      title: 'ID',
      dataIndex: 'id',
      defaultHidden: true,
      ellipsis: true
    },
    // { title: '唯一标识', dataIndex: 'uniqueCode', ellipsis: true },
    {
      title: '大类编码',
      dataIndex: 'projectCateCode',
      ellipsis: true
    },
    {
      title: '大类名称',
      dataIndex: 'projectCateName',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true
    },
    /*{
      title: '项目目录表名称',
      dataIndex: 'projectTable',
      ellipsis: true
    },*/
    {
      title: '是否生效',
      dataIndex: 'successState',
      ellipsis: true,
      slots: { customRender: 'successState' }
    },
    {
      title: '是否停用',
      dataIndex: 'flag',
      ellipsis: true,
      slots: { customRender: 'flag' }
    }
  ],
  editData: {
    id: '',
    projectCateCode: '',
    projectCateName: '',
    projectTable: '',
    successState: '',
    flag: ''
  }
}
// 这是示例组件
const [registerTable, {  reload }] = useTable({
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
const [registerApprovePage, { openModal: openApprovePage }] = useModal()
const [registerAddPage, { openModal: openAddPage }] = useModal()
const val = {
  id: '',
  projectCateCode: '',
  projectCateName: '',
  projectTable: '',
  successState: '',
  flag: ''
}

const openAdd = () => {
  openAddPage(true, {
    data: val
  })
}
const openEdit = (data:any) => {
  openEditPage(true, {
    data: data
  })
}
const openApprove = (data:any) => {
  openApprovePage(true, {
    data: data
  })
}
const del = async(data:any) => {
  await deleteCate(data)
  // await deleteCate(id, data)
  alert('删除成功！')
  reload()
}
const activeKey = ref(false)
const cardList:any = ref([])
async function reloadCurrentPage() {
  const res:any = await cateFindStateFlag()
  cardList.value = res
  console.log(cardList.value)
}
onMounted(async() => {
  await reloadCurrentPage()
})
async function saveData(data:any) {
  await sysSaveCate(data)
  await reloadCurrentPage()
  reload()
}
async function approveOkDate(data:any) {
  await apporveCate(data)
  await reloadCurrentPage()
  reload()
}
async function editFlagData(data:any) {
  await editFlag(data);
  await reloadCurrentPage()
  reload()
}
function onSearch(){}
</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style scoped lang="less">
:deep(.ant-card-body) {
  padding: 16px;
  border-left: 2px solid rgb(1, 143, 251);
  box-shadow: rgb(72 113 140) -3px 1px 7px -1px;
}
</style>
