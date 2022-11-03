<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="系统模板初始化"
    @ok="handleOk()"
    @register="register"
    :loading="loadMark"
  >
    <div class="nc-open-content">
      <div class="open-content-up" style="margin-top: 30px;">

        <!--        <label>报表类型：</label>
                <a-select v-model:value="formItems.reportName" @change="checkSelectModel()" style="width: 50%">
                  <a-select-option v-for="item in reportAccList" :key="item.reportName" :value="item.reportName">
                    {{ item.reportChName }}
                  </a-select-option>
                </a-select>
                <br/>
                <label>选择模板：</label>
                <a-select v-model:value="selectModel" @change="checkModel()" style="width: 50%">
                  <a-select-option v-for="template in templateList" :key="template.id" :value="template.id">
                    {{ template.templateName }}
                  </a-select-option>
                </a-select>
                <br/>-->
        <a-checkbox-group @change="onChange">
          <div v-for="item in reportAccList">
            <a-checkbox :value="item.reportName" style="width:160px;">
              {{ item.reportChName }}：
            </a-checkbox>
            <a-select v-model:value="item.selectModel" @change="checkModel()" style="width: 240px">
              <a-select-option v-for="template in item.table" :key="template.id"
                               :value="template.id">
                {{ template.titleName }}
              </a-select-option>
            </a-select>
          </div>
        </a-checkbox-group>

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
import {Select as ASelect, Input as AInput, Checkbox as ACheckbox} from "ant-design-vue"
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
