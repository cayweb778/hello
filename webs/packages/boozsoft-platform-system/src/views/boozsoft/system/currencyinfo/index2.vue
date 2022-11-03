<template>
  <div>
    <div class="app-container">
      <div class="app-container-head"><img :src="getThisIndexImg()" class="container-head-img" draggable="false" >
        <div class="container-head-title">
          <b class="noneSpan">国际货币</b>
        </div>

        <div class="ant-btn-group" data-v-a1ccd506="" style="float: right">
          <div
            class="ant-btn-group"
            data-v-a1ccd506=""
            style="float: right" >
            <button
              type="button"
              class="ant-btn ant-btn-me"
              @click="openImportExcel()"
            ><span>导入</span></button>
            <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent(),router.push('/system/home/welcome')"><span>退出</span></button>
        </div>
        <div style="clear:both;margin-top: 10%" />
        <div>
          <div style="float: right; margin-left: 10px">
            <a-button @click="pageReload">
              <SyncOutlined :style="{ fontSize: '14px' }" />
            </a-button>
          </div>
          <div style="float: right; position: relative">
            <a-select v-model:value="pageParameter.searchConditon.requirement" style="width: 130px;" class="special_select">
              <template v-for="item in searchConditonList">
                <a-select-option :value="item.dataIndex">
                  {{ item.title }}
                </a-select-option>
              </template>
            </a-select>
            <a-input-search placeholder="" v-model:value="pageParameter.searchConditon.value" @search="pageReload" style="width: 200px; border-radius: 4px" />
          </div>
        </div>
      </div>
    </div>
      <Edit
        @save="saveData"
        @register="registerEditPage"
      />
      <ImprotExcel
        @save="importData"
        @register="registerImportPage"
      />
    </div>
    <div class="app-container">
      <BasicTable
        v-if="activeKey"
        :class="'a-table-font-size-12'"
        :scroll="{ y: windowHeight }"
        @register="registerTable"
      >
      </BasicTable>
    </div>
  </div>
</template>
<script setup lang="ts">
import { BasicTable, useTable, TableAction } from '/@/components/Table'
import Edit from './popup/edit.vue'
import ImprotExcel from './popup/improtExcel.vue'
import { useModal } from '/@/components/Modal'
import { onMounted, reactive, ref, toRaw } from 'vue'
import { currencySaveApi, delCurrency, findAll } from '/@/api/record/system/currency'
import { getThisIndexImg } from "/@/api/task-api/tast-bus-api";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import { Select as ASelect } from 'ant-design-vue';
import { SyncOutlined} from '@ant-design/icons-vue';

const ASelectOption = ASelect.Option;
const CrudApi = {
  list: findAll,
  columns: [
    {
      title: 'ID',
      dataIndex: 'id',
      defaultHidden: true,
      ellipsis: true
    },
    {
      title: '国际货币名称',
      dataIndex: 'currencyZhCnName',
      ellipsis: true,
      align: 'left',
    },
    {
      title: '货币单位',
      dataIndex: 'countryUnit',
      ellipsis: true,width:200
    },
    {
      title: '国际代码',
      dataIndex: 'abbreviation',
      ellipsis: true,width:200
    },
    {
      title: '换算率',
      dataIndex: 'fractionalCurrency',
      ellipsis: true,align: 'left',
    },

  ]
}
const { closeCurrent } =useTabs();
const searchConditonList = ref([{dataIndex:'currencyZhCnName',title:'国际货币名称'}])
const windowHeight = (window.innerHeight - (300))
const pageParameter = reactive({
  searchConditon: {
    requirement: 'currencyZhCnName',
    value: '',
  },
})
// 这是示例组件
const [registerTable, { reload }] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: false,
  pagination: {
    pageSize: 100,
    showSizeChanger: true,
    pageSizeOptions: ['100', '200'],
    showTotal: t => `总共${t}条数据`
  },searchInfo: pageParameter
})
const [registerEditPage, { openModal: openEditPage }] = useModal()
const [registerImportPage, { openModal: openImportPage }] = useModal();
const val = {
  id: '',
  countryName: '',
  currencyZhCnName: '',
  currencyEnName: '',
  currencySymbol: '',
  abbreviation: '',
  fractionalCurrency: '',
  num: ''
}
function openImportExcel() {
  openImportPage(true, {

  });
}

function pageReload() {
  reload({
    searchInfo: pageParameter
  })
}
const openAddPage = (data, index, e) => {
  openEditPage(true, {
    data: val
  })
}
const openEdit = (data, index, e) => {
  openEditPage(true, {
    data: data
  })
}
const del = (id, data) => {
  delCurrency(id)
  reload()
}
const activeKey = ref(true)

const prefixCls = 'list-card'
const cardList = ref({})

async function reloadCurrentPage() {
  const res = await findAll()
  cardList.value = res.items
}
// onMounted(async() => {
//   await reloadCurrentPage()
// })

// 关闭弹框刷新页面
async function saveData(data) {
  await currencySaveApi(data)
  reload()
}
</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>
<style scoped>
.app-container:nth-of-type(1) {
  background-color: #f2f2f2;
  padding: 10px 5px;
  margin: 10px 10px 5px;
}
.app-container:nth-of-type(2) {
  padding: 0px;
  margin: 5px 10px;
  background: #b4c8e3 !important;
}
:deep(.ant-select-selector) {
  border: none !important;
  border-bottom: 1px solid #bdb9b9 !important;
}
:deep(.ant-checkbox) {
  border: 1px solid #2f2a2a;
  border-radius: 4px;
}
:deep(.ant-radio){
  border: 1px solid;
  border-radius: 10px;
}
.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 16px !important;
  padding: 5px 8px !important;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
}
</style>
