<template>
  <BasicModal
    width="800px"
    v-bind="$attrs"
    title="项目信息导入"
    @ok="handleOk()"
    @register="register"
    okText="开始导入"
    :loading="loadMark"
    :canFullscreen="false"
  >
    <template #title>
      <div style="display: flex;margin-top: 10px;" class="vben-basic-title">
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
            <span style="font-size: 16px;">栏目样式：</span>
            <Select v-model:value="cateCode" @change="cateCodeChange" placeholder="请选择项目样式" style="width: 60%;font-size: 16px;font-weight: bold;">
              <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
              <SelectOption v-for="cate in cateList" :key="cate.projectCateCode" :value="cate.projectCateCode">
                ({{ cate.projectCateCode }}){{ cate.projectCateName }}
              </SelectOption>
            </Select><br/><br/>
            <span style="font-size: 16px;">编码方式：</span>
            <Select
              v-model:value="isAutoCode"
              placeholder=""
              style="width: 60%;margin-right: 2%;font-size: 16px;font-weight: bold;">
              <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
              <SelectOption value="1">自动编码</SelectOption>
              <SelectOption value="2">手动编码</SelectOption>
            </Select>

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
        <br/>
      </div>
    </div>
    <!--    <div class="nc-open-content">
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
        </div>-->

    <template #footer>
      <div>
        <Button @click="closeModal()">放弃</Button>
        <Button @click="handleOk()" :disabled="saveClick" type="primary">开始导入</Button>
      </div>
    </template>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {nextTick, ref, unref} from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import { ImpExcel } from '../../../system/project/excel/components/importexcel'
// import { ImpExcel } from '/@/components/excel'
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
import {cateFindStateFlag} from "/@/api/group_project_category/project_category";
import {aoaToSheetXlsx} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
import {
  findBukongProjectCode, findMaxProjectCode,
  findProjectByCateCode,
  getFromEdit
} from "/@/api/record/system/group-project";
import {load} from "/@/api/group/FileEncodingRules";
//import {mul} from "/@/views/boozsoft/gdzc/gu-ding-zi-chan-add/calculation";
const mul=null
const {
  createErrorModal
} = useMessage()

const formItems:any = ref({})
const excelValue:any = ref(1)
function onChange(e) {
  console.log('radio checked', e.target.value);
}

const saveClick:any = ref(false)
const isAutoCode = ref('1')

const columnList:any = ref([])
const projectList = ref([])
async function cateCodeChange(){
  columnList.value = await getFromEdit(cateCode.value)
  findProjectByCateCode(cateCode.value).then(res=>{
    // console.log(res)
    projectList.value = res
  })
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
      }
      if (item1['jiesuan']==null || item1['jiesuan']==''){
        item1['jiesuan'] = '0'
      }
      list.value.push(item1)
    }
    for (let i=0; i<list.value.length; i++) {
      const item1 = list.value[i];
      //判断项目编号是否为空
      if (isAutoCode.value!='1' || excelValue.value != '1') {
        if (item1['projectCode'] == null || item1['projectCode'] == '') {
          createErrorModal({
            iconType: 'warning',
            title: '提示',
            content: '第' + (i + 2) + '行项目编号为空,不能进行项目信息导入！'
          })
          list.value = []
          return false
        }
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
              content: '第' + (i+2) + '行项目名称信息与第' + (j+2)  + '行的信息重复，请修改后重新导入！'
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
async function checkExcel() {
  msg = ''
  if (list.value.length > 0) {
    await getMaxCode()
    for (let i = 0; i < list.value.length; i++) {
      const item = list.value[i];
      //判断是否为自动编码
      if (isAutoCode.value=='1' && excelValue.value == '1') {
        item.projectCode = ccode.value.substring(0, qianzhui.value) + '' +
          pad(add(ccode.value.substring(qianzhui.value, add(qianzhui.value, serialNumLength.value)), i), serialNumLength.value)
      }
      //判断项目编码是否为空
      const projectCode = item['projectCode']
      if (projectCode == null || projectCode == '') {
        msg = "第" + (i + 2) + '行项目编码为空,不能进行项目信息导入'
        return false
      }
      //判断导入名称是否为空
      const projectName = item['projectName']
      if (projectName == null || projectName == '') {
        msg = "第" + (i + 2) + '行项目名称为空,不能进行项目信息导入'
        return false
      }
      //根据字段属性判断字段附加字段值是否正确
      for (let a = 0; a < columnList.value.length; a++) {
        let column = columnList.value[a]
        let columnValue: any = item[toHump(column.ctitle)]
        let columnType = column.ctype
        if (columnValue != null) {
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
              item[column.cname] = columnValue
              // console.log("data是日期格式！")
            } else {
              msg = "第" + (i + 2) + '行添加的' + column.cname + '值不正确，请检查后重新导入'
              return false
            }
          }
          //判断是否布尔类型
          else if (columnType == '5') {
            if (columnValue == true || columnValue=='true') {
              columnValue = '1'
              item[column.cname] = '1'
            } else if (columnValue == false || columnValue=='false') {
              columnValue = '0'
              item[column.cname] = '0'
            }
            if (columnValue != '0' && columnValue != '1') {
              msg = "第" + (i + 2) + '行添加的' + column.cname + '值不正确，请检查后重新导入'
              return false
            }
          }
        }
      }
      list.value[i] = item
      //根据导入类型导入数据是否重复
      if (excelValue.value == '1') {
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
        if (id == null || id == '') {
          msg = "第" + (i + 2) + '行导入的字段对应项目信息不存在,不能进行附加字段信息导入'
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

const cateList:any = ref([])
const cateCode:any = ref()
const [register, { closeModal }] = useModalInner(async (data) => {
  saveClick.value=false
  loadMark.value = true
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
  isActiveImpExcel.value = false
  await nextTick(() => {
    isActiveImpExcel.value = true
  })
  loadMark.value = false
})
async function handleOk() {
  saveClick.value=true
  // formItems.value.excelValue = excelValue.value
  // formItems.value.object = list.value
  // formItems.value.cateCode = cateCode.value
  await checkExcel()
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
    filename: cateCode.value+'大类项目模板.xlsx',
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

//根据编码规则获取编码
const serialNumLength:any = ref(0)
const qianzhui:any = ref(0)
const ccode:any = ref('')
async function getMaxCode() {
  // 读取编码规则-项目
  let str = ""
  let temp = await load('1-4')
  // console.log(temp)
  if(temp.id==null){
    return false
  }
  serialNumLength.value = temp.serialNumLength
  if(temp.prefixOneIs=='true'){
    if (temp.prefixOne=='4-0'){
      //项目大类
      let projectCode = ''
      if (formItems.value.itemCode==null || formItems.value.itemCode==''){
        projectCode = pad(0,temp.prefixOneLength)
      } else {
        projectCode = formItems.value.itemCode.length >= temp.prefixOneLength?
          formItems.value.itemCode.substring(0,temp.prefixOneLength) : pad(formItems.value.itemCode,temp.prefixOneLength)
      }
      str = str + projectCode
    }
    if (temp.prefixOne=='4-1'){
      //项目分类
      let projectCode = ''
      if (formItems.value.projectClassCode==null || formItems.value.projectClassCode==''){
        projectCode = pad(0,temp.prefixOneLength)
      } else {
        projectCode = formItems.value.projectClassCode.length >= temp.prefixOneLength?
          formItems.value.projectClassCode.substring(0,temp.prefixOneLength) : pad(formItems.value.projectClassCode,temp.prefixOneLength)
      }
      str = str + projectCode
    }
    if (temp.prefixOne=='88'){
      //日期(年月)
      let yy = new Date().getFullYear()
      let mm = new Date().getMonth() + 1 < 10 ? '0' + (new Date().getMonth()+ 1) : (new Date().getMonth()+ 1)
      str = str + yy + mm
    }
    if (temp.prefixOne=='99'){
      //手动录入
      str = str + pad(temp.prefixOneCustomize,temp.prefixOneLength)
    }
    //分隔符
    if (temp.delimiter=='2'){
      str = str + '.'
    } else if (temp.delimiter=='3'){
      str = str + '-'
    }
  }
  if(temp.prefixTwoIs=='true'){
    if (temp.prefixTwo=='4-0'){
      //项目大类
      let projectCode = ''
      if (formItems.value.itemCode==null || formItems.value.itemCode==''){
        projectCode = pad(0,temp.prefixOneLength)
      } else {
        projectCode = formItems.value.itemCode.length >= temp.prefixTwoLength?
          formItems.value.itemCode.substring(0,temp.prefixTwoLength) : pad(formItems.value.itemCode,temp.prefixTwoLength)
      }
      str = str + projectCode
    }
    if (temp.prefixTwo=='4-1'){
      //项目分类
      let projectCode = ''
      if (formItems.value.projectClassCode==null || formItems.value.projectClassCode==''){
        projectCode = pad(0,temp.prefixTwoLength)
      } else {
        projectCode = formItems.value.projectClassCode.length >= temp.prefixTwoLength?
          formItems.value.projectClassCode.substring(0,temp.prefixTwoLength) : pad(formItems.value.projectClassCode,temp.prefixTwoLength)
      }
      str = str + projectCode
    }
    if (temp.prefixTwo=='88'){
      //日期(年月)
      let yy = new Date().getFullYear()
      let mm = new Date().getMonth() + 1 < 10 ? '0' + (new Date().getMonth()+ 1) : (new Date().getMonth()+ 1)
      str = str + yy + mm
    }
    if (temp.prefixTwo=='99'){
      //手动录入
      str = str + pad(temp.prefixTwoCustomize,temp.prefixTwoLength)
    }
    //分隔符
    if (temp.delimiter=='2'){
      str = str + '.'
    } else if (temp.delimiter=='3'){
      str = str + '-'
    }
  }
  if(temp.prefixThreeIs=='true'){
    if (temp.prefixThree=='4-0'){
      //项目大类
      let projectCode = ''
      if (formItems.value.itemCode==null || formItems.value.itemCode==''){
        projectCode = pad(0,temp.prefixThreeLength)
      } else {
        projectCode = formItems.value.itemCode.length >= temp.prefixThreeLength?
          formItems.value.itemCode.substring(0,temp.prefixThreeLength) : pad(formItems.value.itemCode,temp.prefixThreeLength)
      }
      str = str + projectCode
    }
    if (temp.prefixThree=='4-1'){
      //项目分类
      let projectCode = ''
      if (formItems.value.projectClassCode==null || formItems.value.projectClassCode==''){
        projectCode = pad(0,temp.prefixThreeLength)
      } else {
        projectCode = formItems.value.projectClassCode.length >= temp.prefixThreeLength?
          formItems.value.projectClassCode.substring(0,temp.prefixThreeLength) : pad(formItems.value.projectClassCode,temp.prefixThreeLength)
      }
      str = str + projectCode
    }
    if (temp.prefixThree=='88'){
      //日期(年月)
      let yy = new Date().getFullYear()
      let mm = new Date().getMonth() + 1 < 10 ? '0' + (new Date().getMonth()+ 1) : (new Date().getMonth()+ 1)
      str = str + yy + mm
    }
    if (temp.prefixThree=='99'){
      //手动录入
      str = str + pad(temp.prefixThreeCustomize,temp.prefixThreeLength)
    }
    //分隔符
    if (temp.delimiter=='2'){
      str = str + '.'
    } else if (temp.delimiter=='3'){
      str = str + '-'
    }
  }
  qianzhui.value = str.length
  // console.log(temp.autoNum)
  if(temp.autoNum=='false') {
    const project = await findBukongProjectCode(temp.serialNumLength,add(str.length,temp.serialNumLength),str.length,str)
    if (project.projectCode != null && project.projectCode != '') {
      str = str + pad(project.projectCode, temp.serialNumLength)
    } else {
      str = str + pad(temp.serialNumStr, temp.serialNumLength)
    }
  } else {
    const project = await findMaxProjectCode(temp.serialNumLength,add(str.length,temp.serialNumLength),str.length,str)
    if (project.projectCode != null && project.projectCode != '') {
      str = str + pad(add(project.projectCode,1), temp.serialNumLength)
    } else {
      str = str + pad(temp.serialNumStr, temp.serialNumLength)
    }
  }
  ccode.value = str
}

/**
 * 字符串前补0
 * @param num
 * @param n
 */
function pad(num, n) {
  let len = num.toString().length;
  while(len < n) {
    num = "0" + num;
    len++;
  }
  return num;
}

/**
 * 加法
 * @param a
 * @param b
 */
function add(a, b) {
  let c, d, e;
  try {
    c = a.toString().split(".")[1].length;
  } catch (f) {
    c = 0;
  }
  try {
    d = b.toString().split(".")[1].length;
  } catch (f) {
    d = 0;
  }
  return e = Math.pow(10, Math.max(c, d)), (mul(a, e) + mul(b, e)) / e;
}

</script>
<style lang="less" scoped>
.vben-basic-title {
  color: #0096c7 !important;
  border:none !important;
}
.import-centent-div{
  .import-info-div {
    width: 90%;
    margin-left: 2%;
    height: 180px;
    border-radius: 4px;
    margin-top: 30px;

    .import-div-top {
      width: 100%;
      height: 70%;
      display: inline-flex;
      justify-content: space-between;

      > div:nth-of-type(1) {
        width: 25%;
        height: auto;
        background-image: url(/nc/download2.png);
        background-size: 80% 80%;
        background-repeat: no-repeat;
        background-position: 12px;
      }

      > div:nth-of-type(2) {
        width: 55%;
        height: auto;
        padding: 18px 2%;

        > span {
          color: black;
          font-size: 20px;
        }

        > span:nth-of-type(1) {
          font-size: 20px;
        }
      }

      > div:nth-of-type(3) {
        width: 20%;
        height: auto;
        padding: 3.5% 0;
      }

    }
  }
  .import-div-bottom {
    margin-left: 2%;
    width: 96%;
    height: 30%;
  }
  .import-download-div{
    width: 100%;height: 60px;display: inline-flex; justify-content: center; line-height: 50px;
    .download-div {
      width: 50px;
      height: 50px;
      display: block;
      background-color: #6a6a6a;
      border-radius: 50%;
      padding: 2px 10px;
      font-size: 30px;
      color: white;
      cursor: pointer;
    }

    .download-div:hover {
      color: #0096c7;
      background-color: #b4b4b4;
    }
  }
}
:deep(.ant-select-selector) {
  border: none !important;
  border-bottom: 1px solid #bdb9b9 !important;
}
</style>
