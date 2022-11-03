<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">
        <div class="container-head-title">
          <b class="noneSpan">承兑汇票期初</b>
        </div>

        <div class="ant-btn-group" data-v-a1ccd506="" style="float: right">
          <a-button class="ant-btn ant-btn-me" @click="openAddPage"><span>新增</span></a-button>
          <a-button class="ant-btn ant-btn-me" @click="editOpen()"><span>修改</span></a-button>
          <a-button class="ant-btn ant-btn-me" @click="delList()"><span>删除</span></a-button>
          <a-popover placement="bottom">
            <a-button class="ant-btn ant-btn-me"><span>审核</span></a-button>
            <template #content>
              <span class="group-btn-span-special2 p_specifics" @click="approved()">审核</span><br/>
              <span class="group-btn-span-special2 p_specifics" @click="unApproved()">取消审核</span>
            </template>
          </a-popover>
          <a-button class="ant-btn ant-btn-me" @click="exportExcel()"><span>导出</span></a-button>
          <a-button class="ant-btn ant-btn-me" @click="closeCurrent(),router.push('/home/welcome')">
            <span>退出</span></a-button>
        </div>
      </div>
      <div style="clear: none;"></div>
      <!--    <UnitChange v-if="!defaultPage" @reloadTable="dynamicAdReload" v-model="pageParameter"/>-->
      <div style="display: inline-block;float: left;margin-left: 1%;font-size: 14px;">
        <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
      </div>

      <div class="app-container-neck">
        <div style="float: right; margin-left: 10px">
          <a-button @click="reload()">
            <SyncOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
          <a-popover class="ant-btn-default" placement="bottom">
            <template #content>
              <a-popconfirm
                ok-text="确定"
                cancel-text="放弃"
                @confirm="confirm"
                @cancel="cancel">
                <template #icon><b>栏目设置</b><br></template>
                <template #title>
                  <div style="width:665px">
                    <a-table bordered :data-source="dynamicColumnData" :columns="dynamicColumns"
                             childrenColumnName="children" :pagination="false"
                             style="max-height: 500px;overflow-y: auto" class="lanmu-table">
                      <template #checkBox="{ text, record }">
                        <a-checkbox v-model:checked="record.check" :disabled="record.isFixed"/>
                      </template>
                      <template #widthInput="{ text, record }">
                        <div class="editable-cell">
                          <div v-if="editableData[record.key]" class="editable-cell-input-wrapper">
                            <a-input type="number" v-model:value="editableData[record.key].width"
                                     @pressEnter="save(record.key,record.min,record.max)"
                                     style="width: 80px"/>
                            <check-outlined class="editable-cell-icon-check"
                                            @click="save(record.key,record.min,record.max)"/>
                          </div>
                          <div v-else class="editable-cell-text-wrapper">
                            {{ text || ' ' }}
                            <edit-outlined class="editable-cell-icon" @click="edit(record.key)"/>
                            <span style="float: right;">{{ record.min + '~' + record.max }}</span>
                          </div>
                        </div>
                      </template>
                      <template #nameInput="{ text, record }">
                        <div class="editable-cell">
                          <div v-if="editableData[record.key]" class="editable-cell-input-wrapper">
                            <a-input type="text" v-model:value="editableData[record.key].nameNew"
                                     @pressEnter="saveName(record.key)" style="width: 100px"/>
                            <check-outlined class="editable-cell-icon-check"
                                            @click="saveName(record.key)"/>
                          </div>
                          <div v-else class="editable-cell-text-wrapper">
                            {{ text || ' ' }}
                            <edit-outlined class="editable-cell-icon" @click="edit(record.key)"/>
                          </div>
                        </div>
                      </template>
                      <template #alignRadio="{ text, record }">
                        <a-radio-group default-value="a" size="small" v-model:value="record.align"
                                       :disabled="record.align==''">
                          <a-radio-button value="left">
                            左
                          </a-radio-button>
                          <a-radio-button value="center">
                            中
                          </a-radio-button>
                          <a-radio-button value="right">
                            右
                          </a-radio-button>
                        </a-radio-group>
                      </template>
                    </a-table>
                  </div>
                </template>
                <a-button style="width: 120px;margin-bottom: 2px">栏目设置</a-button>
              </a-popconfirm>
              <br/>
              <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MAX'"
                    :style="pageParameter.showRulesSize==='MAX'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortDescendingOutlined/>&nbsp;大号字体&ensp;<CheckOutlined
                v-if="pageParameter.showRulesSize==='MAX'"/></span><br/>
              <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MIN'"
                    :style="pageParameter.showRulesSize==='MIN'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortAscendingOutlined/>&nbsp;小号字体&ensp;<CheckOutlined
                v-if="pageParameter.showRulesSize==='MIN'"/></span>
            </template>
            <!--            <template #title>
                          <b>设置表格字号</b></template>-->
            <a-button>
              <SettingFilled :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>
          <!--        <a-popover placement="bottom">
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
                  </a-button>-->
        </div>
        <div style="float: right; position: relative">
          <!--        <label style="font-size: 14px">检索条件：</label>-->
          <a-select v-model:value="formItems.selectType" style="width: 140px;font-size: 12px;"
                    class="special_select">
            <a-select-option style="font-size: 12px;" value="billCode">票据编号</a-select-option>
            <a-select-option style="font-size: 12px;" value="chupiaoUnit">出票单位</a-select-option>
            <a-select-option style="font-size: 12px;" value="billRemarks">摘要</a-select-option>
          </a-select>
          <!-- 搜索 -->
          <a-input-search
            v-model:value="formItems.selectValue"
            placeholder=""
            style="width: 200px; border-radius: 4px"
            @search="onSearch"
          />
        </div>
      </div>

    </div>
    <div class="app-container">
      <Edit @save="saveData" @register="registerEditPage"/>
      <BasicTable
        :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
        :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
        @register="registerTable">

        <template #icheck="{ record }">
          <span>
            <a-tag :color="record.icheck === '1' ? 'green' : 'volcano'">
              {{ record.icheck === '1' ? '已审核' : '未审核' }}
            </a-tag>
          </span>
        </template>
        <template #flag="{ record }">{{ formatFlag(record.flag, record.daoqiDate) }}</template>
        <template #fangxiang="{ record }">{{ record.fangxiang == '1' ? '收款' : '付款' }}</template>
        <template #billCode="{ record }">
          <span style="color: blue" @click="condClick(record)">{{ record.billCode }}</span>
        </template>
        <template #money="{ record }">{{ toThousandFilter(record.money) }}</template>
        <template #chupiaoUnit="{ record }">{{ formatChupiaoUnit(record) }}</template>

<!--        <template #action="{ record }">
          <div>
            <a-popover placement="bottom">
              <a-button style="padding: 0px 4px; height: 20px;">
                <CaretDownFilled/>
              </a-button>
              <template #content>
                <p style="cursor: pointer" @click="condClick(record)">
                  <FormOutlined/>
                  编辑
                </p>
                <p style="cursor: pointer" @click="del(record)">
                  <DeleteOutlined/>
                  删除
                </p>
              </template>
            </a-popover>
          </div>
        </template>-->
      </BasicTable>

    </div>
  </div>
</template>
<script setup lang="ts">
import {
  deleteAcceptBill,
  saveAcceptBill, findAcceptBillByQichu
} from '/@/api/record/system/accept-bill'
import {BasicTable, useTable} from '/@/components/Table'
import Edit from './popup/edit.vue'
import {useModal} from '/@/components/Modal'
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
  EditOutlined
} from '@ant-design/icons-vue'
import {onMounted, reactive, ref} from 'vue'

import {useMessage} from "/@/hooks/web/useMessage";
import {getThisAdInfoData} from "/@/api/record/system/financial-settings";
import {getCurrentAccountName, getThisIndexImg} from "/@/api/task-api/tast-bus-api";
import {aoaToSheetXlsx} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findDataBase} from "/@/api/record/system/account";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {
  Select as ASelect, Input as AInput, Popover as APopover, Tag as ATag, Radio as ARadio,
  Table as ATable, Checkbox as ACheckbox, Button as AButton,
  Popconfirm as APopconfirm,
  message,
} from 'ant-design-vue'

const ARadioButton = ARadio.Button
const ARadioGroup = ARadio.Group
const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option

const {closeCurrent} = useTabs(router);

const {
  createSuccessModal,
  createErrorModal
} = useMessage()

const formItems = ref({
  selectType: 'billCode',
  selectValue: ''
})

const columns = [
  {
    title: '审核状态',
    dataIndex: 'icheck',
    ellipsis: true,
    slots: {customRender: 'icheck'}
  },
  {
    title: '状态',
    dataIndex: 'flag',
    ellipsis: true,
    slots: {customRender: 'flag'}
  },
  {
    title: '收到日期',
    dataIndex: 'receiveDate',
    ellipsis: true
  },
  {
    title: '出票日期',
    dataIndex: 'chupiaoDate',
    ellipsis: true
  },
  {
    title: '到期日期',
    dataIndex: 'daoqiDate',
    ellipsis: true
  },
  {
    title: '收支方向',
    dataIndex: 'fangxiang',
    ellipsis: true,
    slots: {customRender: 'fangxiang'}
  },
  {
    title: '票据编号',
    dataIndex: 'billCode',
    ellipsis: true,
    slots: {customRender: 'billCode'}
  },
  {
    title: '出票单位',
    dataIndex: 'chupiaoUnit',
    ellipsis: true,
    slots: {customRender: 'chupiaoUnit'}
  },
  {
    title: '票据摘要',
    dataIndex: 'billRemarks',
    ellipsis: true
  },
  {
    title: '金额',
    dataIndex: 'money',
    ellipsis: true,
    slots: {customRender: 'money'}
  },
  {
    title: '付款银行',
    dataIndex: 'payBank',
    ellipsis: true
  },
]
const dynamicTenantId = ref(getCurrentAccountName(true))
// 这是示例组件
const [registerTable, {
  reload,
  getDataSource,
  setTableData,
  setColumns,
  getColumns,
  getPaginationRef,
  setPagination
}] = useTable({
  api: useRouteApi(findAcceptBillByQichu, {schemaName: dynamicTenantId}),
  columns: columns,
  bordered: true,
  showIndexColumn: false,
  /*actionColumn: {
    width: 100,
    title: '操作',
    dataIndex: 'action',
    slots: {customRender: 'action'}
  }*/
})

function formatFlag(flag, daoqiDate) {
  let str = '未到期'
  if (flag == '0') {
    let today = FormatDate(new Date());
    if (today > daoqiDate) {
      str = '已到期'
    }
  }
  if (flag == '1') {
    str = '已贴现'
  }
  if (flag == '2') {
    str = '已背书'
  }
  if (flag == '3') {
    str = '已转出'
  }
  if (flag == '4') {
    str = '已结算'
  }
  if (flag == '-1') {
    str = '贴现锁定'
  }
  if (flag == '-2') {
    str = '背书锁定'
  }
  return str;
}

function FormatDate(strTime) {
  let date = new Date(strTime);
  return date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
}

function formatChupiaoUnit(row) {
  let str = row.chupiaoUnit
  if (row.fangxiang == '1') {
    customerList.value.forEach(item => {
      if (item.uniqueCode == row.chupiaoUnit) {
        str = item.custName
      }
    })
  }
  return str
}

const customerList: any = ref([])

async function reloadCustomer() {
  const res = await useRouteApi(cusFindAllByFlag, {schemaName: dynamicTenantId})('1')
  customerList.value = res.items
}

const [registerEditPage, {openModal: openEditPage}] = useModal()
const val = {
  // id: '',
  billCode: '',
  billType: '1',
  fangxiang: '1',
  receiveDate: '',
  chupiaoUnit: '',
  chupiaoUnitAccount: '',
  chupiaoDate: '',
  daoqiDate: '',
  settModes: '',
  payBank: '',
  paymentUnit: '',
  paymentAccount: '',
  paymentBank: '',
  currency: '',
  huilv: '',
  money: '',
  contractNum: '',
  billLilv: '',
  payBankNum: '',
  payBankAddress: '',
  beishuUser: '',
  beishuMoney: '',
  remarks: '',
  yewuUser: '',
  billRemarks: '',
  acceptDate: '',
  beishuDate: '',
  icheck: '0',
  icheckDate: '',
  icheckUser: '',
  qichu: '1'
}
const openAddPage = () => {
  openEditPage(true, {
    data: val,
    dynamicTenantId: dynamicTenantId.value,
    defaultAdName: defaultAdName.value,
    isEdit: true
  })
}

const del = async (data: any) => {
  await useRouteApi(deleteAcceptBill, {schemaName: dynamicTenantId})(data.id)
  // alert('删除成功！')
  createSuccessModal({
    iconType: 'success',
    title: '删除',
    content: '删除成功！'
  })
  await reload()
}

async function saveData(data: any) {
  await useRouteApi(saveAcceptBill, {schemaName: dynamicTenantId})(data)
  await reload()
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

const condClick = (data: any) => {
  openEditPage(true, {
    data: data,
    dynamicTenantId: dynamicTenantId.value,
    defaultAdName: defaultAdName.value,
    isEdit: false
  })
}

const editOpen = () => {
  if (checkRow.value.length == 1) {
    if(checkRow.value[0].icheck=='1'){
      createErrorModal({
        iconType: 'warning',
        title: '编辑',
        content: '已审核的不能进行编辑！'
      })
      return false
    } else {
      openEditPage(true, {
        data: checkRow.value[0],
        isEdit: true
      })
    }
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '编辑',
      content: '请选择一条进行编辑！'
    })
  }
}

async function delList() {
  if (checkRow.value.length > 0) {
    let num = 0
    for (let i = 0; i < checkRow.value.length; i++) {
      const item = checkRow.value[i]
      if (item.icheck == '1') {
        num++
        createErrorModal({
          iconType: 'warning',
          title: '警告',
          content: '已审核的不能进行删除！'
        })
        return false
      }
    }
    if (num == 0) {
      createConfirm({
        iconType: 'error',
        title: '警告',
        content: '删除后数据将不能恢复，你确认要删除吗?',
        onOk: async () => {
          for (let i = 0; i < checkRow.value.length; i++) {
            const item = checkRow.value[i]
            await useRouteApi(deleteAcceptBill, {schemaName: dynamicTenantId})(item.id)
          }
          message.success('删除成功！')
          state.selectedRowKeys = []
          checkRow.value = []
          await reload()
          return true
        },
        onCancel: () => {
          return false
        }
      })
    }
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '警告',
      content: '请选择需要删除的内容！'
    })
    return false
  }
}

//审核
async function approved(){
  if (checkRow.value.length > 0) {
    let num = 0
    for (let i = 0; i < checkRow.value.length; i++) {
      const item = checkRow.value[i]
      if (item.icheck == '1') {
        num++
        createErrorModal({
          iconType: 'warning',
          title: '审核',
          content: '已有审核项不能进行审核！'
        })
        return false
      }
    }
    if (num == 0) {
      let dateTime = new_Date()
      for (let i = 0; i < checkRow.value.length; i++) {
        const item = checkRow.value[i]
        item.icheck = '1'
        item.icheckDate = dateTime
        item.icheckUser = useUserStore().getUserInfo['name']
        await useRouteApi(saveAcceptBill, {schemaName: dynamicTenantId})(item)
      }
    }
    state.selectedRowKeys = []
    checkRow.value = []
    await reload()
    return true
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '审核',
      content: '请选择需要审核的内容！'
    })
    return false
  }
}

//取消审核
async function unApproved(){
  if (checkRow.value.length > 0) {
    let num = 0
    for (let i = 0; i < checkRow.value.length; i++) {
      const item = checkRow.value[i]
      if (item.icheck != '1') {
        num++
        createErrorModal({
          iconType: 'warning',
          title: '取消审核',
          content: '已有未审核项不能进行取消审核！'
        })
        return false
      }
    }
    if (num == 0) {
      for (let i = 0; i < checkRow.value.length; i++) {
        const item = checkRow.value[i]
        item.icheck = '0'
        item.icheckDate = ''
        item.icheckUser = ''
        await useRouteApi(saveAcceptBill, {schemaName: dynamicTenantId})(item)
      }
    }
    state.selectedRowKeys = []
    checkRow.value = []
    await reload()
    return true
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '取消审核',
      content: '请选择需要取消审核的内容！'
    })
    return false
  }
}

//贴现锁定
async function tiexianFlag() {
  if (checkRow.value.length > 0) {
    let num = 0
    for (let i = 0; i < checkRow.value.length; i++) {
      const item = checkRow.value[i]
      if (item.flag < 0) {
        num++
        createErrorModal({
          iconType: 'warning',
          title: '贴现锁定',
          content: '已有锁定项不能进行锁定！'
        })
        return false
      }
    }
    if (num == 0) {
      for (let i = 0; i < checkRow.value.length; i++) {
        const item = checkRow.value[i]
        item.flag = '-1'
        await useRouteApi(saveAcceptBill, {schemaName: dynamicTenantId})(item)
      }
    }
    checkRow.value = []
    await reload()
    return true
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '贴现锁定',
      content: '请选择需要锁定的内容！'
    })
    return false
  }
}

//贴现解锁
async function tiexianUnFlag() {
  if (checkRow.value.length > 0) {
    let num = 0
    for (let i = 0; i < checkRow.value.length; i++) {
      const item = checkRow.value[i]
      if (item.flag != '-1') {
        num++
        createErrorModal({
          iconType: 'warning',
          title: '贴现解锁',
          content: '必须是贴现锁定项才能进行解锁！'
        })
        return false
      }
    }
    if (num == 0) {
      for (let i = 0; i < checkRow.value.length; i++) {
        const item = checkRow.value[i]
        item.flag = '0'
        await useRouteApi(saveAcceptBill, {schemaName: dynamicTenantId})(item)
      }
    }
    checkRow.value = []
    await reload()
    return true
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '贴现解锁',
      content: '请选择需要解锁的内容！'
    })
    return false
  }
}

//背书锁定
async function beishuFlag() {
  if (checkRow.value.length > 0) {
    let num = 0
    for (let i = 0; i < checkRow.value.length; i++) {
      const item = checkRow.value[i]
      if (item.flag < 0) {
        num++
        createErrorModal({
          iconType: 'warning',
          title: '背书锁定',
          content: '已有锁定项不能进行锁定！'
        })
        return false
      }
    }
    if (num == 0) {
      for (let i = 0; i < checkRow.value.length; i++) {
        const item = checkRow.value[i]
        item.flag = '-2'
        await useRouteApi(saveAcceptBill, {schemaName: dynamicTenantId})(item)
      }
    }
    checkRow.value = []
    await reload()
    return true
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '背书锁定',
      content: '请选择需要锁定的内容！'
    })
    return false
  }
}

//背书解锁
async function beishuUnFlag() {
  if (checkRow.value.length > 0) {
    let num = 0
    for (let i = 0; i < checkRow.value.length; i++) {
      const item = checkRow.value[i]
      if (item.flag != '-2') {
        num++
        createErrorModal({
          iconType: 'warning',
          title: '背书解锁',
          content: '必须是背书锁定才能进行解锁！'
        })
        return false
      }
    }
    if (num == 0) {
      for (let i = 0; i < checkRow.value.length; i++) {
        const item = checkRow.value[i]
        item.flag = '0'
        await useRouteApi(saveAcceptBill, {schemaName: dynamicTenantId})(item)
      }
    }
    checkRow.value = []
    await reload()
    return true
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '背书解锁',
      content: '请选择需要解锁的内容！'
    })
    return false
  }
}

//文件导出
async function exportExcel() {
  // debugger
  const arrHeader = columns.map((column) => column.title);
  const arrData = getDataSource().map((item) => getColumns().map((column: any) => item[column.dataIndex]));
  // console.log(arrHeader)
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: arrData,
    header: arrHeader,
    filename: pageParameter.companyName + '_' + '承兑汇票.xlsx',
  });
}

const cardList: any = ref([])

async function reloadCurrentPage() {
  const res = await useRouteApi(findAcceptBillByQichu, {schemaName: dynamicTenantId})()
  cardList.value = res.items
}

function onSearch() {
  reloadCurrentPage()
  let a: any = []
  a = cardList.value.filter(item => {
    //通过票据编号
    if (formItems.value.selectType == 'billCode' && formItems.value.selectValue != '') {
      return item.billCode.indexOf(formItems.value.selectValue) != -1
    }
    //通过出票单位
    if (formItems.value.selectType == 'chupiaoUnitAccount' && formItems.value.selectValue != '') {
      const b = customerList.value.filter(bb => {
        return bb.accStandardName.indexOf(formItems.value.selectValue) != -1
      })
      if (b.map(bb => bb.uniqueCode).filter(dd => dd == item.custName).length > 0) {
        return true
      }
      return item.chupiaoUnitAccount.indexOf(b.map(bb => bb.uniqueCode)) != -1 || item.chupiaoUnitAccount.indexOf(formItems.value.selectValue) != -1
      // return item.chupiaoUnitAccount.indexOf(formItems.value.selectValue) != -1
    }
    //通过摘要
    if (formItems.value.selectType == 'billRemarks' && formItems.value.selectValue != '') {
      return item.billRemarks.indexOf(formItems.value.selectValue) != -1
    }
    return item
  })
  // console.log(a)
  // setDataSource(a)
  setTableData([])
  setTableData(a)
}

const pageParameter: any = reactive({
  showRulesSize: 'MIN',
  companyCode: '102',
  companyName: '公司一',
  ifUnit: false,
})
const defaultPage = ref(false)
onMounted(async () => {
  await reloadCustomer()
  // 进页面执行
  getThisAdInfoData({'accId': getCurrentAccountName(false)}).then(res => {
    if (null != res && res.independent == 0) {
      defaultPage.value = false
      pageParameter.companyName = res.accNameCn
      pageParameter.companyCode = res.coCode
      pageParameter.ifUnit = res.icorp == '1'
    }
    resetDynamicColumnData()
  })
})
const defaultAdName = ref(useCompanyOperateStoreWidthOut().getSchemaName)
const dynamicAdReload = async (obj) => {
  // const dataBase:any = await findDataBase(obj.accId,obj.year)
  dynamicTenantId.value = obj.accountMode
  defaultAdName.value = obj.accId
  await reloadCustomer()
  let data: any = {}
  data.page = getPaginationRef().current
  data.size = getPaginationRef().pageSize
  let res: any = await useRouteApi(findAcceptBillByQichu, {schemaName: obj.accountMode})(data)
  setTableData([]) // 清空可能残留的数据
  setTableData(res.items)
  // 底部分页信息
  pageParameter.thisAdInfo = {}
  pageParameter.thisAdInfo = obj
  pageParameter.total = res.total
  setPagination({total: res.total})
}

function toThousandFilter(num: any) {
  if (num == '' || num == null) {
    return ''
  }
  return (+num || 0).toFixed(2).replace(/\d{1,3}(?=(\d{3})+(\.\d*)?$)/g, '$&,')
}

//获取当年月日
function new_Date() {
  let dateTime
  let yy = new Date().getFullYear()
  let mm = new Date().getMonth() + 1 < 10 ? '0' + new Date().getMonth() + 1 : new Date().getMonth() + 1
  let dd = new Date().getDate() < 10 ? '0' + new Date().getDate() : new Date().getDate()
  let hh = new Date().getHours()
  let mf = new Date().getMinutes() < 10 ? '0' + new Date().getMinutes() : new Date().getMinutes()
  let ss = new Date().getSeconds() < 10 ? '0' + new Date().getSeconds() : new Date().getSeconds()
  dateTime = yy + '-' + mm + '-' + dd + ' ' + hh + ':' + mf + ':' + ss
  console.log(dateTime)
  return dateTime
}

/**********************栏目设置**********************/
import {initDynamics, assemblyDynamicColumn} from "./data";
import {
  findDbLanMuList,
  saveLanMuList,
} from '/@/api/record/system/accvoucher';
import {cloneDeep} from "lodash-es";
import {useUserStore, useUserStoreWidthOut} from "/@/store/modules/user";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {findAllByFlag as cusFindAllByFlag} from "/@/api/record/costomer_data/customer";

const {createConfirm, createWarningModal} = useMessage();
const visible = ref(false);
const windowWidth = (document.documentElement.clientWidth - (70 + 280))
const windowHeight = (window.innerHeight - (400))
const totalColumnWidth = ref(0)
const dynamicColumns = initDynamics().DEFAULT
const dynamicColumnData: any = ref([])
let dynamicColumnDataCopy = []
const editableData = reactive({});
const tableRef: any = ref(null)
const lanMuData = {
  'accId': '',
  'menuName': '承兑汇票',
  'type': '账套',
  objects: '',
  username: useUserStoreWidthOut().getUserInfo.username
}
const confirm = (e: MouseEvent) => {
  // 询问
  createConfirm({
    iconType: 'warning',
    title: '栏目同步',
    content: '是否将刚才设置同步数据库!',
    onOk: async () => {
      // 调整数据库 列参数
      lanMuData.accId = getCurrentAccountName(false)
      lanMuData.objects = JSON.stringify(filterModifyData(dynamicColumnData.value, dynamicColumnDataCopy))
      if (lanMuData.objects == '[]') {
        createWarningModal({content: '请先做修改后再进行确认同步数据库！'})
      } else {
        saveLanMuList(lanMuData).then(res => {
          message.success("数据库同步成功！")
        })
        // 重新赋值
        dynamicColumnDataCopy = JSON.parse(JSON.stringify(dynamicColumnData.value))
      }
    }
  });
  // 重新获取数据
  reloadColumns()
}
const cancel = (e: MouseEvent) => {
  // 恢复默认
  dynamicColumnData.value = []
  dynamicColumnData.value = dynamicColumnDataCopy
}
const edit = (key: string) => {
  if (key.toString().indexOf('-') != -1) {
    let arr: any = key.split('-');
    let one = parseInt(arr[0])
    if (arr.length == 2) {
      editableData[key] = cloneDeep(dynamicColumnData.value[one].children.filter(item => key === item.key)[0]);
    } else {
      let two = parseInt(arr[1] - 1)
      editableData[key] = cloneDeep(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0]);
    }
  } else {
    editableData[key] = cloneDeep(dynamicColumnData.value.filter(item => key === item.key)[0]);
  }
}
const save = (key: string, min: number, max: number) => {
  editableData[key].width = editableData[key].width > max ? max : editableData[key].width < min ? min : editableData[key].width
  if (key.toString().indexOf('-') != -1) {
    let arr: any = key.split('-');
    let one = parseInt(arr[0])
    if (arr.length == 2) {
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
    } else {
      let two = parseInt(arr[1] - 1)
      Object.assign(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0], editableData[key]);
    }
  } else {
    Object.assign(dynamicColumnData.value.filter(item => key === item.key)[0], editableData[key]);
  }
  delete editableData[key];
}
const saveName = (key: string) => {
  if (key.toString().indexOf('-') != -1) {
    let arr: any = key.split('-');
    let one = parseInt(arr[0])
    if (arr.length == 2) {
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
    } else {
      let two = parseInt(arr[1] - 1)
      Object.assign(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0], editableData[key]);
    }
  } else {
    Object.assign(dynamicColumnData.value.filter(item => key === item.key)[0], editableData[key]);
  }
  delete editableData[key];
}

function filterModifyData(lanMuList: any, copyList) {
  let a = lanMuList.filter(item => {
    try {
      copyList.forEach(item2 => {
        if (item.key === item2.key && item.name == item2.name) {
          if (item.nameNew != item2.nameNew || item.width != item2.width || item.check != item2.check || item.align != item2.align)
            throw new Error('ok')
        }
      })
      return false
    } catch (e: any) {
      if (e.message == 'ok') {
        return true
      } else {
        return false
      }
    }
  })
  return a;
}

function combineParameters(staticList: any, dbList: any) {
  staticList.forEach(item => {
    dbList.forEach(item2 => {
      if (item.key === item2.key && item.name === item2.name) {
        item.nameNew = item2.nameNew
        item.width = parseInt(item2.width)
        item.check = item2.check == 'true'
        item.align = item2.align
      }
    })
  })
  return staticList
}

const reloadColumns = () => {
  let a: any = []
  a = getColumns()
  let newA = JSON.parse(JSON.stringify(columns))
  newA = assemblyDynamicColumn(dynamicColumnData.value, newA)
  setColumns(newA)
  initTableWidth(newA)
  //searchConditonList.value = newA
}

function resetDynamicColumnData() {
  // 先从数据查询是否已经设置
  lanMuData.accId = getCurrentAccountName(false)
  findDbLanMuList(lanMuData).then(res => {
    // 栏目列
    let dbList = res.items
    if (dbList.length > 0) {
      let statiList = initDynamics()['DATA']
      dbList = combineParameters(statiList, dbList)
      dynamicColumnData.value = dbList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(dbList))
    } else {
      let statiList = initDynamics()['DATA']
      dynamicColumnData.value = statiList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(statiList))
    }
    // 表格列
    reloadColumns()
    //pageReload()
  })
}

function initTableWidth(thisCs) {
  let total = 60
  thisCs.forEach(item => {
    if (item.ifShow == null || item.ifShow)
      total += parseInt(item.width)
  })
  /*  // 去除操作咧宽
    total -= 100*/
  if (total > windowWidth) {
    let f = 0
    if (visible.value) f = 260
    totalColumnWidth.value = Number(windowWidth) - f
    // tableRef.value.$el.style.setProperty('width',(windowWidth+20-f)+'px')
  } else {
    if (visible.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
    totalColumnWidth.value = total
    // tableRef.value.$el.style.setProperty('width',(total+20)+'px')
  }
}

const pageReload = () => {
  setTableData([]) // 清空可能残留的数据
  reload({
    searchInfo: pageParameter
  })
}
/*栏目设置end*/
</script>
<style src="../../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style>
<style lang='less' scoped></style>
<style src="../../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>
<style lang='less' scoped></style>
<style scoped lang="less">
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

.choose {
  line-height: 30px;
  cursor: pointer;
}

.choose:hover {
  color: blue; /*DiV背景颜色*/
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
