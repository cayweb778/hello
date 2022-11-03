<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="期初余额导入模板"
    @ok="handleOk()"
    @register="register"
  >
    1、<a-select style="width: 250px" placeholder="下载导入模板" @change="handleDownByData">
      <a-select-option value="1">空白模板-只有表头</a-select-option>
      <a-select-option value="2">当前账套模板-自带末级科目</a-select-option>
    </a-select>
    <!--    <a-button @click="handleDownByData">-->
    &nbsp;
    <a-upload
      v-model:file-list="fileList"
      :headers="headers"
      :action="'/api/subjectinitialBalance/importInitalBalance?iyear=' + iyear123"
      @change="handleChange"
    >
      2、
      <a-button>
        <!--        <upload-outlined />-->
        选择文件
      </a-button>
    </a-upload>
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
  import { ref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { message } from 'ant-design-vue';
  import { findAllByBendAndIyearOrderByCcode } from '/@/api/subjectInitialBalance/subjectInitialBalance';
  import { jsonToSheetXlsx } from '/@/components/Excel';

  import { useUserStoreWidthOut } from '/@/store/modules/user';
  import {
    Select as ASelect,
    Input as AInput,
    Popover as APopover,
    Checkbox as ACheckbox,
    Modal as AModal,
    Upload as AUpload,
  } from 'ant-design-vue';
  import {
    useCompanyOperateStore,
    useCompanyOperateStoreWidthOut,
  } from '/@/store/modules/operate-company';
  const ASelectOption = ASelect.Option;
  const AInputSearch = AInput.Search;
  // 上传文件
  const headers = ref({
    authorization: useUserStoreWidthOut().getToken,
    datasource: JSON.stringify({
      databaseName: '',
      schemaName: useCompanyOperateStoreWidthOut().getSchemaName,
    }),
  });

  const fileList = ref([]);
  const data = ref('');
  const iyear123 = ref('');

  const emit=defineEmits([]);

  const [register, { closeModal }] = useModalInner((data) => {
    iyear123.value = data.data;
  });

  // 文件下载
  const handleDownByData = async (val: any) => {
    const lastCodeList = await findAllByBendAndIyearOrderByCcode('1', '2021');
    var arr = [];
    for (let i = 0; i < lastCodeList.length; i++) {
      arr.push({
        ccode: lastCodeList[i].ccode,
        ccodeName: lastCodeList[i].ccodeName,
        md: '',
        mc: '',
        name1: '',
        name2: '',
        name3: '',
        name4: '',
        name5: '',
        name6: '',
      });
    }
    arr = val === '1' ? [] : arr;
    dataExcel(arr);
  };

  function dataExcel(val: any) {
    jsonToSheetXlsx({
      data: val,
      header: {
        ccode: '科目编码',
        ccodeName: '科目名称',
        md: '借方金额',
        mc: '贷方金额',
        name1: '借方数量',
        name2: '贷方数量',
        name3: '单价',
        name4: '借方汇率',
        name5: '贷方汇率',
        name6: '外币金额',
      },
      filename: '期初余额导入模板.xlsx',
    });
  }

  // 上传回调
  const handleChange = async (info: any) => {
    if (info.fileList[0].status === 'done') {
      if (info.fileList[0].response.result[0].code === '401') {
        fileList.value = [];
        message.error(info.fileList[0].response.result[0].error);
        return false;
      } else {
        message.success('导入成功', 1, function () {
          handleOk();
        });
      }
    }
  };

  const handleOk = () => {
    emit('save');
    closeModal();
  };
</script>
<style lang="less" scoped>
  :dept(.ant-calendar-picker-input.ant-input),
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
    input {
      width: 35%;
      border: none !important;
      border-bottom: 1px solid #bdb9b9 !important;
    }

    .ant-input:focus {
      box-shadow: none;
    }

    :deep(.ant-select-selector) {
      border: none !important;
      border-bottom: 1px solid #bdb9b9 !important;
    }

    label {
      text-align: right;
      width: 110px;
      display: inline-block;
      padding: 5px 10px;
    }

    .open-content-down {
      margin-top: 5%;
    }
  }
</style>
