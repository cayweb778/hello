<template>
  <div
    class="pingzheng-header"
    style="border-radius: none; border-radius: 10px 10px 0 0; background: rgb(0 150 199)"
  >
    <div style="float: right; margin-top: 10px; margin-right: 5px">
      <PingzhengButtons
        @ok="emit('ok', $event)"
        @searchRow="emit('searchRow', $event)"
        @checkAll="emit('checkAll', $event)"
        @delRows="emit('delRows', $event)"
        @saveTempDanJu="emit('saveTempDanJu', $event)"
      />
    </div>

    <div class="pingzheng-accountPicker" v-if="!pingzhengData.settings.enableModelSave">
      <CopyOutlined style="color: white;font-size: 50px;"/>
      <AccountPicker theme="one" @reloadTable="reloadTable" />
    </div>

    <div class="pingzheng-setting" style="position: absolute; bottom: 13px; right: 6px">
<!--      <SearchInoid />-->
      <Button>
        <template #icon>
          <PrinterOutlined :style="{ fontSize: '14px' }" />
        </template>
      </Button>

      <div
        v-if="showPingZhengListDrawer"
        class="PingZhengListDrawerContainer"
        style="width: 300px; height: 500px; position: fixed; right: 30px; background: gray"
      >
        <PingZhengListDrawer v-if="true" />
      </div>
    </div>
  </div>
</template>
<script setup>
  import { computed, inject, onMounted, provide, ref } from 'vue';
  import { Button } from 'ant-design-vue';
  import PingZhengListDrawer from '../../components/PingZhengListDrawer.vue';
  import PingzhengButtons from './components/PingzhengButtons/index.vue';
  import SearchInoid from './components/SearchInoid.vue';
  import {
    SettingFilled,
    OrderedListOutlined,
    BorderHorizontalOutlined,
    PrinterOutlined,
    SyncOutlined,
    EditFilled,
    PayCircleOutlined,CopyOutlined
  } from '@ant-design/icons-vue';
  import { useRouterApi } from '/@/utils/boozsoft/datasource/datasourceUtil';
  import AccountPicker from '/@/boozsoft/components/AccountPicker/AccountPicker.vue';
  import {
    resetPreData,
    usePingzhengZhiDanRen,
    useReloadTable,
    useSetNewPingZhengDefaultData,
    useVerifyLabel,
  } from '/@/components/pingzheng-fillin/layouts/header/funs';

  const emit = defineEmits(['ok', 'searchRow', 'checkAll', 'delRows', 'saveTempDanJu']);
  const props = defineProps(['modelValue']);

  const { usePageRouterApi } = useRouterApi();
  const datasourcePicker = inject('datasourcePicker');
  const { setNewPingZhengDefaultData } = useSetNewPingZhengDefaultData(
    usePageRouterApi,
    datasourcePicker,
  );

  const { reloadTable } = useReloadTable(inject('reloadPage'));
  const showPingZhengListDrawer = ref();
  const viewModel = computed(() => props.modelValue.value);
  const pingzhengData = viewModel.value.instances[0].params.pingzhengData;
  const { state } = viewModel.value.accountSetting;

  function openInsertPingZheng() {
    const apiData = [];
    viewModel.value.openPingzhengModal(apiData);
  }

  viewModel.value.setNewPingZhengDefaultData = setNewPingZhengDefaultData;
  const { hasEnableModelSave, isNewPingzhengLabel } = useVerifyLabel(pingzhengData);
  const { isZhiDanRenEmpty, resetZhiDanRen } = usePingzhengZhiDanRen(pingzhengData);

  onMounted(async () => {
    if (hasEnableModelSave() || isNewPingzhengLabel()) {
      await setNewPingZhengDefaultData(pingzhengData);
      resetPreData(state, pingzhengData);
    }

    isZhiDanRenEmpty() && resetZhiDanRen();
  });


  provide('viewModel', viewModel);
  provide('type', null);
</script>
<style scoped>
  .ant-btn-group button {
    padding: 4px 15px;
    color: blue;
    font-size: 16px;
  }

  .pingzheng-header {
    height: 100%;
    position: absolute;
    z-index: 1;
    background: #f1f1f1;
    left: 0px;
    overflow: hidden;
    top: 0px;
    width: 100%;
    border-radius: 10px;
  }

</style>

<style scoped>
  .pingzheng-btns :deep(.ant-btn) {
    color: #0096c7;
  }

  .pingzheng-btns :deep(.ant-btn:hover) {
    color: white;
  }

  .pingzheng-header-title {
    color: rgb(0, 150, 199);
    position: absolute;
    z-index: 1;
    left: calc((100% - 100px) / 2);
    top: 4px;
    margin: 0 auto;
    font-size: 26px;
    font-weight: 900;
  }


  .pingzheng-date-label {
    margin-right: 5px;
  }

  .pingzheng-date {
    margin-right: 5px;
    position: absolute;
    bottom: 5px;
    left: calc((100% - 147px) / 2);
  }

  .pingzheng-date-picker {
    width: 120px !important;
  }

  .pingzheng-accountPicker {
    position: absolute;
    bottom: 15px;
    width: 550px;
    left: 29px;
    display: inline-flex;
    color: white !important;
  }
</style>
