<template>
  <div class="app-financial-info" :style="{height: (windowHeight-95)+'px'}">
    <div class="a-container-head">
      <div class="a-c-head-title">
        <SettingOutlined style="font-size: 30px;color: #666666;float: left;margin: 0 .5%;"/>
        <h2>现金银行参数设置</h2>
      </div>
      <div class="a-c-head-btns">
        <div class="ant-btn-group a-c-h-btns-group" style="min-width: 600px">
          <AccountPicker theme="one" @reloadTable="dynamicAdReload" style="width: 100%"/>
        </div>
      </div>
    </div>
    <div class="setting_box"></div>
    <div class="a-container-content">
      <a-tabs type="card" :tabBarGutter="0" v-model:activeKey="activeKey" tabPosition="left">
        <a-tab-pane key="1" :style="activeKey=='1'?{height: (windowHeight-170)+'px',overflowY: 'auto'}:{}">
          <template #tab>
            <span style="line-height: 30px;display: flex;">
            <AppstoreOutlined style="font-size: 30px;color: #666666;"/>
              基本设置
            </span>
          </template>
          <div class="a-container-content-one">
            <div class="acco-btn">
                <a-button v-if="isEdit" type="primary" @click="handleBus('0')">编辑</a-button>
                <template v-if="!isEdit">
                  <a-button @click="handleBus('1')">恢复系统设置</a-button>
                  <a-button type="primary" @click="handleBus('2')">保存</a-button>
                  <a-button @click="handleBus('3')">放弃</a-button>
                </template>
            </div>
            <div class="acco-sideline">
              <span>基础信息</span>
              <div class="acco-sideline-content-one acco-sideline-content-two">
                <div style="width: 38%">
                  <span>会计准则：</span>
                  <a-select v-model:value="dataModel.basisMap.accStandard" style="width: 200px" :disabled="true">
                    <a-select-option v-for="item in pageParameter.acountStandardList" :value="item.id+''">
                      {{ item.tname }}
                    </a-select-option>
                  </a-select>
                </div>
                <div style="width: 30%">
                  <span>对账单余额方向：</span>
                  <a-select v-model:value="dataModel.statementMap.statementFangxiang" :disabled="isEdit" style="width: 100px">
                    <a-select-option value="1">借</a-select-option>
                    <a-select-option value="2">贷</a-select-option>
                  </a-select>
                </div>
                <div style="width: 20%">
                  <span>本位币：{{ dataModel.basisMap.currency }}</span>
                </div>
                <a-checkbox-group v-model:value="dataModel.pingZhenControlList" :disabled="isEdit" style="width: 100%;">
                  <a-row>
                    <a-col :span="5">
                      <a-checkbox value="icashier">
                        出纳凭证必须出纳签字
                      </a-checkbox>
                    </a-col>
                    <a-col :span="5">
                    </a-col>
                    <a-col :span="5">
                      <a-checkbox value="icashierNo">
                        出纳签字取消须为同一人
                      </a-checkbox>
                    </a-col>
                  </a-row>
                </a-checkbox-group>
              </div>
            </div>
            <div class="acco-sideline">
              <span>现金日记账排序方式</span>
              <div class="acco-sideline-content-there">
               <ul>
                 <li>
                   <a-radio-group v-model:value="dataModel.statementMap.cashBankDayBookSort" :disabled="isEdit">
                     <a-radio value="1" style="width: 30%;">
                       日期 + 凭证类别字 + 凭证号
                     </a-radio>
                     <a-radio value="2" style="width: 30%;">
                       日期 + 制单顺序号
                     </a-radio>
                   </a-radio-group>
                 </li>
               </ul>
              </div>
            </div>
            <div class="acco-sideline">
              <span>银行对账匹配优先级</span>
              <div class="acco-sideline-content-there">
                <ul>
                  <li>
                    <a-radio-group v-model:value="dataModel.statementMap.bankStatementSort" :disabled="isEdit">
                      <a-radio value="1" style="width: 30%;">
                        对方单位 + 票号 + 日期 + 结算方式
                      </a-radio>
                      <a-radio value="2" style="width: 30%;">
                        对方单位 + 日期 + 票号 + 结算方式
                      </a-radio>
                    </a-radio-group>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </a-tab-pane>
        <a-tab-pane key="2" :style="activeKey=='2'?{height: (windowHeight-170)+'px',overflowY: 'auto'}:{}" >
          <template #tab>
            <span style="line-height: 30px;display: flex;">
            <ProfileOutlined style="font-size: 30px;color: #666666;"/>
              未达账项账龄
            </span>
          </template>
          <div class="a-container-content-two" style="margin-top: 30px;">
              <a-table size="small" bordered :data-source="bankStatementDataSource" :columns="Columns"
                       :pagination="false" :scroll="{ y  : 400 }" style="width: 80%;margin-left: 10%;margin-bottom: 30px;">
                <template #startDayNumber="{ text, record }">{{text+(text == ''?'':text.indexOf('天') != -1?'':`天`)}}</template>
                <template #totalDayNumber="{ text, record }">
                  <div class="editable-cell">
                    <div v-if="editableData[record.key]"
                         class="editable-cell-input-wrapper">
                      <a-input v-model:value="editableData[record.key].totalDayNumber" style="width: 80%;" @pressEnter="rowEdit(record.key,'end')"/>
                      <check-outlined class="editable-cell-icon-check"
                                      @click="rowEdit(record.key,'end')"/>
                    </div>
                    <div v-else class="editable-cell-text-wrapper">
                      {{ text || ' ' }}
                      <edit-outlined v-if="record.serialNumber !='10'" class="editable-cell-icon" @click="rowEdit(record.key,'start')"/>
                    </div>
                  </div>
                </template>
              </a-table>
          </div>
        </a-tab-pane>
      </a-tabs>
    </div>
  </div>
</template>
<script setup="props, {emit}" lang="ts">
import {
  Select as ASelect,
  Input as AInput,
  Radio as ARadio,
  Tabs as ATabs,
  Checkbox as ACheckbox,
  Row as ARow,
  Col as ACol,
  Button as AButton,
  message,
  Table as ATable,
  DatePicker as ADatePicker,
  Tree as ATree,
  Popconfirm as APopconfirm
} from "ant-design-vue"
const ATabPane = ATabs.TabPane
const ASelectOption = ASelect.Option
const ACheckboxGroup = ACheckbox.Group
const ARadioGroup = ARadio.Group
const ATreeNode = ATree.TreeNode
const AMonthPicker = ADatePicker.MonthPicker
import {onMounted, reactive, ref, watch} from "vue";
import {
  SortDescendingOutlined,
  SortAscendingOutlined,
  CheckOutlined,
  OrderedListOutlined,
  FormOutlined,
  DeleteOutlined,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  PieChartFilled, FilterFilled,
  EditOutlined,
  DownOutlined, SmileOutlined, FrownOutlined, FrownFilled,AppstoreOutlined,ProfileOutlined,ProjectOutlined,SettingOutlined
} from '@ant-design/icons-vue'
import {
  initBasisTabAccoutData,
  initBasisTabData,
  initBasisTabDataDefault,
  modifyBasisTabDBData, modifyPeriodDBDataByAccId,
  currentAccountQjInfo,
  currentAccountYaerQjAndJdList,
  saveQjJdQuarterly,
  saveSystemQuarterly,
  currentAccountTypes,
  saveVoucherData,
  delVoucherData,
  delAccountData,
  currentCyDatas,
  findCurrencyTypeList,
  saveCurrencyData,
  deleteCurrencyData,
  deleteWbData,
  deleteCsData,
  saveCsData,
  findCurrentWbhvInfo,
  findAllWbhvEntrys,
  saveWbhvData,
  saveWbhvEntrys,
  findAllXjOrLlList, findAllMJList,
  saveAIData, findCorpBankAll, parameterAccuracyDatas, defaultSettingModel
} from '/@/api/record/system/financial-settings'
const windowHeight  = (document.documentElement.clientHeight)
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {hasBlank} from "/@/api/task-api/tast-bus-api";
import {cloneDeep} from "lodash-es";
import {useMessage} from "/@/hooks/web/useMessage";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findAllByAccIdModel, saveModel} from "/@/api/record/system/aging-range";
const {createWarningModal} = useMessage()
const activeKey= ref('1')
const database = ref('')
const databaseCn = ref('')
const databaseYear = ref('')

/************************************基础选项************************************/
const isEdit = ref(true)
const isEdit2 = ref(true)
const pageParameter:any = reactive({
  acountStandardList: [],
  levelList: [],
  currencyList: []
})

const dataModel = reactive(defaultSettingModel)

const dataModelCopy = reactive(defaultSettingModel)

const currencyTypeList = ref([])
onMounted(()=>{
  // pageReload()
  findCurrencyTypeList({}).then(res=>{
    currencyTypeList.value = res.items.filter(obj => obj.currencyZhCnName.indexOf('人民币') == -1  /*|| obj.currencyZhCnName != dataModel.basisMap.currency*/)
  })
  currDataSource()
})
const pageReload = ()=>{
  // 初始化选项一的默认数据
  initBasisTabAccoutData().then(res=>{
    pageParameter.acountStandardList = res.acountStandardList
    pageParameter.levelList = res.levelList
    //pageParameter.currencyList = res.currencyList
  })
    .catch(error => {
      console.log('error', error)
    })
  findCurrencyTypeList({}).then(res=>{
    currencyTypeList.value = res.items
  })

  initBasisTabData({'accId': databaseCn.value}).then(res=>{
    if (res.basisMap.length != 0) {
      dataModel.basisMap = res.basisMap
      dataModelCopy.basisMap = res.basisMap
      dataModel.statementMap = res.statementMap
      dataModelCopy.statementMap = res.statementMap
      standardChange(dataModel.basisMap.accStandard)
      if (res.zhiDanList.length != 0) {
        dataModel.zhiDanList = res.zhiDanList
        dataModelCopy.zhiDanList = res.zhiDanList
      }
      if (res.pingZhenNumberList.length != 0){
        dataModel.pingZhenNumberList = res.pingZhenNumberList
        dataModelCopy.pingZhenNumberList = res.pingZhenNumberList}
    }
    if (res.pingZhenControlList.length != 0) {
      dataModel.pingZhenControlList = res.pingZhenControlList
      dataModelCopy.pingZhenControlList = res.pingZhenControlList
    }
    if (res.settingList.length != 0) {
      dataModel.settingList = res.settingList
      dataModelCopy.settingList = res.settingList
    }
    if (Object.keys(res.dateMap).length != 0) {
      dataModel.dateMap = res.dateMap
      dataModelCopy.dateMap = res.dateMap
    }
    if (res.statementMap.statementFangxiang==null){
      dataModel.statementMap.statementFangxiang='1'
    }
    if (res.statementMap.cashBankDayBookSort==null){
      dataModel.statementMap.cashBankDayBookSort='1'
    }
    if (res.statementMap.bankStatementSort==null){
      dataModel.statementMap.bankStatementSort='1'
    }
  }).catch(error => {
    console.log('error', error)
  })
}

const handleBus = async (bus)=>{
  if(bus == '0'){
    isEdit.value=false
  }else if (bus == '1'){
    let data = initBasisTabDataDefault();
    dataModel.basisMap = data.basisMap
    dataModel.zhiDanList = data.zhiDanList
    dataModel.pingZhenNumberList = data.pingZhenNumberList
    dataModel.pingZhenControlList = data.pingZhenControlList
    dataModel.settingList = data.settingList
    dataModel.dateMap = data.dateMap
    dataModel.statementMap = data.statementMap
  }else if(bus == '2'){
    isEdit.value=true

    dataModel.basisMap.ccodeLevel = splitLevel(levelPrefix.value, levelSuffix.value)
    if (dataModel.basisMap.taxClass==null) {
      dataModel.basisMap.taxClass = '1'
    }
    modifyBasisTabDBData({modifyModel: JSON.stringify(dataModel),accId: databaseCn.value}).then(res=>{
      console.log('保存成功!')
      pageReload()
    }).catch(error => {
      console.log('error', error)
    })
  }else{
    dataModel.basisMap = dataModelCopy.basisMap
    dataModel.zhiDanList = dataModelCopy.zhiDanList
    dataModel.pingZhenNumberList = dataModelCopy.pingZhenNumberList
    dataModel.pingZhenControlList = dataModelCopy.pingZhenControlList
    dataModel.settingList = dataModelCopy.settingList
    dataModel.dateMap = dataModelCopy.dateMap
    dataModel.statementMap = dataModelCopy.statementMap
    isEdit.value=true
  }
}
const levelPrefix = ref('')
const levelSuffix = ref('')
const standardChange = async (val) => {
  let obj:any = pageParameter.acountStandardList.filter(item => item.id == val)
  if (obj.length > 0) {
    levelPrefix.value = obj[0].tjici/*.replace(/-/g,"")*/
    if (dataModel.basisMap.ccodeLevel != '' && isEdit.value) {
      levelSuffix.value = dataModel.basisMap.ccodeLevel.replace(obj[0].tjici, '').replace(/-/g, "").split('').join('-')
    }
  }
}

const splitLevel = (a, b) => {
  let c = a
  if (b != '')
    c = c + '-' + b
  return c
}
/************************************基础选项************************************/

/************************************未达账项开始************************************/
const editableData = reactive({});
const tableHeight = (document.documentElement.clientHeight)+'px'
const Columns = [
  {
    title: '序号',
    key: 'serialNumber',
    dataIndex: 'serialNumber',
    align: 'center',
    width: '10%'
  },
  {
    title: '起止天数',
    key: 'startDayNumber',
    dataIndex: 'startDayNumber',
    align: 'center',
    slots: {customRender: 'startDayNumber'}
  },
  {
    title: '总天数',
    key: 'totalDayNumber',
    dataIndex: 'totalDayNumber',
    align: 'center',
    width: '20%',
    slots: {customRender: 'totalDayNumber'},
  }
]

const createNeatRow = () => {
  return {
    id: null,
    key: '',
    serialNumber: '',
    agingModel: '',
    accId: '',
    startDayNumber: '',
    totalDayNumber: '',
  }
}
async function assemblyRow(list:any) {
  let rowList:any = []
  if (list.length > 0){
    list.forEach((item,index)=>{
      let row =  createNeatRow()
      row['id'] = item.id
      row['key'] = item.id+''
      row['serialNumber'] = (index+1)+''
      row['agingModel'] = item.agingModel
      row['accId'] = item.accId
      row['startDayNumber'] = item.startDayNumber
      row['totalDayNumber'] = item.totalDayNumber
      rowList.push(row)
    })
  }
  if (list.length < 10){
    for (let i = list.length;i < 10;i++){
      let row =  createNeatRow()
      row['key'] = i+''
      row['serialNumber'] = (i+1) +''
      row['agingModel'] = 'BANK'
      row['accId'] = databaseCn.value
      // 第一次
      if (rowList.length > 0 && i == list.length) row['startDayNumber'] = `${(parseInt(rowList[rowList.length-1].totalDayNumber)+1)}天以上`
      rowList.push(row)
    }
  }
  return rowList
}
const checkAndModifyData =  (key) => {
  try {
    if (hasBlank(editableData[key].totalDayNumber)) throw new Error('总天数为必填项且只能为正整数！')
    let xNum = parseInt(editableData[key].serialNumber)
    if (xNum == 1){
      if (parseInt(editableData[key].totalDayNumber) < 30) throw new Error('每次增加总天数建议不低于30天！')
      editableData[key].startDayNumber = `0-${ editableData[key].totalDayNumber}`
    }else {
      // 获取上一次
      let currList:any = bankStatementDataSource
      let previous:any = currList.value.filter(item => (xNum-1) == item?.serialNumber)[0]
      let prevNum = previous?.totalDayNumber
      if (hasBlank(prevNum))throw new Error(`序号列【${xNum-1}】总天数为必填项且只能为正整数！`)
      if ((parseInt(editableData[key].totalDayNumber) - parseInt(prevNum)) < 30) throw new Error('每次增加总天数建议不低于30天！')
      editableData[key].startDayNumber = `${parseInt(prevNum)+1}-${ editableData[key].totalDayNumber}`
      let nextNum = parseInt(xNum)+1
      // 如果改变的是中间的 重置后面 保留
      if (nextNum != 10){
        let nexts = currList.value.filter(item => nextNum == item?.serialNumber)[0]
        nexts.startDayNumber = (parseInt(editableData[key].totalDayNumber)+1)+'天以上'
      }
    }
    return true
  } catch (e:any) {
    createWarningModal({content: e.message});
    return false
  }
}
const checkPrevFilledIn = (key,currList) => {
  let xNum = currList.value.filter(item => key == item?.key)[0].serialNumber
  if (xNum != 1){ //上一行是否填写
    let previous = currList.value.filter(item => (xNum-1) == item?.serialNumber)[0]
    if (hasBlank(previous?.totalDayNumber)) return true;
  }
  return false;
}
const rowEdit = async (key,type) => {
  let currList:any = bankStatementDataSource
  if (type=='start'){
    // 查看上一行是否填写
    if (!checkPrevFilledIn(key,currList)){
      editableData[key] = cloneDeep(currList.value.filter(item => key === item?.key)[0]);
    }else {
      createWarningModal({title: '温馨提示',content: '请先完善上面空白行！'})
    }
  }else {
    editableData[key].totalDayNumber = editableData[key].totalDayNumber.replaceAll(/[^\d]/g,'')
    if (checkAndModifyData(key)){
      let res =  await save(editableData[key])
      if (res != null) {
        editableData[key].key = res?.id
        editableData[key].id = res?.id
        Object.assign(currList.value.filter(item => key === item.key)[0], editableData[key]);
        delete editableData[key];
        message.success('已进行异步更新！')
      }
    }else {
      delete editableData[key];
    }
  }
}
const bankStatementDataSource = ref([])
async function query(type:string) {   // 查询数据库
  return await useRouteApi(findAllByAccIdModel,{schemaName: database.value})({accId: databaseCn.value, model: type})
}
async function currDataSource() {
  let list = await query('BANK')
  bankStatementDataSource.value = await assemblyRow(list)
  return bankStatementDataSource
}
async function save(data:any) {   // 查询数据库
  return await useRouteApi(saveModel,{schemaName: database.value})(data)
}
/************************************未达账项结束************************************/

/************************************* 2021.10.14 补充改造 ******************************************/
const dynamicAdReload = (obj) => {
  database.value = obj.accountMode
  databaseCn.value = obj.accId
  databaseYear.value = obj.year
  pageReload()
  currDataSource()
}

/************************************* 2021.10.14 补充改造 ******************************************/
</script>
<style scoped="scoped" lang="less">
@import '/@/assets/styles/financial-info-menu2.less';
</style>
