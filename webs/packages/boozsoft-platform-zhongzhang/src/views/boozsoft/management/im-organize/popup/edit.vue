<template>
  <BasicModal width="940px" v-bind="$attrs" title="" :canFullscreen="false"
              @ok="handleOk()" :closeFunc="handleClose" :showOkBtn="showNext" @register="register"
              :ok-text="isStepOne?'下一步':'确定'" :cancel-text="isStepOne?(isLook?'关闭':'取消'):'上一步'">
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <PlusCircleOutlined v-if="!isEdit" style="font-size: 50px;color: #0096c7;"/>
        <FormOutlined v-else-if="isEdit && !isLook" style="font-size: 50px;color: #0096c7;"/>
        <FileSearchOutlined v-else style="font-size: 50px;color: #0096c7;"/>
        <span style="line-height:50px;font-size: 30px;color: #0096c7;">&ensp;组织</span>
      </div>
      <div style="display: inline-block;position:absolute;right: 50px;top: 20px;background:#ffffff">
        <img src="/@/assets/images/zt.png" style="height:100px;margin-right: 10px;"/>
      </div>
    </template>

    <div class="nc-open-content" style="height: 100%" :class="isLook?'nc-open-content-look':''">
      <div v-show="isStepOne">
        <div
          style="display: inline-flex;justify-content: space-between;width: 624px;margin:5%  15%">
          <div style="margin-left: 24px; width: 560px;padding-top: 5%;">
            <span style="font-size: 18px;font-weight: bold">组织名称:</span>
            <a-input @blur="checkName()"
                     v-model:value="formItems.orgName"
                     placeholder="组织名称"
                     style="margin-left: 3%;font-size: 20px;width: 440px;font-weight: bold;border-bottom: 3px #666666 solid !important;"
                     @keydown.enter.native="$refs.focus2.focus(),formItems.orgSimpName = formItems.orgName"
            />
            <span class="red_span">⋆</span>
          </div>
          <!--    文件上传  暂时放弃  -->
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
        <label>代码：</label>
        <a-input v-model:value="formItems.orgCode" @blur="checkCode()" placeholder="一位数字或字母"
                 style="width: 32%;text-align: center"
                 ref="focus2"
                 @keydown.enter.native="$refs.focus3.focus()"
        />
        <span class="red_span">⋆</span>
        <label>简称：</label>
        <a-input v-model:value="formItems.orgSimpName" placeholder="" style="width: 32%"
                 ref="focus3"
                 @keydown.enter.native="$refs.focus5.focus()"/>
        <span class="red_span">⋆</span>
        <br/>
        <!--      <label>所属集团：</label>
              <a-select
                v-model:value="formItems.uniqueGroupCode"
                placeholder=""
                style="width: 32%;"
                allow-clear
                ref="focus4"
                @keydown.enter.native="$refs.focus5.focus()"
              >
                <a-select-option v-for="(item,index) in groupInfoList"
                                 :key="index"
                                 :value="item.uniqueCode"
                >{{ item.groupName }}
                </a-select-option>
              </a-select>
              <span class="red_span">⋆</span>-->
        <label>上级组织：</label>
        <a-select
          v-model:value="formItems.orgSuperior"
          show-search
          placeholder=""
          style="width: 32%;border: none"
          allow-clear
          ref="focus5"
          @keydown.enter.native="$refs.focus6.focus()"
        >
          <a-select-option
            v-for="(psn,index) in upList"
            :key="index"
            :value="psn.uniqueCode"
          >{{ psn.orgName }}
          </a-select-option>
        </a-select>
        <span class="red_span"></span>
        <label>所属行业：</label>
        <Cascader
          v-model:value="formItems.industryclassCode"
          :options="industryList"
          placeholder="门类/大类/中类/小类"
          change-on-select
          style="width: 32%;"
          ref="focus6"
          @keydown.enter.native="$refs.focus7.focus()"
        />
        <span class="red_span"></span>
        <br/>
        <label>行政区划：</label>
        <Cascader
          v-model:value="formItems.uniqueCodeZone"
          :options="zoningList"
          placeholder="省/市/区"
          style="width: 32%;border: none;text-align: center"
          ref="focus7"
          @keydown.enter.native="$refs.focus8.focus()"
        />
        <span class="red_span"></span>
        <label>国家(地区)：</label>
        <a-select
          v-model:value="formItems.countryId"
          show-search
          placeholder=""
          style="width: 32%;border: none;text-align: center"
          allow-clear
          ref="focus8"
          @keydown.enter.native="$refs.focus9.focus()"
        >
          <a-select-option
            v-for="psn in countryList"
            :key="psn.id"
            :value="psn.uniqueCode"
          >{{ psn.namech }}
          </a-select-option>
        </a-select>

        <label>启用日期：</label>
        <a-date-picker v-model:value="formItems.createDate" format="YYYY-MM-DD"
                       value-format="YYYY-MM-DD" style="width: 32%;"
                       ref="focus9" @keydown.enter.native="$refs.focus10.focus()"/>
        <span class="red_span">⋆</span>
        <label>负责人：</label>
        <a-input v-model:value="formItems.personInCharge" placeholder="" style="width: 32%"
                 ref="focus10" @keydown.enter.native="$refs.focus11.focus()"/>
        <span class="red_span"></span>
        <br/>
        <label>联系电话：</label>
        <a-input v-model:value="formItems.telephone" placeholder="" style="width: 32%" ref="focus11"
                 @keydown.enter.native="$refs.focus12.focus()"/>
        <span class="red_span"></span>
        <label>通讯地址：</label>
        <a-input v-model:value="formItems.address" placeholder="" style="width: 32%" ref="focus12"
                 @keydown.enter.native="$refs.focus13.focus()"/>
        <br/>
        <label>简介：</label>
        <a-input v-model:value="formItems.remarks" style="width: 80%;" ref="focus13"/>
      </div>
      <div v-show="!isStepOne" style="padding: 10% 20px">
        <label>会计制度模板：</label>
        <a-select
          v-model:value="formItems.uniqueAccStandard"
          placeholder=""
          style="width: 32%;text-align: center"
          :disabled="isOrg"
          allow-clear
          @keydown.enter.native="$refs.focus14.focus()"
          @change="standardChange"
        >
          <a-select-option v-for="(item,index) in normList"
                           :key="index"
                           :value="item.id+''"
          >{{ item.tname }}
          </a-select-option>
        </a-select>
        <span class="red_span">⋆</span>
        <label>会计制度：</label>
        <a-input v-model:value="formItems.accStandardName" placeholder=""
                 style="width: 32%;background-color: white;color: black;" :disabled="true"/>
        <span class="red_span"></span>
        <br/>
        <label>会计期间数量：</label>
        <a-select v-model:value="formItems.periodNum" :disabled="isOrg"
                  style="text-align: center;width: 32%;text-align-last: center;" ref="focus14"
                  @keydown.enter.native="$refs.focus15.focus()">
          <a-select-option v-for="index in 5" :value="(index+11)+''">
            {{ index + 11 }}
          </a-select-option>
        </a-select>
        <span class="red_span">⋆</span>
        <label>科目级次：</label>
        <span
          style="display: inline-block;width: 124px;color: black;text-align: center">{{
            levelPrefix || '选择准则'
          }}-</span>
        <!--            v-if="!isOrg"-->
        <a-input v-model:value="levelSuffix" placeholder="" style="width: 18%"
                 ref="focus15" @keydown.enter.native="$refs.focus16.focus()"
                 @keyup="importLimit" @blur="importLimit"/>
        <span class="red_span"></span>
        <br/>
        <label>年度开始日期：</label>
        <a-date-picker v-model:value="formItems.yearStartDate" format="MM-DD" value-format="MM-DD"
                       @change="dateChange"
                       ref="focus16" @keydown.enter.native="$refs.focus18.focus()"
                       style="width: 32%;text-align-last: center;"/>
        <span class="red_span">⋆</span>
        <label>年度结束日期：</label>
        <a-date-picker v-model:value="formItems.yearEndDate" format="MM-DD" value-format="MM-DD"
                       style="width: 32%;pointer-events: none; cursor: default;text-align-last: center;"/>
        <span class="red_span">⋆</span>
        <br>
        <label>数量小数位：</label>
        <a-select v-model:value="formItems.numberDec" style="text-align: center;width: 32%;"
                  :disabled="isOrg" ref="focus18" @keydown.enter.native="$refs.focus19.focus()">
          <a-select-option v-for="index in 9" :value="(index-1)+''">
            {{ (index - 1) }}
          </a-select-option>
        </a-select>
        <span class="red_span"></span>
        <label>单价小数位：</label>
        <a-select v-model:value="formItems.unitPriceDec" style="text-align: center;width: 32%;"
                  :disabled="isOrg" ref="focus19" @keydown.enter.native="$refs.focus20.focus()">
          <a-select-option v-for="index in 9" :value="(index-1)+''">
            {{ (index - 1) }}
          </a-select-option>
        </a-select>
        <span class="red_span"></span>
        <br/>
        <label>汇率小数位：</label>
        <a-select v-model:value="formItems.rateDec" style="text-align: center;width: 32%;"
                  :disabled="isOrg" ref="focus20" @keydown.enter.native="$refs.focus21.focus()">
          <a-select-option v-for="index in 9" :value="(index-1)+''">
            {{ (index - 1) }}
          </a-select-option>
        </a-select>
        <span class="red_span"></span>
        <label>凭证编号数位：</label>
        <a-select v-model:value="formItems.accvouchDec" style="text-align: center;width: 32%;"
                  :disabled="isOrg" ref="focus21" @keydown.enter.native="$refs.focus22.focus()">
          <a-select-option v-for="index in 6" :value="(index+2)+''">
            {{ index + 2 }}
          </a-select-option>
        </a-select>
        <span class="red_span"></span>
        <br/>
        <label>默认类别：</label>
        <a-select v-model:value="formItems.voucherTypeNum" style="width: 32%;" :disabled="isOrg"
                  ref="focus22">
          <a-select-option v-for="item in pingZhengTypeList" :value="item.id+''">
            {{ item.voucherTypeName }}
          </a-select-option>
        </a-select>
        <span class="red_span">⋆</span>
        <!--        <label>其他类别：</label>
                <a-select
                  mode="multiple"
                  v-model:value="formItems.voucherTypeOtherNums"
                  style="width: 34%"
                  placeholder=""
                  :disabled="isOrg"
                >
                  <a-select-option
                    v-for="item in (pingZhengTypeList.filter(i=>i.id != formItems.voucherTypeNum))"
                    :value="item.id+''">
                    {{ item.voucherTypeName }}
                  </a-select-option>
                </a-select>
                <span class="red_span"></span>-->
        <br/>


      </div>
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
    width: 120px;
    display: inline-block;
    padding: 6px 10px;
    margin-left: 15px;
    font-weight: bold;
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
import {ref, unref} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {useMessage} from "/@/hooks/web/useMessage";
import {findByCode, findByName} from "/@/api/record/group/im-organize";
import {
  Select as ASelect,
  Input as AInput,
  DatePicker as ADatePicker,
  Checkbox as ACheckbox,
  Tree as ATree, Upload,
  Cascader
} from 'ant-design-vue'

import {
  LoadingOutlined,
  PlusOutlined,
  PlusCircleOutlined,
  FormOutlined,
  FileSearchOutlined
} from '@ant-design/icons-vue';
import {importImg} from "/@/api/record/group/im-group";
import {findGroupVoucherTypes} from "/@/api/record/system/financial-settings";
import moment from "moment";
import {hasBlank} from "/@/api/task-api/tast-bus-api";
import {DateTool} from "/@/api/task-api/tools/universal-tools";

const Textarea = AInput.TextArea
const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
const {createMessage} = useMessage();
const emit = defineEmits(['register', 'save'])

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
const showNext = ref(true)
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
    showNext.value = true
    return false
  }
}

const isOrg = ref(false)

async function handleOk() {
  // 必填项不能为空
  showNext.value = false
  let r = /^[a-zA-Z0-9]+$/g;
  if (formItems.value.orgName == '') {
    createWarningModal({content: '组织名称为必填项！'})
  } else if (formItems.value.orgCode == '') {
    createWarningModal({content: '组织编码为必填项！'})
  } else if (formItems.value.orgSimpName == '') {
    createWarningModal({content: '组织简称为必填项！'})
  } else if (formItems.value.uniqueGroupCode == '') {
    createWarningModal({content: '组织所属集团为必选项！'})
  } else if (formItems.value.orgCode.length != 1 || (!r.test(formItems.value.orgCode))) {
    createWarningModal({content: '组织编码长度只能为一位且只能为数字或字母！'})
  } else if (formItems.value.createDate == '') {
    createWarningModal({content: '组织启用日期为必选项！'})
  } else {
    if (isStepOne.value) {
      isStepOne.value = false
      if (isLook.value) showNext.value = false
      // 初始化 组织的值
      if (!hasBlank(formItems.value.orgSuperior)) {
        let obj = upList.value.filter((item) => item.uniqueCode == formItems.value.orgSuperior)[0]
        if (null != obj && formItems.value.independent != '1') {
          isOrg.value = true
          formItems.value.uniqueAccStandard = obj.uniqueAccStandard
          formItems.value.yearStartDate = hasBlank(obj.yearStartDate) ? '01-01' : obj.yearStartDate
          formItems.value.yearEndDate = hasBlank(obj.yearEndDate) ? '12-31' : obj.yearEndDate
          formItems.value.periodNum = hasBlank(obj.periodNum) ? '12' : obj.periodNum

          formItems.value.numberDec = hasBlank(obj.numberDec) ? '2' : obj.numberDec
          formItems.value.unitPriceDec = hasBlank(obj.numberDec) ? '2' : obj.unitPriceDec
          formItems.value.rateDec = hasBlank(obj.numberDec) ? '5' : obj.rateDec
          formItems.value.accvouchDec = hasBlank(obj.numberDec) ? '4' : obj.accvouchDec
          formItems.value.voucherTypeNum = hasBlank(obj.voucherTypeNum) ? '' : obj.voucherTypeNum
          formItems.value.voucherTypeOtherNums = hasBlank(obj.voucherTypeOtherNums) ? [] : JSON.parse(obj.voucherTypeNum)

          if (hasBlank(formItems.value.ccodeLevel)) formItems.value.ccodeLevel = obj.ccodeLevel
          if (formItems.value.uniqueAccStandard != '') {
            standardChange(formItems.value.uniqueAccStandard)
          }
        }
      } else {
        if (hasBlank(formItems.value.voucherTypeNum) && pingZhengTypeList.value.length > 0) {
          let ar = pingZhengTypeList.value.filter(it => it.voucherTypeName.indexOf('记账') != -1)
          if (ar.length > 0) formItems.value.voucherTypeNum = ar[0].id
        }
        isOrg.value = false
      }
    } else {
      // 当为独立账套时 必填
      if (formItems.value.uniqueAccStandard == '') {
        createWarningModal({content: '会计准则为必选项！'})
      } else if (levelPrefix.value == '') {
        createWarningModal({content: '科目级次为必填项！'})
      } else if (formItems.value.voucherTypeNum == '') {
        createWarningModal({content: '公司（单位）凭证类别为必选项！'})
      } else if (formItems.value.yearStartDate == '') {
        createWarningModal({content: '公司（单位）年度开始日期为必选项！'})
      } else {
        formItems.value.industryclassCode = formItems.value.industryclassCode.length !== 0 ? JSON.stringify(formItems.value.industryclassCode) : ''
        formItems.value.voucherTypeOtherNums = formItems.value.voucherTypeOtherNums.length !== 0 ? JSON.stringify(formItems.value.voucherTypeOtherNums) : ''
        formItems.value.uniqueCodeZone = formItems.value.uniqueCodeZone.length !== 0 ? JSON.stringify(formItems.value.uniqueCodeZone) : ''

        formItems.value.ccodeLevel = splitLevel(levelPrefix.value, levelSuffix.value)
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
        if (typeof (formItems.value.yearStartDate) == 'string') { // 转型
          formItems.value.yearEndDate = moment(new Date(formItems.value.yearEndDate))
          formItems.value.yearStartDate = moment(new Date(formItems.value.yearStartDate))
        }
        formItems.value.yearEndDate = formItems.value.yearEndDate.format('MM-DD')
        formItems.value.yearStartDate = formItems.value.yearStartDate.format('MM-DD')
        emit('save', unref(formItems))
        console.log(unref(formItems))
        closeModal()
      }
    }
  }
  showNext.value = true
  return false;
}

const isEdit = ref(true)
const isLook = ref(false)
let changeBeforeModel: any = {}
const countryList = ref([])
const zoningList = ref([])
const industryList = ref([])
const pingZhengTypeList = ref([])
const upList = ref([])
const [register, {closeModal, setModalProps}] = useModalInner(({data}) => {
  resetForm()
  isEdit.value = data.isEdit
  isLook.value = data.isLook
  groupInfoList.value = data.groupInfoList
  normList.value = data.normList
  levelList.value = data.levelList
  industryTreeData.value = data.industryTreeData
  upList.value = data.upList
  data.groupInfoList = []
  data.normList = []
  data.levelList = []
  data.industryTreeData = []
  countryList.value = data.countryList
  zoningList.value = data.zoningList
  industryList.value = data.industryList
  data.countryList = []
  data.zoningList = []
  data.industryList = []
  data.upList = []
  formItems.value = data
  formItems.value.industryclassCode = formItems.value.industryclassCode != '' ? JSON.parse(formItems.value.industryclassCode) : []
  formItems.value.uniqueCodeZone = formItems.value.uniqueCodeZone != '' ? JSON.parse(formItems.value.uniqueCodeZone) : []
  formItems.value.voucherTypeOtherNums = !hasBlank(formItems.value.voucherTypeOtherNums) ? JSON.parse(formItems.value.voucherTypeOtherNums) : []
  if (formItems.value.beiyong1 != null && formItems.value.beiyong1.length > 22) imageUrl.value = formItems.value.beiyong1
  if (!isEdit.value) {
    formItems.value.uniqueGroupCode = groupInfoList.value[0].uniqueCode
    if (countryList.value.length > 0 && hasBlank(formItems.value.country)) {
      let iarr = countryList.value.filter(it => it.namech.indexOf('中国') != -1).map(it => it.uniqueCode)
      if (iarr.length > 0) formItems.value.countryId = iarr[0]
    }
    formItems.value.createDate = DateTool().nowDate()
  } else {
    if (formItems.value.uniqueAccStandard != '') {
      standardChange(formItems.value.uniqueAccStandard)
    }
  }
  changeBeforeModel = JSON.parse(JSON.stringify(formItems))
  setModalProps({minHeight: 380});
  if (pingZhengTypeList.value.length == 0) {
    findGroupVoucherTypes().then(res => {
      pingZhengTypeList.value = res
    })
  }
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
  formItems.value.countryId = ''
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
    formItems.value.accStandardName = obj[0].accStandardName
    levelPrefix.value = obj[0].tjici/*.replace(/-/g,"")*/
    if (formItems.value.ccodeLevel != '' && isEdit.value) {
      levelSuffix.value = formItems.value.ccodeLevel.replace(obj[0].tjici, '').replace(/-/g, "").split('').join('-')
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
const dateChange = () => {
  if (formItems.value.yearStartDate != null) {
    let d2 = new Date(formItems.value.createDate.split('-')[0] + '-' + formItems.value.yearStartDate);
    d2.setFullYear(d2.getFullYear() + 1);
    d2.setDate(d2.getDate() - 1);
    formItems.value.yearEndDate = moment(d2).format('MM-DD')
  } else {
    formItems.value.yearEndDate = ''
    formItems.value.yearEndDate = ''
  }
}

</script>
