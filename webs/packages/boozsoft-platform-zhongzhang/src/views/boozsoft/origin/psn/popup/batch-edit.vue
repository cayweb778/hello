<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="人员信息批量修改"
    @ok="handleOk()"
    :showOkBtn="isEdit"
    :showCancelBtn="true"
    @register="register"
    :canFullscreen="false"
  >
    <!--    <template #title>
          <div style="display: flex;" class="vben-basic-title">
            <img src="/@/assets/images/create.svg" style="width:25px;margin-right: 10px;"/>
            <span style="line-height: 25px;font-size: 16px;">人员信息批量修改</span>
          </div>
        </template>-->
    <template #title>
      <div style="display: flex;margin-top: 10px;" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;">
          <FormOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;人员信息批量修改
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 60px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="/@/assets/images/person.png" style="height:100px;margin-right: 10px;"/>
      </div>
    </template>
    <div
      style="height: 100%;margin-top: 60px;"
      :class="isEdit?'nc-open-content':'nc-open-show-content'"
    >
      <div class="open-content-up">
        <label>属性：</label>
        <a-select
          v-model:value="formItems.psnType"
          placeholder="内部/外部人员"
          style="width: 30%;"
        >
          <a-select-option value="1">内部人员</a-select-option>
          <a-select-option value="2">外部人员</a-select-option>
        </a-select>
        <span class="red_span"></span>
        <label>性别：</label>
        <a-select
          v-model:value="formItems.psnSex"
          placeholder="性别"
          style="width: 30%;"
        >
          <a-select-option value="0">未知的性别</a-select-option>
          <a-select-option value="1">男</a-select-option>
          <a-select-option value="2">女</a-select-option>
          <a-select-option value="9">未说明的性别</a-select-option>
        </a-select>
        <span class="red_span"></span>
        <br>

        <label>类别：</label>
        <a-select
          v-model:value="formItems.uniquePsnType"
          show-search
          placeholder="请选择人员类别"
          style="width: 30%"
          :filter-option="filterOption"
          @focus="handleFocus"
          @blur="handleBlur"
          @change="handleChange"
        >
          <a-select-option v-for="psntype in psnTypeList" :key="psntype.psnTypeName"
                           :value="psntype.uniqueCode">
            {{ psntype.psnTypeName }}
          </a-select-option>
        </a-select>
        <span class="red_span"></span>
        <label>国家(地区)：</label>
        <a-select
          v-model:value="formItems.countryId"
          show-search
          placeholder="国家/地区"
          style="width: 30%"
        >
          <a-select-option v-for="countrys in countryList" :key="countrys.namech"
                           :value="countrys.namech">
            {{ countrys.namech }}
          </a-select-option>
        </a-select>
        <span class="red_span"></span>
        <br>
        <label>省市区：</label>
        <a-input v-model:value="formItems.province" placeholder="省/市/区"/>
        <span class="red_span"></span>
        <label>详细地址：</label>
        <a-input v-model:value="formItems.psnAddress" placeholder="街道"/>
        <span class="red_span"></span>

        <label style="font-size: 14px;">状态：</label>
        <a-select
          v-model:value="formItems.flag"
          placeholder="正常/停用"
          style="width: 30%;"
          allow-clear
        >
          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
          <a-select-option value="1">正常</a-select-option>
          <a-select-option value="0">停用</a-select-option>
        </a-select>
        <span class="red_span"></span>

      </div>

      <div class="open-content-down">
        <a-tabs
          v-model:activeKey="activeKey"
          type="card"
        >
          <a-tab-pane
            key="1"
            tab="入职信息"
          >
            <div class="down-tab-content">
              <label>证件类型：</label>
              <a-select
                v-model:value="formItems.documentType"
                style="width: 30%;"
              >
                <a-select-option value="">请选择证件类型</a-select-option>
<!--                <a-select-option value="1">居民身份证</a-select-option>
                <a-select-option value="2">港澳居民来往内地通行证</a-select-option>
                <a-select-option value="3">台湾居民来往大陆通行证</a-select-option>
                <a-select-option value="4">中国护照</a-select-option>
                <a-select-option value="5">外国护照</a-select-option>-->
                <a-select-option v-for="item in documentTypeList" :value="item.ccode">{{ item.cname }}</a-select-option>
              </a-select>
              <span class="red_span"></span>
              <!--              <label>证件号码：</label>
                            <a-input v-model:value="formItems.documentCode" placeholder="请输入证件号码（不允许重复）"/>
                            <span class="red_span"></span>-->
              <br>
              <!--              <label>工位：</label>
                            <a-input v-model:value="formItems.psnStation" placeholder=""/>
                            <span class="red_span"></span>
                            <label>出生日期：</label>
                            <a-date-picker v-model:value="formItems.birthDate" style="width: 30%;"/>
                            <span class="red_span"></span>
                            <br/>-->

              <label>入职日期：</label>
              <a-date-picker v-model:value="formItems.entryDate" style="width: 30%;"/>
              <span class="red_span"></span>
              <label>离职日期：</label>
              <a-date-picker v-model:value="formItems.leaveDate" style="width: 30%;"/>
              <span class="red_span"></span>
              <br/>
            </div>
          </a-tab-pane>
          <a-tab-pane
            key="2"
            tab="开户信息"
          >
            <div class="content-down-tab">
              <div class="down-tab-content">
                <label>开户银行：</label>
                <a-input v-model:value="formItems.psnBank" placeholder="例如：中国建设银行朝阳路支行"/>
                <span class="red_span"></span>
                <label>开户地：</label>
                <a-input v-model:value="formItems.bankArea" placeholder="例如：武汉市汉阳区"/>
                <span class="red_span"></span>
                <br>
                <!--                <label>银行账户：</label>
                                <a-input v-model:value="formItems.bankAccount" placeholder=""/>
                                <span class="red_span"></span>-->
                <label>银行行号：</label>
                <a-input v-model:value="formItems.bankNum" placeholder=""/>
                <span class="red_span"></span>
                <br/>
              </div>
            </div>
          </a-tab-pane>
        </a-tabs>
      </div>
    </div>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {ref, unref} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {findAllCountry} from "/@/api/record/costomer_data/customer";
import {findAllProvince} from "/@/api/record/system/zone";
import {
  Input as AInput,
  Select as ASelect,
  Popover as APopover,
  Tabs as ATabs,
  DatePicker as ADatePicker,
  Cascader as ACascader,
  TreeSelect as ATreeSelect
} from "ant-design-vue";
import {
  PlusCircleOutlined,
  FormOutlined,
  FileSearchOutlined,
  CaretDownOutlined,
} from '@ant-design/icons-vue'
import {useMessage} from "/@/hooks/web/useMessage";
import {psnTypeFindAll} from "/@/api/record/system/origin-psn-type";
import {findDocumentTypeAll} from "/@/api/record/system/group-document-type";

const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ATabPane = ATabs.TabPane
const emit = defineEmits(['register', 'save'])

const {
  createErrorModal
} = useMessage()

const formItems: any = ref({})

const treeData: any = ref([])

const psnTypeList: any = ref([])

//国家
const countryList: any = ref([])
const options = ref([])
const documentTypeList: any = ref([])
// 是否为修改
const isEdit = ref(true)

let changeBeforeModel: any = {}

const originId = ref("")
const [register, {closeModal}] = useModalInner(async (data) => {
  originId.value = data.originId
  //选择人员类别
  psnTypeFindAll(data.originId).then((res: any) => {
    psnTypeList.value = res.items
  })

  //证件类型
  documentTypeList.value = await findDocumentTypeAll()

  // 国家信息
  findAllCountry().then(res => {
    countryList.value = res.items
  })
  // 全部省信息
  findAllProvince().then(res => {
    options.value = res
  })

  // 方式2
  formItems.value.psnType = data.data.psnType
  formItems.value.psnSex = data.data.psnSex
  formItems.value.uniquePsnType = data.data.uniquePsnType
  formItems.value.province = data.data.province
  formItems.value.psnAddress = data.data.psnAddress
  formItems.value.countryId = data.data.countryId
  formItems.value.documentType = data.data.documentType
  formItems.value.entryDate = data.data.entryDate
  formItems.value.leaveDate = data.data.leaveDate
  formItems.value.psnBank = data.data.psnBank
  formItems.value.bankArea = data.data.bankArea
  formItems.value.bankNum = data.data.bankNum
  formItems.value.flag = data.data.flag

  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
let isChanged: boolean = false

async function handleOk() {
  isChanged = !(formItems.value.psnType == changeBeforeModel.psnType
    && formItems.value.psnSex == changeBeforeModel.psnSex
    && formItems.value.uniquePsnType == changeBeforeModel.uniquePsnType
    && formItems.value.province == changeBeforeModel.province
    && formItems.value.psnAddress == changeBeforeModel.psnAddress
    && formItems.value.countryId == changeBeforeModel.countryId
    && formItems.value.documentType == changeBeforeModel.documentType
    && formItems.value.entryDate == changeBeforeModel.entryDate
    && formItems.value.leaveDate == changeBeforeModel.leaveDate
    && formItems.value.psnBank == changeBeforeModel.psnBank
    && formItems.value.bankArea == changeBeforeModel.bankArea
    && formItems.value.bankNum == changeBeforeModel.bankNum
    && formItems.value.flag == changeBeforeModel.flag)
  if (isChanged) {
    emit('save', unref(formItems))
    closeModal()
    return true
  }
  closeModal()
  // alert("没有改变过")
  return false
}

const activeKey = ref('1')

function filterOption() {
}

function handleFocus() {
}

function handleBlur() {
}

function handleChange() {
}
</script>
<style>
.vben-basic-title {
  color: #0096c7 !important;
  border:none !important;
}

.ant-modal-body {
  padding: 0px;
  border-bottom: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
  border-top: none !important;
}
.ant-modal-header{
  border: none !important;
}
</style>
<style lang="less" scoped>
:deep(.ant-calendar-picker-input.ant-input), :deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector .ant-select-selection-search-input) {
  border: none !important;
  //border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;
  font-weight: bold;
}

:deep(.ant-picker){
  border: none !important;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  input {
    font-size: 14px;
    font-weight: bold;
    border: none !important;
  }
}

:deep(.ant-select-single:not(.ant-select-customize-input)
.ant-select-selector
.ant-select-selection-search-input) {
  border: none !important;
}

.vben-basic-title {
  color: #0096c7 !important;
  border: none !important;
}

.ant-modal-body {
  padding: 0px;
  border-bottom: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
}

.red_span {
  color: red;
  font-weight: bold;
  display: inline-block;
  width: 20px;
  text-align: right;
}

.nc-open-content {

  input {
    width: 30%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
  }

  .abc{
    border-bottom: 2px solid #666666 !important;
    font-size: 18px;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
  }

  label {
    text-align: left;
    width: 120px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    padding-left: 2em;
    color: #535353;
    font-size: 13px;
    margin-left: 2em;
    font-weight: bold;
  }

  .open-content-down {
    margin-top: 5%;
    margin-bottom: 10px;

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

.nc-open-show-content {

  input {
    width: 30%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
  }

  .abc{
    border-bottom: 2px solid #666666 !important;
    font-size: 18px;
  }

  pointer-events: none;
  cursor: default;

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
  }

  label {
    text-align: left;
    width: 120px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    padding-left: 2em;
    color: #535353;
    font-size: 13px;
    margin-left: 2em;
    font-weight: bold;
  }

  .open-content-down {
    margin-top: 5%;
    margin-bottom: 10px;

    :deep(.ant-tabs-tab) {
      pointer-events: auto;
    }

    .ant-tabs-tab-active::before {
      position: absolute;
      top: 0px;
      left: 0;
      width: 100%;
      border-top: 2px solid transparent;
      border-radius: 2px 2px 0 0;
      transition: all 0.3s;
      content: '';
      background-color: rgb(1, 143, 251);
    }
  }
}

/*:deep(.ant-tabs.ant-tabs-card .ant-tabs-card-bar .ant-tabs-tab) {
  height: 40px;
  margin: 0;
  margin-right: 2px;
  padding: 0 16px;
  line-height: 38px;
  background: #fafafa;
  border: 1px solid #f0f0f0;
  border-radius: 2px 2px 0 0;
  transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
  font-weight: bold;
  font-size: 13px;
}*/

:deep(.ant-tabs-card).ant-tabs-top > .ant-tabs-nav  {
  height: 40px;
  font-weight: bold;
  font-size: 13px;
  .ant-tabs-tab-active,.ant-tabs-card.ant-tabs-top > div > .ant-tabs-nav .ant-tabs-tab-active {
    border-top: 2px solid rgb(1, 143, 251) !important;
  }
}

</style>
