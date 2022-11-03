<template>
  <BasicModal
    width="800px"
    v-bind="$attrs"
    title="科目导入"
    :canFullscreen="false"
    @register="register"
  >
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
        <br>
        <div class="import-div-top">
          <div>
          </div>
          <div>
            <span>导入内容：</span><span style="font-weight: bold;font-size: 20px;">会计科目</span><br/><br/>
            <span>模板样式：</span>
            <Select
              v-model:value="codeTemplateId"
              placeholder=""
              style="width: 50%;margin-right: 2%">
              <SelectOption v-for="tem in templateList" :key="tem.id" :value="tem.id">
                {{ tem.tname }}
              </SelectOption>
              <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
            </Select>
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
              <a @click="handleDownByData()">&emsp;模板下载</a>
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
    <a-modal v-model:visible="visible" :closable="false" @ok="ModalClose" @cancel="ModalClose" style="top: 150px;" width="280px">
      <p style="color: #328306;font-size: 16px;font-weight: bold;margin-left: 10px;"><CheckCircleOutlined />&nbsp;导入完成</p>
      <p style="margin-left: 50px;font-size: 14px;font-weight: bold;">记录总数：{{excelResultInfo.excellistLength-1}}条</p>
      <p style="margin-left: 50px;font-size: 14px;font-weight: bold;">导入成功：{{excelResultInfo.successInfo}}条</p>
      <p style="margin-left: 50px;font-size: 14px;font-weight: bold;">导入失败：{{excelResultInfo.errorInfo}}条</p>
      <p style="margin-left: 20px;font-size: 14px;font-weight: bold;"><a v-if="excelResultInfo.errorInfo>0" style="margin-left: 30px;" @click="errorExcel">下载描述错误excel</a></p>
    </a-modal>
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
import {message, Select} from 'ant-design-vue';
import {aoaToSheetXlsx} from '/@/components/Excel';
import { DownloadOutlined,CloudUploadOutlined,CaretDownOutlined,EllipsisOutlined,CheckCircleOutlined } from '@ant-design/icons-vue';
import { useUserStoreWidthOut } from '/@/store/modules/user';
import {
  Upload as AUpload,
  Spin as ASpin,
  Select as ASelect,
  Input as AInput,
  Modal as AModal, Badge, Button, Tabs, Radio,Checkbox,Tooltip
} from 'ant-design-vue';
import {
  useCompanyOperateStoreWidthOut,
} from '/@/store/modules/operate-company';
import {useMessage} from "/@/hooks/web/useMessage";
import {sysimportCoke, sysimportOrgCoke} from '/@/api/codekemu/codekemu';
import {saveLog} from "/@/api/record/system/group-sys-login-log";


const SelectOption = Select.Option;
const AInputSearch = AInput.Search;
const TabPane = Tabs.TabPane;
const RadioGroup = Radio.Group;
// 上传文件
const headers = ref({
  authorization: useUserStoreWidthOut().getToken,
  datasource: JSON.stringify({
    databaseName: '',
    schemaName: useCompanyOperateStoreWidthOut().getSchemaName,
  }),
});


const uploading = ref(false);
const templateList = ref([{id:1,tname:'系统标准模板'}]);
const fileList = ref([]);
const codeTemplateId = ref(1);
const uniqueAccStandard = ref('');
const jici = ref('');
const templateId = ref('');
const iyear = ref('');
const accstyle = ref('');
const emit=defineEmits(['register']);
const databasenum =ref('')
const databaseName =ref('')
const visible = ref<boolean>(false);
const colunmStr = ref([]);
const excelResultInfo:any = reactive({
  excellistInfo:[],
  excellistLength:'',
  successInfo:'',
  errorInfo:''
})
const excelcolumn=ref('')
const {
  createErrorModal, createWarningModal
} = useMessage()

const accStyleUnique = ref('');
const typeData = ref('');
const uploadParm = ref('')
const orientation = ref('')
const [register, { closeModal }] = useModalInner((data) => {
  uniqueAccStandard.value = data.data.uniqueAccStandard;
  templateId.value = data.data.templateId;
  jici.value = data.data.jici;
  accStyleUnique.value = data.data.accStyleUnique;
  typeData.value = data.type;
  iyear.value = data.iyear;
  orientation.value = data.orientation;
});

const ModalClose = () => {
  fileList.value = [];
  visible.value=false
  emit('save');
  closeModal();
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
  return true;
}

const handleOk = () => {
  uploading.value=true
  uploadParm.value=uniqueAccStandard.value+'>>>'+templateId.value+'>>>'+jici.value+'>>>'+accStyleUnique.value
  const formData = new FormData();
  fileList.value.forEach((file) => {
    formData.append('file', file as any);
  });
  formData.append('templateInfo', uploadParm.value )
  if(typeData.value==='zz'){
    formData.append('iyear', iyear.value )
    sysimportOrgCoke(formData)
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

          // 埋点-操作日志
          for (let i = 0; i < info[0].list.length; i++) {
            // 埋点-操作日志
            let log='【导入会计科目】,科目编码【'+info[0].list[i].ccode+'】,科目名称【'+info[0].list[i].ccodeName+'】'
            /************** 记录操作日志 ****************/
            let map={
              loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
              userId: useUserStoreWidthOut().getUserInfo.username,
              userName: useUserStoreWidthOut().getUserInfo.realName,
              optModule:'org',
              optFunction:'客户',
              optRange:'0',
              optAction:'导入',
              optContent:log,
            }
            await saveLog(map)
            /************** 记录操作日志 ****************/
          }
        }else{
          fileList.value = [];
          uploading.value = false;
          message.error(info[0].error);
        }
      })
  }
  else if(typeData.value==='jt'){
    sysimportCoke(formData)
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

          // 埋点-操作日志
          for (let i = 0; i < info[0].list.length; i++) {
            // 埋点-操作日志
            let log='【导入会计科目】,科目编码【'+info[0].list[i].ccode+'】,科目名称【'+info[0].list[i].ccodeName+'】'
            /************** 记录操作日志 ****************/
            let map={
              loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
              userId: useUserStoreWidthOut().getUserInfo.username,
              userName: useUserStoreWidthOut().getUserInfo.realName,
              optModule:'group',
              optFunction:'客户',
              optRange:'2',
              optAction:'导入',
              optContent:log,
            }
            await saveLog(map)
            /************** 记录操作日志 ****************/
          }
        }else{
          fileList.value = [];
          uploading.value = false;
          message.error(info[0].error);
        }
      })
  }
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
    filename: '导入失败会计科目信息.xlsx',
  });
}

// 文件下载
const handleDownByData = async (val: any) => {
  aoaToSheetXlsx({
    data: [],
    header: ['科目编码','科目名称','方向','科目类型','现金科目(是/空)','银行科目(是/空)','现金等价物(是/空)','数量核算(是/空)','计量单位(名称/空)',
      '外币核算(是/空)','外币名称(名称/空)','个人往来(是/空)','客户往来(是/空)','供应商往来(是/空)','部门核算(是/空)','项目核算(是/空)'],
    filename: '会计科目导入模板.xlsx',
  });
};
</script>
<style src="../../../../../assets/styles/global-import-open3.less" lang="less" ></style>
<style lang="less" scoped>
:deep(.ant-select-selector) {
  border: none !important;
  border-bottom: 1px solid #bdb9b9 !important;
}
</style>
