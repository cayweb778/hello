<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="辅助核算定义"
    @ok="handleOk()"
    @register="register"
    :canFullscreen="false"
  >
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='0'">
          <PlusCircleOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;辅助核算定义
        </span>
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='1'">
          <FormOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;辅助核算定义
        </span>
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='2'">
          <FileSearchOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;辅助核算定义
        </span>
      </div>
    </template>
    <div class="nc-open-content">
      <div class="open-content-up" style="height: 320px">
        <a-radio-group v-model:value="formItems.ctype">
          <a-radio value="1" style="width:100px;">
            <span style="color: #000000;font-weight: bold;font-size: 10px;">系统档案</span>
          </a-radio>
          <a-radio value="2" style="width:100px;margin-left: -10px;">
            <span style="color: #000000;font-weight: bold;font-size: 10px;">自定义档案</span>
          </a-radio>
        </a-radio-group>
        <br/><br/>

        <div style="text-align: center;" v-if="formItems.ctype=='1'">
          <label style="font-size: 18px;margin-left: 0;width:150px;">系统档案名称：</label>
<!--          <a-input v-model:value="formItems.cname" placeholder="系统档案名称" class="abc" style="width: 50%;" />-->
          <a-select v-model:value="formItems.cankaoDuixiang" placeholder="系统档案名称" @change="changSysValue()" class="abc" style="width: 50%;font-size: 18px;">
            <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
            <a-select-option v-for="item in systemlist" :key="item.key" :value="item.value">
              {{ item.cname }}
            </a-select-option>
          </a-select>
          <br/><br/>
          <label>档案编码：</label>
          <a-input v-model:value="formItems.ccode" :disabled="true" @blur="checkCode()" placeholder="编码"/>
          <br/><br/>
          <label>凭证字段：</label>
          <a-input v-model:value="formItems.cfield" readonly placeholder="凭证字段"/>
          <br/><br/>
          <label>取值方式：</label>
          <a-select v-model:value="formItems.bend" placeholder="取值方式" style="width: 50%">
            <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
            <a-select-option value="1">末级档案</a-select-option>
            <a-select-option value="0">标准档案</a-select-option>
          </a-select>
          <br/><br/>
          <label>说明：</label>
          <a-input v-model:value="formItems.remarks" placeholder="说明" />
        </div>

        <div style="text-align: center;" v-if="formItems.ctype=='2'">
          <label style="font-size: 18px;margin-left: 0;width:150px;">自定义档案名称：</label>
          <a-input v-model:value="formItems.cname" placeholder="自定义档案名称" class="abc" style="width: 50%;" />
          <br/><br/>
          <label>档案编码：</label>
          <a-input v-model:value="formItems.ccode" :disabled="true" @blur="checkCode()" placeholder="编码"/>
          <br/><br/>
          <label>数据来源：</label>
          <a-select v-model:value="formItems.cankaoDuixiang" @change="changValue()" placeholder="参考对象" style="width: 50%">
            <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
            <a-select-option v-for="item in cankaolist" :key="item.key" :value="item.value">
              {{ item.value }}
            </a-select-option>
          </a-select>
          <br/><br/>
          <label>对应凭证字段：</label>
          <a-select v-model:value="formItems.cdfine" @change="changCdfine()" placeholder="对应凭证表辅助核算字段" style="width: 50%">
            <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
            <a-select-option v-for="num in numList" :key="num" :value="num">
              cdfine{{ num }}
            </a-select-option>
          </a-select>
          <br/><br/>
          <label>说明：</label>
          <a-input v-model:value="formItems.remarks" placeholder="说明" />
        </div>

      </div>
    </div>

    <template #footer>
      <div v-if="isState=='2'">
        <a-button @click="closeModal()" type="primary">关闭</a-button>
      </div>
      <div v-if="isState=='0'">
        <a-button @click="closeModal()">取消</a-button>
        <a-button @click="handleOk()" :disabled="saveClick">保存</a-button>
        <a-button @click="handleOkAdd()" :disabled="saveClick" type="primary">保存并新增</a-button>
      </div>
      <div v-if="isState=='1'">
        <a-button @click="closeModal()">放弃</a-button>
        <a-button @click="handleOk()" :disabled="saveClick" type="primary">保存</a-button>
      </div>
    </template>

  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import { ref, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {Input as AInput,Select as ASelect,Radio as ARadio} from 'ant-design-vue'
import {
  PlusCircleOutlined,
  FormOutlined,
  FileSearchOutlined,
  CaretDownOutlined
} from '@ant-design/icons-vue'
import {useMessage} from "/@/hooks/web/useMessage";
import {
  findAllFuzhuHesuanList, findBukongCcode,
  findByCankaoDuixiang,
  findByCode,
  findByName,
  findMaxCcode
} from "/@/api/record/system/fuzhu-hesuan";
import {findByModelOrderByDefinedCode, getDefinedFileList} from "/@/api/record/system/defined-file";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
//import {mul} from "/@/views/boozsoft/gdzc/gu-ding-zi-chan-add/calculation";
const mul=null

const ASelectOption = ASelect.Option
const ARadioGroup = ARadio.Group

const {
  createErrorModal
} = useMessage()

const emit = defineEmits(['register', 'save'])

const formItems: any = ref({})

let changeBeforeModel: any = {}

const cankaolist: any = ref([])
const systemlist: any = ref([])

const numList: any = ref([])

const dynamicTenantId = ref('')
const defaultAdName = ref('')
const isState = ref('0')
const saveClick:any = ref(false)
const [register, {closeModal}] = useModalInner((data: any) => {
  saveClick.value=false
  dynamicTenantId.value = data.dynamicTenantId
  defaultAdName.value = data.defaultAdName
  isState.value = data.isState
  numList.value = []
  for (let i = 1; i < 31; i++) {
    numList.value.push(i)
  }
  systemlist.value = [
    {
      key: 'sys_psn',
      cname: '个人',
      value: '人员档案',
      where: "where is_del='0' ",
      flag: " and flag='1' ",
      unique: 'unique_code',
      name: 'psn_name',
      code: 'psn_code',
      cfield: 'cperson_id'
    },
    {
      key: 'sys_department',
      cname: '部门',
      value: '部门档案',
      where: "where is_del='0' ",
      flag: " and flag='1' ",
      unique: 'unique_code',
      name: 'dept_name',
      code: 'dept_code',
      cfield: 'cdept_id'
    },
    {
      key: 'customer',
      cname: '客户',
      value: '客户档案',
      where: "where is_del='0' ",
      flag: " and flag='1' ",
      unique: 'unique_code',
      name: 'cust_name',
      code: 'cust_code',
      cfield: 'ccus_id'
    },
    {
      key: 'supplier',
      cname: '供应商',
      value: '供应商档案',
      where: "where is_del='0' ",
      flag: " and flag='1' ",
      unique: 'unique_code',
      name: 'sup_name',
      code: 'sup_code',
      cfield: 'csup_id'
    },
    {
      key: 'project',
      cname: '项目',
      value: '项目档案',
      where: "where is_del='0' ",
      flag: '',
      unique: 'unique_code',
      name: 'project_name',
      code: 'project_code',
      cfield: 'project_id'
    },
    {
      key: 'project_item',
      cname: '项目大类',
      value: '项目大类档案',
      where: 'where 1=1 ',
      flag: '',
      unique: 'id',
      name: 'item_name',
      code: 'item_code',
      cfield: 'project_class_id'
    },
    {
      key: 'expenditure_class',
      cname: '支出功能分类',
      value: '支出功能分类档案',
      where: 'where 1=1 ',
      flag: " and flag='1' ",
      unique: 'unique_code',
      name: 'ec_name',
      code: 'ec_code',
      cfield: 'ys_zcgnfl'
    },
    {
      key: 'sys_dept_class',
      cname: '部门支出功能分类',
      value: '部门支出功能分类档案',
      where: 'where 1=1 ',
      flag: " and flag='1' ",
      unique: 'unique_code',
      name: 'ec_name',
      code: 'ec_code',
      cfield: 'ys_bmzcjjfl'
    },
    {
      key: 'sys_zf_class',
      cname: '政府支出经济分类',
      value: '政府支出经济分类档案',
      where: 'where 1=1 ',
      flag: " and flag='1' ",
      unique: 'unique_code',
      name: 'ec_name',
      code: 'ec_code',
      cfield: 'ys_zfzcjjfl'
    },
    {
      key: 'budget_source',
      cname: '预算来源',
      value: '预算来源档案',
      where: 'where 1=1 ',
      flag: " and flag='1' ",
      unique: 'bs_code',
      name: 'bs_name',
      code: 'bs_code',
      cfield: 'ys_ysly'
    },
  ]
  cankaolist.value = [
    {
      key: 'customer_class',
      value: '客户分类',
      where: 'where 1=1 ',
      flag: " and flag='1' ",
      unique : 'unique_custclass',
      name : 'cus_cclass_name',
      code : 'cus_class'
    },
    {
      key : 'supplier_class',
      value : '供应商分类',
      where : 'where 1=1 ',
      flag : " and flag='1' ",
      unique : 'unique_supclass',
      name : 'sup_class_name',
      code : 'sup_class'
    },
    {
      key : 'sys_psn_type',
      value : '人员类别',
      where : 'where 1=1 ',
      flag : '',
      unique : 'unique_code',
      name : 'psn_type_name',
      code : 'psn_type_code'
    },
    /*{
      key: '_app_group_sys_zone',
      value: '行政区划',
      where: 'where 1=1 ',
      flag: '',
      unique: 'id',
      name: 'zone_name',
      code: 'procode'
    },*/
    {
      key: 'project_class',
      value: '项目分类',
      where : 'where 1=1 ',
      flag: '',
      unique: 'project_code',
      name: 'project_name',
      code: 'project_code'
    },
  ]
  useRouteApi(findByModelOrderByDefinedCode, {schemaName: dynamicTenantId})('ZW').then(res => {
    res.forEach(item => {
      const item1: any = {}
      item1.key = 'defined_record'
      item1.value = '(' + item.definedCode + ')' + item.definedName
      item1.where = "where defined_code='" + item.definedCode + "' "
      item1.flag = " and flag='1' "
      item1.unique = 'record_code'
      item1.name = 'record_name'
      item1.code = 'record_code'
      item1.definedCode = item.definedCode
      cankaolist.value.push(item1)
    })

    useRouteApi(findAllFuzhuHesuanList, {schemaName: dynamicTenantId})({}).then(resList => {
      resList.forEach(item => {
        numList.value.forEach((num, index) => {
          if (num == item.cdfine && data.data.cdfine != item.cdfine) {
            numList.value.splice(index, 1)
          }
        })
        cankaolist.value.forEach((cankao, index) => {
          if (cankao.value == item.cankaoDuixiang && data.data.cankaoDuixiang != item.cankaoDuixiang) {
            cankaolist.value.splice(index, 1)
          }
        })
        systemlist.value.forEach((cankao, index) => {
          if (cankao.value == item.cankaoDuixiang && data.data.cankaoDuixiang != item.cankaoDuixiang) {
            systemlist.value.splice(index, 1)
          }
        })
      })
    })
  })
  // 方式2
  formItems.value = {
    id: data.data.id,
    ccode: data.data.ccode,
    cname: data.data.cname,
    cankaoDuixiang: data.data.cankaoDuixiang,
    flag: '1',
    remarks: data.data.remarks,
    cankaoDuixiangTable: data.data.cankaoDuixiangTable,
    cdfine: data.data.cdfine,
    cankaoDuixiangWhere: data.data.cankaoDuixiangWhere,
    cankaoDuixiangFlag: data.data.cankaoDuixiangFlag,
    cankaoDuixiangKey: data.data.cankaoDuixiangKey,
    cankaoDuixiangLabel: data.data.cankaoDuixiangLabel,
    cankaoDuixiangCode: data.data.cankaoDuixiangCode,
    ctype: data.data.ctype==null?'1':data.data.ctype,
    cfield: data.data.cfield,
    bend: data.data.bend==null?'0':data.data.bend,
    definedCode: data.data.definedCode
  }
  if (data.data.id==null || data.data.id==''){
    maxCode()
  }
  changeBeforeModel = JSON.parse(JSON.stringify(formItems))
})

function changValue(){
  checkCankaoDuixiang()
  cankaolist.value.forEach(item=>{
    if (formItems.value.cankaoDuixiang==item.value){
      formItems.value.cankaoDuixiangTable = item.key
      formItems.value.cankaoDuixiangWhere = item.where
      formItems.value.cankaoDuixiangFlag = item.flag
      formItems.value.cankaoDuixiangKey = item.unique
      formItems.value.cankaoDuixiangLabel = item.name
      formItems.value.cankaoDuixiangCode = item.code
      formItems.value.definedCode = item.definedCode
    }
  })
}
function changSysValue(){
  systemlist.value.forEach(item=>{
    if (formItems.value.cankaoDuixiang==item.value){
      formItems.value.cname = item.cname
      formItems.value.cankaoDuixiangTable = item.key
      formItems.value.cankaoDuixiangWhere = item.where
      formItems.value.cankaoDuixiangFlag = item.flag
      formItems.value.cankaoDuixiangKey = item.unique
      formItems.value.cankaoDuixiangLabel = item.name
      formItems.value.cankaoDuixiangCode = item.code
      formItems.value.cfield = item.cfield
    }
  })
}

function changCdfine() {
  formItems.value.cfield = 'cdfine'+formItems.value.cdfine
}

let isChanged:boolean = false
async function handleOk() {
  saveClick.value=true
  isChanged = !(formItems.value.ccode == changeBeforeModel._value.ccode
    &&formItems.value.cname == changeBeforeModel._value.cname
    &&formItems.value.remarks == changeBeforeModel._value.remarks
    &&formItems.value.cankaoDuixiang == changeBeforeModel._value.cankaoDuixiang
    &&formItems.value.flag == changeBeforeModel._value.flag
    &&formItems.value.remarks == changeBeforeModel._value.remarks
    &&formItems.value.cankaoDuixiangTable == changeBeforeModel._value.cankaoDuixiangTable)
  console.log(isChanged)
  if (formItems.value.ccode==''){
    createErrorModal({
      iconType: 'warning',
      title: '保存失败',
      content: '编码不能为空！'
    })
    saveClick.value=false
    return false
  }
  else if (formItems.value.cname==''){
    createErrorModal({
      iconType: 'warning',
      title: '保存失败',
      content: '名称不能为空！'
    })
    saveClick.value=false
    return false
  }
  else if (formItems.value.cankaoDuixiang==''){
    createErrorModal({
      iconType: 'warning',
      title: '保存失败',
      content: '数据来源不能为空！'
    })
    saveClick.value=false
    return false
  }
  else if (formItems.value.cdfine=='' && formItems.value.ctype=='2'){
    createErrorModal({
      iconType: 'warning',
      title: '保存失败',
      content: '对应凭证字段不能为空！'
    })
    saveClick.value=false
    return false
  }
  if(isChanged){
    emit('save', unref(formItems))
    closeModal()
    saveClick.value=false
    return true
  }
  closeModal()
  saveClick.value=false
  // alert("没有改变过")
  return false
}

async function handleOkAdd() {
  saveClick.value=true
  isChanged = !(formItems.value.ccode == changeBeforeModel._value.ccode
    &&formItems.value.cname == changeBeforeModel._value.cname
    &&formItems.value.remarks == changeBeforeModel._value.remarks
    &&formItems.value.cankaoDuixiang == changeBeforeModel._value.cankaoDuixiang
    &&formItems.value.flag == changeBeforeModel._value.flag
    &&formItems.value.remarks == changeBeforeModel._value.remarks
    &&formItems.value.cankaoDuixiangTable == changeBeforeModel._value.cankaoDuixiangTable)
  console.log(isChanged)
  if (formItems.value.ccode==''){
    createErrorModal({
      iconType: 'warning',
      title: '保存失败',
      content: '编码不能为空！'
    })
    saveClick.value=false
    return false
  }
  else if (formItems.value.cname==''){
    createErrorModal({
      iconType: 'warning',
      title: '保存失败',
      content: '名称不能为空！'
    })
    saveClick.value=false
    return false
  }
  else if (formItems.value.cankaoDuixiang==''){
    createErrorModal({
      iconType: 'warning',
      title: '保存失败',
      content: '数据来源不能为空！'
    })
    saveClick.value=false
    return false
  }
  else if (formItems.value.cdfine=='' && formItems.value.ctype=='2'){
    createErrorModal({
      iconType: 'warning',
      title: '保存失败',
      content: '对应凭证字段不能为空！'
    })
    saveClick.value=false
    return false
  }
  if(isChanged){
    formItems.value = changeBeforeModel._value
    await maxCode()
    saveClick.value=false
    return true
  }
  // closeModal()
  // alert("没有改变过")
  saveClick.value=false
  return false
}

async function checkCode() {
  if ((changeBeforeModel._value.ccode != undefined && changeBeforeModel._value.ccode != '') && changeBeforeModel._value.ccode == formItems.value.ccode) {
    return true
  }
  if (formItems.value.ccode == null || formItems.value.ccode == '') {
    return true
  }
  const res = await useRouteApi(findByCode, {schemaName: dynamicTenantId})(formItems.value.ccode)
  if (res.length != 0) {
    createErrorModal({
      iconType: 'warning',
      title: '输入失败',
      content: '编码已存在，请重新输入！'
    })
    formItems.value.ccode = ''
    return false
  }
  return true
}

async function checkName() {
  if ((changeBeforeModel._value.cname != undefined && changeBeforeModel._value.cname != '') && changeBeforeModel._value.cname == formItems.value.cname) {
    return true
  }
  if (formItems.value.cname == null || formItems.value.cname == '') {
    return true
  }
  const res = await useRouteApi(findByName, {schemaName: dynamicTenantId})(formItems.value.cname)
  if (res.length != 0) {
    createErrorModal({
      iconType: 'warning',
      title: '输入失败',
      content: '名称已存在，请重新输入！'
    })
    formItems.value.cname = ''
    return false
  }
  return true
}

async function checkCankaoDuixiang() {
  if ((changeBeforeModel._value.cankaoDuixiang != undefined && changeBeforeModel._value.cankaoDuixiang != '') && changeBeforeModel._value.cankaoDuixiang == formItems.value.cankaoDuixiang) {
    return true
  }
  if (formItems.value.cankaoDuixiang == null || formItems.value.cankaoDuixiang == '') {
    return true
  }
  const res = await useRouteApi(findByCankaoDuixiang, {schemaName: dynamicTenantId})(formItems.value.cankaoDuixiang)
  if (res.length != 0) {
    createErrorModal({
      iconType: 'warning',
      title: '输入失败',
      content: '数据来源已存在，请重新选择！'
    })
    formItems.value.cankaoDuixiang = ''
    return false
  }
  return true
}

async function maxCode(){
  const item1 = await useRouteApi(findBukongCcode,{schemaName: dynamicTenantId})({})
  if (item1!=null && item1.ccode!=null && item1.ccode!=''){
    formItems.value.ccode = pad(item1.ccode,2)
  } else {
    const item2 = await useRouteApi(findMaxCcode, {schemaName: dynamicTenantId})({})
    if (item2 != null && item2.ccode != null && item2.ccode != '') {
      formItems.value.ccode = pad(add(item2.ccode, 1),2)
    } else {
      formItems.value.ccode = '01'
    }
  }
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
<style>
.vben-basic-title {
  color: #0096c7 !important;
  border:none !important;
}

.ant-modal-body {
  padding: 0px;
  border-bottom: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
  border-top: none !important;
}
.ant-modal-header{
  border: none !important;
}
</style>
<style lang="less" scoped>
:deep(.ant-calendar-picker-input.ant-input),
:deep(.ant-select-single:not(.ant-select-customize-input)
.ant-select-selector
.ant-select-selection-search-input) {
  border: none;
  width: 100%;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
}

:deep(.ant-select-single:not(.ant-select-customize-input)
.ant-select-selector
.ant-select-selection-search-input) {
  border: none !important;
}

:deep(.vben-basic-title) {
  color: #0096c7 !important;
}
.vben-basic-title {
  color: #0096c7 !important;
  border:none !important;
}

.ant-modal-body {
  padding: 0px;
  border-bottom: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
  border-top: none !important;
}
.ant-modal-header{
  border: none !important;
}
:deep(.vben-basic-title) {
  color: #0096c7 !important;
}

.nc-open-content {
  input {
    width: 50%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
  }

  .abc{
    border-bottom: 2px solid #666666 !important;
    font-size: 18px !important;
    :deep(.ant-select-selector) {
      font-size: 18px !important;
    }
  }

  label {
    text-align: left;
    width: 120px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    color: #535353;
    font-size: 13px;
    margin-left: 2em;
    font-weight: bold;
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
