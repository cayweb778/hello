<template>
  <div class="app-container">
    <div class="app-container-head">
      <img src="/img/menu/index_dept.png" />

      <span style="margin-left: 10px"><b>辅助期初余额</b></span>
      <div style="display: inline; margin-left: 20px">
        <a-select v-model:value="iyearselected" style="width: 120px">
          <a-select-option :value="item.key" v-for="(item, i) in iyearlist">{{
            item.key
          }}</a-select-option>
        </a-select>
        <span style="margin-left: 30px">
          <span>辅助科目</span>
          <a-select v-model:value="ccodeselected" style="width: 120px">
            <a-select-option :value="item.ccode" v-for="(item, i) in codelist">{{
              item.ccodeName
            }}</a-select-option>
          </a-select>
        </span>
      </div>
      <div class="ant-btn-group" data-v-a1ccd506="" style="float: right">
        <button type="button" class="ant-btn" @click="editflg(edittext)"
          ><span>{{ edittext }}</span></button
        >
        <span>
          <button type="button" class="ant-btn" @click="setMdMcPop"><span>添加</span></button>
        </span>
      </div>
      <div>
        <div style="float: right; position: relative">
          <label style="font-size: 14px">检索条件：</label>
          <a-input-search placeholder="" style="width: 200px; border-radius: 4px" />
        </div>
      </div>
      <div style="clear: both"></div>
      <BasicTable
        :row-selection="{ type: 'checkbox' ,fixed: true}"
        :columns="tableColumns"
        :dataSource="tableDataAll"
        :loading="loading"
        :pagination="false"
        :bordered="true"
        @register="registerTable"
      >
        <template #ccode="{ record }">
          <span v-if="record.flag === '0'">{{ record.ccode }}</span>
        </template>
        <template #bprogerty="{ record }">
          {{ record.bprogerty === '1' ? '借方' : record.bprogerty === '0' ? '贷方' : '' }}
        </template>
        <template #md="{ record }">
          <a-button
            type="link"
            :disabled="!roweditflg"
            style="float: right"
            @click="setModal1Visible(record, 'md')"
          >
            <FormOutlined v-if="record.bend === '1'" />
          </a-button>
          <span v-if="record.md > 1" style="float: right; margin-right: 10px">{{
            money(record.md)
          }}</span>
        </template>
        <template #mc="{ record }">
          <a-button
            type="link"
            :disabled="!roweditflg"
            style="float: right"
            @click="setModal1Visible(record, 'mc')"
          >
            <FormOutlined v-if="record.bend === '1'" />
          </a-button>
          <span v-if="record.mc > 1" style="float: right; margin-right: 10px">{{
            money(record.mc)
          }}</span>
        </template>
      </BasicTable>
    </div>
    <!--    余额弹框-->
    <a-modal
      title="                 "
      v-model:visible="modal1Visible"
      centered
      @ok="modal1Visible = false"
    >
      <template #footer>
        <a-button
          key="back"
          type="danger"
          @click="(modal1Visible = false), (md = formatmoney(0)), (mc = formatmoney(0))"
          ><CloseOutlined
        /></a-button>
        <a-button key="submit" type="primary" @click="submitfuzhu"><CheckOutlined /></a-button>
      </template>
      <br />
      <p v-if="psnflag" style="margin-left: 10px">
        <label>个人</label>&nbsp;&nbsp;
        <a-select v-model:value="personselected" style="width: 120px" show-search>
          <a-select-option :value="item.uniqueCode" v-for="item in personlist">
            {{ item.psnName }}
          </a-select-option>
        </a-select>
      </p>
      <p v-if="deptflag" style="margin-left: 10px">
        <label>部门</label>&nbsp;&nbsp;
        <a-select v-model:value="deptselected" style="width: 120px" show-search>
          <a-select-option :value="item.uniqueCode" v-for="item in deptlist">
            {{ item.deptName }}
          </a-select-option>
        </a-select>
      </p>
      <p v-if="cusflag" style="margin-left: 10px">
        <label>客户</label>&nbsp;&nbsp;
        <a-select v-model:value="cusselected" style="width: 120px" show-search>
          <a-select-option :value="item.uniqueCode" v-for="item in cuslist">
            {{ item.custName }}
          </a-select-option>
        </a-select>
      </p>
      <p v-if="supflag" style="margin-left: 10px">
        <label>供应商</label>&nbsp;&nbsp;
        <a-select v-model:value="supselected" style="width: 120px" show-search>
          <a-select-option :value="item.uniqueCode" v-for="item in suplist">
            {{ item.custName }}
          </a-select-option>
        </a-select>
      </p>
      <p v-if="itemflag" style="margin-left: 10px">
        <label>项目</label>&nbsp;&nbsp;
        <a-select v-model:value="itemselected" style="width: 120px" show-search>
          <a-select-option :value="item.uniqueCode" v-for="item in itemlist">
            {{ item.projectName }}
          </a-select-option>
        </a-select>
      </p>

      <p style="margin-left: 10px">
        <label>借方金额</label>&nbsp;&nbsp;
        <a-input
          v-model:value="md"
          @keyup="md = md.replace(/[^\d{1,}\.\d{1,}|\d{1,}]/g, '')"
          @blur="
            (md =
              md == ''
                ? '0.00'
                : parseFloat(md)
                    .toFixed(2)
                    .replace(/\d+/, function (n) {
                      return n.replace(/(\d)(?=(\d{3})+$)/g, function ($1) {
                        return $1 + ',';
                      });
                    })),
              (mc = formatmoney(0))
          "
          @focus="md = md == '0.00' ? '' : md.replace(/,/g, '')"
          autocomplete="off"
          style="
            width: 40%;
            text-align: right;
            margin-left: 10px;
            border-top: hidden;
            border-left: hidden;
            border-right: hidden;
          "
        />
      </p>
      <p style="margin-left: 10px">
        <label>贷方金额</label>&nbsp;&nbsp;
        <a-input
          v-model:value="mc"
          @keyup="mc = mc.replace(/[^\d{1,}\.\d{1,}|\d{1,}]/g, '')"
          @blur="
            (mc =
              mc == ''
                ? '0.00'
                : parseFloat(mc)
                    .toFixed(2)
                    .replace(/\d+/, function (n) {
                      return n.replace(/(\d)(?=(\d{3})+$)/g, function ($1) {
                        return $1 + ',';
                      });
                    })),
              (md = formatmoney(0))
          "
          @focus="mc = mc == '0.00' ? '' : mc.replace(/,/g, '')"
          autocomplete="off"
          style="
            width: 40%;
            text-align: right;
            margin-left: 10px;
            border-top: hidden;
            border-left: hidden;
            border-right: hidden;
          "
        />
      </p>
    </a-modal>
  </div>
</template>
<script setup lang="ts">
  import {
    CloseOutlined,
    CheckOutlined,
    CaretDownFilled,
    ExclamationCircleOutlined,
    FormOutlined,
    DeleteOutlined,
    CheckCircleOutlined,
    CloseCircleOutlined,
    SettingFilled,
    SyncOutlined,
    PicLeftOutlined,
    EditFilled,
    PieChartFilled,
    FilterFilled,
  } from '@ant-design/icons-vue';
  import { message, Modal } from 'ant-design-vue';
  import { BasicTable, useTable } from '/@/components/Table';
  import {
    Select as ASelect,
    Input as AInput,
    Checkbox as ACheckbox,
    Modal as AModal,
    Upload as AUpload,
    Row as ARow,
    Col as ACol,
    Statistic as AStatistic,
  } from 'ant-design-vue';
  const ASelectOption = ASelect.Option;
  const AInputSearch = AInput.Search;
  const AStatisticCountdown = AStatistic.Countdown;
  import { useModal } from '/@/components/Modal';
  import { findAllIperiodIyear } from '/@/api/subjectInitialBalance/subjectInitialBalance';
  import {
    findByFuzhuCode,
    findByFuzhuInitalBalance,
    findByItem,
    findBySup,
    findByCus,
    findByDept,
    findByPerson,
    saveFuzhuInitalBalance,
  } from '/@/api/fuzhuInitialBalance/fuzhuInitialBalance';
  import { onMounted, ref } from 'vue';

  const roweditflg = ref(false);
  const edittext = ref('编辑');
  // 录入期初弹框
  const modal1Visible = ref(false);
  // 加载动画
  const loading = ref(false);
  // 默认年度
  const iyearselected = ref('');
  const iyearlist = ref([]);
  // 科目list
  const codelist = ref([]);
  const codeInfo = ref([]);
  const ccodeselected = ref('');
  // 动态表头、内容
  const tableDataAll = ref([]);
  const tableColumns = ref([]);
  // 人员表list
  const psnflag = ref(false);
  const personlist = ref([]);
  const personselected = ref('');
  // 部门表list
  const deptflag = ref(false);
  const deptlist = ref([]);
  const deptselected = ref('');
  // 客户list
  const cusflag = ref(false);
  const cuslist = ref([]);
  const cusselected = ref('');
  // 供应商list
  const supflag = ref(false);
  const suplist = ref([]);
  const supselected = ref('');
  // 项目list
  const itemflag = ref(false);
  const itemlist = ref([]);
  const itemselected = ref('');
  // 借贷方金额
  const md = ref(formatmoney(0));
  const mc = ref(formatmoney(0));

  // 这是示例组件
  const [registerImportPage, { openModal: openImprotPage }] = useModal();
  const [registerTable, { reload, getSelectRows }] = useTable({});

  // 提交
  async function submitfuzhu() {
    if (parseFloat(md.value) === 0 && parseFloat(mc.value) === 0) {
      message.error('借贷双方不能等于0');
      return;
    } else {
      await saveFuzhuInitalBalance(
        null,
        iyearselected.value,
        ccodeselected.value,
        personselected.value,
        deptselected.value,
        cusselected.value,
        supselected.value,
        itemselected.value,
        md.value.replace(/,/g, ''),
        mc.value.replace(/,/g, '')
      );
      modal1Visible.value = false;
    }
  }
  // 金额格式化
  function formatmoney(val: any) {
    if (val == null) val = '';
    val = val.toString().replace(/\$|\,/g, '');
    if (isNaN(val)) {
      val = '0';
    }
    const sign = val === (val = Math.abs(val));
    val = Math.floor(val * 100 + 0.50000000001);
    let cents = val % 100;
    val = Math.floor(val / 100).toString();
    if (cents < 10) {
      cents = '0' + cents;
    }
    for (let i = 0; i < Math.floor((val.length - (1 + i)) / 3); i++) {
      val =
        val.substring(0, val.length - (4 * i + 3)) + ',' + val.substring(val.length - (4 * i + 3));
    }
    return (sign ? '' : '') + val + '.' + cents;
  }
  // 设置期初
  async function setMdMcPop() {
    md.value = formatmoney(0);
    mc.value = formatmoney(0);

    modal1Visible.value = true;
    // 是否显示
    psnflag.value = codeInfo.value.bperson === '1';
    deptflag.value = codeInfo.value.bdept === '1';
    cusflag.value = codeInfo.value.bcus === '1';
    supflag.value = codeInfo.value.bsup === '1';
    itemflag.value = codeInfo.value.bitem === '1';

    // 获取 人员表
    if (codeInfo.value.bperson === '1') {
      const person = await findByPerson();
      if (person.length > 0) {
        personlist.value = person;
        personselected.value = person[0].uniqueCode;
      }
    }

    // 获取 部门表
    if (codeInfo.value.bdept === '1') {
      const dept = await findByDept();
      if (dept.length > 0) {
        deptlist.value = dept;
        deptselected.value = dept[0].uniqueCode;
      }
    }

    // 获取 客户表
    if (codeInfo.value.bcus === '1') {
      const cus = await findByCus();
      if (cus.length > 0) {
        cuslist.value = cus;
        cusselected.value = cus[0].uniqueCode;
      }
    }
    // 获取 供应商表
    if (codeInfo.value.bsup === '1') {
      const sup = await findBySup();
      if (sup.length > 0) {
        suplist.value = sup;
        supselected.value = sup[0].uniqueCode;
      }
    }
    // 获取 项目表
    if (codeInfo.value.bitem === '1') {
      const item = await findByItem();
      if (item.length > 0) {
        itemlist.value = item;
        itemselected.value = item[0].uniqueCode;
      }
    }
  }
  // 编辑期初前查询科目表是否锁定
  const editflg = async (data: any) => {};
  onMounted(async () => {
    // 会计期间年度
    const res: any = await findAllIperiodIyear();
    for (let i = 0; i < res.length; i++) {
      iyearlist.value.push({
        key: res[i],
        label: res[i],
      });
    }
    iyearselected.value = res[0];

    const a = await findByFuzhuCode(iyearselected.value);
    if (a.length === 0) {
      message.error('没有找到辅助项科目,请前往会计科目中设置。');
      return;
    } else {
      codelist.value = a;
      ccodeselected.value = a[0].ccode;

      loading.value = true;
      const data = await findByFuzhuInitalBalance(ccodeselected.value, iyearselected.value);
      tableColumns.value = data.tableColumns;
      codeInfo.value = data.codeInfo;
      loading.value = false;
    }
  });
</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>
