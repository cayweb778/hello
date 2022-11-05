<template>
  <BasicModal
    destroyOnClose
    width="800px"
    v-bind="$attrs"
    okText="保存"
    title="会计科目"
    @cancel="closeModal(),resetFormValue()"
    @register="register"
  >
    <template #title>
      <div style="display: flex;color: #0096c7;margin-top: 5px;margin-left: 10px;">
        <span style="line-height:60px;font-size: 28px;">
          <FormOutlined style="font-size: 34px;font-weight: bold"/>
        </span>
        <span style="line-height:40px;font-size: 28px;margin-top:5px;">
          &nbsp;&nbsp;会计科目
        </span>
        <label style="margin-left: 15%;color: black;font-size: 14px;margin-top:15px;">编码级次规则：{{jici}}</label>
      </div>
      <div style="display: inline-block;position:absolute;right: 40px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="/cus.png" style="height:90px;margin-right: 55px;"/>
      </div>
    </template>
    <div class="nc-open-content">
      <div class="open-content-up">
        <div style="margin-left: 20px;margin-top: 53px;">
          <a-radio-group v-model:value="formItems.bprogerty" v-model:disabled="bprogertyDisabled" style="width:40%;">
            <a-radio value="1">
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
            v-model:value="superiorCcode"
            disabled
          >
            <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
          </TreeSelect>
          <br/>
          <label>科目编码：</label>
          <a-input
            @blur="findByCodeNum(formItems.ccode)"
            v-model:value.trim="formItems.ccode"
            v-model:disabled="bprogertyDisabled"
            autocomplete="off"
            style="width: 25%;"
            onkeyup="value=value.replace(/o/,'').replace(/l/,'').replace(/[\u4e00-\u9fa5]/, '')"
          />
          <label>科目类型：</label>
          <a-select
            v-model:value="formItems.cclass"
            v-model:disabled="bprogertyDisabled"
            style="width: 24%;text-align: center;font-weight: bold;"
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
            <a-input v-model:value.trim="formItems.ccodeName" v-model:disabled="bprogertyDisabled" @blur="findByCodeName(formItems.ccodeName)" placeholder="科目名称" autocomplete="off" class="abc" style="font-size: 18px;width: 60%;" />
            <span class="red_span">*</span>
          </span>
        </div>
      </div>
      <div class="open-content-down">
        <a-tabs v-model:activeKey="activeKey" type="card">
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
          <a-tab-pane key="2" tab="辅助核算">
            <div class="down-tab-content">
              &emsp;
              <a style="color: black;font-size: 18px;" v-show="pzqc=='0'" @click="fuzhuAdd"><PlusCircleTwoTone /></a>
              &emsp;
              <a style="color: black;font-size: 18px;" v-show="pzqc=='0'" @click="fuzhuDel"><DeleteTwoTone /></a>
              <br><br>
              <p style="font-weight: bold;margin-left: 2%;">辅助项:
                &emsp;
                <span v-if="fuzhuList.length>0">
                  {{ fuzhuList.map(a=>a.cname).join(',') }}
                </span>
              </p>
            </div>
          </a-tab-pane>
          <a-tab-pane key="3" tab="外币核算">
            <div style="margin-top: 20px;margin-left: 25%;" class="down-tab-content">
              <label>外币币种：</label>
              <a-select
                v-model:value="formItems.currencyType"
                v-model:disabled="currencyTypeflg"
                @change="currencychecked"
                show-search
                style="width: 25%;text-align: center;"
                allow-clear
              >
                <a-select-option value="">无</a-select-option>
                <a-select-option :value="item.foreignName" v-for="(item, i) in currencylist">
                  {{ item.foreignName }}
                </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
              </a-select>
            </div>
          </a-tab-pane>
          <a-tab-pane key="4" tab="数量核算">
            <div style="margin-top: 20px;margin-left: 25%;" class="down-tab-content">
              <label>计量单位</label>
              <a-select
                v-model:value="formItems.menterage"
                v-model:disabled="menterageflg"
                show-search
                @change="bnumchecked"
                style="width: 25%;text-align: center;"
                allow-clear
              >
                <a-select-option value="">无</a-select-option>
                <a-select-option :key="item.unitCode" :value="item.unitCode" v-for="(item, i) in unitMeaList">
                  {{ item.unitName }}
                </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
              </a-select>
            </div>
          </a-tab-pane>
          <a-tab-pane key="5" tab="集团账套" v-if="!independent">
            <div style="margin-top: 20px;" class="down-tab-content">
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
            </div>
          </a-tab-pane>
          <a-tab-pane key="6" tab="预算会计" v-if="ibudgetAccounting=='1'">
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

    <FuzhuPop @throwData="throwFuzhu" @register="registerFuzhuPopPage" />

    <template #footer>
      <a-button @click="closeModal(),resetFormValue()" >取消</a-button>
      <a-button :disabled="saveClick" @click="handleOk()">保存</a-button>
    </template>
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
import { reactive, ref, toRaw, watch } from 'vue';
import {BasicModal, useModal, useModalInner} from '/@/components/Modal';
import { FormOutlined,CaretDownOutlined,LinkOutlined,PlusCircleTwoTone,DeleteTwoTone} from '@ant-design/icons-vue'
import {
  company_findByCodeNum,
  findAllByCurrency,
  company_findByLowerMaxCodeNum,
  company_findByCcode,
  company_treeCode,
  company_findByCodeName,
  company_findBySuperCodeName, company_delFindByCodekemu,
} from '/@/api/codekemu/codekemu';
import {
  Row as ARow,
  Col as ACol,
  Tabs as ATabs,
  Select as ASelect,
  Input as AInput,
  Statistic as AStatistic,
  Checkbox as ACheckbox,
  CheckboxGroup as ACheckboxGroup,
  Table as ATable,
  Radio as ARadio,
  TreeSelect,message,Modal as AModal
} from 'ant-design-vue';
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import { findFuzhuHesuanList } from "/@/api/record/system/fuzhu-hesuan";
import {findAll as findAllSingle} from '/@/api/record/system/unit-mea'
import {useMessage} from "/@/hooks/web/useMessage";
import ModalAllPop from '/@/views/boozsoft/system/acccode2/popup/modalAllPop.vue';
import FuzhuPop from '/@/views/boozsoft/system/acccode2/popup/fuzhuPop.vue';

const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const AStatisticCountdown = AStatistic.Countdown;
const ATabPane = ATabs.TabPane;
const ARadioGroup = ARadio.Group
const emit=defineEmits(['register','save']);
// ======================= 参数 ====================
const use =ref('') // 是否做过凭证、期初
const jici =ref('')
const iyear =ref('')
const superiorCcode =ref('')
const database = ref('');
const activeKey = ref('1')
const pzqc = ref('0')                           // 科目是否做过凭证、期初
const independent = ref(false)                    // 是否独立账套
const ibudgetAccounting: any = ref('');           // 账套是否预算会计 1是 0否
const super_lowerControl: any = ref(false);       // 上级科目-科目下级控制
const last_jici: any = ref(1);                    // 级次:增加科目用到-下级科目级次
const jici2: any = ref(1);                        // 级次:增加科目用到-上级科目级次
const standardUnique: any = ref('');
const templateID: any = ref('');
const superCodeBend: any = ref('');
const formItems:any =ref({})
const codelist:any =ref([])
const styleName:any =ref([])
const currencylist:any =ref([])
const unitMeaList:any =ref([])
const fuzhuList:any =ref([])
// ======================= 禁用状态 ====================
const bprogertyDisabled:any =ref(false)
const bcashDisabled = ref(false);
const bbankDisabled = ref(false);
const bcashEquivalenceDisabled = ref(false);
const xjllDisabled = ref(false);
const bdaybookDisabled = ref(false);
const controlledDisabled = ref(false);
const saveClick:any =ref(false)
const currencyTypeflg:any =ref(false)
const menterageflg:any =ref(false)
const lowerDisabled:any =ref(false)
const fuzhutDisabled:any =ref(false)
const pxjzDisabled:any =ref(false)
const cyfxDisabled:any =ref(false)

const {createConfirm, createWarningModal, createMessage} = useMessage();
const [registerCodeAllPopPage, { openModal: openCodeAllPopPage }] = useModal();
const [registerFuzhuPopPage, { openModal: openFuzhuPopPage }] = useModal();

const [register, { closeModal }] = useModalInner(async (data) => {
  independent.value=data.independent
  ibudgetAccounting.value=data.ibudgetAccounting
  pzqc.value=data.use

  jici.value=data.data.jici
  iyear.value=data.data.iyear
  database.value=data.data.database
  standardUnique.value = data.data.standardUnique;
  templateID.value = data.data.templateID;
  styleName.value = data.data.styleName.filter(v=>v.cclass!=='全部');
  formItems.value=JSON.parse(data.superiorData)
  superiorCcode.value=JSON.parse(data.superiorData).superiorCcode!='0'?JSON.parse(data.superiorData).superiorCcode:''

  const fuzhu=await useRouteApi(findFuzhuHesuanList,{schemaName:database.value})('')
  // 获取上级辅助信息
  if (JSON.parse(data.superiorData).fuzhu!==null && JSON.parse(data.superiorData).fuzhu !== '') {
    fuzhuList.value=[]
    for (let i = 0; i < JSON.parse(data.superiorData).fuzhu.split(',').length; i++) {
      let temp=fuzhu.items.filter(a=>a.cname==JSON.parse(data.superiorData).fuzhu.split(',')[i])
      if(temp.length>0){
        fuzhuList.value.push(temp[0])
      }
    }
  }
  if(data.use=='1'){
    bprogertyDisabled.value=true
    currencyTypeflg.value=true
    menterageflg.value=true
    lowerDisabled.value=true
    fuzhutDisabled.value=true
  }
  getAllCurrency()
  getSingleUnit()
});

// 重置表单值
function resetFormValue() {
  pzqc.value='0'
  bprogertyDisabled.value=false
  currencyTypeflg.value=false
  menterageflg.value=false
  lowerDisabled.value=false
  fuzhutDisabled.value=false

  superiorCcode.value=''
  fuzhuList.value=[]
  formItems.value={}
  // 新增时默认值
  formItems.value.bprogerty='1'
  formItems.value.bcash='0'
  formItems.value.bbank='0'
  formItems.value.bcashEquivalence='0'
  formItems.value.xjll='0'
  formItems.value.bdaybook='0'
  formItems.value.controlled='0'
  formItems.value.lowerControl='0'
  formItems.value.fuzhutDisabled='0'
  formItems.value.pxjz='0'
  formItems.value.cyfx='0'
  formItems.value.currencyType=''
  formItems.value.menterage=''
}
// 获取所有国币-读取常用外币表
const getAllCurrency = async () => {
  await useRouteApi(findAllByCurrency,{schemaName:database})('').then((res) => {
    currencylist.value = res;
  });
}
// 获取单计量-计量单位
const getSingleUnit = async () => {
  unitMeaList.value=await useRouteApi(findAllSingle,{schemaName:database})('')
}
// 校验科目编码
const findByCodeNum = async (val) => {
  let codeFirstLevelLength=jici.value.split('-')[0]
  if(val.length!==parseInt(codeFirstLevelLength)){
    saveClick.value=true
    return message.error('科目级次不符合')
  }else{
    const sum = await useRouteApi(company_findByCodeNum,{schemaName:database})({ccode: formItems.value.ccode,iyear: iyear.value});
    if(sum>0){
      saveClick.value=true
      return message.error('科目编码在系统中已存在')
    }
    formItems.value.cclass=styleName.value.filter(v=>v.order === formItems.value.ccode.charAt(0))[0]?.cclass
    if(formItems.value.cclass==undefined){
      saveClick.value=true
      return message.error('系统中没有对应的科目类型！')
    }
    saveClick.value=false
  }

}
// 检验科目名称
const findByCodeName = async (val) => {
  let sum=await useRouteApi(company_findByCodeName,{schemaName:database})({ccodename: val,iyear: iyear.value});
  if(sum>0){
    saveClick.value=true
    return message.error('科目名称在系统中已存在')
  }else{
    saveClick.value=false
  }
}
const findByLastCodeName = async (val) => {
  let sum=await useRouteApi(company_findBySuperCodeName,{schemaName:database})({ccodename: val,iyear: iyear.value,superCcod:superiorCcode.value});
  if(sum>0){
    saveClick.value=true
    return message.error('科目名称在系统中已存在')
  }else{
    saveClick.value=false
  }
}
// 选中现金
const bcashchecked = (val: any) => {
  formItems.value.bdaybook=val.target.value
  bbankDisabled.value = val.target.value=='1';
  bcashEquivalenceDisabled.value = val.target.value=='1';
  bdaybookDisabled.value=val.target.value=='1';
};
// 选中银行
const bbankchecked = (val: any) => {
  formItems.value.bdaybook=val.target.value
  bcashDisabled.value = val.target.value=='1';
  bcashEquivalenceDisabled.value = val.target.value=='1';
  bdaybookDisabled.value=val.target.value=='1';
};
// 选中现金等价物
const bcashEquivalencechecked = (val) => {
  formItems.value.bdaybook=val.target.value
  bbankDisabled.value = val.target.value=='1';
  bcashDisabled.value = val.target.value=='1';
  bdaybookDisabled.value=val.target.value=='1';
};
// 选中外币核算
const currencychecked = (val: any) => {
  if(val!==undefined){
    formItems.value.currency='1'
  }
};
// 选中数量核算单位
const bnumchecked = async (val: any) => {
  if(val!==undefined){
    formItems.value.bnum='1'
  }
};
// 辅助项增加
const fuzhuAdd = () => {
  getAllFuzhu('add')
}
// 辅助项删除
const fuzhuDel = () => {
  getAllFuzhu('del')
}
const getAllFuzhu = async (btnType) => {
  const fuzhu=await useRouteApi(findFuzhuHesuanList,{schemaName:database.value})('')
  openFuzhuPopPage(true, {
    tableData: btnType=='add'?fuzhu.items:fuzhuList.value,
    fuzhuList: fuzhuList.value,
    btnType:btnType
  });
}
// 辅助回调
const throwFuzhu = (val) => {
  let data=val.data
  let btnType=val.btnType
  if(btnType=='del' || data.length>0){
    fuzhuList.value=[]
  }
  for (let i = 0; i < data.length; i++) {
    fuzhuList.value.push(data[i])
  }
}

const handleOk = () => {
  // 获取是否预算科目标志
  let yusuanflag=styleName.value.filter((x)=>{
    return x.cclass===formItems.value.cclass;
  })
  formItems.value.yusuan=yusuanflag[0].flagYusuan==='1'?'1':'0';
  formItems.value.flag = '1';
  formItems.value.igrade = last_jici.value
  formItems.value.iyear = iyear.value
  formItems.value.superiorCcode = superiorCcode.value
  formItems.value.superCodeBend = superCodeBend.value
  formItems.value.uniqueAccStandard = standardUnique.value;
  formItems.value.templateId = templateID.value;
  formItems.value.lowerControl = formItems.value.lowerControl === true ? '1' : '0';
  formItems.value.fuzhuControl = formItems.value.lowerControl === true ? '1' : '0';
  formItems.value.yusuan = yusuanflag[0].flagYusuan==='1'?'1':'0';
  formItems.value.fuzhuhesuan = fuzhuList.value.length>0?fuzhuList.value.map(a=>a.cfield).join(','):'';
  let info=toRaw(formItems.value)
  saveClick.value=true
  emit('save', info);
  saveClick.value=false
  closeModal();
}
</script>
<style lang="less" scoped>
.red_span {
  color: red;
  font-weight: bold;
  display: inline-block;
  width: 20px;
  text-align: right;
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
    padding: 5px 35px;
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
      background-color: #0096c7;
    }
  }
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

:deep(.ant-tabs-content-holder){
  margin-top: -9px;
}
</style>
