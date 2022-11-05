<template>
  <BasicModal
    destroyOnClose
    width="1300px"
    :minHeight="400"
    v-bind="$attrs"
    title="自定义结转"
    @ok="handleOk()"
    okText="生成凭证"
    @cancel="handleClose()"
    @register="register"
  >
    <div
      class="carry-open-content"
    >
      <div class="open-content">
        <div class="open-content-up">
          <!--          <div class="up-div">-->
          <!--            <label>模板编码：</label>-->
          <!--            <a-input v-model:value="formItems.templateNum" placeholder="请输入模板编码" style="width: 130px"/>-->
          <!--          </div>-->
          <!--          <div class="up-div">-->
          <!--            <label>模板名称：</label>-->
          <!--            <a-input v-model:value="formItems.templateName" placeholder="请输入模板名称" style="width: 220px"/>-->
          <!--          </div>-->
<!--          <div class="up-div">-->
<!--            <label>结转类型：</label>-->
<!--            <a-select :disabled="true" v-model:value="formItems.carryOverType"-->
<!--                      @change="formItems.entryList = [];addRow(formItems.carryOverType)"-->
<!--                      placeholder="支票类型" style="width: 120px;">-->
<!--              <a-select-option value="1">公式结转</a-select-option>-->
<!--              <a-select-option value="2">对应结转</a-select-option>-->
<!--            </a-select>-->
<!--          </div>-->
        </div>
        <div class="open-content-down">
          <!--          公式结转-->
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
          <!--          对应结转-->
          <div v-if="formItems.carryOverType==='2'">
            <BasicTable
              :columns="columns"
              :dataSource="tableData2"
              :striped="true"
              :bordered="true"
              :loading="loading"
              :maxHeight="350"
              :pagination="false"
            >
              <template #accountSubjectNum="{ record }">{{record.accountSubjectNum +'-'+record.accountSubjectName }}</template>
            </BasicTable>
          </div>

          <!--          对应结转-->
          <!--          <table id="subjectTableId2" class="subjectTable" v-if="formItems.carryOverType==='2'">-->
          <!--            <thead>-->
          <!--            <tr>-->
          <!--              <th style="width:20%">摘要</th>-->
          <!--              <th style="width:27%">会计科目</th>-->
          <!--&lt;!&ndash;              <th style="width:20%">辅助核算</th>&ndash;&gt;-->
          <!--              <th style="width:10%">转账方式</th>-->
          <!--              <th style="width:14%">取数方向</th>-->
          <!--              <th style="width:10%">借方金额</th>-->
          <!--              <th style="width:10%">贷方金额</th>-->
          <!--              <th style="width:9%">百分比(%)</th>-->
          <!--            </tr>-->
          <!--            </thead>-->
          <!--            <tbody>-->
          <!--            <tr v-for="(entry,index) in formItems.entryList">-->
          <!--              <td>-->
          <!--                <a-input v-model:value="entry.summary" style="width: 88%"/>-->
          <!--              </td>-->
          <!--              <td>-->
          <!--                <a-select-->
          <!--                  show-search-->
          <!--                  style="width: 200px"-->
          <!--                  :filter-option="filterOption"-->
          <!--                  v-model:value="entry.accountSubjectNum" class="down-select">-->
          <!--                  <a-select-option-->
          <!--                    v-for="row in (entry.transferMethod=='2'?pageParameter.allKemuList:pageParameter.lastKemuList)"-->
          <!--                    :value="row.ccode+'&#45;&#45;'+row.ccodeName">{{ row.ccode }} {{ row.ccodeName }}-->
          <!--                  </a-select-option>-->
          <!--                </a-select>-->
          <!--              </td>-->
          <!--&lt;!&ndash;              <td>&ndash;&gt;-->
          <!--&lt;!&ndash;                <a-input v-model:value="entry.auxiliaryAccountingNum" style="width: 92%"/>&ndash;&gt;-->
          <!--&lt;!&ndash;              </td>&ndash;&gt;-->
          <!--              <td>-->
          <!--                {{entry.transferMethod ==='1' ? '转入' : '转出'}}-->
          <!--              </td>-->
          <!--              <td>-->
          <!--                <a-select v-model:value="entry.direction" class="down-select">-->
          <!--                  <a-select-option v-for="row in getList(entry)" :value="row.value">{{ row.label }}</a-select-option>-->
          <!--                </a-select>-->
          <!--              </td>-->
          <!--              <td>-->
          <!--                <a-input v-model:value="entry.md" :disabled="true" style="width: 88%"/>-->
          <!--              </td>-->
          <!--              <td>-->
          <!--                <a-input v-model:value="entry.mc" :disabled="true" style="width: 88%"/>-->
          <!--              </td>-->
          <!--              <td>-->
          <!--                <a-input v-model:value="entry.percentage" :disabled="true" style="width: 76%"/>-->
          <!--              </td>-->
          <!--            </tr>-->
          <!--            </tbody>-->
          <!--          </table>-->
        </div>
      </div>
    </div>
    <formula @save="saveFormula" @register="registerFormulaPage"/>
  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import formula from './formula.vue'
import {reactive, unref, ref} from 'vue'
import {BasicModal, useModal, useModalInner} from '/@/components/Modal'
import {BasicTable} from '/@/components/Table'
import {findAllKeMuCode, carryoverCheckApi} from '/@/api/record/system/definition-correspond'
import {useMessage} from "/@/hooks/web/useMessage";
import {
  Input as AInput,
  Select as ASelect,
} from "ant-design-vue";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getCurrentAccountName, hasBlank} from "/@/api/task-api/tast-bus-api";
import {findByCcode} from "/@/api/record/system/losses-gain";
import router from "/@/router";

const [registerFormulaPage, {openModal: openFormulaPage}] = useModal()
const {
  createConfirm,
  createSuccessModal,
  createInfoModal,
  createErrorModal,
  createWarningModal,
} = useMessage()
const AInputSearch = AInput.Search;
const ASelectOption = ASelect.Option;
const emit = defineEmits(['register', 'save'])
const dynamicTenantId = ref('')
const formItems = reactive({
  id: null,
  templateNum: '',
  templateName: '',
  carryOverType: '1',
  carryOverModule: '2',
  entryList: [],
  carryOverEntries: '',
  orderNum: '',
  kmtype: '',
  dynamicTenantId: '',
  period: '',
  ishaveRjz:'',
  voucherType:'',
  styleList:'',
})

const pageParameter = reactive({
  allKemuList: [],
  lastKemuList: []
})

let changeBeforeModel: any = {}
const loading=ref(true)
const columns=ref([
  {
    title: '结转方式',
    dataIndex: 'transferMethod',
    ellipsis: true,
    align: 'left',
    width: 100,
    format: function (value) {
      return value == '1' ? '转入' : '转出'
    }
  },
  {
    title: '结转类型',
    dataIndex: 'carryOverType',
    ellipsis: true,
    align: 'left',
    width: 100,
    format: function (value) {
      return value == '1' ? '公式结转' : '对应结转'
    }
  },
  {
    title: '结转比例%',
    dataIndex: 'percentage',
    ellipsis: true,
    align: 'left',
    width: 100,
  },
  {
    title: '科目名称',
    dataIndex: 'accountSubjectNum',
    ellipsis: true,
    align: 'left',slots: { customRender: 'accountSubjectNum' },
  },
  {
    title: '方向',
    dataIndex: 'direction',
    ellipsis: true,
    width: 100,
    format: function (value) {
      return value == '0' ? '余额' : value =='1'?'借方余额':value =='2'?'贷方余额':value =='3'?'借方本期发生':value =='4'?'贷方本期发生':value =='5'?'借方累计发生':value =='6'?'贷方累计发生':''
    }
  },
  {
    title: '借方余额',
    dataIndex: 'md',
    ellipsis: true,
    align: 'right',
    format: function (value) {
      return value == '0' ? '' : moneyformat(value)
    }
  },
  {
    title: '贷方余额',
    dataIndex: 'mc',
    ellipsis: true,
    align: 'right',
    format: function (value) {
      return value == '0' ? '' : moneyformat(value)
    }
  },
])
const tableData2=ref([])
const [register, {closeModal}] = useModalInner(async (data) => {
  dynamicTenantId.value = data.dynamicTenantId
  // 修改初始化
  if (pageParameter.lastKemuList.length == 0 || pageParameter.allKemuList.length == 0) {
    let res = await useRouteApi(findAllKeMuCode, {schemaName: getCurrentAccountName(true)})({'year': 2021})
    pageParameter.lastKemuList = res.t1
    pageParameter.allKemuList = res.t2
  }
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
  formItems.dynamicTenantId = data.dynamicTenantId
  formItems.period = data.modelList.variable.period
  formItems.ishaveRjz = data.modelList.ishaveRjz
  formItems.voucherType = data.modelList.variable.voucherType
  formItems.styleList = data.modelList.styleList
  /****************************************************************************/
  // 对应结转
  if(formItems.carryOverType==='2'){
    loading.value=true
    tableData2.value= await useRouteApi(findByCcode,{schemaName: dynamicTenantId})(formItems)
    loading.value=false
  }
  return null;
})
async function handleClose() {
  tableData2.value=[]
  return true;
}
// 金额格式化
function moneyformat(data: any) {
  let str = ""
  if(data){
    // 千分位保留2位小数
    var source = String(data.toFixed(2)).split("."); //按小数点分成2部分
    source[0] = source[0].replace( new RegExp('(\\d)(?=(\\d{3})+$)' , 'ig'), "$1,");//只将整数部分进行都好分割
    str = source.join("." );//再将小数部分合并进来
  }
  return str;
}
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
    return [{value: '0', label: '余额'}, {value: '1', label: '借方发生'}, {value: '2', label: '贷方发生'}];
  }
}

let isChanged: boolean = false

async function handleOk() {
  if (formItems.templateNum === '') {
    createWarningModal({content: '结转模板编码为必填项！'});
  } else if (formItems.templateName === '') {
    createWarningModal({content: '结转模板名称为必填项！'});
  } else {
    let res = await useRouteApi(carryoverCheckApi, {schemaName: getCurrentAccountName(true)})({
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
</script>
<style src="../../../../../assets/styles/global-open.less" lang="less"></style>
