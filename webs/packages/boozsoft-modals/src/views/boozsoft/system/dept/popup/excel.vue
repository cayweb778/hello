<template>
  <BasicModal
    width="700px"
    v-bind="$attrs"
    title="部门导入"
    @ok="handleOk()"
    @register="register"
    okText="开始导入"
    :loading="loadMark"
    :canFullscreen="false"
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
      <span style="font-size: 20px;">导入内容：</span><span style="font-weight: bold;font-size: 20px;">部门信息</span>
      <p/>
      <span style="font-size: 20px;">模板样式：</span>
      <Select
        v-model:value="codeTemplateId"
        placeholder=""
        style="width: 40%;margin-right: 2%;font-weight: bold;font-size: 20px;">
        <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
        <SelectOption v-for="tem in templateList" :key="tem.id" :value="tem.id">
          {{ tem.tname }}
        </SelectOption>
      </Select>
      <span style="font-size: 18px;margin-left: 100px;">
        <DownloadOutlined style="font-size: 30px;"/>
        <a @click="exportExcel()">&emsp;模板下载</a>
      </span>
    </div>
    <Tabs v-model:activeKey="excelValue" style="margin-top: 40px;">
      <TabPane key="1" tab="全新添加导入">
      </TabPane>
      <TabPane key="2" :disabled="true" tab="字段覆盖导入">
      </TabPane>
    </Tabs>
    <br/>
    <ImpExcel v-if="isActiveImpExcel" @success="loadDataSuccess">
      <a-button class="m-3"> 导入Excel </a-button>
    </ImpExcel>
    <br/>

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
          <Button @click="closeModal()">放弃</Button>
          <Button @click="handleOk()" :disabled="saveClick" type="primary">开始导入</Button>
        </div>
      </div>
    </template>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {nextTick, reactive,ref, unref} from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {ImpExcel} from "/@/views/boozsoft/system/project/excel/components/importexcel";
import {
  Upload as AUpload,
  Spin as ASpin,
  Select,
  Input as AInput,
  Modal as AModal, Badge, Button, Tabs, Radio,Checkbox,Tooltip
} from 'ant-design-vue';
import { DownloadOutlined,CloudUploadOutlined,CaretDownOutlined,EllipsisOutlined,AppstoreOutlined } from '@ant-design/icons-vue';
const AInputSearch=AInput.Search
const SelectOption=Select.Option
const RadioGroup = Radio.Group
const TabPane = Tabs.TabPane
import {useMessage} from "/@/hooks/web/useMessage";
// import {aoaToSheetXlsx} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
const aoaToSheetXlsx=null
import {getPsnList} from "/@/api/record/system/psn";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {psnTypeFindAll} from "/@/api/psn-type/psn-type";
import {getDeptList} from "/@/api/record/system/dept";
import {findDocumentTypeAll} from "/@/api/record/system/group-document-type";
import { Loading } from '/@/components/Loading';
const {
  createErrorModal
} = useMessage()

const formItems:any = ref({})
const excelValue:any = ref(1)
function onChange(e) {
  console.log('radio checked', e.target.value);
}

const codeTemplateId = ref('1');
const templateList = ref([{tname:'系统简约模板',id:'1'},{tname:'系统标准模板',id:'2'}]);

const saveClick:any = ref(false)

/******************* 弹框加载中 **************************/
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
      item1.deptCode = item['部门编码']
      item1.deptName = item['部门名称']
      item1.parentId = item['上级部门']
      // item1.uniqueCodeUser = item['部门负责人']
      item1.flag = '1'
      list.value.push(item1)
    }
    for (let i=0; i<list.value.length; i++){
      const item1 = list.value[i];
      //判断导入编码是否为空
      const deptCode = item1.deptCode
      if (deptCode==null || deptCode==''){
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '第'+(i+1)+'行部门编码为空,不能进行信息导入'
        })
        list.value = []
        return false
      }
      //判断导入名称是否为空
      const deptName = item1.deptName
      if (deptName==null || deptName==''){
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '第'+(i+1)+'行部门名称为空,不能进行信息导入'
        })
        list.value = []
        return false
      }
      for (let j=0; j<list.value.length; j++) {
        const item2 = list.value[j];
        if (i!=j){
          if (item1.deptCode==item2.deptCode){
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第'+i+'行部门编码信息与第'+j+'行的信息重复，请修改后重新导入！'
            })
            list.value = []
            return false
          }
          if (item1.deptName==item2.deptName){
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第'+i+'行部门名称信息与第'+j+'行的信息重复，请修改后重新导入！'
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
        if (dept.deptCode == item.deptCode) {
          msg = '第' + (i + 1) + '行部门编码重复,不能进行部门信息导入'
          return false
        }
        if (dept.deptName == item.deptName) {
          msg = '第' + (i + 1) + '行部门名称重复,不能进行部门信息导入'
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

const deptList:any = ref([])
const dynamicTenantId = ref()
const [register, { closeModal }] = useModalInner((data) => {
  saveClick.value=false
  dynamicTenantId.value = data.dynamicTenantId
  useRouteApi(getDeptList,{schemaName: dynamicTenantId})({}).then(res=>{
    deptList.value = res.items
  })

  isActiveImpExcel.value=false
  nextTick(()=>{
    isActiveImpExcel.value=true
  })
})
async function handleOk() {
  saveClick.value=true
  // formItems.value.excelValue = excelValue.value
  // formItems.value.object = list.value
  // formItems.value.cateCode = cateCode.value
  checkExcel()
  console.log(msg)
  if (msg=='') {
    emit('save', unref(list))
    closeModal()
    saveClick.value=false
    return true
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '导入失败',
      content: msg
    })
    saveClick.value=false
    return false
  }
}

//下载导入模板
function exportExcel() {
  const arrHeader = ['部门编码','部门名称','上级部门'];
  console.log(arrHeader)
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: [],
    header: arrHeader,
    filename: '部门导入模板.xlsx',
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
