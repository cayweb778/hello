<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="选择新建"
    @ok="handleOk()"
    @register="register"
  >
    <div class="nc-open-content">
      <div class="open-content-up" style="margin-top: 30px;text-align: center;">

        <label style="width: 150px;">模板标识：</label>
        <a-input v-model:value="formItems.titleName" @blur="changTitleName()" placeholder="模板标识"
                 style="width: 50%"/>
        <br/>
        <label style="width: 150px;">报表类型：</label>
        <a-select v-model:value="formItems.reportName" @change="checkSelectModel()"
                  style="width: 50%">
          <a-select-option v-for="item in reportAccList" :key="item.reportName"
                           :value="item.reportName">
            {{ item.reportChName }}
          </a-select-option>
        </a-select>
        <br/>
        <label style="width: 150px;">选择模板：</label>
        <a-select v-model:value="selectModel" @change="checkModel()" style="width: 50%">
          <!--          <a-select-option value="0">自定义模板</a-select-option>-->
          <a-select-option v-for="template in templateList" :key="template.id" :value="template.id">
            {{ template.titleName }}
          </a-select-option>
        </a-select>
        <br/>
        <!--        <label style="width: 150px;">会计准则：</label>
                <a-select v-model:value="formItems.accStandard" @change="standardCheck()" placeholder="会计准则" style="width: 50%">
                  <a-select-option v-for="accstandard in accstandardList" :key="accstandard.uniqueAccStandard" :value="accstandard.uniqueAccStandard">
                    {{ accstandard.accStandardName }}
                  </a-select-option>
                </a-select>
                <br/>
                <label style="width: 150px;">科目模板：</label>
                <a-select v-model:value="formItems.kemuTemplateId" placeholder="科目模板" style="width: 50%">
                  <a-select-option v-for="item in templateKemuList" :key="item.id" :value="item.id">
                    {{ item.tname }}
                  </a-select-option>
                </a-select>
                <br/>
                <label style="width: 150px;">是否复制公式：</label>
                <a-select v-model:value="isCopy" placeholder="是否复制公式" style="width: 50%">
                  <a-select-option value="0">否</a-select-option>
                  <a-select-option value="1">是</a-select-option>
                </a-select>
                <br/>-->

      </div>

    </div>

  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import {onMounted, ref, unref} from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {
  findByColumn,
  findByFormula, findByReportName,
  findByReportNameAndFlag, findByTitleName, findFormulaByTemplate,
  findTemplateById, findTemplateBySysFlag
} from "/@/api/record/system/report-template";
import {Select as ASelect, Input as AInput} from "ant-design-vue"
import {findByUniqueAccStandard} from "/@/api/acctemplate/acctemplate";
import {useMessage} from "/@/hooks/web/useMessage";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getThisAdInfoData, initBasisTabAccoutData} from "/@/api/record/system/financial-settings";
import {findByAccStandardOrderByNum} from "/@/api/record/system/group-report-acc";

const ASelectOption = ASelect.Option

const {
  createErrorModal
} = useMessage()

const emit = defineEmits(['register', 'save'])

const formItems: any = ref({})

const selectModel = ref('0')
const isCopy = ref('0')

const templateList: any = ref([])

const accstandardList: any = ref([])

let changeBeforeModel: any = {}
const dynamicTenantId = ref()
const defaultAdName = ref()
const accStandard: any = ref('')
const reportAccList: any = ref([])
const [register, {closeModal}] = useModalInner((data) => {
  dynamicTenantId.value = data.dynamicTenantId
  defaultAdName.value = data.defaultAdName
  accstandardList.value = data.data
  getThisAdInfoData({'accId': defaultAdName.value,}).then(res => {
    if (null != res) {
      initBasisTabAccoutData().then(list => {
        // const acountStandardList = list.acountStandardList
        const levelList = list.levelList
        levelList.forEach(item => {
          if (res.accStandard == item.id) {
            formItems.value.kemuTemplateId = res.accStandard
            accStandard.value = item.uniqueAccStandard
            findByAccStandardOrderByNum(accStandard.value).then(report => {
              reportAccList.value = []
              reportAccList.value = report
              if (reportAccList.value.length > 0) {
                formItems.value.reportName = reportAccList.value[0].reportName
                checkSelectModel()
              } else {
                formItems.value.reportName = ''
              }
              // console.log(reportAccList.value)
            })
          }
        })
      })
    }
  })

  // 方式2
  useRouteApi(findByReportName, {schemaName: dynamicTenantId})({reportName: formItems.value.reportName}).then(res => {
    templateList.value = res.items
    if (templateList.value.length > 0) {
      selectModel.value = templateList.value[0].id
    }
    checkModel()
  })

  formItems.value.titleName = ''
  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})

function checkSelectModel() {
  useRouteApi(findByReportName, {schemaName: dynamicTenantId})({reportName: formItems.value.reportName}).then(res => {
    templateList.value = res.items
    if (templateList.value.length > 0) {
      selectModel.value = templateList.value[0].id
    }
    checkModel()
  })
}

// 会计准则下拉框回调
/*const templateKemuList:any = ref([])
const standardCheck = async () => {
  const template = await findByUniqueAccStandard(formItems.value.accStandard)
  templateKemuList.value = template.items
  let num = 0
  templateKemuList.value.forEach(
    function (item:any,index){
      if (formItems.value.kemuTemplateId==item.id) {
        formItems.value.kemuTemplateId = item.id
        num++
      } else if (formItems.value.kemuTemplateId==''&& index==0) {
        formItems.value.kemuTemplateId = item.id
      }
    }
  )
  if (num==0) {
    formItems.value.kemuTemplateId = templateKemuList.value[0].id
  }
}*/

function checkModel(){
  reportAccList.value.forEach(item => {
    if (item.reportName == formItems.value.reportName) {
      formItems.value.templateName = item.reportChName
    }
  })
  if (selectModel.value=='0'){
    formItems.value.id = null
    formItems.value.reportName = ''
    // formItems.value.templateName = '自定义档案'
    // formItems.value.accStandard = ''
    // formItems.value.kemuTemplateId = ''
    // formItems.value.titleName = ''
    formItems.value.flag = ''
    formItems.value.menu1 = '核算单位:&核算单位'
    formItems.value.menu2 = '日期:&date'
    formItems.value.menu3 = '币种:&币种'
    formItems.value.menu4 = '主管:'
    formItems.value.menu5 = '制单人:&操作员姓名'
    formItems.value.menu6 = ''
  } else {
    useRouteApi(findTemplateById,{schemaName: dynamicTenantId})(selectModel.value).then(res=>{
      formItems.value.id = null
      formItems.value.reportName = res.reportName
      // formItems.value.templateName = ''
      formItems.value.accStandard = res.accStandard
      // formItems.value.kemuTemplateId = res.kemuTemplateId
      /*templateList.value.forEach( item => {
        if (selectModel.value== item.id){
          formItems.value.templateName = item.templateName
        }
      })*/
      /*accstandardList.value.forEach(
        function (item){
          if (formItems.value.accStandard==item.uniqueAccStandard) {
            formItems.value.accStandard = item.uniqueAccStandard
            standardCheck()
          }
        }
      )*/
      // formItems.value.titleName = ''
      formItems.value.flag = '1'
      formItems.value.menu1 = res.menu1
      formItems.value.menu2 = res.menu2
      formItems.value.menu3 = res.menu3
      formItems.value.menu4 = res.menu4
      formItems.value.menu5 = res.menu5
      formItems.value.menu6 = res.menu6

      /*formItems.value.zcTable = []
      formItems.value.fzTable = []
      findByColumn(selectModel.value).then(res => {
        res.forEach(function (column){
          const columns: any = ref({})
          columns.value = column
          findByFormula(column.id).then(res1 => {
            columns.value.formulaTable = res1
          })
          columns.value.id = null
          columns.value.sysFlag = null
          if (column.columnType=='zc') {
            formItems.value.zcTable.push(columns.value)
          } else {
            formItems.value.fzTable.push(columns.value)
          }
        })
      })*/
    })
  }
}

//提交方法
async function handleOk() {

  formItems.value.zcTable = []
  formItems.value.fzTable = []
  formItems.value.table = []
  const columnList: any = await useRouteApi(findByColumn,{schemaName: dynamicTenantId})(selectModel.value)
  const formulaList: any = ref([])
  if (isCopy.value=='1') {
    formulaList.value = await useRouteApi(findFormulaByTemplate,{schemaName: dynamicTenantId})(selectModel.value)
  }
  columnList.forEach(function (column){
    const columns: any = ref([])
    columns.value = column
    columns.value.formulaTable = formulaList.value.filter(item => {
      item.id = null
      return item.columnId == column.id
    }).map(item=>{
      item.templateId=null
      item.columnId=null
      return item
    })
    columns.value.id = null
    columns.value.templateId = null
    columns.value.sysFlag = null
    if (formItems.value.reportName=='zcfzb') {
      if (column.columnType == 'zc') {
        formItems.value.zcTable.push(columns.value)
      } else {
        formItems.value.fzTable.push(columns.value)
      }
    } else {
      formItems.value.table.push(columns.value)
    }
  })
  if (formItems.value.titleName=='' || formItems.value.titleName==null){
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '模板标识不能为空！'
    })
    return false
  }
  emit('save', unref(formItems))
  closeModal()
  return true
}

async function changTitleName() {
  if((changeBeforeModel.titleName!=undefined && changeBeforeModel.titleName!='') || changeBeforeModel.titleName==formItems.value.titleName){
    return true
  }
  const res = await useRouteApi(findByTitleName,{schemaName: dynamicTenantId})({reportName: formItems.value.reportName,titleName:formItems.value.titleName})
  if(res.length!=0){
    // alert('模板标识已存在，请重新输入！')
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '模板标识已存在，请重新输入！'
    })
    formItems.value.titleName = ''
    console.log(false)
    return false
  }
  return true
}

</script>
<style lang="less">
.vben-basic-title {
  color: rgb(1, 129, 226) !important;
}

.ant-modal-body {
  padding: 0px;
  border: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
}

.nc-open-content {
  input {
    width: 35%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  .ant-select-selector {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  label {
    text-align: left;
    width: 150px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    padding-left: 2em;
    color: #535353;
  }

  .open-content-down {
    margin-top: 5%;

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

<style lang="less" scope>
@import "../../../../../assets/styles/layui.less";
@import "../../../../../assets/styles/theme.less";
</style>
<style type="text/css">

.even {
  background: #FCFCFC;
}

.odd {
  background: #FFFFFF;
}

td:hover {
  background: none;
}

td .bjgs {
  display: none;
}

td:hover .bjgs {
  display: block;
  position: absolute;
  top: 8px;
  left: 130px;
  /*background-color: whitesmoke;*/
  color: royalblue;
}
table{

  table-layout:fixed;/* 只有定义了表格的布局算法为fixed，下面td的定义才能起作用。 */
}
th,td{
  width:100%;
  word-break:keep-all;/* 不换行 */
  white-space:nowrap;/* 不换行 */
  overflow:hidden;/* 内容超出宽度时隐藏超出部分的内容 */
  text-overflow:ellipsis;/* 当对象内文本溢出时显示省略标记(...) ；需与overflow:hidden;一起使用。*/
}
#tableId > thead,
#tableId > tbody > tr,
#tableId1 > thead,
#tableId1 > tbody > tr {
  display: table;
  width: 100%;
  height: 30px;
  table-layout: fixed; /**表格列的宽度由表格宽度决定，不由内容决定*/
  text-align: center;
}

#tableId th ,#tableId1 th  {
  border: 1px solid #6f696f;
}
#tableId tr td ,#tableId1 tr td  {
  border: 1px solid #6f696f;
  border-top: hidden;
}

a{
  cursor: pointer;
}
/*td缩进*/
.indent{
  text-indent:30px;
}
</style>
