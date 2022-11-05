<template>
  <BasicModal
    width="600px"
    height="300"
    v-bind="$attrs"
    title="科目批量复制"
    @ok="handleOk()"
    @register="register"
  >
    <div class="nc-open-content" style="height: 100%; overflow: hidden">
      <div class="open-content-up" style="font-weight: bolder; margin-top: 10px">
        <label>批量复制规则</label>
        <p>1、来源科目必须是非末级科目</p>
        <p>2、目标科目必须是末级科目</p>
        <p>3、来源科目和目标科目级次必须相同</p>
        <p>4、来源科目的所有下级科目都将被复制到目标科目</p>

        <label>来源科目</label>
        <a-select
          show-search
          v-model:value="nolastcode"
          option-filter-prop="children"
          style="width: 30%"
          allow-clear
        >
          <a-select-option :value="item.ccode" v-for="(item, i) in findAllNoLastCode">
            {{ item.ccode+'-'+item.ccodeName }}
          </a-select-option>
        </a-select>
      </div>
      <div class="open-content-up" style="font-weight: bolder; margin-top: 10px">
        <label>目标科目</label>
        <a-select
          show-search
          v-model:value="lastcode"
          option-filter-prop="children"
          style="width: 30%"
          allow-clear
          @change="lastcodeChange"
        >
          <a-select-option :value="item.ccode" v-for="(item, i) in findAllLastCode">
            {{ item.ccode+'-'+item.ccodeName }}
          </a-select-option>
        </a-select>
      </div>
    </div>
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
  import { message } from 'ant-design-vue';
  import { onMounted, reactive, ref, toRaw, unref,  watch } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import {
    Divider as ADivider,
    Switch as ASwitch,
    Tabs as ATabs,
    Select as ASelect,
    Input as AInput,
    Popover as APopover,
    Checkbox as ACheckbox,
    Modal as AModal,
    Upload as AUpload,
    Row as ARow,
    Col as ACol,
    Statistic as AStatistic,
  } from 'ant-design-vue';
  import {
    company_findByIyearAndBendCode,
    company_copyCode,
    findByIyearAndBendCode,
    copyCode, GroupCopyCode
  } from '/@/api/codekemu/codekemu';
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
  const uniqueAccStandard=ref('')
  const templateId=ref('')


  const [register, { closeModal }] = useModalInner(async (data) => {
    uniqueAccStandard.value=data.uniqueAccStandard
    templateId.value=data.templateId
    // 来源科目
    await findByIyearAndBendCode(data.uniqueAccStandard,data.templateId,'0')
      .then((res) => {
          findAllNoLastCode.value=res
        });
    // 目标科目
    await findByIyearAndBendCode(data.uniqueAccStandard,data.templateId,'1')
      .then((res) => {
        findAllLastCode.value=res
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

    await GroupCopyCode(uniqueAccStandard.value,templateId.value,lastcode.value,nolastcode.value)
    emit('save');
    closeModal();
  }
</script>
<style lang="less" scoped>
  :deep(.ant-calendar-picker-input.ant-input),
  :deep(.ant-select-single:not(.ant-select-customize-input)
      .ant-select-selector
      .ant-select-selection-search-input) {
    border: none;
    border-bottom: solid 1px rgb(191, 191, 191) !important;
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


    .ant-input:focus {
      box-shadow: none;
    }

    :deep(.ant-select-selector) {
      border: none !important;
      border-bottom: 1px solid #ffffff !important;
    }

    label {
      text-align: right;
      width: 110px;
      display: inline-block;
      padding: 5px 10px;
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
</style>
