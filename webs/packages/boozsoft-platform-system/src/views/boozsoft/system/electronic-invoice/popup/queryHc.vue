<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="发票列表"
    @ok="handleOk()"
    @cancel="handleClose()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up">
        <div class="ocup-position"> 系统方案 </div>
        <a-tabs v-model:activeKey="activeKey" tabPosition="left" type="card" @change="tabChange">
          <a-tab-pane key="" tab="标准模式">
            <ul>
              <li>
                <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
              </li>
              <li>
                <span>发票代码：</span>
                <a-input
                  v-model:value="formItems.fapiaoCode"
                  style="width: 180px;text-align: center;"/>
                <span style="padding-left: 30px">发票号码：</span>
                <a-input
                  v-model:value="formItems.fapiaoNumber"
                  style="width: 180px;text-align: center;"/>
              </li>
              <li>
                <span>往来单位：</span>
                <a-input
                  v-model:value="formItems.buyerSupplier"
                  style="width: 180px;text-align: center;"/>
              </li>
              <li>
                <span>开票日期：</span>
                <a-range-picker v-model:value="formItems.fapiaoDate" class="range"/>
              </li>
              <li>
                <span>业务类型：</span>
                <a-select style="width: 180px" v-model:value="formItems.ywType">
                  <a-select-option value="0">
                    销项税发票
                  </a-select-option>
                  <a-select-option value="1">
                    进项税发票
                  </a-select-option>
                </a-select>
                <span  style="padding-left: 30px">发票类型：</span>
                <a-select style="width: 180px" v-model:value="formItems.fpType">
                  <a-select-option value="0">
                    全部
                  </a-select-option>
                  <a-select-option value="1">
                    电子普通发票
                  </a-select-option>
                  <a-select-option value="2">
                    电子专用发票
                  </a-select-option>
                  <a-select-option value="3">
                    普通发票
                  </a-select-option>
                  <a-select-option value="4">
                    专用发票
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <span>发票状态：</span>
                <a-select style="width: 180px" v-model:value="formItems.fpStatus">
                  <a-select-option value="3">
                    红冲
                  </a-select-option>
                </a-select>
                <span style="padding-left: 30px">认证状态：</span>
                <a-select  style="width: 180px" v-model:value="formItems.rzStatus">
                  <a-select-option value="0">
                    全部
                  </a-select-option>
                  <a-select-option value="1">
                    已认证
                  </a-select-option>
                  <a-select-option value="2">
                    未认证
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <span>认证日期：</span>
                <a-range-picker v-model:value="formItems.rzDate"  class="range"/>
              </li>
              <li>
                <span>价税合计：</span>
                <a-select style="width: 180px" v-model:value="formItems.fapiaoTotalAmount">
                  <a-select-option  v-for="d in bzList" :value="d.id">
                    {{ d.currChName }}
                  </a-select-option>
                </a-select>
                <span  style="padding-left: 30px">单据状态：</span>
                <a-select style="width: 180px" v-model:value="formItems.djStatus">
                  <a-select-option value="0">
                    全部
                  </a-select-option>
                  <a-select-option value="1">
                    已审核
                  </a-select-option>
                  <a-select-option value="2">
                    未审核
                  </a-select-option>
                </a-select>
              </li>
            </ul>
          </a-tab-pane>
          <a-tab-pane key="0" tab="集团模式">
            <ul>
              <li>
                <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
              </li>
              <li>
                <span>发票代码：</span>
                <a-input
                  v-model:value="formItems.fapiaoCode"
                  style="width: 180px;text-align: center;"/>
                <span style="padding-left: 30px">发票号码：</span>
                <a-input
                  v-model:value="formItems.fapiaoNumber"
                  style="width: 180px;text-align: center;"/>
              </li>
              <li>
                <span>往来单位：</span>
                <a-input
                  v-model:value="formItems.buyerSupplier"
                  style="width: 180px;text-align: center;"/>
              </li>
              <li>
                <span>开票日期：</span>
                <a-range-picker v-model:value="formItems.fapiaoDate" class="range"/>
              </li>
              <li>
                <span>业务类型：</span>
                <a-select style="width: 180px" v-model:value="formItems.ywType">
                  <a-select-option value="0">
                    销项税发票
                  </a-select-option>
                  <a-select-option value="1">
                    进项税发票
                  </a-select-option>
                </a-select>
                <span  style="padding-left: 30px">发票类型：</span>
                <a-select style="width: 180px" v-model:value="formItems.fpType">
                  <a-select-option value="0">
                    全部
                  </a-select-option>
                  <a-select-option value="1">
                    电子普通发票
                  </a-select-option>
                  <a-select-option value="2">
                    电子专用发票
                  </a-select-option>
                  <a-select-option value="3">
                    普通发票
                  </a-select-option>
                  <a-select-option value="4">
                    专用发票
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <span>发票状态：</span>
                <a-select style="width: 180px" v-model:value="formItems.fpStatus">
                  <a-select-option value="3">
                    红冲
                  </a-select-option>
                </a-select>
                <span style="padding-left: 30px">认证状态：</span>
                <a-select  style="width: 180px" v-model:value="formItems.rzStatus">
                  <a-select-option value="0">
                    全部
                  </a-select-option>
                  <a-select-option value="1">
                    已认证
                  </a-select-option>
                  <a-select-option value="2">
                    未认证
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <span>认证日期：</span>
                <a-range-picker v-model:value="formItems.rzDate"  class="range"/>
              </li>
              <li>
                <span>价税合计：</span>
                <a-select style="width: 180px" v-model:value="formItems.fapiaoTotalAmount">
                  <a-select-option  v-for="d in bzList" :value="d.id">
                    {{ d.currChName }}
                  </a-select-option>
                </a-select>
                <span  style="padding-left: 30px">单据状态：</span>
                <a-select style="width: 180px" v-model:value="formItems.djStatus">
                  <a-select-option value="0">
                    全部
                  </a-select-option>
                  <a-select-option value="1">
                    已审核
                  </a-select-option>
                  <a-select-option value="2">
                    未审核
                  </a-select-option>
                </a-select>
              </li>
            </ul>
          </a-tab-pane>
        </a-tabs>
      </div>
    </div>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
  import {ref, unref, onMounted, reactive} from 'vue'
  import {BasicModal, useModalInner} from '/@/components/Modal'
  import {PageWrapper} from '/@/components/Page'
  import {
    DatePicker as ADatePicker,
    Select as ASelect,
    Input as AInput,
    Checkbox as ACheckbox,
    Popover as APopover,
    Switch as ASwitch,
    Radio as ARadio,
    Upload as AUpload,
    Table as ATable,
    Tabs as ATabs, message
  } from "ant-design-vue"
  import CheckboxGroup from "ant-design-vue/lib/checkbox/Group";
  import { useTabs } from '/@/hooks/web/useTabs';
  import router from "/@/router";
  import { Moment } from 'moment';
  import {
    findPeriodByAccontId,
    findBzAll,
    findMaxJc,
    findCodeKmByPeriod
  } from '/@/api/record/generalLedger/data';
  import {findByImgid} from "/@/api/record/eletronicInvoice_data/eletronic_invoice";
  import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
  import {finByParameterAccuracy} from "/@/api/record/km_mingxi/data";
  import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
  import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
  import {filterAccListByAuth, getAdInfoDatas, getAllAccAuths} from "/@/api/record/system/financial-settings";
  import {company_findAllStyleByUnique, findByAccStyle, findByStandardUnique} from "/@/api/accstandard/accstandard";
  import {useUserStore} from "/@/store/modules/user";
  import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
  import {groupStandardAndTemplate} from "/@/api/codekemu/codekemu";
  import {findByAccId} from "/@/api/record/system/account";
  import {findByQueryPlan, saveQueryPlan} from "/@/api/query-plan/queryPlan";
  import {useNcLogger} from "/@/utils/boozsoft/record/recordUtils";

  const userStore = useUserStore();
  const { closeCurrent } = useTabs(router);
  const ARangePicker = ADatePicker.RangePicker
  const ASelectOption = ASelect.Option
  const AInputSearch = AInput.Search
  const ARadioGroup = ARadio.Group
  const ARadioButton = ARadio.Button
  const ACheckboxGroup = ACheckbox.Group
  const ATabPane = ATabs.TabPane
  const emit=defineEmits([])
  const data = []

  const formItems: any = ref({
    ywType: '1',
    fpType: '1',
    rzStatus: '0',
    fpStatus: '3'
  })
  let changeBeforeModel: any = {}

  const year = new Date().getFullYear();



  const showStyle = ref<string>('J')
  const fontSize = ref<string>('MAX')

  let styleValue: any = ref('全部');
  let styleList: any = ref([]);
  const queryPlan: any = ref([]);
  const activeKey: any = ref('');
  // 页面变量
  const pageParameter = reactive({
    companyCode: '',
    companyName: '',
  })
  //查询条件
  const seach : any = ref({})

  const [register, {closeModal, setModalProps}] = useModalInner((data) => {
    // 方式2
    formItems.value.openOne = data.data.openOne
    changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
    setModalProps({minHeight: 400});
  })
  let isChanged: boolean = false

  //获取当前年月
  const nowDate = ()=>{
    const nowDate = new Date();
    const date = {
      year: nowDate.getFullYear(),
      month: nowDate.getMonth() + 1,
      date: nowDate.getDate(),
    }
    const newmonth = date.month>10?date.month:date.month
    return date.year + '-' + newmonth
  }
  const recordName = 'general-ledger'
  const {ncLogger} = useNcLogger({recordName})
  async function handleOk() {
    emit('save', unref(formItems))
    closeModal()
    return true
  }
  async function handleClose() {
    if (null != formItems.value.openOne && formItems.value.openOne == 1){
      await closeCurrent()
      router.push('/home/welcome')
    }
  }
  // 数据库模式名称
  const databases = ref(getCurrentAccountName(false))

  const filterOption = (input: string, option: any) => {
    return option.value.toLowerCase().indexOf(input.toLowerCase()) >= 0;
  };

  const whetherGroup = ref(false)
  // 数据库模式名称
  const database = ref(useCompanyOperateStoreWidthOut().getSchemaName);

  function tabChange(val) {
    if(val===''){
      styleValue.value='全部'
    }else if(val==='1'){

    }
  }

  const databaseTrue = ref(getCurrentAccountName(true));
  async function dynamicAdReload(data) {
    database.value=data.accId
    databaseTrue.value =data.accId+'-'+data.year

  }

  const getAdObjInfoByCoCode = (value, type,accList) => {
    let list = accList.filter(item => item[type] == value)
    return list.length > 0 ? list[0] : null
  }


</script>
<style lang="less" scoped>
  .nc-open-content {
    position: relative;

    input {
      width: 35%;
      border: none !important;
      border-bottom: 1px solid #bdb9b9 !important;
    }

    .range {
      border: none !important;
      border-bottom: 1px solid #bdb9b9 !important;
    }

    .open-content-title {
      > div {
        display: inline-block;
      }

      > div:nth-of-type(1) {
        width: 200px;
        background-color: #efeeee;
        color: black;
        font-size: 20px;
        text-align: center;
        padding: 5px 10px
      }
    }

    .open-content-up {
      position: relative;

      .ocup-position {
        position: absolute;
        left: 0;
        width: 180px;
        background-color: #0096c7;
        color: white;
        font-size: 16px;
        text-align: center;
        padding: 5px 10px;
      }
      .ocup-position:nth-of-type(1) {
        top: 0px;
      }
      .ocup-position:nth-of-type(2) {
        top: 190px;
      }

      ul {
        padding: 10px 30px;

        li {
          margin: 10px 0;

          span {
            font-size: 14px;
            color: #747272;
          }

          > span:nth-of-type(1), .right_span {
            display: inline-block;
            width: 80px;
          }

          .ant-select {
            font-size: 14px;
          }
        }
      }
    }

    .open-content-foot {
      display: block;
      position: fixed;
      margin-top: 43px;
    }

    .ant-tabs-tabpane-active {
      overflow-y: auto;
      height: 400px;
    }

    .ant-select-selection-search-input {
      border-bottom: none !important;
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

    .ant-radio-group {
      .ant-radio-wrapper {
        width: 70px;
        .ant-radio-input{
          border-color: slategrey;
        }
      }
      p:nth-of-type(2){
        margin-bottom: 0;
      }
    }
  }

  :deep(.ant-tabs-left-bar) {
    margin-right: 0px !important;
  }

  :deep(.ant-tabs-left) {
    .ant-tabs-tab {
      width: 180px;
      font-weight: bold;
      text-align: center;
      margin-bottom: 0px !important;
    }

    .ant-tabs-tab-active {
      background-color: #65cbec !important;
      color: rgba(0, 0, 0, 0.85) !important;
    }
    .ant-tabs-tab:nth-of-type(1) {
      margin-top: 35px;
    }
    .ant-tabs-tab:nth-of-type(3) {
      margin-top: 110px;
    }
  }

</style>
