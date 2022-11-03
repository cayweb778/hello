<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="员工信息"
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
        <label style="font-size: 16px;">员工姓名：</label>
        <a-input
          v-model:value="formItems.psnName"
          placeholder="请输入姓名(使用后不允许修改)"
        />
        <span class="red_span">*</span>

<!--        <a-select
          v-model:value="formItems.psnSex"
          placeholder="性别"
          style="width: 15%;"
        >
          <a-select-option value="0">未知的性别</a-select-option>
          <a-select-option value="1">男</a-select-option>
          <a-select-option value="2">女</a-select-option>
          <a-select-option value="9">未说明的性别</a-select-option>
        </a-select>&nbsp;&nbsp;-->
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
        <br><br>

        <label>编号：</label>
        <a-input v-model:value="formItems.psnCode" placeholder="请输入编号（不允许重复）"/>
        <span class="red_span">*</span>
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

        <!--        <a-select v-model:value="formItems.uniqueCodeDept" placeholder="" style="width: 35%;">
                  <a-select-option value="">请选择</a-select-option>
                  <a-select-option value="1">财务部</a-select-option>
                </a-select>-->
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
        <label>部门：</label>
        <a-tree-select
          v-model:value="formItems.uniqueCodeDept"
          :disabled="true"
          style="width: 30%"
          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
          :tree-data="treeData"
          placeholder="请选择部门"
          tree-default-expand-all
          allow-clear
        >
        </a-tree-select>
        <span class="red_span"></span>
<!--        <label>职务</label>
        <a-select v-model:value="formItems.psnPost" placeholder="" style="width: 35%;">
          <a-select-option value="">请选择</a-select-option>
          <a-select-option value="1">程序员</a-select-option>
        </a-select>-->
        <br>

        <label>工号：</label>
        <a-input v-model:value="formItems.jobNum" placeholder=""/>
        <span class="red_span"></span>
        <!--        <a-select v-model:value="formItems.uniquePsnType" placeholder="请选择人员类别" style="width: 35%;">
                  <a-select-option value="">请选择</a-select-option>
                  <a-select-option value="1">在职员工</a-select-option>
                </a-select>-->
        <label>手机：</label>
        <a-input v-model:value="formItems.cellPhoneNum" placeholder="请输入手机（不允许重复）"/>
        <span class="red_span"></span>
        <br>

        <label>省市区：</label>
        <a-input v-model:value="formItems.province" placeholder="省/市/区"/>
        <span class="red_span"></span>
<!--        <a-cascader v-model:value="cityValye" :options="options" placeholder="省/市/区"
                  style="width: 35%;"/>-->
<!--        <a-select
          v-model:value="formItems.countryId"
          show-search
          placeholder="国家/地区"
          style="width: 35%"
          allow-clear
        >
          <a-select-option v-for="countrys in countryList" :key="countrys.namech"
                           :value="countrys.namech">
            {{ countrys.namech }}
          </a-select-option>
        </a-select>-->
        <label>Email：</label>
        <a-input v-model:value="formItems.psnEmail" placeholder=""/>
        <span class="red_span"></span>
        <br>

        <label>详细地址：</label>
        <a-input v-model:value="formItems.psnAddress" placeholder="街道"/>
        <span class="red_span"></span>
        <!--        <a-select v-model:value="formItems.province" style="width: 11%;" placeholder="省">
                  <a-select-option value="">请选择</a-select-option>
                  <a-select-option value="1">湖北省</a-select-option>
                </a-select>
                <a-select
                  v-model:value="formItems.city"
                  style="width: 11%;margin: 0 1%"
                  placeholder="市"
                >
                  <a-select-option value="">请选择</a-select-option>
                  <a-select-option value="0">武汉市</a-select-option>
                </a-select>
                <a-select
                  v-model:value="formItems.district"
                  style="width: 11%;"
                  placeholder="区/县"
                >
                  <a-select-option value="">请选择</a-select-option>
                  <a-select-option value="1">汉阳区</a-select-option>
                </a-select>-->
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

<!--        <label/>
        <a-input v-model:value="formItems.psnAddress" placeholder="街道"/>-->
        <label>微信：</label>
        <a-input v-model:value="formItems.psnWechat" placeholder="请输入微信（不允许重复）"/>
        <span class="red_span"></span>
        <label>钉钉：</label>
        <a-input v-model:value="formItems.psnQq" placeholder="请输入钉钉（不允许重复）"/>
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
              <label>证件号码：</label>
              <a-input v-model:value="formItems.documentCode" placeholder="请输入证件号码（不允许重复）"/>
              <span class="red_span"></span>
              <br>
              <label>工位：</label>
              <a-input v-model:value="formItems.psnStation" placeholder=""/>
              <span class="red_span"></span>
              <label>出生日期：</label>
              <a-date-picker v-model:value="formItems.birthDate" style="width: 30%;"/>
              <span class="red_span"></span>
              <br/>

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
                <label>银行账户：</label>
                <a-input v-model:value="formItems.bankAccount" placeholder=""/>
                <span class="red_span"></span>
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
import {GetDeptTreeByFlag} from '/@/api/sys/dept'
import {psnTypeFindAll} from "/@/api/record/system/group-psn-type";
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
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getPsnList} from "/@/api/record/system/group-psn";
import {useMessage} from "/@/hooks/web/useMessage";

const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ATabPane = ATabs.TabPane
const emit = defineEmits(['register','save'])

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

const [register, {closeModal}] = useModalInner((data) => {
  //选择部门
  /*GetDeptTreeByFlag().then(res => {
    function a(deptTree: any) {
      deptTree.forEach(
        (item: any) => {
          if (item.children != null) {
            a(item.children)
          }
          item.title = '(' + item.deptCode + ')' + item.deptName
          item.value = item.uniqueCode
          item.key = item.uniqueCode
        })
    }
    a(res)
    treeData.value = res
    if (null != data.data.isEdit) isEdit.value = data.data.isEdit
  })*/
  //选择人员类别
  psnTypeFindAll().then((res: any) => {
    psnTypeList.value = res.items
    if ((data.data.uniquePsnType=='' || data.data.uniquePsnType==null) && psnTypeList.value.length>0){
      data.data.uniquePsnType = psnTypeList.value[0].uniqueCode
      formItems.value.uniquePsnType = psnTypeList.value[0].uniqueCode
    }
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
  formItems.value.id = data.data.id
  formItems.value.uniqueCode = data.data.uniqueCode
  formItems.value.psnCode = data.data.psnCode
  formItems.value.jobNum = data.data.jobNum
  formItems.value.psnName = data.data.psnName
  formItems.value.psnSex = data.data.psnSex
  formItems.value.uniqueCodeDept = data.data.uniqueCodeDept
  formItems.value.uniquePsnType = data.data.uniquePsnType
  formItems.value.psnType = data.data.psnType
  formItems.value.psnPost = data.data.psnPost
  formItems.value.documentType = data.data.documentType
  formItems.value.idNum = data.data.idNum
  formItems.value.psnStation = data.data.psnStation
  formItems.value.cellPhoneNum = data.data.cellPhoneNum
  formItems.value.psnEmail = data.data.psnEmail
  formItems.value.psnWechat = data.data.psnWechat
  formItems.value.psnQq = data.data.psnQq
  formItems.value.psnAddress = data.data.psnAddress
  formItems.value.psnBank = data.data.psnBank
  formItems.value.bankArea = data.data.bankArea
  formItems.value.bankAccount = data.data.bankAccount
  formItems.value.EntryDate = data.data.EntryDate
  formItems.value.createDate = data.data.createDate
  formItems.value.flag = data.data.flag
  formItems.value.countryId = data.data.countryId
  formItems.value.uniqueCodeZone = data.data.uniqueCodeZone
  formItems.value.bankNum = data.data.bankNum
  formItems.value.province = data.data.province
  formItems.value.city = data.data.city
  formItems.value.birthDate = data.data.birthDate
  formItems.value.leaveDate = data.data.leaveDate

  if (formItems.value.countryId=='' || formItems.value.countryId==null){
    formItems.value.countryId = '中国'
  }

  // cityValye.value = data.data.province === null ? '' : ref(data.data.province.split(','))

  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
let isChanged: boolean = false

async function handleOk() {
  if (formItems.value.uniqueCodeDept==''||formItems.value.uniqueCodeDept==null){
    formItems.value.uniqueCodeDept = '9999'
  }
  if (formItems.value.uniquePsnType==''||formItems.value.uniquePsnType==null){
    formItems.value.uniquePsnType = '9999'
  }
  const psnList = await getPsnList()
  if (formItems.value.psnCode==null || formItems.value.psnCode==''){
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '人员编码不能为空！'
    })
    return false
  }
  if (formItems.value.psnName==null || formItems.value.psnName==''){
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '人员编码不能为空！'
    })
    return false
  }
  if (!formItems.value.psnCode == changeBeforeModel.psnCode && formItems.value.psnCode!='' && formItems.value.psnCode!=null){
    for (let i = 0; i < psnList.length; i++) {
      const psn = psnList[i];
      if (psn.psnCode==formItems.value.psnCode){
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '人员编码重复不允许添加！'
        })
        return false
      }
    }
  }
  if (!formItems.value.cellPhoneNum == changeBeforeModel.cellPhoneNum && formItems.value.cellPhoneNum!='' && formItems.value.cellPhoneNum!=null){
    for (let i = 0; i < psnList.length; i++) {
      const psn = psnList[i];
      if (psn.cellPhoneNum==formItems.value.cellPhoneNum){
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '手机号重复不允许添加！'
        })
        return false
      }
    }
  }
  if (!formItems.value.psnWechat == changeBeforeModel.psnWechat && formItems.value.psnWechat!='' && formItems.value.psnWechat!=null){
    for (let i = 0; i < psnList.length; i++) {
      const psn = psnList[i];
      if (psn.psnWechat==formItems.value.psnWechat){
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '微信重复不允许添加！'
        })
        return false
      }
    }
  }
  if (!formItems.value.psnQq == changeBeforeModel.psnQq && formItems.value.psnQq!='' && formItems.value.psnQq!=null){
    for (let i = 0; i < psnList.length; i++) {
      const psn = psnList[i];
      if (psn.psnQq==formItems.value.psnQq){
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '钉钉重复不允许添加！'
        })
        return false
      }
    }
  }
  if (!formItems.value.documentCode == changeBeforeModel.documentCode && formItems.value.documentCode!='' && formItems.value.documentCode!=null){
    for (let i = 0; i < psnList.length; i++) {
      const psn = psnList[i];
      if (psn.documentCode==formItems.value.documentCode){
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '身份证号重复不允许添加！'
        })
        return false
      }
    }
  }
  // formItems.value.province = cityValye.value.join(',')
  isChanged = !(formItems.value.id == changeBeforeModel.id
    && formItems.value.uniqueCode == changeBeforeModel.uniqueCode
    && formItems.value.psnCode == changeBeforeModel.psnCode
    && formItems.value.jobNum == changeBeforeModel.jobNum
    && formItems.value.psnName == changeBeforeModel.psnName
    && formItems.value.psnSex == changeBeforeModel.psnSex
    && formItems.value.uniqueCodeDept == changeBeforeModel.uniqueCodeDept
    && formItems.value.uniquePsnType == changeBeforeModel.uniquePsnType
    && formItems.value.psnType == changeBeforeModel.psnType
    && formItems.value.psnPost == changeBeforeModel.psnPost
    && formItems.value.documentType == changeBeforeModel.documentType
    && formItems.value.idNum == changeBeforeModel.idNum
    && formItems.value.psnStation == changeBeforeModel.psnStation
    && formItems.value.cellPhoneNum == changeBeforeModel.cellPhoneNum
    && formItems.value.psnEmail == changeBeforeModel.psnEmail
    && formItems.value.psnWechat == changeBeforeModel.psnWechat
    && formItems.value.psnQq == changeBeforeModel.psnQq
    && formItems.value.psnAddress == changeBeforeModel.psnAddress
    && formItems.value.psnBank == changeBeforeModel.psnBank
    && formItems.value.bankArea == changeBeforeModel.bankArea
    && formItems.value.bankAccount == changeBeforeModel.bankAccount
    && formItems.value.EntryDate == changeBeforeModel.EntryDate
    && formItems.value.createDate == changeBeforeModel.createDate
    && formItems.value.flag == changeBeforeModel.flag
    && formItems.value.countryId == changeBeforeModel.countryId
    && formItems.value.uniqueCodeZone == changeBeforeModel.uniqueCodeZone
    && formItems.value.bankNum == changeBeforeModel.bankNum
    && formItems.value.province == changeBeforeModel.province
    && formItems.value.city == changeBeforeModel.city)
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
