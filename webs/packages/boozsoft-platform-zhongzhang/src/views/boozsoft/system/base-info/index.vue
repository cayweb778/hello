<template>
  <div class="app-container">

    <div class="app-container-head">
      <img src="/img/menu/index_dept.png" style="display: inline-block;">
      <span style="margin-left: 10px;"><b>基础档案</b></span>
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
        ><span>导入</span></button>
        <button
          type="button"
          class="ant-btn ant-btn-me"
        ><span>导出</span></button>
        <button
          type="button"
          class="ant-btn ant-btn-me"
          ant-click-animating-without-extra-node="false"
        ><span>打印</span></button>
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
<!--        <a-popover placement="bottom">
          <a-button>
            <SettingFilled :style="{ fontSize: '14px' }" />
          </a-button>
        </a-popover>-->
        <a-popover placement="bottom">
          <a-button @click="activeKey=!activeKey">
            <PicLeftOutlined :style="{ fontSize: '14px' }" />
          </a-button>
        </a-popover>

        <a-button>
          <EditFilled :style="{ fontSize: '14px' }" />
        </a-button>

<!--        <a-button >
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
    <BasicTable
      v-if="activeKey"
      :row-selection="{ type: 'checkbox' ,fixed: true}"
      @row-click="condClick"
      @register="registerTable"
    >
      <template #action="{ record }">

        <div>
          <a-popover placement="bottom">
            <a-button style="padding: 0px 4px; height: 20px;">
              <CaretDownFilled />
            </a-button>
            <template #content>
              <p style="cursor: pointer" @click="openEdit(record)"><FormOutlined /> 编辑</p>
              <p style="cursor: pointer"><CheckCircleOutlined /> 启用</p>
              <p style="cursor: pointer"><CloseCircleOutlined /> 停用</p>
              <p style="cursor: pointer" @click="del(record)"><DeleteOutlined /> 删除</p>
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
              v-for="(item,index) in cardList"
            >
              <a-col :span="6">
                <a-list-item style="width: 95%">
                  <a-card
                    :hoverable="true"
                    :class="`${cardList}__card`"
                    style="width: 100%"
                  >
                    <div style="width: 100%;float: right;">
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
                        {{ item.baseTable }}
                      </div>
                      <div style="font-size: 24px; width: 70%; text-align:center">
                        {{ item.baseName }}
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
import {deleteBaseInfo, saveBaseInfo} from '/@/api/record/system/baseInfo'
import {
  Input as AInput, Select as ASelect, Popover as APopover,
   Radio as ARadio, Table as ATable, message,Card as ACard,List as AList,Col as ACol,Row as ARow
} from "ant-design-vue";
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ARadioButton = ARadio.Button
const ARadioGroup = ARadio.Group
const AListItem = AList.Item
import { BasicTable, useTable } from '/@/components/Table'
import Edit from './popup/edit.vue'
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
import { baseInfoFindAll } from '/@/api/base-info/base-info'

const CrudApi = {
  list: baseInfoFindAll,
  columns: [
    {
      title: 'ID',
      dataIndex: 'id',
      defaultHidden: true,
      ellipsis: true
    },
    // { title: '唯一标识', dataIndex: 'uniqueCode', ellipsis: true },
    {
      title: '基础档案名称',
      dataIndex: 'baseName',
      ellipsis: true
    },
    {
      title: '基础档案表名',
      dataIndex: 'baseTable',
      ellipsis: true
    }
  ],
  editData: {
    id: '',
    uniqueCode: '',
    psnTypeCode: '',
    psnTypeName: ''
  }
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
  uniqueCode: '',
  psnTypeCode: '',
  psnTypeName: ''
}
const condClick = (data:any, e:any) => {
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
  await deleteBaseInfo(data)
  alert('删除成功！')
  reload()
}
const activeKey = ref(false)

const cardList:any = ref({})
async function reloadCurrentPage() {
  const res:any = await baseInfoFindAll()
  cardList.value = res.items
}
onMounted(async() => {
  await reloadCurrentPage()
})
async function saveData(data:any) {
  await saveBaseInfo(data)
  await reloadCurrentPage()
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
