<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="核销历史"
    @ok="handleOk()"
    @cancel="handleClose()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up">
        <div class="ocup-position"> 系统方案 </div>
        <div class="ocup-position"> 个人方案 </div>
        <a-tabs v-model:activeKey="activeKey" tabPosition="left" type="card" @change="tabChange">
          <a-tab-pane key="" tab="标准模式">
            <ul>
              <li>
                <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
              </li>
              <li><span>科&emsp;&emsp;目：</span>
                <a-select
                  v-model:value="minKm"
                  show-search
                  style="width: 180px;text-align: center;"
                  allowClear="true"
                  :filter-option="filterOption"
                >
                  <a-select-option v-for="d in kmList" :value="d.ccode">
                    {{ d.value }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <span>借方凭证日期：</span>
                <a-range-picker v-model:value="jpzDate"  class="range"/>
              </li>
              <li><span>贷方凭证日期：</span>
                <a-range-picker v-model:value="dpzDate"  class="range"/>
              </li>
              <li>
                <span>币&emsp;&emsp;种：</span>
                <a-select style="width: 200px" v-model:value="bz">
                  <a-select-option  v-for="d in bzList" :value="d.id">
                    {{ d.currChName }}
                  </a-select-option>
                </a-select>
                <span>&emsp;字&emsp;号：</span>
                <a-select  style="width: 175px" v-model:value="fontSize">
                  <a-select-option value="MAX">
                    大号字体
                  </a-select-option>
                  <a-select-option value="MIN">
                    小号字体
                  </a-select-option>
                </a-select>
              </li>
            </ul>

          </a-tab-pane>
          <a-tab-pane v-if="queryPlan!=null" key="1" tab="个人标准模式">
            <ul>
              <li>
                <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
              </li>
              <li><span>科&emsp;&emsp;目：</span>
                <a-select
                  v-model:value="minKm"
                  show-search
                  option-filter-prop="children"
                  style="width: 180px;text-align: center;"
                  allowClear="true"
                  :filter-option="filterOption"
                  @change="handleChangeMinKm"
                >
                  <a-select-option v-for="d in kmList" :value="d.ccode">
                    {{ d.value }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <span>借方凭证日期：</span>
                <a-range-picker v-model:value="formItems.jpzDate"  class="range"/>
              </li>
              <li><span>贷方凭证日期：</span>
                <a-range-picker v-model:value="formItems.dpzDate"  class="range"/>
              </li>
              <li>
                <span>币&emsp;&emsp;种：</span>
                <a-select style="width: 200px" v-model:value="bz">
                  <a-select-option  v-for="d in bzList" :value="d.id">
                    {{ d.currChName }}
                  </a-select-option>
                </a-select>
                <span>&emsp;字&emsp;号：</span>
                <a-select  style="width: 175px" v-model:value="fontSize">
                  <a-select-option value="MAX">
                    大号字体
                  </a-select-option>
                  <a-select-option value="MIN">
                    小号字体
                  </a-select-option>
                </a-select>
              </li>
            </ul>
          </a-tab-pane>
        </a-tabs>
      </div>
    </div>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
  import {ref, unref, onMounted, reactive} from 'vue'
  import {BasicModal, useModalInner} from '/@/components/Modal'
  import {PageWrapper} from '/@/components/Page'
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
    Tabs as ATabs, message
  } from "ant-design-vue"
  import CheckboxGroup from "ant-design-vue/lib/checkbox/Group";
  import { useTabs } from '/@/hooks/web/useTabs';
  import router from "/@/router";
  import { Moment } from 'moment';
  import {
    findPeriodByAccontId,
    findBzAll,
    findCodeKmAll
  } from '/@/api/record/generalLedger/data';
  import {findByImgid} from "/@/api/record/eletronicInvoice_data/eletronic_invoice";
  import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
  import {finByParameterAccuracy} from "/@/api/record/km_mingxi/data";
  import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
  import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
  import {filterAccListByAuth, getAdInfoDatas, getAllAccAuths} from "/@/api/record/system/financial-settings";
  import {useUserStore} from "/@/store/modules/user";
  import {company_findAllStyleByUnique, findByAccStyle, findByStandardUnique} from "/@/api/accstandard/accstandard";
  import {findByQueryPlan, saveQueryPlan} from "/@/api/query-plan/queryPlan";
  import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
  import {groupStandardAndTemplate} from "/@/api/codekemu/codekemu";
  import {findByAccId} from "/@/api/record/system/account";

  const userStore = useUserStore();
  const { closeCurrent } = useTabs(router);
  const ARangePicker = ADatePicker.RangePicker
  const ASelectOption = ASelect.Option
  const AInputSearch = AInput.Search
  const ARadioGroup = ARadio.Group
  const ARadioButton = ARadio.Button
  const ACheckboxGroup = ACheckbox.Group
  const ATabPane = ATabs.TabPane
  const emit=defineEmits([])
  const data = []
  const formItems: any = ref({})
  let changeBeforeModel: any = {}
  // 会计区间
  const dateList: any = ref([])
  // 会计科目
  const kmList: any = ref([])
  //级次
  const jcList: any = ref([])
  //币种
  const bzList: any = ref([])
  //借贷方余额
  const jfye: any = ref<boolean>(false)
  const dfye: any = ref<boolean>(false)
  //借贷方余额
  const minYe: any = ref<number>()
  const maxYe: any = ref<number>()
  //部门
  const deptList: any = ref([])

  const minDept = ref<string>()
  const maxDept = ref<string>()

  const jpzDate = ref<String>("")
  const dpzDate = ref<String>("")
  let endDateList: any = ref([])
  let strDateList: any = ref([])

  let minkmList: any = ref([])
  let maxkmList: any = ref([])
  const minKm = ref<string>()
  const maxKm = ref<string>()

  const jc = ref<number>(1)
  let maxJcList: any = ref([])
  let minJcList: any = ref([])
  const minJc = ref<number>(1)
  const maxJc = ref<number>(1)

  const showStyle = ref<string>('J')
  const fontSize = ref<string>('MAX')
  const bz = ref()
  const jd = ref<number>(2)
  const ishaveRjz = ref<boolean>(true)
  const isShowQjlh = ref<boolean>(false)

  const kmKj = ref<string>('')

  const isType = ref<string>('')
  const isCode = ref<string>('')
  const userId = ref<string>('')
  let styleValue: any = ref('全部');
  let styleList: any = ref([]);
  //查询条件
  const seach : any = ref({})
  const year = new Date().getFullYear();

  const [register, {closeModal, setModalProps}] = useModalInner((data) => {
    // 方式2
    formItems.value.openOne = data.data.openOne
    changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
    getQueryPlan()
    setModalProps({minHeight: 400});
  })
  let isChanged: boolean = false
  //获取当前年月日
  const nowDate = ()=>{
    const nowDate = new Date();
    const date = {
      year: nowDate.getFullYear(),
      month: nowDate.getMonth() + 1,
      date: nowDate.getDate(),
    }
    const newmonth = date.month>10?date.month:'0'+date.month
    const day = date.date>10?date.date:'0'+date.date
    return date.year + '-' + newmonth
  }
  async function handleOk() {

    //kmList 不存在 不让查询
    if(kmList.value.length === 0){
      message.error('您没有科目权限！')
      return false
    }
    if(jpzDate.value){
      formItems.value.jpzDate = transformTimestamp(jpzDate.value[0])+'='+transformTimestamp(jpzDate.value[1])
    }
    if(dpzDate.value) {
      formItems.value.dpzDate = transformTimestamp(dpzDate.value[0])+'='+transformTimestamp(dpzDate.value[1])
    }
    formItems.value.minKm = minKm.value

    formItems.value.showStyle = showStyle.value
    formItems.value.fontSize = fontSize.value
    formItems.value.bz = bz.value
    formItems.value.bzName = bzList.value.filter(o=> o.id === bz.value)[0].currChName

    formItems.value.ishaveRjz = ishaveRjz.value
    formItems.value.isShowQjlh = isShowQjlh.value
    formItems.value.jd = jd.value

    //科目 类别权限
    formItems.value.isType = isType.value
    formItems.value.isCode = isCode.value
    formItems.value.userId = userId.value

    formItems.value.kmList = kmList.value;
    formItems.value.pageParameter = pageParameter;
    const queryPlanEntity={
      accountId:database.value,
      owningMenuName:'客户往来核销',
      owningPlan:activeKey.value,
      planPerson:userStore.getUserInfo.id,
      queryConditions:{
        ishaveRjz:formItems.value.ishaveRjz,
        bend:formItems.value.jc,
        minKm:formItems.value.minKm,
        bz:formItems.value.bz,
        fontSize:formItems.value.fontSize,
        styleValue:formItems.value.styleValue
      }
    }
    emit('save', unref(formItems))
    closeModal()
    return true
  }
  async function handleClose() {
    if (null != formItems.value.openOne && formItems.value.openOne == 1){
      await closeCurrent()
      router.push('/home/welcome')
    }
  }

  //时间转换
  function transformTimestamp(timestamp){
    let a = new Date(timestamp).getTime();
    const date = new Date(a);
    const Y = date.getFullYear() + '-';
    const M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
    const D = (date.getDate() < 10 ? '0'+date.getDate() : date.getDate()) + '  ';
    // const s = date.getSeconds(); // 秒
    const dateString = Y + M + D;
    console.log('dateString', dateString); // > dateString 2021-07-06 14:23
    return dateString;
  }

  async function handleChangeYe() {
    //余额大小控制
    if(minYe.value > maxYe.value){
      maxYe.value = '';
    }
  }
  // 数据库模式名称
  const databases = ref(getCurrentAccountName(false))


  const handleChangeMinKm = () => {
    if(maxKm.value && maxKm.value < minKm.value){
      maxKm.value = minKm.value
    }
  };

  const handleFocusMaxKm = () => {
    //获取焦点时 如果最小科目已选择 判断过滤小于最小科目的数据
    if(minKm.value){
      maxkmList.value = kmList.value.filter( o => o.ccode >= minKm.value)
    }else{
      maxkmList.value = kmList.value
    }
  };
  const handleChangeMinDept = () => {
    if(maxDept.value && maxDept.value < minDept.value){
      maxDept.value = minDept.value
    }
  };
  const handleFocusMaxDept = () => {
    //获取焦点时 如果最小科目已选择 判断过滤小于最小部门的数据
    if(maxDept.value){
      deptList.value = deptList.value.filter( o => o.uniqueCode >= minDept.value)
    }else{
      deptList.value = deptList.value
    }
  };
  const filterOption = (input: string, option: any) => {
    return option.value.toLowerCase().indexOf(input.toLowerCase()) >= 0;
  };
  const handleFocusMinCj = () => {
    //获取焦点时 如果最大科目已选择 则过滤大于最大科目的数据
    if(maxJc.value){
      minJcList.value = jcList.value.filter( o => o.value <= maxJc.value)
    }else{
      minJcList.value = jcList.value
    }
  };
  const handleFocusMaxCj = () => {
    //获取焦点时 如果最小科目已选择 判断过滤小于最小科目的数据
    if(minJc.value){
      maxJcList.value = jcList.value.filter( o => o.value >= minJc.value)
    }else{
      maxJcList.value = jcList.value
    }
  };
  const whetherGroup = ref(false)
  // 数据库模式名称
  const database = ref(useCompanyOperateStoreWidthOut().getSchemaName);

  const accId = ref(getCurrentAccountName(true))
  onMounted(async () => {
    //加载数据
    dateList.value =  await findPeriodByAccontId(database.value);
    console.log(accId.value.substr(accId.value.length-4,4))
    kmList.value = await useRouteApi(findCodeKmAll,{schemaName:accId.value})({});

    //kmList 不存在 不让查询
    if(kmList.value.length === 0){
      message.error('您没有科目权限！')
      return false
    }
    minkmList.value = kmList.value
    minKm.value = kmList.value[0].ccode
    //币种
    bzList.value = await findBzAll();
    // 设置默认选中
    const  sel = bzList.value.filter(o=> o.standCurr === '1')
    bz.value = sel[0].id
  });

  async function styleChange(val) {
    // 重新筛选科目类型
    let accstyle =await useRouteApi(company_findAllStyleByUnique,{schemaName:accId.value})('');
    if(val!=='全部'){
      minKm.value = ''
      maxKm.value = ''
      kmList.value = []
      maxkmList.value = []
      if(val!=='财务会计'&&val!=='预算会计'){
        kmList.value = minkmList.value.filter(ck=>ck.cclass===val)
        maxkmList.value =  kmList.value
      }else if(val=='财务会计'){
        const culist = accstyle.filter(ck=>ck.flagYusuan != '1')
        kmList.value = minkmList.value.filter(ck=> culist.findIndex(v=> v.cclass ===ck.cclass) >= 0)
        maxkmList.value = kmList.value
      }else if(val=='预算会计'){
        const yslist = accstyle.filter(ck=>ck.flagYusuan === '1')
        kmList.value = minkmList.value.filter(ck=> yslist.findIndex(v=> v.cclass ===ck.cclass) >= 0)
        maxkmList.value = kmList.value
      }
    }else{
      kmList.value = minkmList.value
      maxkmList.value =  minkmList.value
    }
  }
  const queryPlan: any = ref([]);
  const activeKey: any = ref('');
  // 页面变量
  const pageParameter = reactive({
    companyCode: '',
    companyName: '',
  })
  const databaseTrue = ref(getCurrentAccountName(true));
  async function dynamicAdReload(data) {
    database.value=data.accId
    databaseTrue.value =data.accId+'-'+data.year
    AllCondition();

  }
  async function AllCondition() {
    findByDateBase()
    //加载数据
    dateList.value = await findPeriodByAccontId(database.value);

    //币种
    bzList.value = await findBzAll();
    bzList.value.push({ id: '', currChName: '全部'  });

  }
  const getAdObjInfoByCoCode = (value, type,accList) => {
    let list = accList.filter(item => item[type] == value)
    return list.length > 0 ? list[0] : null
  }
  async function findByDateBase() {
    // 获取所有账套信息 并过滤f已授权的账套
    let accList = await getAdInfoDatas()
    // 获取当前用户的权限根据用户信息汇率
    let test1 = await getAllAccAuths({'userId': userStore.getUserInfo.id})
    let test2 = filterAccListByAuth(accList, test1)
    let codes = getAdObjInfoByCoCode(database.value, 'accId',test2)
    if (codes != null) {
      pageParameter.companyCode = codes.coCode
      pageParameter.companyName = codes.accNameCn
    }
  }
  async function getQueryPlan() {
    let queryPlanIngo= await findByQueryPlan(database.value,'科目部门表')
    console.log(queryPlanIngo)
    if(queryPlanIngo!=null){
      activeKey.value='1'
      queryPlan.value=queryPlanIngo

      ishaveRjz.value=JSON.parse(queryPlanIngo.queryConditions).ishaveRjz
      jc.value=JSON.parse(queryPlanIngo.queryConditions).bend
      minJc.value=JSON.parse(queryPlanIngo.queryConditions).minJc
      maxJc.value=JSON.parse(queryPlanIngo.queryConditions).maxJc
      minKm.value=JSON.parse(queryPlanIngo.queryConditions).minKm
      maxKm.value=JSON.parse(queryPlanIngo.queryConditions).maxKm
      bz.value=JSON.parse(queryPlanIngo.queryConditions).bz
      styleValue.value=JSON.parse(queryPlanIngo.queryConditions).styleValue
      fontSize.value=JSON.parse(queryPlanIngo.queryConditions).fontSize
    }
  }
</script>
<style lang="less" scoped>
  .nc-open-content {
    position: relative;

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

      .ocup-position {
        position: absolute;
        left: 0;
        width: 180px;
        background-color: #0096c7;
        color: white;
        font-size: 16px;
        text-align: center;
        padding: 5px 10px;
      }
      .ocup-position:nth-of-type(1) {
        top: 0px;
      }
      .ocup-position:nth-of-type(2) {
        top: 190px;
      }

      ul {
        padding: 10px 30px;

        li {
          margin: 10px 0;

          span {
            font-size: 14px;
            color: #747272;
          }

          > span:nth-of-type(1), .right_span {
            display: inline-block;
            width: 120px;
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

    :deep(.ant-select-selector) {
      border: none !important;
      border-bottom: 1px solid #bdb9b9 !important;
    }

    label {
      text-align: right;
      width: 110px;
      display: inline-block;
      padding: 5px 10px;
    }

    .ant-radio-group {
      .ant-radio-wrapper {
        width: 70px;
        .ant-radio-input{
          border-color: slategrey;
        }
      }
      p:nth-of-type(2){
        margin-bottom: 0;
      }
    }
  }

  :deep(.ant-tabs-left-bar) {
    margin-right: 0px !important;
  }

  :deep(.ant-tabs-left) {
    .ant-tabs-tab {
      width: 180px;
      font-weight: bold;
      text-align: center;
      margin-bottom: 0px !important;
    }

    .ant-tabs-tab-active {
      background-color: #65cbec !important;
      color: rgba(0, 0, 0, 0.85) !important;
    }
    .ant-tabs-tab:nth-of-type(1) {
      margin-top: 35px;
    }
    .ant-tabs-tab:nth-of-type(3) {
      margin-top: 110px;
    }
  }

</style>
