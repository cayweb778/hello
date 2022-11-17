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
        <div> <b class="noneSpan" style="font-size: 36px;color: #0096c7;">BOM物料清单列表</b></div>
      </div>
      <div>
        <div>
          <button type="button" class="ant-btn ant-btn-me"  @click="startOpenTags('add')"><span>新增</span></button>
          <button type="button" class="ant-btn ant-btn-me"  @click="startOpenTags('edit')"><span>修改</span></button>
<!--          <button type="button" class="ant-btn ant-btn-me" @click="batchEditFun" ><span>批改</span></button>-->
          <button type="button" class="ant-btn ant-btn-me" @click="delFun" ><span>删除</span></button>
          <button type="button" class="ant-btn ant-btn-me" ><span>审核</span></button>
          <button type="button" class="ant-btn ant-btn-me" ><span>复制</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="exportExcel123()"><span>导出</span></button>
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
              <a-select-option value="stockJiliang">计量单位</a-select-option>
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
        <div style="max-height: 370px">
          <BasicTable ref="tableRef" @register="registerTable" :data-source="tableData"
                      :loading="loadingMsg"
                      :row-selection="{ type: 'checkbox',getCheckboxProps:rowSelection.getCheckboxProps }" @row-click="rowDbClick" :scroll="{ x: totalColumnWidth,y: windowHeight-400 }" :clickToRowSelect="false"
                      :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'">

            <template #bcheck="{ record }">
              <template v-if="record.id  != null">
                <Tag color="#87d068" v-if="record.bcheck  === '1'">已审核</Tag>
                <Tag color="#f50" v-else>未审核</Tag>
              </template>
            </template>
            <template #bcloser="{ record }">
              <template v-if="record.id  != null">
                <Tag color="#87d068" v-if="record.bcloser  === '1'">已停用</Tag>
                <Tag color="#f50" v-else>未停用</Tag>
              </template>
            </template>
            <template #unitId="{ record }">{{ formatCunitid(record.bomId,record.unitId) }}</template>
            <template #stockGgxh="{ record }">
              <span>{{formatGgxh(record.bomId)}}</span>
            </template>
            <template #quantity="{ record }">{{null == record.quantity?'':parseFloat(record.quantity).toFixed(2)}}</template>
            <template #chengpingLv="{ record }">{{null == record.chengpingLv?'':parseFloat(record.chengpingLv).toFixed(2)}}</template>
          </BasicTable>
          <div class="pagination-text"  v-show="showPaginationText">
            {{`共 ${tableData?.filter(a=>a.id!=null)?.length}条记录&emsp;每页 200 条`}}
          </div>
        </div>
        <div style="max-height: 370px;margin-top: 10px">
          <BasicTable ref="tableRef" @register="registerTable2" :data-source="tableData2"
                      :loading="loadingMsg2"
                      :row-selection="null" :scroll="{ x: totalColumnWidth,y: windowHeight-400 }"
                      :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'">
            <template #unitId="{ record }">{{ formatCunitid(record.cinvode,record.unitId) }}</template>
            <template #cwhcode="{ record }">{{ formatCwhcode(record.cwhcode) }}</template>
            <template #isum="{ record }">{{null == record.isum?'':parseFloat(record.isum).toFixed(2)}}</template>
            <template #sunhaoLv="{ record }">{{null == record.sunhaoLv?'':parseFloat(record.sunhaoLv).toFixed(2)}}</template>
          </BasicTable>
          <!--          :style="{top: (windowHeight+45)+'px'}"-->
          <div class="pagination-text"  v-show="showPaginationText">
            {{`共 ${tableData2?.filter(a=>a.id!=null)?.length}条记录&emsp;每页 200 条`}}
          </div>
        </div>
      </PageWrapper>
    </div>
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
  Tag ,
} from 'ant-design-vue';
import {nextTick, reactive, ref} from 'vue';
import {getCurrentAccountName, hasBlank} from "/@/api/task-api/tast-bus-api";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker-DATA.vue";
import {useTabs} from "/@/hooks/web/useTabs";
import {useMessage} from "/@/hooks/web/useMessage";
import {PageWrapper} from '/@/components/Page';
import {useUserStoreWidthOut} from '/@/store/modules/user';
import {assemblyDynamicColumn, initDynamics} from "./data";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {delAll, editStockFlag, findAll, verifyStockIsData} from "/@/api/record/stock/stock";
import Add from "/@/views/boozsoft/stock/stock_info/popup/save.vue";
import Edit from "/@/views/boozsoft/stock/stock_info/popup/edit.vue";
import ImprotExcel from "/@/views/boozsoft/stock/stock_info/popup/improtExcel.vue";
import StockClassAdd from '/@/views/boozsoft/stock/stock_class/popup/save.vue';
import StockInfoModalPop from '/@/views/boozsoft/stock/stock_info/popup/stockInfoModalPop.vue';
import BatchEdit from '/@/views/boozsoft/stock/stock_info/popup/batchEdit.vue';
import {useModal} from "/@/components/Modal";
import {aoaToSheetXlsx} from "/@/components/Excel";
import DynamicColumn from "/@/views/boozsoft/stock/stock_sales_add/component/DynamicColumn.vue";
import {bomDel, findAllPage, findEntryList} from "/@/api/record/stock/stock-bom";
import {findStockCaiGouList} from "/@/api/record/stock/stock-caigou";
import {findAll as findAllJiLang, findUnitInfoList} from "/@/api/record/system/unit-mea";
import {JsonTool} from "/@/api/task-api/tools/universal-tools";
// 全局常量

const {createConfirm, createWarningModal, createMessage} = useMessage();
const ARadioGroup = ARadio.Group
const ARadioButton = ARadio.Button
const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const AStatisticCountdown = AStatistic.Countdown;
const ATabPane = ATabs.TabPane;
const baseName = ref('');
const database = ref(getCurrentAccountName(true));
const databaseCo = ref('');
const accId = ref('');
const iyear = ref('');
const loadingMsg = ref(false);
const loadingMsg2 = ref(false);
const tableData = ref([])
const tableData2 = ref([])
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
      dataIndex: 'bcheck',
      ellipsis: true,
     slots: { customRender: 'bcheck' },
    },
    {
      title: '父件编码',
      dataIndex: 'bomId',
      ellipsis: true,
      slots: { customRender: 'bomId' },
    },
    {
      title: '父件名称',
      dataIndex: 'bomName',
      ellipsis: true,
     slots: { customRender: 'bomName' },
    },
    {
      title: '规格型号',
      dataIndex: 'stockGgxh',
      ellipsis: true, slots: { customRender: 'stockGgxh' },

    },
    {
      title: '版本号',
      dataIndex: 'bomVersion',
      ellipsis: true,

    },
    {
      title: '计量单位',
      dataIndex: 'unitId',
      ellipsis: true,
      slots: { customRender: 'unitId' },
    },
    {
      title: '数量',
      dataIndex: 'quantity',
      ellipsis: true,
      slots: { customRender: 'quantity' },
    },
    {
      title: '成品率',
      dataIndex: 'chengpingLv',
      ellipsis: true,
      slots: { customRender: 'chengpingLv' },
    },
    {
      title: '默认BOM',
      dataIndex: 'stockPropertyPurchase',
      ellipsis: true,
      slots: { customRender: 'stockPropertyPurchase' },
    },{
      title: '是否停用',
      dataIndex: 'bcloser',
      ellipsis: true,
      slots: { customRender: 'bcloser' },
    },
    {
      title: '制单人',
      dataIndex: 'cmaker',
      ellipsis: true,
      slots: { customRender: 'cmaker' },
    }, {
      title: '审核日期',
      dataIndex: 'bcheckTime',
      ellipsis: true,
      slots: { customRender: 'bcheckTime' },
    }
  ],
  columns2: [
    {
      title: '子件编码',
      dataIndex: 'cinvode',
      ellipsis: true,
     slots: { customRender: 'cinvode' },
    },
    {
      title: '子件名称',
      dataIndex: 'cinvName',
      ellipsis: true,
     slots: { customRender: 'cinvName' },
    },
    {
      title: '规格型号',
      dataIndex: 'guigexh',
      ellipsis: true,
    },
    {
      title: '子件BOM',
      dataIndex: 'bomVersion',
      ellipsis: true,
    },
    {
      title: '默认仓库',
      dataIndex: 'cwhcode',
      ellipsis: true,
      slots: { customRender: 'cwhcode' },
    },

    {
      title: '计量单位',
      dataIndex: 'unitId',
      ellipsis: true,
      slots: { customRender: 'unitId' },
    },
    {
      title: '数量',
      dataIndex: 'isum',
      ellipsis: true,
      slots: { customRender: 'isum' },
    },
    {
      title: '损耗率',
      dataIndex: 'sunhaoLv',
      ellipsis: true,
      slots: { customRender: 'sunhaoLv' },
    }
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
  }else {
    findMingXi(data.bomUniqueId)
  }
}
const { closeCurrent,closeToFullPaths } =useTabs();
const [registerSavePage, { openModal: openSavePage }] = useModal();
const [registerEditPage, { openModal: openEditPage }] = useModal();
const [registerImprotExcelPage, { openModal: openImprotExcelPage }] = useModal();
const [registerStockClassSavePage, { openModal: openStockClassSavePage }] = useModal();
const [registerStockInfoPage, { openModal: openStockInfoPage }] = useModal();
const [registerBatchEditPage, { openModal: openBatchEditPage }] = useModal();

// 这是示例组件
const [registerTable, { reload,getSelectRows,getDataSource,setTableData,setColumns,clearSelectedRowKeys,getColumns,deleteSelectRowByKey }] = useTable({
  columns: CrudApi.columns,
  loadingMsg: loadingMsg.value,
  indexColumnProps:{ fixed:true },
  pagination: {
    pageSize: 200,
    simple:true,
},
});

const [registerTable2, {  }] = useTable({
  columns: CrudApi.columns2,
  loadingMsg: loadingMsg2.value,
  indexColumnProps:{ fixed:true },
  pagination: {
    pageSize: 200,
    simple:true,
  },
});


const dynamicAdReload = async (obj) => {
  database.value=obj.accountMode
  databaseCo.value=obj.coCode
  await initData()
  loadingMsg.value=true
  accId.value=obj.accId
  iyear.value=obj.year
  baseName.value=obj.baseName
  visible.value = true
  setTimeout(()=>
      visible.value = false
    ,100)
  lanMuData.value.changeNumber+=1
  await findAllStock()
}
const jiList = ref([])
const manyJiList = ref([])

const selectModel = ref({})
const initData = async () => {
  let q = [ 'supplier', 'operator', 'warehouse', 'stock']
  selectModel.value = (await useRouteApi(findStockCaiGouList, {schemaName: database})([...new Set(q)].join(',')))
  jiList.value = await useRouteApi(findAllJiLang, {schemaName: database})({unitName: ''})
  manyJiList.value = await useRouteApi(findUnitInfoList, {schemaName: database})({})
}

function formatCwhcode(cwhcode){
  let str = cwhcode
  if (!hasBlank(cwhcode))selectModel.value['warehouse']?.forEach(item=>{
    if(item.id == cwhcode){
      str = item.ckName
    }
  })
  return str
}

  function formatCunitid(cinvode,k){
  let str = k
    if (!hasBlank(k)) selectModel.value['stock']?.forEach(item=> {
    if (item.stockNum == cinvode) {
      if (item['stockMeasurementType'] == '单计量') {
        jiList.value.forEach(
          function (psn: any) {
            if (psn.id == item['stockMeasurementUnit'])
              str = psn.unitName
          }
        )
      } else {
        let res = manyJiList.value.filter(it1 => it1.id == item['stockMeasurementUnit'])[0]
        if (res != null) {
          let list = JsonTool.parseObj(res.detail) || []
          if (null == k && list.length > 0){
            str = list[0].unitName
          }else{
            str  = list.filter(it=>it.id == k)[0]?.unitName
          }
        }
      }
    }
  })
  return str
}

function formatGgxh(cinvode){
  let str = ''
  if (!hasBlank(cinvode))selectModel.value['stock']?.forEach(item=>{
    if (item.stockNum == cinvode){
      str = item.stockGgxh
    }
  })
  return str
}

const showPaginationText=ref(false)
async function findAllStock() {
  loadingMsg.value=true
  tableData.value=[]
  let res =(await useRouteApi(findAllPage,{schemaName: database})(pageParameter))
  let temp = res['items'] || []
  showPaginationText.value=false
  if(temp.length<30) for (let i =  temp.length; i < 30; i++) temp.push({})
  setTimeout(()=>{
    tableData.value=temp
    showPaginationText.value=true
    loadingMsg.value=false
  },200)
}
async function findMingXi(id) {
  loadingMsg2.value=true
  tableData2.value=[]
  let temp =(await useRouteApi(findEntryList,{schemaName: database})({uniqueId: id}))
  if(temp.length<10) for (let i =  temp.length; i < 10; i++) temp.push({})
  setTimeout(()=>{
    tableData2.value=temp
    loadingMsg2.value=false
  },200)
}



function delFun() {
  if(getSelectRows().length!=1){
    return createWarningModal({content: '请选择一项进行删除！'});
  }
  createConfirm({
    iconType: 'warning',
    title: '警告',
    content: '数据不可恢复,确定删除吗?',
    onOk: async () => {
      await useRouteApi(bomDel, {schemaName: database})({id: getSelectRows()[0].id})
      pageReload()
    },
  });
}

async function pageReload() {
  clearSelectedRowKeys()
  findAllStock()
}
async function pageReload2() {
  clearSelectedRowKeys()
  findAllStock()
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
  menuName: 'BOM物料清单列表',
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

const startOpenTags =async (type) => {
    if(type=='edit' && getSelectRows().length!=1){
      return createWarningModal({content: '选择一项进行修改！'});
    }
    await closeToFullPaths('/seven-materiel/stock_bom')
    setTimeout(()=>{
      router.push({path: 'stock_bom',query: {type:type,code:getSelectRows()[0]?.bomSysId,co: databaseCo.value}})
    },1000)
  }
</script>
<style lang="less" scoped>
@import '/@/assets/styles/global-menu-index1.less';
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
  border-left: 1px solid #c9c9c9 !important;
  border-left: 1px solid #c9c9c9 !important;
}
.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 13px !important;
  padding: 2px 8px !important;
  border-color: #aaaaaa !important;
  height: 25px;
  color: black;
  border-left: 1px solid #c9c9c9 !important;
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
:deep(.vben-page-wrapper-content){
  display: inline-block;
}

:deep(.ant-table-measure-row){
  td{
    padding: 0 !important;
  }
}
</style>
