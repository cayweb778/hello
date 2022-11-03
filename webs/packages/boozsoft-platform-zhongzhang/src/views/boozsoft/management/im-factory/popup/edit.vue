<template>
  <BasicModal width="940px" v-bind="$attrs"
              title="工厂信息" @ok="handleOk()"
              :ok-text="isEdit?'保存':'开始新建'"
              :cancel-text="isLook?'关闭':!isEdit?'放弃':'取消'"
              :showOkBtn="showNext" @register="register"
              :closeFunc="handleClose" :loading="modelLoadIng"
              :canFullscreen="false" :maskClosable="false" :loadingTip="loadingText">
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <PlusCircleOutlined v-if="!isEdit" style="font-size: 50px;color: #0096c7;"/>
        <FormOutlined v-else-if="isEdit && !isLook" style="font-size: 50px;color: #0096c7;"/>
        <FileSearchOutlined v-else style="font-size: 50px;color: #0096c7;"/>
        <span style="line-height:50px;font-size: 30px;color: #0096c7;">&ensp;工厂</span>
      </div>
      <div style="display: inline-block;position:absolute;right: 50px;top: 20px;background:#ffffff">
        <img src="/@/assets/images/zt.png" style="height:100px;margin-right: 10px;"/>
      </div>
    </template>
    <div class="nc-open-content" style="height: 100%">
      <div :class="isLook?'nc-open-content-look':''">
        <RadioGroup name="radioGroup" v-model:value="formItems.status"
                    style="margin-left: 12%;margin-top: 2%;">
          <Radio value="1">可用</Radio>
          <Radio value="0">不可用</Radio>
        </RadioGroup>
        <br/>
        <div
          style="display: inline-flex;justify-content: space-between;width: 624px;margin:3%  15%;">
          <div style="width: 600px;">

            <span style="font-size: 18px;margin-left: 20px;font-weight: bold;">工厂名称:</span>
            <Input v-model:value="formItems.plantNamefull"
                     placeholder=""
                     style="margin-left: 3%;font-size: 20px;width: 380px;border-color: black !important;border-width: 2px !important;"
                     @keydown.enter.native="$refs.focus1.focus(),formItems.plantNamej = formItems.plantNamefull,checkName()"/>
            <span class="red_span">⋆</span>
          </div>
          <!--    文件上传    -->
<!--          <Upload
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
              <img v-else src="../../../../../assets/images/camera.png">
            </div>
          </Upload>-->
        </div>
        <br/>
        <label>工厂代码：</label>
        <Input v-model:value="formItems.plantCode" @focus="checkInput" @blur="checkCode()" placeholder="公司代码-四位工厂码"
                 style="width: 30%;text-align: center" ref="focus1"
                 @keydown.enter.native="$refs.focus2.focus()"/>
        <span class="red_span">⋆</span>
        <label>简称：</label>
        <Input v-model:value="formItems.plantNamej" @blur="checkName()" placeholder=""
                 style="width: 30%" ref="focus2"
                 @keydown.enter.native="$refs.focus3.focus()"/>
        <span class="red_span">⋆</span>
        <br/>
        <label>所属公司：</label>
        <Select
          v-model:value="formItems.corpUniqueCode"
          show-search
          placeholder=""
          style="width: 30%;border: none"
          allow-clear
          @change="unitChange"
          ref="focus3" @keydown.enter.native="$refs.focus4.focus()"
        >
          <SelectOption v-for="(psn,index) in upList" :key="index" :value="psn.id">{{
              psn.coCode+' '+psn.accNameCn
            }}
          </SelectOption>
        </Select>
        <span class="red_span">⋆</span>
        <label>工厂类型：</label>
        <Select
          v-model:value="formItems.plantClass"
          show-search
          placeholder=""
          style="width: 30%;border: none"
          allow-clear
          ref="focus4" @keydown.enter.native="$refs.focus5.focus()"
        >
          <SelectOption value="1">自有工厂</SelectOption>
          <SelectOption value="2">联营工厂</SelectOption>
          <SelectOption value="3">代工工厂</SelectOption>
        </Select>
        <span class="red_span">⋆</span>
        <label>国家(地区)：</label>
        <Select
          v-model:value="formItems.countryRegion"
          show-search
          placeholder=""
          style="width: 30%;border: none;text-align: center"
          allow-clear
          ref="focus5" @keydown.enter.native="$refs.focus6.focus()"
        >
          <SelectOption
            v-for="psn in countryList"
            :key="psn.id"
            :value="psn.uniqueCode"
          >{{ psn.namech }}
          </SelectOption>
        </Select>
        <span class="red_span"></span>
        <label>城市&邮政编码：</label>
        <Input v-model:value="formItems.cityPostcode" placeholder=""
                 style="width: 30%" ref="focus6"
                 @keydown.enter.native="$refs.focus7.focus()"/>
        <span class="red_span"></span>
        <br/>
        <label>工厂注释：</label>
        <Input v-model:value="formItems.plantNotes" style="width: 78%;" ref="focus7"/>
      </div>
      <Tabs type="card" size="small" style="margin-top: 2%">
        <TabPane key="1" tab="详细信息">
          <div :class="isLook?'nc-open-content-look':''">
            <label>收/送货地址：</label>
            <Input v-model:value="formItems.addr" placeholder="" style="width: 32%" ref="focus8"
                     @keydown.enter.native="$refs.focus9.focus()"/>
            <label>联系人及电话：</label>
            <Input v-model:value="formItems.contact" placeholder="" style="width: 32%"
                     ref="focus9"
                     @keydown.enter.native="$refs.focus10.focus()"/>
            <span class="red_span"></span>
            <label>全球管理代码：</label>
            <Input v-model:value="formItems.globalCode" placeholder="三十位随机码" :disabled="true" style="width: 32%"/>
            <label>地球经纬度：</label>
            <Input v-model:value="formItems.longitLatitu" placeholder="" style="width: 32%"
                     ref="focus10" @keydown.enter.native="$refs.focus11.focus()"/>
            <label>时区：</label>
            <Select
              v-model:value="formItems.timeZone"
              placeholder=""
              style="width: 32%;border: none;text-align: center"
              ref="focus11">
              <SelectOption v-for="it in generateTimeZoneList()" :value="it.value">{{it.label}}</SelectOption>
            </Select>
          </div>
        </TabPane>
      </Tabs>
    </div>
  </BasicModal>
</template>
<style lang="less" scoped>
@import '/@/assets/styles/part-open.less';

:deep(.ant-calendar-picker-input.ant-input),
:deep(.ant-select-single:not(.ant-select-customize-input)
  .ant-select-selector
  .ant-select-selection-search-input), :deep(.ant-picker), :deep(.ant-input-number) {
  border: none;
  width: 100%;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  text-align: center;
}

:deep(.ant-form-item-control button) {
  display: none;
}

:deep(.ant-input-number-input) {
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

:deep(.ant-calendar-picker-input) {
  text-align: center;
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
  color: #666666;

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
    width: 130px;
    display: inline-block;
    padding: 6px 10px;
    margin-left: 15px;
    font-weight: bold;
    color: #666666;
  }

  .red_span {
    color: red;
    font-weight: bold;
    display: inline-block;
    width: 20px;
    text-align: right;
  }
  :deep(.ant-tabs-tab-active){
    border-top: 2px #0096c7 solid;
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
  border: 1px solid #bfbfbf;
  margin: 3% 0;

  .nc-border-div-span {
    min-width: 80px;
    background-color: white;
    position: absolute;
    top: -12px;
    left: 50px;
    display: block;
    text-align: center;
    color: #4f4f4f;
    font-size: 12px;
  }

  .nc-border-div-content {
    padding: 10px;
    min-height: 40px;
  }


}
</style>
<script setup="props, {content}" lang="ts">
import {ref, unref, watch} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {useMessage} from "/@/hooks/web/useMessage";
import {findByCode, findByName} from "/@/api/record/group/im-plant";
import {Select, Input, Radio, Tabs, Upload} from 'ant-design-vue';
import {LoadingOutlined, PlusOutlined,FileSearchOutlined,FormOutlined,PlusCircleOutlined} from '@ant-design/icons-vue';
import {hasBlank} from "/@/api/task-api/tast-bus-api";

const SelectOption = Select.Option
const RadioGroup = Radio.Group
const Textarea = Input.TextArea
const TabPane = Tabs.TabPane
import {importImg} from "/@/api/record/group/im-group";

const {createMessage} = useMessage();
const emit = defineEmits(['register', 'save'])
const formItems: any = ref({})
const loadingText = ref('请稍等...')
const {createWarningModal} = useMessage();

async function handleClose() {
  showNext.value = true
  resetForm()
  return true
}
async function handleOk() {
  // 数字与字母
  let r = /^[a-zA-Z0-9\-]+$/g;
  if (hasBlank(formItems.value.plantNamefull)) {
    createWarningModal({content: '工厂全称为必填项！'})
  } else if (hasBlank(formItems.value.plantCode)) {
    createWarningModal({content: '工厂代码为必填项！'})
  } else if (!formItems.value.plantCode.startsWith(theCoCode.value) || (formItems.value.plantCode.indexOf('-') != formItems.value.plantCode.lastIndexOf('-'))) {
    createWarningModal({content: '工厂代码不符合【"两位公司代码" + "-" + "四位工厂码"】的命名规则！'})
  } else if (formItems.value.plantCode.length != 7 || (!r.test(formItems.value.plantCode))) {
    createWarningModal({content: '工厂代码长度只能为七位且只能为数字、字母和-！'})
  }  else if (hasBlank(formItems.value.plantNamej )) {
    createWarningModal({content: '工厂简称为必填项！'})
  } else if (hasBlank(formItems.value.plantClass)) {
    createWarningModal({content: '工厂类型为必选项！'})
  } else {
    // 当为独立账套时 必填
    showNext.value = false
    if (fileList.value.length > 0) {
      // 上传图片得到路径
      const formData = new FormData();
      fileList.value.forEach((file) => {
        formData.append('file', file);
      });
      formData.append('oldPath', formItems.value.beiyong2);
      let result = await importImg(formData)
      formItems.value.beiyong1 = result;
    } else {
      if (isEdit.value && !hasBlank(formItems.value.beiyong1) && formItems.value.beiyong1.startsWith('data:image/png')) formItems.value.beiyong1 = formItems.value.beiyong2
    }
    console.log(unref(formItems.value))
    modelLoadIng.value = true
    emit('save', {
      closeOpen() {
        showNext.value = true
        modelLoadIng.value = false
        closeModal()
      },
      data: unref(formItems.value)
    })
  }
  return false;
}

import { StrTool} from "/@/api/task-api/tools/universal-tools";
import {generateTimeZoneList} from "/@/views/boozsoft/management/im-factory/time-zone";

const modelLoadIng = ref(true)
const showNext = ref(true)
const isEdit = ref(true)
const isLook = ref(false)

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
let changeBeforeModel: any = {}
const countryList = ref([])
const upList = ref([])
const [register, {closeModal, setModalProps}] = useModalInner(async ({data}) => {
  resetForm()
  isEdit.value = data.isEdit
  isLook.value = data.isLook
  countryList.value = data.countryList
  upList.value = data.unitList
  data.countryList = []
  data.unitList = []
  formItems.value = data
  if (hasBlank(formItems.value.globalCode))formItems.value.globalCode =StrTool.randomCode(30)
  if (formItems.value.beiyong1 != null && formItems.value.beiyong1.length > 22) imageUrl.value = formItems.value.beiyong1
  changeBeforeModel = JSON.parse(JSON.stringify(formItems))
  setModalProps({minHeight: 470});
  modelLoadIng.value = false
  if (isLook.value) showNext.value = false
})

const resetForm = () => {
  formItems.value.id = ''
  formItems.value.plantNamefull = ''
  formItems.value.corpUniqueCode = ''
  formItems.value.plantCode = ''
  formItems.value.plantNamej = ''
  formItems.value.plantClass = ''
  formItems.value.countryRegion = ''
  formItems.value.cityPostcode = ''
  formItems.value.plantNotes = ''
  formItems.value.addr = ''
  formItems.value.contact = ''
  formItems.value.globalCode = ''
  formItems.value.longitLatitu = ''
  formItems.value.timeZone = ''
  formItems.value.status = '1'
  imageUrl.value = ''
}
const theCoCode = ref('')
const unitChange = () => {
  if (hasBlank(formItems.value.corpUniqueCode)){
    formItems.value.plantCode = ''
    theCoCode.value = ''
  }else {
    let a =  upList.value.filter(it=>it.id == formItems.value.corpUniqueCode)[0].coCode
    theCoCode.value = a+ '-'
    formItems.value.plantCode = a + '-'
  }
}
async function checkInput(){
  if (hasBlank(formItems.value.corpUniqueCode)){
    createWarningModal({content: '请先选择所属公司，后进行补充输入代码！'})
    focus3.value.focus()
  }
}

async function checkCode() {
  formItems.value.plantCode = formItems.value.plantCode.trim()
  if ((changeBeforeModel._value.plantCode != undefined && changeBeforeModel._value.plantCode != '') || changeBeforeModel._value.plantCode == formItems.value.plantCode) {
    return true
  }
  const res = await findByCode({id: formItems.value.id,checkVal:formItems.value.plantCode })
  if (res != 0) {
    createWarningModal({content: '工厂代码已存在，请重新输入！'})
    formItems.value.plantCode = ''
    return false
  }
  return true
}


async function checkName() {
  formItems.value.plantNamefull = StrTool.closeSpecialChars3(formItems.value.plantNamefull)
  formItems.value.plantNamej = StrTool.closeSpecialChars3(formItems.value.plantNamej)
  if ((changeBeforeModel._value.plantNamej != undefined && changeBeforeModel._value.plantNamej != '') || changeBeforeModel._value.plantNamej == formItems.value.plantNamej) {
    return true
  }
  const res = await findByName({id: formItems.value.id,checkVal:formItems.value.plantNamej })
  if (res != 0) {
    createWarningModal({content: '工厂简称已存在，请重新输入！'})
    formItems.value.plantNamej = ''
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
