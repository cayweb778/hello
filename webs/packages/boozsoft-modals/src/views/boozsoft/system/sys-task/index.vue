<template>
  <div class="app-container">
    <div class="app-container-top">
      <div class="app-container-head"><img :src="getThisIndexImg()" class="container-head-img" draggable="false" >
        <div class="container-head-title">
         <b class="noneSpan" >{{ titleName }}</b>
       </div>
      <div class="ant-btn-group">
        <button
          v-if="pageParameter.lb != '1'"
          type="button"
          class="ant-btn ant-btn-me"
          ant-click-animating-without-extra-node="false"
          @click="openQueryPage()"
        ><span>发布</span></button>
        <a-popover placement="bottom">
          <template #content>
            <span class="group-btn-span" @click="exportExcelNow()">导出当前</span><br/>
            <span class="group-btn-span" >条件导出</span>
          </template>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
          ><span>导出</span></button></a-popover>
        <button
          type="button"
          class="ant-btn ant-btn-me"
          ant-click-animating-without-extra-node="false"
        ><span>退出</span></button>
      </div>
      <div style="clear:both" />
      <div style="margin-top: .5%;">
        <div style="display: inline-block;float: left">
          <span style="font-size: 16px;color: #aaa;font-weight: bold">类别：</span>
          <a-select
            show-search
            placeholder="类别选择"
            option-filter-prop="children"
            style="width: 180px"
            :filter-option="filterOption"
            @change="handleChangeLb"
            v-model:value="pageParameter.lb"
          >
            <a-select-option v-for="d in lbList" :value="d.value">
              {{ d.label }}
            </a-select-option>
          </a-select>
        </div>

        <div style="display: inline-block;float: left;padding-left: 10px;">
          <span style="font-size: 16px;color: #aaa;font-weight: bold">状态：</span>
          <a-select
            show-search
            placeholder="状态选择"
            option-filter-prop="children"
            style="width: 180px"
            :filter-option="filterOption"
            @change="handleChangeMinKm"
            v-model:value="pageParameter.ifrag"
          >
            <a-select-option v-for="d in kmList" :value="d.value">
              {{ d.label }}
            </a-select-option>
          </a-select>
        </div>


        <div style="float: right; margin-left: 10px">
          <a-button class="ant-btn-default" @click="closeFilterV(),pageReload()">
            <SyncOutlined :style="{ fontSize: '14px' }" />
          </a-button>

          <a-popover placement="bottom">
            <template #content>
               <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MAX'"
                                  :style="pageParameter.showRulesSize==='MAX'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortDescendingOutlined/>&nbsp;大号字体&ensp;<CheckOutlined
                              v-if="pageParameter.showRulesSize==='MAX'"/></span><br/>
              <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MIN'"
                    :style="pageParameter.showRulesSize==='MIN'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortAscendingOutlined/>&nbsp;小号字体&ensp;<CheckOutlined
                v-if="pageParameter.showRulesSize==='MIN'"/></span>
            </template>

            <a-button>
              <SettingFilled :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>
          <a-button @click="()=>{
            if (!visible){ visible = true;}
            return false
          }">
            <FilterFilled :style="{ fontSize: '14px' }" />
          </a-button>
        </div>
        <div style="float: right; position: relative">
          <!-- 搜索 -->
          <span class="noneSpan" style="font-size: 16px">检索条件：</span>
          <a-select v-model:value="pageParameter.searchConditon.requirement" style="width: 130px;" class="special_select">
            <template v-for="item in searchConditonList">
              <a-select-option  :value="item.dataIndex">
                {{item.title}}
              </a-select-option>
            </template>
          </a-select>
          <a-input-search
            placeholder=""
            v-model:value="pageParameter.searchConditon.value"
            @search="pageSearch"
            style="width: 150px;border-radius: 4px"
          />
        </div>
      </div>
    </div>
  </div>
  <div class="app-container-bottom" />
      <BasicTable
        ref="tableRef"
        :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
        :scroll="{ x: totalColumnWidth,y: windowHeight }"
        size="small"
        @register="registerTable"
      >
        <template #number="{record,index }">
          <span slot="number" slot-scope="text,record,index">
           {{index+1}}
          </span>
        </template>

        <template #ifrag="{record,index }">
          <a-tag color="success" v-if="record.ifrag === '1'"  slot="ifrag" slot-scope="text,record,index">
           {{formatIfrag(record.ifrag)}}
          </a-tag>
          <a-tag color="processing" v-else-if="record.ifrag === '0'"  slot="ifrag" @click="read(record.messageId)" slot-scope="text,record,index">
            {{formatIfrag(record.ifrag)}}
          </a-tag>
          <a-tag color="warning" v-else-if="record.ifrag === '2'"  slot="ifrag" slot-scope="text,record,index">
            {{formatIfrag(record.ifrag)}}
          </a-tag>
          <a-tag color="success" v-else="record.ifrag != '-1'"  slot="ifrag" slot-scope="text,record,index">
            已发布
          </a-tag>


        </template>


        <template #sendTime="{record,index }">
          <span slot="sendTime" slot-scope="text,record,index">
              {{formatTime(record.sendTime)}}
          </span>
        </template>

        <template #weights="{record,index }">
          <span slot="weights" slot-scope="text,record,index">
            <div v-if="record.weights === '1'&& record.ifrag != '1'" style="background-color: rebeccapurple;height: 6px"> {{'\u00a0'}}</div>
            <div v-else-if="record.weights === '2'&& record.ifrag != '1'" style="background-color: rebeccapurple;width: 75%;height: 6px"> {{'\u00a0'}}</div>
            <div v-else-if="record.weights === '3'&& record.ifrag != '1'" style="background-color: rebeccapurple;width: 50%;height: 6px"> {{'\u00a0'}}</div>

            <div v-else-if="record.weights === '1' && record.ifrag === '1'" style="background-color: green;height: 6px"> {{'\u00a0'}}</div>
            <div v-else-if="record.weights === '2' && record.ifrag === '1'" style="background-color: green;width: 75%;height: 6px"> {{'\u00a0'}}</div>
            <div v-else-if="record.weights === '3' && record.ifrag === '1'" style="background-color: green;width: 50%;height: 6px"> {{'\u00a0'}}</div>

            <div v-else="record.weights === '4' && record.ifrag === '1'" style="background-color: green;width: 25%;height: 6px"> {{'\u00a0'}}</div>
          </span>
        </template>

        <template #caozuo="{record,index }">
          <div>
            <a-popover placement="bottom">
              <a-button style="padding: 0px 4px; height: 25px">
                <CaretDownFilled />
              </a-button>
              <template #content>
                <p v-if="pageParameter.lb === '1' &&  record.ifrag === '1'" style="cursor: pointer" @click="notShow(record.messageId)"><FormOutlined /> 隐藏</p>
                <p v-if="pageParameter.lb === '1' &&  record.ifrag != '1'" style="cursor: pointer" @click="read(record.messageId)"><FormOutlined /> 已读</p>
                <p v-if="pageParameter.lb != '1'" style="cursor: pointer" @click="del(record)"><DeleteOutlined /> 撤销</p>
                <p  style="cursor: pointer" @click="look(record)"><DeleteOutlined /> 查看</p>
              </template>
            </a-popover>
          </div>
        </template>

      </BasicTable>
      <a-drawer
        title="过滤漏斗"
        placement="right"
        :closable="true"
        v-if="visible"
        :mask="false"
        :visible="visible"
        :get-container="false"
        :wrap-style="{ position: 'absolute' }"
        @close="visible=false"
      >
        <ul>
          <li>
            <span style="color: black;font-weight: bold">
              发送时间：
            </span>
            <div>
              <a-input v-model:value="pageParameter.filterConditon.amountMinJf"  placeholder="" style="width: 95px;font-size: 10px"/>
              ~
              <a-input v-model:value="pageParameter.filterConditon.amountMaxJf"  placeholder="" style="width: 95px;font-size: 10px;float: right"/>
            </div>
          </li>
        </ul>
        <br/>
        <a-button type="primary"  style="float: right;" @click="filterSearch">
          <span style="font-size: 14px">开始</span>
        </a-button>
      </a-drawer>
      <Query
        @save="loadPage"
        @register="registerQueryPage"
      />
      <Look
        @register="registerLookPage"
      />
  </div>
</template>
<script setup lang="ts">
  import { BasicTable, useTable } from '/@/components/Table'
  import { useModal } from '/@/components/Modal'
  import { PageWrapper } from '/@/components/Page'
  import {
    DatePicker as ADatePicker, Select as ASelect, Input as AInput, Popover as APopover,
    Radio as ARadio, message,Tag as ATag
  } from "ant-design-vue"
  const ARangePicker=ADatePicker.RangePicker
  const ASelectOption=ASelect.Option
  const AInputSearch=AInput.Search
  const ARadioGroup=ARadio.Group
  const ARadioButton=ARadio.Button
  import {
    SortDescendingOutlined,
    SortAscendingOutlined,
    CaretDownFilled,
    FormOutlined,
    DeleteOutlined,
    CheckCircleOutlined,
    CloseCircleOutlined,
    SettingFilled,
    SyncOutlined,
    PicLeftOutlined,
    EditFilled,
    PieChartFilled,
    FilterFilled,CheckOutlined,EditOutlined,SearchOutlined
  } from '@ant-design/icons-vue'
  import {onMounted, reactive, ref} from "vue";
  import moment from "moment";
  import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
  import { initDynamics,assemblyDynamicColumn} from "./data";
  import Query from "./popup/add.vue";
  import Look from "./popup/look.vue";
  import {cloneDeep} from "lodash-es";
  import {useMessage} from "/@/hooks/web/useMessage";
  import {
    getCurrentAccountName,getThisIndexImg
  } from "/@/api/task-api/tast-bus-api";
  import {useUserStoreWidthOut} from "/@/store/modules/user";
  import router from "/@/router";
  import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
  import { exportExcel } from "/@/api/record/generalLedger/excelExport";
  import { findAll,hide,goread,chexiao } from "/@/api/record/sys-task/data";

  const { createConfirm,createWarningModal } = useMessage();

  // 页面变量
  const pageParameter = reactive({
    ifrag: '-1',
    user:  useUserStoreWidthOut().getUserInfo.id,
    lb: '1',
    showRulesSize: 'MIN',
    condition: {},
    searchConditon: {
      requirement: 'content',
      value: '',
    },
    filterConditon: {
      amountMinDf: '',
      amountMaxDf: '',
    },
    reloadMark: false,

  })

  // 科目名称标题
  const titleName = ref<String>("任务协作")

  const lbList = ref([
    {
      value: '1',
      label: '接收任务',
    },
    {
      value: '0',
      label: '发送任务',
    },
  ])
  const kmList = ref([
    {
      value: '-1',
      label: '全部',
    },
    {
      value: '0',
      label: '未完成',
    },
    {
      value: '1',
      label: '已完成',
    },
    {
      value: '2',
      label: '作废',
    },
  ])
  const kmList1 = ref([
    {
      value: '-1',
      label: '全部',
    },
    {
      value: '0',
      label: '未完成',
    },
    {
      value: '1',
      label: '已完成',
    },
    {
      value: '2',
      label: '作废',
    },
  ])
  const kmList2 = ref([
    {
      value: '-1',
      label: '全部',
    },
    {
      value: '2',
      label: '作废',
    },
  ])
  const showStyle = ref([
      {
        title: '序号',
        dataIndex: 'number',
        key: 'number',
        align: 'center',
        width: '80px',
        slots: { customRender: 'number' },
      },
      {
        title: '优先级',
        dataIndex: 'weights',
        key: 'weights',
        width: '80px',
        align: 'center',
        slots: { customRender: 'weights' },
      },
      {
        title: '标题',
        dataIndex: 'title',
        key: 'title',
        align: 'left',
        width: '220px',
        slots: { customRender: 'title' },
      },
      {
        title: '消息内容',
        dataIndex: 'content',
        key: 'content',
        align: 'left',
        width: '220px',
        slots: { customRender: 'content' },
      },
      {
        title: '发送时间',
        dataIndex: 'sendTime',
        key: 'sendTime',
        width: '100px',
        align: 'center',
        slots: { customRender: 'sendTime' },
      },
      {
        title: '发送人',
        dataIndex: 'username',
        key: 'username',
        width: '100px',
        align: 'center',
        slots: { customRender: 'username' },
      },
      {
        title: '状态',
        dataIndex: 'ifrag',
        key: 'ifrag',
        align: 'center',
        width: '80px',
        slots: { customRender: 'ifrag' },
      },
      {
        title: '操作',
        dataIndex: 'caozuo',
        key: 'caozuo',
        align: 'center',
        width: '80px',
        slots: { customRender: 'caozuo' },
      },
  ])
  const val = {
    data: '',
    flg: '',
    openOne: 0
  }
  const visible = ref(false);
  const searchConditonList = ref(
      [{
      title: '消息内容',
      dataIndex: 'content',
      key: 'content',
      align: 'left',
      width: '220px',
      slots: { customRender: 'content' },
      },
      {
        title: '发送时间',
        dataIndex: 'sendTime',
        key: 'sendTime',
        width: '100px',
        align: 'center',
        slots: { customRender: 'sendTime' },
      },
      {
        title: '发送人',
        dataIndex: 'sender',
        key: 'sender',
        width: '100px',
        align: 'center',
        slots: { customRender: 'sender' },
      },
      {
        title: '状态',
        dataIndex: 'ifrag',
        key: 'ifrag',
        align: 'center',
        width: '80px',
        slots: { customRender: 'ifrag' },
      },
    ])
  // 表格参数
  const loadMark = ref(false)
  const tableSelectedRowKeys = ref([])
  const tableSelectedRowObjs = ref([])
  const windowWidth  = (document.documentElement.clientWidth -70)
  const windowHeight  = (document.documentElement.clientHeight -350)
  const totalColumnWidth = ref(0)
  // 数据库模式名称
  const databases = ref(getCurrentAccountName(false))
  let databasesName = ref('bjxgkj-001-2021')
  const CrudApi = {
    list: findAll,
    columns: JSON.parse(JSON.stringify(showStyle.value))
  }
  const tableRef = ref(null)

  // 组件实例区
  const [registerTable, { reload,setColumns,getColumns,getDataSource }] = useTable({
    api: CrudApi.list,
    columns: CrudApi.columns,
    bordered: true,
    loading: loadMark.value,
    immediate: false,
    canResize: true,
    /*  showSummary: true,
      summaryData: [],*/
    showIndexColumn: false , //显示序号列
    pagination:{ pageSize: 200,showSizeChanger: true, pageSizeOptions: ['200','500','1000','1500'],showTotal: t => `总共${t}条数据` },
    /*actionColumn: {
      width: 80,
      title: '操作',
      dataIndex: 'action',
      slots: { customRender: 'action' }
    } as　BasicTable*/
    tableSetting: {
      // 是否显示刷新按钮
      redo: true,
      // 是否显示尺寸调整按钮
      size: true,
      // 是否显示字段调整按钮
      setting: true,
      // 是否显示全屏按钮
      fullScreen: true,
    }
  })
  const [registerQueryPage, { openModal: openQueryPageM }] = useModal()

  const [registerLookPage, { openModal: openLookPageM }] = useModal()


  onMounted( ()=>{
    pageReload()
  })

  const openQueryPage = () => {
    val.openOne = 0
    openQueryPageM(true, {
      data: val
    })
  }
  // 页面函数区
  const onSelectChange = (selectedRowKeys,obj) => {
    tableSelectedRowKeys.value = selectedRowKeys;
    tableSelectedRowObjs.value = obj;
  };
  const pageReload =  () =>{
    reload({
      searchInfo: pageParameter
    })
  }
  const breakNumTidyBtn = async() => {
    pageReload()
  }

  const  pageSearch = async ()=>{
    /* if (''==pageParameter.searchConditon.value.trim())return false*/
    // 搜索前校验格式
    if (''==pageParameter.searchConditon.requirement.trim()){
      message.warn('请选择检索条件')
      return false
    }
    // 校验完成后搜索
    closeFilterV()
    pageReload()
  }

  const  filterSearch = async ()=>{
    /* if (''==pageParameter.searchConditon.value.trim())return false*/
    // 搜索前校验格式

    // 校验完成后搜索
    pageReload()
  }

  const closeFilterV = ()=>{
    pageParameter.filterConditon.amountMinDf = ''
    pageParameter.filterConditon.amountMaxDf = ''
    visible.value = false
  }

  function isRealNum(val){
    // isNaN()函数 把空串 空格 以及NUll 按照0来处理 所以先去除，
    if(val === "" || val ==null){
      return false;
    }
    if(!isNaN(val)){
      //对于空数组和只有一个数值成员的数组或全是数字组成的字符串，isNaN返回false，例如：'123'、[]、[2]、['123'],isNaN返回false,   //所以如果不需要val包含这些特殊情况，则这个判断改写为if(!isNaN(val) && typeof val === 'number' )
      return true; 　　}　else{ 　　　　return false; 　　}
  }

  const userName = useUserStoreWidthOut().getUserInfo.username
  // 数据库模式名称
  const database = ref(useCompanyOperateStoreWidthOut().getSchemaName);
  const goto = async () => {

  }

  const handleChangeLb = (val)=>{
    //切换后 切换状态
    pageParameter.ifrag = '-1'
    if(val === '1'){
      kmList.value = kmList1.value
    }else{
      kmList.value = kmList2.value
    }
    pageReload()
  }
  const handleChangeMinKm = (val)=>{
    pageReload()
  }

  const formatIfrag = (val)=>{
    let str
    if(val === '1'){
      str = '已完成'
    }else if(val === '0'){
      str = '未完成'
    }else{
      str = '作废'
    }
    return str;
  }
  const formatWeights = (val)=>{
    let str
    if(val === '1'){
      str = '重要紧急'
    }else if(val === '2'){
      str = '重要不紧急'
    }else if(val === '3'){
      str = '紧急不重要'
    }else{
      str = '一般'
    }
    return str;
  }
  const formatTime = (val)=>{
    return moment(val).format('YYYY-MM-DD HH:mm:ss')
  }


  const loadPage =  async() =>{
    pageReload()
  }

  const notShow =  async(id) =>{
    await hide(useUserStoreWidthOut().getUserInfo.id, id)
    message.info('操作成功！')
    pageReload()
  }

  const read =  async(id) =>{
    await goread(useUserStoreWidthOut().getUserInfo.id, id)
    message.info('操作成功！')
    pageReload()
  }
  const del =  async(data) =>{
    //5分钟内可以撤销
    const c = moment().diff(moment(data.sendTime),'m')
    if(c < 6 && c >= 0){
      await chexiao(useUserStoreWidthOut().getUserInfo.id, data.messageId)
      message.info('撤销成功!')
    }else{
      message.error('已超过5分钟无法撤销!')
    }
  }
  const look =  async(data) =>{
    val.openOne = 0
    val.flg = pageParameter.lb
    val.data = data
    openLookPageM(true, {
      data: val
    })
  }

</script>

<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style scoped>/*针对当前页面特有样式*/
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

.a-table-font-size-16 :deep(td) .a-table-money-font-size {
font-size: 14px !important;
color: black;
}

.a-table-font-size-12 :deep(td) .a-table-money-font-size {
font-size: 13px !important;
color: black;
}

:deep(.a-table-font-arial) {
font-family: Arial !important;
}

:deep(.pz-unit-change) {
width: 50px;
border-radius: 4px;
text-align: center;
font-weight: bold;
color: black;
pointer-events: none;
border: none;
}

.pz-unit-change :deep(.ant-input) {
font-weight: bold;
color: black;
}
.app-container {
padding:  0px;
margin: 10px 10px 5px;
}
.account-picker-c {
width: 40%;
display: inline-block;
font-size: 14px
}
</style>
