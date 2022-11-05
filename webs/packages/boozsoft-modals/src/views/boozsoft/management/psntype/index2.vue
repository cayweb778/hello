<template>
  <div>
  <div class="app-container">
    <div class="app-container-head">
      <!--      <img src="/img/menu/index_dept.png" class="container-head-img">-->
      <div class="container-head-title" style="margin-left: 20px">
        <b class="noneSpan"><TeamOutlined style="font-size: 26px;" />&nbsp;&nbsp;人员分类</b>
      </div>
      <div class="ant-btn-group" style="float: right">
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
          @click="editOpen()"
        ><span>修改</span></button>
        <button
          type="button"
          class="ant-btn ant-btn-me"
          ant-click-animating-without-extra-node="false"
          @click="delList()"
        ><span>删除</span></button>
        <button
          type="button"
          class="ant-btn ant-btn-me"
          @click="openAssign()"
        ><span>分配</span></button>
<!--        <a-popover placement="bottom">
          <button
            type="button"
            class="ant-btn ant-btn-me"
          ><span>分配</span></button>
          <template #content>
            <span @click="openAssign()" class="group-btn-span-special2 p_specifics">分配</span><br/>
            <span @click="openUnAssign()" class="group-btn-span-special2 p_specifics">取消分配</span>
          </template>
        </a-popover>-->
<!--        <button
          type="button"
          class="ant-btn ant-btn-me"
          ant-click-animating-without-extra-node="false"
        ><span>打印</span></button>-->
        <button
          type="button"
          class="ant-btn ant-btn-me"
          @click="openExcel"
        ><span>导入</span></button>
        <button
          type="button"
          class="ant-btn ant-btn-me"
        ><span>导出</span></button>
        <button
          type="button"
          class="ant-btn ant-btn-me"
          @click="closeCurrent()"
        ><span>退出</span></button>
      </div>
    </div>

    <div style="float: left; margin-left: 10px;line-height: 30px;">共 {{cardList.length}} 条记录</div>
    <div class="app-container-neck">
<!--        <div style="float: left; line-height: 30px">
          <span style="font-size: 14px; color: black">共{{ cardList.length }}条</span>
        </div>-->
        <div style="float: right; margin-left: 10px">
          <a-button @click="reloadCurrentPage()">
            <SyncOutlined :style="{ fontSize: '14px' }" />
          </a-button>
<!--          <a-popover class="ant-btn-default" placement="bottom">
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
<!--          <a-popover placement="bottom">
            <a-button>
              <PicLeftOutlined :style="{ fontSize: '14px' }" />
            </a-button>
          </a-popover>

          <a-button>
            <EditFilled :style="{ fontSize: '14px' }" />
          </a-button>-->

<!--          <a-button @click="activeKey=!activeKey">
            <PieChartFilled :style="{ fontSize: '14px' }" />
          </a-button>-->
<!--          <a-button>
            <FilterFilled :style="{ fontSize: '14px' }" />
          </a-button>-->
        </div>
        <div style="float: right; position: relative">
<!--          <label style="font-size: 14px">检索条件：</label>-->
          <a-select v-model:value="formItems.selectType" style="width: 120px;font-size: 12px;" class="special_select">
            <a-select-option style="font-size: 12px;" value="1">类别编码</a-select-option>
            <a-select-option style="font-size: 12px;" value="2">类别名称</a-select-option>
          </a-select>
          <!-- 搜索 -->
          <a-input-search
            placeholder=""
            style="width: 200px; border-radius: 4px"
            @search="onSearch"
          />
        </div>
      </div>
    </div>

    <div class="app-container">
    <Edit
      @save="saveData"
      @register="registerEditPage"
    />
    <Excel @save="saveExcel" @register="registerExcelPage"/>
    <Assign @save="saveAssign" @register="registerAssignPage"/>
    <UnAssign @save="saveUnAssign" @register="registerUnAssignPage"/>
    <BasicTable
      v-if="activeKey"
      :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
      @row-click="condClick"
      :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
      style="width: 800px;"
      @register="registerTable"
      :loading="loadMark"
    >
      <template #action="{ record }">

        <div>
          <a-popover placement="bottom">
            <a-button style="padding: 0px 4px; height: 20px;">
              <CaretDownFilled />
            </a-button>
            <!--            <template #content>
                          <p style="cursor: pointer" @click="openEdit(record)"><FormOutlined /> 编辑</p>
                          <p style="cursor: pointer" @click="del(record)"><DeleteOutlined /> 删除</p>
                        </template>-->
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
        <a-list :style="{height: treeHeight,overflowY: 'scroll',overflowX: 'hidden'}">
          <a-row :gutter="16">
            <template
              v-for="(item,index) in cardList"
              :key="index"
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
                        {{ item.psnTypeCode }}
                      </div>
                      <div style="font-size: 24px;margin-top: 20px;">
                        {{ item.psnTypeName }}
                      </div>
                      <div v-if="item.psnTypeCode!='9999'" style="float: right;margin-top: -20px;">
                        <a-popover placement="bottom" v-if="item.sysFlag!='1'">
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
  psnTypeFindAll,
  deletePsnType,
  savePsnType,
  excelPsnType
} from '/@/api/record/system/group-psn-type'
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
  message
} from "ant-design-vue"

const ARangePicker = ADatePicker.RangePicker
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ARadioGroup = ARadio.Group
const AListItem = AList.Item
const AMenuItem = AMenu.Item
import {BasicTable, useTable} from '/@/components/Table'
import Edit from './popup/edit.vue'
import Excel from './popup/excel.vue'
import Assign from './popup/assign.vue'
import UnAssign from './popup/un-assign.vue'
import {useModal} from '/@/components/Modal'
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
  TeamOutlined,
  EllipsisOutlined
} from '@ant-design/icons-vue'
import {psnFindAll} from "/@/api/record/system/group-psn";
import {useMessage} from "/@/hooks/web/useMessage";
import {useUserStore} from "/@/store/modules/user";
import {
  deleteById,
  findByPsnTypeIdAndAccId, findByUniqueCode,
  saveList
} from "/@/api/record/system/group-psn-type-account";
import {findDataBase} from "/@/api/record/system/account";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {acceptSave} from "/@/api/record/system/psn-type";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";

const {closeCurrent} = useTabs(router);

const formItems = ref({
  selectType: '1'
})

const {
  createConfirm,
  createWarningModal
} = useMessage()

const CrudApi = {
  list: psnTypeFindAll,
  columns: [
    {
      title: 'ID',
      dataIndex: 'id',
      defaultHidden: true,
      ellipsis: true
    },
    // { title: '唯一标识', dataIndex: 'uniqueCode', ellipsis: true },
    {
      title: '类别编码',
      dataIndex: 'psnTypeCode',
      width: '30%',
      ellipsis: true
    },
    {
      title: '类别名称',
      dataIndex: 'psnTypeName',
      ellipsis: true,
      align: 'left',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return {style: {'text-align': 'left', '-webkit-line-clamp': 2}} // return 想要设置的样式
      },
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
const [registerTable, { reload,getDataSource,setTableData,setColumns,getColumns,getPaginationRef,setPagination }] = useTable({
  api: CrudApi.list,
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
const [registerEditPage, {openModal: openEditPage}] = useModal()
const [registerExcelPage, {openModal: openExcelPage}] = useModal()
const val = {
  id: null,
  uniqueCode: '',
  psnTypeCode: '',
  psnTypeName: ''
}
const treeHeight = (document.documentElement.clientHeight - 180) + 'px'
const condClick = (data: any, index: any, e: any) => {
  /*if (e.target.cellIndex == 1) {
    openEditPage(true, {
      data: data
    })
  }*/
}
const openAddPage = () => {
  openEditPage(true, {
    data: val,
    isState: '0'
  })
}
const openEdit = (data:any) => {
  openEditPage(true, {
    data: data,
    isState: '1'
  })
}
const del = async(data:any) => {
  /*await deletePsnType(data)
  message.success('删除成功！')
  // alert('删除成功！')
  await reload()*/
  const res = await findByUniqueCode(data.uniqueCode)
  if (res.length > 0){
    createWarningModal({
      iconType: 'warning',
      title: '删除',
      content: '人员类别已分配不允许删除！'
    })
    return false
  }
  const psnList = await psnFindAll({})
  for (let a = 0; a < psnList.items.length; a++) {
    const psn = psnList.items[a]
    if (data.uniqueCode == '9999') {
      createWarningModal({
        iconType: 'warning',
        title: '删除',
        content: '编码为“9999”的人员类别为系统预制类别不允许删除！'
      })
      return false
    }
    if (psn.uniquePsnType == data.uniqueCode) {
      createWarningModal({
        iconType: 'warning',
        title: '删除',
        content: '<span style="font-weight: bold;color: red;">[' + data.psnTypeName + ']</span>人员类别中经已存在人员，不能进行删除操作！'
      })
      return false
    }
  }
  createConfirm({
    iconType: 'error',
    title: '警告',
    content: '删除后数据将不能恢复，你确认要删除吗?',
    onOk: async () => {
      await deletePsnType(data)
      message.success('删除成功！')
      await reloadCurrentPage()
    },
    onCancel: () => {
      return false
    }
  })
}
const activeKey = ref(false)

const cardList:any = ref({})
async function reloadCurrentPage() {
  const res = await psnTypeFindAll()
  cardList.value = res.items
  // console.log(cardList.value)
}
onMounted(async() => {
  await reloadCurrentPage()
})
async function saveData(data:any) {
  await savePsnType(data)
  await reloadCurrentPage()
  if (activeKey.value == true) {
    await reload()
  }
}

function onSearch() {
}

const pageParameter: any = reactive({
  showRulesSize: 'MIN'
})

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
      isState: '1'
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
    const psnList = await psnFindAll({})
    for (let i = 0; i < checkRow.value.length; i++) {
      const item = checkRow.value[i]
      for (let a = 0; a < psnList.items.length; a++) {
        const psn = psnList.items[a]
        if (item.uniqueCode == '9999') {
          createWarningModal({
            iconType: 'warning',
            title: '删除',
            content: '编码为“9999”的人员类别为系统预制类别不允许删除！'
          })
          return false
        }
        if (psn.uniquePsnType == item.uniqueCode) {
          createWarningModal({
            iconType: 'warning',
            title: '删除',
            content: '<span style="font-weight: bold;color: red;">[' + item.psnTypeName + ']</span>人员类别中经已存在人员，不能进行删除操作！'
          })
          return false
        }
      }
    }
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据将不能恢复，你确认要删除吗?',
      onOk: async () => {
        for (let i = 0; i < checkRow.value.length; i++) {
          const item = checkRow.value[i]
          await deletePsnType(item)
        }
        message.success('删除成功！')
        await reloadCurrentPage()
        if (activeKey.value == true) {
          await reload()
        }
        checkRow.value = []
        state.selectedRowKeys = []
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

//导入
const openExcel = () => {
  openExcelPage(true, {})
}

async function saveExcel(data) {
  await excelPsnType(data)
  message.success('导入成功！')
  await reloadCurrentPage()
  if (activeKey.value == true) {
    await reload()
    checkRow.value = []
    state.selectedRowKeys = []
  }
}

//分配
const [registerAssignPage, {openModal: openAssignPage}] = useModal()
const [registerUnAssignPage, {openModal: openUnAssignPage}] = useModal()
const openAssign = () => {
  openAssignPage(true, {})
  /*if (checkRow.value.length > 0) {
    openAssignPage(true, {})
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '分配',
      content: '请选择需要分配的内容！'
    })
  }*/
}

const loadMark = ref(false);
async function saveAssign(data) {
  /*let list: any = []
  let dateTime = new_Date()
  let dataBase: any
  loadMark.value = true
  if (data.addAccount) {
    dataBase = await findDataBase(data.table[0].accId, data.iyear)
  }
  for (const res of checkRow.value) {
    if (data.addAccount) {
      let item: any = {}
      item.psnTypeId = res.uniqueCode
      item.accId = data.table[0].accId
      item.iyear = data.iyear
      item.assignUser = useUserStore().getUserInfo['name']
      item.assignDate = dateTime
      item.flag = '1'
      item.acceptUser = useUserStore().getUserInfo['name']
      item.acceptDate = dateTime
      const assignList = await findByPsnTypeIdAndAccId({
        psnTypeId: item.psnTypeId,
        accId: item.accId
      })
      if (assignList.length == 0) {
        list.push(item)
        await useRouteApi(acceptSave, {schemaName: dataBase.accountMode})(res)
      }
    } else {
      for (const acc of data.table) {
        let item: any = {}
        item.psnTypeId = res.uniqueCode
        item.accId = acc.accId
        item.iyear = data.iyear
        item.assignUser = useUserStore().getUserInfo['name']
        item.assignDate = dateTime
        item.flag = '0'
        item.acceptUser = ''
        item.acceptDate = ''
        const assignList = await findByPsnTypeIdAndAccId({
          psnTypeId: item.psnTypeId,
          accId: item.accId
        })
        if (assignList.length == 0) {
          list.push(item)
        }
      }
    }
  }
  await saveList(list)
  loadMark.value = false
  message.success('分配成功！')
  checkRow.value = []
  state.selectedRowKeys = []*/
}

//取消分配
const openUnAssign = () => {
  openUnAssignPage(true, {})
}

async function saveUnAssign(data) {
  console.log(data)
  loadMark.value = true
  for (const acc of data.table) {
    await deleteById(acc.id)
  }
  loadMark.value = false
  message.success('取消分配成功！')
  checkRow.value = []
  state.selectedRowKeys = []
}

//获取当年月日
function new_Date() {
  let dateTime
  let yy = new Date().getFullYear()
  let mm = new Date().getMonth() + 1
  let dd = new Date().getDate()
  let hh = new Date().getHours()
  let mf = new Date().getMinutes() < 10 ? '0' + new Date().getMinutes() : new Date().getMinutes()
  let ss = new Date().getSeconds() < 10 ? '0' + new Date().getSeconds() : new Date().getSeconds()
  dateTime = yy + '-' + mm + '-' + dd + ' ' + hh + ':' + mf + ':' + ss
  console.log(dateTime)
  return dateTime
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
</style>
