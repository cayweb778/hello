<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="项目信息导入"
    @ok="handleOk()"
    @register="register"
    okText="开始导入"
    :loading="loadMark"
  >
    <div class="nc-open-content">
      <div class="open-content-up" style="height: 400px;margin-left: 5%;width:90%;">

        <div style="position: relative;background: #ffffff;border: 1px solid #999999;padding: 10px 20px;margin-top: 20px;">
          <div style="position: absolute;background: #ffffff; top: -15px;left: 20px;padding: 0 10px;">导入说明</div>
          <p>1.导入文件格式必须为xls或xlsx，且数据表内容必须放置在第一个页签sheet1中；</p>
          <p>2.“导入全新项目信息”是指项目库中不存在的项目，项目编码和名称不允许与当前库重复；</p>
          <p>3.“对已存在的项目进行辅加字段信息导入”是导入系统默认字段之外的项目信息，导入的项目编码在项目库中必须存在，且导入字段名与当前项目样式栏目定义名称相同</p>
        </div>

        <label>数据导入范围</label><br/>
        <a-radio-group v-model:value="excelValue" @change="onChange">
          <a-radio :value="1" style="width:100%;text-align: left;">
            导入全新项目信息
          </a-radio><br/>
          <a-radio :value="2" style="width:100%;text-align: left">
            对已存在项目进行辅加字段信息导入
          </a-radio>
        </a-radio-group>
        <br/><br/>
        <label>项目栏目样式</label>
        <a-select v-model:value="cateCode" @change="cateCodeChange" placeholder="请选择项目样式" style="width: 35%;">
          <a-select-option v-for="cate in cateList" :key="cate.projectCateCode" :value="cate.projectCateCode">
            ({{ cate.projectCateCode }}){{ cate.projectCateName }}
          </a-select-option>
        </a-select>
        <label style="width: 200px;">按项目栏目样式下载：</label>
        <a @click="exportExcel()">导入模板</a>

        <div style="margin-left: 40px;margin-top: 30px;">
          <ImpExcel v-if="isActiveImpExcel" @success="loadDataSuccess">
            <a-button class="m-3"> 导入Excel </a-button>
          </ImpExcel>
        </div>
        <br>

      </div>
    </div>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {nextTick, ref, unref} from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import { ImpExcel } from '../excel/components/importexcel'
// import { ImpExcel } from '/@/components/excel'
import {Select as ASelect,Input as AInput,Radio as ARadio} from 'ant-design-vue'
const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option
const ARadioGroup = ARadio.Group
import {useMessage} from "/@/hooks/web/useMessage";
import {cateFindStateFlag} from "/@/api/project_category/project_category";
import {getFromEdit} from "/@/api/record/system/sys_project_category";
//import {aoaToSheetXlsx} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
const aoaToSheetXlsx=null
import {findAllList, findProjectByCateCode} from "/@/api/record/system/project";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useProjectStoreWidthOut} from "/@/store/modules/project";
import {findProClassTree, projectClassFindAll} from "/@/api/record/system/project_class";
const {
  createErrorModal
} = useMessage()

const formItems:any = ref({})
const excelValue:any = ref(1)
function onChange(e) {
  console.log('radio checked', e.target.value);
}

const columnList:any = ref([])
const projectList = ref([])
const projectClassList = ref([])
async function cateCodeChange(){
  columnList.value = await useRouteApi(getFromEdit,{schemaName: useProjectStoreWidthOut().getDynamicTenantId})(cateCode.value)
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
        item1['projectClassCode'] = '9999'
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
          content: '第' + (i + 1) + '行项目编号为空,不能进行项目信息导入！'
        })
        list.value = []
        return false
      }
      //判断项目名称是否为空
      if (item1['projectName'] == null || item1['projectName'] == '') {
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '第' + (i + 1) + '行项目名称为空,不能进行项目信息导入！'
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
              content: '第' + i + '行项目编号信息与第' + j + '行的信息重复，请修改后重新导入！'
            })
            list.value = []
            return false
          }
          if (item1['projectName'] != '' && item1['projectName'] != null && item1['projectName'] == item2['projectName']) {
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第' + i + '行项目名称信息与第' + j + '行的信息重复，请修改后重新导入！'
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
      msg="第"+(i+1)+'行项目编码为空,不能进行项目信息导入'
      return false
    }
    //判断导入名称是否为空
    const projectName = item['projectName']
    if (projectName==null || projectName==''){
      msg="第"+(i+1)+'行项目名称为空,不能进行项目信息导入'
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
            msg = "第" + (i + 1) + '行添加的' + column.cname + '值不正确，请检查后重新导入'
            return false
          }
        }
        //判断是否实数
        else if (columnType == '3') {
          if (!/^-?(?:\d+|\d{1,3}(?:,\d{3})+)(?:\.\d+)?$/.test(columnValue)) {
            msg = "第" + (i + 1) + '行添加的' + column.cname + '值不正确，请检查后重新导入'
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
            msg = "第" + (i + 1) + '行添加的' + column.cname + '值不正确，请检查后重新导入'
            return false
          }
        }
        //判断是否布尔类型
        else if (columnType == '5') {
          if (columnValue=='true'){
            columnValue='1'
            item[column.cname]='1'
          }
          else if (columnValue=='false'){
            columnValue='0'
            item[column.cname]='0'
          }
          item[column.cname] = Number(columnValue)
          if (columnValue!='0' && columnValue!='1'){
            msg = "第" + (i + 1) + '行添加的' + column.cname + '值不正确，请检查后重新导入'
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
          msg = "第" + (i + 1) + '行项目编码重复,不能进行项目信息导入'
          return false
        }
        if (project['projectName'] == projectName) {
          msg = "第" + (i + 1) + '行项目名称重复,不能进行项目信息导入'
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
        msg = "第" + (i + 1) + '行导入的字段对应项目信息不存在,不能进行附加字段信息导入'
        return false
      }
    }
  }
  return true
}

const cateList:any = ref([])
const cateCode:any = ref()
const itemCode:any = ref()
const [register, { closeModal }] = useModalInner(async (data) => {
  loadMark.value = true
  itemCode.value = data.data.itemCode
  const res = await useRouteApi(cateFindStateFlag, {schemaName: useProjectStoreWidthOut().getDynamicTenantId})({})
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
  projectList.value = await useRouteApi(findAllList,{schemaName: useProjectStoreWidthOut().getDynamicTenantId})({itemCode:data.data.itemCode,projectClassCtl:data.data.projectClassCtl})
  projectClassList.value = await useRouteApi(projectClassFindAll, {schemaName: useProjectStoreWidthOut().getDynamicTenantId})({})
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
:deep(.ant-calendar-picker-input.ant-input),:deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector .ant-select-selection-search-input){
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;
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

    .ant-tabs-tab-active::before {
      position: absolute;
      top: 0px;
      left: 0;
      width: 100%;
      border-top: 2px solid transparent;
      border-radius: 2px 2px 0 0;
      transition: all 0.3s;
      content: '';
      pointer-events: none;
      background-color: rgb(1, 143, 251);
    }
  }
}
</style>
