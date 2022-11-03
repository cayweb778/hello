<template>
  <BasicModal
    width="800px"
    v-bind="$attrs"
    title="银行对账单模板"
    @ok="handleOk()"
    :showOkBtn="isEdit"
    :showCancelBtn="true"
    @register="register"
  >
<!--    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <img src="/@/assets/images/create.svg" style="width:25px;margin-right: 10px;"/>
        <span style="line-height: 25px;font-size: 16px;">银行对账单模板</span>
      </div>
    </template>-->
    <template #title>
      <div style="display: flex;margin-left: 10px;margin-top: 10px;" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='0'">
          <PlusCircleOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;银行对账单模板
        </span>
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='1'">
          <FormOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;银行对账单模板
        </span>
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='2'">
          <FileSearchOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;银行对账单模板
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 60px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="/@/assets/images/bank.png" style="height:80px;margin-right: 10px;"/>
      </div>
    </template>
    <div :class="isEdit?'nc-open-content':'nc-open-show-content'" >
      <div class="open-content-up" style="min-height: 460px">

        <div style="text-align: center;margin-top: 60px;">
          <label style="width: 150px;font-size: 18px;">模板名称：</label>
          <a-input v-model:value="itemConfig.base.templateName" @blur="checkName()" class="abc" placeholder="请输入模板名称" style="width:50%"/>
<!--          <br/>
          <label style="width: 150px">对应账户：</label>
          <a-select v-model:value="itemConfig.base.templateTitleStart" placeholder="对应账户"
                    style="width: 35%;">
            <a-select-option v-for="item in codeKemu" :key="item.ccode" :value="item.ccode">
              {{ item.ccode }}-{{ item.ccodeName }}
            </a-select-option>
          </a-select>-->
        </div>
        <br/>

<!--        <div style="border-bottom: 1px solid #999999;font-weight: bold;">
          栏目设置
        </div>-->
        <div style="padding-left: 10%;">
        <div style="margin-top: 5px;">
          <label style="width: 30%;font-size: 16px;color: #000000;">系统字段名称</label>&nbsp;
          <label style="width: 15%;text-align: center;font-size: 16px;color: #000000;">类型</label>&nbsp;
          <label style="width: 30%;text-align: center;font-size: 16px;color: #000000;">银行对账单名称</label>&nbsp;
          <!--          <label style="width: 25%;text-align: center;font-size: 18px;">样式</label>-->
        </div>
        <div v-for="(row) in itemConfig.base.table" style="margin-top: 5px;">
          <!--          <a-input v-model:value="row.systemField" type="text" placeholder="系统字段名称" style="width: 25%;"/>-->
          <label style="width: 30%;margin-left: 2%;">{{ row.systemField }}：</label>&nbsp;
          <a-select v-model:value="row.billType" placeholder="类型" style="width: 15%;" :disabled="true">
            <a-select-option value="文本">文本</a-select-option>
            <a-select-option value="日期">日期</a-select-option>
            <a-select-option value="金额">金额</a-select-option>
          </a-select>&emsp;
          <a-input v-model:value="row.billField" type="text" placeholder="" style="width: 30%"/>
          <EditOutlined v-if="isEdit"/>&nbsp;
        </div>
        </div>

      </div>
    </div>

    <template #footer>
      <div v-if="!isEdit">
        <a-button @click="closeModal()" type="primary">关闭</a-button>
      </div>
      <div v-if="isEdit">
        <a-button @click="closeModal()">取消</a-button>
        <a-button @click="handleOk()" type="primary">确认</a-button>
      </div>
    </template>

  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import {ref, unref} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
/*import {
  PlusOutlined,
  DeleteOutlined,
  EditOutlined
} from '@ant-design/icons-vue'*/
import {
  PlusCircleOutlined,
  FormOutlined,
  FileSearchOutlined,
  CaretDownOutlined
} from '@ant-design/icons-vue'
import bankTemplateItemConfigModelHelper from "./helper/bankTemplateItemConfigModelHelper"
import {findByColumn, findByName} from '/@/api/record/system/bank-template'
import {Select as ASelect, Input as AInput} from 'ant-design-vue'
import {findCodeKemuByBr} from "/@/api/record/system/bank-statement";
import {useMessage} from "/@/hooks/web/useMessage";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";

const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option

const {
  createWarningModal
} = useMessage()

const emit = defineEmits(['register', 'save'])
let changeBeforeModel: any = {}
let isChanged: boolean = false

async function handleOk() {
  if (itemConfig.value.base.templateName == null || itemConfig.value.base.templateName == '') {
    createWarningModal({
      iconType: 'warning',
      title: '提示',
      content: '模板名称不能为空！'
    })
    return false
  }
  isChanged = !(JSON.stringify(itemConfig.value.base) == JSON.stringify(changeBeforeModel))
  if (isChanged) {
    emit('save', unref(itemConfig.value.base))
    closeModal()
    return true
  }
  closeModal()
  // alert("没有改变过")
  return false
}

const {
  createBaseRow,
  createBaseRow1,
  createBankTemplateItemConfig
} = bankTemplateItemConfigModelHelper()
const itemConfig: any = ref(createBankTemplateItemConfig())
const codeKemu: any = ref([])
const dynamicTenantId = ref('')
const defaultAdName = ref('')
const isEdit = ref()
const isState = ref('0')
const saveClick:any = ref(false)
const [register, {closeModal}] = useModalInner((data) => {
  saveClick.value=false
  isState.value = data.isState
  isEdit.value = data.isEdit
  dynamicTenantId.value = data.dynamicTenantId
  defaultAdName.value = data.defaultAdName
  //给头部赋值
  itemConfig.value.base.id = data.data.id
  itemConfig.value.base.templateName = data.data.templateName
  itemConfig.value.base.templateEnName = data.data.templateEnName
  itemConfig.value.base.templateTitleStart = data.data.templateTitleStart
  itemConfig.value.base.flag = '1'


  useRouteApi(findCodeKemuByBr, {schemaName: dynamicTenantId})({}).then(res => {
    codeKemu.value = []
    res.forEach(
      function (item) {
        if (item.bend == '1') {
          codeKemu.value.push(item)
        }
      }
    )
  })
  //给栏目赋值
  itemConfig.value.base.table = []
  if (data.data.table==null){
    if (data.data.id!=null&&data.data.id!='') {
      useRouteApi(findByColumn, {schemaName: dynamicTenantId})(data.data.id)
        .then(list => {
          itemConfig.value.base.table = list
          //如果没有值默认给一行
          if (list.length==0){
            itemConfig.value.base.table = createBaseRow1()
          }
          changeBeforeModel = JSON.parse(JSON.stringify(itemConfig.value.base))
        })
    } else {
      itemConfig.value.base.table = createBaseRow1()

      changeBeforeModel = JSON.parse(JSON.stringify(itemConfig.value.base))
    }
  } else {
    itemConfig.value.base.table = data.data.table
  }
})

async function checkName(){
  if ((changeBeforeModel.templateName != undefined && changeBeforeModel.templateName != '') || changeBeforeModel.templateName == itemConfig.value.base.templateName) {
    return true
  }
  if (itemConfig.value.base.templateName == null || itemConfig.value.base.templateName == '') {
    return false
  }
  const res = await useRouteApi(findByName, {schemaName: dynamicTenantId})(itemConfig.value.base.templateName)
  if(res.length!=0) {
    // alert('银行编码已存在，请重新输入！')
    createWarningModal({
      iconType: 'warning',
      title: '提示',
      content: '模板名称已存在，请重新输入！'
    })
    itemConfig.value.base.templateName = ''
    console.log(false)
    return false
  }
  return true
}

</script>
<style>
.vben-basic-title {
  color: #0096c7 !important;
  border:none !important;
}

.ant-modal-body {
  padding: 0px;
  border-bottom: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
  border-top: none !important;
}
.ant-modal-header{
  border: none !important;
}
</style>
<style lang="less" scoped>
:deep(.ant-calendar-picker-input.ant-input), :deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector .ant-select-selection-search-input) {
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;
  font-weight: bold;
}

:deep(.ant-select-single:not(.ant-select-customize-input)
.ant-select-selector
.ant-select-selection-search-input) {
  border: none !important;
}

.vben-basic-title {
  color: #0096c7 !important;
  border: none !important;
}

.ant-modal-body {
  padding: 0px;
  border-bottom: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
}

.red_span {
  color: red;
  font-weight: bold;
  display: inline-block;
  width: 20px;
  text-align: right;
}

.nc-open-content {

  input {
    width: 30%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
  }

  .abc{
    border-bottom: 2px solid #666666 !important;
    font-size: 18px;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
  }

  label {
    text-align: left;
    width: 120px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    padding-left: 2em;
    color: #535353;
    font-size: 13px;
    font-weight: bold;
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

.nc-open-show-content {

  input {
    width: 30%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
    color: #000000;
  }

  .abc{
    border-bottom: 2px solid #666666 !important;
    font-size: 18px;
  }

  pointer-events: none;
  cursor: default;

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
    color: #000000;
  }

  label {
    text-align: left;
    width: 150px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    padding-left: 2em;
    color: #535353;
    font-size: 13px;
    font-weight: bold;
  }

  .open-content-down {
    margin-top: 5%;

    :deep(.ant-tabs-tab) {
      pointer-events: auto;
    }

    .ant-tabs-tab-active::before {
      position: absolute;
      top: 0px;
      left: 0;
      width: 100%;
      border-top: 2px solid transparent;
      border-radius: 2px 2px 0 0;
      transition: all 0.3s;
      content: '';
      background-color: rgb(1, 143, 251);
    }
  }
}
</style>
