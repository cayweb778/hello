<template>
  <BasicModal
    width="700px"
    v-bind="$attrs"
    title="导入"
    :canFullscreen="false"
    @register="register"
  >
    <div class="import-centent-div" style="margin: 2% 5% 0%">
      <div class="import-info-div">
        <div class="import-div-top">
          <div>
          </div>
          <div>
            <span>导入</span><br/><br/>
            <Badge status="success"/>
            <span>导入</span>
          </div>
          <div>
            <Tooltip placement="top" >
              <Button size="small" style="color: #3eadbe">查看帮助</Button>&emsp;
              <EllipsisOutlined style="cursor: pointer"/>
            </Tooltip>
          </div>
        </div>
        <div class="import-div-bottom">
          <Tabs>
            <TabPane key="1" tab="导入">

            </TabPane>
          </Tabs>
        </div>
      </div>
      <div style="margin: 3% 5%">

      </div>

      <div class="import-download-div"
           :style="{textAlign: 'right'}">
        <span>下载模板文件</span>&emsp;&emsp;
        <div class="download-div" @click="downLoad()">
          <DownloadOutlined/>
        </div>
      </div>

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
    <br>
    <br>
    <a-modal v-model:visible="visible" title="导入结果" @ok="ModalClose" @cancel="ModalClose" style="top: 0px">
      <p>文件数据：{{excelResultInfo.excellistLength-1}}个</p>
      <p>导入成功：{{excelResultInfo.successInfo}}个</p>
      <p>导入失败：{{excelResultInfo.errorInfo}}个 <a v-if="excelResultInfo.errorInfo>0" style="margin-left: 30px;" @click="errorExcel">下载描述错误excel</a></p>
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
import { DownloadOutlined} from '@ant-design/icons-vue';
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
import {company_proItemImport} from "/@/api/codekemu/codekemu";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findAll} from '/@/api/record/system/code-import-template'
import {useMessage} from "/@/hooks/web/useMessage";

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
const proItem = ref('');
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
  databasenum.value=data.data.database
  proItem.value=data.data.proItem
  fileList.value = [];
  uploading.value = false;
  findAllCodeTemplate()
});

async function findAllCodeTemplate() {
  templateList.value=await findAll()
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
  uploading.value=true
  const formData = new FormData();
  fileList.value.forEach((file) => {
    formData.append('file', file as any);
  });


  await useRouteApi(company_proItemImport,{schemaName: databasenum.value})({params:formData,proItem:proItem.value})
    .then(async (info:any)=>{
      ModalClose()
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
  aoaToSheetXlsx({
    data: [],
    header: ['科目编码','科目名称'],
    filename: '项目大类指定导入模板.xlsx',
  });
}
</script>
<style src="../../../../../assets/styles/global-import-open2.less" lang="less" scoped="scoped"></style>
