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
          <button type="button" class="ant-btn ant-btn-me" @click="del()"><span>删除</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="bringGroupDataBtn()"><span>引入</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="assignClass()"><span>分配</span></button>
          <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false"><span>打印</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent(),router.push('/system/home/welcome')"><span>退出</span></button>
        </div>
      </div>
      <div style="clear: none"/>
      <div style="margin-top: -30px;margin-left: 80px;">
        <div style="display: inline-block;float: left;margin-top: -25px;">
          &emsp;<label>组织：</label>
          <a-select
            v-model:value="orgUnique"
            option-filter-prop="children"
            show-search
            @change="orgCheck"
          >
            <a-select-option
              v-for="item in orientationlist"
              :key="item.uniqueCode"
              :value="item.uniqueCode"
            >
              {{ item.orgName }}
            </a-select-option>
          </a-select>
        </div>
<!--        <div style="display: inline-block;position:absolute;top:85px;left:110px;font-size: 10px;color:#999999;">共 {{tableTotal}} 条记录</div>-->
        <div style="float: right; margin-left: 10px;">
          <a-button @click="pageReload">
            <SyncOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
        </div>
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
          <BasicTable ref="tableRef" @fetch-success="fetchSuccess" @row-click="rowClick" @register="registerTable" :scroll="{ y: windowHeight }" :class="'a-table-font-size-12'" v-if="tableshow">
            <template #cusClass="{ record }">
              {{ record.cusClass }}
            </template>
            <template #cusCclassName="{ record }">
              <span v-if="record.cusCclassName!=undefined">
                {{ addspace(record.cusClassGrade, record.cusCclassName) }}
              </span>
            </template>
          </BasicTable>

          <div class="pagination-text" :style="{top: (windowHeight+45)+'px'}" v-show="showPaginationText">
            {{`共 ${tableTotal}条记录&emsp;每页 200 条`}}
          </div>
        </div>
      </PageWrapper>
    </div>
    <EditPage @save="saveData" @register="registerEditPage" />
    <ImprotExcel @save="pageReload" @register="registerExcelPage" />
    <BringGroupModal @throwData="bringGroupDataFunction" @register="registerBringGroupModalPage" />
    <OrgAssignModal @throwData="pageReload" @register="registerOrgAssignModalPage" />
  </div>
</template>
<script setup lang="ts">
  import router from "/@/router";
  import {
    delGroupCustomerClass,
    findAllCustomerClass, GetCustomerClassTree, GroupCustomerSaveApi,
  } from '/@/api/record/system/customer_class_group';
  import { BasicTable, useTable } from '/@/components/Table';
  import EditPage from './popup/edit.vue';
  import ImprotExcel from './popup/improtExcel.vue';
  import BringGroupModal from './popup/bringGroupModal.vue';
  import OrgAssignModal from './popup/orgAssignModal.vue';
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
  import {
    delOriginCustomerClassById,
    findAllOrgCusClass,
    saveAll,
    treeCustomerClass
  } from "/@/api/org/cus_class/org_cus_class";
  import {findByUniqueCode, getOrganizeAll, getOrganizeList} from "/@/api/record/group/im-organize";
  import {saveLog} from "/@/api/record/system/group-sys-login-log";
  import {hasBlank} from "/@/api/task-api/tast-bus-api";

  const tableTotal = ref(0)
  const orgUnique = ref('')
  const orientationlist = ref([])
  const tableRef = ref(null)
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
  const pageParameter = reactive({
    searchValue: '',
    superid: '',
    orgUnique: '',
  })

  const rowSelection = {
    getCheckboxProps: (record) => ({
      disabled: record.cusClass === '9999' || record.cusClass==undefined
    }),
  };

  // 这是示例组件
  const [registerTable, { getPaginationRef,reload,getSelectRows,getDataSource,setTableData,deleteSelectRowByKey }] = useTable({
    api: findAllOrgCusClass,
    columns: columns,
    rowSelection: { type: 'checkbox',getCheckboxProps:rowSelection.getCheckboxProps },
    bordered: true,
    showIndexColumn: true,
    indexColumnProps: {fixed: true},
    pagination: {
      pageSize: 200,
      simple:true
    },
    searchInfo: pageParameter
  });
  const [registerEditPage, { openModal: openEditPage }] = useModal();
  const [registerExcelPage, { openModal: openExcelPage }] = useModal();
  const [registerBringGroupModalPage, { openModal: openBringGroupModalPage }] = useModal();
  const {createConfirm, createWarningModal, createMessage} = useMessage();
  const [registerOrgAssignModalPage, { openModal: openOrgAssignModalPage }] = useModal();

  function rowClick(a) {
    if (a.cusClass == '9999' || hasBlank(a.cusCclassName)) {
      deleteSelectRowByKey(a.key)
    }
  }

  const showPaginationText=ref(false)
  function fetchSuccess(data) {
    showPaginationText.value=false
    tableTotal.value=data.items.length
    showPaginationText.value=true
    if(data.items.length<50){
      for (let i =  data.items.length; i < 50; i++) {
        data.items.push({successState: '11'})
      }
    }
    setTableData(data.items)
  }
  // 分配
  function assignClass() {
    openOrgAssignModalPage(true, {
      orgUnique:orgUnique.value
    });
  }
  const val = {
    parentId: '',
    cusCclassName: '',
    cusClass: '',
  };

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

  const openAddPage = () => {

  };

  const del = async () => {
    if(getSelectRows().length==0){
      return message.error('至少选择一条数据进行删除');
    }
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据不能恢复，你确认要删除吗？',
      onOk: async() => {
        await delOriginCustomerClassById({orgUnique:orgUnique.value,list:JSON.stringify(getSelectRows())}).then(async (data)=>{
          if(data.length>0){
            createConfirm({
              iconType: 'warning',
              title: '警告',
              content: data+'不是末级,不能删除!!',
              onOk: async () => {
                pageReload();
              },
            });
            // 埋点-操作日志
            let arr=getSelectRows().map(a=>a.cusCclassName).filter(a=>data.indexOf(a)==-1)
            if(arr.length>0){
              let orgInfo=await findByUniqueCode(orgUnique.value)
              for (let i = 0; i < arr.length; i++) {
                let log='操作内容【删除客户分类】,组织代码【'+orgInfo.orgCode+'】,组织名称【'+orgInfo.orgName+'】,分类编码【'+getSelectRows().map(a=>a.cusClass).filter(a=>data.indexOf(a)==-1)[i]+'】,分类名称【'+getSelectRows().map(a=>a.cusCclassName).filter(a=>data.indexOf(a)==-1)[i]+'】'
                /************** 记录操作日志 ****************/
                let logmap={
                  loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
                  userId: useUserStoreWidthOut().getUserInfo.username,
                  userName: useUserStoreWidthOut().getUserInfo.realName,
                  optModule:'org',
                  optFunction:'客户分类',
                  optRange:'0',
                  uniqueCode:orgUnique.value,
                  optAction:'删除',
                  optContent:log,
                }
                await saveLog(logmap)
                /************** 记录操作日志 ****************/
              }
            }
          }else{
            let orgInfo=await findByUniqueCode(orgUnique.value)
            // 埋点-操作日志
            for (let i = 0; i < [...new Set(getSelectRows().map(a=>a.ccode))].length; i++) {
              let log='操作内容【删除客户分类】,组织代码【'+orgInfo.orgCode+'】,组织名称【'+orgInfo.orgName+'】,分类编码【'+[...new Set(getSelectRows().map(a=>a.cusClass))][i]+'】,分类名称【'+[...new Set(getSelectRows().map(a=>a.cusCclassName))][i]+'】'
              /************** 记录操作日志 ****************/
              let logmap={
                loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
                userId: useUserStoreWidthOut().getUserInfo.username,
                userName: useUserStoreWidthOut().getUserInfo.realName,
                optModule:'org',
                optFunction:'客户分类',
                optRange:'0',
                uniqueCode:orgUnique.value,
                optAction:'删除',
                optContent:log,
              }
              await saveLog(logmap)
              /************** 记录操作日志 ****************/
            }
            pageReload();
          }
        })
      }
    })
  };
  async function saveData(data: any) {
    await GroupCustomerSaveApi(data, username.value);
    // 所有集团账套
    let dataBaseInfo=await findByAllSysAccPeriodGroupDataBase()
    dataBaseInfo.forEach(async (v)=>{
      await useRouteApi(customerSaveApi,{schemaName: v.accountMode})({params:data, username:username.value});
    })
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
    // 组织列表
    const oration = await getOrganizeAll();
    orientationlist.value = oration;
    if(orgUnique.value==''){
      orgUnique.value=orientationlist.value[0].uniqueCode
    }
    pageParameter.orgUnique=orgUnique.value
    pageReload()
  })

  async function fetch() {
    const deptTree =await treeCustomerClass(pageParameter)
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
    setTimeout(()=>{
      reload()
    },500)
  }

  // 引入集团客户分类档案
  function bringGroupDataBtn() {
    openBringGroupModalPage(true, {
      orgUnique:orgUnique.value,
      list:getDataSource()
    });
  }

  // 引入回调
  async function bringGroupDataFunction(data) {
    data.forEach(a=>{
      a.id=null
      a.flag='1'
      a.orgUnique=orgUnique.value
    })
    await saveAll(data).then(()=>pageReload())
    // 埋点-操作日志
    let orgInfo=await findByUniqueCode(orgUnique.value)
    for (let i = 0; i < data.map(a=>a.cusClass).length; i++) {
      let log='操作内容【组织引入客户分类】,组织代码【'+orgInfo.orgCode+'】,组织名称【'+orgInfo.orgName+'】,分类编码【'+data.map(a=>a.cusClass)[i]+'】,分类名称【'+data.map(a=>a.cusCclassName)[i]+'】'
      /************** 记录操作日志 ****************/
      let logmap={
        loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
        userId: useUserStoreWidthOut().getUserInfo.username,
        userName: useUserStoreWidthOut().getUserInfo.realName,
        optModule:'org',
        optFunction:'客户分类',
        optRange:'0',
        uniqueCode:orgUnique.value,
        optAction:'分配',
        optContent:log,
      }
      await saveLog(logmap)
      /************** 记录操作日志 ****************/
    }
  }

  // 组织切换
  function orgCheck() {
    pageParameter.orgUnique=orgUnique.value
    pageParameter.superid=''
    pageParameter.searchValue=''
    pageReload()
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
  height: calc(100% - 228px);
  border: 1px #cccccc solid;
  background:white ;
  margin-top: -0.5%;
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
