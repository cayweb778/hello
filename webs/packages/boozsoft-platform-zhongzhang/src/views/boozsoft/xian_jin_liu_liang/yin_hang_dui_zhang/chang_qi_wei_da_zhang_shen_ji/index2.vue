<template>
  <div>
    <div class="app-container">

      <div class="app-container-head">
        <!--        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">-->
        <div class="container-head-title" style="float: left;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 40%;text-align: center;margin-top: 20px;">
          <b class="noneSpan" style="font-size: 26px;">长期未达账项审计</b>
          <div style="font-size: 14px;text-align: center;margin-top: 20px;">
          <span style="font-size: 14px;font-weight: bold;">
            截止日期：{{ pageParameter.endDate }}
          </span>
          </div>
        </div>

        <!--      <div style="display: inline">
                <a-select v-model:value="kemu" @change="checkDate()" class="head-index-select" placeholder="请选择科目">
                  <a-select-option v-for="item in codeKemu" :key="item.ccode" :value="item.ccode">
                    {{ item.ccode }}-{{ item.ccodeName }}
                  </a-select-option>
                </a-select>
                <a-select v-model:value="year" @change="checkDate()" class="head-index-select" placeholder="请选择年度">
                  <a-select-option v-for="item in yearList" :key="item.accountYear" :value="item.accountYear">
                    {{ item.accountYear }}
                  </a-select-option>
                </a-select>
              </div>-->

        <div class="ant-btn-group" style="float: right">
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="openQuery()"
          ><span>查询</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="exportExcel()"
          ><span>导出</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="printData()"
          ><span>打印</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
          ><span>退出</span></button>
        </div>
      </div>

      <div style="clear:both"/>

      <div style="margin-top: -30px;margin-left: 90px;">
        <div style="display: inline-block;float: left;margin-top: -35px;">
          <AccountPicker readonly theme="three" @reloadTable="dynamicAdReload"/>
        </div>
        <div style="display: inline-block;float: left;margin-left: 5px;font-weight: bold">
          科目：
          <a-select v-model:value="kemu" placeholder="请选择科目" style="width: 200px;" @change="changeKemu()">
            <a-select-option v-for="item in codeKemu" :key="item.ccode" :value="item.ccode">
              {{ item.ccode }}-{{ item.ccodeName }}
            </a-select-option>
          </a-select>
          <span style="font-size: 14px;">&emsp;&emsp;币种：人民币</span>
          <span style="font-size: 14px;">&emsp;&emsp;未达账项：{{ weidaZhang == '0' ? '银行未达账项' : '' }}{{ weidaZhang == '1' ? '企业未达账项' : '' }}</span>
        </div>

        <div style="float: right; margin-left: 10px">
          <a-button class="ant-btn-me">
            <SyncOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
          <a-popover class="ant-btn-default" placement="bottom">
            <template #content>
            <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MAX'"
                  :style="pageParameter.showRulesSize==='MAX'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortDescendingOutlined/>&nbsp;大号字体&ensp;<CheckOutlined
              v-if="pageParameter.showRulesSize==='MAX'"/></span><br/>
              <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MIN'"
                    :style="pageParameter.showRulesSize==='MIN'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortAscendingOutlined/>&nbsp;小号字体&ensp;<CheckOutlined
                v-if="pageParameter.showRulesSize==='MIN'"/></span>
            </template>
            <a-button>
              <SettingFilled :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>
          <a-popover class="ant-btn-default" placement="bottom">
            <a-button>
              <PicLeftOutlined :style="{ fontSize: '14px' }"/>
            </a-button>
            <template #content>
            <span class="group-btn-span-special2" @click="clickWeidaZhang('1')"
                  style="width: 130px;"
                  :style="weidaZhang=='1'?{backgroundColor: '#0096c7',color: 'white'}:''">企业未达账项&nbsp;<CheckOutlined
              v-if="weidaZhang=='1'"/></span><br/>
              <span class="group-btn-span-special2" @click="clickWeidaZhang('0')"
                    style="width: 130px;"
                    :style="weidaZhang=='0'?{backgroundColor: '#0096c7',color: 'white'}:''">银行未达账项&nbsp;<CheckOutlined
                v-if="weidaZhang=='0'"/></span>
            </template>
            <!--          <template #title>
                        <b>对账状态</b>
                      </template>-->
          </a-popover>

          <!--        <a-button>
                    <EditFilled :style="{ fontSize: '14px' }" />
                  </a-button>

                  <a-button>
                    <PieChartFilled :style="{ fontSize: '14px' }" />
                  </a-button>-->
          <!--        <a-button>
                    <FilterFilled :style="{ fontSize: '14px' }" />
                  </a-button>-->
        </div>
      </div>
      <div style="clear:both"/>
    </div>

    <div class="app-container">
      <Accvoucher v-show="weidaZhang=='1'" v-model="pageParameter"   />
      <BankStatement v-show="weidaZhang=='0'" v-model="pageParameter"/>
    </div>
    <Query
      @save="saveQuery"
      @register="registerQueryPage"
    />
  </div>
</template>

<script setup="props, {emit}" lang="ts">
import {
  Input as AInput,
  Select as ASelect,
  Popover as APopover,
  Tabs as ATabs,
  DatePicker as ADatePicker,
  Popconfirm as APopconfirm,
  Checkbox as ACheckbox,
  Radio as ARadio,
  Table as ATable,
  Drawer as ADrawer,
  message
} from "ant-design-vue";

const ARadioButton = ARadio.Button
const ARadioGroup = ARadio.Group
const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
import {useModal} from '/@/components/Modal'
import {
  CaretDownFilled,
  FormOutlined,
  DeleteOutlined,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  EditFilled,
  PieChartFilled,
  FilterFilled,
  CheckOutlined,
  EditOutlined,
  SortAscendingOutlined,
  SortDescendingOutlined,
  ProfileOutlined
} from '@ant-design/icons-vue'
import {onMounted, reactive, ref,provide} from "vue";
import {useMessage} from "/@/hooks/web/useMessage";
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {getThisAdInfoData} from "/@/api/record/system/financial-settings";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findCodeKemuByBr} from "/@/api/record/system/bank-statement";
import BankStatement from "./popup/bank-statement.vue";
import Accvoucher from "./popup/accvoucher.vue";
import Query from './popup/query.vue'
import {findAllByCode, findByUserIdAndAccIdAndYear} from "/@/api/sys-acc-auth/sys-acc-anth";
import {useUserStore} from "/@/store/modules/user";

const {
  createErrorModal
} = useMessage()

const kemu: any = ref('')
const year: any = ref('')
const showPopover = ref(true)
const weidaZhang = ref('1')
const dynamicTenantId = ref(getCurrentAccountName(true))
const defaultAdName = ref(useCompanyOperateStoreWidthOut().getSchemaName)

const defaultPage = ref(false)
const accFun: any = ref({
  exportF : ()=>{},
  printF : ()=>{}
})
const bankFun: any = ref({
  exportF : ()=>{},
  printF : ()=>{}
})
const exportExcel = () => {
  if (weidaZhang.value=='1') {
    accFun.value.exportF()
  } else {
    bankFun.value.exportF()
  }
}

const printData = () => {
  if (weidaZhang.value=='1') {
    accFun.value.printF()
  } else {
    bankFun.value.printF()
  }
}

provide('fun',accFun)
provide('bankFun',bankFun)

const pageParameter: any = reactive({
  showRulesSize: 'MIN',
  companyCode: '100',
  companyName: '北京希格科技',
  ifUnit: false,
  kemu: kemu.value
})
onMounted(async () => {
  // console.log(aaa.value)
  await reloadKemuPage()
  openQueryPage(true, {
    data: {
      dynamicTenantId: dynamicTenantId.value,
      defaultAdName: defaultAdName.value,
      year: year.value,
      kemu: kemu.value,
      weidaZhang: weidaZhang.value,
      showRulesSize: pageParameter.showRulesSize,
      openOne: '1'
    }
  })
  // 进页面执行
  getThisAdInfoData({'accId': getCurrentAccountName(false)}).then(res => {
    if (null != res && res.independent == 0) {
      defaultPage.value = false
      pageParameter.companyName = res.accNameCn
      pageParameter.companyCode = res.coCode
      pageParameter.ifUnit = res.icorp == '1'
    }
  })
})

const codeKemu: any = ref({})

async function reloadKemuPage() {
  codeKemu.value = []
  const res = await useRouteApi(findCodeKemuByBr, {schemaName: dynamicTenantId})({})
  codeKemu.value = res.filter(item => {
    item.value = item.ccode + '-' + item.ccodeName
    return item.bend == '1'
  })
  /*const sysAccAuth = await findByUserIdAndAccIdAndYear({userId:useUserStore().getUserInfo['id'],accId:defaultAdName.value,year:year.value})
  if (sysAccAuth.length>0) {
    if (sysAccAuth[0].supervisor=='1' || sysAccAuth[0].ccodeAll=='1') {
      codeKemu.value = []
      codeKemu.value = res.filter(item => {
        item.value = item.ccode + '-' + item.ccodeName
        return item.bend == '1'
      })
    } else {
      const codeList = await findAllByCode({userId:useUserStore().getUserInfo['id'],accId:defaultAdName.value,year:year.value})
      if (codeList.length>0){
        codeKemu.value = []
        codeList.forEach(code=>{
          res.forEach(item=>{
            if (item.ccode==code && item.bend == '1'){
              item.value = item.ccode + '-' + item.ccodeName
              codeKemu.value.push(item)
            }
          })
        })
      }
    }
  }*/
  // console.log(codeKemu.value)
  /*if (codeKemu.value.length>0) {
    kemu.value = codeKemu.value[0].ccode
  }*/
}

function clickWeidaZhang(str) {
  weidaZhang.value = str
}

function changeKemu() {
  pageParameter.kemu = kemu.value
}

//查询功能
const [registerQueryPage, {openModal: openQueryPage}] = useModal()
const openQuery = () => {
  openQueryPage(true, {
    data: {
      dynamicTenantId: dynamicTenantId.value,
      defaultAdName: defaultAdName.value,
      year: year.value,
      kemu: kemu.value,
      weidaZhang: weidaZhang.value,
      showRulesSize: pageParameter.showRulesSize,
      openOne: '0'
    }
  })
}

async function saveQuery(data) {
  dynamicTenantId.value = data.dynamicTenantId
  defaultAdName.value = data.defaultAdName
  year.value = data.year
  kemu.value = data.kemu
  weidaZhang.value = data.weidaZhang

  pageParameter.endDate = data.endDate
  pageParameter.showRulesSize = data.fontSize
  pageParameter.kemu = kemu.value
  pageParameter.thisAdInfo = {}
  pageParameter.thisAdInfo = data.thisAdInfo
  getThisAdInfoData({'accId': defaultAdName.value}).then(res => {
    if (null != res) {
      defaultPage.value = false
      pageParameter.companyName = res.accNameCn
      pageParameter.companyCode = res.coCode
      pageParameter.ifUnit = res.icorp == '1'
    }
  })
}

const dynamicAdReload = async (obj) => {
  dynamicTenantId.value = obj.accountMode
  defaultAdName.value = obj.accId
  year.value = obj.year
  pageParameter.thisAdInfo = {}
  pageParameter.thisAdInfo = obj
  pageParameter.kemu = kemu.value
  pageParameter.companyName = obj.baseName
  await reloadKemuPage()
  getThisAdInfoData({'accId': defaultAdName.value}).then(res => {
    if (null != res) {
      defaultPage.value = false
      pageParameter.companyName = res.accNameCn
      pageParameter.companyCode = res.coCode
      pageParameter.ifUnit = res.icorp == '1'
    }
  })
}

</script>
<style src="./global-menu-index.less" lang="less" scoped></style>
<style scoped lang="less">
.app-container:nth-of-type(1) {
  background-color: #f2f2f2;
  padding: 10px 5px;
  margin: 10px 10px 5px;
}

.app-container:nth-of-type(2) {
  padding: 0px;
  margin: 5px 10px;
  background: #b4c8e3 !important;
}
</style>
