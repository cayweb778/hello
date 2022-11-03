<template >
<!--  <BasicModal
    width="1100px"
    v-bind="$attrs"
    title="资产负债表模板"
    @ok="handleOk()"
    @register="register"
  >-->

  <div v-show="showEdit" style="position: absolute;left:0;top:0;z-index:100;width: 100%;height:100%;background:#ffffff;overflow:auto;">
    <h1 style="padding:10px;height: 40px;background:#ffffff;text-align: center;font-size: 24px; ">
      现金流量表
    </h1>
    <div style="padding:10px;height:750px;overflow: auto !important;">
      <div class="nc-open-content">
        <div class="open-content-up">

          <label>模板名称:</label>
          <a-input v-model:value="itemConfig.base.templateName" placeholder="模板名称" style="width: 20%" />
          <div style="text-align: center;">
          <label>标题名称:</label>
          <a-input v-model:value="itemConfig.base.titleName" placeholder="标题名称" style="width: 20%" />
          </div>

        </div>

        <div align="center" style="margin-left: 5%;margin-right: 5%;">
          <a-input v-model:value="itemConfig.base.menu1" placeholder="" style="width: 20%;float: left;" />
          <a-input v-model:value="itemConfig.base.menu2" placeholder="" style="width: 20%;margin: 0 auto;" />
          <a-input v-model:value="itemConfig.base.menu3" placeholder="" style="width: 20%;float: right;" />
        <div style="width: 100%;">
          <div v-if="itemConfig.base.sysFlag!='1'" style="float: left;margin-top: 10px;">
            <a-button @click="openAddPage()" style="background-color: rgb(1, 129, 226);color: #fff">
              增行
            </a-button>&nbsp;&nbsp;
            <a-button @click="delRow()" style="background-color: rgb(1, 129, 226);color: #fff">
              删行
            </a-button>&nbsp;&nbsp;
            <a-button @click="inRow()" style="background-color: rgb(1, 129, 226);color: #fff">
              插入
            </a-button>&nbsp;&nbsp;
            <a-button @click="editRow()" style="background-color: rgb(1, 129, 226);color: #fff">
              修改
            </a-button>&nbsp;&nbsp;
          </div>
            <a-checkbox-group @change="onChange" v-if="isReload">
          <table class="layui-table" id="tableId" style="height: calc( 100% - 20px );margin-top: 10px;background-color:#f2f4f6;">
            <thead>
            <tr class="dy_table_thead_tr even">
              <th style="width: 50px">选择</th>
              <th style="width: 50px">序号</th>
              <th>项目名称</th>
              <th style="width: 50px">行次</th>
              <th style="width: 100px">显示级次</th>
              <th style="width: 100px">计算方式</th>
            </tr>
            </thead>
            <tbody id="tbody" style="height: calc( 100% - 100px );">
            <tr class="odd" v-for="(row,rowIndex) in itemConfig.base.table">
              <td style="width: 50px">
                <a-checkbox :value="row" style="width: auto" />
              </td>
              <td style="width: 50px">{{ row.xuhao=rowIndex+1 }}</td>
              <td @click="openEditPage(row)" style="text-align: left;padding-left:1em;">
                <a v-if="row.jici=='1' || row.jici==''">{{ row.columnShowName }}</a>
                <a v-if="row.jici=='2'" style="padding-left:2em;">{{ row.columnShowName }}</a>
                <a v-if="row.jici=='3'" style="padding-left:4em;">{{ row.columnShowName }}</a>
                <a v-if="row.jici=='4'" style="padding-left:6em;">{{ row.columnShowName }}</a>
              </td>
              <td style="width: 50px">{{ row.hangci }}</td>
              <td style="width: 100px">{{ row.jici }}</td>
              <td style="width: 100px">
                <span v-if="row.formulaMethod!=''">{{ row.formulaMethod=='1'?'行次':'项目' }}</span>
              </td>
            </tr>
            </tbody>
          </table>
          </a-checkbox-group>
        </div>

      </div>

      <div style="height: 30px;margin-top: 10px;float: left; width: 90%;margin-left: 5%;margin-right: 5%;">
        <a-input v-model:value="itemConfig.base.menu4" placeholder="" style="width: 20%;float: left;" />
        <a-input v-model:value="itemConfig.base.menu5" placeholder="" style="width: 20%;float: right;" />
      </div>

      <SelectKemu @register="registerSelectKemuPage" @save="addData" />

      <div style="margin-top: 20px;width: 90%; float: left;margin-left: 5%;margin-right: 5%;">
        <p>1、现金流量表只取现金流量项目余额或者行次加减值；若项目设置某项目01，则取两项目余额，设置1+2+3，则等于三个行次的值相加；</p>
        <p>2、序号为自增值，若某行公式值为空，则该行只做显示使用，不做任何数据处理，建议不设置行次值；</p>
        <p>3、允许增行、删行或修改行，完善五项值；项目名称、显示级次、公式、取值方式、行次；</p>
        <p>4、系统预置参数通过&+参数名称取值。包括：核算单位名称，年月日（系统年月日），date（生成日期），币种，操作员姓名；</p>
        <p>5、添加项目时，只允许现金流量，系统需要较验；</p>
        <p>6、取值方式有五种：余额、借方余额、贷方余额、借方发生、贷方发生；</p>
      </div>

      <div style="float: right;margin-left: 5%;margin-right: 5%;">
        <a-button v-if="itemConfig.base.sysFlag!='1'" @click="handleOk()" style="background-color: rgb(1, 129, 226);color: #fff">提交</a-button>&nbsp;&nbsp;&nbsp;&nbsp;
        <a-button @click="closed()" style="background-color: rgb(1, 129, 226);color: #fff">取消</a-button>
      </div>


      </div>

    </div>
  </div>

<!--  </BasicModal>-->
</template>
<script setup="props, {content}" lang="ts">
import {getCurrentInstance, nextTick, ref, unref} from 'vue'
import {BasicModal, useModal, useModalInner} from '/@/components/Modal'
import {
  PlusOutlined,
  MinusOutlined
} from '@ant-design/icons-vue'
import SelectKemu from './select-kemu.vue'
import columnItemConfigModelHelper
  from "./helper/columnItemConfigModelHelper";
import {deleteColumn, deleteFormulaByTemplate, findByColumn, findByFormula} from "/@/api/record/system/report-template";
import {useMessage} from "/@/hooks/web/useMessage";
import {Select as ASelect,Input as AInput,Checkbox as ACheckbox} from "ant-design-vue"
const ASelectOption=ASelect.Option
const ACheckboxGroup = ACheckbox.Group

const {
  createErrorModal
} = useMessage()
const isReload=ref(true)
const emit=defineEmits(['register','save','close'])
function initValue(){
  return {
    open(data){
      showEdit.value=true
      showValue(data)
    },
    close(){
      showEdit.value=false
    },
    instance:getCurrentInstance()
  }
}
const table:any = ref([])
//赋值方法
function showValue(data){

  itemConfig.value.base.id = data.data.id
  itemConfig.value.base.reportName = data.data.reportName
  itemConfig.value.base.templateName = data.data.templateName
  // itemConfig.value.base.accStandard = data.data.accStandard
  itemConfig.value.base.kemuTemplateId = data.data.kemuTemplateId
  itemConfig.value.base.titleName = data.data.titleName
  itemConfig.value.base.flag = data.data.flag
  itemConfig.value.base.menu1 = data.data.menu1
  itemConfig.value.base.menu2 = data.data.menu2
  itemConfig.value.base.menu3 = data.data.menu3
  itemConfig.value.base.menu4 = data.data.menu4
  itemConfig.value.base.menu5 = data.data.menu5
  itemConfig.value.base.sysFlag = data.data.sysFlag

  isReload.value = false
  nextTick(() => isReload.value = true)
  itemConfig.value.base.table = []
  if (data.data.table==null) {
    if (itemConfig.value.base.id != '' && itemConfig.value.base.id != undefined) {
      findByColumn(itemConfig.value.base.id).then(res => {
        res.forEach(function (column, index) {
          itemConfig.value.base.table.push(column)
          findByFormula(column.id).then(res1 => {
            itemConfig.value.base.table[index].formulaTable = res1
          })
        })
      })
    }
  } else {
    itemConfig.value.base.table = data.data.table
  }
}
//注册传值事件
emit('register',initValue())
//关闭编辑页
function closed(){
  showEdit.value=false
  emit('close')
}
//获取使用行数据
const hang:any = ref([])
function getHang(){
  hang.value=[]
  table.value=[]
  itemConfig.value.base.table.forEach(
    function (item){
      if(item.hangci!='') {
        hang.value.push(item.hangci)
        table.value.push(item)
      }
    }
  )
}
const [registerSelectKemuPage, { openModal: openSelectKemuPage }] = useModal()
//添加栏目
const openAddPage = () => {
  getHang()
  openSelectKemuPage(true, {
    data: {
      id: '',
      templateId:'',
      xuhao: '',
      columnShowName: '',
      jici: '1',
      hangci: '',
      columnType: '',
      formulaMethod: '',
      valueMethod: '',
      formulaCount: '',
      formulaTable: []
    },
    table: table.value,
    hang: hang.value
  })
}
//修改
const openEditPage = (data:any) => {
  getHang()
  openSelectKemuPage(true, {
    data,
    accStandard: itemConfig.value.base.accStandard,
    kemuTemplateId: itemConfig.value.base.kemuTemplateId,
    table: table.value,
    hang: hang.value
  })
}
//添加栏目方法
function addData(data:any){
  isReload.value = false
  nextTick(() => isReload.value = true)
  if (data.xuhao=='') {
    itemConfig.value.base.table.push(JSON.parse(JSON.stringify(data)))
  } else {
    let num = parseInt(data.xuhao) - 1
    //判断行次是否改变
    if (itemConfig.value.base.table[num].hangci == data.hangci) {
      itemConfig.value.base.table[num] = JSON.parse(JSON.stringify(data))
    } else {
      itemConfig.value.base.table.forEach(
        function (item,index) {
          if (item.formulaMethod=='1') {
            item.formulaTable.forEach(
              function (formula,index1) {
                //改变对应的行次公式
                if (formula.ccode == itemConfig.value.base.table[num].hangci) {
                  formula.ccode = data.hangci
                }
                item.formulaTable[index1] = formula
              }
            )
            itemConfig.value.base.table[index] = item
          }
        }
      )
      itemConfig.value.base.table[num] = JSON.parse(JSON.stringify(data))
    }
  }
}

const {createBaseRow, createColumnItemConfig} = columnItemConfigModelHelper()
const itemConfig:any = ref(createColumnItemConfig())

//删除行
const delRows = ref([])
function delRow() {
  if (checked.value.length>0) {
    isReload.value = false
    nextTick(() => isReload.value = true)
    for (let i = checked.value.length; i > 0; i--) {
      let delNum = parseInt(checked.value[i - 1].xuhao) - 1
      //判断行次是否为空
      if (checked.value[i - 1].hangci != '') {
        itemConfig.value.base.table.forEach(
          function (item, index) {
            if (item.formulaMethod == '1') {
              item.formulaTable.forEach(
                function (formula, index1) {
                  //删除对应行次公式
                  if (formula.ccode == checked.value[i - 1].hangci) {
                    item.formulaTable.splice(index1, 1)
                  }
                }
              )
              itemConfig.value.base.table[index] = item
            }
          }
        )
      }

      itemConfig.value.base.table.splice(delNum, 1);
      if (checked.value[i - 1].id != '') {
        delRows.value.push(checked.value[i - 1])
        // deleteColumn(zcCheck.value[i-1])
      }
    }
    checked.value = []
    return true
  }
  createErrorModal({
    iconType: 'warning',
    title: '删行',
    content: '请选择需要删除的内容'
  })
  // alert('请选择需要删除的内容')
  return false
}
//点击修改按钮
function editRow() {
  if (checked.value.length==1) {
    openEditPage(checked.value[0])
    return true
  }
  createErrorModal({
    iconType: 'warning',
    title: '修改',
    content: '请选择一行进行修改'
  })
  return false
}
//点击插入按钮
function inRow() {
  if (checked.value.length==1) {
    isReload.value = false
    nextTick(() => isReload.value = true)
    let num = parseInt(checked.value[0].xuhao)-1
    itemConfig.value.base.table.splice(num,0,createBaseRow());
    return true
  }
  createErrorModal({
    iconType: 'warning',
    title: '插入',
    content: '请选择插入行位置'
  })
  return false
}

const showEdit:any = ref(false)
const checked:any = ref([])
//获取选中栏目
const onChange = (checkedValues:any) => {
  checked.value=checkedValues
  // console.log('checked = ', checkedValues);
}
//保存模板方法
async function handleOk() {
  itemConfig.value.base.reportName='xjllb'
  itemConfig.value.base.flag='1'
  if (delRows.value.length>0){
    delRows.value.forEach(
      function (item){
        deleteColumn(item)
      }
    )
  }
  if (itemConfig.value.base.id!=''){
    deleteFormulaByTemplate(itemConfig.value.base)
  }
  emit('save', unref(JSON.parse(JSON.stringify(itemConfig.value.base))))
  closed()
}

</script>
<style lang="less">
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
    width: 20%;
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

<style lang="less" scope>
@import "../../../../../assets/styles/layui.less";
@import "../../../../../assets/styles/theme.less";
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
