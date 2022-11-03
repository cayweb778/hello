<template>
  <BasicModal
    destroyOnClose
    width="800px"
    v-bind="$attrs"
    okText="保存"
    cancelText="取消"
    title="会计科目"
    @ok="handleOk()"
    @cancel="handleClose()"
    @register="register"
  >
    <template #title>
      <div style="display: flex;color: #0096c7;">
        <span style="line-height:40px;font-size: 28px;">
          <PlusCircleOutlined style="font-size: 34px;font-weight: bold"/>
        </span>
        <span style="line-height:40px;font-size: 28px;margin-top:-5px;">
          &nbsp;&nbsp;组织会计科目
        </span>
        <label style="margin-left: 15%;color: black;font-size: 14px;margin-top:15px;">编码级次规则：{{jici}}</label>
      </div>
      <div style="display: inline-block;position:absolute;right: 40px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="/cus.png" style="height:66px;margin-right: 55px;"/>
      </div>
    </template>
    <div class="nc-open-content">
      <div class="open-content-up">
        <div style="margin-left: 20px;margin-top: 20px;">
          <a-radio-group v-model:value="formItems.bprogerty" v-model:disabled="bprogertyDisabled" style="width:40%;">
            <a-radio value="1" >
              <span style="color: #000000;font-weight: bold;font-size: 10px;">借方</span>
            </a-radio>
            <a-radio value="0" style="margin-left: -10px;">
              <span style="color: #000000;font-weight: bold;font-size: 10px;">贷方</span>
            </a-radio>
          </a-radio-group>
          <br/>
          <label>上级科目：</label>
          <TreeSelect
            style="width: 67%;"
            :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
            :tree-data="codelist"
            placeholder="请选择上级科目"
            tree-default-expand-all
            allow-clear
            show-search
            v-model:value="superiorCcode"
            v-model:disabled="superiorTreeFlag"
            @change="superiorCcodeChange"
            :filterTreeNode="filterOption"
          >
            <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
          </TreeSelect>
          <br/>
          <label>科目编码：</label>
          <a-input
            @blur="ccodeblur"
            @keyup="ccodeWacth"
            @change="findByCclass"
            @focus="focusInput($event)"
            v-model:value.trim="formItems.ccode"
            autocomplete="off"
            style="width: 25%;"
            onkeyup="value=value.replace(/o/,'').replace(/l/,'').replace(/[\u4e00-\u9fa5]/, '')"
          />
          <label>科目类型：</label>
          <a-select
            v-model:value="formItems.cclass"
            style="width: 25%;text-align: center;"
            allow-clear
          >
            <a-select-option :value="item.cclass" v-for="(item, i) in styleName">{{
                item.cclass
              }}</a-select-option>
            <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
          </a-select>
          <br/><br/>
          <span style="margin-left: 23px;">
            <label style="font-size: 18px;margin-left: 0;width:150px;">科目名称：</label>
            <a-input v-model:value.trim="formItems.ccodeName" placeholder="会计科目名称" autocomplete="off" class="abc" style="font-size: 18px;width: 60%;" />
            <span class="red_span">*</span>
          </span>
          <br/><br/>
        </div>
      </div>
      <div class="open-content-down">
        <a-tabs v-model:activeKey="activeKey" @change="tabChange" type="card">
          <a-tab-pane key="1" tab="科目属性">
            <div class="down-tab-content">
              <a-radio-group v-model:value="formItems.bcash" v-model:disabled="bcashDisabled" @change="bcashchecked" style="width: 50%">
                <label>现金科目：&emsp;&emsp;&emsp;</label>
                <a-radio value="0" style="width:80px;margin-left: -10px;">
                  否
                </a-radio>
                <a-radio value="1" style="width:80px;margin-left: -10px;">
                  是
                </a-radio>
              </a-radio-group>
              <a-radio-group v-model:value="formItems.bbank" v-model:disabled="bbankDisabled" @change="bbankchecked" style="width: 50%">
                <label>银行科目：&emsp;&emsp;&emsp;</label>
                <a-radio value="0" style="width:80px;margin-left: -10px;">
                  否
                </a-radio>
                <a-radio value="1" style="width:80px;margin-left: -10px;">
                  是
                </a-radio>
              </a-radio-group>
              <br/>
              <a-radio-group v-model:value="formItems.bcashEquivalence" v-model:disabled="bcashEquivalenceDisabled" @change="bcashEquivalencechecked" style="width: 50%">
                <label>现金等价物科目：</label>
                <a-radio value="0" style="width:80px;margin-left: -10px;">
                  否
                </a-radio>
                <a-radio value="1" style="width:80px;margin-left: -10px;">
                  是
                </a-radio>
              </a-radio-group>
              <a-radio-group v-model:value="formItems.xjll" v-model:disabled="xjllDisabled" style="width: 50%">
                <label>现金流量科目：&emsp;</label>
                <a-radio value="0" style="width:80px;margin-left: -10px;">
                  否
                </a-radio>
                <a-radio value="1" style="width:80px;margin-left: -10px;">
                  是
                </a-radio>
              </a-radio-group>
              <br/>
              <a-radio-group v-model:value="formItems.bdaybook" v-model:disabled="bdaybookDisabled" style="width: 50%">
                <label>日记账科目：&emsp;&emsp;</label>
                <a-radio value="0" style="width:80px;margin-left: -10px;">
                  否
                </a-radio>
                <a-radio value="1" style="width:80px;margin-left: -10px;">
                  是
                </a-radio>
              </a-radio-group>

            </div>
          </a-tab-pane>
          <a-tab-pane key="2" tab="辅助核算">
            <div class="down-tab-content">
              <a-table
                ref="tableRef"
                :bordered="true"
                :pagination="false"
                :class="'a-table-font-size-12'"
                :dataSource="fuzhuModalDataSource"
                :columns="fuzhuModalColumns"
                :row-selection="{selectedRowKeys:selectedRowKeys, onChange: onFuzhuHeSuanChange}"
                :scroll="{ y: 130 }"
              />
            </div>
          </a-tab-pane>
          <a-tab-pane key="3" tab="外币核算">
            <div class="down-tab-content">
              <label>外币币种：</label>
              <a-select
                v-model:value="formItems.currencyType"
                @change="currencychecked"
                show-search
                placeholder="请选择"
                style="width: 25%;text-align: center;"
                allow-clear
              >
                <a-select-option :value="item.currencyZhCnName" v-for="(item,i) in currencylist">
                  {{ item.currencyZhCnName }}
                </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
              </a-select>
            </div>
          </a-tab-pane>
          <a-tab-pane key="4" tab="数量核算">
            <div class="down-tab-content">
              <label>计量单位</label>
              <a-select
                v-model:value="formItems.menterage"
                @change="bnumchecked"
                show-search
                placeholder="请选择"
                style="width: 25%;text-align: center;"
                allow-clear
              >
                <a-select-option :value="item.unitName" v-for="(item, i) in unit_meaList">
                  {{ item.unitName }}
                </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
              </a-select>
            </div>
          </a-tab-pane>
          <a-tab-pane key="5" tab="科目控制">
            <div class="down-tab-content">
              <a-radio-group v-model:value="formItems.lowerControl" v-model:disabled="lowerDisabled" style="width: 50%">
                <label>允许添加下级科目：</label>
                <a-radio value="0" style="width:80px;margin-left: -23px;">
                  否
                </a-radio>
                <a-radio value="1" style="width:80px;margin-left: -10px;">
                  是
                </a-radio>
              </a-radio-group>
              <a-radio-group v-model:value="formItems.fuzhuControl" v-model:disabled="fuzhutDisabled" style="width: 50%">
                <label>允许添加辅助核算：</label>
                <a-radio value="0" style="width:80px;margin-left: -23px;">
                  否
                </a-radio>
                <a-radio value="1" style="width:80px;margin-left: -10px;">
                  是
                </a-radio>
              </a-radio-group>
              <a-radio-group v-model:value="formItems.controlled" v-model:disabled="controlledDisabled" style="width: 50%">
                <label>业务受控科目：&emsp;</label>
                <a-radio value="0" style="width:80px;margin-left: -10px;">
                  否
                </a-radio>
                <a-radio value="1" style="width:80px;margin-left: -10px;">
                  是
                </a-radio>
              </a-radio-group>
            </div>
          </a-tab-pane>
          <a-tab-pane key="6" tab="预算会计" v-if="styleName.lenght==8">
            <div style="margin-top: 20px;" class="down-tab-content">
              <a-radio-group v-model:value="formItems.pxjz" v-model:disabled="pxjzDisabled" style="width: 50%">
                <label>平行记账科目：&emsp;</label>
                <a-radio value="0" style="width:80px;margin-left: -23px;">
                  否
                </a-radio>
                <a-radio value="1" style="width:80px;margin-left: -10px;">
                  是
                </a-radio>
              </a-radio-group>
              <a-radio-group v-model:value="formItems.cyfx" v-model:disabled="cyfxDisabled" style="width: 50%">
                <label>差异分析科目：&emsp;</label>
                <a-radio value="0" style="width:80px;margin-left: -10px;">
                  否
                </a-radio>
                <a-radio value="1" style="width:80px;margin-left: -10px;">
                  是
                </a-radio>
              </a-radio-group>
            </div>
          </a-tab-pane>
        </a-tabs>
      </div>
    </div>
    <template #footer>
      <a-button @click="closeModal(),handleClose()">取消</a-button>
      <a-button @click="handleOk('close')" :disabled="saveClick">保存</a-button>
      <a-button @click="handleOk('add')" :disabled="saveClick" type="primary">保存并新增</a-button>
    </template>
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
import { PlusCircleOutlined,CaretDownOutlined } from '@ant-design/icons-vue'
import { onMounted, reactive, ref, toRaw, unref,  watch } from 'vue';
import { BasicModal, useModalInner } from '/@/components/Modal';
import {
  findAllByGorupCurrency,
  findByCcode,
  findByCodeNum,
  findByCodeName,
  findBySuperCodeName,
  findByLowerMaxCodeNum,
  company_findByCodeNum,
  findByOrgLowerMaxCodeNum,
  findByOrgCcode,
  findByOrgCodeNum,
  findByOrgCodeName, findByOrgAccountCodeKemuSuperCodeAccvoucher,
} from '/@/api/codekemu/codekemu';
import {
  TreeSelect,
  Tabs as ATabs,
  Select as ASelect,
  Input as AInput,
  Statistic as AStatistic,
  Table as ATable,
  Radio as ARadio,
} from 'ant-design-vue';
import {useMessage} from "/@/hooks/web/useMessage";
import {treeCode, treeOrgCode} from "/@/api/acctemplate/acctemplate";
import {findFuzhuHesuanList} from "/@/api/record/system/origin-fuzhu-hesuan";
import {findAllByAccGroup} from "/@/api/record/system/account";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";

const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const AStatisticCountdown = AStatistic.Countdown;
const ATabPane = ATabs.TabPane;
const ARadioGroup = ARadio.Group
const emit=defineEmits(['register','save']);
const {createConfirm, createWarningModal, createMessage} = useMessage();
// 上级科目末级状态
const superCodeBend = ref('');
const tableRef:any = ref(null)
const bcashDisabled = ref(false);
const bbankDisabled = ref(false);
const bcashEquivalenceDisabled = ref(false);
const bprogertyDisabled = ref(false);
const controlledDisabled = ref(false);
const pxjzDisabled = ref(false);
const cyfxDisabled = ref(false);
const xjllDisabled = ref(false);
const bdaybookDisabled = ref(false);
const currencyDisabled = ref(false);
const bnumDisabled = ref(false);
const lowerDisabled = ref(false);
const fuzhutDisabled = ref(false);

// 'no' 只能修改名称、编码；其他属性与上级科目一致,'yes' 全部
const allProperty: any = ref(true);
// 辅助model
const fuzhuModal = ref(false);
// 是否独立账套
const independent = ref(false);
// 计算单位list
const unitmealist: any = ref([]);
// 货币list
const currencylist: any = ref([]);
// 科目list
const codelist: any = ref([]);
// 科目类型状态
const cclassflg: any = ref(true);
// 会计准则
const standardName: any = ref('');
const standardUnique: any = ref('');
const templateID: any = ref('');
const templateName: any = ref('');
// 账套是否预算会计
const ibudgetAccounting: any = ref('');
// 级次
const jici: any = ref('');
// 级次:增加科目用到-上级科目级次
const jici2: any = ref(1);
// 级次:增加科目用到-下级科目级次
const last_jici: any = ref(1);
// 科目类型
const styleName: any = ref([]);
const unit_meaList: any = ref([]);
// 本位币
const currency: any = ref('人民币');
// 上级科目
const superiorCcode: any = ref('');
const superiorTreeFlag: any = ref(false);
const lowerControl: any = ref(false);
const fuzhuControl: any = ref(false);
const saveClick: any = ref(false);
const pagetype: any = ref('');
const orgUnique: any = ref('');

// 上级科目-科目下级控制
const super_lowerControl: any = ref(false);
// 业务受控制
const controlled: any = ref(false);
// 表单封装数据
const formItems = reactive({
  controlled: '0',
  ccode: '',
  ccodeName: '',
  cclass: '',
  bprogerty: '1',
  bnum: '0',
  menterage: '',
  currency: '0',
  currencyType: '',
  bcash: '0',
  bbank: '0',
  bcashEquivalence: '0',
  bdaybook: '0',
  igrade: '',
  uniqueAccStandard: '',
  superiorCcode: '',
  templateId: '',
  lowerControl: '0',
  fuzhuControl: '0',
  uniqueAccStandardName: '',
  templateName: '',
  bend: '1',
  flag: '1',
  iyear: '',
  pxjz: '0',
  xjll: '0',
  cyfx: '0',
  fuzhu: '',
  yusuan: '0',
  superCodeBend: '',
  oldCcode: '',
  closeflag: '',
  orgUnique: '',
  ysYsly: '0',
  ysZcgnfl: '0',
  ysZfzcjjfl: '0',
  ysBmzcjjfl: '0',
  cassItem: '0',
  bstock: '0',
});

// 数据库模式名称
const database = ref('');
const accId = ref('');
const iyear: any = ref('');
// tab默认
const activeKey = ref('1');
const fzcolumns = ref([
  {
    title: '自定义项编码',
    dataIndex: 'ccode',
    key: 'ccode',
  },
  {
    title: '自定义项名称',
    dataIndex: 'cname',
    key: 'cname',
  }
]);
const fuzhuModalDataSource = ref([
  {
    key:999,
    ccode:'',
    cname:'个人',
    cdfine:999
  },
  {
    key:998,
    ccode:'',
    cname:'部门',
    cdfine:998
  },
  {
    key:997,
    ccode:'',
    cname:'项目',
    cdfine:997
  },
  {
    key:996,
    ccode:'',
    cname:'客户',
    cdfine:996
  },
  {
    key:995,
    ccode:'',
    cname:'供应商',
    cdfine:995
  },
  {
    key:994,
    ccode:'',
    cname:'支出功能分类',
    cdfine:994
  },
  {
    key:993,
    ccode:'',
    cname:'部门支出经济分类',
    cdfine:993
  },
  {
    key:992,
    ccode:'',
    cname:'政府支出经济分类',
    cdfine:992
  },
  {
    key:991,
    ccode:'',
    cname:'预算来源',
    cdfine:991
  },
  {
    key:990,
    ccode:'',
    cname:'项目大类',
    cdfine:990
  }
]);
const fuzhuModalDataSource2 = ref([
  {
    key:999,
    ccode:'',
    cname:'个人',
    cdfine:999
  },
  {
    key:998,
    ccode:'',
    cname:'部门',
    cdfine:998
  },
  {
    key:997,
    ccode:'',
    cname:'项目',
    cdfine:997
  },
  {
    key:996,
    ccode:'',
    cname:'客户',
    cdfine:996
  },
  {
    key:995,
    ccode:'',
    cname:'供应商',
    cdfine:995
  },
  {
    key:994,
    ccode:'',
    cname:'支出功能分类',
    cdfine:994
  },
  {
    key:993,
    ccode:'',
    cname:'部门支出经济分类',
    cdfine:993
  },
  {
    key:992,
    ccode:'',
    cname:'政府支出经济分类',
    cdfine:992
  },
  {
    key:991,
    ccode:'',
    cname:'预算来源',
    cdfine:991
  },
  {
    key:990,
    ccode:'',
    cname:'项目大类',
    cdfine:990
  }
]);
const fuzhuModalColumns = ref([
  {
    title: '辅助项',
    dataIndex: 'cname',
    key: 'cname',
    width:'20%',
    colSpan: 2,
    customRender: ({ text, index }) => {
      const obj = {
        children: text,
        props: {} as any,
      };
      obj.props.colSpan = 2;
      return obj;
    },
  },
]);
const selectedRowKeys = ref([]);
const ccodeInfo=ref('')
const [register, { closeModal }] = useModalInner(async (data) => {
  handleClose()
  pagetype.value = data.type;
  orgUnique.value = data.orientation;
  jici.value = data.data.jici;
  ibudgetAccounting.value=data.ibudgetAccounting
  standardUnique.value = data.data.uniqueAccStandard;
  templateID.value = data.data.templateId;
  styleName.value = data.data.styleName.filter(v=>v.cclass!=='全部');
  independent.value = data.data.independent;
  iyear.value = data.data.iyear;
  accId.value = data.data.accId;
  superiorCcode.value=data.ccodeInfo!==undefined&&data.ccodeInfo!==''?data.ccodeInfo[0].bend==='1'?data.ccodeInfo[0].ccode:'':''
  allProperty.value = data.all==='yes'?true:false;
  superiorTreeFlag.value = data.all==='no'?true:false;
  if(superiorCcode.value!==''){
    superiorCcodeChange(superiorCcode.value)
  }
  ccodeInfo.value=data.ccodeInfo
// 获取所有计算单位
//   findAllOrderById().then((res) => {
//     unit_meaList.value = res;
//   });
  // 获取所有国币
  findAllByGorupCurrency().then((res) => {
    currencylist.value = res;
  });
  saveClick.value=false

  // 组织辅助核算自定义
  if(data.type==='zz'){
    fuzhuModalDataSource.value=[]
    let orgHs=await findFuzhuHesuanList({originId: data.orientation})
    orgHs.items.forEach((i,index)=>{
      let temp:any=fuzhuModalDataSource2.value.filter(v=>v.cname==i.cname)
      fuzhuModalDataSource.value.push({
        key:temp[0].key,
        ccode:temp[0].ccode,
        cname:temp[0].cname,
        cdfine:temp[0].cfield
      })
    })
    treeOrgCode(orgUnique.value,iyear.value).then((res) => {
      codelist.value = res;
    });
  }else{
    treeCode(data.data.uniqueAccStandard, data.data.templateId).then((res) => {
      codelist.value = res;
    });
  }
});

watch(() => superiorCcode.value,async (newValue, oldValue) => {
  if(newValue===undefined){
    formItems.ccode=''
    formItems.cclass=''
    cclassflg.value=false
    last_jici.value =1 ;
    jici2.value =1 ;
    superiorCcode.value ='' ;
    // findByAllProperty(true)
  }
});

function focusInput(event) {
  event.currentTarget.select();
}
function ccodeWacth() {
  if(formItems.ccode=='' ){
    superiorCcode.value =''
    allProperty.value=true
    findByAllProperty('')
  }
}
async function findByAllProperty(data) {
    formItems.ccode = '';
    formItems.ccodeName = '';
    formItems.currencyType=''
    formItems.menterage=''
    formItems.bdaybook='0'
    formItems.controlled='0'

    formItems.bnum='0'
    formItems.currency='0'
    formItems.bcashEquivalence='0'
    formItems.bbank='0'
    formItems.bcash='0'
    formItems.xjll='0'
    formItems.pxjz='0'
    formItems.cyfx='0'
    formItems.bprogerty='1'

    xjllDisabled.value=false;
    pxjzDisabled.value=false;
    cyfxDisabled.value=false;
    bprogertyDisabled.value=false;
    controlledDisabled.value=false;
    bdaybookDisabled.value=false;
    bcashDisabled.value=false;
    bbankDisabled.value=false;
    bcashEquivalenceDisabled.value=false;
    lowerDisabled.value=false;
    fuzhutDisabled.value=false;
    currencyDisabled.value=false
    bnumDisabled.value=false
    selectData.value=[]
    selectedRowKeys.value=[]

}

// 科目编码blur触发
async function findByAllProperty2(data) {
  if(!allProperty.value){
    formItems.menterage = data.menterage;
    formItems.currencyType = data.currencyType;
    formItems.bdaybook = data.bdaybook
    formItems.controlled = data.controlled
    formItems.bnum = data.bnum ;
    formItems.currency = data.currency;
    formItems.bprogerty = data.bprogerty;
    formItems.cclass = data.cclass;
    formItems.pxjz = data.pxjz;
    formItems.xjll = data.xjll;
    formItems.cyfx = data.cyfx;
    //
    lowerDisabled.value=true
    fuzhutDisabled.value=true
    bnumDisabled.value=true
    currencyDisabled.value=true
    bcashEquivalenceDisabled.value=true
    bbankDisabled.value=true
    bcashDisabled.value=true
    bdaybookDisabled.value=true
    cyfxDisabled.value=true
    pxjzDisabled.value=true
    controlledDisabled.value=true
    bprogertyDisabled.value=true
    // 现金...3个
    formItems.bcash = data.bcash;
    formItems.bbank = data.bbank;
    formItems.bcashEquivalence =data.bcashEquivalence;

    if (data.fuzhu!==null && data.fuzhu !== '') {
      for (let i = 0; i < data.fuzhu.split(',').length; i++) {
        let list = fuzhuModalDataSource.value.filter(item => item.cname === data.fuzhu.split(',')[i]);
        if(list.length>0){
          selectedRowKeys.value.push(list[0].key)
          selectData.value.push({
            key: i,
            ccode: list[0].ccode,
            cname: list[0].cname,
            cdfine: list[0].cdfine,
          })
        }
      }
    }
  }
}
function createConfirmPop(text) {
  createConfirm({
    iconType: 'warning',
    title: '警告',
    content: text,
    onOk: async() => {},
  })
}

const superiorCcodeChange = async (val) => {

  let a=0
  // 判断上级科目在 组织所属账套是否有下级、期初或凭证
  await findByOrgAccountCodeKemuSuperCodeAccvoucher(orgUnique.value,iyear.value,val).then(async (data)=>{
    for (let i = 0; i < data.length; i++) {
      if(parseInt(data[i].accvoucherCount)+parseInt(data[i].superiorCcode)>0){
        superiorCcode.value=''
        a++
        return createWarningModal({title: '温馨提示', content: '当前会计科目已经在【'+data[i].accName+'】账套中使用,不能进行新增下级操作!'})
        break
      }
    }
  })

  if(a==0){
    var sumname;
    if(superiorCcode.value===''){
      sumname = await findByCodeName(standardUnique.value,templateID.value,formItems.ccodeName);
    }else{
      sumname = await findBySuperCodeName(standardUnique.value,templateID.value,formItems.ccodeName,superiorCcode.value,iyear.value);
    }
    if (sumname > 0) {
      createConfirmPop('科目名称已存在')
      return false;
    }

    // 获取上级科目信息
    let code:any = ''
    if(pagetype.value=='zz'){
      code=await findByOrgCcode(val,orgUnique.value,iyear.value)
    }else{
      code = await findByCcode(val,templateID.value);
    }
    getNewCcode(code,val)
  }
}


async function getNewCcode(code,val) {
  if(code!=='不存在'){
    const igrade = code.igrade;
    if(!independent.value){
      if (code.lowerControl === '0') {
        superiorCcode.value=''
        formItems.ccode=''
        createConfirmPop('新增下级科目控制已开启,不能增加下级科目！')
        return false;
      }
    }

    superCodeBend.value = code.bend;
    formItems.cclass = code.cclass;
    formItems.bprogerty = code.bprogerty;
    cclassflg.value = true;
    last_jici.value = parseInt(igrade) + 1 ;
    const arr = jici.value.replaceAll('-', '').substring(0, last_jici.value);
    var code_length = 0; // 符合规则的科目长度
    for (let i = parseInt(igrade); i < arr.length; i++) {
      code_length += parseInt(arr[i]);
    }
    if(val!=undefined){
      // 编码_级次
      let newccode=await findByOrgLowerMaxCodeNum(orgUnique.value,val,code_length,iyear.value);
      formItems.ccode = newccode;
      // 获取上级科目级次
      jici2.value =  igrade;
    }
  }
  else{
    formItems.bprogerty = '1';
  }
}

const selectData=ref([])
const onFuzhuHeSuanChange = (a,b) => {
  selectedRowKeys.value=a
  selectData.value=b.filter(item=>a.indexOf(item.key)!=-1)
  if(a.length>10){
    createConfirmPop('不能超过10个自定义项')
    selectedRowKeys.value=[]
    selectData.value=[]
  }
}

function cancelModel() {
  selectedRowKeys.value=[]
  selectData.value=[]
  fuzhuModalDataSource.value=[]
}
function modalOk() {
  fuzhuModal.value=false
}
// 选中数量核算单位
const bnumchecked = async (val: any) => {
  if(val!==''||val!==undefined){
    formItems.menterage = val;
    formItems.bnum = '1';
  }
};
// 选中外币核算
const currencychecked = (val: any) => {
  if(val!==''||val!==undefined){
    formItems.currencyType = val;
    formItems.currency='1'
  }
};
// 选中现金
const bcashchecked = (val: any) => {
  bbankDisabled.value = val.target.value=='1';
  bcashEquivalenceDisabled.value = val.target.value=='1';
};
// 选中银行
const bbankchecked = (val: any) => {
  bcashDisabled.value = val.target.value=='1';
  bcashEquivalenceDisabled.value = val.target.value=='1';
};
// 选中现金等价物
const bcashEquivalencechecked = (val) => {
  bbankDisabled.value = val.target.value=='1';
  bcashDisabled.value = val.target.value=='1';
};

// 获取编码第几级
function getJiCiIndex(jiciLenth){
  let jiciIndes='0'
  let temp=formItems.ccode.length
  jiciLenth.forEach((v,index)=>{
    if(v===temp){
      jiciIndes=index
    }
  })
  return jiciIndes
}

const ccodeblur = async () => {
  // 每级的长度
  let jiciLenth:any=[]
  let t:any=0
  jici.value.split('-').forEach((v,index)=>{
    if(parseInt(v)===4||parseInt(v)===3){t=parseInt(v)}else{t+=parseInt(v)}
    jiciLenth.push(t)
  })

  // 下级科目
  if(formItems.ccode.length>4){
    let index= getJiCiIndex(jiciLenth)
    let codeLength=jiciLenth[parseInt(index)-1]
    if(codeLength===undefined){
      createConfirmPop('科目级次不符合！')
      return false;
    }
    // 算出上级科目
    let superiorCcode2=formItems.ccode.substring(0,parseInt(codeLength))
    if(formItems.ccode.substring(superiorCcode2.length)==='00'){
      createConfirmPop('科目编码必须大于正整数！')
      return false;
    }
    // 获取上级科目信息
    const superCodeInfo:any = await findByOrgCcode(superiorCcode2,orgUnique.value,iyear.value);
    if(superCodeInfo==='不存在'){
      createConfirmPop('没有上级科目信息1！')
      return false;
    }else{

      let a=0
      // 判断上级科目在 组织所属账套是否有下级、期初或凭证
      await findByOrgAccountCodeKemuSuperCodeAccvoucher(orgUnique.value,iyear.value,superiorCcode2).then(async (data)=>{
        for (let i = 0; i < data.length; i++) {
          if(parseInt(data[i].accvoucherCount)+parseInt(data[i].superiorCcode)>0){
            formItems.ccode=''
            a++
            return createWarningModal({title: '温馨提示', content: '当前会计科目已经在【'+data[i].accName+'】账套中使用,不能进行新增下级操作!'})
            break
          }
        }
      })

      if(a==0){
        // 开启下级控制
        super_lowerControl.value = superCodeInfo.lowerControl === '0';
        superiorCcode.value=superCodeInfo.ccode
        // findByAllProperty2(superCodeInfo)
        formItems.bprogerty=superCodeInfo.bprogerty
      }
    }
  }
  else{
    // 科目编码是一级科目，上级科目下拉框清空
    if(superiorCcode.value!==''){
      superiorCcode.value=''
      allProperty.value=true
      // findByAllProperty3()
      findByAllProperty('')
    }
  }

  last_jici.value = superiorCcode.value !== '' ? parseInt(jici2.value) + 1 : 1;
  const arr = jici.value.replaceAll('-', '').substring(0, last_jici.value);
  var code_length = 0; // 符合规则的科目长度
  for (let i = 0; i < arr.length; i++) {
    code_length += parseInt(arr[i]);
  }

  const newcode_length = formItems.ccode !== '' ? formItems.ccode.length : 0; // 计算输入的科目长度
  console.log('true=' + code_length);
  console.log('false=' + newcode_length);

  // 开启新增下级科目控制，不能提交
  if (super_lowerControl.value) {
    superiorCcode.value=''
    formItems.ccode=''
    createConfirmPop('新增下级科目控制已开启,不能增加下级科目！');
    return false;
  }
  if (formItems.ccode !== '') {
    const sum = await findByOrgCodeNum(orgUnique.value,iyear.value,formItems.ccode);
    var sumname;
    if(superiorCcode.value===''){
      sumname = await findByOrgCodeName(orgUnique.value,iyear.value,formItems.ccodeName);
    }else{
      sumname = await findBySuperCodeName(standardUnique.value,templateID.value,formItems.ccodeName,superiorCcode.value,iyear.value);
    }

    if (sum > 0) {
      createConfirmPop('新增的会计科目编码在系统中重复！不能添加，请修改科目编码后继续');
      return false;
    }
    if (sumname > 0) {
      createConfirmPop('新增的会计科目名称在系统中重复！不能添加，请修改科目名称后继续');
      return false;
    }
  }
  return true;
};
// 根据编码首位查询类型
const findByCclass = () => {
  if(formItems.ccode!==''){
    formItems.cclass=styleName.value.filter(v=>v.order === formItems.ccode.charAt(0))[0].cclass
  }else{formItems.cclass=''}
}
async function handleOk(closeflag) {
  if (formItems.ccodeName === '') {
    createConfirmPop('请填写科目名称!')
    return false;
  }
  if (formItems.ccode === '') {
    createConfirmPop('请填写科目编码!')
    return false;
  }
  if (formItems.cclass === '' || formItems.cclass==undefined) {
    createConfirmPop('请选择科目类型!')
    return false;
  }
  if (formItems.bprogerty === '') {
    createConfirmPop('请选择科目方向!')
    return false;
  }
  if (formItems.bnum==='1') {
    if (formItems.menterage === '') {
      createConfirmPop('新增科目为数量核算科目，计量单位不能为空，请设置计量单位！');
      return false;
    }
  }
  if (formItems.currency==='1') {
    if (formItems.currencyType === '') {
      createConfirmPop('新增科目为外币核算科目，外币名称不能为空，请选择对应的外币档案！');
      return false;
    }
  }
  // 每级的长度
  let jiciLenth:any=[]
  let t:any=0
  jici.value.split('-').forEach((v,index)=>{
    if(parseInt(v)===4||parseInt(v)===3){t=parseInt(v)}else{t+=parseInt(v)}
    jiciLenth.push(t)
  })
  // 下级科目
  if(formItems.ccode.length>4){
    let index= getJiCiIndex(jiciLenth)
    let codeLength=jiciLenth[parseInt(index)-1]
    if(codeLength===undefined){
      createConfirmPop('科目级次不符合！')
      return false;
    }
    // 算出上级科目
    let superiorCcode2=formItems.ccode.substring(0,parseInt(codeLength))
    // 获取上级科目信息
    const superCodeInfo = await findByOrgCcode(superiorCcode2,orgUnique.value,iyear.value);
    if(superCodeInfo==='不存在'){
      createConfirmPop('没有上级科目信息2！')
      return false;
    }
  }

  // 判断 科目类型是否 与选择的一致
  let findByCclass= styleName.value.filter(v=>v.order === formItems.ccode.charAt(0))
  if(findByCclass.length==0 || findByCclass[0].cclass!==formItems.cclass){
    createConfirm({
      iconType: 'warning',
      title: '警告',
      content: '科目类型不一致,确定添加吗',
      onOk: async() => {
        save123(closeflag)
      }
    })
  }
  else{
    save123(closeflag)
  }
}

async function save123(closeflag) {
  // 获取是否预算科目标志
  let yusuanflag=styleName.value.filter((x)=>{
    return x.cclass===formItems.cclass;
  })
  formItems.superiorCcode = superiorCcode.value.split('_')[0];
  formItems.flag = '1';
  formItems.igrade = last_jici.value;
  formItems.uniqueAccStandard = standardUnique.value;
  formItems.templateId = templateID.value;
  formItems.uniqueAccStandardName = standardName.value;
  formItems.templateName = templateName.value;
  formItems.yusuan = yusuanflag[0].flagYusuan==='1'?'1':'0';
  formItems.superCodeBend = superCodeBend.value
  formItems.oldCcode = formItems.ccode
  formItems.closeflag = closeflag
  formItems.orgUnique = orgUnique.value

  let fzccode:any=[]
  if(selectedRowKeys.value.length>0){
    for (let i = 0; i < selectedRowKeys.value.length; i++) {
      fzccode.push(selectedRowKeys.value[i])
    }
  }
  formItems.fuzhu = fzccode.length>0?fzccode.join(','):'';
  if(await ccodeblur()){
    saveClick.value=true
    emit('save', unref(formItems));
    closeModal();
  }
}
function handleClose() {
  activeKey.value='1'
  superCodeBend.value = ''
  // 辅助model
  fuzhuModal.value = false
  // 是否独立账套
  independent.value = false
  // 计算单位list
  unitmealist.value=[]
  // 货币list
  currencylist.value=[]
  // 科目list
  codelist.value=[]
  // 科目类型状态
  cclassflg.value=true
  // 级次
  jici.value=''
  // 级次:增加科目用到-上级科目级次
  jici2.value=1
  // 级次:增加科目用到-下级科目级次
  last_jici.value=1
  // 科目类型
  styleName.value=[]
  selectData.value=[]
  // 本位币
  currency.value='人民币'
  // 上级科目
  superiorCcode.value=''
  lowerControl.value=false
  fuzhuControl.value=false
  // 上级科目-科目下级控制
  super_lowerControl.value=false
  // 业务受控制
  controlled.value=false
  // 表单封装数据
  formItems.ccode = ''
  formItems.ccodeName = ''
  formItems.cclass = ''
  formItems.bprogerty = '1'
  formItems.menterage = ''
  formItems.bnum='0'
  formItems.currency='0'
  formItems.currencyType = ''
  formItems.igrade = ''
  formItems.uniqueAccStandard = ''
  formItems.superiorCcode = ''
  formItems.templateId = ''
  formItems.lowerControl = '0'
  formItems.fuzhuControl = '0'
  formItems.uniqueAccStandardName = ''
  formItems.templateName = ''
  formItems.flag = ''
  formItems.iyear = ''
  formItems.fuzhu = ''
  formItems.yusuan = '0'
  formItems.bdaybook = '0';
  formItems.controlled = '0';
  formItems.pxjz = '0';
  formItems.xjll = '0';
  formItems.cyfx = '0';
  formItems.bcash = '0';
  formItems.bbank = '0';
  formItems.bcashEquivalence = '0';
  selectedRowKeys.value=[]

  // 现金科目
  bcashDisabled.value=false
  // 银行科目
  bbankDisabled.value=false
  // 现金等价物
  bcashEquivalenceDisabled.value=false
  controlledDisabled.value=false
  bdaybookDisabled.value=false
  lowerDisabled.value=false
  fuzhutDisabled.value=false
  pxjzDisabled.value=false
  xjllDisabled.value=false
  cyfxDisabled.value=false
  return true;
}
function tabChange() {
  if(activeKey.value=='2'){
    tableRef.value.$el.style.setProperty('width', '500px')
  }
}
function filterOption(input, option) {
  return option.title.toLowerCase().indexOf(input.toLowerCase()) >= 0;
}
</script>
<style>
.ant-modal-body {
  padding: 0px;
  border-bottom: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
  border-top: none !important;
}
.ant-modal-header{
  border: none !important;
}
</style>
<style lang="less" scoped>
.red_span {
  color: red;
  font-weight: bold;
  display: inline-block;
  width: 20px;
  text-align: right;
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

.nc-open-content {
  input {
    width: 35%;
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
  }

  label {
    padding: 5px 30px;
    color: #535353;
    font-size: 13px;
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
.ant-modal-content {
  margin-top: 200px;
}
:deep(.ant-checkbox){
  border: 1px solid #2f2a2a
}
.nc-border-div {
  position: relative;
  border: 1px solid #4949496b;
  margin: 2% 0;
  width: 83%;

  .nc-border-div-span {
    min-width: 140px;
    background-color: white;
    position: absolute;
    top: -12px;
    left: 50px;
    display: block;
    text-align: center;
    color: black;
    font-weight: bold;
  }

  .nc-border-div-content {
    padding: 10px;
    min-height: 40px;
  }
}

.divtop{
  margin-top: 10px;
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

.tablePointer {
  pointer-events: none;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
}
</style>
