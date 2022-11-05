<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="银行档案"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up">

        <label>银行名称</label>
        <a-input v-model:value="formItems.bankName" placeholder="请输入银行名称" style="font-size: 18px" />
        <label>银行账号</label>
        <a-input v-model:value="formItems.bankAccount" placeholder="请输入银行账号" style="font-size: 18px" />
        <br>

        <label>银行编码</label>
        <a-input v-model:value="formItems.bankCode" @blur="checkCode()" placeholder="银行编码（不允许重复）" />
        <label>开户行</label>
        <a-input v-model:value="formItems.bankAddr" placeholder="开户行" />
        <br>

        <label>开户地</label>
        <a-input v-model:value="formItems.bankArea" placeholder="开户地" />
        <label>行号</label>
        <a-input v-model:value="formItems.bankNum" placeholder="行号" />
        <br>

        <label>联系人</label>
        <a-input v-model:value="formItems.lianxiPerson" placeholder="联系人" />
        <label>联系电话</label>
        <a-input v-model:value="formItems.lianxiPhone" placeholder="联系电话" />
        <br>

        <label>联系地址</label>
        <a-select
          v-model:value="formItems.countryId"
          show-search
          placeholder="国家/地区"
          option-filter-prop="children"
          style="width: 35%"
          allow-clear
        >
          <a-select-option v-for="countrys in countryList" :key="countrys.namech" :value="countrys.namech">
            {{ countrys.namech }}
          </a-select-option>
        </a-select>
        <label>联系人QQ</label>
        <a-input v-model:value="formItems.lianxiQq" placeholder="联系人QQ" />
        <br>

        <label></label>
        <a-cascader v-model:value="cityValue" :options="options" placeholder="省/市/区" style="width: 35%;"/>
        <label>联系人微信</label>
        <a-input v-model:value="formItems.lianxiPhone" placeholder="联系人微信" />
        <br>

        <label></label>
        <a-input v-model:value="formItems.address" placeholder="详细地址" />

      </div>
    </div>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {onMounted, ref, unref} from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {findByBankCode, findByDatabaseUniqueCode } from "/@/api/record/system/bank";
import {Select as ASelect,Input as AInput, Cascader as ACascader} from 'ant-design-vue'
import {findAllCountry} from "/@/api/record/costomer_data/customer";
import {findAllProvince} from "/@/api/record/system/zone";
import {useMessage} from "/@/hooks/web/useMessage";
const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option

const {
  createErrorModal
} = useMessage()

const emit=defineEmits([])

const formItems:any = ref({})

let changeBeforeModel:any = {}

//查询集团管控状态
const dist:any = ref([])
async function findDatabase() {
  const res = await findByDatabaseUniqueCode()
  dist.value = res
}

onMounted(async () => {
  await findDatabase()
})

//国家
const countryList:any = ref([])
const options = ref([])
const cityValue:any = ref([])

const [register, { closeModal }] = useModalInner((data) => {
  // 国家信息
  findAllCountry().then(res => {
    countryList.value = res.items
  })
  // 全部省信息
  findAllProvince().then(res => {
    options.value = res
  })

  // 方式2
  formItems.value.id = data.data.id
  formItems.value.bankCode = data.data.bankCode
  formItems.value.bankName = data.data.bankName
  formItems.value.bankAccount = data.data.bankAccount
  formItems.value.bankAddr = data.data.bankAddr
  formItems.value.bankArea = data.data.bankArea
  formItems.value.bankNum = data.data.bankNum
  formItems.value.lianxiPerson = data.data.lianxiPerson
  formItems.value.lianxiPhone = data.data.lianxiPhone
  formItems.value.lianxiQq = data.data.lianxiQq
  formItems.value.lianxiWechat = data.data.lianxiWechat
  formItems.value.countryId = data.data.countryId
  formItems.value.zoneId = data.data.zoneId
  formItems.value.address = data.data.address
  formItems.value.successState = data.data.successState
  formItems.value.applyDatabaseUniqueCode = data.data.applyDatabaseUniqueCode
  formItems.value.applyUser = data.data.applyUser
  formItems.value.applyDate = data.data.applyDate
  formItems.value.approveUser = data.data.approveUser
  formItems.value.approveDate = data.data.approveDate
  formItems.value.biandongMethod = data.data.biandongMethod

  formItems.value.isControl = dist.isControl
  formItems.value.isKeyword = dist.isKeyword
  formItems.value.isOther = dist.isOther
  formItems.value.isAuto = dist.isAuto

  if (data.data.zoneId!=null || data.data.zoneId!='') {
    cityValue.value = data.data.zoneId.split(',')
  }

  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
let isChanged:boolean = false
async function handleOk() {
  if (cityValue.value!='') {
    formItems.value.zoneId = cityValue.value.join(',')
  }
  isChanged = !(formItems.value.bankCode == changeBeforeModel.bankCode
    && formItems.value.bankName == changeBeforeModel.bankName
    && formItems.value.bankAccount == changeBeforeModel.bankAccount
    && formItems.value.bankAddr == changeBeforeModel.bankAddr
    && formItems.value.bankArea == changeBeforeModel.bankArea
    && formItems.value.bankNum == changeBeforeModel.bankNum
    && formItems.value.lianxiPerson == changeBeforeModel.lianxiPerson
    && formItems.value.lianxiPhone == changeBeforeModel.lianxiPhone
    && formItems.value.lianxiQq == changeBeforeModel.lianxiQq
    && formItems.value.lianxiWechat == changeBeforeModel.lianxiWechat
    && formItems.value.countryId == changeBeforeModel.countryId
    && formItems.value.zoneId == changeBeforeModel.zoneId
    && formItems.value.address == changeBeforeModel.address
    && formItems.value.successState == changeBeforeModel.successState
    && formItems.value.applyDatabaseUniqueCode == changeBeforeModel.applyDatabaseUniqueCode
    && formItems.value.applyUser == changeBeforeModel.applyUser
    && formItems.value.applyDate == changeBeforeModel.applyDate
    && formItems.value.approveUser == changeBeforeModel.approveUser
    && formItems.value.approveDate == changeBeforeModel.approveDate
    && formItems.value.biandongMethod == changeBeforeModel.biandongMethod)
  if (formItems.value.bankName==undefined || formItems.value.bankName=='') {
    // alert('请输入银行名称！')
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '请输入银行名称！'
    })
    return false
  }
  if (formItems.value.bankCode==undefined || formItems.value.bankCode=='') {
    // alert('请输入银行编码！')
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '请输入银行编码！'
    })
    return false
  }
  if(isChanged){
    emit('save', unref(formItems))
    closeModal()
    return true
  }
  closeModal()
  // alert("没有改变过")
  return false
}

async function checkCode(){
  if((changeBeforeModel.bankCode!=undefined && changeBeforeModel.bankCode!='') || changeBeforeModel.bankCode==formItems.value.bankCode){
    return true
  }
  if(formItems.value.bankCode == ''){
    return false
  }
  const res = await findByBankCode(formItems.value.bankCode)
  if(res.length!=0){
    // alert('银行编码已存在，请重新输入！')
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '银行编码已存在，请重新输入！'
    })
    formItems.value.bankCode = ''
    console.log(false)
    return false
  }
  return true
}

</script>
<style lang="less" scoped>
:deep(.ant-input),:deep(.ant-calendar-picker-input.ant-input),:deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector .ant-select-selection-search-input){
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;
}
:deep(.ant-select-single:not(.ant-select-customize-input)
.ant-select-selector
.ant-select-selection-search-input) {
  border: none !important;
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

.nc-open-content {
  input {
    width: 35%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-select-selector) {
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
