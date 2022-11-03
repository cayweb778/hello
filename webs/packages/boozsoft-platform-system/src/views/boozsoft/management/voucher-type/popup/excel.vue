<template>
  <BasicModal
    width="620px"
    v-bind="$attrs"
    title="凭证类别导入"
    @ok="handleOk()"
    @register="register"
    okText="开始导入"
  >
    <div class="nc-open-content">
      <div class="open-content-up" style="height: 240px;margin-left: 5%;width:90%;">

        <div style="position: relative;background: #ffffff;border: 1px solid #999999;padding: 10px;margin-top: 20px;">
          <div style="position: absolute;background: #ffffff; top: -15px;left: 20px;padding: 0 10px;">
            导入说明
          </div>
          <p>1.导入文件格式必须为xls或xlsx，且数据表内容必须放置在第一个页签sheet1中；</p>
          <p>2.凭证类别编码、类别字简称和类别字简称不允许与当前库重复；</p>
        </div>

        <br/>
        <label style="width: 150px;">
          <a @click="exportExcel()">
            <DownloadOutlined/>
            下载导入模板</a>
        </label>

        <div style="margin-left: 40px;margin-top: 30px;">
          <ImpExcel v-if="isActiveImpExcel" @success="loadDataSuccess">
            <a-button class="m-3"> 导入Excel</a-button>
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
import {ImpExcel} from '/@/components/Excel'
import {DownloadOutlined} from '@ant-design/icons-vue'
import {Select as ASelect, Input as AInput, Radio as ARadio} from 'ant-design-vue'
const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option
const ARadioGroup = ARadio.Group
import {useMessage} from "/@/hooks/web/useMessage";
import {aoaToSheetXlsx} from "/@/components/Excel";
import {findVoucherTypeAll} from "/@/api/record/system/group-voucher-type";
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
  if (items.length > 0) {
    for (let i = 0; i < items.length; i++) {
      const item = items[i]
      const item1: any = {}
      item1.voucherNum = item['编码']
      item1.voucherTypeCode = item['类别字简称']
      item1.voucherTypeName = item['类别字名称']
      list.value.push(item1)
    }
    for (let i = 0; i < list.value.length; i++) {
      const item1 = list.value[i];
      //判断导入编码是否为空
      const voucherNum = item1.voucherNum
      if (voucherNum == null || voucherNum == '') {
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '第' + (i + 1) + '行编码为空,不能进行信息导入'
        })
        list.value = []
        return false
      }
      //判断导入类别字简称是否为空
      const voucherTypeCode = item1.voucherTypeCode
      if (voucherTypeCode == null || voucherTypeCode == '') {
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '第' + (i + 1) + '行类别字简称为空,不能进行信息导入'
        })
        list.value = []
        return false
      }
      //判断导入类别字名称是否为空
      const voucherTypeName = item1.voucherTypeName
      if (voucherTypeName == null || voucherTypeName == '') {
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '第' + (i + 1) + '行类别字名称为空,不能进行信息导入'
        })
        list.value = []
        return false
      }
      for (let j = 0; j < list.value.length; j++) {
        const item2 = list.value[j];
        if (i != j) {
          if (item1.voucherNum == item2.voucherNum) {
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第' + i + '行编码信息与第' + j + '行的信息重复，请修改后重新导入！'
            })
            list.value = []
            return false
          }
          if (item1.voucherTypeCode == item2.voucherTypeCode) {
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第' + i + '行类别字简称与第' + j + '行的信息重复，请修改后重新导入！'
            })
            list.value = []
            return false
          }
          if (item1.voucherTypeName == item2.voucherTypeName) {
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第' + i + '行类别字名称与第' + j + '行的信息重复，请修改后重新导入！'
            })
            list.value = []
            return false
          }
        }
      }
    }
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '未发现导入数据，请检查数据是否在sheet1页签中'
    })
  }
}
//导入时判断日期、借贷方、结算方式
let msg=''
function checkExcel(){
  msg=''
  for (let i=0; i<list.value.length; i++){
    const item = list.value[i];
    //判断导入编码是否为空
    const voucherNum = item['voucherNum']
    if (voucherNum==null || voucherNum==''){
      msg="第"+(i+1)+'行编码为空,不能进行信息导入'
      return false
    }
    //判断导入类别字简称是否为空
    const voucherTypeCode = item['voucherTypeCode']
    if (voucherTypeCode==null || voucherTypeCode==''){
      msg="第"+(i+1)+'行类别字简称为空,不能进行信息导入'
      return false
    }
    //判断类别字名称是否为空
    const voucherTypeName = item['voucherTypeName']
    if (voucherTypeName==null || voucherTypeName==''){
      msg="第"+(i+1)+'行类别字名称为空,不能进行信息导入'
      return false
    }
    //根据导入类型导入数据是否重复
    for (let j = 0; j < voucherTypeList.value.length; j++) {
      const object = voucherTypeList.value[j];
      if (object['voucherNum'] == voucherNum) {
        msg = "第" + (i + 1) + '行编码重复,不能进行信息导入'
        return false
      }
      if (object['voucherTypeCode'] == voucherTypeCode) {
        msg = "第" + (i + 1) + '行类别字简称重复,不能进行信息导入'
        return false
      }
      if (object['voucherTypeName'] == voucherTypeName) {
        msg = "第" + (i + 1) + '行类别字名称重复,不能进行信息导入'
        return false
      }
    }
  }
  return true
}

const voucherTypeList: any = ref([])
const [register, {closeModal}] = useModalInner((data) => {
  findVoucherTypeAll().then(res => {
    voucherTypeList.value = res
  })
  isActiveImpExcel.value = false
  nextTick(() => {
    isActiveImpExcel.value = true
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
  const arrHeader = ['编码','类别字简称','类别字名称'];
  console.log(arrHeader)
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: [],
    header: arrHeader,
    filename: '凭证类别模板.xlsx',
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
