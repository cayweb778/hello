<template>
  <BasicModal
    width="800px"
    v-bind="$attrs"
    :canFullscreen="false"
    @register="register"
  >
    <Loading :loading="compState.loading" :absolute="compState.absolute" :tip="compState.tip" />
    <template #title>
      <div style="height: 30px;width: 100%;background-color: #5f375c;color: white;line-height: 30px;text-align: left;">
        <AppstoreOutlined  style="margin: 0 2px;font-size: 14px;"/>
        <span style="font-size: 14px">数据导入</span>
      </div>
    </template>
    <div style="width: 100%;display: table;">
      <div  style="text-align: right;width: 44%;float: left;">
        <CloudUploadOutlined style="margin: 0 2px;font-size: 40px;color: #0096c7;"/>
      </div>
      <div style="text-align: left;width: 54%;float: right;font-size: 24px;font-weight: bold;color: #0096c7;">
        主单据导入
      </div>
    </div>
    <div style="margin-top: 40px;margin-left: 30px;">
      <span style="font-size: 20px;">导入内容：</span><span style="font-weight: bold;font-size: 20px;">客户信息</span>
      <p/>
      <span style="font-size: 20px;">模板样式：</span>
      <Select
        v-model:value="codeTemplateId"
        placeholder=""
        style="width: 30%;margin-right: 2%;font-weight: bold;font-size: 20px;"
        allow-clear>
        <SelectOption v-for="tem in templateList" :key="tem.id" :value="tem.id">
          {{ tem.tname }}
        </SelectOption>
        <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
      </Select>
      <span style="font-size: 18px;margin-left: 100px;">
        <DownloadOutlined style="font-size: 30px;"/>
        <a @click="excelTemplate(codeTemplateId)">&emsp;模板下载</a>
      </span>
    </div>
    <Tabs v-model:activeKey="tabPaneKey" style="margin-top: 40px;">
      <TabPane key="1" tab="数据文件">
        <br>
        <span style="margin-left: 70px;">
            <a-upload
              :file-list="fileList"
              :remove="handleRemove"
              :before-upload="beforeUpload"
            >
          <a-button type="primary">
            请选择导入的文件
          </a-button>
        </a-upload>
        </span>
        <br><br>
      </TabPane>
      <TabPane key="2" disabled tab="覆盖"/>
    </Tabs>
    <a-modal v-model:visible="visible" :closable="false" @ok="ModalClose" @cancel="ModalClose" style="top: 150px;" width="450px">
      <p style="color: #0096c7;font-size: 20px;font-weight: bold;margin-left: 10px;margin-top: 10px;">
        <CheckCircleOutlined style="font-size: 20px;font-weight: bold;"/>&nbsp;导入完成
      </p>
      <img src="/download2.png" style="float: right;width: 68px;margin-top: -25px;margin-right: 20px;" >
      <div style="height: 150px;margin-top:80px;">
        <p style="margin-left: 50px;font-size: 14px;font-weight: bold;">
          记录总数：
          <span style="float: right;margin-right: 30%;">{{excelResultInfo.excellistLength-1}}条</span>
        </p>
        <p style="margin-left: 50px;font-size: 14px;font-weight: bold;">
          导入成功：
          <span style="float: right;margin-right: 30%;">{{excelResultInfo.successInfo}}条</span>
        </p>
        <p style="margin-left: 50px;font-size: 14px;font-weight: bold;">
          导入失败：
          <span style="float: right;margin-right: 30%;">{{excelResultInfo.errorInfo}}条</span>
        </p>
        <p style="margin-left: 20px;font-size: 14px;">
          <a v-if="excelResultInfo.errorInfo>0" style="margin-left: 30px;" @click="errorExcel">下载描述错误excel</a>
        </p>
      </div>
    </a-modal>
    <template #footer>
      <div style="height: 35px">
        <div style="float: left">
          <a-popover title="使用说明" trigger="click">
            <template #content>
              <p>文件中带 * 为必填项</p>
            </template>
            <a-button>使用说明</a-button>
          </a-popover>
        </div>
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
import { DownloadOutlined,CloudUploadOutlined,CaretDownOutlined,EllipsisOutlined ,AppstoreOutlined} from '@ant-design/icons-vue';
import {reactive, ref} from 'vue';
import { BasicModal, useModalInner } from '/@/components/Modal';
import {message, Select} from 'ant-design-vue';
import {aoaToSheetXlsx, jsonToSheetXlsx} from '/@/components/Excel';
import { useUserStoreWidthOut } from '/@/store/modules/user';
import { useCompanyOperateStoreWidthOut} from '/@/store/modules/operate-company';
import {importCus} from "/@/api/record/costomer_data/customer";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {Upload as AUpload,
  Select as ASelect,
  Input as AInput,
  Modal as AModal,Button, Tabs,Tooltip} from 'ant-design-vue'
import {exportExcel} from "/@/api/record/generalLedger/excelExport";
const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option
const SelectOption = Select.Option;
// 上传文件
const headers = ref({
  authorization: useUserStoreWidthOut().getToken,
  datasource: JSON.stringify({
    databaseName: '',
    schemaName: useCompanyOperateStoreWidthOut().getSchemaName,
  }),
});
const excelValue:any = ref(1)
const excelResultInfo:any = reactive({
  excellistInfo:[],
  excellistLength:'',
  successInfo:'',
  errorInfo:''
})
const tabPaneKey = ref('1');
const codeTemplateId = ref('1');
const templateList = ref([{tname:'系统简约模板',id:'1'},{tname:'系统标准模板',id:'2'}]);
const fileList = ref([]);
const username = ref('');
const emit=defineEmits([]);
const uploading=ref(false)
const visible = ref<boolean>(false);
const databaseName=ref('')
const database=ref('')
const excelcolumn=ref('')
const accountInfo=ref({})

const [register, { closeModal }] = useModalInner((data) => {
  username.value = data.data;
  databaseName.value=data.databaseName
  database.value=data.database
  accountInfo.value=data.accountInfo
  fileList.value=[]
});

/******************* 弹框加载中 **************************/
import { Loading } from '/@/components/Loading';
import {saveAllLog, saveLog} from "/@/api/record/system/group-sys-login-log";
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

const excelTemplate = (data) => {
  handleDownByData(data)
}
// 文件下载
const handleDownByData = async (val: any) => {
  const columns1 = ['客户编码','客户全称','客户简称','税号','分类名称']
  const columns2 = ['客户编码','客户全称','客户简称','税号','分类名称','母公司','对应供应商','省','市','区','详细地址','电话','手机','网站','EMail','行业性质','开户银行','开户地','银行账户','银行行号','联系人','价格级次','税率']
  aoaToSheetXlsx({
    data: [],
    header: val === '1'?columns1:columns2,
    filename: databaseName.value+'-客户信息导入模板.xlsx',
  });
}

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
const handleOk = async () => {
  openCompFullLoading()
  const formData = new FormData();
  fileList.value.forEach((file) => {
    formData.append('file', file as any);
  });
  await useRouteApi(importCus,{schemaName: database})({params:formData,userid:useUserStoreWidthOut().getUserInfo.id}).then(async (info:any)=>{
    // console.log(info[0])
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

      // 埋点-操作日志
      let loglist:any=[]
      for (let i = 0; i < info[0].list.length; i++) {
        let log='【导入客户信息】,账套代码【'+accountInfo.value.coCode+'】,账套名称【'+accountInfo.value.companyName+'】,客户编码【'+info[0].list[i].cusCode+'】,客户全称【'+info[0].list[i].cusName+'】,客户全称【'+info[0].list[i].cusAbbname+'】'
        /************** 记录操作日志 ****************/
        let map={
          loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
          userId: useUserStoreWidthOut().getUserInfo.username,
          userName: useUserStoreWidthOut().getUserInfo.realName,
          optModule:'master_data',
          optFunction:'客户',
          optRange:'1',
          uniqueCode:database.value.substring(0,database.value.length-5),
          optAction:'导入',
          optContent:log,
        }
        loglist.push(map)
        // await saveLog(map)
        /************** 记录操作日志 ****************/
      }
      await saveAllLog(loglist)
    }else{
      fileList.value = [];
      uploading.value = false;
      compState.loading = false
      message.error(info[0].error);
    }
  })
};
const ModalClose = () => {
  visible.value=false
  emit('save');
  closeModal();
}
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
    filename: '导入失败客户信息.xlsx',
  });
}
</script>
<style lang="less" scoped>
@import '/@/assets/styles/redTitle-open.less';
:deep(.ant-select-selector) {
  border: none !important;
  border-bottom: 1px solid #bdb9b9 !important;
}
</style>
