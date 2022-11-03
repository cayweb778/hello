<template>
  <BasicModal
    width="650px"
    v-bind="$attrs"
    title="系统模板初始化"
    @ok="handleOk()"
    @register="register"
    :loading="loadMark"
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
          <span style="line-height:30px;font-size: 24px;color: #0096c7;font-weight: bold;">&ensp;系统模板初始化</span>
        </div>
        <div class="nc-open-content">
          <div class="open-content-up">

            <div class="border-div">
              <span>业务范围</span>
              <AccountPicker theme="three" readonly class="account"/>
            </div>
            <div class="border-div">
              <span>报表模板</span>
              <a-checkbox-group @change="onChange">
                <div v-for="item in reportAccList">
                  <a-checkbox :value="item.reportName" style="width:160px;">
                    <label style="float:left; margin-left: 30px;margin-top: -15px;font-size: 14px;">{{ item.reportChName }}：</label>
                  </a-checkbox>
                  <div style="margin-top: -50px;margin-left: 150px;">
                  <a-select v-model:value="item.selectModel" style="width: 240px;">
                    <a-select-option v-for="template in item.table" :key="template.id" :value="template.id">
                      {{ template.titleName }}
                    </a-select-option>
                  </a-select>
                  </div>
                </div>
              </a-checkbox-group>
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
import {BasicModal, useModalInner} from '/@/components/Modal'
import {
  deleteTemplate,
  findByReportNameAndFlag,
  findByTitleName, saveTemplate
} from "/@/api/record/system/report-template";
import {Select as ASelect, Input as AInput, Checkbox as ACheckbox,Button} from "ant-design-vue"
import {findByUniqueAccStandard} from "/@/api/acctemplate/acctemplate";
import {useMessage} from "/@/hooks/web/useMessage";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getThisAdInfoData, initBasisTabAccoutData} from "/@/api/record/system/financial-settings";
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {findByAccStandardOrderByNum} from "/@/api/record/system/group-report-acc";
import {
  findByAccStandardAndReportName,
  findTemplateById,
  findByColumn,
  findFormulaByTemplate
} from "/@/api/record/system/group-report-template";
import {AppstoreOutlined, SearchOutlined,CaretDownOutlined,LinkOutlined,FormOutlined} from '@ant-design/icons-vue';
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";

const ASelectOption = ASelect.Option
const ACheckboxGroup = ACheckbox.Group

const {
  createErrorModal,
  createConfirm
} = useMessage()

const emit = defineEmits(['register', 'save'])

const formItems:any = ref({})

const selectModel = ref('')
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
            // console.log(res.accStandard)
            formItems.value.kemuTemplateId = res.accStandard
            accStandard.value = item.uniqueAccStandard
            findByAccStandardOrderByNum(accStandard.value).then(report => {
              reportAccList.value = []
              reportAccList.value = report
              if (reportAccList.value.length > 0) {
                formItems.value.reportName = reportAccList.value[0].reportName
                reportAccList.value.forEach(acc => {
                  findByAccStandardAndReportName(accStandard.value, acc.reportName).then(template => {
                    // templateList.value = template
                    acc.table = []
                    acc.table = template
                    if (acc.table.length) {
                      acc.selectModel = acc.table[0].id
                    } else {
                      acc.selectModel = ''
                    }
                  })
                })
                // checkSelectModel()
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
  formItems.value.titleName = '系统模板'
  // formItems.value.templateName = '系统模板'
  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})

const checked: any = ref([])
//获取选中数据
const onChange = (checkedValues: any) => {
  checked.value = checkedValues
  // console.log(checkedValues)
}

//提交方法
const loadMark = ref(false)
async function handleOk() {
  if (checked.value == null || checked.value.length == 0) {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '请选择需要初始化的模板！'
    })
    return false
  }

  let reportList: any = []
  for (const aa of checked.value) {
    const reportList1 = await useRouteApi(findByReportNameAndFlag, {schemaName: dynamicTenantId})(aa)
    reportList.push(...reportList1.items)
  }
  if (reportList.length > 0) {
    createConfirm({
      iconType: 'error',
      title: '提示',
      content: '已存在系统模板，是否需要初始化系统模板?',
      onOk: async () => {
        loadMark.value = true
        for (const aa of reportList) {
          await useRouteApi(deleteTemplate, {schemaName: dynamicTenantId})(aa)
        }
        // emit('save', unref(formItems))
        for (const item of reportAccList.value) {
          for (const aa of checked.value) {
            if (item.reportName == aa) {
              await saveReport(item.selectModel)
            }
          }
        }
        emit('save')
        closeModal()
        loadMark.value = false
        return true
      },
      onCancel: () => {
        return false
      }
    })
  } else {
    loadMark.value = true
    for (const item of reportAccList.value) {
      for (const aa of checked.value) {
        if (item.reportName == aa) {
          await saveReport(item.selectModel)
        }
      }
    }
    // emit('save', unref(formItems))
    emit('save')
    closeModal()
    loadMark.value = false
    return true
  }
}

async function saveReport(id) {
  const res = await findTemplateById(id)
  formItems.value.id = null
  formItems.value.reportName = res.reportName
  // formItems.value.templateName = ''
  formItems.value.accStandard = res.accStandard
  if (formItems.value.kemuTemplateId == null || formItems.value.kemuTemplateId == "") {
    formItems.value.kemuTemplateId = res.kemuTemplateId
  }
  reportAccList.value.forEach(item => {
    if (item.reportName == formItems.value.reportName) {
      formItems.value.templateName = item.reportChName
    }
  })
  formItems.value.flag = '1'
  formItems.value.sysFlag = '1'
  formItems.value.menu1 = res.menu1
  formItems.value.menu2 = res.menu2
  formItems.value.menu3 = res.menu3
  formItems.value.menu4 = res.menu4
  formItems.value.menu5 = res.menu5
  formItems.value.menu6 = res.menu6
  formItems.value.zcTable = []
  formItems.value.fzTable = []
  formItems.value.table = []
  const columnList: any = await findByColumn(id)
  const formulaList: any = ref([])
  if (isCopy.value == '1') {
    formulaList.value = await useRouteApi(findFormulaByTemplate, {schemaName: dynamicTenantId})(id)
  }
  columnList.forEach(function (column) {
    const columns: any = ref([])
    columns.value = column
    columns.value.formulaTable = formulaList.value.filter(item => {
      item.id = null
      return item.columnId == column.id
    }).map(item => {
      item.templateId = null
      item.columnId = null
      return item
    })
    columns.value.id = null
    columns.value.templateId = null
    columns.value.sysFlag = '1'
    formItems.value.table.push(columns.value)
  })
  await useRouteApi(saveTemplate, {schemaName: dynamicTenantId})(formItems.value)
}

</script>
<style lang="less" scoped>
:deep(.ant-checkbox){
  margin-top: -8px;
}
:deep(.ant-checkbox-checked)::after{
  border: none;
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
  width: 150px;background-color: #f1f1f1;padding: 10% 4%;height: 380px;
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

