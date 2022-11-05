<template>
  <BasicModal
    title="银行对账勾对"
    width="700px"
    class="spaceLogo"
    v-bind="$attrs"
    @ok="handleOk()"
    @cancel="handleClose()"
    @register="register"
    :canFullscreen="false"
    :footer="null"
    loadingTip="查询参数初始化中"
  >
    <template #title>
      <div style="height: 30px;width: 100%;background-color: #5f375c;color: white;line-height: 30px;">
        <AppstoreOutlined  style="margin: 0 2px;font-size: 14px;"/>
        <span style="font-size: 14px">现金银行</span>
      </div>
    </template>
    <div style="display: inline-flex;justify-content: space-between;width: 100%;">
      <div style="width: calc(100% - 150px);height: 100%;">
        <div style="text-align: center;padding: 10px 0 5px;">
          <SearchOutlined style="font-size: 24px;color: #0096c7;margin-top: 2px"/>
          <span style="line-height:30px;font-size: 24px;color: #0096c7;font-weight: bold;">&ensp;银行对账勾对</span>
        </div>
        <div class="nc-open-content">
          <div class="open-content-up">
            <div class="border-div">
              <span>业务范围</span>
              <AccountPicker  theme="three" @reloadTable="dynamicAdReload" style="text-align: center;"/>
            </div>
            <div class="border-div">
              <span>查询条件</span>
              <div>
                <span class="red_span"></span>
                <label>银行科目：</label>
                <a-select
                  v-model:value="kemu"
                  show-search
                  style="width: 300px;">
                  <template #suffixIcon><CaretDownOutlined style="color:#666666;"/></template>
                  <a-select-option v-for="d in kmList" :value="d.ccode">
                    {{ d.value }}
                  </a-select-option>
                </a-select>
              </div>
              <div>
                <span class="red_span"></span>
                <label>日期：</label>
                <a-date-picker
                  v-model:value="formItems.ddate1"
                  placeholder=""
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  :disabled-date="disabledStartDate"
                  style="width: 140px;"
                /> ~
                <a-date-picker
                  v-model:value="formItems.ddate2"
                  placeholder=""
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  :disabled-date="disabledEndDate"
                  style="width: 140px;"
                />
              </div>
              <div>
                <span class="red_span"></span>
                <label>对方单位：</label>
                <a-input v-model:value="formItems.duifangUnit" style="width: 300px;"/>
              </div>
              <div>
                <span class="red_span"></span>
                <label>金额：</label>
                <a-input-number v-model:value="formItems.amountMin" placeholder="最小金额" style="width: 140px"/>
                <span> ~ </span>
                <a-input-number v-model:value="formItems.amountMax" placeholder="最大金额" style="width: 140px"/>
              </div>
              <div>
                <span class="red_span"></span>
                <label>对账币种：</label>
                <a-select
                  v-model:value="bz"
                  show-search
                  style="width: 300px;">
                  <template #suffixIcon><CaretDownOutlined style="color:#666666;"/></template>
                  <a-select-option v-for="d in bzList" :value="d.foreignCode">
                    {{ d.foreignName }}
                  </a-select-option>
                </a-select>
              </div>
              <div>
                <span class="red_span"></span>
                <label>对账状态：</label>
                <a-select v-model:value="flag" style="width: 300px;">
                  <template #suffixIcon><CaretDownOutlined style="color:#666666;"/></template>
                  <a-select-option value=""> 全部</a-select-option>
                  <a-select-option value="1"> 已对帐</a-select-option>
                  <a-select-option value="0"> 未对帐</a-select-option>
                </a-select>
              </div>
              <div>
                <span class="red_span"></span>
                <label>未达天数：</label>
                <a-input-number v-model:value="formItems.weidaDay" style="width: 300px;"/>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="right-btns">
        <Button style="width: 100px;" shape="round" @click="handleOk" type="primary">查询</Button>
        <Button style="width: 100px;margin-top: 10px" shape="round" @click="handleClose">取消</Button>
      </div>
    </div>
  </BasicModal>
</template>

<script lang="ts" setup="props, { content }">
import {ref, unref, onMounted, reactive} from 'vue';
import {BasicModal, useModalInner} from '/@/components/Modal';
import {PageWrapper} from '/@/components/Page';
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
  Tabs as ATabs,
  message,
  Button,
  InputNumber as AInputNumber
} from 'ant-design-vue';
import {AppstoreOutlined, SearchOutlined,CaretDownOutlined,LinkOutlined} from '@ant-design/icons-vue';
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getCurrentAccountName, hasBlank} from "/@/api/task-api/tast-bus-api";
import {useUserStore} from "/@/store/modules/user";
import {currentCyDatas} from "/@/api/record/system/financial-settings";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {findCodeKemuByBr} from "/@/api/record/system/bank-statement";

const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const ARadioGroup = ARadio.Group;
const ARadioButton = ARadio.Button;
const ACheckboxGroup = ACheckbox.Group;
const ATabPane = ATabs.TabPane;
const emit = defineEmits(['register', 'save']);

import router from '/@/router';
import {useTabs} from '/@/hooks/web/useTabs';
import {findAllByCode, findByUserIdAndAccIdAndYear} from "/@/api/sys-acc-auth/sys-acc-anth";
import {useMessage} from "/@/hooks/web/useMessage";
import moment from "moment";

const {closeCurrent} = useTabs(router);
const {
  createErrorModal
} = useMessage()
const activeKey: any = ref('');
const formItems: any = ref({});
// 会计科目
const kmList: any = ref([]);
//币种
const bzList: any = ref([]);
// const year = new Date().getFullYear();
const year = ref(useCompanyOperateStoreWidthOut().getLoginDate.slice(0, 4))
const kemu: any = ref();

const flag: any = ref('1');
const bz: any = ref('');

const userStore: any = useUserStore();
// 数据库模式名称
const database = ref(getCurrentAccountName(false));
const databaseTrue = ref(getCurrentAccountName(true));

// 页面变量
const pageParameter = reactive({
  companyCode: '',
  companyName: '',
})

const [register, {closeModal, setModalProps}] = useModalInner((data) => {
  formItems.value.openOne = data.data.openOne;
  // 方式2
  databaseTrue.value = data.data.dynamicTenantId
  database.value = data.data.defaultAdName
  year.value = data.data.year
  kemu.value = data.data.kemu
  bz.value = data.data.currency
  flag.value = data.data.flag
  if (hasBlank(formItems.value.ddate1)) {
    formItems.value.ddate1 = useCompanyOperateStoreWidthOut().getLoginDate.substring(0,4)+'-01-01'
  }
  if (hasBlank(formItems.value.ddate2)) {
    formItems.value.ddate2 = useCompanyOperateStoreWidthOut().getLoginDate.substring(0,4)+'-12-31'
  }
});

async function handleOk() {
  if (kemu.value == null || kemu.value == '') {
    message.error('请选择银行科目!');
    return;
  }
  formItems.value.dynamicTenantId = databaseTrue.value
  formItems.value.defaultAdName = database.value
  formItems.value.year = year.value
  formItems.value.kemu = kemu.value == null ? '' : kemu.value
  formItems.value.currency = bz.value
  formItems.value.flag = flag.value

  emit('save', unref(formItems));
  closeModal();
  return true;
}

async function handleClose() {
  if (null != formItems.value.openOne && formItems.value.openOne == 1) {
    await closeCurrent()
    await router.push('/')
  }
}

//获取银行科目
async function reloadKemu() {
  kmList.value = []
  const res = await useRouteApi(findCodeKemuByBr, {schemaName: databaseTrue})({})
  kmList.value = res.filter(item => {
    item.value = item.ccode + '-' + item.ccodeName
    return item.bend == '1'
  })
  if (kmList.value.length > 0) {
    kemu.value = kmList.value[0].ccode
  } else {
    kemu.value = ''
    createErrorModal({
      iconType: 'warning',
      title: '警告',
      content: '没有授权的银行科目，请授权后重新查询！'
    })
  }
}

//获取币种
async function reloadCurrency() {
  bzList.value = [];
  useCompanyOperateStoreWidthOut().getAccountList.forEach(item => {
    if (item.accId == database.value && item.currency != '' && item.currency != null) {
      bzList.value = [{
        accountId: database.value,
        foreignCode: item.currency,
        foreignName: item.currencyName
      }]
      bz.value = item.currency
    }
  })
  useRouteApi(currentCyDatas, {schemaName: databaseTrue})({accId: database.value}).then(res => {
    bzList.value.push(...res.items)
    // bzList.value = res.items/*.filter(item=>item.foreignName.indexOf('人民币') == -1)*/
  });
}

const disabledStartDate = (current) => {
  // 获取区间最小区间
  if (!hasBlank(formItems.value.ddate2)) {
    let variable = formItems.value.ddate2
    let max = moment(variable, 'YYYY-MM-DD')
    return current > max
  }
};

const disabledEndDate = (current) => {
  // 获取区间最小区间
  if (!hasBlank(formItems.value.ddate1)) {
    let variable = formItems.value.ddate1
    let min = moment(variable, 'YYYY-MM-DD')
    return current < min
  }
};

onMounted(async () => {
  // await reloadKemu()
  await reloadCurrency()
});

const loadMark = ref(false)
async function dynamicAdReload(data) {
  loadMark.value = true
  database.value = data.accId
  databaseTrue.value = data.accountMode
  year.value = data.year
  await reloadKemu()
  await reloadCurrency()
  loadMark.value = false
}
</script>
<style lang="less" scoped>
:deep(.ant-checkbox){
  margin-top: -8px;
}
.nc-open-content {
  background-image: url(/@/assets/images/homes/bg-pattern.png);
  background-repeat: no-repeat;
  background-position: 66% 8%;
  background-size: contain;
  position: relative;
  :deep(.ant-select-selector),:deep(.ant-input),:deep(.ant-picker), :deep(.ant-input-affix-wrapper) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    background: none;
  }
  .border-div {
    position: relative;
    border: 1px #a29f9f solid;
    margin: 20px 10px;
    padding: 2.5%;

    > span {
      display: block;
      text-align: center;
      background-color: white;
      position: absolute;
      left: 50px;
      top: -10px;
      color: #888888;
      font-size: 12px;
      font-weight: bold;
      padding-left: 1em;
      padding-right: 1em;
    }
    :deep(.account-picker){
      >div{
        text-align: left;
      }
    }

    label {
      text-align: left;
      width: 100px;
      display: inline-block;
      color: #535353;
      font-size: 13px;
      font-weight: bold;
    }
    :deep(.ant-input){
      border: none !important;
    }

    :deep(.red_span) {
      color: red;
      font-weight: bold;
      display: inline-block;
      width: 20px;
      text-align: left;
    }

    .ant-select{
      color: #000000;
      font-weight: bold;
    }
    :deep(.ant-input){
      border: none !important;
      border-bottom: 1px solid #bdb9b9 !important;
    }
  }

}

:deep(.ant-select-selector),:deep(.ant-input),:deep(.ant-picker), :deep(.ant-input-affix-wrapper),:deep(.ant-input-number) {
  border: none !important;
  border-bottom: 1px solid #bdb9b9 !important;
  background: none;
}

.right-btns{
  width: 150px;background-color: #f1f1f1;padding: 10% 4%;height: 400px;
  :deep(.ant-btn-primary:hover){
    border: 1px solid #5f375c;
  }
}
:global(.ant-modal-header) {
  padding: 10px 0 !important;
}
:global(.ant-modal-close-x){
  height: 30px !important;
  color: white;
}

:deep(.ant-radio-button-wrapper){
  color: #0096c7;
}
</style>
