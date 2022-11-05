<template>
  <BasicModal
    width="600px"
    class="spaceLogo"
    v-bind="$attrs"
    title="出纳凭证修改"
    @ok="handleOk()"
    @cancel="handleClose()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <ul>
        <li><span>结算方式&emsp;&emsp;：</span>
          <a-select v-model:value="formItems.value1" :allowClear="true" style="width: 76%">
            <a-select-option v-for="item in settlementMethodList" :key="item.id" :value="item.settModesCode">{{item.settModesName}}</a-select-option>
          </a-select>
        </li>
        <li>
          <span>结算号码&emsp;&emsp;：</span>
          <a-input v-model:value="formItems.value2" :allowClear="true" placeholder="" style="width: 76%"/>
        </li>
        <li><span>结算日期&emsp;&emsp;：</span>
          <a-date-picker v-model:value="formItems.value3" style="width: 76%" />
        </li>
        <li><span>结算单位类型：</span>
          <a-radio-group v-model:value="unitNameType" size="small">
            <a-radio-button value="1">客户列表&emsp;</a-radio-button>
            <a-radio-button value="2">供应商列表</a-radio-button>
            <a-radio-button value="3">手工录入&emsp;</a-radio-button>
          </a-radio-group>
        </li>
        <li><span>结算单位名称：</span>
          <a-input v-if="unitNameType == '3'" v-model:value="formItems.value4" :allowClear="true" placeholder="" style="width: 76%"/>
          <a-select  show-search v-else v-model:value="formItems.value4" :allowClear="true" style="width: 76%">
            <template v-if="unitNameType == '1'" v-for="item in customerList">
              <a-select-option  :value="item.custName">{{item.custName}}</a-select-option>
            </template>
            <template v-else v-for="item in partnerList">
              <a-select-option  :value="item.supName">{{item.supName}}</a-select-option>
            </template>
          </a-select>
        </li>
      </ul>
    </div>
  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {ref, reactive,  onMounted} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {PageWrapper} from '/@/components/Page'
import {
  DatePicker as ADatePicker,
  Select as ASelect,
  Input as AInput,
  Checkbox as ACheckbox,
  Switch as ASwitch,
  Radio as ARadio,
  Button as AButton,
  message
} from "ant-design-vue"
import moment from 'moment';
import {useUserStore} from "/@/store/modules/user";
import {findAll as customerFindAll} from '/@/api/record/system/customer';
import {findAll as supplierFindAll} from '/@/api/record/supplier_data/supplier';
import {changeVoucherSettlementInfo, findAllCashierAccvoucher} from "/@/api/record/system/accvoucher";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {findAllSubjectInitialBalance} from "/@/api/subjectInitialBalance/subjectInitialBalance";

const userStore = useUserStore();
const ARangePicker = ADatePicker.RangePicker
const ASelectOption = ASelect.Option
const ARadioButton = ARadio.Button
const ARadioGroup = ARadio.Group
const emit = defineEmits(['reload','register']);

const formItems: any = ref({
  value1:'',
  value2:'',
  value3: moment(),
  value4:''
})
let changeBeforeModel: any = {}

const unitNameType = ref('3')
const settlementMethodList = ref([])
const customerList = ref([])
const partnerList = ref([])

/*onMounted(async ()=>{
  initDefalutFormData()
})*/

const [register, {closeModal, setModalProps}] = useModalInner((data) => {
  changeBeforeModel = data.data
  setModalProps({minHeight: 250});
  if (settlementMethodList.value.length === 0)
    settlementMethodList.value = data.data.settlementMethodList
  initDefalutFormData()
  if (null != changeBeforeModel.pjCsettle && changeBeforeModel.pjCsettle != ''){
      formItems.value.value1 =  changeBeforeModel.pjCsettle,
      formItems.value.value2 =  changeBeforeModel.pjId,
      formItems.value.value3 =  moment(changeBeforeModel.pjDate,'YYYY-MM-DD')
      formItems.value.value4 =  changeBeforeModel.pjUnitName
  }
})

let isChanged: boolean = false
async function handleOk() {

  let pjDate = null != formItems.value.value3?formItems.value.value3.format('YYYY-MM-DD'):''
  isChanged = (formItems.value.value1 == changeBeforeModel.pjCsettle && formItems.value.value2 == changeBeforeModel.pjId
    && pjDate == changeBeforeModel.pjDate && formItems.value.value4 ==  changeBeforeModel.pjUnitName)
  if (null == formItems.value.value1 || formItems.value.value1 == '') {
    message.error('请选择结算方式!');
  }/*else if (formItems.value.value2 == ''){
    message.error('请输入结算号码!');
  }*/else if (pjDate == ''){
    message.error('请选择结算日期!');
  }/*else if (formItems.value.value4 == ''){
    message.error('请选择或输入结算单位名称!');
  }*/else if (isChanged){
    message.error('请修改后再点击确定按钮!');
  }else {
    // 获取当前凭证的唯一 分录唯一码
    changeBeforeModel.pjCsettle =  formItems.value.value1
    changeBeforeModel.pjId = formItems.value.value2
    changeBeforeModel.pjDate =  pjDate
    changeBeforeModel.pjUnitName = formItems.value.value4
    let res = await useRouteApi(changeVoucherSettlementInfo,{schemaName:getCurrentAccountName(false)+'-'+changeBeforeModel.iyear})(changeBeforeModel)
    if (res.length >= 0){
      message.success('修改成功!');
    }else {
      message.error('修改失败!');
    }
    emit('reload')
    closeModal()
    resetForm()
    return true
  }
}
async function handleClose() {
  closeModal()
  resetForm()
}

/********* Mr.ye **********/
const initDefalutFormData = async () => {
  // 获取初始化数据 结算方式列表 、客户列表 与供应商列表
  if (customerList.value.length === 0)
  customerList.value = (await customerFindAll()).items
  if (partnerList.value.length === 0)
  partnerList.value = (await supplierFindAll()).items
}

const resetForm = ()=> {
  formItems.value.value1 = '',
  formItems.value.value2 = '',
  formItems.value.value3 = '',
  formItems.value.value4 = ''
  changeBeforeModel = {}
}
/********* Mr.ye **********/
</script>
<style lang="less" scoped>
:deep(.ant-calendar-picker-input.ant-input), :deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector),:deep(.ant-input-affix-wrapper) {
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;
}
.nc-open-content{
  ul{
    margin: 5% 10% 0;
    li{
      margin: 2% 0;
    }
  }
}
</style>
