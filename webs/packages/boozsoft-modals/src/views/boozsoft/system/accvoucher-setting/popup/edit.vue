<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="常用凭证设置"
    @ok="handleOk()"
    @register="register"
    :canFullscreen="false"
  >
    <template #title>
      <div style="display: flex;margin-top: 10px;" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='0'">
          <PlusCircleOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;常用凭证设置
        </span>
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='1'">
          <FormOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;常用凭证设置
        </span>
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='2'">
          <FileSearchOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;常用凭证设置
        </span>
      </div>
    </template>
    <div class="nc-open-content" :class="isState=='2'?'is_show':''" style="margin-bottom: 0 !important;">
      <div class="open-content-up" style="margin-top: 30px;">

<!--        <label>编码:</label>
        <a-input v-model:value="formItems.ccode" placeholder="" style="width:20%;"/>-->

        <label style="width: 100px;font-size: 14px;">常用凭证名称:</label>
        <a-input v-model:value="formItems.remarks" placeholder="" style="width:20%;font-size: 15px;"/>
        <label style="font-size: 14px;">凭证类别:</label>
        <a-select v-model:value="formItems.ctype" placeholder="" style="width:20%;font-size: 15px;">
          <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
          <a-select-option v-for="item in voucherTypeList" :key="item.voucherTypeCode" :value="item.voucherTypeCode">
            {{ item.voucherTypeCode }}
          </a-select-option>
        </a-select>
<!--        <label>附单据数:</label>
        <a-input type="number" v-model:value="formItems.cnum" placeholder=""/>-->
        <label style="font-size: 14px;">所属分类:</label>
        <a-select v-model:value="formItems.classCode" placeholder="" style="width:20%;font-size: 15px;">
          <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
          <a-select-option v-for="item in classList" :key="item.classCode" :value="item.classCode">
            {{ item.className }}
          </a-select-option>
        </a-select>
      </div>

      <div class="open-content-down" style="margin: 10px 10px 0;">
        <div v-if="isState!='2'">
          <a-button @click="createRow()" style="padding: 0px 4px; height: 25px;background-color: rgb(1,129,226);color: #ffffff">
            <PlusOutlined/>
          </a-button>
        </div>
        <div style="height:300px;overflow: auto;">
        <table class="layui-table" id="tableId" style="margin-top: 10px;background-color:#f2f4f6;">
          <thead>
          <tr class="dy_table_thead_tr even">
            <th style="width: 5%;">序号</th>
            <th style="width: 30%;">摘要</th>
            <th style="width: 25%;">科目</th>
            <th style="width: 15%;">借方</th>
            <th style="width: 15%;">贷方</th>
            <th v-if="isState!='2'" style="width: 10%;">操作</th>
          </tr>
          </thead>
          <tbody>
          <tr class="odd" v-for="(row,rowIndex) in formItems.table">
            <td style="width: 5%;">{{ row.num=rowIndex+1 }}</td>
            <td style="width: 30%;">
              <a-input v-model:value="row.cdigest" style="width: 100%;"/>
            </td>
            <td style="width: 25%;">
              <a-select v-model:value="row.ccode" style="width: 100%;text-align: left;" show-search :filter-option="false" @search="handleSearch" @change="handleChange">
                <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
                <a-select-option v-for="kemu in kemuCodeList" :key="kemu.ccode" :value="kemu.ccode">
                  {{ kemu.ccode }}--{{ kemu.ccodeName }}
                </a-select-option>
              </a-select>
            </td>
            <td style="width: 15%;">
              <a-input-number v-model:value="row.md" @change="row.mc=''" class="right" :precision="2" style="width: 100%;"/>
            </td>
            <td style="width: 15%;">
              <a-input-number v-model:value="row.mc" @change="row.md=''" class="right" :precision="2" style="width: 100%;"/>
            </td>
            <td v-if="isState!='2'" style="width: 10%;">
              <a @click="delRow(row,rowIndex)"><DeleteOutlined/>删除</a>
            </td>
          </tr>
          </tbody>
        </table>
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
import {ref, unref} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {Select as ASelect, Input as AInput,InputNumber as AInputNumber} from "ant-design-vue"
import {
  findAllApi as findClassAllApi
} from '/@/api/boozsoft/account/AccvoucherSettingClass'
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {
  deleteEntryById,
  findAllApi,
  findAllByParentIdOrderByNum,
  findBukongCcode,
  findMaxCcode
} from "/@/api/boozsoft/account/AccvoucherSetting";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  PlusOutlined,
  DeleteOutlined,
  PlusCircleOutlined,
  FormOutlined,
  FileSearchOutlined,
  CaretDownOutlined
} from '@ant-design/icons-vue'
//import {mul} from "/@/views/boozsoft/gdzc/gu-ding-zi-chan-add/calculation";
const mul=null
import {findAllByBendAndIyearOrderByCcode} from "/@/api/subjectInitialBalance/subjectInitialBalance";
import {findVoucherTypeAll} from "/@/api/record/system/voucher-type";

const ASelectOption = ASelect.Option

const emit = defineEmits(['register', 'save'])
const props = defineProps(['modelValue'])

const {
  createErrorModal
} = useMessage()

const formItems: any = ref({})

let changeBeforeModel: any = {}

const classList: any = ref([])
const kemuList: any = ref([])
const voucherTypeList: any = ref([])

const dynamicTenantId = ref('')
const isState = ref('0')
const saveClick:any = ref(false)
const [register, {closeModal}] = useModalInner((data) => {
  saveClick.value=false
  dynamicTenantId.value = data.dynamicTenantId
  isState.value = data.isState
  //获取分类
  useRouteApi(findClassAllApi, {schemaName: dynamicTenantId})({}).then(res => {
    classList.value = res
  })
  //获取末级科目
  useRouteApi(findAllByBendAndIyearOrderByCcode,{schemaName: dynamicTenantId})({bend:'1', iyear:data.year}).then(res=>{
    kemuList.value = res
    kemuCodeList.value = kemuList.value
  })
  //获取凭证类别
  useRouteApi(findVoucherTypeAll, {schemaName: dynamicTenantId})({}).then(res => {
    voucherTypeList.value = res.items
    if ((data.data.ctype==null|| data.data.ctype=='') && voucherTypeList.value.length>0){
      formItems.value.ctype = voucherTypeList.value[0].voucherTypeCode
    }
  })
  // 方式2
  formItems.value = {
    id: data.data.id,
    ccode: data.data.ccode,
    remarks: data.data.remarks,
    ctype: data.data.ctype,
    cnum: data.data.cnum,
    classCode: data.data.classCode,
    table: []
  }

  if (data.isState=='0') {
    maxCode()
    createRow()
    createRow()
  } else {
    useRouteApi(findAllByParentIdOrderByNum,{schemaName: dynamicTenantId})(data.data.id).then(item=>{
      formItems.value.table=item
    })
  }
  ids.value = []

  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})

//添加行
function createRow() {
  const item = {
    uniqueCode: '',
    manageCode: '',
    cardUnique: '',
    deptUnique: '',
    proportion: '',
    cdate: '',
    cuserId: ''
  }
  formItems.value.table.push(item)
  // console.log(formItems.value.table)
}

//删除行
const ids:any = ref([])
function delRow(row:any,rowIndex:any) {
  formItems.value.table.splice(rowIndex, 1);
  if (row.id!=null && row.id!=''){
    ids.value.push(row.id)
    console.log(ids.value)
  }
}

let isChanged: boolean = false

async function handleOk() {
  saveClick.value=true
  if (formItems.value.ccode == null || formItems.value.ccode == '') {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '编码不能为空！'
    })
    saveClick.value=false
    return false
  }
  if (formItems.value.remarks == null || formItems.value.remarks == '') {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '常用凭证名称不能为空！'
    })
    saveClick.value=false
    return false
  }
  if (formItems.value.table.length<2){
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '常用凭证内容至少为2行！'
    })
    saveClick.value=false
    return false
  }
  for (let i = 0; i < formItems.value.table.length; i++) {
    const item = formItems.value.table[i]
    if (item.cdigest==null || item.cdigest==''){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '第'+(i+1)+'行摘要为空，不能进行保存！'
      })
      saveClick.value=false
      return false
    }
    if (item.ccode==null || item.ccode==''){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '第'+(i+1)+'行科目为空，不能进行保存！'
      })
      saveClick.value=false
      return false
    }
    /*if ((item.md==null || item.md=='') && (item.mc==null || item.mc=='')){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '第'+(i+1)+'行借贷方同时为空，不能进行保存！'
      })
      saveClick.value=false
      return false
    }*/
    if ((item.md!=null && item.md!='') && (item.mc!=null && item.mc!='')){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '第'+(i+1)+'行借贷方同时为有值，不能进行保存！'
      })
      saveClick.value=false
      return false
    }
  }
  const res = await useRouteApi(findAllApi, {schemaName: dynamicTenantId})({})
  const accList = res.items
  for (let i = 0; i < accList.length; i++) {
    const item: any = accList[i];
    if (formItems.value.ccode != changeBeforeModel.ccode && item.ccode == formItems.value.ccode) {
      createErrorModal({
        iconType: 'warning',
        title: '删除',
        content: '编码重复不允许添加！'
      })
      formItems.value.ccode = ''
      saveClick.value=false
      return false
    }
    if (formItems.value.remarks != changeBeforeModel.remarks && item.remarks == formItems.value.remarks) {
      createErrorModal({
        iconType: 'warning',
        title: '删除',
        content: '常用凭证名称重复不允许添加！'
      })
      formItems.value.content = ''
      saveClick.value=false
      return false
    }
  }
  if (formItems.value.classCode == '' || formItems.value.classCode == null) {
    formItems.value.classCode = '9999'
  }
  isChanged = !(formItems.value.ccode == changeBeforeModel.ccode
    && formItems.value.remarks == changeBeforeModel.remarks
    && formItems.value.ctype == changeBeforeModel.remarks
    && formItems.value.cnum == changeBeforeModel.ctype
    && formItems.value.classCode == changeBeforeModel.classCode)
  if (isChanged) {
    if (ids.value.length>0){
      for (let i=0; i<ids.value.length; i++) {
        await useRouteApi(deleteEntryById, {schemaName: dynamicTenantId})(ids.value[i])
      }
    }
    emit('save', unref(formItems))
    closeModal()
    saveClick.value=false
    return true
  }
  closeModal()
  // alert("没有改变过")
  saveClick.value=false
  return false
}

async function handleOkAdd() {
  saveClick.value=true
  if (formItems.value.ccode == null || formItems.value.ccode == '') {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '编码不能为空！'
    })
    saveClick.value=false
    return false
  }
  if (formItems.value.remarks == null || formItems.value.remarks == '') {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '常用凭证名称不能为空！'
    })
    saveClick.value=false
    return false
  }
  if (formItems.value.table.length<2){
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '常用凭证内容至少为2行！'
    })
    saveClick.value=false
    return false
  }
  for (let i = 0; i < formItems.value.table.length; i++) {
    const item = formItems.value.table[i]
    if (item.cdigest==null || item.cdigest==''){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '第'+(i+1)+'行摘要为空，不能进行保存！'
      })
      saveClick.value=false
      return false
    }
    if (item.ccode==null || item.ccode==''){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '第'+(i+1)+'行科目为空，不能进行保存！'
      })
      saveClick.value=false
      return false
    }
    /*if ((item.md==null || item.md=='') && (item.mc==null || item.mc=='')){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '第'+(i+1)+'行借贷方同时为空，不能进行保存！'
      })
      saveClick.value=false
      return false
    }*/
    if ((item.md!=null && item.md!='') && (item.mc!=null && item.mc!='')){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '第'+(i+1)+'行借贷方同时为有值，不能进行保存！'
      })
      saveClick.value=false
      return false
    }
  }
  const res = await useRouteApi(findAllApi, {schemaName: dynamicTenantId})({})
  const accList = res.items
  for (let i = 0; i < accList.length; i++) {
    const item: any = accList[i];
    if (formItems.value.ccode != changeBeforeModel.ccode && item.ccode == formItems.value.ccode) {
      createErrorModal({
        iconType: 'warning',
        title: '删除',
        content: '编码重复不允许添加！'
      })
      formItems.value.ccode = ''
      saveClick.value=false
      return false
    }
    if (formItems.value.remarks != changeBeforeModel.remarks && item.remarks == formItems.value.remarks) {
      createErrorModal({
        iconType: 'warning',
        title: '删除',
        content: '常用凭证名称重复不允许添加！'
      })
      formItems.value.content = ''
      saveClick.value=false
      return false
    }
  }
  if (formItems.value.classCode == '' || formItems.value.classCode == null) {
    formItems.value.classCode = '9999'
  }
  isChanged = !(formItems.value.ccode == changeBeforeModel.ccode
    && formItems.value.remarks == changeBeforeModel.remarks
    && formItems.value.ctype == changeBeforeModel.remarks
    && formItems.value.cnum == changeBeforeModel.ctype
    && formItems.value.classCode == changeBeforeModel.classCode)
  if (isChanged) {
    if (ids.value.length>0){
      for (let i=0; i<ids.value.length; i++) {
        await useRouteApi(deleteEntryById, {schemaName: dynamicTenantId})(ids.value[i])
      }
    }
    emit('save', unref(formItems))
    // closeModal()
    formItems.value = {}
    formItems.value.table=[]
    ids.value = []
    await maxCode()
    saveClick.value=false
    return true
  }
  // closeModal()
  // alert("没有改变过")
  saveClick.value=false
  return false
}

async function maxCode(){
  const item1 = await useRouteApi(findBukongCcode,{schemaName: dynamicTenantId})({})
  if (item1!=null && item1.ccode!=null && item1.ccode!=''){
    formItems.value.ccode = item1.ccode
  } else {
    const item2 = await useRouteApi(findMaxCcode, {schemaName: dynamicTenantId})({})
    if (item2 != null && item2.ccode != null && item2.ccode != '') {
      formItems.value.ccode = add(item2.ccode, 1)
    } else {
      formItems.value.ccode = '1'
    }
  }
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

//搜索科目
const kemuCodeList = ref([])

function handleSearch(value) {
  kemuCodeList.value = kemuList.value.filter(item => {
    return item.ccode.slice(0, value.length) == value || item.ccodeName.indexOf(value) != -1
  })
  // debugger
  // console.log(sourceList.value)
}

function handleChange(value) {
  // console.log(value);
  formItems.value.sourceCode = value;
  kemuCodeList.value = kemuList.value.filter(item => {
    return item.ccode.slice(0, value.length) == value
  })
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
:deep(.vben-basic-title) {
  color: #0096c7 !important;
}
.ant-modal-header{
  border: none !important;
}

:deep(.ant-calendar-picker-input.ant-input), :deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector .ant-select-selection-search-input) {
  border: none !important;
  //border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;
  font-weight: bold;
}
:deep(.ant-select-single:not(.ant-select-customize-input)
.ant-select-selector
.ant-select-selection-search-input) {
  border: none !important;
}

.vben-basic-title {
  color: #0096c7 !important;
  border: none !important;
}

.ant-modal-body {
  padding: 0px;
  border-bottom: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
  border-top: none !important;
}

.red_span {
  color: red;
  font-weight: bold;
  display: inline-block;
  width: 20px;
  text-align: right;
}

.is_show{
  pointer-events: none;
  cursor: default;
}

:deep(.ant-input-number){
  border: none;
  width: 100%;

  .ant-input-number-handler-wrap{
    display:none !important;
  }
  input{
    width: 100%;
    font-size: 14px;
    font-weight: bold;
  }
}
:deep(.right) input{
  text-align:right;
}

.nc-open-content {
  input {
    width: 30%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
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

  label {
    text-align: left;
    width: 80px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    color: #535353;
    margin-left: 1em;
    font-size: 13px;
    font-weight: bold;
  }

  .open-content-down {
    margin-top: 0;
    margin-bottom: 0;
    input{
      width: 100%;
      font-size: 14px;
      font-weight: bold;
    }
  }
}
</style>
<style lang="less" scoped>
@import "../../../../../assets/styles/layui.less";
@import "../../../../../assets/styles/theme.less";
@import "../../../../../assets/styles/global-menu-index.less";
</style>
<style type="text/css">

.even {
  background: #FCFCFC;
}

.odd {
  background: #FFFFFF;
}

td:hover {
  background: none;
}

td .bjgs {
  display: none;
}

td:hover .bjgs {
  display: block;
  position: absolute;
  top: 8px;
  left: 130px;
  /*background-color: whitesmoke;*/
  color: royalblue;
}
table{

  table-layout:fixed;/* 只有定义了表格的布局算法为fixed，下面td的定义才能起作用。 */
}
th,td{
  width:100%;
  word-break:keep-all;/* 不换行 */
  white-space:nowrap;/* 不换行 */
  overflow:hidden;/* 内容超出宽度时隐藏超出部分的内容 */
  text-overflow:ellipsis;/* 当对象内文本溢出时显示省略标记(...) ；需与overflow:hidden;一起使用。*/
}
#tableId > thead,
#tableId > tbody > tr {
  display: table;
  width: 100%;
  height: 30px;
  table-layout: fixed; /**表格列的宽度由表格宽度决定，不由内容决定*/
  text-align: center;
}

#tableId th {
  border: 1px solid #6f696f;
}
#tableId tr td  {
  border: 1px solid #6f696f;
  border-top: hidden;
}

a{
  cursor: pointer;
}
/*td缩进*/
.indent{
  text-indent:30px;
}
</style>
