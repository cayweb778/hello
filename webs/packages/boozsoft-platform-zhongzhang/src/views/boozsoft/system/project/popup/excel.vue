<template>
  <BasicModal
    width="750px"
    v-bind="$attrs"
    title="项目信息导入"
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
      <span style="font-size: 20px;">导入内容：</span><span style="font-weight: bold;font-size: 20px;">项目信息</span>
      <p/>
      <span v-if="projectClassCtl!='1'">
        <span style="font-size: 20px;">栏目样式：</span>
        <Select v-model:value="cateCode" @change="cateCodeChange" placeholder="请选择项目样式" style="width: 50%;font-size: 16px;font-weight: bold;">
          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
          <SelectOption v-for="cate in cateList" :key="cate.projectCateCode" :value="cate.projectCateCode">
            ({{ cate.projectCateCode }}){{ cate.projectCateName }}
          </SelectOption>
        </Select>
      </span>
      <span v-if="projectClassCtl=='1'">
        <span style="font-size: 20px;">项目大类：</span>
        <Select v-model:value="itemCode" @change="itemCodeChange" placeholder="请选择项目样式" style="width: 50%;font-size: 16px;font-weight: bold;">
          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
          <SelectOption v-for="item in itemList" :key="item.itemCode" :value="item.itemCode">
            ({{ item.itemCode }}){{ item.itemName }}
          </SelectOption>
        </Select>
      </span>
      <span style="font-size: 18px;margin-left: 100px;">
        <DownloadOutlined style="font-size: 30px;"/>
        <a @click="exportExcel()">&emsp;模板下载</a>
      </span>
    </div>
    <Tabs v-model:activeKey="excelValue" style="margin-top: 40px;">
      <TabPane key="1" tab="全新添加导入">
      </TabPane>
      <TabPane key="2" tab="字段覆盖导入">
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
<!--    <template #title>
      <div style="display: flex;margin-top: 10px;margin-left: 10px;" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;">
          <CloudUploadOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;数据导入
        </span>
      </div>
    </template>
    <div class="import-centent-div" >
      <div class="import-info-div">
        <div class="import-div-top">
          <div style="width: 20%;">
          </div>
          <div style="width: 65%;">
            <span style="font-size: 16px;">导入内容：</span><span style="font-weight: bold;font-size: 16px;">项目信息</span><br/><br/>
            <span v-if="projectClassCtl!='1'">
            <span style="font-size: 16px;">栏目样式：</span>
            <Select v-model:value="cateCode" @change="cateCodeChange" placeholder="请选择项目样式" style="width: 60%;font-size: 16px;font-weight: bold;">
              <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
              <SelectOption v-for="cate in cateList" :key="cate.projectCateCode" :value="cate.projectCateCode">
                ({{ cate.projectCateCode }}){{ cate.projectCateName }}
              </SelectOption>
            </Select>
            </span>
            <span v-if="projectClassCtl=='1'">
            <span style="font-size: 16px;">项目大类：</span>
            <Select v-model:value="itemCode" @change="itemCodeChange" placeholder="请选择项目样式" style="width: 70%;font-size: 16px;font-weight: bold;">
              <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
              <SelectOption v-for="item in itemList" :key="item.itemCode" :value="item.itemCode">
                ({{ item.itemCode }}){{ item.itemName }}
              </SelectOption>
            </Select>
            </span>

          </div>
          <div style="width: 15%;">
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
        <Tabs v-model:activeKey="excelValue">
          <TabPane key="1" tab="全新添加导入">
          </TabPane>
          <TabPane key="2" tab="字段覆盖导入">
          </TabPane>
        </Tabs>
        <br/>
        <ImpExcel v-if="isActiveImpExcel" @success="loadDataSuccess">
          <a-button class="m-3"> 导入Excel </a-button>
        </ImpExcel>
      </div>
    </div>

    <template #footer>
      <div>
        <Button @click="closeModal()">放弃</Button>
        <Button @click="handleOk()" :disabled="saveClick" type="primary">开始导入</Button>
      </div>
    </template>-->

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {nextTick, reactive, ref, unref} from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import { ImpExcel } from '../excel/components/importexcel'
// import { ImpExcel } from '/@/components/excel'
// import {Select as ASelect,Input as AInput,Radio as ARadio} from 'ant-design-vue'
import {
  Upload as AUpload,
  Spin as ASpin,
  Select,
  Input as AInput,
  Modal as AModal, Badge, Button, Tabs, Radio,Checkbox,Tooltip
} from 'ant-design-vue';
import { DownloadOutlined,CloudUploadOutlined,CaretDownOutlined,EllipsisOutlined } from '@ant-design/icons-vue';
const AInputSearch=AInput.Search
const SelectOption=Select.Option
const RadioGroup = Radio.Group
const TabPane = Tabs.TabPane
import {useMessage} from "/@/hooks/web/useMessage";
import {cateFindStateFlag,columnFindCate} from "/@/api/group_project_category/project_category";
// import {getFromEdit} from "/@/api/record/system/sys_project_category";
// import {aoaToSheetXlsx} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
const aoaToSheetXlsx=null
import {findAllList, findProjectByCateCode} from "/@/api/record/system/project";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useProjectStoreWidthOut} from "/@/store/modules/project";
import {findProClassTree, projectClassFindAll} from "/@/api/record/system/project_class";
import {getProjectItemList} from "/@/api/record/system/project-item";
import { Loading } from '/@/components/Loading';
const {
  createErrorModal
} = useMessage()

const formItems:any = ref({})
const excelValue:any = ref(1)
function onChange(e) {
  console.log('radio checked', e.target.value);
}
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

const columnList:any = ref([])
const projectList = ref([])
const projectClassList = ref([])
async function itemCodeChange(){
  itemList.value.forEach(item=>{
    if (item.itemCode==itemCode.value){
      cateCode.value = item.cateCode
    }
  })
  projectList.value = await useRouteApi(findAllList,{schemaName: useProjectStoreWidthOut().getDynamicTenantId})({itemCode:itemCode.value,projectClassCtl:projectClassCtl.value})
  await cateCodeChange()
}
async function cateCodeChange(){
  columnList.value = await columnFindCate(cateCode.value)
  // useRouteApi(findProjectByCateCode,{schemaName: useProjectStoreWidthOut().getDynamicTenantId})(cateCode.value).then(res=>{
  //   // console.log(res)
  //   projectList.value = res
  // })
  // useRouteApi(findProClassTree, {schemaName: useProjectStoreWidthOut().getDynamicTenantId})(cateCode.value).then(res=>{
  //   projectClassList.value = res
  // })
}

const emit=defineEmits(['register','save'])
const isActiveImpExcel=ref(false)
const list:any = ref([])
const loadMark = ref(false)
function loadDataSuccess(excelDataList) {
  // console.log(excelDataList);
  // console.log(excelDataList[0].results);
  list.value = []
  if (cateCode.value!='' && cateCode.value!=null){
    const items = excelDataList[0].results
    for (let i=0; i<items.length; i++){
      const item = items[i]
      const item1:any = {}
      for (let j=0; j<columnList.value.length; j++){
        const column = columnList.value[j]
        item1[toHump(column.ctitle)]=item[column.cname]
      }
      item1['projectCateCode']=cateCode.value
      item1['successState']='1'
      if (item1['projectClassCode']==null || item1['projectClassCode']==''){
        item1['projectClassCode'] = '99'
      } else {
        let num = 0
        for (let i=0; i<projectClassList.value.length; i++) {
          const projectClass:any = projectClassList.value[i];
          if (item1['projectClassCode']==projectClass.projectClassCode){
            num= num+1
          }
        }
        if (num==0){
          item1['projectClassCode'] = '99'
        }
      }
      if (item1['jiesuan']==null || item1['jiesuan']==''){
        item1['jiesuan'] = '0'
      }
      item1['itemCode'] = itemCode.value
      list.value.push(item1)
    }
    for (let i=0; i<list.value.length; i++) {
      const item1 = list.value[i];
      //判断项目编号是否为空
      if (item1['projectCode'] == null || item1['projectCode'] == '') {
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '第' + (i + 2) + '行项目编号为空,不能进行项目信息导入！'
        })
        list.value = []
        return false
      }
      //判断项目名称是否为空
      if (item1['projectName'] == null || item1['projectName'] == '') {
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '第' + (i + 2) + '行项目名称为空,不能进行项目信息导入！'
        })
        list.value = []
        return false
      }
      for (let j=0; j<list.value.length; j++) {
        const item2 = list.value[j];
        if (i != j) {
          if (item1['projectCode'] != '' && item1['projectCode'] != null && item1['projectCode'] == item2['projectCode']) {
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第' + (i+2) + '行项目编号信息与第' + (j+2) + '行的信息重复，请修改后重新导入！'
            })
            list.value = []
            return false
          }
          if (item1['projectName'] != '' && item1['projectName'] != null && item1['projectName'] == item2['projectName']) {
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第' + (i+2) + '行项目名称信息与第' + (j+2) + '行的信息重复，请修改后重新导入！'
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
      content: '未发现导入数据，请检查数据是否在sheet1页签中！'
    })
  }
}
//导入时判断日期、借贷方、结算方式
let msg=''
function checkExcel(){
  msg=''
  for (let i=0; i<list.value.length; i++){
    const item = list.value[i];
    //判断项目编码是否为空
    const projectCode = item['projectCode']
    if (projectCode==null || projectCode==''){
      msg="第"+(i+2)+'行项目编码为空,不能进行项目信息导入'
      return false
    }
    //判断导入名称是否为空
    const projectName = item['projectName']
    if (projectName==null || projectName==''){
      msg="第"+(i+2)+'行项目名称为空,不能进行项目信息导入'
      return false
    }
    //根据字段属性判断字段附加字段值是否正确
    for (let a = 0; a < columnList.value.length; a++) {
      let column = columnList.value[a]
      let columnValue:any = item[toHump(column.ctitle)]
      let columnType = column.ctype
      if (columnValue!=null) {
        if (columnType == '1') {
        }
        //判断是否整数
        else if (columnType == '2') {
          if (!isInteger(columnValue)) {
            msg = "第" + (i + 2) + '行添加的' + column.cname + '值不正确，请检查后重新导入'
            return false
          }
        }
        //判断是否实数
        else if (columnType == '3') {
          if (!/^-?(?:\d+|\d{1,3}(?:,\d{3})+)(?:\.\d+)?$/.test(columnValue)) {
            msg = "第" + (i + 2) + '行添加的' + column.cname + '值不正确，请检查后重新导入'
            return false
          }
        }
        //判断是否日期
        else if (columnType == '4') {
          let isnum = /^\d+$/.test(columnValue)
          if (isnum && columnValue.length == 8) {
            columnValue = columnValue.substring(0, 4) + '-' + columnValue.substring(4, 6) + '-' + columnValue.substring(6, 8)
          } else if (columnValue.length == 10) {
            columnValue = columnValue.substring(0, 4) + '-' + columnValue.substring(5, 7) + '-' + columnValue.substring(8, 10)
          }
          if (isNaN(columnValue) && !isNaN(Date.parse(columnValue))) {
            item[column.cname]=columnValue
            // console.log("data是日期格式！")
          } else {
            msg = "第" + (i + 2) + '行添加的' + column.cname + '值不正确，请检查后重新导入'
            return false
          }
        }
        //判断是否布尔类型
        else if (columnType == '5') {
          if (columnValue==true || columnValue=='true'){
            columnValue='1'
            item[column.cname]='1'
          }
          else if (columnValue==false || columnValue=='false'){
            columnValue='0'
            item[column.cname]='0'
          }
          item[column.cname] = Number(columnValue)
          if (columnValue!='0' && columnValue!='1'){
            msg = "第" + (i + 2) + '行添加的' + column.cname + '值不正确，请检查后重新导入'
            return false
          }
        }
      }
    }
    list.value[i]=item
    //根据导入类型导入数据是否重复
    if (excelValue.value=='1') {
      //导入全新项目信息
      for (let j = 0; j < projectList.value.length; j++) {
        const project = projectList.value[j];
        if (project['projectCode'] == projectCode) {
          msg = "第" + (i + 2) + '行项目编码重复,不能进行项目信息导入'
          return false
        }
        if (project['projectName'] == projectName) {
          msg = "第" + (i + 2) + '行项目名称重复,不能进行项目信息导入'
          return false
        }
      }
    } else {
      //对已存在项目进行辅加字段信息导入
      for (let j = 0; j < projectList.value.length; j++) {
        const project = projectList.value[j];
        if (project['projectCode'] == item['projectCode']) {
          item['id'] = project['id']
          item['uniqueCode'] = project['uniqueCode']
        }
      }
      const id = item['id']
      if (id==null || id==''){
        msg = "第" + (i + 2) + '行导入的字段对应项目信息不存在,不能进行附加字段信息导入'
        return false
      }
    }
  }
  return true
}

const cateList:any = ref([])
const itemList:any = ref([])
const cateCode:any = ref('')
const itemCode:any = ref('')
const projectClassCtl:any = ref('')
const [register, { closeModal }] = useModalInner(async (data) => {
  saveClick.value=false
  loadMark.value = true
  itemCode.value = data.data.itemCode
  projectClassCtl.value = data.data.projectClassCtl
  const res = await cateFindStateFlag()
    cateList.value = res
    cateList.value.forEach(
      function (item) {
        if (item.projectCateCode == data.data.projectCateCode) {
          cateCode.value = item.projectCateCode
          cateCodeChange()
        }
      }
    )
    if (cateCode.value == '' || cateCode.value == null) {
      cateCode.value = cateList.value[0].projectCateCode
    }
  const items: any = await useRouteApi(getProjectItemList,{schemaName: useProjectStoreWidthOut().getDynamicTenantId})({})
  itemList.value = items
  projectList.value = await useRouteApi(findAllList,{schemaName: useProjectStoreWidthOut().getDynamicTenantId})({itemCode:data.data.itemCode,projectClassCtl:data.data.projectClassCtl})
  projectClassList.value = await useRouteApi(projectClassFindAll, {schemaName: useProjectStoreWidthOut().getDynamicTenantId})({itemCode:data.data.itemCode,projectClassCtl:data.data.projectClassCtl})
  isActiveImpExcel.value = false
  await nextTick(() => {
    isActiveImpExcel.value = true
  })
  loadMark.value = false
})
async function handleOk() {
  // formItems.value.excelValue = excelValue.value
  // formItems.value.object = list.value
  // formItems.value.cateCode = cateCode.value
  saveClick.value=true
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
  const arrHeader = columnList.value.map(item=>item.cname);
  console.log(arrHeader)
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: [],
    header: arrHeader,
    filename: cateCode.value+'栏目样式项目模板.xlsx',
  });
}

// 下划线转换驼峰
function toHump(name:any) {
  return name.replace(/\_(\w)/g, function(all:any, letter:any){
    return letter.toUpperCase();
  });
}
//判断是否整数
function isInteger(obj) {
  return obj%1 === 0
}
</script>
<style lang="less" scoped>
@import '/@/assets/styles/redTitle-open.less';
:deep(.ant-select-selector) {
  border: none !important;
  border-bottom: 1px solid #bdb9b9 !important;
}
</style>
