<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    :closable="false"
    title="客户分类"
    centered="true"
    @ok="handleOk()"
    @register="register"
  >
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <span style="line-height: 25px;font-size: 16px;"><PlusCircleOutlined style="font-size: 18px;font-weight: bold"/>&nbsp;&nbsp;客户分类</span>
      </div>
    </template>
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up" style="text-align: center;margin-top: 30px;">
        <label style="font-size: 18px;margin-left: 0;">分类名称：</label>
        <a-input v-model:value="formItems.cusCclassName" placeholder="" class="abc" style="width: 65%;" />
        <span class="red_span">*</span>
        <br/><br/><br/>
        <label>分类编码：</label>
        <a-input v-model:value="formItems.cusClass" placeholder=""/>
        <span class="red_span">*</span>
        <br/><br/>

        <label>上级分类：</label>
        <TreeSelect
          v-model:value="formItems.parentId"
          style="width: 50%"
          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
          :tree-data="treeData"
          placeholder="请选择上级分类"
          tree-default-expand-all
          allow-clear
          @change="aaa(formItems.parentId)"
        >
        </TreeSelect>
        <span class="red_span"></span>
      </div>
    </div>
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
  import { CaretDownOutlined,PlusCircleOutlined,FormOutlined,FileSearchOutlined } from '@ant-design/icons-vue'
  import { onMounted, reactive, ref, toRaw, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import {
    GetCustomerClassTree,
    verifyCusClass,
    verifyCusClassName,
    verifySuperCusClassName,
  } from '/@/api/record/system/customer_class_group';
  import {
    TreeSelect,
    Form as AForm,
    Select as ASelect,
    Input as AInput,
    Statistic as AStatistic,
    message,
  } from 'ant-design-vue';
  import {useMessage} from "/@/hooks/web/useMessage";
  const ASelectOption = ASelect.Option;
  const AInputSearch = AInput.Search;
  const AStatisticCountdown = AStatistic.Countdown;
  const AFormItem = AForm.Item;
  const emit=defineEmits(['register']);
  const { createWarningModal } = useMessage()
  const formItems: any = ref({});
  const [register, { closeModal }] = useModalInner((data) => {
    formItems.value.cusCclassName=''
    formItems.value.cusClass=''
    formItems.value.parentId=''

    GetCustomerClassTree().then((res) => {
      function a(customerTree) {
        customerTree.forEach((item) => {
          if (item.children != null) {
            a(item.children);
          }
          item.title = '(' + item.cusClass + ')' + item.cusCclassName;
          item.value = item.uniqueCustclass;
          item.key = item.uniqueCustclass;
        });
      }
      a(res);
      treeData.value = res;
    });
  });

  async function handleOk() {
    if(formItems.value.cusClass==='' || formItems.value.cusClass===undefined){return createWarningModal({ content: '请填写分类编码！' });}
    const sumNum = await verifyCusClass(formItems.value.cusClass);
    if (sumNum > 0) {return createWarningModal({ content: '分类编码已存在！' });}

    if(formItems.value.cusCclassName==='' || formItems.value.cusCclassName===undefined){
      return createWarningModal({ content: '请填写分类名称！' });
    }else{
      const sumName = await verifySuperCusClassName(formItems.value.parentId,formItems.value.cusCclassName);
      if (sumName > 0) {return createWarningModal({ content: '同级别分类名称已存在！' });}
      const sumName2 = await verifyCusClassName(formItems.value.cusCclassName);
      if (sumName2 > 0) {return createWarningModal({ content: '同级别分类名称已存在！' });}
    }

    emit('save', unref(formItems));
    closeModal();
  }

  const treeData = ref([]);
  async function fetch() {
    const customerTree = await GetCustomerClassTree();
    function a(customerTree) {
      customerTree.forEach((item) => {
        if (item.children != null) {
          a(item.children);
        }
        item.title = '(' + item.cusClass + ')' + item.cusCclassName;
        item.value = item.uniqueCustclass;
        item.key = item.uniqueCustclass;
      });
    }
    a(customerTree);
    treeData.value = customerTree;
  }
</script>
<style>
.vben-basic-title {
  color: rgb(1, 129, 226) !important;
}

.ant-modal-body {
  padding: 0px;
  border: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
}
</style>
<style lang="less" scoped>
:deep(.vben-basic-title) {
  color: rgb(1, 129, 226) !important;
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
    width: 50%;
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
    width: 110px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    color: #535353;
    font-size: 13px;
    margin-left: 1em;
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
</style>
