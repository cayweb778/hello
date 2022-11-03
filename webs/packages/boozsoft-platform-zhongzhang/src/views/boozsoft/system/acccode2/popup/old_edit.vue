<!--2022年9月29日11:45:26 备份-->
<template>
  <BasicModal
    destroyOnClose
    width="800px"
    v-bind="$attrs"
    okText="保存"
    title="会计科目"
    @ok="handleOk()"
    @cancel="handleClose()"
    @register="register"
  >
    <template #title>
      <div style="display: flex;color: #0096c7;margin-top: 5px;">
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
            @change="findByCclass"
            v-model:value.trim="formItems.ccode"
            v-model:disabled="ccodeDisabled"
            autocomplete="off"
            style="width: 25%;"
            onkeyup="value=value.replace(/o/,'').replace(/l/,'').replace(/[\u4e00-\u9fa5]/, '')"
          />
          <label>科目类型：</label>
          <a-select
            v-model:value="formItems.cclass"
            v-model:disabled="cclassflg"
            style="width: 25%;text-align: center;"
            allow-clear
          >
            <a-select-option :value="item.cclass" v-for="(item, i) in styleName">{{ item.cclass }}</a-select-option>
            <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
          </a-select>
          <br/>
          <label>启用状态：</label>
          <a-select
            v-model:value="codeflag"
            style="width: 25%;text-align: center;"
            allow-clear
          >
            <a-select-option value="1">是</a-select-option>
            <a-select-option value="0">否</a-select-option>
            <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
          </a-select>
          <br/><br/>
          <span style="margin-left: 50px;">
            <label style="font-size: 18px;margin-left: 0;width:150px;">科目名称：</label>
            <a-input v-model:value.trim="formItems.ccodeName" v-model:disabled="ccodeDisabled" placeholder="科目名称" autocomplete="off" class="abc" style="font-size: 18px;width: 60%;" />
              <span class="red_span">*</span>
          </span>
          <br/>
        </div>
      </div>
      <div class="open-content-down">
        <a-tabs v-model:activeKey="activeKey" type="card">
          <a-tab-pane key="1" tab="科目属性">
            <div class="down-tab-content">
              <a-radio-group v-model:value="formItems.bcash" v-model:disabled="bcashflg" @change="bcashchecked" style="width: 50%">
                <label>现金科目：&emsp;&emsp;&emsp;</label>
                <a-radio value="0" style="width:80px;margin-left: -10px;">
                  否
                </a-radio>
                <a-radio value="1" style="width:80px;margin-left: -10px;">
                  是
                </a-radio>
              </a-radio-group>
              <a-radio-group v-model:value="formItems.bbank" v-model:disabled="bbankflg" @change="bbankchecked" style="width: 50%">
                <label>银行科目：&emsp;&emsp;&emsp;</label>
                <a-radio value="0" style="width:80px;margin-left: -10px;">
                  否
                </a-radio>
                <a-radio value="1" style="width:80px;margin-left: -10px;">
                  是
                </a-radio>
              </a-radio-group>
              <br/>
              <a-radio-group v-model:value="formItems.bcashEquivalence" v-model:disabled="bcashEquivalenceflg" @change="bcashEquivalencechecked" style="width: 50%">
                <label>现金等价物科目：</label>
                <a-radio value="0" style="width:80px;margin-left: -10px;">
                  否
                </a-radio>
                <a-radio value="1" style="width:80px;margin-left: -10px;">
                  是
                </a-radio>
              </a-radio-group>
              <a-radio-group v-model:value="formItems.xjll" style="width: 50%">
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
              <a-table
                ref="tableRef"
                :pagination="false"
                :class="'a-table-font-size-12'"
                :dataSource="fuzhuModalDataSource"
                :columns="fuzhuModalColumns"
                :row-selection="{selectedRowKeys:selectedRowKeys, onChange: onFuzhuHeSuanChange }"
                :scroll="{ y: 50 }"
              />
            </div>
          </a-tab-pane>
          <a-tab-pane key="3" tab="外币核算">
            <div style="margin-top: 20px;margin-left: 25%;" class="down-tab-content">
              <label>外币币种：</label>
              <a-select
                v-model:value="formItems.currencyType"
                v-model:disabled="currencyTypeflg"
                show-search
                @change="currencychecked"
                placeholder="请选择"
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
                placeholder="请选择"
                style="width: 25%;text-align: center;"
                allow-clear
              >
                <a-select-option value="">无</a-select-option>
                <a-select-option :value="item" v-for="(item, i) in unit_meaList">{{ item }}</a-select-option>
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

    <a-modal v-model:visible="visibleModel" :closable="false" @ok="handleOk2" style="margin-top: 50px;">
      <p style="color: #0090bf;font-size: 16px;font-weight: bold;margin-left: 10px;">辅助项</p>
      <div style="margin-top: 10px;margin-left: 20px;" v-for="(item,index) in fuzhulist">
        <label>{{ item.title }}：</label>
        <a-select
          v-model:value="item.changeVal"
          show-search
          @change="currencychecked"
          placeholder="请选择"
          style="width: 65%;"
          allow-clear
        >
          <a-select-option :value="data.value" v-for="(data, i) in item.value">
            {{ data.title }}
          </a-select-option>
          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
        </a-select>
        &nbsp;
        <a style="font-weight: bold;font-size: 18px;" v-if="item.cfield=='cperson_id'"><LinkOutlined @click="openSelect('cperson_id')" /></a>
        <a style="font-weight: bold;font-size: 18px;" v-if="item.cfield=='cdept_id'"><LinkOutlined @click="openSelect('cdept_id')" /></a>
        <a style="font-weight: bold;font-size: 18px;" v-if="item.cfield=='ccus_id'"><LinkOutlined @click="openSelect('ccus_id')" /></a>
        <a style="font-weight: bold;font-size: 18px;" v-if="item.cfield=='csup_id'"><LinkOutlined @click="openSelect('csup_id')" /></a>
        <a style="font-weight: bold;font-size: 18px;" v-if="item.cfield=='project_id'"><LinkOutlined @click="openSelect('project_id')" /></a>
        <a style="font-weight: bold;font-size: 18px;" v-if="item.cfield=='ys_ysly'"><LinkOutlined @click="openSelect('ys_ysly')" /></a>
        <a style="font-weight: bold;font-size: 18px;" v-if="item.cfield=='ys_zcgnfl'"><LinkOutlined @click="openSelect('ys_zcgnfl')" /></a>
        <a style="font-weight: bold;font-size: 18px;" v-if="item.cfield=='ys_bmzcjjfl'"><LinkOutlined @click="openSelect('ys_bmzcjjfl')" /></a>
        <a style="font-weight: bold;font-size: 18px;" v-if="item.cfield=='ys_zfzcjjfl'"><LinkOutlined @click="openSelect('ys_zfzcjjfl')" /></a>
      </div>
      <br>

      <SelectPsn @save="saveSelectPsn" @register="registerSelectPsnPage"/>
      <SelectDept @save="saveSelectDept" @register="registerSelectDeptPage"/>
      <CusModalPop @throwData="throwCusData" @register="registerCusModalPopPage"/>
      <SupModalPop @throwData="throwSupData" @register="registerSupModalPopPage"/>
      <SelectProject @save="saveSelectProject" @register="registerSelectProjectPage"/>
      <YslyModalPop @throwData="YslyData" @register="registerYslyModalPopPage" />
      <ZcgnModalPop @throwData="ZcgnData" @register="registerZcgnModalPopPage" />
      <BmzcjjflModalPop @throwData="BmzcjjflData" @register="registerBmzcjjflModalPopPage" />
      <ZfzcjjflModalPop @throwData="ZfzcjjflData" @register="registerZfzcjjflModalPopPage" />
    </a-modal>
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
import {CaretDownOutlined, FormOutlined, LinkOutlined} from '@ant-design/icons-vue'
import {reactive, ref, toRaw} from 'vue';
import {BasicModal, useModal, useModalInner} from '/@/components/Modal';
import {
  company_auditCodeKemuSave,
  company_delFindByCodekemu,
  company_executeAccvoucher,
  company_executeSQL,
  company_findByCcode,
  company_findByCodeAccVoucher,
  company_findByCodeName,
  company_findByCodeNum,
  company_findByLowerMaxCodeNum,
  company_findBySuperCodeName,
  company_treeCode,
  findAllByCurrency,
} from '/@/api/codekemu/codekemu';
import {
  Input as AInput,
  Modal as AModal,
  Radio as ARadio,
  Select as ASelect,
  Statistic as AStatistic,
  Table as ATable,
  Tabs as ATabs,
  TreeSelect
} from 'ant-design-vue';
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findFuzhuHesuanList, getFuzhuHesuanList} from "/@/api/record/system/fuzhu-hesuan";
import {findAllByUnitName} from "/@/api/record/system/unit-mea";
import {useMessage} from "/@/hooks/web/useMessage";
import SelectPsn from '/@/views/boozsoft/system/department/popup/select-psn.vue'
import SelectDept from '/@/views/boozsoft/system/department/popup/select-dept.vue'
import CusModalPop from '/@/views/boozsoft/system/customer_info/popup/modalPop.vue'
import SupModalPop from '/@/views/boozsoft/system/supplier/popup/modalPop.vue'
import SelectProject from '/@/views/boozsoft/system/project/popup/select-project.vue'
import YslyModalPop from '/@/views/boozsoft/system/budget-source/popup/modalPop2.vue';
import ZcgnModalPop from '/@/views/boozsoft/system/expenditure-class/popup/modalPop2.vue';
import BmzcjjflModalPop from '/@/views/boozsoft/system/dept-class/popup/modalPop2.vue';
import ZfzcjjflModalPop from '/@/views/boozsoft/system/zf-class/popup/modalPop2.vue';
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {saveLog} from "/@/api/record/system/group-sys-login-log";

const [registerSelectPsnPage, { openModal: openSelectPsnPage }] = useModal()
const [registerSelectDeptPage, { openModal: openSelectDeptPage }] = useModal()
const [registerCusModalPopPage, { openModal: openCusMoalPopPage }] = useModal();
const [registerSupModalPopPage, { openModal: openSupMoalPopPage }] = useModal();
const [registerSelectProjectPage, { openModal: openSelectProjectPage }] = useModal()
const [registerYslyModalPopPage, { openModal: openYslyMoalPopPage }] = useModal();
const [registerZcgnModalPopPage, { openModal: openZcgnMoalPopPage }] = useModal();
const [registerBmzcjjflModalPopPage, { openModal: openBmzcjjflMoalPopPage }] = useModal();
const [registerZfzcjjflModalPopPage, { openModal: openZfzcjjflMoalPopPage }] = useModal();

const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const AStatisticCountdown = AStatistic.Countdown;
const ATabPane = ATabs.TabPane;
const ARadioGroup = ARadio.Group
const emit = defineEmits(['register','save']);
const {createConfirm, createWarningModal, createMessage} = useMessage();

const visibleModelSelectValue = ref('');
const visibleModel = ref(false);
const ccodeDisabled = ref(false);
const bprogertyDisabled = ref(false);
const controlledDisabled = ref(false);
const pxjzDisabled = ref(false);
const cyfxDisabled = ref(false);
const bdaybookDisabled = ref(false);
const currencyDisabled = ref(false);
const bnumDisabled = ref(false);
const lowerDisabled = ref(false);
const fuzhutDisabled = ref(false);


const fuzhtype: any = ref('');
const fuzhulist: any = ref([]);
// 是否独立账套
const independent = ref(false);
// 计算单位list
const unitmealist: any = ref([]);
// 货币list
const currencylist: any = ref([]);
// 科目list
const codelist: any = ref([]);
const unit_meaList: any = ref([]);
// 计量单位状态
const menterageflg: any = ref(false);
// 科目类型状态
const cclassflg: any = ref(true);
// 币种状态
const currencyTypeflg: any = ref(false);
// 会计准则
const standardName: any = ref('');
const standardUnique: any = ref('');
const templateID: any = ref('');
const templateName: any = ref('');
// 级次
const jici: any = ref('');
const superCodeBend: any = ref('');
// 级次:增加科目用到-上级科目级次
const jici2: any = ref(1);
// 级次:增加科目用到-下级科目级次
const last_jici: any = ref(1);
// 科目类型
const styleName: any = ref([]);
// 本位币
const currency: any = ref('人民币');
// 上级科目
const superiorTreeFlag: any = ref(true);
const superiorCcode: any = ref('');
const lowerControl: any = ref(true);
const fuzhuControl: any = ref(true);
// 上级科目-科目下级控制
const super_lowerControl: any = ref(false);
// 封存状态
const codeflag: any = ref('1');
// 表单封装数据
const formItems = reactive({
  index: '',
  key: '',
  id: '',
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
  bperson: '0',
  bcus: '0',
  bsup: '0',
  bdept: '0',
  bitem: '0',
  iyear: '',
  flag: '',
  pxjz: '0',
  xjll: '0',
  cyfx: '0',
  fuzhuhesuan: '',
  fuzhuhesuanName: '',
  yusuan: '',
  tenantId: '',
  oldCcode: '',
  listmap: [],
  superCodeBend:''
});
// 没修改前表单封装数据
let oldformItems = reactive({
  index: '',
  key: '',
  id: '',
  controlled: '0',
  ccode: '',
  ccodeName: '',
  cclass: '',
  bprogerty: '',
  bnum: '',
  menterage: '',
  currency: '',
  currencyType: '',
  bcash: '',
  bbank: '',
  bcashEquivalence: '',
  bdaybook: '0',
  igrade: '',
  uniqueAccStandard: '',
  superiorCcode: '',
  templateId: '',
  lowerControl: '',
  fuzhuControl: '',
  uniqueAccStandardName: '',
  templateName: '',
  bend: '1',
  bperson: '',
  bcus: '',
  bsup: '',
  bdept: '',
  bitem: '',
  iyear: '',
  flag: '',
  pxjz: '',
  xjll: '',
  cyfx: '',
  fuzhuhesuan: '',
  fuzhuhesuanName: '',
  yusuan: '',
  oldCcode: '',
  superCodeBend: '',
  optMethod: '',  // 操作类型：1删除、0修改
  optUsername: '',
  optTime: '',
});
// 现金科目
const bcashflg: any = ref(false);
// 银行科目
const bbankflg: any = ref(false);
// 现金等价物
const bcashEquivalenceflg: any = ref(false);

const pageParameter = reactive({
  cclass: '全部',
  databasenum: '',
  showRulesSize: 'MIN',
  queryMark: 'J',
  searchConditon: {
    requirement: 'ccode',
    value: '',
  },
  uniqueAccStandard: '',
  templateId: '',
  activeKey: '',
  reloadMark: false,
  companyCode: '',
  companyName: '',
  ifUnit: false,
  total: 0,
  iyear: '',
});
// 数据库模式名称
const database = ref('');
const accId = ref('');
const iyear: any = ref('');
// 账套是否预算会计
const ibudgetAccounting: any = ref('');
const isEdit: any = ref(true);
// tab默认
const activeKey = ref('1');
const selectedRowKeys = ref([]);
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
const fuzhuModalDataSource = ref([]);
const fuzhuModalColumns = ref([
  {
    title: '辅助项',
    dataIndex: 'cname',
    key: 'cname',
    // width:'20%',
    // colSpan: 2,
    // customRender: ({ text, index }) => {
    //   const obj = {
    //     children: text,
    //     props: {} as any,
    //   };
    //   obj.props.colSpan = 2;
    //   return obj;
    // },
  }
]);
const selectData = ref([])
// 辅助model
const fuzhuModal = ref(false);
// 确认选中
let affirm_checked:any=ref([])
// 取消选中
let cancel_checked:any=ref([])
const tableRef = ref(null)
// 五个辅助项是否修改
const verifyDeptChecked = ref(false);
const verifyCusChecked = ref(false);
const verifySupChecked = ref(false);
const verifyItemChecked = ref(false);
const verifyPsnChecked = ref(false);
// 五个辅助项弹框model
const deptModal = ref(false);
const cusModal = ref(false);
const supModal = ref(false);
const psnModal = ref(false);
const itemModal = ref(false);
// 五个辅助项list
const deptlist = ref([]);
const cuslist = ref([]);
const suplist = ref([]);
const psnlist = ref([]);
const itemlist = ref([]);
// 五个辅助项list
const deptvalue = ref('');
const cusvalue = ref('');
const supvalue = ref('');
const psnvalue = ref('');
const itemvalue = ref('');
const accvoucherlist = ref([]);
// 修改前的辅助项
const oldFz:any = ref([]);
const [register, {closeModal}] = useModalInner(async (data) => {
  handleClose()
  iyear.value = data.data.iyear;
  jici.value = data.data.jici;
  independent.value = data.data.independent;
  standardName.value = data.data.standardName;
  standardUnique.value = data.data.standardUnique;
  templateID.value = data.data.templateID;
  templateName.value = data.data.templateName;
  styleName.value = data.data.styleName.filter(v=>v.cclass!=='全部');
  superiorCcode.value = data.data.data.superiorCcode === '0' ? '' : data.data.data.superiorCcode;
  database.value = data.data.database;
  accId.value = data.data.accId;
  ibudgetAccounting.value = data.ibudgetAccounting;
  pageParameter.iyear = data.pageParameter.iyear;
  pageParameter.companyCode = data.pageParameter.companyCode;
  pageParameter.companyName = data.pageParameter.companyName;
  codeflag.value = data.data.data.flag;

  formItems.index = data.data.data.index;
  formItems.key = data.data.data.key;
  formItems.flag = data.data.data.flag;
  formItems.id = data.data.data.id;
  formItems.ccode = data.data.data.ccode;
  formItems.oldCcode = data.data.data.ccode;
  formItems.ccodeName = data.data.data.ccodeName;
  formItems.menterage = data.data.data.menterage;
  formItems.currencyType = data.data.data.currencyType;
  formItems.bdaybook = data.data.data.bdaybook!=='1'?'0':'1'
  formItems.controlled = data.data.data.controlled!=='1'?'0':'1'
  formItems.bnum = data.data.data.bnum;
  formItems.currency = data.data.data.currency;
  formItems.bprogerty = data.data.data.bprogerty;
  formItems.cclass = data.data.data.cclass;
  formItems.bend = data.data.data.bend;
  formItems.iyear = data.data.data.iyear;
  formItems.tenantId = data.data.data.tenantId;
  formItems.pxjz = data.data.data.pxjz;
  formItems.xjll = data.data.data.xjll;
  formItems.cyfx = data.data.data.cyfx;
  formItems.superCodeBend = data.data.data.superCodeBend;
  formItems.lowerControl = data.data.data.lowerControl;
  formItems.fuzhuControl = data.data.data.fuzhuControl;

  // 现金...3个
  formItems.bcash = data.data.data.bcash;
  formItems.bbank = data.data.data.bbank;
  formItems.bcashEquivalence = data.data.data.bcashEquivalence;
  last_jici.value = data.data.data.igrade;
  isEdit.value = data.isEdit;
  oldformItems=data.data.data

  // 集团账套不能修改 .....
  if(!independent.value){
    bprogertyDisabled.value=true
    currencyTypeflg.value=true
    menterageflg.value=true
    lowerDisabled.value=true
    fuzhutDisabled.value=true
    ccodeDisabled.value=true
  }

  // 现金...3个
  if(data.data.data.bcash !== '0' && data.data.data.bcash !== null){
    bbankflg.value=true
    bcashEquivalenceflg.value=true
    bdaybookDisabled.value=true
  }else if(data.data.data.bbank !== '0' && data.data.data.bbank !== null){
    bcashflg.value=true
    bcashEquivalenceflg.value=true
    bdaybookDisabled.value=true
  }else if(data.data.data.bcashEquivalence !== '0' && data.data.data.bcashEquivalence !== null){
    bcashflg.value=true
    bbankflg.value=true
    bdaybookDisabled.value=true
  }

  await useRouteApi(company_treeCode, {schemaName: database.value})({
    iyear: iyear.value,
  }).then((res) => {
    codelist.value = res;
  });

  // 计量单位
  unit_meaList.value=await useRouteApi(findAllByUnitName,{schemaName:database.value})('')

  // 获取所有国币-读取常用外币表
  await useRouteApi(findAllByCurrency, {schemaName: database.value})('').then((res) => {
    currencylist.value = res;
  });

  // 5个常用辅助核算 和 自定义辅助核算
  await fuzhuModalShow()

  if (data.data.data.fuzhu !== '') {
    for (let i = 0; i < data.data.data.fuzhu.split(',').length; i++) {
      let list = fuzhuModalDataSource.value.filter(item => item.cname == data.data.data.fuzhu.split(',')[i]);
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
});

// 选中数量核算单位
const treeselected = async (newValue: any) => {
  // 获取最新科目
  const ccode = newValue === undefined ? newValue : newValue;
  // 获取上级科目信息
  const code = await useRouteApi(company_findByCcode, {schemaName: database})({ccode: ccode,iyear:iyear.value});
  const igrade = code.igrade;
  // 开启下级控制
  super_lowerControl.value = code.lowerControl === '1';
  if (code.lowerControl === '1') {
    formItems.ccode=''
    createConfirmPop('新增下级科目控制已开启,不能增加下级科目！');
    return false;
  }

  formItems.cclass = code.cclass;
  formItems.bprogerty = code.bprogerty;
  cclassflg.value = true;

  last_jici.value = newValue !== undefined ? parseInt(igrade) + 1 : 1;
  const arr = jici.value.replaceAll('-', '').substring(0, last_jici.value);
  var code_length = 0; // 符合规则的科目长度
  for (let i = parseInt(igrade); i < arr.length; i++) {
    code_length += parseInt(arr[i]);
  }

  // 编码_级次
  if (newValue !== undefined) {
    const newccode = await useRouteApi(company_findByLowerMaxCodeNum, {schemaName: database})({
      superiorCcode: ccode,
      code_length: code_length,
    });
    formItems.ccode = newccode;
  } else {
    formItems.ccode = '';
  }

  // 获取上级科目级次
  jici2.value = newValue === undefined ? '1' : igrade;
  superiorCcode.value = newValue === undefined ? '' : newValue;
};

// 选中数量核算单位
const bnumchecked = async (val: any) => {
  if(val!==undefined){
    formItems.bnum='1'
  }
};
// 选中外币核算
const currencychecked = (val: any) => {
  if(val!==undefined){
    formItems.currency='1'
  }
};
// 选中现金
const bcashchecked = (val: any) => {
  formItems.bdaybook=val.target.value
  bbankflg.value = val.target.value=='1';
  bcashEquivalenceflg.value =val.target.value=='1';
  bdaybookDisabled.value=val.target.value=='1';
};
// 选中银行
const bbankchecked = (val: any) => {
  formItems.bdaybook=val.target.value
  bcashflg.value = val.target.value=='1';
  bcashEquivalenceflg.value = val.target.value=='1';
  bdaybookDisabled.value=val.target.value=='1';
};
// 选中现金等价物
const bcashEquivalencechecked = (val: any) => {
  formItems.bdaybook=val.target.value
  bbankflg.value = val.target.value=='1';
  bcashflg.value = val.target.value=='1';
  bdaybookDisabled.value=val.target.value=='1';
};

// 科目编码校验
const ccodeblur = async () => {
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
    formItems.ccode=''
    createConfirmPop('新增下级科目控制已开启,不能增加下级科目！');
    return false;
  }
  // 旧的科目不等于新科目
  if (oldformItems.ccode !== formItems.ccode) {
    if (formItems.ccode !== '') {
      if (newcode_length !== code_length) {
        createConfirmPop('科目级次不符合！');
        return false;
      } else {
        const sum = await useRouteApi(company_findByCodeNum, {schemaName: database})({
          uniqueAccStandard: standardUnique.value,
          templateId: templateID.value,
          ccode: formItems.ccode,
          iyear:iyear.value
        });

        const sumname = await useRouteApi(company_findByCodeName, {schemaName: database})({
          uniqueAccStandard: standardUnique.value,
          templateId: templateID.value,
          ccode: formItems.ccodeName,
          iyear:iyear.value
        });
        if (sum > 0) {
          createConfirmPop('科目编码已存在！');
          return false;
        }
        if (sumname > 0) {
          createConfirmPop('科目名称已存在！');
          return false;
        }
      }
    }
  }
  if( formItems.ccode!==oldformItems.ccode){
    // 科目被使用不能修改编码
    const delFirstMsg=await useRouteApi(company_delFindByCodekemu,{schemaName: database})({
      ccode: oldformItems.ccode,
      iyear: iyear.value,
      databasenum:database.value
    })
    if(delFirstMsg.code==='200'){
      return true;
    }
    else{
      if(delFirstMsg.msg==='accvoucher'){
        createConfirmPop(oldformItems.ccode+'科目已做凭证,不能修改科目编码')
        return false;
      }
      if(delFirstMsg.msg==='xjll'){
        createConfirmPop(oldformItems.ccode+'科目已被设置成现金流量,不能修改科目编码')
        return false;
      }
      if(delFirstMsg.msg==='zdkm'){
        createConfirmPop(oldformItems.ccode+'科目已被指定,不能修改科目编码')
        return false;
      }
    }
  }
  return true;
};
// 根据编码首位查询类型
const findByCclass = () => {
  formItems.cclass=styleName.value.filter(v=>v.order === formItems.ccode.charAt(0))[0].cclass
}

async function handleOk() {
  if (formItems.ccodeName === '') {
    createConfirmPop('请填写科目名称！');
    return false;
  }
  if (formItems.ccode === '') {
    createConfirmPop('请填写科目编码！');
    return false;
  }
  if (formItems.cclass === '') {
    createConfirmPop('请选择科目类型！');
    return false;
  }
  if (formItems.bprogerty === '') {
    createConfirmPop('请选择科目方向！');
    return false;
  }

  let listmap:any=[
    {
      deptchecked:verifyDeptChecked.value,
      deptvalue:deptvalue.value
    },
    {
      cuschecked:verifyCusChecked.value,
      cusvalue:cusvalue.value
    },
    {
      supchecked:verifySupChecked.value,
      supvalue:supvalue.value
    },
    {
      psnchecked:verifyPsnChecked.value,
      psnvalue:psnvalue.value
    },
    {
      itemchecked:verifyItemChecked.value,
      itemvalue:itemvalue.value
    }
  ]
  formItems.superiorCcode = superiorCcode.value.split('_')[0];
  formItems.igrade = last_jici.value;
  formItems.uniqueAccStandard = standardUnique.value;
  formItems.templateId = templateID.value;
  formItems.flag = codeflag.value;
  formItems.uniqueAccStandardName = standardName.value;
  formItems.templateName = templateName.value;
  formItems.superCodeBend = superCodeBend.value;
  formItems.listmap = listmap;

  // 获取是否预算科目标志
  let yusuanflag=styleName.value.filter((x)=>{
    return x.cclass===formItems.cclass;
  })
  formItems.yusuan = yusuanflag[0].flagYusuan==='1'?'1':'0';

  let fzccode:any=[]
  let fzccodeName:any=[]
  if(selectData.value.length>0){
    fzccode=selectData.value.map(item=>item.cdfine)
    fzccodeName=selectData.value.map(item=>item.cname)
  }
  formItems.fuzhuhesuan = fzccode.length > 0 ? fzccode.join(',') : '';
  formItems.fuzhuhesuanName = fzccode.length > 0 ? fzccodeName.join(',') : '';

  // 后选的辅助项
  let laterFz:any=[]
  let accvoucher=await useRouteApi(company_findByCodeAccVoucher,{schemaName: database})({iyear: iyear.value})
  if(accvoucher.length>0){
    oldFz.value=[]
    // 科目是否已做凭证\期初
    let findBy=accvoucher.filter(a=>a.ccode==formItems.ccode && a.imonth)
    if(findBy.length>0){
      // 选出后加的辅助项
      laterFz=selectData.value.map(s1=>s1.cdfine).filter(v1=>oldformItems.fuzhuhesuan.split(',').indexOf(v1)==-1)
      // 选出已设置并取消的辅助项
      oldformItems.fuzhuhesuan.split(',').forEach(v1=>{
        let temp=selectData.value.map(s1=>s1.cdfine).filter(v2=>v2.indexOf(v1)!=-1)
        if(temp.length==0){
          oldFz.value.push(v1)
        }
      })
    }

    // 弹框数据
    if(laterFz.length>0){
      let fuzhuHeSuan= await useRouteApi(getFuzhuHesuanList,{schemaName: database})({})

      visibleModel.value=true
      fuzhulist.value=[]
      laterFz.forEach(async (a)=>{
        let temp=fuzhuHeSuan.filter(fz=>fz.cfield===a)
        if(temp.length>0){
          let arr:any=[]
          let sql='select '+temp[0].cankaoDuixiangLabel+' as a,'+temp[0].cankaoDuixiangKey+' as b from '+temp[0].cankaoDuixiangTable+' '+temp[0].cankaoDuixiangWhere+' '+temp[0].cankaoDuixiangFlag;
          let data= await useRouteApi(company_executeSQL,{schemaName: database})(sql)
          data.forEach(d=>{
            arr.push({title:d.a,value:d.b})
          })
          fuzhulist.value.push({title:temp[0].cname,value:arr,changeVal:'',cfield:temp[0].cfield})
        }
      })
      return false;
    }
  }
  // 判断 科目类型是否 与选择的一致
  if(oldformItems.cclass!==formItems.cclass){
    let findByCclass= styleName.value.filter(v=>v.order === formItems.ccode.charAt(0))
    if(findByCclass.length==0 || findByCclass[0].cclass!==formItems.cclass){
      createConfirm({
        iconType: 'warning',
        title: '警告',
        content: '科目类型不一致,确定修改吗',
        onOk: async() => {
          save123()
        }
      })
    }
    else{
      save123()
    }
  }else{
    save123()
  }
}

// 后选的辅助项,再进行保存
async function handleOk2() {
  let tempNull=fuzhulist.value.filter(a=>a.changeVal=='')
  if(tempNull.length>0){
    createConfirmPop('辅助项不能为空,请选择辅助项!!')
    return false;
  }

  let accvoucher=await useRouteApi(company_findByCodeAccVoucher,{schemaName: database})({iyear: iyear.value})
  let fingByCodeAccvoucher= accvoucher.filter(a=>a.ccode==formItems.ccode && a.imonth!=='00'&& a.imonth!=='20'&& a.imonth!=='21')
  fingByCodeAccvoucher.forEach(v=>{
    let dept=fuzhulist.value.filter(f=>f.cfield=='cdept_id')
    if(dept.length>0){v.cdeptId=dept[0].changeVal}
    let cus=fuzhulist.value.filter(f=>f.cfield=='ccus_id')
    if(cus.length>0){ v.ccusId=cus[0].changeVal }
    let sup=fuzhulist.value.filter(f=>f.cfield=='csup_id')
    if(sup.length>0){ v.csupId=sup[0].changeVal }
    let psn=fuzhulist.value.filter(f=>f.cfield=='cperson_id')
    if(psn.length>0){ v.cpersonId=psn[0].changeVal }
    let pro=fuzhulist.value.filter(f=>f.cfield=='project_id')
    if(pro.length>0){ v.projectId=pro[0].changeVal }
    let proClass=fuzhulist.value.filter(f=>f.cfield=='project_class_id')
    if(proClass.length>0){ v.projectClassId=proClass[0].changeVal }

    let c1=fuzhulist.value.filter(f=>f.cfield=='cdfine1')
    if(c1.length>0){ v.cdfine1=c1[0].changeVal }
    let c2=fuzhulist.value.filter(f=>f.cfield=='cdfine2')
    if(c2.length>0){ v.cdfine2=c2[0].changeVal }
    let c3=fuzhulist.value.filter(f=>f.cfield=='cdfine3')
    if(c3.length>0){ v.cdfine3=c3[0].changeVal }
    let c4=fuzhulist.value.filter(f=>f.cfield=='cdfine4')
    if(c4.length>0){ v.cdfine4=c4[0].changeVal }
    let c5=fuzhulist.value.filter(f=>f.cfield=='cdfine5')
    if(c5.length>0){ v.cdfine5=c5[0].changeVal }
    let c6=fuzhulist.value.filter(f=>f.cfield=='cdfine6')
    if(c6.length>0){ v.cdfine6=c6[0].changeVal }
    let c7=fuzhulist.value.filter(f=>f.cfield=='cdfine7')
    if(c7.length>0){ v.cdfine7=c7[0].changeVal }
    let c8=fuzhulist.value.filter(f=>f.cfield=='cdfine8')
    if(c8.length>0){ v.cdfine8=c8[0].changeVal }
    let c9=fuzhulist.value.filter(f=>f.cfield=='cdfine9')
    if(c9.length>0){ v.cdfine9=c9[0].changeVal }
    let c10=fuzhulist.value.filter(f=>f.cfield=='cdfine10')
    if(c10.length>0){ v.cdfine10=c10[0].changeVal }

    let c11=fuzhulist.value.filter(f=>f.cfield=='cdfine11')
    if(c11.length>0){ v.cdfine11=c11[0].changeVal }
    let c12=fuzhulist.value.filter(f=>f.cfield=='cdfine12')
    if(c12.length>0){ v.cdfine12=c12[0].changeVal }
    let c13=fuzhulist.value.filter(f=>f.cfield=='cdfine13')
    if(c13.length>0){ v.cdfine13=c13[0].changeVal }
    let c14=fuzhulist.value.filter(f=>f.cfield=='cdfine14')
    if(c14.length>0){ v.cdfine14=c14[0].changeVal }
    let c15=fuzhulist.value.filter(f=>f.cfield=='cdfine15')
    if(c15.length>0){ v.cdfine15=c15[0].changeVal }
    let c16=fuzhulist.value.filter(f=>f.cfield=='cdfine16')
    if(c16.length>0){ v.cdfine16=c16[0].changeVal }
    let c17=fuzhulist.value.filter(f=>f.cfield=='cdfine17')
    if(c17.length>0){ v.cdfine17=c17[0].changeVal }
    let c18=fuzhulist.value.filter(f=>f.cfield=='cdfine18')
    if(c18.length>0){ v.cdfine18=c18[0].changeVal }
    let c19=fuzhulist.value.filter(f=>f.cfield=='cdfine19')
    if(c19.length>0){ v.cdfine19=c19[0].changeVal }
    let c20=fuzhulist.value.filter(f=>f.cfield=='cdfine20')
    if(c20.length>0){ v.cdfine20=c20[0].changeVal }

    let c21=fuzhulist.value.filter(f=>f.cfield=='cdfine21')
    if(c21.length>0){ v.cdfine21=c21[0].changeVal }
    let c22=fuzhulist.value.filter(f=>f.cfield=='cdfine22')
    if(c22.length>0){ v.cdfine22=c22[0].changeVal }
    let c23=fuzhulist.value.filter(f=>f.cfield=='cdfine23')
    if(c23.length>0){ v.cdfine23=c23[0].changeVal }
    let c24=fuzhulist.value.filter(f=>f.cfield=='cdfine24')
    if(c24.length>0){ v.cdfine24=c24[0].changeVal }
    let c25=fuzhulist.value.filter(f=>f.cfield=='cdfine25')
    if(c25.length>0){ v.cdfine25=c25[0].changeVal }
    let c26=fuzhulist.value.filter(f=>f.cfield=='cdfine26')
    if(c26.length>0){ v.cdfine26=c26[0].changeVal }
    let c27=fuzhulist.value.filter(f=>f.cfield=='cdfine27')
    if(c27.length>0){ v.cdfine27=c27[0].changeVal }
    let c28=fuzhulist.value.filter(f=>f.cfield=='cdfine28')
    if(c28.length>0){ v.cdfine28=c28[0].changeVal }
    let c29=fuzhulist.value.filter(f=>f.cfield=='cdfine29')
    if(c29.length>0){ v.cdfine29=c29[0].changeVal }
    let c30=fuzhulist.value.filter(f=>f.cfield=='cdfine30')
    if(c30.length>0){ v.cdfine30=c30[0].changeVal }
  })

  let fingBy00= accvoucher.filter(a=>a.ccode==formItems.ccode && a.imonth=='00')
  let fingBy21= accvoucher.filter(a=>a.ccode==formItems.ccode && a.imonth=='21')

  // 有就修改，没有增加辅助期初明细
  if(fingBy21.length==0){
    let dept=fuzhulist.value.filter(f=>f.cfield=='cdept_id')
    let cus=fuzhulist.value.filter(f=>f.cfield=='ccus_id')
    let sup=fuzhulist.value.filter(f=>f.cfield=='csup_id')
    let psn=fuzhulist.value.filter(f=>f.cfield=='cperson_id')
    let proClass=fuzhulist.value.filter(f=>f.cfield=='project_class_id')
    let pro=fuzhulist.value.filter(f=>f.cfield=='project_id')
    let c1=fuzhulist.value.filter(f=>f.cfield=='cdfine1')
    let c2=fuzhulist.value.filter(f=>f.cfield=='cdfine2')
    let c3=fuzhulist.value.filter(f=>f.cfield=='cdfine3')
    let c4=fuzhulist.value.filter(f=>f.cfield=='cdfine4')
    let c5=fuzhulist.value.filter(f=>f.cfield=='cdfine5')
    let c6=fuzhulist.value.filter(f=>f.cfield=='cdfine6')
    let c7=fuzhulist.value.filter(f=>f.cfield=='cdfine7')
    let c8=fuzhulist.value.filter(f=>f.cfield=='cdfine8')
    let c9=fuzhulist.value.filter(f=>f.cfield=='cdfine9')
    let c10=fuzhulist.value.filter(f=>f.cfield=='cdfine10')
    let c11=fuzhulist.value.filter(f=>f.cfield=='cdfine11')
    let c12=fuzhulist.value.filter(f=>f.cfield=='cdfine12')
    let c13=fuzhulist.value.filter(f=>f.cfield=='cdfine13')
    let c14=fuzhulist.value.filter(f=>f.cfield=='cdfine14')
    let c15=fuzhulist.value.filter(f=>f.cfield=='cdfine15')
    let c16=fuzhulist.value.filter(f=>f.cfield=='cdfine16')
    let c17=fuzhulist.value.filter(f=>f.cfield=='cdfine17')
    let c18=fuzhulist.value.filter(f=>f.cfield=='cdfine18')
    let c19=fuzhulist.value.filter(f=>f.cfield=='cdfine19')
    let c20=fuzhulist.value.filter(f=>f.cfield=='cdfine20')
    let c21=fuzhulist.value.filter(f=>f.cfield=='cdfine21')
    let c22=fuzhulist.value.filter(f=>f.cfield=='cdfine22')
    let c23=fuzhulist.value.filter(f=>f.cfield=='cdfine23')
    let c24=fuzhulist.value.filter(f=>f.cfield=='cdfine24')
    let c25=fuzhulist.value.filter(f=>f.cfield=='cdfine25')
    let c26=fuzhulist.value.filter(f=>f.cfield=='cdfine26')
    let c27=fuzhulist.value.filter(f=>f.cfield=='cdfine27')
    let c28=fuzhulist.value.filter(f=>f.cfield=='cdfine28')
    let c29=fuzhulist.value.filter(f=>f.cfield=='cdfine29')
    let c30=fuzhulist.value.filter(f=>f.cfield=='cdfine30')

    let data={
      id: null,
      ibook: '0',
      imonth: "21",
      iperiod: "21",
      iyear: iyear.value,
      iyperiod: iyear.value+"21",
      uniqueCode: RandomRange(15,15),
      ccode: formItems.ccode,
      ccodeName: formItems.ccodeName,
      mc: "0.00",
      mcF: "0.00",
      md: "0.00",
      mdF: "0.00",
      ncS: "0.00",
      ndS: "0.00",
      ccusId: cus.length==0?null:cus[0].changeVal,
      cdeptId: dept.length==0?null:dept[0].changeVal,
      cpersonId: psn.length==0?null:psn[0].changeVal,
      csupId: sup.length==0?null:sup[0].changeVal,
      projectClassId: proClass.length==0?null:proClass[0].changeVal,
      projectId: pro.length==0?null:pro[0].changeVal,
      cdfine1: c1.length==0?null:c1[0].changeVal,
      cdfine2: c2.length==0?null:c2[0].changeVal,
      cdfine3: c3.length==0?null:c3[0].changeVal,
      cdfine4: c4.length==0?null:c4[0].changeVal,
      cdfine5: c5.length==0?null:c5[0].changeVal,
      cdfine6: c6.length==0?null:c6[0].changeVal,
      cdfine7: c7.length==0?null:c7[0].changeVal,
      cdfine8: c8.length==0?null:c8[0].changeVal,
      cdfine9: c9.length==0?null:c9[0].changeVal,
      cdfine10: c10.length==0?null:c10[0].changeVal,
      cdfine11: c11.length==0?null:c11[0].changeVal,
      cdfine12: c12.length==0?null:c12[0].changeVal,
      cdfine13: c13.length==0?null:c13[0].changeVal,
      cdfine14: c14.length==0?null:c14[0].changeVal,
      cdfine15: c15.length==0?null:c15[0].changeVal,
      cdfine16: c16.length==0?null:c16[0].changeVal,
      cdfine17: c17.length==0?null:c17[0].changeVal,
      cdfine18: c18.length==0?null:c18[0].changeVal,
      cdfine19: c19.length==0?null:c19[0].changeVal,
      cdfine20: c20.length==0?null:c20[0].changeVal,
      cdfine21: c21.length==0?null:c21[0].changeVal,
      cdfine22: c22.length==0?null:c22[0].changeVal,
      cdfine23: c23.length==0?null:c23[0].changeVal,
      cdfine24: c24.length==0?null:c24[0].changeVal,
      cdfine25: c25.length==0?null:c25[0].changeVal,
      cdfine26: c26.length==0?null:c26[0].changeVal,
      cdfine27: c27.length==0?null:c27[0].changeVal,
      cdfine28: c28.length==0?null:c28[0].changeVal,
      cdfine29: c29.length==0?null:c29[0].changeVal,
      cdfine30: c30.length==0?null:c30[0].changeVal,
    }
    fingByCodeAccvoucher.push(data)
  }
  else{
    fingBy21.forEach(v=>{
      let dept=fuzhulist.value.filter(f=>f.cfield=='cdept_id')
      if(dept.length>0){v.cdeptId=dept[0].changeVal}
      let cus=fuzhulist.value.filter(f=>f.cfield=='ccus_id')
      if(cus.length>0){ v.ccusId=cus[0].changeVal }
      let sup=fuzhulist.value.filter(f=>f.cfield=='csup_id')
      if(sup.length>0){ v.csupId=sup[0].changeVal }
      let psn=fuzhulist.value.filter(f=>f.cfield=='cperson_id')
      if(psn.length>0){ v.cpersonId=psn[0].changeVal }
      let pro=fuzhulist.value.filter(f=>f.cfield=='project_id')
      if(pro.length>0){ v.projectId=pro[0].changeVal }
      let proClass=fuzhulist.value.filter(f=>f.cfield=='project_class_id')
      if(proClass.length>0){ v.projectClassId=proClass[0].changeVal }

      let c1=fuzhulist.value.filter(f=>f.cfield=='cdfine1')
      if(c1.length>0){ v.cdfine1=c1[0].changeVal }
      let c2=fuzhulist.value.filter(f=>f.cfield=='cdfine2')
      if(c2.length>0){ v.cdfine2=c2[0].changeVal }
      let c3=fuzhulist.value.filter(f=>f.cfield=='cdfine3')
      if(c3.length>0){ v.cdfine3=c3[0].changeVal }
      let c4=fuzhulist.value.filter(f=>f.cfield=='cdfine4')
      if(c4.length>0){ v.cdfine4=c4[0].changeVal }
      let c5=fuzhulist.value.filter(f=>f.cfield=='cdfine5')
      if(c5.length>0){ v.cdfine5=c5[0].changeVal }
      let c6=fuzhulist.value.filter(f=>f.cfield=='cdfine6')
      if(c6.length>0){ v.cdfine6=c6[0].changeVal }
      let c7=fuzhulist.value.filter(f=>f.cfield=='cdfine7')
      if(c7.length>0){ v.cdfine7=c7[0].changeVal }
      let c8=fuzhulist.value.filter(f=>f.cfield=='cdfine8')
      if(c8.length>0){ v.cdfine8=c8[0].changeVal }
      let c9=fuzhulist.value.filter(f=>f.cfield=='cdfine9')
      if(c9.length>0){ v.cdfine9=c9[0].changeVal }
      let c10=fuzhulist.value.filter(f=>f.cfield=='cdfine10')
      if(c10.length>0){ v.cdfine10=c10[0].changeVal }

      let c11=fuzhulist.value.filter(f=>f.cfield=='cdfine11')
      if(c11.length>0){ v.cdfine11=c11[0].changeVal }
      let c12=fuzhulist.value.filter(f=>f.cfield=='cdfine12')
      if(c12.length>0){ v.cdfine12=c12[0].changeVal }
      let c13=fuzhulist.value.filter(f=>f.cfield=='cdfine13')
      if(c13.length>0){ v.cdfine13=c13[0].changeVal }
      let c14=fuzhulist.value.filter(f=>f.cfield=='cdfine14')
      if(c14.length>0){ v.cdfine14=c14[0].changeVal }
      let c15=fuzhulist.value.filter(f=>f.cfield=='cdfine15')
      if(c15.length>0){ v.cdfine15=c15[0].changeVal }
      let c16=fuzhulist.value.filter(f=>f.cfield=='cdfine16')
      if(c16.length>0){ v.cdfine16=c16[0].changeVal }
      let c17=fuzhulist.value.filter(f=>f.cfield=='cdfine17')
      if(c17.length>0){ v.cdfine17=c17[0].changeVal }
      let c18=fuzhulist.value.filter(f=>f.cfield=='cdfine18')
      if(c18.length>0){ v.cdfine18=c18[0].changeVal }
      let c19=fuzhulist.value.filter(f=>f.cfield=='cdfine19')
      if(c19.length>0){ v.cdfine19=c19[0].changeVal }
      let c20=fuzhulist.value.filter(f=>f.cfield=='cdfine20')
      if(c20.length>0){ v.cdfine20=c20[0].changeVal }

      let c21=fuzhulist.value.filter(f=>f.cfield=='cdfine21')
      if(c21.length>0){ v.cdfine21=c21[0].changeVal }
      let c22=fuzhulist.value.filter(f=>f.cfield=='cdfine22')
      if(c22.length>0){ v.cdfine22=c22[0].changeVal }
      let c23=fuzhulist.value.filter(f=>f.cfield=='cdfine23')
      if(c23.length>0){ v.cdfine23=c23[0].changeVal }
      let c24=fuzhulist.value.filter(f=>f.cfield=='cdfine24')
      if(c24.length>0){ v.cdfine24=c24[0].changeVal }
      let c25=fuzhulist.value.filter(f=>f.cfield=='cdfine25')
      if(c25.length>0){ v.cdfine25=c25[0].changeVal }
      let c26=fuzhulist.value.filter(f=>f.cfield=='cdfine26')
      if(c26.length>0){ v.cdfine26=c26[0].changeVal }
      let c27=fuzhulist.value.filter(f=>f.cfield=='cdfine27')
      if(c27.length>0){ v.cdfine27=c27[0].changeVal }
      let c28=fuzhulist.value.filter(f=>f.cfield=='cdfine28')
      if(c28.length>0){ v.cdfine28=c28[0].changeVal }
      let c29=fuzhulist.value.filter(f=>f.cfield=='cdfine29')
      if(c29.length>0){ v.cdfine29=c29[0].changeVal }
      let c30=fuzhulist.value.filter(f=>f.cfield=='cdfine30')
      if(c30.length>0){ v.cdfine30=c30[0].changeVal }
      fingByCodeAccvoucher.push(v)
    })
  }

  if(fingBy00.length==0){
    let dept=fuzhulist.value.filter(f=>f.cfield=='cdept_id')
    let cus=fuzhulist.value.filter(f=>f.cfield=='ccus_id')
    let sup=fuzhulist.value.filter(f=>f.cfield=='csup_id')
    let psn=fuzhulist.value.filter(f=>f.cfield=='cperson_id')
    let proClass=fuzhulist.value.filter(f=>f.cfield=='project_class_id')
    let pro=fuzhulist.value.filter(f=>f.cfield=='project_id')
    let c1=fuzhulist.value.filter(f=>f.cfield=='cdfine1')
    let c2=fuzhulist.value.filter(f=>f.cfield=='cdfine2')
    let c3=fuzhulist.value.filter(f=>f.cfield=='cdfine3')
    let c4=fuzhulist.value.filter(f=>f.cfield=='cdfine4')
    let c5=fuzhulist.value.filter(f=>f.cfield=='cdfine5')
    let c6=fuzhulist.value.filter(f=>f.cfield=='cdfine6')
    let c7=fuzhulist.value.filter(f=>f.cfield=='cdfine7')
    let c8=fuzhulist.value.filter(f=>f.cfield=='cdfine8')
    let c9=fuzhulist.value.filter(f=>f.cfield=='cdfine9')
    let c10=fuzhulist.value.filter(f=>f.cfield=='cdfine10')
    let c11=fuzhulist.value.filter(f=>f.cfield=='cdfine11')
    let c12=fuzhulist.value.filter(f=>f.cfield=='cdfine12')
    let c13=fuzhulist.value.filter(f=>f.cfield=='cdfine13')
    let c14=fuzhulist.value.filter(f=>f.cfield=='cdfine14')
    let c15=fuzhulist.value.filter(f=>f.cfield=='cdfine15')
    let c16=fuzhulist.value.filter(f=>f.cfield=='cdfine16')
    let c17=fuzhulist.value.filter(f=>f.cfield=='cdfine17')
    let c18=fuzhulist.value.filter(f=>f.cfield=='cdfine18')
    let c19=fuzhulist.value.filter(f=>f.cfield=='cdfine19')
    let c20=fuzhulist.value.filter(f=>f.cfield=='cdfine20')
    let c21=fuzhulist.value.filter(f=>f.cfield=='cdfine21')
    let c22=fuzhulist.value.filter(f=>f.cfield=='cdfine22')
    let c23=fuzhulist.value.filter(f=>f.cfield=='cdfine23')
    let c24=fuzhulist.value.filter(f=>f.cfield=='cdfine24')
    let c25=fuzhulist.value.filter(f=>f.cfield=='cdfine25')
    let c26=fuzhulist.value.filter(f=>f.cfield=='cdfine26')
    let c27=fuzhulist.value.filter(f=>f.cfield=='cdfine27')
    let c28=fuzhulist.value.filter(f=>f.cfield=='cdfine28')
    let c29=fuzhulist.value.filter(f=>f.cfield=='cdfine29')
    let c30=fuzhulist.value.filter(f=>f.cfield=='cdfine30')

    let data={
      id: null,
      ibook: '0',
      imonth: "00",
      iperiod: "00",
      iyear: iyear.value,
      iyperiod: iyear.value+"00",
      uniqueCode: RandomRange(15,15),
      ccode: formItems.ccode,
      ccodeName: formItems.ccodeName,
      mc: "0.00",
      mcF: "0.00",
      md: "0.00",
      mdF: "0.00",
      ncS: "0.00",
      ndS: "0.00",
      ccusId: cus.length==0?null:cus[0].changeVal,
      cdeptId: dept.length==0?null:dept[0].changeVal,
      cpersonId: psn.length==0?null:psn[0].changeVal,
      csupId: sup.length==0?null:sup[0].changeVal,
      projectClassId: proClass.length==0?null:proClass[0].changeVal,
      projectId: pro.length==0?null:pro[0].changeVal,
      cdfine1: c1.length==0?null:c1[0].changeVal,
      cdfine2: c2.length==0?null:c2[0].changeVal,
      cdfine3: c3.length==0?null:c3[0].changeVal,
      cdfine4: c4.length==0?null:c4[0].changeVal,
      cdfine5: c5.length==0?null:c5[0].changeVal,
      cdfine6: c6.length==0?null:c6[0].changeVal,
      cdfine7: c7.length==0?null:c7[0].changeVal,
      cdfine8: c8.length==0?null:c8[0].changeVal,
      cdfine9: c9.length==0?null:c9[0].changeVal,
      cdfine10: c10.length==0?null:c10[0].changeVal,
      cdfine11: c11.length==0?null:c11[0].changeVal,
      cdfine12: c12.length==0?null:c12[0].changeVal,
      cdfine13: c13.length==0?null:c13[0].changeVal,
      cdfine14: c14.length==0?null:c14[0].changeVal,
      cdfine15: c15.length==0?null:c15[0].changeVal,
      cdfine16: c16.length==0?null:c16[0].changeVal,
      cdfine17: c17.length==0?null:c17[0].changeVal,
      cdfine18: c18.length==0?null:c18[0].changeVal,
      cdfine19: c19.length==0?null:c19[0].changeVal,
      cdfine20: c20.length==0?null:c20[0].changeVal,
      cdfine21: c21.length==0?null:c21[0].changeVal,
      cdfine22: c22.length==0?null:c22[0].changeVal,
      cdfine23: c23.length==0?null:c23[0].changeVal,
      cdfine24: c24.length==0?null:c24[0].changeVal,
      cdfine25: c25.length==0?null:c25[0].changeVal,
      cdfine26: c26.length==0?null:c26[0].changeVal,
      cdfine27: c27.length==0?null:c27[0].changeVal,
      cdfine28: c28.length==0?null:c28[0].changeVal,
      cdfine29: c29.length==0?null:c29[0].changeVal,
      cdfine30: c30.length==0?null:c30[0].changeVal,
    }
    fingByCodeAccvoucher.push(data)
  }
  else{
    fingBy00.forEach(v=>{
      let dept=fuzhulist.value.filter(f=>f.cfield=='cdept_id')
      if(dept.length>0){v.cdeptId=dept[0].changeVal}
      let cus=fuzhulist.value.filter(f=>f.cfield=='ccus_id')
      if(cus.length>0){ v.ccusId=cus[0].changeVal }
      let sup=fuzhulist.value.filter(f=>f.cfield=='csup_id')
      if(sup.length>0){ v.csupId=sup[0].changeVal }
      let psn=fuzhulist.value.filter(f=>f.cfield=='cperson_id')
      if(psn.length>0){ v.cpersonId=psn[0].changeVal }
      let pro=fuzhulist.value.filter(f=>f.cfield=='project_id')
      if(pro.length>0){ v.projectId=pro[0].changeVal }
      let proClass=fuzhulist.value.filter(f=>f.cfield=='project_class_id')
      if(proClass.length>0){ v.projectClassId=proClass[0].changeVal }

      let c1=fuzhulist.value.filter(f=>f.cfield=='cdfine1')
      if(c1.length>0){ v.cdfine1=c1[0].changeVal }
      let c2=fuzhulist.value.filter(f=>f.cfield=='cdfine2')
      if(c2.length>0){ v.cdfine2=c2[0].changeVal }
      let c3=fuzhulist.value.filter(f=>f.cfield=='cdfine3')
      if(c3.length>0){ v.cdfine3=c3[0].changeVal }
      let c4=fuzhulist.value.filter(f=>f.cfield=='cdfine4')
      if(c4.length>0){ v.cdfine4=c4[0].changeVal }
      let c5=fuzhulist.value.filter(f=>f.cfield=='cdfine5')
      if(c5.length>0){ v.cdfine5=c5[0].changeVal }
      let c6=fuzhulist.value.filter(f=>f.cfield=='cdfine6')
      if(c6.length>0){ v.cdfine6=c6[0].changeVal }
      let c7=fuzhulist.value.filter(f=>f.cfield=='cdfine7')
      if(c7.length>0){ v.cdfine7=c7[0].changeVal }
      let c8=fuzhulist.value.filter(f=>f.cfield=='cdfine8')
      if(c8.length>0){ v.cdfine8=c8[0].changeVal }
      let c9=fuzhulist.value.filter(f=>f.cfield=='cdfine9')
      if(c9.length>0){ v.cdfine9=c9[0].changeVal }
      let c10=fuzhulist.value.filter(f=>f.cfield=='cdfine10')
      if(c10.length>0){ v.cdfine10=c10[0].changeVal }

      let c11=fuzhulist.value.filter(f=>f.cfield=='cdfine11')
      if(c11.length>0){ v.cdfine11=c11[0].changeVal }
      let c12=fuzhulist.value.filter(f=>f.cfield=='cdfine12')
      if(c12.length>0){ v.cdfine12=c12[0].changeVal }
      let c13=fuzhulist.value.filter(f=>f.cfield=='cdfine13')
      if(c13.length>0){ v.cdfine13=c13[0].changeVal }
      let c14=fuzhulist.value.filter(f=>f.cfield=='cdfine14')
      if(c14.length>0){ v.cdfine14=c14[0].changeVal }
      let c15=fuzhulist.value.filter(f=>f.cfield=='cdfine15')
      if(c15.length>0){ v.cdfine15=c15[0].changeVal }
      let c16=fuzhulist.value.filter(f=>f.cfield=='cdfine16')
      if(c16.length>0){ v.cdfine16=c16[0].changeVal }
      let c17=fuzhulist.value.filter(f=>f.cfield=='cdfine17')
      if(c17.length>0){ v.cdfine17=c17[0].changeVal }
      let c18=fuzhulist.value.filter(f=>f.cfield=='cdfine18')
      if(c18.length>0){ v.cdfine18=c18[0].changeVal }
      let c19=fuzhulist.value.filter(f=>f.cfield=='cdfine19')
      if(c19.length>0){ v.cdfine19=c19[0].changeVal }
      let c20=fuzhulist.value.filter(f=>f.cfield=='cdfine20')
      if(c20.length>0){ v.cdfine20=c20[0].changeVal }

      let c21=fuzhulist.value.filter(f=>f.cfield=='cdfine21')
      if(c21.length>0){ v.cdfine21=c21[0].changeVal }
      let c22=fuzhulist.value.filter(f=>f.cfield=='cdfine22')
      if(c22.length>0){ v.cdfine22=c22[0].changeVal }
      let c23=fuzhulist.value.filter(f=>f.cfield=='cdfine23')
      if(c23.length>0){ v.cdfine23=c23[0].changeVal }
      let c24=fuzhulist.value.filter(f=>f.cfield=='cdfine24')
      if(c24.length>0){ v.cdfine24=c24[0].changeVal }
      let c25=fuzhulist.value.filter(f=>f.cfield=='cdfine25')
      if(c25.length>0){ v.cdfine25=c25[0].changeVal }
      let c26=fuzhulist.value.filter(f=>f.cfield=='cdfine26')
      if(c26.length>0){ v.cdfine26=c26[0].changeVal }
      let c27=fuzhulist.value.filter(f=>f.cfield=='cdfine27')
      if(c27.length>0){ v.cdfine27=c27[0].changeVal }
      let c28=fuzhulist.value.filter(f=>f.cfield=='cdfine28')
      if(c28.length>0){ v.cdfine28=c28[0].changeVal }
      let c29=fuzhulist.value.filter(f=>f.cfield=='cdfine29')
      if(c29.length>0){ v.cdfine29=c29[0].changeVal }
      let c30=fuzhulist.value.filter(f=>f.cfield=='cdfine30')
      if(c30.length>0){ v.cdfine30=c30[0].changeVal }
      fingByCodeAccvoucher.push(v)
    })
  }
  // 判断 科目类型是否 与选择的一致
  if(oldformItems.cclass!==formItems.cclass){
    let findByCclass= styleName.value.filter(v=>v.order === formItems.ccode.charAt(0))
    if(findByCclass.length==0 || findByCclass[0].cclass!==formItems.cclass){
      createConfirm({
        iconType: 'warning',
        title: '警告',
        content: '科目类型不一致,确定修改吗',
        onOk: async() => {
          save123()
        }
      })
    }
    else{
      save123()
    }
  }else{
    save123()
  }
  accvoucherlist.value=fingByCodeAccvoucher
}

// js生成字母和数字随机数
function RandomRange(min, max) {
  var returnStr = "",
    range = (max ? Math.round(Math.random() * (max-min)) + min : min),
    arr = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',             'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];
  for(var i=0; i<range; i++){
    var index = Math.round(Math.random() * (arr.length-1));
    returnStr += arr[index];
  }
  return returnStr;
}
// 两个数组相同的
function getArrEqual(arr1, arr2) {
  let newArr:any = [];
  for (let i = 0; i < arr2.length; i++) {
    for (let j = 0; j < arr1.length; j++) {
      if (arr1[j] === arr2[i]) {
        newArr.push(arr1[j]);
      }
    }
  }
  return newArr;
}
// 两个数组不相同的
function getArrNoEqual(arr1, arr2) {
  return arr1.concat(arr2).filter(function (v, i, arr) {
    return arr.indexOf(v) === arr.lastIndexOf(v);
  });
}
async function save123() {
    // 埋点-操作日志
    let log=''
    if(oldformItems.bprogerty!==formItems.bprogerty){
      let oldbprogerty=oldformItems.bprogerty=='1'?'借':'贷'
      let newbprogerty=formItems.bprogerty=='1'?'借':'贷'
      log+='科目方向【'+oldbprogerty+','+newbprogerty+'】,'
    }
    if(oldformItems.ccode!==formItems.ccode){
      log+='科目编码【'+oldformItems.ccode+','+formItems.ccode+'】,'
    }
    if(oldformItems.ccodeName!==formItems.ccodeName){
      log+='科目名称【'+oldformItems.ccodeName+','+formItems.ccodeName+'】,'
    }
    if(oldformItems.flag!==formItems.flag){
      let oldflag=oldformItems.flag=='1'?'启用':'停用'
      let newflag=formItems.flag=='1'?'启用':'停用'
      log+='启用状态【'+oldflag+','+newflag+'】,'
    }
    if(oldformItems.bcash!==formItems.bcash){
      let old=oldformItems.bcash=='1'?'是':'否'
      let news=formItems.bcash=='1'?'是':'否'
      log+='现金科目【'+old+','+news+'】,'
    }
    if(oldformItems.bbank!==formItems.bbank){
      let old=oldformItems.bbank=='1'?'是':'否'
      let news=formItems.bbank=='1'?'是':'否'
      log+='银行科目【'+old+','+news+'】,'
    }
    if(oldformItems.bcashEquivalence!==formItems.bcashEquivalence){
      let old=oldformItems.bcashEquivalence=='1'?'是':'否'
      let news=formItems.bcashEquivalence=='1'?'是':'否'
      log+='现金等价物科目【'+old+','+news+'】,'
    }
    if(oldformItems.xjll!==formItems.xjll){
      let old=oldformItems.xjll=='1'?'是':'否'
      let news=formItems.xjll=='1'?'是':'否'
      log+='现金流量科目【'+old+','+news+'】,'
    }
    if(oldformItems.bdaybook!==formItems.bdaybook){
      let old=oldformItems.bdaybook=='1'?'是':'否'
      let news=formItems.bdaybook=='1'?'是':'否'
      log+='日记账科目【'+old+','+news+'】,'
    }
    if(oldformItems.controlled!==formItems.controlled){
      let old=oldformItems.controlled=='1'?'是':'否'
      let news=formItems.controlled=='1'?'是':'否'
      log+='业务受控科目【'+old+','+news+'】,'
    }

    let oldfuzhuhesuan=oldformItems.fuzhuhesuan==null?'':oldformItems.fuzhuhesuan
    if(oldfuzhuhesuan!==formItems.fuzhuhesuan){
      log+='辅助核算【'+oldformItems.fuzhuhesuan+','+formItems.fuzhuhesuan+'】,'
    }
    if(oldformItems.currencyType!==(formItems.currencyType==undefined?null:formItems.currencyType)){
      let old=oldformItems.currencyType==null?'':oldformItems.currencyType
      let news=formItems.currencyType==null||formItems.currencyType==undefined?'':formItems.currencyType
      log+='外币核算【'+old+','+news+'】,'
    }
    if(oldformItems.menterage!==(formItems.menterage==undefined?null:formItems.menterage)){
      let old=oldformItems.menterage==null?'':oldformItems.menterage
      let news=formItems.menterage==null||formItems.menterage==undefined?'':formItems.menterage
      log+='数量核算【'+old+','+news+'】,'
    }

    if(oldformItems.lowerControl!==formItems.lowerControl){
      let old=oldformItems.lowerControl=='1'?'是':'否'
      let news=formItems.lowerControl=='1'?'是':'否'
      log+='添加下级科目控制【'+old+','+news+'】,'
    }
    if(oldformItems.fuzhuControl!==formItems.fuzhuControl){
      let old=oldformItems.fuzhuControl=='1'?'是':'否'
      let news=formItems.fuzhuControl=='1'?'是':'否'
      log+='添加辅助核算控制【'+old+','+news+'】,'
    }

    if(log!==''){
      let arrlog='操作内容【修改会计科目】,账套代码【'+pageParameter.companyCode+'】,账套名称【'+pageParameter.companyName+'】,年度【'+pageParameter.iyear+'】,'+log.substring(0,log.length-1)
      let logtime=new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," ")
      /************** 记录操作日志 ****************/
      let map={
        loginTime:logtime,
        userId: useUserStoreWidthOut().getUserInfo.username,
        userName: useUserStoreWidthOut().getUserInfo.realName,
        optModule:'master_data',
        optFunction:'会计科目',
        optRange:'1',
        uniqueCode:accId.value,
        billDate:pageParameter.iyear,
        optAction:'修改',
        optContent:arrlog,
      }
      await saveLog(map)
      /************** 记录操作日志 END ****************/

      // 集团账套-审计会计科目记录
      oldformItems.optMethod='0'
      oldformItems.optUsername=useUserStoreWidthOut().getUserInfo.realName
      oldformItems.optTime=logtime
      await useRouteApi(company_auditCodeKemuSave,{schemaName: database})(toRaw(oldformItems))
    }
    else{
      let arrlog='操作内容【修改会计科目,未发生变化】,账套代码【'+pageParameter.companyCode+'】,账套名称【'+pageParameter.companyName+'】,年度【'+pageParameter.iyear+'】'
      let logtime=new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," ")
      /************** 记录操作日志 ****************/
      let map={
        loginTime:logtime,
        userId: useUserStoreWidthOut().getUserInfo.username,
        userName: useUserStoreWidthOut().getUserInfo.realName,
        optModule:'master_data',
        optFunction:'会计科目',
        optRange:'1',
        uniqueCode:accId.value,
        optAction:'修改',
        optContent:arrlog,
      }
      await saveLog(map)
      /************** 记录操作日志 END ****************/
    }
    visibleModel.value=false
    // 如果中途辅助项有变动,先执行【修改凭证、期初中的辅助项列】
    if(accvoucherlist.value.length>0){
      await useRouteApi(company_executeAccvoucher, {schemaName: database.value})(accvoucherlist.value)
    }
    // 没有中途添加辅助，查看是否中途取消辅助
    if(oldFz.value.length>0){
      let accvoucher=await useRouteApi(company_findByCodeAccVoucher,{schemaName: database})({iyear: iyear.value})
      let fingByCodeAccvoucher= accvoucher.filter(a=>a.ccode==formItems.ccode)
      oldFz.value.forEach(a=>{
        if(a=='cdept_id'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdeptId='' })
        }else if(a=='ccus_id'){
          fingByCodeAccvoucher.forEach(v=>{ v.ccusId='' })
        }else if(a=='csup_id'){
          fingByCodeAccvoucher.forEach(v=>{ v.csupId='' })
        }else if(a=='cperson_id'){
          fingByCodeAccvoucher.forEach(v=>{ v.cpersonId='' })
        }else if(a=='project_class_id'){
          fingByCodeAccvoucher.forEach(v=>{ v.projectClassId='' })
        }else if(a=='projectId'){
          fingByCodeAccvoucher.forEach(v=>{ v.projectId='' })
        }else if(a=='cdfine1'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine1='' })
        }else if(a=='cdfine2'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine2='' })
        }else if(a=='cdfine3'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine3='' })
        }else if(a=='cdfine4'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine4='' })
        }else if(a=='cdfine5'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine5='' })
        }else if(a=='cdfine6'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine6='' })
        }else if(a=='cdfine7'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine7='' })
        }else if(a=='cdfine8'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine8='' })
        }else if(a=='cdfine9'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine9='' })
        }else if(a=='cdfine10'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine10='' })
        }else if(a=='cdfine11'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine11='' })
        }else if(a=='cdfine12'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine12='' })
        }else if(a=='cdfine13'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine13='' })
        }else if(a=='cdfine14'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine14='' })
        }else if(a=='cdfine15'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine15='' })
        }else if(a=='cdfine16'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine16='' })
        }else if(a=='cdfine17'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine17='' })
        }else if(a=='cdfine18'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine18='' })
        }else if(a=='cdfine19'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine19='' })
        }else if(a=='cdfine20'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine20='' })
        }else if(a=='cdfine21'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine21='' })
        }else if(a=='cdfine22'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine22='' })
        }else if(a=='cdfine23'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine23='' })
        }else if(a=='cdfine24'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine24='' })
        }else if(a=='cdfine25'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine25='' })
        }else if(a=='cdfine26'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine26='' })
        }else if(a=='cdfine27'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine27='' })
        }else if(a=='cdfine28'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine28='' })
        }else if(a=='cdfine29'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine29='' })
        }else if(a=='cdfine30'){
          fingByCodeAccvoucher.forEach(v=>{ v.cdfine30='' })
        }
      })
      console.log(accvoucher)
      await useRouteApi(company_executeAccvoucher, {schemaName: database.value})(fingByCodeAccvoucher)
    }
    emit('save', toRaw(formItems));
    closeModal();
}
const onFuzhuHeSuanChange = async (a, b) => {
  selectData.value=[]
  selectedRowKeys.value=[]
  selectedRowKeys.value = a
  selectData.value = b.filter(item => a.indexOf(item.key) != -1)

  if (a.length > 10) {
    createConfirmPop('不能超过10个自定义项')
    selectedRowKeys.value = []
    selectData.value = []
  }
}
async function fuzhuModalShow() {
  const fuzhuhesuan = await useRouteApi(findFuzhuHesuanList, {schemaName: database.value})('')
  fuzhuModalDataSource.value=[]
  if (fuzhuhesuan.items.length > 0) {
    for (let i = 0; i < fuzhuhesuan.items.length; i++) {
      fuzhuModalDataSource.value.push({
        key: i,
        ccode: fuzhuhesuan.items[i].ccode,
        cname: fuzhuhesuan.items[i].cname,
        cdfine: fuzhuhesuan.items[i].cfield
      })
    }
  }
}
function cancelModel() {
  selectedRowKeys.value = []
  selectData.value = []
  fuzhuModalDataSource.value = []
}
function modalOk() {
  fuzhuModal.value = false
}
async function findByCodePzQc(ccode) {
  // 判断 凭证、期初、现金流量是否使用
  const delFirstMsg=await useRouteApi(company_delFindByCodekemu,{schemaName: database})({
    ccode: ccode,
    iyear: iyear.value,
    databasenum:database.value
  })
  if(delFirstMsg.code==='200'){
  }else{
    if(delFirstMsg.msg==='accvoucher' || delFirstMsg.msg==='qc'){
    }else{
    }
  }
}
const superiorCcodeChange = async (val) => {
  var sumname;
  if(superiorCcode.value===''){
    sumname = await useRouteApi(company_findByCodeName,{schemaName:database})({
      uniqueAccStandard: standardUnique.value,
      templateId: templateID.value,
      ccode: formItems.ccodeName,
    });
  }else{
    sumname = await useRouteApi(company_findBySuperCodeName,{schemaName:database})({
      uniqueAccStandard: standardUnique.value,
      templateId: templateID.value,
      ccode: formItems.ccodeName,
      superCcod: superiorCcode.value,
      iyear: iyear.value,
    });
  }
  if (sumname > 0) {
    createConfirmPop('科目名称已存在！');
    return false;
  }

  // 获取上级科目信息
  const code = await useRouteApi(company_findByCcode,{schemaName:database})({ccode: val,iyear:iyear.value});

  await findByCodePzQc(val)
}
async function getNewCcode(code,val) {
  const igrade = code.igrade;
  if(!independent.value){
    // 开启下级控制
    super_lowerControl.value = code.lowerControl === '1';
    if (code.lowerControl === '1') {
      formItems.ccode=''
      createConfirmPop('新增下级科目控制已开启,不能增加下级科目！');
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
  // 编码_级次
  const newccode = await useRouteApi(company_findByLowerMaxCodeNum,{schemaName:database})({
    superiorCcode: val,
    code_length: code_length,
  });
  formItems.ccode = newccode;
  // 获取上级科目级次
  jici2.value =  igrade;
}
function handleClose() {
  activeKey.value='1'
  // 辅助model
  fuzhuModal.value = false
  // 是否独立账套
  independent.value = false
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
  // 表单封装数据
  formItems.ccode = ''
  formItems.ccodeName = ''
  formItems.cclass = ''
  formItems.bprogerty = ''
  formItems.menterage = ''
  formItems.currencyType = ''
  formItems.igrade = ''
  formItems.uniqueAccStandard = ''
  formItems.superiorCcode = ''
  formItems.templateId = ''
  formItems.lowerControl = ''
  formItems.fuzhuControl = ''
  formItems.uniqueAccStandardName = ''
  formItems.templateName = ''
  formItems.bend = ''
  formItems.flag = ''
  formItems.iyear = ''
  formItems.fuzhuhesuan = ''
  formItems.yusuan = ''
  formItems.bdaybook = ''
  formItems.controlled = ''
  formItems.pxjz = '';
  formItems.xjll = '';
  formItems.cyfx = '';

  formItems.bnum = '0';
  formItems.currency = '0';
  formItems.bcash = '0';
  formItems.bbank = '0';
  formItems.bcashEquivalence = '0';
  formItems.bperson = '0';
  formItems.bcus = '0';
  formItems.bsup = '0';
  formItems.bdept = '0';
  formItems.bitem = '0';


  // 现金科目
  bcashflg.value=false
  // 银行科目
  bbankflg.value=false
  // 现金等价物
  bcashEquivalenceflg.value=false
  currencyDisabled.value=false
  bnumDisabled.value=false

  verifyDeptChecked.value=false
  deptvalue.value=''
  verifyCusChecked.value=false,
  cusvalue.value=''
  verifySupChecked.value=false,
  supvalue.value=''
  verifyPsnChecked.value=false,
  psnvalue.value=''
  verifyItemChecked.value=false,
  itemvalue.value=''
  return true;
}
function createConfirmPop(text) {
  createWarningModal({
    iconType: 'warning',
    title: '警告',
    content: text,
    onOk: async() => {},
  })
}
function checkedText(val) {
  let textPop=''
  if(val){
    textPop='该科目已经使用，添加辅助核算项可能会导致辅助账查询异常，建议你在下一年期初时再进行修改，你确定要继续添加该科目辅助核算项吗？'
  }else{
    textPop='该科目辅助核算项已经使用，取消辅助核算项可能会导致辅助账查询异常，建议你在下一年期初时再进行修改，你确定要继续取消该科目辅助核算项吗？'
  }
  createConfirmPop(textPop)
}
function filterOption(input, option) {
  // 不是数字
  if(isNaN(input)){
    return option.title.toLowerCase().indexOf(input.toLowerCase()) >= 0;
  }else{
    return option.title.toLowerCase().startsWith(input.toLowerCase());
  }
}

function saveSelectPsn(data) { fuzhulist.value.filter(a=>a.cfield=='cperson_id').forEach(a=>{ a.changeVal=data.uniqueCode }) }
function saveSelectDept(data) { fuzhulist.value.filter(a=>a.cfield=='cdept_id').forEach(a=>{ a.changeVal=data.uniqueCode }) }
function throwCusData(data) { fuzhulist.value.filter(a=>a.cfield=='ccus_id').forEach(a=>{ a.changeVal=data.uniqueCode }) }
function throwSupData(data) { fuzhulist.value.filter(a=>a.cfield=='csup_id').forEach(a=>{ a.changeVal=data.uniqueCode }) }
function saveSelectProject(data) { fuzhulist.value.filter(a=>a.cfield=='project_id').forEach(a=>{ a.changeVal=data.uniqueCode }) }
function YslyData(data) { fuzhulist.value.filter(a=>a.cfield=='ys_ysly').forEach(a=>{ a.changeVal=data.bsCode }) }
function ZcgnData(data) { fuzhulist.value.filter(a=>a.cfield=='ys_zcgnfl').forEach(a=>{ a.changeVal=data.uniqueCode }) }
function BmzcjjflData(data) { fuzhulist.value.filter(a=>a.cfield=='ys_bmzcjjfl').forEach(a=>{ a.changeVal=data.uniqueCode }) }
function ZfzcjjflData(data) { fuzhulist.value.filter(a=>a.cfield=='ys_zfzcjjfl').forEach(a=>{ a.changeVal=data.uniqueCode }) }


const openSelect = (type) => {
  if(type=='cdept_id'){openSelectDeptPage(true, {dynamicTenantId: database.value})}
  if(type=='cperson_id'){openSelectPsnPage(true, {dynamicTenantId: database.value})}
  if(type=='ccus_id'){openCusMoalPopPage(true, {database: database.value,accId: accId.value,})}
  if(type=='csup_id'){openSupMoalPopPage(true, {database: database.value,accId: accId.value,})}
  if(type=='project_id'){openSelectProjectPage(true, {dynamicTenantId: database.value,projectClassCtl: '9999',})}
  if(type=='ys_ysly'){openYslyMoalPopPage(true, {database: database.value,accId: accId.value,})}
  if(type=='ys_zcgnfl'){openZcgnMoalPopPage(true, {database: database.value,accId: accId.value,})}
  if(type=='ys_bmzcjjfl'){openBmzcjjflMoalPopPage(true, {database: database.value,accId: accId.value,})}
  if(type=='ys_zfzcjjfl'){openZfzcjjflMoalPopPage(true, {database: database.value,accId: accId.value,})}
}
</script>
<style lang="less" scoped>
.vben-basic-title {
  color: rgb(1, 129, 226) !important;
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

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
}
:deep(.ant-tabs-content-holder){
  margin-top: -9px;
}
</style>
