<template>
  <BasicModal
    width="800px"
    :height="300"
    v-bind="$attrs"
    title="现金流量项目导入"
    :canFullscreen="false"
    @register="register"
  >
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
            <span>导入内容：</span><span style="font-weight: bold;font-size: 20px;">现金流量项目</span><br/><br/>
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
              <a @click="exportExcel()">&emsp;模板下载</a>
            </Tooltip>
          </div>
        </div>
      </div>
      <div class="import-div-bottom">
        <Tabs>
          <TabPane key="1" tab="全新添加导入" >
            <ImpExcel v-if="isActiveImpExcel" @success="loadDataSuccess" style="padding-left: 60px;">
            </ImpExcel>
          </TabPane>

        </Tabs>
      </div>
    </div>
    <a-modal v-model:visible="visible" title="导入结果" @ok="ModalClose" @cancel="ModalClose" style="top: 0px">
      <p>文件数据：{{excelResultInfo.excellistLength-1}}个</p>
      <p>导入成功：{{excelResultInfo.successInfo}}个</p>
      <p>导入失败：{{excelResultInfo.errorInfo}}个 <a v-if="excelResultInfo.errorInfo>0" style="margin-left: 30px;" @click="errorExcel">下载描述错误excel</a></p>
    </a-modal>
    <template #footer>
      <div style="height: 35px">
        <div style="float: right">
          <a-button @click="handleClose" :disabled="uploading">放弃</a-button>
          <a-button @click="handleOk(true)" type="primary" :loading="uploading" >开始导入
          </a-button>
        </div>
      </div>
    </template>
  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {nextTick, reactive, ref, unref} from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {message, Select} from 'ant-design-vue';
import { DownloadOutlined,CloudUploadOutlined,CaretDownOutlined,EllipsisOutlined } from '@ant-design/icons-vue';
import {
  Upload as AUpload,
  Select as ASelect,
  Input as AInput,
  Modal as AModal,
  Button,
  Tabs,Tooltip} from 'ant-design-vue'
const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option
const SelectOption = Select.Option;
import {useMessage} from "/@/hooks/web/useMessage";
import {ImpExcel,aoaToSheetXlsx} from "/@/components/Excel";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getProjectCashList} from "/@/api/record/system/group-project-cash";
const {
  createErrorModal
} = useMessage()
const templateList = ref([{tname:'系统标准模板',id:'1'}]);
const codeTemplateId = ref('1');
const emit=defineEmits(['register','save'])
const isActiveImpExcel=ref(false)
const list:any = ref([])
function loadDataSuccess(excelDataList) {
  // console.log(excelDataList);
  // console.log(excelDataList[0].results);
  list.value = []
  const items = excelDataList[0].results
  for (let i=0; i<items.length; i++){
    const item = items[i]
    const item1:any = {}
    item1.projectCode = item['项目编码']
    item1.projectName = item['项目名称']
    item1.projectType = item['类型编码']
    item1.projectTypeName = item['类型名称']
    item1.fangxiang = item['方向']
    item1.flag = '1'
    list.value.push(item1)
  }
}
//导入时判断
let msg=''
function checkExcel(){
  msg=''
  for (let i=0; i<list.value.length; i++){
    const item = list.value[i];
    //判断导入编码是否为空
    const projectCode = item.projectCode
    if (projectCode==null || projectCode==''){
      msg="第"+(i+1)+'行项目编码为空,不能进行信息导入!'
      return false
    }
    //判断导入名称是否为空
    const projectName = item.projectName
    if (projectName==null || projectName==''){
      msg="第"+(i+1)+'行项目名称为空,不能进行信息导入!'
      return false
    }
    //根据导入类型导入数据是否重复
    for (let j = 0; j < projectList.value.length; j++) {
      const object = projectList.value[j];
      if (object.projectCode == projectCode) {
        msg = "第" + (i + 1) + '行项目编码重复,不能进行信息导入!'
        return false
      }
      if (object.projectName == projectName) {
        msg = "第" + (i + 1) + '行项目名称重复,不能进行信息导入!'
        return false
      }
    }
  }
  return true
}

const projectList:any = ref([])

const dynamicTenantId = ref()
const [register, { closeModal }] = useModalInner(async(data) => {
  projectList.value = await getProjectCashList()

  isActiveImpExcel.value=false
  nextTick(()=>{
    isActiveImpExcel.value=true
  })
})
async function handleOk() {
  // formItems.value.excelValue = excelValue.value
  // formItems.value.object = list.value
  // formItems.value.cateCode = cateCode.value
  checkExcel()
  console.log(msg)
  if (msg=='') {
    emit('save', unref(list))
    closeModal()
    return true
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '导入失败',
      content: msg
    })
    return false
  }
}

//下载导入模板
function exportExcel() {
  const arrHeader = ['项目编码','项目名称','类型编码','类型名称','方向','对应借方科目','对应贷方科目','状态'];
  console.log(arrHeader)
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: [],
    header: arrHeader,
    filename: '现金流量项目模板.xlsx',
  });
}
const uploading=ref(false)
const loadingRef = ref(false);
const excelValue:any = ref(1)
const excelResultInfo:any = reactive({
  excellistInfo:[],
  excellistLength:'',
  successInfo:'',
  errorInfo:''
})
/******************* 弹框加载中 **************************/
import { Loading } from '/@/components/Loading';
import {findCodeByYearAndBend} from "/@/api/record/system/group-project-cash";
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

const handleClose = () => {
  closeModal()
}
</script>

<style src="../../../../../assets/styles/global-import-open3.less" lang="less"></style>
<style lang="less" scoped>
:deep(.ant-select-selector) {
  border: none !important;
  border-bottom: 1px solid #bdb9b9 !important;
}
</style>

