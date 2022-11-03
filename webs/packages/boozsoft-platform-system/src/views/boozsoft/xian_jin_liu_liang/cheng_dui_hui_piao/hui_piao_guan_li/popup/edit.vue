<template>
  <BasicModal
    width="800px"
    v-bind="$attrs"
    title="承兑汇票"
    @ok="handleOk()"
    :showOkBtn="isEdit"
    :showCancelBtn="true"
    @register="register"
    :canFullscreen="false"
  >
    <template #title>
      <div style="display: flex;margin-left: 10px;margin-top: 10px;" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='0'">
          <PlusCircleOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;承兑汇票
        </span>
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='1'">
          <FormOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;承兑汇票
        </span>
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='2'">
          <FileSearchOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;承兑汇票
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 60px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="/@/assets/images/bank.png" style="height:80px;margin-right: 10px;"/>
      </div>
    </template>
    <div :class="isEdit?'nc-open-content':'nc-open-show-content'" style="max-height: 350px;">
      <div class="open-content-up">
        <div style="width:70%;margin-left: 10%;margin-top: 20px;">
          <a-radio-group v-model:value="formItems.fangxiang" @change="changFangxiang()" style="margin-left:15px;">
            <a-radio value="1" style="width:100px;">
              <span style="color: #000000;font-weight: bold;font-size: 12px;">收款</span>
            </a-radio>
            <a-radio value="2" style="width:100px;margin-left: -10px;">
              <span style="color: #000000;font-weight: bold;font-size: 12px;">付款</span>
            </a-radio>
          </a-radio-group>
          <br/>

          <label style="font-size: 18px;margin-left: 0;width:100px;">金额：</label>
<!--          <a-input-number
            v-model:value="formItems.money"
            placeholder=""
            class="abc"
            :precision="2"
            style="width: 60%;border-bottom: 2px solid #000000;"
          />-->
          <span @mouseover="clickMoney=false" @mouseout="clickMoney=true">
            <a-input v-if="clickMoney" :value="toThousandFilter(formItems.money)" prefix="￥" placeholder="金额" class="abc" style="width: 60%;border-bottom: 2px solid #000000;" />
            <a-input v-if="!clickMoney" type="number" v-model:value="formItems.money" prefix="￥" placeholder="金额" class="abc" style="width: 60%;border-bottom: 2px solid #000000;" />
          </span>
          <span class="red_span">{{isState=='2'?'':'*'}}</span>
        </div>
        <br/><br/>

        <label>出票日期：</label>
        <a-date-picker
          v-model:value="formItems.chupiaoDate"
          placeholder="出票日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          style="width: 30%"
          @change="changeChupiaoDate()"
        />
        <span class="red_span">{{isState=='2'?'':'*'}}</span>
        <label>票据编号:</label>
        <a-input v-model:value="formItems.billCode" @blur="changBillCode()" placeholder="票据编号" />
        <span class="red_span">{{isState=='2'?'':'*'}}</span>
        <br/>

        <label>票据类型：</label>
        <a-select v-model:value="formItems.billType" placeholder="票据类型" style="width: 30%">
          <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
          <a-select-option value="1">银行承兑汇票</a-select-option>
          <a-select-option value="2">商业承兑汇票</a-select-option>
        </a-select>
        <span class="red_span">{{isState=='2'?'':'*'}}</span>
        <label>承兑日期：</label>
        <a-date-picker
          v-model:value="formItems.acceptDate"
          placeholder="承兑日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          :disabled-date="disabledChupiaoDate"
          style="width: 30%"
          @change="changeAcceptDate()"
        />
        <span class="red_span">{{isState=='2'?'':'*'}}</span>
<!--        <label>方向<span style="color: red">*</span></label>
        <a-select v-model:value="formItems.fangxiang" @change="changFangxiang()" placeholder="方向" style="width: 20%">
          <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
          <a-select-option value="1">收款</a-select-option>
          <a-select-option value="2">付款</a-select-option>
        </a-select>-->
        <br />

<!--        <label>结算方式</label>
        <a-select v-model:value="formItems.settModes" placeholder="结算方式" style="width: 30%">
          <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
          <a-select-option v-for="settModes in settModesList" :key="settModes.settModesName" :value="settModes.settModesCode" >
            {{ settModes.settModesCode }}-{{ settModes.settModesName }}
          </a-select-option>
        </a-select>-->
        <label>票面利率：</label>
        <a-input v-model:value="formItems.billLilv" placeholder="票面利率" />
        <span class="red_span"></span>
        <label>业务员：</label>
        <a-select v-model:value="formItems.yewuUser" placeholder="业务员" style="width: 30%"
                  show-search
                  :filter-option="false"
                  @search="yewuUserHandleSearch"
                  @change="yewuUserHandleChange">
          <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
          <a-select-option v-for="item in psnList" :key="item.label" :value="item.key" >
            {{ item.label }}
          </a-select-option>
        </a-select>
        <span class="red_span"></span>
        <br/>

        <label>出票单位：</label>
        <a-select v-model:value="formItems.chupiaoUnit" :disabled="formItems.fangxiang=='2'" placeholder="出票单位" style="width: 30%"
                  show-search
                  :filter-option="false"
                  @search="chupiaoUnitHandleSearch"
                  @change="chupiaoUnitHandleChange">
          <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
          <a-select-option v-for="item in cusList" :key="item.label" :value="item.key" >
            {{ item.label }}
          </a-select-option>
        </a-select>
        <span class="red_span">{{isState=='2'?'':'*'}}</span>
        <label>收款单位：</label>
        <a-select v-model:value="formItems.paymentUnit" :disabled="formItems.fangxiang=='1'" placeholder="收款单位" style="width: 30%"
                  show-search
                  :filter-option="false"
                  @search="paymentUnitHandleSearch"
                  @change="paymentUnitHandleChange">
          <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
          <a-select-option v-for="item in supList" :key="item.label" :value="item.key" >
            {{ item.label }}
          </a-select-option>
        </a-select>
        <span class="red_span">{{isState=='2'?'':'*'}}</span>
      </div>

      <div class="open-content-down">
        <a-tabs v-model:activeKey="activeKey" type="card">
          <a-tab-pane key="1" tab="常用">
            <div class="down-tab-content">
              <label>收票日期：</label>
              <a-date-picker
                v-model:value="formItems.receiveDate"
                placeholder="收到日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                :disabled-date="disabledChupiaoDate"
                style="width: 30%"
              />
              <span class="red_span">{{isState=='2'?'':'*'}}</span>
              <label>到期日：</label>
              <a-date-picker
                v-model:value="formItems.daoqiDate"
                placeholder="到期日"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 30%"
                :disabled-date="disabledAcceptDate"
              />
              <span class="red_span">{{isState=='2'?'':'*'}}</span>
              <br/>
<!--              <label>汇率</label>
              <a-input v-model:value="formItems.huilv" placeholder="汇率" />-->
              <label>交易合同号：</label>
              <a-input v-model:value="formItems.contractNum" placeholder="交易合同号码" />
              <span class="red_span"></span>
              <label>票据摘要：</label>
              <a-input v-model:value="formItems.billRemarks" placeholder="票据摘要" style="width: 30%;" />
              <span class="red_span"></span>
              <br/><br/><br/><br/>
<!--              <label>备注</label>
              <a-input v-model:value="formItems.remarks" placeholder="备注" style="width: 50%;" />-->
            </div>
          </a-tab-pane>
          <a-tab-pane key="2" tab="账户">
            <div class="down-tab-content">
              <label>出票单位银行：</label>
              <a-input v-model:value="formItems.payBank" placeholder="出票单位银行" />
              <span class="red_span"></span>
              <label>收款单位银行：</label>
              <a-input v-model:value="formItems.paymentBank" placeholder="收款单位银行" />
              <span class="red_span"></span>
              <br/>
              <label>出票单位行号：</label>
              <a-input v-model:value="formItems.payBankNum" placeholder="出票单位行号" />
              <span class="red_span"></span>
              <label>收款单位行号：</label>
              <a-input v-model:value="formItems.paymentBankNum" placeholder="收款单位行号" />
              <span class="red_span"></span>
              <br/>
              <label>出票单位账号：</label>
              <a-input v-model:value="formItems.chupiaoUnitAccount" placeholder="出票单位账号" />
              <span class="red_span"></span>
              <label>收款单位账号：</label>
              <a-input v-model:value="formItems.paymentAccount" placeholder="收款单位账号" />
              <span class="red_span"></span>
              <br/>
              <label>出票单位地址：</label>
              <a-input v-model:value="formItems.payBankAddress" placeholder="出票单位地址" />
              <span class="red_span"></span>
              <br/>
            </div>
          </a-tab-pane>
          <a-tab-pane key="3" tab="背书">
            <div class="down-tab-content">
              <label>背书人：</label>
              <a-input v-model:value="formItems.beishuUser" placeholder="背书人" />
              <span class="red_span"></span>
              <label>背书金额：</label>
              <a-input :value="toThousandFilter(formItems.money)" prefix="￥" placeholder="金额" style="width: 30%" />
              <span class="red_span"></span>
              <br/>
              <label>背书日期：</label>
              <a-date-picker
                v-model:value="formItems.beishuDate"
                placeholder="背书日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 30%"
              />
              <span class="red_span"></span>
              <br/><br/><br/><br/>
            </div>
          </a-tab-pane>
          <a-tab-pane key="4" tab="贴现">
            <div class="down-tab-content">
              <label>贴现人：</label>
              <a-input v-model:value="formItems.tiexianUser" placeholder="贴现人" />
              <span class="red_span"></span>
              <label>贴现金额：</label>
              <a-input :value="toThousandFilter(formItems.money)" prefix="￥" placeholder="金额" style="width: 30%" />
              <span class="red_span"></span>
              <br/>
              <label>贴现日期：</label>
              <a-date-picker
                v-model:value="formItems.tiexianDate"
                placeholder="贴现日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 30%"
              />
              <span class="red_span"></span>
              <br/><br/><br/><br/>
            </div>
          </a-tab-pane>
          <a-tab-pane key="5" tab="结算">
            <div class="down-tab-content">
              <label>结算人：</label>
              <a-input v-model:value="formItems.jiesuanUser" placeholder="结算人" />
              <span class="red_span"></span>
              <label>结算金额：</label>
              <a-input :value="toThousandFilter(formItems.money)" prefix="￥" placeholder="金额" style="width: 30%" />
              <span class="red_span"></span>
              <br/>
              <label>结算日期：</label>
              <a-date-picker
                v-model:value="formItems.jiesuanDate"
                placeholder="结算日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 30%"
              />
              <span class="red_span"></span>
              <br/><br/><br/><br/>
            </div>
          </a-tab-pane>
        </a-tabs>




<!--        <label>金额<span style="color: red">*</span></label>
        <span @mouseover="clickMoney=false" @mouseout="clickMoney=true">
        <a-input v-if="clickMoney" :value="toThousandFilter(formItems.money)" placeholder="金额" />
        <a-input v-if="!clickMoney" type="number" v-model:value="formItems.money" placeholder="金额" />
        </span>-->

      </div>
    </div>

    <template #footer>
      <div v-if="isState=='2'">
        <a-button @click="closeModal()" type="primary">关闭</a-button>
      </div>
      <div v-if="isState=='0'">
        <a-button @click="closeModal()">取消</a-button>
        <a-button @click="handleOk()" :disabled="saveClick">保存</a-button>
        <a-button @click="handleOkAdd()" :disabled="saveClick" type="primary">保存并新增</a-button>
      </div>
      <div v-if="isState=='1'">
        <a-button @click="closeModal()">放弃</a-button>
        <a-button @click="handleOk()" :disabled="saveClick" type="primary">保存</a-button>
      </div>
    </template>
  </BasicModal>
</template>

<script setup="props, { content }" lang="ts">
  import { ref, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import {
    Select as ASelect,
    Input as AInput,
    InputNumber as AInputNumber,
    DatePicker as ADatePicker,
    Radio as ARadio,
    Tabs as ATabs,
    message
  } from 'ant-design-vue';
  import {findSettModesByFlag} from "/@/api/record/system/sett-modes";
  import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
  import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
  import {useUserStore} from "/@/store/modules/user";
  import {useMessage} from "/@/hooks/web/useMessage";
  import {psnFindByFlag} from "/@/api/psn/psn";
  import {findAllByFlag as cusFindAllByFlag} from "/@/api/record/costomer_data/customer";
  import {findAllByFlag as supFindAllByFlag} from "/@/api/record/supplier_data/supplier";
  import {findAcceptBillByBillCode, saveAcceptBill} from "/@/api/record/system/accept-bill";
  import {
    PlusCircleOutlined,
    FormOutlined,
    FileSearchOutlined,
    CaretDownOutlined
  } from '@ant-design/icons-vue'
  import {hasBlank} from "/@/api/task-api/tast-bus-api";
  import moment from "moment";
  const AInputSearch = AInput.Search;
  const ASelectOption = ASelect.Option;
  const ARadioGroup = ARadio.Group
  const ATabPane = ATabs.TabPane

  const emit=defineEmits(['register','save']);
  const activeKey = ref('1')

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
  const isState = ref('0')
  const saveClick:any = ref(false)
  const [register, { closeModal }] = useModalInner((data) => {
    saveClick.value=false
    isEdit.value = data.isEdit
    dynamicTenantId.value = data.dynamicTenantId
    isState.value = data.isState
    // 方式2
    formItems.value = JSON.parse(JSON.stringify(data.data))
    //获取启用人员信息
    useRouteApi(psnFindByFlag,{schemaName: dynamicTenantId})({}).then(res=>{
      personList.value = res.map(item=>{
        const item1:any = {}
        item1.key=item.uniqueCode
        item1.label=item.psnCode+'  -  '+item.psnName
        return item1
      })
      psnList.value = personList.value
    })
    //获取客户列表
    useRouteApi(cusFindAllByFlag,{schemaName: dynamicTenantId})('1').then(res=>{
      customerList.value = res.items.map(item=>{
        const item1:any = {}
        item1.key=item.uniqueCode
        item1.label=item.custCode+'  -  '+item.custName
        return item1
      })
      cusList.value = customerList.value
    })
    //获取供应商列表
    useRouteApi(supFindAllByFlag,{schemaName: dynamicTenantId})('1').then(res=>{
      supplierList.value = res.items.map(item=>{
        const item1:any = {}
        item1.key=item.uniqueCode
        item1.label=item.supCode+'  -  '+item.supName
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
  /*const endOpen=ref(false)
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
  }*/

  const disabledChupiaoDate = (current) => {
    // 获取区间最小区间
    if (!hasBlank(formItems.value.chupiaoDate)) {
      let min = moment(formItems.value.chupiaoDate, 'YYYY-MM-DD')
      if (formItems.value.receiveDate==null || formItems.value.receiveDate=='' || formItems.value.chupiaoDate>formItems.value.receiveDate) {
        formItems.value.receiveDate = formItems.value.chupiaoDate
      }
      return current < min
    }
  }
  function changeChupiaoDate(){
    if (formItems.value.chupiaoDate!=null && formItems.value.chupiaoDate!='') {
      if (formItems.value.receiveDate != null && formItems.value.receiveDate != '') {
        if (formItems.value.chupiaoDate > formItems.value.receiveDate) {
          formItems.value.buyTime = formItems.value.creatTime
        }
      } else {
        formItems.value.receiveDate = formItems.value.chupiaoDate
      }
      if (formItems.value.acceptDate != null && formItems.value.acceptDate != '') {
        if (formItems.value.chupiaoDate > formItems.value.acceptDate) {
          formItems.value.acceptDate = formItems.value.acceptDate
          changeAcceptDate()
        }
      }
    }
  }
  const disabledAcceptDate = (current) => {
    // 获取区间最小区间
    if (!hasBlank(formItems.value.acceptDate)) {
      let min = moment(formItems.value.acceptDate, 'YYYY-MM-DD')
      if (formItems.value.daoqiDate==null || formItems.value.daoqiDate=='' || formItems.value.acceptDate>formItems.value.daoqiDate) {
        formItems.value.daoqiDate = formItems.value.acceptDate
      }
      return current < min
    }
  }
  function changeAcceptDate(){
    if (formItems.value.acceptDate!=null && formItems.value.acceptDate!='') {
      if (formItems.value.daoqiDate != null && formItems.value.daoqiDate != '') {
        if (formItems.value.acceptDate > formItems.value.daoqiDate) {
          formItems.value.daoqiDate = formItems.value.acceptDate
        }
      } else {
        formItems.value.daoqiDate = formItems.value.acceptDate
      }
    }
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
    saveClick.value=true
    if (formItems.value.billCode=='' || formItems.value.billCode==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '票据编号不能为空！'
      })
      saveClick.value=false
      return false
    }
    if (formItems.value.billType=='' || formItems.value.billType==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '出票类型不能为空！'
      })
      saveClick.value=false
      return false
    }
    if (formItems.value.fangxiang=='' || formItems.value.fangxiang==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '收支方向不能为空！'
      })
      saveClick.value=false
      return false
    }
    if (formItems.value.receiveDate=='' || formItems.value.receiveDate==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '收到日期不能为空！'
      })
      saveClick.value=false
      return false
    }
    if (formItems.value.chupiaoDate=='' || formItems.value.chupiaoDate==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '出票日期不能为空！'
      })
      saveClick.value=false
      return false
    }
    if (formItems.value.daoqiDate=='' || formItems.value.daoqiDate==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '到期日不能为空！'
      })
      saveClick.value=false
      return false
    }
    if (formItems.value.acceptDate=='' || formItems.value.acceptDate==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '承兑日期不能为空！'
      })
      saveClick.value=false
      return false
    }
    if (formItems.value.money=='' || formItems.value.money==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '金额不能为空！'
      })
      saveClick.value=false
      return false
    }
    isChanged = !(JSON.stringify(formItems.value) == JSON.stringify(changeBeforeModel))
    if (isChanged) {
      emit('save', unref(formItems));
      closeModal();
      saveClick.value=false
      return true;
    }
    closeModal();
    // alert("没有改变过")
    saveClick.value=false
    return false;
  }

  async function handleOkAdd() {
    saveClick.value=true
    if (formItems.value.billCode=='' || formItems.value.billCode==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '票据编号不能为空！'
      })
      saveClick.value=false
      return false
    }
    if (formItems.value.billType=='' || formItems.value.billType==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '出票类型不能为空！'
      })
      saveClick.value=false
      return false
    }
    if (formItems.value.fangxiang=='' || formItems.value.fangxiang==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '收支方向不能为空！'
      })
      saveClick.value=false
      return false
    }
    if (formItems.value.receiveDate=='' || formItems.value.receiveDate==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '收到日期不能为空！'
      })
      saveClick.value=false
      return false
    }
    if (formItems.value.chupiaoDate=='' || formItems.value.chupiaoDate==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '出票日期不能为空！'
      })
      saveClick.value=false
      return false
    }
    if (formItems.value.daoqiDate=='' || formItems.value.daoqiDate==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '到期日不能为空！'
      })
      saveClick.value=false
      return false
    }
    if (formItems.value.acceptDate=='' || formItems.value.acceptDate==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '承兑日期不能为空！'
      })
      saveClick.value=false
      return false
    }
    if (formItems.value.money=='' || formItems.value.money==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '金额不能为空！'
      })
      saveClick.value=false
      return false
    }
    isChanged = !(JSON.stringify(formItems.value) == JSON.stringify(changeBeforeModel))
    if (isChanged) {
      // emit('save', unref(formItems));
      // closeModal();
      await useRouteApi(saveAcceptBill, {schemaName: dynamicTenantId})(formItems.value)
      message.success('保存成功！')
      formItems.value = changeBeforeModel
      saveClick.value=false
      return true;
    }
    closeModal();
    // alert("没有改变过")
    saveClick.value=false
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

:deep(.ant-picker){
  border: none !important;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  input {
    font-size: 14px;
    font-weight: bold;
    border: none !important;
  }
}

:deep(.ant-input-number){
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;

  .ant-input-number-handler-wrap{
    display:none;
  }
  input{
    border: none;
    font-size: 14px;
    font-weight: bold;
    font-family: Arial !important;
  }
}

:deep(.abc){
  input{
    font-size: 19px;
    font-weight: bold;
  }
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
  .ant-input-affix-wrapper{
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  input {
    width: 30%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
  }

  .abc{
    border-bottom: 2px solid #666666 !important;
    font-size: 19px !important;
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
    margin-top: 10px;

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
  pointer-events: none;
  cursor: default;

  .ant-input-affix-wrapper{
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  input {
    width: 30%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
  }

  .abc{
    border-bottom: 2px solid #666666 !important;
    font-size: 19px !important;
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

:deep(.ant-tabs-card).ant-tabs-top > .ant-tabs-nav  {
  height: 40px;
  font-weight: bold;
  font-size: 13px;
  .ant-tabs-tab-active,.ant-tabs-card.ant-tabs-top > div > .ant-tabs-nav .ant-tabs-tab-active {
    border-top: 2px solid rgb(1, 143, 251) !important;
  }
}
</style>
