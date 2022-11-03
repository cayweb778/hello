<template>
  <div>
    <Loading :loading="compState.loading" :absolute="compState.absolute" :tip="compState.tip" />
    <div class="app-container">
      <div class="app-container-head">
        <div class="container-head-title" style="float: left;margin-top: 10px;">
          <b class="noneSpan" style="font-size: 60px;">
            <ProfileOutlined/>
          </b>
        </div>
        <div class="container-head-title"
             style="padding-left: 35%;text-align: center;margin-top: 30px;">
          <b class="noneSpan" style="font-size: 36px;">会计科目（国家地区）</b>
        </div>
        <div class="ant-btn-group" style="float: right;">
          <button type="button" class="ant-btn ant-btn-me" @click="openImportExcel()"><span>导入</span></button>
          <a-popconfirm placement="bottom" title="确认清空科目吗？" @confirm="delAllCode">
            <button type="button" class="ant-btn ant-btn-me"><span>清空</span></button>
          </a-popconfirm>
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent(),router.push('/system/home/welcome')"><span>退出</span></button>
        </div>
      </div>
      <div style="clear: none"/>
      <div style="margin-top: -30px;margin-left: 80px;">
        <div style="display: inline-block;float: left;margin-top: -25px;">
          &emsp;<label>会计准则：</label>
          <a-select
            v-model:value="standardSelected"
            option-filter-prop="children"
            show-search
            @change="standardCheck"
          >
            <a-select-option
              v-for="item in accStandardList"
              :key="item.uniqueAccStandard"
              :value="item.uniqueAccStandard + '_' + item.accStyleUnique + 'BOOZ' + item.codeFirst"
            >
              {{ item.accStandardName }}
            </a-select-option>
          </a-select>
        </div>
        <div
          style="display: inline-block;position:absolute;top:85px;left:110px;font-size: 10px;color:#999999;">
          共 {{ tableData.length }} 条记录
        </div>
        <div style="float: right; margin-left: 10px;">
          <a-button @click="findAllData">
            <SyncOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
        </div>
        <div style="float: right; position: relative">
          <a-select v-model:value="pageParameter.searchConditon.requirement" style="width: 130px;" class="special_select">
            <template v-for="item in searchConditonList">
              <a-select-option :value="item.dataIndex">
                {{ item.title }}
              </a-select-option>
            </template>
          </a-select>
          <a-input-search placeholder="" style="width: 200px; border-radius: 4px" v-model:value="inputsearchdata" @search="inputsearch"/>
        </div>
      </div>
      <div style="clear:both"/>
    </div>
    <div class="app-container">
      <div style="float: left; width: 104px">
        <a-tabs @change="tabsCheck" tab-position="left">
          <a-tab-pane v-for="(item, i) in styleNamelist" :key="item" :tab="`${item!=='全部'?i+item:item}`" />
        </a-tabs>
      </div>
      <div style="width: calc(100% - 104px); float: right">
        <BasicTable
          ref="tableRef"
          :columns="tableColumns"
          :dataSource="tableData"
          :scroll="{y: windowHeight}"
          :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
          :pagination="{pageSize: 200,simple: true}"
          :showIndexColumn="true"
        >
          <template #yusuan="{ record }">
            {{record.yusuan==='1'?'预算类会计科目':'财务类会计科目'}}
          </template>
          <template #bcash="{ record }">
            <span v-if="record.bcash == '1'">
              <CheckOutlined/>
            </span>
          </template>
          <template #bbank="{ record }">
            <span v-if="record.bbank == '1'">
              <CheckOutlined/>
            </span>
          </template>
          <template #bprogerty="{ record }">
            {{ record.bprogerty == '1' ? '借方' : '贷方' }}
          </template>
          <template #ccodeName="{ record }">
            {{ addspace(record.igrade, record.ccodeName) }}
          </template>
        </BasicTable>
        <div class="pagination-text" :style="{top: (windowHeight+50)+'px'}" v-show="showPaginationText">
          {{`共 ${tableData.length}条记录&emsp;每页 200 条`}}
        </div>
        <CountryImprotExcel @save="saveExcelData" @register="registerExcelPage" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { findAllAcctandardList, findAllStyleByUnique } from '/@/api/accstandard/accstandard';
  import { BasicTable, useTable } from '/@/components/Table';
  import { onMounted, reactive, ref, toRaw } from 'vue';
  import { useRoute } from 'vue-router';
  import {
    Popconfirm as APopconfirm,
    Popover as APopover,
    Tag as ATag,
    Tabs as ATabs,
    Select as ASelect,
    Input as AInput,
    Statistic as AStatistic,
  } from 'ant-design-vue';
  import {
    findAllByUniqueAccStndard,
    findAllByUniqueAccStandardAndTemplateIdAndCclass, findAllByCountryCodeKeMu,
  } from '/@/api/acctemplate/acctemplate';
  import { useUserStoreWidthOut } from '/@/store/modules/user';
  import { CheckOutlined,SyncOutlined,SettingFilled,ProfileOutlined } from '@ant-design/icons-vue';
  import router from "/@/router";
  import CountryImprotExcel from './popup/country_improtExcel.vue';

  const { closeCurrent } =useTabs();
  const ASelectOption = ASelect.Option;
  const AInputSearch = AInput.Search;
  const AStatisticCountdown = AStatistic.Countdown;
  const ATabPane = ATabs.TabPane;
  // route.query.data=会计准则唯一码_科目类型唯一码BOOZ科目级次 第一级_科目模板ID

  const route = useRoute();
  const windowHeight = (window.innerHeight - (320))
  const username = ref(useUserStoreWidthOut().getUserInfo.username);
  const tableRef = ref(null);

  const inputsearchdata = ref('');
  // 文件导入
  const fileList = ref([]);
  // table加载
  const loading = ref(true);
  // 模板属于系统还是自定义
  const sysflg = ref(route.query.tType);
  // 会计准则下拉框回显
  const local_reloadData = ref(route.query.data);
  const accStandardList = ref([]);
  const accStyleUnique = ref('');
  const activeKey = ref('全部');
  const standardSelected = ref('');
  const standardName = ref('');
  // 科目模板下拉框
  const templateList = ref([]);
  const templateSelected = ref(route.query.templateId + '_' + route.query.jici);
  const templateId = ref(route.query.templateId);
  const templateName = ref('');
  // tabs默认选中
  const tabsKey = ref('0');
  const jici = ref(route.query.jici);
  // 科目类型
  const styleNamelist = ref([]);

  const tableData = ref([]);
  const tableDataAll = ref([]);
  const tableColumns = [
    {
      title: 'ID',
      dataIndex: 'id',
      defaultHidden: true,
    },
    {
      title: '科目级次',
      align: 'left',
      dataIndex: 'igrade',
      width: 80,
    },
    {
      title: '科目编码',
      align: 'left',
      dataIndex: 'ccode',
      width: 150,
    },
    {
      title: '科目名称',
      dataIndex: 'ccodeName',
      align: 'left',
      width: 250,slots: { customRender: 'ccodeName' },ellipsis: true,
    },
    {
      title: '科目类型',
      dataIndex: 'cclass',
      width: 80,
    },{
      title: '方向',
      dataIndex: 'bprogerty',
      slots: { customRender: 'bprogerty' },
      width: 50,
    },
    {
      title: '现金科目',
      dataIndex: 'bcash',
      width: 80,slots: { customRender: 'bcash' },
    },{
      title: '银行科目',
      dataIndex: 'bbank',
      width: 80,slots: { customRender: 'bbank' },
    },{
      title: '管理类型',
      dataIndex: 'yusuan',
      width: 150,slots: { customRender: 'yusuan' },
    },
  ];
  const searchConditonList=ref([
    {
    title:'科目编码',
    dataIndex:'ccode'
  },{
      title:'科目名称',
      dataIndex:'ccodeName'
    }
  ])
  import {getThisIndexImg} from '/@/api/task-api/tast-bus-api';
  // 这是示例组件
  const [registerTable, { reload }] = useTable({});
  const [registerExcelPage, { openModal: openExcelPage }] = useModal();

  // 页面变量
  const pageParameter = reactive({
    showRulesSize: 'MIN',
    searchConditon: {
      requirement: 'ccode',
      value: '',
    },
  })

  /******************* 弹框加载中 **************************/
  import { Loading } from '/@/components/Loading';
  import {useTabs} from "/@/hooks/web/useTabs";
  import {delAllCountryKeMu} from "/@/api/codekemu/codekemu";
  import {useModal} from "/@/components/Modal";
  const loadingRef = ref(false);
  const compState = reactive({
    absolute: false,
    loading: false,
    tip: '加载中...',
  });

  const addspace = (igrade, str) => {
    var space = '';
    for (let i = 1; i < igrade; i++) {
      space += '\xa0\xa0\xa0\xa0';
    }
    return space + str;
  }

  function openCompFullLoading() {
    openLoading(false);
  }
  function openLoading(absolute: boolean) {
    compState.absolute = absolute;
    compState.loading = true;
  }
  /*******************END**************************/

  const inputsearch = async (a) => {
    let all = tableDataAll.value;
    if(pageParameter.searchConditon.requirement==='ccode'){
      tableData.value = all.filter(
        (a) =>
          a.ccode.indexOf(inputsearchdata.value) !== -1
      );
    }else if(pageParameter.searchConditon.requirement==='ccodeName'){
      tableData.value = all.filter(
        (a) =>
          a.ccodeName.indexOf(inputsearchdata.value) !== -1
      );
    }
  };

  // 会计准则下拉框回调
  const standardCheck = async (data: any, b: any) => {
    openCompFullLoading()
    standardName.value = b.children[0].children;
    loading.value = true;
    local_reloadData.value = data;
    styleNamelist.value = [];
    await findAllData();
  };

  // 查找会计科目  会计准则+科目模板+科目类型
  const findByCode = async () => {
    const a = standardSelected.value.toString().split('_')[0];
    tableDataAll.value = await findAllByCountryCodeKeMu(
      a,
      templateSelected.value.toString().slice('_')[0],
      activeKey.value
    );
    loading.value = false;
  };

  const tabsCheck = async (data: any) => {
    activeKey.value = data;
    tableData.value =
      data === '全部' ? tableDataAll.value : tableDataAll.value.filter((o) => o.cclass === data);
  };

  const showPaginationText=ref(false)
  const findAllData = async () => {
    styleNamelist.value=[]
    tableDataAll.value=[]

    // 会计准则
    const list = await findAllAcctandardList();
    accStandardList.value = list;
    accStyleUnique.value =local_reloadData.value===undefined?list[0].accStyleUnique:local_reloadData.value.toString().split('BOOZ')[0].split('_')[1];
    // 重新获取科目类型
    let accstyle = await findAllStyleByUnique(accStyleUnique.value);
    styleNamelist.value.push('全部');
    for (let i = 0; i < accstyle.length; i++) {
      styleNamelist.value.push(accstyle[i].cclass);
    }
    // 会计准则下拉框回显
    const data: any = local_reloadData.value;
    standardSelected.value = local_reloadData.value===undefined?list[0].uniqueAccStandard+'_'+list[0].accStyleUnique+'BOOZ'+list[0].codeFirst:data;
    // 获取科目模板
    sysflg.value=sysflg.value===undefined?'系统模板':sysflg.value
    const template = await findAllByUniqueAccStndard(
      standardSelected.value.toString().split('_')[0],
      accStyleUnique.value,
      sysflg.value
    );
    jici.value=template.items[0].tjici
    templateId.value=template.items[0].id

    activeKey.value = '全部'
    tableDataAll.value = await findAllByCountryCodeKeMu(
      template.items[0].uniqueAccStandard,
      template.items[0].id,
      activeKey.value
    );
    tableData.value = tableDataAll.value
    showPaginationText.value=true
    loading.value = false;
    compState.loading = false;
  };
  onMounted(async () => {
    openCompFullLoading()
    tableRef.value.$el.style.setProperty('width', '1000px');
    findAllData();
  });

  async function delAllCode() {
    await delAllCountryKeMu(standardSelected.value.split('_')[0],templateId.value).then(item=>{
      findAllData();
    })
  }

  const openImportExcel = () => {
    openExcelPage(true, {
      data: {
        uniqueAccStandard: standardSelected.value.toString().split('_')[0],
        templateId: templateId.value,
        accStyleUnique: accStyleUnique.value,
        iyear: new Date().getFullYear(),
        jici: jici.value,
      },
      type:'gj',
      iyear:''
    });
  };
 async function saveExcelData() {
    findAllData()
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
  height: 1800px;
  position: relative;
  .pagination-text {
    position: absolute;
    bottom: 6px;
    right: 55%;
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
