<template>
  <div>
    <div style="display: flex; flex-direction: row; align-items: center">
      <Popover placement="bottom">
        <Button
          class="hello-btn"
          style="color: rgb(0, 150, 199); font-weight: 400"
          @click="buttonEvents.updateOkFun"
          >保存修改</Button
        >
        <template #content>
          <div>
            <div>摘要[<span style="color: red">未检查</span>]</div>
            <div>会计科目[<span style="color: red">未检查</span>]</div>
            <div>辅助核算[<span style="color: red">未检查</span>]</div>
            <div style="text-align: center" type="primary">
              <Button>开始检查</Button>
            </div>
          </div>
        </template>
      </Popover>

      <!--            <Button style="font-size:15px" @click="emit('ok')" danger>放弃</Button>-->
      <Button
        style="color: rgb(0, 150, 199); font-weight: 400; background: #abecab; font-size: 15px"
        @click="emit('ok')"
        >审核
      </Button>
      <Button
        style="color: rgb(0, 150, 199); font-weight: 400; background: #abecab; font-size: 15px"
        @click="emit('ok')"
        >记账
      </Button>

      <Button style="color: rgb(0, 150, 199); font-weight: 400; font-size: 15px" @click="abc()"
        >流量</Button
      >
      <!--      <Popover placement="bottom">-->
      <!--/*        <Button style="    color: rgb(0, 150, 199);font-weight:400;font-size:15px" @click="emit('ok')">更多</Button>*/-->
      <!--        <template #content>-->
      <!--        </template>-->
      <!--      </Popover>-->

      <Button
        style="color: rgb(0, 150, 199); font-weight: 400; font-size: 15px"
        @click="buttonEvents.backEdit"
        >放弃</Button
      >

      <div style="float: right" v-if="false">
        <div
          class="ant-btn-group"
          style="margin-left: 30px"
          v-if="
            tableData.settings.titleName === '查看凭证' ||
            tableData.settings.titleName === '复制凭证' ||
            tableData.settings.titleName === '冲销凭证'
          "
        >
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="openQueryPage()"
            ><span>修改</span></button
          >
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="ssss"
            ><span>复制</span></button
          >
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="accvoucherStore.verifyAccvoucherTemplateSum(() => openImportPage())"
            ><span>冲销</span></button
          >
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            ><span>删除</span></button
          >
          <Popover placement="bottom">
            <template #content>
              <span class="group-btn-span" @click="startChangeStatus(true, 1)"
                ><LockTwoTone />&nbsp;作废凭证</span
              ><br />
              <span class="group-btn-span" @click="startChangeStatus(false, 1)"
                ><UnlockTwoTone />&nbsp;取消作废</span
              >
            </template>
            <button type="button" class="ant-btn ant-btn-me"><span>审核</span></button>
          </Popover>
          <Popover placement="bottom">
            <template #content>
              <span class="group-btn-span" @click="startChangeStatus(true, 3)"
                ><LockTwoTone />&nbsp;标记错误</span
              ><br />
              <span class="group-btn-span" @click="startChangeStatus(false, 3)"
                ><UnlockTwoTone />&nbsp;取消标错</span
              >
            </template>
            <button type="button" class="ant-btn ant-btn-me"><span>记账</span></button>
          </Popover>
        </div>
        <!--  <div  style="margin-left: -100px;margin-top:10px">-->
        <!--    &lt;!&ndash; 搜索 &ndash;&gt;-->
        <!--    <span class="noneSpan" style="font-size: 16px">查找凭证：</span>-->
        <!--&lt;!&ndash;    <Select v-model:value="pageParameter.searchConditon.requirement" style="width: 80px;" class="special_select">&ndash;&gt;-->
        <!--&lt;!&ndash;      &lt;!&ndash;              <template v-for="item in searchConditonList.slice(1)">&ndash;&gt;&ndash;&gt;-->
        <!--&lt;!&ndash;      &lt;!&ndash;                <a-select-option v-if="item.ifShow == true" :value="item.dataIndex">&ndash;&gt;&ndash;&gt;-->
        <!--&lt;!&ndash;      &lt;!&ndash;                  {{item.title}}&ndash;&gt;&ndash;&gt;-->
        <!--&lt;!&ndash;      &lt;!&ndash;                </a-select-option>&ndash;&gt;&ndash;&gt;-->
        <!--&lt;!&ndash;      &lt;!&ndash;              </template>&ndash;&gt;&ndash;&gt;-->
        <!--&lt;!&ndash;    </Select>&ndash;&gt;-->
        <!--    <InputSearch-->
        <!--      placeholder=""-->
        <!--      style="width: 100px;border-radius: 4px"-->
        <!--    />-->

        <!--&lt;!&ndash;      v-model:vwalue="pageParameter.searchConditon.value"&ndash;&gt;-->
        <!--    <Button>-->
        <!--      <SyncOutlined :style="{ fontSize: '14px' }"/>-->
        <!--    </Button>-->
        <!--    <Button>-->
        <!--      <SettingFilled :style="{ fontSize: '14px' }"/>-->
        <!--    </Button>-->
        <!--    <Button>-->
        <!--      <PicLeftOutlined :style="{ fontSize: '14px' }"/>-->
        <!--    </Button>-->

        <!--    <Button>-->
        <!--      <FilterFilled :style="{ fontSize: '14px' }"/>-->
        <!--    </Button>-->
        <!--    <Button @click="exportExcel">-->
        <!--      <UsbOutlined :style="{ fontSize: '14px' }"/>-->
        <!--    </Button>-->
        <!--    <Button @click="printData">-->
        <!--      <PrinterOutlined :style="{ fontSize: '14px' }"/>-->
        <!--    </Button>-->
        <!--  </div>-->
      </div>
    </div>
    <!--      <Button type="primary" @click="emit('ok')">凭证查看器</Button>-->

    <div v-if="pingzhengType === 'show'">
      <Button type="primary" @click="emit('ok')">关闭凭证</Button>
      <Button type="primary" @click="emit('ok')" style="margin-left: 10px">复制凭证</Button>
      <Button type="primary" @click="emit('ok')" style="margin-left: 10px">插入凭证</Button>
      <Button type="primary" @click="emit('ok')" style="margin-left: 10px">冲销凭证</Button>
      <Button type="primary" @click="emit('ok')" style="margin-left: 10px">作废凭证</Button>
      <!--            <Button type="primary" @click="emit('ok')">返回</Button>-->
    </div>

    <div v-if="pingzhengType === 'chongxiao'">
      <Button type="primary" @click="emit('ok')">冲销</Button>
      <Button type="primary" @click="backEdit" style="margin-left: 20px">放弃冲销</Button>
    </div>
    <!--            <Button type="primary" @click="emit('ok')">返回</Button>-->
  </div>
</template>
<script setup>
  import {
    Button,
    Input,
    Row,
    Col,
    Form,
    Select,
    DatePicker,
    Popover,
    Tooltip,
    Modal,
    Table,
  } from 'ant-design-vue';
  import {
    SortDescendingOutlined,
    SortAscendingOutlined,
    CaretDownFilled,
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
    CheckOutlined,
    EditOutlined,
    SearchOutlined,
    MacCommandOutlined,
    PrinterOutlined,
    UsbOutlined,
    UnlockTwoTone,
    LockTwoTone,
    ExpandOutlined,
    HddOutlined,
    FundViewOutlined,
    FlagOutlined,
    CalculatorOutlined,
    DollarOutlined,
    ControlOutlined,
    ExclamationCircleOutlined,
  } from '@ant-design/icons-vue';

  const viewModel = inject('viewModel');
  const tableData = viewModel.value.instances[0].params.pingzhengData;
  const pingzhengData = viewModel.value.instances[0].params.pingzhengData;
  import { inject, nextTick, onMounted, ref } from 'vue';
  import { apiDataToShowModel } from '/@/components/pingzheng-fillin/hooks/models/datas/model';
  import { creatApiRow } from '/@/components/pingzheng-fillin/hooks/models/apiData';
  import { queryDefaultPingZhengDate } from '/@/api/pingzheng/pingzheng';
  import { usePingZhengResult } from '/@/components/pingzheng-fillin/hooks/okFun';
  import {setAccvoucherRevision} from "/@/api/record/system/accvoucher";
  const pingzhengType = inject('type');
  const showAddLoading = ref(false);
  function hideAddLoading() {
    showAddLoading.value = false;
  }

  function thisOkFun() {
    showAddLoading.value = true;
    okFun(hideAddLoading);
  }

  function backEdit() {
    setAccvoucherRevision({
      uniqueCode:tableData.options.apiData[0].uniqueCode,
      revision:"0"
    })
    function toApiDataModel(apiData, params) {
      tableData.value.pageStore.page = tableData.value.pageStore.inoIdIn.length + 1;
      const a = apiDataToShowModel(apiData, params);
      return a;
    }

    const apiData = [creatApiRow()];
    apiData[0].inoId = tableData.value.defaultInoid;
    const tableData2 = toApiDataModel(apiData, {
      settings: { titleName: '记账凭证', typeLabel: '填制', onlyShow: false },
    });
    tableData2.options.optionDate = tableData.value.optionDate;
    tableData.value.reloadPingZheng(tableData2, { type: '新增' });
  }
  async function openAddPage(params) {
    function toApiDataModel(apiData, params) {
      const a = apiDataToShowModel(apiData, params);
      return a;
    }

    if (params.settings.typeLabel == '插入') {
      const tableData2 = toApiDataModel([creatApiRow()], {
        options: {
          originPingZheng: params.options.originPingZheng,
          optionInoId: params.options.optionInoId,
          optionDate: params.options.optionDate,
        },
        settings: { titleName: '记账凭证', typeLabel: '插入' },
      });
      tableData.value.reloadPingZheng(tableData2);
    } else if (params.settings.typeLabel == '冲销') {
      tableData.value.settings.onlyShow = false;
      tableData.value.settings.typeLabel = params.settings.typeLabel;
      if (params.options != null) {
        tableData.value.options.originPingZheng = params.options.originPingZheng;
      }
      tableData.value.options.optionId2 = null;
      tableData.value.options.optionPzId = null;
      tableData.value.rowDefines.forEach((it) => {
        if (it.rowData.zhaiYao.value != '') {
          it.target.zhaiYao.setValue(`[冲销]${it.rowData.zhaiYao.value}`);
        }
        it.rowData.jieMoney.value = (parseFloat(it.rowData.jieMoney.value) * -1).toFixed(2);
        it.rowData.daiMoney.value = (parseFloat(it.rowData.daiMoney.value) * -1).toFixed(2);
      });

      const aa = await queryDefaultPingZhengDate();
      const defaultInoid = viewModel.value.findDefaultInoId(aa.dbillDate);
      tableData.value.options.optionInoId = viewModel.value.toInoIdText(defaultInoid);
    } else if (params.settings.typeLabel == '复制') {
      const aa = await queryDefaultPingZhengDate();
      const defaultInoid = viewModel.value.findDefaultInoId(aa.dbillDate);
      tableData.value.options.optionInoId = viewModel.value.toInoIdText(defaultInoid);

      tableData.value.settings.onlyShow = false;
      tableData.value.settings.typeLabel = params.settings.typeLabel;
      if (params.options != null) {
        tableData.value.options.originPingZheng = params.options.originPingZheng;
      }
      tableData.value.options.optionId2 = null;
      tableData.value.options.optionPzId = null;
    } else {
      tableData.value.settings.onlyShow = false;
      tableData.value.settings.typeLabel = params.settings.typeLabel;
      if (params.options != null) {
        tableData.value.options.originPingZheng = params.options.originPingZheng;
      }
    }
  }

  function thisSaveFun() {
    saveFun();
  }

  function thisTempSave() {
    emit('saveTempDanJu');
  }

  function saveFun() {
    showSaveLoading.value = true;
    nextTick(() => {
      okAndShowFun(() => {});
    });
    // emit('okAndShow',hideLoading)
  }

  const showSaveLoading = ref(false);
  function hideLoading() {
    showSaveLoading.value = false;
  }
  const pingZhengResultFun = usePingZhengResult({
    value: pingzhengData,
  });
  const { okFun, saveTempDanJu, okAndShowFun, deletePingZhengByUniqueCode } = pingZhengResultFun;

  const buttonEvents = {
    async updateOkFun() {
      await okFun();
      tableData.settings.onlyShow = true;
      tableData.settings.typeLabel = '查看';
    },
    backEdit() {
      function toApiDataModel(apiData, params) {
        tableData.pageStore.page = tableData.pageStore.inoIdIn.length + 1;
        const a = apiDataToShowModel(apiData, params);
        return a;
      }

      const apiData = [creatApiRow()];
      apiData[0].inoId = tableData.defaultInoid;
      const tableData2 = toApiDataModel(apiData, {
        settings: { titleName: '记账凭证', typeLabel: '填制', onlyShow: false },
      });
      tableData2.options.optionDate = tableData.optionDate;
      tableData.reloadPingZheng(tableData2, { type: '新增' });
    },
  };

  onMounted(() => {
    document.addEventListener('keydown', function (e) {
      if (e.altKey && e.key == '\\') {
        thisSaveFun();
      }
      if (e.altKey && e.key == 'Z') {
        thisTempSave();
      }
      if (e.altKey && e.key == 'L') {
        console.log(333);
      }
      if (e.altKey && e.key == 'n') {
        thisOkFun();
      }
      // console.log(e.keyCode)
      // console.log(navigator.platform)
      // console.log(e.metaKey)
      // if (e.keyCode == 83 && (navigator.platform.match("Mac") ? e.metaKey : e.ctrlKey))      {
      //   e.preventDefault();
      //   alert('saved');
      // }
      //
      // if (e.keyCode == 83 && (navigator.platform.match("Mac") ? e.metaKey : e.ctrlKey))      {
      //   e.preventDefault();
      //   alert('saved');
      // }
    });
  });
</script>
