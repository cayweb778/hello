<template>
  <BasicModal width="700px" v-bind="$attrs" title="导入模板" @ok="handleOk()" @register="register">
    <Loading :loading="compState.loading" :absolute="compState.absolute" :tip="compState.tip" />
    <template #title>
      <div style="display: flex;color: #0096c7;">
        <span style="line-height:40px;font-size: 28px;">
          <CloudUploadOutlined style="font-size: 34px;font-weight: bold"/>
        </span>
        <span style="line-height:40px;font-size: 22px;margin-top:-5px;">
          &nbsp;&nbsp;数据导入
        </span>
      </div>
    </template>
    <div class="import-centent-div" >
      <div class="import-info-div">
        <div class="import-div-top">
          <div>
          </div>
          <div>
            <span>导入内容：</span><span style="font-weight: bold;font-size: 20px;">供应商信息</span><br/><br/>
            <span>模板样式：</span>
            <Select
              v-model:value="codeTemplateId"
              placeholder=""
              style="width: 50%;margin-right: 2%"
              allow-clear>
              <SelectOption v-for="tem in templateList" :key="tem.id" :value="tem.id">
                {{ tem.tname }}
              </SelectOption>
              <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
            </Select><br/><br/>
            <span>编码规则：</span>
            <Select
              v-model:value="cusCodeType"
              placeholder=""
              style="width: 50%;margin-right: 2%"
              allow-clear>
              <SelectOption value="1">手动编码</SelectOption>
              <SelectOption value="2">自动编码</SelectOption>
              <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
            </Select><br/><br/>
          </div>
          <div>
            <Tooltip placement="top" >
              <Button size="small" style="color: #3eadbe">查看帮助</Button>
              <EllipsisOutlined style="cursor: pointer;margin-left: 10%;color: #3eadbe"/>
            </Tooltip>
            <br>
            <br>
            <Tooltip placement="top" >
              <DownloadOutlined style="font-size: 30px;"/>
              <a @click="excelTemplate(codeTemplateId)">&emsp;模板下载</a>
            </Tooltip>
          </div>
        </div>
      </div>
      <div class="import-div-bottom">
        <Tabs>
          <TabPane key="1" tab="全新添加导入">
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
          <TabPane key="2" disabled tab="覆盖">

          </TabPane>
        </Tabs>
      </div>
    </div>
    <template #footer>
      <div style="height: 35px">
        <div style="float: right">
          <a-button @click="handleClose">放弃</a-button>
          <a-button @click="handleOk(true)" type="primary" :loading="uploading"
                    :disabled="fileList.length == 0 || fileList.length> 1">开始导入
          </a-button>
        </div>
      </div>
    </template>
    <a-modal v-model:visible="visible" :closable="false" @ok="ModalClose" @cancel="ModalClose" style="top: 150px;" width="450px">
      <p style="color: #0096c7;font-size: 20px;font-weight: bold;margin-left: 10px;margin-top: 10px;">
        <CheckCircleOutlined style="font-size: 20px;font-weight: bold;"/>&nbsp;导入完成
      </p>
      <img style="float: right;width: 68px;margin-top: -25px;margin-right: 20px;" >
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
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
import { DownloadOutlined,CloudUploadOutlined,CaretDownOutlined,EllipsisOutlined,CheckCircleOutlined } from '@ant-design/icons-vue';
import {reactive, ref} from 'vue';
import {BasicModal, useModalInner} from '/@/components/Modal';
import {message, Select} from 'ant-design-vue';
import {aoaToSheetXlsx} from '/@/components/Excel';

import {useUserStoreWidthOut} from '/@/store/modules/user';
import {Upload as AUpload,
  Select as ASelect,
  Input as AInput,
  Modal as AModal,Button, Tabs,Tooltip} from 'ant-design-vue'
import {
  useCompanyOperateStoreWidthOut,
} from '/@/store/modules/operate-company';
import {groupCusAssignOrgSave} from '/@/api/record/system/supplier_group';

const {createConfirm, createWarningModal, createMessage} = useMessage();
const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
const SelectOption = Select.Option;
// 上传文件
const headers = ref({
  authorization: useUserStoreWidthOut().getToken,
  datasource: JSON.stringify({
    databaseName: '',
    schemaName: useCompanyOperateStoreWidthOut().getSchemaName,
  }),
});
const excelValue: any = ref(1)
const excelResultInfo: any = reactive({
  excellistInfo: [],
  excellistLength: '',
  successInfo: '',
  errorInfo: ''
})
const accountInfo = ref({});
const fileList = ref([]);
const cusCodeType = ref('1');
const orgSaveFlag = ref('0');
const tenantId = ref('');
const orgUnique = ref('');
const uploading = ref(false)
const visible = ref<boolean>(false);
const excelcolumn = ref('')
const emit = defineEmits(['register']);
const codeTemplateId = ref('1');
const templateList = ref([{tname:'系统简约模板',id:'1'},{tname:'系统标准模板',id:'2'}]);
const [register, {closeModal}] = useModalInner((data) => {
  orgUnique.value = data.orgUnique;
  tenantId.value = data.tenantId;
  accountInfo.value = data.accountInfo;
  fileList.value=[]
  findByGroupPermission()
});

// 集团档案权限分配表-集团供应商
async function findByGroupPermission() {
  // 集团档案权限分配表-集团供应商
  let findByPermission=await findByUserIdAndTenantId(useUserStoreWidthOut().getUserInfo.id,tenantId.value,'customer')
  if(findByPermission.length>0){
    console.log(findByPermission[0].isAuto)
    orgSaveFlag.value=findByPermission[0].isAuto
  }
}

/******************* 弹框加载中 **************************/
import { Loading } from '/@/components/Loading';
import {useMessage} from "/@/hooks/web/useMessage";
import {
  findByUserIdAndOriginId,
  findByUserIdAndTenantId
} from "/@/api/record/system/group-permission";
import {saveLog} from "/@/api/record/system/group-sys-login-log";
import {originCustomerSaveAll} from "/@/api/org/sup/org_sup";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {applyImportCus, duLiSaveApi, importCus} from "/@/api/record/supplier_data/supplier";
import {findByUniqueCode} from "/@/api/record/group/im-organize";
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
  const columns1 = ['供应商编码','供应商全称','供应商简称','税号','分类名称']
  const columns2 = ['供应商编码','供应商全称','供应商简称','税号','分类名称','母公司','对应客户','省','市','区','详细地址','电话','手机','网站','EMail','行业性质','开户银行','开户地','银行账户','银行行号']
  aoaToSheetXlsx({
    data: [],
    header: val === '1' ? columns1 : columns2,
    filename: '供应商信息.xlsx',
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
const ModalClose = () => {
  visible.value = false
  emit('save');
  closeModal();
}
const handleOk = async () => {
  openCompFullLoading()
  const formData = new FormData();
  fileList.value.forEach((file) => {
    formData.append('file', file as any);
  });
  formData.append('orgUnique', orgUnique.value);
  formData.append('cusCodeType', cusCodeType.value);
  formData.append('userName', useUserStoreWidthOut().getUserInfo.name);
  formData.append('orgPermiss', orgSaveFlag.value);
  formData.append('tenantId', tenantId.value.substring(0,tenantId.value.length-5));
  await useRouteApi(applyImportCus,{schemaName: tenantId})(formData)
    .then(async (info: any) => {
      console.log(info[0])
      if (info[0].code === '200' || info[0].error === 'error') {
        visible.value = true
        compState.loading = false
        excelResultInfo.successInfo = info[0].list.length
        excelResultInfo.excellistLength = info[0].excellist.length
        excelResultInfo.excellistInfo = info[0].excellist
        excelcolumn.value = info[0].column;
        let column = info[0].column;
        let errorsize = 0
        excelResultInfo.excellistInfo.forEach(v => {
          if (v[column] !== null) {
            errorsize = errorsize + 1;
          }
        })
        excelResultInfo.errorInfo = errorsize
        info[0].list.forEach(async (tx)=>{
          /************** 记录操作日志 ****************/
          let map={
            loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
            userId: useUserStoreWidthOut().getUserInfo.username,
            userName: useUserStoreWidthOut().getUserInfo.realName,
            optModule:'master_data',
            optFunction:'供应商',
            optRange:'1',
            uniqueCode:tenantId.value,
            optAction:'新增申请',
            optContent:'操作内容【导入供应商信息】,账套代码【'+accountInfo.value.coCode+'】,账套名称【'+accountInfo.value.companyName+'】,供应商编码【'+tx.custCode+'】,供应商全称【'+tx.custName+'】,供应商简称【'+tx.custAbbname+'】',
          }
          await saveLog(map)
          // 自动审批开启，组织供应商表、集团供应商分配表插入
          if(orgSaveFlag.value=='1'){
            tx.id=null
            // 往组织供应商信息表增加
            await originCustomerSaveAll([tx])
            .then(async (a)=>{
              let orgInfo=await findByUniqueCode(orgUnique.value)
              /************** 记录操作日志 ****************/
              let map={
                loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
                userId: useUserStoreWidthOut().getUserInfo.username,
                userName: useUserStoreWidthOut().getUserInfo.realName,
                optModule:'org',
                optFunction:'供应商',
                optRange:'0',
                uniqueCode:orgUnique.value,
                optAction:'新增',
                optContent:'操作内容【账套导入自动审批供应商信息】,组织代码【'+orgInfo.orgCode+'】,组织名称【'+orgInfo.orgName+'】,供应商编码【'+tx.custCode+'】,供应商全称【'+tx.custName+'】,供应商简称【'+tx.custAbbname+'】',
              }
              await saveLog(map)
            })
            // 往账套供应商信息表增加
            await useRouteApi(duLiSaveApi,{schemaName: tenantId})(tx)
              .then(async (a)=>{
                /************** 记录操作日志 ****************/
                let map={
                  loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
                  userId: useUserStoreWidthOut().getUserInfo.username,
                  userName: useUserStoreWidthOut().getUserInfo.realName,
                  optModule:'master_data',
                  optFunction:'供应商',
                  optRange:'1',
                  uniqueCode:tenantId.value,
                  optAction:'新增',
                  optContent:'操作内容【账套导入自动审批供应商信息】,账套代码【'+accountInfo.value.coCode+'】,账套名称【'+accountInfo.value.companyName+'】,供应商编码【'+tx.custCode+'】,供应商全称【'+tx.custName+'】,供应商简称【'+tx.custAbbname+'】',
                }
                await saveLog(map)
              })

            tx.assignUserId=useUserStoreWidthOut().getUserInfo.id
            tx.assignDate=new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," ")
            tx.acceptUserId=useUserStoreWidthOut().getUserInfo.id
            tx.acceptDate=new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," ")
            tx.dataUnique=tx.uniqueCode
            // 集团供应商信息分配记录表
            await groupCusAssignOrgSave([tx])
          }
        })
      } else {
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
    filename: '导入失败供应商信息.xlsx',
  });
}
</script>
<style src="../../../../../assets/styles/global-import-open3.less" lang="less"></style>
<style lang="less" scoped>
:deep(.ant-select-selector) {
  border: none !important;
  border-bottom: 1px solid #bdb9b9 !important;
}

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
