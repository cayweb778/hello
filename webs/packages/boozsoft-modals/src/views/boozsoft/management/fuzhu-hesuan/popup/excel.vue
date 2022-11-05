<template>
  <BasicModal
    width="620px"
    v-bind="$attrs"
    title="辅助核算定义导入"
    @ok="handleOk()"
    @register="register"
    okText="开始导入"
  >
    <div class="nc-open-content">
      <div class="open-content-up" style="height: 240px;margin-left: 5%;width:90%;">

        <div
          style="position: relative;background: #ffffff;border: 1px solid #999999;padding: 10px;margin-top: 20px;">
          <div
            style="position: absolute;background: #ffffff; top: -15px;left: 20px;padding: 0 10px;">
            导入说明
          </div>
          <p>1.导入文件格式必须为xls或xlsx，且数据表内容必须放置在第一个页签sheet1中；</p>
          <p>2.自定义项设置编码和名称不允许与当前库重复；</p>
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
import {aoaToSheetXlsx} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
import {findAllFuzhuHesuanList} from "/@/api/record/system/group-fuzhu-hesuan";
import {getDefinedFileList} from "/@/api/record/system/group-defined-file";
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
      item1.ccode = item['编码']
      item1.cname = item['名称']
      item1.cankaoDuixiang = item['数据来源']
      item1.cdfine = item['对应凭证字段']
      item1.remarks = item['说明']
      cankaolist.value.forEach(cankao => {
        if (item['参照对象'] == cankao.value) {
          item1.cankaoDuixiangTable = cankao.key
          item1.cankaoDuixiangWhere = cankao.where
          item1.cankaoDuixiangFlag = cankao.flag
          item1.cankaoDuixiangKey = cankao.unique
          item1.cankaoDuixiangLabel = cankao.name
          item1.cankaoDuixiangCode = cankao.code
        }
      })
      item1.flag = '1'
      list.value.push(item1)
    }
    for (let i = 0; i < list.value.length; i++) {
      const item1 = list.value[i];
      //判断导入编码是否为空
      const ccode = item1.ccode
      if (ccode == null || ccode == '') {
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '第' + (i + 1) + '行编码为空,不能进行信息导入'
        })
        list.value = []
        return false
      }
      //判断导入名称是否为空
      const cname = item1.cname
      if (cname == null || cname == '') {
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '第' + (i + 1) + '行名称为空,不能进行信息导入'
        })
        list.value = []
        return false
      }
      //判断导入数据来源是否为空
      const cankaoDuixiang = item1.cankaoDuixiang
      if (cankaoDuixiang == null || cankaoDuixiang == '') {
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '第' + (i + 1) + '行数据来源为空,不能进行信息导入'
        })
        list.value = []
        return false
      }
      //判断导入对应凭证字段是否为空
      const cdfine = item1.cdfine
      if (cdfine == null || cdfine == '') {
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '第' + (i + 1) + '行对应凭证字段为空,不能进行信息导入'
        })
        list.value = []
        return false
      }
      if (isInteger(cdfine)) {
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '第' + (i + 1) + '行对应凭证字段不是整数,不能进行信息导入'
        })
        list.value = []
        return false
      }
      for (let j = 0; j < list.value.length; j++) {
        const item2 = list.value[j];
        if (i != j) {
          if (item1.ccode == item2.ccode) {
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第' + i + '行编码信息与第' + j + '行的信息重复，请修改后重新导入！'
            })
            list.value = []
            return false
          }
          if (item1.cname == item2.cname) {
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第' + i + '行名称信息与第' + j + '行的信息重复，请修改后重新导入！'
            })
            list.value = []
            return false
          }
          if (item1.cankaoDuixiang == item2.cankaoDuixiang) {
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第' + i + '行数据来源信息与第' + j + '行的信息重复，请修改后重新导入！'
            })
            list.value = []
            return false
          }
          if (item1.cdfine == item2.cdfine) {
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第' + i + '行对应凭证字段信息与第' + j + '行的信息重复，请修改后重新导入！'
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
//导入时判断
let msg=''
function checkExcel(){
  msg=''
  for (let i=0; i<list.value.length; i++){
    const item = list.value[i];
    //判断导入编码是否为空
    const ccode = item['ccode']
    if (ccode==null || ccode==''){
      msg="第"+(i+1)+'行编码为空,不能进行信息导入'
      return false
    }
    //判断导入名称是否为空
    const cname = item['cname']
    if (cname==null || cname==''){
      msg="第"+(i+1)+'行名称为空,不能进行信息导入'
      return false
    }
    //判断数据来源为空
    const cankaoDuixiang = item['cankaoDuixiang']
    if (cankaoDuixiang==null || cankaoDuixiang==''){
      msg="第"+(i+1)+'行数据来源为空,不能进行信息导入'
      return false
    }
    //判断数据来源错误
    const cankaoDuixiangTable = item['cankaoDuixiangTable']
    if (cankaoDuixiangTable==null || cankaoDuixiangTable==''){
      msg="第"+(i+1)+'行数据来源错误,不能进行信息导入'
      return false
    }
    //判断对应凭证字段为空
    const cdfine = item['cdfine']
    if (cdfine==null || cdfine==''){
      msg="第"+(i+1)+'行对应凭证字段为空,不能进行信息导入'
      return false
    }
    //判断对应凭证字段格式
    if (isInteger(cdfine)){
      msg="第"+(i+1)+'行对应凭证字段不是整数,不能进行信息导入'
      return false
    }
    //根据导入类型导入数据是否重复
    for (let j = 0; j < fuzhuHesuanList.value.length; j++) {
      const object = fuzhuHesuanList.value[j];
      if (object['ccode'] == ccode) {
        msg = "第" + (i + 1) + '行编码重复,不能进行信息导入'
        return false
      }
      if (object['cname'] == cname) {
        msg = "第" + (i + 1) + '行名称重复,不能进行信息导入'
        return false
      }
      if (object['cankaoDuixiang'] == cankaoDuixiang) {
        msg = "第" + (i + 1) + '行数据来源重复,不能进行信息导入'
        return false
      }
      if (object['cdfine'] == cdfine) {
        msg = "第" + (i + 1) + '行对应凭证字段重复,不能进行信息导入'
        return false
      }
    }
  }
  return true
}

function isInteger(obj) {
  return obj % 1 === 0
}

const fuzhuHesuanList: any = ref([])

const cankaolist: any = ref([])

const [register, {closeModal}] = useModalInner((data) => {
  cankaolist.value = [
    {
      key: 'customer_class',
      value: '客户分类',
      where: 'where 1=1 ',
      flag: ' and flag=1 ',
      unique: 'unique_custclass',
      name: 'cus_cclass_name',
      code: 'cus_class'
    },
    {
      key : 'supplier_class',
      value : '供应商分类',
      where : 'where 1=1 ',
      flag : ' and flag=1 ',
      unique : 'unique_supclass',
      name : 'sup_class_name',
      code : 'sup_class'
    },
    {
      key : 'sys_psn_type',
      value : '员工类别',
      where : 'where 1=1 ',
      flag : '',
      unique : 'unique_code',
      name : 'psn_type_name',
      code : 'psn_type_code'
    },
    {
      key: '_app_group_sys_zone',
      value: '行政区划',
      where: 'where 1=1 ',
      flag: '',
      unique: 'id',
      name: 'zone_name',
      code: 'procode'
    },
  ]
  getDefinedFileList().then(res => {
    res.forEach(item => {
      const item1: any = {}
      item1.key = 'defined_code'
      item1.value = '(' + item.definedCode + ')' + item.definedName
      item1.where = 'where defined_code=' + item.definedCode + ' '
      item1.flag = ' and flag=1 '
      item1.unique = 'record_code'
      item1.name = 'record_name'
      item1.code = 'record_code'
      cankaolist.value.push(item1)
    })
  })
  findAllFuzhuHesuanList().then(res => {
    fuzhuHesuanList.value = res
  })
  isActiveImpExcel.value = false
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
  const arrHeader = ['编码','名称','数据来源','对应凭证字段','说明'];
  console.log(arrHeader)
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: [],
    header: arrHeader,
    filename: '辅助核算定义模板.xlsx',
  });
}

</script>
<style>
.vben-basic-title {
  color: rgb(1, 129, 226) !important;
}

.ant-modal-body {
  padding: 0px;
  border: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
}
</style>
<style lang="less" scoped>
:deep(.ant-calendar-picker-input.ant-input), :deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector .ant-select-selection-search-input) {
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
