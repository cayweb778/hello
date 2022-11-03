<template>
  <div class="app-container">
    <div class="app-container-head">
      <AppstoreOutlined style="font-size: 36px;color: #0096c7;" class="container-head-img"/>
      <div class="container-head-title" style="margin-top: 13px;">
        <b class="noneSpan">集团（机构）信息</b>
      </div>
      <div class="ant-btn-group" data-v-a1ccd506="" style="float: right">
        <button type="button" class="ant-btn ant-btn-me"
                ant-click-animating-without-extra-node="false" v-if="isLook" @click="isLook=false">
          <span>编辑</span></button>
        <button type="button" class="ant-btn ant-btn-me"
                ant-click-animating-without-extra-node="false" v-if="!isLook" @click="handleOk">
          <span>保存</span></button>
        <button type="button" class="ant-btn ant-btn-me"
                ant-click-animating-without-extra-node="false" v-if="!isLook" @click="isLook=true">
          <span>放弃</span></button>
        <button type="button" class="ant-btn ant-btn-me"
                @click="closeCurrent()">
          退出
        </button>
      </div>
    </div>
    <div class="nc-open-content" :class="isLook?'nc-open-content-look':''"
         :style="{height:windowHeight+'px'}">
      <div
        style="display: inline-flex;justify-content: space-between;width: 710px;margin-bottom: 5%">
        <div style="width: 600px;padding-top: 2%;">
          <span style="font-size: 18px;font-weight: bold">集团（机构）名称：</span>
          <a-input @blur="checkName()"
                   v-model:value="formItems.groupName"
                   placeholder="集团名称"
                   style="margin-left: 3%;font-size: 22px;width: 400px;color: black;border-bottom: 3px #666666 solid !important;"
                   @keydown.enter.native="$refs.focus1.focus()"
          />
          <span class="red_span"><template v-if="!isLook">⋆</template></span>
        </div>
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
            <img v-else src="../../../../assets/images/camera.png">
          </div>
        </Upload>
      </div>
      <br/>
      <!--      <label>代码：</label>
            <a-input v-model:value="formItems.groupCode" @blur="checkCode()" ref="focus2"
                     @keydown.enter.native="$refs.focus3.focus()" placeholder="一位数字字母组合"
                     style="width: 32%;text-align: center;"/>
            <span class="red_span"><template v-if="!isLook"> ⋆</template></span>-->
      <label>成立日期：</label>
      <a-date-picker v-model:value="formItems.createDate" format="YYYY-MM-DD"
                     value-format="YYYY-MM-DD" style="width: 32%;text-align-last: center;"
                     ref="focus1"
                     @keydown.enter.native="$refs.focus2.focus()"/>
      <span class="red_span"><template v-if="!isLook"> ⋆</template></span>
      <label>所属品牌：</label>
      <a-input v-model:value="formItems.brand" placeholder="" ref="focus2"
               @keydown.enter.native="$refs.focus3.focus()" style="width: 32%;text-align: center;"/>
      <br/>
      <label>所属行业：</label>
      <Cascader
        ref="focus3"
        @keydown.enter.native="$refs.focus4.focus()"
        v-model:value="formItems.industryclassCode"
        :options="industryList"
        placeholder="门类/大类/中类/小类"
        change-on-select
        style="width: 32%;"
      />
      <span class="red_span"></span>
      <label>行政区划：</label>
      <Cascader
        ref="focus4"
        @keydown.enter.native="$refs.focus5.focus()"
        v-model:value="formItems.uniqueCodeZone"
        :options="zoningList"
        placeholder="省/市/区"
        style="width: 32%;border: none"
      />
      <br/>
      <label>国家(地区)：</label>
      <a-select
        ref="focus5"
        @keydown.enter.native="$refs.focus6.focus()"
        v-model:value="formItems.countryId"
        show-search
        placeholder=""
        style="width: 32%;border: none;text-align: center;"
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
      <label>集团性质：</label>
      <a-select
        v-model:value="formItems.icorp"
        placeholder="公司及企业/行政事业、民间非盈利及其他"
        style="width: 32%;"
        allow-clear
        ref="focus6"
        @keydown.enter.native="$refs.focus7.focus()"
      >
        <a-select-option
          key="0"
          value="1"
        >公司及企业
        </a-select-option>
        <a-select-option
          key="1"
          value="2"
        >行政事业、民间非盈利及其他
        </a-select-option>
      </a-select>
      <span class="red_span"><template v-if="!isLook"> ⋆</template></span>
      <br/>
      <label>负责人：</label>
      <a-input v-model:value="formItems.contacts" placeholder="" style="width: 32%" ref="focus7"
               @keydown.enter.native="$refs.focus8.focus()"/>
      <span class="red_span"></span>
      <label>联系电话：</label>
      <a-input v-model:value="formItems.telephone" placeholder=""
               style="width: 32%;text-align: center;" ref="focus8"
               @keydown.enter.native="$refs.focus9.focus()"/>
      <br/>
      <label>地址：</label>
      <a-input v-model:value="formItems.address" placeholder="" style="width: 32%;" ref="focus9"
               @keydown.enter.native="$refs.focus10.focus()"/>
      <span class="red_span"></span>
      <label>官网：</label>
      <a-input v-model:value="formItems.website" placeholder=""
               style="width: 32%;text-align: center;" ref="focus10"
               @keydown.enter.native="$refs.focus11.focus()"/>

      <br/>
      <label>简介：</label>
      <a-input v-model:value="formItems.remarks" style="width: 80%;" ref="focus11"/>
    </div>
  </div>
</template>
<script setup lang="ts">
import {computed, onMounted, ref, reactive, watch, unref} from 'vue'
import {
  getGroupList,
  saveGroup,
  findAllIndustry,
  importImg, findByCode, findByName
} from '/@/api/record/group/im-group'

const {closeCurrent} = useTabs(router);
import {useMessage} from "/@/hooks/web/useMessage";
import {findAllCountry} from "/@/api/record/costomer_data/customer";
import {findAllProvince} from "/@/api/record/system/zone";

onMounted(async () => {
  message.loading("加载数据中")
  let list = (await getGroupList()).items
  countryList.value = (await findAllCountry()).items
  zoningList.value = (await findAllProvince())
  industryList.value = (await findAllIndustry())
  await pageReload(list)
})
const pageReload = async (list) => {
  isLook.value = true
  if (list.length == 1) {
    let data = list[0]
    isEdit.value = true
    data.industryclassCode = data.industryclassCode != '' ? JSON.parse(data.industryclassCode) : []
    data.uniqueCodeZone = data.uniqueCodeZone != '' ? JSON.parse(data.uniqueCodeZone) : []
    formItems.value = data
    if (formItems.value.beiyong1 != null && formItems.value.beiyong1.length > 22) imageUrl.value = formItems.value.beiyong1
    changeBeforeModel = JSON.parse(JSON.stringify(formItems))
  } else if (list.length > 1) {
    createWarningModal({title: '数据异常', content: '系统发现一个以上的集团（机构）信息,请联系管理员检查数据库！'})
  } else {
    isEdit.value = false
    if (!isEdit.value) {
      resetForm()
      createInfoModal({title: '温馨提示', content: '暂未发现集团（机构）信息,请完善集团（机构）信息！'})
      formItems.value.icorp = '1'
      formItems.value.createDate = DateTool().nowDate()
      if (countryList.value.length > 0 && hasBlank(formItems.value.countryId)) {
        let iarr = countryList.value.filter(it => it.namech.indexOf('中国') != -1).map(it => it.uniqueCode)
        if (iarr.length > 0) formItems.value.countryId = iarr[0]
      }
    }
  }
}

import {pointMessage, hasBlank} from "/@/api/task-api/tast-bus-api";

async function saveData(data: any) {
  if (data.id == '') data.id = null
  try {
    await saveGroup(data).then(async (res) => {
      await pointMessage({title: '处理结果', content: '保存成功！', delay: true})
      await pageReload([res])
    }).catch(async () => {
      await pointMessage({title: '处理结果', content: '保存失败！', delay: true})
    })
  } catch (e) {
    console.log('Nc集团信息修改失败！')
  } finally {
    isLook.value = true
  }
}

const windowHeight = (window.innerHeight - 320)

import {
  LoadingOutlined, PlusOutlined, AppstoreOutlined
} from '@ant-design/icons-vue';
import {
  Select as ASelect,
  Input as AInput,
  DatePicker as ADatePicker,
  Cascader,
  Upload, message
} from 'ant-design-vue'
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {usePlatformsStore} from "/@/store/modules/platforms";
import {DateTool} from "/@/api/task-api/tools/universal-tools";

const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
const formItems: any = ref({})

const psnList: any = ref([])

const {createWarningModal, createInfoModal} = useMessage();


async function handleOk() {
    isLook.value  = true
  // 必填项不能为空
  let r = /^[a-zA-Z0-9]+$/g;
  if (formItems.value.groupName == '') {
    createWarningModal({title: '温馨提示', content: '集团名称为必填项！'})
  }/* else if (formItems.value.groupCode == '') {
    createWarningModal({title: '温馨提示', content: '集团编码为必填项！'})
  } else if (formItems.value.groupCode.length != 1) {
    createWarningModal({title: '温馨提示', content: '集团编码长度只能为一位且只能为数字或字母！'})
  } else if (!r.test(formItems.value.groupCode)) {
    createWarningModal({title: '温馨提示', content: '集团编码长度只能为一位且只能为数字或字母！'})
  } */ else if (formItems.value.icorp == '') {
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
    } else {
      if (isEdit.value && formItems.value.beiyong1.startsWith('data:image/png')) formItems.value.beiyong1 = formItems.value.beiyong2
    }
    await saveData(unref(formItems.value))
  }
}

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

const isEdit = ref(true)
const isLook = ref(false)
let changeBeforeModel: any = {}

// 所属行业
const industryList = ref([])
// 行政区划
const zoningList = ref([])
// 国家
const countryList = ref([])


const resetForm = () => {
  formItems.value.id = ''
  formItems.value.uniqueCode = ''
  formItems.value.groupCode = 'A'
  formItems.value.groupName = ''
  formItems.value.brand = ''
  formItems.value.industryclassCode = []
  formItems.value.uniqueCodeZone = []
  formItems.value.countryId = ''
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
  if ((changeBeforeModel.groupCode != undefined && changeBeforeModel.projectCode != '') || changeBeforeModel.groupCode == formItems.value.groupCode) {
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
  if ((changeBeforeModel.groupName != undefined && changeBeforeModel.groupName != '') || changeBeforeModel.groupName == formItems.value.groupName) {
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
<style lang="less" scoped>
@import '/@/assets/styles/global-menu-index.less';
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
  color: #747475;
  width: 940px;
  margin-left: calc((100% - 940px) / 2);
  margin-top: 5%;

  input {
    width: 32%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-size: 17px;
  }

  :deep(.ant-picker-input input) {
    font-size: 17px;
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

  :deep(.ant-cascader-picker) {
    .ant-cascader-picker-label {
      font-size: 17px;
    }

    .ant-cascader-input {
      border: none !important;
      border-bottom: 1px solid #bdb9b9 !important;
    }
  }


  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-size: 17px;
  }

  label {
    text-align: left;
    width: 120px;
    display: inline-block;
    padding: 5px 10px;
    margin-left: 15px;
    font-weight: bold;
    font-size: 16px;
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
