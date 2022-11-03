<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="银行对账单"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up">

        <label style="width: 150px;">选择导入模板</label>
        <a-select v-model:value="templateName" style="width: 50%;" >
          <a-select-option v-for="(item,index) in templateList" :key="item.id" :value="item.id">
            {{item.templateName}}
          </a-select-option>
        </a-select>
        <br/><br/>
        <label style="width: 160px;">
          <a @click="exportExcel()"><DownloadOutlined/>下载导入模板</a>
        </label>
        <br/>

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
import {findBankTemplateByFlag, findByColumn} from "/@/api/record/system/bank-template";
import { ImpExcel } from '../excel/components/importexcel'
import {DownloadOutlined} from '@ant-design/icons-vue'
import {Select as ASelect,Input as AInput} from 'ant-design-vue'
const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option
import {useMessage} from "/@/hooks/web/useMessage";
import {findSettModesByFlag} from "/@/api/record/system/sett-modes";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {aoaToSheetXlsx} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
const {
  createErrorModal
} = useMessage()
const emit=defineEmits(['register','save'])
const isActiveImpExcel=ref(false)
const list:any = ref([])
function loadDataSuccess(excelDataList) {
  // console.log(excelDataList);
  // console.log(excelDataList[0].results);
  list.value = []
  let str = ''
  if (templateName!=''){
    templateList.value.forEach(
      function (template){
        if (templateName.value==template.id){
          if (template.table==null){
            useRouteApi(findByColumn, {schemaName: dynamicTenantId})(template.id).then(list => {
              template.table = list
            })
          }
          const items = excelDataList[0].results
          items.forEach(
            function (item){
              const item1:any = {}
              template.table.forEach(
                function (column){
                  if (item[column.billField]!=undefined) {
                    if (column.tableField == 'statementDate') {
                      let time = item[column.billField]
                      if (time != undefined) {
                        let isnum = /^\d+$/.test(time)
                        if (isnum && time.length == 8) {
                          time = time.substring(0, 4) + '-' + time.substring(4, 6) + '-' + time.substring(6, 8)
                        } else if (time.length == 10) {
                          time = time.substring(0, 4) + '-' + time.substring(5, 7) + '-' + time.substring(8, 10)
                        }
                        if (isNaN(time) && !isNaN(Date.parse(time))) {
                          // console.log("data是日期格式！")
                        }
                      }
                      item1.statementDate = time
                    }
                    if (column.tableField == 'duifangUnit') {
                      item1.duifangUnit = item[column.billField]
                    }
                    if (column.tableField == 'settModes') {
                      item1.settModes = item[column.billField]
                    }
                    if (column.tableField == 'piaohao') {
                      item1.piaohao = item[column.billField]
                    }
                    if (column.tableField == 'jie') {
                      let jie = item[column.billField]
                      // console.log("借方金额：==" + !isNaN(jie))
                      item1.jie = jie
                    }
                    if (column.tableField == 'dai') {
                      let dai = item[column.billField]
                      // console.log("贷方金额：==" + !isNaN(dai))
                      item1.dai = dai
                    }
                    if (column.tableField == 'remarks') {
                      item1.remarks = item[column.billField]
                    }
                    if (column.tableField == 'currencyId') {
                      item1.currencyId = item[column.billField]
                    }
                  } else {
                    return false
                  }
                }
              )
              if (JSON.stringify(item1)!='{}') {
                item1.qichu = year.value+'00'
                item1.kemuCode = kemuCode.value
                item1.flag='0'
                list.value.push(item1)
              } else {
                str = '导入模板有误，请重新设置模板进行导入'
              }
            }
          )
          // console.log(list.value)
        }
      }
    )
    if (str!='') {
      createErrorModal({
        iconType: 'warning',
        title: '导入',
        content: str
      })
    }
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '导入',
      content: '请先选择模板'
    })
  }
  // const items = excelDataList[0].results
  // console.log(items)
}
//导入时判断日期、借贷方、结算方式
let msg=''
function checkExcel(){
  msg=''
  if (list.value.length>0) {
    list.value.forEach(
      function (item) {
        let time = item.statementDate
        let jie = item.jie
        let dai = item.dai
        let settModes = item.settModes
        //较验日期
        if (isNaN(time) && !isNaN(Date.parse(time))) {
          // console.log("data是日期格式！")
          let today = time;
          let today_time =  FormatDate(today);
          if(time>qiyongDate.value){
            msg = '日期已超过期初日期，请核对后重新导入！'
            return false;
          }
        } else {
          msg = '日期格式有误！'
          return false
        }
        //较验借贷方
        if ((!isNaN(jie)||jie==null) && (!isNaN(dai)||dai==null)){
          if (jie=='' || jie==null || parseFloat(jie)==0){
            if (dai=='' || dai==null || parseFloat(dai)==0) {
              msg = '借贷方金额不能都为空值'
              return false
            } else {
              item.fangxiang = '0'
            }
          } else {
            if (dai!='' && dai!=null && parseFloat(dai)!=0){
              msg = '借贷方金额不能都有值'
              return false
            } else {
              item.fangxiang = '1'
            }
          }
        } else {
          msg = '借贷方金额有误，请核查后重新导入'
          return false
        }
        //较验结算方式
        if (settModes!='' && settModes!=null){
          let num = 0
          settModesList.value.forEach(
            function (jiesuan){
              if (jiesuan.settModesName == settModes){
                item.settModes = jiesuan.settModesCode
                num++
              }
            }
          )
          if (num==0){
            msg = '结算方式不存在，请先添加结算方式后再进行导入'
            return false
          }
        }
      }
    )
    return true
  }
  msg = '请先导入Excel'
  return false
}
function FormatDate (strTime) {
  let date = new Date(strTime);
  return date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate();
}
const settModesList:any = ref({})
/*async function reloadCurrentPage() {
  const res = await findSettModesByFlag()
  settModesList.value = res.items
  console.log(settModesList.value)
}*/
const templateName: any = ref()

const templateList: any = ref([])
const year = ref()
const qiyongDate = ref()
const kemuCode = ref()
const defaultAdName = ref()
const dynamicTenantId = ref()
const [register, {closeModal}] = useModalInner((data) => {
  defaultAdName.value = data.data.defaultAdName
  dynamicTenantId.value = data.data.dynamicTenantId
  isActiveImpExcel.value = false
  nextTick(() => {
    isActiveImpExcel.value = true
  })
  year.value = data.data.year
  qiyongDate.value = data.data.qiyongDate
  kemuCode.value = data.data.kemuCode
  //币种信息
  templateList.value = []
  list.value = []
  useRouteApi(findBankTemplateByFlag, {schemaName: dynamicTenantId})().then(res => {
    templateList.value = res.items
    templateList.value.forEach(
      function (item) {
        useRouteApi(findByColumn, {schemaName: dynamicTenantId})(item.id).then(column => {
          item.table = column
        })
        if (item.templateTitleStart == kemuCode.value && templateList.value.length > 0) {
          templateName.value = item.id
        }
      }
    )
    if ((templateName.value == '' || templateName.value == null) && templateList.value.length > 0) {
      templateName.value = templateList.value[0].id
    }
  })
  //结算方式
  settModesList.value = []
  useRouteApi(findSettModesByFlag, {schemaName: dynamicTenantId})().then(res => {
    settModesList.value = res.items
  })
})
async function handleOk() {
  checkExcel()
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
async function exportExcel() {
  const columnList = await useRouteApi(findByColumn, {schemaName: dynamicTenantId})(templateName.value)
  const arrHeader = columnList.map(item=>item.billField);
  // const arrHeader = ['编码', '名称', '说明'];
  console.log(arrHeader)
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: [],
    header: arrHeader,
    filename: '银行对账单模板.xlsx',
  });
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
