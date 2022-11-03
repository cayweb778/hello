<template>
  <div>
    <Loading :loading="compState.loading" :absolute="compState.absolute" :tip="compState.tip" />
    <div class="app-container">
      <div class="app-container-head">
        <div class="ant-btn-group" style="float: right;">
          <a-button @click="bianjiBtn(bianjiMSG=='编辑'?'返回':'编辑')" class="ant-btn ant-btn-me"> {{ bianjiMSG }} </a-button>
          <span v-if="bianjiMSG=='返回'">
            <a-button @click="openAddPage()" class="ant-btn ant-btn-me"> 新增 </a-button>
            <a-button @click="ClickOpenEdit()" class="ant-btn ant-btn-me"> 修改 </a-button>
            <a-button @click="delCode()" class="ant-btn ant-btn-me"> 删除 </a-button>
            <a-button @click="openImportExcel()" class="ant-btn ant-btn-me"> 导入 </a-button>
          </span>
          <button @click="handleDownExcel" type="button" class="ant-btn ant-btn-me"><span>导出</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent(), router.push('/origin/home/welcome')"><span>退出</span></button>
        </div>
        <div class="container-head-title" style="float: left;">
          <b class="noneSpan" style="font-size: 50px;color: rgb(0 150 199)"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="text-align: center;">
          <b class="noneSpan" style="font-size: 36px;color: rgb(0 150 199)">组织会计科目</b>
        </div>
      </div>
      <div style="clear: none"/>
      <div style="clear:both"/>
      <div style="margin-left: 60px;">
        <div style="display: inline-block;float: left;margin-top: -60px;">
          &emsp;<label>组织：</label>
          <a-select
            v-model:value="orientation"
            option-filter-prop="children"
            show-search
            @change="standardCheck"
          >
            <a-select-option
              v-for="item in orientationlist"
              :key="item.uniqueCode"
              :value="item.uniqueCode"
            >
              {{ item.orgName }}
            </a-select-option>
          </a-select>
          &emsp;<label>年度：</label>
          <a-select
            v-model:value="orgIyear"
            option-filter-prop="children"
            show-search
            @change="standardCheck"
          >
            <a-select-option v-for="item in orgIyearList" :key="item" :value="item">
              {{ item }}
            </a-select-option>
          </a-select>
        </div>
        <div style="display: inline-block;position:absolute;top:85px;left:90px;font-size: 10px;color:#999999;">共 {{tableData.length}} 条记录</div>
        <div style="display: inline-block;position:absolute;top:85px;left:200px;font-size: 10px;color:#999999;">会计准则：{{tname}}</div>
        <div style="display: inline-block;position:absolute;top:85px;left:350px;font-size: 10px;color:#999999;">科目级次：{{tjici}}</div>
        <div style="display: inline-block;position:absolute;top:85px;left:350px;font-size: 10px;color:#999999;">科目级次：{{tjici}}</div>
      </div>
      <div style="float: right;margin-top: -30px;">
        <a-select v-model:value="selectsearch" style="width: 200px" @change="inputsearch">
          <a-select-option value="ccode">科目编码</a-select-option>
          <a-select-option value="ccodeName">科目名称</a-select-option>
          <a-select-option value="bprogerty">方向</a-select-option>
          <a-select-option value="cclass">科目类型</a-select-option>
        </a-select>
        <!-- 搜索 -->
        <a-input-search
          v-model:value="inputsearchdata"
          placeholder=""
          style="width: 200px; border-radius: 4px"
          @search="inputsearch"
        />
        <a-button @click="findAllData">
          <SyncOutlined :style="{ fontSize: '14px' }"/>
        </a-button>
      </div>
    </div>
    <div class="app-container">
      <div style="float: left; width: 104px">
        <a-tabs @change="tabsCheck" tab-position="left">
          <a-tab-pane v-for="(item, i) in styleNamelist" :key="item" :tab="`${item}`" />
        </a-tabs>
      </div>
      <div style="width: calc(100% - 104px); float: right">
        <BasicTable
          ref="tableRef"
          :row-selection="{ type: 'checkbox', fixed: true }"
          :columns="tableColumns"
          :dataSource="tableData"
          :loading="loading"
          :bordered="true"
          :class="'a-table-font-size-12'"
          :scroll="{ y: windowHeight }"
          :pagination="{
            pageSize: 200,
            simple:true
          }"
          @register="registerTable"
          :showIndexColumn="true"
        >
          <template #yusuan="{ record }">
            {{ record.yusuan === '1' ? '预算类会计科目' : '财务类会计科目' }}
          </template>
          <template #ccode="{ record }">
            <a @click="RowOpenEdit(record)">{{ record.ccode }}</a>
          </template>
          <template #ccodeName="{ record }">
            <a @click="RowOpenEdit(record)">{{ addspace(record.igrade, record.ccodeName) }}</a>
          </template>
          <template #bprogerty="{ record }">
            {{ record.bprogerty == '1' ? '借' : '贷' }}
          </template>
          <template #lowerControl="{ record }">
            <span v-if="record.lowerControl == '1'">
              <CheckOutlined />
            </span>
          </template>
          <template #fuzhuControl="{ record }">
            <span v-if="record.fuzhuControl == '1'">
              <CheckOutlined />
            </span>
          </template>
          <template #bnum="{ record }">
            <span v-if="record.bnum == '1'">
              <CheckOutlined />
            </span>
          </template>
          <template #currency="{ record }">
            <span v-if="record.currency == '1'">
              <CheckOutlined />
            </span>
          </template>
          <template #bcash="{ record }">
            <span v-if="record.bcash == '1'">
              <CheckOutlined />
            </span>
          </template>
          <template #bbank="{ record }">
            <span v-if="record.bbank == '1'">
              <CheckOutlined />
            </span>
          </template>
          <template #flag="{ record }">
            <a-tag :color="record.flag === '1' ? 'green' : 'volcano'">
              {{ record.flag === '1' ? '启用' : '停用' }}
            </a-tag>
          </template>
        </BasicTable>
        <div class="pagination-text" v-show="showPaginationText">
          {{`共 ${tableData.length}条记录&emsp;每页 200 条`}}
        </div>
      </div>
    </div>

    <EditPage @save="editData" @register="registerEditPage" />
    <OrgSavePage @save="saveData" @register="registerSavePage" />
    <ImprotExcel @save="findAllData" @register="registerExcelPage" />
    <Copy @save="findAllData" @register="registerCopyPage" />
  </div>
</template>

<script setup lang="ts">
  import { CheckOutlined } from '@ant-design/icons-vue';
  import { findAllStyleByUnique, findByStandardUnique } from '/@/api/accstandard/accstandard';
  import { BasicTable, useTable } from '/@/components/Table';
  import OrgSavePage from './popup/org_save.vue';
  import EditPage from './popup/org_edit.vue';
  import Copy from './popup/copy.vue';
  import ImprotExcel from './popup/improtExcel.vue';
  import { onMounted, reactive, ref, toRaw } from 'vue';
  import { useModal } from '/@/components/Modal';
  import { useRoute } from 'vue-router';
  import {
    Tabs as ATabs,
    Tag as ATag,
    Select as ASelect,
    Input as AInput,
    Statistic as AStatistic,
    message,
  } from 'ant-design-vue';
  import { findByTOrganization, findAllByOrgCodeKeMu } from '/@/api/acctemplate/acctemplate';
  import {
    saveOrgCodekemu,
    delOrgCodekemuLikeSuperCode,
    delOrgCodekemu,
    company_saveCodekemu,
    company_findByIyearAndCcode,
    company_countByLikeCcode,
    company_delCodekemu,
    editOrgCodeKemuFlag,
    findByOrgAccountCodeKemu,
    findByAccountCodeKemuTask, company_AccountKeMuReplaceOrgKeMu,
  } from '/@/api/codekemu/codekemu';

  import { SyncOutlined, UnorderedListOutlined,ProfileOutlined } from '@ant-design/icons-vue';
  import { useUserStoreWidthOut } from '/@/store/modules/user';
  import { useCompanyOperateStoreWidthOut } from '/@/store/modules/operate-company';
  import { getCurrentAccountName } from '/@/api/task-api/tast-bus-api';
  import {
    findByUniqueCode,
    getOrganizeList,
    groupByOrgPeriodIyear,
  } from '/@/api/record/group/im-organize';
  import router from '/@/router';
  const ASelectOption = ASelect.Option;
  const AInputSearch = AInput.Search;
  const AStatisticCountdown = AStatistic.Countdown;
  const ATabPane = ATabs.TabPane;
  const { closeCurrent } = useTabs();
  // route.query.data=会计准则唯一码_科目类型唯一码BOOZ科目级次 第一级_科目模板ID
  const route = useRoute();
  const username = ref(useUserStoreWidthOut().getUserInfo.username);
  const selectsearch = ref('ccode');
  const inputsearchdata = ref('');
  const windowHeight = (window.innerHeight - (320))
  const orientationlist = ref([]);
  const styleNamelist = ref([]);
  const styleNamelist2 = ref([]);
  const tname = ref(route.query.tname);
  const orientation = ref(route.query.torganization);
  const orgIyear = ref('');
  const orgIyearList = ref([]);
  const templateId = ref(route.query.templateId);
  const uniqueAccStandard = ref(route.query.uniqueAccStandard);
  const accStyleUnique = ref(route.query.accStyleUnique);
  const { createConfirm, createWarningModal, createSuccessModal } = useMessage();
  const orgCreateDate = ref('');
  const tjici = ref('');
  const tableRef = ref(null);
  const loading = ref(true);
  const activeKey = ref('全部');
  const tableDataAll = ref([]);
  const tableData = ref([]);
  const tableColumns = [
    {
      title: 'ID',
      dataIndex: 'id',
      defaultHidden: true,
    },
    {
      title: '状态',
      dataIndex: 'flag',
      slots: { customRender: 'flag' },
      fixed: 'left',
      width: 80,
    },
    {
      title: '科目级次',
      dataIndex: 'igrade',
      fixed: 'left',
      width: 100,
    },
    {
      title: '科目编码',
      align: 'left',
      dataIndex: 'ccode',
      slots: { customRender: 'ccode' },
      fixed: 'left',
      width: 250,
    },
    {
      title: '科目名称',
      dataIndex: 'ccodeName',
      align: 'left',
      slots: { customRender: 'ccodeName' },
      ellipsis: true,
      fixed: 'left',
      width: 300,
    },
    {
      title: '科目类型',
      dataIndex: 'cclass',
    },
    {
      title: '方向',
      dataIndex: 'bprogerty',
      slots: { customRender: 'bprogerty' },
      width: 80,
    },
    {
      title: '辅助核算项',
      dataIndex: 'fuzhu',
    },
    {
      title: '数量核算',
      dataIndex: 'bnum',
      slots: { customRender: 'bnum' },
      width: 100,
    },
    {
      title: '计量单位',
      dataIndex: 'menterage',
      width: 100,
    },
    {
      title: '外币核算',
      dataIndex: 'currency',
      slots: { customRender: 'currency' },
      width: 100,
    },
    {
      title: '外币名称',
      dataIndex: 'currencyType',
      slots: { customRender: 'currencyType' },
      width: 100,
    },
    {
      title: '现金科目',
      dataIndex: 'bcash',
      slots: { customRender: 'bcash' },
      width: 100,
    },
    {
      title: '银行科目',
      dataIndex: 'bbank',
      slots: { customRender: 'bbank' },
      width: 100,
    },
    {
      title: '核算控制',
      dataIndex: 'fuzhuControl',
      slots: { customRender: 'fuzhuControl' },
      width: 100,
    },
    {
      title: '下级科目控制',
      dataIndex: 'lowerControl',
      slots: { customRender: 'lowerControl' },
    },
    {
      title: '管理类型',
      dataIndex: 'yusuan',
      slots: { customRender: 'yusuan' },
    },
  ];
  const bianjiMSG = ref('编辑');


  // 增加操作日志：1、修改2、删除3、停用、4查看、5导入、6导出、7打印
  const logmap = ref({
    accId: getCurrentAccountName(true),
    username: useUserStoreWidthOut().getUserInfo.username,
    flag: '3',
    text: '',
  });

  // 这是示例组件
  const [registerTable, { reload, getSelectRows, getDataSource,clearSelectedRowKeys }] = useTable({});
  const [registerEditPage, { openModal: openEditPage }] = useModal();
  const [registerSavePage, { openModal: openSavePage }] = useModal();
  const [registerExcelPage, { openModal: openExcelPage }] = useModal();
  const [registerCopyPage, { openModal: openCopyPage }] = useModal();
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useTabs } from '/@/hooks/web/useTabs';

  /******************* 弹框加载中 **************************/
  import { Loading } from '/@/components/Loading';
  import { aoaToSheetXlsx } from '/@/components/Excel';
  import { findAllByAccGroup } from '/@/api/record/system/account';
  import { useRouteApi } from '/@/utils/boozsoft/datasource/datasourceUtil';
  import {
    findByFunctionModuleAndIyearAndRecordNum,
    groupTaskDelByUserIdAndOrgUniqueAndFunctionModule,
    groupTaskSave
  } from "/@/api/record/group/group-task";
  import {saveLog} from "/@/api/record/system/group-sys-login-log";
  const loadingRef = ref(false);
  const compState = reactive({
    absolute: false,
    loading: false,
    tip: '加载中...',
  });
  function openCompFullLoading() {
    openLoading(false);
  }
  function openLoading(absolute: boolean) {
    compState.absolute = absolute;
    compState.loading = true;
  }
  /*******************END**************************/

  async function bianjiBtn(txt) {
    if(txt=='返回'){
      // 是否存在组织会计科目任务
      let task=await findByFunctionModuleAndIyearAndRecordNum('组织会计科目',orgIyear.value,orientation.value)
      if(task==''){
        // 添加任务
        let map={
          caozuoUnique:useUserStoreWidthOut().getUserInfo.id,
          functionModule:'组织会计科目',
          recordNum:orientation.value,
          iyear:orgIyear.value,
          method:'删除',
        }
        await groupTaskSave(map)
        bianjiMSG.value=txt
      }else{
        // 任务操作人员是否当前操作员
        if(task.caozuoUnique==useUserStoreWidthOut().getUserInfo.id){
          // 修改任务时间
          let map={
            id:task.id,
            caozuoUnique:useUserStoreWidthOut().getUserInfo.id,
            functionModule:'组织会计科目',
            recordNum:orientation.value,
            iyear:orgIyear.value,
            method:'删除',
          }
          await groupTaskSave(map)
          bianjiMSG.value=txt
        }else{
          return createWarningModal({title: '温馨提示', content: task.username+' 操作员正在操作组织会计科目,不能同时操作！'})
        }
      }
    }
    // 删除任务
    else{
      bianjiMSG.value=txt
      await groupTaskDelByUserIdAndOrgUniqueAndFunctionModule('组织会计科目',orgIyear.value,orientation.value,useUserStoreWidthOut().getUserInfo.id)
    }
  }

  const tabsCheck = async (data: any) => {
    activeKey.value = data;
    findAllData();
  };

  const showPaginationText=ref(false)
  let a = 0;
  const findAllData = async () => {
    a++;
    loading.value = true;
    tableDataAll.value = [];
    // 组织列表
    const oration = await getOrganizeList('');
    orientationlist.value = oration.items;
    if (uniqueAccStandard.value === undefined) {
      let findByTemplate = await findByTOrganization(orientationlist.value[0].uniqueCode);
      uniqueAccStandard.value = findByTemplate[0].uniqueAccStandard;
      templateId.value = findByTemplate[0].id;
      orientation.value = orientationlist.value[0].uniqueCode;
    }
    // 组织信息表
    let org = await findByUniqueCode(orientation.value);
    // 组织期间表
    orgIyearList.value = await groupByOrgPeriodIyear(org.uniqueCode);
    orgIyear.value = orgIyearList.value[0];
    orgCreateDate.value = org.createDate;
    tjici.value = org.ccodeLevel;
    tableDataAll.value = await findAllByOrgCodeKeMu(
      activeKey.value,
      orgIyear.value,
      org.uniqueCode,
    );
    tableData.value = tableDataAll.value;
    loading.value = false;
    compState.loading = false;
    showPaginationText.value=true
  };

  async function standardCheck() {
    openCompFullLoading();
    loading.value = true;
    tableDataAll.value = [];
    tableData.value = [];
    uniqueAccStandard.value = '';
    templateId.value = '';
    styleNamelist.value = [];
    styleNamelist2.value = [];
    styleNamelist.value.push('全部');
    activeKey.value = '全部';
    let template = await findByTOrganization(orientation.value);
    if (template.length > 0) {
      uniqueAccStandard.value = template[0].uniqueAccStandard;
      templateId.value = template[0].id;
      // 查询会计准则
      let accStyleUnique = await findByStandardUnique(template[0].uniqueAccStandard);
      accStyleUnique.value = accStyleUnique.accStyleUnique;
      // 获取 科目类型
      let accStyle = await findAllStyleByUnique(accStyleUnique.accStyleUnique);
      accStyle.forEach((v) => {
        styleNamelist.value.push(v.cclass);

        styleNamelist2.value.push({
          cclass: v.cclass,
          flagYusuan: v.flagYusuan,
          order: v.order,
        });
      });
      findAllData();
    }
    loading.value = false;
  }
  onMounted(async () => {
    tableRef.value.$el.style.setProperty('width', '1800px');
    findAllData();
    setTimeout(() => {
      findByAccStyle();
    }, 500);
  });

  function findByOrgYear() {}

  async function findByAccStyle() {
    let accstandard = await findByStandardUnique(uniqueAccStandard.value);
    // 重新获取科目类型
    let accstyle = await findAllStyleByUnique(accstandard.accStyleUnique);
    styleNamelist.value = [];
    styleNamelist2.value = [];
    activeKey.value = '全部';
    styleNamelist.value.push('全部');
    for (let i = 0; i < accstyle.length; i++) {
      styleNamelist.value.push(i + 1 + '.' + accstyle[i].cclass);

      styleNamelist2.value.push({
        cclass: accstyle[i].cclass,
        flagYusuan: accstyle[i].flagYusuan,
        order: accstyle[i].order,
      });
    }
  }

  // input检索
  const inputsearch = () => {
    let all = tableDataAll.value;
    if (selectsearch.value === 'ccode') {
      tableData.value = all.filter((a) => a.ccode.indexOf(inputsearchdata.value) !== -1);
    }
    if (selectsearch.value === 'ccodeName') {
      tableData.value = all.filter((a) => a.ccodeName.indexOf(inputsearchdata.value) !== -1);
    }
    if (selectsearch.value === 'bprogerty') {
      tableData.value = all.filter((a) => a.bprogerty.indexOf(inputsearchdata.value) !== -1);
    }
    if (selectsearch.value === 'cclass') {
      tableData.value = all.filter((a) => a.cclass.indexOf(inputsearchdata.value) !== -1);
    }
  };

  const openAddPage = async () => {
   let accountKemuTask= await findByAccountCodeKemuTask(orientation.value,'会计科目')
    if(accountKemuTask.length>0){
      return createWarningModal({title: '温馨提示', content: '当前组织下【'+accountKemuTask+'】账套正在进行会计科目编辑操作，任务互斥，不能进行新增下级科目操作'})
    }

    openSavePage(true, {
      data: {
        jici: tjici.value,
        styleName: styleNamelist2.value,
        uniqueAccStandard: uniqueAccStandard.value,
        templateId: templateId.value,
        iyear: orgIyear.value,
      },
      isEdit: true,
      type: 'zz',
      orientation: orientation.value,
    });
  };
  // 行修改
  const RowOpenEdit = async (data) => {
    let accountKemuTask= await findByAccountCodeKemuTask(orientation.value,'会计科目')
    if(accountKemuTask.length>0){
      return createWarningModal({title: '温馨提示', content: '当前组织下【'+accountKemuTask+'】账套正在进行会计科目编辑操作，任务互斥，不能进行新增下级科目操作'})
    }

    openEditPage(true, {
      data: {
        jici: tjici.value,
        styleName: styleNamelist2.value,
        uniqueAccStandard: uniqueAccStandard.value,
        templateId: templateId.value,
        data: data,
        iyear: orgIyear.value,
      },
      type: 'zz',
      isEdit: true,
      orientation: orientation.value,
    });
  };
  // 选中修改
  const ClickOpenEdit = async () => {
    let accountKemuTask= await findByAccountCodeKemuTask(orientation.value,'会计科目')
    if(accountKemuTask.length>0){
      return createWarningModal({title: '温馨提示', content: '当前会计科目已经在组织下【'+accountKemuTask+'】账套中使用，不能修改该会计科目！'})
    }

    if (getSelectRows().length > 1 || getSelectRows().length === 0) {
      message.error('只能选择一条进行编辑！');
      return false;
    }
    openEditPage(true, {
      data: {
        jici: tjici.value,
        styleName: styleNamelist2.value,
        uniqueAccStandard: uniqueAccStandard.value,
        templateId: templateId.value,
        data: getSelectRows()[0],
        iyear: orgIyear.value,
      },
      type: 'zz',
      isEdit: true,
      orientation: orientation.value,
    });
  };
  const delCode = async () => {
    if (getSelectRows().length == 0) {
      return createWarningModal({title: '温馨提示', content: '至少选择一条！'})
    }
    let bend0=getSelectRows().filter(a=>a.bend=='0').map(a=>a.ccode)
    if (bend0.length > 0) {
      return createWarningModal({title: '温馨提示', content: bend0+'非末级科目不能删除！'})
    }

    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据不能恢复，你确认要删除吗？',
      onOk: async () => {
        let orgInfo=await findByUniqueCode(orientation.value)
        for (let i = 0; i < getSelectRows().length; i++) {
          let id=getSelectRows()[i].id
          let ccode=getSelectRows()[i].ccode
          let ccodeName=getSelectRows()[i].ccodeName
          let orgAccKeMu=await findByOrgAccountCodeKemu(orientation.value,orgIyear.value,ccode)
          if(orgAccKeMu.length>0){
           return createWarningModal({title: '温馨提示', content: '当前会计科目已经在【'+orgAccKeMu+'】账套中使用,不能进行删除'})
            break
          }else{
            // 查询之前任务是否存在
            if(await findByOrgTask()){
              await delOrgCodekemu(id, orgIyear.value, orientation.value).then(async ()=>{
                findAllData();
                // 埋点-操作日志
                let log='操作内容【删除会计科目】,组织代码【'+orgInfo.orgCode+'】,组织名称【'+orgInfo.orgName+'】,科目编码【'+ccode+'】,科目名称【'+ccodeName+'】'
                let logtime=new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," ")
                /************** 记录操作日志 ****************/
                let map={
                  loginTime:logtime,
                  userId: useUserStoreWidthOut().getUserInfo.username,
                  userName: useUserStoreWidthOut().getUserInfo.realName,
                  optModule:'org',
                  optFunction:'会计科目',
                  optRange:'0',
                  uniqueCode:orgIyear.value,
                  optAction:'删除',
                  optContent:log,
                }
                await saveLog(map)
                /************** 记录操作日志 END ****************/
              })
            }
          }
        }
      },
    })
  };

 async function findByOrgTask(){
   // 是否存在组织会计科目任务
   let task=await findByFunctionModuleAndIyearAndRecordNum('组织会计科目',orgIyear.value,orientation.value)
   if(task==''){
     createWarningModal({title: '温馨提示', content: '当前任务已失效,请重新进行编辑!!'})
     return false
   }else if(parseInt(task.caozuoUnique)!==parseInt(useUserStoreWidthOut().getUserInfo.id)){
     createWarningModal({title: '温馨提示', content: '当前任务已失效,请重新进行编辑!!'})
     return false
   }
   return true
  }

  const saveData = async (data: any) => {
    let orgInfo=await findByUniqueCode(orientation.value)
    // 查询之前任务是否存在
    if(await findByOrgTask()){
      data.iyear = orgCreateDate.value.split('-')[0];
      await saveOrgCodekemu(data).then(async (a)=>{
        // 埋点-操作日志
        let log='操作内容【新增会计科目】,组织代码【'+orgInfo.orgCode+'】,组织名称【'+orgInfo.orgName+'】,科目编码【'+a.ccode+'】,科目名称【'+a.ccodeName+'】'
        let logtime=new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," ")
        /************** 记录操作日志 ****************/
        let map={
          loginTime:logtime,
          userId: useUserStoreWidthOut().getUserInfo.username,
          userName: useUserStoreWidthOut().getUserInfo.realName,
          optModule:'org',
          optFunction:'会计科目',
          optRange:'0',
          uniqueCode:orgIyear.value,
          optAction:'新增',
          optContent:log,
        }
        await saveLog(map)
        /************** 记录操作日志 END ****************/
        clearSelectedRowKeys()
        findAllData();
       return createSuccessModal({title: '温馨提示', content: '会计科目添加成功!!'})
      })
    }
  };
  const editData = async (data: any) => {
    let orgInfo=await findByUniqueCode(orientation.value)
    let orgDatabase = await findAllByAccGroup(orientation.value);
    // 组织下的账套
    orgDatabase.forEach(async (v) => {
      // 账套下的科目
      let dataBaseCode = await useRouteApi(company_findByIyearAndCcode, {
        schemaName: v.accId+'-'+orgIyear.value,
      })({iyear: orgIyear.value,ccode: data.ccode});
      if(dataBaseCode!==''){
        // 替换账套科目
        data.id=dataBaseCode.id
        data.uniqueCode=dataBaseCode.uniqueCode
        data.isDel='0'
        data.delName=''
        data.delDate=''
        await useRouteApi(company_AccountKeMuReplaceOrgKeMu, {
          schemaName: v.accId+'-'+orgIyear.value,
        })(data)
      }
    });

    data.iyear=orgCreateDate.value.split('-')[0]
    await saveOrgCodekemu(data).then(async ()=>{
      // 埋点-操作日志
      let log='操作内容【修改会计科目】,组织代码【'+orgInfo.orgCode+'】,组织名称【'+orgInfo.orgName+'】,科目编码【'+data.ccode+'】,科目名称【'+data.ccodeName+'】'
      let logtime=new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," ")
      /************** 记录操作日志 ****************/
      let map={
        loginTime:logtime,
        userId: useUserStoreWidthOut().getUserInfo.username,
        userName: useUserStoreWidthOut().getUserInfo.realName,
        optModule:'org',
        optFunction:'会计科目',
        optRange:'0',
        uniqueCode:orgIyear.value,
        optAction:'修改',
        optContent:log,
      }
      await saveLog(map)
      /************** 记录操作日志 END ****************/
    })
    findAllData()
    clearSelectedRowKeys()
  };

  function createConfirmPop(text) {
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: text,
      onOk: async () => {},
    });
  }

  async function handleDownExcel() {
    const data = getDataSource();
    const dataArr: any = [];
    data.forEach((v) => {
      let temp: any = [];
      temp[0] = v.ccode;
      temp[1] = v.ccodeName;
      temp[2] = v.cclass;
      temp[3] = v.bprogerty === '1' ? '借' : '贷';
      temp[4] = v.fuzhu;
      temp[5] = v.bnum === '1' ? '是' : '否';
      temp[6] = v.menterage;
      temp[7] = v.currency === '1' ? '是' : '否';
      temp[8] = v.currencyType;
      temp[9] = v.bcash === '1' ? '是' : '否';
      temp[10] = v.bbank === '1' ? '是' : '否';
      temp[11] = v.fuzhuControl === '1' ? '是' : '否';
      temp[12] = v.lowerControl === '1' ? '是' : '否';
      temp[13] = v.yusuan === '1' ? '预算类会计科目' : '财务类会计科目';
      dataArr.push(temp);
    });

    aoaToSheetXlsx({
      data: dataArr,
      header: [
        '科目编码',
        '科目名称',
        '科目类型',
        '方向',
        '辅助核算项',
        '数量核算',
        '计量单位',
        '外币核算',
        '外币名称',
        '现金科目',
        '银行科目',
        '核算控制',
        '下级科目控制',
        '管理类型',
      ],
      filename: '组织会计科目.xlsx',
    });
  }
  const openImportExcel = () => {
    openExcelPage(true, {
      data: {
        uniqueAccStandard: uniqueAccStandard.value,
        templateId: templateId.value,
        accStyleUnique: accStyleUnique.value,
        iyear: new Date().getFullYear(),
        jici: tjici.value,
      },
      type: 'zz',
      iyear: orgCreateDate.value.split('-')[0],
      orientation:orientation.value
    });
  };
  async function copyCodeView() {
    openCopyPage(true, {
      uniqueAccStandard: uniqueAccStandard.value,
      templateId: templateId.value,
    });
  }
  function addspace(igrade, str) {
    var space = '';
    for (let i = 1; i < igrade; i++) {
      space += '\xa0\xa0\xa0\xa0';
    }
    return space + str;
  }
</script>
<style src="./global-menu-index.less" lang="less" scoped></style>
<style scoped>
:deep(.vben-basic-table) .ant-pagination {
  background-color: #cccccc;
  padding-right: 20px;
  padding-top: 5px;
  padding-bottom: 5px;
}

.app-container:nth-of-type(1) {
  background-color: #f2f2f2;
  padding: 10px 5px;
  margin: 10px 10px 5px;
}
/*第二段背景色长度*/
.app-container:nth-of-type(2) {
  background-color: #f2f2f2;
  padding: 0px;
  margin: 5px 10px;
  height: 660px;
  position: relative;
  .pagination-text {
    position: absolute;
    bottom: 6px;
    right: 11%;
    top: 623px;
    font-size: 13px;
    color: black;
    z-index: 99999999;
  }
}

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

.tableUStyle {
  color: #0096c7;
  cursor: pointer;
  text-decoration: none;
}
.tableUStyle:hover {
  color: red;
}
:deep(.ant-table-thead) th {
  text-align: center !important;
  font-weight: bold;
  background-color: #f2f2f2 !important;
  border-color: #e5e7eb !important;
}
</style>
