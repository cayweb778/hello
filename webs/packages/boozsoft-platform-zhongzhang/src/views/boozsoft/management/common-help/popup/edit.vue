<template>
  <BasicModal width="940px" v-bind="$attrs" title="集团"
              @ok="handleOk()" :showOkBtn="!isLook" :showCancelBtn="true"
              :cancel-text="isLook?'关闭':'取消'" :canFullscreen="false" @register="register">
    <div class="nc-open-content" :class="isLook?'nc-open-content-look':''" style="height: 100%">
      <div style="display: inline-flex;justify-content: space-between;width: 710px;">
        <div style="width: 600px;">
          <a-input @blur="checkName()"
                   v-model:value="formItems.groupName"
                   placeholder="集团名称"
                   style="margin-left: 3%;font-size: 20px;width: 540px;border-color: black"
                   @keydown.enter.native="$refs.focus2.focus()"
          />
          <span class="red_span">⋆</span>
        </div>
        <!--    文件上传    -->
        <Upload
          list-type="picture-card"
          accept=".png,.jpeg,.jpg"
          :file-list="fileList"
          :show-upload-list="false"
          :before-upload="beforeUpload"
        >
          <img v-if="imageUrl" :src="imageUrl" alt="avatar"/>
          <div v-else>
            <LoadingOutlined v-if="loading"></LoadingOutlined>
            <PlusOutlined v-else></PlusOutlined>
            <img v-else :src="'@/assets/images/camera.png'">
          </div>
        </Upload>
      </div>
      <br/>
      <label>代码</label>
      <a-input v-model:value="formItems.groupCode" @blur="checkCode()" ref="focus2"
               @keydown.enter.native="$refs.focus3.focus()" placeholder="一位数字字母组合"
               style="width: 32%"/>
      <span class="red_span">⋆</span>
      <label>所属品牌</label>
      <a-input v-model:value="formItems.brand" placeholder="" ref="focus3"
               @keydown.enter.native="$refs.focus4.focus()" style="width: 32%"/>
      <br/>
      <label>所属行业</label>
      <Cascader
        ref="focus4"
        @keydown.enter.native="$refs.focus5.focus()"
        v-model:value="formItems.industryclassCode"
        :options="industryList"
        placeholder="门类/大类/中类/小类"
        change-on-select
        style="width: 32%;"
      />
      <span class="red_span"></span>
      <label>行政区划</label>
      <Cascader
        ref="focus5"
        @keydown.enter.native="$refs.focus6.focus()"
        v-model:value="formItems.uniqueCodeZone"
        :options="zoningList"
        placeholder="省/市/区"
        style="width: 32%;border: none"
      />
      <br/>
      <label>国家(地区)</label>
      <a-select
        ref="focus6"
        @keydown.enter.native="$refs.focus7.focus()"
        v-model:value="formItems.countryId"
        show-search
        placeholder=""
        style="width: 32%;border: none"
        allow-clear
      >
        <a-select-option
          v-for="psn in countryList"
          :key="psn.id"
          :value="psn.uniqueCode"
        >{{ psn.namech }}
        </a-select-option>
      </a-select>
      <span class="red_span"></span>
      <label>成立日期</label>
      <a-date-picker v-model:value="formItems.createDate" format="YYYY-MM-DD" style="width: 32%;"
                     ref="focus7"
                     @keydown.enter.native="$refs.focus8.focus()"/>
      <span class="red_span">⋆</span>
      <br/>
      <label>负责人</label>
      <a-input v-model:value="formItems.contacts" placeholder="" style="width: 32%" ref="focus8"
               @keydown.enter.native="$refs.focus9.focus()"/>
      <span class="red_span"></span>
      <label>联系电话</label>
      <a-input v-model:value="formItems.telephone" placeholder="" style="width: 32%" ref="focus9"
               @keydown.enter.native="$refs.focus10.focus()"/>
      <br/>
      <label>地址</label>
      <a-input v-model:value="formItems.address" placeholder="" style="width: 32%" ref="focus10"
               @keydown.enter.native="$refs.focus11.focus()"/>
      <span class="red_span"></span>
      <label>官网</label>
      <a-input v-model:value="formItems.website" placeholder="" style="width: 32%" ref="focus11"
               @keydown.enter.native="$refs.focus12.focus()"/>
      <br/>
      <label>集团性质</label>
      <a-select
        v-model:value="formItems.icorp"
        placeholder="公司或企业/行政事业、民间非盈利和非企业"
        style="width: 32%;"
        allow-clear
        ref="focus12"
        @keydown.enter.native="$refs.focus13.focus()"
      >
        <a-select-option
          key="0"
          value="1"
        >公司或企业
        </a-select-option>
        <a-select-option
          key="1"
          value="2"
        >行政事业、民间非盈利和非企业
        </a-select-option>
      </a-select>
      <span class="red_span">⋆</span>
      <br/>
      <label>简介</label>
      <a-input v-model:value="formItems.remarks" style="vertical-align: text-top;width: 80%;"
               ref="focus13"/>
    </div>
  </BasicModal>
</template>
<!--<style src="../../../../../assets/styles/system/model-form.less" lang="less" scope />-->
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
    padding: 5px 10px;
    margin-left: 15px;
  }

  .red_span {
    color: red;
    font-weight: bold;
    display: inline-block;
    width: 20px;
    text-align: right;
  }
}
</style>
<script setup="props, {content}" lang="ts">
import {ref, unref} from 'vue';
import {BasicModal, useModalInner} from '/@/components/Modal';
import {useMessage} from "/@/hooks/web/useMessage";
import {findByCode, findByName, importImg} from "/@/api/record/group/im-group";

import {
  LoadingOutlined, PlusOutlined
} from '@ant-design/icons-vue';
import {
  Select as ASelect,
  Input as AInput,
  DatePicker as ADatePicker,
  Cascader,
  Upload, message
} from 'ant-design-vue'

const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option

const {createMessage} = useMessage();
const emit = defineEmits(['register'])

const formItems: any = ref({})

const psnList: any = ref([])

const {createWarningModal} = useMessage();

async function handleOk() {
  // 必填项不能为空
  let r = /^[a-zA-Z0-9]+$/g;
  if (formItems.value.groupName == '') {
    createWarningModal({title: '温馨提示', content: '集团名称为必填项！'})
  } else if (formItems.value.groupCode == '') {
    createWarningModal({title: '温馨提示', content: '集团编码为必填项！'})
  } else if (formItems.value.groupCode.length != 1) {
    createWarningModal({title: '温馨提示', content: '集团编码长度只能为一位且只能为数字或字母！'})
  } else if (!r.test(formItems.value.groupCode)) {
    createWarningModal({title: '温馨提示', content: '集团编码长度只能为一位且只能为数字或字母！'})
  } else if (formItems.value.groupCode == '') {
    createWarningModal({title: '温馨提示', content: '集团编码为必填项！'})
  } else if (formItems.value.icorp == '') {
    createWarningModal({title: '温馨提示', content: '集团启用日期为必选项！'})
  } else if (formItems.value.icorp == '') {
    createWarningModal({title: '温馨提示', content: '集团性质为必选项！'})
  } else {
    formItems.value.industryclassCode = formItems.value.industryclassCode.length !== 0 ? JSON.stringify(formItems.value.industryclassCode) : ''
    formItems.value.uniqueCodeZone = formItems.value.uniqueCodeZone.length !== 0 ? JSON.stringify(formItems.value.uniqueCodeZone) : ''
    if (fileList.value.length > 0) {
      // 上传图片得到路径
      const formData = new FormData();
      fileList.value.forEach((file) => {
        formData.append('file', file);
      });
      formData.append('oldPath', formItems.value.beiyong2);
      let result = await importImg(formData)
      formItems.value.beiyong1 = result;
    }
    emit('save', unref(formItems.value))
    closeModal()
  }
}
const focus1=ref()
const focus2=ref()
const focus3=ref()
const focus4=ref()
const focus5=ref()
const focus6=ref()
const focus7=ref()
const focus8=ref()
const focus9=ref()
const focus10=ref()
const focus11=ref()
const focus12=ref()
const focus13=ref()

const isEdit = ref(true)
const isLook = ref(false)
let changeBeforeModel: any = {}

// 所属行业
const industryList = ref([])
// 行政区划
const zoningList = ref([])
// 国家
const countryList = ref([])
const [register, {closeModal, setModalProps}] = useModalInner(async ({data}) => {
  isEdit.value = data.isEdit
  isLook.value = data.isLook
  isLook.value = data.isLook
  countryList.value = data.countryList
  zoningList.value = data.zoningList
  industryList.value = data.industryList
  data.countryList = []
  data.zoningList = []
  data.industryList = []
  formItems.value = data
  formItems.value.industryclassCode = formItems.value.industryclassCode != '' ? JSON.parse(formItems.value.industryclassCode) : []
  formItems.value.uniqueCodeZone = formItems.value.uniqueCodeZone != '' ? JSON.parse(formItems.value.uniqueCodeZone) : []
  if (formItems.value.beiyong1 != null && formItems.value.beiyong1.length > 22) imageUrl.value = formItems.value.beiyong1
  if (!isEdit.value) {
    resetForm()
  }
  changeBeforeModel = JSON.parse(JSON.stringify(formItems))
  setModalProps({minHeight: 330});
})

const resetForm = () => {
  formItems.value.id = ''
  formItems.value.uniqueCode = ''
  formItems.value.groupCode = ''
  formItems.value.groupName = ''
  formItems.value.brand = ''
  formItems.value.industryclassCode = []
  formItems.value.uniqueCodeZone = []
  formItems.value.countryId = '0001'
  formItems.value.createDate = ''
  formItems.value.contacts = ''
  formItems.value.telephone = ''
  formItems.value.address = ''
  formItems.value.website = ''
  formItems.value.icorp = ''
  formItems.value.remarks = ''
  formItems.value.beiyong1 = ''
  imageUrl.value = ''
}

async function checkCode() {
  if ((changeBeforeModel._value.projectCode != undefined && changeBeforeModel._value.projectCode != '') || changeBeforeModel._value.projectCode == formItems.value.projectCode) {
    return true
  }
  const res = await findByCode(formItems.value)
  if (res != 0) {
    createWarningModal({title: '温馨提示', content: '集团代码已存在，请重新输入！'})
    formItems.value.groupCode = ''
    return false
  }
  return true
}

async function checkName() {
  if ((changeBeforeModel._value.projectName != undefined && changeBeforeModel._value.projectName != '') || changeBeforeModel._value.projectName == formItems.value.projectName) {
    return true
  }
  const res = await findByName(formItems.value)
  if (res != 0) {
    createWarningModal({title: '温馨提示', content: '集团名称已存在，请重新输入！'})
    formItems.value.groupName = ''
    return false
  }
  return true
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
  const isLt5M = file.size / 1024 / 1024 < 2;
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
  if (!isLt5M) {
    createWarningModal({title: '温馨提示', content: '上传图片大小不能超过200kB!'})
    return false;
  }
  if (!isJPG) {
    createWarningModal({title: '温馨提示', content: '上传图片格式是jpg/jpeg/png!'})
    return false;
  }
  new Promise(async function (resolve, reject) {
    let width = 280;
    let height = 80;
    let _URL = window.URL || window.webkitURL;
    let img = new Image();
    img.onload = function () {
      let valid = img.width > width || img.height > height;
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
</script>
