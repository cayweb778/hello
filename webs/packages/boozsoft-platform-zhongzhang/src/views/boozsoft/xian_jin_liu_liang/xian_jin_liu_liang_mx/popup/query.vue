<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="现金流量明细查询"
    :loading="loading"
    @ok="handleOk()"
    @cancel="handleClose()"
    @register="register"
  >
    <divfindMaxJc
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
              <li>
                <a-radio-group style="width: 100%" v-model:value="radiovalue" @change="radiochange">
                  <p>
                    <a-radio :value="1">
                      <span style="color: red">*</span>
                    </a-radio>
                    <a-select
                      v-model:value="strDate"
                      show-search
                      :disabled="dateselflg"
                      EditOutlined
                      style="width: 227px"
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
                    </a-select>
                    <span>&emsp;~&emsp;</span>
                    <a-select
                      v-model:value="endDate"
                      show-search
                      :disabled="dateselflg"
                      EditOutlined
                      style="width: 227px"
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
                    </a-select>
                  </p>
                  <p>
                    <!--                    日&emsp;&emsp;期：-->
                    <a-radio :value="2">
                      <span style="color: red">*</span>
                    </a-radio>
                    <a-range-picker
                      v-model:value="riqi"
                      style="width: 496px"
                      :disabled="timeselflg"
                      @change="timechange"
                    />
                  </p>
                </a-radio-group>
              </li>              <li>
                <span>项目大类：</span>
                <a-select
                  v-model:value="projectCate"
                  show-search
                  option-filter-prop="children"
                  style="width: 400px"
                  :filter-option="filterOption"
                  @change="handleChangeProjectCate"
                >
                  <a-select-option v-for="d in projectCateList" :value="d.projectCateCode">
                    {{ d.projectCateName }}
                  </a-select-option>
                </a-select>
              </li>

              <li>
                <span>项目分类：</span>
                <a-select
                  v-model:value="projectClass"
                  show-search
                  option-filter-prop="children"
                  style="width: 400px"
                  allowClear="true"
                  :disabled="!projectCate"
                  :filter-option="filterOption"
                  @change="handleChangeProjectClass"
                >
                  <a-select-option v-for="d in projectClassList" :value="d.uniqueCode">
                    {{ d.projectClassName }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <span>&emsp;字&emsp;&emsp;号：</span>
                <a-select  style="width: 120px" v-model:value="fontSize">
                  <a-select-option value="MAX">
                    大号字体
                  </a-select-option>
                  <a-select-option value="MIN">
                    小号字体
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <a-checkbox v-model:checked="ishaveRjz" style="width: 130px"> 包含未记账 </a-checkbox>
              </li>
            </ul>
          </a-tab-pane>
          <!--<a-tab-pane key="0" tab="集团模式">
            <ul>
              <li>
                <span>核算单位：</span>

                <a-select
                  mode="multiple"
                  style="width: 80%"
                  placeholder="可多选"
                  option-label-prop="label"
                >
                  <a-select-option value="湖北万亚" label="湖北万亚">
                    湖北万亚
                  </a-select-option>
                  <a-select-option value="北京希格" label="北京希格">
                    北京希格
                  </a-select-option>
                </a-select>
              </li>
              <li><span>期&emsp;&emsp;间：</span>
                <a-select default-value="2020-01" style="width: 218px;text-align: center" @change="handleChange">
                  <a-select-option value="2020-01">
                    2020-01
                  </a-select-option>
                  <a-select-option value="2020-02">
                    2020-02
                  </a-select-option>
                  <a-select-option value="2020-03" disabled>
                    2020-03
                  </a-select-option>
                  <a-select-option value="2020-04">
                    2020-04
                  </a-select-option>
                </a-select>
                <span>&emsp;~&emsp;</span>
                <a-select default-value="2020-01" style="width: 218px;text-align: center" @change="handleChange">
                  <a-select-option value="2020-01">
                    2020-01
                  </a-select-option>
                  <a-select-option value="2020-02">
                    2020-02
                  </a-select-option>
                  <a-select-option value="2020-03" disabled>
                    2020-03
                  </a-select-option>
                  <a-select-option value="2020-04">
                    2020-04
                  </a-select-option>
                </a-select>
              </li>
              <li><span>科&emsp;&emsp;目：</span>

                <a-select
                  show-search
                  placeholder="最小科目"
                  style="width: 218px"
                  :default-active-first-option="false"
                  :show-arrow="false"
                  :filter-option="false"
                  :not-found-content="null"
                  @change="handleChange"
                >
                  <a-select-option v-for="d in data" :key="d.value">
                    {{ d.text }}
                  </a-select-option>
                </a-select>
                <span>&emsp;~&emsp;</span>
                <a-select
                  show-search
                  placeholder="最大科目"
                  style="width: 218px"
                  :default-active-first-option="false"
                  :show-arrow="false"
                  :filter-option="true"
                  :not-found-content="null"
                  @change="handleChange"
                >
                  <a-select-option v-for="d in data" :key="d.value">
                    {{ d.text }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <a-radio-group  @change="onChange" style="width: 100%">
                  <a-radio :value="1">
                    <span>级次</span>
                    <a-select default-value="1" style="width: 150px;text-align: center" @change="handleChange">
                      <a-select-option value="1">
                        1
                      </a-select-option>
                      <a-select-option value="2">
                        2
                      </a-select-option>
                      <a-select-option value="3">
                        3
                      </a-select-option>
                      <a-select-option value="4">
                        4
                      </a-select-option>
                      <a-select-option value="5">
                        5
                      </a-select-option>
                      <a-select-option value="6">
                        6
                      </a-select-option>
                    </a-select>
                    <span>&emsp;~&emsp;</span>
                    <a-select default-value="1" style="width: 150px;text-align: center" @change="handleChange">
                      <a-select-option value="1">
                        1
                      </a-select-option>
                      <a-select-option value="2">
                        2
                      </a-select-option>
                      <a-select-option value="3">
                        3
                      </a-select-option>
                      <a-select-option value="4">
                        4
                      </a-select-option>
                      <a-select-option value="5">
                        5
                      </a-select-option>
                      <a-select-option value="6">
                        6
                      </a-select-option>
                    </a-select>
                  </a-radio>
                  <a-radio :value="2" style="margin-left: 50%;">
                    <span>末级科目</span>
                  </a-radio>
                </a-radio-group>
              </li>
              <li><span>科目类型：</span>
                <a-select
                  mode="multiple"
                  style="width: 80%"
                  placeholder="可多选"
                  option-label-prop="label"
                >
                  <a-select-option value="" label="全部">
                    全部
                  </a-select-option>
                  <a-select-option value="1" label="损益">
                    损益
                  </a-select-option>
                </a-select>
              </li>
              <li><span>样&emsp;&emsp;式：</span> <a-select default-value="1" style="width: 185px" @change="handleChange">
                <a-select-option value="1">
                  金额式
                </a-select-option>
                <a-select-option value="2">
                  数量金额式
                </a-select-option>
                <a-select-option value="3" >
                  外币金额式
                </a-select-option>
                <a-select-option value="4">
                  数量外币式
                </a-select-option>
              </a-select>
                <span>&emsp;字&emsp;&emsp;号：</span> <a-select default-value="1" style="width: 185px" @change="handleChange">
                  <a-select-option value="1">
                    大号字体
                  </a-select-option>
                  <a-select-option value="2">
                    小号字体
                  </a-select-option>
                </a-select>
              </li>
              <li><span>币&emsp;&emsp;种：</span> <a-select default-value="1" style="width: 200px" @change="handleChange">
                <a-select-option value="1">
                  人民币
                </a-select-option>
                <a-select-option value="2">
                  美元
                </a-select-option>
                <a-select-option value="3" disabled>
                  欧元
                </a-select-option>
                <a-select-option value="4">
                  港元
                </a-select-option>
              </a-select></li>
            </ul>
            <a-checkbox-group @change="onChange">
              <a-checkbox value="A" style="width: 200px">
                包含未记账
              </a-checkbox>
              <a-checkbox value="B" style="width: 200px">
                期间无发生显示累计
              </a-checkbox>
            </a-checkbox-group>
          </a-tab-pane>-->
          <a-tab-pane v-if="queryPlan!=null" key="1" tab="个人标准模式">
            <ul>
              <li>
                <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
              </li>
              <li>
                <a-radio-group style="width: 100%" v-model:value="radiovalue" @change="radiochange">
                  <p>
                    <a-radio :value="1">
                      <span style="color: red">*</span>
                    </a-radio>
                    <a-select
                      v-model:value="strDate"
                      show-search
                      :disabled="dateselflg"
                      EditOutlined
                      style="width: 227px"
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
                    </a-select>
                    <span>&emsp;~&emsp;</span>
                    <a-select
                      v-model:value="endDate"
                      show-search
                      :disabled="dateselflg"
                      EditOutlined
                      style="width: 227px"
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
                    </a-select>
                  </p>
                  <p>
                    <!--                    日&emsp;&emsp;期：-->
                    <a-radio :value="2">
                      <span style="color: red">*</span>
                    </a-radio>
                    <a-range-picker
                      v-model:value="riqi"
                      style="width: 496px"
                      :disabled="timeselflg"
                      @change="timechange"
                    />
                  </p>
                </a-radio-group>
              </li>
              <li>
                <span>项目大类：</span>
                <a-select
                  v-model:value="projectCate"
                  show-search
                  option-filter-prop="children"
                  style="width: 400px"
                  :filter-option="filterOption"
                  @change="handleChangeProjectCate"
                >
                  <a-select-option v-for="d in projectCateList" :value="d.projectCateCode">
                    {{ d.projectCateName }}
                  </a-select-option>
                </a-select>
              </li>

              <li>
                <span>项目分类：</span>
                <a-select
                  v-model:value="projectClass"
                  show-search
                  option-filter-prop="children"
                  style="width: 400px"
                  allowClear="true"
                  :disabled="!projectCate"
                  :filter-option="filterOption"
                  @change="handleChangeProjectClass"
                >
                  <a-select-option v-for="d in projectClassList" :value="d.uniqueCode">
                    {{ d.projectClassName }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <span>&emsp;字&emsp;&emsp;号：</span>
                <a-select  style="width: 120px" v-model:value="fontSize">
                  <a-select-option value="MAX">
                    大号字体
                  </a-select-option>
                  <a-select-option value="MIN">
                    小号字体
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <a-checkbox v-model:checked="ishaveRjz" style="width: 130px"> 包含未记账 </a-checkbox>
              </li>
            </ul>

            <!--<a-checkbox v-model:checked="isShowQjlh" style="width: 200px">
              期间无发生显示累计
            </a-checkbox>-->
          </a-tab-pane>

        </a-tabs>
      </div>
    </divfindMaxJc>

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
    findMaxJc,
    findCodeKmByPeriod, findProjectClass, findProject, findProjectCategory
  } from '/@/api/record/generalLedger/data';
  import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
  import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
  import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
  import {filterAccListByAuth, getAdInfoDatas, getAllAccAuths} from "/@/api/record/system/financial-settings";
  import {company_findAllStyleByUnique, findByAccStyle, findByStandardUnique} from "/@/api/accstandard/accstandard";
  import {useUserStore} from "/@/store/modules/user";
  import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
  import {findByQueryPlan, saveQueryPlan} from "/@/api/query-plan/queryPlan";

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
  const year = new Date().getFullYear();
  //项目大类
  let projectCateList: any = ref([])
  //项目分类
  let projectClassList: any = ref([])
  const riqi: any = ref([]);
  // 项目
  const projectCate = ref<string>('')
  const projectClass = ref<string>('')
  let radiovalue = ref(1);

  const years = useCompanyOperateStoreWidthOut().getLoginDate.split('-')[0];
  const month = useCompanyOperateStoreWidthOut().getLoginDate.split('-')[1];

  const endDate = ref<String>(years+'-'+month)
  const strDate = ref<String>(years + '-01');
  const dateStart = ref<String>("")
  const dateEnd = ref<String>("")

  let endDateList: any = ref([])
  let strDateList: any = ref([])

  const showStyle = ref<string>('J')
  const fontSize = ref<string>('MIN')
  const ishaveRjz = ref<boolean>(true)
  const isShowQjlh = ref<boolean>(false)

  const kmKj = ref<string>('')
  const isType = ref<string>('')
  const isCode = ref<string>('')
  const userId = ref<string>('')

  let styleValue: any = ref('全部');
  let styleList: any = ref([]);
  const queryPlan: any = ref([]);
  const activeKey: any = ref('');
  // 日期是否可选
  let timeselflg: any = ref(true);
  // 期间是否可选
  let dateselflg: any = ref(false);
  const maxPingzhengQj = ref('')
  // 页面变量
  const pageParameter = reactive({
    companyCode: '',
    companyName: '',
  })
  //查询条件
  const seach : any = ref({})
  let loading: any = ref(true)

  const [register, {closeModal, setModalProps, changeOkLoading}] = useModalInner((data) => {
    // 方式2
    formItems.value.openOne = data.data.openOne
    changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
    getQueryPlan()
    setModalProps({minHeight: 400});
    if(loading.value === true){
      changeOkLoading(true);
    }
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

  function radiochange(val) {
    timeselflg.value = val.target.value === 1;
    dateselflg.value = val.target.value === 2;
    if (val.target.value === 1) {
      riqi.value = '';
      strDate.value = maxPingzhengQj.value;
      endDate.value = maxPingzhengQj.value;
    } else {
      strDate.value = '';
      endDate.value = '';
    }
  }

  async function handleOk() {
    let startQj = strDate.value || ''
    let endQj = endDate.value || ''
    let startRq = null == riqi.value[0]? '':riqi.value[0].format('YYYY-MM-DD')
    let endRq = null == riqi.value[1]? '':riqi.value[1].format('YYYY-MM-DD')
    if ((startQj == '' &&  endQj == '') && riqi .value.length === 0) {
      message.error('请选择会计区间or日期期间!');
      return
    }
    if ((startQj != '' &&  endQj == '') || (startQj == '' &&  endQj != '')  ) {
      message.error('请完善会计区间');
      return
    }else if ((startQj != '' && endQj != '' && (startQj.substring(0,4) != endQj.substring(0,4)))){
      message.error('暂不支持跨越年度会计区间查询');
      return
    }else if ((startRq != '' && endRq != '' && (startRq.substring(0,4) != endRq.substring(0,4)))){
      message.error('暂不支持跨越年度日期区间查询');
      return
    }

    formItems.value.strDate = strDate.value
    formItems.value.endDate = endDate.value

    formItems.value.dateStart = startRq
    formItems.value.dateEnd = endRq

    formItems.value.showStyle = showStyle.value
    formItems.value.fontSize = fontSize.value

    formItems.value.ishaveRjz = ishaveRjz.value
    formItems.value.isShowQjlh = isShowQjlh.value

    //科目 类别权限
    formItems.value.isType = isType.value
    formItems.value.isCode = isCode.value
    formItems.value.userId = userId.value

    formItems.value.styleList = styleValue.value!=='全部'?styleList.value.filter(s=>s.cclass===styleValue.value):styleList.value.filter(s=>s.cclass!==styleValue.value);
    formItems.value.pageParameter = pageParameter;
    formItems.value.accountId = databaseTrue.value
    const queryPlanEntity={
      accountId:database.value,
      owningMenuName:'现金流量明细表',
      owningPlan:activeKey.value,
      planPerson:userStore.getUserInfo.id,
      queryConditions:{
        ishaveRjz:formItems.value.ishaveRjz,
        bend:formItems.value.jc,
        strDate:formItems.value.strDate,
        endDate:formItems.value.endDate,
        dateStart:formItems.value.dateStart,
        dateEnd:formItems.value.dateEnd,
        timflg:'qj',
        fontSize:formItems.value.fontSize,
        styleValue:formItems.value.styleValue
      }
    }
    await saveQueryPlan(queryPlanEntity);
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
  // 数据库模式名称
  const databases = ref(getCurrentAccountName(false))
  async function handleChangeStrDate() {
    // 开始日期变动  如果开始日期大于结束于日期 则重置结束日期
    if(endDate.value){
      if(strDate.value > endDate.value){
        endDate.value = strDate.value
      }
      userId.value = userStore.getUserInfo.id
    }
  }
  async function handleChangeEndDate() {
    // 开始日期变动  如果结束日期小于开始日期 则重置开始日期
    if(strDate.value){
      if(strDate.value > endDate.value){
        strDate.value = endDate.value
      }
    }
  }

  async function focusEndDate() {
    if(strDate.value){
      endDateList.value = dateList.value.filter( o => o.value >= strDate.value)
    }else{
      endDateList.value = dateList.value
    }
  }

  const filterOption = (input: string, option: any) => {
    return option.value.toLowerCase().indexOf(input.toLowerCase()) >= 0;
  };

  const whetherGroup = ref(false)
  // 数据库模式名称
  const database = ref(getCurrentAccountName(false));
  async function newfindAllCode(s,start) {
    // 会计科目
    let list=await useRouteApi(findCodeKmByPeriod,{schemaName:s})({ strDate: start, endDate: 'test', accId: database.value,userId:userStore.getUserInfo.id });
    return list;
  }
  function tabChange(val) {
    if(val===''){
      styleValue.value='全部'
    }else if(val==='1'){
      getQueryPlan()
    }
  }
  async function focusStrDate() {
    /*if (endDate.value) {
      strDateList.value = dateList.value.filter(o => o.value <= endDate.value)
    } else {
      strDateList.value = dateList.value
    }*/
    strDateList.value = dateList.value
  }

  onMounted( ()=>{
    AllCondition();
  })
  const databaseTrue = ref(getCurrentAccountName(true));
  async function dynamicAdReload(data) {
    database.value = data.accId
    databaseTrue.value = data.accId+'-'+data.year
    pageParameter.accId = data.accId
    pageParameter.database = data.accId+'-'+data.year
    pageParameter.accNameAll = data.target.accName
    if(databaseTrue.value != data.accId+'-'+data.year){
      changeOkLoading(true);
      loading.value  = true
      AllCondition();
    }
  }
  async function AllCondition() {
    findByDateBase()
    //加载数据
    dateList.value = await findPeriodByAccontId(database.value);
    // 设置默认期间
    // 如果没有当前年度 获取 上年第一个区间
    const test = dateList.value.filter((o) => o.iyear === year);
    if (test.length == 0) {
      strDate.value = dateList.value[0].value;
      endDate.value = dateList.value[0].value;
    }
    //项目大类
    projectCateList.value = await useRouteApi(findProjectCategory,{schemaName:databaseTrue.value})({accId:databaseTrue.value});

    changeOkLoading(false);
    loading.value  = false
  }
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
    let codes = getAdObjInfoByCoCode(database.value, 'accId',test2)
    if (codes != null) {
      pageParameter.companyCode = codes.coCode
      pageParameter.companyName = codes.accNameCn
    }
  }
  async function getQueryPlan() {
    let queryPlanIngo= await findByQueryPlan(database.value,'现金流量明细表')
    console.log(queryPlanIngo)
    if(queryPlanIngo!=null){
      activeKey.value='1'
      queryPlan.value=queryPlanIngo

      ishaveRjz.value=JSON.parse(queryPlanIngo.queryConditions).ishaveRjz
      strDate.value=JSON.parse(queryPlanIngo.queryConditions).strDate
      endDate.value=JSON.parse(queryPlanIngo.queryConditions).endDate

      dateStart.value=JSON.parse(queryPlanIngo.queryConditions).dateStart
      dateEnd.value=JSON.parse(queryPlanIngo.queryConditions).dateEnd

      projectCate.value=JSON.parse(queryPlanIngo.queryConditions).projectCate
      projectClass.value=JSON.parse(queryPlanIngo.queryConditions).projectClass
      styleValue.value=JSON.parse(queryPlanIngo.queryConditions).styleValue
      fontSize.value=JSON.parse(queryPlanIngo.queryConditions).fontSize
    }
  }
  // 项目大类触发事件
  async function handleChangeProjectCate() {
    //项目分类
    projectClassList.value = await useRouteApi(findProjectClass,{schemaName:databaseTrue.value})({projectCate:projectCate.value,accId:databaseTrue.value});
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
