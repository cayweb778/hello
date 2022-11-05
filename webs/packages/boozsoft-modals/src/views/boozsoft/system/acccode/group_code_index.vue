<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <div class="container-head-title" style="float: left;">
          <b class="noneSpan" style="font-size: 50px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="text-align: center;">
          <b class="noneSpan" style="font-size: 36px;">会计科目</b>
        </div>
        <div class="ant-btn-group" style="float: right;">
          <a-button class="ant-btn ant-btn-me" v-if="sysflg !== '系统模板'" @click="openAddPage()"> 新增 </a-button>
          <a-button class="ant-btn ant-btn-me" @click="openEdit()"> 修改 </a-button>
          <a-button class="ant-btn ant-btn-me" @click="delCode()"> 删除 </a-button>
          <a-button class="ant-btn ant-btn-me" v-if="sysflg !== '系统模板'" @click="openImportExcel()"> 导入 </a-button>
          <button @click="handleDownExcel" type="button" class="ant-btn ant-btn-me"><span>导出</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent(),router.push('/system/home/welcome')"><span>退出</span></button>
        </div>

      </div>
      <div style="margin-left: 80px;">
        <div style="display: inline-block;float: left;margin-top: -25px;">
          &emsp;<label>科目模板：</label>
          <a-select
            v-model:value="templateSelected"
            option-filter-prop="children"
            show-search
            @change="templateCheck"
          >
            <a-select-option
              v-for="item in templateList"
              :key="item.id"
              :value="item.id + '_' + item.tjici"
            >
              {{ item.tname }}
            </a-select-option>
          </a-select>

        </div>
        <div style="display: inline-block;position:absolute;top:85px;left:110px;font-size: 10px;color:#999999;">共 {{ tableData.length }} 条记录</div>
        <div style="display: inline-block;position:absolute;top:85px;left:200px;font-size: 10px;color:#999999;">科目级次：{{ jici }}</div>
        <div style="float: right; margin-left: 10px;">
          <a-button @click="findAllData">
            <SyncOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
          <a-popover placement="bottom">
            <template #content>
            <span @click="editfontsize('MAX')"
                  :style="{backgroundColor: (pageParameter.showRulesSize==='MAX')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;&emsp;<SortDescendingOutlined
              :style="{ fontSize: '14px' }"/>&emsp;大号字体&emsp;<CheckOutlined v-if="pageParameter.showRulesSize==='MAX'"
                                                                            :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
              <span @click="editfontsize('MIN')"
                    :style="{backgroundColor: (pageParameter.showRulesSize==='MIN')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;&emsp;<SortAscendingOutlined
                :style="{ fontSize: '14px' }"/>&emsp;小号字体&emsp;<CheckOutlined v-if="pageParameter.showRulesSize==='MIN'"
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
        </div>
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
          :row-selection="{ type: 'checkbox' ,fixed: true}"
          :columns="tableColumns"
          :dataSource="tableData"
          :loading="loading"
          :scroll="{y: windowHeight }"
          :bordered="true"
          :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
          :pagination="{ pageSize: 200, simple:true}"
          @register="registerTable"
          :showIndexColumn="true"
        >
          <template #yusuan="{ record }">
            {{record.yusuan==='1'?'预算类会计科目':'财务类会计科目'}}
          </template>
          <template #ccode="{ record }">
            <a @click="openLook(record)">{{ record.ccode }}</a>
          </template>
          <template #ccodeName="{ record }">
            <a @click="openLook(record)">{{ addspace(record.igrade, record.ccodeName) }}</a>
          </template>
          <template #bprogerty="{ record }">
            {{ record.bprogerty == '1' ? '借' : '贷' }}
          </template>
          <template #lowerControl="{ record }">
          <span v-if="record.lowerControl == '1'">严格控制</span>
          <span v-else>不控制</span>
          </template>
          <template #fuzhuControl="{ record }">
            <span v-if="record.fuzhuControl == '1'">严格控制</span>
            <span v-else>不控制</span>
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
        <div class="pagination-text" :style="{top: (windowHeight+50)+'px'}" v-show="showPaginationText">
          {{`共 ${tableData.length}条记录&emsp;每页 200 条`}}
        </div>
      </div>
    </div>
    <EditPage @save="editData" @register="registerEditPage" />
    <AddPage @save="saveData" @register="registerSavePage" />
    <ImprotExcel @save="saveExcelData" @register="registerExcelPage" />
  </div>
</template>

<script setup lang="ts">
  import { CheckOutlined } from '@ant-design/icons-vue';
  import {
    findAllAcctandardList,
    findAllStyleByUnique,
    findByStandardUnique
  } from '/@/api/accstandard/accstandard';
  import { BasicTable, useTable } from '/@/components/Table';
  import AddPage from './popup/save.vue';
  import EditPage from './popup/edit.vue';
  import ImprotExcel from './popup/improtExcel.vue';
  import { onMounted, reactive, ref, toRaw } from 'vue';
  import { useModal } from '/@/components/Modal';
  import { useRoute } from 'vue-router';
  import {
    Tabs as ATabs,
    Tag as ATag,
    Select as ASelect,
    Input as AInput,
    Popover as APopover,
    Checkbox as ACheckbox,
    Modal as AModal,
    Upload as AUpload,
    Row as ARow,
    Col as ACol,
    Statistic as AStatistic,
    message,
  } from 'ant-design-vue';
  import router from '/@/router';
  import {
    findAllByUniqueAccStndard,
    findAllByUniqueAccStandardAndTemplateIdAndCclass,
    findAllByTemplateID,
    findByCustomTemplate,
  } from '/@/api/acctemplate/acctemplate';
  import { saveCodekemu, delCodekemu, saveGroupCodeTask } from '/@/api/codekemu/codekemu';

  import {
    ProfileOutlined,
    UnorderedListOutlined,
    SyncOutlined,
    CaretDownFilled,
    FormOutlined,
    DeleteOutlined,
    CheckCircleOutlined,
    CloseCircleOutlined,
    SettingFilled,
    PicLeftOutlined,
    EditFilled,
    PieChartFilled,
    FilterFilled,
  } from '@ant-design/icons-vue';
  import { findAllAccvoucher } from '/@/api/record/system/accvoucher';
  import { findAll } from '/@/api/record/system/currency';
  import { useUserStoreWidthOut } from '/@/store/modules/user';
  import {
    sys_findByFunctionModule,
    sys_delFunctionModule,
  } from '/@/api/subjectInitialBalance/subjectInitialBalance';
  import { savelog } from '/@/api/record/log';
  import { useCompanyOperateStoreWidthOut } from '/@/store/modules/operate-company';
  import {aoaToSheetXlsx, jsonToSheetXlsx} from '/@/components/Excel';
  import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
  import { getThisIndexImg } from "/@/api/task-api/tast-bus-api";
  import {useTabs} from "/@/hooks/web/useTabs";
  import {useMessage} from "/@/hooks/web/useMessage";
  import {exportExcel} from "/@/api/record/generalLedger/excelExport";
  import {saveLog} from "/@/api/record/system/group-sys-login-log";
  const ASelectOption = ASelect.Option;
  const AInputSearch = AInput.Search;
  const AStatisticCountdown = AStatistic.Countdown;
  const ATabPane = ATabs.TabPane;
  // route.query.data=会计准则唯一码_科目类型唯一码BOOZ科目级次 第一级_科目模板ID
  const route = useRoute();
  const { closeCurrent } =useTabs();
  const username = ref(useUserStoreWidthOut().getUserInfo.username);
  const windowHeight = (window.innerHeight - (330))
  const selectsearch = ref('ccode');
  const inputsearchdata = ref('');
  // 文件导入
  const fileList = ref([]);
  // table加载
  const loading = ref(true);
  // 模板属于系统还是自定义
  const sysflg = ref('自定义模板');
  // 会计准则下拉框回显
  const local_reloadData = ref(route.query.data);
  const accStandardList = ref([]);
  const accStyleUnique = ref('');
  const flagyusuan = ref('0');
  const activeKey = ref('全部');
  const standardSelected = ref(0);
  const standardName = ref('');
  // 科目模板下拉框
  const templateList = ref([]);
  const templateSelected = ref(route.query.templateId + '_' + route.query.jici);
  const templateId = ref(route.query.templateId);
  const templateName = ref('');
  const jici = ref(route.query.jici);
  // 科目类型
  const styleNamelist = ref([]);
  const styleNamelist2 = ref([]);
  const databaseYear = ref(useCompanyOperateStoreWidthOut().getLoginDate.split('-')[0])

  const { createConfirm,createWarningModal,createMessage } = useMessage();

  const tableData = ref([]);
  const tableDataAll = ref('');
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
      fixed: 'left',width:80
    },
    {
      title: '科目级次',
      dataIndex: 'igrade',
      fixed: 'left',width:100
    },
    {
      title: '科目编码',
      align: 'left',
      dataIndex: 'ccode',
      slots: { customRender: 'ccode' },fixed: 'left',width:250
    },
    {
      title: '科目名称',
      dataIndex: 'ccodeName',
      align: 'left',
      slots: { customRender: 'ccodeName' },ellipsis: true,fixed: 'left',width:300
    },
    {
      title: '科目类型',
      dataIndex: 'cclass',
    },
    {
      title: '方向',
      dataIndex: 'bprogerty',
      slots: { customRender: 'bprogerty' },width:80
    },
    {
      title: '辅助核算项',
      dataIndex: 'fuzhu',
    },
    {
      title: '数量核算',
      dataIndex: 'bnum',
      slots: { customRender: 'bnum' },width:100
    },
    {
      title: '计量单位',
      dataIndex: 'menterage',width:100
    },
    {
      title: '外币核算',
      dataIndex: 'currency',
      slots: { customRender: 'currency' },width:100
    },
    {
      title: '外币名称',
      dataIndex: 'currencyType',
      slots: { customRender: 'currencyType' },width:100
    },
    {
      title: '现金科目',
      dataIndex: 'bcash',
      slots: { customRender: 'bcash' },width:100
    },
    {
      title: '银行科目',
      dataIndex: 'bbank',
      slots: { customRender: 'bbank' },width:100
    },
    {
      title: '核算控制',
      dataIndex: 'fuzhuControl',
      slots: { customRender: 'fuzhuControl' },width:100
    },{
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
  const tableRef = ref(null);
  const edittext = ref('开始编辑');
  //   显示
  const ibookflg = ref(false);
  // 单元格可编状态
  const roweditflg = ref(false);
  // 任务管理信息
  const taskinfo = ref('');
  // 页面变量
  const pageParameter = reactive({
    cclass: '全部',
    showRulesSize: 'MIN',
    queryMark: 'J',
    searchConditon: {
      requirement: 'ccode',
      value: '',
    },
    uniqueAccStandard: '',
    templateId: '',
    activeKey: '',
    reloadMark: false,
    companyCode: '100',
    companyName: '湖北万亚软件技术有限公司',
    ifUnit: false,
  })
  // 这是示例组件
  const [registerTable, { reload, getSelectRows,getDataSource,clearSelectedRowKeys }] = useTable({});

  const [registerEditPage, { openModal: openEditPage }] = useModal();
  const [registerSavePage, { openModal: openSavePage }] = useModal();
  const [registerExcelPage, { openModal: openExcelPage }] = useModal();

  // 增加操作日志：1、修改2、删除3、停用、4查看、5导入、6导出、7打印
  const logmap = ref({
    accId: getCurrentAccountName(true),
    username: useUserStoreWidthOut().getUserInfo.username,
    flag: '2',
    text: '',
  });

  const editfontsize = (data) => {
    pageParameter.showRulesSize = data
    findAllData()
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
  async function a_openEdit(data) {

  }
  async function handleDownExcel() {
    const tabledata=getDataSource()
    const columns=[]
    const datas=[]
    tableColumns.splice(1).forEach(v=>{
      columns.push(v.title)
    })
    tabledata.forEach(v=>{
      let item1:any = []
      item1[0]=v.flag==='1'?'启用':'停用'
      item1[1]=v.igrade
      item1[2]=v.ccode
      item1[3]=v.ccodeName
      item1[4]=v.cclass
      item1[5]=v.bprogerty==='1'?'借':'贷'
      item1[6]=v.fuzhu
      item1[7]=v.bnum==='1'?'是':''
      item1[8]=v.menterage
      item1[9]=v.currency==='1'?'是':''
      item1[10]=v.currencyType
      item1[12]=v.bcash==='1'?'是':''
      item1[12]=v.bbank==='1'?'是':''
      item1[13]=v.fuzhuControl==='1'?'严格控制':'不控制'
      item1[14]=v.lowerControl==='1'?'严格控制':'不控制'
      item1[15]=v.yusuan==='1'?'预算类会计科目':'财务类会计科目'
      datas.push(item1)
    })
    aoaToSheetXlsx({
      data: datas,
      header: columns,
      filename: '集团会计科目.xlsx',
    });
  }

  async function delCode() {
    if (getSelectRows().length == 0) {
      message.error('至少选择一条！');
      return false;
    }
    let arr = [];
    let codenum = [];
    let codename = [];
    let no_moji = [];//不是末级
    for (let i = 0; i < getSelectRows().length; i++) {
      if(getSelectRows()[i].bend==='0'){
        no_moji.push(getSelectRows()[i].ccode)
        codenum.push(getSelectRows()[i].ccode);
        codename.push(getSelectRows()[i].ccodeName);
      }else{
        arr.push(getSelectRows()[i].id);
        codenum.push(getSelectRows()[i].ccode);
        codename.push(getSelectRows()[i].ccodeName);
      }
    }
    if(no_moji.length>0){
      for (let i = 0; i < no_moji.length; i++) {
       let aa= getDataSource().filter(v=>v.ccode.indexOf(no_moji[i]) != -1)
        if(aa.length>0){
          for (let j = 0; j < aa.length; j++) {
            arr.push(aa[j].id)
          }
        }
      }
    }

    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据不能恢复，你确认要删除吗？',
      onOk: async() => {
        await delCodekemu(
          Array.from(new Set(arr)).join(','),
          standardSelected.value.split('_')[0],
          templateSelected.value.split('_')[0]
        );
        // 埋点-操作日志
        for (let i = 0; i < Array.from(new Set(arr)).length; i++) {
          // 埋点-操作日志
          let log='操作内容【删除会计科目】,科目编码【'+Array.from(new Set(arr))[i]+'】,科目名称【'+Array.from(new Set(codename))[i]+'】'
          let logtime=new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," ")
          /************** 记录操作日志 ****************/
          let map={
            loginTime:logtime,
            userId: useUserStoreWidthOut().getUserInfo.username,
            userName: useUserStoreWidthOut().getUserInfo.realName,
            optModule:'group',
            optFunction:'会计科目',
            optRange:'2',
            optAction:'删除',
            optContent:log,
          }
          await saveLog(map)
          /************** 记录操作日志 END ****************/
        }
        let log='【删除了'+Array.from(new Set(arr)).length+'条会计科目】,科目编码【'+Array.from(new Set(arr)).join(',')+'】,科目名称【'+Array.from(new Set(codename)).join(',')+'】'
        console.log(log)
        clearSelectedRowKeys()
        findAllData();
      }
    })
  }
  async function editflg(data) {
    if (data === '开始编辑') {
      var aa: any = true;
      const task1 = await sys_findByFunctionModule(new Date().getFullYear());
      if (task1.length === 0) {
        aa = true;
      } else {
        const task2 = task1.filter((t) => t.functionModule === '集团会计科目');
        if (task2.length > 0) {
          for (let i = 0; i < task2.length; i++) {
            if (task2[i].caozuoUnique !== null && task2[i].caozuoUnique !== username.value) {
              var obj = JSON.parse(task2[i].recordNum);
              if (
                obj.accstandard === standardSelected.value.split('_')[0] &&
                obj.templateid === templateSelected.value.split('_')[0]
              ) {
                message.error(
                  '提示：任务冲突！操作员【' +
                    task2[i].caozuoUnique +
                    '】正在进行编辑操作，不能同时进行编辑，请销后再试，或联系主管清理该任务'
                );
                aa = false;
              }
            } else {
              taskinfo.value = task2[0];
            }
          }
        }
      }
      if (aa) {
        await saveGroupCodeTask(
          taskinfo.value === '' ? null : taskinfo.value.id,
          username.value,
          standardSelected.value.split('_')[0],
          templateSelected.value.split('_')[0]
        );
        edittext.value = data === '开始编辑' ? '退出编辑' : '开始编辑';
        roweditflg.value = true;
      }
    } else {
      await sys_delFunctionModule(taskinfo.value.id);
      taskinfo.value = '';
      edittext.value = data === '退出编辑' ? '开始编辑' : '退出编辑';
      roweditflg.value = false;
    }
  }
  const openAddPage = () => {
    const val = {
      standardName: standardName.value,
      uniqueAccStandard: standardSelected.value.toString().split('_')[0],
      templateId: templateSelected.value.split('_')[0],
      jici: jici.value,
      styleName: styleNamelist2.value,
      templateName: templateName.value,
    };
    openSavePage(true, {
      data: val,
      ccodeInfo:'',
      all:'yes',
      ibudgetAccounting:'0',
      isEdit:true
    });
  };
  async function openEdit(flag) {
    if (getSelectRows().length > 1 || getSelectRows().length === 0) {
      message.error('只能选择一条进行编辑！');
      return false;
    }
    const val = {
      standardName: standardName.value,
      uniqueAccStandard: standardSelected.value.toString().split('_')[0],
      templateId: templateSelected.value.split('_')[0],
      jici: jici.value,
      styleName: styleNamelist2.value,
      templateName: templateName.value,
      data: getSelectRows()[0],
    };
    openEditPage(true, {
      data: val,
      type: 'zz',
      ccodeInfo:'',
      all:'yes',
      isEdit:true
    });
  }
  const openLook = (data) => {
    const val = {
      standardName: standardName.value,
      uniqueAccStandard: standardSelected.value.toString().split('_')[0],
      templateId: templateSelected.value.split('_')[0],
      jici: jici.value,
      styleName: styleNamelist2.value,
      templateName: templateName.value,
      data: data,
    };
    openEditPage(true, {
      data: val,
      type: 'zz',
      ccodeInfo:'',
      all:'yes',
      isEdit:false
    });
  }
  const openImportExcel = () => {
    openExcelPage(true, {
      data: {
        uniqueAccStandard: standardSelected.value.toString().split('_')[0],
        templateId: templateSelected.value.split('_')[0],
        accStyleUnique: accStyleUnique.value,
        iyear: new Date().getFullYear(),
        jici: jici.value,
      },
      type:'jt',
      iyear:''
    });
  };
  function local_reload() {
    reload({
      searchInfo: {
        str: route.query.data,
      },
    });
    loading.value = false;
  }

  // 会计准则下拉框回调
  const standardCheck = async (data: any, b: any) => {
    local_reloadData.value = data;
    styleNamelist.value = [];
    styleNamelist2.value = [];
    await findAllData();
  };

  const templateCheck = async (data: any) => {
    loading.value = true;
    jici.value = data.split('_')[1];
    let templateInfo= await findAllByTemplateID(data.split('_')[0])
    let accstandard=await findByStandardUnique(templateInfo.uniqueAccStandard)
    accStyleUnique.value=accstandard.accStyleUnique
    // 重新获取科目类型
    let accstyle = await findAllStyleByUnique(accstandard.accStyleUnique);
    console.log(accstyle)
    styleNamelist.value=[]
    styleNamelist2.value=[]
    activeKey.value='全部'
    styleNamelist.value.push('全部');
    for (let i = 0; i < accstyle.length; i++) {
      styleNamelist.value.push(i + 1 + '.' + accstyle[i].cclass);

      styleNamelist2.value.push({
        cclass: accstyle[i].cclass,
        flagYusuan: accstyle[i].flagYusuan,
        order: accstyle[i].order
      });
    }

    tableDataAll.value = await findAllByUniqueAccStandardAndTemplateIdAndCclass(
      templateInfo.uniqueAccStandard,
      templateInfo.id,
      '全部'
    );

    tableData.value = tableDataAll.value;
    loading.value = false;
    sysflg.value = templateInfo.ttype;
    accStyleUnique.value = data.split('_')[0];
  };
  const tabsCheck = async (data: any) => {
    loading.value = true;
    activeKey.value = data;
    findAllData();
  };
  const saveData = async (data: any) => {
    clearSelectedRowKeys()
    await saveCodekemu(data, username.value).then(async ()=>{
      // 埋点-操作日志
      let log='操作内容【新增会计科目】,科目编码【'+data.ccode+'】,科目名称【'+data.ccodeName+'】'
      let logtime=new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," ")
      /************** 记录操作日志 ****************/
      let map={
        loginTime:logtime,
        userId: useUserStoreWidthOut().getUserInfo.username,
        userName: useUserStoreWidthOut().getUserInfo.realName,
        optModule:'group',
        optFunction:'会计科目',
        optRange:'2',
        optAction:'新增',
        optContent:log,
      }
      await saveLog(map)
      /************** 记录操作日志 END ****************/
    })

    if(data.closeflag==='add'){
      openAddPage()
      findAllData();
    }else{
      findAllData();
    }
  };
  const editData = async (data: any) => {
    await saveCodekemu(data, username.value).then(async ()=>{
      // 埋点-操作日志
      let log='操作内容【修改会计科目】,科目编码【'+data.ccode+'】,科目名称【'+data.ccodeName+'】'
      let logtime=new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," ")
      /************** 记录操作日志 ****************/
      let map={
        loginTime:logtime,
        userId: useUserStoreWidthOut().getUserInfo.username,
        userName: useUserStoreWidthOut().getUserInfo.realName,
        optModule:'group',
        optFunction:'会计科目',
        optRange:'2',
        optAction:'新增',
        optContent:log,
      }
      await saveLog(map)
      /************** 记录操作日志 END ****************/
    })
    clearSelectedRowKeys()
    findAllData();
  };
  // 科目分类
  async function typeTab() {
    styleNamelist.value = [];
    styleNamelist2.value = [];
    // 会计准则
    const list = await findAllAcctandardList();
    accStandardList.value = list;
    if(local_reloadData.value===undefined){
      local_reloadData.value=list[0].uniqueAccStandard+'_'+list[0].accStyleUnique+'BOOZ'+list[0].codeFirst
    }
    accStyleUnique.value = local_reloadData.value.toString().split('BOOZ')[0].split('_')[1];

    // 新增需要显示所在的会计准则
    standardName.value = list.filter(
      (acc) => acc.uniqueAccStandard === parseInt(local_reloadData.value.split('_')[0])
    )[0].accStandardName;

    standardSelected.value = local_reloadData.value;
    // 重新获取科目类型
    let accstyle = await findAllStyleByUnique(accStyleUnique.value);
    styleNamelist.value.push('全部');
    for (let i = 0; i < accstyle.length; i++) {
      styleNamelist.value.push(i + 1 + '.' + accstyle[i].cclass);

      styleNamelist2.value.push({
        cclass: accstyle[i].cclass,
        flagYusuan: accstyle[i].flagYusuan,
        order: accstyle[i].order
      });
    }
  }

  const showPaginationText=ref(false)
  const findAllData = async () => {
    loading.value = true;
    // 获取科目模板
    let template = await findAllByUniqueAccStndard(
      standardSelected.value.toString().split('_')[0],
      accStyleUnique.value,
      sysflg.value
    );
    templateId.value=templateId.value===undefined?template.items[0].id:templateId.value
    template=template.items.filter(t=>t.id===templateId.value)
    if (template.length > 0) {
      // 单个
      // templateList.value = template.items;
      // 所有
      let temp=await findByCustomTemplate('自定义模板')
      templateList.value = temp.items
      templateName.value = template[0].tname;
      jici.value = template[0].tjici;

      templateId.value=template[0].id
      // 科目模板回显
      templateSelected.value = templateId.value + '_' + template[0].tjici;
      const uniqueAccStandard = local_reloadData.value.split('BOOZ')[0].split('_')[0];
      tableDataAll.value = await findAllByUniqueAccStandardAndTemplateIdAndCclass(
        uniqueAccStandard,
        templateId.value,
        activeKey.value
      );
      tableData.value = tableDataAll.value;
    } else {
      tableData.value = [];
      templateList.value = [];
      templateSelected.value = '';
      styleNamelist.value = [];
      styleNamelist2.value = [];
      jici.value = '';
    }
    loading.value = false;
    showPaginationText.value = true;
  };
  async function saveExcelData() {
    styleNamelist.value = [];
    styleNamelist2.value = [];
   await findAllData();
  }
  function addspace(igrade, str) {
    var space = '';
    for (let i = 1; i < igrade; i++) {
      space += '\xa0\xa0\xa0\xa0';
    }
    return space + str;
  }
  onMounted(async () => {
    // tableRef.value.$el.style.setProperty('width', '1800px');
    typeTab()
    setTimeout(()=>{
      findAllData();
    },500)
  });
</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>
<style scoped lang="less">
:deep(.vben-basic-table) .ant-pagination {
  background-color: #cccccc;
  padding-right: 20px;
  padding-top: 5px;
  padding-bottom: 5px;
}
.tableUStyle {
  color: #0096c7;
  cursor: pointer;
  text-decoration: none;
}
.tableUStyle:hover {
   color: red;
 }
.app-container:nth-of-type(1) {
  background-color: #f2f2f2;
  margin: 10px 10px 5px;
  height: 100px;
  padding: 5px 5px;
}
.app-container:nth-of-type(2) {
  background-color: #f2f2f2;
  padding: 0px;
  margin: 5px 10px;
  height: 1800px;
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
:deep(.ant-table-thead) th{
  text-align: center !important;
  font-weight: bold;
  background-color: #f2f2f2 !important;
  border-color: #e5e7eb !important;
}
</style>
