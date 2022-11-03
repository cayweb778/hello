<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <div class="container-head-title" style="float: left;margin-top: 10px;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 35%;text-align: center;margin-top: 30px;">
          <b class="noneSpan" style="font-size: 36px;">客户分类</b>
        </div>
        <div class="ant-btn-group" style="float: right;">
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="openAddPage()"
          ><span>新增</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="openEdit()"><span>修改</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="del()"><span>删除</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="assignClass()"><span>分配</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="openImportExcel()"><span>导入</span></button>
          <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false"><span>打印</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent(),router.push('/system/home/welcome')"><span>退出</span></button>
        </div>
      </div>
      <div style="clear: none"/>
<!--      <div style="margin-top: -30px;margin-left: 80px;">-->
<!--        <div style="display: inline-block;position:absolute;top:85px;left:110px;font-size: 10px;color:#999999;">共 {{tableTotal}} 条记录</div>-->
<!--      </div>-->
      <div style="float: right; margin-left: 10px;">
        <a-button @click="pageReload">
          <SyncOutlined :style="{ fontSize: '14px' }"/>
        </a-button>
      </div>
      <div style="clear:both"/>
    </div>
    <div class="app-container">
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <div class="bg-white m-4 mr-0 overflow-hidden" style="margin-top: -0.5px;margin-left: -5px;">
          <div style="width: 100%; height: 32px;background-color: #d9d9d9;padding-top:5px;font-weight: bold;text-align: center;">
            客户分类
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
          <BasicTable @fetch-success="fetchSuccess" @register="registerTable" :scroll="{ y: windowHeight }" :bordered="true" :class="'a-table-font-size-12'" v-if="tableshow">
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
    <GroupAssignModal @throwData="pageReload" @register="registerGroupAssignModalPage" />
  </div>
</template>
<script setup lang="ts">
  import router from "/@/router";
  import {
    delGroupCustomerClass,
    findAllCustomerClass, findByOrgAssigAccountCtype, GetCustomerClassTree, GroupCustomerSaveApi,
  } from '/@/api/record/system/customer_class_group';
  import { BasicTable, useTable } from '/@/components/Table';
  import EditPage from './popup/edit.vue';
  import AddPage from './popup/save.vue';
  import ImprotExcel from './popup/improtExcel.vue';
  import GroupAssignModal from './popup/groupAssignModal.vue';
  import { useModal } from '/@/components/Modal';
  import { SyncOutlined,UnorderedListOutlined,ProfileOutlined } from '@ant-design/icons-vue';
  import {
    Tabs as ATabs,
    Select as ASelect,
    Input as AInput,
    Statistic as AStatistic,
    message,
  } from 'ant-design-vue';
  import {onMounted, reactive, ref} from 'vue';
  import { useUserStoreWidthOut } from '/@/store/modules/user';
  import {findAllByUniqueCustclass} from "/@/api/record/system/customer_group";
import {
  findByAllSysAccPeriodGroupDataBase
} from "/@/api/record/system/account";
  import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
  import { customerSaveApi } from "/@/api/record/system/customer_class";
  import {useTabs} from "/@/hooks/web/useTabs";
  import {useMessage} from "/@/hooks/web/useMessage";
  import { BasicTree } from '/@/components/Tree';
  import { PageWrapper } from '/@/components/Page';
  import {saveLog} from "/@/api/record/system/group-sys-login-log";
  import {lighten} from "/@/utils/color";

  const windowHeight = (window.innerHeight - (300))
  const ASelectOption = ASelect.Option;
  const AInputSearch = AInput.Search;
  const AStatisticCountdown = AStatistic.Countdown;
  const ATabPane = ATabs.TabPane;
  const { closeCurrent } =useTabs();
  const treeData:any = ref([]);
  const selectedKeys2:any = ref<string[]>([])
  const tableshow = ref(false);
  const username = ref(useUserStoreWidthOut().getUserInfo.username);
  const usernum = ref(useUserStoreWidthOut().getUserInfo.username);
  const columns = [
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
  const searchValue = ref('')
  const tableTotal = ref(0)
  const pageParameter = reactive({
    searchValue: '',
    superid: ''
  })
  const rowSelection = {
    getCheckboxProps: (record) => ({
      disabled: record.cusClass === '9999' || record.cusClass==undefined
    }),
  };
  // 这是示例组件
  const [registerTable, { getPaginationRef,reload,getSelectRows,getDataSource,setTableData,clearSelectedRowKeys }] = useTable({
    api: findAllCustomerClass,
    columns: columns,
    rowSelection: { type: 'checkbox',getCheckboxProps:rowSelection.getCheckboxProps },
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
  const [registerGroupAssignModalPage, { openModal: openGroupAssignModalPage }] = useModal();

  const {createConfirm, createWarningModal, createMessage} = useMessage();

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
    });
  };

  const openEdit = () => {
    if(getSelectRows().length==0||getSelectRows().length>1){
      return createWarningModal({ content: '请选择一条数据进行编辑！' });
    }
    openEditPage(true, {
      data: getSelectRows()[0],
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
        await delGroupCustomerClass(getSelectRows()).then(async (data)=>{
          // 不是末级的
          let listStr=data['listStr']
          // 分类已使用的。
          let useCus=data['useCus']
          // 分类已分配的。优先判断
          let assignCusList=data['assignCusList']
          if(assignCusList!==undefined&&assignCusList.length>0){
            createConfirm({
              iconType: 'warning',
              title: '警告',
              content: assignCusList+'已分配,不能删除!!',
              onOk: async () => {
                pageReload()
              },
            });
            return
          }
          if(useCus!==undefined&&useCus.length>0){
            createConfirm({
              iconType: 'warning',
              title: '警告',
              content: useCus+'已使用,不能删除!!',
              onOk: async () => {
                pageReload()
              },
            });
          }
          if(listStr!==undefined&&listStr.length>0){
            createConfirm({
              iconType: 'warning',
              title: '警告',
              content: listStr+'不是末级科目,不能删除!!',
              onOk: async () => {
                pageReload()
              },
            });
          }
          getSelectRows().forEach(async (a)=>{
            let log='【删除客户分类】,分类编码【'+a.cusClass+'】,分类名称【'+a.cusCclassName+'】'
            /************** 记录操作日志 ****************/
            let logmap={
              loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
              userId: useUserStoreWidthOut().getUserInfo.username,
              userName: useUserStoreWidthOut().getUserInfo.realName,
              optModule:'group',
              optFunction:'客户分类',
              optRange:'2',
              optAction:'删除',
              optContent:log,
            }
            await saveLog(logmap)
            /************** 记录操作日志 ****************/
          })
          clearSelectedRowKeys()
          pageReload();
        })
      }
    })
  };
  async function saveData(data: any) {
    await GroupCustomerSaveApi(data, username.value);
    pageReload();
  }

  function openImportExcel() {
    openExcelPage(true, {
      data: ''
    });
  }
  const addspace = (igrade, str) => {
    var space = '';
    for (let i = 1; i < igrade; i++) {
      space += '\xa0\xa0\xa0\xa0';
    }
    return space + str;
  }

  onMounted(async () => {
   await fetch()
  })

  async function fetch() {
    const deptTree =await GetCustomerClassTree()
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

  // 分配
  function assignClass() {
    openGroupAssignModalPage(true, {

    });
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
