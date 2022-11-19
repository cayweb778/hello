<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <div class="container-head-title" style="float: left;margin-top: 10px;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 35%;text-align: center;margin-top: 30px;">
          <b class="noneSpan" style="font-size: 36px;">供应商信息</b>
        </div>
        <div class="ant-btn-group" style="float: right;">
          <span v-if="defaultPage">
             <button
               type="button"
               class="ant-btn ant-btn-me"
               ant-click-animating-without-extra-node="false"
               @click="openAddPage()"
             ><span>新增</span></button>
            <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false" @click="rowOpenEdit"><span>修改</span></button>
            <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false" @click="openBatchEdit"><span>批改</span></button>
            <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false" @click="rowOpenDel"><span>删除</span></button>
            <button type="button" class="ant-btn ant-btn-me" @click="openImportExcel"><span>导入</span></button>
          </span>
          <span v-else>
            <button type="button" class="ant-btn ant-btn-me" @click="applyAdd"><span>新增申请</span></button>
            <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false" @click="rowOpenEdit"><span>修改</span></button>
            <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false" @click="rowOpenDel2"><span>删除</span></button>
            <button type="button" class="ant-btn ant-btn-me" @click="introduce"><span>引入</span></button>
          </span>
          <button type="button" class="ant-btn ant-btn-me" @click="exportExcel123()"><span>导出</span></button>
          <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false" @click="jsonPrint"><span>打印</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent(),router.push('/one/home/welcome')"><span>退出</span></button>
        </div>
      </div>
      <div style="clear: none"/>
      <div style="margin-top: -30px;margin-left: 80px;">
        <div style="display: inline-block;float: left;margin-top: -25px;">
          <AccountPicker theme="three" @reloadTable="dynamicAdReload"/>
        </div>
<!--        <div style="display: inline-block;position:absolute;top:85px;left:110px;font-size: 10px;color:#999999;">共 {{custCountAllList}} 条记录</div>-->
        <div style="float: right; margin-left: 10px;">
          <a-button @click="pageReload">
            <SyncOutlined :style="{ fontSize: '14px' }" />
          </a-button>
          <a-popover placement="bottom" v-model:visible="visible3">
            <template #content>
              <DynamicColumn :defaultData="initDynamics()['DATA']" :dynamicData="dynamicColumnData" :lanmuInfo="lanMuData" @reload="reloadColumns"/>
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
          <span v-if="defaultPage">
            <a-button title="回收站" @click="openSupDel()">
              <DeleteOutlined :style="{ fontSize: '14px' }"/>
            </a-button>
          </span>
        </div>
        <div style="float: right; position: relative">
          <span style="float: left; position: relative">
            <a-select v-model:value="pageParameter.searchConditon.requirement" style="width: 130px;" class="special_select">
              <template v-for="item in searchConditonList">
                <a-select-option :value="item.dataIndex">
                  {{ item.title }}
                </a-select-option>
              </template>
            </a-select>
            <a-input-search
              placeholder=""
              v-model:value="pageParameter.searchConditon.value"
              @search="pageSearch"
              style="width: 150px;border-radius: 4px"
            />
          </span>
        </div>
      </div>
      <div style="clear:both"/>
    </div>
    <div class="app-container">
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <div class="bg-white m-4 mr-0 overflow-hidden" style="margin-top: -0.5px;margin-left: -5px;">
          <div style="width: 100%; height: 32px;background-color: #d9d9d9;padding-top:5px;font-weight: bold;text-align: center;">
            供应商分类
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
            v-model:expandedKeys="expandedKeys"
            @select="handleSelect"
          />
        </div>
        <div class="temp" style="margin-left: 5px;">
          <BasicTable
            ref="tableRef"
            :bordered="true"
            :loading="loadMark"
            :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
            :scroll="{ x: totalColumnWidth ,y: windowHeight }"
            :row-selection="{ type: 'checkbox',getCheckboxProps:rowSelection.getCheckboxProps }"
            @register="registerTable"
            @row-click="rowClick"
          >
            <template #successState="{ record }">
          <span>
            <a-tag :color="record.successState === '1' ? 'green' : 'volcano'">
              {{ successStateFlag(record.successState) }}
            </a-tag>
          </span>
            </template>
            <template #flag="{ record }">
              <span  v-if="record.flag!=='11'">
                <a-tag :color="record.flag === '1' ? 'green' : 'volcano'">
                  {{ flagStr(record.flag) }}
                </a-tag>
              </span>
            </template>
            <template #manageType="{ record }">
              <span v-if="record.manageType==='0'">内部供应商</span>
              <span v-if="record.manageType==='1'">外部供应商</span>
            </template>
            <template #custCode="{ record }">
              <a style="cursor: pointer;" @click="openLook(record,false)">{{ record.custCode}}</a>
            </template>

            <template #custName="{ record }">
              <a style="cursor: pointer;" @click="openLook(record,false)">{{ record.custName}}</a>
            </template>
            <template #action="{ record }">
              <div v-if="record.flag!=='11'">
                <a-popover placement="bottom">
                  <a-button style="padding: 0px 4px; height: 20px">
                    <CaretDownFilled />
                  </a-button>
                  <template #content>
                    <p style="cursor: pointer" @click="editflag(record.id,'1')" v-if="record.flag=='0'"><CheckCircleOutlined /> 启用</p>
                    <p style="cursor: pointer" @click="editflag(record.id,'0')" v-if="record.flag=='1'"><CloseCircleOutlined /> 停用</p>
                    <!--已生效的删除-->
                    <p style="cursor: pointer" @click="del(record.id,record.uniqueCode,record)" v-if="record.successState=='1'"><DeleteOutlined /> 删除</p>
                    <!--未生效的删除-->
                    <p style="cursor: pointer" @click="wsxdel(record.id,record)" v-if="record.successState=='0'"><DeleteOutlined /> 删除</p>
                  </template>
                </a-popover>
              </div>
            </template>
          </BasicTable>
          <div class="pagination-text" :style="{top: (windowHeight+45)+'px',left:(totalColumnWidth-330)+'px'}" v-show="showPaginationText">
            {{`共 ${custCountAllList}条记录&emsp;每页 200 条`}}
          </div>
        </div>
        <Edit
          @closeOk="pageReload()"
          @dulisave="dulisaveData"
          @register="registerEditPage"
        />
        <ImprotExcel @save="fetch" @register="registerExcelPage" />
        <!--供应商分类增加页面-->
        <SupClassAdd @save="saveTreeData" @register="registerTreeAddPage" />
        <ModalPop @throwData="modalData" @register="registerModalPopPage" />
        <ParentModalPop @throwData="parentModalData" @register="registerParentModalPopPage" />
        <BringGroupModal @throwData="bringOrgDataFunction" @register="registerBringOrgModalPage" />
        <ApplyList @closeOk="pageReload" @register="registerApplyListPage" />
        <SupDel @save="pageReload" @register="registerSupDelPage" />
        <Print @save="loadPrint" @register="registerPrintPage" />
        <BatchEdit @closeOk="pageReload" @register="registerBatchEditPage" />

      </PageWrapper>
    </div>
  </div>
</template>
<script setup lang="ts">
import router from "/@/router";
import {BasicTree} from '/@/components/Tree';
import {BasicTable, useTable} from '/@/components/Table';
import {PageWrapper} from '/@/components/Page';
import {useModal} from '/@/components/Modal';
import {
  PlusCircleTwoTone,
  CaretDownFilled,
  CheckCircleOutlined,
  CheckOutlined,
  CloseCircleOutlined,
  DeleteOutlined,
  EditOutlined,
  FormOutlined,
  PicLeftOutlined,
  ReadOutlined,
  RestOutlined,
  SafetyOutlined,
  SettingFilled,
  SortAscendingOutlined,
  SortDescendingOutlined,
  SyncOutlined,
  UnorderedListOutlined,
  ProfileOutlined
} from '@ant-design/icons-vue';
import {
  auditSupplierSave,
  countAccVoucherByCusUnique20,
  countAccVoucherByCusUnique21,
  countAccVoucheryByCusUnique,
  custCountAll,
  delCus,
  delCusArr,
  duLiSaveApi,
  editCusFlag,
  findAll,
  findById,
  editIsDel, delFindByStock
} from '/@/api/record/supplier_data/supplier';
import {
  delCusArrGroup,
  delCusGroup,
  editFlagByCtypeAndOrgUniqueAndTenantId
} from '/@/api/record/system/supplier_group';
import Edit from './popup/edit.vue';
import ModalPop from './popup/modalPop.vue';
import ParentModalPop from './popup/parentModalPop.vue';
import ImprotExcel from './popup/improtExcel.vue';
import BringGroupModal from './popup/bringGroupModal.vue';
import ApplyList from './popup/apply-list.vue';
import SupDel from './popup/sup-del.vue';
import Print from './popup/print.vue';
import BatchEdit from './popup/batchEdit.vue';
import {
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
import {useUserStoreWidthOut} from '/@/store/modules/user';
import {reactive, ref} from 'vue';
import {aoaToSheetXlsx} from '/@/components/Excel';
import {
  assemblyDynamicColumn,
  initDynamics
} from "/@/views/boozsoft/system/supplier/data";
import {useMessage} from "/@/hooks/web/useMessage";
import {findDbLanMuList, saveLanMuList,} from '/@/api/record/system/accvoucher';
import {getCurrentAccountName, hasBlank} from "/@/api/task-api/tast-bus-api";
import {cloneDeep} from "lodash-es";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker-DATA.vue";
import {countByFunctionModule, findByFunctionModule, saveTaskApi} from "/@/api/record/system/task";
import {
  customerSaveApi,
  GetCustomerClassTree,
  getCusClassAuthorData,
  findAllByCusBendEq1
} from "/@/api/record/system/supplier_class";
import SupClassAdd from '../sup_class/popup/save.vue';
import {useTabs} from "/@/hooks/web/useTabs";
import {useRoute} from "vue-router";
import {saveLog} from "/@/api/record/system/group-sys-login-log";
import {findByAccId} from "/@/api/record/system/account";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {useNewPrint} from "/@/utils/boozsoft/print/print";
import {tableStyle} from "/@/store/modules/abc-print";
import { getCusTypeSeeSwitch} from "/@/api/record/system/customer_class";
import { getCusDataAuthorData } from "/@/api/record/supplier_data/supplier";
import {findByStockAccountAccId} from "/@/api/record/system/stock-account";
//import DynamicColumn from "/@/views/boozsoft/stock/stock_sales_add/component/DynamicColumn.vue";
const DynamicColumn=null

const { createConfirm,createWarningModal,createMessage } = useMessage();
  const searchConditonList = ref([
    {
      title: '状态',
      dataIndex: 'flag',
      width: 60,
      slots: { customRender: 'flag' },
    },
    {
      title: '供应商编码',
      dataIndex: 'custCode',
      ellipsis: true,
      slots: { customRender: 'custCode' },
    },
    {
      title: '供应商全称',
      dataIndex: 'custName',
      ellipsis: true,
      slots: { customRender: 'custName' },
      align:'left',
    },
    {
      title: '供应商简称',
      dataIndex: 'custAbbname',
      ellipsis: true,align:'left',
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
    },
  ])
  const treeData:any = ref([]);
  const selectedKeys2:any = ref<string[]>([])
const expandedKeys:any = ref([]);
  const tableshow = ref(false);
  const windowWidth  = (window.innerWidth -(70))
const windowHeight = (window.innerHeight - (300))
  const totalColumnWidth = ref(0)
  const dynamicColumns = initDynamics().DEFAULT
const dynamicColumnData = ref({value:[]})
  const editableData = reactive({});
  const tableRef:any = ref(null)
  const visible = ref(false);
  const visible3 = ref(false);
  const ASelectOption = ASelect.Option;
  const AInputSearch = AInput.Search;
  const AStatisticCountdown = AStatistic.Countdown;
  const ATabPane = ATabs.TabPane;
  const independent = ref(true);
  const ARadioButton = ARadio.Button
  const ARadioGroup = ARadio.Group
  const database=ref(getCurrentAccountName(true))
  const accId=ref('')
  const accountInfo:any=ref({})
  const accGroup=ref('')    // 所属组织
  const databaseName=ref('')
  const defaultPage = ref(true)   // 先判断是否独立账套,true 是；false 否
  const loadMark =ref(true)
  const custCountAllList = ref(0)
  const mockData = ref([])
  const targetKeys = ref([])
  const selectedKeys = ref([])
  const { closeCurrent } =useTabs();
  const dataBasePeriodList = ref([]) // 会计期间
  const CrudApi = {
    list: findAll,
    columns: [
      {
        title: '申请状态',
        dataIndex: 'successState',
        ellipsis: true,
        defaultHidden: true,width: 100,
        slots: { customRender: 'successState' },
      },
      {
        title: '状态',
        dataIndex: 'flag',
        width: 60,
        slots: { customRender: 'flag' },
      },
      {
        title: '供应商编码',
        dataIndex: 'custCode',
        ellipsis: true,
        slots: { customRender: 'custCode' },
      },
      {
        title: '供应商全称',
        dataIndex: 'custName',
        ellipsis: true,
        slots: { customRender: 'custName' },
        align:'left',
      },
      {
        title: '供应商简称',
        dataIndex: 'custAbbname',
        ellipsis: true,align:'left',
      },
      {
        title: '供应商分类',
        dataIndex: 'uniqueCustclassName',
        ellipsis: true,align:'left',
      },
      {
        title: '管理部门',
        dataIndex: 'deptName',
        ellipsis: true,align:'left',
      },{
        title: '业务员',
        dataIndex: 'psnName',
        ellipsis: true,align:'left',
      }, {
        title: '税号',
        dataIndex: 'custSregcode',
        ellipsis: true,align:'left',
      }, {
        title: '价格级次',
        dataIndex: 'priceLevel',
        ellipsis: true,align:'center',
      },{
        title: '联系人',
        dataIndex: 'contacts',
        ellipsis: true,align:'left',
      },{
        title: '联系电话',
        dataIndex: 'telephone',
        ellipsis: true,align:'left',
      },{
        title: '手机',
        dataIndex: 'cellPhoneNum',
        ellipsis: true,align:'left',
      },{
        title: 'Email',
        dataIndex: 'email',
        ellipsis: true,align:'left',
      },{
        title: '省市区',
        dataIndex: 'city',
        ellipsis: true,slots: { customRender: 'city' },align:'left',
      },{
        title: '详细地址',
        dataIndex: 'address2',
        ellipsis: true,align:'left',
      },{
        title: '开户银行',
        dataIndex: 'custBank',
        ellipsis: true,align:'left',
      },{
        title: '开户地',
        dataIndex: 'bankArea',
        ellipsis: true,align:'left',
      },{
        title: '银行账号',
        dataIndex: 'custAccount',
        ellipsis: true,align:'left',
      },{
        title: '银行行号',
        dataIndex: 'bankCode',
        ellipsis: true,align:'left',
      },{
        title: '税率',
        dataIndex: 'taxRate',
        ellipsis: true,align:'left',
      },{
        title: '母公司',
        dataIndex: 'parentName',
        ellipsis: true,align:'left',
      },{
        title: '对应客户',
        dataIndex: 'supName',
        ellipsis: true,align:'left',
      },{
        title: '开票抬头全称',
        dataIndex: 'aaa',
        ellipsis: true,align:'left',
      },{
        title: '纳税人识别号',
        dataIndex: 'bbb',
        ellipsis: true,align:'left',
      },{
        title: '地址电话',
        dataIndex: 'ccc',
        ellipsis: true,align:'left',
      },{
        title: '开户行及账号',
        dataIndex: 'ddd',
        ellipsis: true,align:'left',
      },{
        title: '管理类型',
        dataIndex: 'manageType',
        ellipsis: true,
        slots: { customRender: 'manageType' },
      }
    ],
  };
  const pageParameter = reactive({
    showRulesSize: 'MIN',
    companyCode: '',
    companyName: '',
    ifUnit: false,
    total: 0,
    thisAdInfo: {},
    thisLeftKey: '',
    flag:'1',
    database: database.value,
    accId: accId.value,
    username: useUserStoreWidthOut().getUserInfo.username,
    uniqueCustclass:'',
    searchConditon: {
      requirement: 'custCode',
      value: '',
    },
  })

  const val = {
    id: '',
    uniqueCode: '',
    custName: '',
    uniqueCustclass: '',
    custCode: '',
    custAbbname: '',
    custSregcode: '',
    manageType: '',
    uniqueCodeCcus: '',
    uniqueCodeSupplier: '',
    address1: '',
    contacts: '',
    address2: '',
    telephone: '',
    countryName: '',
    cellPhoneNum: '',
    website: '',
    email: '',
    industryclassName: '',
    successState: '',
  };
  // 增加操作日志：1、修改2、删除3、停用、4查看、5导入、6导出、7打印
  const logmap = ref({
    accId: database.value,
    username: useUserStoreWidthOut().getUserInfo.username,
    flag: '',
    text: '',
  });
  // 这是示例组件
  const [registerTable, { deleteSelectRowByKey,reload,getColumns,setColumns,setTableData,getPaginationRef,setPagination,getSelectRows,getDataSource,clearSelectedRowKeys }] = useTable({
    columns: CrudApi.columns,
    bordered: true,
    showIndexColumn: true,
    indexColumnProps:{ fixed:true },
    pagination: {
      pageSize: 200,
      simple:true
    },actionColumn: {
      width: 50,
      title: '操作',
      dataIndex: 'action',
      slots: {customRender: 'action'},
    },
    searchInfo: pageParameter
  });

  const [registerEditPage, { openModal: openEditPage }] = useModal();
  const [registerExcelPage, { openModal: openExcelPage }] = useModal();
  const [registerTreeAddPage, { openModal: openCusClassTreePage }] = useModal();
  const [registerModalPopPage, { openModal: openMoalPopPage }] = useModal();
  const [registerParentModalPopPage, { openModal: openParentMoalPopPage }] = useModal();
  const [registerBringOrgModalPage, { openModal: openBringOrgModalPage }] = useModal();
  const [registerApplyListPage, { openModal: openApplyListPage }] = useModal();
  const [registerSupDelPage, { openModal: openSupDelPage }] = useModal();
  const [registerPrintPage, { openModal: openPrintPage }] = useModal();
  const [registerBatchEditPage, { openModal: openBatchEditPage }] = useModal();


function rowClick(a) {
  if(hasBlank(a.custCode)){
    deleteSelectRowByKey(a.key)
  }
}
const rowSelection = {
  getCheckboxProps: (record) => ({
    disabled: record.custCode==undefined
  }),
};

  // 新增申请
  async function applyAdd() {
    /************** 记录操作日志 ****************/
    let map={
      loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
      userId: useUserStoreWidthOut().getUserInfo.username,
      userName: useUserStoreWidthOut().getUserInfo.realName,
      optModule:'master_data',
      optFunction:'账套',
      optRange:'1',
      uniqueCode:database.value,
      optAction:'新增申请',
      optContent:'操作内容【点击新增申请】,账套代码【'+accountInfo.value.coCode+'】,账套名称【'+accountInfo.value.companyName+'】',
    }
    await saveLog(map)
    /************** 记录操作日志 ****************/
    openApplyListPage(true, {
      tenantId:database.value,
      accGroup:accGroup.value,
      accountInfo:accountInfo.value
    });
  }


  function successStateFlag(flag: any) {
    let str = '已生效';
    switch (flag) {
      case '1':
        str = '已生效';
        break;
      case '0':
        str = '未生效';
        break;
    }
    return str;
  }
  function flagStr(flag: any) {
    let str = '启用';
    switch (flag) {
      case '1':
        str = '启用';
        break;
      case '0':
        str = '停用';
        break;
    }
    return str;
  }

  // 打印
  function jsonPrint() {
    openPrintPage(true, {
      data: {
        dynamicTenantId: database.value,
        defaultAdName: useCompanyOperateStoreWidthOut().getSchemaName,
      },
    });
  }
  // 导出Excel
  const exportExcel123 = async () => {
    const data = JSON.parse(JSON.stringify(getDataSource()))
    const columns:any = ['供应商编码','供应商全称','供应商简称','税号','分类名称','母公司','对应客户','省','市','区','详细地址','电话','手机','网站', 'EMail','行业性质','开户银行','开户地','银行账户','银行行号','联系人','价格级次','税率']
    const dataArr:any=[]
    data.forEach(v=>{
      let province=v.province=='' || v.province==null?'':v.province;
      let city=v.city=='' || v.city==null?'':v.city;
      let area=v.area=='' || v.area==null?'':v.area;
      let temp:any=[]
      temp[0]=v.custCode,
        temp[1]=v.custName,
        temp[2]=v.custAbbname,
        temp[3]=v.custSregcode,
        temp[4]=v.uniqueCustclassName,
        temp[5]=v.parentName,
        temp[6]=v.supName,
        temp[7]=province,
        temp[8]=city,
        temp[9]=area,
        temp[10]=v.address2,
        temp[11]=v.telephone,
        temp[12]=v.cellPhoneNum,
        temp[13]=v.website,
        temp[14]=v.email,
        temp[15]=v.industryclassName,
        temp[16]=v.custBank,
        temp[17]=v.bankArea,
        temp[18]=v.custAccount,
        temp[19]=v.bankCode
        temp[20]=v.contacts
        temp[21]=v.priceLevel
        temp[22]=v.taxRate
      dataArr.push(temp)
    })
    aoaToSheetXlsx({
      data: dataArr,
      header: columns.map(item=>item.title),
      filename: '供应商信息_'+databaseName.value+'.xlsx',
    });
  }

  function openImportExcel() {
    openExcelPage(true, {
      data: useUserStoreWidthOut().getUserInfo.username,
      databaseName: databaseName.value,
      database:database.value,
      accountInfo:accountInfo.value
    });
  }
  const openAddPage = async () => {
    await useRouteApi(findAllByCusBendEq1,{schemaName: database})('')
      .then(async (data)=>{
        if(data.total==0){
          await useRouteApi(customerSaveApi,{schemaName: database})({uniqueCustclass:'',cusCclassName:'默认分类',cusClass:'9999',cusClassGrade:'1',parentId:'',cusBend:'1',flag:'1',cusGradeCode:'001'})
        }
      })
      openEditPage(true, {
        data: val,
        type: 'add',
        defaultPage:defaultPage.value,
        database:database.value,
        isEdit:true,
        accountInfo:accountInfo.value
      });
  };
  const openEdit = async (data: any,flag) => {
    // if(await findByTask()){
      openEditPage(true, {
        data: data,
        type: 'edit',
        defaultPage:defaultPage.value,
        database:database.value,
        isEdit:true,
        accountInfo:accountInfo.value
      });
    // }
  };
  const openLook = async (data: any,flag) => {
    openEditPage(true, {
        data: data,
        type: 'look',
        defaultPage:defaultPage.value,
        database:database.value,
        isEdit:flag,
        accountInfo:accountInfo.value
      });
  };

  const rowOpenEdit = async() => {
    if(getSelectRows().length==0 || getSelectRows().length>1){
      return createWarningModal({ content: '请选择一条数据进行编辑' });
    }
    const info=getSelectRows()[0];
    // 是否做过存货单据
    let len=await useRouteApi(delFindByStock,{schemaName: database})(info.custCode)
    if(len>0){
      return createWarningModal({ content: info.custCode+'已生成存货管理,不能修改！' });
    }
    openEditPage(true, {
      data: info,
      type: 'edit',
      defaultPage:defaultPage.value,
      database:database.value,
      isEdit:true,
      accountInfo:accountInfo.value
    });
  };
  const del = async (id,cusUnique,data) => {
   let accvoucher= await useRouteApi(countAccVoucheryByCusUnique,{schemaName: database})({cusUnique:cusUnique});
   if(accvoucher>0){
     return createWarningModal({ content: '此供应商已生成凭证,不能删除' });
   }
    let accvoucher20= await useRouteApi(countAccVoucherByCusUnique20,{schemaName: database})({cusUnique:cusUnique});
    if(accvoucher20>0){
      return createWarningModal({ content: '不能删除' });
    }
    let accvoucher21= await useRouteApi(countAccVoucherByCusUnique21,{schemaName: database})({cusUnique:cusUnique});
    if(accvoucher21>0){
      return createWarningModal({ content: '此供应商已设置辅助期初,不能删除' });
    }
    await useRouteApi(delCus,{schemaName: database})({id:id});
    reload();
  };

  // 多选删除
  const rowOpenDel= async () => {
    let idArr:any=[]
    if(getSelectRows().length==0){
      return createWarningModal({ content: '至少选择一条数据进行删除' });
    }
    for (let i = 0; i < getSelectRows().length; i++) {
      let v=getSelectRows()[i]
      // 是否做过存货单据
      let len=await useRouteApi(delFindByStock,{schemaName: database})(v.custCode)
      if(len>0){
        return createWarningModal({ content: v.custCode+'已生成存货管理,不能删除！' });
      }
      idArr.push(v.id)
    }
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据不能恢复，你确认要删除吗？',
      onOk: async() => {
        // 账套数据
        let map={
          delName:useUserStoreWidthOut().getUserInfo.username,
          id:idArr.join(',')
        }
        await useRouteApi(editIsDel,{schemaName: database})(map)
        pageReload();
      }
    })
  }
  const wsxdel = async (id,data) => {
    await delCusGroup(id,'')
    pageReload();
  }

  const editflag = async (id,flag) => {
    await useRouteApi(editCusFlag,{schemaName: database})({id:id,flag:flag})
    pageReload();
  }
  async function handleSelect(obj: any) {
    if(obj.toString()!==''){
      pageParameter.uniqueCustclass=obj.toString()
    }else{
      pageParameter.uniqueCustclass='0'
      selectedKeys2.value='0'
    }
    findByCusAuthData()
  }

  // 独立账套：只增加到账套中
  async function dulisaveData(data: any) {
    await useRouteApi(duLiSaveApi,{schemaName: database})(data)
    pageReload();
  }

  // 查询 账套中 任务管理表 是否 有供应商信息任务
  const findByTask = async () => {
   let counttask= await useRouteApi(countByFunctionModule,{schemaName: database})({name:'供应商信息'})
    if(counttask==0){
      const data={
        caozuoUnique:useUserStoreWidthOut().getUserInfo.id,
        functionModule:'供应商信息',
        state:'1',
        method:'1'
      }
      await useRouteApi(saveTaskApi,{schemaName: database})({params:data})
      return true;
    }else{
      let taskinfo= await useRouteApi(findByFunctionModule,{schemaName: database})({name:'供应商信息'})
      if(parseInt(taskinfo.caozuoUnique)!==useUserStoreWidthOut().getUserInfo.id){
        return createWarningModal({ content: '操作员:'+taskinfo.username+',正在编辑供应商信息' });
        return false;
      }else{ return true;}
    }
  }

  function timeformat(dateData) {
    let date = new Date(dateData);
    let y = date.getFullYear();
    let m = date.getMonth() + 1;
    m = m < 10 ? '0' + m : m;
    let d = date.getDate();
    d = d < 10 ? '0' + d : d;
    return y + '-' + m + '-' + d;
  }

  // 引入
  const introduce = async () => {
    openBringOrgModalPage(true, {
      tenantId:accId.value,
      orgUnique:accGroup.value,
    });
  }
  // 引入回调
  async function bringOrgDataFunction(data) {
    data.forEach(async (a)=>{
      a.id=null
      a.flag='1'

      await useRouteApi(duLiSaveApi,{schemaName: database})(a).then(async (t)=>{
        // 埋点-操作日志
        let log='操作内容【账套引入供应商信息】,账套代码【'+accountInfo.value.coCode+'】,账套名称【'+accountInfo.value.companyName+'】,供应商编码【'+t.custCode+'】,供应商全称【'+t.custName+'】,供应商简称【'+t.custAbbname+'】'
        /************** 记录操作日志 ****************/
        let map={
          loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
          userId: useUserStoreWidthOut().getUserInfo.username,
          userName: useUserStoreWidthOut().getUserInfo.realName,
          optModule:'master_data',
          optFunction:'供应商',
          optRange:'1',
          uniqueCode:database.value,
          optAction:'引入',
          optContent:log,
        }
        await saveLog(map)
        /************** 记录操作日志 ****************/
        pageReload()
      })
    })
  }
  // tree
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
  setTimeout(()=>{
    findByCusAuthData()
    visible3.value = false
  },200)
}

// 供应商分类增加
const openTreeAddPage = () => {
  openCusClassTreePage(true, {
    data: '',
    database:database.value,
    accountInfo:accountInfo.value
  });
}
// 供应商分类增加方法
const saveTreeData = async (data) => {
  await useRouteApi(customerSaveApi,{schemaName: database})(data)
  await fetch();
}

const findByflagCheck = (a) => {
  pageParameter.flag=a
  reload()
}
  const route = useRoute();
  const dynamicAdReload = async (obj) => {
    loadMark.value=true
    visible3.value=true
    accountInfo.value=obj
    accId.value=obj.accId
    database.value =obj.accountMode
    databaseName.value=obj.baseName
    lanMuData.value.changeNumber+=1
    // 账套信息
    let temp=await findByAccId(obj.accId)
    temp=hasBlank(temp)?await findByStockAccountAccId(obj.accId):temp
    // 先判断是否独立账套,true 是；false 否
    defaultPage.value = temp.independent==undefined?true:temp.independent=='1'?true:false
    pageParameter.ifUnit = temp.icorp == '1'
    accGroup.value = temp.accGroup

    // 独立账套
    if(obj.target.independent=='1'){
      fetch()
    }else{
      findByCusClass()
    }

    /******************* 主页ID查看 *********************/
    if (Object.keys(route.query).length != 0){
      let id = Object.values(route.query).join('')
      let data = await useRouteApi(findById,{schemaName: database})(id)
      route.query = {} // 失效
      if(null == data){
        message.warning('数据查看失败！')
      }else {
        openLook(data,false)
      }
    }
    /******************* 主页ID查看 *********************/
    lanMuData.value.changeNumber += 1
  }

async function findByCusClass() {
  treeData.value=[]
  // 是否设置权限控制
  let temp=await getCusTypeSeeSwitch({accId:database.value,dataBaseName:"supplier_class"})
  if(temp!==''){
    let cusClassAuthorData=await getCusClassAuthorData({tableName:'supplier_class',userId:useUserStoreWidthOut().getUserInfo.id,accId:database.value})
    if(cusClassAuthorData.length==1){
      treeData.value.push({title: '['+cusClassAuthorData[0].cusClass+']'+cusClassAuthorData[0].cusCclassName,key:cusClassAuthorData[0].uniqueCustclass,children: []})
      selectedKeys2.value=[cusClassAuthorData[0].uniqueCustclass]
      expandedKeys.value=[cusClassAuthorData[0].uniqueCustclass]

      pageParameter.uniqueCustclass=cusClassAuthorData[0].uniqueCustclass
      setTimeout(()=>{
        findByCusAuthData()
      },200)

    }else if(cusClassAuthorData.length>1){
      let deptTree=await getCusClassAuthorData({tableName:'supplier_class',userId:useUserStoreWidthOut().getUserInfo.id,accId:database.value})
      function a(customerTree) {
        customerTree.forEach((item) => {
          if (item.children != null) {
            a(item.children);
          }
          item.title = setString('[' + item.cusClass + '] ' + item.cusCclassName,20);
          item.key = item.uniqueCustclass;
        });
      }
      a(deptTree);
      treeData.value = []
      treeData.value.push({title: '全部',key:'0',children: deptTree})
      selectedKeys2.value=['0']
      expandedKeys.value=['0']
      setTimeout(()=>{
        findByCusAuthData()
      },200)
    }
  }else{
    fetch()
  }
}
const showPaginationText:any = ref(false)
async function findByCusAuthData() {
  // 是否设置权限控制
  let temp=await getCusTypeSeeSwitch({accId:database.value,dataBaseName:"supplier"})
  if(temp!==''){
    let data=await getCusDataAuthorData({tableName:'supplier',userId:useUserStoreWidthOut().getUserInfo.id,accId:database.value,classVal:pageParameter.uniqueCustclass,searchConditon: pageParameter.searchConditon,})
    showPaginationText.value=false
    custCountAllList.value=data.length
    showPaginationText.value=true
    if(data.length<50){
      for (let i =  data.length; i < 50; i++) {
        data.push({flag: '11'})
      }
    }
    setTableData(data)
    loadMark.value=false
  }
  else{
    pageParameter.uniqueCustclass=selectedKeys2.value[0]
    let temp=await useRouteApi(findAll,{schemaName: database})(pageParameter)
    showPaginationText.value=false
    custCountAllList.value=temp.items.length
    showPaginationText.value=true
    if(temp.items.length<50){
      for (let i =  temp.items.length; i < 50; i++) {
        temp.items.push({flag: '11'})
      }
    }
    setTableData(temp.items)
    loadMark.value=false
  }
}
  async function rowOpenDel2() {
    if(getSelectRows().length==0){
      return createWarningModal({ content: '至少选择一条数据进行删除' });
    }

    let idArr:any=[]
    let group_idArr:any=[] // 未生效的单据，去集团表删除
    if(getSelectRows().length==0){
      return createWarningModal({ content: '至少选择一条数据进行删除' });
    }

    for (let i = 0; i < getSelectRows().length; i++) {
      let v=getSelectRows()[i]
      // 是否做过存货单据
      let len=await useRouteApi(delFindByStock,{schemaName: database})(v.custCode)
      if(len>0){
        return createWarningModal({ content: v.custCode+'已生成存货管理,不能删除！' });
      }
      idArr.push(v.id)
      if(v.successState!=='1'){
        group_idArr.push(v.id)
      }
    }
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据不能恢复，你确认要删除吗？',
      onOk: async () => {
        // 删除账套供应商数据
        await useRouteApi(delCusArr,{schemaName: database})({id:idArr.join(',')})
        // 包含未审批的数据，去集团供应商表删除
        if(group_idArr.length>0){
          // 删除集团 未生效的 和 账套数据
          await delCusArrGroup(group_idArr.join(','))
        }

        getSelectRows().forEach(async (v)=>{
          let logtime=new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," ")
          /************** 记录操作日志 ****************/
          let map={
            loginTime:logtime,
            userId: useUserStoreWidthOut().getUserInfo.username,
            userName: useUserStoreWidthOut().getUserInfo.realName,
            optModule:'master_data',
            optFunction:'供应商',
            optRange:'1',
            uniqueCode:accId.value,
            optAction:'删除',
            optContent:'操作内容【删除供应商信息】,账套代码【'+accountInfo.value.coCode+'】,账套名称【'+accountInfo.value.companyName+'】,供应商编码【'+v.custCode+'】,供应商简称【'+v.custName+'】,供应商简称【'+v.custAbbname+'】',
          }
          await saveLog(map)
          /************** 记录操作日志 END ****************/

          let map2={
            flag:'0',
            ctype:'2',
            orgUnique:accGroup.value,
            tenantId:accId.value,
            list:[v.uniqueCode],
          }
          // 批量修改集团引入状态
          await editFlagByCtypeAndOrgUniqueAndTenantId(map2)

          // 只对集团账套审计记录-保存删除前的
          v.optMethod='1'
          v.optUsername=useUserStoreWidthOut().getUserInfo.realName
          v.optTime=logtime
          await useRouteApi(auditSupplierSave,{schemaName: database})(v)
        })
        pageReload()
      }
    })
  }


/******************************************************************************************栏目设置**************************************************************************************************/
const lanMuData = ref({
    accId: database.value,
    menuName: '供应商信息',
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
  pageReload()
}
function initTableWidth(thisCs) {
  let total = 60
  thisCs.forEach(item => {
    if (item.ifShow == null || item.ifShow)
      total += parseInt(item.width)
  })
  if (total > windowWidth) {
    let f =250
    totalColumnWidth.value = Number(windowWidth) - f
    tableRef.value.$el.style.setProperty('width', (windowWidth +50- f) + 'px')
  } else {
    if (visible.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
    totalColumnWidth.value = total
    tableRef.value.$el.style.setProperty('width', (total + 50) + 'px')
  }
}
  /*栏目设置end*/
const pageReload =  () =>{
  loadMark.value=true
  clearSelectedRowKeys()
  findByCusClass()
}

  const modalShow = () => {
    openMoalPopPage(true, {
      database: database.value,
      accId: accId.value,
    });
  }

const parentModalShow = () => {
  openParentMoalPopPage(true, {
    database: database.value,
    accId: accId.value,
  });
}


  // 弹选回调
  const modalData = (data) => {
    var  lastItem = data[data.length - 1]
    console.log(lastItem)
  }
  function parentModalData(data) {

  }

function openSupDel() {
  openSupDelPage(true, {
    database:database.value,
    accountInfo:accountInfo.value
  })
}
const loadPrint = (obj) => {
  const data = JSON.parse(JSON.stringify(getDataSource()));
  let printList: any = [];

  let printUser = '';
  if (obj.printUser) {
    printUser = '打印人：' + useUserStoreWidthOut().getUserInfo.username;
  }

  data.forEach((item, index) => {
    let item1 = {};
    item1[0] = setString(item.custCode,25);
    item1[1] = setString(item.custName,25);
    item1[2] = setString(item.custAbbname,25);
    item1[3] = item.custSregcode;
    item1[4] = item.uniqueCustclassName;
    item1[5] = item.manageType=='1'?'外部客户':'内部客户';
    printList.push(item1);
  });
  for (let i = 0; i < printList.length % 27; i++) {
    let item1 = {};
    item1[0] = '';
    item1[1] = '';
    item1[2] = '';
    item1[3] = '';
    item1[4] = '';
    item1[5] = '';
    printList.push(item1);
  }
  let num = Math.ceil(printList.length / 27);
  useNewPrint({ data: ['l', 'px', 'a4', true] }, (doc) => {
    loadMark.value = false;
    doc.autoTable({
      head: [
        ['', '', '供应商信息', '', '', '',],
        ['核算单位：' + accountInfo.value.baseName, '', '', '' , '', '',],
        ['供应商编码', '供应商全称', '供应商简称', '税号', '分类名称', '管理类型'],
      ],
      body: printList,
      // startY: 60,
      styles: tableStyle(),
      margin: {
        left: 60,
        top: 20,
      },
      addPageContent: (data) => {
        //data.table.finalY 表格最大可容纳y轴坐标，超出时将根据设置决定是否另取一页.在页面需要分页时出现
        let tabHeigth = data.table.height,
          tabMarginTop = data.settings.margin.top,
          tabSize = data.table.finalY - tabMarginTop, //表格最大Y轴-表格顶部距离得到每页表格的最大值
          tabMarginLeft = data.settings.margin.left;
        if (data.table.finalY)
          if (data.pageNumber != Math.ceil(tabHeigth / tabSize))
            //是否分页 有分页时才有该属性finalY
            return; //如果需要每一页都显示页尾则注释该行代码
        //data.cursor.y ,data.cursor.x:表格最后一个单元坐标
        data.doc.setFontSize(9);
        // data.doc.setFont('fuhuiR', 'bold')
        // doc.autoTableText(
        //   '核算单位：' + accNameAll.value,
        //   tabMarginLeft,
        //   data.cursor.y + 3,
        //   0
        // );
        doc.autoTableText(printUser, data.cursor.x / 2 - 25, data.cursor.y + 3, 0);
        doc.autoTableText(
          '第' + data.doc.getCurrentPageInfo().pageNumber + '页/共' + num + '页',
          // '第'+data.doc.getCurrentPageInfo().pageNumber+'页',
          data.cursor.x - 50,
          data.cursor.y + 3,
          0,
        );
      },
      didParseCell(data) {
        data.cell.styles.cellPadding = { top: 3, left: 2, right: 2, bottom: 2 };
        data.cell.styles.fillColor = [255, 255, 255];
        data.cell.styles.fontSize = 9;
        data.cell.styles.lineColor = [150, 150, 150];
        // data.cell.styles.bold = false

        if (data.section == 'head' && data.row.index == 0) {
          data.cell.styles.fontSize = 20;
          data.cell.styles.fontStyle = 'bold';
          data.cell.styles.lineColor = [255, 255, 255];
          if (data.column.index == 2) {
            data.cell.colSpan = 1;
            data.cell.styles.halign = 'center';
          }
        }
        if (data.section == 'head' && data.row.index == 1) {
          data.cell.styles.fontSize = 10;
          data.cell.styles.fontStyle = 'bold';
          data.cell.styles.lineColor = [255, 255, 255];
          if (data.column.index == 0) {
            data.cell.colSpan = 3;
            data.cell.styles.halign = 'left';
          } else if (data.column.index == 3) {
            data.cell.colSpan = 2;
            data.cell.styles.halign = 'center';
          }
        }
        if (data.section == 'head' && data.row.index == 2) {
          data.cell.styles.fontSize = 10;
          data.cell.styles.cellPadding = { top: 4, left: 2, right: 2, bottom: 3 };
          data.cell.styles.fontStyle = 'bold';
          data.cell.styles.fillColor = [230, 230, 230];
          data.cell.styles.halign = 'center';
        }
        if (data.section == 'body') {
          if (data.row.index % 2 == 1) {
            data.cell.styles.fillColor = [240, 240, 240];
          }
        }
      },
      columnStyles: {
        0: { cellWidth: 100, halign: 'center' },
        1: { cellWidth: 100, halign: 'center' },
        2: { cellWidth: 100, halign: 'center' },
        3: { cellWidth: 100, halign: 'center' },
        4: { cellWidth: 50, halign: 'center' },
        5: { cellWidth: 50, halign: 'center' },
      },
    });
  });
}
//js切割字符串
function setString(str, len) {
  var strlen = 0;
  var s = '';
  for (var i = 0; i < str.length; i++) {
    if (str.charCodeAt(i) > 128) {
      strlen += 2;
    } else {
      strlen += 1.2;
    }
    s += str.charAt(i);
    if (strlen >= len) {
      return s + '...';
    }
  }
  return s;
}

async function openBatchEdit() {
  if(getSelectRows().length==0){
    return createWarningModal({ content: '至少选择一条数据进行批量修改!!' });
  }
  for (let i = 0; i < getSelectRows().length; i++) {
    let v=getSelectRows()[i]
    // 是否做过存货单据
    let len=await useRouteApi(delFindByStock,{schemaName: database})(v.custCode)
    if(len>0){
      return createWarningModal({ content: v.custCode+'已生成存货管理,不能修改！' });
    }
  }
  openBatchEditPage(true, {
    database:database.value,
    idlist:getSelectRows().map(a=>a.id)
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

}

.temp{
  position: relative;
  .pagination-text {
    position: absolute;
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
