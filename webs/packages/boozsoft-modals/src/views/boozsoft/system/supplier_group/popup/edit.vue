<template>
  <BasicModal
    destroyOnClose
    width="900px"
    :height="520"
    :closable="false"
    v-bind="$attrs"
    title="供应商信息"
    @ok="handleOk()"
    @cancel="closeOk()"
    @register="register"
  >
    <Loading :loading="compState.loading" :absolute="compState.absolute" :tip="compState.tip" />
    <template v-if="openType=='add'" #title>
      <div style="display: flex;color: #0096c7;">
        <span>
          <PlusCircleOutlined style="font-size: 24px;font-weight: bold;margin-top:5px;"/>
        </span>
        <span style="line-height:40px;font-size: 22px;margin-top:-5px;">
          &nbsp;&nbsp;集团-供应商信息
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 40px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="/cus.png" style="height:115px;margin-right: 55px;"/>
      </div>
    </template>
    <template v-if="openType=='edit'" #title>
      <div style="display: flex;color: #0096c7;">
        <span>
          <FormOutlined style="font-size: 24px;font-weight: bold;margin-top:5px;"/>
        </span>
        <span style="line-height:40px;font-size: 22px;margin-top:-5px;">
          &nbsp;&nbsp;集团-供应商信息
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 40px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="/cus.png" style="height:115px;margin-right: 55px;"/>
      </div>
    </template>
    <template v-if="openType=='look'" #title>
      <div style="display: flex;color: #0096c7;">
        <span>
          <FileSearchOutlined style="font-size: 24px;font-weight: bold;margin-top:5px;"/>
        </span>
        <span style="line-height:40px;font-size: 22px;margin-top:-5px;">
          &nbsp;&nbsp;集团-供应商信息
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 40px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="/cus.png" style="height:115px;margin-right: 55px;"/>
      </div>
    </template>

    <div :class="isEdit?'nc-open-content':'nc-open-show-content'" style="height: 100%; overflow: hidden">
      <div class="open-content-up" style="margin-top: 20px">
        <a-radio-group v-model:value="formItems.manageType" >
          <a-radio value="1">
            <span style="color: #000000;font-weight: bold;font-size: 10px;">外部供应商</span>
          </a-radio>
          <a-radio value="0" style="margin-left: -10px;">
            <span style="color: #000000;font-weight: bold;font-size: 10px;">内部供应商</span>
          </a-radio>
        </a-radio-group>
        <br/><br/>
        <label style="font-size: 18px;margin-left: 70px;width:150px;">供应商全称：</label>
        <a-input
          v-model:value="formItems.custName"
          class="abc"
          style="width: 70%;border-bottom: 2px solid #000000;"
        />
        <span class="red_span">*</span>
        <span style="color: red;margin-left: 40%;">{{nameAllError}}</span>
        <br/><br/>
        <label>供应商编号：</label>
        <a-input v-model:value="formItems.custCode" @blur="verifyNum(formItems.custCode)"/>
        <span class="red_span">*</span>
        <label>供应商简称：</label>
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
          @change="verifySregcode(formItems.custSregcode)"
          autocomplete="off"
          placeholder=""
        />
        <span class="red_span"></span>
        <label>供应商分类：</label>
        <a-select
          v-model:value="uniqueCustclass"
          show-search
          placeholder="供应商分类"
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
        <br/>
        <span v-if="openType=='edit'">
          <label>启用状态：</label>
          <a-radio-group v-model:value="custflag">
            <a-radio value="1" >
              <span style="color: #000000;font-weight: bold;font-size: 10px;">启用</span>
            </a-radio>
            <a-radio value="0" style="margin-left: -10px;">
              <span style="color: #000000;font-weight: bold;font-size: 10px;">停用</span>
            </a-radio>
          </a-radio-group>
        </span>
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
                    v-for="customer in supplierList"
                    :key="customer.uniqueCode"
                    :value="customer.uniqueCode"
                  >
                    {{ customer.custName }}
                  </a-select-option>
                  <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                </a-select>
                &nbsp;
                <a v-if="isEdit" style="font-weight: bold;font-size: 18px;"><LinkOutlined @click="modalShow" /></a>
                <a v-else style="font-weight: bold;font-size: 18px;">&emsp;</a>

                <label>对应客户：</label>
                <a-select
                  v-model:value="formItems.uniqueCodeSupplier"
                  show-search
                  option-filter-prop="children"
                  style="width: 26%;margin-left: -8px;"
                  allow-clear
                >
                  <a-select-option
                    v-for="supplier in custmerList"
                    :key="supplier.uniqueCode"
                    :value="supplier.uniqueCode"
                  >
                    {{ supplier.custName }}
                  </a-select-option>
                  <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                </a-select>
                &nbsp;
                <a v-if="isEdit" style="font-weight: bold;font-size: 18px;"><LinkOutlined @click="cusModalShow" /></a>
                <a v-else style="font-weight: bold;font-size: 18px;">&emsp;</a>

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
      <div v-if="!isEdit">
        <a-button @click="closeOk()" type="primary">关闭</a-button>
      </div>
      <div v-if="isEdit">
        <a-button @click="closeOk()">取消</a-button>

        <span v-if="openType=='edit'">
          &emsp;
          <a-button @click="handleOk('close')" :disabled="saveClick" type="primary">保存</a-button>
        </span>
        <span v-else>&emsp;
          <a-button @click="handleOk('close')" :disabled="saveClick">保存</a-button>
          <a-button @click="handleOk('add')" :disabled="saveClick" type="primary">保存并新增</a-button>
        </span>
      </div>
    </template>

    <ModalPop @throwData="modalData" @register="registerModalPopPage" />
    <CusModalPop @throwData="throwModalData" @register="registerCusModalPopPage" />
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
import {
  CaretDownOutlined,
  FileSearchOutlined,
  FormOutlined,
  LinkOutlined,
  PlusCircleOutlined
} from '@ant-design/icons-vue'
import {
  DatePicker,
  Input as AInput,
  Radio as ARadio,
  Select as ASelect,
  Statistic as AStatistic,
  Tabs as ATabs,Cascader
} from 'ant-design-vue';
import {reactive, ref, unref} from 'vue';
import {BasicModal, useModal, useModalInner} from '/@/components/Modal';
import {
  findAllByFlag,
  findAllCountry,
  findAllSysTradeNature,
  getCodingRule,
  getMaxCusCodeByCodingFlag,
  verifyByCustAbbname,
  verifyByCustSregcode,
  verifyCustAccount,
  verifyCustomerName,
  verifyCustomerNum,
} from '/@/api/record/system/supplier_group';
import {findAllByFlag as CusFindAllByFlag,} from '/@/api/record/system/customer_group';
import {findAllByCusBendEq1} from '/@/api/record/system/supplier_class_group';
import {findAllProvince, findByZoneId} from '/@/api/record/system/zone';
import ModalPop from './modalPop.vue';
import CusModalPop from '/@/views/boozsoft/system/customer_info_group/popup/modalPop.vue';
/******************* 弹框加载中 **************************/
import {Loading} from '/@/components/Loading';
import {useMessage} from "/@/hooks/web/useMessage";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {saveLog} from "/@/api/record/system/group-sys-login-log";


const {createConfirm, createWarningModal, createMessage} = useMessage();
  const ASelectOption = ASelect.Option;
  const AInputSearch = AInput.Search;
  const AStatisticCountdown = AStatistic.Countdown;
  const ATabPane = ATabs.TabPane;
  const ARadioGroup = ARadio.Group
  const activeKey = ref('1');
  const emit=defineEmits(['register']);
  const verifyCustAccountFlag: any = ref(true);
  const verifyNameFlag: any = ref(true);
  const verifyNumFlag: any = ref(true);
  const verifyAbbnameFlag: any = ref(true);
  const verifySregcodeFlag: any = ref(true);
  const isEdit: any = ref(true);
  const saveClick: any = ref(false);
  const nameAllError: any = ref('');
  const openType: any = ref('');
  const custflag: any = ref('1');

  const formItems: any = ref({});
  const countryList: any = ref([]);
  const supplierList: any = ref([]);
  const custmerList: any = ref([]);
  const custmerClassList: any = ref([]);
  const systradenatureList: any = ref([]);
  // 供应商分类
  const uniqueCustclass = ref();
  const manageType = ref('1');
  const manageTypelist = ref([
    {
    value:'0',
    label:'内部供应商'
  },
    {
      value:'1',
      label:'外部供应商'
    },
  ]);
  const options = ref([]);
  // 编码规则
  const showRules = ref('');
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
  const zoningList = ref([]);
const zoneVal = ref([]);
const zoneData = ref('');
  const [registerModalPopPage, { openModal: openMoalPopPage }] = useModal();
  const [registerCusModalPopPage, { openModal: openCusMoalPopPage }] = useModal();

  const [register, { closeModal }] = useModalInner(async (data) => {
    openCompFullLoading()
    zoneVal.value=[]
    zoneData.value=''
    zoningList.value = await findAllProvince()

    // 全部供应商信息
    findAllByFlag('1').then((res) => {
      supplierList.value = res.items;
    });
    // 全部客户信息
    CusFindAllByFlag('1').then((res) => {
      custmerList.value = res.items;
    });
    // 全部供应商分类
    findAllByCusBendEq1().then((res) => {
      custmerClassList.value = res.items;
      if (res.items.length > 0) {
        if (data.type === 'add') {
          uniqueCustclass.value = res.items[0].uniqueCustclass;
        } else {
          uniqueCustclass.value = data.data.uniqueCustclass;
        }
      }
    });
    // 全部行业信息
    findAllSysTradeNature().then((res) => {
      systradenatureList.value = res.items;
    });
    // 国家信息
    findAllCountry().then((res) => {
      countryList.value = res.items;
    });
    if(data.data!==''){
      formItems.value.successState=data.data.successState
      formItems.value.address1=data.data.address1
      formItems.value.address2=data.data.address2
      formItems.value.applyDatabaseUniqueCode=data.data.applyDatabaseUniqueCode
      formItems.value.applyDate=data.data.applyDate
      formItems.value.applyUser=data.data.applyUser
      formItems.value.approveDate=data.data.approveDate
      formItems.value.approveUser=data.data.approveUser
      formItems.value.area=data.data.area
      formItems.value.flag=data.data.flag
      formItems.value.bankArea=data.data.bankArea
      formItems.value.bankCode=data.data.bankCode
      formItems.value.beiyong1=data.data.beiyong1
      formItems.value.beiyong2=data.data.beiyong2
      formItems.value.beiyong3=data.data.beiyong3
      formItems.value.biandongMethod=data.data.biandongMethod
      formItems.value.cellPhoneNum=data.data.cellPhoneNum
      formItems.value.city=data.data.city
      formItems.value.contacts=data.data.contacts
      formItems.value.countryName=data.data.countryName
      formItems.value.custAbbname=data.data.custAbbname
      formItems.value.custAccount=data.data.custAccount
      formItems.value.custBank=data.data.custBank
      formItems.value.custCode=data.data.custCode
      formItems.value.custDevdate=data.data.custDevdate
      formItems.value.custName=data.data.custName
      formItems.value.custSregcode=data.data.custSregcode
      formItems.value.email=data.data.email
      formItems.value.id=data.data.id
      formItems.value.uniqueCode=data.data.uniqueCode
      formItems.value.uniqueCodeCcus=data.data.uniqueCodeCcus
      formItems.value.uniqueCodeSupplier=data.data.uniqueCodeSupplier
      formItems.value.uniqueCustclass=data.data.uniqueCustclass
      formItems.value.website=data.data.website
      formItems.value.manageType=data.data.manageType
      formItems.value.zone=data.data.zone
      custflag.value=data.data.flag==undefined || data.data.flag=='1'?'1':'0'
      let zoneArr:any=[]
      if(data.data.zone!==undefined && data.data.zone!==''&& data.data.zone!==null){
        for (let i = 0; i < data.data.zone.split(',').length; i++) {
          let temp=await findByZoneId(data.data.zone.split(',')[i])
          zoneArr.push(
            {value:temp[0].id,label:temp[0].zone_name}
          )
        }
      }
      zoneData.value=zoneArr.length>0?zoneArr:''
      formItems.value.zone=''
    }
    else{

      // formItems.value=''
      formItems.value.address1=''
      formItems.value.address2=''
      formItems.value.applyDatabaseUniqueCode=''
      formItems.value.applyDate=''
      formItems.value.applyUser=''
      formItems.value.approveDate=''
      formItems.value.approveUser=''
      formItems.value.area=''
      formItems.value.bankArea=''
      formItems.value.bankCode=''
      formItems.value.beiyong1=''
      formItems.value.beiyong2=''
      formItems.value.beiyong3=''
      formItems.value.biandongMethod=''
      formItems.value.cellPhoneNum=''
      formItems.value.city=''
      formItems.value.contacts=''
      formItems.value.countryName=''
      formItems.value.custAbbname=''
      formItems.value.custAccount=''
      formItems.value.custBank=''
      formItems.value.custCode=''
      formItems.value.custDevdate=''
      formItems.value.custName=''
      formItems.value.custSregcode=''
      formItems.value.email=''
      formItems.value.id=''
      formItems.value.uniqueCode=''
      formItems.value.uniqueCodeCcus=''
      formItems.value.uniqueCodeSupplier=''
      formItems.value.uniqueCustclass=''
      formItems.value.website=''
      formItems.value.manageType='1'
      getCodingRuleNum()

    }
    compState.loading = false;
    saveClick.value = false;
    openType.value = data.type;
    isEdit.value = data.isEdit;
  });

  // 读取编码规则
 async function getCodingRuleNum(){
   // 读取编码规则-供应商
   let temp= await getCodingRule('1-2')
   if(temp!==undefined){
     // 获取最新的编码
    let temp2= await getMaxCusCodeByCodingFlag(temp)
     showRules.value=temp2
     formItems.value.custCode=showRules.value
   }
  }
  const verifyAccount = async () => {
    if(formItems.value.custAccount!==''){
      const custAccount = await verifyCustAccount(formItems.value.custAccount)
      if (custAccount > 0) {
        createWarningModal({ content: '银行账户已存在！' });
        verifyCustAccountFlag.value = false;
      } else {
        verifyCustAccountFlag.value = true;
      }
    }else{
      verifyCustAccountFlag.value = false;
    }
  }
  async function verifyName(name: any) {
    const a = await verifyCustomerName(name);
    if (a > 0) {
      nameAllError.value='供应商全称已存在'
      verifyNameFlag.value = false;
    } else {
      nameAllError.value=''
      verifyNameFlag.value = true;
    }
  }
  async function verifyNum(num: any) {
    const a = await verifyCustomerNum(num);
    if (a > 0) {
      createWarningModal({ content: '供应商编码已存在！' });
      verifyNumFlag.value = false;
    } else {
      verifyNumFlag.value = true;
    }
  }
  async function verifyAbbname(abbname: any) {
    const a = await verifyByCustAbbname(abbname);
    if (a > 0) {
      createWarningModal({ content: '供应商简称已存在！' });
      verifyAbbnameFlag.value = false;
    } else {
      verifyAbbnameFlag.value = true;
    }
  }
  async function verifySregcode(sregcode: any) {
    if (sregcode !== '') {
      const a = await verifyByCustSregcode(sregcode);
      if (a > 0) {
        createWarningModal({ content: '税号已存在！' });
        verifySregcodeFlag.value = false;
      } else {
        verifySregcodeFlag.value = true;
      }
    }
  }

  async function handleOk(closeflag) {
    if (formItems.value.custName === '') {
      createWarningModal({ content: '请填写供应商全称！' });
      return;
    } else if (!verifyNameFlag.value) {
      nameAllError.value='供应商全称已存在'
    }

    if (formItems.value.custCode === '') {
      createWarningModal({ content: '请填写供应商编码！' });
      return;
    } else if (!verifyNumFlag.value) {
      createWarningModal({ content: '供应商编码已存在！' });
      return;
    }

    if (formItems.value.custAbbname === '') {
      createWarningModal({ content: '请填写供应商简称！' });
      return;
    } else if (!verifyAbbnameFlag.value) {
      createWarningModal({ content: '供应商简称已存在！' });
      return;
    }
    if (!verifyAbbnameFlag.value) {
      createWarningModal({ content: '税号已存在！' });
      return;
    }
    if (!verifyCustAccountFlag.value) {
      createWarningModal({ content: '银行账户已存在！' });
      return;
    }
    formItems.value.uniqueCustclass = uniqueCustclass.value;
    formItems.value.flag = custflag.value;
    formItems.value.province =zoneData.value!==''?zoneData.value[0].label:formItems.value.province;
    formItems.value.city = zoneData.value!==''?zoneData.value[1].label:formItems.value.city;
    formItems.value.area = zoneData.value!==''?zoneData.value[2].label:formItems.value.area;
    formItems.value.zone = zoneVal.value.join(',')
    saveClick.value=true

    if(openType.value=='add'){
      getCodingRuleNum()
    }

    /************** 记录操作日志 ****************/
    let optAction=''
    let optContent=''
    if(openType.value=='add'){
      optAction='新增'
      optContent='【新增供应商信息】,供应商编码【'+formItems.value.custCode+'】,供应商全称【'+formItems.value.custName+'】,供应商简称【'+formItems.value.custAbbname+'】'
    }else{
      optAction='修改'
      optContent='操作内容【修改供应商信息】,供应商编码【'+formItems.value.custCode+'】,供应商全称【'+formItems.value.custName+'】,供应商简称【'+formItems.value.custAbbname+'】'
    }
    let map={
      loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
      userId: useUserStoreWidthOut().getUserInfo.username,
      userName: useUserStoreWidthOut().getUserInfo.realName,
      optModule:'group',
      optFunction:'供应商',
      optRange:'2',
      optAction:optAction,
      optContent:optContent,
    }
    await saveLog(map)
    /************** 记录操作日志 END ****************/

    if(closeflag=='add'){
      emit('save', unref(formItems));
      formItems.value={}
      formItems.value.manageType='1'
      formItems.value.creditMommand='0'
      getCodingRuleNum()
      saveClick.value=false
      return false;
    }
    emit('save', unref(formItems));
    closeModal();
  }
  const closeOk = async () => {
    let optAction=''
    let optContent=''
    if(openType.value=='add'){
      optAction='新增'
      optContent='【点击新增供应商页面】'
    }else{
      optAction='修改'
      optContent='【关闭修改供应商页面】'
    }
    /************** 记录操作日志 ****************/
    let map={
      loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
      userId: useUserStoreWidthOut().getUserInfo.username,
      userName: useUserStoreWidthOut().getUserInfo.realName,
      optModule:'group',
      optFunction:'供应商',
      optRange:'2',
      optAction:'生效审批',
      optContent:'操作内容【关闭生效审批-新增】',
    }
    await saveLog(map)
    /************** 记录操作日志 END ****************/
    emit('closeOk');
    closeModal();
  }
  const modalShow = () => {
    openMoalPopPage(true, {});
  }
  const cusModalShow = () => {
    openCusMoalPopPage(true, {});
  }

  function modalData(data) {
    formItems.value.uniqueCodeCcus=data.uniqueCode
  }
  function throwModalData(data) {
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

:deep(.ant-modal-title){
  margin-left: 10px;
}
</style>
