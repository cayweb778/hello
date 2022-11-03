<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="对应结转模板设置"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 500px"
    >
      <div class="open-content">
        <div class="open-content-up">
          <div class="up-div">
            <label>模板编码：</label>
            <a-input v-model:value="formItems.templateNum" placeholder="请输入模板编码" style="width: 150px"/>
          </div>
          <div class="up-div">
            <label>模板名称：</label>
            <a-input v-model:value="formItems.templateName" placeholder="请输入模板名称" style="width: 250px"/>
          </div>
<!--          <div class="up-div">
            <label>结转类型：</label>
            <a-select v-model:value="formItems.carryOverType" @change="formItems.entryList = [];addRow(formItems.carryOverType)" placeholder="支票类型" style="width: 120px;">
              <a-select-option value="2">对应结转</a-select-option>
              <a-select-option value="1" >公式结转</a-select-option>
            </a-select>
            &lt;!&ndash;     disabled="true"       &ndash;&gt;
          </div>-->
        </div>
        <div class="open-content-down">
          <table id="subjectTableId1" class="subjectTable" v-if="formItems.carryOverType==='1'">
            <thead>
            <tr>
              <th style="width:30%">摘要</th>
              <th style="width:40%">会计科目</th>
              <th style="width:7%">方向</th>
              <th style="width:23%">金额</th>
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
                  <a-select-option v-for="row in (pageParameter.lastKemuList)" :value="row.ccode+'--'+row.ccodeName">
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
                <a-input v-model:value="entry.amountFormula" style="width: 88%;text-align: center;" readonly=""
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
              <th style="width:14%">取数方向</th>
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
                <a-input v-model:value="entry.auxiliaryAccountingNum" style="width: 92%"/>
              </td>
              <td>
                <a-select v-model:value="entry.transferMethod" :default-value="1" class="down-select">
                  <a-select-option value="1">转入</a-select-option>
                  <a-select-option value="2">转出</a-select-option>
                </a-select>
              </td>
              <td>
                <a-select v-model:value="entry.direction" class="down-select">
                  <a-select-option v-for="row in getList(entry)" :value="row.value">{{ row.label }}</a-select-option>
                </a-select>
              </td>
              <td>
                <a-input v-model:value="entry.percentage" style="width: 76%"/>
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

<script setup="props, {content}" lang="ts">
import formula from './formula.vue'
import {reactive, unref,  onMounted, nextTick} from 'vue'
import {BasicModal, useModal, useModalInner} from '/@/components/Modal'
import {carryoverCheckApi, carryoverSaveApi, findAllKeMuCode} from '/@/api/record/system/definition-correspond'
import {useMessage} from "/@/hooks/web/useMessage";
import {is} from "/@/utils/is";

const [registerFormulaPage, {openModal: openFormulaPage}] = useModal()
import {
  Input as AInput,
  Select as ASelect,
} from "ant-design-vue";
const AInputSearch= AInput.Search;
const ASelectOption= ASelect.Option;
const {
  createConfirm,
  createSuccessModal,
  createInfoModal,
  createErrorModal,
  createWarningModal,
} = useMessage()
const emit=defineEmits(['register'])

const formItems = reactive({
  id: '',
  templateNum: '',
  templateName: '',
  carryOverType: '2',
  carryOverModule: '1',
  entryList: [],
  carryOverEntries: ''
})

const pageParameter = reactive({
  allKemuList: [],
  lastKemuList: []
})

let changeBeforeModel: any = {}

const [register, {closeModal}] = useModalInner((data) => {
  // 修改初始化
  findAllKeMuCode({'year': 2021}).then(res => {
    pageParameter.lastKemuList = res.t1
    pageParameter.allKemuList = res.t2
  })
  formItems.entryList = []
  addRow(formItems.carryOverType)
  // changeBeforeModel = JSON.parse(JSON.stringify(formItems))
  formItems.templateNum = data.data.templateNum
  formItems.templateName = data.data.templateName
  formItems.carryOverType = data.data.carryOverType
  formItems.carryOverModule = data.data.carryOverModule
  formItems.id = data.data.id
  formItems.entryList = JSON.parse(data.data.carryOverEntries)
  formItems.carryOverEntries = data.data.carryOverEntries
  return null;


})


const openForePage = (map: any) => {
  let openData = {entryFormulas: [], id: map.id,lastKemuList:[]}
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
    return [{value: '0', label: '余额'}, {value: '1', label: '借方发生'}, {value: '2', label: '借方发生'}];
  }
}

let isChanged: boolean = false

async function handleOk() {
  if (formItems.templateNum === '') {
    createWarningModal({content: '结转模板编码为必填项！'});
  } else if (formItems.templateName === '') {
    createWarningModal({content: '结转模板名称为必填项！'});
  } else {
    carryoverCheckApi({id: formItems.id,carryOverModule: formItems.carryOverModule,templateNum: formItems.templateNum,templateName: formItems.templateNum}).then(res => {
      if (null != res.message && res.message != '') {
        createWarningModal({content: res.message});
      } else {
        let dataList = formItems.entryList
        if (dataList.length > 1) {
          try {
            dataList.forEach((item, index) => {
              if (item.accountSubjectNum == '' || item.summary == '') {
                throw new Error('结转分录必填项：摘要、会计科目不能为空！')
              } else if ((formItems.carryOverType == '1') && (item.entryFormulas == '')) {
                throw new Error('公式结转分录必填项：摘要、会计科目与公式内容不能为空！')
              } else if ((formItems.carryOverType == '2') && item.transferMethod == '2' && item.direction == '') {
                throw new Error('对应结转分录必填项：转出取数方向不能为空！')
              }
              let temps = item.accountSubjectNum.split('--')
              //let auxs = item.auxiliaryAccountingNum.split('--')
              item.accountSubjectName = temps[1]
              item.accountSubjectNum = temps[0]
              //item.auxiliaryAccountingName = auxs[1]
              //item.auxiliaryAccountingNum = auxs[0]
            })
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
    })}
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
      'percentage': '',
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
  let indexs = formItems.entryList.findIndex((item,index) => {
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
</script>
<style lang="less" scoped>

:deep(.ant-calendar-picker-input.ant-input), :deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector) {
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;
}

.vben-basic-title {
  color: rgb(1, 129, 226) !important;
}

.ant-modal-body {
  padding: 0px;
  border: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;

}

:deep(.scroll-container) {
  min-height: 400px !important;
}

.nc-open-content {
  input {
    width: 35%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }


  :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9;
  }
  .ant-input:focus {
    box-shadow: none;
  }
  label {
    text-align: right;
    width: 110px;
    display: inline-block;
    padding: 5px 10px;
  }

  .open-content-down {

    .ant-tabs-tab-active::before {
      position: absolute;
      top: 0px;
      left: 0;
      width: 100%;
      border-top: 2px solid transparent;
      border-radius: 2px 2px 0 0;
      transition: all 0.3s;
      content: '';
      pointer-events: none;
      background-color: rgb(1, 143, 251);
    }
  }
}
</style>
<style scoped>
.up-div {
  display: inline-block;
  margin: 5px 10px;
}

.open-content-down {
  padding: 1%;
}

.subjectTable {
  width: 100%;
}

.subjectTable > thead {
  background-color: #46728e;
  color: white;
}

.subjectTable > thead > tr:hover {
  background-color: #46728e;
}

.subjectTable td {
  text-align: center;
}

.subjectTable td, .subjectTable th {
  height: 40px;
  text-align: center;
}

.subjectTable th:first-child, .subjectTable td:first-child {
  border-left: none;
}

.subjectTable th:last-child, .subjectTable td:last-child {
  border-right: none;
}

.down-select {
  width: 92%;
  background-color: white;
}

.subjectTable tr:hover .symbol-span {
  display: inline-block;
}

.symbol-span {
  font-size: 13px;
  display: none;
  width: 16px;
  height: 15px;
  line-height: 16px;
  text-align: center;
  font-weight: 800;
  color: #000000;
  border-radius: 50%;
  box-shadow: 0 0 1px 3px #121212;
  margin: 6px 0;
  cursor: pointer;
}

</style>
