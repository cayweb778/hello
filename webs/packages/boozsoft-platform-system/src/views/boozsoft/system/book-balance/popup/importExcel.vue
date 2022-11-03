<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="导出发生及余额表"
    @ok="handleOk()"
    @cancel="resetPageData()"
    @register="register"
  >
    <a-spin :spinning="spinning" :delay="500">
      <div class="nc-open-content">
        <div style="width: 120px;border-bottom: 3px solid #0188e8">
          <span style="font-size: 18px;color: #2828c9;margin-left: 5px">导出范围</span>
        </div>
        <div style="margin: 4% 0">
          <a-radio-group v-model:value="pageVariable.scopeCondition" @change="radioChange">
            <a-radio :value="1">
              导出当前页
            </a-radio>
            <div style="height: 10px"></div>
            <a-radio :value="2">
              按条件导出
            </a-radio>
          </a-radio-group>
        </div>
        <div style="position: relative;border: 1px #666565 solid">
          <span style="position: inherit; top: -12px; left: 35px;background-color: white;">&emsp;导出条件&emsp;</span>
          <div>
            <ul>
              <li>
                <span>&emsp;凭证号：</span>
                <a-input v-model:value="pageVariable.batchCondition.voucherNumberStart" :allowClear="true"
                         :disabled="pageVariable.scopeCondition == 1" placeholder="" style="width: 227px"/>
                <span>&emsp;~&emsp;</span>
                <a-input v-model:value="pageVariable.batchCondition.voucherNumberEnd" :allowClear="true"
                         :disabled="pageVariable.scopeCondition == 1" placeholder="" style="width: 227px"/>
              </li>
              <li>
                <span>&emsp;制单人：</span>
                <a-select v-model:value="pageVariable.batchCondition.maker" :disabled="pageVariable.scopeCondition == 1"
                          style="width: 227px">
                </a-select>
                <span>&emsp;&emsp;&emsp;出纳：</span>
                <a-select v-model:value="pageVariable.batchCondition.cashier"
                          :disabled="pageVariable.scopeCondition == 1" style="width: 185px">
                </a-select>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </a-spin>
  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import {onMounted, ref, reactive,  unref} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {message} from 'ant-design-vue'
import {useUserStore, useUserStoreWidthOut} from "/@/store/modules/user";
import {useMessage} from "/@/hooks/web/useMessage";

import {
  DatePicker as ADatePicker,
  Select as ASelect,
  Input as AInput,
  Popover as APopover,
  Switch as ASwitch,
  Radio as ARadio,
  Checkbox as ACheckbox,
  Spin as ASpin
} from "ant-design-vue"
import {findPeriodByAccontId} from "/@/api/record/generalLedger/data";
import {
  askTask,
  compareTime,
  findByFunctionModule,
  findAccCloseListByYaer,
  getCurrentAccountName,
  offsetToStr, pointMessage
} from "/@/api/task-api/tast-bus-api";
import {initBasisTabData} from "/@/api/record/system/financial-settings";
import {startVoucherReviewProcedural} from '/@/api/record/system/accvoucher'

const { createWarningModal } = useMessage()
const ARangePicker = ADatePicker.RangePicker
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ARadioGroup = ARadio.Group
const ARadioButton = ARadio.Button
const userStore = useUserStore();
// 页面变量
const pageMark = ref(true)
const intervals = ref([])
const dateList: any = ref([])
const pageVariable = reactive({
  scopeCondition: 1,
  batchCondition: {
    voucherPeriod: '',
    voucherDateStart: '',
    voucherDateEnd: '',
    voucherNumberStart: '',
    voucherNumberEnd: '',
    maker: '',
    cashier: '',
    checkName: ''
  },
  operatorUserName: '',
  selectMaker: false,
  selectCashier: false
})
const selectedRowKeys: any = ref([])

const spinning = ref<boolean>(false);
const emit=defineEmits(['reload','register'])
const [register, {closeModal, changeOkLoading}] = useModalInner((data) => {
  selectedRowKeys.value = data.data.selectedRowKeys
  pageMark.value = data.data.mark
})


onMounted(async () => {

})

const resetPageData = ()=>{
  pageVariable.scopeCondition = 1
  pageVariable.batchCondition.voucherPeriod = ''
  pageVariable.batchCondition.voucherDateStart = ''
  pageVariable.batchCondition.voucherDateEnd = ''
  pageVariable.batchCondition.voucherNumberStart = ''
  pageVariable.batchCondition.voucherNumberEnd = ''
  pageVariable.batchCondition.maker = ''
  pageVariable.batchCondition.cashier = ''
  pageVariable.batchCondition.checkName = ''
  intervals.value = []
  pageVariable.selectMaker = false
  pageVariable.selectCashier =  false
  selectedRowKeys.value = []
}

const handleOk = async () => {

}

const radioChange = () => {
  if (pageVariable.scopeCondition === 1) {
    pageVariable.batchCondition.voucherPeriod = ''
    pageVariable.batchCondition.voucherDateStart = ''
    pageVariable.batchCondition.voucherDateEnd = ''
    pageVariable.batchCondition.voucherNumberStart = ''
    pageVariable.batchCondition.voucherNumberEnd = ''
    pageVariable.batchCondition.maker = ''
    pageVariable.batchCondition.cashier = ''
    pageVariable.batchCondition.checkName = ''
    intervals.value = []
  }
}
</script>
<style lang="less" scoped>
:deep(.ant-calendar-picker-input.ant-input), :deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector), :deep(.ant-input-affix-wrapper) {
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;
}

.nc-open-content {
  margin: 2% 10%;
  color: black;
  font-size: 14px;
  font-weight: bold;

  ul {
    margin: 0 5% 5%;

    li {
      margin: 2% 0;
    }
  }
}
</style>
