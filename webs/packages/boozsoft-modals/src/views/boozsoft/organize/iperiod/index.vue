<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <MenuOutlined style="font-size: 28px;color: #0096c7;" class="container-head-img"/>
        <div class="container-head-title" style="margin-top: 10px;text-align: center;">
          <b class="noneSpan">组织财务会计期间设置</b>
        </div>
        <div class="ant-btn-group" style="float: right">
          <button type="button" class="ant-btn ant-btn-me">导出</button>
          <button type="button" class="ant-btn ant-btn-me">打印</button>
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent()">退出</button>
        </div>
      </div>
      <div class="app-container-neck">
        <div style="float:left;margin-left: 1em;">
                <span>组织：</span>
                <a-select v-model:value="pageParameter.accOrg" @change="pageChange"  show-search
                          :options="organizeList.map(it=>({value: it.uniqueCode,label: it.orgCode+' '+ it.orgSimpName}))"
                :filter-option="filterOption" style="width: 250px;margin-top: -10px;margin-left: 10px">
                  <template #suffixIcon><search-outlined /></template>
                </a-select>
        </div>
        <div style="display: inline-block;float: left;margin-left: 2%;">

        </div>
        <div style="float: right; margin-left: 10px">
          <a-button class="ant-btn-me">
            <SyncOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
          <a-popover placement="bottom">
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
      </div>
    </div>
    <div class="app-container">
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <LeftTree @select="handleSelect" @reload="reloadLeftData" :accOrg="pageParameter.accOrg"/>
        <div style="width: calc(100% - 180px);margin-left: .5%;background-color: #f2f2f2;" :style="{height: (windowHeight-240)+'px',overflowY: 'auto'}">
          <div class="special-border-div">
            <span>基本信息</span>
            <div>
              <span class="acctd-span">启用期间：</span>
              <a-select v-model:value="QjBasicInfo.startPeriod"
                        style="width: 125px;margin: 5px 50px 10px 5px;text-align: center"
                        :style="!isEdit?{pointerEvents: 'none'}:{}">
                <a-select-option v-for="index in dynamicInterval" :value="index">
                  {{ index }}
                </a-select-option>
              </a-select>
              <template v-if="showQuarter">
                <span class="acctd-span">季度数量：</span>
                <a-select v-model:value="QjBasicInfo_quarterlyNumber"
                          style="width: 125px;text-align: center" @change="QjHandleChange"
                          :style="!isEdit?{pointerEvents: 'none'}:{}">
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
                          style="width: 125px;margin: 5px 50px 10px 5px;text-align: center" @change="QjNumHandleChange"
                          :style="!isEdit?{pointerEvents: 'none'}:{}">
                  <a-select-option v-for="index in 5" :value="(index+11)+''">
                    {{ index + 11 }}
                  </a-select-option>
                </a-select>
              </template>
            </div>
          </div>
          <div class="special-border-div" v-if="!showQuarter">
            <span>会计期间</span>
            <div>
              <Table size="small" bordered :data-source="QjDataSource" :columns="initQjColumns(1)"
                     :pagination="false" class="a-table-font-size-14">
                <template #dateEnd="{ text, record }">
                  <div class="editable-cell">
                    <div v-if="editableData[record.key] && editableData[record.key].type == 'YFQJ'"
                         class="editable-cell-input-wrapper">
                      <a-date-picker v-model:value="editableData[record.key].dateEnd"
                                     value-format="YYYY-MM-DD" format="YYYY-MM-DD"/>
                      <check-outlined class="editable-cell-icon-check"
                                      @click="yueSave(record.key,'YFQJ')"/>
                    </div>
                    <div v-else class="editable-cell-text-wrapper">
                      {{ text || ' ' }}
                      <edit-outlined v-if="record.iperiodNum == '01' && isEdit"
                                     class="editable-cell-icon" @click="edit(record.key,'YFQJ')"/>
                    </div>
                  </div>
                </template>
              </Table>
            </div>
          </div>
          <div class="special-border-div" v-else>
            <span>季度期间</span>
            <div>
              <Table size="small" bordered :data-source="QjJdDataSource" :columns="initQjColumns(2)"
                     :pagination="false" class="a-table-font-size-14">
                <template #dateStart="{ text, record }">
                  <div class="editable-cell">
                    <div v-if="editableData[record.key] && editableData[record.key].type == 'JD'"
                         class="editable-cell-input-wrapper">
                      <a-select v-model:value="editableData[record.key].dateStart" style="width: 85%">
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
import {createVNode, reactive, ref, onMounted, provide} from 'vue'
import {Select as ASelect, Button as AButton, message, Table, DatePicker as ADatePicker,  Popover as APopover} from "ant-design-vue";
import {PageWrapper} from '/@/components/Page'
const ASelectOption = ASelect.Option
import {SettingFilled, EditOutlined,SyncOutlined,SortAscendingOutlined, CheckOutlined, SortDescendingOutlined,SearchOutlined,MenuOutlined} from '@ant-design/icons-vue';
const windowHeight = (window.innerHeight)
import {compareDate,  hasBlank} from "/@/api/task-api/tast-bus-api";
import {
  checkPeriod,
  savePeriod,
  currentAccountYaerQjAndJdList,
  saveSystemQuarterly,
  currentAccountQj2Info,
  modifyPeriodDBDataByAccIdAndYaer,
} from "/@/api/record/system/financial-settings";
import {DateTool, JsonTool} from "/@/api/task-api/tools/universal-tools";
import {useMessage} from "/@/hooks/web/useMessage";
import CustomizeYear from "/@/boozsoft/components/CustomizeYear/CustomizeOpenYear.vue";
import {cloneDeep} from "lodash-es";
import {useUserStore} from "/@/store/modules/user";
const {createWarningModal,createSuccessModal,createConfirm} = useMessage();
const userStore = useUserStore();
import LeftTree from './popup/LeftTree.vue';
import {
  getOrganizeAll,
  groupByOrgPeriodIyear,
  groupByOrgPeriodList
} from "/@/api/record/group/im-organize";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
const pageParameter = reactive({showRulesSize: 'MIN', accOrg: ''})
const organizeList = ref([])
const {closeCurrent} = useTabs(router);
const filterOption = (input: string, option: any) => {
  return option.label.indexOf(input) >= 0;
};

onMounted(async () => {
  organizeList.value = await getOrganizeAll()
  if (organizeList.value.length>0) {
    pageParameter.accOrg = organizeList.value[0].uniqueCode
    pageChange(pageParameter.accOrg)
  }
})

const dynamicAdReload = async (e) => {
  databaseCn.value = e.accId
  database.value = e.accountMode
  databaseYear.value = e.year
  ccodeLevelStr.value = e.target?.ccodeLevel
  message.loading("查询中请稍候。。。")

/*  currentAccountQj2Info({accId: databaseCn.value,year: databaseYear.value}).then(res => {
    QjBasicInfo.value = res
    defalutStartPeriod = res?.startPeriod
    fiscalYear.value =  res.maxYear
    fiscalYear2.value = res.maxYear
    res.maxYear = fiscalYear.value + '--' + fiscalYear2.value
    initDataSource(res, databaseCn.value);
  })*/
}
const editableData = reactive({});
const databaseCn = ref('')
const database = ref('')
const databaseYear = ref('')
const ccodeLevelStr = ref('')
const fiscalYear = ref('')
const fiscalYear2 = ref('')
let defalutStartPeriod =''
const isEdit = ref(false) // true 编辑 false 查看

const QjBasicInfo = ref({
  startPeriod: '2021-01', periodNum: '12'
})
const QjBasicInfo_quarterlyNumber = ref(4)
const QjDataSource = ref([]);
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

const QjNumHandleChange = (value) => {
  let number = QjDataSource.value.length // 总条数
    let thisYear = databaseYear.value
  if (value > number) { // 加
    let s = (value - number)
    let thisa = parseInt(s)
    for (let index = 0; index < s; index++) {
      let thisNumber = (number + index + 1).toString()
      let offset =   parseInt('-'+thisa) +1
      let data = {
        key: `${number + index}`,
        serialNumber: thisNumber,
        iperiodNum: thisNumber,
        yearMonth: thisYear + (thisNumber.length == 2 ? thisNumber : ('0' + thisNumber)),
        dateStart: DateTool().dateStr(new Date(thisYear,11,31+offset)),
        dateEnd: DateTool().dateStr(new Date(thisYear,11,31+offset)),
        iyear: thisYear,
        enablePeriod: '0',
        accountId: databaseCn.value,
      }
      QjDataSource.value.push(data)
      thisa--
    }
    QjDataSource.value[11].dateEnd = DateTool().dateStr(new Date(thisYear,11,31+(parseInt('-'+s))))
  } else { //减
    let s = (number - value)
    for (let index = 0; index < s; index++) {
      QjDataSource.value.pop()
    }
    QjDataSource.value[QjDataSource.value.length-1].dateEnd = DateTool().dateStr(new Date(thisYear,11,31))
  }
  changePeriodNum(QjBasicInfo.value.startPeriod,QjBasicInfo.value.periodNum)
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
      width: '80px'
    },
    {
      title: flag ? '会计期间' : '季度区间',
      key: 'iperiodNum',
      dataIndex: 'iperiodNum',
      align: 'center',
      width: '120px',
    },
    {
      title: flag ? '系统期间值' : '季度区间值',
      key: 'yearMonth',
      dataIndex: 'yearMonth',
      align: 'center',
      width: '150px',
    },
    {
      title: '开始日期',
      key: 'dateStart',
      dataIndex: 'dateStart',
      align: 'center',
      width: '120px',
      slots: {customRender: 'dateStart'},
    }, {
      title: '结束日期',
      key: 'dateEnd',
      dataIndex: 'dateEnd',
      align: 'center',
      width: '120px',
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
const edit = (key: string,type:string) => {
  if(type=='JD'){
    editableData[key] = cloneDeep(QjJdDataSource.value.filter(item => key === item.key)[0]);
    editableData[key].type = 'JD'
  }else if (type == 'YFQJ'){
    editableData[key] = cloneDeep(QjDataSource.value.filter(item => key === item.key)[0]);
    editableData[key].type = 'YFQJ'
  }
}

const save = async (key: string,type:string) => {
  let handle = null
 if(type=='JD'){
    Object.assign(QjJdDataSource.value.filter(item => key === item.key)[0], editableData[key]);
    if (checkQjKdData()){
      // 添加
      handle = saveSystemQuarterly(QjJdDataSource.value)
      // 账套同步
      // await saveQjJdQuarterly(QjJdDataSource.value)
    }
  }
  if (null != handle){
    handle.then(res=>{
      if (res.message === 'ok' || res.length > 0 || res.t1 != null){
        message.success('会计期间季报系统已同步！')
      }else {
        message.error('会计期间季报系统同步失败！')
      }
      isEdit.value  = false
    })
  }
  delete editableData[key];
}

const yueSave = (key: string,type:string) => {
  let data = editableData[key]
  let thisDate = new Date(data.dateEnd)
  let thisDay = thisDate.getDate()
  QjDataSource.value[0].dateEnd = data.dateEnd
  QjDataSource.value.forEach(item=>{
    let num = parseInt(item.iperiodNum)
    if(num != 1 && num  <  12){
      let year = thisDate.getFullYear();
      let month = thisDate.getMonth()+1
      let after = null
      let afterDay = DateTool().getLastDay(year,month+1)
      let pervDay =  DateTool().getLastDay(year,month)
      if (thisDay > afterDay){ // 当前日期大于本月最虎一天时 去最后一天
        after =  new Date(year,month,afterDay)
      }else if (thisDay <= afterDay ){
        after =   new Date(year,month,thisDay)
      }
      item.dateStart = DateTool().dateStr(new Date(year,thisDate.getMonth(),thisDay > pervDay?((pervDay)+1):(thisDay+1)))
      item.dateEnd = DateTool().dateStr(after)
      thisDate = after
    }else if (parseInt(item.iperiodNum) == 12){
      let year = thisDate.getFullYear();
      let month = thisDate.getMonth()
      let after = new Date(year,month,thisDay+1)
      if (thisDay<30){
        item.dateStart = DateTool().dateStr(after)
      }else {
        item.dateStart = year+'-12-01'
      }
    }
  })
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
       let year =  document.getElementsByClassName("customize-year")[0]?.getElementsByClassName('ant-input')[0]?.value
       let num =  document.getElementsByClassName("customize-select1")[0]?.getElementsByClassName('ant-select-selection-item')[0]?.textContent
       let start =  document.getElementsByClassName("customize-select2")[0]?.getElementsByClassName('ant-select-selection-item')[0]?.textContent
        if (!hasBlank(year)){
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
              createSuccessModal({title: '执行结果', content: `${data.year}年度会计期间系统已创建,可通过刷新页面进行切换年度修改！`})
            }else {
              createWarningModal({title: '执行结果', content: `${data.year}年度会计期间系统新增失败，请稍候再试！`})
            }
          } else {
            createWarningModal({title: '温馨提示', content: `${data.year}年度会计期间系统已存在，不能进行重复添加！`})
          }
        }
      },onCancel: async () => {
      }
    });
  } else if (type == 'edit') { // 检查是否已使用
    let data = {
      type: 'edit',
      accId: database.value,
      year: databaseYear.value,
    }
    let size = await checkPeriod(data)
    if (null != size && size == 0) {
      isEdit.value = true
      changePeriodNum(QjBasicInfo.value.startPeriod,QjBasicInfo.value.periodNum)
    } else {
      createWarningModal({title: '温馨提示', content: `${data.year}年度会计期间已被使用，不能进行修改！`})
    }
  } else if (type == 'del') { //删除

  } else if (type == 'waiver') { //还原
    isEdit.value = false
    Object.keys(editableData).forEach(key=>delete editableData[key])
    yearChange()
  } else {
    // 组装数据添加
    if (JsonTool.json(QjDataSource.value) == JsonTool.json(QjDataSourceCopy) && defalutStartPeriod == QjBasicInfo.value.startPeriod){
      createWarningModal({title: '温馨提示', content: `${databaseYear.value}年度会计期间暂未发现任何修改！`})
    }else {
      let data = []
      QjDataSource.value.forEach(item=>{
        data.push({periodNum: item.periodNum,period: item.yearMonth,dateStart: item.dateStart.replace(databaseYear.value+"-",""),dateEnd: item.dateEnd.replace(databaseYear.value+"-","")})
      })
      modifyPeriodDBDataByAccIdAndYaer({modifyModel: JSON.stringify(data),accId: databaseCn.value,periodNum: QjBasicInfo.value.periodNum,startPeriod: QjBasicInfo.value.startPeriod}).then(res=>{
        message.success('会计期间系统已同步！')
        yearChange()
        isEdit.value = false
      }).catch(error => {
        message.error('会计期间系统同步失败！')
      })
    }
  }
}
const handleSelect = (o) => {
  showQuarter.value = o.type == 'quarter'
  if (null == o.value){
    pageChange(o.orgId)
  }else {
    fiscalYear.value = o.value
    if (!showQuarter.value){
      let last = orgPeriodList.value.filter(it=>it.enablePeriod=='1' && it.iyear ==o.value)
      console.log(last)
      QjBasicInfo.value.startPeriod = o.value+'-'+(last.length == 0?'01':last[0]?.iperiodNum)
      QjDataSource.value = []
      let a =  JsonTool.parseProxy(orgPeriodList.value.filter(it=>it.iyear == o.value))
      QjBasicInfo.value.periodNum = a.length
      a.forEach((item, index) => {
        item.key = `${index}`
        item.serialNumber = `${index + 1}`
        item.yearMonth = fiscalYear.value + (item.iperiodNum.length == 2 ? item.iperiodNum : ('0' + item.iperiodNum))
        item.dateStart = fiscalYear.value + '-' + item.dateStart
        item.dateEnd = fiscalYear.value + '-' + item.dateEnd
        QjDataSource.value.push(item)
      })
    }
  }
}

const yearList = ref([])
const orgPeriodList = ref([])
const showQuarter = ref(false)
provide('yearList',yearList)
const pageChange = async (v) => {
  // 查询指定组织所有期间 升序
  let list = await groupByOrgPeriodList(v)
  orgPeriodList.value = list;
  if (null != list && list.length > 0)
  yearList.value = [...new Set(list.map(it=>it.iyear))]
}

const reloadLeftData = async (o) => {
 await pageChange(o.orgId)
}
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
.a-table-font-size-16 :deep(.ant-checkbox-inner), .a-table-font-size-12 :deep(.ant-checkbox-inner)  {
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
