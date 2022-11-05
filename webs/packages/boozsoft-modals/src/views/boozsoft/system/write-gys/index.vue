<template>
  <div class="app-container">
    <div class="app-container-top">
        <div class="app-container-head">
          <div class="container-head-title" style="float: none;text-align: center;display: revert;margin-left:0px">
          <b class="noneSpan">供应商往来核销</b>
        </div>

        <div>
          <div class="ant-btn-group" style="padding-top: 5px">
            <button
              type="button"
              class="ant-btn ant-btn-me"
              ant-click-animating-without-extra-node="false"
              @click="openQueryPage()"
            ><span>查询</span></button>
            <button
              type="button"
              class="ant-btn ant-btn-me"
              ant-click-animating-without-extra-node="false"
              @click="addHx()"
            ><span>手动核销</span></button>
            <button
              type="button"
              class="ant-btn ant-btn-me"
              ant-click-animating-without-extra-node="false"
              @click="autoHx()"
            ><span>自动核销</span></button>
            <button
              type="button"
              class="ant-btn ant-btn-me"
              ant-click-animating-without-extra-node="false"
              @click="goHxHistiry()"
            ><span>核销历史</span></button>
            <button
              type="button"
              class="ant-btn ant-btn-me"
              ant-click-animating-without-extra-node="false"
              @click="openQueryPage()"
            ><span>联查凭证</span></button>
            <button
              type="button"
              class="ant-btn ant-btn-me"
              ant-click-animating-without-extra-node="false"
              @click="openQueryPage()"
            ><span>退出</span></button>
          </div>
          <div style="display: inline-block;margin-left: 1%;font-size: 14px;width: 40%">
            <div style="padding-top: 20px">
              <AccountPicker theme="one" readonly="" @reloadTable="dynamicAdReload"/>
            </div>
          </div>
          <div style="clear:both" />
          <div style="margin-top: .5%;">
            <div style="display: inline-block;font-size: 14px;padding-top: 8px">
              <span style="font-size: 14px;">科目：</span>
              <a-select
                show-search
                placeholder="科目选择"
                option-filter-prop="children"
                style="width: 220px;font-size: 14px;"
                :filter-option="filterOption"
                @change="handleChangeMinKm"
                v-model:value="pageParameter.km"
              >
                <a-select-option   style="width: 180px;font-size: 14px;" v-for="d in kmList" :value="d.ccode">
                  {{ d.value }}
                </a-select-option>
              </a-select>
              <!--&emsp;&emsp;<span style="font-size: 14px;color: #aaa">核销年度：</span><span style="font-size: 14px;font-weight: bold">{{ bzName }} </span>-->
              &emsp;&emsp;<span style="font-size: 14px;">币种：</span><span style="color: black;font-weight: bold">{{ pageParameter.bzName }}</span>
            </div>
            <div style="float: right; margin-left: 10px">
              <a-button class="ant-btn-default" @click="closeFilterV(),pageReload()">
                <SyncOutlined :style="{ fontSize: '14px' }" />
              </a-button>
              <a-popover class="ant-btn-default" placement="bottom">
                <template #content>
                  <a-popconfirm
                    ok-text="确定"
                    cancel-text="放弃"
                    @confirm="confirm"
                    @cancel="cancel"
                  >
                    <template #icon><b>栏目设置</b><br></template>
                    <template #title>
                      <a-table bordered :data-source="dynamicColumnData" :columns="dynamicColumns"
                               childrenColumnName="children" :pagination="false" style="max-height: 500px;overflow-y: auto" class="lanmu-table">
                        <template #checkBox="{ text, record }">
                          <a-checkbox v-model:checked="record.check" :disabled="record.isFixed"/>
                        </template>
                        <template #widthInput="{ text, record }">
                          <div class="editable-cell">
                            <div v-if="editableData[record.key]" class="editable-cell-input-wrapper">
                              <a-input type="number" v-model:value="editableData[record.key].width"
                                       @pressEnter="save(record.key,record.min,record.max)" style="width: 80px"/>
                              <check-outlined class="editable-cell-icon-check"
                                              @click="save(record.key,record.min,record.max)"/>
                            </div>
                            <div v-else class="editable-cell-text-wrapper">
                              {{ text || ' ' }}
                              <edit-outlined class="editable-cell-icon" @click="edit(record.key)"/>
                              <span style="float: right;">{{ record.min + '~' + record.max }}</span>
                            </div>
                          </div>
                        </template>
                        <template #nameInput="{ text, record }">
                          <div class="editable-cell">
                            <div v-if="editableData[record.key]" class="editable-cell-input-wrapper">
                              <a-input type="text" v-model:value="editableData[record.key].nameNew"
                                       @pressEnter="saveName(record.key)" style="width: 100px"/>
                              <check-outlined class="editable-cell-icon-check"
                                              @click="saveName(record.key)"/>
                            </div>
                            <div v-else class="editable-cell-text-wrapper">
                              {{ text || ' ' }}
                              <edit-outlined class="editable-cell-icon" @click="edit(record.key)"/>
                            </div>
                          </div>
                        </template>
                        <template #alignRadio="{ text, record }">
                          <a-radio-group default-value="a" size="small" v-model:value="record.align"
                                         :disabled="record.align=='' || record.name=='本币金额' || record.name=='原币金额'
                                          || record.name=='借方' || record.name=='贷方' || record.name=='余额'">
                            <a-radio-button value="left">
                              左
                            </a-radio-button>
                            <a-radio-button value="center">
                              中
                            </a-radio-button>
                            <a-radio-button value="right">
                              右
                            </a-radio-button>
                          </a-radio-group>
                        </template>
                      </a-table>
                    </template>
                    <a-button style="width: 120px;margin-bottom: 2px">栏目设置</a-button>
                  </a-popconfirm>
                  <br/>
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
                <template #content>
                  <span class="group-btn-span-special">导出当前</span><br/>
                  <span class="group-btn-span-special">条件导出</span>
                </template>
                <button
                  type="button"
                  class="ant-btn"
                  ant-click-animating-without-extra-node="false"
                >
                  <UsbOutlined/>
                </button>
              </a-popover>
              <a-popover class="ant-btn-default" placement="bottom">
                <template #content>
                  <span class="group-btn-span-special">打印当前</span><br/>
                  <span class="group-btn-span-special">条件打印</span>
                </template>
                <button
                  type="button"
                  class="ant-btn"
                  ant-click-animating-without-extra-node="false"
                >
                  <PrinterOutlined/>
                </button>

              </a-popover>
              <a-button @click="()=>{if (!visible){visible = true;reloadColumns();}return false}">
                <FilterFilled :style="{ fontSize: '14px' }"/>
              </a-button>
            </div>
            <div style="float: right; position: relative">
              <a-select v-model:value="pageParameter.searchConditon.requirement" style="width: 130px;" class="special_select">
                <template v-for="item in searchConditonList.slice(1)">
                  <a-select-option v-if="item.ifShow == true" :value="item.dataIndex">
                    {{item.title}}
                  </a-select-option>
                </template>
              </a-select>
              <a-input-search
                placeholder=""
                v-model:value="pageParameter.searchConditon.value"
                @search="pageSearch"
                style="width: 200px;border-radius: 4px"
              />
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="app-container-bottom"/>
        <BasicTable
          ref="tableRef"
          :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
          :row-selection="{ type: 'checkbox', selectedRowKeys: state1.selectedRowKeys, onChange: onSelectChange1 }"
          :scroll="{ x: totalColumnWidth,y: windowHeight }"
          style="width: 100%"
          size="small"
          @register="registerTable"
        >
          <template #serialNumber="{record,index }">
          <span slot="serialNumber" slot-scope="text,record,index">
           {{record.cdfine30}}
          </span>
          </template>

          <template #md="{ record }">
          <span style="float: right">
           {{ money(record.md) }}
          </span>
          </template>

          <template #mc="{ record }">
          <span style="float: right">
           {{ money(record.mc) }}
          </span>
          </template>

          <template #remainMoney="{ record }">
          <span style="float: right">
           {{ money(record.remainMoney) }}
          </span>
          </template>

          <template #hxStatue="{ record }">
          <span style="float: right">
           {{ moneyhxStatue(record.hxStatue) }}
          </span>
          </template>

        </BasicTable>
        <a-drawer
          title="过滤漏斗"
          placement="right"
          :closable="true"
          v-if="visible"
          :mask="false"
          :visible="visible"
          :get-container="false"
          :wrap-style="{ position: 'absolute' }"
          @close="visible=false,reloadColumns()"
        >
          <ul>
            <li>
            <span style="color: black;font-weight: bold">
            分配状态：
            </span>
              <div>
                <a-select
                  :allowClear="true"
                  placeholder=""
                  v-model:value="pageParameter.filterConditon.cashProject"
                  style="width: 200px"
                >
                  <a-select-option value="ok">已分配</a-select-option>
                  <a-select-option value="no">未分配</a-select-option>
                </a-select>
              </div>
            </li>

          </ul>
          <br/>
          <a-button type="primary"  style="float: right;" @click="filterSearch">
            <span style="font-size: 14px">开始</span>
          </a-button>
        </a-drawer>
        <Query
          @save="loadPage"
          @register="registerQueryPage"
        />
       <AddHx
          @save="saveHx"
          @register="registerAddHxPage"
        />

      <TablePiece v-model="pageParameter" ref="mychild" />

  </div>
</template>
<script setup lang="ts">
  import { BasicTable, useTable } from '/@/components/Table'
  import { useModal } from '/@/components/Modal'
  import { PageWrapper } from '/@/components/Page'

  import {
    DatePicker as ADatePicker, Select as ASelect, Input as AInput, Popover as APopover, Switch as ASwitch,
    Radio as ARadio, Pagination as APagination, Popconfirm as APopconfirm, Table as ATable,
    Checkbox as ACheckbox, message,Drawer as ADrawer
  } from "ant-design-vue"
  const ARangePicker=ADatePicker.RangePicker
  const ASelectOption=ASelect.Option
  const AInputSearch=AInput.Search
  const ARadioGroup=ARadio.Group
  const ARadioButton=ARadio.Button
  import {
    SortDescendingOutlined,
    SortAscendingOutlined,
    CaretDownFilled,
    FormOutlined,
    DeleteOutlined,
    CheckCircleOutlined,
    CloseCircleOutlined,
    SettingFilled,
    SyncOutlined,
    PicLeftOutlined,
    EditFilled,
    PieChartFilled,
    FilterFilled,CheckOutlined,EditOutlined,SearchOutlined,MacCommandOutlined,PrinterOutlined,UsbOutlined,UnlockTwoTone,LockTwoTone
  } from '@ant-design/icons-vue'
  import {
    breakNumTidy,
    findDbLanMuList,
    saveLanMuList,
  } from '/@/api/record/system/accvoucher'
  import {
    findAllAccvoucherHexiaoGys, autoWriteOff, saveManualWriteOff,
  } from '/@/api/record/system/write-off'
  import { useAccvoucherHxStore } from '/@/store/modules/accvoucher-hexiao'
  import {onMounted, reactive, ref,UnwrapRef} from "vue";
  import moment from "moment";
  import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
  import {changeDefaultDynamics, initDynamics,assemblyDynamicColumn} from "./data";
  import Query from "./popup/query.vue";
  import AddHx from "./popup/addHx.vue";

  import {cloneDeep} from "lodash-es";
  import {
    getThisAdInfoData,
  } from "/@/api/record/system/financial-settings";
  import {useMessage} from "/@/hooks/web/useMessage";
  import {
    askTask,
    compareTime, findAccCloseListByYaer,
    findByFunctionModule,
    getCurrentAccountName,
    offsetToStr, pointMessage,getThisIndexImg
  } from "/@/api/task-api/tast-bus-api";
  import {
    intervalWorking
  } from "/@/views/boozsoft/system/accvoucher/data";
  import {useUserStore, useUserStoreWidthOut} from "/@/store/modules/user";
  import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
  import {findProjectCash,saveFp} from "/@/api/record/generalLedger/data";
  import AccountInfo from "/@/views/boozsoft/system/accvoucher/popup/AccountInfo.vue";
  import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
  import fa from "element-plus/packages/locale/lang/fa";
  import TablePiece from "./popup/TablePiece.vue";
  import {deletePsn} from "/@/api/record/system/unit-mea";
  import {delMessage} from "/@/api/record/sys-task/data";
  import {columnProps} from "ant-design-vue/es/table/interface";
  import router from "/@/router";

  const { createConfirm,createWarningModal } = useMessage();
  // 全局常量
  //
  const accvoucherStore = useAccvoucherHxStore()
  // 页面变量
  const pageParameter = reactive({
    queryMark: '1',
    reloadMark: false,
    showRulesSize: 'MIN',
    biZhong: '人民币',
    companyCode: '',
    ifUnit: false,
    companyName: '',
    condition: {},
    searchConditon: {
      requirement: 'custname',
      value: '',
    },
    filterConditon: {
      amountMin: '',
      amountMax: '',
    },

    km: '',
    jpzDate: '',
    dpzDate: '',
    type: 'j',
  })
  const kmList = ref([])
  //对方科目list
  const dkmList = ref([])
  //流量项目list
  const pcList = ref([])
  //现金科目条数
  const countNum = ref(0)
  //净利润
  const jlr = ref(0.00)
  let defalutCompanyCode = ''
  const val = {
    openOne: 0
  }
  const pageChange = () => {
    pageReload()
  }

  type Key = columnProps['id'];
  const state = reactive<{
    selectedRowKeys: Key[];
    loading: boolean;
  }>({
    selectedRowKeys: [], // Check here to configure the default column
    loading: false,
  });

  const state1 = reactive<{
    selectedRowKeys: Key[];
    loading: boolean;
  }>({
    selectedRowKeys: [], // Check here to configure the default column
    loading: false,
  });
  const checkRow = ref([])
  const onSelectChange1 = (selectedRowKeys,row) => {
    // console.log('selectedRowKeys changed: ', row);
    state1.selectedRowKeys = selectedRowKeys;
    checkRow.value = row
  };
  const userStore = useUserStore();
  const defaultPage = ref(true) // 默认为独立账套
  const visible = ref(false);
  const searchConditonList = ref([])
  const lanMuData = {'accId':'','menuName':'往来核销','type':'',objects: '',username:useUserStoreWidthOut().getUserInfo.username}
  // 表格参数
  const loadMark = ref(false)
  const tableSelectedRowKeys = ref([])
  const tableSelectedRowObjs = ref([])
  const windowWidth  = (document.documentElement.clientWidth -70)
  const windowHeight  = (document.documentElement.clientHeight -350)
  const totalColumnWidth = ref(0)
  const defaultDbName = useCompanyOperateStoreWidthOut().getTenentName
  // const defaultDbName = 'bjxgkj-001-2021' ||
  const manipulateDbName = ref(getCurrentAccountName(true))
  const CrudApi = reactive({
    list: useRouteApi(findAllAccvoucherHexiaoGys,{schemaName:manipulateDbName.value}),
    columns: JSON.parse(JSON.stringify(accvoucherStore.getColumns(pageParameter.queryMark))),
  })
  const tableRef = ref(null)

  const tableRef2 = ref(null)
  // 组件实例区
  const [registerTable, { reload,setColumns,getColumns,setTableData,getDataSource }] = useTable({
    api: CrudApi.list,
    columns: CrudApi.columns,
    bordered: true,
    loading: loadMark.value,
    immediate: false,
    canResize: true,
    /*  showSummary: true,
      summaryData: [],*/
    showIndexColumn: false , //显示序号列
    pagination:{ pageSize: 8,showSizeChanger: true, pageSizeOptions: ['8','16','32','78'],showTotal: t => `总共${t}条数据`, },
    /*actionColumn: {
      width: 100,
      title: '操作',
      dataIndex: 'action',
      slots: { customRender: 'action' }
    } as　BasicTable*/
    tableSetting: {
      // 是否显示刷新按钮
      redo: true,
      // 是否显示尺寸调整按钮
      size: true,
      // 是否显示字段调整按钮
      setting: true,
      // 是否显示全屏按钮
      fullScreen: true,
    }
  })

  const [registerInfo, {openModal: openInfoPageM}] = useModal()

  const [registerQueryPage, { openModal: openQueryPageM }] = useModal()


  const [registerAddHxPage, { openModal: openAddHxPage }] = useModal()


  onMounted( ()=>{
    // useCompanyOperateStoreWidthOut().commitSchemaName('bjxgkj-001');
    val.openOne = 1
    openQueryPageM(true, {
      data: val
    })
    loadMark.value = true
  })

  function money(data:any){
    let str = ""
    if(data){
      // 千分位保留2位小数
      var source = String(parseFloat(data).toFixed(2)).split("."); //按小数点分成2部分
      source[0] = source[0].replace( new RegExp('(\\d)(?=(\\d{3})+$)' , 'ig'), "$1,");//只将整数部分进行都好分割
      str = source.join("." );//再将小数部分合并进来
    }
    return data == ('0.00' || '0')?'': str;
  }

  function formeatData(val:any) {
    return val === "1"?'已分配': '未分配'
  }
  function moneyhxStatue(val:any) {
    return val === "1"?'已核销': '待核销'
  }

  const modify = (code) => {
    if (pageParameter.companyCode != code) {
      pageParameter.companyCode = code
      pageSearch()
    }
  }
  const openQueryPage = () => {
    val.openOne = 0
    openQueryPageM(true, {
      data: val
    })
  }

  const unCheckTable = ()=>{
    if (tableSelectedRowKeys.value.length > 0){
      tableSelectedRowKeys.value = []
      tableSelectedRowObjs.value = []
    }
  }
  // 页面函数区
  const onSelectChange = (selectedRowKeys,obj) => {
    tableSelectedRowKeys.value = selectedRowKeys;
    tableSelectedRowObjs.value = obj;
  };
  const pageReload =  () =>{
    setTableData([]) // 清空可能残留的数据
    reload({
      searchInfo: pageParameter
    })
    //刷新子页面list
    mychild.value.initPageParameter()
  }
  const showThis = ref(false)


  const onChangeSwitch = async(v)=>{ // 动态列
    pageParameter.queryMark = v?'1':'2'
    resetDynamicColumnData()
    pageParameter.searchConditon.requirement = 'inoId'
    pageParameter.searchConditon.value = ''
  }
  const  pageSearch = async ()=>{
    /* if (''==pageParameter.searchConditon.value.trim())return false*/
    // 搜索前校验格式
    if (''==pageParameter.searchConditon.requirement.trim()){
      message.warn('请选择检索条件')
      return false
    }
    // 校验完成后搜索
    closeFilterV()
    pageReload()
  }

  const  pageSearch2 = async ()=>{
    // 检查是否存在 查看是否拥有权限
    if ((defalutCompanyCode == pageParameter.companyCode && manipulateDbName.value == '')
      || pageParameter.companyCode == tempValue.value  && manipulateDbName.value != '') return false
    let accObj = accAuthList.value.filter(item=>item.coCode == pageParameter.companyCode)
    if (accObj.length == 0){
      message.warn('代码不存在停止切换！')
      pageParameter.companyCode = defalutCompanyCode
    }else if(accObj.length > 0){
      let iyear = pageParameter.thisInterval.substring(0,4)
      let authObj = userAuthMap.value.filter(item=>accObj[0].accId == item.accId && item.iyear == iyear)
      if (authObj.length == 0){
        message.warn('无该代码'+iyear+'年度数据权限！')
        pageParameter.companyCode = defalutCompanyCode
      }else {
        manipulateDbName.value = accObj[0].accId+'-'+iyear
        pageParameter.companyName = accObj[0].accNameCn

      }
    }
  }

  const tempValue = ref(defalutCompanyCode)

  const initCodeValue = async (type)=>{
    if (type == 'set'){
      if ((pageParameter.companyCode == '' || pageParameter.companyCode != '' ) && manipulateDbName.value == ''){
        pageParameter.companyCode = defalutCompanyCode
      }else if (manipulateDbName.value != '' && pageParameter.companyCode == ''){
        pageParameter.companyCode =tempValue.value
      }
    }else {
      tempValue.value = pageParameter.companyCode
      pageParameter.companyCode = ''
    }
  }

  const  filterSearch = async ()=>{
    /* if (''==pageParameter.searchConditon.value.trim())return false*/
    // 搜索前校验格式
    let min  = pageParameter.filterConditon.amountMin.trim()
    let max  = pageParameter.filterConditon.amountMax.trim()
    if (''==min && max != '' || ''==max && min != ''){
      message.warn('请完善金额区间过滤条件！')
      return false
    }
    if (min != '' && max != '' && (!isRealNum(min) || !isRealNum(max) || parseFloat(min) == 0 || parseFloat(max) == 0)){
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    }else if (parseFloat(min) > parseFloat(max)){
      message.warn('金额区间最小值部门大于最大值！')
      closeFilterV()
      return false
    }
    // 校验完成后搜索
    pageReload()
  }

  const closeFilterV = ()=>{
    pageParameter.filterConditon.amountMin =''
    pageParameter.filterConditon.amountMax =''
    visible.value = false
    reloadColumns()
  }

  function isRealNum(val){
    // isNaN()函数 把空串 空格 以及NUll 按照0来处理 所以先去除，
    if(val === "" || val ==null){
      return false;
    }
    if(!isNaN(val)){
      //对于空数组和只有一个数值成员的数组或全是数字组成的字符串，isNaN返回false，例如：'123'、[]、[2]、['123'],isNaN返回false,   //所以如果不需要val包含这些特殊情况，则这个判断改写为if(!isNaN(val) && typeof val === 'number' )
      return true; 　　}　else{ 　　　　return false; 　　}
  }

  const analyzeTheYearAndAllMonths = (list:any)=>{
    let months = []
    if (list.length === 0) return{year: '',months: months}
    list.forEach(item=>{
      let thisMonth = item.dbillDate.split('-')[1]
      if (months.indexOf(thisMonth) == -1){
        months.push(thisMonth)
      }
    })
    return {
      year: list[0].dbillDate.split('-')[0],
      months: months
    }
  }
  /*start栏目设置*/
  const dynamicColumns = initDynamics().DEFAULT
  const dynamicColumnData = ref([])
  let dynamicColumnDataCopy = []
  const editableData = reactive({});
  const editableData2 = reactive({});

  const confirm = (e: MouseEvent) => {
    // 询问
    createConfirm({
      iconType: 'warning',
      title: '栏目同步',
      content: '是否将刚才设置同步数据库!',
      onOk: async() => {
        // 调整数据库 列参数
        lanMuData.accId = getCurrentAccountName(false)
        lanMuData.objects = JSON.stringify(filterModifyData(dynamicColumnData.value, dynamicColumnDataCopy))
        if (lanMuData.objects == '[]'){
          createWarningModal({content: '请先做修改后再进行确认同步数据库！'})
        }else {
          saveLanMuList(lanMuData).then(res=>{
            message.success("数据库同步成功！")
          })
          // 重新赋值
          dynamicColumnDataCopy = JSON.parse(JSON.stringify(dynamicColumnData.value))
        }
      }
    });
    // 重新获取数据
    reloadColumns()
  }

  function filterModifyData(lanMuList:any,copyList) {
    let a =  lanMuList.filter(item=> {
      try {
        copyList.forEach(item2=>{
          if (item.key === item2.key && item.name == item2.name){
            if (item.nameNew != item2.nameNew || item.width != item2.width || item.check != item2.check || item.align != item2.align)
              throw new Error('ok')
          }
        })
        return false
      }catch (e) {
        if (e.message == 'ok'){
          return  true
        }else {
          return  false
        }
      }
    })
    return a;
  }

  const cancel = (e: MouseEvent) => {
    // 恢复默认
    dynamicColumnData.value = []
    dynamicColumnData.value = dynamicColumnDataCopy
  }


  function resetDynamicColumnData() {
    // 先从数据查询是否已经设置
    lanMuData.accId = getCurrentAccountName(false)
    lanMuData.type = pageParameter.queryMark
    findDbLanMuList(lanMuData).then(res=>{
      // 栏目列
      let dbList = res.items
      if (dbList.length > 0){
        let statiList = initDynamics()['DATA'+pageParameter.queryMark]
        dbList = combineParameters(statiList,dbList)
        dynamicColumnData.value = dbList
        dynamicColumnDataCopy = JSON.parse(JSON.stringify(dbList))
      }else {
        let statiList = initDynamics()['DATA'+pageParameter.queryMark]
        dynamicColumnData.value = statiList
        dynamicColumnDataCopy = JSON.parse(JSON.stringify(statiList))
      }
      // 表格列
      reloadColumns()
      pageReload()
    })
  }

  function combineParameters(staticList:any,dbList:any) {
    staticList.forEach(item=>{
      dbList.forEach(item2=>{
        if (item.key === item2.key && item.name === item2.name){
          item.nameNew = item2.nameNew
          item.width = parseInt(item2.width)
          item.check = item2.check == 'true'
          item.align = item2.align
        }
      })
    })
    return staticList
  }

  const edit = (key: string) => {
    if (key.toString().indexOf('-') != -1) {
      let arr = key.split('-');
      let one = parseInt(arr[0])
      if (arr.length == 2) {
        editableData[key] = cloneDeep(dynamicColumnData.value[one].children.filter(item => key === item.key)[0]);
      } else {
        let two = parseInt(arr[1] - 1)
        editableData[key] = cloneDeep(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0]);
      }
    } else {
      editableData[key] = cloneDeep(dynamicColumnData.value.filter(item => key === item.key)[0]);
    }
  }

  const save = (key: string, min: number, max: number) => {
    editableData[key].width = editableData[key].width > max ? max : editableData[key].width < min ? min : editableData[key].width
    if (key.toString().indexOf('-') != -1) {
      let arr = key.split('-');
      let one = parseInt(arr[0])
      if (arr.length == 2) {
        Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
        Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
      } else {
        let two = parseInt(arr[1] - 1)
        Object.assign(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0], editableData[key]);
      }
    } else {
      Object.assign(dynamicColumnData.value.filter(item => key === item.key)[0], editableData[key]);
    }
    delete editableData[key];
  }

  const saveName = (key: string) => {
    if (key.toString().indexOf('-') != -1) {
      let arr = key.split('-');
      let one = parseInt(arr[0])
      if (arr.length == 2) {
        Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
        Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
      } else {
        let two = parseInt(arr[1] - 1)
        Object.assign(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0], editableData[key]);
      }
    } else {
      Object.assign(dynamicColumnData.value.filter(item => key === item.key)[0], editableData[key]);
    }
    delete editableData[key];
  }

  const reloadColumns = ()=>{
    let a = []
    a = getColumns()
    let newA = JSON.parse(JSON.stringify(accvoucherStore.getColumns(pageParameter.queryMark)))
    newA= assemblyDynamicColumn(dynamicColumnData.value,newA)
    console.log(newA)
    setColumns(newA)
    initTableWidth(newA)
    searchConditonList.value = newA
  }

  function initTableWidth(thisCs){
    let total = 60
    thisCs.forEach(item=>{
      if (item.ifShow== null || item.ifShow)
        total+= parseInt(item.width)
    })
    if (total > windowWidth){
      let f = 0
      if (visible.value) f=260
      totalColumnWidth.value = Number(windowWidth)-f
      tableRef.value.$el.style.setProperty('width',(windowWidth+20-f)+'px')
    }else {
      if (visible.value && (windowWidth-260) < total) total-=(total-(windowWidth-260))
      totalColumnWidth.value = total
      tableRef.value.$el.style.setProperty('width',(total+20)+'px')
    }
  }
  /*栏目设置end*/

  // 已授权账套列表
  const accAuthList = ref([])
  // 账套授权信息
  const userAuthMap = ref([])
  const mychild =  ref(null)
  const loadPage = async(data)=>{
    //查询
    kmList.value = data.kmList
    pageParameter.km =  data.minKm
    pageParameter.bzName = data.bzName
    pageParameter.jpzDate =  data.jpzDate
    pageParameter.dpzDate = data.dpzDate
    mychild.value.initPageParameter()
    if (data.openOne == 1){ // 第一次初始化 + 条件查询
      resetDynamicColumnData()
      pageParameter.reloadMark = false
    }else { // 查询条件查询
      closeFilterV()
      pageParameter.searchConditon.value = ''
      pageParameter.reloadMark = true
      pageReload()
    }
  }

  const autoHx = async(data)=>{
    //自动核销 根据当前查询条件 自动核销
    pageParameter.createCode = '1'
    pageParameter.tenantId = manipulateDbName.value
    await useRouteApi(autoWriteOff, {schemaName: manipulateDbName.value})(pageParameter)
    pageReload()
  }
  const addHx = async(data)=>{
    //手动核销 根据选中条件 手动核销 判断是否同意核算单位
    console.log(checkRow.value)
    //贷方选中
    console.log(mychild.value.getSelectData())
    let  a = checkRow.value
    let  b = mychild.value.getSelectData()
    //验证数据
    if((a.length + b.length) <= 1){
      message.error("请选择至少2条数据！")
      return
    }

    let flg = true;
    //借方是否包含不同单位
    a.forEach(v=> {
      a.forEach(o=>{
        if(v.custname != o.custname){
          flg = false;
          return
        }
      })
    })
    //贷方是否包含不同单位
    b.forEach(v=>{
      b.forEach(o=>{
        if(v.custname != o.custname){
          flg = false;
          return
        }
      })
    })
    //借贷方是否包含不同单位
    a.forEach(v=>{
      b.forEach(o=>{
        if(v.custname != o.custname){
          flg = false;
          return
        }
      })
    })
    if(!flg){
      message.error("往来核算单位必须一致！")
      return
    }

    openAddHxPage(true, {
      data1: a,
      data2: b
    })

  }

  const data = ref([])
  //点击的凭证唯一码
  const uniqueCode = ref()
  // 数据库模式名称
  const database = ref(useCompanyOperateStoreWidthOut().getSchemaName);

  const saveHx = async(data)=>{
    console.log(data)
    await useRouteApi(saveManualWriteOff, {schemaName: manipulateDbName.value})({
      list: JSON.stringify(data.list),
      createCode: '1',
      tenantId : ref(getCurrentAccountName(true)).value,
    })
    pageReload()
  }

  const changeHx = (data) => {
    console.log(data)
  };

  const goHxHistiry = () => {
    pageParameter.isHxindex = '1'
    router.push({
      path: '/zhongZhang/write-off/gys-contacts/gys-contacts-history',
      query: pageParameter,
    });
  }
  const cancelTable = (key: string) => {
    delete editableData2[key];
  };
  const fzTable = (record:any) => {
    let d = JSON.parse(JSON.stringify(record))
    d.num =  data.value.length + 1
    data.value.push(d)
  };


  const filterOption = (input: string, option: any) => {
    return option.value.toLowerCase().indexOf(input.toLowerCase()) >= 0;
  };
  const filterOption2 = (input: string, option: any) => {
    return option.value.toLowerCase().indexOf(input.toLowerCase()) >= 0;
  };
  const findName = (value: string) => {
    const list = pcList.value.filter(v=> v.value === value)
    return list.length ===0 ? '' : list[0].label;
  };
  const findCode = (value: string) => {
    const list = dkmList.value.filter(v=> v.value === value)
    return list.length ===0 ? '' : list[0].label;
  };


</script>

<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style scoped>/*针对当前页面特有样式*/
.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 16px !important;
  padding: 5px 8px !important;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
}

.a-table-font-size-16 :deep(td) .a-table-money-font-size {
  font-size: 14px !important;
  color: black;
}

.a-table-font-size-12 :deep(td) .a-table-money-font-size {
  font-size: 13px !important;
  color: black;
}

:deep(.a-table-font-arial) {
  font-family: Arial !important;
}

:deep(.pz-unit-change) {
  width: 50px;
  border-radius: 4px;
  text-align: center;
  font-weight: bold;
  color: black;
  pointer-events: none;
  border: none;
}

.pz-unit-change :deep(.ant-input) {
  font-weight: bold;
  color: black;
}
.app-container {
  padding:  0px;
  margin: 10px 10px 5px;
}
.account-picker-c {
  width: 40%;
  display: inline-block;
  font-size: 14px
}
</style>
