<template>
  <div>
  <div class="app-container">

      <div class="app-container-head">
<!--        <img :src="getThisIndexImg()" class="container-head-img" draggable="false" >-->
<!--      <div class="container-head-title" style="margin-left: 20px">
        <b class="noneSpan"><UnorderedListOutlined style="font-size: 26px;" />&nbsp;&nbsp;凭证类别</b>
      </div>-->
        <div class="container-head-title" style="float: left;margin-top: 10px;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 35%;text-align: center;margin-top: 30px;">
          <b class="noneSpan" style="font-size: 36px;">凭证类别</b>
        </div>

      <div class="ant-btn-group" style="float: right">
        <button
          type="button"
          class="ant-btn ant-btn-me"
          ant-click-animating-without-extra-node="false"
          @click="openAddPage()"
        ><span>新增</span></button>
<!--        <button
          type="button"
          class="ant-btn ant-btn-me"
          ant-click-animating-without-extra-node="false"
          @click="editOpen()"
        ><span>修改</span></button>
        <button
          type="button"
          class="ant-btn ant-btn-me"
          ant-click-animating-without-extra-node="false"
          @click="delList()"
        ><span>删除</span></button>-->
<!--        <button
          type="button"
          class="ant-btn ant-btn-me"
          ant-click-animating-without-extra-node="false"
        ><span>打印</span></button>-->
        <button
          type="button"
          class="ant-btn ant-btn-me"
          @click="openExcel()"
        ><span>导入</span></button>
        <button
          type="button"
          class="ant-btn ant-btn-me"
          @click="exportExcel()"
        ><span>导出</span></button>
        <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent()"><span>退出</span></button>
      </div>
    </div>
    <div style="clear: none"/>
    <div style="margin-top: -30px;margin-left: 80px;">
      <div style="display: inline-block;float: left;margin-top: -25px;">
        <AccountPicker theme="three" @reloadTable="dynamicAdReload"/>
      </div>
      <div style="display: inline-block;position:absolute;top:85px;left:110px;font-size: 10px;color:#999999;">共 {{cardList.length}} 条记录</div>
      <div style="float: right; margin-left: 10px">
        <a-button class="ant-btn-me" @click="reloadTable()">
          <SyncOutlined :style="{ fontSize: '14px' }" />
        </a-button>
<!--        <a-popover class="ant-btn-default" placement="bottom">
          <template #content>
              <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MAX'"
                    :style="pageParameter.showRulesSize==='MAX'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortDescendingOutlined/>&nbsp;大号字体&ensp;<CheckOutlined
                v-if="pageParameter.showRulesSize==='MAX'"/></span><br/>
            <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MIN'"
                  :style="pageParameter.showRulesSize==='MIN'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortAscendingOutlined/>&nbsp;小号字体&ensp;<CheckOutlined
              v-if="pageParameter.showRulesSize==='MIN'"/></span>
          </template>
          <a-button>
            <SettingFilled :style="{ fontSize: '14px' }"/>
          </a-button>
        </a-popover>-->
<!--        <a-popover placement="bottom">
          <a-button>
            <PicLeftOutlined :style="{ fontSize: '14px' }" />
          </a-button>
        </a-popover>

        <a-button>
          <EditFilled :style="{ fontSize: '14px' }" />
        </a-button>-->

<!--        <a-button @click="activeKey=!activeKey">
          <PieChartFilled :style="{ fontSize: '14px' }" />
        </a-button>-->
<!--        <a-button>
          <FilterFilled :style="{ fontSize: '14px' }" />
        </a-button>-->
      </div>
      <div style="float: right; position: relative">
        <!-- 搜索 -->
<!--        <a-input-search
          placeholder=""
          style="width: 200px; border-radius: 4px"
          @search="onSearch"
        />-->
      </div>
    </div>
    <div style="clear:both"/>
  </div>
  <div class="app-container">
    <Edit
      @save="saveData"
      @register="registerEditPage"
    />
    <Excel
      @save="saveExcel"
      @register="registerExcelPage"
    />
    <BasicTable
      v-if="activeKey"
      :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
      :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
      @row-click="condClick"
      @register="registerTable"
    >
<!--      <template #action="{ record }">

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
      </template>-->

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
                        {{ item.voucherTypeCode }}
                      </div>
                      <div style="font-size: 24px;margin-top: 20px;">
                        {{ item.voucherTypeName }}
                      </div>
                      <div style="float: right;margin-top: -20px;">
                        <a-popover placement="bottom">
                          <a-button style="padding: 0px 4px; height: 20px;margin-right: 10px;">
                            <EllipsisOutlined />
                          </a-button>
                          <template #content>
                            <p style="cursor: pointer" class="p_specifics" @click="openEdit(item)"><FormOutlined /> 修改</p>
                            <p style="cursor: pointer" class="p_specifics" @click="del(item)"><DeleteOutlined /> 删除</p>
                          </template>
                        </a-popover>
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
  </div>
</template>
<script setup="props, {emit}" lang="ts">
import {
  findVoucherTypeAll,
  deleteVoucherType,
  saveVoucherType,
  excelVoucherType, findAccountByCode
} from '/@/api/record/system/voucher-type'
import {getCurrentAccountName, getThisIndexImg} from '/@/api/task-api/tast-bus-api';
import { BasicTable, useTable } from '/@/components/Table'
import Edit from './popup/edit.vue'
import Excel from './popup/excel.vue'
import { useModal } from '/@/components/Modal'
import {onMounted, reactive, ref} from 'vue'
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
  FilterFilled,
  SortDescendingOutlined,
  CheckOutlined,
  SortAscendingOutlined,
  UnorderedListOutlined,
  EllipsisOutlined,ProfileOutlined
} from '@ant-design/icons-vue'
import {
  Select as ASelect, Input as AInput, Popover as APopover,
  List as AList, Row as ARow, Card as ACard, Col as ACol, message
} from "ant-design-vue"
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker-DATA.vue";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useMessage} from "/@/hooks/web/useMessage";
//import {aoaToSheetXlsx} from "/@/components/Excel";
const aoaToSheetXlsx=null
const ASelectOption=ASelect.Option
const AInputSearch=AInput.Search
const AListItem=AList.Item

const {closeCurrent} = useTabs(router);

const {
  createConfirm,
  createSuccessModal,
  createWarningModal
} = useMessage()

const dynamicTenantId = ref(getCurrentAccountName(true))
const CrudApi = {
  list: useRouteApi(findVoucherTypeAll, {schemaName: dynamicTenantId}),
  columns: [
    {
      title: '编码',
      dataIndex: 'voucherNum',
      ellipsis: true
    },
    {
      title: '类别字简称',
      dataIndex: 'voucherTypeCode',
      ellipsis: true
    },
    {
      title: '类别字名称',
      dataIndex: 'voucherTypeName',
      ellipsis: true
    }
  ],
  editData: {
    id: '',
    voucherNum: '',
    voucherTypeCode: '',
    voucherTypeName: ''
  }
}
// 这是示例组件
const [registerTable, { reload,getPaginationRef,setTableData,setPagination }] = useTable({
  api: useRouteApi(CrudApi.list, {schemaName: dynamicTenantId}),
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: false,
  pagination: false,
  /*actionColumn: {
    width: 160,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' }
  }*/
})
const [registerEditPage, { openModal: openEditPage }] = useModal()
const val = {
  id: null,
  voucherNum: '',
  voucherTypeCode: '',
  voucherTypeName: ''
}
const condClick = (data:any,index:any, e:any) => {
  if (e.target.cellIndex == 1) {
    openEditPage(true, {
      data: data,
      dynamicTenantId: dynamicTenantId.value,
      isState: '1'
    })
  }
}
const openAddPage = () => {
  openEditPage(true, {
    data: val,
    dynamicTenantId: dynamicTenantId.value,
    isState: '0'
  })
}
const openEdit = (data:any) => {
  openEditPage(true, {
    data: data,
    dynamicTenantId: dynamicTenantId.value,
    isState: '1'
  })
}
const del = async(data:any) => {
  const account:any = await useRouteApi(findAccountByCode,{schemaName: dynamicTenantId})(data.voucherTypeCode)
  if (account>0){
    createWarningModal({
      iconType: 'warning',
      title: '删除',
      content: '凭证类别已做凭证不能进行删除！'
    })
    return false
  } else {
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据将不能恢复，你确认要删除吗?',
      onOk: async () => {
        await useRouteApi(deleteVoucherType, {schemaName: dynamicTenantId})(data)
        checkRow.value = []
        state.selectedRowKeys = []
        message.success('删除成功！')
        await reloadTable()
        return true
      },
      onCancel: () => {
        return false
      }
    })
  }
}
const activeKey = ref(false)

const cardList:any = ref({})
async function reloadCurrentPage() {
  const res = await useRouteApi(findVoucherTypeAll, {schemaName: dynamicTenantId})({})
  cardList.value = res.items
  // console.log(cardList.value)
}
onMounted(async() => {
  await reloadCurrentPage()
})
async function saveData(data:any) {
  await useRouteApi(saveVoucherType, {schemaName: dynamicTenantId})(data)
  await reloadCurrentPage()
  await reloadTable()
}

//选中内容
const state = reactive<{
  selectedRowKeys: [];
  loading: boolean;
}>({
  selectedRowKeys: [], // Check here to configure the default column
  loading: false,
});
const checkRow: any = ref([])
const onSelectChange = (selectedRowKeys, row) => {
  // console.log('selectedRowKeys changed: ', row);
  state.selectedRowKeys = selectedRowKeys;
  checkRow.value = row
};
const editOpen = () => {
  if (checkRow.value.length == 1) {
    openEditPage(true, {
      data: checkRow.value[0],
      dynamicTenantId: dynamicTenantId.value
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '编辑',
      content: '请选择一条进行编辑！'
    })
  }
}

async function delList() {
  if (checkRow.value.length > 0) {
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据将不能恢复，你确认要删除吗?',
      onOk: async () => {
        for (let i = 0; i < checkRow.value.length; i++) {
          const item = checkRow.value[i]
          await useRouteApi(deleteVoucherType, {schemaName: dynamicTenantId})(item)
        }
        checkRow.value = []
        state.selectedRowKeys = []
        message.success('删除成功！')
        await reloadTable()
      },
      onCancel: () => {
        return false
      }
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '删除',
      content: '请选择需要删除的内容！'
    })
  }
}

async function reloadTable() {
  if (activeKey.value == true) {
    await reload()
  }
  await reloadCurrentPage()
}

function onSearch(){}

//导入Excel
const [registerExcelPage, { openModal: openExcelPage }] = useModal()
const openExcel = () => {
  openExcelPage(true, {
    data: {},
    dynamicTenantId: dynamicTenantId.value
  })
}

async function saveExcel(data:any) {
  await useRouteApi(excelVoucherType, {schemaName: dynamicTenantId})(data)
  await reloadCurrentPage()
  if (activeKey.value == true) {
    await reload()
  }
}

//文件导出
async function exportExcel() {
  // debugger
  const arrHeader = ['类别字简称','类别字名称'];
  const columnList = ['voucherTypeCode','voucherTypeName']
  const arrData = cardList.value.map((item) => columnList.map(column=>item[column]));
  // console.log(arrHeader)
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: arrData,
    header: arrHeader,
    filename: '凭证类别.xlsx',
  });
}

const pageParameter:any = reactive({
  showRulesSize: 'MIN',
  companyCode: '100',
  companyName: '北京希格科技',
  ifUnit: false,
})
const dynamicAdReload = async (obj) =>{
  dynamicTenantId.value = obj.accountMode
  await reloadCurrentPage()
  if (activeKey.value == true) {
    let data: any = {}
    data.page = getPaginationRef().current
    data.size = getPaginationRef().pageSize
    let res: any = await useRouteApi(findVoucherTypeAll, {schemaName: obj.accountMode})(data)
    setTableData([]) // 清空可能残留的数据
    setTableData(res.items)
    // 底部分页信息
    dynamicTenantId.value = obj.accountMode
    pageParameter.thisAdInfo = {}
    pageParameter.thisAdInfo = obj
    pageParameter.total = res.total
    setPagination({total: res.total})
  }
}
</script>
<style src="./global-menu-index.less" lang="less" scoped></style>
<style scoped lang="less">
:deep(.ant-card-body) {
  padding: 16px;
  border-left: 2px solid rgb(1, 143, 251);
  box-shadow: rgb(72 113 140) -3px 1px 7px -1px;
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

.app-container:nth-of-type(1) {
  background-color: #f2f2f2;
  padding: 10px 5px;
  margin: 10px 10px 5px;
}

.app-container:nth-of-type(2) {
  padding: 0px;
  margin: 5px 10px;
  background-color: #b4c8e3 !important;
  background-image: url("/@/assets/images/homes/bg-pattern.png");
  background-repeat: no-repeat;
  background-position: 50% 50%;
}

:deep(.ant-input),:deep(.ant-select),:deep(.ant-btn){
  border: 1px solid #c9c9c9;
}
</style>
