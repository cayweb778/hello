<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    @ok="handleOk()"
    @cancel="closeOk()"
    :showOkBtn="isEdit"
    :showCancelBtn="isEdit"
    @register="register"
  >
    <Loading :loading="compState.loading" :absolute="compState.absolute" :tip="compState.tip" />
    <template #title>
      <div style="display: flex;color: #0096c7;margin-left: 10px;">
        <span style="line-height:40px;font-size: 28px;">
          <FormOutlined style="font-size: 29px;font-weight: bold"/>
        </span>
        <span style="line-height:40px;font-size: 28px;">
          &nbsp;&nbsp;批改供应商信息
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 40px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="/cus.png" style="height:90px;margin-right: 55px;"/>
      </div>
    </template>

    <div :class="'nc-open-content'" style="margin-top: 100px;">
      <div class="open-content-up">
        <label>供应商分类：</label>
        <a-select
          v-model:value="formItems.uniqueCustclass"
          show-search
          option-filter-prop="children"
          style="width: 27%"
          allow-clear
        >
          <a-select-option
            v-for="custmerclass in custmerClassList"
            :key="custmerclass.uniqueCustclass"
            :value="custmerclass.uniqueCustclass"
          >
            {{ custmerclass.cusCclassName }}
          </a-select-option>
          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
        </a-select>
        <label style="margin-left: 55px;">省市：</label>
        <Cascader
          v-model:value="formItems.zoneVal"
          :options="zoningList"
          style="width: 235px;border: none;text-align: center"
          @change="zoneChange"
        >
          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
        </Cascader>
        <label>管理部门：</label>
        <a-select
          v-model:value="formItems.custDept"
          show-search
          option-filter-prop="children"
          style="width: 27%;"
          allow-clear
          :filter-option="filterDeptOption"
        >
          <a-select-option
            v-for="psn in deptList"
            :key="psn.deptName"
            :value="psn.uniqueCode"
          >
            {{ psn.deptName }}
          </a-select-option>
          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
        </a-select>
        &nbsp;
        <a style="font-weight: bold;font-size: 18px;"><LinkOutlined @click="openDept" /></a>

        <label>业务员：</label>
        <a-select
          v-model:value="formItems.custPsn"
          show-search
          option-filter-prop="children"
          style="width: 27%;"
          allow-clear
          :filter-option="filterDeptOption"
        >
          <a-select-option
            v-for="psn in psnList"
            :key="psn.psnName"
            :value="psn.uniqueCode"
          >
            {{ psn.psnName }}
          </a-select-option>
          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
        </a-select>
        &nbsp;
        <a style="font-weight: bold;font-size: 18px;"><LinkOutlined @click="openPsn" /></a>

        <label>价格级次：</label>
        <a-select
          v-model:value="formItems.priceLevel"
          option-filter-prop="children"
          style="width: 27%;"
        >
          <a-select-option value="协议价">协议价</a-select-option>
          <a-select-option value="零售价">零售价</a-select-option>
          <a-select-option value="一级客户价">一级客户价</a-select-option>
          <a-select-option value="二级客户价">二级客户价</a-select-option>
          <a-select-option value="三级客户价">三级客户价</a-select-option>
          <a-select-option value="四级客户价">四级客户价</a-select-option>
          <a-select-option value="五级客户价">五级客户价</a-select-option>
          <a-select-option value="六级客户价">六级客户价</a-select-option>
          <a-select-option value="七级客户价">七级客户价</a-select-option>
          <a-select-option value="八级客户价">八级客户价</a-select-option>
          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
        </a-select>

        <label style="margin-left: 56px;">税率：</label>
        <a-input-number style="width: 235px;" v-model:value="formItems.taxRate" autocomplete="off" />
        <label>应收科目：</label>
        <a-input style="width: 235px;" readonly autocomplete="off" />
        <label style="margin-left: 58px;">收款策略：</label>
        <a-input style="width: 235px;" readonly autocomplete="off" />
      </div>
    </div>

    <template #footer>
      <div v-if="!isEdit">
        <a-button @click="closeOk()" type="primary">关闭</a-button>
      </div>
      <div v-if="isEdit">
        <a-button @click="closeOk()">取消</a-button>
        <a-button @click="handleOk('close')" :disabled="saveClick" type="primary">保存</a-button>
      </div>
    </template>
    <ModalPop @throwData="parentModalData" @register="registerParentModalPopPage" />
    <SupModalPop @throwData="throwSupData" @register="registerSupModalPopPage" />
    <PsnSelect @save="saveSelectPsn" @register="registerSelectPsnPage" />
    <DeptSelect @save="saveSelectDept" @register="registerSelectDeptPage" />
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
import {CaretDownOutlined, FormOutlined, LinkOutlined} from '@ant-design/icons-vue'
import {
  Cascader,
  Input as AInput,
  InputNumber as AInputNumber,
  Radio as ARadio,
  Select as ASelect,
  Statistic as AStatistic,
  Tabs as ATabs
} from 'ant-design-vue';
import {reactive, ref} from 'vue';
import {BasicModal, useModal, useModalInner} from '/@/components/Modal';
import {batchEditCust, findAllByFlag,} from '/@/api/record/supplier_data/supplier';
import {findAllByCusBendEq1} from '/@/api/record/system/supplier_class';
import {findAllProvince} from '/@/api/record/system/zone';
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import ModalPop from '/@/views/boozsoft/system/customer_info/popup/modalPop.vue';
import SupModalPop from '/@/views/boozsoft/system/supplier/popup/modalPop.vue';
import PsnSelect from '/@/views/boozsoft/system/department/popup/select-psn.vue';
import DeptSelect from '/@/views/boozsoft/system/department/popup/select-dept.vue';
/******************* 弹框加载中 **************************/
import {Loading} from '/@/components/Loading';
import {useMessage} from "/@/hooks/web/useMessage";
import {hasBlank} from "/@/api/task-api/tast-bus-api";
import {getPsnList} from "/@/api/record/system/psn";
import {getDeptListById} from "/@/api/record/system/dept";

const [registerParentModalPopPage, { openModal: openParentMoalPopPage }] = useModal();
const [registerSupModalPopPage, { openModal: openSupMoalPopPage }] = useModal();
const [registerSelectPsnPage, { openModal: openSelectPsnPage }] = useModal();
const [registerSelectDeptPage, { openModal: openSelectDeptPage }] = useModal();

const {createConfirm, createWarningModal, createMessage} = useMessage();
// tab默认
const activeKey = ref('1');
const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const AStatisticCountdown = AStatistic.Countdown;
const ATabPane = ATabs.TabPane;
const ARadioGroup = ARadio.Group
const emit=defineEmits(['save','approveSave','updateKeyWordIsAutoSave', 'edit_database','register']);
const  saveClick= ref(false)
const  isEdit= ref(true)
const verifyCustAccountFlag: any = ref(true);
const verifyNameFlag: any = ref(true);
const verifyNumFlag: any = ref(true);
const verifyAbbnameFlag: any = ref(true);
const verifySregcodeFlag: any = ref(true);
const accountInfo: any = ref({});
const formItems: any = ref({});
const oldformItems: any = ref({});
const countryList: any = ref([]);
const supplierList: any = ref([]);
const custmerList: any = ref([]);
const custmerClassList: any = ref([]);
const systradenatureList: any = ref([]);
const custflag: any = ref('1');
// 客户分类
const uniqueCustclass: any = ref();
// 省
const provinceList: any = ref();
// 市
const cityList: any = ref([]);
// 区
const districtList: any = ref([]);
// 是否管控
const isControl: any = ref();
// 是否自动分配
const isAuto: any = ref();
// 允许修改关键字
const isKeyword: any = ref();
// 允许允许修改其他信息
const isOther: any = ref();
// 添加/修改 区分标识
const type: any = ref();
/* ************关键字**************** */
// 全称是否可输入状态
const custNameFlag: any = ref(false);
// 简称是否可输入状态
const custAbbnameFlag: any = ref(false);
// 税号是否可输入状态
const custSregcodeFlag: any = ref(false);
/* ************非关键字**************** */
// 管理类型
const manageTypeFlag: any = ref(false);
// 母公司
const uniqueCodeCcusFlag: any = ref(false);
// 供应商
const uniqueCodeSupplierFlag: any = ref(false);
// 省
const provinceFlag: any = ref(false);
// 市
const cityFlag: any = ref(false);
// 区
const areaFlag: any = ref(false);
// 联系人
const contactsFlag: any = ref(false);
// 街道
const address2Flag: any = ref(false);
// 电话
const telephoneFlag: any = ref(false);
// 国家
const countryNameFlag: any = ref(false);
// 手机
const cellPhoneNumFlag: any = ref(false);
// 网址
const websiteFlag: any = ref(false);
// 邮箱
const emailFlag: any = ref(false);
// 行业性质
const industryclassNameFlag: any = ref(false);
// 开户银行
const custBankFlag: any = ref(false);
// 开户地
const bankAreaFlag: any = ref(false);
// 银行账户
const custAccountFlag: any = ref(false);
// 银行行号
const bankCodeFlag: any = ref(false);
const defaultPage: any = ref(true);
const database: any = ref('');
const nameAllError: any = ref('');
const openType: any = ref('');
const zoningList = ref([]);
const zoneVal = ref([]);
const zoneData = ref('');
const custCodeReadonly = ref(true);
const deptList = ref([]);
const psnList = ref([]);
const idlist = ref([]);


const [register, { closeModal }] = useModalInner(async (data) => {
  database.value=data.database
  idlist.value=data.idlist
  formItems.value={}

  // 全部客户分类
  await useRouteApi(findAllByCusBendEq1,{schemaName: database})('').then((res) => {
    custmerClassList.value = res.items;
    if (res.items.length > 0) {
      uniqueCustclass.value = res.items[0].uniqueCustclass;
    }
  });
  // 省市区
  zoningList.value = await findAllProvince()
  // 部门-员工
  const psn = await useRouteApi(getPsnList,{schemaName: database})({})
  psnList.value=psn
  const dept:any = await useRouteApi(getDeptListById,{schemaName: database})({})
  deptList.value=dept.items

  saveClick.value = false
});
const loadingRef = ref(false);
const compState = reactive({
  absolute: false,
  loading: false,
  tip: '加载中...',
});
function openCompFullLoading() {
  openLoading(false);
}
function openLoading(absolute: boolean) {
  compState.absolute = absolute;
  compState.loading = true;
}
/*******************END**************************/
async function findByCus() {
  // 全部客户信息
  await useRouteApi(findAllByFlag,{schemaName: database})('1').then((res) => {
    custmerList.value = res.items;
  });
}

const modalShow = () => {
  openParentMoalPopPage(true, {
    database: database.value,
    accId: database.value.substring(0,database.value.length-5),
  });
}
const supModalShow = () => {
  openSupMoalPopPage(true, {
    database: database.value,
    accId: database.value.substring(0,database.value.length-5),
  });
}

function throwSupData(data) {
  formItems.value.uniqueCodeSupplier=data[data.length-1].uniqueCode
}
function parentModalData(data) {
  formItems.value.uniqueCodeCcus=data[data.length-1].uniqueCode
}

async function handleOk() {
  if(JSON.stringify(formItems.value)=='{}'){
    return createWarningModal({ content: '至少变更一条内容!' });
  }
  createConfirm({
    iconType: 'warning',
    title: '警告',
    content: '确定批改供应商信息吗？',
    onOk: async () => {
      let map={
        uniqueCustclass:hasBlank(formItems.value.uniqueCustclass)?"":formItems.value.uniqueCustclass,
        zoneVal:hasBlank(formItems.value.zoneVal)?"":formItems.value.zoneVal.join(','),
        custDept:hasBlank(formItems.value.custDept)?"":formItems.value.custDept,
        custPsn:hasBlank(formItems.value.custPsn)?"":formItems.value.custPsn,
        priceLevel:hasBlank(formItems.value.priceLevel)?"":formItems.value.priceLevel,
        taxRate:hasBlank(formItems.value.taxRate)?"":formItems.value.taxRate,
        idlist:idlist.value.join(',')
      }
      await useRouteApi(batchEditCust,{schemaName: database})(map)
      closeOk()
      saveClick.value=false
    }
  })

}

function clearData() {
  formItems.value={}
  formItems.value.manageType='1'
  formItems.value.isDel='0'
}

const closeOk = () => {
  emit('closeOk');
  closeModal();
}

function zoneChange(a,b) {
  zoneVal.value=a
  zoneData.value=b
}
const filterDeptOption = (input: string, option: any) => {
  return option.key.toLowerCase().indexOf(input.toLowerCase()) >= 0;
}

function openPsn() {
  openSelectPsnPage(true, {
    dynamicTenantId: database.value,
  });
}
function openDept() {
  openSelectDeptPage(true, {
    dynamicTenantId: database.value,
  });
}

function saveSelectPsn(e) {
  formItems.value.custPsn=e.uniqueCode
}
function saveSelectDept(e) {
  formItems.value.custDept=e.uniqueCode
}
</script>
<style lang="less" scoped>
:deep(.ant-calendar-picker-input.ant-input), :deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector .ant-select-selection-search-input),:deep(.ant-picker),:deep(.ant-input-number) {
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;
  font-weight: bold;
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
    font-size: 18px;
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
    margin-left: 2em;
    font-weight: bold;
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
    width: 30%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
  }

  .abc{
    border-bottom: 2px solid #666666 !important;
    font-size: 18px;
  }

  pointer-events: none;
  cursor: default;

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
  }

  :deep(.ant-cascader-input) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
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
    margin-left: 2em;
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

:deep(.ant-tabs.ant-tabs-card .ant-tabs-card-bar .ant-tabs-tab) {
  height: 40px;
  margin: 0;
  margin-right: 2px;
  padding: 0 16px;
  line-height: 38px;
  background: #fafafa;
  border: 1px solid #f0f0f0;
  border-radius: 2px 2px 0 0;
  transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
  font-weight: bold;
  font-size: 13px;
}
</style>
