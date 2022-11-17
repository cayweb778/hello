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
        <div>  <b class="noneSpan" style="font-size: 36px;color: #0096c7;">存货分类</b></div>
      </div>
      <div>
        <div>
          <button type="button" class="ant-btn ant-btn-me" @click="addFun"><span>新增</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="openEdit" ><span>修改</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="del" ><span>删除</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="openImportExcel"><span>导入</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="exportExcel123()"><span>导出</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="editFlag"><span>启\停</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent(),router.push('/one/home/welcome')"><span>退出</span></button>

        </div>
        <div>
          <div>
            <a-button class="ant-btn-me">
              <SyncOutlined :style="{ fontSize: '14px' }" @click="pageReload"/>
            </a-button>
            <a-popover  placement="bottom">
                        <template #content>
                           <span class="group-btn-span-special2" @click="pageParameter.stockFlag='',reload()" :style="pageParameter.stockFlag==''?{backgroundColor: '#0096c7',color: 'white'}:''">
                            <ReadOutlined/>&nbsp;&nbsp;全部&emsp;&ensp;<CheckOutlined  v-if="pageParameter.stockFlag==''"/></span><br/>
                          <span class="group-btn-span-special2" @click="pageParameter.stockFlag='1',reload()" :style="pageParameter.stockFlag=='1'?{backgroundColor: '#0096c7',color: 'white'}:''">
                            <SafetyOutlined/>&nbsp;&nbsp;启用&emsp;&ensp;<CheckOutlined  v-if="pageParameter.stockFlag=='1'"/></span><br/>
                          <span class="group-btn-span-special2" @click="pageParameter.stockFlag='0',reload()" :style="pageParameter.stockFlag=='0'?{backgroundColor: '#0096c7',color: 'white'}:''">
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
        <div class="bg-white m-4 mr-0 overflow-hidden" style="margin-top: -0.5px;margin-left: -5px;">
          <div style="width: 100%; height: 50px">
            <label style="font-size: 20px; line-height: 50px; margin-left: 20px">存货分类</label>
          </div>
          <BasicTree
            defaultExpandAll
            :click-row-to-expand="false"
            :tree-data="treeData"
            v-if="treeData.length"
            v-model:selectedKeys="selectedKeys2"
            @select="handleSelect"
          />
        </div>
        <div style="width: calc(100% - 250px); float: right;margin-left: 5px;">
          <BasicTable @register="registerTable" @row-click="rowDbClick" :scroll="{ y: windowHeight }" :bordered="true"
                      :class="'a-table-font-size-12'"
                      :rowKey="r=>r.stockGradeCode"
                      @fetch-success="fetchSuccess"
                      :row-selection="{ type: 'checkbox',selections:false,selectedRowKeys: tableSelectedRowKeys,onSelect: onSelectChange,onSelectAll: onSelectChange2 }"
                      v-if="tableshow">
            <template #stockClass="{ record }">
              {{ record.stockClass }}
            </template>
            <template #stockCclassName="{ record }">
              <span v-if="record.ckFlag!=='11'">
              {{ addspace(record.stockClassGrade, record.stockCclassName) }}
              </span>
            </template>
          </BasicTable>
          <div class="pagination-text" :style="{top: (windowHeight+45)+'px'}" v-show="showPaginationText">
            {{`共 ${tableTotal}条记录&emsp;每页 200 条`}}
          </div>
        </div>
      </PageWrapper>
    </div>
    <AddPage @save="saveData" @register="registerSavePage"/>
    <EditPage @save="saveData" @register="registerEditPage"/>
    <ImprotExcel @save="pageReload" @register="registerExcelPage"/>
    <ClassAllModalPop @throwData="classAllThrowData" @register="registerClassAllPage"/>
  </div>
</template>
<script setup lang="ts">
import router from "/@/router";
import {
  delStockClass, editStockCalssFlag,
  findAllStockClass,
  findByUniqueStockclass,
  countByUniqueStockclass,
  GetStockClassTree,
  stockSaveApi, delAll
} from '/@/api/record/stock/stock_class';
import {BasicTable, useTable} from '/@/components/Table';
import EditPage from './popup/edit.vue';
import AddPage from './popup/save.vue';
import ImprotExcel from './popup/improtExcel.vue';
import ClassAllModalPop from './popup/classAllModalPop.vue';
import {useModal} from '/@/components/Modal';
import {CheckOutlined,
  EditOutlined,
  PicLeftOutlined,
  PlusCircleTwoTone,
  ReadOutlined,
  RestOutlined,
  SafetyOutlined,
  SettingFilled,
  SyncOutlined,
  UnorderedListOutlined,ProfileOutlined} from '@ant-design/icons-vue';
import {
  Table as ATable,
  Popconfirm as APopconfirm,
  Popover as APopover,
  Checkbox as ACheckbox,
  Input as AInput,
  message,
  Select as ASelect,
  Statistic as AStatistic,
  Tabs as ATabs,
} from 'ant-design-vue';
import {reactive, ref} from 'vue';
import {useUserStoreWidthOut} from '/@/store/modules/user';
import {getCurrentAccountName, hasBlank} from "/@/api/task-api/tast-bus-api";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker-DATA.vue";
import {findByAccId, findDataBase} from "/@/api/record/system/account";
import {useTabs} from "/@/hooks/web/useTabs";
import {useMessage} from "/@/hooks/web/useMessage";
import {BasicTree} from '/@/components/Tree';
import {PageWrapper} from '/@/components/Page';
import {aoaToSheetXlsx} from "/@/components/Excel";
import {countByStockClass, findAllByStockClass, saveList} from "/@/api/record/stock/stock";

const tableSelectedRowKeys:any = ref([])
const tableSelectedRowObjs:any = ref([])
const windowHeight = (window.innerHeight - (310))
  const databaseName = ref('');
  const treeData:any = ref([]);
  const selectedKeys2:any = ref<string[]>([])
  const {createConfirm, createWarningModal, createMessage} = useMessage();
  const ASelectOption = ASelect.Option;
  const AInputSearch = AInput.Search;
  const AStatisticCountdown = AStatistic.Countdown;
  const ATabPane = ATabs.TabPane;
  const username = ref(useUserStoreWidthOut().getUserInfo.username);
  const usernum = ref(useUserStoreWidthOut().getUserInfo.username);
  const database = ref(getCurrentAccountName(true));
  const accId = ref('');
  const tableshow = ref(false);
  const tableTotal = ref(0);

  const loadMark = ref(true);
  const columns:any = [
    {
      title: 'ID',
      dataIndex: 'id',
      defaultHidden: true,
      ellipsis: true,
    },
    {
      title: '分类编码',
      dataIndex: 'stockClass',
      ellipsis: true,
      align: 'left',
      slots: { customRender: 'stockClass' },
    },
    {
      title: '分类名称',
      dataIndex: 'stockCclassName',
      ellipsis: true,
      align: 'left',
      slots: { customRender: 'stockCclassName' },
    },
    {
      title: '上级名称',
      dataIndex: 'superClassName',
      ellipsis: true,
    }
  ];
  const independent = ref('1') // 1独立账套  0集团账套
  const searchValue = ref('')
  const dataBasePeriodList = ref([]) // 会计期间
  const { closeCurrent } =useTabs();
  const pageParameter = reactive({
    stockFlag: '',
    searchValue: '',
    superid: ''
  })
  const rowSelection = {
    getCheckboxProps: (record) => ({
      disabled: record.stockClass === '9999'
    }),
  };
  // 这是示例组件
  const [registerTable, { deleteSelectRowByKey,reload,getSelectRows,getDataSource,setTableData,getPaginationRef }] = useTable({
    api: useRouteApi(findAllStockClass,{schemaName: database}),
    columns: columns,
    loading: loadMark.value,
    pagination: {
      pageSize: 200,
      simple:true
    },
    searchInfo: pageParameter
  });

  const [registerEditPage, { openModal: openEditPage }] = useModal();
  const [registerSavePage, { openModal: openSavePage }] = useModal();
  const [registerExcelPage, { openModal: openExcelPage }] = useModal();
  const [registerClassAllPage, { openModal: openClassAllPage }] = useModal();
  const val = {
    parentId: '',
    stockCclassName: '',
    stockClass: '',
  };

const showPaginationText:any = ref(false)
function fetchSuccess(data) {
  showPaginationText.value=false
  tableTotal.value = data.total
  showPaginationText.value=true
  if (data.items.length < 50) {
    for (let i = data.items.length; i < 50; i++) {
      data.items.push({ckFlag: '11'})
    }
  }
  setTableData(data.items)
}
function rowDbClick(data) {
  if(data.stockClass=='9999'){
    deleteSelectRowByKey(data.key)
  }
}
  async function editFlag() {
    if(tableSelectedRowObjs.value.length==0){
      return message.error('至少选择一条数据进行修改');
    }
    await useRouteApi(editStockCalssFlag,{schemaName: database})(tableSelectedRowObjs.value);
  }

// 分类弹框回调
  function classAllThrowData(data) {
    console.log(data)
  }
  // 测试全部弹框
  function openClassAllModal() {
    openClassAllPage(true, {
      database:database.value,
    });
  }


function handleSelect(obj) {
  if(obj.toString()!==''){
    pageParameter.superid=obj.toString()
    reload()
  }else{
    selectedKeys2.value='0'
    pageParameter.superid=''
    reload()
  }
}

  const openAddPage = (data: any) => {
    data !== undefined ? (val.parentId = data.parentId) : '';
    openSavePage(true, {
      data: val,
      database:database.value,
      isEdit:false
    });
  };

  const openEdit = () => {
    if(tableSelectedRowObjs.value.length>1){
      return message.error('请选择一条数据进行编辑');
    }
    openEditPage(true, {
      data: tableSelectedRowObjs.value[0],
      database:database.value,
      isEdit:false
    });
  };

  function addFun() {
    openSavePage(true, {
      data: val,
      database:database.value,
      isEdit:false
    });
  }

  const del = async () => {
    if(tableSelectedRowObjs.value.length==0){
      return message.error('至少选择一条数据进行删除');
    }
    for (let i = 0; i < tableSelectedRowObjs.value.length; i++) {
      let stock= await useRouteApi(countByStockClass,{schemaName: database})(tableSelectedRowObjs.value[i].uniqueStockclass)
      if(stock>0){
        return message.error('【'+tableSelectedRowObjs.value[i].stockCclassName+'】分类下已存在存货信息，不能进行删除');
      }
    }
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据不能恢复，你确认要删除吗？',
      onOk: async() => {
        for (let i = 0; i < tableSelectedRowObjs.value.length; i++) {
          await useRouteApi(delStockClass,{schemaName: database})(tableSelectedRowObjs.value[i].id);
        }
        tableSelectedRowKeys.value=[]
        tableSelectedRowObjs.value=[]
        fetch()
        reload();
      }
    })
  };

  async function saveData(data: any) {
    await useRouteApi(stockSaveApi,{schemaName: database})({params:data})
    .then(async (a)=>{
      await useRouteApi(findAllByStockClass,{schemaName: database})(a.parentId)
      .then(async (list)=>{
        list.forEach(b=>{
          b.stockClass=a.uniqueStockclass
        })
        await useRouteApi(saveList,{schemaName: database})(list)
      })
    })
    fetch()
    reload();
  }

  function openImportExcel() {
    openExcelPage(true, {
      database: database.value,
    });
  }

  function exportExcel123() {
    const data = JSON.parse(JSON.stringify(getDataSource()))
    let dataArr:any=[]
    data.forEach(v=>{
      let temp:any=[]
      temp[0]=v.stockClass,
      temp[1]=v.stockCclassName,
      temp[2]=v.superClassName,
      dataArr.push(temp)
      console.log(v)
    })
    aoaToSheetXlsx({
      data: dataArr,
      header: ['分类编码','分类名称','上级名称'],
      filename: databaseName.value+'-存货分类.xlsx',
    });
  }
const addspace = (igrade, str) => {
  var space = '';
  for (let i = 1; i < igrade; i++) {
    space += '\xa0\xa0\xa0\xa0';
  }
  return space + str;
}

  const dynamicAdReload = async (obj) => {
    loadMark.value=true
    treeData.value = []
    databaseName.value =obj.baseName
    await findDataBase(obj.accId,obj.year).then(async (item)=>{
      accId.value=item.accountId
      database.value =item.accountMode
      // 查询是否独立账套  是否独立账套（1）反之集团账套（0）
      const datainfo = await findByAccId(item.accountId);
      independent.value=datainfo.independent
      fetch()
      setTimeout(()=>{
        reload()
        tableTotal.value=getPaginationRef().total
      },500)
      loadMark.value=false
    })
  }
  async function fetch() {
    const deptTree = await useRouteApi(GetStockClassTree,{schemaName: database})('')
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
    selectedKeys2.value='0'
    tableshow.value=true
  }

  function pageReload() {
    fetch()
    reload()
  }


const onSelectChange = (record, selected, obj, nativeEvent) => {
  let selectedRowKeys = obj.map(it => it.stockGradeCode)
  if (selectedRowKeys.length > 0) {
    if (selectedRowKeys.length > tableSelectedRowKeys.value.length) { // 选中
      let list = getDataSource()
      let codes = obj.map(item => item.stockGradeCode);
      let b = [...new Set(codes)]
      for (let i = 0; i < b.length; i++) {
        let temp=list.filter(item => item.stockGradeCode.startsWith(b[i]))
        for (let j = 0; j < temp.length; j++) {
          tableSelectedRowObjs.value.push(temp[j])
        }
      }
    } else { // 取消勾选 先 找到被取消的
      let b = [...new Set(tableSelectedRowObjs.value.map(item => item.stockGradeCode))]
      tableSelectedRowObjs.value = tableSelectedRowObjs.value.filter(item => b.indexOf(item.stockGradeCode) == -1)
    }
    tableSelectedRowKeys.value = tableSelectedRowObjs.value.map(item => item.stockGradeCode)
  } else {
    let list = getDataSource()
    tableSelectedRowObjs.value = list.filter(it => selectedRowKeys.indexOf(it.stockGradeCode) != -1);
    tableSelectedRowKeys.value = selectedRowKeys;
  }
}
const onSelectChange2 = (selected, selectedRows, changeRows) => {
  if (selected) {
    tableSelectedRowObjs.value = selectedRows
    tableSelectedRowKeys.value = tableSelectedRowObjs.value.map(item => item.stockGradeCode)
  } else {
    tableSelectedRowObjs.value = []
    tableSelectedRowKeys.value = []
  }
}
</script>
<style src="./global-menu-index.less" lang="less" scoped></style>
<style lang="less" scoped>
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
.bg-white{
  width: 250px;
  height: calc(100% - 240px);
  border: 1px #cccccc solid;
  background:white ;
  margin-top: -0.5%;
  text-align: left;
}
.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
  border-color: #aaaaaa !important;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 13px !important;
  padding: 2px 8px !important;
  border-color: #aaaaaa !important;
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
  //background-color: #f2f2f2 !important;
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
