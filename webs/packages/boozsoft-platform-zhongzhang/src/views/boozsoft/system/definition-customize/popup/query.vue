<template>
  <BasicModal
    destroyOnClose
    width="700px"
    class="spaceLogo"
    v-bind="$attrs"
    title="自定义结转"
    @ok="handleOk()"
    @cancel="handleClose()"
    :loading="modelLoadIng"
    :canFullscreen="false"
    @register="register"
  >
    <ul>
      <li>
        <AccountPicker theme="one" @reloadTable="codeChange" readonly=""/>
      </li>
      <li>
        <span>结转期间：</span>
        <a-select
          v-model:value="modelList['1'].variable.period"
          show-search
          style="width: 150px;"
        >
          <a-select-option
            v-for="item in dateList"
            :key="item.id"
            :value="item.value"
          >{{ item.value }}
          </a-select-option>
        </a-select>
        &emsp;&emsp;<span class="right_span">凭证类型：</span>
        <a-select
          v-model:value="modelList['1'].variable.voucherType"
          placeholder=""
          style="width: 120px"
          :allowClear="true">
          <a-select-option v-for="(item,index) in typesList" :key="index"
                           :value="item.voucherTypeCode">{{ item.voucherTypeCode }}
          </a-select-option>
        </a-select>
      </li>
      <li>
        <a-checkbox v-model:checked="ishaveRjz" style="width: 130px"> 包含未记账 </a-checkbox>
      </li>
    </ul>

    <SetAccvoucher @save="saveData" @register="registerSetAccvoucherPage"/>
  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {ref, unref, reactive, onMounted, watch, computed} from 'vue'
import {BasicModal, useModal, useModalInner} from '/@/components/Modal'
import {
  Checkbox as ACheckbox,
  Input as AInput,
  Select as ASelect,
  Tabs as ATabs, message
} from "ant-design-vue"
import {useTabs} from '/@/hooks/web/useTabs';
import router from "/@/router";
import {
  findPeriodByAccontId, findMaxPingZhengQiJian, findBzAll
} from '/@/api/record/generalLedger/data';
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {
  currentAccountTypes,
} from "/@/api/record/system/financial-settings";
import {useUserStore} from "/@/store/modules/user";
import {getCurrentAccountName, hasBlank, pointMessage} from "/@/api/task-api/tast-bus-api";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useMessage} from "/@/hooks/web/useMessage";
import {findParameterValue, modifyParameterValue} from "/@/api/task-api/query-bus-api";
import {ObjTool, StrTool} from "/@/api/task-api/tools/universal-tools";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {companyfindAll} from "/@/api/codekemu/codekemu";
import {findByAccIdAccStyle} from "/@/api/accstandard/accstandard";
import SetAccvoucher from './setAccvoucher.vue'
import {findByCcode} from "/@/api/record/system/losses-gain";

const {closeCurrent} = useTabs(router);
const emit = defineEmits(['register', 'save'])
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ATabPane = ATabs.TabPane

const userStore = useUserStore();

const {createWarningModal} = useMessage();
const [registerSetAccvoucherPage, {openModal: openSetAccvoucherPage}] = useModal()
const bz: any = ref('全部')
const bzList: any = ref([])
const ishaveRjz: any = ref(true)
const formItems: any = ref({})
let changeBeforeModel: any = {}
// 会计区间
const dateList: any = ref([])
const styleList: any = ref([])
// 会计科目
const kmList: any = ref([])
// 类别
const typesList: any = ref([])
const userId = userStore.getUserInfo?.id
const currentDynamicTenant = ref('')

const defaultTabsKey = ref('1')
const whetherGroup = ref(false)
const modelLoadIng = ref(false)
const accId = ref(getCurrentAccountName(false))
const dynamicTenantId = ref(getCurrentAccountName(true))
const [register, {closeModal, setModalProps}] = useModalInner((data) => {
  // 方式2
  formItems.value = data.data
  dynamicTenantId.value = data.dynamicTenantId
  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
  setModalProps({minHeight: 300});
})

let count = 0
const codeChange = async (value) => {
  accId.value=value.accId
  ++count
  if (count == 2) return false;
  let newName = value.accId
  // 获取已存在查询数据
  // let dbQueryData = await findParameterValue(dbQueryJson)
  // if (null != dbQueryData) { // 存在
  //   defaultTabsKey.value = '3'
  //   dbQueryJson.id = dbQueryData.id
  //   dbQueryJson.queryConditions = dbQueryData.queryConditions
  //   if (!hasBlank(dbQueryData.queryConditions)) {
  //     let queryMap = JSON.parse(dbQueryData.queryConditions)
  //     modelList["3"].constant = queryMap.constant
  //     modelList["3"].variable = queryMap.variable
  //   }
  // } else {
    // 清除条件
    modelList[defaultTabsKey.value].constant = defaultModel.constant
    modelList[defaultTabsKey.value].variable = defaultModel.variable
  // }
  modelList[defaultTabsKey.value].constant.tenantId = value.accountMode
  currentDynamicTenant.value = value.accountMode
  dynamicTenantId.value=value.accountMode
  reLifeQueryParameter(currentDynamicTenant.value)

  //币种
  let currency = await findBzAll();
  bzList.value = currency.filter(c => c.currChName.indexOf('人民币') < 0);
  bzList.value.push({id: '', currChName: '全部'});
}

const reLifeQueryParameter = async (dynamicTenant) => {
  if (hasBlank(dynamicTenant)) return
  // 获取最大凭证区间
  let tenantName = dynamicTenant.split('-')[0] + '-' + dynamicTenant.split('-')[1]
  //加载期间数据
  dateList.value = []
  dateList.value = await findPeriodByAccontId(tenantName);
  if (dateList.value.length > 0) {
    const qijian = await useRouteApi(findMaxPingZhengQiJian, {schemaName: dynamicTenant})({}) || '';
    // 初始化默认区间
    if (hasBlank(qijian)) {
      modelList[defaultTabsKey.value].variable.period = dateList.value[0].value
    } else {
      modelList[defaultTabsKey.value].variable.period = qijian.substring(0, 4) + '-' + qijian.substring(4, 6)
    }
    // await reLifeKmList(currentDynamicTenant.value)
    await reLifeTypesList(currentDynamicTenant.value)
  } else {
    await pointMessage({title: '温馨提示', content: '请先初始化该公司代码账套会计期间后在进行查询！', delay: true})
    currentDynamicTenant.value = ''
  }
}

async function reLifeKmList(dynamicTenant) {
  if (hasBlank(dynamicTenant)) return
  // 获取所有权益科目
  let map = {
    cclass:'权益',
    databasenum: dynamicTenant,
    size: 1000,
    page: 1,
    searchConditon: {
      requirement: '',value: ''
    }
  }

  let ccode=(await useRouteApi(companyfindAll,{schemaName: dynamicTenant})(map)).items.filter(v=>v.bend==='1')
  kmList.value = ccode
}

async function reLifeTypesList(dynamicTenant) {
  if (hasBlank(dynamicTenant)) return
  typesList.value = []
  await useRouteApi(currentAccountTypes, {schemaName: dynamicTenant})({
    pageSize: 20,
    pageNumber: 1
  }).then(res => {
    typesList.value = res.items
  })
}

const defaultModel = {
  constant: {
    tenantId: '',
    thisAdInfo: {}
  },
  variable: {
    period: '',
    voucherType: '',
    carryOverlevel: '0',
    incomeAndExpenditure: '',
    yearProfitCode:'',
  }
}
const modelList = reactive({
  '1': {
    constant: {
      tenantId: '',
      thisAdInfo: {}
    },
    variable: {
      period: '',
      voucherType: '',
      carryOverlevel: '0',
      incomeAndExpenditure: '',
      yearProfitCode:'',
    }
  },
  '2': {
  },
  '3': {
    constant: {
      tenantId: '',
      thisAdInfo: {}
    },
    variable: {
      period: '',
      voucherType: '',
      carryOverlevel: '0',
      incomeAndExpenditure: '',
      yearProfitCode:'',
    }
  },
  '4': {}
})


/*辅助核算过滤框*/
async function handleOk() {
  if (hasBlank(modelList[defaultTabsKey.value].variable.period)){
    createWarningModal({title: '温馨提示',content: '结转期间为必选项'})
  }
  await findByStyle()
  modelList['1'].styleList = styleList.value
  modelList['1'].ishaveRjz = ishaveRjz.value

  openSetAccvoucherPage(true, {
    data: formItems.value,
    dynamicTenantId:dynamicTenantId.value,
    modelList:modelList['1'],
  })
}
// 监听页面反馈
const totalChange = computed(() => formItems.value.total)
watch(totalChange, async (a) => {
    if (a != 0) {
      modelLoadIng.value = false
      /*if (a > 0) {*/
      closeModal()
      /* } else {
         createWarningModal({title: '温馨提示', content: '暂无任何查询结果！'});
       }*/
      formItems.value.total = 0
    }
  }
)
async function handleClose() {
  if (null != formItems.value.openOne && formItems.value.openOne == 1) {
    await closeCurrent()
    router.push('/zhongZhang/home/welcome');
  }
  return true;
}

 async function findByStyle() {
   styleList.value= await useRouteApi(findByAccIdAccStyle, {schemaName: dynamicTenantId.value})(accId.value)
}

const filterOption = (input: string, option: any) => {
  return option.key.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

function saveData() {

}
</script>
<style src="../../../../../assets/styles/global-open.less" lang="less"></style>
<style lang="less" scoped>
:deep(.ant-select-selector) {
  border: none !important;
  border-bottom: 1px solid #bdb9b9 !important;
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
          width: 120px;
        }

        .ant-select {
          font-size: 14px;
        }
      }
    }
//.nc-open-content {
//  position: relative;
//
//  .open-content-title {
//    > div {
//      display: inline-block;
//    }
//
//    > div:nth-of-type(1) {
//      width: 200px;
//      background-color: #efeeee;
//      color: black;
//      font-size: 20px;
//      text-align: center;
//      padding: 5px 10px
//    }
//    /*> div:nth-of-type(2) {
//      width: calc(100% - 200px);
//      border-bottom: 1px #e6e3e3 solid;
//      font-size: 18px;
//      padding: 5px;
//
//      > span {
//        border-bottom: 4px #0960bd solid;
//        padding: 6px 20px;
//        color: #0960bd;
//      }
//    }*/
//  }
//
//  .open-content-up {
//    position: relative;
//
//    .ocup-position {
//      position: absolute;
//      left: 0;
//      width: 170px;
//      background-color: #0096c7;
//      color: white;
//      font-size: 16px;
//      text-align: center;
//      padding: 5px 10px;
//    }
//    .ocup-position:nth-of-type(1) {
//      top: 0px;
//    }
//    .ocup-position:nth-of-type(2) {
//      top: 190px;
//    }
//
//    ul {
//      padding: 10px 30px;
//
//      li {
//        margin: 10px 0;
//
//        span {
//          font-size: 14px;
//          color: #747272;
//        }
//
//        > span:nth-of-type(1), .right_span {
//          display: inline-block;
//          width: 120px;
//        }
//
//        .ant-select {
//          font-size: 14px;
//        }
//      }
//    }
//  }
//
//  .open-content-foot {
//    display: block;
//    position: fixed;
//    margin-top: 43px;
//  }
//
//  .ant-tabs-tabpane-active {
//    overflow-y: auto;
//    height: 300px;
//  }
//
//  .ant-select-selection-search-input {
//    border-bottom: none !important;
//  }
//
//  .ant-input:focus {
//    box-shadow: none;
//  }
//
//  :deep(.ant-select-selector) {
//    border: none !important;
//    border-bottom: 1px solid #bdb9b9 !important;
//  }
//
//  label {
//    text-align: right;
//    width: 110px;
//    display: inline-block;
//    padding: 5px 10px;
//  }
//}
//
//:deep(.ant-tabs-left-bar) {
//  margin-right: 0px !important;
//}
//
//:deep(.ant-tabs-left) {
//  .ant-tabs-tab {
//    width: 170px;
//    font-weight: bold;
//  }
//  .ant-tabs-tab-active {
//    background-color: #65cbec !important;
//    color: rgba(0, 0, 0, 0.85) !important;
//  }
//
//  .ant-tabs-tabpane-active {
//    padding-left: 0 !important;
//  }
//
//  .ant-tabs-tab:nth-of-type(1) {
//    margin-top: 35px;
//  }
//  .ant-tabs-tab:nth-of-type(2) {
//    margin-top: 150px !important;
//  }
//}
</style>
