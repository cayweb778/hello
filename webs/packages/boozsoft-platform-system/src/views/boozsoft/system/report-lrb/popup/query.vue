<template>
  <BasicModal
    width="650px"
    v-bind="$attrs"
    title="生成利润表"
    @ok="handleOk()"
    @cancel="handleClose()"
    @register="register"
    :canFullscreen="false"
    :footer="null"
    loadingTip="查询参数初始化中"
  >
    <template #title>
      <div style="height: 30px;width: 100%;background-color: #5f375c;color: white;line-height: 30px;">
        <AppstoreOutlined  style="margin: 0 2px;font-size: 14px;"/>
        <span style="font-size: 14px">报表中心</span>
      </div>
    </template>
    <div style="display: inline-flex;justify-content: space-between;width: 100%;">
      <div style="width: calc(100% - 150px);height: 100%;">
        <div style="text-align: center;padding: 10px 0 5px;">
          <SearchOutlined style="font-size: 24px;color: #0096c7;margin-top: 2px"/>
          <span style="line-height:30px;font-size: 24px;color: #0096c7;font-weight: bold;">&ensp;生成利润表</span>
        </div>
        <div class="nc-open-content">
          <div class="open-content-up">

            <div class="border-div">
              <span>业务范围</span>
              <AccountPicker theme="three" @reloadTable="dynamicAdReload" readonly class="account"/>
            </div>
            <div class="border-div">
              <span>报表模板</span>
              <label>选择模板：</label>
              <a-select v-model:value="selectModel" style="width: 300px;text-align: center;font-weight: bold;">
                <a-select-option v-for="template in templateList" :key="template.id" :value="template.id">
                  {{ template.titleName }}
                </a-select-option>
              </a-select>
            </div>

            <div class="border-div">
              <span>取值范围</span>
              <a-radio-group style="width: 100%" v-model:value="radiovalue">
                <a-radio :value="1"><span>月份：</span></a-radio>
                <a-select v-model:value="formItems.month" :disabled="radiovalue!='1'"
                          style="width: 300px;text-align: center;font-weight: bold;">
                  <a-select-option v-for="item in dateList" :key="item.iperiodNum"
                                   :value="item.iperiodNum">
                    {{ item.iperiodNum }}
                  </a-select-option>
                </a-select>

                <br/>
                <a-radio :value="2"><span>季度：</span></a-radio>
                <a-select v-model:value="formItems.jidu" :disabled="radiovalue!='2'"
                          style="width: 300px;text-align: center;font-weight: bold;">
                  <a-select-option value="1">第一季度</a-select-option>
                  <a-select-option value="2">第二季度</a-select-option>
                  <a-select-option value="3">第三季度</a-select-option>
                  <a-select-option value="4">第四季度</a-select-option>
                </a-select>

                <br/>
                <a-radio :value="3"><span>年度：</span></a-radio>
                <label style="width: 280px;text-align: center;">{{formItems.year}}年</label>
              </a-radio-group>
            </div>
            <div style="margin-left: 30px;">
              <a-checkbox :checked="formItems.jizhang" @change="onChange" style="width: 150px;">
                包含未记账
              </a-checkbox>
              <a-checkbox :checked="formItems.day" @change="onChange1" style="width: 220px;">
                报表报告日精确到天数
              </a-checkbox>
            </div>

          </div>
        </div>
      </div>
      <div class="right-btns">
        <Button style="width: 100px;" shape="round" @click="handleOk"  type="primary">生成</Button>
        <Button style="width: 100px;margin-top: 10px" shape="round" @click="handleClose">取消</Button>
      </div>
    </div>

  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import {onMounted, reactive, ref, unref} from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {Select as ASelect,Input as AInput,Radio as ARadio,Checkbox as ACheckbox,Button} from "ant-design-vue"
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
import {AppstoreOutlined, SearchOutlined,CaretDownOutlined,LinkOutlined} from '@ant-design/icons-vue';

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
const [register, {closeModal,setModalProps}] = useModalInner((data) => {
  setModalProps({ minHeight: 380 });
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
  useRouteApi(findByReportNameAndFlag, {schemaName: dynamicTenantId})("lrb").then(res => {
    templateList.value = res.items
    selectModel.value = ''
    if (templateList.value.length > 0) {
      selectModel.value = templateList.value[0].id
    }
  })
  //获取会计区间
  dateList.value = []
  findPeriodByAccontId(defaultAdName.value).then(res => {
    dateList.value = res.filter(item=>item.iyear==formItems.value.year)
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
  } else {
    closeModal()
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
  useRouteApi(findByReportNameAndFlag, {schemaName: dynamicTenantId})("lrb").then(res => {
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
<style lang="less" scoped>
:deep(.ant-checkbox){
  margin-top: -8px;
}
.nc-open-content {
  background-image: url(/@/assets/images/homes/bg-pattern.png);
  background-repeat: no-repeat;
  background-position: 66% 8%;
  background-size: contain;
  position: relative;
  :deep(.ant-select-selector),:deep(.ant-input),:deep(.ant-picker), :deep(.ant-input-affix-wrapper) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    background: none;
  }
  .border-div {
    position: relative;
    border: 1px #a29f9f solid;
    margin: 20px 10px;
    padding: 2.5%;

    > span {
      display: block;
      text-align: center;
      background-color: white;
      position: absolute;
      left: 50px;
      top: -10px;
      color: #888888;
      font-size: 12px;
      font-weight: bold;
      padding-left: 1em;
      padding-right: 1em;
    }
    :deep(.account-picker){
      >div{
        text-align: left;
      }
    }

    label {
      text-align: left;
      width: 100px;
      display: inline-block;
      color: #535353;
      font-size: 13px;
      font-weight: bold;
    }
    :deep(.ant-input){
      border: none !important;
    }

    :deep(.red_span) {
      color: red;
      font-weight: bold;
      display: inline-block;
      width: 20px;
      text-align: left;
    }

    .ant-select{
      color: #000000;
      font-weight: bold;
    }
  }

}

.right-btns{
  width: 150px;background-color: #f1f1f1;padding: 10% 4%;height: 380px;
  :deep(.ant-btn-primary:hover){
    border: 1px solid #5f375c;
  }
}
:global(.ant-modal-header) {
  padding: 10px 0 !important;
}
:global(.ant-modal-close-x){
  height: 30px !important;
  color: white;
}

:deep(.ant-radio-button-wrapper){
  color: #0096c7;
}
</style>
