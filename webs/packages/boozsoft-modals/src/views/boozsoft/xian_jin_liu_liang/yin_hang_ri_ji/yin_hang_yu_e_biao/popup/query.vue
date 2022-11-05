<template>
  <BasicModal
    width="700px"
    v-bind="$attrs"
    title="长期未达账项"
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
          <span style="line-height:30px;font-size: 24px;color: #0096c7;font-weight: bold;">&ensp;现金银行余额表</span>
        </div>
        <div class="nc-open-content">
          <div class="open-content-up">
            <div class="border-div">
              <span>业务范围</span>
              <AccountPicker theme="three" @reloadTable="dynamicAdReload" style="text-align: center;"/>
            </div>
            <div class="border-div">
              <span>查询条件</span>
              <div>
                <span class="red_span"></span>
                <label>期间：&emsp;&emsp;</label>
                <a-select
                  v-model:value="strDate"
                  show-search
                  style="width: 135px;text-align: center;"
                  @change="handleChangeStrDate"
                  @focus="focusStrDate"
                >
                  <a-select-option
                    v-for="item in strDateList"
                    :key="item.id"
                    :value="item.value"
                  >
                    {{ item.value }}
                  </a-select-option>
                </a-select>
                <span>&emsp;~&emsp;</span>
                <a-select
                  v-model:value="endDate"
                  show-search
                  style="width: 135px;text-align: center;"
                  @change="handleChangeEndDate"
                  @focus="focusEndDate"
                >
                  <a-select-option
                    v-for="item in endDateList"
                    :key="item.id"
                    :value="item.value"
                  >
                    {{ item.value }}
                  </a-select-option>
                </a-select>
              </div>
              <div>
                <span class="red_span"></span>
                <label>科目：&emsp;&emsp;</label>
                <a-select v-model:value="formItems.kemu" placeholder="请选择科目" style="width: 300px;">
                  <template #suffixIcon><CaretDownOutlined style="color:#666666;"/></template>
                  <a-select-option v-for="item in kmList" :key="item.ccode" :value="item.ccode">
                    {{ item.ccode }}-{{ item.ccodeName }}
                  </a-select-option>
                </a-select>
              </div>
              <div>
                <span class="red_span"></span>
                <label>币种：&emsp;&emsp;</label>
                <a-select v-model:value="formItems.bz" style="width: 300px;">
                  <a-select-option v-for="d in bzList" :value="d.foreignCode">
                    {{ d.foreignName }}
                  </a-select-option>
                </a-select>
              </div>
              <div>
                <span class="red_span"></span>
                <label style="">字号：&emsp;&emsp;</label>
                <a-select v-model:value="formItems.fontSize" style="width: 300px;">
                  <template #suffixIcon><CaretDownOutlined style="color:#666666;"/></template>
                  <a-select-option value="MAX"> 大号字体</a-select-option>
                  <a-select-option value="MIN"> 小号字体</a-select-option>
                </a-select>
              </div>
              <br/>
              <div>
                <span class="red_span"></span>
                <label style="margin-left: 80px;width: 110px;">&emsp;&emsp;&emsp;&emsp;</label>
                <a-checkbox v-model:checked="ishaveRjz" style="width: 200px;">
                  <label>包含未记账</label>
                </a-checkbox>
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

<script setup="props, { content }" lang="ts">
import {onMounted, ref, unref} from 'vue';
import {BasicModal, useModalInner} from '/@/components/Modal';
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
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {getCurrentAccountName, hasBlank} from "/@/api/task-api/tast-bus-api";
import {findCodeKemuByBr} from "/@/api/record/system/bank-statement";
import router from "/@/router";
import {useTabs} from "/@/hooks/web/useTabs";
import {findAllByCode, findByUserIdAndAccIdAndYear} from "/@/api/sys-acc-auth/sys-acc-anth";
import {useUserStore} from "/@/store/modules/user";
import {useMessage} from "/@/hooks/web/useMessage";
import moment from "moment";
import {currentCyDatas, findAllXjOrLlList} from "/@/api/record/system/financial-settings";
import {findPeriodByAccontId} from "/@/api/record/generalLedger/data";
const {
  createErrorModal
} = useMessage()
const AInputSearch = AInput.Search;
const ASelectOption = ASelect.Option;

const {closeCurrent} = useTabs(router);

const emit = defineEmits(['register', 'save']);

const dynamicTenantId = ref(getCurrentAccountName(true))
const defaultAdName = ref(useCompanyOperateStoreWidthOut().getSchemaName)
const year = ref(useCompanyOperateStoreWidthOut().getLoginDate.slice(0, 4))
const endDate: any = ref<String>(useCompanyOperateStoreWidthOut().getLoginDate.slice(0, 7));
const strDate: any = ref<String>(year.value + '-01');
let endDateList: any = ref([]);
let strDateList: any = ref([]);
// 会计区间
const dateList: any = ref([]);
const userStore:any = useUserStore();
const ishaveRjz = ref<boolean>(true)

const formItems: any = ref({});
const [register, {closeModal}] = useModalInner((data) => {
  formItems.value.openOne = data.data.openOne;

  if (data.data.kemu != null && data.data.kemu != '') {
    formItems.value.kemu = data.data.kemu
  }
  formItems.value.weidaZhang = data.data.weidaZhang
  formItems.value.fontSize = data.data.showRulesSize
});

async function reloadDate(){
  const res = await findPeriodByAccontId(defaultAdName.value);
  // dateList.value = res.filter(item => item.iyear == year.value)
  dateList.value = res
  // 会计科目
  kmList.value = await useRouteApi(findAllXjOrLlList, {schemaName: dynamicTenantId})({
    strDate: strDate.value,
    endDate: 'test',
    accId: dynamicTenantId.value,
    userId: userStore.getUserInfo.id
  });
  kmList.value = kmList.value.filter(item => {
    item.value = item.ccode + '-' + item.ccodeName
    return item.bend == '1'
  })
}

//获取币种
const bzList: any = ref([])
async function reloadCurrency() {
  bzList.value = [];
  useCompanyOperateStoreWidthOut().getAccountList.forEach(item => {
    if (item.accId == defaultAdName.value && item.currency != '' && item.currency != null) {
      bzList.value = [{
        accountId: defaultAdName.value,
        foreignCode: item.currency,
        foreignName: item.currencyName
      }]
      formItems.value.bz = item.currency
    }
  })
  useRouteApi(currentCyDatas, {schemaName: dynamicTenantId})({accId: defaultAdName.value}).then(res => {
    bzList.value.push(...res.items)
    // bzList.value = res.items/*.filter(item=>item.foreignName.indexOf('人民币') == -1)*/
  });
}

const kmList: any = ref([])
async function handleChangeStrDate() {
  // 切换数据库
  // const s = database.value+'-'+strDate.value.substring(0,4);
  // 会计科目
  // 开始日期变动  如果开始日期大于结束于日期 则重置结束日期
  if (endDate.value) {
    if (strDate.value > endDate.value) {
      endDate.value = '';
    }
    // 会计科目
    kmList.value = await useRouteApi(findAllXjOrLlList, {schemaName: dynamicTenantId})({
      strDate: strDate.value,
      endDate: 'test',
      accId: dynamicTenantId.value,
      userId: userStore.getUserInfo.id
    });
    kmList.value = kmList.value.filter(item => {
      item.value = item.ccode + '-' + item.ccodeName
      return item.bend == '1'
    })
  }
}
async function handleChangeEndDate() {
  // 切换数据库
  // const s = database.value+'-'+endDate.value.substring(0,4);
  // 开始日期变动  如果结束日期小于开始日期 则重置开始日期
  if (strDate.value) {
    if (strDate.value > endDate.value) {
      strDate.value = '';
    }
    // 会计科目
    kmList.value = await useRouteApi(findAllXjOrLlList, {schemaName: dynamicTenantId})({
      strDate: endDate.value,
      endDate: 'test',
      accId: defaultAdName.value,
      userId: userStore.getUserInfo.id
    });
    kmList.value = kmList.value.filter(item => {
      item.value = item.ccode + '-' + item.ccodeName
      return item.bend == '1'
    })
  }
}
async function handleChange() {
}
async function focusStrDate() {
  if (endDate.value) {
    strDateList.value = dateList.value.filter((o) => o.value <= endDate.value);
  } else {
    strDateList.value = dateList.value;
  }
}
async function focusEndDate() {
  if (strDate.value) {
    endDateList.value = dateList.value.filter((o) => o.value >= strDate.value);
  } else {
    endDateList.value = dateList.value;
  }
}

async function handleOk() {
  formItems.value.strDate = strDate.value;
  formItems.value.endDate = endDate.value;
  formItems.value.ishaveRjz = ishaveRjz.value;
  if (strDate.value!=null && endDate.value!=null && strDate.value.length <= 0 && endDate.value.length <= 0) {
    message.error('请选择会计期间!');
    return false
  }
  if (strDate.value.substring(0,4)!=endDate.value.substring(0,4)){
    message.error('暂不支持跨越年度会计区间查询！');
    return false
  }
  emit('save', unref(formItems));
  closeModal();
  return false;
}

async function handleClose() {
  if (null != formItems.value.openOne && formItems.value.openOne == 1) {
    await closeCurrent()
    await router.push('/')
  }
}

const loadMark = ref(false)
async function dynamicAdReload(obj) {
  loadMark.value = true
  defaultAdName.value = obj.accId
  dynamicTenantId.value = obj.accountMode
  year.value = obj.year

  formItems.value.defaultAdName = obj.accId
  formItems.value.dynamicTenantId = obj.accountMode
  formItems.value.year = obj.year
  formItems.value.thisAdInfo = obj
  await reloadDate()
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
    text-align: left;

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
      //display: inline-block;
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
  width: 150px;background-color: #f1f1f1;padding: 10% 4%;height: 350px;
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
