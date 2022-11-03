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
              style="width: 50%;margin-right: 2%"
              allow-clear>
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
              <a @click="downLoad()">&emsp;模板下载</a>
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
import {company_importCoke} from "/@/api/codekemu/codekemu";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findAll} from '/@/api/record/system/code-import-template'
import {useMessage} from "/@/hooks/web/useMessage";
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

const pageParameter = reactive({
  companyCode: '',
  companyName: '',
  iyear: '',
});
const uploading = ref(false);
const templateList = ref([]);
const fileList = ref([]);
const codeTemplateId = ref('');
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
const [register, { closeModal }] = useModalInner((data) => {
  databasenum.value=data.data.databasenum
  uniqueAccStandard.value = data.data.uniqueAccStandard;
  templateId.value = data.data.templateId;
  iyear.value = data.data.iyear;
  jici.value = data.data.jici;
  accstyle.value = data.data.accstyle;
  databaseName.value = data.data.databaseName;
  fileList.value = [];
  uploading.value=false
  pageParameter.companyCode=data.data.pageParameter.companyCode
  pageParameter.companyName=data.data.pageParameter.companyName
  pageParameter.iyear=data.data.pageParameter.iyear
  findAllCodeTemplate()
});

async function findAllCodeTemplate() {
  templateList.value=await findAll()
  codeTemplateId.value=templateList.value[0].id
}



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
const handleOk = async () => {
  if(codeTemplateId.value===''){
    createWarningModal({title: '提示', content: '请先选择要进行导入的模板类型！'})
  }
  uploading.value=true
  const formData = new FormData();
  fileList.value.forEach((file) => {
    formData.append('file', file as any);
  });

  await useRouteApi(company_importCoke,{schemaName: databasenum.value})({params:formData,databasenum:databasenum.value,
    uniqueAccStandard:uniqueAccStandard.value,templateId:templateId.value,jici:jici.value,iyear:iyear.value,codeTemplateId:codeTemplateId.value})
    .then(async (info:any)=>{
      console.log(info[0])
      if(info[0].code==='200' || info[0].error==='error'){
        visible.value=true
        excelResultInfo.successInfo=info[0].list.length
        excelResultInfo.excellistLength=info[0].excellist.length
        excelResultInfo.excellistInfo=info[0].excellist
        excelcolumn.value=info[0].column;
        colunmStr.value=info[0].colunmStr;
        let column=info[0].column;
        let errorsize=0
        excelResultInfo.excellistInfo.forEach(v=>{
          if(v[column]!==null && v[column]!==''){
            errorsize=errorsize+1;
          }
        })
        excelResultInfo.errorInfo=errorsize
        uploading.value = false;

        // 埋点-操作日志
        for (let i = 0; i < info[0].list.length; i++) {
          // 埋点-操作日志
          let log='操作内容【导入会计科目】,账套代码【'+pageParameter.companyCode+'】,账套名称【'+pageParameter.companyName+'】,年度【'+pageParameter.iyear+'】,科目编码【'+info[0].list[i].ccode+'】,科目名称【'+info[0].list[i].ccodeName+'】'
          /************** 记录操作日志 ****************/
          let map={
            loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
            userId: useUserStoreWidthOut().getUserInfo.username,
            userName: useUserStoreWidthOut().getUserInfo.realName,
            optModule:'master_data',
            optFunction:'客户',
            optRange:'1',
            optAction:'导入',
            billDate:pageParameter.iyear,
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
};

// 把错误信息生成新的Excel导出
const errorExcel = () => {
  const columns1:any = []
  let biaotou=colunmStr.value;
  for (let i = 0; i < biaotou.length; i++) {
    if(biaotou[i]!==null && biaotou[i]!=''){
      columns1.push(biaotou[i])
    }
  }
  columns1.push('失败原因')
  aoaToSheetXlsx({
    data: excelResultInfo.excellistInfo.splice(1),
    header: columns1,
    filename: databaseName.value+'-导入失败会计科目信息.xlsx',
  });
}

function downLoad() {
  let temp:any=templateList.value.filter(v=>v.id===codeTemplateId.value)
  let tjson=temp[0].tjson
  aoaToSheetXlsx({
    data: [],
    header: JSON.parse(tjson).map(item=>item.titel),
    filename: '会计科目导入模板.xlsx',
  });
}
</script>
<style src="../../../../../assets/styles/global-import-open3.less" lang="less" ></style>
<style lang="less" scoped>
:deep(.ant-select-selector) {
  border: none !important;
  border-bottom: 1px solid #bdb9b9 !important;
}
</style>
