<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="组织会计科目制度"
    @ok="handleOk()"
    @register="register"
  >
    <template #title>
      <div style="display: flex;color: #0096c7;">
        <span style="line-height:40px;font-size: 28px;">
          <PlusCircleOutlined style="font-size: 34px;font-weight: bold"/>
        </span>
        <span style="line-height:40px;font-size: 28px;margin-top:-5px;">
          &nbsp;&nbsp;组织会计制度模板
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 40px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="/009.png" style="height:76px;margin-right: 55px;"/>
      </div>
    </template>
    <div class="nc-open-content">
      <div class="open-content-up">
        <label>模板名称：</label>
        <a-input class="abc" style="width: 50%;border-bottom: 2px solid #000000;" v-model:value="formState.tName" autocomplete="off" />
        <span class="red_span">*</span>
        <br>
        <span style="color: red;margin-left: 20%;">只能修改名称</span>
      </div>
    </div>
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
  import { CaretDownOutlined,PlusCircleOutlined} from '@ant-design/icons-vue'
  import { message } from 'ant-design-vue';
  import { onMounted, reactive, ref, toRaw, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import {
    countByName,
    countByUniqueAccStandardAndTName,
    findByCustomTemplate
  } from '/@/api/acctemplate/acctemplate';
  import {
    Select as ASelect,
    Input as AInput,
    Radio as ARadio,
  } from 'ant-design-vue';
  import {getOrganizeList} from "/@/api/record/group/im-organize";
  const ASelectOption = ASelect.Option;
  const AInputSearch = AInput.Search;
  const ARadioGroup = ARadio.Group
  const emit=defineEmits([]);
  const standardSelected = ref('');

  const zz = ref(''); // 组织
  const zzlist = ref([]); // 组织
  const codeFirst = ref('');
  const accStandardList = ref([]);
  const formRef = ref({});
  const oldName = ref('');
  const formState:any=ref({})

  const [register, { closeModal }] = useModalInner(async (data) => {
    oldName.value=data.data.tname
    formState.value.tName=data.data.tname
    formState.value.id=data.data.id

    codeFirst.value=data.data.tjici.split('-')[0]
    for (let i = 0; i < data.data.tjici.split('-').length; i++) {
      formState['tJici'+(i+1)]=data.data.tjici.split('-')[i]
      // disabledflg['tJici'+(i+1)]=true
    }
  });
  async function handleOk() {
    if(formState.tName!==oldName.value){
      const name= await countByName('组织模板',formState.tName)
      if (name > 0) {
        return message.error('名称已存在！');
      }
    }else{
      return message.error('请填写名称！');
    }
    emit('save', unref(formState));
    closeModal();
  }
  const findByAll = async () => {
    // 获取组织
    const orientation =await getOrganizeList();
    zzlist.value=orientation.items
    // 集团科目会计准则（自定义）
    const list = await findByCustomTemplate('自定义模板');
    accStandardList.value = list.items;
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
