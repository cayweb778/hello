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
      <div style="display: flex; color: #0096c7">
        <span style="line-height: 40px; font-size: 28px">
          <FormOutlined v-if="isEdit" style="font-size: 34px; font-weight: bold" />
          <FileSearchOutlined v-else style="font-size: 34px; font-weight: bold" />
        </span>
        <span style="line-height: 40px; font-size: 28px; margin-top: -5px">
          &nbsp;&nbsp;会计科目
        </span>
        <label>编码级次规则：{{jici}}</label>
      </div>
      <div
        style="
          display: inline-block;
          position: absolute;
          right: 40px;
          top: 10px;
          z-index: 100000;
          background: #ffffff;
        "
      >
        <img src="/cus.png" style="height: 66px; margin-right: 55px" />
      </div>
    </template>
    <div :class="isEdit ? 'nc-open-content' : 'nc-open-show-content'">
      <div class="open-content-up">
        <div style="margin-left: 20px">
          <a-radio-group
            v-model:value="formItems.bprogerty"
            v-model:disabled="bprogertyDisabled"
            style="width: 40%"
          >
            <a-radio value="1">
              <span style="color: #000000; font-weight: bold; font-size: 10px">借方</span>
            </a-radio>
            <a-radio value="0" style="margin-left: -10px">
              <span style="color: #000000; font-weight: bold; font-size: 10px">贷方</span>
            </a-radio>
          </a-radio-group>
          <br />
          <label>上级科目：</label>
          <TreeSelect
            style="width: 67%"
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
            <template #suffixIcon><CaretDownOutlined style="color: #666666" /></template>
          </TreeSelect>
          <br />
          <label>科目编码：</label>
          <a-input
            @blur="ccodeblur"
            @change="findByCclass"
            v-model:value.trim="formItems.ccode"
            v-model:disabled="ccodeDisabled"
            autocomplete="off"
            style="width: 25%"
            onkeyup="value=value.replace(/o/,'').replace(/l/,'').replace(/[\u4e00-\u9fa5]/, '')"
          />
          <label>科目类型：</label>
          <a-select
            v-model:value="formItems.cclass"
            style="width: 25%; text-align: center"
            allow-clear
          >
            <a-select-option :value="item.cclass" v-for="(item, i) in styleName">{{
              item.cclass
            }}</a-select-option>
            <template #suffixIcon><CaretDownOutlined style="color: #666666" /></template>
          </a-select>
          <br />
          <label>启用状态：</label>
          <a-select v-model:value="codeflag" style="width: 25%; text-align: center" allow-clear>
            <a-select-option value="1">是</a-select-option>
            <a-select-option value="0">否</a-select-option>
            <template #suffixIcon><CaretDownOutlined style="color: #666666" /></template>
          </a-select>
          <br /><br />
          <span style="margin-left: 23px">
            <label style="font-size: 18px; margin-left: 0; width: 150px">科目名称：</label>
            <a-input
              v-model:value.trim="formItems.ccodeName"
              placeholder="科目名称"
              autocomplete="off"
              class="abc"
              style="font-size: 18px; width: 60%"
            />
            <span class="red_span">*</span>
          </span>
          <br /><br />
        </div>
      </div>

      <div class="open-content-down">
        <a-tabs v-model:activeKey="activeKey" @change="tabChange" type="card">
          <a-tab-pane key="1" tab="指定科目">
            <div class="down-tab-content">
              <a-radio-group
                v-model:value="formItems.bcash"
                v-model:disabled="bcashflg"
                @change="bcashchecked"
                style="width: 50%"
              >
                <label>现金科目：&emsp;&emsp;&emsp;</label>
                <a-radio value="0" style="width: 80px; margin-left: -10px"> 否 </a-radio>
                <a-radio value="1" style="width: 80px; margin-left: -10px"> 是 </a-radio>
              </a-radio-group>
              <a-radio-group
                v-model:value="formItems.bbank"
                v-model:disabled="bbankflg"
                @change="bbankchecked"
                style="width: 50%"
              >
                <label>银行科目：&emsp;&emsp;&emsp;</label>
                <a-radio value="0" style="width: 80px; margin-left: -10px"> 否 </a-radio>
                <a-radio value="1" style="width: 80px; margin-left: -10px"> 是 </a-radio>
              </a-radio-group>
              <br />
              <a-radio-group
                v-model:value="formItems.bcashEquivalence"
                v-model:disabled="bcashEquivalenceflg"
                @change="bcashEquivalencechecked"
                style="width: 50%"
              >
                <label>现金等价物科目：</label>
                <a-radio value="0" style="width: 80px; margin-left: -10px"> 否 </a-radio>
                <a-radio value="1" style="width: 80px; margin-left: -10px"> 是 </a-radio>
              </a-radio-group>
              <a-radio-group v-model:value="formItems.xjll" style="width: 50%">
                <label>现金流量科目：&emsp;</label>
                <a-radio value="0" style="width: 80px; margin-left: -10px"> 否 </a-radio>
                <a-radio value="1" style="width: 80px; margin-left: -10px"> 是 </a-radio>
              </a-radio-group>
              <br />
              <a-radio-group
                v-model:value="formItems.bdaybook"
                v-model:disabled="bdaybookDisabled"
                style="width: 50%"
              >
                <label>日记账科目：&emsp;&emsp;</label>
                <a-radio value="0" style="width: 80px; margin-left: -10px"> 否 </a-radio>
                <a-radio value="1" style="width: 80px; margin-left: -10px"> 是 </a-radio>
              </a-radio-group>

            </div>
          </a-tab-pane>
          <a-tab-pane key="2" tab="辅助核算">
            <div class="down-tab-content">
              <a-table
                ref="tableRef"
                :pagination="false"
                :bordered="true"
                :class="'a-table-font-size-12'"
                :dataSource="fuzhuModalDataSource"
                :columns="fuzhuModalColumns"
                :row-selection="{
                  selectedRowKeys: selectedRowKeys,
                  onChange: onFuzhuHeSuanChange,
                  getCheckboxProps: rowSelection.getCheckboxProps,
                }"
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
              <a-radio-group
                v-model:value="formItems.controlled"
                v-model:disabled="controlledDisabled"
                style="width: 50%"
              >
                <label>业务受控科目：&emsp;</label>
                <a-radio value="0" style="width: 80px; margin-left: -10px"> 否 </a-radio>
                <a-radio value="1" style="width: 80px; margin-left: -10px"> 是 </a-radio>
              </a-radio-group>
            </div>
          </a-tab-pane>
          <a-tab-pane key="6" tab="预算会计" v-if="styleName.length==8">
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
      <div v-if="!isEdit">
        <a-button @click="closeModal(), handleClose()">关闭</a-button>
      </div>
      <div v-else>
        <a-button @click="closeModal(), handleClose()">取消</a-button>
        <a-button @click="handleOk()" :disabled="saveClick">确定</a-button>
      </div>
    </template>
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
  import { CaretDownOutlined, FormOutlined, FileSearchOutlined } from '@ant-design/icons-vue';
  import { onMounted, reactive, ref, toRaw, unref, watch } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import {
    company_findByCodeName,
    company_findByCodeNum,
    findAllByCurrency,
    company_findByLowerMaxCodeNum,
    company_findByCcode,
    company_treeCode,
    company_findBySuperCodeName,
    company_delFindByCodekemu,
    findAllByGorupCurrency,
    findByCodeNum,
    findByCodeName,
    findBySuperCodeName,
    company_findByIyearAndCcode,
  } from '/@/api/codekemu/codekemu';
  import { number } from 'vue-types';
  import {
    Checkbox as ACheckbox,
    Tabs as ATabs,
    Select as ASelect,
    Input as AInput,
    Statistic as AStatistic,
    Modal as AModal,
    Table as ATable,
    Radio as ARadio,
    message,
    TreeSelect,
    Modal,
  } from 'ant-design-vue';
  import { useRouteApi } from '/@/utils/boozsoft/datasource/datasourceUtil';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { treeCode } from '/@/api/acctemplate/acctemplate';
  import { findAllOrderById } from '/@/api/group/unit-mea';
  import { findFuzhuHesuanList } from '/@/api/record/system/origin-fuzhu-hesuan';
  import { findAllByAccGroup } from '/@/api/record/system/account';

  const ASelectOption = ASelect.Option;
  const AInputSearch = AInput.Search;
  const AStatisticCountdown = AStatistic.Countdown;
  const ATabPane = ATabs.TabPane;
  const ARadioGroup = ARadio.Group;
  const emit = defineEmits(['register', 'save']);
  const { createConfirm, createWarningModal, createMessage } = useMessage();

  const saveClick = ref(false);
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

  // 是否独立账套
  const independent = ref(false);
  // 计算单位list
  const unitmealist: any = ref([]);
  // 货币list
  const currencylist: any = ref([]);
  // 科目list
  const codelist: any = ref([]);
  const unit_meaList: any = ref([]);
  // 科目类型状态
  const cclassflg: any = ref(true);
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
  const tableRef: any = ref(null);

  const pagetype: any = ref('');
  const orgUnique: any = ref('');

  // 表单封装数据
  const formItems = reactive({
    id: '',
    controlled: '0',
    ccode: '',
    ccodeName: '',
    cclass: '',
    bprogerty: '1',
    bnum: '0',
    menterage: '0',
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
    fuzhu: '',
    yusuan: '',
    tenantId: '',
    oldCcode: '',
    superCodeBend: '',
    orgUnique: '',
    ysYsly: '0',
    ysZcgnfl: '0',
    ysZfzcjjfl: '0',
    ysBmzcjjfl: '0',
    cassItem: '0',
    bstock: '0',
  });
  // 没修改前表单封装数据
  let oldformItems = reactive({
    id: '',
    controlled: '0',
    ccode: '',
    ccodeName: '',
    cclass: '',
    bprogerty: '1',
    bnum: '0',
    menterage: '0',
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
    fuzhu: '',
    yusuan: '',
    oldCcode: '',
    superCodeBend: '',
  });
  // 现金科目
  const bcashflg: any = ref(false);
  // 银行科目
  const bbankflg: any = ref(false);
  // 现金等价物
  const bcashEquivalenceflg: any = ref(false);

  // 数据库模式名称
  const database = ref('');
  const accId = ref('');
  const iyear: any = ref('');
  // 辅助项checkbox禁用状态
  const fuzhuCheckflag: any = ref(false);
  // 'no' 只能修改名称、编码；其他属性与上级科目一致,'yes' 全部
  const allProperty: any = ref(true);
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
    },
  ]);
  const fuzhuModalDataSource = ref([]);
  const fuzhuModalDataSource2 = ref([]);
  const fuzhuModalColumns = ref([
    {
      title: '辅助项',
      dataIndex: 'cname',
      key: 'cname',
      width: '20%',
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
  const selectData = ref([]);
  // 辅助model
  const fuzhuModal = ref(false);
  // 确认选中
  let affirm_checked: any = ref([]);
  // 取消选中
  let cancel_checked: any = ref([]);

  const [register, { closeModal }] = useModalInner(async (data) => {
    pagetype.value = data.type;
    orgUnique.value = data.orientation;
    saveClick.value = false;
    iyear.value = data.data.iyear;
    jici.value = data.data.jici;
    independent.value = data.data.independent;
    standardName.value = data.data.standardName;
    standardUnique.value = data.data.uniqueAccStandard;
    templateID.value = data.data.templateId;
    templateName.value = data.data.templateName;
    styleName.value = data.data.styleName.filter((v) => v.cclass !== '全部');
    superiorCcode.value = data.data.data.superiorCcode === '0' ? '' : data.data.data.superiorCcode;
    accId.value = data.data.accId;

    codeflag.value = data.data.data.flag;
    formItems.flag = data.data.data.flag;
    formItems.id = data.data.data.id;
    formItems.ccode = data.data.data.ccode;
    formItems.oldCcode = data.data.data.ccode;
    formItems.ccodeName = data.data.data.ccodeName;
    formItems.menterage = data.data.data.menterage;
    formItems.currencyType = data.data.data.currencyType;
    formItems.bdaybook = data.data.data.bdaybook !== '1' ? '0' : '1';
    formItems.controlled = data.data.data.controlled !== '1' ? '0' : '1';
    formItems.bnum = data.data.data.bnum !== '1' ? '0' : '1';
    formItems.currency = data.data.data.currency !== '1' ? '0' : '1';
    formItems.bprogerty = data.data.data.bprogerty;
    formItems.cclass = data.data.data.cclass;
    formItems.bend = data.data.data.bend;
    formItems.iyear = data.data.data.iyear;
    formItems.tenantId = data.data.data.tenantId;
    formItems.orgUnique = data.data.data.orgUnique;
    formItems.pxjz = data.data.data.pxjz !== '1' ? '0' : '1';
    formItems.xjll = data.data.data.xjll !== '1' ? '0' : '1';
    formItems.cyfx = data.data.data.cyfx !== '1' ? '0' : '1';
    formItems.superCodeBend = data.data.data.superCodeBend;
    formItems.lowerControl = data.data.data.lowerControl !== '1' ? '0' : '1';
    formItems.fuzhuControl = data.data.data.fuzhuControl !== '1' ? '0' : '1';
    // 5个辅助项
    formItems.bperson = data.data.data.bperson;
    formItems.bcus = data.data.data.bcus !== '1' ? '0' : '1';
    formItems.bsup = data.data.data.bsup !== '1' ? '0' : '1';
    formItems.bdept = data.data.data.bdept !== '1' ? '0' : '1';
    formItems.bitem = data.data.data.bitem !== '1' ? '0' : '1';
    // 现金...3个
    formItems.bcash = data.data.data.bcash !== '1' ? '0' : '1';
    formItems.bbank = data.data.data.bbank !== '1' ? '0' : '1';
    formItems.bcashEquivalence = data.data.data.bcashEquivalence !== '1' ? '0' : '1';

    last_jici.value = data.data.data.igrade;
    allProperty.value = data.all === 'yes' ? true : false;
    isEdit.value = data.isEdit;

    oldformItems=data.data.data

    // 现金...3个
    if (data.data.data.bcash !== '0' && data.data.data.bcash !== null) {
      bbankflg.value = true;
      bcashEquivalenceflg.value = true;
    } else if (data.data.data.bbank !== '0' && data.data.data.bbank !== null) {
      bcashflg.value = true;
      bcashEquivalenceflg.value = true;
    } else if (
      data.data.data.bcashEquivalence !== '0' &&
      data.data.data.bcashEquivalence !== null
    ) {
      bcashflg.value = true;
      bbankflg.value = true;
    }

    treeCode(data.data.uniqueAccStandard, data.data.templateId).then((res) => {
      codelist.value = res;
    });

    // 获取所有计算单位
    // findAllOrderById().then((res) => {
    //   unit_meaList.value = res;
    // });

    // 获取所有国币
    findAllByGorupCurrency().then((res) => {
      currencylist.value = res;
    });

    // 组织辅助核算自定义
    if (data.type === 'zz') {
      fuzhuModalDataSource.value = [];
      let orgHs = await findFuzhuHesuanList({ originId: data.orientation });
      orgHs.items.forEach((i, index) => {
        let temp: any = fuzhuModalDataSource2.value.filter((v) => v.cname == i.cname);
        fuzhuModalDataSource.value.push({
          key: temp[0].key,
          ccode: temp[0].ccode,
          cname: temp[0].cname,
          cdfine: temp[0].cfield,
        });
      });
    }

    if (data.data.data.fuzhu !== null && data.data.data.fuzhu !== '') {
      for (let i = 0; i < data.data.data.fuzhu.split(',').length; i++) {
        let list = fuzhuModalDataSource.value.filter(
          (item) => item.cname == data.data.data.fuzhu.split(',')[i],
        );
        if (list.length > 0) {
          selectedRowKeys.value.push(list[0].key);
          selectData.value.push({
            key: i,
            ccode: list[0].ccode,
            cname: list[0].cname,
            cdfine: list[0].cdfine,
          });
        }
      }
    }
  });

  const rowSelection = {
    getCheckboxProps: (record) => ({
      disabled: fuzhuCheckflag.value,
    }),
  };

  // 选中数量核算单位
  const treeselected = async (newValue: any) => {
    // 获取最新科目
    const ccode = newValue === undefined ? newValue : newValue;
    // 获取上级科目信息
    const code = await useRouteApi(company_findByCcode, { schemaName: database })({ ccode: ccode });
    const igrade = code.igrade;
    // 开启下级控制
    super_lowerControl.value = code.lowerControl === '1';
    if (code.lowerControl === '1') {
      formItems.ccode = '';
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
      const newccode = await useRouteApi(company_findByLowerMaxCodeNum, { schemaName: database })({
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
    bbankflg.value = val.target.value == '1';
    bcashEquivalenceflg.value = val.target.value == '1';
  };
  // 选中银行
  const bbankchecked = (val: any) => {
    bcashflg.value = val.target.value == '1';
    bcashEquivalenceflg.value = val.target.value == '1';
  };
  // 选中现金等价物
  const bcashEquivalencechecked = (val: any) => {
    bbankflg.value = val.target.value == '1';
    bcashflg.value = val.target.value == '1';
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
      formItems.ccode = '';
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
          const sum = await findByCodeNum(standardUnique.value, templateID.value, formItems.ccode);

          var sumname;
          if (superiorCcode.value === '') {
            sumname = await findByCodeName(
              standardUnique.value,
              templateID.value,
              formItems.ccodeName,
            );
          } else {
            sumname = await findBySuperCodeName(
              standardUnique.value,
              templateID.value,
              formItems.ccodeName,
              superiorCcode.value,
              iyear.value,
            );
          }
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
    if (formItems.ccode !== oldformItems.ccode) {
      // 科目被使用不能修改编码
      const delFirstMsg = await useRouteApi(company_delFindByCodekemu, { schemaName: database })({
        ccode: oldformItems.ccode,
        iyear: iyear.value,
        databasenum: database.value,
      });
      if (delFirstMsg.code === '200') {
        return true;
      } else {
        if (delFirstMsg.msg === 'accvoucher') {
          createConfirmPop(oldformItems.ccode + '科目已做凭证,不能修改科目编码');
          return false;
        }
        if (delFirstMsg.msg === 'xjll') {
          createConfirmPop(oldformItems.ccode + '科目已被设置成现金流量,不能修改科目编码');
          return false;
        }
        // if (delFirstMsg.msg === 'zdkm') {
        //   createConfirmPop(oldformItems.ccode + '科目已被指定,不能修改科目编码');
        //   return false;
        // }
      }
    }
    return true;
  };
  // 根据编码首位查询类型
  const findByCclass = () => {
    formItems.cclass = styleName.value.filter(
      (v) => v.order === formItems.ccode.charAt(0),
    )[0].cclass;
  };

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
    if (formItems.bnum === '1') {
      if (formItems.menterage === '') {
        createConfirmPop('请选择计量单位！');
        return false;
      }
    }
    if (formItems.currency === '1') {
      if (formItems.currencyType === '') {
        createConfirmPop('请选择外币币种！');
        return false;
      }
    }

    // 判断 科目类型是否 与选择的一致
    if (oldformItems.cclass !== formItems.cclass) {
      let findByCclass = styleName.value.filter((v) => v.order === formItems.ccode.charAt(0));
      if (findByCclass.length == 0 || findByCclass[0].cclass !== formItems.cclass) {
        createConfirm({
          iconType: 'warning',
          title: '警告',
          content: '科目类型不一致,确定添加吗',
          onOk: async () => {
            save123();
          },
        });
      } else {
        save123();
      }
    } else {
      save123();
    }


  }

  async function save123() {
    formItems.superiorCcode = superiorCcode.value.split('_')[0];
    formItems.igrade = last_jici.value;
    formItems.uniqueAccStandard = standardUnique.value;
    formItems.templateId = templateID.value;
    formItems.flag = codeflag.value;
    formItems.uniqueAccStandardName = standardName.value;
    formItems.templateName = templateName.value;
    formItems.superCodeBend = superCodeBend.value;

    // 获取是否预算科目标志
    let yusuanflag = styleName.value.filter((x) => {
      return x.cclass === formItems.cclass;
    });
    formItems.yusuan = yusuanflag[0].flagYusuan === '1' ? '1' : '0';

    let fzccode = [];
    if (selectData.value.length > 0) {
      for (let i = 0; i < selectData.value.length; i++) {
        fzccode.push(selectData.value[i].cdfine);
      }
    }
    formItems.fuzhu = fzccode.length > 0 ? fzccode.join(',') : '';
    if (await ccodeblur()) {
      saveClick.value = true;

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

      let oldfuzhuhesuan=oldformItems.fuzhu==null?'':oldformItems.fuzhu
      if(oldfuzhuhesuan!==formItems.fuzhu){
        log+='辅助核算【'+oldformItems.fuzhu+','+formItems.fuzhu+'】,'
      }
      if(oldformItems.currencyType!==formItems.currencyType){
        log+='外币核算【'+oldformItems.currencyType+','+formItems.currencyType+'】,'
      }
      if(oldformItems.menterage!==formItems.menterage){
        log+='数量核算【'+oldformItems.menterage+','+formItems.menterage+'】,'
      }
      if(log!==''){
        let arrlog='【修改了一条会计科目】,'+log.substring(0,log.length-1)
        console.log(arrlog)
      }

      emit('save', toRaw(formItems));
      closeModal();
    }
  }

  const onFuzhuHeSuanChange = (a, b) => {
    selectedRowKeys.value = a;
    selectData.value = b.filter((item) => a.indexOf(item.key) != -1);
    if (a.length > 10) {
      createConfirmPop('不能超过10个自定义项');
      selectedRowKeys.value = [];
      selectData.value = [];
    }
  };

  function cancelModel() {
    selectedRowKeys.value = [];
    selectData.value = [];
    fuzhuModalDataSource.value = [];
  }

  function modalOk() {
    fuzhuModal.value = false;
  }

  async function findByAllProperty(data) {
    if (!allProperty.value) {
      formItems.ccode = '';
      formItems.ccodeName = '';
      formItems.menterage = data.menterage;
      formItems.currencyType = data.currencyType;
      formItems.bdaybook = data.bdaybook;
      formItems.controlled = data.controlled;
      formItems.bnum = data.bnum;
      formItems.currency = data.currency;
      formItems.bprogerty = data.bprogerty;
      formItems.cclass = data.cclass;
      formItems.bend = data.bend;
      formItems.iyear = data.iyear;
      formItems.pxjz = data.pxjz;
      formItems.xjll = data.xjll;
      formItems.cyfx = data.cyfx;

      // 5个辅助项
      formItems.bperson = data.bperson;
      formItems.bcus = data.bcus;
      formItems.bsup = data.bsup;
      formItems.bdept = data.bdept;
      formItems.bitem = data.bitem;

      lowerDisabled.value = true;
      fuzhutDisabled.value = true;
      bnumDisabled.value = true;
      currencyDisabled.value = true;
      bcashEquivalenceflg.value = true;
      bbankflg.value = true;
      bcashflg.value = true;
      bdaybookDisabled.value = true;
      cyfxDisabled.value = true;
      pxjzDisabled.value = true;
      controlledDisabled.value = true;
      bprogertyDisabled.value = true;

      // 现金...3个
      formItems.bcash = data.bcash;
      formItems.bbank = data.bbank;
      formItems.bcashEquivalence = data.bcashEquivalence;

      if (data.fuzhu !== null && data.fuzhu !== '') {
        for (let i = 0; i < data.fuzhu.split(',').length; i++) {
          let list = fuzhuModalDataSource.value.filter(
            (item) => item.cname === data.fuzhu.split(',')[i],
          );
          if (list.length > 0) {
            selectedRowKeys.value.push(list[0].key);
            selectData.value.push({
              key: i,
              ccode: list[0].ccode,
              cname: list[0].cname,
              cdfine: list[0].cdfine,
            });
          }
        }
      }
    } else {
      formItems.ccode = '';
      formItems.ccodeName = '';
      formItems.currencyType = '';
      formItems.menterage = '';
      formItems.bdaybook = '0';
      formItems.cyfx = '0';
      formItems.pxjz = '0';
      formItems.controlled = '0';

      formItems.bnum = '0';
      formItems.currency = '0';
      formItems.bcashEquivalence = '0';
      formItems.bbank = '0';
      formItems.bcash = '0';
      formItems.bdept = '0';
      formItems.bperson = '0';
      formItems.bcus = '0';
      formItems.bsup = '0';
      formItems.bitem = '0';

      bprogertyDisabled.value = false;
      controlledDisabled.value = false;
      bdaybookDisabled.value = false;
      bcashflg.value = false;
      bbankflg.value = false;
      bcashEquivalenceflg.value = false;
      lowerDisabled.value = false;
      fuzhutDisabled.value = false;
      currencyDisabled.value = false;
      bnumDisabled.value = false;
      selectData.value = [];
      selectedRowKeys.value = [];
    }
  }

  const superiorCcodeChange = async (val) => {
    var sumname;
    if (superiorCcode.value === '') {
      sumname = await useRouteApi(company_findByCodeName, { schemaName: database })({
        uniqueAccStandard: standardUnique.value,
        templateId: templateID.value,
        ccode: formItems.ccodeName,
      });
    } else {
      sumname = await useRouteApi(company_findBySuperCodeName, { schemaName: database })({
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
    const code = await useRouteApi(company_findByCcode, { schemaName: database })({ ccode: val });

    if (!allProperty.value) {
      createConfirm({
        iconType: 'warning',
        title: '警告',
        content: '此科目已做凭证或期初，原数据将会覆盖。确定继续操作吗？',
        onOk: async () => {
          findByAllProperty(code);
          getNewCcode(code, val);
        },
      });
    } else {
      findByAllProperty('');
    }
  };
  async function getNewCcode(code, val) {
    const igrade = code.igrade;
    if (!independent.value) {
      // 开启下级控制
      super_lowerControl.value = code.lowerControl === '1';
      if (code.lowerControl === '1') {
        formItems.ccode = '';
        createConfirmPop('新增下级科目控制已开启,不能增加下级科目！');
        return false;
      }
    }
    superCodeBend.value = code.bend;
    formItems.cclass = code.cclass;
    formItems.bprogerty = code.bprogerty;
    cclassflg.value = true;
    last_jici.value = parseInt(igrade) + 1;
    const arr = jici.value.replaceAll('-', '').substring(0, last_jici.value);
    var code_length = 0; // 符合规则的科目长度
    for (let i = parseInt(igrade); i < arr.length; i++) {
      code_length += parseInt(arr[i]);
    }
    // 编码_级次
    const newccode = await useRouteApi(company_findByLowerMaxCodeNum, { schemaName: database })({
      superiorCcode: val,
      code_length: code_length,
    });
    formItems.ccode = newccode;
    // 获取上级科目级次
    jici2.value = igrade;
  }
  function handleClose() {
    // 辅助model
    fuzhuModal.value = false;
    // 是否独立账套
    independent.value = false;
    // 货币list
    currencylist.value = [];
    // 科目list
    codelist.value = [];
    // 科目类型状态
    cclassflg.value = true;
    // 级次
    jici.value = '';
    // 级次:增加科目用到-上级科目级次
    jici2.value = 1;
    // 级次:增加科目用到-下级科目级次
    last_jici.value = 1;
    // 科目类型
    styleName.value = [];
    selectData.value = [];
    // 本位币
    currency.value = '人民币';
    // 上级科目
    superiorCcode.value = '';
    lowerControl.value = false;
    fuzhuControl.value = false;

    // 上级科目-科目下级控制
    super_lowerControl.value = false;
    // 表单封装数据
    formItems.ccode = '';
    formItems.ccodeName = '';
    formItems.cclass = '';
    formItems.bprogerty = '1';
    formItems.menterage = '0';
    formItems.bnum = '0';
    formItems.currencyType = '';
    formItems.igrade = '';
    formItems.uniqueAccStandard = '';
    formItems.superiorCcode = '';
    formItems.templateId = '';
    formItems.lowerControl = '0';
    formItems.fuzhuControl = '0';
    formItems.uniqueAccStandardName = '';
    formItems.templateName = '';
    formItems.bend = '';
    formItems.flag = '';
    formItems.iyear = '';
    formItems.fuzhu = '';
    formItems.yusuan = '';
    formItems.bdaybook = '0';
    formItems.controlled = '0';
    formItems.pxjz = '0';
    formItems.xjll = '0';
    formItems.cyfx = '0';

    formItems.currency = '0';
    formItems.bcash = '0';
    formItems.bbank = '0';
    formItems.bcashEquivalence = '0';
    formItems.bperson = '0';
    formItems.bcus = '0';
    formItems.bsup = '0';
    formItems.bdept = '0';
    formItems.bitem = '0';
    return true;
  }
  function createConfirmPop(text) {
    createConfirm({
      iconType: 'warning',
      title: '警告',
      content: text,
      onOk: async () => {},
    });
  }
  function checkedText(val) {
    let textPop = '';
    if (val) {
      textPop =
        '该科目已经使用，添加辅助核算项可能会导致辅助账查询异常，建议你在下一年期初时再进行修改，你确定要继续添加该科目辅助核算项吗？';
    } else {
      textPop =
        '该科目辅助核算项已经使用，取消辅助核算项可能会导致辅助账查询异常，建议你在下一年期初时再进行修改，你确定要继续取消该科目辅助核算项吗？';
    }
    createConfirmPop(textPop);
  }
  function tabChange() {
    if (activeKey.value == '2') {
      tableRef.value.$el.style.setProperty('width', '500px');
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
  .ant-modal-header {
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
    .abc {
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
  :deep(.ant-checkbox) {
    border: 1px solid #2f2a2a;
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
  .nc-open-show-content {
    input {
      width: 30%;
      border: none !important;
      border-bottom: 1px solid #bdb9b9 !important;
      font-weight: bold;
      font-size: 14px;
    }

    .abc {
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
      padding: 5px 30px;
      color: #535353;
      font-size: 13px;
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
  .divtop {
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
</style>
