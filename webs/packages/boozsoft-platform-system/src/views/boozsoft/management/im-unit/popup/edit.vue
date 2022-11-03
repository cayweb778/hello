<template>
  <BasicModal width="940px" v-bind="$attrs"
              title="公司（单位）" @ok="handleOk()"
              :ok-text="isEdit?'开始修改':'开始新建'"
              :cancel-text="isLook?'关闭':'取消'"
              :showOkBtn="showNext" @register="register"
              :closeFunc="handleClose" :loading="modelLoadIng"
              :canFullscreen="false" :maskClosable="false" :loadingTip="loadingText">
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <PlusCircleOutlined v-if="!isEdit" style="font-size: 50px;color: #0096c7;"/>
        <FormOutlined v-else-if="isEdit && !isLook" style="font-size: 50px;color: #0096c7;"/>
        <FileSearchOutlined v-else style="font-size: 50px;color: #0096c7;"/>
        <span style="line-height:50px;font-size: 30px;color: #0096c7;">&ensp;公司（单位）</span>
      </div>
      <div style="display: inline-block;position:absolute;right: 50px;top: 20px;background:#ffffff">
        <img src="/@/assets/images/zt.png" style="height:100px;margin-right: 10px;"/>
      </div>
    </template>
    <div class="nc-open-content" style="height: 100%">
      <div :class="isLook?'nc-open-content-look':''">
        <a-radio-group name="radioGroup" v-model:value="formItems.independent"
                       :style="isEdit?{pointerEvents: 'none'}:''" style="margin-left: 12%;margin-top: 2%">
          <a-radio value="1" v-if="mark == 1003">独立单位</a-radio>
          <a-radio value="0">集团单位</a-radio>
        </a-radio-group>
        <br/>
        <div style="display: inline-flex;justify-content: space-between;width: 636px;margin:3%  15%">
          <div style="width: 640px;">
            <span style="font-size: 18px;font-weight: bold;">公司（单位）名称:</span>
            <a-input v-model:value="formItems.accName"
                     placeholder=""
                     style="margin-left: 3%;font-size: 20px;width: 440px;font-weight: bold;border-bottom: 3px #666666 solid !important;"
                     ref="focus1"
                     @keydown.enter.native="$refs.focus2.focus(),formItems.accNameCn = formItems.accName,checkName()"
            />
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
        <label>公司代码：</label>
        <a-input v-model:value="formItems.coCode" @blur="checkCode()" placeholder="二位数字字母组合"
                 style="width: 32%;text-align: center" ref="focus2"  :disabled="isUsed"
                 @keydown.enter.native="$refs.focus3.focus()"/>
        <span class="red_span">⋆</span>
        <label>简称：</label>
        <a-input v-model:value="formItems.accNameCn" @blur="checkName()" placeholder=""
                 style="width: 32%" ref="focus3"
                 @keydown.enter.native="$refs.focus4.focus()"/>
        <span class="red_span">⋆</span>
        <br/>
        <label>税号：</label>
        <a-input v-model:value="formItems.taxCode" placeholder="" style="width: 32%" ref="focus4"
                 @keydown.enter.native="$refs.focus5.focus()"/>
        <span class="red_span"></span>
        <label>国家(地区)：</label>
        <a-select
          v-model:value="formItems.countryId"
          show-search
          placeholder=""
          style="width: 32%;border: none;text-align: center"
          allow-clear
          ref="focus5" @keydown.enter.native="$refs.focus6.focus()"
        >
          <a-select-option
            v-for="psn in countryList"
            :key="psn.id"
            :value="psn.uniqueCode"
          >{{ psn.namech }}
          </a-select-option>
        </a-select>
        <br/>
        <label>所属集团组织：</label>
        <a-select
          v-model:value="formItems.accGroup"
          placeholder=""
          style="width: 32%;"
          allow-clear
          :disabled="!isOrg"
          ref="focus6" @keydown.enter.native="$refs.focus7.focus()"
        >
          <a-select-option v-for="(item,index) in organizeList"
                           :key="index"
                           :value="item.uniqueCode"
          >{{ item.orgName }}
          </a-select-option>
        </a-select>
        <span class="red_span"></span>
        <label>上级单位：</label>
        <a-select
          v-model:value="formItems.corpCode"
          show-search
          placeholder=""
          style="width: 32%;border: none"
          allow-clear
          ref="focus7" @keydown.enter.native="$refs.focus8.focus()"
        >
          <a-select-option v-for="(psn,index) in upList" :key="index" :value="psn.id">{{
              psn.accName
            }}
          </a-select-option>
        </a-select>
        <br/>
        <label>所属行业：</label>
        <Cascader
          v-model:value="formItems.industryclassCode"
          :options="industryList"
          placeholder="门类/大类/中类/小类"
          change-on-select
          style="width: 32%;"
          ref="focus8" @keydown.enter.native="$refs.focus9.focus()"
        />
        <span class="red_span"></span>
        <label>简介：</label>
        <a-input v-model:value="formItems.remarks" style="width: 32%;" ref="focus9"
                 @keydown.enter.native="$refs.focus10.focus()"/>
      </div>
      <Tabs type="card" size="small" style="margin-top: 5%">
        <TabPane key="1" tab="基础信息">
          <div :class="isLook?'nc-open-content-look':''">
            <label>行政区划：</label>
            <Cascader
              v-model:value="formItems.uniqueCodeZone"
              :options="zoningList"
              placeholder="省/市/区"
              style="width: 32%;border: none;text-align: center"
              ref="focus10" @keydown.enter.native="$refs.focus11.focus()"
            />
            <span class="red_span"></span>
            <label>通讯地址：</label>
            <a-input v-model:value="formItems.address" placeholder="" style="width: 32%"
                     ref="focus11"
                     @keydown.enter.native="$refs.focus12.focus()"/>
            <label>联系电话：</label>
            <a-input v-model:value="formItems.telephone" placeholder="" style="width: 32%"
                     ref="focus12"
                     @keydown.enter.native="$refs.focus13.focus()"/>
            <span class="red_span"></span>
            <label>联系人：</label>
            <a-input v-model:value="formItems.contacts" placeholder="" style="width: 32%"
                     ref="focus13"
                     @keydown.enter.native="$refs.focus14.focus()"/>
            <label>官网地址：</label>
            <a-input v-model:value="formItems.website" placeholder="" style="width: 32%"
                     ref="focus14"/>
          </div>
        </TabPane>
        <TabPane key="2" tab="发票信息">
          <div :class="isLook?'nc-open-content-look':''">
            <label>开票抬头全称：</label>
            <a-input v-model:value="formItems.invoiceLookUp" placeholder="" style="width: 32%"
                     @keydown.enter.native="$refs.focus15.focus()"/>
            <label>纳税人识别号：</label>
            <a-input v-model:value="formItems.invoiceIdentifier" placeholder="" style="width: 32%"
                     ref="focus15"
                     @keydown.enter.native="$refs.focus16.focus()"/>
            <span class="red_span"></span>
            <label>地址及电话：</label>
            <a-input v-model:value="formItems.invoiceAddressPhone" placeholder="" style="width: 32%"
                     ref="focus16"
                     @keydown.enter.native="$refs.focus17.focus()"/>
            <label>开户行及账户：</label>
            <a-input v-model:value="formItems.invoiceBanks" placeholder="" style="width: 32%"
                     ref="focus17"/>
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

  :deep(.ant-calendar-picker-input) {
    background: white;
    color: black;
  }
  :deep(.ant-tabs-tab-active){
    border-top: 2px #0096c7 solid;
  }

}
</style>
<script setup="props, {content}" lang="ts">
import {ref, unref, watch} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {useMessage} from "/@/hooks/web/useMessage";
import {checkUsed, findByCode, findByName} from "/@/api/record/group/im-unit";
import {
  Select as ASelect,
  Input as AInput,
  Radio as ARadio, Tabs,
  Cascader, Upload, message
} from 'ant-design-vue';
import {LoadingOutlined, PlusOutlined,  PlusCircleOutlined, FormOutlined, FileSearchOutlined} from '@ant-design/icons-vue';
import {hasBlank} from "/@/api/task-api/tast-bus-api";

const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
const ARadioGroup = ARadio.Group
const Textarea = AInput.TextArea
const TabPane = Tabs.TabPane

import {importImg} from "/@/api/record/group/im-group";

const {createMessage} = useMessage();
const emit = defineEmits(['register', 'save'])

const formItems: any = ref({})
const isOrg: any = ref(true) //是否集团

const loadingText = ref('请稍等...')
const {createWarningModal} = useMessage();

async function handleClose() {
  showNext.value = true
  return true
}


async function handleOk() {
  // 数字与字母
  let r = /^[a-zA-Z0-9]+$/g;
  if (formItems.value.accName == '') {
    createWarningModal({content: '公司（单位）名称为必填项！'})
  } else if (formItems.value.coCode == '') {
    createWarningModal({content: '公司（单位）编码为必填项！'})
  } else if (formItems.value.accNameCn == '') {
    createWarningModal({content: '公司（单位）简称为必填项！'})
  } else if (formItems.value.coCode.length != 2 || (!r.test(formItems.value.coCode))) {
    createWarningModal({content: '公司（单位）编码长度只能为二位且只能为数字或字母！'})
  } else if (formItems.value.independent == '') {
    createWarningModal({content: '公司（单位）类型为必选项！'})
  } else if (formItems.value.independent == '0' && formItems.value.accGroup == '') {
    createWarningModal({content: '公司（单位）所属组织为必选项！'})
  } else {
    // 当为独立账套时 必填
    showNext.value = false
    formItems.value.organizeList = []
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
    } else {
      if (isEdit.value && !hasBlank(formItems.value.beiyong1) && formItems.value.beiyong1.startsWith('data:image/png')) formItems.value.beiyong1 = formItems.value.beiyong2
    }
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

import {usePlatformsStore} from "/@/store/modules/platforms";
import {JsonTool, StrTool} from "/@/api/task-api/tools/universal-tools";

const mark = usePlatformsStore().getCurrentPlatformId
const modelLoadIng = ref(true)
const showNext = ref(true)
const isEdit = ref(true)
const isLook = ref(false)
const isUsed = ref(false)
const organizeList = ref([])

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
const zoningList = ref([])
const industryList = ref([])
const upList = ref([])
const [register, {closeModal, setModalProps}] = useModalInner(async ({data}) => {
  resetForm()
  isEdit.value = data.isEdit
  isLook.value = data.isLook
  organizeList.value = data.organizeList
  countryList.value = data.countryList
  zoningList.value = data.zoningList
  industryList.value = data.industryList
  upList.value = data.upList
  data.organizeList = []
  data.countryList = []
  data.zoningList = []
  data.industryList = []
  data.upList = []
  formItems.value = data
  formItems.value.industryclassCode = !hasBlank(formItems.value.industryclassCode) ? JSON.parse(formItems.value.industryclassCode) : []
  formItems.value.uniqueCodeZone = !hasBlank(formItems.value.uniqueCodeZone) ? JSON.parse(formItems.value.uniqueCodeZone) : []
  if (formItems.value.beiyong1 != null && formItems.value.beiyong1.length > 22) imageUrl.value = formItems.value.beiyong1
  changeBeforeModel = JSON.parse(JSON.stringify(formItems))
  setModalProps({minHeight: 500});
  modelLoadIng.value = false
  if (isLook.value) showNext.value = false
  if (isEdit.value && !isLook.value)checkDisabled()
})
const resetForm = () => {
  formItems.value.id = ''
  formItems.value.accId = ''
  formItems.value.accName = ''
  formItems.value.coCode = ''
  formItems.value.accGroup = ''
  formItems.value.accNameCn = ''
  formItems.value.industryclassCode = []
  formItems.value.uniqueCodeZone = []
  formItems.value.corpCode = ''
  formItems.value.countryId = '0001'
  formItems.value.taxCode = ''
  formItems.value.contacts = ''
  formItems.value.telephone = ''
  formItems.value.address = ''
  formItems.value.website = ''
  formItems.value.remarks = ''
  formItems.value.independent = '0'
  formItems.value.accStandard = ''
  formItems.value.ccodeLevel = ''
  formItems.value.ibudgetAccStandard = ''
  formItems.value.numberDec = '2'
  formItems.value.unitPriceDec = '2'
  formItems.value.rateDec = '5'
  formItems.value.accvouchDec = '4'
  formItems.value.periodNum = '12'
  formItems.value.currency = 'CNY'
  formItems.value.currencyName = '人民币'
  formItems.value.currencyCh = 'RMB'
  formItems.value.voucherTypeNum = ''
  formItems.value.voucherTypeOtherNums = []
  formItems.value.yearStartDate = ''
  formItems.value.yearEndDate = ''
  formItems.value.startPeriod = ''
  formItems.value.nvoiceLookUp = '',formItems.value.invoiceIdentifier = '',formItems.value.invoiceAddressPhone ='',formItems.value.invoiceBanks=''
  imageUrl.value = ''
}

async function checkCode() {
  formItems.value.coCode = formItems.value.coCode.trim()
  if ((changeBeforeModel._value.coCode != undefined && changeBeforeModel._value.coCode != '') || changeBeforeModel._value.coCode == formItems.value.coCode) {
    return true
  }
  const res = await findByCode(formItems.value)
  if (res != 0) {
    createWarningModal({content: '公司代码已存在，请重新输入！'})
    formItems.value.coCode = ''
    return false
  }
  return true
}

async function checkName() {
  formItems.value.accName = StrTool.closeSpecialChars3(formItems.value.accName)
  formItems.value.accNameCn = StrTool.closeSpecialChars3(formItems.value.accNameCn)
  if ((changeBeforeModel._value.accNameCn != undefined && changeBeforeModel._value.accNameCn != '') || changeBeforeModel._value.accNameCn == formItems.value.accNameCn) {
    return true
  }
  const res = await findByName(formItems.value)
  if (res != 0) {
    createWarningModal({content: '公司简称已存在，请重新输入！'})
    formItems.value.accNameCn = ''
    return false
  }
  return true
}

watch(
  () => formItems.value.independent,
  () => {
    if (formItems.value.independent != '1') { // 集团
      if (organizeList.value.length > 0) {
        isOrg.value = true
        formItems.value.accGroup = organizeList.value[0].uniqueCode
      } else {
        createWarningModal({content: '暂无组织数据，请先建立组织数据后在进行该操作！'})
        formItems.value.independent = '1'
      }
    } else {     //等于独立账套
      isOrg.value = false
      if (!isEdit.value) {
        formItems.value.accGroup = ''
      }
    }
  }
);

interface MockData {
  key: string;
  title: string;
  description: string;
  disabled: boolean;
}

const mockData: MockData[] = [];
for (let i = 0; i < 20; i++) {
  mockData.push({
    key: i.toString(),
    title: `content${i + 1}`,
    description: `description of content${i + 1}`,
    disabled: i % 3 < 1,
  });
}
const oriTargetKeys = mockData.filter(item => +item.key % 3 > 1).map(item => item.key);
const targetKeys = ref<string[]>(oriTargetKeys);

const selectedKeys = ref<string[]>(['1', '4']);

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

async function checkDisabled(){
  if (!hasBlank(formItems.value.id) && !hasBlank(formItems.value.coCode)){
    isUsed.value = false
    let res =  await checkUsed({id: formItems.value.id})
    if (null == res.isUsed || res.isUsed){
      isUsed.value = true
    }
  }
}
</script>
