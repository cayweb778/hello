<template>
  <BasicModal
    width="800px"
    v-bind="$attrs"
    title="单位日记账期初"
    @ok="handleOk()"
    @register="register"
    :canFullscreen="false"
  >
<!--    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <img src="/@/assets/images/create.svg" style="width:25px;margin-right: 10px;"/>
        <span style="line-height: 25px;font-size: 16px;">期初对账凭证</span>
      </div>
    </template>-->
    <template #title>
      <div style="display: flex;margin-left: 10px;margin-top: 10px;" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='0'">
          <PlusCircleOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;期初对账凭证
        </span>
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='1'">
          <FormOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;期初对账凭证
        </span>
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='2'">
          <FileSearchOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;期初对账凭证
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 60px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="/@/assets/images/bank.png" style="height:100px;margin-right: 10px;"/>
      </div>
    </template>
    <div class="nc-open-content">
      <div class="open-content-up">
<!--        <label>借方金额：</label>
        <a-input type="number" v-model:value="formItems.md" placeholder="借方金额" />
        <label>贷方金额：</label>
        <a-input type="number" v-model:value="formItems.mc" placeholder="贷方金额" />
        <br />-->
        <div style="width:70%;margin-left: 10%;margin-top: 60px;">
          <a-radio-group v-model:value="formItems.fangxiang" style="margin-left: -20px;">
            <a-radio value="1" style="width:100px;">
              <span style="color: #000000;font-weight: bold;font-size: 12px;">借方</span>
            </a-radio>
            <a-radio value="0" style="width:100px;margin-left: -10px;">
              <span style="color: #000000;font-weight: bold;font-size: 12px;">贷方</span>
            </a-radio>
          </a-radio-group>
          <br/>

          <label style="font-size: 18px;margin-left: 0;width:100px;">金额：</label>
          <a-input-number
            v-model:value="formItems.money"
            placeholder=""
            class="abc"
            style="width: 60%;border-bottom: 2px solid #000000;"
          />
          <span class="red_span">{{isState=='2'?'':'*'}}</span>
        </div>
        <br/><br/>

        <label>凭证日期：</label>
        <a-date-picker
          v-model:value="formItems.dbillDate"
          placeholder="制单日期"
          format="YYYY-MM-DD"
          style="width: 30%"
        />
        <span class="red_span">*</span>
        <label>凭证类型：</label>
        <a-select v-model:value="formItems.csign" placeholder="凭证类型" style="width: 30%">
          <a-select-option v-for="item in voucherTypeList" :key="item.voucherTypeCode" :value="item.voucherTypeCode" >
            [{{ item.voucherTypeCode }}]-{{ item.voucherTypeName }}
          </a-select-option>
        </a-select>
        <span class="red_span">*</span>
        <br />

        <label>对方单位：</label>
        <a-input v-model:value="formItems.pjUnitName" />
        <span class="red_span"></span>
        <label>结算方式：</label>
        <a-select v-model:value="formItems.pjCsettle" placeholder="结算方式" style="width: 30%">
          <a-select-option v-for="settModes in settModesList" :key="settModes.settModesName" :value="settModes.settModesCode" >
            {{ settModes.settModesCode }}-{{ settModes.settModesName }}
          </a-select-option>
        </a-select>
        <span class="red_span"></span>
        <br/>

        <label>摘要：</label>
        <a-input v-model:value="formItems.cdigest" placeholder="摘要" />
        <span class="red_span">*</span>
        <label>凭证编号：</label>
        <a-input v-model:value="formItems.inoId" placeholder="凭证编号" />
        <span class="red_span">*</span>
        <br />

        <label>票据日期：</label>
        <a-date-picker
          v-model:value="formItems.pjDate"
          placeholder="票据日期"
          format="YYYY-MM-DD"
          style="width: 30%"
        />
        <span class="red_span"></span>
        <label>票据编号：</label>
        <a-input v-model:value="formItems.pjId" placeholder="票据编号" />
        <span class="red_span"></span>
        <br /><br />

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
    message
  } from 'ant-design-vue';
  import {
    PlusCircleOutlined,
    FormOutlined,
    FileSearchOutlined,
    CaretDownOutlined
  } from '@ant-design/icons-vue'
  import {findSettModesByFlag} from "/@/api/record/system/sett-modes";
  import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
  import {currentCyDatas} from "/@/api/record/system/financial-settings";
  import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
  import {useMessage} from "/@/hooks/web/useMessage";
  import moment from "moment";
  import {findVoucherTypeAll} from "/@/api/record/system/voucher-type";
  const AInputSearch = AInput.Search;
  const ASelectOption = ASelect.Option;
  const ARadioGroup = ARadio.Group

  const emit=defineEmits(['register','save']);

  const {
    createErrorModal
  } = useMessage()

  const formItems: any = ref({});
  const isState = ref('0')
  const saveClick:any = ref(false)

  const currencyList: any = ref([]);

  let changeBeforeModel: any = {};

  const settModesList:any = ref([])
  const voucherTypeList:any = ref([])
  const defaultAdName = ref()
  const dynamicTenantId = ref()
  const qiyongDate = ref('')
  const [register, { closeModal }] = useModalInner((data) => {
    saveClick.value=false
    defaultAdName.value = data.defaultAdName
    dynamicTenantId.value = data.dynamicTenantId
    qiyongDate.value = data.qiyongDate
    isState.value = data.isState
    //币种信息
    currencyList.value = [];
    useCompanyOperateStoreWidthOut().getAccountList.forEach(item => {
      if (item.uniqueCode==data.defaultAdName && item.currency!='' && item.currency!=null){
        currencyList.value=[{
          accountId: defaultAdName.value,
          foreignCode: item.currency,
          foreignName: item.currencyCh
        }]
      }
    })
    useRouteApi(currentCyDatas,{schemaName: dynamicTenantId})({accId: defaultAdName.value}).then(res=>{
      currencyList.value.push(...res.items)
      // bzList.value = res.items/*.filter(item=>item.foreignName.indexOf('人民币') == -1)*/
    });
    settModesList.value = []
    useRouteApi(findSettModesByFlag,{schemaName: dynamicTenantId})({}).then(res=>{
      settModesList.value = res.items
    })

    useRouteApi(findVoucherTypeAll, {schemaName: dynamicTenantId})({}).then(res => {
      voucherTypeList.value = res.items
    })

    // 方式2
    formItems.value.id = data.data.id
    formItems.value.dbillDate = null
    if (data.data.dbillDate!=null || data.data.dbillDate!=null) {
      formItems.value.dbillDate = moment(data.data.dbillDate)
    }
    formItems.value.csign = data.data.csign
    formItems.value.inoId = data.data.inoId
    formItems.value.cdigest = data.data.cdigest
    formItems.value.ccode = data.data.ccode
    formItems.value.ccodeName = data.data.ccodeName
    formItems.value.md = data.data.md
    formItems.value.mc = data.data.mc
    if (data.data.jie==null || data.data.jie==''){
      formItems.value.money = data.data.mc
      formItems.value.fangxiang = '0'
    } else {
      formItems.value.money = data.data.md
      formItems.value.fangxiang = '1'
    }
    formItems.value.pjCsettle = data.data.pjCsettle
    formItems.value.pjId = data.data.pjId
    formItems.value.pjDate = data.data.pjDate
    formItems.value.pjUnitName = data.data.pjUnitName
    formItems.value.iyear = data.data.iyear
    formItems.value.iperiod = data.data.iperiod
    formItems.value.ifrag = data.data.ifrag
    formItems.value.iyperiod = data.data.iyperiod

    changeBeforeModel = JSON.parse(JSON.stringify(formItems.value));
  });
  let isChanged = false;
  //必输项（制单日期，科目编码，借贷方金额）
  //导入时判断制单日期必须小于启用日期，借贷方金额，
  async function handleOk() {
    saveClick.value=true
    if (formItems.value.fangxiang=='1'){
      formItems.value.md = formItems.value.money
      formItems.value.mc = ''
    } else {
      formItems.value.mc = formItems.value.money
      formItems.value.md = ''
    }
    if (formItems.value.dbillDate=='' || formItems.value.dbillDate==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '凭证日期不能为空！'
      })
      saveClick.value=false
      return false
    }else{
      //较验日期
      let time = FormatDate(formItems.value.dbillDate.format('YYYY-MM-DD'))
      let today = time;
      let today_time =  FormatDate(today);
      if(formItems.value.dbillDate.format('YYYY-MM-DD')>qiyongDate.value){
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '凭证日期已超过期初日期！'
        })
        saveClick.value=false
        return false
      }
    }
    //较验借贷方
    let jie = formItems.value.md
    let dai = formItems.value.mc
    if ((!isNaN(jie)||jie==null) && (!isNaN(dai)||dai==null)){
      if (jie=='' || jie==null || parseFloat(jie)==0){
        if (dai=='' || dai==null || parseFloat(dai)==0) {
          createErrorModal({
            iconType: 'warning',
            title: '提示',
            content: '借贷方金额不能都为空值！'
          })
          saveClick.value=false
          return false
        }
      } else {
        if (dai!='' && dai!=null && parseFloat(dai)!=0){
          createErrorModal({
            iconType: 'warning',
            title: '提示',
            content: '借贷方金额不能都有值！'
          })
          saveClick.value=false
          return false
        }
      }
    } else {
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '借贷方金额有误，请核查后重新导入！'
      })
      saveClick.value=false
      return false
    }
    if (formItems.value.csign=='' || formItems.value.csign==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '凭证类型不能为空！'
      })
      saveClick.value=false
      return false
    }
    if (formItems.value.cdigest=='' || formItems.value.cdigest==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '摘要不能为空！'
      })
      saveClick.value=false
      return false
    }
    if (formItems.value.inoId=='' || formItems.value.inoId==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '凭证编号不能为空！'
      })
      saveClick.value=false
      return false
    }
    isChanged = !(JSON.stringify(formItems.value) == JSON.stringify(changeBeforeModel));
    if (isChanged) {
      emit('save', unref(formItems));
      closeModal();
      saveClick.value=false
      return true;
    }
    closeModal();
    saveClick.value=false
    // alert("没有改变过")
    return false;
  }

  async function handleOkAdd() {
    saveClick.value=true
    if (formItems.value.fangxiang=='1'){
      formItems.value.md = formItems.value.money
      formItems.value.mc = ''
    } else {
      formItems.value.mc = formItems.value.money
      formItems.value.md = ''
    }
    if (formItems.value.dbillDate=='' || formItems.value.dbillDate==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '凭证日期不能为空！'
      })
      saveClick.value=false
      return false
    }else{
      //较验日期
      let time = FormatDate(formItems.value.dbillDate.format('YYYY-MM-DD'))
      let today = time;
      let today_time =  FormatDate(today);
      if(formItems.value.dbillDate.format('YYYY-MM-DD')>qiyongDate.value){
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '凭证日期已超过期初日期！'
        })
        saveClick.value=false
        return false
      }
    }
    //较验借贷方
    let jie = formItems.value.md
    let dai = formItems.value.mc
    if ((!isNaN(jie)||jie==null) && (!isNaN(dai)||dai==null)){
      if (jie=='' || jie==null || parseFloat(jie)==0){
        if (dai=='' || dai==null || parseFloat(dai)==0) {
          createErrorModal({
            iconType: 'warning',
            title: '提示',
            content: '借贷方金额不能都为空值！'
          })
          saveClick.value=false
          return false
        }
      } else {
        if (dai!='' && dai!=null && parseFloat(dai)!=0){
          createErrorModal({
            iconType: 'warning',
            title: '提示',
            content: '借贷方金额不能都有值！'
          })
          saveClick.value=false
          return false
        }
      }
    } else {
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '借贷方金额有误，请核查后重新导入！'
      })
      saveClick.value=false
      return false
    }
    if (formItems.value.csign=='' || formItems.value.csign==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '凭证类型不能为空！'
      })
      saveClick.value=false
      return false
    }
    if (formItems.value.cdigest=='' || formItems.value.cdigest==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '摘要不能为空！'
      })
      saveClick.value=false
      return false
    }
    if (formItems.value.inoId=='' || formItems.value.inoId==null){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '凭证编号不能为空！'
      })
      saveClick.value=false
      return false
    }
    isChanged = !(JSON.stringify(formItems.value) == JSON.stringify(changeBeforeModel));
    if (isChanged) {
      emit('save', unref(formItems));
      // closeModal();
      message.success('保存成功！')
      formItems.value = changeBeforeModel
      saveClick.value=false
      return true;
    }
    closeModal();
    saveClick.value=false
    // alert("没有改变过")
    return false;
  }

  function FormatDate(strTime) {
    let date = new Date(strTime);
    return date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
  }
</script>
<style>
.vben-basic-title {
  color: #0096c7 !important;
  border:none !important;
}

.ant-modal-body {
  padding: 0px;
  border-bottom: 1px solid rgb(1, 129, 226) !important;
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
    font-size: 14px;
    font-weight: bold;
    font-family: Arial !important;
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
  input {
    width: 30%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
  }

  .abc{
    border-bottom: 2px solid #666666 !important;
    font-size: 19px;
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
    width: 90px;
    display: inline-block;
    padding: 5px 10px;
    margin-left: 2em;
    color: #535353;
    font-weight: bold;
    font-size: 13px;
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
