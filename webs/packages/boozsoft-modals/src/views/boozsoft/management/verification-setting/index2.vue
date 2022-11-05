<template>
  <div>
    <div class="app-container">

      <div class="app-container-head">
        <div class="container-head-title">
          <UnorderedListOutlined style="color: #0096c7"/>
          <b class="noneSpan">发票验真参数</b>
        </div>
        <div
          class="ant-btn-group"
          data-v-a1ccd506=""
          style="float: right"
        >
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="editOpen()"
            v-if="isGroupFlg === '1'"
          ><span>修改</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="closeCurrent()"
          ><span>退出</span></button>
        </div>
      </div>

      <div class="app-container-neck">

        <div style="float: right; margin-left: 10px">
          <a-button class="ant-btn-me">
            <SyncOutlined :style="{ fontSize: '14px' }" />
          </a-button>
          <a-popover class="ant-btn-default" placement="bottom">
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
          </a-popover>

        </div>
        <div style="float: right; position: relative">
        </div>
      </div>
    </div>
    <div class="app-container">
      <Edit @save="saveData" @register="registerEditPage"/>
      <Loading :loading="compState.loading" :absolute="compState.absolute" :tip="compState.tip" />
      <div class="bg-white" style="width: 70%; float: left;margin-left: 5px;margin-top: -0.5px;">
      <BasicTable
        v-if="activeKey"
        :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
        @row-click="condClick"
        :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
        @register="registerTable"
      >
        <template #bsCode="{ record }" class="a-table-money-font-size a-table-font-arial">
            <span style="float: left" >{{ record.bsCode }}</span>
          </template>
        <template #bsName="{ record }" class="a-table-money-font-size a-table-font-arial">
          <span style="float: left" >{{ record.bsName }}</span>
        </template>
      </BasicTable>
      </div>
    </div>
  </div>
</template>
<script setup="props, {emit}" lang="ts">
import {
  findSettModesAll,
  saveSettModes
} from '/@/api/record/group/verification-api'
import {getCurrentAccountName, getThisIndexImg} from "/@/api/task-api/tast-bus-api";
import {BasicTable, useTable} from '/@/components/Table'
import Edit from './popup/edit.vue'
import { useModal } from '/@/components/Modal'
import {onMounted, reactive, ref} from 'vue'
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
  FilterFilled,
  CheckOutlined,
  SortDescendingOutlined,
  SortAscendingOutlined  ,UnorderedListOutlined
} from '@ant-design/icons-vue'
import {
  DatePicker as ADatePicker,
  Select as ASelect,
  Input as AInput,
  Popover as APopover,
  Switch as ASwitch,
  Radio as ARadio,
  Upload as AUpload,
  List as AList,
  Row as ARow,
  Menu as AMenu,
  Dropdown as ADropdown,
  Card as ACard,
  Col as ACol,
  Divider as ADivider,
  message,
  Tag as ATag
} from "ant-design-vue"
import {columnProps} from "ant-design-vue/es/table/interface";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useMessage} from "/@/hooks/web/useMessage";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import { Loading } from '/@/components/Loading';

const ARangePicker = ADatePicker.RangePicker
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ARadioGroup = ARadio.Group
const AListItem = AList.Item
const AMenuItem = AMenu.Item
const {closeCurrent} = useTabs(router);

const {
  createConfirm,
  createWarningModal
} = useMessage()
const compState = reactive({
  absolute: false,
  loading: false,
  tip: '加载中...',
});
function openCompFullLoading() {
  openLoading(false);
}
function openLoading(absolute: boolean) {
  compState.absolute = absolute;
  compState.loading = true;
}
const database = ref('');
const accId = ref('');
const CrudApi = {
  list: findSettModesAll,
  columns: [
    {
      title: 'ID',
      dataIndex: 'id',
      defaultHidden: true,
      ellipsis: true
    },
    {
      title: 'secretId',
      dataIndex: 'secret',
      width: '40%',
      slots: { customRender: 'secret' },

      customHeaderCell: () => {		// 表头加粗
        return {style: {'font-weight':'bold'}} // return 想要设置的样式
      },
      ellipsis: true
    },
    {
      title: 'secretKey',
      dataIndex: 'key',
      width: '40%',
      customHeaderCell: () => {		// 表头加粗
        return {style: {'font-weight':'bold'}} // return 想要设置的样式
      },
      ellipsis: true,
      slots: {customRender: 'key'}
    },
    {
      title: '服务商名称',
      dataIndex: 'groupName',
      width: '30%',
      customHeaderCell: () => {		// 表头加粗
        return {style: {'font-weight':'bold'}} // return 想要设置的样式
      },
      ellipsis: true,
      slots: {customRender: 'groupName'}
    },
  ],
  editData: {
    id: '',
    settModesCode: '',
    settModesName: ''
  }
}

// 这是示例组件
const [registerTable, { reload,getDataSource,setTableData,setColumns,getColumns,getPaginationRef,setPagination }] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: false,
})
const [registerEditPage, { openModal: openEditPage }] = useModal()
const val = {
  id: '',
  settModesCode: '',
  settModesName: '',
  database: '',
}
const condClick = (data:any, e:any) => {

}
const openAddPage = () => {
  val.database = database.value
  openEditPage(true, {
    data: val
  })
}
const openEdit = (data:any) => {
  data.data.database = database.value
  openEditPage(true, {
    data: data
  })
}

const activeKey = ref(true)

const cardList:any = ref({})
async function reloadCurrentPage() {
  reload()
}
onMounted(async() => {
  await reloadCurrentPage()
})

async function saveData(data: any) {
  await useRouteApi(saveSettModes,{schemaName: database})(data)
  await reloadCurrentPage()
  await reload()
  checkRow.value = []
  state.selectedRowKeys = []
}

//选中内容
type Key = columnProps['id'];

const state = reactive<{
  selectedRowKeys: Key[];
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
    checkRow.value[0].isEdit = true
    openEditPage(true, {
      data: checkRow.value[0]
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '编辑',
      content: '请选择一条进行编辑！'
    })
  }
}

function onSearch() {
}

const pageParameter: any = reactive({
  showRulesSize: 'MIN'
})

const totalData = ref('0')
const accNameAll =  ref(null)
const isGroupFlg = ref('1')

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

</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style><style lang='less' scoped ></style>
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
  background: #b4c8e3 !important;
}
</style>
