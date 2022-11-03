<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="员工信息批量修改"
    @ok="handleOk()"
    :showOkBtn="isEdit"
    :showCancelBtn="true"
    @register="register"
  >
    <div
      style="height: 100%"
      :class="isEdit?'nc-open-content':'nc-open-show-content'"
    >
      <div class="open-content-up">
        <label>属性：</label>
        <a-select
          v-model:value="formItems.psnType"
          placeholder="内部/外部员工"
          style="width: 30%;"
        >
          <a-select-option value="1">内部员工</a-select-option>
          <a-select-option value="2">外部员工</a-select-option>
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
        <br>
        <label>省市区：</label>
        <a-input v-model:value="formItems.province" placeholder="省/市/区"/>
        <span class="red_span"></span>
        <label>详细地址：</label>
        <a-input v-model:value="formItems.psnAddress" placeholder="街道"/>
        <span class="red_span"></span>
        <br/>
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
                <a-select-option value="1">居民身份证</a-select-option>
                <a-select-option value="2">港澳居民来往内地通行证</a-select-option>
                <a-select-option value="3">台湾居民来往大陆通行证</a-select-option>
                <a-select-option value="4">中国护照</a-select-option>
                <a-select-option value="5">外国护照</a-select-option>
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
              <br>
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
                <br>
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
import {useMessage} from "/@/hooks/web/useMessage";
import {psnTypeFindAll} from "/@/api/record/system/group-psn-type";

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
const cityValye: any = ref([])
// 是否为修改
const isEdit = ref(true)

let changeBeforeModel: any = {}

const dynamicTenantId = ref("")
const [register, {closeModal}] = useModalInner((data) => {
  //选择人员类别
  psnTypeFindAll().then((res: any) => {
    psnTypeList.value = res.items
  })

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

  // cityValye.value = data.data.province === null ? '' : ref(data.data.province.split(','))

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
    && formItems.value.bankNum == changeBeforeModel.bankNum)
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

:deep(.ant-select-single:not(.ant-select-customize-input)
.ant-select-selector
.ant-select-selection-search-input) {
  border: none !important;
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
    width: 150px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    padding-left: 2em;
    color: #535353;
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

.nc-open-show-content {

  input {
    width: 30%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  pointer-events: none;
  cursor: default;

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  label {
    text-align: left;
    width: 150px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    padding-left: 2em;
    color: #535353;
  }

  .open-content-down {
    margin-top: 5%;

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
</style>
