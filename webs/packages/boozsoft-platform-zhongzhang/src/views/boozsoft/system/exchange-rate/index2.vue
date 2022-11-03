<template>
  <div>
    <div class="app-container lcr-theme-div">
      <div>
        <div>
          <ProfileOutlined style="color: #0096c7;font-size: 50px;"/>
        </div>
        <div> <AccountPicker theme="three" @reloadTable="dynamicAdReload"/></div>
      </div>
      <div>
        <div style="margin-top: 5%;">  <b class="noneSpan" style="font-size: 26px;color: #0096c7;">外币汇率设置</b></div>
      </div>
      <div>
        <div>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="openAddPage()"
          ><span>新增</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="openEdit()"
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
            @click="closeCurrent()"
          ><span>退出</span></button>
        </div>
        <div>
          <div>
            <a-input-search
              placeholder=""
              style="width: 200px; border-radius: 4px"
              @search="onSearch"
            />
          </div>
          <div>
            <a-button>
              <SyncOutlined :style="{ fontSize: '14px' }"/>
            </a-button>
            <a-button @click="()=>{
            if (!visible){ visible = true}
            return false
          }">
              <FilterFilled :style="{ fontSize: '14px' }"/>
            </a-button>
          </div>
        </div>
      </div>
    </div>

    <div class="app-container">
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <TypeTree class="w-1/4 xl:w-1/5" @select="handleSelect"/>
        <div style="width: calc(100% - 250px); float: right;margin-left: 5px;">
          <div class="a-container-content-two">
            <div class="acct-up">
              <span>设置参数</span>
              <br/>
              <!--              <a-button v-if="ifModify" class="editable-add-btn" type="primary" size="small" @click="handleWbModify" style="margin-right: 15px;float: right">保存</a-button>
                            <br/>-->
              <div>
                <span class="acctd-span">汇率小数位精度：</span>
                <a-select v-model:value="WbBasicInfo.decimalNum" default-value="7" :disabled="true"
                          style="width: 158px;text-align: center;margin-right: 30px">
                  <a-select-option value="2">2</a-select-option>
                  <a-select-option value="3">3</a-select-option>
                  <a-select-option value="4">4</a-select-option>
                  <a-select-option value="5">5</a-select-option>
                  <a-select-option value="6">6</a-select-option>
                  <a-select-option value="7">7</a-select-option>
                  <a-select-option value="8">8</a-select-option>
                  <a-select-option value="9">9</a-select-option>
                </a-select>
                <span class="acctd-span">最大误差：</span>
                <a-input v-model:value="WbBasicInfo.errorValue"
                         style="width: 200px;text-align: center;margin-right: 30px;pointer-events: auto"
                         v-bind:readonly="true"/>
                <span class="acctd-span">计算方式：</span>
                <a-select default-value="" v-model:value="WbBasicInfo.comMethod"
                          style="width: 200px;text-align: center" :disabled="true">
                  <a-select-option value="*">原币*汇率=本位币</a-select-option>
                  <a-select-option value="/">原币/汇率=本位币</a-select-option>
                </a-select>
              </div>
              <div>
                <span class="acctd-span">汇率设置：</span>
                <a-select default-value="" v-model:value="WbBasicInfo.rateStyle" @change="WbChange"
                          style="width: 200px;text-align: center;margin-right: 30px">
                  <a-select-option value="MONTH">月汇率</a-select-option>
                  <a-select-option value="DAY">日汇率</a-select-option>
                </a-select>
                <span class="acctd-span">取值方式：</span>
                <a-select default-value="" v-model:value="WbBasicInfo.valueMethod"
                          style="width: 200px;text-align: center" :disabled="true">
                  <a-select-option value="VOUCHER">
                    距离凭证日期最近
                  </a-select-option>
                  <a-select-option value="SYSTEM">
                    距离系统日期最近
                  </a-select-option>
                </a-select>
              </div>
            </div>
            <div class="acct-down">
              <span>汇率设置</span>
              <div>
                <span style="margin-right: 15px">当前外币: <span
                  style="color: red">{{ getWbName(WbBasicInfo.foreignCode) }}</span></span>
                <span v-if=" WbBasicInfo.rateStyle === 'DAY'">月份：</span>
                <a-month-picker
                  v-model:value="WbMonth"
                  v-if="WbBasicInfo.rateStyle === 'DAY'"
                  @change="WbChange"
                  valueFormat="MM"
                  format="MM"
                  placeholder=""
                  style="width: 80px"
                />
                <span v-if="!ifModify && WbBasicInfo.rateStyle === 'DAY'">选择新增日期：</span>
                <a-select v-model:value="WbDay" v-if="!ifModify && WbBasicInfo.rateStyle === 'DAY'">
                  <a-select-option v-for="value in getMonthDays(WbMonth)"
                                   :value="(value) > 9?value+'':'0'+(value)">
                    {{ (value) > 9 ? value + '' : '0' + (value) }}
                  </a-select-option>
                </a-select>
                <a-button v-if="!ifModify" class="editable-add-btn" type="primary" size="small"
                          @click="handleWbAdd(WbDay)" style="margin-left: 15px;">+
                </a-button>
                <a-table size="small" bordered :data-source="WbDataSource" :columns="WbColumns"
                         :pagination="false" :scroll="{ y  : tableHeight }">
                  <template #rateBook="{ text, record }">
                    <div class="editable-cell">
                      <div
                        v-if="editableData[record.key]   && editableData[record.key].type == 'WB'"
                        class="editable-cell-input-wrapper">
                        <a-input v-model:value="editableData[record.key].rateBook"
                                 @pressEnter="save(record.key,'WB')"/>
                        <check-outlined class="editable-cell-icon-check"
                                        @click="save(record.key,'WB')"/>
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ text || ' ' }}
                        <edit-outlined class="editable-cell-icon" v-if="!ifModify"
                                       @click="edit(record.key,'WB')"/>
                      </div>
                    </div>
                  </template>
                  <template #operation="{ record }">
                    <a-popconfirm
                      v-if="!ifModify"
                      title="你确定删除当前外币汇率数据吗?"
                      @confirm="onDelete(record.key)"
                    >
                      <a-button class="editable-add-btn" type="primary" size="small">-</a-button>
                    </a-popconfirm>
                  </template>
                </a-table>
              </div>
            </div>
          </div>
        </div>
        <Edit
          @save="saveData"
          @register="registerEditPage"
        />
      </PageWrapper>
    </div>
  </div>
</template>
<script setup lang="ts">
import {PageWrapper} from '/@/components/Page'
import {useRouteApi} from '/@/utils/boozsoft/datasource/datasourceUtil';
import TypeTree from './TypeTree.vue'
import Edit from './popup/edit.vue';
import {useModal} from '/@/components/Modal'
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
  PieChartFilled, ReadOutlined, SafetyOutlined, RestOutlined,
  FilterFilled, CheckOutlined, SortDescendingOutlined, SortAscendingOutlined, EditOutlined,ProfileOutlined
} from '@ant-design/icons-vue'
import {onMounted, provide, reactive, ref} from 'vue'
import {
  Input as AInput,
  Select as ASelect,
  DatePicker as ADatePicker,
  Popconfirm as APopconfirm,
  Radio as ARadio,
  Table as ATable,
  message
} from "ant-design-vue";

const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ARadioButton = ARadio.Button
const ARadioGroup = ARadio.Group
const AMonthPicker = ADatePicker.MonthPicker
import {
  getCurrentAccountName, getCurrentMonthLast, getThisIndexImg, hasBlank, serverTime,
} from "/@/api/task-api/tast-bus-api";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  deleteWbData, deleteWbInfo,
  findAllWbhvEntrys,
  findCurrentWbhvInfo,
  getThisAdInfoData,
  saveAIData, saveCsData,
  saveCurrencyData,
  saveSystemQuarterly,
  saveVoucherData,
  saveWbhvData, saveWbhvEntrys,
} from "/@/api/record/system/financial-settings";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {cloneDeep} from "lodash-es";
import {saveSettModes} from "/@/api/record/system/sett-modes";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";

const {
  createConfirm,
  createWarningModal
} = useMessage()

const flag = ref('1')
const {closeCurrent} = useTabs(router);
const thisCheckKey = ref('')
const currencyList = ref([])
const infoList = ref([])
const treeReloads = ref([])
function handleSelect(data) {
  thisCheckKey.value = data.uniqueCode
  if (!hasBlank(data.currencyList))
  currencyList.value = data.currencyList
  if (!hasBlank(data.infoList))
  infoList.value = data.infoList
  if (!hasBlank(data.uniqueCode)) {
    WbBasicInfo.value = infoList.value.filter(item=>item.foreignCode == data.uniqueCode )[0]
    WbChange()
  }else {
    WbDataSource.value = []
  }
  if (treeReloads.value.length == 0)
    treeReloads.value[0] = data.treeReload
}

// 部门带参数,给人员，人员翻页，带部门

const [registerEditPage, {openModal: openEditPage}] = useModal()

onMounted(async () => {
})

const onSearch = () => {

}

const openAddPage = () => {
  openEditPage(true, {
    data: {}
  })
}

const openEdit = () => {
  let data = infoList.value.filter(item => item.foreignCode == thisCheckKey.value)[0]
  if (null != data) {
    openEditPage(true, {
      data: data
    })
  }else {
    createWarningModal({title:'温馨提示',content: '请选择币种！'})
  }
}
const delList = async () => {
  let data = infoList.value.filter(item => item.foreignCode == thisCheckKey.value)[0]
  if (null != data) {
    let res = await useRouteApi(deleteWbInfo, {schemaName: accountInfo.value.accountMode})(data)
    message.success('数据更新成功！')
   await reloadPage()
  }else {
    createWarningModal({title:'温馨提示',content: '请选择需要删除的汇率币种！'})
  }
}
const reloadPage =async () => {
  if (treeReloads.value.length > 0){
    let reload = treeReloads.value[0]
    if (null != reload) reload()
  }
}

async function saveData(data: any) {
  // 添加汇率
  if (hasBlank(data?.id)) {
    data.accountId = accountInfo.value.accId
    data.iyear = accountInfo.value.iyear
  }
  let res = await useRouteApi(saveWbhvData, {schemaName: accountInfo.value.accountMode})(data)
    message.success('数据更新成功！')
  await reloadPage()
}

/************************************外币汇率************************************/
const editableData = reactive({});
const CyDataSource = ref([])
const ifModify = ref(false)
const tableHeight = (document.documentElement.clientHeight - 540) + 'px'
const WbBasicInfo = ref({
  id: '',
  foreignCode: 'S.RP(S)',
  localCode: 'RMB',
  comMethod: '*',
  valueMethod: 'VOUCHER',
  decimalNum: '7',
  errorValue: '0.01',
  rateStyle: 'MONTH',
  accountId: getCurrentAccountName(),
  iyear: '2021'
})

const WbMonth = ref('')
const WbDay = ref('')
const WbDataSource = ref([{
  key: '0',
  serialNumber: '1',
  iperiod: '2021-01',
  rateBook: '',
  checkInTime: '',
  uniqueCode: ''
}]);
const WbColumns = [
  {
    title: '序号',
    key: 'serialNumber',
    dataIndex: 'serialNumber',
    align: 'center',
    width: '10%'
  },
  {
    title: '汇率期间',
    key: 'iperiod',
    dataIndex: 'iperiod',
    align: 'center',
    width: '25%',
  },
  {
    title: '记账汇率',
    key: 'rateBook',
    dataIndex: 'rateBook',
    align: 'center',
    width: '25%',
    slots: {customRender: 'rateBook'},
  },
  {
    title: '登记时间',
    key: 'checkInTime',
    dataIndex: 'checkInTime',
    align: 'center',
    width: '30%',
  }, {
    title: '操作',
    dataIndex: 'operation',
    width: '10%',
    align: 'center',
    slots: {customRender: 'operation'},
  }
]


let changeBeforeModel: any = {}
const handleWbModify = () => {
  // 校验必填
  let msg = ''
  for (let i in WbBasicInfo.value) {
    if (i != 'id' && WbBasicInfo.value[i] == '') {
      msg = '请完善基础性信息后再点击保存！'
      break;
    }
  }
  let isChanged: boolean = false
  isChanged = !(WbBasicInfo.value.foreignCode == changeBeforeModel.foreignCode
    && WbBasicInfo.value.comMethod == changeBeforeModel.comMethod
    && WbBasicInfo.value.valueMethod == changeBeforeModel.valueMethod
    && WbBasicInfo.value.decimalNum == changeBeforeModel.decimalNum
    && WbBasicInfo.value.rateStyle == changeBeforeModel.rateStyle
    && WbBasicInfo.value.iyear == changeBeforeModel.iyear
    && WbBasicInfo.value.errorValue == changeBeforeModel.errorValue)
  if (!isChanged) {
    msg = '数据无变化！无法进行保存操作！'
  }
  if (msg != '') {
    createWarningModal({content: msg});
    return;
  }
  // 后台同步
  saveWbhvData(WbBasicInfo.value).then(res => {
    if (null == res) {
      message.success('系统已同步！')
      initWbPageData()
    } else {
      message.success('系统同步失败！')
    }
  })
}
const getMonthDays = (month) => {
  if (month.value == '') {
    return []
  } else {
    return parseInt(getCurrentMonthLast(WbBasicInfo.value.iyear + '-' + WbMonth.value))
  }
}
const handleWbAdd = () => {
  let a = null == WbDataSource.value ? 0 : WbDataSource.value.length
  if (WbBasicInfo.value.rateStyle === 'DAY') {
    if ('' == WbMonth.value) {
      createWarningModal({content: '请选择日汇率所属月份！'});
      return;
    }
    if ('' == WbDay.value) {
      createWarningModal({content: '请选择日汇率所属具体日期！'});
      return;
    }
    if (WbDataSource.value.filter(item => item.iperiod == assembleTheCurrentPeriod(a)).length > 0) {
      createWarningModal({content: '该日期已存在不能进行重复添加！'});
      return;
    }
    if (a == parseInt(WbDay.value)) {
      return
    }
  } else {
    if (a == 12) {
      return
    }
  }
  const newData = {
    key: `${a}`,
    serialNumber: `${a + 1}`,
    iperiod: assembleTheCurrentPeriod(a),
    rateBook: '',
    checkInTime: '',
    uniqueCode: '',
    currencyCode: ''
  };
  WbDataSource.value.push(newData)
  WbDay.value = ''
}

const assembleTheCurrentPeriod = (count) => {
  if (WbBasicInfo.value.rateStyle === 'DAY') {
    return WbBasicInfo.value.iyear + '-' + WbMonth.value + '-' + WbDay.value
  } else {
    return WbBasicInfo.value.iyear + '-' + ((count + 1) > 9 ? '' + (count + 1) : '0' + (count + 1))
  }
}
const loadWbDataSource = async (uniqueCode, condition) => {
  WbDataSource.value = []
  await useRouteApi(findAllWbhvEntrys, {schemaName: accountInfo.value.accountMode})({
    uniqueCode: uniqueCode,
    date: condition,
    currencyCode: WbBasicInfo.value.foreignCode
  }).then(res2 => {
    res2.items.forEach((item, index) => {
      item.key = `${index}`
      item.serialNumber = `${index + 1}`
      WbDataSource.value.push(item)
    })
  })
}

const getWbName = (num) => {
  let obj = infoList.value.filter(item => item.foreignCode == num)[0]
  if (null == obj) {
    return ''
  }
  return obj.foreignCode + ' ' + obj.localCode
}
const WbChange = () => {
  if (hasBlank(thisCheckKey.value)) return
  let condition = WbBasicInfo.value.iyear
  if (WbBasicInfo.value.rateStyle == 'DAY') {
    condition = WbBasicInfo.value.iyear + '-' + WbMonth.value
  }
  loadWbDataSource(WbBasicInfo.value.id, condition)
}
const initWbPageData = async () => {
  await useRouteApi(findCurrentWbhvInfo, {schemaName: accountInfo.value.accountMode})({
    accId: getCurrentAccountName(),
    iyear: WbBasicInfo.value.iyear
  }).then(res => {
    changeBeforeModel = JSON.parse(JSON.stringify(res))
    WbBasicInfo.value = res
    let condition = ''
    if (res.rateStyle == 'DAY') {
      WbMonth.value = (new Date().getMonth() + 1 > 9) ? (new Date().getMonth() + 1 + '') : ('0' + (new Date().getMonth() + 1))
      condition = res.iyear + '-' + WbMonth.value
    } else {
      condition = res.iyear
    }
    loadWbDataSource(res.id, condition)
  })
}
const edit = (key: string, type: string) => {
  editableData[key] = cloneDeep(WbDataSource.value.filter(item => key === item.key)[0]);
  editableData[key].type = 'WB'
}

const save = async (key: string, type: string) => {
  let data = editableData[key]
  editableData[key].rateBook = parseFloat(editableData[key].rateBook).toFixed(parseInt(WbBasicInfo.value.decimalNum))
  editableData[key].checkInTime = serverTime()
  editableData[key].uniqueCode = WbBasicInfo.value.id
  editableData[key].currencyCode = WbBasicInfo.value.foreignCode
  Object.assign(WbDataSource.value.filter(item => key === item.key)[0], editableData[key]);
  let handle = await useRouteApi(saveWbhvEntrys, {schemaName: accountInfo.value.accountMode})(data)
  if (null == handle) {
    message.success('更新成功!')
    WbChange()
    delete editableData[key];
  } else {
    message.warning('更新失败!')
  }
}
const onDelete = async (key) => {
  let data = WbDataSource.value.filter(item => item.key === key)[0]
  if (data.id == null || data.id == '') { // 空白行删除
    WbDataSource.value = WbDataSource.value.filter(item => item.key !== key);
  } else {
    let res = await useRouteApi(deleteWbData, {schemaName: accountInfo.value.accountMode})(data)
    if (null == res) {
      message.success('删除成功!')
      WbDataSource.value = WbDataSource.value.filter(item => item.key !== key);
    } else {
      message.warning('删除失败!')
    }
  }
}
/************************************外币汇率************************************/

const accountInfo = ref({})
const dynamicAdReload = async (obj) => {
  accountInfo.value = obj
  ifModify.value = obj.target?.iexchangeRateControl == '1' ? true : false
  await reloadPage()
}
provide('accountInfo', accountInfo)
provide('currencyList', currencyList)
provide('infoList', infoList)
</script>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>
<style lang="less" scoped>
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

  .a-container-content-two {
    background-color: white;
    height: 80%;
    padding-top: 2%;

    .acco-btn {
      width: 1060px;
      text-align: right;
      padding: 10px 0 0;

      .ant-btn {
        margin: 0 5px;
      }
    }

    .a-table-font-size-14 td,
    .a-table-font-size-14 th {
      font-size: 14px !important;
      padding: 2px 8px !important;
    }

    .ant-select-disabled > .ant-select-selector {
      color: #252525 !important;
      background: white !important;
    }

    .acct-up {
      position: relative;
      border: 1px #acabab solid;
      width: 1060px;
      margin: 10px 1% 20px;

      > span {
        position: absolute;
        top: -12px;
        left: 50px;
        font-size: 15px;
        background: white;
        padding: 0 15px;
      }

      > div {
        margin: 15px;
        color: black;

        :deep(.ant-select-selector), .ant-input {
          border: none;
          border-bottom: 1px slategrey solid;
          text-align: center;
        }

        :deep(.ant-select-disabled) {
          .ant-select-selector {
            background-color: white;
            color: black;
          }
        }
      }

      .acctd-span {
        margin: 0 10px;
      }

      .ant-input {
        pointer-events: none;
      }
    }

    .acct-down {
      position: relative;
      border: 1px #acabab solid;
      width: 1060px;
      margin: 10px 1% 20px;

      > span {
        position: absolute;
        top: -12px;
        left: 50px;
        font-size: 15px;
        background: white;
        padding: 0 15px;
      }

      > div {
        text-align: center;
        margin: 10px 20px;

        tr > th {
          text-align: center;
          font-weight: bold;
        }

        .ant-input {
          width: 85%;
        }
      }

      .ant-select-selector, .ant-input {
        border: none;
        border-bottom: 1px slategrey solid;
      }

    }
  }
}
.lcr-theme-div{
  display: inline-flex;justify-content: space-between;width: 99%;height: 100px;
  >div:nth-of-type(1){
    width: 40%;
    position: relative;
    >div:nth-of-type(1){width: 64px;display: inline-block;text-align: center;    top: 12px;
      position: inherit
    }
    >div:nth-of-type(2){
      width: calc( 100% - 64px);display: inline-block;
    }
  }
  >div:nth-of-type(2){
    width: 20%;text-align:center;
    >div:nth-of-type(2){margin-top: 14px;}
  }
  >div:nth-of-type(3){
    width: 40%;text-align: right;

    >div:nth-of-type(1){
      .ant-btn-me {
        color: #0096c7;
      }
    }
    >div:nth-of-type(2){
      display: inline-flex;justify-content: space-between;margin-top: 14px;
    }
  }
}
</style>
