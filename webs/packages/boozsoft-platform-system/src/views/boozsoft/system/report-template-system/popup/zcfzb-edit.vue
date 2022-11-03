<template>
  <!--  <BasicModal
      width="1100px"
      v-bind="$attrs"
      title="资产负债表模板"
      @ok="handleOk()"
      @register="register"
    >-->

  <div v-show="showEdit"
       style="position: absolute;left:0;top:0;z-index:100;width: 100%;height:100%;background:#b4c8e3;overflow:auto;">
    <div class="app-container" style="padding:10px;height: auto">
      <div class="app-container-head">
<!--        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">-->
        <div class="container-head-title" style="float: left;margin-top: 10px;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 40%;text-align: center;margin-top: 30px;">
          <b class="noneSpan" style="font-size: 26px;">资产负债表模板</b>
        </div>

        <div class="ant-btn-group" style="float: right">
          <a-button @click="handleOk()" :disabled="saveClick" class="ant-btn ant-btn-me">保存
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
              <div style="width: 600px;">
                <p>1、资产负债表只取科目余额或者行次加减值；若科目设置某科目1001+1002，则取两科目余额相加，若设置1001-1002，则取值两科目余额相减，设置1+2+3，则等于三个行次的值相加；</p>
                <p>2、序号为自增值，若某行公式值为空，则该行只做显示使用，不做任何数据处理，建议不设置行次值；</p>
                <p>3、允许增行、删行或修改行，完善四项值；资产或负债和所有者权益的名称、显示级次、公式、行次；</p>
                <p>4、系统预置参数通过&+参数名称取值。包括：核算单位名称，年月日（系统年月日），date（生成日期），币种，操作员姓名；</p>
                <p>5、左侧添加科目时，只允许添加资产和成本科目，右边只允许添加负债和所有者权益科目，系统需要较验；</p>
                <p>6、取值方式有五种：余额、借方余额、贷方余额、借方发生、贷方发生，默认值取余额；</p>
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
            <a-input v-model:value="itemConfig.base.templateName" :disabled="true"
                     placeholder="模板名称" style="width: 20%"/>
            <label>会计准则：</label>
            <a-select v-model:value="itemConfig.base.accStandard" :disabled="true"
                      @change="standardCheck()" placeholder="会计准则" style="width: 20%">
              <a-select-option v-for="accstandard in accstandardList"
                               :key="accstandard.uniqueAccStandard"
                               :value="accstandard.uniqueAccStandard">
                {{ accstandard.accStandardName }}
              </a-select-option>
            </a-select>
            <label>科目模板：</label>
            <a-select v-model:value="itemConfig.base.kemuTemplateId" :disabled="true"
                      placeholder="科目模板" style="width: 20%">
              <a-select-option v-for="item in templateList" :key="item.id" :value="item.id">
                {{ item.tname }}
              </a-select-option>
            </a-select>
            <br/>

<!--            <label>排序号：</label>
            <a-input type="number" v-model:value="itemConfig.base.num" placeholder="排序号" style="width: 20%"/>-->
            <label>模板标识：</label>
            <a-input v-model:value="itemConfig.base.titleName" :disabled="true" placeholder="模板标识"
                     @blur="changTitleName()" style="width: 20%"/>
            <label>报表编码：</label>
            <a-input v-model:value="itemConfig.base.menu6" placeholder="报表编码" style="width: 20%;"/>
          </div>

        </div>
      </div>
      <div style="margin-top: 10px;">
        <!--        <div class="open-content-up">

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

                </div>-->

        <div style="text-align: center;">
          <a-input v-model:value="itemConfig.base.menu1" placeholder="" style="width: 20%;float: left;" />
          <a-input v-model:value="itemConfig.base.menu2" placeholder="" style="width: 20%;margin: 0 auto;" />
          <a-input v-model:value="itemConfig.base.menu3" placeholder="" style="width: 20%;float: right;" />
          <div style="float: left;width: 50%;">
            <div v-if="itemConfig.base.sysFlag!='1'" style="float: left;margin-top: 10px;">
              <!--            <a-button @click="openAddZcPage()" style="padding: 0px 4px; height: 20px;background-color: rgb(1, 129, 226);color: #fff">
                            <PlusOutlined />
                          </a-button>&nbsp;&nbsp;
                          <a-button @click="delZcRow()" style="padding: 0px 4px; height: 20px;background-color: rgb(1, 129, 226);color: #fff">
                            <MinusOutlined />
                          </a-button>-->
              <a-button @click="openAddZcPage()" style="background-color: rgb(1, 129, 226);color: #fff">
                增行
              </a-button>&nbsp;&nbsp;
              <a-button @click="delZcRow()" style="background-color: rgb(1, 129, 226);color: #fff">
                删行
              </a-button>&nbsp;&nbsp;
              <a-button @click="inZcRow()" style="background-color: rgb(1, 129, 226);color: #fff">
                插入
              </a-button>&nbsp;&nbsp;
              <a-button @click="editZcRow()" style="background-color: rgb(1, 129, 226);color: #fff">
                修改
              </a-button>&nbsp;&nbsp;
            </div>
<!--            <a-checkbox-group @change="onChangeZc" v-if="isReload">
              <table class="layui-table" id="tableId" style="height: calc( 100% - 20px );margin-top: 10px;background-color:#f2f4f6;">
                <thead>
                <tr class="dy_table_thead_tr even">
                  <th style="width: 50px">选择</th>
                  <th style="width: 50px">序号</th>
                  <th>资产名称</th>
                  <th style="width: 50px">行次</th>
                  <th style="width: 80px">显示级次</th>
                  <th style="width: 100px">计算方式</th>
                </tr>
                </thead>
                <tbody id="tbody" style="height: calc( 100% - 100px );">
                <tr class="odd" v-for="(row,rowIndex) in itemConfig.base.zcTable">
                  <td style="width: 50px">
                    <a-checkbox :value="row" style="width: auto;margin-left: -20px;" />
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
                    <span v-if="row.formulaMethod!=''">{{ row.formulaMethod=='1'?'行次计算':'科目取值' }}</span>
                  </td>
                </tr>
                </tbody>
              </table>
            </a-checkbox-group>-->
            <BasicTable
              :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
              class="a-table-font-size-12"
              @register="registerTable"
              :dataSource="itemConfig.base.zcTable"
              :scroll="{ y: 480 }"
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
                <span v-if="record.formulaMethod!=''">{{ record.formulaMethod=='1'?'行次计算':'科目取值' }}</span>
              </template>
            </BasicTable>
          </div>
          <div style="float: left;width: 50%;">
            <div v-if="itemConfig.base.sysFlag!='1'" style="float: right;margin-top: 10px;">
              <!--            <a-button @click="openAddFzPage()" style="padding: 0px 4px; height: 20px;background-color: rgb(1, 129, 226);color: #fff">
                            <PlusOutlined />
                          </a-button>&nbsp;&nbsp;
                          <a-button @click="delFzRow()" style="padding: 0px 4px; height: 20px;background-color: rgb(1, 129, 226);color: #fff">
                            <MinusOutlined />
                          </a-button>-->
              <a-button @click="openAddFzPage()" style="background-color: rgb(1, 129, 226);color: #fff">
                增行
              </a-button>&nbsp;&nbsp;
              <a-button @click="delFzRow()" style="background-color: rgb(1, 129, 226);color: #fff">
                删行
              </a-button>&nbsp;&nbsp;
              <a-button @click="inFzRow()" style="background-color: rgb(1, 129, 226);color: #fff">
                插入
              </a-button>&nbsp;&nbsp;
              <a-button @click="editFzRow()" style="background-color: rgb(1, 129, 226);color: #fff">
                修改
              </a-button>&nbsp;&nbsp;
            </div>
<!--            <a-checkbox-group @change="onChangeFz" v-if="isReload">
              <table class="layui-table" id="tableId1" style="height: calc( 100% - 20px );margin-top: 10px;background-color:#f2f4f6;">
                <thead>
                <tr class="dy_table_thead_tr even">
                  <th style="width: 50px">选择</th>
                  <th style="width: 50px">序号</th>
                  <th>负债和所有者权益</th>
                  <th style="width: 50px">行次</th>
                  <th style="width: 80px">显示级次</th>
                  <th style="width: 100px">计算方式</th>
                </tr>
                </thead>
                <tbody id="tbody1" style="height: calc( 100% - 100px );">
                <tr class="odd" v-for="(row,rowIndex) in itemConfig.base.fzTable">
                  <td style="width: 50px">
                    <a-checkbox :value="row" style="width: auto;margin-left: -20px;" />
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
                    <span v-if="row.formulaMethod!=''">{{ row.formulaMethod=='1'?'行次计算':'科目取值' }}</span>
                  </td>
                </tr>
                </tbody>
              </table>
            </a-checkbox-group>-->
            <BasicTable
              :row-selection="{ type: 'checkbox', selectedRowKeys: state1.selectedRowKeys, onChange: onSelectChange1 }"
              class="a-table-font-size-12"
              @register="registerTable1"
              :dataSource="itemConfig.base.fzTable"
              :scroll="{ y: 480 }"
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
                <span v-if="record.formulaMethod!=''">{{ record.formulaMethod=='1'?'行次计算':'科目取值' }}</span>
              </template>
            </BasicTable>
          </div>

        </div>

        <div style="height: 30px;margin-top: 10px;float: left; width: 100%;">
          <a-input v-model:value="itemConfig.base.menu4" placeholder="" style="width: 20%;float: left;"/>
          <a-input v-model:value="itemConfig.base.menu5" placeholder="" style="width: 20%;float: right;"/>
        </div>

        <SelectKemu @register="registerSelectKemuPage" @save="addData" />

<!--        <div style="margin-top: 20px;width: 100%; float: left;">
          <p>1、资产负债表只取科目余额或者行次加减值；若科目设置某科目1001+1002，则取两科目余额相加，若设置1001-1002，则取值两科目余额相减，设置1+2+3，则等于三个行次的值相加；</p>
          <p>2、序号为自增值，若某行公式值为空，则该行只做显示使用，不做任何数据处理，建议不设置行次值；</p>
          <p>3、允许增行、删行或修改行，完善四项值；资产或负债和所有者权益的名称、显示级次、公式、行次；</p>
          <p>4、系统预置参数通过&+参数名称取值。包括：核算单位名称，年月日（系统年月日），date（生成日期），币种，操作员姓名；</p>
          <p>5、左侧添加科目时，只允许添加资产和成本科目，右边只允许添加负债和所有者权益科目，系统需要较验；</p>
          <p>6、取值方式有五种：余额、借方余额、贷方余额、借方发生、贷方发生，默认值取余额；</p>
        </div>-->

<!--        <div style="float: right;">
          <a-button @click="handleOk()" :disabled="saveClick" style="background-color: rgb(1, 129, 226);color: #fff">保存
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
const accstandardList:any = ref([])
const zcTable:any = ref([])
const fzTable:any = ref([])

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
  itemConfig.value.base.zcTable = []
  itemConfig.value.base.fzTable = []
  if (data.data.zcTable==null) {
    let zcNum = 0
    let fzNum = 0
    if (itemConfig.value.base.id != '' && itemConfig.value.base.id != undefined) {
      useRouteApi(findByColumn,{schemaName: dynamicTenantId})(itemConfig.value.base.id).then(async res => {
        const formulaList: any = await useRouteApi(findFormulaByTemplate,{schemaName: dynamicTenantId})(itemConfig.value.base.id)
        res.forEach(function (column) {
          if (column.columnType == 'zc') {
            itemConfig.value.base.zcTable.push(column)
            itemConfig.value.base.zcTable[zcNum].formulaTable = formulaList.filter(item => {
              return item.columnId == column.id
            })
            zcNum++
            /*findByFormula(column.id).then(res1 => {
              itemConfig.value.base.zcTable[zcNum].formulaTable = res1
              zcNum++
            })*/
          } else {
            itemConfig.value.base.fzTable.push(column)
            itemConfig.value.base.fzTable[fzNum].formulaTable = formulaList.filter(item => {
              return item.columnId == column.id
            })
            fzNum++
            /*findByFormula(column.id).then(res1 => {
              itemConfig.value.base.fzTable[fzNum].formulaTable = res1
              fzNum++
            })*/
          }
        })
        changeBeforeModel = JSON.parse(JSON.stringify(itemConfig.value))
      })
    }
  } else {
    itemConfig.value.base.zcTable = data.data.zcTable
    itemConfig.value.base.fzTable = data.data.fzTable
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
      title: '资产名称',
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

const CrudApi1 = {
  columns: [
    {
      title: '序号',
      dataIndex: 'xuhao',
      ellipsis: true,
      width: 50,
      slots: { customRender: 'xuhao' }
    },
    {
      title: '负债和所有者权益',
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
const [registerTable1, {
  reload:reload1,
  getDataSource:getDataSource1,
  setTableData:setTableData1,
  setPagination:setPagination1,
  getPaginationRef:getPaginationRef1
}] = useTable({
  columns: CrudApi1.columns,
  bordered: true,
  showIndexColumn: false,
  pagination: false,
  /*actionColumn: {
    width: 120,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' }
  },*/
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
  zcCheck.value = row
};

//选中内容
const state1 = reactive<{
  selectedRowKeys: [];
  loading: boolean;
}>({
  selectedRowKeys: [], // Check here to configure the default column
  loading: false,
});
const checkRow1:any = ref([])
const onSelectChange1 = (selectedRowKeys,row) => {
  // console.log('selectedRowKeys changed: ', row);
  state1.selectedRowKeys = selectedRowKeys;
  checkRow1.value = row
  fzCheck.value = row
};

//获取使用行数据
const hang:any = ref([])
function getHang(){
  hang.value=[]
  zcTable.value=[]
  fzTable.value=[]
  itemConfig.value.base.zcTable.forEach(
    function (item){
      if(item.hangci!='') {
        hang.value.push(item.hangci)
        zcTable.value.push(item)
      }
    }
  )
  itemConfig.value.base.fzTable.forEach(
    function (item){
      if(item.hangci!='') {
        hang.value.push(item.hangci)
        fzTable.value.push(item)
      }
    }
  )
}
const [registerSelectKemuPage, { openModal: openSelectKemuPage }] = useModal()
//添加资产栏目
const openAddZcPage = () => {
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
        columnType: 'zc',
        formulaMethod: '',
        valueMethod: '',
        formulaCount: '',
        formulaTable: []
      },
      accStandard: itemConfig.value.base.accStandard,
      kemuTemplateId: itemConfig.value.base.kemuTemplateId,
      table: zcTable.value,
      hang: hang.value,
      reportName:itemConfig.value.base.reportName,
      dynamicTenantId: dynamicTenantId.value,
      year:year.value
    })
  }
}
//添加负债栏目
const openAddFzPage = () => {
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
        columnType: 'fz',
        formulaMethod: '',
        valueMethod: '',
        formulaCount: '',
        formulaTable: []
      },
      accStandard: itemConfig.value.base.accStandard,
      kemuTemplateId: itemConfig.value.base.kemuTemplateId,
      table: fzTable.value,
      hang: hang.value,
      reportName:itemConfig.value.base.reportName,
      dynamicTenantId: dynamicTenantId.value,
      year:year.value
    })
  }
}
//修改
const openEditPage = (data:any) => {
  getHang()
  if (data.columnType=='zc') {
    openSelectKemuPage(true, {
      data,
      accStandard: itemConfig.value.base.accStandard,
      kemuTemplateId: itemConfig.value.base.kemuTemplateId,
      table: zcTable,
      hang: hang.value,
      reportName:itemConfig.value.base.reportName,
      dynamicTenantId: dynamicTenantId.value,
      year:year.value
    })
  } else {
    openSelectKemuPage(true, {
      data,
      accStandard: itemConfig.value.base.accStandard,
      kemuTemplateId: itemConfig.value.base.kemuTemplateId,
      table: fzTable,
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
  if (data.columnType=='zc') {
    if (data.xuhao=='') {
      itemConfig.value.base.zcTable.push(JSON.parse(JSON.stringify(data)))
    } else {
      let num = parseInt(data.xuhao) - 1
      //判断行次是否改变
      if (itemConfig.value.base.zcTable[num].hangci == data.hangci) {
        itemConfig.value.base.zcTable[num] = JSON.parse(JSON.stringify(data))
      } else {
        itemConfig.value.base.zcTable.forEach(
          function (item,index) {
            if (item.formulaMethod=='1') {
              item.formulaTable.forEach(
                function (formula,index1) {
                  //改变对应的行次公式
                  if (formula.ccode == itemConfig.value.base.zcTable[num].hangci) {
                    formula.ccode = data.hangci
                  }
                  item.formulaTable[index1] = formula
                }
              )
              itemConfig.value.base.zcTable[index] = item
            }
          }
        )
        itemConfig.value.base.zcTable[num] = JSON.parse(JSON.stringify(data))
      }
    }
    setTableData(itemConfig.value.base.zcTable)
  } else {
    if (data.xuhao=='') {
      itemConfig.value.base.fzTable.push(JSON.parse(JSON.stringify(data)))
    } else {
      let num = parseInt(data.xuhao) - 1
      //判断行次是否改变
      if (itemConfig.value.base.fzTable[num].hangci == data.hangci) {
        itemConfig.value.base.fzTable[num] = JSON.parse(JSON.stringify(data))
      } else {
        itemConfig.value.base.fzTable.forEach(
          function (item,index) {
            if (item.formulaMethod=='1') {
              item.formulaTable.forEach(
                function (formula,index1) {
                  //改变对应的行次公式
                  if (formula.ccode == itemConfig.value.base.fzTable[num].hangci) {
                    formula.ccode = data.hangci
                  }
                  item.formulaTable[index1] = formula
                }
              )
              itemConfig.value.base.fzTable[index] = item
            }
          }
        )
        itemConfig.value.base.fzTable[num] = JSON.parse(JSON.stringify(data))
      }
    }
    setTableData1(itemConfig.value.base.fzTable)
  }
}

const {createBaseRow, createColumnItemConfig} = columnItemConfigModelHelper()
const itemConfig:any = ref(createColumnItemConfig())

//删除行
const delRows:any = ref([])
function delZcRow() {
  if (zcCheck.value.length>0) {
    isReload.value = false
    nextTick(() => isReload.value = true)
    for (let i = zcCheck.value.length; i > 0; i--) {
      let delNum = parseInt(zcCheck.value[i - 1].xuhao) - 1
      //判断行次是否为空
      if (zcCheck.value[i - 1].hangci != '') {
        itemConfig.value.base.zcTable.forEach(
          function (item, index) {
            if (item.formulaMethod == '1') {
              item.formulaTable.forEach(
                function (formula, index1) {
                  //删除对应行次公式
                  if (formula.ccode == zcCheck.value[i - 1].hangci) {
                    item.formulaTable.splice(index1, 1)
                  }
                }
              )
              itemConfig.value.base.zcTable[index] = item
            }
          }
        )
      }

      itemConfig.value.base.zcTable.splice(delNum, 1);
      if (zcCheck.value[i - 1].id != '' && zcCheck.value[i - 1].id != null) {
        delRows.value.push(zcCheck.value[i - 1])
        // deleteColumn(zcCheck.value[i-1])
      }
    }
    setTableData(itemConfig.value.base.zcTable)
    zcCheck.value = []
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
//删除行
function delFzRow() {
  if (fzCheck.value.length>0) {
    isReload.value = false
    nextTick(() => isReload.value = true)
    for (let i = fzCheck.value.length; i > 0; i--) {
      let delNum = parseInt(fzCheck.value[i - 1].xuhao) - 1
      //判断行次是否为空
      if (fzCheck.value[i - 1].hangci != '') {
        itemConfig.value.base.fzTable.forEach(
          function (item, index) {
            if (item.formulaMethod == '1') {
              item.formulaTable.forEach(
                function (formula, index1) {
                  //删除对应行次公式
                  if (formula.ccode == fzCheck.value[i - 1].hangci) {
                    item.formulaTable.splice(index1, 1)
                  }
                }
              )
              itemConfig.value.base.fzTable[index] = item
            }
          }
        )
      }

      itemConfig.value.base.fzTable.splice(delNum, 1);
      if (fzCheck.value[i - 1].id != '' && fzCheck.value[i - 1].id != null) {
        delRows.value.push(fzCheck.value[i - 1])
        // deleteColumn(zcCheck.value[i-1])
      }
    }
    setTableData1(itemConfig.value.base.fzTable)
    fzCheck.value = []
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
//资产点击修改按钮
function editZcRow() {
  if (zcCheck.value.length==1) {
    openEditPage(zcCheck.value[0])
    return true
  }
  createErrorModal({
    iconType: 'warning',
    title: '修改',
    content: '请选择一行进行修改'
  })
  return false
}
//负债点击修改按钮
function editFzRow() {
  if (fzCheck.value.length==1) {
    openEditPage(fzCheck.value[0])
    return true
  }
  createErrorModal({
    iconType: 'warning',
    title: '修改',
    content: '请选择一行进行修改'
  })
  return false
}
//资产点击插入按钮
function inZcRow() {
  if (zcCheck.value.length==1) {
    isReload.value = false
    nextTick(() => isReload.value = true)
    let zcNum = parseInt(zcCheck.value[0].xuhao)-1
    itemConfig.value.base.zcTable.splice(zcNum,0,
      {templateId:'',
        xuhao: '',
        columnShowName: '',
        jici: '',
        hangci: '',
        columnType: 'zc',
        formulaMethod: '',
        valueMethod: '',
        formulaCount: '',
        formulaTable: []}
    );
    setTableData(itemConfig.value.base.zcTable)
    return true
  }
  createErrorModal({
    iconType: 'warning',
    title: '插入',
    content: '请选择插入行位置'
  })
  return false
}
//负债点击插入按钮
function inFzRow() {
  if (fzCheck.value.length==1) {
    isReload.value = false
    nextTick(() => isReload.value = true)
    let fzNum = parseInt(fzCheck.value[0].xuhao)-1
    itemConfig.value.base.fzTable.splice(fzNum,0,
      {templateId:'',
        xuhao: '',
        columnShowName: '',
        jici: '',
        hangci: '',
        columnType: 'fz',
        formulaMethod: '',
        valueMethod: '',
        formulaCount: '',
        formulaTable: []}
    );
    setTableData1(itemConfig.value.base.fzTable)
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
const zcCheck:any = ref([])
const fzCheck:any = ref([])
//获取资产选中栏目
const onChangeZc = (checkedValues:any) => {
  zcCheck.value=checkedValues
  // console.log('checkedZc = ', checkedValues);
}
//获取负债选择栏目
const onChangeFz = (checkedValues:any) => {
  fzCheck.value=checkedValues
  // console.log('checkedFz = ', checkedValues);
}
//保存模板方法
async function handleOk() {
  itemConfig.value.base.reportName='zcfzb'
  itemConfig.value.base.flag='1'
  if (delRows.value.length>0){
    for (const item of delRows.value) {
      await useRouteApi(deleteColumn,{schemaName: dynamicTenantId})(item)
    }
  }
  if (itemConfig.value.base.id!='' && itemConfig.value.base.id!=null){
    await useRouteApi(deleteFormulaByTemplate,{schemaName: dynamicTenantId})(itemConfig.value.base)
  }
  emit('save', unref(JSON.parse(JSON.stringify(itemConfig.value.base))))
  closed()
}

async function changTitleName() {
  if((changeBeforeModel.base.titleName!=undefined && changeBeforeModel.base.titleName!='') || changeBeforeModel.base.titleName==itemConfig.value.base.titleName){
    return true
  }
  const res = await useRouteApi(findByTitleName,{schemaName: dynamicTenantId})({reportName: 'zcfzb',titleName:itemConfig.value.base.titleName})
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
    //padding-top: 5px;
    //padding-bottom: 5px;
    //padding-left: 2em;
    color: #535353;
  }
  .ant-checkbox-inner{
    border: 1px solid #999999 !important;
    width:15px;
    height:15px;
    margin-top: 15px;
  }
}

/*.ant-checkbox {
  border: 1px solid #999999 !important;
  border-radius: 0 !important;
  margin-top: -10px !important;
}
.ant-checkbox-inner{
  width:14px;
  height:14px;
}*/
</style>

<style lang="less" scope>
@import "../../../../../assets/styles/layui.less";
@import "../../../../../assets/styles/theme.less";
@import "../global-menu-index.less";
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
