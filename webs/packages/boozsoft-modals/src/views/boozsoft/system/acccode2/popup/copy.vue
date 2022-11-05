<template>
  <BasicModal
    destroyOnClose
    width="600px"
    height="300"
    v-bind="$attrs"
    title="科目批量复制"
    @ok="handleOk()"
    @register="register"
  >
    <template #title>
      <div style="display: flex;color: #0096c7;margin-top: 5px;">
        <span style="line-height:40px;font-size: 28px;">
          <CopyOutlined style="font-size: 34px;font-weight: bold"/>
        </span>
        <span style="line-height:40px;font-size: 28px;margin-top:-5px;">
          &nbsp;&nbsp;科目复制
        </span>
      </div>
    </template>
    <div class="nc-open-content" style="height: 100%; overflow: hidden">
      <div class="open-content-up" style="font-weight: bolder; margin-top: 50px">
        <label>来源科目</label>
        <a-select
          show-search
          v-model:value="nolastcode"
          option-filter-prop="children"
          style="width: 50%"
          allow-clear
        >
          <a-select-option :value="item.ccode" v-for="(item, i) in findAllNoLastCode">
            {{ item.ccode+'-'+item.ccodeName }}
          </a-select-option>
          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
        </a-select>
        &nbsp;&nbsp;
        <a style="font-weight: bold;font-size: 20px;" @click="modalBend0Show"><LinkOutlined /></a>
        <br>
        <div style="font-size: 10px;margin-left:60px;margin-top: 10px;">
          <span>1、来源科目必须是非末级科目</span><br>
          <span>2、来源科目和目标科目级次必须相同</span>
        </div>
      </div>
      <br>
      <div class="open-content-up" style="font-weight: bolder; margin-top: 10px">
        <label>目标科目</label>
        <a-select
          show-search
          v-model:value="lastcode"
          option-filter-prop="children"
          style="width: 50%"
          allow-clear
          @change="lastcodeChange"
        >
          <a-select-option :value="item.ccode" v-for="(item, i) in findAllLastCode">
            {{ item.ccode+'-'+item.ccodeName }}
          </a-select-option>
          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
        </a-select>
        &nbsp;&nbsp;
        <a style="font-weight: bold;font-size: 20px;" @click="modalBend1Show"><LinkOutlined /></a>
        <br>
        <div style="font-size: 10px;margin-left:60px;margin-top: 10px;">
          <span>1、目标科目必须是末级科目</span><br>
          <span>2、只复制来源科目下一级科目</span>
        </div>
      </div>
    </div>

    <ModalBend1Pop @throwData="modalBend1Data" @register="registerModalBend1PopPage" />
    <ModalBend0Pop @throwData="modalBend0Data" @register="registerModalBend0Page" />
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
  import ModalBend1Pop from './modalPop.vue';
  import ModalBend0Pop from './modalBend0Pop.vue';
  import { CaretDownOutlined,CopyOutlined,LinkOutlined } from '@ant-design/icons-vue'
  import { message } from 'ant-design-vue';
  import { onMounted, reactive, ref, toRaw, unref,  watch } from 'vue';
  import {BasicModal, useModal, useModalInner} from '/@/components/Modal';
  import {
    Tabs as ATabs,
    Select as ASelect,
    Input as AInput,
    Statistic as AStatistic,
  } from 'ant-design-vue';
  import { company_findByIyearAndBendCode,company_copyCode } from '/@/api/codekemu/codekemu';
  import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
  import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";

  const ASelectOption = ASelect.Option;
  const AInputSearch = AInput.Search;
  const AStatisticCountdown = AStatistic.Countdown;
  const ATabPane = ATabs.TabPane;
  const emit=defineEmits(['register']);

  // 所有末级科目
  const findAllLastCode=ref([])
  const findAllNoLastCode=ref([])
  const nolastcode=ref('')
  const lastcode=ref('')
  const iyear=ref('')
  // 数据库模式名称
  const database = ref('');

  const [registerModalBend1PopPage, { openModal: openModalBend1Page }] = useModal();
  const [registerModalBend0Page, { openModal: openModalBend0Page }] = useModal();

  const [register, { closeModal }] = useModalInner(async (data) => {
    console.log()
    iyear.value=data.data
    database.value=data.accId
    const databasenum=data.accId+'-'+data.data
    await useRouteApi(company_findByIyearAndBendCode,{schemaName:databasenum})({iyear:iyear.value,bend:'1',databasenum:databasenum}).then((res) => {
      findAllLastCode.value=res
    });
    await useRouteApi(company_findByIyearAndBendCode,{schemaName:databasenum})({iyear:iyear.value,bend:'0',databasenum:databasenum}).then((res) => {
      findAllNoLastCode.value=res
    });
  });

  async function lastcodeChange() {
    if(parseInt(lastcode.value.length)!==parseInt(nolastcode.value.length)){
      message.error('科目级次不相同,无法复制！请重新选择')
      nolastcode.value=''
      lastcode.value=''
    }
  }
  async function handleOk() {
    if(nolastcode.value===''){
      message.error('来源科目不能为空,无法复制！请重新选择')
      return false;
    }
    if(lastcode.value===''){
      message.error('目标科目不能为空,无法复制！请重新选择')
      return false;
    }
    const databasenum=database.value+'-'+iyear.value
    await useRouteApi(company_copyCode,{schemaName:databasenum})({iyear:iyear.value,lastcode:lastcode.value,nolastcode:nolastcode.value});
    emit('save');
    closeModal();
  }

  // 末级
  const modalBend1Show = () => {
    openModalBend1Page(true, {
      database: database.value+'-'+iyear.value,
      accId: database.value,
      iyear: iyear.value,
    });
  }
  // 非末级
  const modalBend0Show = () => {
    openModalBend0Page(true, {
      database: database.value+'-'+iyear.value,
      accId: database.value,
      iyear: iyear.value,
    });
  }

  function modalBend1Data(data) {
    lastcode.value=data.ccode
  }
  function modalBend0Data(data) {
    nolastcode.value=data.ccode
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
    font-size: 18px;
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
</style>
