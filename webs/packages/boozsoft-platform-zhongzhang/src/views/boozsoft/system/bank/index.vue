<template>
  <div class="app-container">

    <div class="app-container-head">
      <img src="/img/menu/index_dept.png">

      <span style="    margin-left: 10px;"><b>银行档案(账套)</b></span>
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
      <div style="float: left; line-height: 30px">
        <span style="font-size: 14px; color: black">共{{ cardList.length }}条</span>
      </div>
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

        <a-button @click="activeKey=!activeKey">
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
      v-if="activeKey"
      :row-selection="{ type: 'checkbox' ,fixed: true}"
      @row-click="condClick"
      @register="registerTable"
    >
      <template #currency="{ record }"> {{ record.currency === '1' ? '单币种' : '多币种' }} </template>
      <template #successState="{ record }">
          <span>
            <a-tag
              :color="record.successState === '1' ? 'green' : 'volcano'"
            >
              {{ record.successState === '1' ? '已生效' : '未生效' }}
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
              <p style="cursor: pointer" @click="openEdit(record)"><FormOutlined /> 编辑</p>
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
              v-for="(item) in cardList"
              :key="index"
            >
              <a-col :span="6">
                <a-list-item style="width: 95%">
                  <a-card
                    :hoverable="true"
                    :class="`${cardList}__card`"
                    style="width: 100%"
                  >
                    <div style="width: 100%;float: right;" @click="openEdit(item)" >
                      <div :class="`${cardList}__card-title`">
                        <Icon
                          v-if="item.icon"
                          class="icon"
                          :icon="item.icon"
                          :color="item.color"
                        />
                      </div>
                      <div style="font-size: 24px;">
                        {{ item.bankName }}
                      </div>
                      <div style="font-size: 16px;">
                        银行账号：{{ item.bankAccount }}<br/>
                        会计科目：{{ item.kemuCode }}
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
import { accGetBankList, accDeleteBank, accSaveBank} from '/@/api/record/system/bank'

import { BasicTable, useTable } from '/@/components/Table'
import Edit from './popup/edit.vue'
import { useModal } from '/@/components/Modal'
import { onMounted, ref } from 'vue'
import Icon from '/@/components/Icon/index'
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
  list: accGetBankList,
  columns: [
    {
      title: 'ID',
      dataIndex: 'id',
      defaultHidden: true,
      ellipsis: true
    },
    {
      title: '银行编码',
      dataIndex: 'bankCode',
      width: 100,
      ellipsis: true
    },
    {
      title: '银行名称',
      dataIndex: 'bankName',
      ellipsis: true
    },
    {
      title: '银行账户',
      dataIndex: 'bankAccount',
      ellipsis: true
    },
    {
      title: '币种',
      dataIndex: 'currency',
      width: 100,
      ellipsis: true,
      slots: { customRender: 'currency' }
    },
    {
      title: '开户地',
      dataIndex: 'bankArea',
      ellipsis: true
    },
    {
      title: '行号',
      dataIndex: 'bankNum',
      width: 100,
      ellipsis: true
    },
    {
      title: '对应会计科目',
      dataIndex: 'kemuCode',
      width: 120,
      ellipsis: true
    },
    {
      title: '生效状态',
      dataIndex: 'successState',
      width: 100,
      ellipsis: true,
      slots: { customRender: 'successState' }
    }
  ],
  editData: {
    id: '',
    bankCode: '',
    bankName: '',
    bankAccount: '',
    currency: '',
    bankArea: '',
    bankNum: '',
    kemuCode: '',
    successState: '',
    applyDatabaseUniqueCode: '',
    applyUser: '',
    applyDate: '',
    approveUser: '',
    approveDate: '',
    biandongMethod: ''
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
  bankCode: '',
  bankName: '',
  bankAccount: '',
  currency: '',
  bankArea: '',
  bankNum: '',
  kemuCode: '',
  successState: '',
  applyDatabaseUniqueCode: '',
  applyUser: '',
  applyDate: '',
  approveUser: '',
  approveDate: '',
  biandongMethod: ''
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
  await accDeleteBank(data)
  await reloadCurrentPage()
  alert('删除成功！')
  reload()
}
const activeKey = ref(false)

const cardList:any = ref({})
async function reloadCurrentPage() {
  const res = await accGetBankList()
  cardList.value = res.items
  console.log(cardList.value)
}
onMounted(async() => {
  await reloadCurrentPage()
})
async function saveData(data:any) {
  await accSaveBank(data)
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
