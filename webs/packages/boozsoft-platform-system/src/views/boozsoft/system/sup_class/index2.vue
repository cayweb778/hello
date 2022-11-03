<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <div class="container-head-title" style="float: left;margin-top: 10px;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 35%;text-align: center;margin-top: 30px;">
          <b class="noneSpan" style="font-size: 36px;">供应商分类</b>
        </div>
        <div class="ant-btn-group" style="float: right;">
          <span v-if="independent=='1'">
            <button
              type="button"
              class="ant-btn ant-btn-me"
              ant-click-animating-without-extra-node="false"
              @click="openAddPage()"
            ><span>新增</span></button>
            <button type="button" class="ant-btn ant-btn-me" @click="openEdit()"><span>修改</span></button>
            <button type="button" class="ant-btn ant-btn-me" @click="del()"><span>删除</span></button>
            <button type="button" class="ant-btn ant-btn-me" @click="openImportExcel()"><span>导入</span></button>
          </span>
          <span v-else>
            <button type="button" class="ant-btn ant-btn-me" @click="del()"><span>删除</span></button>
            <button type="button" class="ant-btn ant-btn-me" @click="bringGroupDataBtn()"><span>引入</span></button>
          </span>
          <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false"><span>打印</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent(),router.push('/one/home/welcome')"><span>退出</span></button>
        </div>
      </div>
      <div style="clear: none"/>
      <div style="margin-top: -30px;margin-left: 80px;">
        <div style="display: inline-block;float: left;margin-top: -25px;">
          <AccountPicker theme="three" @reloadTable="dynamicAdReload"/>
        </div>
<!--        <div style="display: inline-block;position:absolute;top:85px;left:110px;font-size: 10px;color:#999999;">共 {{ tableTotal }} 条记录</div>-->
        <div style="float: right; margin-left: 10px;">
          <a-button @click="pageReload">
            <SyncOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
        </div>
        <div style="float: right; position: relative">
          <a-input-search placeholder="" style="width: 200px; border-radius: 4px" v-model:value="pageParameter.searchValue" @search="pageReload"/>
        </div>
      </div>
      <div style="clear:both"/>
    </div>
    <div class="app-container">
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <div class="bg-white m-4 mr-0 overflow-hidden" style="margin-top: -0.5px;margin-left: -5px;">
          <div style="width: 100%; height: 32px;background-color: #d9d9d9;padding-top:5px;font-weight: bold;text-align: center;">
            供应商分类
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
          <BasicTable @fetch-success="fetchSuccess" @register="registerTable" :scroll="{ y: windowHeight }" :class="'a-table-font-size-12'" :bordered="true" v-if="tableshow" @row-click="rowClick">
            <template #cusClass="{ record }">
              {{ record.cusClass }}
            </template>
            <template #cusCclassName="{ record }">
              {{ addspace(record.cusClassGrade, record.cusCclassName) }}
            </template>
          </BasicTable>
          <div class="pagination-text" :style="{top: (windowHeight+45)+'px'}" v-show="showPaginationText">
            {{`共 ${tableTotal}条记录&emsp;每页 200 条`}}
          </div>
        </div>
      </PageWrapper>
    </div>

    <EditPage @save="saveData" @register="registerEditPage" />
    <AddPage @save="saveData" @register="registerSavePage" />
    <ImprotExcel @save="pageReload" @register="registerExcelPage" />
    <BringGroupModal @throwData="bringOrgDataFunction" @register="registerBringOrgModalPage" />
  </div>
</template>
<script setup lang="ts">
  import router from "/@/router";
  import {
    customerSaveApi, customerSaveCusClass,
    delCustomerClass,
    findAllCustomerClass, GetCustomerClassTree,
  } from '/@/api/record/system/supplier_class';
import {BasicTable, useTable} from '/@/components/Table';
import EditPage from './popup/edit.vue';
import AddPage from './popup/save.vue';
import ImprotExcel from './popup/improtExcel.vue';
import BringGroupModal from './popup/bringGroupModal.vue';
import {useModal} from '/@/components/Modal';
import {SyncOutlined,UnorderedListOutlined,ProfileOutlined} from '@ant-design/icons-vue';
import {
  Input as AInput,
  Select as ASelect,
  Statistic as AStatistic,
  Tabs as ATabs,
} from 'ant-design-vue';
import {onMounted, reactive, ref} from 'vue';
import {useUserStoreWidthOut} from '/@/store/modules/user';
  import {getCurrentAccountName, hasBlank} from "/@/api/task-api/tast-bus-api";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
  import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker-DATA.vue";
  import {findByAccId} from "/@/api/record/system/account";
import {useTabs} from "/@/hooks/web/useTabs";
  import {useMessage} from "/@/hooks/web/useMessage";
  import { BasicTree } from '/@/components/Tree';
  import { PageWrapper } from '/@/components/Page';
  import {saveLog} from "/@/api/record/system/group-sys-login-log";
  import {editFlagByCtypeAndTenantId} from "/@/api/record/system/supplier_class_group";
  import {findByStockAccountAccId} from "/@/api/record/system/stock-account";
  import {editUniqueCustclass} from "/@/api/record/supplier_data/supplier";
  const windowHeight = (window.innerHeight - (300))
  const accountInfo = ref({});
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
      dataIndex: 'cusClass',
      ellipsis: true,
      align: 'left',
      slots: { customRender: 'cusClass' },
    },
    {
      title: '分类名称',
      dataIndex: 'cusCclassName',
      ellipsis: true,
      align: 'left',
      slots: { customRender: 'cusCclassName' },
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
    searchValue: '',
    superid: ''
  })
  const tableTotal = ref(0)

  const rowSelection = {
    getCheckboxProps: (record) => ({
      disabled: record.cusClass === '9999'|| record.cusClass==undefined
    }),
  };
  // 这是示例组件
  const [registerTable, { getPaginationRef,reload,getSelectRows,getDataSource,setTableData,clearSelectedRowKeys,deleteSelectRowByKey }] = useTable({
    api: useRouteApi(findAllCustomerClass,{schemaName: database}),
    columns: columns,
    loading: loadMark.value,
    rowSelection: { type: 'checkbox' ,getCheckboxProps:rowSelection.getCheckboxProps},
    showIndexColumn: true,
    indexColumnProps: {fixed: true},
    pagination: {
      pageSize: 200,
      simple: true
    },
    searchInfo: pageParameter
  });

  const [registerEditPage, { openModal: openEditPage }] = useModal();
  const [registerSavePage, { openModal: openSavePage }] = useModal();
  const [registerExcelPage, { openModal: openExcelPage }] = useModal();
  const [registerBringOrgModalPage, { openModal: openBringOrgModalPage }] = useModal();

  const val = {
    parentId: '',
    cusCclassName: '',
    cusClass: '',
  };
  const showPaginationText:any = ref(false)
  function fetchSuccess(data) {
    showPaginationText.value=false
    tableTotal.value = data.total
    showPaginationText.value=true
    if (data.items.length < 50) {
      for (let i = data.items.length; i < 50; i++) {
        data.items.push({cusCclassName: ''})
      }
    }
    setTableData(data.items)
  }
  function rowClick(a) {
    if(a.cusClass=='9999'|| hasBlank(a.cusCclassName)){
      deleteSelectRowByKey(a.key)
    }
  }

  // 引入
  function bringGroupDataBtn() {
    openBringOrgModalPage(true, {
      tenantId:accId.value,
      list:getDataSource()
    });
  }

  // 引入回调
  async function bringOrgDataFunction(data) {
    data.forEach(a=>{
      a.id=null
      a.flag='1'
      a.tenantId=database.value
    })
    await useRouteApi(customerSaveCusClass,{schemaName: database})(data).then(async ()=>{
      // 埋点-操作日志
      for (let i = 0; i < data.length; i++) {
        let log='操作内容【账套引入供应商分类】,账套代码【'+accountInfo.value.coCode+'】,账套名称【'+accountInfo.value.companyName+'】,分类编码【'+data[i].cusClass+'】,分类名称【'+data[i].cusCclassName+'】'
        /************** 记录操作日志 ****************/
        let logmap={
          loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
          userId: useUserStoreWidthOut().getUserInfo.username,
          userName: useUserStoreWidthOut().getUserInfo.realName,
          optModule:'master_data',
          optFunction:'供应商分类',
          optRange:'1',
          uniqueCode:database.value,
          optAction:'引入',
          optContent:log,
        }
        await saveLog(logmap)
        /************** 记录操作日志 ****************/
      }
      pageReload()
    })
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
      isEdit:false,
      accountInfo:accountInfo.value
    });
  };

  const openEdit = () => {
    if(getSelectRows().length==0||getSelectRows().length>1){
      return createWarningModal({ content: '请选择一条数据进行编辑！' });
    }
    openEditPage(true, {
      data: getSelectRows()[0],
      database:database.value,
      isEdit:false,
      accountInfo:accountInfo.value
    });
  };

  const del = async () => {
    if(getSelectRows().length==0){
      return createWarningModal({ content: '至少选择一条数据进行删除！' });
    }
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据不能恢复，你确认要删除吗？',
      onOk: async () => {
        await useRouteApi(delCustomerClass,{schemaName: database})(getSelectRows()).then(async (data)=>{
          // 不是末级的
          let listStr=data['listStr']
          // 分类已使用的。优先判断
          let useCus=data['useCus']
          if(useCus.length>0){
            createConfirm({
              iconType: 'warning',
              title: '警告',
              content: useCus+'已使用,不能删除!!',
              onOk: async () => {
                pageReload()
              },
            });
            return
          }
          if(listStr.length>0){
            createConfirm({
              iconType: 'warning',
              title: '警告',
              content: listStr+'不是末级分类,不能删除!!',
              onOk: async () => {
                pageReload()
              },
            });
          }
          getSelectRows().forEach(async (a)=>{
            let log='操作内容【账套删除供应商分类】,账套代码【'+accountInfo.value.coCode+'】,账套名称【'+accountInfo.value.companyName+'】,分类编码【'+a.cusClass+'】,分类名称【'+a.cusCclassName+'】'
            /************** 记录操作日志 ****************/
            let logmap={
              loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
              userId: useUserStoreWidthOut().getUserInfo.username,
              userName: useUserStoreWidthOut().getUserInfo.realName,
              optModule:'master_data',
              optFunction:'供应商分类',
              optRange:'1',
              uniqueCode:database.value,
              optAction:'删除',
              optContent:log,
            }
            await saveLog(logmap)
            /************** 记录操作日志 ****************/

            let uniques=[...new Set(getSelectRows().map(a=>a.uniqueCustclass))]
            let map={
              flag:'0',
              ctype:'2',
              tenantId:accId.value,
              list:uniques,
            }
            // 批量修改集团引入状态
            await editFlagByCtypeAndTenantId(map)
            clearSelectedRowKeys()
            pageReload()
          })
        })
      }
    })
  };

  async function saveData(data: any) {
    loadMark.value=true
    await useRouteApi(customerSaveApi,{schemaName: database})(data)
    .then(async (a)=>{
      fetch()
      reload();
      loadMark.value=false
      await useRouteApi(editUniqueCustclass,{schemaName: database})({uniqueCustclass:a.uniqueCustclass,oldUniqueCustclass:a.parentId})
    })
  }

  function openImportExcel() {
    openExcelPage(true, {
      database: database.value,
      accountInfo:accountInfo.value
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
    accountInfo.value=obj
    databaseName.value=obj.baseName
    accId.value=obj.accId
    database.value=obj.accountMode

    // 查询是否独立账套  是否独立账套（1）反之集团账套（0）
    let datainfo = await findByAccId(obj.accId);
    datainfo=hasBlank(datainfo)?await findByStockAccountAccId(obj.accId):datainfo
    independent.value=datainfo.independent==undefined?"1":datainfo.independent
    fetch()
    setTimeout(()=>{
      reload()
    },500)
    loadMark.value=false
  }
  async function fetch() {
    const deptTree = await useRouteApi(GetCustomerClassTree,{schemaName: database})('')
    function a(customerTree) {
      customerTree.forEach((item) => {
        if (item.children != null) {
          a(item.children);
        }
        item.title = '[' + item.cusClass + '] ' + item.cusCclassName;
        item.key = item.uniqueCustclass;
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
</script>
<style src="./global-menu-index.less" lang="less" scoped></style>
<style lang="less" scoped>
a {
  color: #0096c7;
  text-decoration: none;
  cursor: pointer;
}
.bg-white{
  width: 250px;
  height: calc(100% - 230px);
  border: 1px #cccccc solid;
  background: white;
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
  .pagination-text{
    position: absolute;
    bottom: 9px;
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
