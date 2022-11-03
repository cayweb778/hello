<template>
  <BasicModal
    title="银行对账勾对"
    v-bind="$attrs"
    width="800px"
    @ok="handleOk()"
    @cancel="handleClose()"
    :loading="loadMark"
    @register="register"
  >
    <div class="nc-open-content" style="height: 100%">
      <div class="open-content-up">
        <div class="ocup-position"> 系统方案</div>
        <div class="ocup-position"> 个人方案</div>

        <a-tabs v-model:activeKey="activeKey" tabPosition="left" type="card">
          <a-tab-pane key="" tab="标准模式">
            <ul>
              <li>
                <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
              </li>
              <li>
                <span>银行科目：</span>
                <a-select
                  v-model:value="kemu"
                  allowClear
                  show-search
                  style="width: 340px;">
                  <a-select-option v-for="d in kmList" :value="d.ccode">
                    {{ d.value }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <span>日期：</span>
                <a-range-picker v-model:value="riqi" style="width: 340px;"/>
              </li>
              <li>
                <span>对方单位：</span>
                <a-input v-model:value="duifangUnit" style="width: 340px;"/>
              </li>
              <li>
                <span>金&emsp;&emsp;额：</span>
                <a-input-number v-model:value="amountMin" placeholder="最小金额" style="width: 150px"/>
                <span>&emsp;~&emsp;</span>
                <a-input-number v-model:value="amountMax" placeholder="最大金额" style="width: 150px"/>
              </li>
              <li>
                <span>对账币种：</span>
                <a-select v-model:value="bz" style="width: 340px;" allowClear>
                  <a-select-option v-for="d in bzList" :value="d.foreignCode">
                    {{ d.foreignName }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <span>对账状态：</span>
                <a-select v-model:value="flag" style="width: 340px;">
                  <a-select-option value=""> 全部</a-select-option>
                  <a-select-option value="1"> 已对账</a-select-option>
                  <a-select-option value="0"> 未对账</a-select-option>
                </a-select>
              </li>
              <li>
                <span>未达天数：</span>
                <a-input-number v-model:value="weidaDay" style="width: 340px;"/>
              </li>
            </ul>
          </a-tab-pane>
          <a-tab-pane key="0" tab="集团模式">
            <ul>
              <li>
                <span>核算单位：</span>

                <a-select
                  mode="multiple"
                  option-label-prop="label"
                  placeholder="可多选"
                  style="width: 340px;"
                >
                  <a-select-option label="湖北万亚" value="湖北万亚"> 湖北万亚</a-select-option>
                  <a-select-option label="北京希格" value="北京希格"> 北京希格</a-select-option>
                </a-select>
              </li>
              <li>
                <span>银行科目：</span>
                <a-select
                  v-model:value="kemu"
                  allowClear
                  show-search
                  style="width: 340px;">
                  <a-select-option v-for="d in kmList" :value="d.ccode">
                    {{ d.value }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <span>日期：</span>
                <a-range-picker v-model:value="riqi" style="width: 340px;"/>
              </li>
              <li>
                <span>对方单位：</span>
                <a-input v-model:value="duifangUnit" style="width: 340px;"/>
              </li>
              <li>
                <span>金&emsp;&emsp;额：</span>
                <a-input-number v-model:value="amountMin" placeholder="最小金额" style="width: 150px"/>
                <span>&emsp;~&emsp;</span>
                <a-input-number v-model:value="amountMax" placeholder="最大金额" style="width: 150px"/>
              </li>
              <li>
                <span>对账币种：</span>
                <a-select v-model:value="bz" style="width: 340px;" allowClear>
                  <a-select-option v-for="d in bzList" :value="d.foreignCode">
                    {{ d.foreignName }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <span>对账状态：</span>
                <a-select v-model:value="flag" style="width: 340px;">
                  <a-select-option value=""> 全部</a-select-option>
                  <a-select-option value="1"> 已对账</a-select-option>
                  <a-select-option value="0"> 未对账</a-select-option>
                </a-select>
              </li>
              <li>
                <span>未达天数：</span>
                <a-input-number v-model:value="weidaDay" style="width: 340px;"/>
              </li>
            </ul>
          </a-tab-pane>
          <a-tab-pane key="1" tab="个人标准模式">
            <ul>
              <li>
                <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
              </li>
              <li>
                <span>银行科目：</span>
                <a-select
                  v-model:value="kemu"
                  allowClear
                  show-search
                  style="width: 340px;">
                  <a-select-option v-for="d in kmList" :value="d.ccode">
                    {{ d.value }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <span>日期：</span>
                <a-range-picker v-model:value="riqi" style="width: 340px;"/>
              </li>
              <li>
                <span>对方单位：</span>
                <a-input v-model:value="duifangUnit" style="width: 340px;"/>
              </li>
              <li>
                <span>金&emsp;&emsp;额：</span>
                <a-input-number v-model:value="amountMin" placeholder="最小金额" style="width: 150px"/>
                <span>&emsp;~&emsp;</span>
                <a-input-number v-model:value="amountMax" placeholder="最大金额" style="width: 150px"/>
              </li>
              <li>
                <span>对账币种：</span>
                <a-select v-model:value="bz" style="width: 340px;" allowClear>
                  <a-select-option v-for="d in bzList" :value="d.foreignCode">
                    {{ d.foreignName }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <span>对账状态：</span>
                <a-select v-model:value="flag" style="width: 340px;">
                  <a-select-option value=""> 全部</a-select-option>
                  <a-select-option value="1"> 已对账</a-select-option>
                  <a-select-option value="0"> 未对账</a-select-option>
                </a-select>
              </li>
              <li>
                <span>未达天数：</span>
                <a-input-number v-model:value="weidaDay" style="width: 340px;"/>
              </li>
            </ul>
          </a-tab-pane>
        </a-tabs>
      </div>
    </div>
  </BasicModal>
</template>

<script lang="ts" setup="props, { content }">
import {ref, unref, onMounted, reactive} from 'vue';
import {BasicModal, useModalInner} from '/@/components/Modal';
import {
  DatePicker as ADatePicker,
  Select as ASelect,
  Input as AInput,
  InputNumber as AInputNumber,
  Checkbox as ACheckbox,
  Popover as APopover,
  Switch as ASwitch,
  Radio as ARadio,
  Upload as AUpload,
  Table as ATable,
  Tabs as ATabs,
  message,
} from 'ant-design-vue';


import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {useUserStore} from "/@/store/modules/user";
import {currentCyDatas} from "/@/api/record/system/financial-settings";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {findCodeKemuByBr} from "/@/api/record/system/bank-statement";

const ARangePicker = ADatePicker.RangePicker
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

const flag: any = ref('0');
const bz: any = ref('');
const riqi: any = ref([moment(new Date().getFullYear(), 'YYYY/MM/DD'), moment(new Date(), 'YYYY/MM/DD')])
const duifangUnit: any = ref('')
const amountMin: any = ref('')
const amountMax: any = ref('')
const weidaDay: any = ref('')

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
});

async function handleOk() {
  let startDate = null == riqi.value[0]? '':riqi.value[0].format('YYYY-MM-DD')
  let endDate = null == riqi.value[1]? '':riqi.value[1].format('YYYY-MM-DD')
  if (kemu.value == null || kemu.value == '') {
    message.error('请选择银行科目!');
    return;
  }
  formItems.value.dynamicTenantId = databaseTrue.value
  formItems.value.defaultAdName = database.value
  formItems.value.year = year.value
  formItems.value.kemu = kemu.value == null ? '' : kemu.value
  formItems.value.currency = bz.value == null ? '' : bz.value
  formItems.value.startDate = startDate
  formItems.value.endDate = endDate

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
  const res = await useRouteApi(findCodeKemuByBr, {schemaName: databaseTrue})()
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
      bz.value = item.currency
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
:deep(.ant-calendar-picker-input.ant-input), :deep(.ant-input), :deep(.ant-input-number) {
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;
}

.nc-open-content {
  position: relative;

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
          width: 120px;
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

      .ant-radio-input {
        border-color: slategrey;
      }
    }

    p:nth-of-type(2) {
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
