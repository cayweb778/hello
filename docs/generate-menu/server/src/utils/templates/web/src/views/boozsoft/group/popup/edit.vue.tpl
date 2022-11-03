<template>
  <BasicModal width="940px" v-bind="$attrs" title="档案编辑页" :canFullscreen="false"
              @ok="handleOk()" :closeFunc="handleClose" :showOkBtn="!isLook" @register="register"
              :ok-text="'保存'">
    <div v-if="true" style="height: 500px">
      <BasicForm @register="registerForm" />
    </div>
  </BasicModal>
</template>
<style src="../../../../../assets/styles/global-open.less" lang="less" scoped></style>
<style lang="less" scoped>
:deep(.ant-form-item-control button) {
  display: none;
}

:deep(.ant-input-number-input) {
  width: 100%;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
}

:deep(.ant-calendar-picker-input.ant-input),
:deep(.ant-select-single:not(.ant-select-customize-input)
  .ant-select-selector
  .ant-select-selection-search-input) {
  border: none;
  width: 100%;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
}

:deep(.ant-input-number) {
  width: 90%;
  border: none;
}

:deep(.nc-open-content input ) {
  width: 100% !important;
}


:deep(.ant-select-single:not(.ant-select-customize-input)
  .ant-select-selector
  .ant-select-selection-search-input) {
  border: none !important;
}

.nc-open-content-look {
  pointer-events: none;
  cursor: default;
}

.nc-open-content {
  color: #747475;

  input {
    width: 32%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  :deep(.ant-upload-picture-card-wrapper) {
    width: 88px;

    .ant-upload-select-picture-card {
      width: 280px;
      height: 80px;
      margin-right: 0;
      margin-bottom: 0;

      .ant-upload {
        //padding: 0;
      }
    }
  }

  :deep(.ant-cascader-input) {
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
    text-align: left;
    width: 120px;
    display: inline-block;
    padding: 6px 10px;
    margin-left: 15px;font-weight: bold;
  }

  .red_span {
    color: red;
    font-weight: bold;
    display: inline-block;
    width: 20px;
    text-align: right;
  }
}

:deep(.ant-select-disabled) {
  .ant-select-selector {
    background-color: white !important;
    color: black !important;
  }
}

.nc-border-div {
  position: relative;
  border: 1px solid #494949;
  margin: 3% 0;

  .nc-border-div-span {
    min-width: 80px;
    background-color: white;
    position: absolute;
    top: -12px;
    left: 50px;
    display: block;
    text-align: center;
    color: black;
    font-weight: bold;
  }

  .nc-border-div-content {
    padding: 10px;
    min-height: 40px;
  }
}
</style>
<script setup="props, {content}" lang="ts">
import {inject, reactive, ref, unref} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {useMessage} from "/@/hooks/web/useMessage";
import {findByCode, findByName} from "/@/api/record/group/im-organize";
import {
  Select as ASelect,
  Input as AInput,
  DatePicker as ADatePicker,
  Checkbox as ACheckbox,
  Tree as ATree, Upload,
  Cascader,
  Form
} from 'ant-design-vue'

import {
  LoadingOutlined, PlusOutlined
} from '@ant-design/icons-vue';
import {importImg} from "/@/api/record/group/im-group";
import {findGroupVoucherTypes} from "/@/api/record/system/financial-settings";
import moment from "moment";
import {hasBlank} from "/@/api/task-api/tast-bus-api";

const Textarea = AInput.TextArea
const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
const {createMessage} = useMessage();
const emit = defineEmits(['register','save'])


const arr2=ref([
  {field:'name',label:'栏目名称'},
  {field:'name2',label:'栏目名称2'},
  {field:'name2',label:'栏目名称2'},
  {field:'name2',label:'栏目名称2'},
  {field:'name2',label:'栏目名称2'},
  {field:'name2',label:'栏目名称2'},
  {field:'name2',label:'栏目名称2'},
  {field:'name2',label:'栏目名称2'},
  {field:'name2',label:'栏目名称2'},
  {field:'name2',label:'栏目名称2'},
])
const formItems: any = ref({})

const psnList: any = ref([])

const groupInfoList = ref([])
const normList = ref([])
const levelList = ref([])
const industryTreeData = ref([])

const {createWarningModal} = useMessage();
const importLimit = (e) => {
  // 排除指定文本 /[^123456789\-]/g
  e.target.value = e.target.value.replace(/[^123456789]/g, "")
  if (!hasBlank(e.target.value)) e.target.value = e.target.value.split('').join('-')
  levelSuffix.value = e.target.value
}
const isStepOne = ref(true)

const focus1 = ref()
const focus2 = ref()
const focus3 = ref()
const focus4 = ref()
const focus5 = ref()
const focus6 = ref()
const focus7 = ref()
const focus8 = ref()
const focus9 = ref()
const focus10 = ref()
const focus11 = ref()
const focus12 = ref()
const focus13 = ref()
const focus14 = ref()
const focus15 = ref()
const focus16 = ref()
const focus17 = ref()
const focus18 = ref()
const focus19 = ref()
const focus20 = ref()
const focus21 = ref()
const focus22 = ref()
const focus23 = ref()
const focus24 = ref()

async function handleClose() {
  if (isStepOne.value) {
    return true;
  } else {
    isStepOne.value = true
    return false
  }
}

const isOrg = ref(false)

async function handleOk() {
  emit('save',getFieldsValue())
  closeModal()
}

const isEdit = ref(true)
const isLook = ref(false)
let changeBeforeModel: any = {}
const countryList = ref([])
const zoningList = ref([])
const industryList = ref([])
const pingZhengTypeList = ref([])
const upList = ref([])
const [register, {closeModal, setModalProps}] = useModalInner(params => {
  if(params==2){
    resetFields()
  }else{
    setFieldsValue(params.params)
  }
  // params.params
})


const resetForm = () => {
  formItems.value.id = ''
  formItems.value.uniqueCode = ''
  formItems.value.orgCode = ''
  formItems.value.orgName = ''
  formItems.value.orgSimpName = ''
  formItems.value.uniqueGroupCode = ''
  formItems.value.orgSuperior = ''
  formItems.value.industryclassCode = []
  formItems.value.uniqueCodeZone = []
  formItems.value.countryId = '0001'
  formItems.value.createDate = ''
  formItems.value.personInCharge = ''
  formItems.value.uniqueAccStandard = ''
  formItems.value.telephone = ''
  formItems.value.address = ''
  formItems.value.ccodeLevel = ''
  formItems.value.remarks = ''

  formItems.value.numberDec = '2'
  formItems.value.unitPriceDec = '2'
  formItems.value.rateDec = '5'
  formItems.value.accvouchDec = '4'
  formItems.value.periodNum = '12'
  formItems.value.voucherTypeNum = ''
  formItems.value.voucherTypeOtherNums = []
  formItems.value.yearStartDate = ''
  formItems.value.yearEndDate = ''

  formItems.value.beiyong1 = ''
  formItems.value.beiyong2 = ''
  imageUrl.value = ''
  levelPrefix.value = ''
  levelSuffix.value = ''
  isStepOne.value = true
}

async function checkCode() {
  if ((changeBeforeModel._value.orgCode != undefined && changeBeforeModel._value.orgCode != '') || changeBeforeModel._value.orgCode == formItems.value.orgCode) {
    return true
  }
  const res = await findByCode(formItems.value)
  if (res != 0) {
    createWarningModal({content: '组织代码已存在，请重新输入！'})
    formItems.value.orgCode = ''
    return false
  }
  return true
}

async function checkName() {
  if ((changeBeforeModel._value.orgName != undefined && changeBeforeModel._value.orgName != '') || changeBeforeModel._value.orgName == formItems.value.orgName) {
    return true
  }
  const res = await findByName(formItems.value)
  if (res != 0) {
    createWarningModal({content: '组织名称已存在，请重新输入！'})
    formItems.value.orgName = ''
    return false
  }
  return true
}

const levelPrefix = ref('')
const levelSuffix = ref('')
const standardChange = async (val) => {
  let obj = normList.value.filter(item => item.id == val)
  if (obj.length > 0) {
    levelPrefix.value = obj[0].tjici/*.replace(/-/g,"")*/
    if (formItems.value.ccodeLevel != '' && isEdit.value) {
      levelSuffix.value = formItems.value.ccodeLevel.replace(obj[0].tjici, '').replace(/-/g,"").split('').join('-')
    }
  }
}
const splitLevel = (a, b) => {
  let c = a
  if (b != '')
    c = c + '-' + b
  return c
}
// 图片
const fileList = ref([])

function getBase64(file: File, callback: (base64Url: string) => void) {
  const reader = new FileReader();
  reader.readAsDataURL(file);
  reader.onload = () => callback(reader.result as string);
}

const loading = ref<boolean>(false);
const imageUrl = ref<string>('');
const beforeUpload = (file) => {
  const isLt5M = file.size / 1024 / 1024 < 0.2;
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
  if (!isLt5M) {
    createWarningModal({content: '上传图片大小不能超过200kB!'})
    return false;
  }
  if (!isJPG) {
    createWarningModal({content: '上传图片格式是jpg/jpeg/png!'})
    return false;
  }
  new Promise(async function (resolve, reject) {
    let width = 280;
    let height = 80;
    let _URL = window.URL || window.webkitURL;
    let img = new Image();
    img.onload = function () {
      let valid = img.width > width && img.height > height;
      valid ? resolve() : reject();
    }
    img.src = _URL.createObjectURL(file);
  }).then(() => {
    createWarningModal({title: '温馨提示', content: '上传图片不得超出宽高 (280px*80px) 像素限制!'})
  }).catch(() => {
    // 默认存储最新单文件
    if (fileList.value.length > 0) fileList.value = []
    fileList.value = [...fileList.value, file]
    getBase64(file, (base64Url: string) => {
      imageUrl.value = base64Url;
      loading.value = false;
    });
  })
  return false;
}
const dateChange = () => {
  if (formItems.value.yearStartDate != null) {
    let startDate = (formItems.value.yearStartDate.format('YYYY-MM-DD'))
    let d2 = new Date(startDate);
    d2.setFullYear(d2.getFullYear() + 1);
    d2.setDate(d2.getDate() - 1);
    formItems.value.yearEndDate = moment(d2)
    formItems.value.yearStartDate = moment(startDate)
  } else {
    formItems.value.yearEndDate = ''
    formItems.value.yearEndDate = ''
  }
}

//
// const useForm = Form.useForm;
// const { resetFields, validate, validateInfos } = useForm(
//   modelRef,
//   reactive({
//     name: [
//       {
//         required: true,
//         message: 'Please input name',
//       },
//     ],
//     'sub.name': [
//       {
//         required: true,
//         message: 'Please input sub name',
//       },
//     ],
//   }),
// );
import { BasicForm, useForm } from '/@/components/Form/index'
import {accountFormSchema} from "/@/views/boozsoft/system/department/account.data";
const recordData=inject(['recordData'])
const [registerForm, { getFieldsValue,setFieldsValue, updateSchema, resetFields, validate }] = useForm({
  labelWidth: 100,
  schemas: recordData.value.columns.map(item=>{
    return {
      field:item.field,
      label: item.label,
      component: 'Input',
    }
  }),
  showActionButtonGroup: false,
  actionColOptions: {
    span: 23
  }
})

</script>
