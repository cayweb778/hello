<template>
  <BasicModal
    width="600px"
    v-bind="$attrs"
    title="生成资产负债表"
    @ok="handleOk()"
    @cancel="handleClose()"
    @register="register"
  >
    <div
      class="nc-open-content"
    >
      <div class="open-content-up">

        <AccountPicker theme="two" @reloadTable="dynamicAdReload" readonly class="account"/>
        <br/>

        <label>选择模板：</label>
        <a-select v-model:value="selectModel" style="width: 50%">
          <a-select-option v-for="template in templateList" :key="template.id" :value="template.id">
            {{ template.titleName }}
          </a-select-option>
        </a-select>
        <!--        <br/>
                <label>选择年度：</label>
                <a-select v-model:value="formItems.year" style="width: 150px">
                  <a-select-option v-for="item in yearList" :key="item.iyear" :value="item.iyear" >
                    {{ item.iyear }}
                  </a-select-option>
                </a-select>-->
        <br/><br/>

        <div
          style="background: #ffffff;position: relative;border: 1px solid #999999;margin: 20px 40px;padding: 10px 20px;border-radius: 5px;">
          <div
            style="position: absolute;background: #ffffff; top: -15px;left: 20px;padding: 0 10px;">
            取值范围
          </div>
          <a-radio-group style="width: 100%" v-model:value="radiovalue">
            <a-radio :value="1"><span>月份：</span></a-radio>
            <a-select v-model:value="formItems.month" :disabled="radiovalue!='1'"
                      style="width: 150px;">
              <a-select-option v-for="item in dateList" :key="item.iperiodNum"
                               :value="item.iperiodNum">
                {{ item.iperiodNum }}
              </a-select-option>
            </a-select>

            <br/>
            <a-radio :value="2"><span>季度：</span></a-radio>
            <a-select v-model:value="formItems.jidu" :disabled="radiovalue!='2'"
                      style="width: 150px;">
              <a-select-option value="1">第一季度</a-select-option>
              <a-select-option value="2">第二季度</a-select-option>
              <a-select-option value="3">第三季度</a-select-option>
              <a-select-option value="4">第四季度</a-select-option>
            </a-select>

            <br/>
            <a-radio :value="3"><span>年度：</span></a-radio>
            全年
          </a-radio-group>
        </div>
        <a-checkbox :checked="formItems.jizhang" @change="onChange" style="width: 150px;">
          包含未记账
        </a-checkbox>
        <a-checkbox :checked="formItems.day" @change="onChange1" style="width: 220px;">
          报表报告日精确到天数
        </a-checkbox>

      </div>
    </div>

  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import {onMounted, reactive, ref, unref} from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {Select as ASelect,Input as AInput,Radio as ARadio,Checkbox as ACheckbox} from "ant-design-vue"
import {findByReportNameAndFlag} from "/@/api/record/system/report-template";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {getThisAdInfoData} from "/@/api/record/system/financial-settings";
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {findPeriod, findPeriodByAccontId} from "/@/api/record/generalLedger/data";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {useUserStore} from "/@/store/modules/user";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {findDataBase} from "/@/api/record/system/account";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useMessage} from "/@/hooks/web/useMessage";

const ASelectOption = ASelect.Option
const ARadioGroup = ARadio.Group

const {
  createErrorModal
} = useMessage()

const emit = defineEmits(['register', 'save'])

const templateList: any = ref([])

const selectModel: any = ref('')

let radiovalue: any = ref(1);

const formItems: any = ref({})

const yearList: any = ref([])
const dateList: any = ref([])
const dynamicTenantId = ref()
const defaultAdName = ref()
const [register, {closeModal}] = useModalInner((data) => {
  dynamicTenantId.value = data.dynamicTenantId
  defaultAdName.value = data.defaultAdName
  formItems.value.year = data.year
  getThisAdInfoData({'accId': data.defaultAdName}).then(res => {
    if (null != res && res.independent == 0) {
      pageParameter.companyName = res.accNameCn
      pageParameter.companyCode = res.coCode
      pageParameter.ifUnit = res.icorp == '1'
    }
  })
  useRouteApi(findByReportNameAndFlag, {schemaName: dynamicTenantId})("zcfzb").then(res => {
    templateList.value = res.items
    selectModel.value = ''
    if (templateList.value.length > 0) {
      selectModel.value = templateList.value[0].id
    }
  })
  //获取会计区间
  findPeriodByAccontId(defaultAdName.value).then(res => {
    dateList.value = res
    if ((formItems.value.month == '' || formItems.value.month == null) && dateList.value.length > 0) {
      formItems.value.month = dateList.value[0].iperiodNum
    }
  })
  //获取会计年度
  /*findPeriod(defaultAdName.value).then(res => {
    yearList.value = res
    if (formItems.value.year=='' && yearList.value.length>0) {
      formItems.value.year = yearList.value[0].iyear
    }
  })*/
})
let template:any = {}
async function handleOk() {
  if (selectModel.value == '' || selectModel.value == null) {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '请先添加模板！'
    })
    return false
  }
  if (formItems.value.year == '' || formItems.value.year == null) {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '请先添加年度！'
    })
    return false
  }
  templateList.value.forEach(item => {
    if (item.id == selectModel.value) {
      template = item
      template.id = null
    }
  })
  template.templateId = selectModel.value
  template.iyear = formItems.value.year
  template.dataType = radiovalue.value
  template.createUser = useUserStore().getUserInfo['realName']
  // template.editUser = useUserStore().getUserInfo['name']
  template.jizhang = formItems.value.jizhang
  let date = ''
  let timeDate = ''
  if (radiovalue.value == '1') {
    if (formItems.value.day == true) {
      dateList.value.forEach(item => {
        if (formItems.value.month == item.iperiodNum) {
          timeDate = formItems.value.year + '-' + item.dateEnd
        }
      })
    } else {
      timeDate = formItems.value.year + '年' + formItems.value.month + '月'
    }
    template.iperiod = formItems.value.month
    template.iyperiod1 = formItems.value.year + formItems.value.month
    template.iyperiod2 = formItems.value.year + formItems.value.month
    date = formItems.value.month + '月'
  } else if (radiovalue.value == '2') {
    template.jidu = formItems.value.jidu
    if (formItems.value.jidu == '1') {
      template.iyperiod1 = formItems.value.year + '01'
      template.iyperiod2 = formItems.value.year + '03'
      date = '第一季度'
    } else if(formItems.value.jidu=='2'){
      template.iyperiod1 = formItems.value.year+'04'
      template.iyperiod2 = formItems.value.year+'06'
      date = '第二季度'
    } else if(formItems.value.jidu=='3'){
      template.iyperiod1 = formItems.value.year+'07'
      template.iyperiod2 = formItems.value.year+'09'
      date = '第三季度'
    } else if(formItems.value.jidu=='4'){
      template.iyperiod1 = formItems.value.year+'10'
      template.iyperiod2 = formItems.value.year+dateList.value.length
      date = '第四季度'
    }
    timeDate = formItems.value.year + '年' + date
  } else if (radiovalue.value=='3') {
    template.iyperiod1 = formItems.value.year + '01'
    template.iyperiod2 = formItems.value.year + dateList.value.length
    date = ''
    timeDate = formItems.value.year + '年'
  }
  template.dataCode = template.menu6+formItems.value.year+'年'+date
  //给date赋值
  if (template.menu1.split('&').length==2 && template.menu1.split('&')[1]=='date'){
    template.menu1 = template.menu1.split('&')[0] + timeDate
  } else if (template.menu2.split('&').length==2 && template.menu2.split('&')[1]=='date'){
    template.menu2 = template.menu2.split('&')[0] + timeDate
  } else if (template.menu3.split('&').length==2 && template.menu3.split('&')[1]=='date'){
    template.menu3 = template.menu3.split('&')[0] + timeDate
  } else if (template.menu4.split('&').length==2 && template.menu4.split('&')[1]=='date'){
    template.menu4 = template.menu4.split('&')[0] + timeDate
  } else if (template.menu5.split('&').length==2 && template.menu5.split('&')[1]=='date'){
    template.menu5 = template.menu5.split('&')[0] + timeDate
  }
  //给操作员姓名赋值
  if (template.menu1.split('&').length==2 && template.menu1.split('&')[1]=='操作员姓名'){
    template.menu1=template.menu1.split('&')[0]+useUserStore().getUserInfo['realName']
  } else if (template.menu2.split('&').length==2 && template.menu2.split('&')[1]=='操作员姓名'){
    template.menu2=template.menu2.split('&')[0]+useUserStore().getUserInfo['realName']
  } else if (template.menu3.split('&').length==2 && template.menu3.split('&')[1]=='操作员姓名'){
    template.menu3=template.menu3.split('&')[0]+useUserStore().getUserInfo['realName']
  } else if (template.menu4.split('&').length==2 && template.menu4.split('&')[1]=='操作员姓名'){
    template.menu4=template.menu4.split('&')[0]+useUserStore().getUserInfo['realName']
  } else if (template.menu5.split('&').length==2 && template.menu5.split('&')[1]=='操作员姓名'){
    template.menu5=template.menu5.split('&')[0]+useUserStore().getUserInfo['realName']
  }
  useCompanyOperateStoreWidthOut().getAccountList.forEach(item => {
    //给币种赋值
    if (item.accId==defaultAdName.value && item.currency!='' && item.currency!=null){
      if (template.menu1.split('&').length==2 && template.menu1.split('&')[1]=='币种'){
        template.menu1=template.menu1.split('&')[0]+item.currencyCh
      } else if (template.menu2.split('&').length==2 && template.menu2.split('&')[1]=='币种'){
        template.menu2=template.menu2.split('&')[0]+item.currencyCh
      } else if (template.menu3.split('&').length==2 && template.menu3.split('&')[1]=='币种'){
        template.menu3=template.menu3.split('&')[0]+item.currencyCh
      } else if (template.menu4.split('&').length==2 && template.menu4.split('&')[1]=='币种'){
        template.menu4=template.menu4.split('&')[0]+item.currencyCh
      } else if (template.menu5.split('&').length==2 && template.menu5.split('&')[1]=='币种'){
        template.menu5=template.menu5.split('&')[0]+item.currencyCh
      }
    }
    //给核算单位赋值
    if (item.accId==defaultAdName.value && item.accName!='' && item.accName!=null){
      if (template.menu1.split('&').length==2 && template.menu1.split('&')[1]=='核算单位'){
        template.menu1=template.menu1.split('&')[0]+item.accName
      } else if (template.menu2.split('&').length==2 && template.menu2.split('&')[1]=='核算单位'){
        template.menu2=template.menu2.split('&')[0]+item.accName
      } else if (template.menu3.split('&').length==2 && template.menu3.split('&')[1]=='核算单位'){
        template.menu3=template.menu3.split('&')[0]+item.accName
      } else if (template.menu4.split('&').length==2 && template.menu4.split('&')[1]=='核算单位'){
        template.menu4=template.menu4.split('&')[0]+item.accName
      } else if (template.menu5.split('&').length==2 && template.menu5.split('&')[1]=='核算单位'){
        template.menu5=template.menu5.split('&')[0]+item.accName
      }
    }
  })
  template.dynamicTenantId = dynamicTenantId.value
  template.defaultAdName = defaultAdName.value
  emit('save', unref(template))
  closeModal()
  return true
}

const {closeCurrent} = useTabs(router);

async function handleClose() {
  if (null == template || template == '') {
    await closeCurrent()
    // router.push('/home/welcome')
  }
}

function onChange(e) {
  formItems.value.jizhang = e.target.checked
  console.log('checked = ' + e.target.checked);
}

function onChange1(e) {
  formItems.value.day = e.target.checked
  console.log('checked = ' + e.target.checked);
}

const pageParameter = reactive({
  companyCode: '100',
  companyName: '北京希格科技',
  ifUnit: false,
})

onMounted(async () => {
  formItems.value.day = true
  formItems.value.jizhang = true
  // 进页面执行
  /*getThisAdInfoData({'accId': getCurrentAccountName(false)}).then(res=>{
    if (null != res && res.independent == 0){
      pageParameter.companyName = res.accNameCn
      pageParameter.companyCode = res.coCode
      pageParameter.ifUnit = res.icorp == '1'
    }
  })*/
})

const dynamicAdReload = async (obj) => {
  // const dataBase:any = await findDataBase(obj.accId,obj.year)
  formItems.year = obj.year
  dynamicTenantId.value = obj.accountMode
  defaultAdName.value = obj.accId
  useRouteApi(findByReportNameAndFlag, {schemaName: dynamicTenantId})("zcfzb").then(res => {
    templateList.value = res.items
    if (templateList.value.length > 0) {
      selectModel.value = templateList.value[0].id
    } else {
      selectModel.value = ''
    }
  })
  findPeriodByAccontId(defaultAdName.value).then(res => {
    dateList.value = res
    if (dateList.value.length > 0) {
      formItems.value.month = dateList.value[0].iperiodNum
    } else {
      formItems.value.month = ''
    }
  })
}
</script>
<style lang="less">
.vben-basic-title {
  color: rgb(1, 129, 226) !important;
}

.ant-modal-body {
  padding: 0px;
  border: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
}

.nc-open-content {
  input {
    width: 35%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  .ant-select-selector {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  label {
    text-align: left;
    width: 110px;
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

.ant-input-affix-wrapper {
  width: 100px !important;
}

.ant-input-affix-wrapper .ant-input {
  width: 60px !important;
}

.account div div {
  font-size: 14px !important;
  color: #535353 !important;
  margin-left: 0px !important;
}

.account {
  margin-left: 15px !important;
}

</style>
