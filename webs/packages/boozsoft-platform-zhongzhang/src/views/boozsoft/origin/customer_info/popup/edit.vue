<template>
  <BasicModal
    destroyOnClose
    width="900px"
    :height="460"
    :closable="false"
    v-bind="$attrs"
    title="客户信息"
    @ok="handleOk()"
    @cancel="closeOk()"
    @register="register"
  >
    <template #title>
      <div style="display: flex;color: #0096c7;">
        <span>
          <FormOutlined v-if="isEdit" style="font-size: 24px;font-weight: bold;margin-top:5px;"/>
          <FileSearchOutlined v-if="!isEdit" style="font-size: 24px;font-weight: bold;margin-top:5px;"/>
        </span>
        <span style="line-height:40px;font-size: 22px;margin-top:-5px;">
          &nbsp;&nbsp;组织-客户信息
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 40px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="/cus.png" style="height:50px;margin-right: 55px;"/>
      </div>
    </template>

    <div :class="isEdit?'nc-open-content':'nc-open-show-content'" style="height: 100%; overflow: hidden">
      <div class="open-content-up" style="margin-top: 20px">
        <a-radio-group v-model:value="manageType" style="margin-left: 50px;">
          <a-radio value="1">
            <span style="color: #000000;font-weight: bold;font-size: 10px;">外部客户</span>
          </a-radio>
          <a-radio value="0" >
            <span style="color: #000000;font-weight: bold;font-size: 10px;">内部客户</span>
          </a-radio>
        </a-radio-group>
        <br/>
        <label style="font-size: 18px;margin-left: 70px;width:150px;">客户全称：</label>
        <a-input
          v-model:value="formItems.custName"
          class="abc"
          style="width: 70%;border-bottom: 2px solid #000000;"
        />
        <span class="red_span">*</span>
        <span style="color: red;margin-left: 40%;">{{nameAllError}}</span>
        <br/><br/><br/>
        <label>客户编号：</label>
        <a-input v-model:value="formItems.custCode" style="pointer-events: none"/>
        <span class="red_span">*</span>
        <label>客户简称：</label>
        <a-input
          v-model:value.trim="formItems.custAbbname"
          @blur="verifyAbbname(formItems.custAbbname)"
          autocomplete="off"
        />
        <span class="red_span">*</span>
        <br/>
        <label>税号：</label>
        <a-input
          v-model:value.trim="formItems.custSregcode"
          autocomplete="off"
          placeholder=""
        />
        <span class="red_span"></span>
        <label>客户分类：</label>
        <a-select
          v-model:value="formItems.uniqueCustclass"
          show-search
          placeholder="客户分类"
          option-filter-prop="children"
          style="width: 30%"
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
        <span class="red_span"></span>
      </div>
      <div class="open-content-down" style="margin-top: 80px;">
        <a-tabs v-model:activeKey="activeKey" type="card">
          <a-tab-pane key="1" tab="管理信息">
            <div class="content-down-tab">
              <div class="down-tab-content">
                <label>母公司：</label>
                <a-select
                  v-model:value="formItems.uniqueCodeCcus"
                  show-search
                  option-filter-prop="children"
                  style="width: 27%;"
                  allow-clear
                >
                  <a-select-option
                    v-for="customer in custmerList"
                    :key="customer.uniqueCode"
                    :value="customer.uniqueCode"
                  >
                    {{ customer.custName }}
                  </a-select-option>
                  <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                </a-select>
                &nbsp;
                <a style="font-weight: bold;font-size: 18px;"><LinkOutlined @click="modalShow" /></a>
                <label>对应供应商：</label>
                <a-select
                  v-model:value="formItems.uniqueCodeSupplier"
                  show-search
                  option-filter-prop="children"
                  style="width: 26%;margin-left: -8px;"
                  allow-clear
                >
                  <a-select-option
                    v-for="supplier in supplierList"
                    :key="supplier.uniqueCode"
                    :value="supplier.uniqueCode"
                  >
                    {{ supplier.supName }}
                  </a-select-option>
                  <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                </a-select>
                &nbsp;
                <a style="font-weight: bold;font-size: 18px;"><LinkOutlined @click="supModalShow" /></a>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <label>国家(地区)：</label>
                <a-select
                  v-model:value="formItems.countryName"
                  show-search
                  option-filter-prop="children"
                  style="width: 27%;"
                  allow-clear
                >
                  <a-select-option
                    v-for="countrys in countryList"
                    :key="countrys.countryId"
                    :value="countrys.countryId"
                  >
                    {{ countrys.namech }}
                  </a-select-option>
                  <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                </a-select>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <label>行业性质：</label>
                <a-select
                  v-model:value="formItems.industryclassName"
                  show-search
                  option-filter-prop="children"
                  style="width: 26%;margin-left: -8px;"
                  allow-clear
                >
                  <a-select-option
                    v-for="nature in systradenatureList"
                    :key="nature.name"
                    :value="nature.name"
                  >
                    {{ nature.name }}
                  </a-select-option>
                  <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                </a-select>
                <label>销售组织：</label>
                <a-select style="width: 27%;">
                  <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                </a-select>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <label>成立日期：</label>
                <DatePicker style="width: 230px;margin-left: -9px;"/>
              </div>
            </div>
          </a-tab-pane>
          <a-tab-pane key="2" tab="联系方式">
            <div class="content-down-tab">
              <div class="down-tab-content">
                <label>省市：</label>
                <Cascader
                  v-model:value="zoneVal"
                  :options="zoningList"
                  placeholder="省/市/区"
                  style="width: 240px;border: none;text-align: center"
                  @change="zoneChange"
                />
                <label>邮政编码：</label>
                <a-input
                  v-model:value.trim="formItems.beiyong1"
                  autocomplete="off"
                  placeholder=""
                />
                <label>详细地址：</label>
                <a-input
                  v-model:value.trim="formItems.address2"
                  autocomplete="off"
                  placeholder=""
                  style="width: 27%"
                />
                <span style="margin-left: 4px;">
                  <label>官网：</label>
                  <a-input
                    v-model:value.trim="formItems.website"
                    autocomplete="off"
                    placeholder=""
                    style="width: 30%"
                  />
                </span>
                <label>联系人：</label>
                <a-input
                  v-model:value.trim="formItems.contacts"
                  autocomplete="off"
                  placeholder=""
                  style="width: 27%"
                />
                <span style="margin-left: 4px;">
                  <label>联系电话：</label>
                  <a-input
                    v-model:value.trim="formItems.telephone"
                    autocomplete="off"
                    placeholder=""
                  />
                </span>
                <label>手机：</label>
                <a-input
                  v-model:value.trim="formItems.cellPhoneNum"
                  autocomplete="off"
                  placeholder=""
                  style="width: 27%"
                />
                <span style="margin-left: 4px;">
                    <label>EMail：</label>
                    <a-input
                      v-model:value.trim="formItems.email"
                      autocomplete="off"
                      placeholder=""
                      style="width: 30%"
                    />
                </span>
              </div>
            </div>
          </a-tab-pane>
          <a-tab-pane key="3" tab="银行账户">
            <div class="content-down-tab">
              <div class="down-tab-content">
                <label>开户银行：</label>
                <a-input
                  v-model:value.trim="formItems.custBank"
                  autocomplete="off"
                  placeholder="例如：中国建设银行朝阳路支行"
                />

                <label>开户地：</label>
                <a-input
                  v-model:value.trim="formItems.bankArea"
                  autocomplete="off"
                  placeholder="例如：武汉市汉阳区"
                />
                <br />
                <label>银行账号：</label>
                <a-input
                  v-model:value.trim="formItems.custAccount"
                  autocomplete="off"
                  placeholder=""
                  @change="verifyAccount"
                  style="width:30%;"
                />

                <label>银行行号：</label>
                <a-input
                  v-model:value.trim="formItems.bankCode"
                  autocomplete="off"
                  placeholder=""
                  style="width:30%;"
                />
              </div>
            </div>
          </a-tab-pane>
          <a-tab-pane key="4" tab="开发票">
            <div class="content-down-tab">
              <div class="down-tab-content">
                <label>开票抬头全称：</label>
                <a-input autocomplete="off" />

                <label>纳税人识别号：</label>
                <a-input autocomplete="off" />

                <label>地址、电话：</label>
                <a-input autocomplete="off" />

                <label>开户行及账号：</label>
                <a-input autocomplete="off" />
              </div>
            </div>
          </a-tab-pane>
          <a-tab-pane key="5" tab="财务设置">
            <div class="content-down-tab">
              <div class="down-tab-content">
                <label>应收科目控制：</label>
                <a-input autocomplete="off" />
                <label>收款策略：</label>
                <a-input autocomplete="off" />
                <label>业务币种：</label>
                <a-input autocomplete="off" />
                <label>价格级次：</label>
                <a-input autocomplete="off" />

                <label>信用控制：</label>
                <a-radio-group >
                  <a-radio value="1" style="width:100px;">
                    <span style="color: #000000;font-weight: bold;font-size: 10px;">不控制</span>
                  </a-radio>
                  <a-radio value="0" style="margin-left: -10px;">
                    <span style="color: #000000;font-weight: bold;font-size: 10px;">严格控制</span>
                  </a-radio>
                </a-radio-group>
              </div>
            </div>
          </a-tab-pane>
        </a-tabs>
      </div>
    </div>
    <template #footer>
      <a-button @click="closeOk()" v-if="isEdit">取消</a-button>
      <a-button @click="closeOk()" v-if="!isEdit" type="primary">取消</a-button>
      <a-button @click="handleOk('close')" v-if="isEdit" :disabled="saveClick" type="primary">保存</a-button>
    </template>

    <ModalPop @throwData="modalData" @register="registerModalPopPage" />
    <SupModalPop @throwData="supThrowData" @register="registerSupModalPopPage" />
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
import {CaretDownOutlined, FileSearchOutlined, FormOutlined,LinkOutlined} from '@ant-design/icons-vue'
import {
  DatePicker,
  Input as AInput,
  Radio as ARadio,
  Select as ASelect,
  Statistic as AStatistic,
  Tabs as ATabs,Cascader
} from 'ant-design-vue';
import {ref, unref} from 'vue';
import {BasicModal, useModal, useModalInner} from '/@/components/Modal';
import {
  findAllSysTradeNature,
  findByOrgCusAbbNameAndCustNameAssignAccount,
  verifyByCustAbbname
} from "/@/api/record/system/customer_group";
import {useMessage} from "/@/hooks/web/useMessage";
import {findAllOrgCusClass} from "/@/api/org/cus_class/org_cus_class";
import {originCustomerFindAll} from "/@/api/org/cus/org_cus";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {saveLog} from "/@/api/record/system/group-sys-login-log";
import {findByUserIdAndOriginId} from "/@/api/record/system/group-permission";
import {originCustomerFindAll as originSupplierFindAll} from "/@/api/org/sup/org_sup";
import {findByUniqueCode} from "/@/api/record/group/im-organize";
import ModalPop from './modalPop.vue';
import SupModalPop from '/@/views/boozsoft/origin/supplier_info/popup/modalPop.vue';
import {findAllProvince, findByZoneId} from "/@/api/record/system/zone";

const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const AStatisticCountdown = AStatistic.Countdown;
const ATabPane = ATabs.TabPane;
const ARadioGroup = ARadio.Group
const activeKey = ref('1');
const emit=defineEmits(['register']);
const {createConfirm, createWarningModal, createMessage} = useMessage();

const isEdit: any = ref(true);
const saveClick: any = ref(false);
const nameAllError: any = ref('');
const orgUnique: any = ref('');
const manageType: any = ref('1');
const orgSaveFlag: any = ref('0');

const formItems: any = ref({});
const oldformItems: any = ref({});
const countryList: any = ref([]);
const supplierList: any = ref([]);
const custmerList: any = ref([]);
const custmerClassList: any = ref([]);
const systradenatureList: any = ref([]);
const zoningList = ref([]);
const zoneVal = ref([]);
const zoneData = ref('');

const [registerModalPopPage, { openModal: openMoalPopPage }] = useModal();
const [registerSupModalPopPage, { openModal: openSupMoalPopPage }] = useModal();

const [register, { closeModal }] = useModalInner(async (data) => {
  saveClick.value=false
  isEdit.value=data.isEdit
  manageType.value=data.data.manageType
  formItems.value=data.data
  oldformItems.value=data.data
  orgUnique.value = data.orgUnique;
  zoningList.value = await findAllProvince()
  zoneVal.value=data.data.zone==undefined|| data.data.zone==null?[]:data.data.zone.split(',')
  let zoneArr:any=[]
  if(data.data.zone!==undefined && data.data.zone!=='' && data.data.zone!==null){
    for (let i = 0; i < data.data.zone.split(',').length; i++) {
      let temp=await findByZoneId(data.data.zone.split(',')[i])
      zoneArr.push(
        {value:temp[0].id,label:temp[0].zone_name}
      )
    }
  }
  zoneData.value=zoneArr.length>0?zoneArr:''

  // 组织客户分类
  findByOrgClass()
  // 母公司
  findByOrgCusAll()
  // 对应供应商
  findByOrgSupAll()
  // 全部行业信息
  findAllSysTradeNature().then((res) => {
    systradenatureList.value = res.items;
  });

  findByGroupPermission()
});

// 母公司
async function findByOrgCusAll() {
  let map={
    searchConditon: {
      requirement: 'custCode',
      value: '',
    },
    showRulesSize: 'MIN',
    uniqueCustclass:'0',
    database: useCompanyOperateStoreWidthOut().getSchemaName,
    username: useUserStoreWidthOut().getUserInfo.username,
    flag:'1',
    orgUnique:orgUnique.value,
    searchValue: '',
    superid: '',
    page: '1',
    size: '1000000',
  }
  let temp=await originCustomerFindAll(map)
  custmerList.value=temp.items
}
// 对应供应商
async function findByOrgSupAll() {
  let map={
    searchConditon: {
      requirement: 'custCode',
      value: '',
    },
    showRulesSize: 'MIN',
    uniqueCustclass:'0',
    database: useCompanyOperateStoreWidthOut().getSchemaName,
    username: useUserStoreWidthOut().getUserInfo.username,
    flag:'1',
    orgUnique:orgUnique.value,
    searchValue: '',
    superid: '',
    page: '1',
    size: '1000000',
  }
  let temp=await originSupplierFindAll(map)
  supplierList.value=temp.items
}

// 组织客户分类
async function findByOrgClass() {
  let map={
    searchValue: '',
    superid: '',
    orgUnique: orgUnique.value,
  }
  let temp=await findAllOrgCusClass(map)
  if(temp.items.length>0){
    custmerClassList.value=temp.items.filter(a=>a.cusBend=='1')
    formItems.value.uniqueCustclass=custmerClassList.value[0].uniqueCustclass
  }else{
    closeModal();
    return createWarningModal({ content: '请先引入客户分类！' });
  }
}
// 集团档案权限分配表-集团客户
async function findByGroupPermission() {
  // 集团档案权限分配表-集团客户
  let findByPermission=await findByUserIdAndOriginId(useUserStoreWidthOut().getUserInfo.id,orgUnique.value,'customer')
  if(findByPermission.length>0){
    orgSaveFlag.value=findByPermission[0].isAuto
  }
}

async function verifyAbbname(abbname: any) {
  if(formItems.value.custName==''){
    return createWarningModal({ content: '客户全称不能为空！' });
  }
  if(orgSaveFlag.value=='1'&&oldformItems.value.custAbbname!==abbname){
    // 查询引入信息是否简称重复
    let orgAssignAbbName= await findByOrgCusAbbNameAndCustNameAssignAccount('1','1',abbname,formItems.value.custName,orgUnique.value)
    if(orgAssignAbbName>0){
      return createWarningModal({ content: '客户信息已引入！' });
    }
    // 查询未引入信息是否简称重复
    let noOrgAssignAbbName= await findByOrgCusAbbNameAndCustNameAssignAccount('1','0',abbname,formItems.value.custName,orgUnique.value)
    if(noOrgAssignAbbName>0){
      return createWarningModal({ content: '客户信息已存在,未引入！' });
    }
    const a = await verifyByCustAbbname(abbname);
    if (a > 0) {
      return createWarningModal({ content: '客户简称在集团中已存在！' });
    }
  }
}

async function handleOk(closeflag) {
  if(formItems.value.custName==''){
    return createWarningModal({ content: '客户全称不能为空！' });
  }
  if(formItems.value.custAbbname==''){
    return createWarningModal({ content: '客户简称不能为空！' });
  }
  // 查询引入信息是否重复
  let orgAssignAbbName= await findByOrgCusAbbNameAndCustNameAssignAccount('1','1',formItems.value.custAbbname,formItems.value.custName,orgUnique.value)
  if(orgAssignAbbName>0){
    return createWarningModal({ content: '客户信息已引入,不允许重复！' });
  }
  // 查询未引入信息是否重复
  let noOrgAssignAbbName= await findByOrgCusAbbNameAndCustNameAssignAccount('1','0',formItems.value.custAbbname,formItems.value.custName,orgUnique.value)
  if(noOrgAssignAbbName>0){
    return createWarningModal({ content: '客户信息已分配组织,未引入！' });
  }

  formItems.value.manageType = manageType.value;
  formItems.value.closeflag = closeflag;
  formItems.value.successState = '0';
  formItems.value.flag = '1';
  formItems.value.province =zoneData.value!==''?zoneData.value[0].label:formItems.value.province;
  formItems.value.city = zoneData.value!==''?zoneData.value[1].label:formItems.value.city;
  formItems.value.area = zoneData.value!==''?zoneData.value[2].label:formItems.value.area;
  formItems.value.zone = zoneVal.value.join(',')
  saveClick.value=true
  emit('save', unref(formItems.value));
  closeModal();
}
const closeOk = async () => {
  let orgInfo=await findByUniqueCode(orgUnique.value)
  /************** 记录操作日志 ****************/
  let map={
    loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
    userId: useUserStoreWidthOut().getUserInfo.username,
    userName: useUserStoreWidthOut().getUserInfo.realName,
    optModule:'org',
    optFunction:'客户',
    optRange:'0',
    uniqueCode:orgUnique.value,
    optAction:'新增申请',
    optContent:'操作内容【关闭新增申请-修改】,组织代码【'+orgInfo.orgCode+'】,组织名称【'+orgInfo.orgName+'】',
  }
  await saveLog(map)
  /************** 记录操作日志 End ****************/
  emit('closeOk');
  closeModal();
}

const modalShow = () => {
  openMoalPopPage(true, {
    orgUnique:orgUnique.value
  });
}
const supModalShow = () => {
  openSupMoalPopPage(true, {
    orgUnique:orgUnique.value
  });
}

function modalData(data) {
  formItems.value.uniqueCodeCcus=data.uniqueCode
}
function supThrowData(data) {
  formItems.value.uniqueCodeSupplier=data.uniqueCode
}
function zoneChange(a,b) {
  zoneVal.value=a
  zoneData.value=b
}
</script>
<style lang="less" scoped>
:deep(.ant-calendar-picker-input.ant-input), :deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector .ant-select-selection-search-input) {
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
