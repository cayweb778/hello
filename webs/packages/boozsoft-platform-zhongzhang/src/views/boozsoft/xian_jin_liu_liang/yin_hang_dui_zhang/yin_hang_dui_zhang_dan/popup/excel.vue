<template>
  <BasicModal
    width="700px"
    v-bind="$attrs"
    title="银行对账单"
    @ok="handleOk()"
    @register="register"
    :canFullscreen="false"
  >
    <Loading :loading="compState.loading" :absolute="compState.absolute" :tip="compState.tip" />
    <template #title>
      <div style="height: 30px;width: 100%;background-color: #5f375c;color: white;line-height: 30px;text-align: left;">
        <AppstoreOutlined  style="margin: 0 2px;font-size: 14px;"/>
        <span style="font-size: 14px">数据导入</span>
      </div>
    </template>
    <div style="width: 100%;display: table;">
      <div  style="text-align: right;width: 44%;float: left;">
        <CloudUploadOutlined style="margin: 0 2px;font-size: 40px;color: #0096c7;"/>
      </div>
      <div style="text-align: left;width: 54%;float: right;font-size: 24px;font-weight: bold;color: #0096c7;">
        主单据导入
      </div>
    </div>
    <div style="margin-top: 40px;margin-left: 30px;">
      <span style="font-size: 20px;">导入内容：</span><span style="font-weight: bold;font-size: 20px;">银行对账单</span>
      <p/>
      <span style="font-size: 20px;">模板样式：</span>
      <a-select v-model:value="templateName" style="width: 40%;margin-right: 2%;font-weight: bold;font-size: 20px;" >
        <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
        <a-select-option v-for="(item,index) in templateList" :key="item.id" :value="item.id">
          {{item.templateName}}
        </a-select-option>
      </a-select>
      <span style="font-size: 18px;margin-left: 100px;">
        <DownloadOutlined style="font-size: 30px;"/>
        <a @click="exportExcel()">&emsp;模板下载</a>
      </span>
    </div>
    <Tabs v-model:activeKey="excelValue" style="margin-top: 40px;">
      <TabPane key="1" tab="全新添加导入">
      </TabPane>
      <TabPane key="2" :disabled="true" tab="字段覆盖导入">
      </TabPane>
    </Tabs>
    <br/>
    <ImpExcel v-if="isActiveImpExcel" @success="loadDataSuccess">
      <a-button class="m-3"> 请选择导入的文件 </a-button>
    </ImpExcel>
    <br/>

    <template #footer>
      <div style="height: 35px">
        <div style="float: left">
          <a-popover title="使用说明" trigger="click">
            <template #content>
              <p>文件中带 * 为必填项</p>
            </template>
            <Button>使用说明</Button>
          </a-popover>
        </div>
        <div style="float: right">
          <Button @click="closeModal()">放弃</Button>
          <Button @click="handleOk()" :disabled="saveClick" type="primary">开始导入</Button>
        </div>
      </div>
    </template>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {nextTick, reactive, ref, unref} from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {findBankTemplateByFlag, findByColumn} from "/@/api/record/system/bank-template";
import {aoaToSheetXlsx, ImpExcel} from '../excel/components/importexcel'
import {
  Popover as APopover,
  Upload as AUpload,
  Spin as ASpin,
  Select as ASelect,
  Input as AInput,
  Modal as AModal, Badge, Button, Tabs, Radio,Checkbox,Tooltip
} from 'ant-design-vue';
import { DownloadOutlined,CloudUploadOutlined,CaretDownOutlined,EllipsisOutlined,AppstoreOutlined } from '@ant-design/icons-vue';
const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option
const TabPane = Tabs.TabPane
import {useMessage} from "/@/hooks/web/useMessage";
import {findSettModesByFlag} from "/@/api/record/system/sett-modes";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";

const {
  createErrorModal
} = useMessage()
const emit=defineEmits(['register','save'])

/******************* 弹框加载中 **************************/
const loadingRef = ref(false);
const compState = reactive({
  absolute: false,
  loading: false,
  tip: '处理中...',
});
function openCompFullLoading() {
  openLoading(false);
}
function openLoading(absolute: boolean) {
  compState.absolute = absolute;
  compState.loading = true;
}
/*******************END**************************/

const excelValue:any = ref('1')
function onChange(e) {
  console.log('radio checked', e.target.value);
}

const saveClick:any = ref(false)

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
                      item1.jie = item[column.billField]
                    }
                    if (column.tableField == 'dai') {
                      let dai = item[column.billField]
                      // console.log("贷方金额：==" + !isNaN(dai))
                      item1.dai = item[column.billField]
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
                item1.kemuCode = kemuCode.value
                item1.flag='0'
                item1.iyear = year.value
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
const settModesList:any = ref({})
async function reloadCurrentPage() {
  const res = await useRouteApi(findSettModesByFlag, {schemaName: dynamicTenantId})({})
  settModesList.value = res.items
  console.log(settModesList.value)
}

const templateName: any = ref()

const templateList: any = ref([])

const kemuCode = ref()
const year = ref()
const defaultAdName = ref()
const dynamicTenantId = ref()
const [register, {closeModal,setModalProps}] = useModalInner((data) => {
  saveClick.value=false
  defaultAdName.value = data.data.defaultAdName
  dynamicTenantId.value = data.data.dynamicTenantId
  isActiveImpExcel.value = false
  nextTick(() => {
    isActiveImpExcel.value = true
  })
  kemuCode.value = data.data.kemuCode
  year.value = data.data.year
  //币种信息
  templateList.value = []
  list.value = []
  useRouteApi(findBankTemplateByFlag, {schemaName: dynamicTenantId})({}).then(res => {
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
  useRouteApi(findSettModesByFlag, {schemaName: dynamicTenantId})({}).then(res => {
    settModesList.value = res.items
  })
  setModalProps({ minHeight: 350 });
})
async function handleOk() {
  saveClick.value=true
  checkExcel()
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
@import '/@/assets/styles/redTitle-open.less';
:deep(.ant-select-selector) {
  border: none !important;
  border-bottom: 1px solid #bdb9b9 !important;
}
</style>
