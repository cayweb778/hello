<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <div class="container-head-title">
          <UnorderedListOutlined style="color: #0096c7"/>
          <b class="noneSpan">指定现金流量科目</b>
        </div>
        <div class="ant-btn-group" data-v-a1ccd506="" style="float: right">
          <button type="button" class="ant-btn ant-btn-me" @click="save('1')" v-if="pageParameter.flag ==='0'"><span>指定</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="save('0')" v-if="pageParameter.flag==='1'"><span>取消</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent,router.push('/zhongZhang/home/welcome')"><span>退出</span></button>
        </div>
      </div>

      <div class="app-container-neck">
        <div style="float:left;width: 600px;">
          <AccountPicker  theme="one" @reloadTable="dynamicAdReload"/>
        </div>
        <div style="float: right; margin-left: 10px">
          <a-button style="padding: 0px 12px !important; margin-right: 10px" @click="reload">
            <SyncOutlined :style="{ fontSize: '14px' }" />
          </a-button>
        </div>
        <div style="float: right; position: relative">
          <!-- 搜索 -->
          <a-select v-model:value="pageParameter.searchConditon.requirement" style="width: 130px;" class="special_select">
            <template v-for="item in searchConditonList">
              <a-select-option :value="item.value">
                {{ item.title }}
              </a-select-option>
            </template>
          </a-select>
          <a-input-search
            placeholder=""
            v-model:value="pageParameter.searchConditon.value"
            @search="reload"
            style="width: 150px;border-radius: 4px"
          />
        </div>
      </div>
    </div>
    <div class="app-container">
      <div class="bg-white" style="float: left;margin-top: 2px;">
        <a-tabs @change="tabsCheck" tab-position="left" style="padding-left: 50px;">
          <a-tab-pane v-for="(item, i) in typylist" :key="item.value" :tab="`${item.title}`"/>
        </a-tabs>
      </div>

      <div style="width: calc(100% - 184px); float: right;margin-top: 2px;">
        <BasicTable
          ref="tableRef"
          :class="'a-table-font-size-12'"
          :scroll="{ x: windowWidth,y: windowHeight }"
          :bordered="true"
          :row-selection="{ type: 'checkbox',selectedRowKeys: tableSelectedRowKeys, onChange: tableCheck }"
          size="small"
          @register="registerTable"
        >

          <template #ccode="{ record }">
            <span style="float: left;">{{ record.ccode}}</span>
          </template>
          <template #bprogerty="{ record }">
            <span v-if="record.bprogerty==='1'" >借</span>
            <span v-else >贷</span>
          </template>
          <template #ccodeName="{ record }">
            <span style="float: left;">{{ addspace(record.igrade,record.ccodeName) }}</span>
          </template>
        </BasicTable>
      </div>
    </div>
    <a-table v-if="tableshow" :columns="columns" :data-source="data" :row-selection="rowSelection" />
    <a-modal v-model:visible="visibleModel" title="复制平行记账科目" @ok="handleModelOk">
      <br>
      <div style="margin-left: 10%;">
        选择上年区间： <a-select v-model:value="upiyear" style="width: 130px;" class="special_select">
        <a-select-option v-for="item in upiyearList" :value="item.accountYear">
          {{ item.accountYear }}
        </a-select-option>
      </a-select>
      </div>
      <br>
    </a-modal>
  </div>

</template>
<script setup lang="ts">
import router from "/@/router";
import {BasicTable, useTable} from '/@/components/Table';
import {SyncOutlined,} from '@ant-design/icons-vue';
import {
  Checkbox as ACheckbox,
  Table as ATable,
  Modal as AModal,
  Input as AInput,
  Select as ASelect,
  Statistic as AStatistic,
  Tabs as ATabs, message,
} from 'ant-design-vue';
import {
  UnorderedListOutlined
} from '@ant-design/icons-vue'
import {reactive, ref} from 'vue';
import {getCurrentAccountName,getThisIndexImg} from "/@/api/task-api/tast-bus-api";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {useTabs} from "/@/hooks/web/useTabs";
import {
  company_editCodePxjzOrCyfx,
  company_findAllCodePxjzOrCyfxOrderByAsc, company_findByFirstCcodeLength,
} from "/@/api/codekemu/codekemu";

import {findAllKmCashFlow, findAllCodeKm, findPeriod, addKmCashFlow} from "/@/api/record/generalLedger/data";

import {useMessage} from "/@/hooks/web/useMessage";
import {exportExcel} from "/@/api/record/generalLedger/excelExport";
import {findYearByAccount} from "/@/api/record/system/bank-statement";
import {findAllByAccCountPartColumn, findDataBase} from "/@/api/record/system/account";
import {aoaToSheetXlsx} from "/@/components/Excel";
const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const AStatisticCountdown = AStatistic.Countdown;
const ATabPane = ATabs.TabPane;
const database = ref(getCurrentAccountName(true));
const columns = [
  {title: '科目编码',dataIndex: 'ccode',key: 'ccode',slots: { customRender: 'ccode' },width:150},
  {title: '科目名称',dataIndex: 'ccodeName',key: 'ccodeName',slots: { customRender: 'ccodeName' },width:150},
  {title: '科目方向',dataIndex: 'bprogerty',key: 'bprogerty',slots: { customRender: 'bprogerty' },width:40},
  {title: '科目类型',dataIndex: 'cclass',key: 'cclass',width:50},
  {title: '辅助项',dataIndex: 'fuzhu',key: 'fuzhu',width:100}
];
const searchConditonList = ref([
  {title:'科目编码',value: 'ccode'},
  {title:'科目名称',value: 'ccodeName'},
  {title:'科目方向',value: 'bprogerty'},
  {title:'科目类型',value: 'cclass'},
])
const typylist=reactive([
  {title:'已指定科目',value:'1'},
  {title:'待指定科目',value:'0'},

])
const tableshow = ref(false);

const visibleModel = ref(false)
const upiyearList = ref([])
const tableSelectedRowKeys:any = ref([])
const tableSelectedRowObjs:any = ref([])
const upiyear = ref('')
const tableRef = ref(null)
const windowWidth = (document.documentElement.clientWidth - 900)
const windowHeight = (window.innerHeight - (320))
const { closeCurrent } =useTabs();
const { createConfirm,createWarningModal,createMessage } = useMessage();
const pageParameter = reactive({
  searchConditon: {
    value:'',
    requirement:'ccode',
  },
  flag: '1',
  type: 'pxjz',
  companyName: '',
  iyear: '',
  accId: '',
  proItem: '',
})

// 这是示例组件
const [registerTable, { reload,getSelectRows,getDataSource,setTableData,getColumns,clearSelectedRowKeys,deleteSelectRowByKey }] = useTable({
  api: useRouteApi(findAllCodeKm,{schemaName: database}),
  columns: columns,
  isTreeTable: true,
  pagination:{ pageSize: 100,showSizeChanger: true, pageSizeOptions: ['100','500','1000','1500'],showTotal: t => `总共${t}条数据` },
  searchInfo: pageParameter
});
const dynamicAdReload = async (obj) => {
  database.value=obj.accId+'-'+obj.iyear
  pageParameter.companyName=obj.companyName
  pageParameter.iyear=obj.iyear
  pageParameter.accId=obj.accId
  reload()
}
const tabsCheck = (data) => {
  tableSelectedRowObjs.value = [];
  tableSelectedRowKeys.value = [];
  pageParameter.flag=data
  reload()
}

const toSave = async (flag) => {
  // 判断当前账套是否属于政府会计制度
  let temp=await findAllByAccCountPartColumn(pageParameter.accId)
  if(temp.accStyleUnique!=='6'){
    createConfirmPop('当前账套不是政府会计制度,不能进行操作')
    return false;
  }
  if(tableSelectedRowObjs.value.length===0){
    createConfirmPop('至少选择一条会计科目进行操作')
    return false;
  }
  let ccode:any=[]
  let list=getSelectRows()
  list.forEach(v=>{
    ccode.push(v.ccode)
  })
  let map={
    data:tableSelectedRowObjs.value.map(v=>v.ccode).join(','),
    type:'pxjz',
    flag:flag
  }
  await useRouteApi(company_editCodePxjzOrCyfx,{schemaName: database})(map)
    .then(()=>{
      reload()
    })
}
function tableCheck(selectedRowKeys,obj) {
  if(selectedRowKeys.length !=getDataSource().length){
    if (selectedRowKeys.length > tableSelectedRowKeys.value.length) { // 选中
      let b = [...new Set(obj.map(item => item.ccode))]
      // 去重
      let notequal= getArrDifference(selectedRowKeys,tableSelectedRowKeys.value)
      if(notequal.length>0){
        let b=getDataSource().filter(item => item.key==notequal[0])[0].ccode
        let zhuijia= getDataSource().filter(item => item.ccode.startsWith(b))
        zhuijia.forEach(zj=>{
          tableSelectedRowObjs.value.push(zj)
          tableSelectedRowKeys.value.push(zj.key)
        })
      }else{
        tableSelectedRowObjs.value = getDataSource().filter(item => item.ccode.startsWith(b))
        tableSelectedRowKeys.value = tableSelectedRowObjs.value.map(item => item.key)
      }
    }else{  // 取消勾选 先 找到被取消的
      let notequal= getArrDifference(selectedRowKeys,tableSelectedRowKeys.value)
      if(notequal.length>0){
        let temp=getDataSource().filter(item => item.key==notequal[0])[0].ccode
        let zhuijia= getDataSource().filter(item => item.ccode.startsWith(temp)).map(it=>it.ccode)
        tableSelectedRowObjs.value =   tableSelectedRowObjs.value.filter(tt=>zhuijia.indexOf(tt.ccode) == -1)
        tableSelectedRowKeys.value = tableSelectedRowObjs.value.map(item => item.key)
      }
    }
  }else{
    tableSelectedRowKeys.value = selectedRowKeys;
    tableSelectedRowObjs.value=getDataSource().filter(v=>v.key.indexOf(selectedRowKeys))
  }
}
// 两个数组去重
function getArrDifference(arr1, arr2) {
  if(arr2.length==0) return [];
  return arr1.concat(arr2).filter(function(v, i, arr) {
    return arr.indexOf(v) === arr.lastIndexOf(v);
  });
}

function openImportExcelView() {
  let data:any=[]
  getDataSource().forEach(v=>{
    let test:any=[]
    test[0]=v.ccode
    test[1]=v.ccodeName
    test[2]=v.bprogerty==='1'?'借':'贷'
    test[3]=v.cclass
    test[4]=v.fuzhu
    data.push(test)
  })
  aoaToSheetXlsx({
    data: data,
    header: ['科目编码','科目名称','科目方向','科目类型','辅助项'],
    filename: pageParameter.companyName+'-平行记账科目-'+pageParameter.iyear+'.xlsx',
  });
}
function createConfirmPop(text) {
  createConfirm({
    iconType: 'warning',
    title: '警告',
    content: text,
    onOk: async() => {}
  })
}

// 复制上年已选的平行记账科目：编码、名称一致 修改本年科目 平行记账状态
const copyCode = async () => {
  // 判断当前账套是否属于政府会计制度
  let temp=await findAllByAccCountPartColumn(pageParameter.accId)
  if(temp.accStyleUnique!=='6'){
    createConfirmPop('当前账套不是政府会计制度,不能进行操作')
    return false;
  }
  let accperiod=await findYearByAccount(pageParameter.accId)
  let filterperiod =accperiod.filter(v=>parseInt(v.accountYear) < parseInt(pageParameter.iyear) )
  if(filterperiod.length===0){
    createConfirmPop('当前公司没有上年区间,不能复制!')
    return false;
  }else{
    upiyearList.value=filterperiod
    visibleModel.value=true;
  }
}
const handleModelOk = async () => {
  if(upiyear.value===''){
    createConfirmPop('请选择年度')
    return false;
  }
  const dataBase: any = await findDataBase(pageParameter.accId, upiyear.value)
  if(dataBase===''){
    createConfirmPop('没有当前账套,请联系财务管理员')
    return false;
  }else{
    tableshow.value=true
    // 获取上年的末级平行记账科目
    pageParameter.flag='1'
    let upyearCode= await useRouteApi(company_findAllCodePxjzOrCyfxOrderByAsc,{schemaName: dataBase.accountMode})(pageParameter)
    // 获取本年的末级平行记账科目
    pageParameter.flag='0'
    let thisyearCode= await useRouteApi(company_findAllCodePxjzOrCyfxOrderByAsc,{schemaName: database})(pageParameter)
    let list:any = []
    // 双重for循环，时间复杂度：n*n
    for(let i = 0; i < upyearCode.length; i++) {
      let tempArr1 = upyearCode[i]
      for(let j = 0; j < thisyearCode.length; j++) {
        let tempArr2:any = thisyearCode[j]
        if(tempArr1.ccode === tempArr2.ccode && tempArr1.ccodeName === tempArr2.ccodeName){
          list.push(tempArr2.id)
          break;
        }
      }
    }
    if(list.length===0){
      createConfirmPop('上年没有已选平行记账科目 或者 上年科目编码、名称与本年的不同！')
      return false;
    }else{
      // 修改
      await useRouteApi(company_editCodePxjzOrCyfx,{schemaName: database})({id:list.join(','),type:'pxjz',flag:'1'})
        .then(()=>{
          visibleModel.value=false;
          reload()
        })
    }
  }
}
function addspace(igrade, str) {
  var space = '';
  for (let i = 1; i < igrade; i++) {
    space += '\xa0\xa0\xa0\xa0';
  }
  return space + str;
}


async function save(flg) {
  let list=getSelectRows()
  if(list.length < 1){
    message.error("请先选择数据！")
    return
  }
  let ids = []
  list.forEach(v=>{
    ids.push(v.id)
  })
  console.log(ids)
  await useRouteApi(addKmCashFlow,{schemaName:database})({
    ids: ids,
    flg: flg,
  });
  message.success("操作成功！")
  reload()
}

</script>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>

<style>
.bg-white {
  width: 175px !important;
  min-height: 175px !important;
  height: calc(100% - 170px);
  border: 1px #cccccc solid;
  background: white !important;
  margin-right: .2%;
}
</style>
<style lang='less' scoped >
.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 16px !important;
  padding: 5px 8px !important;
}
.bg-white{
  width: 250px ;
  min-height: 250px ;
  height: calc(100% - 228px);
  border: 1px #cccccc solid;
  background:white ;
  margin-top: -0.5%;
}
.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 4px 8px !important;
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
.app-container-head{
  padding-left: 5px;
}
</style>

