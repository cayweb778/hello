<template>
  <div class="pingzheng_editor_header">
    <div style="position: fixed; z-index: 10000; left: 460px" class="pingzheng-header-title"
      >记账凭证
    </div>
    <PingZhengPageable />

    <div
      style="
        position: fixed;
        flex-direction: row;
        align-items: center;
        margin-left: -4px;
        justify-content: space-between;
        width: 95%;
        top: 1em;
        padding: 20px 46px;
        z-index: 10000;
      "
    >
      <div class="pingzheng-table-setting">
        <Select
          class="thisFenLuSelect"
          :value="'分录号'"
          size="small"
          :options="[
            { key: '1', label: '摘要' },
            { key: '1', label: '会计科目' },
            { key: '1', label: '借方金额' },
            { key: '1', label: '贷方金额' },
          ]"
          placeholder="分录号"
        />
        <InputSearch
          style="background: white; border-radius: 6px !important; width: 140px; text-align: center"
          size="small"
          placeholder=""
          @input="inputInput"
        />
      </div>
    </div>
    <div class="pingzheng-option">
      <div>

        <template v-if="tableData.settings.onlyShow">
          <span style="color:blue;font-weight: 900">
            {{tableData.options.optionType}}
          </span>
        </template>
       <template v-else>
         <Select
           size="small"
           style="font-size: 10px; width: 70px; text-align: center"
           class="pingzheng-font"
           v-model:value="tableData.options.optionType"
           :options="typeList.map((it) => ({ value: it.key, label: it.key }))"
           :disabled="tableData.settings.onlyShow"
         />
       </template>


        <span class="pingzheng-option-span-one" style="font-size: 10px">字 第</span>

        <template v-if="tableData.settings.onlyShow">
          <span style="color:blue;font-weight: 900">
            {{tableData.options.optionInoId}}
          </span>
        </template>
        <template v-else>
          <Input
            size="small"
            class="pingzheng-option-input"
            v-model:value="tableData.options.optionInoId"
            :disabled="tableData.settings.onlyShow"
            @blur="inoIdChange"
          />
        </template>

        <span class="pingzheng-option-span-two" style="font-size: 10px">号</span>
      </div>
      <div>
        <span class="pingzheng-option-span-three" style="font-size: 10px; margin-left: 10px"
          >附单据数:</span
        >

        <template v-if="tableData.settings.onlyShow">
          <span style="color:blue;font-weight: 900">
            {{tableData.options.optionDanJuQuantity}}
          </span>
        </template>
        <template v-else>
          <Input
            size="small"
            class="pingzheng-option-fdjs"
            style="width: 30px"
            v-model:value="tableData.options.optionDanJuQuantity"
            :disabled="tableData.settings.onlyShow"
          />
        </template>
      </div>
      <div style="left: 435px; width: 300px; top: 0" class="pingzheng-date">
        <span class="pingzheng-date-label" style="font-size: 10px; color: #838383">制单日期:</span>
        <template v-if="pingzhengData.settings.onlyShow">
          <span v-text="pingzhengData.options.optionDate"></span>
        </template>
        <template v-else>
          <DatePicker
            class="thisDatePick pingzheng-date-picker"
            :allowClear="false"
            format="YYYY-MM-DD"
            :disabled-date="disabledDate"
            size="small"
            style="font-size: 10px; width: 125px !important"
            :disabled="pingzhengData.settings.onlyShow"
            valueFormat="YYYY-MM-DD"
            @change="dateChange"
            v-model:value="pingzhengData.options.optionDate"
          />
        </template>
      </div>
    </div>

    <div class="pingzheng-s">
      <div class="pingzheng-s-one">
        <Tooltip v-for="it in aaaadsadsa" :title="it.title" placement="bottom">
          <component :is="it.ico" @click="it.fun" />
        </Tooltip>
      </div>
    </div>
    <Modal v-model:visible="liuLiang" title="现金流量录入" @ok="handleOk" v-if="liuLiang">
      <div>
        <Table :columns="columns" :data-source="data" bordered>
          <template #name="{ text }">
            <a>{{ text }}</a>
          </template>
          <!--        <template #title >Header</template>-->
          <!--        <template #footer>Footer</template>-->
        </Table>
      </div>
    </Modal>
  </div>
</template>
<script setup lang="ts">
  import dayjs, { Dayjs } from 'dayjs';
  import { createVNode, inject, onMounted, ref } from 'vue';
  import PingZhengPageable from '/@/components/pingzheng-fillin/layouts/header/components/PingZhengPageable.vue';

  import { Input, Select, DatePicker, Tooltip, Modal, Table } from 'ant-design-vue';

  import {
    ExpandOutlined,
    HddOutlined,
    FundViewOutlined,
    FlagOutlined,
    CalculatorOutlined,
    DollarOutlined,
    ControlOutlined,
    ExclamationCircleOutlined,
  } from '@ant-design/icons-vue';

  import { apiDataToShowModel } from '/@/components/pingzheng-fillin/hooks/models/datas/model';
  import { delTempData, findTempData } from '/@/api/pingzheng/pingzheng';
  import { findVoucherTypeAll } from '/@/api/record/system/voucher-type';
  import { usePingZhengResult } from '/@/components/pingzheng-fillin/hooks/okFun';
  import { useFullscreen } from '@vueuse/core';
  import {findNewestCloseMonth} from "/@/api/record/system/financial-settings";
  import {useCompanyOperateStore} from "/@/store/modules/operate-company";

  const InputSearch = Input.Search;

  const { toggle } = useFullscreen();

  const viewModel = inject('viewModel');
  const AppProviderLang = inject('AppProviderLang');

  const emit = defineEmits(['ok', 'aaaasasa', 'checkAll', 'delRows']);
  const liuLiang = ref();
  const datasourcePicker = inject('datasourcePicker');
  const closeMonth=ref('')

  onMounted(async()=>{

    closeMonth.value=await findNewestCloseMonth({

      accId: datasourcePicker.value.params.accId,
      year: datasourcePicker.value.params.year,
    })
  })
  const disabledDate = (current) => {
    const aa = datasourcePicker.value.params.iyear + '-01-01';
    return current && (current < dayjs(datasourcePicker.value.params.iyear + '-'+closeMonth.value.iperiodNum+'-01') || current > dayjs(aa).endOf('year'));
  };
  const aaaadsadsa = ref([
    {
      title: '辅助明细',
      ico: ControlOutlined,
      fun() {},
    },
    {
      title: '最新余额',
      ico: DollarOutlined,
      fun() {},
    },
    {
      title: '根据金额汇率本币平衡关系重算所在字段',
      ico: CalculatorOutlined,
      fun() {},
    },
    {
      title: '找平',
      ico: FlagOutlined,
      fun() {},
    },
    {
      title: '借贷转换',
      ico: FundViewOutlined,
      fun() {
        pingzhengData.rows.forEach((it) => {
          if (it.jieMoney.value == '0.00') {
            it.jieMoney.value = it.daiMoney.value;
            it.daiMoney.value = '0.00';
          } else {
            it.daiMoney.value = it.jieMoney.value;
            it.jieMoney.value = '0.00';
          }
        });
      },
    },
    {
      title: '正负转换',
      ico: HddOutlined,
      fun() {
        function abc(money) {
          const hello = parseFloat(money);
          return (hello * -1).toFixed(2);
        }

        pingzhengData.rows.map((it) => it.jieMoney).forEach((it) => (it.value = abc(it.value)));
        pingzhengData.rows.map((it) => it.daiMoney).forEach((it) => (it.value = abc(it.value)));
      },
    },
    {
      title: '放大/缩小',
      ico: ExpandOutlined,
      fun() {
        toggle();
      },
    },
  ]);
  const typeList = ref([]);

  const columns = [
    {
      title: '序号',
      dataIndex: 'name',
      slots: { customRender: 'name' },
    },
    {
      title: '分录序号',
      className: 'column-money',
      dataIndex: 'money',
    },
    {
      title: '*现金流量科目',
      dataIndex: 'address',
    },
    {
      title: '方向',
      dataIndex: 'address',
    },
    {
      title: '本币',
      dataIndex: 'address',
    },
  ];
  const data = [
    {
      key: '1',
      name: '1',
      money: '1',
      address: 'New York No. 1 Lake Park',
    },
    {
      key: '2',
      name: '2',
      money: '2',
      address: 'London No. 1 Lake Park',
    },
  ];
  const tableData = viewModel.value.instances[0].params.pingzhengData;
  const pingzhengData = viewModel.value.instances[0].params.pingzhengData;

  const { state, $pingZhengInoId } = viewModel.value.accountSetting;
  const usePageRouterApi = inject('usePageRouterApi');
  const { okFun, saveTempData, okAndShowFun, deletePingZhengByUniqueCode } =
    usePingZhengResult(tableData);

  function inoIdChange(e) {
    const pingZhengInoId = $pingZhengInoId();

    if (!pingZhengInoId) {
      tableData.value.options.optionInoId = state.pre.optionInoId;
      return;
    } else {
      state.pre.optionInoId = e;
    }
  }

  function handleOk() {}

  function dateChange(e) {
    const pingZhengDateState = $pingZhengDate();

    if (!pingZhengDateState) {
      pingzhengData.options.optionDate = state.pre.date;
      return;
    } else {
      state.pre.date = e;
    }
  }

  // 系统将删除之前未完成的凭证，且无法恢复，你确定要删除吗？
  function createOk() {
    Modal.confirm({
      title: () => '系统将删除之前未完成的凭证，且无法恢复，你确认要删除吗？',
      icon: () => createVNode(ExclamationCircleOutlined),
      okType: 'danger',
      okText: () => '确认删除',
      cancelText: () => '继续编辑',
      // icon: () => createVNode(ExclamationCircleOutlined),
      // content: () => createVNode('div', { style: 'color:red;' }, 'Some descriptions'),
      async onOk() {
        await usePageRouterApi(delTempData)({
          userId: AppProviderLang.value.getUserInfo.id,
          tenantId: AppProviderLang.value.getCurrentDataPicker.params.accountMode,
          iyear: AppProviderLang.value.getCurrentDataPicker.params.iyear,
        });
      },
      async onCancel() {
        liuLiang.value = true;
      },
      class: 'test',
    });
  }

  function createInfo() {
    Modal.confirm({
      title: () => '发现未保存的凭证，是否继续编辑该凭证？',
      okText: () => '继续编辑',
      cancelText: () => '放弃',
      async onOk() {
        const apiData = JSON.parse(result.data);
        viewModel.value.reloadPingZheng(
          apiDataToShowModel(apiData, { settings: { titleName: '填制凭证' } }),
        );
      },
      onCancel() {
        createOk();
      },
      class: 'test',
    });
  }

  async function hasTempData() {
    const result = await usePageRouterApi(findTempData)({
      userId: AppProviderLang.value.getUserInfo.id,
      tenantId: AppProviderLang.value.getCurrentDataPicker.params.accountMode,
      iyear: AppProviderLang.value.getCurrentDataPicker.params.iyear,
    });
    if (result == null) {
      return false;
    }
    return true;
  }
  async function getTypeList() {
    return (await findVoucherTypeAll()).items.map((item) => ({
      key: item.voucherTypeCode,
      label: item.voucherTypeCode,
    }));
  }
  onMounted(async () => {
    typeList.value = await getTypeList();

    if (hasTempData()) {
      return;
    }

    createInfo();
  });

  function inputInput($event) {
    console.log(1212312);
    emit('aaaasasa', $event);
  }


  const currentAccountSettingInfo=useCompanyOperateStore().getCurrentAccountSettingInfo
  window.debuggerInfo.currentAccountSettingInfo=currentAccountSettingInfo
  window.abb = AppProviderLang.value;
</script>
<style src="./assets/index.less" lang="less"></style>
<style src="./assets/index2.less" lang="less" scoped></style>
