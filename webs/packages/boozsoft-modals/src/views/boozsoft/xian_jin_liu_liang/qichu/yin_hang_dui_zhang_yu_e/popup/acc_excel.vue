<template>
  <BasicModal
    width="700px"
    v-bind="$attrs"
    title="导入单位日记账期初"
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
      <span style="font-size: 20px;">导入内容：</span><span style="font-weight: bold;font-size: 20px;">单位日记账期初</span>
      <p/>
      <span style="font-size: 20px;">模板样式：</span>
      <a-select
        v-model:value="codeTemplateId"
        placeholder=""
        style="width: 40%;margin-right: 2%;font-weight: bold;font-size: 20px;">
        <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
        <a-select-option v-for="tem in templateList" :key="tem.id" :value="tem.id">
          {{ tem.tname }}
        </a-select-option>
      </a-select>
      <span style="font-size: 18px;margin-left: 100px;">
        <DownloadOutlined style="font-size: 30px;"/>
        <a @click="exportAccExcel()">&emsp;模板下载</a>
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
import {findBankTemplateByFlag} from "/@/api/record/system/bank-template";
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
// import {verifyCustomerNum} from "/@/api/record/costomer_data/customer";
// import {verifySupplierNum} from "/@/api/record/system/bank-statement";
import {CrudDayApi} from "/@/views/boozsoft/xian_jin_liu_liang/qichu/yin_hang_dui_zhang_yu_e/data";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import { Loading } from '/@/components/Loading';
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

const codeTemplateId = ref('1');
const templateList = ref([{tname:'系统简约模板',id:'1'},{tname:'系统标准模板',id:'2'}]);

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
  // console.log(excelDataList[0].header)
  const items = excelDataList[0].results
  list.value = []
  items.forEach(
    function (item){
      // console.log(item)
      const res:any = {}
      res.dbillDate = item['制单日期']
      res.csign = item['凭证类型']
      res.inoId = item['凭证编号']
      res.cdigest = item['摘要']
      res.ccode = item['科目编码']+""
      res.ccodeName = item['科目名称']
      res.md = item['借方金额']
      res.mc = item['贷方金额']
      res.pjCsettle = item['结算方式编码']
      res.pjId = item['票号']
      res.pjDate = item['票据日期']
      res.pjUnitName = item['对方单位']
      res.iyear = year.value
      res.iperiod = '20'
      res.ifrag = '0'
      res.iyperiod = year.value+'20'
      list.value.push(res)
      console.log(list.value)
    }
  )
}
//必输项（制单日期，科目编码，借贷方金额）
//导入时判断制单日期必须小于启用日期，借贷方金额，科目编码是否与选择的科目相同，结算方式、客户、供应商是否在档案中已存在，
let msg=''
function checkExcel(){
  msg=''
  console.log(list.value.length)
  if (list.value.length>0) {
    list.value.forEach(
      function (item) {
        //较验日期
        let time = item.dbillDate
        if (isNaN(time) && !isNaN(Date.parse(time))) {
          // console.log("data是日期格式！")
          let today = time;
          let today_time =  FormatDate(today);
          if(time>qiyongDate.value){
            msg = '制单日期已超过期初日期，请核对后重新导入！'
            return false
          }
        } else {
          msg = '日期格式有误！'
          return false
        }
        //较验科目
        let ccode = item.ccode
        if (ccode != kemuCode.value){
          msg = '导入科目与选择科目不项目，请核对后重新导入！'
          return false
        } else if(ccode==null){
          msg = '科目编码不能为空'
          return false
        }
        //较验借贷方
        let jie = item.md
        let dai = item.mc
        if ((!isNaN(jie)||jie==null) && (!isNaN(dai)||dai==null)){
          if (jie=='' || jie==null || parseFloat(jie)==0){
            if (dai=='' || dai==null || parseFloat(dai)==0) {
              msg = '借贷方金额不能都为空值'
              return false
            }
          } else {
            if (dai!='' && dai!=null && parseFloat(dai)!=0){
              msg = '借贷方金额不能都有值'
              return false
            }
          }
        } else {
          msg = '借贷方金额有误，请核查后重新导入'
          return false
        }
        //较验结算方式
        let settModes = item.pjCsettle
        if (settModes!='' && settModes!=null){
          let num = 0
          settModesList.value.forEach(
            function (jiesuan){
              if (jiesuan.settModesCode == settModes){
                num++
              }
            }
          )
          if (num==0){
            msg = '结算方式不存在，请先添加结算方式后再进行导入'
            return false
          }
        }
        // //较验客户
        // let ccusId = item.ccusId
        // if (ccusId!='' && ccusId!=null) {
        //   verifyCustomerNum(ccusId).then(ref => {
        //       if (ref == 0) {
        //         msg = '客户不存在，请先添加客户档案后再进行导入'
        //         return false
        //       }
        //     }
        //   )
        // }
        // //较验供应商
        // let csupId = item.csupId
        // if (csupId!='' && ccusId!=null) {
        //   verifySupplierNum(csupId).then(ref => {
        //       if (ref == 0) {
        //         msg = '供应商不存在，请先添加供应商档案后再进行导入'
        //         return false
        //       }
        //     }
        //   )
        // }
      }
    )
    return true
  }
  msg = '请先导入Excel'
  return false
}
function FormatDate (strTime) {
  let date = new Date(strTime);
  return date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
}

const settModesList: any = ref({})
const templateName: any = ref()

const year = ref()
const qiyongDate = ref()
const kemuCode = ref()
const defaultAdName = ref()
const dynamicTenantId = ref()
const [register, {closeModal,setModalProps}] = useModalInner((data) => {
  defaultAdName.value = data.defaultAdName
  dynamicTenantId.value = data.dynamicTenantId
  isActiveImpExcel.value = false
  nextTick(() => {
    isActiveImpExcel.value = true
  })
  year.value = data.data.year
  qiyongDate.value = data.data.qiyongDate
  kemuCode.value = data.data.kemuCode
  //币种信息
  list.value = []
  //结算方式
  settModesList.value = []
  useRouteApi(findSettModesByFlag, {schemaName: dynamicTenantId})({}).then(res => {
    settModesList.value = res.items
  })
  setModalProps({ minHeight: 350 });
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

function exportAccExcel() {
  const arrHeader1 = CrudDayApi.columns.map((column) => column.title);
  // const arrData1 = getDataSource1().map((item) => getColumns1().map(column=>item[column.dataIndex]));
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: [],
    header: arrHeader1,
    filename: '单位日记账期初.xlsx',
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
