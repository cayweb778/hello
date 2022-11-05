<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="公式设置"
    @ok="handleOk()"
    @register="register"
  >
    <div class="nc-open-content" style="min-height: 500px;">
      <div class="open-content-up">

        <label>栏目名称：</label>
        <a-input v-model:value="formItems.base.columnShowName" placeholder="栏目名称" style="width: 30%" />
        <label>显示级次：</label>
        <a-select v-model:value="formItems.base.jici" style="width: 30%">
          <a-select-option value="">请选择</a-select-option>
          <a-select-option value="1">1</a-select-option>
          <a-select-option value="2">2</a-select-option>
          <a-select-option value="3">3</a-select-option>
          <a-select-option value="4">4</a-select-option>
        </a-select>
        <br/>
        <label>行次：</label>
        <a-input type="number" v-model:value="formItems.base.hangci" @blur="checkHangci()" placeholder="行次" style="width: 30%" />
        <label>计算方式：</label>
        <a-select v-model:value="formItems.base.formulaMethod" @change="checkMethod()" style="width: 30%">
          <a-select-option value="">请选择</a-select-option>
          <a-select-option value="1">行次</a-select-option>
          <a-select-option value="2">科目</a-select-option>
        </a-select>
        <br/>
        <label>取值方式：</label>
        <a-select v-model:value="formItems.base.valueMethod" :disabled="!showKemu" style="width: 30%">
          <a-select-option value="">请选择</a-select-option>
          <a-select-option value="1">余额</a-select-option>
          <a-select-option value="2">借方余额</a-select-option>
          <a-select-option value="3">贷方余额</a-select-option>
          <a-select-option value="4">借方发生</a-select-option>
          <a-select-option value="5">贷方发生</a-select-option>
        </a-select>

      </div>

      <div style="margin: 5px 30px;">
        <div>
          <a-button @click="createRow()" style="padding: 0px 4px; height: 25px;background-color: rgb(1,129,226);color: #ffffff">
            <PlusOutlined />{{formItems.base.sysFlag}}
          </a-button>
        </div>

        <table v-show="showHangci" class="layui-table" id="tableId1" style="height: calc( 100% - 20px );margin-top: 20px;background-color:#f2f4f6;">
          <thead>
          <tr class="dy_table_thead_tr even">
            <th>行次-行次名称</th>
            <th style="width: 100px;">运算符号</th>
            <th style="width: 100px;">操作</th>
          </tr>
          </thead>
          <tbody id="tbody1" style="height: calc( 100% - 100px );">
          <tr class="odd" v-for="(row,rowIndex) in formItems.base.formulaTable">
            <td style="text-align: left">
              <a-select v-model:value="row.ccode" placeholder="行次-行次名称" style="width: 100%">
                <a-select-option v-for="item in table" :key="item.hangci" :value="item.hangci">
                  {{ item.hangci }}--{{ item.columnShowName }}
                </a-select-option>
              </a-select>
            </td>
            <td style="width: 100px;">
              <a-select v-model:value="row.fuhao" style="width: 100px">
                <a-select-option value="+">+</a-select-option>
                <a-select-option value="-">-</a-select-option>
              </a-select>
            </td>
            <td style="width: 100px;">
              <a @click="delRow(row,rowIndex)"><DeleteOutlined /> 删除</a>
            </td>
          </tr>
          </tbody>
        </table>

        <table v-show="showKemu" class="layui-table" id="tableId" style="height: calc( 100% - 20px );margin-top: 20px;background-color:#f2f4f6;">
          <thead>
          <tr class="dy_table_thead_tr even">
            <th>科目编码-科目名称</th>
            <th style="width: 100px;">运算符号</th>
            <th style="width: 100px;">方向</th>
            <th style="width: 100px;">操作</th>
          </tr>
          </thead>
          <tbody id="tbody" style="height: calc( 100% - 100px );">
          <tr class="odd" v-for="(row,rowIndex) in formItems.base.formulaTable">
            <td style="text-align: left">
              <a-select v-model:value="row.ccode" @change="chengeKemu(row);handleChange" show-search :filter-option="false" @search="handleSearch" placeholder="会计科目" style="width: 100%;">
                <a-select-option v-for="kemu in kemuCodeList" :key="kemu.key" :value="kemu.key">
                  {{ kemu.ccode }}--{{ kemu.ccodeName }}
                </a-select-option>
              </a-select>
            </td>
            <td style="width: 100px;">
              <a-select v-model:value="row.fuhao" style="width: 100px">
                <a-select-option value="+">+</a-select-option>
                <a-select-option value="-">-</a-select-option>
              </a-select>
            </td>
            <td style="width: 100px;">
              <span v-if="row.fangxiang!=''">{{ row.fangxiang=='1'?'借':'贷' }}</span>
            </td>
            <td style="width: 100px;">
              <a @click="delRow(row,rowIndex)"><DeleteOutlined /> 删除</a>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

    </div>

  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import { ref, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {
  PlusOutlined,
  DeleteOutlined
} from '@ant-design/icons-vue'
import formulaItemConfigModelHelper
  from "./helper/formulaItemConfigModelHelper";
import {group_findByTemplateIdCode} from "/@/api/acctemplate/acctemplate";
import {Select as ASelect,Input as AInput} from "ant-design-vue"
import {useMessage} from "/@/hooks/web/useMessage";
const ASelectOption=ASelect.Option

const {
  createErrorModal
} = useMessage()

const emit=defineEmits(['register','save'])

const {createBaseRow, createFormulaItemConfig} = formulaItemConfigModelHelper()
const formItems:any = ref(createFormulaItemConfig())

//添加行
function createRow() {
  if (formItems.value.base.formulaMethod!='') {
    formItems.value.base.formulaTable.push(createBaseRow())
  }
}
//删除行
function delRow(row:any,rowIndex:any) {
  formItems.value.base.formulaTable.splice(rowIndex, 1);
  // if (row.id!=''){
  //   deleteFormula(row)
  // }
}

let changeBeforeModel:any = {}

const kemuList:any = ref([])
const table:any = ref([])
const hang:any = ref([])

const [register, { closeModal }] = useModalInner( (data) => {
  // 方式2
  formItems.value.base.id = data.data.id
  formItems.value.base.templateId = data.data.templateId
  formItems.value.base.xuhao = data.data.xuhao
  formItems.value.base.columnShowName = data.data.columnShowName
  formItems.value.base.jici = data.data.jici
  formItems.value.base.hangci = data.data.hangci
  formItems.value.base.columnType = data.data.columnType
  formItems.value.base.formulaMethod = data.data.formulaMethod
  formItems.value.base.valueMethod = data.data.valueMethod
  formItems.value.base.formulaCount = data.data.formulaCount
  formItems.value.base.sysFlag = data.data.sysFlag
  formItems.value.base.originId = data.data.originId

  table.value = []
  data.table.forEach(
    function (item) {
      if (item.xuhao<data.data.xuhao) {
        table.value.push(item)
      }
    }
  )
  hang.value = data.hang

  formItems.value.base.formulaTable = []
  if (data.data.formulaTable.length>0) {
    formItems.value.base.formulaTable = data.data.formulaTable
  }

  //根据会计准则获取科目信息
  group_findByTemplateIdCode(data.kemuTemplateId)
    .then((res:any)=>{
      kemuList.value = res.map(item => {
        item.key = item.ccode + '%'
        return item
      })
      kemuCodeList.value = kemuList.value
    })

  showHangci.value = false
  showKemu.value = false
  if (formItems.value.base.formulaMethod=='1'){
    showHangci.value = true
    showKemu.value = false
  } else if (formItems.value.base.formulaMethod=='2') {
    showHangci.value = false
    showKemu.value = true
  }

  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value.base))
})
//提交方法
let isChanged:boolean = false
async function handleOk() {
  emit('save', unref(formItems.value.base))
  closeModal()
}

const showHangci = ref(false)
const showKemu = ref(false)
//修改计算方式
function checkMethod(){
  if (formItems.value.base.formulaMethod=='1'){
    formItems.value.base.valueMethod=''
    showHangci.value = true
    showKemu.value = false
  } else if (formItems.value.base.formulaMethod=='2') {
    formItems.value.base.valueMethod='1'
    showHangci.value = false
    showKemu.value = true
  } else {
    formItems.value.base.valueMethod=''
    showHangci.value = false
    showKemu.value = false
  }
}
//给科目方向赋值
function chengeKemu(row){
  kemuList.value.forEach(function (kemu) {
    if (row.ccode==kemu.ccode){
      row.fangxiang=kemu.bprogerty
    }
  })
}
//行次重复验证
function checkHangci(){
  console.log(formItems.value.base.hangci)
  if (formItems.value.base.hangci==changeBeforeModel.hangci){
    return true
  } else {
    hang.value.forEach(
      function (item){
        if (formItems.value.base.hangci==item){
          // alert("行次不允许重复，请重新输入！")
          createErrorModal({
            iconType: 'warning',
            title: '提示',
            content: '行次不允许重复，请重新输入！'
          })
          formItems.value.base.hangci = ''
          return false
        }
      }
    )
  }
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
  console.log(value);
  formItems.value.sourceCode = value;
  kemuCodeList.value = kemuList.value.filter(item => {
    return item.ccode.slice(0, value.length) == value
  })
}

</script>
<style scoped lang="less">
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

  .ant-select-selector {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  label {
    text-align: left;
    width: 150px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    padding-left: 2em;
    color: #535353;
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

<style lang="less" scoped>
@import "../../../../../../assets/styles/layui.less";
@import "../../../../../../assets/styles/theme.less";
</style>
<style scoped type="text/css">

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
#tableId > tbody > tr,
#tableId1 > thead,
#tableId1 > tbody > tr {
  display: table;
  width: 100%;
  height: 30px;
  table-layout: fixed; /**表格列的宽度由表格宽度决定，不由内容决定*/
  text-align: center;
}

#tableId th ,#tableId1 th  {
  border: 1px solid #6f696f;
}
#tableId tr td ,#tableId1 tr td  {
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
