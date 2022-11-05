<template>
  <BasicModal
    width="620px"
    v-bind="$attrs"
    title="项目大类导入"
    @ok="handleOk()"
    @register="register"
    okText="开始导入"
  >
    <div class="nc-open-content">
      <div class="open-content-up" style="height: 240px;margin-left: 5%;width:90%;">

        <div style="position: relative;background: #ffffff;border: 1px solid #999999;padding: 10px;margin-top: 20px;">
          <div style="position: absolute;background: #ffffff; top: -15px;left: 20px;padding: 0 10px;">导入说明</div>
          <p>1.导入文件格式必须为xls或xlsx，且数据表内容必须放置在第一个页签sheet1中；</p>
          <p>2.项目大类编码和名称不允许与当前库重复；</p>
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
import {aoaToSheetXlsx} from "/@/components/Excel/src/Export2Excel";
import {cateFindAll} from "/@/api/group_project_category/project_category";
import projectCategoryItemConfigModelHelper
  from "/@/views/boozsoft/management/project-category/popup/helper/projectCategoryItemConfigModelHelper";
const {
  createErrorModal
} = useMessage()
import {DownloadOutlined} from '@ant-design/icons-vue'
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
    item1.projectCateCode = item['大类编码']
    item1.projectCateName = item['大类名称']
    item1.flag = '1'
    item1.table = createBaseRow1()
    list.value.push(item1)
  }
}
//导入时判断日期、借贷方、结算方式
let msg=''
function checkExcel(){
  msg=''
  for (let i=0; i<list.value.length; i++){
    const item = list.value[i];
    //判断项目大类编码是否为空
    const projectCateCode = item['projectCateCode']
    if (projectCateCode==null || projectCateCode==''){
      msg="第"+(i+1)+'行项目大类编码为空,不能进行信息导入'
      return false
    }
    //判断项目大类名称是否为空
    const projectCateName = item['projectCateName']
    if (projectCateName==null || projectCateName==''){
      msg="第"+(i+1)+'行项目大类名称为空,不能进行信息导入'
      return false
    }
    //判断导入数据是否重复
    for (let j = 0; j < cateList.value.length; j++) {
      const object = cateList.value[j];
      if (object['projectCateCode'] == projectCateCode) {
        msg = "第" + (i + 1) + '行项目大类编码重复,不能进行信息导入'
        return false
      }
      if (object['projectCateName'] == projectCateName) {
        msg = "第" + (i + 1) + '行项目大类名称重复,不能进行信息导入'
        return false
      }
    }
  }
  return true
}
const { createBaseRow1 } = projectCategoryItemConfigModelHelper()

const cateList:any = ref([])
const [register, { closeModal }] = useModalInner((data) => {
  cateFindAll().then(res=>{
    cateList.value = res
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
  const arrHeader = ['大类编码','大类名称'];
  console.log(arrHeader)
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: [],
    header: arrHeader,
    filename: '项目大类模板.xlsx',
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
