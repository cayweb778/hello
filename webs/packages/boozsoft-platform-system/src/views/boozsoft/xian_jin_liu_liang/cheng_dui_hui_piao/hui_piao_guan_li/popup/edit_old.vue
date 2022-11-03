<template>
  <BasicModal
    width="1100px"
    v-bind="$attrs"
    title="承兑汇票"
    @ok="handleOk()"
    :showOkBtn="isEdit"
    :showCancelBtn="true"
    @register="register"
  >
    <div :class="isEdit?'nc-open-content':'nc-open-show-content'" style="height: 350px;">
      <div class="open-content-up">
        <label>票据编号<span style="color: red">*</span></label>
        <a-input v-model:value="formItems.billCode" @blur="changBillCode()" placeholder="票据编号" />
        <label>票据类型<span style="color: red">*</span></label>
<!--        <a-input v-model:value="formItems.billType" placeholder="票据类型" />-->
        <a-select v-model:value="formItems.billType" placeholder="票据类型" style="width: 20%">
          <a-select-option value="1">银行承兑汇票</a-select-option>
          <a-select-option value="2">商业承兑汇票</a-select-option>
        </a-select>
        <label>方向<span style="color: red">*</span></label>
<!--        <a-input v-model:value="formItems.fangxiang" placeholder="方向" />-->
        <a-select v-model:value="formItems.fangxiang" @change="changFangxiang()" placeholder="方向" style="width: 20%">
          <a-select-option value="1">收款</a-select-option>
          <a-select-option value="2">付款</a-select-option>
        </a-select>
        <br />

        <label>收到日期<span style="color: red">*</span></label>
        <a-date-picker
          v-model:value="formItems.receiveDate"
          placeholder="收到日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          style="width: 20%"
        />
        <label>出票日期<span style="color: red">*</span></label>
        <a-date-picker
          v-model:value="formItems.chupiaoDate"
          placeholder="出票日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          style="width: 20%"
          :disabled-date="disabledStartDate"
          @openChange="handleStartOpenChange"
        />
        <label>到期日<span style="color: red">*</span></label>
        <a-date-picker
          v-model:value="formItems.daoqiDate"
          placeholder="到期日"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          style="width: 20%"
          :disabled-date="disabledEndDate"
          :open="endOpen"
          @openChange="handleEndOpenChange"
        />
        <br/>

        <label>承兑日期</label>
        <a-date-picker
          v-model:value="formItems.acceptDate"
          placeholder="承兑日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          style="width: 20%"
        />
        <label>结算方式</label>
        <a-select v-model:value="formItems.settModes" placeholder="结算方式" style="width: 20%">
          <a-select-option v-for="settModes in settModesList" :key="settModes.settModesName" :value="settModes.settModesCode" >
            {{ settModes.settModesCode }}-{{ settModes.settModesName }}
          </a-select-option>
        </a-select>
        <label>业务员</label>
        <a-select v-model:value="formItems.yewuUser" placeholder="业务员" style="width: 20%"
                  show-search
                  :filter-option="false"
                  @search="yewuUserHandleSearch"
                  @change="yewuUserHandleChange">
          <a-select-option v-for="item in psnList" :key="item.label" :value="item.key" >
            {{ item.label }}
          </a-select-option>
        </a-select>
        <br/>

        <label>出票单位<span style="color: red">*</span></label>
<!--        <a-input v-model:value="formItems.chupiaoUnit" placeholder="出票单位" />-->
        <a-select v-model:value="formItems.chupiaoUnit" :disabled="formItems.fangxiang=='2'" placeholder="出票单位" style="width: 20%"
                  show-search
                  :filter-option="false"
                  @search="chupiaoUnitHandleSearch"
                  @change="chupiaoUnitHandleChange">
          <a-select-option v-for="item in cusList" :key="item.label" :value="item.key" >
            {{ item.label }}
          </a-select-option>
        </a-select>
        <label>出票单位账号<span style="color: red">*</span></label>
        <a-input v-model:value="formItems.chupiaoUnitAccount" placeholder="出票单位账号" />
        <label>金额<span style="color: red">*</span></label>
        <span @mouseover="clickMoney=false" @mouseout="clickMoney=true">
        <a-input v-if="clickMoney" :value="toThousandFilter(formItems.money)" placeholder="金额" />
        <a-input v-if="!clickMoney" type="number" v-model:value="formItems.money" placeholder="金额" />
        </span>
        <br/>

        <label>付款单位银行<span style="color: red">*</span></label>
        <a-input v-model:value="formItems.payBank" placeholder="付款单位银行" />
        <label>付款行行号<span style="color: red">*</span></label>
        <a-input v-model:value="formItems.payBankNum" placeholder="付款行行号" />
        <label>付款行地址<span style="color: red">*</span></label>
        <a-input v-model:value="formItems.payBankAddress" placeholder="付款行地址" />
        <br/>

        <label>收款单位<span style="color: red">*</span></label>
<!--        <a-input v-model:value="formItems.paymentUnit" placeholder="收款单位" />-->
        <a-select v-model:value="formItems.paymentUnit" :disabled="formItems.fangxiang=='1'" placeholder="收款单位" style="width: 20%"
                  show-search
                  :filter-option="false"
                  @search="paymentUnitHandleSearch"
                  @change="paymentUnitHandleChange">
          <a-select-option v-for="item in supList" :key="item.label" :value="item.key" >
            {{ item.label }}
          </a-select-option>
        </a-select>
        <label>收款单位账号<span style="color: red">*</span></label>
        <a-input v-model:value="formItems.paymentAccount" placeholder="收款单位账号" />
        <label>收款单位银行<span style="color: red">*</span></label>
        <a-input v-model:value="formItems.paymentBank" placeholder="收款单位银行" />
        <br/>

        <label>汇率</label>
        <a-input v-model:value="formItems.huilv" placeholder="汇率" />

        <label>交易合同号码</label>
        <a-input v-model:value="formItems.contractNum" placeholder="交易合同号码" />
        <label>票面利率</label>
        <a-input v-model:value="formItems.billLilv" placeholder="票面利率" />
        <br/>

        <label>背书人</label>
        <a-input v-model:value="formItems.beishuUser" placeholder="背书人" />
        <label>背书金额</label>
        <span @mouseover="clickBeishuMoney=false" @mouseout="clickBeishuMoney=true">
        <a-input v-if="clickBeishuMoney" :value="toThousandFilter(formItems.beishuMoney)" placeholder="背书金额" />
        <a-input v-if="!clickBeishuMoney" type="number" v-model:value="formItems.beishuMoney" placeholder="背书金额" />
        </span>
        <label>背书日期</label>
        <a-date-picker
          v-model:value="formItems.beishuDate"
          placeholder="背书日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          style="width: 20%"
        />
        <br/>

        <label>票据摘要</label>
        <a-input v-model:value="formItems.billRemarks" placeholder="票据摘要" style="width: 50%;" />
        <br/>
        <label>备注</label>
        <a-input v-model:value="formItems.remarks" placeholder="备注" style="width: 50%;" />
        <br/>
      </div>
    </div>
  </BasicModal>
</template>

<script setup="props, { content }" lang="ts">
  import { ref, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { Select as ASelect, Input as AInput, DatePicker as ADatePicker } from 'ant-design-vue';
  import {findSettModesByFlag} from "/@/api/record/system/sett-modes";
  import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
  import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
  import {useUserStore} from "/@/store/modules/user";
  import {useMessage} from "/@/hooks/web/useMessage";
  import {psnFindByFlag} from "/@/api/psn/psn";
  import {findAllByFlag as cusFindAllByFlag} from "/@/api/record/costomer_data/customer";
  import {findAllByFlag as supFindAllByFlag} from "/@/api/record/supplier_data/supplier";
  import {findAcceptBillByBillCode} from "/@/api/record/system/accept-bill";
  const AInputSearch = AInput.Search;
  const ASelectOption = ASelect.Option;

  const emit=defineEmits(['register','save']);

  const {
    createErrorModal
  } = useMessage()

  const formItems: any = ref({});

  const currencyList: any = ref([]);

  let changeBeforeModel: any = {};

  const clickMoney = ref(true)
  const clickBeishuMoney = ref(true)

  const settModesList:any = ref([])
  // const defaultAdName = useCompanyOperateStoreWidthOut().getSchemaName
  const dynamicTenantId = ref()
  const accName = ref()
  const personList:any = ref([])
  const psnList:any = ref([])
  const customerList:any = ref([])
  const supplierList:any = ref([])
  const cusList:any = ref([])
  const supList:any = ref([])
  const isEdit = ref()
  const [register, { closeModal }] = useModalInner((data) => {
    isEdit.value = data.isEdit
    dynamicTenantId.value = data.dynamicTenantId
    // 方式2
    formItems.value = JSON.parse(JSON.stringify(data.data))
    //获取启用人员信息
    useRouteApi(psnFindByFlag,{schemaName: dynamicTenantId})({}).then(res=>{
      personList.value = res.map(item=>{
        const item1:any = {}
        item1.key=item.uniqueCode
        item1.label=item.psnCode+','+item.psnName
        return item1
      })
      psnList.value = personList.value
    })
    //获取客户列表
    useRouteApi(cusFindAllByFlag,{schemaName: dynamicTenantId})('1').then(res=>{
      customerList.value = res.items.map(item=>{
        const item1:any = {}
        item1.key=item.uniqueCode
        item1.label=item.custCode+','+item.custName
        return item1
      })
      cusList.value = customerList.value
    })
    //获取供应商列表
    useRouteApi(supFindAllByFlag,{schemaName: dynamicTenantId})('1').then(res=>{
      supplierList.value = res.items.map(item=>{
        const item1:any = {}
        item1.key=item.uniqueCode
        item1.label=item.supCode+','+item.supName
        return item1
      })
      supList.value = supplierList.value
    })

    //获取公司名
    useCompanyOperateStoreWidthOut().getAccountList.forEach(item => {
      if (item.accId==data.defaultAdName && item.accName!='' && item.accName!=null){
        accName.value = item.accName
        if (formItems.value.fangxiang=='2'){
          formItems.value.chupiaoUnit = item.accName
        } else {
          formItems.value.paymentUnit = item.accName
        }
      }
    })

    settModesList.value = []
    useRouteApi(findSettModesByFlag,{schemaName: dynamicTenantId})({}).then(res=>{
      settModesList.value = res.items
    })

    if (formItems.value.createUser=='' || formItems.value.createUser==null){
      formItems.value.createUser = useUserStore().getUserInfo['name']
    }
    formItems.value.editUser = useUserStore().getUserInfo['name']
    if (formItems.value.receiveDate=='' || formItems.value.receiveDate==null){
      formItems.value.receiveDate = useCompanyOperateStoreWidthOut().loginDate
    }
    if (formItems.value.flag=='' || formItems.value.flag==null){
      formItems.value.flag = '0'
    }

    changeBeforeModel = JSON.parse(JSON.stringify(formItems.value));
  });

  //选择方向事件
  function changFangxiang(){
    if (formItems.value.fangxiang=='2'){
      formItems.value.chupiaoUnit = accName.value
      formItems.value.paymentUnit = ''
    } else {
      formItems.value.paymentUnit = accName.value
      formItems.value.chupiaoUnit = ''
    }
  }

  //出票日期小于到期日
  const endOpen=ref(false)
  function disabledStartDate(startValue) {
    const endValue = formItems.value.daoqiDate;
    if (!startValue || !endValue) {
      return false;
    }
    return startValue.valueOf() > endValue.valueOf();
  }
  function disabledEndDate(endValue) {
    const startValue = formItems.value.chupiaoDate;
    if (!endValue || !startValue) {
      return false;
    }
    return startValue.valueOf() >= endValue.valueOf();
  }

  function handleStartOpenChange(open) {
    if (!open) {
      endOpen.value = true;
    }
  }
  function handleEndOpenChange(open) {
    endOpen.value = open;
  }

  //出票单位筛选事件
  function chupiaoUnitHandleSearch(value){
    cusList.value = customerList.value.filter(item=>{
      return item.label.indexOf(value)!= -1
    })
  }
  function chupiaoUnitHandleChange(value){
    cusList.value = customerList.value.filter(item=>{
      if (item.label.indexOf(value)!= -1 || item.key.indexOf(value)!= -1){
        formItems.value.chupiaoUnit = item.key
      }
      return item.key==formItems.value.chupiaoUnit
    })
  }

  //业务员筛选事件
  function yewuUserHandleSearch(value){
    psnList.value = personList.value.filter(item=>{
      return item.label.indexOf(value)!= -1
    })
  }
  function yewuUserHandleChange(value){
    psnList.value = personList.value.filter(item=>{
      if (item.label.indexOf(value)!= -1 || item.key.indexOf(value)!= -1){
        formItems.value.yewuUser = item.key
      }
      return item.key==formItems.value.yewuUser
    })
  }

  //收款单位筛选事件
  function paymentUnitHandleSearch(value){
    supList.value = supplierList.value.filter(item=>{
      return item.label.indexOf(value)!= -1
    })
  }
  function paymentUnitHandleChange(value){
    supList.value = supplierList.value.filter(item=>{
      if (item.label.indexOf(value)!= -1 || item.key.indexOf(value)!= -1){
        formItems.value.paymentUnit = item.key
      }
      return item.key==formItems.value.paymentUnit
    })
  }

  let isChanged = false;
  async function handleOk() {
    if (formItems.value.billCode=='' || formItems.value.billCode==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '票据编号不能为空！'
      })
      return false
    }
    if (formItems.value.billType=='' || formItems.value.billType==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '出票类型不能为空！'
      })
      return false
    }
    if (formItems.value.fangxiang=='' || formItems.value.fangxiang==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '收支方向不能为空！'
      })
      return false
    }
    if (formItems.value.receiveDate=='' || formItems.value.receiveDate==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '收到日期不能为空！'
      })
      return false
    }
    if (formItems.value.chupiaoDate=='' || formItems.value.chupiaoDate==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '出票日期不能为空！'
      })
      return false
    }
    if (formItems.value.daoqiDate=='' || formItems.value.daoqiDate==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '到期日不能为空！'
      })
      return false
    }
    if (formItems.value.chupiaoUnit=='' || formItems.value.chupiaoUnit==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '出票单位不能为空！'
      })
      return false
    }
    if (formItems.value.chupiaoUnitAccount=='' || formItems.value.chupiaoUnitAccount==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '出票单位账号不能为空！'
      })
      return false
    }
    if (formItems.value.money=='' || formItems.value.money==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '金额不能为空！'
      })
      return false
    }
    if (formItems.value.payBank=='' || formItems.value.payBank==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '付款单位银行不能为空！'
      })
      return false
    }
    if (formItems.value.payBankNum=='' || formItems.value.payBankNum==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '付款行行号不能为空！'
      })
      return false
    }
    if (formItems.value.payBankAddress=='' || formItems.value.payBankAddress==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '付款行地址不能为空！'
      })
      return false
    }
    if (formItems.value.paymentUnit=='' || formItems.value.paymentUnit==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '收款单位不能为空！'
      })
      return false
    }
    if (formItems.value.paymentAccount=='' || formItems.value.paymentAccount==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '收款单位账号不能为空！'
      })
      return false
    }
    if (formItems.value.paymentBank=='' || formItems.value.paymentBank==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '收款单位银行不能为空！'
      })
      return false
    }
    isChanged = !(JSON.stringify(formItems.value) == JSON.stringify(changeBeforeModel))
    if (isChanged) {
      emit('save', unref(formItems));
      closeModal();
      return true;
    }
    closeModal();
    // alert("没有改变过")
    return false;
  }

  //判断票据编号是否存在
  async function changBillCode() {
    if ((changeBeforeModel.billCode != undefined && changeBeforeModel.billCode != '') || changeBeforeModel.billCode == formItems.value.billCode) {
      return true
    }
    if (formItems.value.billCode == null || formItems.value.billCode == '') {
      return true
    }
    const res = await useRouteApi(findAcceptBillByBillCode, {schemaName: dynamicTenantId})(formItems.value.billCode)
    if (res.length != 0) {
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '票据编号已存在，请重新输入！'
      })
      formItems.value.billCode = ''
      console.log(false)
      return false
    }
    return true
  }

  //金额格式化
  function toThousandFilter(num: any) {
    if (num == '' || num == null) {
      return ''
    }
    return (+num || 0).toFixed(2).replace(/\d{1,3}(?=(\d{3})+(\.\d*)?$)/g, '$&,')
  }
</script>
<style>
.vben-basic-title {
  color: rgb(1, 129, 226) !important;
}

.ant-modal-body {
  padding: 0px;
  border: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
}
</style>
<style lang="less" scoped>
:deep(.ant-calendar-picker-input.ant-input),
:deep(.ant-select-single:not(.ant-select-customize-input)
      .ant-select-selector
      .ant-select-selection-search-input) {
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
      width: 20%;
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
      text-align: left;
      width: 112px;
      display: inline-block;
      padding: 5px 10px;
      margin-left: 1em;
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

.nc-open-show-content {

  input {
    width: 20%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  pointer-events: none;
  cursor: default;

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  label {
    text-align: left;
    width: 112px;
    display: inline-block;
    padding: 5px 10px;
    margin-left: 1em;
    color: #535353;
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
