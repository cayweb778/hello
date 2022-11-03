<template>
  <BasicModal width="940px" v-bind="$attrs"
              title="公司（单位）" @ok="handleOk()"
              :ok-text="isEdit?'开始修改':'开始建账'"
              :cancel-text="isLook?'关闭':'取消'"
              :showOkBtn="showNext" @register="register"
              :closeFunc="handleClose" :loading="modelLoadIng"
              :canFullscreen="false" :maskClosable="false" :loadingTip="loadingText">
    <template #title="">
      <div style="display: flex;" class="vben-basic-title">
        <img src="/create.svg" style="width:25px;margin-right: 10px;"/>
        <span style="line-height: 25px;font-size: 16px;">公司信息</span>
      </div>
    </template>
    <div class="nc-open-content" :class="isLook?'nc-open-content-look':''" style="height: 100%">
        <div style="display: inline-flex;justify-content: space-between;width: 710px;margin-bottom: 2%  ">
          <div style="width: 600px;">
            <a-radio-group name="radioGroup" v-model:value="formItems.independent"
                           :style="isEdit?{pointerEvents: 'none'}:''" style="margin: 0;">
              <a-radio value="1" v-if="mark == 1003">独立核算</a-radio>
              <a-radio value="0">集团账套</a-radio>
            </a-radio-group><br/>
            <span style="font-size: 18px;margin-left: 20px;">公司（账套）名称:</span>
            <a-input v-model:value="formItems.accName"
                     placeholder=""
                     style="margin-left: 3%;font-size: 20px;width: 380px;border-color: black !important;border-width: 2px !important;"
                     ref="focus1"
                     @keydown.enter.native="$refs.focus2.focus(),formItems.accNameCn = formItems.accName,checkName()"
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
              <img v-else src="../../../../../assets/images/camera.png">
            </div>
          </Upload>
        </div>
        <br/>
        <label>公司代码：</label>
        <a-input v-model:value="formItems.coCode" @blur="checkCode()" placeholder="三位数字字母组合"
                 style="width: 32%;text-align: center" ref="focus2"
                 @keydown.enter.native="$refs.focus3.focus()"/>
        <span class="red_span">⋆</span>
        <label>简称：</label>
        <a-input v-model:value="formItems.accNameCn" @blur="checkName()" placeholder=""
                 style="width: 32%" ref="focus3"
                 @keydown.enter.native="$refs.focus4.focus()"/>
        <span class="red_span">⋆</span>
        <br/>
        <label>所属组织：</label>
        <a-select
          v-model:value="formItems.accGroup"
          placeholder=""
          style="width: 32%;"
          allow-clear
          :disabled="!isOrg"
          @change="orgChange"
          ref="focus4" @keydown.enter.native="$refs.focus5.focus()"
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
          ref="focus5" @keydown.enter.native="$refs.focus6.focus()"
        >
          <a-select-option
            v-for="(psn,index) in upList"
            :key="index"
            :value="psn.uniqueCode"
          >{{ psn.accName }}
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
          ref="focus6" @keydown.enter.native="$refs.focus7.focus()"
        />
        <span class="red_span"></span>
        <label>简介：</label>
        <a-input v-model:value="formItems.remarks" style="width: 32%;" ref="focus15"/>
        <br/>
        <template v-if="formItems.independent == '1'">
          <label>年度开始日期：</label>
          <MonthPicker v-model:value="formItems.yearStartDate" value-format="YYYY-MM-DD"
                          format="YYYY-MM-DD"
                          style="width: 32%;" @change="dateChange"
                          :disabled="isEdit"/><span class="red_span">⋆</span>
          <label>年度结束日期：</label>
          <DatePicker v-model:value="formItems.yearEndDate" value-format="YYYY-MM-DD"
                         format="YYYY-MM-DD" style="width: 32%;pointer-events: none; cursor: default;"/>
          <br>
          <label>会计期间数量：</label>
          <a-select v-model:value="formItems.periodNum" @change="changePeriodNum"
                    style="text-align: center;width: 32%" :disabled="isEdit">
            <a-select-option v-for="index in 5" :value="(index+11)+''">
              {{ index + 11 }}
            </a-select-option>
          </a-select>
          <span class="red_span">⋆</span>
        </template>
        <template v-else>
          <label>开始年度：</label>
          <DatePicker mode="year" v-model:value="startYaerValue" format="YYYY"
                         style="width: 32%;text-align: center;"
                         :open='yearShowOne' :disabled="isEdit" @openChange="openChangeOne"
                         @panelChange="panelChangeOne"/>
          <span class="red_span">⋆</span>
        </template>
        <label>启用期间：</label>
        <a-select v-model:value="formItems.startPeriod"
                  style="text-align: center;width: 32%" :disabled="isEdit">
          <a-select-option v-for="index in dynamicInterval" :value="index">
            {{ index }}
          </a-select-option>
        </a-select>
      <span class="red_span">⋆</span>
      <Tabs type="card" size="small" style="margin-top: 2%">
        <TabPane key="1" tab="基本信息">
              <label>会计准则：</label>
              <a-select v-model:value="formItems.accStandard" style="width: 32%;text-align: center;"
                        :disabled="isOrg || isEdit" @change="standardChange" ref="focus16"
                        @keydown.enter.native="$refs.focus17.focus()">
                <a-select-option v-for="item in acountStandardList"
                                 :value="item.id+''">
                  {{ item.tname }}
                </a-select-option>
              </a-select>
              <span class="red_span">⋆</span>
              <label>科目级次：</label>
              <span style="display: inline-block;color: black;width: 124px;text-align: center">{{levelPrefix || '选择准则' }}-</span>
              <a-input v-model:value="levelSuffix" placeholder="" style="width: 18%"
                       @keyup="importLimit" @blur="importLimit"/>
              <br/>
              <label>国家(地区)：</label>
              <a-select
                v-model:value="formItems.countryId"
                show-search
                placeholder=""
                style="width: 32%;border: none;text-align: center"
                allow-clear
                ref="focus9" @keydown.enter.native="$refs.focus10.focus()"
              >
                <a-select-option
                  v-for="psn in countryList"
                  :key="psn.id"
                  :value="psn.uniqueCode"
                >{{ psn.namech }}
                </a-select-option>
              </a-select>
              <span class="red_span"></span>
              <label>税号：</label>
              <a-input v-model:value="formItems.taxCode" placeholder="" style="width: 32%" ref="focus10"
                       @keydown.enter.native="$refs.focus11.focus()"/>
              <br/>
          <a-radio-group name="radioGroup" v-model:value="formItems.ibudgetAccStandard" :disabled="isOrg" style="margin: 1% 25%;">
            <label>预置会计科目：</label>
            <a-radio value="1">预置</a-radio>
            <a-radio value="0">不预置</a-radio>
          </a-radio-group>
        </TabPane>
        <TabPane key="2" tab="联系方式">
          <label>行政区划：</label>
          <Cascader
            v-model:value="formItems.uniqueCodeZone"
            :options="zoningList"
            placeholder="省/市/区"
            style="width: 32%;border: none;text-align: center"
            ref="focus7" @keydown.enter.native="$refs.focus8.focus()"
          />
          <span class="red_span"></span>
          <label>通讯地址：</label>
          <a-input v-model:value="formItems.address" placeholder="" style="width: 32%" ref="focus13"
                   @keydown.enter.native="$refs.focus14.focus()"/>

          <label>联系人：</label>
          <a-input v-model:value="formItems.contacts" placeholder="" style="width: 32%" ref="focus11"
                   @keydown.enter.native="$refs.focus12.focus()"/>
          <span class="red_span"></span>
          <label>联系电话：</label>
          <a-input v-model:value="formItems.telephone" placeholder="" style="width: 32%" ref="focus12"
                   @keydown.enter.native="$refs.focus13.focus()"/>
          <br/>
          <label>官网地址：</label>
          <a-input v-model:value="formItems.website" placeholder="" style="width: 32%" ref="focus14"
                   @keydown.enter.native="$refs.focus15.focus()"/>
        </TabPane>
        <TabPane key="3" tab="凭证类别">
          <label>默认凭证类别：</label>
          <a-select v-model:value="formItems.voucherTypeNum" style="text-align: center;width: 32%"
                    :disabled="isOrg" ref="focus22" @keydown.enter.native="$refs.focus23.focus()">
            <a-select-option v-for="item in pingZhengTypeList" :value="item.id+''">
              {{ item.voucherTypeName }}
            </a-select-option>
          </a-select>
          <span class="red_span">⋆</span>
          <br/>
          <label>其他凭证类别：</label>
          <a-select
            mode="multiple"
            v-model:value="formItems.voucherTypeOtherNums"
            style="width: 80%"
            placeholder=""
            :disabled="isOrg"
            ref="focus23"
          >
            <a-select-option
              v-for="item in (pingZhengTypeList.filter(i=>i.id != formItems.voucherTypeNum))"
              :value="item.id+''">
              {{ item.voucherTypeName }}
            </a-select-option>
          </a-select>
        </TabPane>
        <TabPane key="4" tab="本位币">
          <label>币种代码：</label>
          <a-select v-model:value="formItems.currency" style="text-align: center;width: 32%"
                    ref="focus21" @keydown.enter.native="$refs.focus22.focus()">
            <a-select-option v-for="item in currencyList" :value="item.abbreviation"
                             :lable="item.abbreviation">
              {{ item.abbreviation }}<span role="img"
                                           :aria-label="item.currencyZhCnName">-{{
                item.currencyZhCnName
              }}</span>
            </a-select-option>
          </a-select>
          <span class="red_span">⋆</span>
          <label>币种名称：</label>
          <a-input v-model:value="formItems.currencyName" placeholder=""
                   style="text-align: center;width: 32%"/><br/>
          <label>币种符号：</label>
          <a-input v-model:value="formItems.currencyCh" placeholder=""
                   style="text-align: center;width: 32%"/>
        </TabPane>
        <TabPane key="5" tab="数据精度">
          <label>数量小数位：</label>
          <a-select v-model:value="formItems.numberDec" style="text-align: center;width: 32%"
                    :disabled="isOrg" ref="focus17" @keydown.enter.native="$refs.focus18.focus()">
            <a-select-option v-for="index in 9" :value="(index-1)+''">
              {{ (index - 1) }}
            </a-select-option>
          </a-select>
          <span class="red_span"></span>
          <label>单价小数位：</label>
          <a-select v-model:value="formItems.unitPriceDec" style="text-align: center;width: 32%"
                    :disabled="isOrg" ref="focus18" @keydown.enter.native="$refs.focus19.focus()">
            <a-select-option v-for="index in 9" :value="(index-1)+''">
              {{ (index - 1) }}
            </a-select-option>
          </a-select>
          <br/>
          <label>汇率小数位：</label>
          <a-select v-model:value="formItems.rateDec" style="text-align: center;width: 32%"
                    :disabled="isOrg" ref="focus19" @keydown.enter.native="$refs.focus20.focus()">
            <a-select-option v-for="index in 9" :value="(index-1)+''">
              {{ (index - 1) }}
            </a-select-option>
          </a-select>
          <span class="red_span"></span>
          <label>凭证编号数位：</label>
          <a-select v-model:value="formItems.accvouchDec" style="text-align: center;width: 32%"
                    :disabled="isOrg" ref="focus20" @keydown.enter.native="$refs.focus21.focus()">
            <a-select-option v-for="index in 6" :value="(index+2)+''">
              {{ index + 2 }}
            </a-select-option>
          </a-select>
        </TabPane>
        <TabPane key="6" tab="主管账户"><label style="width: 150px">公司（账套）主管：</label>
          <a-select
            :allowClear="true"
            v-model:value="supervisors"
            show-search
            mode="multiple"
            placeholder=""
            style="width: 80%"
          >
            <a-select-option v-for="(item,index) in userList" :key="index" :value="item.id">
              {{ item.realName }}
            </a-select-option>
          </a-select></TabPane>
      </Tabs>
    </div>
  </BasicModal>
</template>
<style lang="less" scoped>
:deep(.ant-select-disabled) {
  .ant-select-selector {
    background-color: white !important;
    color: black !important;
  }
}
:deep(.ant-calendar-picker-input.ant-input),
:deep(.ant-select-single:not(.ant-select-customize-input)
  .ant-select-selector
  .ant-select-selection-search-input) {
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
    margin-left: 15px;font-weight: bold;
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
}
</style>
<script setup="props, {content}" lang="ts">
import {computed, ref, unref, watch} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {useMessage} from "/@/hooks/web/useMessage";
import {findByCode, findByName} from "/@/api/record/group/im-unit";
import {
  Select as ASelect,
  Input as AInput,
  DatePicker,
  Radio as ARadio,Tabs,
  Checkbox as ACheckbox, Cascader, Upload, message
} from 'ant-design-vue';
import {
  findCurrencyTypeList, findGroupVoucherTypes,
  initBasisTabAccoutData
} from "/@/api/record/system/financial-settings";
import moment from "moment";
import {hasBlank} from "/@/api/task-api/tast-bus-api";

const MonthPicker = DatePicker.MonthPicker
const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
const ARadioGroup = ARadio.Group
const Textarea = AInput.TextArea
const TabPane = Tabs.TabPane
import {
  LoadingOutlined, PlusOutlined
} from '@ant-design/icons-vue';
import {importImg} from "/@/api/record/group/im-group";

const {createMessage} = useMessage();
const emit = defineEmits(['register','save'])

const formItems: any = ref({})
const isOrg: any = ref(true) //是否集团

const psnList: any = ref([])
const userList = ref([])
const supervisors = ref([])
const loadingText = ref('请稍等...')
const {createWarningModal} = useMessage();

async function handleClose() {
   showNext.value = true
   yearShowOne.value = false;
   closeModal()
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
  } else if (formItems.value.coCode.length != 3 || (!r.test(formItems.value.coCode))) {
    createWarningModal({content: '公司（单位）编码长度只能为三位且只能为数字或字母！'})
  } else if (formItems.value.independent == '') {
    createWarningModal({content: '公司（单位）类型为必选项！'})
  } else if (formItems.value.independent == '0' && formItems.value.accGroup == '') {
    createWarningModal({content: '公司（单位）所属组织为必选项！'})
  } else {
      // 当为独立账套时 必填
      showNext.value =  false
      if (formItems.value.independent == '1') {
        if (formItems.value.accStandard == '') {
          createWarningModal({content: '公司（单位）会计准则为必选项！'})
          showNext.value = true
          return false;
        } else if (levelPrefix.value == '') {
          createWarningModal({content: '公司（单位）科目级次为必选项！'})
          showNext.value = true
          return false;
        }
      }
      if (formItems.value.currency == '') {
        createWarningModal({content: '公司（单位）本位币为必选项！'})
        showNext.value = true
      } else if (formItems.value.voucherTypeNum == '') {
        createWarningModal({content: '公司（单位）凭证类别为必选项！'})
        showNext.value = true
      } else if (formItems.value.yearStartDate == '') {
        createWarningModal({content: '公司（单位）年度开始日期为必选项！'})
        showNext.value = true
      } else if (formItems.value.startPeriod == '') {
        createWarningModal({content: '公司（单位）启用期间为必选项！'})
        showNext.value = true
      } else {
        formItems.value.organizeList = []
        formItems.value.industryclassCode = formItems.value.industryclassCode.length !== 0 ? JSON.stringify(formItems.value.industryclassCode) : ''
        formItems.value.uniqueCodeZone = formItems.value.uniqueCodeZone.length !== 0 ? JSON.stringify(formItems.value.uniqueCodeZone) : ''
        formItems.value.voucherTypeOtherNums = formItems.value.voucherTypeOtherNums.length !== 0 ? JSON.stringify(formItems.value.voucherTypeOtherNums) : ''
        if (!isOrg.value) formItems.value.ccodeLevel = splitLevel(levelPrefix.value, levelSuffix.value)
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
        if (!isEdit.value) loadingText.value = '建账公中...正在等待数据初始化...请耐心等待创建完成!'
        if (supervisors.value.length > 0) formItems.value.beiyong3 = JsonTool.json(supervisors.value)
        emit('save', {
          closeOpen() {
            showNext.value =  true
            modelLoadIng.value = false
            closeModal()
          },
          data: unref(formItems.value)
        })
    }
  }
  return false;
}

import {usePlatformsStore} from "/@/store/modules/platforms";
import {JsonTool, StrTool} from "/@/api/task-api/tools/universal-tools";
import {findAllByGroupSysUserFlag} from "/@/api/record/accpanel/data";
import {findPeriodYears} from "/@/api/record/generalLedger/data";

const mark = usePlatformsStore().getCurrentPlatformId
const modelLoadIng = ref(true)
/*const totalChange=computed(()=>formItems.value.total)
watch(totalChange, async (a)=>{
    if (a != 0){
      modelLoadIng.value = false
      if (a > 0) {
        closeModal()
        message.info(isEdit.value?'修改公司成功！':'建账成功！')
      }else {
        createWarningModal({title: '温馨提示',content: isEdit.value?'修改公司失败！':'建账失败！'});
      }
      formItems.value.total = 0
    }
  }
)*/
const showNext = ref(true)
const isEdit = ref(true)
const isLook = ref(false)
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
  await initBasisData()
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
  formItems.value.voucherTypeOtherNums = !hasBlank(formItems.value.voucherTypeOtherNums) ? JSON.parse(formItems.value.voucherTypeOtherNums) : []
  supervisors.value = !hasBlank(formItems.value.beiyong3) ? JSON.parse(formItems.value.beiyong3) : []
  if (formItems.value.beiyong1 != null && formItems.value.beiyong1.length > 22) imageUrl.value = formItems.value.beiyong1
  if (!isEdit.value) {

  } else {
    startYaerValue.value = formItems.value.yearStartDate.substring(0, 4)
    standardChange(formItems.value.accStandard)
  }
  changeBeforeModel = JSON.parse(JSON.stringify(formItems))
  setModalProps({minHeight: 480});
  modelLoadIng.value = false
  if (isLook.value) showNext.value = false
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
  formItems.value.ibudgetAccStandard = '1'
  formItems.value.numberDec = '2'
  formItems.value.unitPriceDec = '2'
  formItems.value.rateDec = '5'
  formItems.value.accvouchDec = '4'
  formItems.value.periodNum = '12'
  formItems.value.currency = 'CNY'
  formItems.value.currencyName = '人民币'
  formItems.value.currencyCh = 'RMB'
  formItems.value.voucherTypeNum = '1'
  formItems.value.voucherTypeOtherNums = []
  formItems.value.yearStartDate = ''
  formItems.value.yearEndDate = ''
  formItems.value.startPeriod = ''
  dynamicInterval.value = []
  levelPrefix.value = ''
  levelSuffix.value = ''
  imageUrl.value = ''
  startYaerValue.value = ''
}

async function checkCode() {
  formItems.value.coCode = formItems.value.coCode.trim()
  if ((changeBeforeModel._value.coCode != undefined && changeBeforeModel._value.coCode != '') || changeBeforeModel._value.coCode == formItems.value.coCode) {
    return true
  }
  const res = await findByCode(formItems.value)
  if (res != 0) {
    createWarningModal({content: '集团代码已存在，请重新输入！'})
    formItems.value.coCode = ''
    return false
  }
  return true
}

async function checkName() {
  formItems.value.accName   = StrTool.closeSpecialChars3(formItems.value.accName)
  formItems.value.accNameCn = StrTool.closeSpecialChars3(formItems.value.accNameCn)
  if ((changeBeforeModel._value.accNameCn != undefined && changeBeforeModel._value.accNameCn != '') || changeBeforeModel._value.accNameCn == formItems.value.accNameCn) {
    return true
  }
  const res = await findByName(formItems.value)
  if (res != 0) {
    createWarningModal({content: '集团简称已存在，请重新输入！'})
    formItems.value.accNameCn = ''
    return false
  }
  return true
}

const acountStandardList = ref([])
const levelList = ref([])
const pingZhengTypeList = ref([])
const currencyList = ref([])

const dynamicInterval = ref([])

watch(
  () => formItems.value.currency,
  () => {
    if (null != formItems.value.currency && formItems.value.currency != '') {
      let list = currencyList.value.filter(item => item.abbreviation == formItems.value.currency)
      if (list.length > 0) {
        formItems.value.currencyName = list[0].currencyZhCnName
        formItems.value.currencyCh = list[0].currencySymbol
      }
    }
  }
);
watch(
  () => formItems.value.independent,
  () => {
    if (formItems.value.independent != '1') { // 集团
      if (organizeList.value.length > 0) {
        isOrg.value = true
        formItems.value.accGroup = organizeList.value[0].uniqueCode
        orgChange()
      } else {
        createWarningModal({content: '暂无组织数据，请先建立组织数据后在进行该操作！'})
        formItems.value.independent = '1'
      }
    } else {     //等于独立账套
      isOrg.value = false
      if (!isEdit.value) {
        formItems.value.accGroup = ''
        formItems.value.accStandard = ''
        formItems.value.ccodeLevel = ''
        formItems.value.numberDec = '2'
        formItems.value.unitPriceDec = '2'
        formItems.value.rateDec = '5'
        formItems.value.accvouchDec = '4'
        formItems.value.currency = 'CNY'
        formItems.value.currencyName = '人民币元'
        formItems.value.currencyCh = 'RMB￥'
        formItems.value.voucherTypeNum = '1'
        formItems.value.voucherTypeOtherNums = []
        formItems.value.yearStartDate = ''
        formItems.value.yearEndDate = ''
        formItems.value.periodNum = '12'
        formItems.value.startPeriod = ''
        dynamicInterval.value = []
        levelPrefix.value = ''
        startYaerValue.value = ''
      }
    }
  }
);

const initBasisData = async () => {
  if (acountStandardList.value.length == 0 || levelList.value.length == 0) {
  await  initBasisTabAccoutData().then(res => {
      acountStandardList.value = []
      levelList.value = []
      acountStandardList.value = res.acountStandardList
      levelList.value = res.levelList
    })
  }
  if (pingZhengTypeList.value.length == 0) {
    findGroupVoucherTypes().then(res => {
      pingZhengTypeList.value = res
    })
  }
  if (currencyList.value.length == 0) {
    findCurrencyTypeList().then(res => {
      currencyList.value = res.items
    })
  }
  if (userList.value.length == 0) {
    findAllByGroupSysUserFlag().then(res => {
      userList.value = res
    })
  }
}

const dateChange = () => {
  if (!hasBlank(formItems.value.yearStartDate)) {
    let startDate = (formItems.value.yearStartDate.substring(0, 7)) + '-01'
    let d2 = new Date(startDate);
    d2.setFullYear(d2.getFullYear() + 1);
    d2.setDate(d2.getDate() - 1);
    formItems.value.yearEndDate = moment(d2).format('YYYY-MM-DD')
    formItems.value.yearStartDate = moment(startDate).format('YYYY-MM-DD')
  } else {
    formItems.value.yearEndDate = ''
    formItems.value.yearEndDate = ''
  }
  changePeriodNum()
}

const changePeriodNum = () => {
  formItems.value.startPeriod = ''
  if (formItems.value.yearStartDate != null) {
    let startDate = formItems.value.yearStartDate
    let periodNum = formItems.value.periodNum
    dynamicInterval.value = MonthsBetw(startDate, periodNum)
  } else {
    dynamicInterval.value = []
  }
}

const disabled = ref<boolean>(false);

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

function MonthsBetw(startDate, size) {
  const year = startDate.substring(0, 4)
  let i = 1
  let list = []
  while (i <= parseInt(size)) {
    let num = i;
    if (i < 10) {
      num = '0' + num
    }
    list.push(year + num)
    i++
  }
  return list
}

const yearShowOne = ref(false)

function openChangeOne(status) {
  if (status) {
    yearShowOne.value = true
  }
}

// 得到年份选择器的值
function panelChangeOne(value) {
  startYaerValue.value = value;
  yearShowOne.value = false;
  // 获取所属组织开始期间
  let arr = organizeList.value.filter(item => item.uniqueCode == formItems.value.accGroup)
  if (arr.length > 0) {
    let orgO = arr[0]
    formItems.value.yearStartDate = value.format('YYYY') + '-' + (isEdit.value ? formItems.value.yearStartDate.substring(5, 10) : orgO.yearStartDate)
    formItems.value.yearEndDate = value.format('YYYY') + '-' + (isEdit.value ? formItems.value.yearEndDate.substring(5, 10) : orgO.yearEndDate)
    debugger
    changePeriodNum()
  }

}

const levelPrefix = ref('')
const levelSuffix = ref('')
const startYaerValue = ref('')
const standardChange = async (val) => {
  let obj = acountStandardList.value.filter(item => item.id == val)
  if (obj.length > 0) {
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
const importLimit = (e) => {
  e.target.value = e.target.value.replace(/[^123456789]/g, "")
  if (!hasBlank(e.target.value)) e.target.value = e.target.value.split('').join('-')
  levelSuffix.value = e.target.value
}
const orgChange = () => {
  // 初始化 组织的值
  let obj = organizeList.value.filter((item) => item.uniqueCode == formItems.value.accGroup)[0]
  if (null != obj && formItems.value.independent != '1') {
    formItems.value.accStandard = obj.uniqueAccStandard

    formItems.value.yearStartDate = hasBlank(obj.yearStartDate) ?  startYaerValue.value+'-01-01' :startYaerValue.value+'-'+ obj.yearStartDate
    formItems.value.yearEndDate = hasBlank(obj.yearEndDate) ? startYaerValue.value+'-12-31' : startYaerValue.value+'-'+obj.yearEndDate
    formItems.value.periodNum = hasBlank(obj.periodNum) ? '12' : obj.periodNum

    formItems.value.numberDec = hasBlank(obj.numberDec) ? '2' : obj.numberDec
    formItems.value.unitPriceDec = hasBlank(obj.numberDec) ? '2' : obj.unitPriceDec
    formItems.value.rateDec = hasBlank(obj.numberDec) ? '5' : obj.rateDec
    formItems.value.accvouchDec = hasBlank(obj.numberDec) ? '4' : obj.accvouchDec
    formItems.value.voucherTypeNum = hasBlank(obj.voucherTypeNum) ? '' : obj.voucherTypeNum
    formItems.value.voucherTypeOtherNums = hasBlank(obj.voucherTypeOtherNums) ? [] : JSON.parse(obj.voucherTypeNum)
    if (hasBlank(formItems.value.ccodeLevel)) formItems.value.ccodeLevel = obj.ccodeLevel
    if (!hasBlank(formItems.value.accStandard)) standardChange(formItems.value.accStandard)
    if (hasBlank(obj.currency)) {
      formItems.value.currency = 'CNY'
    }
  }
}
</script>
