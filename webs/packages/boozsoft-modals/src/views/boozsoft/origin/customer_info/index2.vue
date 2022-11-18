<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <div class="container-head-title" style="float: left;margin-top: 10px;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 35%;text-align: center;margin-top: 30px;">
          <b class="noneSpan" style="font-size: 36px;">客户信息</b>
        </div>
        <div class="ant-btn-group" style="float: right;">
          <button
            type="button"
            class="ant-btn  ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="openAddPage()"
          ><span>新增申请</span></button>
          <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false" @click="rowDel"><span>删除</span></button>
          <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false" @click="assignModal"><span>分配</span></button>
          <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false" @click="bringGroupDataBtn"><span>引入</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="exportExcel()"><span>导出</span></button>
          <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false"><span>打印</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="closeTab"><span>退出</span></button>
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
          <a-popover placement="bottom" v-model:visible="visible">
            <template #content>
              <DynamicColumn :defaultData="initDynamics()['DATA']" :dynamicData="dynamicColumnData"
                             :lanmuInfo="lanMuData" @reload="reloadColumns"/>
              <span @click="pageParameter.showRulesSize = 'MAX'"
                    :style="{backgroundColor: (pageParameter.showRulesSize==='MAX')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;&emsp;大号字体&emsp;<CheckOutlined v-if="pageParameter.showRulesSize==='MAX'"
                                                                              :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
              <span @click="pageParameter.showRulesSize = 'MIN'"
                    :style="{backgroundColor: (pageParameter.showRulesSize==='MIN')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;&emsp;小号字体&emsp;<CheckOutlined v-if="pageParameter.showRulesSize==='MIN'"
                                                                              :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
            </template>
            <template #title>
              <b>设置表格字号</b>
            </template>
            <a-button>
              <SettingFilled :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>
        </div>
        <div style="float: right; position: relative">
          <a-select v-model:value="pageParameter.searchConditon.requirement" style="width: 130px;" class="special_select">
            <template v-for="item in searchConditonList">
              <a-select-option :value="item.dataIndex">
                {{item.title}}
              </a-select-option>
            </template>
          </a-select>
          <a-input-search placeholder="" v-model:value="pageParameter.searchConditon.value" @search="pageSearch" style="width: 200px; border-radius: 4px" />
        </div>
      </div>
      <div style="clear:both"/>
    </div>
    <div class="app-container">
        <div style="float: left;">
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
        </div>
        <div style="width: calc(100% - 250px); float: right;margin-left: 5px;">
          <BasicTable
            ref="tableRef"
            :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
            :scroll="{x: totalColumnWidth,y: windowHeight }"
            :row-selection="{ type: 'checkbox' ,getCheckboxProps:rowSelection.getCheckboxProps}"
            @register="registerTable"
            @fetch-success="fetchSuccess"
            @row-click="rowClick"
          >
              <template #manageType="{ record }">
                <span v-if="record.manageType==='0'">内部客户</span>
                <span v-if="record.manageType==='1'">外部客户</span>
              </template>
          </BasicTable>
          <div class="pagination-text" :style="{top: (windowHeight+45)+'px',left:(totalColumnWidth-50)+'px'}" v-show="showPaginationText">
            {{`共 ${tableTotal}条记录&emsp;每页 200 条`}}
          </div>
        </div>
      <BringGroupModal @throwData="bringGroupDataFunction" @register="registerBringGroupModalPage" />
      <ApplyList @closeOk="reload(),clearSelectedRowKeys(),fetch()" @register="registerApplyListPage" />
      <GroupAssignModal @save="pageReload" @register="registerAssignModalPage" />
    </div>
  </div>
</template>
<script setup lang="ts">
import {BasicTree} from '/@/components/Tree';
import {BasicTable, useTable} from '/@/components/Table';
import {useCompanyOperateStoreWidthOut} from '/@/store/modules/operate-company';
import {useModal} from '/@/components/Modal';
import {CheckOutlined, ProfileOutlined, SettingFilled, SyncOutlined} from '@ant-design/icons-vue';
import {editFlagByCtypeAndOrgUnique,} from '/@/api/record/system/customer_group';
import {
  Input as AInput,
  Popover as APopover,
  Radio as ARadio,
  Select as ASelect,
  Statistic as AStatistic,
  Tabs as ATabs,
} from 'ant-design-vue';

import {useUserStoreWidthOut} from '/@/store/modules/user';
import {onMounted, reactive, ref} from 'vue';
import {assemblyDynamicColumn, initDynamics} from "./data";
import {useMessage} from "/@/hooks/web/useMessage";
import router from "/@/router";
import {useTabs} from "/@/hooks/web/useTabs";
import {findByUniqueCode, getOrganizeAll} from "/@/api/record/group/im-organize";
import {treeCustomerClass} from "/@/api/org/cus_class/org_cus_class";
import BringGroupModal from './popup/bringGroupModal.vue';
import ApplyList from './popup/apply-list.vue';
import GroupAssignModal from './popup/groupAssignModal.vue';
import {
  originCustomerDelById,
  originCustomerFindAll,
  originCustomerSaveAll
} from "/@/api/org/cus/org_cus";
import {saveLog} from "/@/api/record/system/group-sys-login-log";
//import {aoaToSheetXlsx} from "/@/components/Excel";
const aoaToSheetXlsx=null
import {hasBlank} from "/@/api/task-api/tast-bus-api";
import DynamicColumn from "/@/views/boozsoft/stock/stock_sales_add/component/DynamicColumn.vue";

const { createConfirm,createWarningModal,createMessage } = useMessage();
  const tableshow = ref(false);
  const visible = ref(false);
  const windowWidth  = (window.innerWidth -(70/*+280*/))
  const windowHeight = (window.innerHeight - (310))
  const totalColumnWidth = ref(0)
  const dynamicColumns = initDynamics().DEFAULT
  const dynamicColumnData:any = ref({value: []})
  const editableData = reactive({});
  const tableRef:any = ref(null)
  const ASelectOption = ASelect.Option;
  const AInputSearch = AInput.Search;
  const AStatisticCountdown = AStatistic.Countdown;
  const ATabPane = ATabs.TabPane;
  const ARadioButton = ARadio.Button
  const ARadioGroup = ARadio.Group
  const searchConditonList = ref([
    {
      title: '客户编码',
      dataIndex: 'custCode',
      ellipsis: true,
      slots: { customRender: 'custCode' },fixed: 'left'
    },
    {
      title: '客户全称',
      dataIndex: 'custName',
      ellipsis: true,
      slots: { customRender: 'custName' },fixed: 'left'
    },
    {
      title: '客户简称',
      dataIndex: 'custAbbname',
      ellipsis: true,fixed: 'left'
    },
    {
      title: '税号',
      dataIndex: 'custSregcode',
      ellipsis: true,
    },
    {
      title: '分类名称',
      dataIndex: 'uniqueCustclassName',
      ellipsis: true,
    },
    {
      title: '管理类型',
      dataIndex: 'manageType',
      ellipsis: true,
      slots: { customRender: 'manageType' },
    },{
      title: '行业性质',
      dataIndex: 'industryclassName',
      ellipsis: true,
    }
  ])
  const selectedKeys = ref([])
  const treeData:any = ref([]);
  const selectedKeys2:any = ref<string[]>([])
  const orientationlist:any = ref<string[]>([])
  const orgUnique:any = ref('')

  const { closeCurrent } =useTabs();
  const tableTotal:any = ref(0)
  const pageParameter = reactive({
    searchConditon: {
      requirement: 'custCode',
      value: '',
    },
    showRulesSize: 'MIN',
    uniqueCustclass:'0',
    database: useCompanyOperateStoreWidthOut().getSchemaName,
    username: useUserStoreWidthOut().getUserInfo.username,
    flag:'1',
    orgUnique:'',
    searchValue: '',
    superid: '',
  })
  const CrudApi = {
    list: originCustomerFindAll,
    columns: [
      {
        title: '客户编码',
        dataIndex: 'custCode',
        ellipsis: true,
        slots: { customRender: 'custCode' },
      },
      {
        title: '客户全称',
        dataIndex: 'custName',
        ellipsis: true,
        slots: { customRender: 'custName' },
      },
      {
        title: '客户简称',
        dataIndex: 'custAbbname',
        ellipsis: true,
      },
      {
        title: '税号',
        dataIndex: 'custSregcode',
        ellipsis: true,
      },
      {
        title: '分类名称',
        dataIndex: 'uniqueCustclassName',
        ellipsis: true,
      },
      {
        title: '管理类型',
        dataIndex: 'manageType',
        ellipsis: true,
        slots: { customRender: 'manageType' },
      },{
        title: '行业性质',
        dataIndex: 'industryclassName',
        ellipsis: true,
      }
    ],
  };
  // 这是示例组件
  const [registerApplyListPage, { openModal: openApplyListPage }] = useModal();
  const [registerBringGroupModalPage, { openModal: openBringGroupModalPage }] = useModal();
  const [registerAssignModalPage, { openModal: openAssignModalPage }] = useModal();

  const [registerTable, { deleteSelectRowByKey,getPaginationRef,reload ,getColumns ,setTableData,setColumns,getSelectRows,getDataSource,clearSelectedRowKeys,setPagination}] = useTable({
    api: CrudApi.list,
    columns: CrudApi.columns,
    bordered: true,
    showIndexColumn: true,
    indexColumnProps: {fixed: true},
    pagination: {
      pageSize: 200,
      simple:true
    },
    searchInfo: pageParameter,
  });
  const rowSelection = {
    getCheckboxProps: (record) => ({
      disabled: record.custCode==undefined
    }),
  };
  function rowClick(a) {
    if(hasBlank(a.custCode)){
      deleteSelectRowByKey(a.key)
    }
  }

const showPaginationText:any = ref(false)
  function fetchSuccess(data) {
    showPaginationText.value=false
    lanMuData.value.changeNumber += 1
    tableTotal.value=data.items.length
    showPaginationText.value=true
    if(data.items.length<50){
      for (let i =  data.items.length; i < 50; i++) {
        data.items.push({successState: '11'})
      }
    }
    setTableData(data.items)
    visible.value = false
  }
  function assignModal() {
    openAssignModalPage(true,{
      orgUnique:orgUnique.value
    })
  }

  async function handleSelect(obj: any) {
    if(obj.toString()!==''){
      pageParameter.uniqueCustclass=obj.toString()
    }else{
      pageParameter.uniqueCustclass='0'
      selectedKeys2.value='0'
    }
    reload();
  }

  async function closeTab() {
    let orgInfo=await findByUniqueCode(orgUnique.value)
    /************** 记录操作日志 ****************/
    let map={
      loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
      userId: useUserStoreWidthOut().getUserInfo.username,
      userName: useUserStoreWidthOut().getUserInfo.realName,
      optModule:'org',
      optFunction:'客户',
      optRange:'0',
      uniqueCode:orgUnique.value,
      optAction:'退出',
      optContent:'操作内容【点击退出】,组织代码【'+orgInfo.orgCode+'】,组织名称【'+orgInfo.orgName+'】',
    }
    await saveLog(map)
    /************** 记录操作日志 ****************/
    closeCurrent(),router.push('/system/home/welcome')
  }

  const pageReload =  () =>{
    fetch()
    setTimeout(()=>{
      reload()
    },500)
  }
  onMounted(async () => {
    dynamicColumnData.value.value=initDynamics()['DATA']
    visible.value = true
    // 组织列表
    const oration = await getOrganizeAll();
    orientationlist.value = oration;
    if(orgUnique.value==''){
      orgUnique.value=orientationlist.value[0].uniqueCode
    }
    pageParameter.orgUnique=orgUnique.value
    pageReload()
  })
  // tree
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

  function orgCheck() {
    pageParameter.orgUnique=orgUnique.value
    pageParameter.superid=''
    pageParameter.searchValue=''
    pageReload()
  }

  // 导出Excel
  async function exportExcel() {
    const data = JSON.parse(JSON.stringify(getDataSource()))
    const columns = [
      {
        title: '客户编码',
        key: 'custCode'},{
        title: '客户全称',
        key: 'custName'},{
        title: '客户简称',
        key: 'custAbbname'},{
        title: '税号',
        key: 'custSregcode'},{
        title: '分类名称',
        key: 'uniqueCustclassName'}
      ,{title: '母公司',key: 'parentName'}
      ,{title: '对应供应商',key: 'supName'},
      {
        title: '区划(省-市-区)',
        key: 'province'},{
        title: '详细地址',
        key: 'address2'},{
        title: '电话',
        key: 'telephone'},{
        title: '手机',
        key: 'cellPhoneNum'},{
        title: '网站',
        key: 'website'},{
        title: 'EMail',
        key: 'email'},{
        title: '行业性质',
        key: 'industryclassName'},{
        title: '开户银行',
        key: 'custBank'},{
        title: '开户地',
        key: 'bankArea'},{
        title: '银行账户',
        key: 'custAccount'},{
        title: '银行行号',
        key: 'bankCode'}
    ]
    const dataArr:any=[]
    data.forEach(v=>{
      let province=v.province=='' || v.province==null?'':v.province+'-';
      let city=v.city=='' || v.city==null?'':v.city+'-';
      let area=v.area=='' || v.area==null?'':province+city+v.area;
      let temp:any=[]
      temp[0]=v.custCode,
        temp[1]=v.custName,
        temp[2]=v.custAbbname,
        temp[3]=v.custSregcode,
        temp[4]=v.uniqueCustclassName,
        temp[5]=v.parentName,
        temp[6]=v.supName,
        temp[7]=area,
        temp[8]=v.address2,
        temp[9]=v.telephone,
        temp[10]=v.cellPhoneNum,
        temp[11]=v.website,
        temp[12]=v.email,
        temp[13]=v.industryclassName,
        temp[14]=v.custBank,
        temp[15]=v.bankArea,
        temp[16]=v.custAccount,
        temp[17]=v.bankCode
      dataArr.push(temp)
    })
    aoaToSheetXlsx({
      data: dataArr,
      header: columns.map(item=>item.title),
      filename: '客户信息.xlsx',
    });
  }

  function bringGroupDataBtn() {
    openBringGroupModalPage(true, {
      orgUnique:orgUnique.value,
    });
  }

  async function bringGroupDataFunction(data) {
    data.forEach(a=>{
      a.id=null
      a.flag='1'
      a.orgUnique=orgUnique.value
    })

    let orgInfo=await findByUniqueCode(orgUnique.value)
    await originCustomerSaveAll(data).then(async (t)=>{
      // 埋点-操作日志
      let log='操作内容【组织引入客户信息】,组织代码【'+orgInfo.orgCode+'】,组织名称【'+orgInfo.orgName+'】,客户编码【'+t.custCode+'】,客户全称【'+t.custName+'】,客户简称【'+t.custAbbname+'】'
      /************** 记录操作日志 ****************/
      let map={
        loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
        userId: useUserStoreWidthOut().getUserInfo.username,
        userName: useUserStoreWidthOut().getUserInfo.realName,
        optModule:'org',
        optFunction:'客户',
        optRange:'0',
        uniqueCode:orgUnique.value,
        optAction:'引入',
        optContent:log,
      }
      await saveLog(map)
      /************** 记录操作日志 ****************/
    })
    pageReload()
  }

  // 删除组织表数据，修改集团客户分配状态
  async function rowDel() {
    if(getSelectRows().length==0){
      return createWarningModal({ content: '请选择数据！' })
    }
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后需重新引入，你确认要删除吗？',
      onOk: async() => {
        // 删除组织表数据
        await originCustomerDelById(getSelectRows())

        let orgInfo=await findByUniqueCode(orgUnique.value)
        getSelectRows().forEach(async (a)=>{
          /************** 记录操作日志 ****************/
          // 埋点-操作日志
          let log='操作内容【组织删除客户信息】,组织代码【'+orgInfo.orgCode+'】,组织名称【'+orgInfo.orgName+'】,客户编码【'+a.custCode+'】,客户全称【'+a.custName+'】,客户简称【'+a.custAbbname+'】'
          let logmap={
            loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
            userId: useUserStoreWidthOut().getUserInfo.username,
            userName: useUserStoreWidthOut().getUserInfo.realName,
            optModule:'org',
            optFunction:'客户',
            optRange:'0',
            uniqueCode:orgUnique.value,
            optAction:'删除',
            optContent:log,
          }
          await saveLog(logmap)

          // 埋点-操作日志
          let log2='操作内容【修改组织客户分配表状态】,组织代码【'+orgInfo.orgCode+'】,组织名称【'+orgInfo.orgName+'】,客户编码【'+a.custCode+'】,客户全称【'+a.custName+'】,客户简称【'+a.custAbbname+'】'
          let logmap2={
            loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
            userId: useUserStoreWidthOut().getUserInfo.username,
            userName: useUserStoreWidthOut().getUserInfo.realName,
            optModule:'org',
            optFunction:'客户',
            optRange:'0',
            uniqueCode:orgUnique.value,
            optAction:'分配',
            optContent:log2,
          }
          await saveLog(logmap2)
          /************** 记录操作日志 ****************/
        })
        // 批量修改引入状态
        let map={
          flag:'0',
          ctype:'1',
          orgUnique:orgUnique.value,
          list:getSelectRows().map(a=>a.uniqueCode)
        }
        await editFlagByCtypeAndOrgUnique(map)
        pageReload()
      }
    })
  }

  const openAddPage = async () => {
    let orgInfo=await findByUniqueCode(orgUnique.value)
    /************** 记录操作日志 ****************/
      let map={
        loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
        userId: useUserStoreWidthOut().getUserInfo.username,
        userName: useUserStoreWidthOut().getUserInfo.realName,
        optModule:'org',
        optFunction:'客户',
        optRange:'0',
        uniqueCode:orgUnique.value,
        optAction:'新增申请',
        optContent:'操作内容【点击新增申请】,组织代码【'+orgInfo.orgCode+'】,组织名称【'+orgInfo.orgName+'】',
      }
      await saveLog(map)
    /************** 记录操作日志 ****************/
    openApplyListPage(true, {
      orgUnique:orgUnique.value,
    });
  }

/**********************栏目设置*****************************************************************************************/
const lanMuData = ref({
  accId: orgUnique.value,
  menuName: '组织客户信息',
  type: '列表',
  objects: '',
  username: useUserStoreWidthOut().getUserInfo.username,
  changeNumber: 0
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
    if (total > windowWidth) {
      let f = 0
      if (visible.value) f = 260
      totalColumnWidth.value = Number(windowWidth) - f
      tableRef.value.$el.style.setProperty('width', (windowWidth + 40 - f) + 'px')
    } else {
      if (visible.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
      totalColumnWidth.value = total
      tableRef.value.$el.style.setProperty('width', (total + 40) + 'px')
    }
  }
/*栏目设置end*/
</script>
<style src="./global-menu-index.less" lang="less" scoped></style>
<style lang="less" scoped>
a {
  color: #0096c7;
  text-decoration: none;
  cursor: pointer;
}
.bg-white {
  width: 250px;
  //height: calc(100% - 230px);
  height: 1800px;
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
  .pagination-text {
    position: absolute;
    bottom: 6px;
    right: 10%;
    top: 620px;
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
