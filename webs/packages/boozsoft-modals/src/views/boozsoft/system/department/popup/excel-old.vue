<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="人员信息导入"
    @ok="handleOk()"
    @register="register"
    okText="开始导入"
  >
    <div class="nc-open-content">
      <div class="open-content-up" style="height: 400px;margin-left: 5%;width:90%;">

        <div style="position: relative;background: #ffffff;border: 1px solid #999999;padding: 10px 20px;margin-top: 20px;">
          <div style="position: absolute;background: #ffffff; top: -15px;left: 20px;padding: 0 10px;">导入说明</div>
          <p>1.导入文件格式必须为xls或xlsx，且数据表内容必须放置在第一个页签sheet1中；</p>
          <p>2.“导入全新人员信息”是指人员库中不存在的人员，人员编码、手机号、微信、钉钉、证件号码不允许与当前库重复；</p>
          <p>3.“对已存在的人员进行辅加字段信息导入”是导入系统默认字段之外的人员信息，导入的人员编码在人员库中必须存在</p>
          <p>4.不使用模板导入时，要确保首行“人员编码、人员姓名、部门名称、和人员类别”栏目名称存在即可</p>
        </div>

        <label>数据导入范围</label><br/>
        <a-radio-group v-model:value="excelValue" @change="onChange">
          <a-radio :value="1">
            导入全新人员信息
          </a-radio><br/>
          <a-radio :value="2">
            对已存在人员进行辅加字段信息导入
          </a-radio>
        </a-radio-group>
        <br/><br/>
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
import {ImpExcel} from "/@/views/boozsoft/system/project/excel/components/importexcel";
import {Select as ASelect,Input as AInput,Radio as ARadio} from 'ant-design-vue'
const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option
const ARadioGroup = ARadio.Group
import {useMessage} from "/@/hooks/web/useMessage";
import {aoaToSheetXlsx} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
import {DownloadOutlined} from '@ant-design/icons-vue'
import {getPsnList} from "/@/api/record/system/psn";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {psnTypeFindAll} from "/@/api/psn-type/psn-type";
import {getDeptList} from "/@/api/record/system/dept";
const {
  createErrorModal
} = useMessage()

const formItems:any = ref({})
const excelValue:any = ref(1)
function onChange(e) {
  console.log('radio checked', e.target.value);
}

const emit=defineEmits(['register','save'])
const isActiveImpExcel=ref(false)
const list:any = ref([])
function loadDataSuccess(excelDataList) {
  list.value = []
  const items = excelDataList[0].results
  if (items.length>0) {
    for (let i = 0; i < items.length; i++) {
      const item = items[i]
      const item1: any = {}
      item1.psnCode = item['人员编码']
      item1.psnName = item['人员姓名']
      item1.psnType = item['属性']
      item1.psnSex = item['性别']
      item1.jobNum = item['工号']
      item1.uniqueCodeDept = item['部门名称']
      item1.uniquePsnType = item['人员类别']
      item1.cellPhoneNum = item['手机']
      item1.psnEmail = item['Email']
      item1.countryId = item['国别']
      item1.province = item['省市区']
      item1.district = item['详细地址']
      item1.psnWechat = item['微信']
      item1.psnQq = item['钉钉']
      item1.documentType = item['证件类型']
      item1.documentCode = item['证件号码']
      item1.psnStation = item['工位']
      item1.birthDate = item['出生日期']
      item1.entryDate = item['入职日期']
      item1.leaveDate = item['离职日期']
      item1.psnBank = item['开户银行']
      item1.bankArea = item['开户地']
      item1.bankAccount = item['银行账户']
      item1.bankNum = item['银行行号']
      item1.flag = '1'
      if (item1.psnSex=='未知的性别'){
        item1.psnSex = '0'
      } else if (item1.psnSex=='男'){
        item1.psnSex = '1'
      } else if (item1.psnSex=='女'){
        item1.psnSex = '2'
      } else if (item1.psnSex=='未说明的性别'){
        item1.psnSex = '9'
      }
      if (item1.psnType=='外部人员'){
        item1.psnType = '2'
      } else {
        item1.psnType = '1'
      }
      if (item1.documentType=='居民身份证'){
        item1.documentType = '1'
      } else if (item1.documentType=='港澳居民来往内地通行证'){
        item1.documentType = '2'
      } else if (item1.documentType=='台湾居民来往大陆通行证'){
        item1.documentType = '3'
      } else if (item1.documentType=='中国护照'){
        item1.documentType = '4'
      } else if (item1.documentType=='外国护照'){
        item1.documentType = '5'
      }
      if (item1.uniqueCodeDept==''||item1.uniqueCodeDept==null){
        item1.uniqueCodeDept = '9999'
      } else {
        //部门名称转换为唯一码
        let num = 0
        deptList.value.forEach(dept => {
          if (dept.deptName == item1.uniqueCodeDept) {
            item1.uniqueCodeDept = dept.uniqueCode
            num++
          }
        })
        if (num == 0) {
          /*createErrorModal({
            iconType: 'warning',
            title: '提示',
            content: '第' + (i + 1) + '行部门名称不存在,自动添加分配到“未分配“部门中'
          })*/
          item1.uniqueCodeDept = '9999'
        }
      }
      if (item1.uniquePsnType==''||item1.uniquePsnType==null){
        item1.uniquePsnType = '9999'
      } else {
        //人员类别名称转换为唯一码
        let num = 0
        psnTypeList.value.forEach(psnType => {
          if (psnType.psnTypeName == item1.uniquePsnType) {
            item1.uniquePsnType = psnType.uniqueCode
            num++
          }
        })
        if (num == 0) {
          /*createErrorModal({
            iconType: 'warning',
            title: '提示',
            content: '第' + (i + 1) + '行人员类别名称不存在,自动添加分配到“未分配“类别中'
          })*/
          item1.uniquePsnType = '9999'
        }
      }
      list.value.push(item1)
    }
    for (let i=0; i<list.value.length; i++) {
      const item1 = list.value[i];
      //判断人员姓名是否为空
      if (item1.psnName==null || item1.psnName==''){
        // msg='第'+(i+1)+'行人员姓名为空,不能进行人员信息导入'
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '第'+(i+1)+'行人员姓名为空,不能进行人员信息导入'
        })
        list.value = []
        return false
      }
      if (item1.countryId==null || item1.countryId==''){
        item1.countryId='中国'
      }
      if (item1.psnSex==null || item1.psnSex==''){
        item1.psnSex='0'
      }
      if (item1.psnType==null || item1.psnType==''){
        item1.psnType='1'
      }
      for (let j=0; j<list.value.length; j++) {
        const item2 = list.value[j];
        if (i!=j){
          if (item1.psnCode != '' && item1.psnCode != null && item1.psnCode == item2.psnCode) {
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第' + i + '行人员的编码信息与第' + j + '行的信息重复，请修改后重新导入！'
            })
            list.value = []
            return false
          }
          if (item1.cellPhoneNum != '' && item1.cellPhoneNum != null && item1.cellPhoneNum == item2.cellPhoneNum) {
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第' + i + '行人员的手机号码信息与第' + j + '行的信息重复，请修改后重新导入！'
            })
            list.value = []
            return false
          }
          if (item1.psnWechat != '' && item1.psnWechat != null && item1.psnWechat == item2.psnWechat) {
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第' + i + '行人员的微信信息与第' + j + '行的信息重复，请修改后重新导入！'
            })
            list.value = []
            return false
          }
          if (item1.psnQq != '' && item1.psnQq != null && item1.psnQq == item2.psnQq) {
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第' + i + '行人员的钉钉信息与第' + j + '行的信息重复，请修改后重新导入！'
            })
            list.value = []
            return false
          }
          if (item1.documentCode != '' && item1.documentCode != null && item1.documentCode == item2.documentCode) {
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第' + i + '行人员的证件号码信息与第' + j + '行的信息重复，请修改后重新导入！'
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
//导入时判断人员姓名不为空
let msg=''
function checkExcel(){
  msg=''
  if (list.value.length>0) {
    for (let i = 0; i < list.value.length; i++) {
      const item = list.value[i];
      /*//判断人员姓名是否为空
    const psnName = item.psnName
    if (psnName==null || psnName==''){
      msg='第'+(i+1)+'行人员姓名为空,不能进行人员信息导入'
      return false
    }
    const psnCode = item.psnCode
    if (item.countryId==null || item.countryId==''){
      item.countryId='中国'
    }
    if (item.psnSex==null || item.psnSex==''){
      item.psnSex='1'
    }
    if (item.psnType==null || item.psnType==''){
      item.psnType='1'
    }*/
      //根据导入类型导入数据是否重复
      if (excelValue.value == '1') {
        //导入全新人员信息
        for (let j = 0; j < psnList.value.length; j++) {
          const psn = psnList.value[j];
          if (item.psnCode != '' && item.psnCode != null && psn.psnCode == item.psnCode) {
            msg = '第' + (i + 1) + '行人员编码重复,不能进行人员信息导入'
            return false
          }
          if (item.cellPhoneNum != '' && item.cellPhoneNum != null && psn.cellPhoneNum == item.cellPhoneNum) {
            msg = '第' + (i + 1) + '行手机号码重复,不能进行人员信息导入'
            return false
          }
          if (item.psnWechat != '' && item.psnWechat != null && psn.psnWechat == item.psnWechat) {
            msg = '第' + (i + 1) + '行微信重复,不能进行人员信息导入'
            return false
          }
          if (item.psnQq != '' && item.psnQq != null && psn.psnQq == item.psnQq) {
            msg = '第' + (i + 1) + '行钉钉重复,不能进行人员信息导入'
            return false
          }
          if (item.documentCode != '' && item.documentCode != null && psn.documentCode == item.documentCode) {
            msg = '第' + (i + 1) + '行证件号码重复,不能进行人员信息导入'
            return false
          }
        }
      } else {
        //对已存在人员进行辅加字段信息导入
        for (let j = 0; j < psnList.value.length; j++) {
          const psn = psnList.value[j];
          if (psn.psnCode == item.psnCode) {
            item.id = psn.id
            item.uniqueCode = psn.uniqueCode
          }
          if (psn.psnCode != item.psnCode) {
            if (item.cellPhoneNum != '' && item.cellPhoneNum != null && psn.cellPhoneNum == item.cellPhoneNum) {
              msg = '第' + (i + 1) + '行手机号码重复,不能进行人员信息导入'
              return false
            }
            if (item.psnWechat != '' && item.psnWechat != null && psn.psnWechat == item.psnWechat) {
              msg = '第' + (i + 1) + '行微信重复,不能进行人员信息导入'
              return false
            }
            if (item.psnQq != '' && item.psnQq != null && psn.psnQq == item.psnQq) {
              msg = '第' + (i + 1) + '行钉钉重复,不能进行人员信息导入'
              return false
            }
            if (item.documentCode != '' && item.documentCode != null && psn.documentCode == item.documentCode) {
              msg = '第' + (i + 1) + '行证件号码重复,不能进行人员信息导入'
              return false
            }
          }
        }
        const id = item.id
        if (id == null || id == '') {
          msg = '第' + (i + 1) + '行导入的字段对应人员信息不存在,不能进行附加字段信息导入'
          return false
        }
      }
    }
    return true
  } else {
    msg = '请选择您需要导入的文件!'
    return false
  }
}

const psnList:any = ref([])
const deptList:any = ref([])
const psnTypeList:any = ref([])
const dynamicTenantId = ref()
const [register, { closeModal }] = useModalInner((data) => {
  dynamicTenantId.value = data.dynamicTenantId
  useRouteApi(getPsnList,{schemaName: dynamicTenantId})().then(res=>{
    psnList.value=res
  })
  useRouteApi(psnTypeFindAll,{schemaName: dynamicTenantId})().then(res=>{
    psnTypeList.value = res.items
  })
  useRouteApi(getDeptList,{schemaName: dynamicTenantId})().then(res=>{
    deptList.value = res.items
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
  const arrHeader = ['人员编码', '人员姓名', '属性', '性别', '工号', '部门名称', '人员类别',
    '手机', 'Email', '国别', '省市区', '详细地址', '微信', '钉钉', '证件类型', '证件号码',
    '工位', '出生日期', '入职日期', '离职日期', '开户银行', '开户地', '银行账户', '银行行号'];
  console.log(arrHeader)
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: [],
    header: arrHeader,
    filename: '人员信息模板.xlsx',
  });
}

// 下划线转换驼峰
function toHump(name: any) {
  return name.replace(/\_(\w)/g, function (all: any, letter: any) {
    return letter.toUpperCase();
  });
}

//判断是否整数
function isInteger(obj) {
  return obj % 1 === 0
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
