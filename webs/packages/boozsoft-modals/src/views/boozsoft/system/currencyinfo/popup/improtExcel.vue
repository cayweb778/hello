<template>
  <BasicModal
    :showOkBtn="false"
    width="700px"
    :height="400"
    v-bind="$attrs"
    title="国际货币导入"
    @ok="handleOk()"
    @register="register"
  >
    <Loading :loading="compState.loading" :absolute="compState.absolute" :tip="compState.tip" />
    <div style="position: relative;background: #ffffff;border: 1px solid #e6e6e6;padding: 10px 20px;margin-top: 20px;">
      <div>
        <div style="width: 170px;background-color: #0a84ff;">
          <img src="/download.png" class="container-head-img">
        </div>
        <div style="float: right;width: calc(100% - 170px);margin-top: -115px;">
          <div style="margin-left: 20px;font-size: 20px;">
            <span style="font-weight: bold;">国际货币导入</span>
            <span style="margin-left: 175px;">
              <a-popover title="导入说明" trigger="focus">
                <template #content>
                  <p>1.导入文件格式必须为xls或xlsx，且数据表内容必须放置在第一个页签sheet1中；</p>
                  <p>2.国际货币名称、货币单位、国际代码 不允许与当前库重复 并且为必填项；</p>
                </template>
                <a-button type="link">查看帮助</a-button>
              </a-popover>
            </span>

            <div style="font-size: 14px;margin-top: 40px;">
              <a-badge status="success" />全新货币导入
            </div>
          </div>
        </div>
        <a-divider />
      </div>
    </div>
    <div>
      <div style="width:150px;margin-left: 30%;">
        <div style="margin-top: 30px;">
          <span style="font-weight: bold;">下载模板文件</span>
          <img src="/d01.png" style="width: 40px;float: right;cursor: pointer;" @click="handleDownByData">
        </div>
      </div>
      <br>
      <br>
      <div style="margin-left: 10%;">
          <a-upload
            :file-list="fileList"
            :remove="handleRemove"
            :before-upload="beforeUpload"
          >
            <a-button type="primary">
              请选择导入的文件
            </a-button>
          </a-upload>
      </div>

    </div>
    <template #footer>
      <div style="height: 35px">
        <div style="float: right">
          <a-button @click="handleClose">放弃</a-button>
          <a-button @click="handleOk(true)" type="primary" :loading="uploading" :disabled="fileList.length == 0 || fileList.length> 1">开始导入
          </a-button>
        </div>
      </div>
    </template>
    <a-modal v-model:visible="visible" title="导入结果" @ok="ModalClose" @cancel="ModalClose" centered="true">
      <p>文件数据：{{excelResultInfo.excellistLength-1}}个</p>
      <p>导入成功：{{excelResultInfo.successInfo}}个</p>
      <p>导入失败：{{excelResultInfo.errorInfo}}个 <a v-if="excelResultInfo.errorInfo>0" style="margin-left: 30px;" @click="errorExcel">下载描述错误excel</a></p>
    </a-modal>
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
  import {reactive, ref} from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { message } from 'ant-design-vue';
  import {aoaToSheetXlsx} from '/@/components/Excel';

  import { useUserStoreWidthOut } from '/@/store/modules/user';
  import {
    Upload as AUpload,
    Tabs as ATabs,
    Divider as ADivider,
    Badge as ABadge,
    Popover as APopover,
    Select as ASelect,
    Input as AInput,
    Modal as AModal,
  } from 'ant-design-vue';
  import {
    useCompanyOperateStoreWidthOut,
  } from '/@/store/modules/operate-company';
  import {exportExcel} from "/@/api/record/generalLedger/excelExport";
  const ASelectOption = ASelect.Option;
  const AInputSearch = AInput.Search;
  const ATabPane = ATabs.TabPane;
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
  const uniqueAccStandard = ref('');
  const templateId = ref('');
  const iyear = ref('');
  const uploading=ref(false)
  const emit=defineEmits(['register']);
  const visible = ref<boolean>(false);
  const database=ref('')
  const excelcolumn=ref('')
  const excelResultInfo:any = reactive({
    excellistInfo:[],
    excellistLength:'',
    successInfo:'',
    errorInfo:''
  })

  const [register, { closeModal }] = useModalInner((data) => {
    database.value=data.database
  });

/******************* 弹框加载中 **************************/
import { Loading } from '/@/components/Loading';
  import {sysimportCurrency} from "/@/api/record/system/currency";
const loadingRef = ref(false);
const compState = reactive({
  absolute: false,
  loading: false,
  tip: '处理中...',
});
function openCompFullLoading() {
  openLoading(false);
}
function openLoading(absolute: boolean) {
  compState.absolute = absolute;
  compState.loading = true;
}
/*******************END**************************/

  const handleRemove = (file) => {
    const index = fileList.value.indexOf(file);
    const newFileList = fileList.value.slice();
    newFileList.splice(index, 1);
    fileList.value = newFileList;
  };

  const beforeUpload = (file) => {
    // 默认存储最新单文件
    if (fileList.value.length > 0) fileList.value = []
    fileList.value = [...fileList.value, file]
    return false;
  }
  const handleClose = () => {
    fileList.value = [];
    closeModal()
  }
  // 文件下载
const handleDownByData = async () => {
  aoaToSheetXlsx({
    data: [],
    header: ['国际货币名称','货币单位','国际代码','换算率'],
    filename: '国际货币导入.xlsx',
  });
}

  const ModalClose = () => {
    visible.value=false
    emit('save');
    closeModal();
  }

  const handleOk = async () => {
    openCompFullLoading()
    const formData = new FormData();
    fileList.value.forEach((file) => {
      formData.append('file', file as any);
    });

    sysimportCurrency(formData)
      .then(async (info:any)=>{
        if(info[0].code==='200' || info[0].error==='error'){
          visible.value=true
          compState.loading = false
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
        }else{
          fileList.value = [];
          uploading.value = false;
          compState.loading = false
          message.error(info[0].error);
        }
      })
  };
// 把错误信息生成新的Excel导出
const errorExcel = () => {
  const columns1:any = []
  let biaotou=excelResultInfo.excellistInfo[0];
  for (let i = 0; i < biaotou.length; i++) {
    if(biaotou[i]!==null){
      columns1.push(biaotou[i])
    }
  }
  columns1.push('失败原因')
  aoaToSheetXlsx({
    data: excelResultInfo.excellistInfo.splice(1),
    header: columns1,
    filename: '导入失败国际货币信息.xlsx',
  });
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
  //     title: '客户分类信息',
  //     tHeader: multiHeader,
  //     table: excelResultInfo.excellistInfo.splice(1),
  //     keys: keys,
  //     sheetName: 'sheet1',
  //     cellStyle: cellStyle,
  //     rightrow:[],
  //     leftrow:[],
  //   },
  // ]
  // exportExcel(sheet, 'xlsx', '导入失败国际货币信息')
}
</script>
<style lang="less" scoped>
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
