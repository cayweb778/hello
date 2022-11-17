<template>
  <div>
    <div class="app-container lcr-theme-div">
      <div>
        <div>
          <ProfileOutlined style="color: #0096c7;font-size: 50px;"/>
        </div>
        <div> <AccountPicker theme="three" @reloadTable="dynamicAdReload"/></div>
      </div>
      <div>
        <div>  <b class="noneSpan" style="font-size: 36px;color: #0096c7;">存货档案</b></div>
      </div>
      <div>
        <div>
          <button type="button" class="ant-btn ant-btn-me" @click="addFun"><span>新增</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="editFun" ><span>修改</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="batchEditFun" ><span>批改</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="delFun" ><span>删除</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="improtExcelFun"><span>导入</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="exportExcel123()"><span>导出</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="editFlag"><span>启\停</span></button>
<!--          <button type="button" class="ant-btn ant-btn-me" @click="openModalPop"><span>测试弹窗</span></button>-->
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent(),router.push('/one/home/welcome')"><span>退出</span></button>
        </div>
        <div>
          <div>
            <a-select
              v-model:value="pageParameter.searchConditon.requirement"
              style="width: 130px;text-align: left;"
              class="special_select"
            >
              <a-select-option value="stockNum">存货编码</a-select-option>
              <a-select-option value="stockBarcode">条形码</a-select-option>
              <a-select-option value="stockName">存货名称</a-select-option>
              <a-select-option value="stockGgxh">规格型号</a-select-option>
              <a-select-option value="stockClass">存货分类</a-select-option>
              <a-select-option value="stockJiliang">计量单位</a-select-option>
              <a-select-option value="stockBrand">品牌</a-select-option>
            </a-select>
            <a-input-search placeholder="" style="width: 200px; border-radius: 4px" v-model:value="pageParameter.searchConditon.value" @search="findAllStock"/>
          </div>
          <div>
            <a-button class="ant-btn-me">
              <SyncOutlined :style="{ fontSize: '14px' }" @click="pageReload2"/>
            </a-button>
            <a-popover placement="bottom" v-model:visible="visible">
              <template #content>
                <DynamicColumn :defaultData="initDynamics()['DATA']" :dynamicData="dynamicColumnData" :lanmuInfo="lanMuData" @reload="reloadColumns"/>
                <span @click="pageParameter.showRulesSize = 'MAX'"
                      :style="{backgroundColor: (pageParameter.showRulesSize==='MAX')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;大号字体&emsp;<CheckOutlined v-if="pageParameter.showRulesSize==='MAX'"
                                                                                :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
                <span @click="pageParameter.showRulesSize = 'MIN'"
                      :style="{backgroundColor: (pageParameter.showRulesSize==='MIN')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;小号字体&emsp;<CheckOutlined v-if="pageParameter.showRulesSize==='MIN'"
                                                                                :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
              </template>
              <a-button>
                <SettingFilled :style="{ fontSize: '14px' }"/>
              </a-button>
            </a-popover>
            <a-popover  placement="bottom">
                        <template #content>
                           <span class="group-btn-span-special2" @click="findByStockFlag('')" :style="pageParameter.stockFlag==''?{backgroundColor: '#0096c7',color: 'white'}:''">
                            <ReadOutlined/>&nbsp;&nbsp;全部&emsp;&ensp;<CheckOutlined  v-if="pageParameter.stockFlag==''"/></span><br/>
                          <span class="group-btn-span-special2" @click="findByStockFlag('1')" :style="pageParameter.stockFlag=='1'?{backgroundColor: '#0096c7',color: 'white'}:''">
                            <SafetyOutlined/>&nbsp;&nbsp;启用&emsp;&ensp;<CheckOutlined  v-if="pageParameter.stockFlag=='1'"/></span><br/>
                          <span class="group-btn-span-special2" @click="findByStockFlag('0')" :style="pageParameter.stockFlag=='0'?{backgroundColor: '#0096c7',color: 'white'}:''">
                            <RestOutlined/>&nbsp;&nbsp;停用&emsp;&ensp;<CheckOutlined  v-if="pageParameter.stockFlag=='0'"/></span><br/>
                        </template>
                        <a-button>
                          <PicLeftOutlined :style="{ fontSize: '14px' }"/>
                        </a-button>
                      </a-popover>
          </div>
        </div>
      </div>
    </div>
    <div class="app-container">
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <div class="bg-white m-4 mr-0 overflow-hidden" style="margin-top: -0.5px;margin-left: -5px;float: left;width: 20%;text-align: left;">
          <div style="width: 100%; height: 32px;background-color: #d9d9d9;padding-top:5px;font-weight: bold;text-align: center;">
            存货分类
            <div style="float: right;">
              <a style="color: black;font-size: 14px;" @click="stockClassModalShow"><PlusCircleTwoTone /></a>&emsp;
            </div>
          </div>
          <BasicTree
            defaultExpandAll
            :tree-data="treeData"
            v-if="treeData.length"
            v-model:selectedKeys="selectedKeys2"
            @select="treeChange"
          />
        </div>
        <div style="width: 80%;float: right;margin-left: 5px;">
          <BasicTable ref="tableRef" @register="registerTable" :bordered="true" :dataSource="tableData"
                      :loading="loadingMsg"
                      :row-selection="{ type: 'checkbox',getCheckboxProps:rowSelection.getCheckboxProps }" @row-click="rowDbClick" :scroll="{ x: totalColumnWidth,y: windowHeight }"
                      :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'">


            <template #stockPropertyMarket="{ record }"><span v-if="record.stockPropertyMarket=='1'"><CheckOutlined/></span></template>
            <template #stockPropertyPurchase="{ record }"><span v-if="record.stockPropertyPurchase=='1'"><CheckOutlined/></span></template>
            <template #stockPropertyProduction="{ record }"> <span v-if="record.stockPropertyProduction=='1'"><CheckOutlined/></span> </template>
            <template #stockPropertyEntrust="{ record }"> <span v-if="record.stockPropertyEntrust=='1'"><CheckOutlined/></span> </template>
            <template #stockPropertyYslwfy="{ record }"> <span v-if="record.stockPropertyYslwfy=='1'"><CheckOutlined/></span> </template>
            <template #stockPropertySeriesNumber="{ record }"> <span v-if="record.stockPropertySeriesNumber=='1'"><CheckOutlined/></span> </template>
            <template #stockPropertyMa="{ record }"> <span v-if="record.stockPropertyMa=='1'"><CheckOutlined/></span> </template>
            <template #stockPropertyBatch="{ record }"> <span v-if="record.stockPropertyBatch=='1'"><CheckOutlined/></span> </template>
            <template #stockIndateManage="{ record }"> <span v-if="record.stockIndateManage=='1'"><CheckOutlined/></span> </template>

            <template #stockNum="{ record }">
              <a @click="showInfo(record)">{{ record.stockNum }}</a>
            </template>
            <template #stockName="{ record }">
              <a @click="showInfo(record)">{{ record.stockName }}</a>
            </template>
            <template #stockFlag="{ record }">
              <a-tag v-if="record.stockFlag!=='11'" :color="record.stockFlag === '1' ? 'green' : 'volcano'">
                {{ record.stockFlag=='1'?'启用':'停用' }}
              </a-tag>
            </template>
          </BasicTable>
          <div class="pagination-text" :style="{top: (windowHeight+45)+'px'}" v-show="showPaginationText">
            {{`共 ${tableData.filter(a=>a.id!=null).length}条记录&emsp;每页 200 条`}}
          </div>
        </div>
      </PageWrapper>
    </div>
    <Add @save="pageReload" @closeOk="pageReload2" @register="registerSavePage" />
    <Edit @save="pageReload" @closeOk="pageReload2" @register="registerEditPage" />
    <ImprotExcel @save="pageReload2" @closeOk="pageReload2" @register="registerImprotExcelPage" />
    <StockClassAdd @save="stockClassSaveData" @register="registerStockClassSavePage" />
    <StockInfoModalPop @save="stockClassSaveData" @register="registerStockInfoPage" />
    <BatchEdit @closeOk="pageReload2" @register="registerBatchEditPage" />
  </div>
</template>
<script setup lang="ts">
import {
  CheckOutlined,
  EditOutlined,
  PicLeftOutlined,
  PlusCircleTwoTone,
  ReadOutlined,
  RestOutlined,
  SafetyOutlined,
  SettingFilled,
  SyncOutlined,
  UnorderedListOutlined,
  ProfileOutlined,
} from '@ant-design/icons-vue'
import router from "/@/router";
import {BasicTable, useTable} from '/@/components/Table';
import {
  Button as AButton,
  Checkbox as ACheckbox,
  Input as AInput,
  message,
  Popconfirm as APopconfirm,
  Popover as APopover,
  Radio as ARadio,
  Select as ASelect,
  Statistic as AStatistic,
  Table as ATable,
  Tabs as ATabs,
  Tag as ATag,
} from 'ant-design-vue';
import {reactive, ref} from 'vue';
import {getCurrentAccountName, hasBlank} from "/@/api/task-api/tast-bus-api";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker-DATA.vue";
import {useTabs} from "/@/hooks/web/useTabs";
import {useMessage} from "/@/hooks/web/useMessage";
import {BasicTree} from '/@/components/Tree';
import {PageWrapper} from '/@/components/Page';
import {useUserStoreWidthOut} from '/@/store/modules/user';
import {assemblyDynamicColumn, initDynamics} from "/@/views/boozsoft/stock/stock_info/data";
import {cloneDeep} from "lodash-es";
import {findDbLanMuList, saveLanMuList} from "/@/api/record/system/accvoucher";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {
  findAllStockClassBend1,
  GetStockClassTree,
  stockSaveApi
} from "/@/api/record/stock/stock_class";
import {delAll, editStockFlag, findAll, verifyStockIsData} from "/@/api/record/stock/stock";
import Add from "/@/views/boozsoft/stock/stock_info/popup/save.vue";
import Edit from "/@/views/boozsoft/stock/stock_info/popup/edit.vue";
import ImprotExcel from "/@/views/boozsoft/stock/stock_info/popup/improtExcel.vue";
import StockClassAdd from '/@/views/boozsoft/stock/stock_class/popup/save.vue';
import StockInfoModalPop from '/@/views/boozsoft/stock/stock_info/popup/stockInfoModalPop.vue';
import BatchEdit from '/@/views/boozsoft/stock/stock_info/popup/batchEdit.vue';
import {useModal} from "/@/components/Modal";
import {findCangkuJoinName} from "/@/api/record/stock/stock-cangku-level-record";
import {aoaToSheetXlsx} from "/@/components/Excel";
import {findUnitInfoById} from "/@/api/record/system/unit-mea";
import DynamicColumn from "/@/views/boozsoft/stock/stock_sales_add/component/DynamicColumn.vue";

// 全局常量
const treeData:any = ref([]);
const selectedKeys2:any = ref<string[]>(['0'])
const {createConfirm, createWarningModal, createMessage} = useMessage();
const ARadioGroup = ARadio.Group
const ARadioButton = ARadio.Button
const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const AStatisticCountdown = AStatistic.Countdown;
const ATabPane = ATabs.TabPane;
const baseName = ref('');
const database = ref(getCurrentAccountName(true));
const accId = ref('');
const iyear = ref('');
const loadingMsg = ref(false);
const tableData = ref([])
const serachVal = ref('')
const dynamicColumnData = ref({value:[]})
const dynamicColumns = initDynamics().DEFAULT
const editableData = reactive({});
const windowWidth  = (window.innerWidth -(70))
const windowHeight = (window.innerHeight - (315))
const visible = ref(false);
const totalColumnWidth = ref(0)
const tableRef:any = ref(null)
const CrudApi = {
  columns: [
    {
      title: '状态',
      dataIndex: 'stockFlag',
      ellipsis: true,
      align: 'left',slots: { customRender: 'stockFlag' },
    },
    {
      title: '存货编码',
      dataIndex: 'stockNum',
      ellipsis: true,
      align: 'left',slots: { customRender: 'stockNum' },
    },
    {
      title: '存货名称',
      dataIndex: 'stockName',
      ellipsis: true,
      align: 'left',slots: { customRender: 'stockName' },
    },
    {
      title: '规格型号',
      dataIndex: 'stockGgxh',
      ellipsis: true,
      align: 'left',
    },
    {
      title: '存货分类',
      dataIndex: 'stockCclassName',
      ellipsis: true,
      align: 'cneter',
    },{
      title: '条形码',
      dataIndex: 'stockBarcode',
      ellipsis: true,
      align: 'cneter',
    },{
      title: '计量方式',
      dataIndex: 'stockMeasurementType',
      ellipsis: true,
      align: 'cneter',
    },
    {
      title: '计量单位',
      dataIndex: 'stockMeasurementName',
      ellipsis: true,
      align: 'cneter',
    },
    {
      title: '计价方式',
      dataIndex: 'stockValuationType',
      ellipsis: true,
      align: 'cneter',
    },
    {
      title: '销售',
      dataIndex: 'stockPropertyMarket',
      ellipsis: true,
      align: 'cneter',slots: { customRender: 'stockPropertyMarket' },
    },
    {
      title: '采购',
      dataIndex: 'stockPropertyPurchase',
      ellipsis: true,
      align: 'cneter',slots: { customRender: 'stockPropertyPurchase' },
    },{
      title: '生产',
      dataIndex: 'stockPropertyProduction',
      ellipsis: true,
      align: 'cneter',slots: { customRender: 'stockPropertyProduction' },
    },
    {
      title: '委托',
      dataIndex: 'stockPropertyEntrust',
      ellipsis: true,
      align: 'cneter',slots: { customRender: 'stockPropertyEntrust' },
    }, {
      title: '应税劳务',
      dataIndex: 'stockPropertyYslwfy',
      ellipsis: true,
      align: 'cneter',slots: { customRender: 'stockPropertyYslwfy' },
    },{
      title: '序列号',
      dataIndex: 'stockPropertySeriesNumber',
      ellipsis: true,
      align: 'cneter',slots: { customRender: 'stockPropertySeriesNumber' },
    },{
      title: 'MA管理',
      dataIndex: 'stockPropertyMa',
      ellipsis: true,
      align: 'cneter',slots: { customRender: 'stockPropertyMa' },
    },{
      title: '批次管理',
      dataIndex: 'stockPropertyBatch',
      ellipsis: true,
      align: 'cneter',slots: { customRender: 'stockPropertyBatch' },
    },
    {
      title: '有效期管理',
      dataIndex: 'stockIndateManage',
      ellipsis: true,
      align: 'cneter',slots: { customRender: 'stockIndateManage' },
    },{
      title: '有效期单位',
      dataIndex: 'stockIndateUnit',
      ellipsis: true,
      align: 'cneter'
    },{
      title: '有效期时长',
      dataIndex: 'stockIndateDuration',
      ellipsis: true,
      align: 'cneter'
    },{
      title: '预警天数',
      dataIndex: 'stockIndateWarningDay',
      ellipsis: true,
      align: 'cneter'
    },{
      title: '品牌',
      dataIndex: 'stockBrand',
      ellipsis: true,
      align: 'cneter'
    },{
      title: '存货代码',
      dataIndex: 'stockCode',
      ellipsis: true,
      align: 'cneter'
    },{
      title: '助记码',
      dataIndex: 'stockZjm',
      ellipsis: true,
      align: 'cneter'
    },{
      title: '生产厂商',
      dataIndex: 'stockManufacture',
      ellipsis: true,
      align: 'cneter'
    },{
      title: '生产地点',
      dataIndex: 'stockManufactureAddress',
      ellipsis: true,
      align: 'cneter'
    },{
      title: '生产地',
      dataIndex: 'stockCountry',
      ellipsis: true,
      align: 'cneter'
    },{
      title: '默认供应商',
      dataIndex: 'supName',
      ellipsis: true,
      align: 'left',
    },
    {
      title: '默认仓库',
      dataIndex: 'stockCangkuName',
      ellipsis: true,
      align: 'left',
    },
  ]
}
const pageParameter = reactive({
  showRulesSize:'MIN',
  stockFlag:'',
  stockClass:'',
  searchConditon:{
    requirement:'stockNum',
    value:''
  }
})
const rowSelection = {
  getCheckboxProps: (record) => ({
    disabled: record.id==undefined
  }),
};
function rowDbClick(data) {
  if(hasBlank(data.id)){
    deleteSelectRowByKey(data.key)
  }
}
const { closeCurrent } =useTabs();
const [registerSavePage, { openModal: openSavePage }] = useModal();
const [registerEditPage, { openModal: openEditPage }] = useModal();
const [registerImprotExcelPage, { openModal: openImprotExcelPage }] = useModal();
const [registerStockClassSavePage, { openModal: openStockClassSavePage }] = useModal();
const [registerStockInfoPage, { openModal: openStockInfoPage }] = useModal();
const [registerBatchEditPage, { openModal: openBatchEditPage }] = useModal();

// 这是示例组件
const [registerTable, { reload,getSelectRows,getDataSource,setTableData,setColumns,clearSelectedRowKeys,getColumns,deleteSelectRowByKey }] = useTable({
  columns: initDynamics()['DATA'],
  loadingMsg:loadingMsg.value,
  indexColumnProps:{ fixed:true },
  pagination: {
    pageSize: 200,
    simple:true,
},
});
const dynamicAdReload = async (obj) => {
  loadingMsg.value=true
  console.log(obj)
  accId.value=obj.accId
  iyear.value=obj.year
  database.value=obj.accountMode
  baseName.value=obj.baseName
  visible.value = true
  setTimeout(()=>
      visible.value = false
    ,100)
  lanMuData.value.changeNumber+=1
  fetch()
  findAllStock()
}
async function fetch() {
  let deptTree = await useRouteApi(GetStockClassTree,{schemaName: database})('')
  deptTree=deptTree.length==0?[]:deptTree.filter(aa=>aa.flag=='1')
  function a(customerTree) {
    customerTree.forEach((item) => {
      if (item.children != null) {
        a(item.children);
      }
      item.title = '[' + item.stockClass + '] ' + item.stockCclassName;
      item.key = item.uniqueStockclass;
    });
  }
  a(deptTree);
  treeData.value = []
  treeData.value.push({title: '全部',key:'0',children: deptTree})
  selectedKeys2.value=['0']
}
function treeChange(obj) {
  if(obj.toString()!==''){
    selectedKeys2.value=obj
    pageParameter.stockClass=obj.toString()
  }else{
    selectedKeys2.value=['0']
    pageParameter.stockClass='0'
  }
  pageReload()
}

const showPaginationText=ref(false)
async function findAllStock() {
  loadingMsg.value=true
  tableData.value=[]
  let temp=await useRouteApi(findAll,{schemaName: database})(pageParameter)
  temp.forEach(async (a)=>{
      let cangkuJoinName=await useRouteApi(findCangkuJoinName,{schemaName: database})(  {id:a.stockCangku})
      a.stockCangkuName=cangkuJoinName[0].cangkuRecordJoinName

      if(a.stockMeasurementType=='多计量'){
        let djlMx=await useRouteApi(findUnitInfoById,{schemaName: database})({id:a.stockMeasurementUnit})
        let mxlist=JSON.parse(djlMx.detail)

        let name1=""
        let name2=""
        if(mxlist.length==1){
          name1=mxlist[0].unitName
        }else if(mxlist.length==2){
          name1=mxlist[0].unitName
          name2=mxlist[1].unitName
        }
        a.stockUnitName1=name1
        a.stockUnitName2=name2
      }
  })

  showPaginationText.value=false
  if(temp.length<50){
    for (let i =  temp.length; i < 50; i++) {
      temp.push({stockFlag:"11"})
    }
  }

  setTimeout(()=>{
    tableData.value=temp
    showPaginationText.value=true
    loadingMsg.value=false
  },200)
}
async function addFun() {
  const aa = await useRouteApi(findAllStockClassBend1, {schemaName: database})('')
  if(aa.length==0){
    let te={stockCclassName:'默认分类',stockClass:'9999'}
    await useRouteApi(stockSaveApi,{schemaName: database})({params:te})
  }
  openSavePage(true,{
    database:database.value,
    iyear:iyear.value,
  })
}
function editFun() {
  if(getSelectRows().length!==1){
    return createWarningModal({content: '只能选择一项进行修改！'});
  }
  openEditPage(true,{
    database:database.value,
    iyear:iyear.value,
    data:getSelectRows()[0],
    isEdit:true
  })
}

function delFun() {
  if(getSelectRows().length==0){
    return createWarningModal({content: '至少选择一项进行删除！'});
  }
  createConfirm({
    iconType: 'warning',
    title: '警告',
    content: '数据不可恢复,确定删除吗?',
    onOk: async () => {
      for (let i = 0; i < getSelectRows().length; i++) {
        // 是否已做过单子
        let stockIsData=await useRouteApi(verifyStockIsData, { schemaName: database })(getSelectRows()[i].stockNum)
        if(stockIsData>0){
          return createWarningModal({content: '【'+getSelectRows()[i].stockNum+'-'+getSelectRows()[i].stockName+'】该存货已经在系统中被使用，不能删除！'});
          break;
        }
      }
      await useRouteApi(delAll, { schemaName: database })(getSelectRows()).then((item) => {
        pageReload()
      });
    },
  });
}

async function pageReload() {
  clearSelectedRowKeys()
  findAllStock()
}
async function pageReload2() {
  loadingMsg.value=true
  clearSelectedRowKeys()
  fetch()
  findAllStock()
}
function showInfo(data) {
  openEditPage(true,{
    database:database.value,
    iyear:iyear.value,
    data:data,
    isEdit:false
  })
}
function stockClassModalShow() {
  let val = {
    parentId: '',
    stockCclassName: '',
    stockClass: '',
  };
  openStockClassSavePage(true, {
    data: val,
    database:database.value,
    isEdit:false
  });
}
async function stockClassSaveData(data) {
  await useRouteApi(stockSaveApi,{schemaName: database})({params:data});
  fetch()
}
async function editFlag() {
  if(getSelectRows().length==0){
    return createWarningModal({content: '至少选择一项！'});
  }
  await useRouteApi(editStockFlag,{schemaName: database})(getSelectRows()).then((item) => {
    pageReload()
  });
}
async function findByStockFlag(flag) {
  pageParameter.stockFlag=flag
  pageReload()
}
// 导出Excel
const exportExcel123 = async () => {
  const data = JSON.parse(JSON.stringify(getDataSource()))
  const columns = ['存货编码','存货名称','规格型号','规格型号1','规格型号2','存货分类名称','计量单位名称','默认采购单位','默认销售单位','助记码',
    '条形码','条形码1','条形码2','存货代码','生产厂商','生产地点','品牌','计价方式','供应商名称','仓库名称','是否销售','是否采购',
    '是否生产','是否委托','是否应税劳务费用','是否批次管理','是否序列号管理','是否MA管理','是否有效期管理','有效期单位','有效期时长']
  const dataArr:any=[]
  data.forEach(v=>{
    let temp:any=[]
    temp[0]=v.stockNum,
      temp[1]=v.stockName,
      temp[2]=v.stockGgxh,
      temp[3]=v.stockUnitBarcode1,
      temp[4]=v.stockUnitBarcode2,
      temp[5]=v.stockCclassName,
      temp[6]=v.stockMeasurementName,
      temp[7]=v.stockMeasurementType=='多计量'?v.stockUnitName1:v.stockMeasurementName,
      temp[8]=v.stockMeasurementType=='多计量'?v.stockUnitName2:v.stockMeasurementName,
    dataArr.push(temp)
  })
  aoaToSheetXlsx({
    data: dataArr,
    header: columns,
    filename: baseName.value+'-存货档案.xlsx',
  });
}
function improtExcelFun() {
  openImprotExcelPage(true,{
    database:database.value,
    iyear:iyear.value,
    baseName:baseName.value,
  })
}
function openModalPop() {
  openStockInfoPage(true,{
    database:database.value,
  })
}
/*************************** 栏目操作 **********************************/
const lanMuData = ref({
  accId: database,
  menuName: '存货信息',
  type: '列表',
  objects: '',
  username: useUserStoreWidthOut().getUserInfo.username,
  changeNumber:0
})
const reloadColumns = () => {
  let newA = JSON.parse(JSON.stringify(CrudApi.columns))
  newA = assemblyDynamicColumn(dynamicColumnData.value.value, newA)
  setColumns(newA)
  initTableWidth(newA)
}
function initTableWidth(thisCs) {
  let total = 60
  thisCs.forEach(item => {
    if (item.ifShow == null || item.ifShow)
      total += parseInt(item.width)
  })
  /*  // 去除操作咧宽
    total -= 100*/
  if (total > windowWidth) {
    let f = 0
    if (visible.value) f = 260
    totalColumnWidth.value = Number(windowWidth) - f
    // tableRef.value.$el.style.setProperty('width', (windowWidth + 40 - f) + 'px')
  } else {
    if (visible.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
    totalColumnWidth.value = total
    // tableRef.value.$el.style.setProperty('width', (total + 40) + 'px')
  }
}
/************************************* End **********************************/

function batchEditFun() {
  if(getSelectRows().length==0){
    return createWarningModal({content: '至少选择一项进行批改！'});
  }
  openBatchEditPage(true,{
    database:database.value,
    iyear:iyear.value,
    idlist:getSelectRows().map(a=>a.id).join(','),
    stockNumlist:getSelectRows().map(a=>a.stockNum).join(',')
  })
}
</script>
<style lang="less" scoped>
@import './global-menu-index.less';
.bg-white{
  width: 250px;
  height: calc(100% - 240px);
  border: 1px #cccccc solid;
  background:white ;
  margin-top: -0.5%;
}

.lcr-theme-div{
  display: inline-flex;justify-content: space-between;width: 99%;height: 100px;text-align: left;
  >div:nth-of-type(1){
    width: 40%;display: inline-flex;margin-top: 20px;
    >div:nth-of-type(1){width: 64px;display: inline-block;text-align: center;vertical-align: super;}
    >div:nth-of-type(2){
      width: calc( 100% - 64px);display: inline-block;margin-top: 10px;
      >div:nth-of-type(2){margin-top: 14px;}
    }
  }
  >div:nth-of-type(2){
    width: 20%;text-align:center;
    >div:nth-of-type(2){margin-top: 14px;}
  }
  >div:nth-of-type(3){
    width: 40%;text-align: right;
    >div:nth-of-type(1){
      .ant-btn-me {
        color: #0096c7;
      }
    }
    >div:nth-of-type(2){
      display: inline-flex;justify-content: space-between;margin-top: 15px;
    }
  }
}

a {
  color: #0096c7;
  text-decoration: none;
  cursor: pointer;
}
.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
  border-color: #aaaaaa !important;
  height: 25px;
  color: black;
}
.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 13px !important;
  padding: 2px 8px !important;
  border-color: #aaaaaa !important;
  height: 25px;
  color: black;
  border-left: 0px solid #c9c9c9 !important;
}
.app-container:nth-of-type(1) {
  background-color: #f2f2f2;
  padding: 10px 5px;
  margin: 10px 10px 5px;
}
.app-container:nth-of-type(2) {
  padding: 0px;
  margin: 5px 10px;
  background: #b4c8e3 !important;
  position: relative;
  .pagination-text {
    position: absolute;
    bottom: 6px;
    right: 10%;
    font-size: 13px;
    color: black;
    z-index: 99999999;
  }
}
:deep(.ant-table-thead) th{
  text-align: center !important;
  font-weight: bold;
  background-color: #cccccc !important;
  border-color: #aaaaaa !important;
}
:deep(.vben-basic-table) .ant-table-wrapper {
  padding: 0px;
}

:deep(.vben-basic-table) .ant-table {
  width: 100%;
  margin: 0;
  overflow-x: hidden;
  height: calc(100% - 160px);
  min-height: 500px;
}
:deep(.vben-basic-table) .ant-pagination {
  background-color: #cccccc;
  padding-right: 20px;
  padding-top: 5px;
  padding-bottom: 5px;
}
:deep(.vben-basic-table){
  min-height: 500px;
  height: calc(100% - 160px);
  margin-bottom: 20px;
}
:deep(.ant-input),:deep(.ant-select),:deep(.ant-btn){
  border: 1px solid #c9c9c9;
}

:deep(.ant-table-measure-row){
  td{
    padding: 0 !important;
  }
}
</style>
