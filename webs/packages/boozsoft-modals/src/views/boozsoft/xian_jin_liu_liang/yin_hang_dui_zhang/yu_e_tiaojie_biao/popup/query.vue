<template>
  <BasicModal
    title="余额调节表"
    v-bind="$attrs"
    width="700px"
    @ok="handleOk()"
    @cancel="handleClose()"
    :loading="loadMark"
    @register="register"
    :canFullscreen="false"
    :footer="null"
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
          <span style="line-height:30px;font-size: 24px;color: #0096c7;font-weight: bold;">&ensp;余额调节表</span>
        </div>
        <div class="nc-open-content">
          <div class="open-content-up">
            <div class="border-div">
              <span>业务范围</span>
              <AccountPicker theme="three" @reloadTable="dynamicAdReload" style="text-align: center;"/>
            </div>
            <div class="border-div">
              <span>查询条件</span>
<!--              <div>
                <span class="red_span"></span>
                <label>对账银行：</label>
                <a-select
                  v-model:value="kemu"
                  show-search
                  style="width: 300px;">
                  <template #suffixIcon><CaretDownOutlined style="color:#666666;"/></template>
                  <a-select-option v-for="d in kmList" :value="d.ccode">
                    {{ d.value }}
                  </a-select-option>
                </a-select>
              </div>-->
              <div>
                <span class="red_span"></span>
                <label>对账截至日期：</label>
                <a-date-picker
                  v-model:value="endDate"
                  placeholder="截止日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  style="width: 300px;"
                />
              </div>
<!--              <div>
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
              </div>-->
              <div>
                <span class="red_span"></span>
                <label>字号：</label>
                <a-select v-model:value="fontSize" style="width: 300px;">
                  <template #suffixIcon><CaretDownOutlined style="color:#666666;"/></template>
                  <a-select-option value="MAX"> 大号字体</a-select-option>
                  <a-select-option value="MIN"> 小号字体</a-select-option>
                </a-select>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="right-btns">
        <Button style="width: 100px;" shape="round" @click="handleOk"  type="primary">查询</Button>
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
  Button
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
import moment from "moment";

const {closeCurrent} = useTabs(router);
const activeKey: any = ref('');
const formItems: any = ref({});
// 会计科目
const kmList: any = ref([]);
//币种
const bzList: any = ref([]);
// const year = new Date().getFullYear();
const year = ref(useCompanyOperateStoreWidthOut().getLoginDate.slice(0, 4))
const kemu: any = ref();

const fontSize: any = ref('MIN');
const endDate: any = ref(useCompanyOperateStoreWidthOut().getLoginDate);
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
  endDate.value = data.data.endDate
  fontSize.value = data.data.showRulesSize
  if (hasBlank(endDate.value)) {
    endDate.value = useCompanyOperateStoreWidthOut().getLoginDate
  }
});

async function handleOk() {
  if (endDate.value == null || endDate.value == '') {
    message.error('请选择对账截止日期!');
    return;
  }
  formItems.value.dynamicTenantId = databaseTrue.value
  formItems.value.defaultAdName = database.value
  formItems.value.year = year.value
  formItems.value.kemu = kemu.value == null ? '' : kemu.value
  formItems.value.currency = bz.value == null ? '' : bz.value
  formItems.value.endDate = endDate.value
  formItems.value.showRulesSize = fontSize.value

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
  const res = await useRouteApi(findCodeKemuByBr, {schemaName: databaseTrue})({})
  kmList.value = res.filter(item => {
    item.value = item.ccode + '-' + item.ccodeName
    return item.bend == '1'
  })
  if (kmList.value.length > 0) {
    kemu.value = kmList.value[0].ccode
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
      bz.value = item.currencyName
    }
  })
  useRouteApi(currentCyDatas, {schemaName: databaseTrue})({accId: database.value}).then(res => {
    bzList.value.push(...res.items)
    // bzList.value = res.items/*.filter(item=>item.foreignName.indexOf('人民币') == -1)*/
  });
}

onMounted(async () => {
  await reloadKemu()
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
  }

}

.right-btns{
  width: 150px;background-color: #f1f1f1;padding: 10% 4%;height: 250px;
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
