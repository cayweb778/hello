<template>
  <BasicModal
    width="600px"
    v-bind="$attrs"
    title="新建会计科目制度"
    @ok="handleOk()"
    @register="register"
  >
    <template #title>
      <div style="display: flex;color: #0096c7;">
        <span style="line-height:40px;font-size: 28px;">
          <PlusCircleOutlined style="font-size: 34px;font-weight: bold"/>
        </span>
        <span style="line-height:40px;font-size: 28px;margin-top:-5px;">
          &nbsp;&nbsp;集团会计制度模板
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 40px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="/009.png" style="height:76px;margin-right: 55px;"/>
      </div>
    </template>
    <div class="nc-open-content">
      <div class="open-content-up">
        <div style="margin-left: 20px;margin-top: 20px;">
          <a-radio-group v-model:value="formState.tFlg">
            <a-radio value="1">
              <span style="color: #000000;font-weight: bold;font-size: 10px;">预置科目</span>
            </a-radio>
            <a-radio value="0" style="margin-left: -10px;">
              <span style="color: #000000;font-weight: bold;font-size: 10px;">不预置</span>
            </a-radio>
          </a-radio-group>
          <br/><br/>
          <label>模板名称：</label>
          <a-input class="abc" style="width: 50%;border-bottom: 2px solid #000000;" v-model:value="formState.tName" autocomplete="off" />
          <span class="red_span">*</span>
          <br/><br/><br/>
          <label>参照会计制度名称：</label>
          <a-select
            v-model:value="standardSelected"
            option-filter-prop="children"
            style="width: 35%"
            show-search
            @change="standardCheck"
          >
            <a-select-option
              v-for="item in accStandardList"
              :key="item.uniqueAccStandard"
              :value="item.uniqueAccStandard + '_' + item.accStyleUnique + 'BOOZ' + item.codeFirst"
            >
              {{ item.accStandardName }}
            </a-select-option>
            <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
          </a-select>
          <br/>
          <label>科目级次：{{ jici1 }}</label>
          <a-input style="margin-left: 10px;width: 35%;" v-model:value="tJici" @keydown.enter.native="$refs.focus16.focus()" @keyup="importLimit" @blur="importLimit" autocomplete="off" />
        </div>
      </div>
    </div>
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
  import { CaretDownOutlined,PlusCircleOutlined} from '@ant-design/icons-vue'
  import { message } from 'ant-design-vue';
  import { onMounted, reactive, ref, toRaw, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { countByUniqueAccStandardAndTName } from '/@/api/acctemplate/acctemplate';
  import {
    Select as ASelect,
    Input as AInput,
    Radio as ARadio,
  } from 'ant-design-vue';
  import { findAllAcctandardList } from '/@/api/accstandard/accstandard';
  import {hasBlank} from "/@/api/task-api/tast-bus-api";
  const ASelectOption = ASelect.Option;
  const AInputSearch = AInput.Search;
  const ARadioGroup = ARadio.Group
  const emit=defineEmits([]);

  // 是否预置科目
  const tFlg = ref(false);
  const jici1 = ref('4-2-2-2');
  const tJici = ref('');
  const standardSelected = ref('');
  const accStandardList = ref([]);

  const formState:any = ref({});
  const [register, { closeModal }] = useModalInner(async (data) => {
    formState.value.tFlg='1'
    formState.value.tName=''
    tJici.value=''
    standardSelected.value=''
    // 会计准则list
    const list = await findAllAcctandardList();
    accStandardList.value = list;
  });
  async function handleOk() {
    if (standardSelected.value === '') {
      message.error('请选择会计制度名称！');
      return false;
    }

    const name = await countByUniqueAccStandardAndTName(
      standardSelected.value.split('_')[0],
      formState.value.tName,
      '自定义模板'
    );
    if (name > 0) {
      message.error('科目模板名称已存在！');
      return;
    }
    formState.value.uniqueAccStandard = standardSelected.value.split('_')[0];
    formState.value.tJici=tJici.value==''?jici1.value:jici1.value+'-'+tJici.value
    emit('save', unref(formState));
    closeModal();
  }

  async function standardCheck(data) {
    jici1.value=data.split('BOOZ')[1]+'-2-2-2'
    standardSelected.value = data;
  }
  const importLimit = (e) => {
    // 排除指定文本 /[^123456789\-]/g
    e.target.value = e.target.value.replace(/[^123456789]/g, "")
    if (!hasBlank(e.target.value)) e.target.value = e.target.value.split('').join('-')
    formState.value.tJici = e.target.value
  }
</script>
<style>
.ant-modal-body {
  padding: 0px;
  border-bottom: 1px solid #0096c7;
  border-left: none;
  border-right: none;
  border-top: none !important;
}
.ant-modal-header{
  border: none !important;
}
</style>
<style lang="less" scoped>
:deep(.ant-calendar-picker-input.ant-input), :deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector .ant-select-selection-search-input) {
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;
  font-weight: bold;
}

:deep(.ant-select-single:not(.ant-select-customize-input)
.ant-select-selector
.ant-select-selection-search-input) {
  border: none !important;
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

.red_span {
  color: red;
  font-weight: bold;
  display: inline-block;
  width: 20px;
  text-align: right;
}

.nc-open-content {

  input {
    width: 30%;
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
    font-weight: bold;
    font-size: 14px;
  }

  label {
    text-align: left;
    //width: 120px;
    display: inline-block;
    color: #000000;
    font-size: 15px;
    margin-left: 3em;
    font-weight: bold;
    padding: 0px 30px;
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

</style>
