<template>
  <BasicModal
    width="600px"
    v-bind="$attrs"
    title="批量生成现金流量表"
    @ok="handleOk()"
    @cancel="handleClose()"
    @register="register"
    :loading="loadMark"
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
            批量取值范围
          </div>
          <a-radio-group style="width: 100%" v-model:value="radiovalue">
            <a-radio :value="1"><span>月份：</span></a-radio>
            <a-select v-model:value="formItems.month1" @change="changMonth()"
                      :disabled="radiovalue!='1'" style="width: 150px;">
              <a-select-option v-for="item in dateList1" :key="item.iperiodNum"
                               :value="item.iperiodNum">
                {{ item.iperiodNum }}
              </a-select-option>
            </a-select>
            ～
            <a-select v-model:value="formItems.month2" @change="changMonth()"
                      :disabled="radiovalue!='1'" style="width: 150px;">
              <a-select-option v-for="item in dateList2" :key="item.iperiodNum"
                               :value="item.iperiodNum">
                {{ item.iperiodNum }}
              </a-select-option>
            </a-select>

            <br/>
            <a-radio :value="2"><span>季度：</span></a-radio>
            <a-select v-model:value="formItems.jidu1" @change="changJidu()"
                      :disabled="radiovalue!='2'" style="width: 150px;">
              <a-select-option v-for="item in jiduList1" :key="item.label" :value="item.label">
                {{ item.value }}
              </a-select-option>
            </a-select>
            ～
            <a-select v-model:value="formItems.jidu2" @change="changJidu()"
                      :disabled="radiovalue!='2'" style="width: 150px;">
              <a-select-option v-for="item in jiduList2" :key="item.label" :value="item.label">
                {{ item.value }}
              </a-select-option>
            </a-select>
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
import {BasicModal, useModalInner} from '/@/components/Modal'
import {
  Select as ASelect,
  Input as AInput,
  Radio as ARadio,
  Checkbox as ACheckbox
} from "ant-design-vue"
import {
  findByCashOrderAndTemplateId,
  findByColumn,
  findByReportNameAndFlag
} from "/@/api/record/system/report-template";
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
import {deleteReport, findByDataType, saveReport} from "/@/api/record/system/report-data";

const ASelectOption = ASelect.Option
const ARadioGroup = ARadio.Group

const {
  createErrorModal,
  createConfirm
} = useMessage()

const emit = defineEmits(['register', 'save'])
const loadMark = ref(true)

const templateList: any = ref([])

const selectModel: any = ref('')

let radiovalue: any = ref(1);

const formItems: any = ref({})

const yearList: any = ref([])
const dateList: any = ref([])
const dateList1: any = ref([])
const dateList2: any = ref([])
const jiduList: any = ref([])
const jiduList1: any = ref([])
const jiduList2: any = ref([])

const dynamicTenantId = ref()
const defaultAdName = ref()
const [register, {closeModal}] = useModalInner((data) => {
  loadMark.value = false
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
  useRouteApi(findByReportNameAndFlag, {schemaName: dynamicTenantId})("xjllb").then(res => {
    templateList.value = res.items
    selectModel.value = ''
    if (templateList.value.length > 0) {
      selectModel.value = templateList.value[0].id
    }
  })
  //获取会计区间
  findPeriodByAccontId(defaultAdName.value).then(res => {
    dateList.value = res
    if ((formItems.value.month1 == '' || formItems.value.month1 == null) && dateList.value.length > 0) {
      formItems.value.month1 = dateList.value[0].iperiodNum
      formItems.value.month2 = dateList.value[0].iperiodNum
    }
    dateList1.value = dateList.value.filter(item => item.iperiodNum <= formItems.value.month2)
    dateList2.value = dateList.value.filter(item => item.iperiodNum >= formItems.value.month1)
  })
  jiduList.value = [
    {label: '1', value: '第一季度'},
    {label: '2', value: '第二季度'},
    {label: '3', value: '第三季度'},
    {label: '4', value: '第四季度'}
  ]
  if ((formItems.value.jidu1 == '' || formItems.value.jidu1 == null) && jiduList.value.length > 0) {
    formItems.value.jidu1 = jiduList.value[0].label
    formItems.value.jidu2 = jiduList.value[0].label
  }
  jiduList1.value = jiduList.value.filter(item => item.label <= formItems.value.jidu2)
  jiduList2.value = jiduList.value.filter(item => item.label >= formItems.value.jidu1)
  //获取会计年度
  /*findPeriod(defaultAdName.value).then(res => {
    yearList.value = res
    if (formItems.value.year=='' && yearList.value.length>0) {
      formItems.value.year = yearList.value[0].iyear
    }
  })*/
})
let template: any = {}
const itemConfig: any = ref({})

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

  let msg = ''
  let ids: any = []
  let checkFlag = ''
  if (radiovalue.value == '1') {
    const monthList = dateList.value.filter(item => item.iperiodNum <= formItems.value.month2 && item.iperiodNum >= formItems.value.month1)
    for (const item of monthList) {
      template.iperiod = item.iperiodNum
      const res = await useRouteApi(findByDataType, {schemaName: dynamicTenantId})({
        templateId: template.templateId,
        dataType: template.dataType,
        iyear: template.iyear,
        jidu: template.jidu,
        iperiod: template.iperiod
      })
      if (res.length > 0) {
        msg = msg + template.iperiod + '月,'
        ids.push(res[0].id)
        if (res[0].flag=='1'){
          checkFlag = checkFlag + template.iperiod + '月,'
        }
      }
    }
  } else if (radiovalue.value == '2') {
    const jdList = jiduList.value.filter(item => item.label <= formItems.value.jidu1 && item.label >= formItems.value.jidu2)
    for (const item of jdList) {
      template.jidu = item.label
      const res = await useRouteApi(findByDataType, {schemaName: dynamicTenantId})({
        templateId: template.templateId,
        dataType: template.dataType,
        iyear: template.iyear,
        jidu: template.jidu,
        iperiod: template.iperiod
      })
      if (res.length > 0) {
        msg = msg + item.value + ','
        ids.push(res[0].id)
        if (res[0].flag=='1'){
          checkFlag = checkFlag + template.iperiod + '月,'
        }
      }
    }
  }
  if (checkFlag!=''){
    createErrorModal({
      iconType: 'warning',
      title: '警告',
      content: checkFlag+'已审核不能进行覆盖！'
    })
    return false
  } else if (msg == '') {
    //直接保存所有
    loadMark.value = true
    await batchSave()
    emit('save')
    closeModal()
    return true
  } else {
    createConfirm({
      iconType: 'warning',
      title: '保存',
      content: msg + '报表已存在，是否需要覆盖?',
      onOk: async () => {
        loadMark.value = true
        for (const id of ids) {
          await useRouteApi(deleteReport, {schemaName: dynamicTenantId})(id)
        }
        await batchSave()
        emit('save')
        closeModal()
        return true
      },
      onCancel: () => {
        return false
      }
    })
  }

  // emit('save', unref(template))
  // closeModal()
  return false
}

//批量保存
async function batchSave() {
  if (radiovalue.value == '1') {
    const monthList = dateList.value.filter(item => item.iperiodNum <= formItems.value.month2 && item.iperiodNum >= formItems.value.month1)
    for (const item of monthList) {
      let date = ''
      let timeDate = ''
      if (formItems.value.day == true) {
        if (item.iperiodNum == item.iperiodNum) {
          timeDate = formItems.value.year + '-' + item.dateEnd
        }
      } else {
        timeDate = formItems.value.year + '年' + item.iperiodNum + '月'
      }
      template.iperiod = item.iperiodNum
      date = item.iperiodNum + '月'
      template.dataCode = template.menu6 + formItems.value.year + '年' + item.iperiodNum + '月'
      template.iperiod1 = template.iperiod
      template.iperiod2 = template.iperiod
      await saveOne(timeDate)
    }
  } else if (radiovalue.value == '2') {
    const jdList = jiduList.value.filter(item => item.label <= formItems.value.jidu1 && item.label >= formItems.value.jidu2)
    for (const item of jdList) {
      let date = ''
      let timeDate = ''
      template.jidu = item.label
      date = item.value
      timeDate = formItems.value.year + '年' + date
      template.dataCode = template.menu6 + formItems.value.year + '年' + item.value
      if (formItems.value.jidu == '1') {
        template.iperiod1 = '01'
        template.iperiod2 = '03'
      } else if (formItems.value.jidu == '2') {
        template.iperiod1 = '04'
        template.iperiod2 = '06'
      } else if (formItems.value.jidu == '3') {
        template.iperiod1 = '07'
        template.iperiod2 = '09'
      } else if (formItems.value.jidu == '4') {
        template.iyperiod1 = '10'
        template.iyperiod2 = dateList.value.length
      }
      await saveOne(timeDate)
    }
  }

}

async function saveOne(timeDate) {
  itemConfig.value.reportName = template.reportName
  itemConfig.value.templateId = template.templateId
  itemConfig.value.templateName = template.templateName
  itemConfig.value.accStandard = template.accStandard
  itemConfig.value.kemuTemplateId = template.kemuTemplateId
  itemConfig.value.titleName = template.titleName
  itemConfig.value.dataCode = template.dataCode
  itemConfig.value.dataType = template.dataType
  itemConfig.value.iyear = template.iyear
  itemConfig.value.jidu = template.jidu
  itemConfig.value.iperiod = template.iperiod
  itemConfig.value.idate = template.idate
  itemConfig.value.createUser = template.createUser
  // itemConfig.value.editUser = template.editUser
  itemConfig.value.menu1 = template.menu1
  itemConfig.value.menu2 = template.menu2
  itemConfig.value.menu3 = template.menu3
  itemConfig.value.menu4 = template.menu4
  itemConfig.value.menu5 = template.menu5
  itemConfig.value.menu6 = template.menu6
  //给date赋值
  if (template.menu1.split('&').length == 2 && template.menu1.split('&')[1] == 'date') {
    itemConfig.value.menu1 = template.menu1.split('&')[0] + timeDate
  } else if (template.menu2.split('&').length == 2 && template.menu2.split('&')[1] == 'date') {
    itemConfig.value.menu2 = template.menu2.split('&')[0] + timeDate
  } else if (template.menu3.split('&').length == 2 && template.menu3.split('&')[1] == 'date') {
    itemConfig.value.menu3 = template.menu3.split('&')[0] + timeDate
  } else if (template.menu4.split('&').length == 2 && template.menu4.split('&')[1] == 'date') {
    itemConfig.value.menu4 = template.menu4.split('&')[0] + timeDate
  } else if (template.menu5.split('&').length == 2 && template.menu5.split('&')[1] == 'date') {
    itemConfig.value.menu5 = template.menu5.split('&')[0] + timeDate
  }
  //给操作员姓名赋值
  if (template.menu1.split('&').length == 2 && template.menu1.split('&')[1] == '操作员姓名') {
    itemConfig.value.menu1 = template.menu1.split('&')[0] + useUserStore().getUserInfo['realName']
  } else if (template.menu2.split('&').length == 2 && template.menu2.split('&')[1] == '操作员姓名') {
    itemConfig.value.menu2 = template.menu2.split('&')[0] + useUserStore().getUserInfo['realName']
  } else if (template.menu3.split('&').length == 2 && template.menu3.split('&')[1] == '操作员姓名') {
    itemConfig.value.menu3 = template.menu3.split('&')[0] + useUserStore().getUserInfo['realName']
  } else if (template.menu4.split('&').length == 2 && template.menu4.split('&')[1] == '操作员姓名') {
    itemConfig.value.menu4 = template.menu4.split('&')[0] + useUserStore().getUserInfo['realName']
  } else if (template.menu5.split('&').length == 2 && template.menu5.split('&')[1] == '操作员姓名') {
    itemConfig.value.menu5 = template.menu5.split('&')[0] + useUserStore().getUserInfo['realName']
  }
  useCompanyOperateStoreWidthOut().getAccountList.forEach(item => {
    //给币种赋值
    if (item.accId == defaultAdName.value && item.currency != '' && item.currency != null) {
      if (template.menu1.split('&').length == 2 && template.menu1.split('&')[1] == '币种') {
        itemConfig.value.menu1 = template.menu1.split('&')[0] + item.currencyCh
      } else if (template.menu2.split('&').length == 2 && template.menu2.split('&')[1] == '币种') {
        itemConfig.value.menu2 = template.menu2.split('&')[0] + item.currencyCh
      } else if (template.menu3.split('&').length == 2 && template.menu3.split('&')[1] == '币种') {
        itemConfig.value.menu3 = template.menu3.split('&')[0] + item.currencyCh
      } else if (template.menu4.split('&').length == 2 && template.menu4.split('&')[1] == '币种') {
        itemConfig.value.menu4 = template.menu4.split('&')[0] + item.currencyCh
      } else if (template.menu5.split('&').length == 2 && template.menu5.split('&')[1] == '币种') {
        itemConfig.value.menu5 = template.menu5.split('&')[0] + item.currencyCh
      }
    }
    //给核算单位赋值
    if (item.accId == defaultAdName.value && item.accName != '' && item.accName != null) {
      if (template.menu1.split('&').length == 2 && template.menu1.split('&')[1] == '核算单位') {
        itemConfig.value.menu1 = template.menu1.split('&')[0] + item.accName
      } else if (template.menu2.split('&').length == 2 && template.menu2.split('&')[1] == '核算单位') {
        itemConfig.value.menu2 = template.menu2.split('&')[0] + item.accName
      } else if (template.menu3.split('&').length == 2 && template.menu3.split('&')[1] == '核算单位') {
        itemConfig.value.menu3 = template.menu3.split('&')[0] + item.accName
      } else if (template.menu4.split('&').length == 2 && template.menu4.split('&')[1] == '核算单位') {
        itemConfig.value.menu4 = template.menu4.split('&')[0] + item.accName
      } else if (template.menu5.split('&').length == 2 && template.menu5.split('&')[1] == '核算单位') {
        itemConfig.value.menu5 = template.menu5.split('&')[0] + item.accName
      }
    }
  })

  //表体
  itemConfig.value.zcTable = []
  itemConfig.value.fzTable = []
  itemConfig.value.table = []
  if (itemConfig.value.templateId != '' && itemConfig.value.templateId != null) {
    const columnList = await useRouteApi(findByColumn, {schemaName: dynamicTenantId})(itemConfig.value.templateId)
    const benyueLeijiList = await useRouteApi(findByCashOrderAndTemplateId, {schemaName: dynamicTenantId})({
      templateId: template.templateId,
      iperiod1: template.iperiod1,
      iperiod2: template.iperiod1
    })
    const bennianLeijiList = await useRouteApi(findByCashOrderAndTemplateId, {schemaName: dynamicTenantId})({
      templateId: template.templateId,
      iperiod1: '01',
      iperiod2: template.iperiod2
    })
    columnList.forEach(column => {
      let benyue = 0
      let bennianLeiji = 0
      benyueLeijiList.forEach(item => {
        if (column.id == item.columnId) {
          if (column.formulaMethod == '2') {//科目计算
            if (column.valueMethod == '4') {//借方发生额
              if (item.fuhao == '+') {
                benyue = add(benyue, item.beiyong1)
              } else {
                benyue = sub(benyue, item.beiyong1)
              }
            }
            if (column.valueMethod == '5') {//贷方发生额
              if (item.fuhao == '+') {
                benyue = add(benyue, item.beiyong2)
              } else {
                benyue = sub(benyue, item.beiyong2)
              }
            }
          }
          if (column.formulaMethod == '1') {//行次计算
            if (item.fuhao == '+') {
              benyue = add(benyue, columnList.filter(item1 => {
                return item.ccode == item1.hangci
              }).map(item1 => item1.benyueMoney))
            } else {
              benyue = sub(benyue, columnList.filter(item1 => {
                return item.ccode == item1.hangci
              }).map(item1 => item1.benyueMoney))
            }
          }
        }
      })
      if (benyue != 0 && benyue != NaN) {
        column.benyueMoney = benyue
      }
      bennianLeijiList.forEach(item => {
        if (column.id == item.columnId) {
          if (column.formulaMethod == '2') {//科目计算
            if (column.valueMethod == '4') {//借方发生额
              if (item.fuhao == '+') {
                bennianLeiji = add(bennianLeiji, item.beiyong1)
              } else {
                bennianLeiji = sub(bennianLeiji, item.beiyong1)
              }
            }
            if (column.valueMethod == '5') {//贷方发生额
              if (item.fuhao == '+') {
                bennianLeiji = add(bennianLeiji, item.beiyong2)
              } else {
                bennianLeiji = sub(bennianLeiji, item.beiyong2)
              }
            }
          }
          if (column.formulaMethod == '1') {//行次计算
            if (item.fuhao == '+') {
              bennianLeiji = add(bennianLeiji, columnList.filter(item1 => {
                return item.ccode == item1.hangci
              }).map(item1 => item1.bennianLeijiMoney))
            } else {
              bennianLeiji = sub(bennianLeiji, columnList.filter(item1 => {
                return item.ccode == item1.hangci
              }).map(item1 => item1.bennianLeijiMoney))
            }
          }
        }
      })
      if (bennianLeiji != 0 && bennianLeiji != NaN) {
        column.bennianLeijiMoney = bennianLeiji
      }
    })
    itemConfig.value.table = columnList.map(item => {
      item.id = null
      return item
    })
  }
  await useRouteApi(saveReport, {schemaName: dynamicTenantId})(itemConfig.value)
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

function changMonth() {
  dateList1.value = dateList.value.filter(item => item.iperiodNum <= formItems.value.month2)
  dateList2.value = dateList.value.filter(item => item.iperiodNum >= formItems.value.month1)
}

function changJidu() {
  jiduList1.value = jiduList.value.filter(item => item.label <= formItems.value.jidu2)
  jiduList2.value = jiduList.value.filter(item => item.label >= formItems.value.jidu1)
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
  useRouteApi(findByReportNameAndFlag, {schemaName: dynamicTenantId})("xjllb").then(res => {
    templateList.value = res.items
    if (templateList.value.length > 0) {
      selectModel.value = templateList.value[0].id
    } else {
      selectModel.value = ''
    }
  })
  findPeriodByAccontId(defaultAdName.value).then(res => {
    dateList.value = res
    if ((formItems.value.month1 == '' || formItems.value.month1 == null) && dateList.value.length > 0) {
      formItems.value.month1 = dateList.value[0].iperiodNum
      formItems.value.month2 = dateList.value[0].iperiodNum
    }
    dateList1.value = dateList.value.filter(item => item.iperiodNum <= formItems.value.month2)
    dateList2.value = dateList.value.filter(item => item.iperiodNum >= formItems.value.month1)
  })
}

//加
function add(a, b) {
  let c, d, e;
  try {
    c = a.toString().split(".")[1].length;
  } catch (f) {
    c = 0;
  }
  try {
    d = b.toString().split(".")[1].length;
  } catch (f) {
    d = 0;
  }
  return e = Math.pow(10, Math.max(c, d)), (a * e + b * e) / e;
}

//减
function sub(a, b) {
  let c, d, e;
  try {
    c = a.toString().split(".")[1].length;
  } catch (f) {
    c = 0;
  }
  try {
    d = b.toString().split(".")[1].length;
  } catch (f) {
    d = 0;
  }
  return e = Math.pow(10, Math.max(c, d)), (a * e - b * e) / e;
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
