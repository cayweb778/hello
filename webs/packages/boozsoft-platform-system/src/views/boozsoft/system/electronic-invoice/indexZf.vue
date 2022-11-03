<template>
  <div class="app-container">
    <div class="app-container-top">
      <div class="app-container-head">
        <div class="container-head-title" style="float: none;text-align: center;display: revert;">
          <b class="noneSpan" > 作废发票列表 </b>
        </div>

        <div style="margin-top: .5%;">
          <div style="display: inline-block;margin-left: 1%;font-size: 14px;width: 44%">
            <div style="padding-top: 20px">
              <AccountPicker theme="one" readonly="" @reloadTable="dynamicAdReload"/>
            </div>
          </div>
          <div class="ant-btn-group">
            <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false" @click="openQueryPage()"><span>查询</span></button>
            <a-popover placement="bottom">
              <template #content>
                <span class="group-btn-span-special" @click="openQueryPage()">一键取票</span><br/>
                <span class="group-btn-span-special" @click="fpIdentify()">拍照识别</span><br/>
                <span class="group-btn-span-special" @click="fpIdentify()">批量导入</span><br/>
                <span class="group-btn-span-special" @click="fpIdentify()">扫码枪采集</span><br/>
                <span class="group-btn-span-special" @click="fpIdentify()">高拍仪采集</span><br/>
                <span class="group-btn-span-special" @click="fpIdentify()">扫描二维码</span>
              </template>
              <button
                type="button"
                class="ant-btn ant-btn-me"
                ant-click-animating-without-extra-node="false"
              ><span>发票采集</span></button></a-popover>
            <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false"><span>凭证关联</span></button>
            <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false"  @click="sh()" ><span>审核</span></button>
            <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false"  @click="rz()" ><span>认证</span></button>
            <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false"  @click="delSelect()"><span>删除</span></button>
            <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false"><span>制单</span></button>
            <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false"><span>退出</span></button>
          </div>
        </div>

        <div style="clear:both" />
        <div style="margin-top: .5%;">
          <div style="display: inline-block;margin-left: 1%;font-size: 14px;width: 30%">
            <div style="display: inline-block;font-size: 14px;padding-top: 8px">
              <span style="font-size: 14px;color: #aaa;font-weight: bold">发票业务类型：</span>
              <a-select
                show-search
                placeholder="发票业务类型"
                style="width: 180px;font-size: 14px;"
                @change="handleChangeMinKm"
                v-model:value="pageParameter.invoiceHeader.fpType"
              >
                <a-select-option   style="width: 180px;font-size: 14px;"  value="1">
                   进项发票
                </a-select-option>
                <a-select-option   style="width: 180px;font-size: 14px;"  value="2">
                   销项发票
                </a-select-option>
              </a-select>
            </div>
          </div>

          <div style="float: right; margin-left: 10px">
            <a-button class="ant-btn" @click="closeFilterV(),pageReload()">
              <SyncOutlined :style="{ fontSize: '14px' }" />
            </a-button>

            <a-popover placement="bottom">
              <template #content>
                <a-popconfirm
                  ok-text="保存"
                  cancel-text="关闭"
                  @confirm="confirm"
                  @cancel="cancel"
                >
                  <template #icon><b>栏目设置</b><br></template>
                  <template #title>
                    <a-table bordered :data-source="dynamicColumnData" :columns="dynamicColumns"
                             childrenColumnName="children" :pagination="false">
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
                  <a-button style="width: 165px;border: none">栏目设置</a-button>
                </a-popconfirm>
                <br/>
                <span @click="pageParameter.showRulesSize = 'MAX'"
                      :style="{backgroundColor: (pageParameter.showRulesSize==='MAX')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;&emsp;<SortDescendingOutlined
                  :style="{ fontSize: '14px' }"/>&emsp;大号字体&emsp;<CheckOutlined v-if="pageParameter.showRulesSize==='MAX'"
                                                                                :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
                <span @click="pageParameter.showRulesSize = 'MIN'"
                      :style="{backgroundColor: (pageParameter.showRulesSize==='MIN')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;&emsp;<SortAscendingOutlined
                  :style="{ fontSize: '14px' }"/>&emsp;小号字体&emsp;<CheckOutlined v-if="pageParameter.showRulesSize==='MIN'"
                                                                                :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
              </template>
              <template #title>
                <b>设置表格字号</b>
              </template>
              <a-button>
                <SettingFilled :style="{ fontSize: '14px' }"/>
              </a-button>
            </a-popover>
            <a-popover class="ant-btn-default" placement="bottom">
              <template #content>
                <span class="group-btn-span-special" @click="exportExcelNow()">导出当前</span><br/>
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

            <a-button @click="()=>{
              if (!visible){ visible = true;reloadColumns()}
              return false
            }">
              <FilterFilled :style="{ fontSize: '14px' }" />
            </a-button>
          </div>
          <div style="float: right; position: relative">
            <!-- 搜索 -->
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
              style="width: 150px;border-radius: 4px"
            />
          </div>
        </div>
      </div>
    </div>
    <div class="app-container-bottom" >
      <BasicTable
        ref="tableRef"
        :row-selection="{ type: 'checkbox', selectedRowKeys: state1.selectedRowKeys, onChange: onSelectChange1 }"
        :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-13'"
        :scroll="{ x: totalColumnWidth,y: windowHeight }"
        size="small"
        @register="registerTable"
      >
        <template #number="{record,index }">
          <span slot="number" slot-scope="text,record,index">
           {{index + 1}}
          </span>
        </template>


        <template #ywType="{record}">
          <span >{{ ywType(record.ywType) }}</span>
        </template>
        <template #fpStatus="{record}">
          <span >{{ fpStatus(record.fpStatus) }}</span>
        </template>
        <template #djStatus="{record}">
          <span >{{ djStatusData(record.djStatus) }}</span>
        </template>
        <template #rzStatus="{record}">
          <span >{{ rzStatusData(record.rzStatus) }}</span>
        </template>


        <template #fapiaoCheck="{record}">
          <span >{{ fapiaoCheck(record.fapiaoCheck) }}</span>
        </template>
        <template #fapiaoType="{record}">
          <span >{{ fapiaoType(record.fapiaoType) }}</span>
        </template>
        <template #fpType="{record}">
          <span >{{ fpType(record.fpType) }}</span>
        </template>
        <template #buyerSupplier="{record}">
          <span >{{ buyerSupplier(record.fpType,record) }}</span>
        </template>

        <template #caozuo="{ record }">
          <div>
            <a-popover placement="bottom">
              <a-button style="padding: 0px 4px; height: 20px;">
                <CaretDownFilled />
              </a-button>
              <template #content>
                <p style="cursor: pointer" @click="openEdit(record)"><FormOutlined /> 编辑</p>
                <p style="cursor: pointer" @click="del(record)"><DeleteOutlined /> 删除</p>
              </template>
            </a-popover>
          </div>
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
              借方本币：
            </span>
            <div>
              <a-input v-model:value="pageParameter.filterConditon.amountMinJf"  placeholder="" style="width: 95px;font-size: 10px"/>
              ~
              <a-input v-model:value="pageParameter.filterConditon.amountMaxJf"  placeholder="" style="width: 95px;font-size: 10px;float: right"/>
            </div>
          </li>
          <li style="margin-top: 5%">
            <span style="color: black;font-weight: bold">
              贷方本币：
            </span>
            <div>
              <a-input v-model:value="pageParameter.filterConditon.amountMinDf"  placeholder="" style="width: 95px;font-size: 10px"/>
              ~
              <a-input v-model:value="pageParameter.filterConditon.amountMaxDf"  placeholder="" style="width: 95px;font-size: 10px;float: right"/>
            </div>
          </li>
          <li style="margin-top: 5%">
            <span style="color: black;font-weight: bold">
              余额本币：
            </span>
            <div>
              <a-input v-model:value="pageParameter.filterConditon.amountMinYe"  placeholder="" style="width: 95px;font-size: 10px"/>
              ~
              <a-input v-model:value="pageParameter.filterConditon.amountMaxYe"  placeholder="" style="width: 95px;font-size: 10px;float: right"/>
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
      <Identify
        @save="loadPage"
        @register="registerFpIdentifyPage"
      />
    </div>
  </div>
</template>
<script setup lang="ts">
  import { BasicTable, useTable } from '/@/components/Table'
  import { useModal } from '/@/components/Modal'
  import { PageWrapper } from '/@/components/Page'

  import {
    DatePicker as ADatePicker, Select as ASelect, Input as AInput, Popover as APopover, Switch as ASwitch,
    Radio as ARadio, Upload as AUpload, Pagination as APagination, Popconfirm as APopconfirm, Table as ATable,
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
    UsbOutlined,
    FilterFilled,CheckOutlined,EditOutlined,SearchOutlined
  } from '@ant-design/icons-vue'
  import { findAllAccvoucher, breakNumTidy,findDbLanMuList,saveLanMuList } from '/@/api/record/system/accvoucher'
  import { useEiStore } from '/@/api/record/eletronicInvoice_data/eletronic_list'
  import Query from "/@/views/boozsoft/system/electronic-invoice/popup/queryZf.vue";
  import Identify from "/@/views/boozsoft/system/electronic-invoice/popup/identify.vue";
  import {onMounted, reactive, ref} from "vue";
  import moment from "moment";
  import { useCompanyOperateStoreWidthOut } from "/@/store/modules/operate-company";
  import { initDynamics,assemblyDynamicColumn } from "./data";
  import { cloneDeep } from "lodash-es";
  import { saveVoucherData } from "/@/adata.tspi/record/system/financial-settings";
  import { useMessage } from "/@/hooks/web/useMessage";
  import {
    askTask,
    compareTime,
    findByFunctionModule,
    getCurrentAccountName,
    markAnomaly,
    offsetToStr
  } from "/@/api/task-api/tast-bus-api";
  import {
    findAllElectronicInvoice,
    eletronicInvoiceSaveApi, delInvoice,djStatus,rzStatus,delByIds
  } from "/@/api/record/eletronicInvoice_data/eletronic_invoice";
  import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
  import {useUserStoreWidthOut} from "/@/store/modules/user";
  import router from "/@/router";
  import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
  import { exportExcel } from "/@/api/record/generalLedger/excelExport";
  import {columnProps} from "ant-design-vue/es/table/interface";

  const { createConfirm,createWarningModal } = useMessage();
  // 全局常量
  const glStore = useEiStore()

  // 页面变量
  const pageParameter = reactive({
    invoiceHeader:{
      fpType : '1',
      fpStatus: '2',
    },
    queryMark: 'J',
    showRulesSize: 'MIN',
    condition: {},
    searchConditon: {
      requirement: 'fpType',
      value: '',
    },
    filterConditon: {
      amountMinDf: '',
      amountMaxDf: '',
    },
    reloadMark: false,
  })

  const styleName = ref<String>("金额式")
  // 科目名称标题
  const titleName = ref<String>("发票列表")
  // 会计科目
  const kmList: any = ref([])
  //币种名称
  const bzName = ref<String>("")
  //显示未记账
  const ibook = ref<boolean>(true)
  const showStyle = ref([
    {
      'name': '金额式',
      'value': 'J'
    },{
      'name': '数量金额式',
      'value': 'SJ'
    },{
      'name': '外币金额式',
      'value': 'WJ'
    },{
      'name': '数量外币式',
      'value': 'SWJ'
    }
  ])
  //选中内容
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

  const val = {
    openOne: 0
  }
  const visible = ref(false);
  const searchConditonList = ref([])
  const lanMuData = {'accId':'','menuName':'发票列表','type':'',objects: '',username:useUserStoreWidthOut().getUserInfo.username}
  // 表格参数
  const loadMark = ref(false)
  const tableSelectedRowKeys = ref([])
  const tableSelectedRowObjs = ref([])
  const windowWidth  = (document.documentElement.clientWidth -70)
  const windowHeight  = (document.documentElement.clientHeight -350)
  const totalColumnWidth = ref(0)
  // 数据库模式名称
  const databases = ref(getCurrentAccountName(false))
  let databasesName = ref(getCurrentAccountName(true))
  const CrudApi = {
    list: useRouteApi(findAllElectronicInvoice,{schemaName:databasesName.value}),
    columns: JSON.parse(JSON.stringify(glStore.getColumns(pageParameter.queryMark)))
  }
  const tableRef = ref(null)

  // 组件实例区
  const [registerTable, { reload,setColumns,getColumns,getDataSource }] = useTable({
    api: CrudApi.list,
    columns: CrudApi.columns,
    bordered: true,
    loading: loadMark.value,
    immediate: false,
    canResize: true,
    /*  showSummary: true,
      summaryData: [],*/
    showIndexColumn: false , //显示序号列
    pagination:{ pageSize: 200,showSizeChanger: true, pageSizeOptions: ['200','500','1000','1500'],showTotal: t => `总共${t}条数据` },
    /*actionColumn: {
      width: 80,
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
  const [registerQueryPage, { openModal: openQueryPageM }] = useModal()
  const [registerFpIdentifyPage, { openModal: fpIdentifyPage }] = useModal()


  onMounted( ()=>{
    val.openOne = 1
    openQueryPageM(true, {
      data: val
    })
    loadMark.value = true
  })
  // 实例函数区
  function money(val:any) { // 金额格式化
    if(val==null)val=''
    val = val.toString().replace(/\$|\,/g, '')
    if (isNaN(val)) {
      val = '0'
    }
    const sign = (val === (val = Math.abs(val)))
    val = Math.floor(val * 100 + 0.50000000001)
    let cents: string = (val % 100) +''
    val = Math.floor(val / 100).toString()
    if (cents < 10) {
      cents = '0' + cents
    }
    for (let i = 0; i < Math.floor((val.length - (1 + i)) / 3); i++) {
      val = val.substring(0, val.length - (4 * i + 3)) + ',' + val.substring(val.length - (4 * i + 3))
    }
    let re = (((sign) ? '' : '') + val + '.' + cents)
    return re == ('0.00' || '0')?'': re
  }
  function formatData(data:any){
    let str = ""
    if(data){
      // 千分位保留2位小数
      var source = String(data.toFixed(2)).split("."); //按小数点分成2部分
      source[0] = source[0].replace( new RegExp('(\\d)(?=(\\d{3})+$)' , 'ig'), "$1,");//只将整数部分进行都好分割
      str = source.join("." );//再将小数部分合并进来
    }
    return str;
  }
  function formatNum(data:any){
    let str = ""
    if(data){
      if(0 === data){
        str = ""
      }else {
        var source = String(data.toFixed(pageParameter.jd)).split("."); //按小数点分成2部分
        source[0] = source[0].replace( new RegExp('(\\d)(?=(\\d{3})+$)' , 'ig'), "$1,");//只将整数部分进行都好分割
        str = source.join("." );
      }
    }
    return str;
  }
  const openQueryPage = () => {
    val.openOne = 0
    openQueryPageM(true, {
      data: val
    })
  }
  const fpIdentify = () => {
    val.openOne = 0
    fpIdentifyPage(true, {
      data: val
    })
  }

  // 页面函数区
  const onSelectChange = (selectedRowKeys,obj) => {
    tableSelectedRowKeys.value = selectedRowKeys;
    tableSelectedRowObjs.value = obj;
  };
  const pageReload =  () =>{
    reload({
      searchInfo: pageParameter
    })
    checkRow.value = []
  }
  const breakNumTidyBtn = async() => {
    breakNumTidy()
    pageReload()
  }
  const onChangeSwitch = async(v)=>{ // 动态列
    styleName.value = showStyle.value.filter(o=> o.value === v)[0].name
    pageParameter.queryMark = v
    resetDynamicColumnData()
    pageParameter.searchConditon.requirement = 'fx'
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

  const  filterSearch = async ()=>{
    /* if (''==pageParameter.searchConditon.value.trim())return false*/
    // 搜索前校验格式
    let min  = pageParameter.filterConditon.amountMinJf.trim()
    let max  = pageParameter.filterConditon.amountMaxJf.trim()
    if (max != '' ||  min != ''){
      if (min != '' && max != '' && (!isRealNum(min) || !isRealNum(max) || parseFloat(min) == 0 || parseFloat(max) == 0)){
        message.warn('请输入数值类型值并且值不能为0！')
        closeFilterV()
        return false
      }else if (parseFloat(min) > parseFloat(max)){
        message.warn('金额区间最小值部门大于最大值！')
        closeFilterV()
        return false
      }
    }

    let min2  = pageParameter.filterConditon.amountMinDf.trim()
    let max2  = pageParameter.filterConditon.amountMaxDf.trim()
    if (max2 != '' ||  min2 != ''){
      if (min2 != '' && max2 != '' && (!isRealNum(min2) || !isRealNum(max2) || parseFloat(min2) == 0 || parseFloat(max2) == 0)){
        message.warn('请输入数值类型值并且值不能为0！')
        closeFilterV()
        return false
      }else if (parseFloat(min2) > parseFloat(max2)){
        message.warn('金额区间最小值部门大于最大值！')
        closeFilterV()
        return false
      }
    }

    let min3  = pageParameter.filterConditon.amountMinYe.trim()
    let max3  = pageParameter.filterConditon.amountMaxYe.trim()
    if (max != '' ||  min3 != ''){
      if (min3 != '' && max3 != '' && (!isRealNum(min3) || !isRealNum(max3) || parseFloat(min3) == 0 || parseFloat(max3) == 0)){
        message.warn('请输入数值类型值并且值不能为0！')
        closeFilterV()
        return false
      }else if (parseFloat(min3) > parseFloat(max3)){
        message.warn('金额区间最小值部门大于最大值！')
        closeFilterV()
        return false
      }
    }

    // 校验完成后搜索
    pageReload()
  }

  const closeFilterV = ()=>{
    pageParameter.filterConditon.amountMinDf = ''
    pageParameter.filterConditon.amountMaxDf = ''
    pageParameter.filterConditon.amountMinJf = ''
    pageParameter.filterConditon.amountMaxJf = ''
    pageParameter.filterConditon.amountMinYe = ''
    pageParameter.filterConditon.amountMaxYe = ''
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

  /*start栏目设置*/
  const dynamicColumns = initDynamics().DEFAULT
  const dynamicColumnData = ref([])
  let dynamicColumnDataCopy = []
  const editableData = reactive({});

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

    //对子节点处理  过滤有子节点并且变动 添加到a
    lanMuList.forEach((item, index)=>{
      if(item.children){
        let b = item.children.filter(item2=>{
          try {
            copyList[index].children.forEach(item3=>{
              if (item2.key === item3.key && item2.name == item3.name){
                if (item2.nameNew != item3.nameNew || item2.width != item3.width || item2.check != item3.check || item2.align != item3.align)
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
        b.forEach(item=>{
          a.push(item);
        })
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
        let statiList = initDynamics()[pageParameter.queryMark]
        dbList = combineParameters(statiList,dbList)
        dynamicColumnData.value = dbList
        dynamicColumnDataCopy = JSON.parse(JSON.stringify(dbList))
      }else {
        let statiList = initDynamics()[pageParameter.queryMark]
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
        }else{
          //对子节点处理
          if(item.children){
            item.children.forEach(item3=>{
              if (item3.key === item2.key && item3.name === item2.name){
                item3.nameNew = item2.nameNew
                item3.width = parseInt(item2.width)
                item3.check = item2.check == 'true'
                item3.align = item2.align
              }
            })
          }
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
    let newA = JSON.parse(JSON.stringify(glStore.getColumns(pageParameter.queryMark)))
    console.log(newA)
    console.log(dynamicColumnData.value)
    newA= assemblyDynamicColumn(dynamicColumnData.value,newA)
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
  const loadPage = (data)=>{

    pageParameter.invoiceHeader = data
    resetDynamicColumnData()
    closeFilterV()
    pageParameter.searchConditon.value = ''
    pageReload()
  }

  async function handleChangeMinKm(){

    closeFilterV()
    pageReload()
  }

  const userName = useUserStoreWidthOut().getUserInfo.username
  // 数据库模式名称
  const database = ref(useCompanyOperateStoreWidthOut().getSchemaName);
  const goto = async () => {
    const kmStyle =  kmList.value.filter(o=> o.ccode === pageParameter.km)[0].cbookType //科目样式
    if(kmStyle){
      pageParameter.queryMark  = showStyle.value.filter(o=> o.name === kmStyle)[0].value //显示样式
    }else {
      pageParameter.queryMark  = 'J';
    }
    const json = {
      ishaveRjz: ibook.value,
      strDate: pageParameter.strDate.replaceAll(".","-"),
      endDate: pageParameter.endDate.replaceAll(".","-"),
      minJc: pageParameter.minJc,
      maxJc: pageParameter.maxJc,
      km: kmList.value[0].ccode,
      maxKm:  kmList.value[0].ccode,
      timflg: 'qj',
      showStyle: pageParameter.queryMark,
      fontSize: pageParameter.showRulesSize,
      bz: bzName.value,
      bzName:'全部',
      jc: pageParameter.moji,
      riqi: '',
    }
    router.push({
      path: '/account-book/ab-kemuzhang/abk-mxtable',
      query: json,
    });
  }


  const ywType = (data:any)=>{
    let str = ""
    if(data === '1'){
      str = '电子普通发票'
    }else if(data === '2'){
      str = '电子专用发票'
    }else if(data === '2'){
      str = '普通发票'
    }else{
      str = '专用发票'
    }
    return str;
  }
  const fpStatus = (data:any)=>{
    let str = ""
    if(data === '1'){
      str = '正常'
    }else if(data === '2'){
      str = '作废'
    }else if(data === '3'){
      str = '冲红'
    }else{
      str = '异常'
    }
    return str;
  }
  const djStatusData = (data:any)=>{
    let str = ""
    if(data === '1'){
      str = '已审核'
    }else{
      str = '未审核'
    }
    return str;
  }
  const rzStatusData = (data:any)=>{
    let str = ""
    if(data === '1'){
      str = '已认证'
    }else{
      str = '未认证'
    }
    return str;
  }


  const fapiaoCheck = (data:any)=>{
    let str = ""
    if(data === '1'){
      str = '已验真'
    }else{
      str = '未验真'
    }
    return str;
  }

  const fapiaoType = (data:any)=>{
    let str = ""
    if(data === '1'){
      str = '电子普票'
    }else{
      str = '电子专票'
    }
    return str;
  }
  const fpType = (data:any)=>{
    let str = ""
    if(data === '1'){
      str = '进项发票'
    }else{
      str = '销项发票'
    }
    return str;
  }
  const buyerSupplier = (fpType:any,data:any)=>{
    let str = ""
    if(fpType === '1'){
      str = data.buyerSupplier
    }else{
      str = data.sellSupplier
    }
    return str;
  }

  const del = async(data:any)=>{
    if(!data.accuniqueCode){
      await delInvoice(data.id)
      message.success('删除成功!')
      pageReload()
    }else{
      message.error('改发票已被使用,不能删除!')
      return false;
    }
  }
  const openEdit = async(data:any)=>{

    console.log(data)
    router.push({
      path: '/zhongZhang/electronic-invoice/ei-mange/ei-invoice-mx',
      query: data,
    });

  }

  // 默认账套编码
  let defalutCompany = getCurrentAccountName(true)
  // 切换后账套编码
  const dynamicCompany = ref(defalutCompany)
  const dynamicTenantId = ref(getCurrentAccountName(true))

  const sh = async(data:any)=>{
    //审核 不能删除 修改
    console.log(checkRow.value)
    if (checkRow.value.length > 0) {
      createConfirm({
        iconType: 'error',
        title: '警告',
        content: '你确认要审核吗?',
        onOk: async () => {
          let item = []
          for (let i = 0; i < checkRow.value.length; i++) {
            item.push(checkRow.value[i].id)
          }
          if(item.length > 0){
            await useRouteApi(djStatus, {schemaName: dynamicTenantId.value})({ids:item})
            message.success('审核成功！')
          }else{
            message.error('请选择至少一条数据！')
          }
          checkRow.value = []
          pageReload()
        },
        onCancel: () => {
          return false
        }
      })
    } else {
      createWarningModal({
        iconType: 'warning',
        title: '审核',
        content: '请选择需要审核的内容！'
      })
    }
  }

  const rz = async(data:any)=>{
    //审核 不能删除 修改
    console.log(checkRow.value)
    if (checkRow.value.length > 0) {
      createConfirm({
        iconType: 'error',
        title: '警告',
        content: '你确认要认证吗?',
        onOk: async () => {
          let item = []
          for (let i = 0; i < checkRow.value.length; i++) {
            //先审核在认证 只能认证 专票 进项发票
            console.log(checkRow.value[i])
            if(checkRow.value[i].djStatus === '1' && checkRow.value[i].fpType === '1' && checkRow.value[i].fapiaoType === '2'){
              item.push(checkRow.value[i].id)
            }
          }
          if(item.length > 0){
            await useRouteApi(rzStatus, {schemaName: dynamicTenantId.value})({ids:item})
            message.success('认证成功！')
          }else{
            message.error('请选择至少一条数据,并且确保数据是已审核进项专票！')
          }
          checkRow.value = []
          pageReload()
        },
        onCancel: () => {
          return false
        }
      })
    } else {
      createWarningModal({
        iconType: 'warning',
        title: '认证',
        content: '请选择需要认证的内容！'
      })
    }
  }


  const delSelect = async(data:any)=>{
    //审核 使用 不能删除
    console.log(checkRow.value)
    if (checkRow.value.length > 0) {
      createConfirm({
        iconType: 'error',
        title: '警告',
        content: '你确认要删除吗?',
        onOk: async () => {
          let item = []
          for (let i = 0; i < checkRow.value.length; i++) {
            //审核 使用 不能删除
            console.log(checkRow.value[i].accuniqueCode)
            if(checkRow.value[i].djStatus === '1' && typeof(checkRow.value[i].accuniqueCode) === 'undefined' ){
              item.push(checkRow.value[i].id)
            }
          }
          if(item.length > 0){
            await useRouteApi(delByIds, {schemaName: dynamicTenantId.value})({ids:item})
            message.success('删除成功！')
          }else{
            message.error('请选择至少一条数据！')
          }
          checkRow.value = []
          pageReload()
        },
        onCancel: () => {
          return false
        }
      })
    } else {
      createWarningModal({
        iconType: 'warning',
        title: '认证',
        content: '请选择需要删除的内容！'
      })
    }

  }


  const exportExcelNow = async () => {
    const data = JSON.parse(JSON.stringify(getDataSource()))
    const columns = getColumns()


    const keys = []
    const title = []
    const cellStyle = [
      {
        cell: 'A1',
        font: {
          sz: 16,
          color: { rgb: "000000" },
          bold: true,
        },
        fill: {
          fgColor: { rgb: "ffffff" },
        }
      }
    ]
    columns.forEach((v,index)=>{
      //多级表头
      if(index > 0 && index < columns.length-1){
        keys.push(v.key)
        title.push(v.title)
      }
    })
    //格式化调整
    data.forEach(v=>{
      v.fpType = fpType(v.fpType)
      v.ywType = ywType(v.ywType)
      v.fpStatus = fpStatus(v.fpStatus)
      v.djStatus = djStatusData(v.djStatus)
      v.rzStatus =  rzStatusData(v.rzStatus)
      v.fapiaoCheck =  fapiaoCheck(v.fapiaoCheck)
      if(!v.accuniqueCode){
        console.log(v.accuniqueCode)
        v.accuniqueCode = ' '
      }
    })

    const sheet =[
      {
        title: titleName.value,
        tHeader: title,
        table: data,
        keys: keys,
        sheetName: pageParameter.km +'-'+ titleName.value,
        cellStyle: cellStyle
      },
    ]
    exportExcel(sheet, 'xlsx','发票列表')
  }

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
  padding: 0px;
  margin: 10px 10px 5px;
}

</style>
