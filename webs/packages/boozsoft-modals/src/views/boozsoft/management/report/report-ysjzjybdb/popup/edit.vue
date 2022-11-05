<template >
<!--  <BasicModal
    width="1100px"
    v-bind="$attrs"
    title="预算结转结余变动表模板"
    @ok="handleOk()"
    @register="register"
  >-->

  <div v-show="showEdit" style="position: absolute;left:0;top:0;z-index:100;width: 100%;height:100%;background:#b4c8e3;overflow:auto;">
    <div class="app-container" style="padding:10px;height: auto">
      <div class="app-container-head">
        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">
        <div class="container-head-title">
          <b class="noneSpan">预算结转结余变动表模板</b>
        </div>

        <div class="ant-btn-group" style="float: right">
          <a-button @click="handleOk()" class="ant-btn ant-btn-me">保存
          </a-button>
          &nbsp;
          <button
            @click="closed()"
            type="button"
            class="ant-btn ant-btn-me"
          ><span>返回</span></button>
        </div>
      </div>
      <div class="nc-open-content">
        <div class="open-content-up">

          <div style="text-align: center;">
            <label>模板名称：</label>
            <a-input v-model:value="itemConfig.base.templateName" :disabled="true" placeholder="模板名称" style="width: 20%" />
            <label>会计准则：</label>
            <a-select v-model:value="itemConfig.base.accStandard" :disabled="true" @change="standardCheck()" placeholder="会计准则" style="width: 20%">
              <a-select-option v-for="accstandard in accstandardList" :key="accstandard.uniqueAccStandard" :value="accstandard.uniqueAccStandard">
                {{ accstandard.accStandardName }}
              </a-select-option>
            </a-select>
            <label>科目模板：</label>
            <a-select v-model:value="itemConfig.base.kemuTemplateId" :disabled="true" placeholder="科目模板" style="width: 20%">
              <a-select-option v-for="item in templateList" :key="item.id" :value="item.id">
                {{ item.tname }}
              </a-select-option>
            </a-select><br/>

            <label>排序号：</label>
            <a-input type="number" v-model:value="itemConfig.base.num" placeholder="排序号" style="width: 20%" />
            <label>模板标识：</label>
            <a-input v-model:value="itemConfig.base.titleName" placeholder="模板标识" @blur="changTitleName()" style="width: 20%" />
            <label>报表编码：</label>
            <a-input v-model:value="itemConfig.base.menu6" placeholder="报表编码" style="width: 20%;" />
            <br/><br/>
          </div>

        </div>
      </div>
    </div>

    <div class="app-container" style="padding:10px;height:630px;overflow: auto !important;">
      <div class="nc-open-content">
        <div style="text-align: center;margin-left: 5%;margin-right: 5%;">
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
                  <th style="width: 80px">显示级次</th>
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
                  <td style="width: 80px">{{ row.jici }}</td>
                  <td style="width: 100px">
                    <span v-if="row.formulaMethod!=''">{{ row.formulaMethod=='1'?'行次':'科目' }}</span>
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
          <p>1、预算结转结余变动表只取科目余额或者行次加减值；若科目设置某科目5001+5002，则取两科目余额相加，若设置5001-5002，则取值两科目余额相减，设置1+2+3，则等于三个行次的值相加；</p>
          <p>2、序号为自增值，若某行公式值为空，则该行只做显示使用，不做任何数据处理，建议不设置行次值；</p>
          <p>3、允许增行、删行或修改行，完善五项值；项目名称、显示级次、公式、取值方式、行次；</p>
          <p>4、系统预置参数通过&+参数名称取值。包括：核算单位名称，年月日（系统年月日），date（生成日期），币种，操作员姓名；</p>
          <p>5、添加项目时，只允许损益类科目，系统需要较验；</p>
          <p>6、取值方式有五种：余额、借方余额、贷方余额、借方发生、贷方发生；</p>
        </div>

        <div style="float: right;margin-left: 5%;margin-right: 5%;">
          <a-button @click="handleOk()" style="background-color: rgb(1, 129, 226);color: #fff">提交</a-button>&nbsp;&nbsp;&nbsp;&nbsp;
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
import {findAllAcctandard} from "/@/api/accstandard/accstandard";
import {
  deleteColumn,
  deleteFormulaByTemplate,
  findByColumn,
  findByFormula, findByTitleName,
  findFormulaByTemplate
} from "/@/api/record/system/group-report-template";
import {findByUniqueAccStandard} from "/@/api/acctemplate/acctemplate";
import {useMessage} from "/@/hooks/web/useMessage";
import {Select as ASelect,Input as AInput,Checkbox as ACheckbox} from "ant-design-vue"
import {getThisIndexImg} from "/@/api/task-api/tast-bus-api";
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
const accstandardList:any = ref({})
const table:any = ref([])

let changeBeforeModel:any = {}
//赋值方法
function showValue(data){

  //获取会计准则列表
  findAllAcctandard().then(res => {
    accstandardList.value = res.items
    accstandardList.value.forEach(
      function (item,index){
        if (data.data.accStandard==item.uniqueAccStandard){
          itemConfig.value.base.accStandard = item.uniqueAccStandard
        } else if (data.data.accStandard==''&& index==0) {
          itemConfig.value.base.accStandard = item.uniqueAccStandard
        }
      }
    )
    standardCheck()
  })

  itemConfig.value.base.id = data.data.id
  itemConfig.value.base.reportName = data.data.reportName
  itemConfig.value.base.templateName = data.data.templateName
  itemConfig.value.base.accStandard = data.data.accStandard
  itemConfig.value.base.kemuTemplateId = data.data.kemuTemplateId
  itemConfig.value.base.titleName = data.data.titleName
  itemConfig.value.base.num = data.data.num
  itemConfig.value.base.flag = data.data.flag
  itemConfig.value.base.menu1 = data.data.menu1
  itemConfig.value.base.menu2 = data.data.menu2
  itemConfig.value.base.menu3 = data.data.menu3
  itemConfig.value.base.menu4 = data.data.menu4
  itemConfig.value.base.menu5 = data.data.menu5
  itemConfig.value.base.menu6 = data.data.menu6
  itemConfig.value.base.sysFlag = data.data.sysFlag

  isReload.value = false
  nextTick(() => isReload.value = true)
  itemConfig.value.base.table = []
  if (data.data.table==null) {
    if (itemConfig.value.base.id != '' && itemConfig.value.base.id != undefined) {
      findByColumn(itemConfig.value.base.id).then(async res => {
        const formulaList: any = await findFormulaByTemplate(itemConfig.value.base.id)
        res.forEach(function (column, index) {
          itemConfig.value.base.table.push(column)
          itemConfig.value.base.table[index].formulaTable = formulaList.filter(item => {
            return item.columnId == column.id
          })
          /*findByFormula(column.id).then(res1 => {
            itemConfig.value.base.table[index].formulaTable = res1
          })*/
        })
        changeBeforeModel = JSON.parse(JSON.stringify(itemConfig.value))
      })
    }
  } else {
    itemConfig.value.base.table = data.data.table
    changeBeforeModel = JSON.parse(JSON.stringify(itemConfig.value))
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
  if(itemConfig.value.base.accStandard == '') {
    // alert("请先选择会计准则！")
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '请先选择会计准则'
    })
  } else {
    getHang()
    openSelectKemuPage(true, {
      data: {
        // id: '',
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
      accStandard: itemConfig.value.base.accStandard,
      kemuTemplateId: itemConfig.value.base.kemuTemplateId,
      table: table.value,
      hang: hang.value
    })
  }
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
const delRows:any = ref([])
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
      if (checked.value[i - 1].id != '' && checked.value[i - 1].id != null) {
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
  itemConfig.value.base.reportName='ysjzjybdb'
  itemConfig.value.base.flag='1'
  if (delRows.value.length>0){
    delRows.value.forEach(
      function (item){
        deleteColumn(item)
      }
    )
  }
  if (itemConfig.value.base.id!=''){
    await deleteFormulaByTemplate(itemConfig.value.base)
  }
  emit('save', unref(JSON.parse(JSON.stringify(itemConfig.value.base))))
  closed()
}

// 会计准则下拉框回调
const templateList:any = ref([])
const standardCheck = async () => {
  const template = await findByUniqueAccStandard(itemConfig.value.base.accStandard)
  templateList.value = template.items
  console.log(template.value)
  let num = 0
  templateList.value.forEach(
    function (item:any,index){
      if (itemConfig.value.base.kemuTemplateId==item.id) {
        itemConfig.value.base.kemuTemplateId = item.id
        num++
      } else if (itemConfig.value.base.kemuTemplateId==''&& index==0) {
        itemConfig.value.base.kemuTemplateId = item.id
      }
    }
  )
  if (num==0) {
    itemConfig.value.base.kemuTemplateId = templateList.value[0].id
  }
}

async function changTitleName() {
  if((changeBeforeModel.base.titleName!=undefined && changeBeforeModel.base.titleName!='') || changeBeforeModel.base.titleName==itemConfig.value.base.titleName){
    return true
  }
  const res = await findByTitleName({reportName: 'ysjzjybdb',titleName:itemConfig.value.base.titleName})
  if(res.length!=0){
    // alert('模板标识已存在，请重新输入！')
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '模板标识已存在，请重新输入！'
    })
    itemConfig.value.base.titleName = ''
    console.log(false)
    return false
  }
  return true
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

<style lang="less" scope>
@import "../../../../../../assets/styles/layui.less";
@import "../../../../../../assets/styles/theme.less";
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
