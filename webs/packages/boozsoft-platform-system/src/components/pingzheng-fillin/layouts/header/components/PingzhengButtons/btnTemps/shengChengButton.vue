<template>
  <div class="pingzhengDefaultButton">
    <ProjectSelect @save="saveSelectProject" @register="registerSelectProjectPage" />
    <div class="pingzhengDefaultButtonFlex">

      <!-- 新增按钮 -->
      <Popover placement="bottom">
        <Button
          class="hello-btn"
          style="width: 100px; margin-right: -2px"
          :loading="showAddLoading"
          @click="thisOkFun"
        >
          <span
            style="color: rgb(0, 150, 199); font-weight: 400"
            v-text="'生成凭证'"
            class="pingzhengDefaultButtonAdd"
          ></span>
        </Button>
        <template #content>
          <div style="width: 150px; padding: 5px">
            <div style="display: flex; justify-content: space-between">
              <div>新增</div> <div>Alt+N</div></div
            >
            <div style="display: flex; justify-content: space-between"
              ><div>参考常用凭证</div> <div>Alt+R</div></div
            >
          </div>
          <!--          <div class="pingzhengDefaultButtonAddCheck">-->
          <!--            <div class="pingzhengDefaultButtonAddCheck1">摘要[<span style="color:red">未检查</span>]-->
          <!--            </div>-->
          <!--            <div class="pingzhengDefaultButtonAddCheck2">会计科目[<span style="color:red">未检查</span>]-->
          <!--            </div>-->
          <!--            <div class="pingzhengDefaultButtonAddCheck3">辅助核算[<span style="color:red">未检查</span>]-->
          <!--            </div>-->
          <!--            <div class="pingzhengDefaultButtonAddCheck4" type="primary">-->
          <!--              <Button>开始检查</Button>-->
          <!--            </div>-->
          <!--          </div>-->
        </template>
      </Popover>


      <Button
        class="hello-btn"
        style="margin-right: -2px; font-size: 15px"
        @click="giveup"
      ><span style="color: rgb(0, 150, 199); font-weight: 400">放弃</span></Button
      >
    </div>

    <div v-if="pingzhengType === 'show'">
      <Button type="primary" @click="emit('ok')">关闭凭证</Button>
      <Button
        type="primary"
        @click="openAddPage({ settings: { typeLabel: '复制' } })"
        style="margin-left: 10px"
        >复制凭证
      </Button>
      <Button type="primary" @click="openInsertPingZheng" style="margin-left: 10px"
        >插入凭证</Button
      >
      <Button
        type="primary"
        @click="openAddPage({ settings: { typeLabel: '冲销' } })"
        style="margin-left: 10px"
        >冲销凭证
      </Button>
      <Button type="primary" @click="emit('ok')" style="margin-left: 10px">作废凭证</Button>
    </div>

    <div v-if="pingzhengType === 'chongxiao'">
      <Button type="primary" @click="emit('ok')">冲销</Button>
      <Button type="primary" @click="emit('ok')" style="margin-left: 20px">放弃冲销</Button>
    </div>
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
  import ProjectSelect from './ProjectSelect.vue';
  const viewModel = inject('viewModel');
  const tableData = viewModel.value.instances[0].params.pingzhengData;
  const pingzhengData = viewModel.value.instances[0].params.pingzhengData;
  import { inject, nextTick, onMounted, ref } from 'vue';
  import { apiDataToShowModel } from '/@/components/pingzheng-fillin/hooks/models/datas/model';
  import { creatApiRow } from '/@/components/pingzheng-fillin/hooks/models/apiData';
  import { queryDefaultPingZhengDate } from '/@/api/pingzheng/pingzheng';
  import { usePingZhengResult } from '/@/components/pingzheng-fillin/hooks/okFun';
  import { useTabs } from '/@/hooks/web/useTabs';
  import router from '/@/router';
  import { useCompanyOperateStoreWidthOut } from '/@/store/modules/operate-company';
  import { initBasisTabAccoutData } from '/@/api/record/system/financial-settings';
  import { useModal } from '/@/components/Modal';
  import {useAccountPickerCache} from "/@/store/modules/boozsoft/components/AccountPicker/cache";

  const pingzhengType = inject('type');
  const showAddLoading = ref(false);

  function hideAddLoading() {
    showAddLoading.value = false;
  }

  const [registerSelectProjectPage, { openModal: openSelectProjectPage }] = useModal();

  function openA() {
    openSelectProjectPage(true, {});
  }
  function saveSelectProject() {}

  function thisOkFun() {
    showAddLoading.value = true;
    okFun(hideAddLoading);
  }

  function backEdit() {
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
    saveTempDanJu();
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

  const pingZhengResultFun = usePingZhengResult({ value: pingzhengData });
  const { okFun, saveTempDanJu, okAndShowFun, deletePingZhengByUniqueCode } = pingZhengResultFun;

  window.debuggerInfo.getApiData = function () {
    pingZhengResultFun.getApiData();
  };

  const isZhengFuKuaiJiZhiDu = ref();
  onMounted(async () => {
    const res = await initBasisTabAccoutData();
    function findacountStandard(key) {
      return res.acountStandardList.filter((it) => {
        return it.id == key;
      })[0];
    }
    console.log(useAccountPickerCache().get())
    const aaa = findacountStandard(
      useAccountPickerCache().get().basisMap.accStandard,
    );
    isZhengFuKuaiJiZhiDu.value = aaa.tname == '政府会计制度';
    document.addEventListener('keydown', function (e) {
      if (e.altKey && e.key == '\\') {
        e.preventDefault();
        thisSaveFun();
      }
      if (e.altKey && e.key == 'x') {
        e.preventDefault();
        thisTempSave();
      }
      if (e.altKey && e.key == 'l') {
        e.preventDefault();
        console.log(333);
      }
      if (e.altKey && e.key == 'n') {
        e.preventDefault();
        thisOkFun();
      }
      // console.log(e.keyCode)
      // console.log(navigator.platform)
      // console.log(e.metaKey)
      // if (e.keyCode == 83 && (navigator.platform.match("Mac") ? e.metaKey : e.ctrlKey))      {

      //   alert('saved');
      // }
      //
      // if (e.keyCode == 83 && (navigator.platform.match("Mac") ? e.metaKey : e.ctrlKey))      {
      //   e.preventDefault();
      //   alert('saved');
      // }
    });
  });

  function openInsertPage() {
    openA();
    // Modal.info({
    //   title:'aassa'
    // })
  }

  const buttonEvents = {
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
      debugger
      tableData.reloadPingZheng(tableData2, { type: '新增' });
    },
  };
  function exit() {
    const { closeCurrent } = useTabs(router);
    closeCurrent();
  }
  function showPingzhengList() {
    router.push('/zhongZhang/vouchers/manage-voucher/list-voucher')
  }
  function giveup(e){
    tableData.pageStore.goShowData(tableData.pageStore.inoIdIn[tableData.pageStore.inoIdIn.length-1])
    // buttonEvents.backEdit(e)
  }
</script>
<style scoped>
  .pingzhengDefaultButton {
  }

  .pingzhengDefaultButtonFlex {
    display: flex;
    flex-direction: row;
    align-items: center;
  }

  :deep(.ant-btn:hover) span {
    /*background:white !important;*/
    color: white !important;
    /*box-shadow: 1px 0px 13px 4px #b0b8bb;*/
  }

  :deep(.hello-btn) {
    font-size: 15px;
    width: 60px;
  }

  .pingzhengDefaultButtonAdd {
    color: white;
    font-weight: 900;
    margin-right: 5px;
  }

  .pingzhengDefaultButtonAddCheck4 {
    text-align: center;
  }

  .group-btn-span-special {
    width: 170px;
  }
</style>
