<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="科目导入模板"
    @ok="handleOk()"
    @register="register"
  >
    <a-spin :spinning="uploading">
      <div style="position: relative;background: #ffffff;border: 1px solid #999999;padding: 10px 20px;margin-top: 20px;width: 70%;margin-left: 17%;">
        <div style="position: absolute;background: #ffffff; top: -15px;left: 20px;padding: 0 10px;">导入说明</div>
        <p>1.导入文件格式必须为xls或xlsx，且数据表内容必须放置在第一个页签sheet1中;</p>
        <p>2.科目编码、名称不可重复且不能为空;</p>
        <p>3.必须有 科目编码、科目名称、方向 固定列;</p>
      </div>
      <div style="margin-left: 18%;margin-top: 20px;">
        <label style="width: 150px;">
          <a @click="handleDownByData()"><DownloadOutlined/>下载导入模板</a>
        </label>
      </div>
      <div style="margin-left: 155px;margin-top: 30px;">
        <a-upload
          :file-list="fileList"
          :remove="handleRemove"
          :before-upload="beforeUpload"
        >
          <a-button type="primary">
            请选择导入的文件
          </a-button>
        </a-upload>
        <br>
        <br>
      </div>

      <a-modal v-model:visible="visible" title="导入结果" @ok="ModalClose" centered="true">
        <p>文件数据：{{excelResultInfo.excellistLength-1}}个</p>
        <p>导入成功：{{excelResultInfo.successInfo}}个</p>
        <p>导入失败：{{excelResultInfo.errorInfo}}个 <a v-if="excelResultInfo.errorInfo>0" style="margin-left: 30px;" @click="errorExcel">下载描述错误excel</a></p>
      </a-modal>
    </a-spin>
    <template #footer>
      <div style="height: 35px">
        <div style="float: right">
          <a-button @click="handleClose" :disabled="uploading">放弃</a-button>
          <a-button @click="handleOk(true)" type="primary" :loading="uploading" :disabled="fileList.length == 0 || fileList.length> 1">开始导入
          </a-button>
        </div>
      </div>
    </template>
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
  import {reactive, ref} from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { message } from 'ant-design-vue';
  import { jsonToSheetXlsx } from '/@/components/Excel';
  import {sysCountryImportCoke, sysimportCoke} from '/@/api/codekemu/codekemu';
  import { useUserStoreWidthOut } from '/@/store/modules/user';
  import {
    Spin as ASpin,
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
import {exportExcel} from "/@/api/record/generalLedger/excelExport";
  const ASelectOption = ASelect.Option;
  const AInputSearch = AInput.Search;
  // 上传文件：模式名（先写死，之后动态获取）
  const headers = ref({
    authorization: useUserStoreWidthOut().getToken,
    datasource: JSON.stringify({
      databaseName: '',
      schemaName: useCompanyOperateStoreWidthOut().getSchemaName,
    }),
  });
  const uploadParm = ref('')
  const fileList = ref([]);
  const uniqueAccStandard = ref('');
  const templateId = ref('');
  const accStyleUnique = ref('');
  const jici = ref('');
  const uploading = ref(false)
  const emit=defineEmits([]);
  const visible = ref<boolean>(false);
  const excelResultInfo:any = reactive({
    excellistInfo:[],
    excellistLength:'',
    successInfo:'',
    errorInfo:''
  })
  const excelcolumn=ref('')

  const [register, { closeModal }] = useModalInner((data) => {
    uniqueAccStandard.value = data.data.uniqueAccStandard;
    templateId.value = data.data.templateId;
    jici.value = data.data.jici;
    accStyleUnique.value = data.data.accStyleUnique;
  });

  const handleClose = () => {
    fileList.value = [];
    closeModal()
  }

  const ModalClose = () => {
    fileList.value = [];
    visible.value=false
    emit('save');
    closeModal();
  }

  const beforeUpload = (file) => {
    // 默认存储最新单文件
    if (fileList.value.length > 0) fileList.value = []
    fileList.value = [...fileList.value, file]
    return false;
  }
  const handleRemove = (file) => {
    const index = fileList.value.indexOf(file);
    const newFileList = fileList.value.slice();
    newFileList.splice(index, 1);
    fileList.value = newFileList;
  };

  // 文件下载
  import { aoaToSheetXlsx } from '/@/components/Excel';
  const handleDownByData = async (val: any) => {
    aoaToSheetXlsx({
      data: [],
      header: ['科目编码','科目名称','方向','现金科目(是/空)','银行科目(是/空)'],
      filename: '会计科目导入模板.xlsx',
    });
  };

  const handleOk = () => {
    uploading.value=true
    uploadParm.value=uniqueAccStandard.value+'>>>'+templateId.value+'>>>'+jici.value+'>>>'+accStyleUnique.value
    const formData = new FormData();
    fileList.value.forEach((file) => {
      formData.append('file', file as any);
    });
    formData.append('templateInfo', uploadParm.value )
    sysCountryImportCoke(formData)
    .then(async (info:any)=>{
        // console.log(info[0])
        if(info[0].code==='200' || info[0].error==='error'){
          visible.value=true
          excelResultInfo.successInfo=info[0].list.length
          excelResultInfo.excellistLength=info[0].excellist.length
          excelResultInfo.excellistInfo=info[0].excellist
          excelcolumn.value=info[0].column;
          let column=info[0].column;
          let errorsize=0
          excelResultInfo.excellistInfo.forEach(v=>{
            if(v[column]!==null){
              errorsize=errorsize+1;
            }
          })
          excelResultInfo.errorInfo=errorsize
          uploading.value = false;
        }else{
          fileList.value = [];
          uploading.value = false;
          message.error(info[0].error);
        }
      })
  };
// 把错误信息生成新的Excel导出
const errorExcel = () => {
  const columns1 = []
  let biaotou=excelResultInfo.excellistInfo[0];
  for (let i = 0; i < biaotou.length; i++) {
    if(biaotou[i]!==null){
      columns1.push(biaotou[i])
    }
  }
  columns1.push('失败原因')
  //------------------------------导出-----------------------------------
  // const columns = columns1
  // const multiHeader = []
  // const keys = []
  // const cellStyle = [
  //   {
  //     cell: 'A1',
  //     font: {
  //       sz: 16,
  //       color: {rgb: "000000"},
  //       bold: true,
  //     },
  //     fill: {
  //       fgColor: {rgb: "ffffff"},
  //     }
  //   }
  // ]
  // columns.forEach((v, index) => {
  //   multiHeader.push(v.title)
  //   keys.push(v.key)
  // })
  // const sheet = [
  //   {
  //     title: [],
  //     tHeader: multiHeader,
  //     table: excelResultInfo.excellistInfo.splice(1),
  //     keys: keys,
  //     sheetName: 'sheet1',
  //     cellStyle: cellStyle,
  //     rightrow:[],
  //     leftrow:[],
  //   },
  // ]
  // exportExcel(sheet, 'xlsx','会计科目导入模板')

  aoaToSheetXlsx({
    data: excelResultInfo.excellistInfo.splice(1),
    header: columns1,
    filename: '会计科目导入模板.xlsx',
  });

}
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
