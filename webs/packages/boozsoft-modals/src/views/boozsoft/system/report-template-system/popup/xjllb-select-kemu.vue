<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="公式设置"
    @ok="handleOk()"
    @register="register"
    :canFullscreen="false"
    :footer="null"
    loadingTip="查询参数初始化中"
  >
    <template #title>
      <div style="height: 30px;width: 100%;background-color: #5f375c;color: white;line-height: 30px;">
        <AppstoreOutlined style="margin: 0 2px;font-size: 14px;"/>
        <span style="font-size: 14px">报表中心</span>
      </div>
    </template>
    <div style="display: inline-flex;justify-content: space-between;width: 100%;">
      <div style="width: calc(100% - 150px);height: 100%;">
        <div style="text-align: center;padding: 10px 0 5px;">
          <FormOutlined style="font-size: 24px;color: #0096c7;margin-top: 2px"/>
          <span style="line-height:30px;font-size: 24px;color: #0096c7;font-weight: bold;">&ensp;公式设置</span>
        </div>
        <div class="nc-open-content" style="margin-top: 20px;">
          <div class="open-content-up">
            <div class="border-div">
              <span>栏目属性</span>
              <a-radio-group v-model:value="formItems.base.formulaMethod" @change="checkMethod()" style="width:100%;">
                <a-radio value="2" style="width:30%;">
                  <span style="color: #000000;font-weight: bold;font-size: 13px;">按项目计算</span>
                </a-radio>
                <a-radio value="1" style="width:30%;">
                  <span style="color: #000000;font-weight: bold;font-size: 13px;">按级次计算</span>
                </a-radio>
                <a-radio value="" style="width:30%;">
                  <span style="color: #000000;font-weight: bold;font-size: 13px;">仅显示</span>
                </a-radio>
              </a-radio-group>
            </div>

            <div class="border-div">
              <span>栏目显示</span>
              <label style="width: 110px;margin:0;padding:0;">栏目显示名称：</label>
              <a-input v-model:value="formItems.base.columnShowName" placeholder="栏目名称" style="width: 220px;"/>
              &nbsp;
              <label style="width: 70px;margin:0;padding:0;">显示行次：</label>
              <a-input-number v-model:value="formItems.base.hangci" @blur="checkHangci()" :min="1" :max="100" :precision="0" placeholder="行次" style="width: 80px;"/>
              &nbsp;
              <label style="width: 70px;margin:0;padding:0;">显示级次：</label>
              <a-select v-model:value="formItems.base.jici" style="width: 80px;text-align: center;">
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                <a-select-option value="">请选择</a-select-option>
                <a-select-option value="1">1</a-select-option>
                <a-select-option value="2">2</a-select-option>
                <a-select-option value="3">3</a-select-option>
                <a-select-option value="4">4</a-select-option>
              </a-select>
            </div>

            <div class="border-div">
              <span>取值来源</span>
              <label style="width: 100px;">取值方式：</label>
              <a-select v-model:value="formItems.base.valueMethod" :disabled="!showKemu" style="width: 200px;">
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                <a-select-option value="">请选择</a-select-option>
                <a-select-option value="1">余额</a-select-option>
                <a-select-option value="2">借方余额</a-select-option>
                <a-select-option value="3">贷方余额</a-select-option>
                <a-select-option value="4">借方发生</a-select-option>
                <a-select-option value="5">贷方发生</a-select-option>
              </a-select>
            </div>

          </div>



        </div>
        <div style="margin: 20px 30px 0;">
          <div v-if="showHangci || showKemu" style="font-size: 14px;font-weight: bold;">
            <a @click="createRow()">增行</a>
            &emsp;
            <a @click="delRowList()">删行</a>
          </div>

          <div v-show="showHangci" style="height:260px;overflow: auto !important;">
            <a-checkbox-group @change="onChange" v-if="isReload">
              <table v-show="showHangci" class="layui-table" id="tableId1" style="height: calc( 100% - 20px );margin-top: 10px;background-color:#f2f4f6;">
                <thead>
                <tr class="dy_table_thead_tr even">
                  <th style="width: 80px;border-left: none;">选择</th>
                  <th>行次-行次名称</th>
                  <th style="width: 100px;border-right: none;">运算符号</th>
                </tr>
                </thead>
                <tbody id="tbody1" style="height: calc( 100% - 20px );margin-top: 10px;background-color:#f2f4f6;">
                <tr class="odd" v-for="(row,rowIndex) in formItems.base.formulaTable">
                  <td style="width: 80px;border-left: none;text-align: center;">
                    <a-checkbox :value="rowIndex" style="width: auto;text-align: center;"/>
                  </td>
                  <td style="text-align: left">
                    <a-select v-model:value="row.ccode" class="none-border" placeholder="行次-行次名称" style="width: 100%">
                      <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                      <a-select-option v-for="item in table" :key="item.hangci" :value="item.hangci">
                        {{ item.hangci }}--{{ item.columnShowName }}
                      </a-select-option>
                    </a-select>
                  </td>
                  <td style="width: 100px;border-right: none;">
                    <a-select v-model:value="row.fuhao" class="none-border" style="width: 100px">
                      <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                      <a-select-option value="+">+</a-select-option>
                      <a-select-option value="-">-</a-select-option>
                    </a-select>
                  </td>
                </tr>
                </tbody>
              </table>
            </a-checkbox-group>
          </div>

          <div v-show="showKemu" style="height:260px;overflow: auto !important;">
            <a-checkbox-group @change="onChange" v-if="isReload">
              <table class="layui-table" id="tableId" style="height: calc( 100% - 20px );margin-top: 10px;background-color:#f2f4f6;">
                <thead>
                <tr class="dy_table_thead_tr even">
                  <th style="width: 80px;border-left: none;">选择</th>
                  <th>项目编码-项目名称</th>
                  <th style="width: 100px;">运算符号</th>
                  <th style="width: 100px;border-right: none">方向</th>
                </tr>
                </thead>
                <tbody id="tbody" style="height: calc( 100% - 100px );">
                <tr class="odd" v-for="(row,rowIndex) in formItems.base.formulaTable">
                  <td style="width: 80px;border-left: none;text-align: center;">
                    <a-checkbox :value="rowIndex" style="width: auto;text-align: center;"/>
                  </td>
                  <td style="text-align: left;border-left: none;">
                    <a-select v-model:value="row.ccode" @change="chengeProject(row)" placeholder="项目编码"
                              style="width: 100%;">
                      <a-select-option v-for="project in projectList" :key="project.projectCode"
                                       :value="project.projectCode">
                        {{ project.projectCode }}--{{ project.projectName }}
                      </a-select-option>
                    </a-select>
                  </td>
                  <td style="width: 100px;">
                    <a-select v-model:value="row.fuhao" class="none-border" style="width: 100px">
                      <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                      <a-select-option value="+">+</a-select-option>
                      <a-select-option value="-">-</a-select-option>
                    </a-select>
                  </td>
                  <td style="width: 100px;border-right: none;">
                    <span v-if="row.fangxiang!=''">{{ row.fangxiang == '1' ? '流入' : '流出' }}</span>
                  </td>
                </tr>
                </tbody>
              </table>
            </a-checkbox-group>
          </div>
        </div>
      </div>
      <div class="right-btns">
        <Button style="width: 100px;" shape="round" @click="handleOk"  type="primary">保存</Button>
        <Button style="width: 100px;margin-top: 10px" shape="round" @click="closeModal">放弃</Button>
      </div>
    </div>

  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import {nextTick, ref, unref} from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {
  PlusOutlined,
  DeleteOutlined,
  FormOutlined,
  CaretDownOutlined,
  AppstoreOutlined
} from '@ant-design/icons-vue'
import formulaItemConfigModelHelper
  from "./helper/formulaItemConfigModelHelper";
import {deleteFormula} from "/@/api/record/system/report-template";
import {company_findByTemplateIdCode} from "/@/api/acctemplate/acctemplate";
import {Select as ASelect,Input as AInput,InputNumber as AInputNumber,Radio as ARadio,Checkbox as ACheckbox,Button} from "ant-design-vue"
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {useMessage} from "/@/hooks/web/useMessage";
import {company_findAllCodeOrderByAsc, company_findCodeByYear} from "/@/api/codekemu/codekemu";
import { BasicTable, useTable } from '/@/components/Table'
import {findProjectCashList} from "/@/api/record/system/project-cash";
const ASelectOption=ASelect.Option
const ARadioGroup = ARadio.Group
const ACheckboxGroup = ACheckbox.Group

const {
  createErrorModal
} = useMessage()

const emit=defineEmits(['register','save'])

const {createBaseRow, createFormulaItemConfig} = formulaItemConfigModelHelper()
const formItems:any = ref(createFormulaItemConfig())
const saveClick:any = ref(false)

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

const projectList:any = ref({})
const table:any = ref([])
const hang:any = ref([])
const dynamicTenantId = ref()
const [register, { closeModal }] = useModalInner((data) => {
  dynamicTenantId.value = data.dynamicTenantId
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

  table.value = []
  data.table.forEach(
    function (item) {
      if (item.xuhao < data.data.xuhao) {
        table.value.push(item)
      }
      if (data.data.xuhao == null || data.data.xuhao == '') {
        table.value.push(item)
      }
    }
  )
  // table.value = data.table
  hang.value = data.hang

  formItems.value.base.formulaTable = []
  if (data.data.formulaTable.length>0) {
    formItems.value.base.formulaTable = data.data.formulaTable
  }

  showHangci.value = false
  showKemu.value = false
  if (formItems.value.base.formulaMethod=='1'){
    showHangci.value = true
    showKemu.value = false
  } else if (formItems.value.base.formulaMethod=='2') {
    showHangci.value = false
    showKemu.value = true
  }

  useRouteApi(findProjectCashList,{schemaName: dynamicTenantId})({}).then(res=>{
    projectList.value = res.items
  })

  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value.base))
})
//提交方法
async function handleOk() {
  emit('save', unref(formItems.value.base))
  closeModal()
}

const showHangci = ref(false)
const showKemu = ref(false)
//删除行
const checked:any = ref([])
const isReload=ref(true)
//获取选中栏目
const onChange = (checkedValues:any) => {
  checked.value=checkedValues.sort()
  // console.log('checked = ', checkedValues.sort());
}
function delRowList(){
  isReload.value = false
  nextTick(() => isReload.value = true)
  checked.value.reverse().forEach(item=> {
    formItems.value.base.formulaTable.splice(item, 1)
  })
  checked.value=[]
}
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
//行次重复验证
function checkHangci(){
  if (formItems.value.base.hangci==changeBeforeModel.hangci){
    return true
  } else {
    hang.value.forEach(
      function (item){
        if (formItems.value.base.hangci==item){
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

//给科目方向赋值
function chengeProject(row){
  projectList.value.forEach(function (project) {
    if (row.ccode==project.projectCode){
      row.fangxiang=project.fangxiang
    }
  })
}

</script>
<style lang="less" scoped>
:deep(.ant-checkbox){
  margin-top: -8px;
}

:deep(.ant-input-number){
  border: none;
  //border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;

  .ant-input-number-handler-wrap{
    display:none;
    width: 100%;
  }
  input{
    width: 100%;
    font-size: 14px;
    font-weight: bold;
    font-family: Arial !important;
  }
}

.nc-open-content {
  background-image: url(/@/assets/images/homes/bg-pattern.png);
  background-repeat: no-repeat;
  background-position: 66% 8%;
  background-size: contain;
  position: relative;
  :deep(.ant-select-selector),:deep(.ant-input),:deep(.ant-picker), :deep(.ant-input-affix-wrapper) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    background: none;
  }
  .border-div {
    position: relative;
    border: 1px #a29f9f solid;
    margin: 20px 10px;
    padding: 2.5%;

    > span {
      display: block;
      text-align: center;
      background-color: white;
      position: absolute;
      left: 50px;
      top: -10px;
      color: #888888;
      font-size: 12px;
      font-weight: bold;
      padding-left: 1em;
      padding-right: 1em;
    }
    :deep(.account-picker){
      >div{
        text-align: left;
      }
    }

    label {
      text-align: left;
      width: 100px;
      display: inline-block;
      color: #535353;
      font-size: 13px;
      font-weight: bold;
    }
    :deep(.ant-input){
      border: none !important;
      border-bottom: 1px solid #bdb9b9 !important;
    }

    :deep(.ant-input-number){
      border: none;
      border-bottom: solid 1px rgb(191, 191, 191) !important;
      width: 100%;

      .ant-input-number-handler-wrap{
        display:none;
      }
      input{
        font-size: 14px;
        font-weight: bold;
        font-family: Arial !important;
      }
    }

    :deep(.red_span) {
      color: red;
      font-weight: bold;
      display: inline-block;
      width: 20px;
      text-align: left;
    }

    .ant-select{
      color: #000000;
      font-weight: bold;
    }
  }

}

.right-btns{
  width: 150px;background-color: #f1f1f1;padding: 10% 4%;height: 600px;
  :deep(.ant-btn-primary:hover){
    border: 1px solid #5f375c;
  }
}
:global(.ant-modal-header) {
  padding: 10px 0 !important;
}
:global(.ant-modal-close-x){
  height: 30px !important;
  color: white;
}

:deep(.ant-radio-button-wrapper){
  color: #0096c7;
}
</style>
