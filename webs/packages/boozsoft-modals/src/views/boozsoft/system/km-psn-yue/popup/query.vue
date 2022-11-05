<template>
  <BasicModal
    width="880px"
    class="spaceLogo"
    v-bind="$attrs"
    @ok="handleOk()"
    @cancel="handleClose()"
    @register="register"
    :loading="modelLoadIng"
    loadingTip="查询参数初始化中"
  >
    <template #title>
      <div style="height: 30px;width: 100%;background-color: #5f375c;color: white;line-height: 30px;text-align: left;">
        <AppstoreOutlined  style="margin: 0 2px;font-size: 14px;"/>
        <span style="font-size: 13px">账簿</span>
      </div>
    </template>

    <div class="nc-open-content" style="height: 100%" >
      <SearchOutlined style="font-size: 24px;color: #0096c7;margin-top: 2px;margin-left: 40%;"/>
      <span style="line-height:30px;font-size: 24px;color: #0096c7;font-weight: bold;">&ensp;{{ prop.queryType }}</span>
      <div class="open-content-up">
        <div class="ocup-position">
          <PicLeftOutlined/>
          查询方案
        </div>
        <a-tabs type="card" v-model:activeKey="currentTabsKey" tabPosition="left" @change="tabChange">
          <a-tab-pane key="1" tab="标准模式" style="overflow-y: hidden;">
            <ul style="width: 93%;margin-top: -20px;">
              <li>
                <div class="special-border-div">
                  <span style="color: #5a5a5a">业务范围</span>
                  <AccountPicker theme="one" @reloadTable="dynamicAdReload" style="display: block;"/>
                </div>
              </li>
              <li>
                <div class="special-border-div">
                  <span style="color: #5a5a5a">查询日期</span>
                  <div>
                    <div class="sbd-left">
                      <span>期间：</span>
                      <a-select
                        v-model:value="strDate"
                        show-search
                        option-filter-prop="children"
                        style="width: 120px;text-align: center;"
                        @focus="focusStrDate"
                        @change="handleChangeStrDate"
                      >
                        <a-select-option
                          v-for="item in strDateList"
                          :key="item.id"
                          :value="item.value"
                        >
                          {{ item.value }}
                        </a-select-option>
                        <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
                      <span>&emsp;~&emsp;</span>
                      <a-select
                        v-model:value="endDate"
                        show-search
                        option-filter-prop="children"
                        style="width: 120px;text-align: center;"
                        @focus="focusEndDate"
                        @change="handleChangeEndDate"
                      >
                        <a-select-option
                          v-for="item in endDateList"
                          :key="item.id"
                          :value="item.value"
                        >
                          {{ item.value }}
                        </a-select-option>
                        <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
                    </div>
                    <div class="sbd-right">
                      <a-radio-group size="small" @change="checkChange">
                        <a-radio-button value="1">上月</a-radio-button>
                        <a-radio-button value="2">本月</a-radio-button>
                        <a-radio-button value="3">本季</a-radio-button>
                        <a-radio-button value="4">本年</a-radio-button>
                      </a-radio-group>
                    </div>
                  </div>
                </div>
              </li>
              <li>
                <div class="special-border-div">
                  <span style="color: #5a5a5a">查询条件</span>
                  <div style="display: inline-block;width: 96%;margin-left: 23px;">
                    <div style="width: 50%;float: left;">
                      <span class="right_span">科目类型：</span>
                      <a-select v-model:value="styleValue" style="width: 160px;text-align: center;" @change="styleChange">
                        <a-select-option v-for="d in styleList" :value="d.cclass">
                          {{ d.cclass }}
                        </a-select-option>
                        <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                      </a-select>
                    </div>
                    <div style="width: 50%;float: left;">
                      <span class="right_span">凭证类别：</span>
                      <a-select v-model:value="pzType" style="width: 160px;text-align: center;">
                        <a-select-option v-for="d in pzTypeList" :value="d">
                          {{ d }}
                        </a-select-option>
                        <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                      </a-select>
                    </div>
                    <div>
                      <span class="right_span">会计科目：</span>
                      <a-select
                        v-model:value="minKm"
                        show-search
                        allowClear=true
                        option-filter-prop="children"
                        style="width: 450px;text-align: center;"
                        :filter-option="filterOption"
                        @focus="handleFocusMinKm"
                      >
                        <a-select-option v-for="d in minkmList" :value="d.ccode">
                          {{ d.value }}
                        </a-select-option>
                        <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
                    </div>
                    <div>
                      <span class="right_span">个人：</span>
                      <a-select
                        v-model:value="minDept"
                        show-search
                        style="width: 450px;text-align: center;"
                        allowClear=true
                        :filter-option="filterOption"
                        @change="handleChangeMinDept"
                      >
                        <a-select-option v-for="d in deptList" :value="d.uniqueCode">
                          {{ d.label }}
                        </a-select-option>
                        <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
                    </div>
                    <div style="float: left;">
                      <span class="right_span">币种：</span>
                      <a-select style="width: 170px;text-align: center;" v-model:value="bz">
                        <a-select-option v-for="d in bzList" :value="d.id">
                          {{ d.currChName }}
                        </a-select-option>
                        <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
                    </div>
                    <div style="float: left;display: flex;margin-top: 5px;margin-left: 70px;">
                      <span class="right_span" style="margin-top: 5px;">包含未记账：</span>
                      <a-checkbox v-model:checked="ishaveRjz" style="width: 130px" />
                    </div>
                  </div>
                </div>
              </li>
            </ul>
          </a-tab-pane>
        </a-tabs>
      </div>
    </div>
  </BasicModal>
</template>

<script setup="props, { content }" lang="ts">
import {CaretDownOutlined,SearchOutlined} from '@ant-design/icons-vue'
import {ref, unref, onMounted, reactive} from 'vue';
import { BasicModal, useModalInner } from '/@/components/Modal';
import {
  DatePicker as ADatePicker,
  Select as ASelect,
  Input as AInput,
  Checkbox as ACheckbox,
  Popover as APopover,
  Switch as ASwitch,
  Radio as ARadio,
  Upload as AUpload,
  Table as ATable,
  Tabs as ATabs,
  message,
} from 'ant-design-vue';
import { useTabs } from '/@/hooks/web/useTabs';
import router from '/@/router';
import {
  findPeriodByAccontId,
  findBzAll,
  findCodeKmByPeriod, findCusAll, findAllAuthPeriodListByUserAndCode, findPsnAll
} from '/@/api/record/generalLedger/data';
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {useUserStore, useUserStoreWidthOut} from "/@/store/modules/user";
import {
  findByAccStyle,
  findByStandardUnique
} from "/@/api/accstandard/accstandard";
import {company_findByAccTypeAuth, findByAccId, findDataBase} from "/@/api/record/system/account";
import {groupStandardAndTemplate} from "/@/api/codekemu/codekemu";
import {
  currentAccountTypes,
  filterAccListByAuth,
  getAdInfoDatas,
  getAllAccAuths
} from "/@/api/record/system/financial-settings";
import {findByQueryPlan, saveQueryPlan} from "/@/api/query-plan/queryPlan";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {useMessage} from "/@/hooks/web/useMessage";
import TimeTool from "/@/boozsoft/components/SelectTimeTools/TimeTool.vue";
import {findByAccIdAndIyear} from "/@/api/record/system/sys_data_auth_swith";
import moment from "moment";
import dayjs from "dayjs";
import {saveLog} from "/@/api/record/system/group-sys-login-log";

const prop = defineProps(['queryType'])
const { closeCurrent } = useTabs(router);
const ARangePicker = ADatePicker.RangePicker;
const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const ARadioGroup = ARadio.Group;
const ARadioButton = ARadio.Button;
const ATabPane = ATabs.TabPane;
const emit=defineEmits([]);
const data = [];
const queryPlan: any = ref([]);
const formItems: any = ref({});
let changeBeforeModel: any = {};
// 会计区间
const dateList: any = ref([]);
// 会计科目
const kmList: any = ref([]);
//币种
const bzList: any = ref([]);
const currentTabsKey = ref<string>('1');
const month = useCompanyOperateStoreWidthOut().getLoginDate.split('-')[1];
const endDate = ref<String>('');
const strDate = ref<String>('');
let endDateList: any = ref([]);
let strDateList: any = ref([]);

let minkmList: any = ref([]);
const minKm = ref<string>();
//部门
const deptList: any = ref([])
const minDept = ref<string>();
const maxDept = ref<string>();

const showStyle = ref<string>('J');
const fontSize = ref<string>('MIN');
const bz = ref<string>('');

const modelLoadIng = ref<boolean>(false);
const ishaveRjz = ref<boolean>(true);
const userStore = useUserStore();
//查询条件
const seach: any = ref({});
let styleValue: any = ref('全部');
let styleList: any = ref([]);
// 数据库模式名称
const database = ref(getCurrentAccountName(true));
const accId = ref(getCurrentAccountName(false));
const accYear = ref('');
// 页面变量
const pageParameter = reactive({
  companyCode: '',
  companyName: '',
  accId: '',
  database: '',
  accNameAll: '',
})
const pzType: any = ref([]);
const pzTypeList: any = ref([]);
const authSwithIsCcode=ref('');
const [register, { closeModal, setModalProps }] = useModalInner((data) => {
  // 方式2
  formItems.value.openOne = data.data.openOne;
  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value));
  setModalProps({ minHeight: 400 });
});

const quarterList: any = ref([])
const busDate = useCompanyOperateStoreWidthOut().getLoginDate
const checkChange = (e) => {
  let v = e.target.value
  let dates = strDate.value.split('-')
  switch (v) {
    case '1':
      let the = parseInt(dates[1])
      if (the != 1) {
        strDate.value = dates[0] + '-' + (((the - 1) > 9) ? `${(the - 1)}` : `0${(the - 1)}`)
        endDate.value = strDate.value
      } else {
        message.info('暂无上月！')
      }
      break
    case '2':
      strDate.value = dates[0] + '-' + busDate.split('-')[1]
      endDate.value = strDate.value
      break
    case '3':
      let qList = quarterList.value.filter(it => it.iyear == dates[0])
      if (qList.length > 0) { // 存在季报
        let arr = qList.filter(it => {
          let min = moment(it.iyear + '-' + it.dateStart, 'YYYY-MM-DD')
          let max = moment(it.iyear + '-' + it.dateEnd, 'YYYY-MM-DD')
          let the = moment(strDate.value + '-01', 'YYYY-MM-DD')
          return min <= the && the <= max
        })
        strDate.value = arr[0].iyear + '-' + arr[0].dateStart.substring(0, 2)
        endDate.value = arr[0].iyear + '-' + arr[0].dateEnd.substring(0, 2)
      } else {
        let min = '01'
        let max = '03'
        let v = dates[1]
        switch (v) {
          case '04':case '05':case '06':
            min = '04'
            max = '06'
            break
          case '07' :case '08' :case '09':
            min = '07'
            max = '09'
            break
          case '10' :case '11' :case '12':
            min = '10'
            max = '12'
            break
        }
        strDate.value = dates[0] + '-' + min
        endDate.value = dates[0] + '-' + max
      }
      break
    case '4':
      let list = dateList.value.filter(it => it.iyear == dates[0])
      strDate.value = list[0].value
      endDate.value = list[list.length - 1].value
      break
    case '5':
      let d = dayjs(busDate, 'YYYY-MM-DD')
      riqi.value = [d, d]
      break
    case '6':
      let yday = dayjs(busDate, 'YYYY-MM-DD').add(-1, 'day')
      riqi.value = [yday, yday]
      break
    case '7':
      let yday1 = dayjs(busDate, 'YYYY-MM-DD').add(-2, 'day')
      riqi.value = [yday1, yday1]
      break
    case '8':
      let yday2 = dayjs(busDate, 'YYYY-MM-DD').endOf('month')
      riqi.value = [yday2, yday2]
      break
  }
}

function tabChange(val) {
  if(val===''){
    queryPlan.value=[]
    strDate.value=accYear.value + '-01'
    endDate.value=accYear.value + '-'+month
    styleValue.value='全部'
    minKm.value=''
    minDept.value=''
    maxDept.value=''
    bz.value=''
    fontSize.value='MIN'
    ishaveRjz.value=true
  }else if(val==='1'){getQueryPlan()}
}

let isChanged = false;
async function handleOk() {
  if(kmList.value.length===0){
    return createConfirmPop('请先设置个人辅助科目')
  }
  if(deptList.value.length===0){
    return createConfirmPop('请先设置个人信息')
  }
  if (strDate.value.length <= 0 && endDate.value.length <= 0) {
    return createConfirmPop('请选择会计区间')
  }
  formItems.value.strDate = strDate.value;
  formItems.value.endDate = endDate.value;
  formItems.value.minDept = minDept.value===undefined || minDept.value===''?'':minDept.value;
  formItems.value.maxDept = maxDept.value===undefined || maxDept.value===''?'':maxDept.value;
  formItems.value.km = minKm.value===undefined ||minKm.value==='' ?kmList.value[0].ccode:minKm.value;
  formItems.value.showStyle = showStyle.value;
  formItems.value.fontSize = fontSize.value;
  formItems.value.bzName = bz.value==='全部'?"":bzList.value.filter((o) => o.id === bz.value)[0].currChName;
  formItems.value.ishaveRjz = ishaveRjz.value;
  formItems.value.styleValue = styleValue.value;

  formItems.value.pageParameter = pageParameter;
  formItems.value.database = database.value;
  // 过滤部门
  let list = deptList.value;
  if (maxDept.value) {
    list = list.filter((o) => parseInt(o.uniqueCode) <= parseInt(maxDept.value));
  }
  if (minDept.value) {
    list = list.filter((o) => parseInt(o.uniqueCode) >= parseInt(minDept.value));
  }
  formItems.value.deptList = list;
  formItems.value.kmList = kmList.value;

  const queryPlanEntity={
    accountId:database.value,
    owningMenuName:prop.queryType,
    owningPlan:currentTabsKey.value,
    planPerson:userStore.getUserInfo.id,
    queryConditions:{
      strDate:formItems.value.strDate,
      endDate:formItems.value.endDate,
      styleValue:styleValue.value,
      minKm:formItems.value.km,
      minDept:formItems.value.minDept,
      maxDept:formItems.value.maxDept,
      bz:formItems.value.bzName,
      fontSize:formItems.value.fontSize,
      ishaveRjz:formItems.value.ishaveRjz,
    }
  }
  // await saveQueryPlan(queryPlanEntity);

  /************** 记录操作日志 ****************/
  let map={
    userId: useUserStoreWidthOut().getUserInfo.id,
    userName: useUserStoreWidthOut().getUserInfo.realName,
    optModule:'gl',
    optFunction:'总账',
    optRange:'1',// 1 账套
    uniqueCode:accId.value,
    optAction:'查询',
    optContent:'操作内容【'+prop.queryType+'查询】,账套代码【'+pageParameter.companyCode+'】,账套名称【'+pageParameter.companyName+'】,查询条件【查询日期：'+strDate.value+','+endDate.value+'】,' +
      '科目类型【'+styleValue.value+'】,凭证类别【'+pzType.value+'】,会计科目【'+minKm.value+'】,客户【'+minDept.value+','+maxDept.value+'】,币种【'+bz.value+'】,包含未记账【'+ishaveRjz.value+'】'
  }
  await saveLog(map)
  /************** 记录操作日志 END ****************/
  emit('save', unref(formItems));
  closeModal();
  return true;
}

async function styleChange(val) {
  minKm.value=''
  formItems.value.minKm = '';
  formItems.value.maxKm = '';
  minkmList.value=[]

  const start =strDate.value;
  // 重新获取科目类型
  // 获取对应的账套科目 所属的 会计准则、科目模板
  let codelistall= await useRouteApi(groupStandardAndTemplate, {schemaName: database.value})({databaseNum:database.value,iyear:strDate.value.split('-')[0]});
  const accstandard= await findByStandardUnique(codelistall[0].uniqueAccStandard)
  // 重新获取科目类型
  let accstyle= await findByAccStyle(accstandard.accStyleUnique)
  if(val=='全部'){
    let list=await newfindAllCode(database.value,start);
    minkmList.value=list
  }
  if(val!=='全部'){
    if(val!=='财务会计'&&val!=='预算会计'){
      let list=await newfindAllCode(database.value,start);
      minkmList.value=list.filter(ck=>ck.cclass===val)
    }else if(val=='财务会计'){
      for (let i = 0; i < accstyle.length; i++) {
        if(accstyle[i].flagYusuan!=='1'){
          let list=await newfindAllCode(database.value,start);
          for (let j = 0; j < list.length; j++) {
            minkmList.value.push({
              ccode:list[j].ccode,
              value:list[j].ccode+'-'+list[j].ccodeName,
            });
          }
        }
      }
    }else if(val=='预算会计'){
      for (let i = 0; i < accstyle.length; i++) {
        if(accstyle[i].flagYusuan=='1'){
          let list=await newfindAllCode(database.value,start);
          for (let j = 0; j < list.length; j++) {
            minkmList.value.push({
              ccode:list[j].ccode,
              value:list[j].ccode+'-'+list[j].ccodeName,
            });
          }
        }
      }
    }
  }
}
async function newfindAllCode(s,start) {
  // 会计科目
  let list=await useRouteApi(findCodeKmByPeriod,{schemaName:s})({ strDate: start, endDate: 'test', accId: accId.value,userId:userStore.getUserInfo.id });
  return list.filter(ck=>ck.bcus==='1' && ck.bend==='1');
}

async function handleClose() {
  if (null != formItems.value.openOne && formItems.value.openOne == 1) {
    await closeCurrent();
    router.push('/zhongZhang/home/welcome');
  }
}
const handleChangeMinDept = () => {
  // if(maxDept.value && maxDept.value < minDept.value){
  //   maxDept.value = minDept.value
  // }
};
const handleFocusMaxDept = () => {
  //获取焦点时 如果最小科目已选择 判断过滤小于最小部门的数据
  if(maxDept.value){
    deptList.value = deptList.value.filter( o => o.uniqueCode >= minDept.value)
  }else{
    deptList.value = deptList.value
  }
};

async function handleChangeStrDate() {
  // 会计科目
  kmList.value=await useRouteApi(findCodeKmByPeriod,{schemaName:database.value})({ strDate: strDate.value, endDate: 'test', accId: accId.value,userId:userStore.getUserInfo.id });
}
async function handleChangeEndDate() {
  // 开始日期变动  如果结束日期小于开始日期 则重置开始日期
  if (strDate.value) {
    if (strDate.value > endDate.value) {
      strDate.value = '';
    }
    // 会计科目
    let list=await useRouteApi(findCodeKmByPeriod,{schemaName:database.value})({ strDate: endDate.value, endDate: 'test', accId: accId.value,userId:userStore.getUserInfo.id });
    let list2= list.filter(function (x) {
      return x.bcus==='1' && x.bend==='1'
    })
    kmList.value=list2
  }
  if(authSwithIsCcode.value==='0'){
    if(kmList.value.length===0){
      minKm.value=''
    }
  }
}
async function focusStrDate() {
  if (endDate.value) {
    strDateList.value = dateList.value.filter((o) => o.value <= endDate.value);
  } else {
    strDateList.value = dateList.value;
  }
}
async function focusEndDate() {
  if (strDate.value) {
    endDateList.value = dateList.value.filter((o) => o.value >= strDate.value);
  } else {
    endDateList.value = dateList.value;
  }
}

const handleFocusMinKm = () => {
  minkmList.value = kmList.value;
};

const filterOption = (input: string, option: any) => {
  return option.children[0].children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

/*************************************************************************************/
const getAdObjInfoByCoCode = (value, type,accList) => {
  let list = accList.filter(item => item[type] == value)
  return list.length > 0 ? list[0] : null
}
async function findByDateBase() {
  // 获取所有账套信息 并过滤已授权的账套
  let accList = await getAdInfoDatas()
  // 获取当前用户的权限根据用户信息汇率
  let test1 = await getAllAccAuths({'userId': userStore.getUserInfo.id})
  let test2 = filterAccListByAuth(accList, test1)
  let codes = getAdObjInfoByCoCode(accId.value, 'accId',test2)
  if (codes != null) {
    pageParameter.companyCode = codes.coCode
    pageParameter.companyName = codes.accNameCn
  }
}

async function AllCondition() {
  //币种
  let currency=await useRouteApi(findBzAll, {schemaName: database})('');
  bzList.value = currency.filter(c=>c.currChName.indexOf('人民币')<0);
  bzList.value.push({ id: '', currChName: '全部' });
  // 客户
  deptList.value = await useRouteApi(findPsnAll,{schemaName:database.value})('');
  modelLoadIng.value=false;
}
async function findByAccStyleAll(accId) {
  const codelistall= await useRouteApi(groupStandardAndTemplate, {schemaName: database})({databaseNum: database.value,iyear:strDate.value.substring(0,4)});
  const accstandard= await findByStandardUnique(codelistall[0].uniqueAccStandard)
  const accStyleUnique= await findByAccStyle(accstandard.accStyleUnique)
  styleList.value=[]
  styleList.value.push({
    cclass:'全部',
    flagYusuan:''
  })
  for (let i = 0; i < accStyleUnique.length; i++) {
    styleList.value.push({
      cclass:accStyleUnique[i].cclass,
      flagYusuan:accStyleUnique[i].flagYusuan
    })
  }
  // 获取账套信息，判断是否预算会计
  const datainfo = await findByAccId(accId);
  if(datainfo.ibudgetAccounting==='1'){   // 是
    styleList.value.push({
      cclass:'财务会计',
      flagYusuan:'1'
    })
    styleList.value.push({
      cclass:'预算会计',
      flagYusuan:'1'
    })
  }
}
async function dynamicAdReload(data) {
  modelLoadIng.value=true
  // 加载DB所有会计期间与季报数据
  let allList = await findAllAuthPeriodListByUserAndCode({
    accId: data.accId,
    code: data.coCode,
    user: userStore.getUserInfo.id
  });
  quarterList.value = allList.filter(it => it.beiyong1 == '1')

  await findDataBase(data.accId,+data.year).then(async (item)=>{
    console.log(item)
    accId.value=item.accountId
    database.value =item.accountMode
    accYear.value =item.accountYear
    pageParameter.accId=item.accountId
    pageParameter.database=item.accountMode
    pageParameter.accNameAll=item.accName
    dateList.value=[]

    // 是否有个人查询记录
    // getQueryPlan()
    //=================================== 按照查询循序 ==============================================
    // 重新加载年度
    dateList.value = await findPeriodByAccontId(item.accountId);
    // 1、期间
    strDate.value=accYear.value+'-01'
    endDate.value=accYear.value+'-'+month
    // 2、科目类型
    findByAccStyleAll(item.accountId)
    // 3、凭证类型
    findByPzTypeList()
    // 4、科目
    handleChangeEndDate();
    // 5 客户、币种
    AllCondition();
  })
}
// 获取凭证类型权限
// 先判断是否主管：是主管 查询全部凭证类别【凭证类别表】
//               不是主管 判断 是否有 查询全部凭证类别权限
//                                  是 查询全部【凭证类别表】
//                                  不是 查询 【凭证类别权限授权表】
async function findByPzTypeList() {
  pzTypeList.value=[]
  pzType.value=[]

  let newPzTypeList:any=[]
  // 凭证类型权限授权表
  let pztypeAuth =await useRouteApi(company_findByAccTypeAuth,{schemaName: database})({userId:userStore.getUserInfo.id,iyear:accYear.value})
  // 凭证类别表
  await useRouteApi(currentAccountTypes,{schemaName: database})('')
    .then(pztypeAll=>{
      pztypeAll.items.forEach(a=>{
        newPzTypeList.push(a.voucherTypeCode)
      })
    })

  // 数据权限表
  let authSwith= await useRouteApi(findByAccIdAndIyear,{schemaName: database})({accId:accId.value,iyear:accYear.value})
  // 没有开启 查询全部权限
  if(authSwith!=='不存在' && authSwith.isVoucherType=='0'){
    authSwithIsCcode.value=authSwith.isCcode
    let test1 = await getAllAccAuths({'userId': userStore.getUserInfo.id})
    const list = test1.filter(v => v.iyear === strDate.value.split('-')[0] && v.accId === accId.value)
    if(list.length>0){
      // 1是0否有主管权限
      if(list[0].supervisor =='0'){
        // 1是0否有查询所有凭证类型权限
        if(list[0].accvocherType =='0'){
          if(pztypeAuth.length==0 ){
            pzType.value=''
            return false
          }
          newPzTypeList=[]
          pztypeAuth.forEach(a=>{
            newPzTypeList.push(a.accvocherType)
          })
        }
      }
    }
    if(list.length==0 ){
      pzType.value=''
      return false
    }
  }
  else{
    let test1 = await getAllAccAuths({'userId': userStore.getUserInfo.id})
    const list = test1.filter(v => v.iyear === strDate.value.split('-')[0] && v.accId === accId.value)
    if(list.length>0){
      // 1是0否有主管权限
      if(list[0].supervisor =='0'){
        // 1是0否有查询所有凭证类型权限
        if(list[0].accvocherType =='0'){
          if(pztypeAuth.length==0 ){
            pzType.value=''
            return false
          }
          newPzTypeList=[]
          pztypeAuth.forEach(a=>{
            newPzTypeList.push(a.accvocherType)
          })
        }
      }
    }
    if(list.length==0 ){
      pzType.value=''
      return false
    }
  }
  pzTypeList.value=newPzTypeList
  return true
}
async function getQueryPlan() {
  let queryPlanIngo= await findByQueryPlan(database.value,prop.queryType)
  if(queryPlanIngo!=null){
    currentTabsKey.value='1'
    queryPlan.value=queryPlanIngo

    strDate.value=JSON.parse(queryPlanIngo.queryConditions).strDate
    endDate.value=JSON.parse(queryPlanIngo.queryConditions).endDate
    styleValue.value=JSON.parse(queryPlanIngo.queryConditions).styleValue
    minKm.value=JSON.parse(queryPlanIngo.queryConditions).minKm
    minDept.value=JSON.parse(queryPlanIngo.queryConditions).minDept
    maxDept.value=JSON.parse(queryPlanIngo.queryConditions).maxDept
    bz.value=JSON.parse(queryPlanIngo.queryConditions).bz
    fontSize.value=JSON.parse(queryPlanIngo.queryConditions).fontSize
    ishaveRjz.value=JSON.parse(queryPlanIngo.queryConditions).ishaveRjz
  }
}
const {createConfirm, createWarningModal, createMessage} = useMessage();
function createConfirmPop(text) {
  createConfirm({
    iconType: 'warning',
    title: '警告',
    content: text,
    onOk: async() => {},
  })
  return false;
}
async function timeReload(data) {
  strDate.value=data.strDate
  endDate.value=data.endDate
}
</script>
<style lang="less" scoped>
@import '/@/assets/styles/redTitle-open.less';
:deep(.ant-select-selection-item){
  font-weight: bold;
}
.nc-open-content {
  background-image: url(/@/assets/images/homes/bg-pattern.png);
  background-repeat: no-repeat;
  background-position: 66% 8%;
  background-size: contain;
  position: relative;
  text-align: left;

  .open-content-title {
    > div {
      display: inline-block;
    }

    > div:nth-of-type(1) {
      width: 200px;
      background-color: #efeeee;
      color: black;
      font-size: 20px;
      text-align: center;
      padding: 5px 10px
    }
  }

  .open-content-up {
    position: relative;
    top: 15px;

    .ocup-position {
      position: absolute;
      left: 0;
      width: 170px;
      background-color: #65cbec;
      color: white;
      font-size: 16px;
      text-align: left;
      padding: 5px 10px;
    }

    .ocup-position:nth-of-type(1) {
      top: 0px;
    }

    .ocup-position:nth-of-type(2) {
      top: 190px;
    }

    ul {
      padding: 10px;

      li {
        margin: 10px 0;

        //span {
        //  font-size: 14px;
        //  color: #5a5a5a;
        //}

        .right_span{
          display: inline-block;
          width: 90px !important;
          color: #5a5a5a;
          font-weight: bold;
        }

        .ant-select {
          font-size: 14px;
        }
      }


    }
  }

  .open-content-foot {
    display: block;
    position: fixed;
    margin-top: 43px;
  }

  .ant-tabs-tabpane-active {
    overflow-y: auto;
    height: 400px;
  }

  .ant-select-selection-search-input {
    border-bottom: none !important;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-input) {
    background: none !important;
  }

  :deep(.ant-select-selector), :deep(.ant-input-affix-wrapper),:deep(.ant-input){
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    background: none;
  }

  label:not(.ant-radio-button-wrapper) {
    text-align: right;
    width: 110px;
    display: inline-block;
    padding: 5px 10px;
  }

  .ant-radio-group {
    .ant-radio-wrapper {
      //width: 70px;

      .ant-radio-input {
        border-color: slategrey;
      }
    }

    p:nth-of-type(2) {
      margin-bottom: 0;
    }
  }

  :deep(.ant-picker-range) {
    background: none;
    border: none;
    border-bottom: 1px solid #bdb9b9;

    input {
      text-align: center;
    }
  }

  .special-border-div {
    position: relative;
    border: 1px #acabab solid;
    margin: 20px 0;
    text-align: left;

    .spanclass{
      font-weight: bold;
      color: #5a5a5a;
    }
    > span {
      position: absolute;
      top: -12px;
      left: 50px;
      font-size: 12px;
      background: white;
      padding: 0 15px;
      font-weight: bold;
    }

    > div {
      margin: 15px;
      color: black;
      display: flex;

      .sbd-left {
        width: 60%;

        .ant-radio-group {
          .ant-radio-wrapper {
            width: 11% !important;
            text-align: left;

            .ant-radio-input {
              border-color: slategrey;
            }
          }
        }
      }

      .sbd-right {
        width: 40%;
        padding: 1% 4% 0;
        .ant-radio-button-wrapper {
          color: #0096c7;
        }
      }

    }
  }
}

:deep(.ant-tabs-left-bar) {
  margin-right: 0px !important;
}

:deep(.ant-tabs-left) {
  .ant-tabs-tab {
    width: 170px;
    font-weight: bold;
    display: inline-block;
  }

  .ant-tabs-tab-active {
    color: #65cbec !important;
    .ant-tabs-tab-btn:after{
      content: '√';
      margin-left: 5px;
    }
  }

  .ant-tabs-tabpane-active {
    padding-left: 0 !important;
  }

  .ant-tabs-tab:nth-of-type(1) {
    margin-top: 35px;
  }

  /*  .ant-tabs-tab:nth-of-type(3) {
      margin-top: 110px !important;
    }*/
}
:deep(.ant-cascader-input){
  border: none;
  border-bottom: 1px solid #bdb9b9;
}
</style>
