<template>
  <BasicModal
    width="650px"
    v-bind="$attrs"
    title="选择新建"
    @ok="handleOk()"
    @register="register"
    :canFullscreen="false"
    :footer="null"
    loadingTip="查询参数初始化中"
  >
    <template #title>
      <div style="height: 30px;width: 100%;background-color: #5f375c;color: white;line-height: 30px;">
        <AppstoreOutlined  style="margin: 0 2px;font-size: 14px;"/>
        <span style="font-size: 14px">报表中心</span>
      </div>
    </template>
    <div style="display: inline-flex;justify-content: space-between;width: 100%;">
      <div style="width: calc(100% - 150px);height: 100%;">
        <div style="text-align: center;padding: 10px 0 5px;">
          <FormOutlined style="font-size: 24px;color: #0096c7;margin-top: 2px"/>
          <span style="line-height:30px;font-size: 24px;color: #0096c7;font-weight: bold;">&ensp;选择新建</span>
        </div>
        <div class="nc-open-content">
          <div class="open-content-up">

            <div class="border-div">
              <span>业务范围</span>
              <AccountPicker theme="three" readonly class="account"/>
            </div>
            <div class="border-div">
              <span>报表模板</span>
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
            </div>

          </div>
        </div>
      </div>
      <div class="right-btns">
        <Button style="width: 100px;" shape="round" @click="handleOk"  type="primary">生成</Button>
        <Button style="width: 100px;margin-top: 10px" shape="round" @click="closeModal">取消</Button>
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
import {Select as ASelect, Input as AInput,Button} from "ant-design-vue"
import {findByUniqueAccStandard} from "/@/api/acctemplate/acctemplate";
import {useMessage} from "/@/hooks/web/useMessage";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getThisAdInfoData, initBasisTabAccoutData} from "/@/api/record/system/financial-settings";
import {findByAccStandardOrderByNum} from "/@/api/record/system/group-report-acc";
import {AppstoreOutlined, SearchOutlined,CaretDownOutlined,LinkOutlined,FormOutlined} from '@ant-design/icons-vue';
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";

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
<style lang="less" scoped>
:deep(.ant-checkbox){
  margin-top: -8px;
}
.nc-open-content {
  background-image: url(/@/assets/images/homes/bg-pattern.png);
  background-repeat: no-repeat;
  background-position: 66% 8%;
  background-size: contain;
  position: relative;
  :deep(.ant-select-selector),:deep(.ant-input),:deep(.ant-picker), :deep(.ant-input-affix-wrapper) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    background: none;
  }
  .border-div {
    position: relative;
    border: 1px #a29f9f solid;
    margin: 20px 10px;
    padding: 2.5%;

    > span {
      display: block;
      text-align: center;
      background-color: white;
      position: absolute;
      left: 50px;
      top: -10px;
      color: #888888;
      font-size: 12px;
      font-weight: bold;
      padding-left: 1em;
      padding-right: 1em;
    }
    :deep(.account-picker){
      >div{
        text-align: left;
      }
    }

    label {
      text-align: left;
      width: 100px;
      display: inline-block;
      color: #535353;
      font-size: 13px;
      font-weight: bold;
    }
    :deep(.ant-input){
      border: none !important;
      border-bottom: 1px solid #bdb9b9 !important;
    }

    :deep(.red_span) {
      color: red;
      font-weight: bold;
      display: inline-block;
      width: 20px;
      text-align: left;
    }

    .ant-select{
      color: #000000;
      font-weight: bold;
    }
  }

}

.right-btns{
  width: 150px;background-color: #f1f1f1;padding: 10% 4%;height: 280px;
  :deep(.ant-btn-primary:hover){
    border: 1px solid #5f375c;
  }
}
:global(.ant-modal-header) {
  padding: 10px 0 !important;
}
:global(.ant-modal-close-x){
  height: 30px !important;
  color: white;
}

:deep(.ant-radio-button-wrapper){
  color: #0096c7;
}
</style>
