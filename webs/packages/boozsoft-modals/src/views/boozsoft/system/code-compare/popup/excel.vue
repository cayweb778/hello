<template>
  <BasicModal
    width="620px"
    v-bind="$attrs"
    title="对照科目导入"
    @ok="handleOk()"
    @register="register"
    okText="开始导入"
  >
    <div class="nc-open-content">
      <div class="open-content-up" style="height: 240px;margin-left: 5%;width:90%;">

        <div style="position: relative;background: #ffffff;border: 1px solid #999999;padding: 10px;margin-top: 20px;">
          <div style="position: absolute;background: #ffffff; top: -15px;left: 20px;padding: 0 10px;">导入说明</div>
          <p>1.导入文件格式必须为xls或xlsx，且数据表内容必须放置在第一个页签sheet1中；</p>
          <p>2.财务会计科目编码不允许与当前对照表中已存在的会计科目重复；</p>
          <p>3.预算会计科目必须是末级科目，财务会计科目不限科目级次；</p>
        </div>

        <br/>
        <label style="width: 150px;">
          <a @click="exportExcel()"><DownloadOutlined/>下载导入模板</a>
        </label>

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
import { ImpExcel } from '/@/components/Excel'
import {Select as ASelect,Input as AInput,Radio as ARadio} from 'ant-design-vue'
const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option
const ARadioGroup = ARadio.Group
import {useMessage} from "/@/hooks/web/useMessage";
//import {aoaToSheetXlsx} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
const aoaToSheetXlsx=null
import {findCodeCompareByIyear} from "/@/api/record/system/code-compare";
import {findCodeByYearAndBend} from "/@/api/record/system/project-cash";
import {DownloadOutlined} from '@ant-design/icons-vue'
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
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
  const items = excelDataList[0].results
  for (let i=0; i<items.length; i++){
    const item = items[i]
    const item1:any = {}
    item1.sourceCode = item['财务会计科目编码']+''
    item1.targetCode = item['预算会计科目编码']+''
    item1.iyear = iyear.value
    item1.sameSource = item1.targetCode
    list.value.push(item1)
  }
}
//导入时判断
let msg=''
function checkExcel(){
  msg=''
  for (let i=0; i<list.value.length; i++){
    const item = list.value[i];
    //判断导入财务会计科目编码是否为空
    const sourceCode = item['sourceCode']
    if (sourceCode==null || sourceCode==''){
      msg="第"+(i+1)+'行财务会计科目编码为空,不能进行信息导入'
      return false
    }
    //判断导入预算会计科目编码是否为空
    const targetCode = item['targetCode']
    if (targetCode==null || targetCode==''){
      msg="第"+(i+1)+'行预算会计科目编码为空,不能进行信息导入'
      return false
    }
    //判读是否与数据库数据重复
    for (let j=0; j<codeCompareList.value.length; j++){
      const item1 = codeCompareList.value[j]
      if (item1.sourceCode.slice(0, sourceCode.length)==sourceCode){
        msg="第"+(i+1)+'行财务会计科目编码重复,不能进行信息导入'
        return false
      }
      else if (sourceCode.slice(0, item1.sourceCode.length)==item1.sourceCode){
        msg="第"+(i+1)+'行财务会计科目编码重复,不能进行信息导入'
        return false
      }
    }
    //判断导入数据是否重复
    for (let a=0; a<list.value.length; a++){
      if (a!=i) {
        const item2 = list.value[a];
        if (item2.sourceCode.slice(0, sourceCode.length) == sourceCode) {
          msg = "第" + (i + 1) + '行财务会计科目编码重复,不能进行信息导入'
          return false
        } else if (sourceCode.slice(0, item2.sourceCode.length) == item2.sourceCode) {
          msg = "第" + (i + 1) + '行财务会计科目编码重复,不能进行信息导入'
          return false
        }
      }
    }

    //较验财务会计科目是否存在
    if (sourceCode!='' && sourceCode!=null){
      let num = 0
      sourcekemuList.value.forEach(
        function (kemu){
          if (kemu.ccode == sourceCode){
            num++
          }
        }
      )
      if (num==0){
        msg = '第'+ (i + 1) + '行财务会计科目编码不存在，请先检查后再进行导入'
        return false
      }
    }

    //较验预算会计科目是否存在
    if (targetCode!='' && targetCode!=null){
      let num = 0
      targetkemuList.value.forEach(
        function (kemu){
          if (kemu.ccode == targetCode){
            num++
          }
        }
      )
      if (num==0){
        msg = '第'+ (i + 1) + '行预算会计科目编码不存在，请先检查后再进行导入'
        return false
      }
    }

  }
  return true
}

const iyear = ref("")
const codeCompareList:any = ref([])
const kemuList:any = ref([])
const sourcekemuList:any = ref([])
const targetkemuList:any = ref([])
const dynamicTenantId = ref()
const [register, { closeModal }] = useModalInner((data) => {
  dynamicTenantId.value = data.dynamicTenantId
  iyear.value = data.iyear

  useRouteApi(findCodeCompareByIyear,{schemaName: dynamicTenantId})({iyear: data.iyear}).then(res=>{
    codeCompareList.value = res.items
  })
  useRouteApi(findCodeByYearAndBend,{schemaName: dynamicTenantId})(data.iyear).then(res=>{
    kemuList.value = res
    sourcekemuList.value = kemuList.value.filter(item=>{
      return item.pxjz=='1'
    }).map(item=>{
      const item1:any = {}
      item1.ccode = item.ccode
      item1.ccodeName = item.ccodeName
      return item1
    })
    targetkemuList.value = kemuList.value.filter(item=>{
      return item.yusuan=='1' && item.bend=='1'
    }).map(item=>{
      const item1:any = {}
      item1.ccode = item.ccode
      item1.ccodeName = item.ccodeName
      return item1
    })
  })

  isActiveImpExcel.value=false
  nextTick(()=>{
    isActiveImpExcel.value=true
  })
})
async function handleOk() {
  // formItems.value.excelValue = excelValue.value
  // formItems.value.object = list.value
  // formItems.value.cateCode = cateCode.value
  checkExcel()
  console.log(msg)
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
function exportExcel() {
  const arrHeader = ['财务会计科目编码','财务会计科目名称','预算会计科目编码','预算会计科目名称'];
  console.log(arrHeader)
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: [],
    header: arrHeader,
    filename: '科目对照模板.xlsx',
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
