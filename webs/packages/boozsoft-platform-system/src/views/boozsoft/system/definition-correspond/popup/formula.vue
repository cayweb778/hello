<template>
  <BasicModal
    width="800px"
    v-bind="$attrs"
    title="公式设置"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 360px"
    >
      <div id="editFormulaDiv">
        <div class="open-content">
          <form id="closeFromTwo" action="" method="post">
            <table class="subjectTable" id="formulaTable">
              <thead>
              <tr>
                <th style="width:10%">运算符号</th>
                <th style="width:26%">会计科目</th>
                <th style="width:26%">辅助核算</th>
                <th style="width:15%">取数规则</th>
                <th style="width:7%">运算</th>
                <th style="width:17%">数值</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="(entry,index) in pageParameter.entryFormulas">
                <td><span class="symbol-span" style="float: left;" @click="addRowForula(this)">✚</span>
                  <a-select v-model:value="entry.calculationSign" style="width: 70%;text-align: left"
                            class="down-select">
                    <a-select-option value="1">+</a-select-option>
                    <a-select-option value="2">-</a-select-option>
                  </a-select>
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
                  <a-input v-model:value="entry.auxiliaryAccountingNum" style="width: 88%"/>
                </td>
                <td>
                  <a-select v-model:value="entry.direction" class="down-select">
                    <a-select-option value="y">余额</a-select-option>
                    <a-select-option value="jy">借方余额</a-select-option>
                    <a-select-option value="dy">贷方余额</a-select-option>
                    <a-select-option value="jf">借方发生</a-select-option>
                    <a-select-option value="df">贷方发生</a-select-option>
                  </a-select>
                </td>
                <td>
                  <a-select v-model:value="entry.calculation" class="down-select">
                    <a-select-option value="1">*</a-select-option>
                    <a-select-option value="2">/</a-select-option>
                  </a-select>
                </td>
                <td>
                  <a-input v-model:value="entry.calculationValue" style="width: 76%;text-align: right"/>
                  <span class="symbol-span" style="float: right;" @click="delRow(index)">✖</span></td>
              </tr>
              </tbody>
            </table>
          </form>
        </div>
      </div>
    </div>
  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {reactive, ref, unref} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {useMessage} from "/@/hooks/web/useMessage";
import {
  Input as AInput,
  Select as ASelect,
} from "ant-design-vue";
const AInputSearch= AInput.Search;
const ASelectOption= ASelect.Option;
const {
  createWarningModal
} = useMessage()
const emit=defineEmits(['register'])

const formItems: any = ref({})
const pageParameter = reactive({
  lastKemuList: [],
  entryFormulas: [],
  changeRowId: ''
})
let changeBeforeModel: any = {}

const [register, {closeModal}] = useModalInner((data) => {
  pageParameter.entryFormulas = []
  pageParameter.lastKemuList = data.data.lastKemuList
  pageParameter.changeRowId = data.data.id
  if (data.data.entryFormulas.length === 0) {
    addRowForula()
  } else {
    pageParameter.entryFormulas = data.data.entryFormulas
  }
  return false
  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
let isChanged: boolean = false

async function handleOk() {
  // 数据过滤
  let dataList = pageParameter.entryFormulas
  try {
    dataList.forEach((item, index) => {
      if (item.accountSubjectNum == '' || item.calculationValue == '') {
        throw new Error('结转分录公式必填项：会计科目与数值内容！')
      }
      let karr = item.accountSubjectNum.split('--')
      // let farr =  item.auxiliaryAccountingNum.split('--')
      // item.auxiliaryAccountingName = farr[1]
      // item.auxiliaryAccountingNum = farr[0]
      item.accountSubjectName = karr[1]
      item.accountSubjectNum = karr[0]
    })
      emit('save', unref({id: pageParameter.changeRowId, data: pageParameter.entryFormulas}))
      closeModal()
    return true
  } catch (e) {
    createWarningModal({content: e.message});
  }
  return false
}

function filterOption(input, option) {
  return (
    option.children[0].children.toLowerCase().indexOf(input.toLowerCase()) >= 0
  );
}

function addRowForula() {
  pageParameter.entryFormulas.push({
    calculationSign: '1',
    accountSubjectNum: '',
    accountSubjectName: '',
    auxiliaryAccountingNum: '',
    auxiliaryAccountingName: '',
    direction: 'y',
    calculation: '1',
    calculationValue: ''
  })
}

async function delRow(id) {
  if (pageParameter.entryFormulas.length < 2) {
    // 最后一条
    return false
  }
  let indexs = pageParameter.entryFormulas.findIndex((item, index) => {
    if (index === id) {
      return true
    }
  })
  pageParameter.entryFormulas.splice(indexs, 1)
}
</script>
<style scoped>
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

input {
  width: 35%;
  border: none !important;
  border-bottom: 1px solid #bdb9b9 !important;
}

:deep(.ant-select-selector) {
  border: none !important;
  border-bottom: 1px solid #bdb9b9 !important;
}
</style>
