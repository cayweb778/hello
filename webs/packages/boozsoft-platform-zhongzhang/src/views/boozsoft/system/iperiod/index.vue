<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <MenuOutlined style="font-size: 28px;color: #0096c7;" class="container-head-img"/>
        <div class="container-head-title" style="margin-top: 10px;text-align: center;">
          <b class="noneSpan">财务账套会计期间</b>
        </div>
        <div class="ant-btn-group" style="float: right">
          <button type="button" class="ant-btn ant-btn-me">导出</button>
          <button type="button" class="ant-btn ant-btn-me">打印</button>
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent()">退出</button>
        </div>
      </div>
      <div class="app-container-neck">
        <div style="float:left;margin-left: 1em;width: 510px;">
          <AccountPicker theme="three" @reloadTable="dynamicAdReload" style="width: 100%"/>
        </div>
        <div style="display: inline-block;float: left;margin-left: 2%;">

        </div>
        <div style="float: right; margin-left: 10px">
          <a-button class="ant-btn-me">
            <SyncOutlined @click="pageReload()" :style="{ fontSize: '14px' }"/>
          </a-button>
          <!--          <a-popover placement="bottom">
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
        </div>
      </div>
    </div>
    <div class="app-container">
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <LeftTree @select="handleSelect" @quarter="quarterHandle" :isOrg="isOrg"
                  :coCode="databaseCoCode"/>
        <div style="width: calc(100% - 185px);margin-left: .5%;background-color: #f2f2f2"
             :style="{height: (windowHeight-240)+'px',overflowY: 'auto'}">
          <div class="special-border-div">
            <span>基本信息</span>
            <div>
              <span class="acctd-span">启用期间：</span>
              <a-select v-model:value="QjBasicInfo.startPeriod"
                        style="width: 125px;margin: 5px 50px 10px 5px;text-align: center"
                        :style="originateMonth==fiscalYear || !isEdit?{pointerEvents: 'none'}:{}">
                <a-select-option v-for="index in dynamicInterval" :value="index">
                  {{ index }}
                </a-select-option>
              </a-select>
              <template v-if="showQuarter">
                <span class="acctd-span">季度数量：</span>
                <a-select v-model:value="QjBasicInfo_quarterlyNumber"
                          style="width: 125px;text-align: center" @change="QjHandleChange"
                          :style="originateMonth==fiscalYear || !isEdit?{pointerEvents: 'none'}:{}">
                  <a-select-option value="1">
                    1
                  </a-select-option>
                  <a-select-option value="2">
                    2
                  </a-select-option>
                  <a-select-option value="3">
                    3
                  </a-select-option>
                  <a-select-option value="4">
                    4
                  </a-select-option>
                  <a-select-option value="5">
                    5
                  </a-select-option>
                  <a-select-option value="6">
                    6
                  </a-select-option>
                </a-select>
              </template>
              <template v-else>
                <span class="acctd-span">期间数量：</span>
                <a-select v-model:value="QjBasicInfo.periodNum"
                          style="width: 125px;margin: 5px 50px 10px 5px;text-align: center"
                          @change="QjNumHandleChange"
                          :style="!isEdit?{pointerEvents: 'none'}:{}">
                  <a-select-option v-for="index in 5" :value="(index+11)+''">
                    {{ index + 11 }}
                  </a-select-option>
                </a-select>
              </template>

              <div v-if="!isOrg" class="ac-bottom-left" style="float:right;">
                <div class="ant-btn-group">
                  <Button type="primary" @click="startExecBefore('edit')" v-if="!isEdit">修改</Button>
                  <Button type="primary" v-if="isEdit" @click="startExecBefore('modify')">保存
                  </Button>
                  <Button v-if="isEdit" @click="startExecBefore('waiver')">放弃</Button>
                </div>
              </div>
            </div>
          </div>
          <div class="special-border-div" v-show="!showQuarter">
            <span>会计期间</span>
            <div>
              <Table size="small" bordered :data-source="QjDataSource" :columns="initQjColumns(1)"
                     :pagination="false" class="a-table-font-size-14">
                <template #dateEnd="{ text, record }">
                  <div class="editable-cell">
                    <div v-if="editableData[record.key] && editableData[record.key].type == 'YFQJ'"
                         class="editable-cell-input-wrapper">
                      <a-date-picker v-model:value="editableData[record.key].dateEnd"
                                     :disabled-date="disabledDate"
                                     value-format="YYYY-MM-DD" format="YYYY-MM-DD"/>
                      <check-outlined class="editable-cell-icon-check"
                                      @click="yueSave(record.key,'YFQJ')"/>
                    </div>
                    <div v-else class="editable-cell-text-wrapper">
                      {{ text || ' ' }}
                      <edit-outlined
                        v-if="(record.iperiodNum == '01' || record.iperiodNum == QjBasicInfo.periodNum) && isEdit"
                        class="editable-cell-icon" @click="edit(record.key,'YFQJ')"/>
                    </div>
                  </div>
                </template>
              </Table>
            </div>
          </div>
          <div class="special-border-div" v-show="showQuarter">
            <span>季度期间</span>
            <div>
              <Table size="small" bordered :data-source="QjJdDataSource" :columns="initQjColumns(2)"
                     :pagination="false" class="a-table-font-size-14">
                <template #dateStart="{ text, record }">
                  <div class="editable-cell">
                    <div v-if="editableData[record.key] && editableData[record.key].type == 'JD'"
                         class="editable-cell-input-wrapper">
                      <a-select v-model:value="editableData[record.key].dateStart"
                                style="width: 85%">
                        <a-select-option v-for="item in QjDataSource" :value="item.dateStart">
                          {{ item.dateStart }}
                        </a-select-option>
                      </a-select>
                      <!--                        <a-input v-model:value="editableData[record.key].dateStart" @pressEnter="save(record.key,'JD')" />-->
                      <check-outlined class="editable-cell-icon-check"
                                      @click="save(record.key,'JD')"/>
                    </div>
                    <div v-else class="editable-cell-text-wrapper">
                      {{ text || ' ' }}
                      <edit-outlined class="editable-cell-icon" v-if="isEdit"
                                     @click="edit(record.key,'JD')"/>
                    </div>
                  </div>
                </template>
                <template #dateEnd="{ text, record }">
                  <div class="editable-cell">
                    <div v-if="editableData[record.key] && editableData[record.key].type == 'JD'"
                         class="editable-cell-input-wrapper">

                      <a-select v-model:value="editableData[record.key].dateEnd" style="width: 85%">
                        <a-select-option v-for="item in QjDataSource" :value="item.dateEnd">
                          {{ item.dateEnd }}
                        </a-select-option>
                      </a-select>
                      <check-outlined class="editable-cell-icon-check"
                                      @click="save(record.key,'JD')"/>
                    </div>
                    <div v-else class="editable-cell-text-wrapper">
                      {{ text || ' ' }}
                      <edit-outlined class="editable-cell-icon" v-if="isEdit"
                                     @click="edit(record.key,'JD')"/>
                    </div>
                  </div>
                </template>
              </Table>
            </div>
          </div>
        </div>
      </PageWrapper>
      <!--      <div class="app-container-bottom" :style="{height: (windowHeight-40)+'px',overflowY: 'auto'}">
              <div class="ac-bottom-left">
                <div class="ant-btn-group">
                  <button
                    type="button"
                    style="background-color: #0096c7;"
                    class="ant-btn ant-btn-me"
                    @click="startExecBefore('save')"
                    v-if="!isEdit"
                  ><span>新增</span></button>
                  <button
                    type="button"
                    style="background-color: #0096c7;"
                    class="ant-btn ant-btn-me"
                    @click="startExecBefore('edit')"
                    v-if="!isEdit"
                  ><span>修改</span></button>
                  &lt;!&ndash;          <button
                              type="button"
                              style="background-color: #0096c7;"
                              class="ant-btn ant-btn-me"
                               @click="startExecBefore('del')"
                            ><span>删除</span></button>&ndash;&gt;
                  <button
                    type="button"
                    style="background-color: #0096c7;"
                    v-if="isEdit"
                    @click="startExecBefore('modify')"
                    class="ant-btn ant-btn-me"
                  ><span>保存</span></button>
                  <button
                    type="button"
                    v-if="isEdit"
                    @click="startExecBefore('waiver')"
                    class="ant-btn ant-btn-me"
                  ><span>放弃</span></button>
                </div>
                <br/>

              </div>
            </div>-->
    </div>
  </div>
</template>
<script setup="props, {emit}" lang="ts">
import {createVNode, reactive, ref, onMounted, provide, nextTick} from 'vue'
import {
  Select as ASelect,
  Button,
  message,
  Table,
  DatePicker as ADatePicker,
  Popover as APopover,
} from "ant-design-vue";
import {PageWrapper} from '/@/components/Page'

const ASelectOption = ASelect.Option
import {
  SettingFilled,
  EditOutlined,
  SyncOutlined,
  SortAscendingOutlined,
  CheckOutlined,
  SortDescendingOutlined,
  SearchOutlined,
  MenuOutlined
} from '@ant-design/icons-vue';

const windowHeight = (window.innerHeight)
import {compareDate, hasBlank} from "/@/api/task-api/tast-bus-api";
import {
  checkPeriod,
  savePeriod,
  currentAccountYaerQjAndJdList,
  saveSystemQuarterly,
  currentAccountQj2Info,
  modifyPeriodDBDataByAccIdAndYaer, currentAccountQj3Info, currentAccountQjInfo
} from "/@/api/record/system/financial-settings";
import {DateTool, JsonTool} from "/@/api/task-api/tools/universal-tools";
import {useMessage} from "/@/hooks/web/useMessage";
import CustomizeYear from "/@/boozsoft/components/CustomizeYear/CustomizeOpenYear.vue";
import {cloneDeep} from "lodash-es";
import {useUserStore} from "/@/store/modules/user";

const {createWarningModal, createSuccessModal, createConfirm} = useMessage();
const userStore = useUserStore();
import LeftTree from './popup/LeftTree.vue';
import {
  getOrganizeAll,
  groupByOrgPeriodList
} from "/@/api/record/group/im-organize";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker-DATA.vue";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import moment from "moment";

const pageParameter = reactive({showRulesSize: 'MIN', accOrg: ''})
const organizeList = ref([])
const isOrg = ref(false)
const {closeCurrent} = useTabs(router);
const filterOption = (input: string, option: any) => {
  return option.label.indexOf(input) >= 0;
};

/*
onMounted(async () => {
  organizeList.value = await getOrganizeAll()
})
*/
const yearList = ref([])
const showQuarter = ref(false)
const databaseCoCode = ref('')
provide('yearList', yearList)

const dynamicAdReload = async (e) => {
  databaseCn.value = e.accId
  database.value = e.accountMode
  databaseYear.value = e.year
  ccodeLevelStr.value = e.target?.ccodeLevel
  databaseCoCode.value = e.coCode
  isOrg.value = null != e.target?.accGroup && e.target?.accGroup != ''
  message.loading("查询中请稍候。。。")
  // 获取当前账套所以年度
  pageReload()
}
const editableData = reactive({});
const databaseCn = ref('')
const database = ref('')
const databaseYear = ref('')
const ccodeLevelStr = ref('')
const fiscalYear = ref('')
const fiscalYear2 = ref('')
let defalutStartPeriod = ''
const isEdit = ref(false) // true 编辑 false 查看

const QjBasicInfo = ref({
  startPeriod: '202101', periodNum: '12', periodMonth: '01-01'
})
const QjBasicInfo_quarterlyNumber = ref(4)
const QjDataSource = ref([{
  key: '0',
  serialNumber: '1',
  iperiodNum: '1',
  yearMonth: '202101',
  dateStart: '2021-01-01',
  dateEnd: '2021-01-31',
}]);
let QjDataSourceCopy = []
const dynamicInterval = ref([])
const QjJdDataSource = ref([
  {
    key: '0',
    serialNumber: '1',
    iperiodNum: '1',
    yearMonth: '202101',
    dateStart: '2021-01-01',
    dateEnd: '2021-01-31',
    iyear: '2021',
    beiyong1: '1',
    uniqueCode: '1',
    enablePeriod: '0',
  }
]);
provide('quarterList', QjJdDataSource)
const QjNumHandleChange = (value) => {
  let number = QjDataSource.value.length // 总条数
  let thisYear = databaseYear.value
  if (value > number) { // 加
    let s = (value - number)
    let thisa = parseInt(s)
    for (let index = 0; index < s; index++) {
      let thisNumber = (number + index + 1).toString()
      let offset = parseInt('-' + thisa) + 1
      let data = {
        key: `${number + index}`,
        serialNumber: thisNumber,
        iperiodNum: thisNumber,
        yearMonth: thisYear + (thisNumber.length == 2 ? thisNumber : ('0' + thisNumber)),
        dateStart: DateTool().dateStr(new Date(thisYear, 11, 31 + offset)),
        dateEnd: DateTool().dateStr(new Date(thisYear, 11, 31 + offset)),
        iyear: thisYear,
        enablePeriod: '0',
        accountId: databaseCn.value,
      }
      QjDataSource.value.push(data)
      thisa--
    }
    QjDataSource.value[11].dateEnd = DateTool().dateStr(new Date(thisYear, 11, 31 + (parseInt('-' + s))))
  } else { //减
    let s = (number - value)
    for (let index = 0; index < s; index++) {
      QjDataSource.value.pop()
    }
    QjDataSource.value[QjDataSource.value.length - 1].dateEnd = DateTool().dateStr(new Date(thisYear, 11, 31))
  }
  changePeriodNum(QjBasicInfo.value.startPeriod, QjBasicInfo.value.periodNum)
}

const QjHandleChange = (value) => {
  let number = QjJdDataSource.value.length
  if (value > number) { // 加
    let s = (value - number)
    let thisYear = QjBasicInfo.value.startPeriod.substring(0, 4)
    for (let index = 0; index < s; index++) {
      let thisNumber = (number + index + 1).toString()
      QjJdDataSource.value.push(
        {
          key: `${number + index}`,
          serialNumber: thisNumber,
          iperiodNum: thisNumber,
          uniqueCode: thisNumber,
          yearMonth: thisYear + (thisNumber.length == 2 ? thisNumber : ('0' + thisNumber)),
          dateStart: '',
          dateEnd: '',
          iyear: thisYear,
          beiyong1: '1',
          enablePeriod: '0',
          accountId: databaseCn.value,
        }
      )
    }
  } else { //减
    let s = (number - value)
    for (let index = 0; index < s; index++) {
      QjJdDataSource.value.pop()
    }
  }
}
const initQjColumns = (type) => {
  let flag = type == '1' ? true : false
  return [
    {
      title: '序号',
      key: 'serialNumber',
      dataIndex: 'serialNumber',
      align: 'center',
      width: '10%'
    },
    {
      title: flag ? '会计期间' : '季度区间',
      key: 'iperiodNum',
      dataIndex: 'iperiodNum',
      align: 'center',
      width: '15%',
    },
    {
      title: flag ? '系统期间值' : '季度区间值',
      key: 'yearMonth',
      dataIndex: 'yearMonth',
      align: 'center',
      width: '25%',
    },
    {
      title: '开始日期',
      key: 'dateStart',
      dataIndex: 'dateStart',
      align: 'center',
      width: '25%',
      slots: {customRender: 'dateStart'},
    }, {
      title: '结束日期',
      key: 'dateEnd',
      dataIndex: 'dateEnd',
      align: 'center',
      width: '25%',
      slots: {customRender: 'dateEnd'},
    },
  ]
}
const changePeriodNum = (startDate, periodNum) => {
  if (startDate != null) {
    dynamicInterval.value = MonthsBetw(startDate, periodNum)
  }
}

function MonthsBetw(startDate, size) {
  const year = startDate.substring(0, 4)
  let i = 1
  let list = []
  while (i <= parseInt(size)) {
    let num = i;
    if (i < 10) {
      num = '0' + num
    }
    list.push(year + num)
    i++
  }
  return list
}

const yearChange = () => {
  let data = {
    maxYear: fiscalYear.value + '--' + fiscalYear2.value
  }
  initDataSource(data, databaseCn.value)
}
const initDataSource = (data, name) => {
  //数据
  let thisYear = data.maxYear

  // 获取当前期间 以及 季度
  currentAccountYaerQjAndJdList({accId: name, iyear: thisYear}).then(res => {
    QjDataSource.value = []
    res.t1.forEach((item, index) => {
      item.key = `${index}`
      item.serialNumber = `${index + 1}`
      item.yearMonth = fiscalYear.value + (item.iperiodNum.length == 2 ? item.iperiodNum : ('0' + item.iperiodNum))
      item.dateStart = fiscalYear.value + '-' + item.dateStart
      item.dateEnd = fiscalYear.value + '-' + item.dateEnd
      QjDataSource.value.push(item)
    })
    if (!showQuarter.value) {
      QjBasicInfo.value.startPeriod = thisYear.split('-')[0] + '01'
      QjBasicInfo.value.iperiodNum = QjDataSource.value.length
    }
    // yearList.value = [...new Set(res.t1.map(it=>it.iyear))]
    QjDataSourceCopy = JsonTool.parseProxy(QjDataSource.value)
    QjJdDataSource.value = []
    if (res.t2.length == 0) {
      let number = QjBasicInfo_quarterlyNumber.value;
      for (let index = 0; index < number; index++) {
        let thisNumber = (index + 1).toString()
        QjJdDataSource.value.push(
          {
            key: `${index}`,
            serialNumber: thisNumber,
            iperiodNum: thisNumber,
            uniqueCode: thisNumber,
            yearMonth: fiscalYear2.value + (thisNumber.length == 2 ? thisNumber : ('0' + thisNumber)),
            dateStart: '',
            dateEnd: '',
            iyear: fiscalYear2.value,
            beiyong1: '1',
            enablePeriod: '0',
            accountId: name,
          }
        )
      }
    } else {
      QjBasicInfo_quarterlyNumber.value = res.t2.length
      res.t2.forEach((item, index) => {
        item.key = `${index}`
        item.serialNumber = `${index + 1}`
        item.yearMonth = fiscalYear2.value + (item.iperiodNum.length == 2 ? item.iperiodNum : ('0' + item.iperiodNum))
        item.uniqueCode = item.iperiodNum
        QjJdDataSource.value.push(item)
      })
    }
  })
}
const edit = (key: string, type: string) => {
  if (type == 'JD') {
    editableData[key] = cloneDeep(QjJdDataSource.value.filter(item => key === item.key)[0]);
    editableData[key].type = 'JD'
  } else if (type == 'YFQJ') {
    editableData[key] = cloneDeep(QjDataSource.value.filter(item => key === item.key)[0]);
    editableData[key].type = 'YFQJ'
  }
}

const save = async (key: string, type: string) => {
  let handle = null
  if (type == 'JD') {
    Object.assign(QjJdDataSource.value.filter(item => key === item.key)[0], editableData[key]);
    if (checkQjKdData()) {
      // 添加
      handle = saveSystemQuarterly(QjJdDataSource.value)
      // 账套同步
      // await saveQjJdQuarterly(QjJdDataSource.value)
    }
  }
  if (null != handle) {
    handle.then(res => {
      if (res.message === 'ok' || res.length > 0 || res.t1 != null) {
        message.success('会计期间季报系统已同步！')
      } else {
        message.error('会计期间季报系统同步失败！')
      }
      isEdit.value = false
    })
  }
  delete editableData[key];
}

const yueSave = (key: string, type: string) => {
  let data = editableData[key]
  if (data.iperiodNum == '01') {
    let thisDate = new Date(data.dateEnd)
    let thisDay = thisDate.getDate()
    QjDataSource.value[0].dateEnd = data.dateEnd
    QjDataSource.value.forEach(item => {
      let num = parseInt(item.iperiodNum)
      if (num != 1 && num < 12) {
        let year = thisDate.getFullYear();
        let month = thisDate.getMonth() + 1
        let after = null
        let afterDay = DateTool().getLastDay(year, month + 1)
        let pervDay = DateTool().getLastDay(year, month)
        if (thisDay > afterDay) { // 当前日期大于本月最虎一天时 去最后一天
          after = new Date(year, month, afterDay)
        } else if (thisDay <= afterDay) {
          after = new Date(year, month, thisDay)
        }
        item.dateStart = DateTool().dateStr(new Date(year, thisDate.getMonth(), thisDay > pervDay ? ((pervDay) + 1) : (thisDay + 1)))
        item.dateEnd = DateTool().dateStr(after)
        thisDate = after
      } else if (parseInt(item.iperiodNum) == 12) {
        let year = thisDate.getFullYear();
        let month = thisDate.getMonth()
        let after = new Date(year, month, thisDay + 1)
        if (thisDay < 30) {
          item.dateStart = DateTool().dateStr(after)
        } else {
          item.dateStart = year + '-12-01'
        }
      }
    })
  } else {
    QjDataSource.value[parseInt(data.iperiodNum) - 1].dateEnd = data.dateEnd
  }
  delete editableData[key];
}

const checkQjKdData = () => {
  let list = QjJdDataSource.value
  try {
    // 先校验是否填充
    list.forEach(item => {
      if (item.dateStart == '' || item.dateEnd == '') {
        throw new Error('请继续完善季度区间值' + item.yearMonth + '的开启与结束区间!')
      }
    })
    // 后校验日期规则
    for (let index = 0; index < list.length; index++) {
      let item = list[index]
      if (compareDate(item.dateStart, item.dateEnd)) {
        throw new Error('数据不规范！季度区间值' + item.yearMonth + '的开始区间不能大于结束期间,请重新修改!')
      }
      if (0 != index && compareDate(list[index - 1].dateEnd, item.dateStart)) {
        throw new Error('数据不规范！季度区间值' + item.yearMonth + '的开始区间不能小于上一季度结束期间,请重新修改!')
      }
    }
    return true
  } catch (e) {
    if (e.message.indexOf('数据不规范！') != -1) {
      createWarningModal({content: e.message});
    } else {
      message.success(e.message)
    }
    return false
  }
}

async function startExecBefore(type) {
  if (type == 'save') { // 检查年度是否已创建
    /*(`<template><CustomizeYear v-model:value="naturalYear" readonly="false"/></template>`)*/
    createConfirm({
      iconType: 'warning',
      title: '选择添加条件',
      content: createVNode(CustomizeYear),
      onOk: async () => {
        let year = document.getElementsByClassName("customize-year")[0]?.getElementsByClassName('ant-input')[0]?.value
        let num = document.getElementsByClassName("customize-select1")[0]?.getElementsByClassName('ant-select-selection-item')[0]?.textContent
        let start = document.getElementsByClassName("customize-select2")[0]?.getElementsByClassName('ant-select-selection-item')[0]?.textContent
        if (!hasBlank(year)) {
          let data = {
            type: 'save',
            accId: databaseCn.value,
            year: year,
          }
          let size = await checkPeriod(data)
          if (null != size && size == 0) {
            // 后台创建periodNum
            data['periodNum'] = num
            data['startPeriod'] = start
            data['mode'] = database.value
            data['super'] = userStore.getUserInfo?.id
            data['ccodeLevel'] = ccodeLevelStr.value
            let res = await savePeriod(data)
            if (null != res && res > 0) {
              createSuccessModal({
                title: '执行结果',
                content: `${data.year}年度会计期间系统已创建,可通过刷新页面进行切换年度修改！`
              })
            } else {
              createWarningModal({title: '执行结果', content: `${data.year}年度会计期间系统新增失败，请稍候再试！`})
            }
          } else {
            createWarningModal({title: '温馨提示', content: `${data.year}年度会计期间系统已存在，不能进行重复添加！`})
          }
        }
      }, onCancel: async () => {
      }
    });
  } else if (type == 'edit') { // 检查是否已使用
    let data = {
      type: 'edit',
      accId: database.value,
      year: QjBasicInfo.value.startPeriod.substring(0, 4),
    }
    let size = await checkPeriod(data)
    if (null != size && size == 0) {
      isEdit.value = true
      changePeriodNum(QjBasicInfo.value.startPeriod, QjBasicInfo.value.periodNum)
    } else {
      createWarningModal({title: '温馨提示', content: `${data.year}年度会计期间已被使用，不能进行修改！`})
    }
  } else if (type == 'del') { //删除

  } else if (type == 'waiver') { //还原
    isEdit.value = false
    Object.keys(editableData).forEach(key => delete editableData[key])
    yearChange()
  } else {
    // 组装数据添加
    let year = QjBasicInfo.value.startPeriod.substring(0, 4)
    if (JsonTool.json(QjDataSource.value) == JsonTool.json(QjDataSourceCopy) && defalutStartPeriod == QjBasicInfo.value.startPeriod) {
      createWarningModal({title: '温馨提示', content: `${year}年度会计期间暂未发现任何修改！`})
    } else {
      let data = []
      QjDataSource.value.forEach(item => {
        data.push({
          periodNum: item.periodNum,
          period: item.yearMonth,
          dateStart: item.dateStart.replace(year + "-", ""),
          dateEnd: item.dateEnd.replace(year + "-", "")
        })
      })
      //console.log({modifyModel: JSON.stringify(data),accId: databaseCn.value,periodNum: QjBasicInfo.value.periodNum,startPeriod: QjBasicInfo.value.startPeriod})
      modifyPeriodDBDataByAccIdAndYaer({
        modifyModel: JSON.stringify(data),
        accId: databaseCn.value,
        periodNum: QjBasicInfo.value.periodNum,
        startPeriod: QjBasicInfo.value.startPeriod
      }).then(res => {
        message.success('会计期间系统已同步！')
        yearChange()
        isEdit.value = false
      }).catch(error => {
        message.error('会计期间系统同步失败！')
      })
    }
  }
}

const originateMonth = ref('')
const pageReload = () => {
  currentAccountQjInfo({accId: databaseCn.value}).then(res => {
    QjBasicInfo.value = res
    defalutStartPeriod = res?.startPeriod
    fiscalYear.value = res.maxYear
    fiscalYear2.value = res.maxYear
    yearList.value = res.yearList.sort((a, b) => parseInt(a) - parseInt(b))
    if (yearList.value.length > 0) originateMonth.value = yearList.value[0]
  })
}

const handleSelect = (o) => {
  showQuarter.value = o.type == 'quarter'
  fiscalYear.value = o.value
  if (o?.reload) {
    pageReload()
  } else {
    initDataSource({maxYear: o.value + "--" + o.value}, databaseCn.value)
  }
}
const quarterHandle = (o) => {
  if (o.type == 'add') {
    QjHandleChange(QjJdDataSource.value.length + o.len)
  } else {
    QjHandleChange(QjJdDataSource.value.length - o.len)
  }
}

const disabledDate = (current) => {
  // 获取区间最小区间
  let date = null
  if (editableData[0] != null) {
    date = editableData[0].dateEnd
  } else {
    date = editableData[parseInt(QjBasicInfo.value.periodNum) - 1].dateEnd
  }
  let min = moment(date.substring(0, 7) + '-01', 'YYYY-MM-DD')
  let max = moment(date, 'YYYY-MM-DD').endOf('month')
  return current < min || current > max
};
/************************************会计期间************************************/
</script>
<style scoped="scoped" lang="less">
@import '/@/assets/styles/global-menu-index.less';

.special-border-div {
  position: relative;
  border: 1px #acabab solid;
  margin: 20px 1%;

  > span {
    position: absolute;
    top: -12px;
    left: 50px;
    font-size: 15px;
    background: #f2f2f2;
    padding: 0 15px;
    font-weight: bold;
    color: #666666;
  }

  > div {
    margin: 15px;

    ul {
      li {
        display: inline-block;
        width: 20%;
        font-weight: bold;
        min-width: 240px;

        span:nth-of-type(1) {
          color: #81817d;
        }

        span:nth-of-type(2) {
          color: black;
          margin-left: 2em;
        }
      }
    }
  }
}
</style>
<style scoped>
.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 16px !important;
  padding: 5px 8px !important;
}

.a-table-font-size-16 :deep(th), .a-table-font-size-12 :deep(th) {
  text-align: center !important;
  font-weight: bold;
  background-color: #f1f1f1;
}

.a-table-font-size-16 :deep(.ant-checkbox-inner), .a-table-font-size-12 :deep(.ant-checkbox-inner) {
  border: 1px solid #c9c9c9;
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
