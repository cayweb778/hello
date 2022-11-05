<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="自定义结转模板设置"
    @ok="handleOk()"
    @register="register"
    :canFullscreen="false"
  >
    <div
      class="carry-open-content"
      style="height: 500px"
    >
      <div class="open-content">
        <div class="open-content-up">
          <div class="up-div">
            <label>模板编码：</label>
            <a-input v-model:value="formItems.templateNum" placeholder="请输入模板编码"
                     style="width: 130px"/>
          </div>
          <div class="up-div">
            <label>模板名称：</label>
            <a-input v-model:value="formItems.templateName" placeholder="请输入模板名称"
                     style="width: 220px"/>
          </div>
          <div class="up-div">
            <label>结转类型：</label>
            <a-select v-model:value="formItems.carryOverType"
                      @change="formItems.entryList = [];addRow(formItems.carryOverType)"
                      placeholder="支票类型" style="width: 120px;">
              <a-select-option value="1">公式结转</a-select-option>
              <a-select-option value="2">对应结转</a-select-option>
            </a-select>
            <!--     disabled="true"       -->
          </div>
        </div>
        <div class="open-content-down">
          <table id="subjectTableId1" class="subjectTable" v-if="formItems.carryOverType==='1'">
            <thead>
            <tr>
              <th style="width:30%">摘要</th>
              <th style="width:40%">会计科目</th>
              <th style="width:8%">方向</th>
              <th style="width:22%">金额</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="(entry,index) in formItems.entryList">
              <td><span class="symbol-span" style="float: left;" @click="addRow('1')">✚</span>
                <a-input v-model:value="entry.summary" style="width: 88%"/>
              </td>
              <td>
                <a-select
                  show-search
                  :filter-option="filterOption"
                  v-model:value="entry.accountSubjectNum" class="down-select">
                  <a-select-option v-for="row in (pageParameter.lastKemuList)"
                                   :value="row.ccode+'--'+row.ccodeName">
                    {{ row.ccode }} {{ row.ccodeName }}
                  </a-select-option>
                </a-select>
              </td>
              <td>
                <a-select v-model:value="entry.direction" class="down-select">
                  <a-select-option value="1">借</a-select-option>
                  <a-select-option value="2">贷</a-select-option>
                </a-select>
              </td>
              <td>
                <a-input v-model:value="entry.amountFormula" style="width: 88%;text-align: center;"
                         readonly=""
                         @click="openForePage({data: entry.entryFormulas,id: index})"/>
                <span class="symbol-span" style="float: right;" @click="delRow(index)">✖</span>
              </td>
            </tr>
            </tbody>
          </table>
          <table id="subjectTableId2" class="subjectTable" v-if="formItems.carryOverType==='2'">
            <thead>
            <tr>
              <th style="width:20%">摘要</th>
              <th style="width:27%">会计科目</th>
              <th style="width:20%">辅助核算</th>
              <th style="width:10%">转账方式</th>
              <th style="width:14%">取数方式</th>
              <th style="width:9%">百分比(%)</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="(entry,index) in formItems.entryList">
              <td><span class="symbol-span" style="float: left;" @click="addRow('2')">✚</span>
                <a-input v-model:value="entry.summary" style="width: 88%"/>
              </td>
              <td>
                <a-select
                  show-search
                  style="width: 200px"
                  :filter-option="filterOption"
                  v-model:value="entry.accountSubjectNum" class="down-select">
                  <a-select-option
                    v-for="row in (entry.transferMethod=='2'?pageParameter.allKemuList:pageParameter.lastKemuList)"
                    :value="row.ccode+'--'+row.ccodeName">{{ row.ccode }} {{ row.ccodeName }}
                  </a-select-option>
                </a-select>
              </td>
              <td>
                <Popover placement="bottom" trigger="click" v-model:visible="entry.visible">
                  <a-input v-model:value="entry.auxiliaryAccountingName" readonly="readonly"  :disabled="entry.accountSubjectNum == ''" style="width: 92%"
                         class="accountCodeInput">
                  </a-input>
                  <template #content>
                    <CustomizeAssist :allKemuList="pageParameter.allKemuList" :entry="entry" @setValue="(v)=>{entry.auxiliaryAccountingName=v.name;entry.auxiliaryAccountingNum=v.value;entry.visible = false}" />
                  </template>
                </Popover>
              </td>
              <td>
                <a-select v-model:value="entry.transferMethod" :default-value="1"
                          class="down-select">
                  <a-select-option value="1">转入</a-select-option>
                  <a-select-option value="2">转出</a-select-option>
                </a-select>
              </td>
              <td>
                <a-select v-model:value="entry.direction" class="down-select">
                  <a-select-option v-for="row in getList(entry)" :value="row.value">{{
                      row.label
                    }}
                  </a-select-option>
                </a-select>
              </td>
              <td>
                <a-input v-model:value="entry.percentage" style="width: 76%;"
                         @blur="numberReplace(entry)"/>
                <span class="symbol-span" style="float: right;" @click="delRow(index)">✖</span></td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <formula @save="saveFormula" @register="registerFormulaPage"/>
  </BasicModal>
</template>

<script setup=" {content}" lang="ts">
import formula from './formula.vue'
import {provide, reactive, unref} from 'vue'
import {BasicModal, useModal, useModalInner} from '/@/components/Modal'
import {findAllKeMuCode, carryoverCheckApi} from '/@/api/record/system/definition-correspond'
import {useMessage} from "/@/hooks/web/useMessage";

const [registerFormulaPage, {openModal: openFormulaPage}] = useModal()

const {
  createConfirm,
  createSuccessModal,
  createInfoModal,
  createErrorModal,
  createWarningModal,
} = useMessage()
import {
  SearchOutlined
} from '@ant-design/icons-vue';
import {
  Input as AInput,
  Select as ASelect,Popover
} from "ant-design-vue";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {hasBlank} from "/@/api/task-api/tast-bus-api";
import CustomizeAssist
  from "/@/views/boozsoft/system/definition-customize/popup/CustomizeAssist.vue";
import {DateTool} from "/@/api/task-api/tools/universal-tools";
import {useUserStoreWidthOut} from "/@/store/modules/user";

const AInputSearch = AInput.Search;
const ASelectOption = ASelect.Option;
const emit = defineEmits(['register', 'save'])
const props = defineProps(['dynamicTenantId'])
const userStore = useUserStoreWidthOut();
const formItems = reactive({
  id: null,
  templateNum: '',
  templateName: '',
  carryOverType: '1',
  carryOverModule: '2',
  entryList: [],
  carryOverEntries: '',
  orderNum: '',
  kmtype: '',createDate:'',createMan:''
})

const pageParameter = reactive({
  allKemuList: [],
  lastKemuList: []
})

provide('allKemuList',pageParameter.allKemuList)

let changeBeforeModel: any = {}

const [register, {closeModal}] = useModalInner(async (data) => {
  // 修改初始化
  if (pageParameter.lastKemuList.length == 0 || pageParameter.allKemuList.length == 0) {
    let res = await useRouteApi(findAllKeMuCode, {schemaName:props.dynamicTenantId})({'year': 2021})
    pageParameter.lastKemuList = res.t1
    pageParameter.allKemuList = res.t2
  }
  formItems.createMan =  userStore.getUserInfo?.realName;
  formItems.createDate = DateTool().nowDate()
  formItems.entryList = []
  addRow(formItems.carryOverType)
  // changeBeforeModel = JSON.parse(JSON.stringify(formItems))
  if (hasBlank(data.data.id)) return false
  formItems.id = data.data.id
  formItems.templateNum = data.data.templateNum
  formItems.templateName = data.data.templateName
  formItems.carryOverType = data.data.carryOverType
  formItems.carryOverModule = data.data.carryOverModule
  formItems.orderNum = data.data.orderNum
  formItems.kmtype = data.data.kmtype
  formItems.entryList = JSON.parse(data.data.carryOverEntries)
  formItems.carryOverEntries = data.data.carryOverEntries
  formItems.createMan = data.data?.createMan
  formItems.createDate = data.data?.createDate
  return null;

})


const openForePage = (map: any) => {
  let openData = {entryFormulas: [], id: map.id, lastKemuList: []}
  if (map.data !== '') {
    openData.entryFormulas = JSON.parse(map.data)
  }
  openData.lastKemuList = pageParameter.lastKemuList
  openFormulaPage(true, {
    data: openData
  })
}

function getList(obj) {
  if (obj.transferMethod == '1') {
    obj.direction = ''
    return [];
  } else if (obj.transferMethod = '2') {
    return [{value: '0', label: '余额'}, {value: '1', label: '借方余额'}, {value: '2', label: '贷方余额'}, {value: '3', label: '借方本期发生'}, {value: '4', label: '贷方本期发生'}, {value: '5', label: '借方累计发生'}, {value: '6', label: '贷方累计发生'}];
  }
}

let isChanged: boolean = false

async function handleOk() {
  if (formItems.templateNum === '') {
    createWarningModal({content: '结转模板编码为必填项！'});
  } else if (formItems.templateName === '') {
    createWarningModal({content: '结转模板名称为必填项！'});
  } else {
    let res = await useRouteApi(carryoverCheckApi, {schemaName: props.dynamicTenantId})({
      id: formItems.id,
      carryOverModule: formItems.carryOverModule,
      templateNum: formItems.templateNum,
      templateName: formItems.templateNum
    })
    if (null != res && null != res.message && res.message != '') {
      createWarningModal({content: res.message});
    } else {
      let dataList = formItems.entryList
      if (dataList.length > 1) {
        try {
          let zr = 100
          dataList.forEach((item, index) => {
            if (item.accountSubjectNum == '' || item.summary == '') {
              throw new Error('结转分录必填项：摘要、会计科目不能为空！')
            } else if ((formItems.carryOverType == '1') && (item.entryFormulas == '')) {
              throw new Error('公式结转分录必填项：摘要、会计科目与公式内容不能为空！')
            } else if ((formItems.carryOverType == '2') && item.transferMethod == '2' && item.direction == '') {
              throw new Error('对应结转分录必填项：转出取数方式不能为空！')
            }
            let temps = item.accountSubjectNum.split('--')
            //let auxs = item.auxiliaryAccountingNum.split('--')
            item.accountSubjectName = temps[1]
            item.accountSubjectNum = temps[0]
            //item.auxiliaryAccountingName = auxs[1]
            //item.auxiliaryAccountingNum = auxs[0]
            if (formItems.carryOverType == '2' && item.transferMethod == '1'){
              zr -= parseFloat(item?.percentage || 0)
            }
          })
          if (zr != 0) throw new Error('总的转入比例必须等于100%！')
          formItems.entryList = []
          formItems.carryOverEntries = JSON.stringify(dataList)
          emit('save', unref(formItems))
          closeModal()
          return true
        } catch (e) {
          createWarningModal({content: e.message});
        }
      } else {
        createWarningModal({content: '结转分录不能少于两行！'});
      }
    }
  }
  return false
}

async function addRow(type) {
  if (type == '2') {
    let row = {
      'summary': '',
      'accountSubjectNum': '',
      'accountSubjectName': '',
      'auxiliaryAccountingNum': '',
      'auxiliaryAccountingName': '',
      'transferMethod': '1',
      'direction': '',
      'percentage': '100',
    }
    formItems.entryList.push(row)
  } else {
    let row = {
      'summary': '',
      'accountSubjectNum': '',
      'accountSubjectName': '',
      'direction': '1',
      'amountFormula': '',
      'entryFormulas': '',
    }
    formItems.entryList.push(row)
  }
}

async function delRow(id) {
  if (formItems.entryList.length < 2) {
    // 最后一条
    return false
  }
  let indexs = formItems.entryList.findIndex((item, index) => {
    if (index === id) {
      return true
    }
  })
  formItems.entryList.splice(indexs, 1)
}

function filterOption(input, option) {
  return (
    option.children[0].children.toLowerCase().indexOf(input.toLowerCase()) >= 0
  );
}

async function saveFormula(data: any) {
  formItems.entryList[data.id].entryFormulas = JSON.stringify(data.data)
  formItems.entryList[data.id].amountFormula = '已编辑'
}

const numberReplace = (v) => {
  let a = parseFloat(v.percentage.replace(/[^.\d]+/g, ''));
  if (a <= 0 || a > 100) {
    createWarningModal({title: '温馨提示', content: '修改值为0.01和100之间！'});
    v.percentage = '100'
  }
}
</script>
<style src="../../../../../assets/styles/global-open.less" lang="less"></style>
<style src="../../../../../assets/styles/carry-over-open.less" lang="less" scoped></style>
