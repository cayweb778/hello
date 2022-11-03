<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="选择新建"
    @ok="handleOk()"
    @register="register"
  >
    <div class="nc-open-content">
      <div class="open-content-up">

        <label>选择模板</label>
        <a-select v-model:value="selectModel" @change="checkModel()" style="width: 50%">
          <a-select-option v-for="template in templateList" :key="template.id" :value="template.id">
            {{ template.templateName }}
          </a-select-option>
        </a-select>

      </div>

    </div>

  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import {ref, unref} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {
  findByColumn,
  findByFormula,
  findByReportNameAndFlag,
  findTemplateById
} from "/@/api/record/system/report-template";
import {Select as ASelect, Input as AInput} from "ant-design-vue"

const ASelectOption = ASelect.Option

const emit = defineEmits(['register', 'save'])

const formItems: any = ref({})

const selectModel = ref('0')

const templateList = ref({})

const [register, {closeModal}] = useModalInner((data) => {
  // 方式2
  findByReportNameAndFlag("lrb").then(res => {
    templateList.value = res.items
    selectModel.value = templateList.value[0].id
    checkModel()
  })

})

function checkModel() {
  if (selectModel.value == '0') {
    formItems.value.id = ''
    formItems.value.reportName = ''
    formItems.value.templateName = ''
    formItems.value.accStandard = ''
    formItems.value.kemuTemplateId = ''
    formItems.value.titleName = ''
    formItems.value.flag = ''
    formItems.value.menu1 = ''
    formItems.value.menu2 = ''
    formItems.value.menu3 = ''
    formItems.value.menu4 = ''
    formItems.value.menu5 = ''
  } else {
    findTemplateById(selectModel.value).then(res => {
      formItems.value.id = ''
      formItems.value.reportName = res.reportName
      formItems.value.templateName = ''
      formItems.value.accStandard = res.accStandard
      formItems.value.kemuTemplateId = res.kemuTemplateId
      formItems.value.titleName = ''
      formItems.value.flag = '1'
      formItems.value.menu1 = res.menu1
      formItems.value.menu2 = res.menu2
      formItems.value.menu3 = res.menu3
      formItems.value.menu4 = res.menu4
      formItems.value.menu5 = res.menu5

      formItems.value.table = []
      findByColumn(selectModel.value).then(res => {
        res.forEach(function (column) {
          const columns: any = ref({})
          columns.value = column
          findByFormula(column.id).then(res1 => {
            columns.value.formulaTable = res1
          })
          columns.value.id = ''
          columns.value.sysFlag = null
          formItems.value.table.push(columns.value)
        })
      })
    })
  }
}

//提交方法
async function handleOk() {
  emit('save', unref(formItems))
  closeModal()
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
    text-align: right;
    width: 110px;
    display: inline-block;
    padding: 5px 10px;
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

table {

  table-layout: fixed; /* 只有定义了表格的布局算法为fixed，下面td的定义才能起作用。 */
}

th, td {
  width: 100%;
  word-break: keep-all; /* 不换行 */
  white-space: nowrap; /* 不换行 */
  overflow: hidden; /* 内容超出宽度时隐藏超出部分的内容 */
  text-overflow: ellipsis; /* 当对象内文本溢出时显示省略标记(...) ；需与overflow:hidden;一起使用。*/
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

#tableId th, #tableId1 th {
  border: 1px solid #6f696f;
}

#tableId tr td, #tableId1 tr td {
  border: 1px solid #6f696f;
  border-top: hidden;
}

a {
  cursor: pointer;
}

/*td缩进*/
.indent {
  text-indent: 30px;
}
</style>
