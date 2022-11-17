<template>
  <div class="app-container">
    <div class="app-container-top lcr-theme-div" style="background-color: rgb(41 150 199)">
      <div>
        <div>
          <CopyOutlined style="color: white;font-size: 50px;"/>
        </div>
        <div>  <AccountPicker theme="three" @reloadTable="dynamicAdReload"/></div>
      </div>
      <div></div>
      <div>
        <div>
          <Button class="actod-btn actod-btn-last" @click="setPz">生成凭证</Button>
          <Button class="actod-btn actod-btn-last" @click="tableDel">清空</Button>
          <Button class="actod-btn actod-btn-last" @click="closeCurrent(),router.push('/zhongZhang/home/welcome')">退出</Button>
        </div>
        <div>
          <div class="acttd-right-d-btns" v-if="status==3">
            <Button class="acttdrd-btn">
              <SyncOutlined :style="{ fontSize: '14px' }"/>
            </Button>
            <Popover placement="bottom" v-model:visible="visible3">
              <template #content>
                <DynamicColumn :defaultData="initDynamics()['DATA']" :dynamicData="dynamicColumnData" :lanmuInfo="lanMuData" @reload="reloadColumns"/>
                <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MAX'"
                      :style="pageParameter.showRulesSize==='MAX'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortDescendingOutlined/>&nbsp;大号字体&ensp;<CheckOutlined
                  v-if="pageParameter.showRulesSize==='MAX'"/></span><br/>
                <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MIN'"
                      :style="pageParameter.showRulesSize==='MIN'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortAscendingOutlined/>&nbsp;小号字体&ensp;<CheckOutlined
                  v-if="pageParameter.showRulesSize==='MIN'"/></span>
              </template>
              <Button class="acttdrd-btn">
                <SettingFilled  :style="{ fontSize: '14px' }"/>
              </Button>
            </Popover>
          </div>
          <div class="acttd-right-d-btns" v-else>
            <Button v-if="status<3" class="acttdrd-btn" >
              <BarcodeOutlined :style="{ fontSize: '20px',display:'inline-flex' }"/>
            </Button>
          </div>
        </div>
      </div>
    </div>
    <div class="app-container-bottom" :style="{height: (windowHeight+70)+'px'}">
      <div class="acb-head">
        <div class="acbgead-one">
          <div class="acbgead-one-triangle">
            <div></div>
            <div>
              <span
                style="color: white;">{{'结转'}}</span>
            </div>
          </div>
          <span style="font-size: 22px;font-weight: bold;margin-right: 300px;" :style="{color:'#0096c7'}">{{ titleContents[0] }}</span>
        </div>
        <div class="acbgead-two dynamic-form">
          <DynamicForm :datasource="dynamicFormModel" :formDataFun="formFuns" :accId="dynamicAccId" :iyear="dynamicYear" :thisDate="ddate"
                     @kmLevelThrow="kmLevelThrow" @dataTypeThrow="dataTypeThrow"  @lirunKmThrow="lirunKmThrow" @ddateThrow="ddateThrow" :dynamicTenant="lanMuData.accId"/>
        </div>
      </div>
      <div class="acb-centent">
        <BasicTable
          ref="tableRef"
          :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
          :clickToRowSelect="false"
          :loading="loadMark"
          :row-selection="{ type: 'checkbox',selectedRowKeys: tableSelectedRowKeys, onChange: onSelectChange }"
          :scroll="{ x: totalColumnWidth,y: windowHeight-(100)}"
          size="small"
          @register="registerTable"
        >
          <template #lirunKm="{ record }" >
            {{ kmAll.filter(t=>t.ccode==record.lirunKm)[0]?.ccode }}
          </template>
          <template #lirunKmName="{ record }" >
            {{ kmAll.filter(t=>t.ccode==record.lirunKmName)[0]?.ccodeName }}
          </template>
        </BasicTable>
      </div>
    </div>

  </div>
</template>

<script setup="props, {emit}" lang="ts">
import {Button, Input, Popover, Radio, Select, Tabs,message} from "ant-design-vue";
import DynamicColumn from "./component/DynamicColumn.vue";
import {
  SearchOutlined,
  BarcodeOutlined,
  CheckOutlined,
  CopyOutlined,
  SettingFilled,
  SortAscendingOutlined,
  SortDescendingOutlined,
  SyncOutlined
} from '@ant-design/icons-vue';
import {reactive, ref} from "vue";
import {useMessage} from "/@/hooks/web/useMessage";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {BasicTable, useTable} from '/@/components/Table'
import {useModal} from '/@/components/Modal'
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {assemblyDynamicColumn, initDynamics} from "./data";
import DynamicForm from './component/DynamicForm.vue';
import {GenerateDynamicColumn} from "./component/ts/DynamicForm";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {company_findByIyearCcod} from "/@/api/codekemu/codekemu";
import {findAllDataByAccountId} from "/@/api/sys_period/data";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {JsonTool} from "/@/api/task-api/tools/universal-tools";
import {newQCSYJZ} from "/@/api/record/system/losses-gain";


const RadioButton = Radio.Button
const RadioGroup = Radio.Group
const InputSearch = Input.Search
const SelectOption = Select.Option
const TabPane = Tabs.TabPane
const {createErrorModal, createConfirm, createWarningModal} = useMessage()
const {closeCurrent} = useTabs(router);
const [registerQueryPage, {openModal: openQueryPageM}] = useModal()
const [registerImportPage, {openModal: openImportPageM}] = useModal()
const [registerPrintPage, {openModal: openPrintPage}] = useModal()
const [registerLackPage, {openModal: openLackPage}] = useModal()
const [registerItemsPage, {openModal: openItemsSourcePage}] = useModal()

const windowHeight = (window.innerHeight - 300)
const dynamicTenantId:any = ref('')
const dynamicTenant:any = ref(null)
const dynamicAccId = ref('')
const dynamicYear = ref('')
const titleContents = ['期间损益结转']
const pageParameter:any = reactive({
  showRulesSize: 'MIN',
  searchConditon: {
    requirement: '',
    value: '',
  },
  type: 'CGFP'
})
const visible3:any = ref(false)
const tableSelectedRowKeys = ref([])
const tableSelectedRowObjs = ref([])
const onSelectChange = (k, o) => {
  tableSelectedRowKeys.value=[]
  tableSelectedRowObjs.value=[]

  tableSelectedRowKeys.value = k
  tableSelectedRowObjs.value = o
}
const dynamicFormModel:any = ref([])
const formRowNum = ref(1)
const loadMark = ref(false)
const windowWidth = (window.innerWidth - (70))
// 0 暂存 1 新建 2编辑 3查看
const status = ref(3)
const formFuns:any = ref({
  getFormValue: () => {
  }, setFormValue: () => {
  }, getSelectMap: () => {
  }
})
const formItems: any = ref({})
const lirunKm: any = ref('')
const lirunKmThrowData: any = ref('')
const ddate: any = ref(useCompanyOperateStoreWidthOut().getLoginDate)
const sykmAll: any = ref([])
const kmAll: any = ref([])
const kmLevel: any = ref('1')
const dataType: any = ref('')




const initTable = ()=>{
  visible3.value = true
  setTimeout(()=>{
      lanMuData.value.changeNumber+=1
      visible3.value = false
    }
    ,300)
}
const dynamicAdReload = async (obj) => {
  console.log('当前时间：'+new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "))
  initTable()
  dynamicTenant.value = obj
  dynamicAccId.value = obj.accId
  dynamicYear.value = obj.iyear
  dynamicTenantId.value=obj.accountMode
  lanMuData.value.accId=obj.accountMode
  await columnReload()
}

const CrudApi = {
  columns: [
    {
      title: '费用科目编码',
      dataIndex: 'feiyongKm',
      ellipsis: true,
      slots: {customRender: 'feiyongKm'},
      width: 120
    },
    {
      title: '费用科目名称',
      dataIndex: 'feiyongKmName',
      ellipsis: true,
      width: 120,slots: {customRender: 'feiyongKmName'},
    },
    {
      title: '本年利润科目编码',
      dataIndex: 'lirunKm',
      ellipsis: true,
      slots: {customRender: 'lirunKm'},
      width: 120
    },
    {
      title: '本年利润科目名称',
      dataIndex: 'lirunKmName',
      ellipsis: true,
      slots: {customRender: 'lirunKmName'},
      width: 200
    }
  ]
}
// 这是示例组件
const [registerTable, {
  reload,
  getDataSource,
  setTableData,
  setPagination,
  getPaginationRef,
  getColumns,
  setColumns,
  getSelectRows
}] = useTable({
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: true,
  indexColumnProps:{ fixed:true },
  pagination: false
})


/*栏目设置*/
const totalColumnWidth = ref(0)
const dynamicColumnData = ref({value:[]})
const tableRef:any = ref(null)
const lanMuData = ref({
  accId: 'postgres',
  menuName: '采购发票',
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
    let f = 0
    if (visible3.value) f = 260
    totalColumnWidth.value = Number(windowWidth) - f
    tableRef.value.$el.style.setProperty('width', (windowWidth + 40 - f) + 'px')
  } else {
    if (visible3.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
    totalColumnWidth.value = total
    tableRef.value.$el.style.setProperty('width', (total + 40) + 'px')
  }
}
/*栏目设置end*/

const columnReload = async () => {
  dynamicFormModel.value = await GenerateDynamicColumn() || []
  formRowNum.value = Math.ceil((dynamicFormModel.value.filter(it=>it.isShow).length/4))-1
}

const pageReload = async () => {
  await contentSwitch()
}
const contentSwitch = async () => {
  loadMark.value=true
  await findAllCode()
  await findAllPeriod()

  setTimeout(()=>{
    formItems.value = {
      jz: '1',
      kmLevel: kmLevel.value,
      ddate: ddate.value,
      lirunKm: lirunKm.value,
      dataType: dataType.value
    }
    formFuns.value.setFormValue({
      jz: '1',
      kmLevel: kmLevel.value,
      ddate: ddate.value,
      lirunKm: lirunKm.value,
      dataType: dataType.value
    })

    let list:any=[]
    for (let i = 0; i < sykmAll.value.length; i++) {
      let temp:any={}
      temp.feiyongKm=sykmAll.value[i].ccode
      temp.feiyongKmName=sykmAll.value[i].ccodeName
      temp.lirunKm=lirunKm.value
      temp.lirunKmName=lirunKm.value
      list.push(temp)
    }

    for (let i = list.length; i < 50; i++) {
      list.push({})
    }
    setTableData(list)
    loadMark.value=false
  },300)
}

// 查询科目
const findAllCode = async () => {
  let kemu = await useRouteApi(company_findByIyearCcod, { schemaName: dynamicTenantId })({iyear:dynamicYear.value});
  if(kemu.length>0){
    kmAll.value=kemu
    let list=kemu.filter(t=>kmLevel.value=='1'?t.igrade=='1':t.bend=='1')
    list=list.filter(t=>t.cclass=='损益')
    list=dataType.value==''?list:list.filter(t=>t.bprogerty==dataType.value)
    sykmAll.value=list
    lirunKm.value=lirunKmThrowData.value==''?kemu.filter(t=>t.cclass=='权益'&&t.ccodeName=='本年利润')[0]?.ccode:lirunKmThrowData.value
  }
}
// 结转期间
const findAllPeriod = async () => {
  let time=useCompanyOperateStoreWidthOut().getLoginDate
  time=time.substring(0,time.length-3)
  let periodList = (await findAllDataByAccountId({accId:dynamicAccId.value}))
  let temp=periodList.filter(t=>t.title==time)
  if(temp.length>0){
    ddate.value=temp[0]?.title
  }else{
    ddate.value=temp[periodList.length-1]?.title
  }
}

const openSelectContent = async () => {

}
const kmLevelThrow = (v) => {
  kmLevel.value=v
  contentSwitch()
}
const dataTypeThrow = (v) => {
  dataType.value=v
  contentSwitch()
}
const lirunKmThrow = (v) => {
  lirunKmThrowData.value=v
  contentSwitch()
}
const ddateThrow = (v) => {
  dynamicYear.value=v.split('-')[0]
  contentSwitch()
}

const tableDel = async () => {
  let list:any = getDataSource()
  for (let i = 0; i < list.length; i++) {
    for (let j = 0; j < tableSelectedRowKeys.value.length; j++) {
      if(list[i].key==tableSelectedRowKeys.value[j]){
        list[i].lirunKm=''
        list[i].lirunKmName=''
      }
    }
  }
  setTableData(list)
  tableSelectedRowKeys.value = []
}

const setPz = async () => {
  let map={
    ddate:formItems.value.ddate,
    kmLevel:formItems.value.kmLevel,
    dataType:formItems.value.dataType,
    lirunKm:formItems.value.lirunKm,
    jz:formItems.value.jz,
    sykmAll:JsonTool.json(sykmAll.value),
    sunYiCodeFirst:sykmAll.value[0].ccode.charAt(0)
  }
  // await useRouteApi(newQCSYJZ, { schemaName: dynamicTenantId })(map);
}
</script>
<style lang="less" scoped="scoped">
@Global-Border-Color: #c9c9c9; // 全局下划线颜色
@Global-Label-Color: #666666; // 全局#c9c9c9颜色
@Global-Comm-BcOrText-Color: #0096c7; // 全局#c9c9c9颜色
.app-container {
  padding: 10px;
  background-color: #b4c8e3;
  margin: 0;

  .app-container-top {
    background-color: #f1f1f1;
    border-radius: 5px 5px 0 0;
    padding: 10px;

    > div {
      width: 100%;
      display: inline-flex;
      justify-content: space-between;
    }

    .app-container-top-one {
      line-height: 40px;

      .act-one-d-img {
        width: 42px;
      }

      .act-one-d-left {

      }

      .act-one-d-title {
        .acto-d-title-span {
          color: @Global-Comm-BcOrText-Color;
          font-weight: bold;
          font-size: 24px;
        }
      }

      .act-one-d-btn-group {
        text-align: right;

        .actod-btn {
          color: @Global-Comm-BcOrText-Color;
          font-size: 14px;
          border-color: @Global-Border-Color;
          border-right: none;
        }

        .actod-btn-last {
          border-right: 1px solid @Global-Border-Color;
        }

        .actod-btn:hover {
          background-color: @Global-Comm-BcOrText-Color;
          color: white;
        }
      }
    }

    .app-container-top-two {
      font-size: 14px;
      line-height: 32px;

      .act-two-d-left {

      }

      .act-two-d-center {
        .acttdrd-search-select {
          width: 120px;

          :deep(.ant-select-selector) {
            border-color: @Global-Border-Color;
            border-radius: 2px 0 0 2px;
          }
        }
      }

      .act-two-d-right {
        display: inline-flex;
        justify-content: space-between;

        .acttd-right-d-search {
          .acttdrd-search-select {
            width: 120px;

            :deep(.ant-select-selector) {
              border-color: @Global-Border-Color;
              border-radius: 2px 0 0 2px;
            }
          }

          .acttdrd-search-input {
            width: 180px;
            border-radius: 0 2px 2px 0;
            border-color: @Global-Border-Color;
            border-left: none;
          }
        }

        .acttd-right-d-btns {
          margin-left: 10px;

          .acttdrd-btn {
            border-color: @Global-Border-Color;
            margin-left: 2px;
          }
        }
      }
    }
  }

  .app-container-bottom {
    background-color: white;

    .acb-head {
      .acbgead-one {
        text-align: center;
        height: 60px;
        line-height: 60px;

        .acbgead-one-triangle {
          > div:nth-of-type(1) {
            width: 0px;
            height: 0px;
            border-top: 60px solid #5141d7;
            border-right: 70px solid transparent;
            position: absolute;
          }

          > div:nth-of-type(2) {
            width: max-content;
            color: #fff;
            transform: rotate(-45deg) translateY(-2px) translateX(10px);
            position: absolute;
          }
        }

        .acbgead-one-changes {
          > span {
            cursor: pointer;
          }

          > span:hover {
            color: black;
          }
        }

        > div:nth-of-type(2) {
          display: inline-block;
          float: left;
          margin-left: 4%;
          font-weight: bold;
          font-size: 24px;
          color: #666666;
        }
      }

      .acbgead-two {
        margin: 0 5rem;
      }
    }

    .acb-centent {
      margin: 0 4%;
    }
  }

  .status-look {
    pointer-events: none;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
  }
}
.lanmu-table{
  :deep(.ant-table-content){
    .ant-table-thead{
      tr{
        th{
          padding: 5px 8px !important;
          text-align: center !important;
        }
      }
    }
    .ant-table-tbody{
      tr{
        td{
          padding:0 5px !important;
          font-size: 12px !important;
          .ant-radio-button-wrapper{
            font-size: 12px;
          }
        }
      }

    }
  }
}
.suspended-div {
  width: 100%;
  height: 22px;

  .anticon-form {
    float: right;
    line-height: 22px;
    display: none;
  }
}
.suspended-div:hover {
  cursor: text;
  background-color: #e4e7e7;
  .anticon-form {
    display: block;
  }
}
</style>
<style lang="less" scoped>
@import '/@/assets/styles/global-menu-index1.less';
.lcr-theme-div{
  display: inline-flex;justify-content: space-between;width: 100%;height: 100px;
  >div:nth-of-type(1){
    width: 40%;
    position: relative;
    display: block;
    >div:nth-of-type(1){width: 64px;display: inline-block;text-align: center;    top: 12px;
      position: inherit
    }
    >div:nth-of-type(2){

      width: calc( 100% - 64px);display: inline-block;
      :deep(.account-picker){
        .ap-title,.thisNameSpan{
          color: white !important;
        }
      }
    }
  }
  >div:nth-of-type(2){
    width: 20%;text-align:center;
    >div:nth-of-type(2){margin-top: 14px;}
  }
  >div:nth-of-type(3){
    width: 43%;text-align: right;
    display: block;
    >div:nth-of-type(1){
      .actod-btn {
        color: @Global-Comm-BcOrText-Color;
        font-size: 14px;
        margin: 0 1px 0 0;
      }

      .actod-btn-last {
        border-right: 1px solid @Global-Border-Color;
      }

      .actod-btn:hover {
        background-color: @Global-Comm-BcOrText-Color;
        color: white;
        font-weight: bold;
        border: 1px solid white;
      }
    }
    >div:nth-of-type(2){
      display: inline-flex;justify-content: space-between;margin-top: 14px;
      .acttd-right-d-search {
        .acttdrd-search-select {
          width: 120px;

          :deep(.ant-select-selector) {
            border-color: @Global-Border-Color;
            border-radius: 2px 0 0 2px;
          }
        }

        .acttdrd-search-input {
          width: 180px;
          border-radius: 0 2px 2px 0;
          border-color: @Global-Border-Color;
          border-left: none;
        }
      }

      .acttd-right-d-btns {
        margin-left: 10px;

        .acttdrd-btn {
          color: @Global-Comm-BcOrText-Color;
          margin-left: 2px;
        }
        .acttdrd-btn:hover{
          border-color: #ffffff;
          color: #ffffff;
          background-color: @Global-Comm-BcOrText-Color;
        }
      }
    }
  }
}
.dynamic-form{
  .span{
    color: #666666;
    font-size: 17px;
    font-weight: bold;
  }
  :deep(.ant-select-selector), :deep(.ant-picker), :deep(.ant-input-affix-wrapper){
    border: none;
    border-bottom: 1px solid #c9c9c9;
    background-color: white;
    color: black;
    .ant-picker-input {
      > input {
        color: black;
      }
    }
  }
  :deep(.ant-input-number){
    width: 100%;
    border: none;
    border-bottom: 1px solid #c9c9c9;
    background-color: white;
    color: black;
  }
  :deep(.ant-col){
    margin-left: 1em;
    .ant-form-item-label{
      text-align: left;
      .ant-form-item-no-colon{
        font-weight: bold;
        color: #666666;
      }
    }

    .ant-form-item:not(.ant-form-item-with-help) {
      margin-bottom: 5px;
    }
  }
  :deep(.ant-col-4){
    display: none;
  }
}
</style>
