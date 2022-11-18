<template>
  <BasicModal
    width="800px"
    :height="300"
    v-bind="$attrs"
    title="预算来源导入"
    @ok="handleOk()"
    @register="register"
    okText="开始导入"
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
            <span>导入内容：</span><span style="font-weight: bold;font-size: 20px;">预算来源</span><br/><br/>
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
          <TabPane key="1" tab="全新添加导入">
            <ImpExcel v-if="isActiveImpExcel" @success="loadDataSuccess">
              <a-button class="m-3"> 导入Excel </a-button>
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
import { ImpExcel } from '/@/components/Excel'
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
// import {aoaToSheetXlsx} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
const aoaToSheetXlsx=null
import {
  findSettModesAll,
  findSettModesByFlag,
  deleteSettModes,
  saveSettModes,
  editFlag
} from '/@/api/record/system/group-budget-source'
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
const {
  createErrorModal
} = useMessage()

const codeTemplateId = ref('1');
const templateList = ref([{tname:'系统标准模板',id:'1'}]);
const emit=defineEmits(['register','save'])
const isActiveImpExcel=ref(false)
const list:any = ref([])
function loadDataSuccess(excelDataList) {
  // console.log(excelDataList);
  // console.log(excelDataList[0].results);
  list.value = []
  const items = excelDataList[0].results
  if (items.length>0) {
    for (let i = 0; i < items.length; i++) {
      const item = items[i]
      const item1: any = {}
      item1.ecCode = item['预算来源编码']
      item1.ecName = item['预算来源名称']
      item1.flag = item['状态']
      //匹配对应参数
      item1.isEnd = '1'
      // item1.uniqueCodeUser = item['部门负责人']
      item1.flag = '1'
      list.value.push(item1)
    }
    for (let i=0; i<list.value.length; i++){
      const item1 = list.value[i];
      //判断导入编码是否为空
      const deptCode = item1.ecCode
      if (deptCode==null || deptCode==''){
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '第'+(i+1)+'行预算来源编码为空,不能进行信息导入'
        })
        list.value = []
        return false
      }
      //判断导入名称是否为空
      const deptName = item1.ecName
      if (deptName==null || deptName==''){
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '第'+(i+1)+'行预算来源名称为空,不能进行信息导入'
        })
        list.value = []
        return false
      }
      for (let j=0; j<list.value.length; j++) {
        const item2 = list.value[j];
        if (i!=j){
          if (item1.ecCode==item2.ecCode){
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第'+i+'行预算来源编码信息与第'+j+'行的信息重复，请修改后重新导入！'
            })
            list.value = []
            return false
          }
          if (item1.ecName==item2.ecName){
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第'+i+'行预算来源名称信息与第'+j+'行的信息重复，请修改后重新导入！'
            })
            list.value = []
            return false
          }
        }
      }
    }
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '未发现导入数据，请检查数据是否在sheet1页签中'
    })
  }
}
//导入时判断
let msg=''
function checkExcel(){
  msg=''
  if (list.value.length>0) {
    for (let i = 0; i < list.value.length; i++) {
      const item = list.value[i];
      //判断导入编码是否为空
      /*const deptCode = item['deptCode']
      if (deptCode==null || deptCode==''){
        msg="第"+(i+1)+'行部门编码为空,不能进行信息导入'
        return false
      }
      //判断导入名称是否为空
      const deptName = item['deptName']
      if (deptName==null || deptName==''){
        msg="第"+(i+1)+'行部门名称为空,不能进行信息导入'
        return false
      }*/
      for (let j = 0; j < deptList.value.length; j++) {
        const dept = deptList.value[j];
        if (dept.ecCode == item.ecCode) {
          msg = '第' + (i + 1) + '行支出分类编码重复,不能进行部门信息导入'
          return false
        }
        if (dept.ecName == item.ecName) {
          msg = '第' + (i + 1) + '行支出分类名称重复,不能进行部门信息导入'
          return false
        }
      }
    }
    return true
  } else {
    msg = '请选择您需要导入的文件!'
    return false
  }
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
import {getDeptList} from "/@/api/record/system/zf-class";
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
const deptList:any = ref([])
const zfList:any = ref([])
const dynamicTenantId = ref()
const [register, { closeModal }] = useModalInner((data) => {
  dynamicTenantId.value = data.data.dynamicTenantId
  useRouteApi(findSettModesAll,{schemaName: dynamicTenantId})().then(res=>{
    deptList.value = res.items
  })
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
  const arrHeader = ['分类编码','分类名称','状态'];
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: [],
    header: arrHeader,
    filename: '预算来源导入模板.xlsx',
  });
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
