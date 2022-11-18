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
          <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false" v-if="spBtnFlag" @click="openApplyPage"><span>生效审批</span></button>
          <button
            type="button"
            class="ant-btn  ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="openAddPage()"
          ><span>新增</span></button>
          <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false" @click="rowEdit"><span>修改</span></button>
          <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false" @click="rowDel"><span>删除</span></button>
          <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false" @click="allot"><span>分配</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="openImportExcel()"><span>导入</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="exportExcel()"><span>导出</span></button>
          <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false"><span>打印</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="closePage"><span>退出</span></button>
        </div>
      </div>
      <div style="clear: none"/>
      <div style="margin-top: -30px;margin-left: 80px;">
<!--        <div style="display: inline-block;position:absolute;top:85px;left:110px;font-size: 10px;color:#999999;">共 {{tableTotal}} 条记录</div>-->
        <div style="float: right; margin-left: 10px;">
          <a-button @click="reload">
            <SyncOutlined :style="{ fontSize: '14px' }" />
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
          <a-popover  placement="bottom">
            <template #content>
             <span class="group-btn-span-special2" @click="findByflagCheck('')" :style="pageParameter.flag==''?{backgroundColor: '#0096c7',color: 'white'}:''">
              <ReadOutlined/>&nbsp;&nbsp;全部&emsp;&ensp;<CheckOutlined  v-if="pageParameter.flag==''"/></span><br/>
              <span class="group-btn-span-special2" @click="findByflagCheck('1')" :style="pageParameter.flag=='1'?{backgroundColor: '#0096c7',color: 'white'}:''">
              <SafetyOutlined/>&nbsp;&nbsp;启用&emsp;&ensp;<CheckOutlined  v-if="pageParameter.flag=='1'"/></span><br/>
              <span class="group-btn-span-special2" @click="findByflagCheck('0')" :style="pageParameter.flag=='0'?{backgroundColor: '#0096c7',color: 'white'}:''">
              <RestOutlined/>&nbsp;&nbsp;停用&emsp;&ensp;<CheckOutlined  v-if="pageParameter.flag=='0'"/></span><br/>
            </template>
            <a-button>
              <PicLeftOutlined :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>
          <a-button title="回收站" @click="openCusDel()">
            <DeleteOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
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
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <div style="float: left;">
          <div class="bg-white m-4 mr-0 overflow-hidden" style="margin-left: -5px;">
            <div style="width: 100%; height: 32px;background-color: #d9d9d9;padding-top:5px;font-weight: bold;text-align: center;">
              客户分类
              <div style="float: right;">
                <a style="color: black;font-size: 14px;" @click="openTreeAddPage()"><PlusCircleTwoTone /></a>&emsp;
              </div>
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
            v-if="tableshow"
            :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
            :scroll="{x: totalColumnWidth ,y: windowHeight }"
            :row-selection="{ type: 'checkbox' ,getCheckboxProps:rowSelection.getCheckboxProps}"
            @register="registerTable"
            @fetch-success="fetchsuccess"
          >
            <template #custCode="{ record }">
              <a style="cursor: pointer;" class="tableUStyle" @click="openLook(record,false)">{{ record.custCode}}</a>
            </template>
            <template #custName="{ record }">
              <a style="cursor: pointer;" @click="openLook(record,false)">{{ record.custName}}</a>
            </template>
            <template #manageType="{ record }">
              <span v-if="record.manageType=='0'">内部客户</span>
              <span v-if="record.manageType=='1'">外部客户</span>
            </template>
            <template #flag="{ record }">
              <a-tag v-if="record.flag=='1'" color="green">启用 </a-tag>
              <a-tag v-if="record.flag=='0'" color="volcano">停用 </a-tag>
            </template>

            <template #successState="{ record }">
              <span v-if="record.successState!==undefined&&record.successState!=='11'">
                <a-tag :color="record.successState === '1' ? 'green' : 'volcano'">
                  {{ successStateFlag(record.successState) }}
                </a-tag>
              </span>
            </template>

            <template #action="{ record }">
              <div v-if="record.successState!=='11'">
                <a-popover placement="bottom">
                  <a-button style="padding: 0px 4px; height: 20px">
                    <CaretDownFilled />
                  </a-button>
                  <template #content>
                    <!--                <p style="cursor: pointer" @click="openEdit(record)"><FormOutlined /> 编辑</p>-->
                    <p style="cursor: pointer" v-if="record.flag=='0'"><CheckCircleOutlined /> 启用</p>
                    <p style="cursor: pointer" v-if="record.flag=='1'"><CloseCircleOutlined /> 停用</p>
                    <!--                <p style="cursor: pointer" @click="del()"><DeleteOutlined /> 删除</p>-->
                  </template>
                </a-popover>
              </div>
            </template>
          </BasicTable>
          <div class="pagination-text" :style="{top: (windowHeight+45)+'px',left:(totalColumnWidth-100)+'px'}" v-show="showPaginationText">
            {{`共 ${tableTotal}条记录&emsp;每页 200 条`}}
          </div>
        </div>
        <Edit @save="saveData" @closeOk="reload(),clearSelectedRowKeys()" @register="registerEditPage" />
        <ImprotExcel @save="reload" @register="registerExcelPage" />
        <!--客户分类增加页面-->
        <CustomerAdd @save="saveTreeData" @register="registerTreeAddPage" />
        <CusDel @save="pageReload" @register="registerCusDelPage" />
        <GroupAssignModal @save="pageReload" @register="registerAssignModalPage" />
        <ApplyList @save="pageReload" @register="registerApplyListPage" />
        <ApplyList @save="pageReload" @register="registerApplyListPage" />
      </PageWrapper>
    </div>
  </div>
</template>
<script setup lang="ts">
import {BasicTree} from '/@/components/Tree';
import {BasicTable, useTable} from '/@/components/Table';
import {PageWrapper} from '/@/components/Page';
import {useCompanyOperateStoreWidthOut} from '/@/store/modules/operate-company';
import {useModal} from '/@/components/Modal';
import {
  CaretDownFilled,
  CheckCircleOutlined,
  CheckOutlined,
  CloseCircleOutlined,
  DeleteOutlined,
  PicLeftOutlined,
  PlusCircleTwoTone,
  ProfileOutlined,
  ReadOutlined,
  RestOutlined,
  SafetyOutlined,
  SettingFilled,
  SortAscendingOutlined,
  SortDescendingOutlined,
  SyncOutlined
} from '@ant-design/icons-vue';
import {
  delCusGroup,
  findAll,
  findByOrgAssigAccountCtype,
  GroupCustomerAddApi,
} from '/@/api/record/system/customer_group';
import Edit from './popup/edit.vue';
import ImprotExcel from './popup/improtExcel.vue';
import CusDel from './popup/cus-del.vue';
import GroupAssignModal from './popup/groupAssignModal.vue';
import {
  Input as AInput,
  message,
  Popover as APopover,
  Radio as ARadio,
  Select as ASelect,
  Statistic as AStatistic,
  Tabs as ATabs,
  Tag as ATag,
} from 'ant-design-vue';

import {useUserStoreWidthOut} from '/@/store/modules/user';
import {aoaToSheetXlsx} from '/@/components/Excel';
import {onMounted, reactive, ref} from 'vue';
import {assemblyDynamicColumn, initDynamics} from "./data";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  findAllCustomerClass,
  GetCustomerClassTree,
  GroupCustomerSaveApi
} from "/@/api/record/system/customer_class_group";
import CustomerAdd from '../customer_class_group/popup/save.vue';
import ApplyList from './popup/apply-list.vue';
import router from "/@/router";
import {useTabs} from "/@/hooks/web/useTabs";
import {findAll as permissionAll} from "/@/api/record/system/group-permission";
import {saveLog} from "/@/api/record/system/group-sys-login-log";
// import DynamicColumn from "/@/views/boozsoft/stock/stock_sales_add/component/DynamicColumn.vue";
const DynamicColumn=null

const { createConfirm,createWarningModal,createMessage } = useMessage();
  const spBtnFlag = ref(false);
  const tableshow = ref(false);
  const visible = ref(false);
  const windowWidth  = (window.innerWidth -(70))
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
  const accPeriodList = ref([])     // 账套期间
  const dataBaseVal = ref('')
  const dataBaseList = ref([])
  const mockData = ref([])
  const targetKeys = ref([])
  const selectedKeys = ref([])
  const treeData:any = ref([]);
  const selectedKeys2:any = ref<string[]>([])
  const loginDate = ref(useCompanyOperateStoreWidthOut().getLoginDate.split('-')[0])
  const { closeCurrent } =useTabs();
  const tableTotal:any = ref(0)
  const pageParameter = reactive({
    searchConditon: {
      requirement: 'custName',
      value: '',
    },
    showRulesSize: 'MIN',
    uniqueCustclass:'0',
    database: useCompanyOperateStoreWidthOut().getSchemaName,
    username: useUserStoreWidthOut().getUserInfo.username,
    flag:'1'
  })
  const CrudApi = {
    list: findAll,
    columns: [
      {
        title: '申请状态',
        dataIndex: 'successState',
        ellipsis: true,
        defaultHidden: true, width: 100,
        slots: {customRender: 'successState'},
      },
      {
        title: '状态',
        dataIndex: 'flag',
        width: 60,
        slots: {customRender: 'flag'},
      },
      {
        title: '客户编码',
        dataIndex: 'custCode',
        ellipsis: true,
        slots: {customRender: 'custCode'},
      },
      {
        title: '客户全称',
        dataIndex: 'custName',
        ellipsis: true,
        slots: {customRender: 'custName'},
        align: 'left',
      },
      {
        title: '客户简称',
        dataIndex: 'custAbbname',
        ellipsis: true, align: 'left',
      },
      {
        title: '客户分类',
        dataIndex: 'uniqueCustclassName',
        ellipsis: true, align: 'left',
      },
      {
        title: '管理部门',
        dataIndex: 'deptName',
        ellipsis: true, align: 'left',
      }, {
        title: '业务员',
        dataIndex: 'psnName',
        ellipsis: true, align: 'left',
      }, {
        title: '税号',
        dataIndex: 'custSregcode',
        ellipsis: true, align: 'left',
      }, {
        title: '价格级次',
        dataIndex: 'priceLevel',
        ellipsis: true, align: 'center',
      }, {
        title: '联系人',
        dataIndex: 'contacts',
        ellipsis: true, align: 'left',
      }, {
        title: '联系电话',
        dataIndex: 'telephone',
        ellipsis: true, align: 'left',
      }, {
        title: '手机',
        dataIndex: 'cellPhoneNum',
        ellipsis: true, align: 'left',
      }, {
        title: 'Email',
        dataIndex: 'email',
        ellipsis: true, align: 'left',
      }, {
        title: '省市区',
        dataIndex: 'city',
        ellipsis: true, slots: {customRender: 'city'}, align: 'left',
      }, {
        title: '详细地址',
        dataIndex: 'address2',
        ellipsis: true, align: 'left',
      }, {
        title: '开户银行',
        dataIndex: 'custBank',
        ellipsis: true, align: 'left',
      }, {
        title: '开户地',
        dataIndex: 'bankArea',
        ellipsis: true, align: 'left',
      }, {
        title: '银行账号',
        dataIndex: 'custAccount',
        ellipsis: true, align: 'left',
      }, {
        title: '银行行号',
        dataIndex: 'bankCode',
        ellipsis: true, align: 'left',
      }, {
        title: '税率',
        dataIndex: 'taxRate',
        ellipsis: true, align: 'left',
      }, {
        title: '母公司',
        dataIndex: 'parentName',
        ellipsis: true, align: 'left',
      }, {
        title: '对应供应商',
        dataIndex: 'supName',
        ellipsis: true, align: 'left',
      }, {
        title: '开票抬头全称',
        dataIndex: 'aaa',
        ellipsis: true, align: 'left',
      }, {
        title: '纳税人识别号',
        dataIndex: 'bbb',
        ellipsis: true, align: 'left',
      }, {
        title: '地址电话',
        dataIndex: 'ccc',
        ellipsis: true, align: 'left',
      }, {
        title: '开户行及账号',
        dataIndex: 'ddd',
        ellipsis: true, align: 'left',
      }, {
        title: '管理类型',
        dataIndex: 'manageType',
        ellipsis: true,
        slots: {customRender: 'manageType'},
      },
      {
        title: '操作',
        dataIndex: 'action',
        ellipsis: true,
        slots: {customRender: 'action'},
      }
    ],
  };

const rowSelection = {
  getCheckboxProps: (record) => ({
    disabled: record.custCode==undefined
  }),
};
  function successStateFlag(flag: any) {
    let str = '';
    switch (flag) {
      case '1':
        str = '已生效';
        break;
      case '0':
        str = '未生效';
        break;
      case '-1':
        str = '已驳回';
        break;
    }
    return str;
  }
  // 这是示例组件
  const [registerTable, { reload ,getColumns ,setTableData,setColumns,getSelectRows,getDataSource,clearSelectedRowKeys,setPagination}] = useTable({
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
  const [registerEditPage, { openModal: openEditPage }] = useModal();
  const [registerExcelPage, { openModal: openExcelPage }] = useModal();
  const [registerTreeAddPage, { openModal: openCusClassTreePage }] = useModal();
  const [registerCusDelPage, { openModal: openCusDelPage }] = useModal();
  const [registerAssignModalPage, { openModal: openAssignModalPage }] = useModal();
  const [registerApplyListPage, { openModal: openApplyListPage }] = useModal();
  const [registerModalPopPage, { openModal: openMoalPopPage }] = useModal();

  const userinfo = ref({
    databaseNum: useCompanyOperateStoreWidthOut().getSchemaName,
    username: useUserStoreWidthOut().getUserInfo.username,
    realName: useUserStoreWidthOut().getUserInfo.realName,
    name: useUserStoreWidthOut().getUserInfo.name,
    phone: useUserStoreWidthOut().getUserInfo.phone,
  });
async function closePage() {
  /************** 记录操作日志 ****************/
  let map={
    loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
    userId: useUserStoreWidthOut().getUserInfo.username,
    userName: useUserStoreWidthOut().getUserInfo.realName,
    optModule:'group',
    optFunction:'客户',
    optRange:'2',
    optAction:'退出',
    optContent:'操作内容【退出客户信息】',
  }
  await saveLog(map)
  /************** 记录操作日志 END ****************/

  closeCurrent(),router.push('/system/home/welcome')
}

  function fetchsuccess(obj) {
    lanMuData.value.changeNumber += 1
    let data=obj.items.filter(v=>v.isDel!=='1')
    if(data.length<50){
      for (let i =  data.length; i < 50; i++) {
        data.push({successState: '11'})
      }
    }
    setTableData(data)
    visible.value = false
  }

  function openCusDel() {
    openCusDelPage(true, {})
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

  function openImportExcel() {
    openExcelPage(true, {
      data: useUserStoreWidthOut().getUserInfo.username,
    });
  }
  const openAddPage = () => {
    openEditPage(true, {
      data: '',
      type:'add',
      isEdit:true
    });
  };

  const openEdit = (data: any,flag:any) => {
    openEditPage(true, {
      data: data,
      type:'edit',
      isEdit:flag
    });
  };

  async function saveData(data: any) {
    data.successState='1'
    await GroupCustomerAddApi(data);
    reload();
  }

  // 行修改
  const rowEdit = async () => {
    if(getSelectRows().length>1||getSelectRows().length==0){
      return createWarningModal({ content: '请选择一条数据进行编辑' });
    }
    openEdit(getSelectRows()[0],true)
  }
  function openLook(data,flag) {
    openEditPage(true, {
      data: data,
      type:'look',
      isEdit:flag
    });
  }
  const rowDel = async () => {
    if(getSelectRows().length==0){
      return createWarningModal({ content: '至少选择一条数据进行删除' });
    }
    let okDel=[]
    let noDel=[]
    let list=getSelectRows()
    // 分配给组织
    let assignOrg=await findByOrgAssigAccountCtype('1')
    list.forEach(v=>{
      let count=assignOrg.filter(item => v.uniqueCode === item.dataUnique)
      if(count.length>0){noDel.push(v.custName)}
      else{okDel.push(v.id)}
    })
    if(noDel.length>0){
      return createWarningModal({ content: noDel.join(',')+'客户已分配组织,不能删除!' });
    }else{
      createConfirm({
        iconType: 'warning',
        title: '提示',
        content: '是否删除选中数据?',
        onOk: async() => {
          okDel.forEach(async (v)=>{
            await delCusGroup(v,useUserStoreWidthOut().getUserInfo.realName)

            let temp=getDataSource().filter(aa=>aa.id==v)
            /************** 记录操作日志 ****************/
            let map={
              loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
              userId: useUserStoreWidthOut().getUserInfo.username,
              userName: useUserStoreWidthOut().getUserInfo.realName,
              optModule:'group',
              optFunction:'客户',
              optRange:'2',
              optAction:'删除',
              optContent:'操作内容【删除客户信息】,客户编码【'+temp[0].custCode+'】,客户全称【'+temp[0].custName+'】,客户简称【'+temp[0].custAbbname+'】',
            }
            await saveLog(map)
            /************** 记录操作日志 END ****************/
            reload()
          })
        }
      })
    }
  }
  const  pageSearch = async ()=>{
    // 搜索前校验格式
    if (''==pageParameter.searchConditon.requirement.trim()){
      message.warn('请选择检索条件')
      return false
    }
    pageReload()
  }


  async function allot() {
    let map={
      searchValue: '',
      superid: '',
      page: '1',
      size: '10000',
    }
    // 末级分类
   let cusClassBend1= await findAllCustomerClass(map)
    openAssignModalPage(true,{
      cusClassList:cusClassBend1.items
    })
    /************** 记录操作日志 ****************/
    let map2={
      loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
      userId: useUserStoreWidthOut().getUserInfo.username,
      userName: useUserStoreWidthOut().getUserInfo.realName,
      optModule:'group',
      optFunction:'客户',
      optRange:'2',
      optAction:'分配',
      optContent:'【点击分配】',
    }
    await saveLog(map2)
    /************** 记录操作日志 END ****************/
  }

  // 选项在两栏之间转移时的回调函数
  function transferChange(a,b) {
    targetKeys.value = a
  }
  // 选中项发生改变时的回调函数
  function transferSelectChange(a,b) {
    selectedKeys.value=[...a,...b]
  }

  // tree
  async function fetch() {
    treeData.value=[]
    const deptTree = await GetCustomerClassTree()
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

  async function handleSelect(obj: any) {
    if(obj.toString()!==''){
      pageParameter.uniqueCustclass=obj.toString()
    }else{
      pageParameter.uniqueCustclass='0'
      selectedKeys2.value='0'
    }
    reload();
  }

  // 客户分类增加
  const openTreeAddPage = () => {
    openCusClassTreePage(true, {
      data: '',
    });
  }
  // 客户分类增加方法
  const saveTreeData = async (data) => {
    await GroupCustomerSaveApi(data, useUserStoreWidthOut().getUserInfo.username);
    fetch()
  }
  const findByflagCheck = (a) => {
    pageParameter.flag=a
    reload()
  }
  // 集团档案权限分配表-集团客户
  async function findByOrgPermission() {
    // 集团档案权限分配表-集团客户
    let findByPermission=await permissionAll()
    // 权限表：是否具有审批权限组织
    let temp=findByPermission.items.filter(a=>a.ctype=='1'&&a.baseName=='客户'&&a.userId==useUserStoreWidthOut().getUserInfo.id)
    if(temp.length>0){
      spBtnFlag.value=temp[0].isApprove=='1'
    }
  }
const showPaginationText=ref(false)
onMounted(async ()=>{
    dynamicColumnData.value.value=initDynamics()['DATA']
    visible.value = true
    fetch()
    pageParameter.page=1
    pageParameter.size=100000
    let totalSum=await findAll(pageParameter)
    showPaginationText.value=false
    tableTotal.value=totalSum.total
    showPaginationText.value=true
    findByOrgPermission()
  })

  async function openApplyPage() {
    openApplyListPage(true, {
      data: '',
    });
    /************** 记录操作日志 ****************/
    let map={
      loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
      userId: useUserStoreWidthOut().getUserInfo.username,
      userName: useUserStoreWidthOut().getUserInfo.realName,
      optModule:'group',
      optFunction:'客户',
      optRange:'2',
      optAction:'生效审批',
      optContent:'操作内容【点击生效审批-新增】',
    }
    await saveLog(map)
    /************** 记录操作日志 END ****************/
  }
  /**********************栏目设置**********************/
  const lanMuData = ref({
    accId: 'postgre',
    menuName: '集团客户信息',
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
  /*  // 去除操作咧宽
    total -= 100*/
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

const pageReload =  () =>{
  setTableData([]) // 清空可能残留的数据
  reload({
    searchInfo: pageParameter
  })
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
.bg-white{
  width: 250px;
  height: calc(100% - 238px);
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
    top: 618px;
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
