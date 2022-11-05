<template>
  <!--  <BasicModal
      width="1100px"
      v-bind="$attrs"
      title="资产负债表模板"
      @ok="handleOk()"
      @register="register"
    >-->

  <div v-show="showEdit" style="position: absolute;left:0;top:0;z-index:100;width: 100%;height:100%;background:#b4c8e3;overflow:auto;">
    <div class="app-container" style="padding:10px;height: auto">
      <!--      <span v-if="itemConfig.base.reportName=='lrb'">利润表</span>
            <span v-if="itemConfig.base.reportName=='xjllb'">现金流量表</span>-->
      <div class="app-container-head">
<!--        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">-->
        <div class="container-head-title" style="float: left;margin-top: 10px;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div v-if="itemConfig.base.reportName=='lrb'" class="container-head-title" style="padding-left: 40%;text-align: center;margin-top: 30px;">
          <b class="noneSpan" style="font-size: 26px;">利润表</b>
        </div>
        <div v-if="itemConfig.base.reportName=='xjllb'" class="container-head-title" style="padding-left: 40%;text-align: center;margin-top: 30px;">
          <b class="noneSpan" style="font-size: 26px;">现金流量表</b>
        </div>
        <div v-if="itemConfig.base.reportName=='srzcb'" class="container-head-title" style="padding-left: 40%;text-align: center;margin-top: 30px;">
          <b class="noneSpan" style="font-size: 26px;">收入支出表</b>
        </div>

        <div class="ant-btn-group" style="float: right">
          <a-button @click="handleOk()" class="ant-btn ant-btn-me">保存
          </a-button>
          <button
            @click="closed()"
            type="button"
            class="ant-btn ant-btn-me"
          ><span>返回</span></button>
        </div>
      </div>
      <div style="clear: none"/>
      <div style="margin-top: -30px;margin-left: 80px;">
        <div style="display: inline-block;float: left;margin-top: -25px;">
          <AccountPicker theme="three" readonly />
        </div>
        <div style="float: right; margin-left: 10px">
          <a-button class="ant-btn-me" @click="reloadTable()">
            <SyncOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
          <a-popover class="ant-btn-default" placement="leftTop">
            <a-button>
              <QuestionOutlined :style="{ fontSize: '14px' }"/>
            </a-button>
            <template #title>帮助说明</template>
            <template #content>
              <div v-if="itemConfig.base.reportName=='lrb'" style="width: 600px;">
                <p>1、利润表只取科目余额或者行次加减值；若科目设置某科目5001+5002，则取两科目余额相加，若设置5001-5002，则取值两科目余额相减，设置1+2+3，则等于三个行次的值相加；</p>
                <p>2、序号为自增值，若某行公式值为空，则该行只做显示使用，不做任何数据处理，建议不设置行次值；</p>
                <p>3、允许增行、删行或修改行，完善五项值；项目名称、显示级次、公式、取值方式、行次；</p>
                <p>4、系统预置参数通过&+参数名称取值。包括：核算单位名称，年月日（系统年月日），date（生成日期），币种，操作员姓名；</p>
                <p>5、添加项目时，只允许损益类科目，系统需要较验；</p>
                <p>6、取值方式有五种：余额、借方余额、贷方余额、借方发生、贷方发生；</p>
              </div>
              <div v-if="itemConfig.base.reportName=='xjllb'" style="width: 600px;">
                <p>1、现金流量表只取现金流量项目余额或者行次加减值；若项目设置某项目01，则取两项目余额，设置1+2+3，则等于三个行次的值相加；</p>
                <p>2、序号为自增值，若某行公式值为空，则该行只做显示使用，不做任何数据处理，建议不设置行次值；</p>
                <p>3、允许增行、删行或修改行，完善五项值；项目名称、显示级次、公式、取值方式、行次；</p>
                <p>4、系统预置参数通过&+参数名称取值。包括：核算单位名称，年月日（系统年月日），date（生成日期），币种，操作员姓名；</p>
                <p>5、添加项目时，只允许现金流量，系统需要较验；</p>
                <p>6、取值方式有五种：余额、借方余额、贷方余额、借方发生、贷方发生；</p>
              </div>
              <div v-if="itemConfig.base.reportName=='srzcb'" style="width: 600px;">
                <p>1、收入费用表只取科目余额或者行次加减值；若科目设置某科目5001+5002，则取两科目余额相加，若设置5001-5002，则取值两科目余额相减，设置1+2+3，则等于三个行次的值相加；</p>
                <p>2、序号为自增值，若某行公式值为空，则该行只做显示使用，不做任何数据处理，建议不设置行次值；</p>
                <p>3、允许增行、删行或修改行，完善五项值；项目名称、显示级次、公式、取值方式、行次；</p>
                <p>4、系统预置参数通过&+参数名称取值。包括：核算单位名称，年月日（系统年月日），date（生成日期），币种，操作员姓名；</p>
                <p>5、添加项目时，只允许损益类科目，系统需要较验；</p>
                <p>6、取值方式有五种：余额、借方余额、贷方余额、借方发生、贷方发生；</p>
              </div>
            </template>
          </a-popover>
        </div>
        <div style="clear:both"/>
      </div>
    </div>
    <div class="app-container" style="padding:10px;height:680px;overflow: auto !important;">
      <div class="nc-open-content">
        <div class="open-content-up">

          <div>
            <label>模板名称：</label>
            <a-input v-model:value="itemConfig.base.templateName" :disabled="true" placeholder="模板名称" style="width: 20%"/>
            <label>会计准则：</label>
            <a-select v-model:value="itemConfig.base.accStandard" :disabled="true" @change="standardCheck()" placeholder="会计准则" style="width: 20%">
              <a-select-option v-for="accstandard in accstandardList"
                               :key="accstandard.uniqueAccStandard"
                               :value="accstandard.uniqueAccStandard">
                {{ accstandard.accStandardName }}
              </a-select-option>
            </a-select>
            <label>科目模板：</label>
            <a-select v-model:value="itemConfig.base.kemuTemplateId" :disabled="true" placeholder="科目模板" style="width: 20%">
              <a-select-option v-for="item in templateList" :key="item.id" :value="item.id">
                {{ item.tname }}
              </a-select-option>
            </a-select>
            <br/>

<!--            <label>排序号：</label>
            <a-input type="number" v-model:value="itemConfig.base.num" placeholder="排序号" style="width: 20%"/>-->
            <label>模板标识：</label>
            <a-input v-model:value="itemConfig.base.titleName" placeholder="模板标识" @blur="changTitleName()" style="width: 20%"/>
            <label>报表编码：</label>
            <a-input v-model:value="itemConfig.base.menu6" placeholder="报表编码" style="width: 20%;"/>
          </div>

        </div>
      </div>
      <div style="margin-top: 10px;">
        <div style="text-align: center;margin-left: 5%;margin-right: 5%;">
          <a-input v-model:value="itemConfig.base.menu1" placeholder="" style="width: 20%;float: left;"/>
          <a-input v-model:value="itemConfig.base.menu2" placeholder="" style="width: 20%;margin: 0 auto;"/>
          <a-input v-model:value="itemConfig.base.menu3" placeholder="" style="width: 20%;float: right;"/>
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
<!--            <a-checkbox-group @change="onChange" v-if="isReload">
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
                    <a-checkbox :value="row" style="width: auto"/>
                  </td>
                  <td style="width: 50px">{{ row.xuhao = rowIndex + 1 }}</td>
                  <td @click="openEditPage(row)" style="text-align: left;padding-left:1em;">
                    <a v-if="row.jici=='1' || row.jici==''">{{ row.columnShowName }}</a>
                    <a v-if="row.jici=='2'" style="padding-left:2em;">{{ row.columnShowName }}</a>
                    <a v-if="row.jici=='3'" style="padding-left:4em;">{{ row.columnShowName }}</a>
                    <a v-if="row.jici=='4'" style="padding-left:6em;">{{ row.columnShowName }}</a>
                  </td>
                  <td style="width: 50px">{{ row.hangci }}</td>
                  <td style="width: 80px">{{ row.jici }}</td>
                  <td style="width: 100px">
                    <span v-if="itemConfig.base.reportName != 'xjllb' && row.formulaMethod!=''">
                      {{ row.formulaMethod == '1' ? '行次计算' : '科目取值' }}
                    </span>
                    <span v-if="itemConfig.base.reportName == 'xjllb' && row.formulaMethod!=''">
                      {{ row.formulaMethod == '1' ? '行次计算' : '项目取值' }}
                    </span>
                  </td>
                </tr>
                </tbody>
              </table>
            </a-checkbox-group>-->
            <BasicTable
              :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
              class="a-table-font-size-12"
              @register="registerTable"
              :dataSource="itemConfig.base.table"
              :scroll="{ y: 440 }"
              v-if="isReload"
            >
              <template #xuhao="{ record,index }">
                {{ record.xuhao = index + 1 }}
              </template>
              <template #columnShowName="{ record }">
                <div @click="openEditPage(record)">
                  <a v-if="record.jici=='1' || record.jici==''">{{ record.columnShowName }}</a>
                  <a v-if="record.jici=='2'" style="padding-left:2em;">{{ record.columnShowName }}</a>
                  <a v-if="record.jici=='3'" style="padding-left:4em;">{{ record.columnShowName }}</a>
                  <a v-if="record.jici=='4'" style="padding-left:6em;">{{ record.columnShowName }}</a>
                </div>
              </template>
              <template #formulaMethod="{ record }">
                <span v-if="itemConfig.base.reportName != 'xjllb' && record.formulaMethod!=''">
                  {{ record.formulaMethod == '1' ? '行次计算' : '科目取值' }}
                </span>
                <span v-if="itemConfig.base.reportName == 'xjllb' && record.formulaMethod!=''">
                  {{ record.formulaMethod == '1' ? '行次计算' : '项目取值' }}
                </span>
              </template>
            </BasicTable>
          </div>

        </div>

        <div style="height: 30px;margin-top: 10px;float: left; width: 90%;margin-left: 5%;margin-right: 5%;">
          <a-input v-model:value="itemConfig.base.menu4" placeholder="" style="width: 20%;float: left;"/>
          <a-input v-model:value="itemConfig.base.menu5" placeholder="" style="width: 20%;float: right;"/>
        </div>

        <SelectKemu @register="registerSelectKemuPage" @save="addData"/>
        <XjllbSelectKemu @register="registerXjllbSelectKemuPage" @save="addData"/>

<!--        <div style="margin-top: 20px;width: 90%; float: left;margin-left: 5%;margin-right: 5%;">
          <p>1、利润表只取科目余额或者行次加减值；若科目设置某科目5001+5002，则取两科目余额相加，若设置5001-5002，则取值两科目余额相减，设置1+2+3，则等于三个行次的值相加；</p>
          <p>2、序号为自增值，若某行公式值为空，则该行只做显示使用，不做任何数据处理，建议不设置行次值；</p>
          <p>3、允许增行、删行或修改行，完善五项值；项目名称、显示级次、公式、取值方式、行次；</p>
          <p>4、系统预置参数通过&+参数名称取值。包括：核算单位名称，年月日（系统年月日），date（生成日期），币种，操作员姓名；</p>
          <p>5、添加项目时，只允许损益类科目，系统需要较验；</p>
          <p>6、取值方式有五种：余额、借方余额、贷方余额、借方发生、贷方发生；</p>
        </div>-->

<!--        <div style="float: right;margin-left: 5%;margin-right: 5%;">
          <a-button @click="handleOk()" style="background-color: rgb(1, 129, 226);color: #fff">保存
          </a-button>&nbsp;&nbsp;&nbsp;&nbsp;
          <a-button @click="closed()" style="background-color: rgb(1, 129, 226);color: #fff">返回
          </a-button>
        </div>-->

      </div>

    </div>
  </div>

  <!--  </BasicModal>-->
</template>
<script setup="props, {content}" lang="ts">
import {getCurrentInstance, nextTick, reactive, ref, unref} from 'vue'
import {BasicModal, useModal, useModalInner} from '/@/components/Modal'
import {
  PlusOutlined,
  MinusOutlined,
  ProfileOutlined,
  SyncOutlined,
  QuestionOutlined
} from '@ant-design/icons-vue'
import SelectKemu from './select-kemu.vue'
import XjllbSelectKemu from './xjllb-select-kemu.vue'
import columnItemConfigModelHelper
  from "./helper/columnItemConfigModelHelper";
import {findAllAcctandard} from "/@/api/accstandard/accstandard";
import {
  deleteColumn,
  deleteFormulaByTemplate,
  findByColumn,
  findByFormula, findByTitleName,
  findFormulaByTemplate
} from "/@/api/record/system/report-template";
import {findByUniqueAccStandard} from "/@/api/acctemplate/acctemplate";
import {useMessage} from "/@/hooks/web/useMessage";
import {Select as ASelect,Input as AInput,Checkbox as ACheckbox,Popover as APopover} from "ant-design-vue"
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getThisIndexImg} from "/@/api/task-api/tast-bus-api";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {findYearByAccount} from "/@/api/record/system/bank-statement";
import { BasicTable, useTable } from '/@/components/Table'
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
const dynamicTenantId = ref()
const defaultAdName = ref()
const year = ref()
function showValue(data){
  dynamicTenantId.value = data.dynamicTenantId
  defaultAdName.value = data.defaultAdName
  useRouteApi(findYearByAccount, {schemaName: dynamicTenantId})(defaultAdName.value).then(res=>{
    if (res.length!=0){
      year.value = res[res.length-1].accountYear
    }
    res.forEach(item=>{
      if (item.accountYear>year.value){
        year.value = item.accountYear
      }
    })
  })

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
      useRouteApi(findByColumn,{schemaName: dynamicTenantId})(itemConfig.value.base.id).then(async res => {
        const formulaList: any = await useRouteApi(findFormulaByTemplate,{schemaName: dynamicTenantId})(itemConfig.value.base.id)
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

const CrudApi = {
  columns: [
    {
      title: '序号',
      dataIndex: 'xuhao',
      ellipsis: true,
      width: 50,
      slots: { customRender: 'xuhao' }
    },
    {
      title: '项目名称',
      dataIndex: 'columnShowName',
      ellipsis: true,
      align: 'left',
      slots: { customRender: 'columnShowName' }
    },
    {
      title: '行次',
      dataIndex: 'hangci',
      ellipsis: true,
      width: 50,
      slots: { customRender: 'hangci' }
    },
    {
      title: '显示级次',
      dataIndex: 'jici',
      ellipsis: true,
      width: 80,
      slots: { customRender: 'jici' }
    },
    {
      title: '计算方式',
      dataIndex: 'formulaMethod',
      ellipsis: true,
      width: 100,
      slots: { customRender: 'formulaMethod' }
    },
  ],
}

// 这是示例组件
const [registerTable, { reload,getDataSource,setTableData,setPagination,getPaginationRef }] = useTable({
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: false,
  pagination: false,
})

//选中内容
const state = reactive<{
  selectedRowKeys: [];
  loading: boolean;
}>({
  selectedRowKeys: [], // Check here to configure the default column
  loading: false,
});
const checkRow:any = ref([])
const onSelectChange = (selectedRowKeys,row) => {
  // console.log('selectedRowKeys changed: ', row);
  state.selectedRowKeys = selectedRowKeys;
  checkRow.value = row
  checked.value = row
};

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
const [registerXjllbSelectKemuPage, { openModal: openXjllbSelectKemuPage }] = useModal()
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
    if (itemConfig.value.base.reportName != 'xjllb') {
      openSelectKemuPage(true, {
        data: {
          // id: '',
          templateId: '',
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
        hang: hang.value,
        reportName:itemConfig.value.base.reportName,
        dynamicTenantId: dynamicTenantId.value,
        year:year.value
      })
    } else {
      openXjllbSelectKemuPage(true, {
        data: {
          // id: '',
          templateId: '',
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
        hang: hang.value,
        reportName:itemConfig.value.base.reportName,
        dynamicTenantId: dynamicTenantId.value,
        year:year.value
      })
    }
  }
}
//修改
const openEditPage = (data:any) => {
  getHang()
  if (itemConfig.value.base.reportName != 'xjllb') {
    openSelectKemuPage(true, {
      data,
      accStandard: itemConfig.value.base.accStandard,
      kemuTemplateId: itemConfig.value.base.kemuTemplateId,
      table: table.value,
      hang: hang.value,
      reportName:itemConfig.value.base.reportName,
      dynamicTenantId: dynamicTenantId.value,
      year:year.value
    })
  } else {
    openXjllbSelectKemuPage(true, {
      data,
      accStandard: itemConfig.value.base.accStandard,
      kemuTemplateId: itemConfig.value.base.kemuTemplateId,
      table: table.value,
      hang: hang.value,
      reportName:itemConfig.value.base.reportName,
      dynamicTenantId: dynamicTenantId.value,
      year:year.value
    })
  }
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
  setTableData(itemConfig.value.base.table)
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
    setTableData(itemConfig.value.base.table)
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
    setTableData(itemConfig.value.base.table)
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
  itemConfig.value.base.flag='1'
  if (delRows.value.length>0){
    for (const item of delRows.value) {
      await useRouteApi(deleteColumn,{schemaName: dynamicTenantId})(item)
    }
  }
  if (itemConfig.value.base.id!=''){
    await useRouteApi(deleteFormulaByTemplate,{schemaName: dynamicTenantId})(itemConfig.value.base)
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
  const res = await useRouteApi(findByTitleName,{schemaName: dynamicTenantId})({reportName: itemConfig.value.base.reportName,titleName:itemConfig.value.base.titleName})
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
.app-container {
  background-color: #f2f2f2 !important;
  padding: 10px 5px;
  margin: 10px 10px 5px;
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
<style scoped lang="less">
:deep(.vben-basic-table) {
  .ant-table-wrapper {
    .ant-table-measure-row{
      td{
        padding: 0!important;
      }
    }
  }
}

:deep(.ant-card-body) {
  padding: 16px;
  border-left: 2px solid rgb(1, 143, 251);
  box-shadow: rgb(72 113 140) -3px 1px 7px -1px;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 13px !important;
  padding: 2px 8px !important;
}

.a-table-font-size-12 :deep(th){
  background-color: #cccccc !important;
  text-align: center !important;
}

.abc:hover {
  color: blue; /*DiV背景颜色*/
}

</style>
