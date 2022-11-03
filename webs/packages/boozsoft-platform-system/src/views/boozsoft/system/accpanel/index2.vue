<template>
  <div>
    <div class="app-container">
      <div class="container-head-title" style="padding-left: 46%;font-size: 18px;color: #0096c7;">
        <b class="noneSpan">总账面板设置</b>
        <div class="ant-btn-group" style="float: right">
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="openUpYear"
          ><span>复制上年</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="condClick"
          ><span>新增</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="closeCurrent(),router.push('/zhongZhang/home/welcome')"
          ><span>退出</span></button>
        </div>
      </div>

      <div class="app-container-neck" style="margin-top: 10px;">
        <div style="float: left;">
          <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
        </div>
        <div style="float: right; ">
          <span>显示方案：</span>
          <a-select v-model:value="panelFlag" style="width: 200px" @change="panelFlagChange">
            <a-select-option value="1">系统方案</a-select-option>
            <a-select-option value="2">个人方案</a-select-option>
          </a-select>
          <a-button style="padding: 0px 12px !important;" @click="findAll">
            <SyncOutlined :style="{ fontSize: '14px' }" />
          </a-button>
        </div>
        <div v-if="pageParameter.panelFlag=='2'" style="float: left;margin-left: 30px ">
          <span>操作员：</span>
          <a-select v-model:value="caozuoyuanselected" style="width: 200px" @change="czyChange">
            <a-select-option v-for="item in caozuoyuanlist" :key="item.id">
              {{item.username}}
            </a-select-option>
          </a-select>
        </div>
      </div>
    </div>
    <div class="nc-hrb">
      <Edit @save="saveData" @register="registerEditPage" />
      <div class="nc-hrb-top">
        <a-row type="flex">
          <a-col :span="4.5" v-for="item in accPanelList ">
            <div><img :src="imgtest"></div>
            <div>
              <span style="white-space:nowrap; text-overflow:ellipsis;overflow:hidden;display:block;margin-top: 10px;text-align: center;">{{item.panelName}}

                <a-popover placement="bottom">
                  <template #content>
                    <span class="group-btn-span" @click="editBtn(item)">修改</span><br/>
                    <span class="group-btn-span" @click="del(item)">删除</span>
                  </template>
                  <EllipsisOutlined style="cursor: pointer;margin-left: 10%;float: right;font-weight: bold;font-size: 20px;"/>
                </a-popover>
              </span>

            </div>
          </a-col>
        </a-row>
      </div>
      <a-modal title="复制上年总账面板设置" :visible="visible" :closable="false" @ok="copySave" @cancel="visible=false">
        <a-table bordered :pagination=false :dataSource="dataSource123" :columns="columns" :class="'a-table-font-size-12'"  :row-selection="{ type: 'checkbox', selectedRowKeys: selectedRowKeys, onChange: onSelectChange }">

        </a-table>
      </a-modal>
    </div>
  </div>
</template>
<script setup lang="ts">
import router from "/@/router";
import { useModal } from '/@/components/Modal';
import { SyncOutlined,EllipsisOutlined } from '@ant-design/icons-vue';
import {
  Modal as AModal,
  Table as ATable,
  Popover as APopover,
  Col as ACol,
  Row as ARow,
  Tabs as ATabs,
  Select as ASelect,
  Input as AInput,
  Statistic as AStatistic,
} from 'ant-design-vue';

import {onMounted, reactive, ref} from 'vue';
import {
  findAllByflag,
  savePanel,
  delPanel,
  countByFlag,
  findAllByGroupSysUserFlag,
  copyPanel
} from '/@/api/record/accpanel/data';
import {useUserStore, useUserStoreWidthOut} from '/@/store/modules/user';
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getCurrentAccountName,getThisIndexImg} from "/@/api/task-api/tast-bus-api";
import Edit from './popup/edit.vue'
import {
  filterAccListByAuth,
  getAdInfoDatas,
  getAllAccAuths
} from "/@/api/record/system/financial-settings";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {useTabs} from "/@/hooks/web/useTabs";
import {findByAccIdIyearGroupBy, findDataBase} from "/@/api/record/system/account";
import {useMessage} from "/@/hooks/web/useMessage";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const AStatisticCountdown = AStatistic.Countdown;
const ATabPane = ATabs.TabPane;
const { closeCurrent } =useTabs();
const username=ref('')
const dataSource123:any=ref( [])
const columns:any=ref( [
  {
    title: '显示名称',
    dataIndex: 'panelName',
  },
  {
    title: '科目公式',
    dataIndex: 'panelFormula',
  },{
    title: '取值方式',
    dataIndex: 'panelDataRange',
  }
])
const selectedRowKeys=ref([])
const selectedRowData=ref([])
const accPanelList=ref([])
const caozuoyuanlist=ref([])
const caozuoyuanselected=ref(String(useUserStoreWidthOut().getUserInfo.id))
const panelFlag=ref('1')
const panelFlagShow=ref(true)
const userStore = useUserStore();
const {createConfirm, createWarningModal} = useMessage();
// 这是示例组件
const database=ref(getCurrentAccountName(true));
const accId=ref(getCurrentAccountName(false));
const accIyear=ref('');
// 页面变量
const pageParameter=reactive({
  panelFlag:'1',
  iyear: '',
  sysuser: useUserStoreWidthOut().getUserInfo.id,
  companyCode: '',
  companyName: '',
  database: database.value,
  type: 'zz',
})
const windowHeight = (window.innerHeight - 350)
const visible=ref(false);


const [registerEditPage, { openModal: openEditPage }] = useModal()
const getAdObjInfoByCoCode = (value, type,accList) => {
  let list = accList.filter(item => item[type] == value)
  return list.length > 0 ? list[0] : null
}
async function findByDateBase() {
  // 获取所有账套信息 并过滤已授权的账套
  let accList = await getAdInfoDatas()
  // 获取当前用户的权限根据用户信息汇率
  let test1 = await getAllAccAuths({'userId': userStore.getUserInfo.id})
  let test2 = filterAccListByAuth(accList, test1)
  let codes = getAdObjInfoByCoCode(accId.value, 'accId',test2)
  if (codes != null) {
    pageParameter.companyCode = codes.coCode
    pageParameter.companyName = codes.accNameCn
  }
}

const findAll = async () => {
  pageParameter.iyear=accIyear.value

  // 先检查是否有数据，没有增加 ：预收账款、预付账款、应付账款、客户应收款、资金情况
  let noPanelName1=['预收账款','预付账款','应付账款','客户应收款','资金情况']
  let noPanelName=[]
  let accpanel=await useRouteApi(findAllByflag,{schemaName: database})(pageParameter)

  accpanel.forEach(v=>{
    if(v.panelName!==null){
      noPanelName.push(v.panelName)
    }
  })
  noPanelName1.forEach(async (v,i)=>{
    let a=[...new Set(noPanelName)].filter(item=>item==v)
    if(a.length==0 && pageParameter.panelFlag==='1'){
      let data={
        id:null,
        panelDataRange:'余额',
        panelName:v,
        panelFlag:'1',
        panelIyear:pageParameter.iyear,
        panelOrder: i+1,
        panelType:'zz'
      }
      await useRouteApi(savePanel,{schemaName:database})({params:data})
    }
  })

  setTimeout(async ()=>{
    let temp=await useRouteApi(findAllByflag,{schemaName: database})(pageParameter)
    accPanelList.value=temp
  },500)
  caozuoyuanlist.value=await findAllByGroupSysUserFlag()
  // findByDateBase()
}
async function dynamicAdReload(data) {
  await findDataBase(data.accId,data.year).then(async (item)=>{
    accIyear.value=item.accountYear
    accId.value=item.accountId
    database.value =item.accountMode
    pageParameter.database=item.accountMode
    pageParameter.iyear=data.year
    await findAll()
  })
}
// 类型下拉触发事件
function panelFlagChange(val) {
  pageParameter.panelFlag=val
  setUserName()
  findAll()
}
function setUserName() {
  username.value = caozuoyuanlist.value.filter( (x) =>{     return x.id=== String(caozuoyuanselected.value);   })[0].username
}
function czyChange() {
  pageParameter.sysuser=caozuoyuanselected.value
  setUserName()
  findAll()
}

// 触发修改弹框
const condClick =  () =>{
  if(accPanelList.value.length===10){
    return createConfirmPop('面板设置最大10条！')
  }
  openEdit('')
}

async function editBtn(data){
  openEdit(data)
}

async function openEdit(data){
  let order:any=[1,2,3,4,5,6,7,8,9,10]
  let panelOrder=accPanelList.value.length==0?0:accPanelList.value[accPanelList.value.length-1].panelOrder
  openEditPage(true, {
    data: data,
    database:database.value,
    accId:accId.value,
    order:order,
    maxorder:panelOrder+1,
    panelFlag:panelFlag.value,
    panelIyear:pageParameter.iyear,
    caozuoUnique:panelFlag.value==='2'?caozuoyuanselected.value:null,
  })
}


// 修改回调事件
const saveData =  async(data) =>{
  await useRouteApi(savePanel,{schemaName:database})({params:data})
  findAll()
}

const del =  async(data) =>{
  createConfirm({
    iconType: 'warning',
    title: '警告',
    content: '确定删除数据吗？',
    onOk: async () => {
      await useRouteApi(delPanel,{schemaName:database})({params:data})
      findAll()
    }
  })
}

function createConfirmPop(text) {
  createConfirm({
    iconType: 'warning',
    title: '警告',
    content: text,
    onOk: async () => {}
  })
}

async function openUpYear() {
  selectedRowKeys.value=[]
  selectedRowData.value=[]
  visible.value=true
  dataSource123.value=[]

  // 上年数据
  pageParameter.iyear=parseInt(accIyear.value)-1
  let list=await useRouteApi(findAllByflag,{schemaName: database})(pageParameter)
  list.forEach(a=>{
    a.key=a.id
    dataSource123.value.push(a)
  })
}

function onSelectChange(a,b) {
  selectedRowKeys.value=a
  selectedRowData.value=b
}

async function copySave() {
  pageParameter.iyear=accIyear.value
  // 当年数据
  let accpanel=await useRouteApi(findAllByflag,{schemaName: database})(pageParameter)
  visible.value=false

  let editData=[]
  let savveData=[]
  selectedRowData.value.forEach(a=>{
    // 判断上年的数据是否在本年存在，存在修改，不存在新增
    let temp=accpanel.filter(b=>b.panelName===a.panelName)
    if(temp.length>0){
      temp[0].panelBz=a.panelBz
      temp[0].panelBzUnit=a.panelBzUnit
      temp[0].panelDataRange=a.panelDataRange
      temp[0].panelFlag=a.panelFlag
      temp[0].panelFormula=a.panelFormula
      temp[0].panelIyear=accIyear.value
      temp[0].panelName=a.panelName
      temp[0].panelOrder=a.panelOrder
      temp[0].panelType=a.panelType
      temp[0].splitCode=a.splitCode
      temp[0].splitFuhao=a.splitFuhao
      editData.push(temp[0])
    }
    else{
      a.id=null
      a.panelIyear=accIyear.value
      savveData.push(a)
    }
  })

  let arr=editData.concat(JSON.parse(JSON.stringify(savveData)))
  // 添加复制数据
  await useRouteApi(copyPanel,{schemaName: database})(arr)
  findAll()
}
</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style>
<style scoped>
a {
  color: blue;
  text-decoration: none;
  cursor: pointer;
}
.app-container:nth-of-type(1) {
  background-color: #f2f2f2;
  padding: 10px 5px;
  margin: 10px 10px 5px;
  height: 100px;
}
.app-container:nth-of-type(2) {
  padding: 0px;
  margin: 5px 10px;
  background: #b4c8e3 !important;
}
:deep(.ant-select-selector) {
  border: none !important;
  border-bottom: 1px solid #bdb9b9 !important;
}
:deep(.ant-checkbox) {
  border: 1px solid #2f2a2a;
  border-radius: 4px;
}
:deep(.ant-radio){
  border: 1px solid;
  border-radius: 10px;
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
</style>
<style lang="less" scoped>
.group-btn-span{
  font-size: 14px;
}
.group-btn-span:hover{
  background-color: #0096c7;
  cursor: pointer;
}
.group-btn-span-special{
  font-size: 14px;
  display: inline-block;
  width: 120px;
  padding: 5px 10%;
}
.group-btn-span-special2{
  width: 120px;
  font-size: 14px;
  display: inline-block;
  padding: 5px 10%;
}

.group-btn-span-special2:hover{
  cursor: pointer;
}
.group-btn-span-special:hover{
  background-color: #0096c7;
  cursor: pointer;
  color: white;
}

.nc-hrb {
  height: calc(100% - 300px);

  .nc-hrb-top {
    :deep(.ant-row) {
      .ant-col {
        min-width: 18% !important;
        width: 18%;
        height: 80px;
        background-color: white;
        margin: 1%;
        border-radius: 4px 4px 4px 20px;

        div {
          height: 100%;
          display: inline-block;
        }

        div:nth-of-type(1) {
          width: 80px;
          background-color: #dbdfe8;
          padding: 20px;
          border-radius: 0 0 0 20px;

          .container-head-img {
            width: 40px;
            height: 40px;
            -webkit-user-drag: none;
          }
        }

        div:nth-of-type(2) {
          width: calc(100% - 80px);
          color: #545454;
          font-weight: bold;
          float: right;
          .span-blue {
            color: blue;
          }
          span:nth-of-type(1){
            padding: 5px;
            display: inline-block;
          }
          span:nth-of-type(3) {
            width: 68%;
            display: inline-flex;
            justify-content: flex-end;
          }
        }
      }
    }
  }

  .nc-hrb-middle {
    :deep(.ant-row) {
      .ant-col {
        height: 200px;
        background-color: white;
        margin: 1%;
        border-radius: 4px;
        padding: 5px;
        border-left: 3px solid slateblue;
        flex: 0 0 48%;
        max-width: 48%;

        div:nth-of-type(1) {
          span {
            font-weight: bold;
          }

          span:nth-of-type(1) {
            font-size: 16px;
            color: #009688;
          }

          span:nth-of-type(2) {
            float: right;
            font-size: 16px;
            cursor: pointer;
          }

          span:nth-of-type(2):hover {
            background-color: #128ecb;
            color: white;
          }

          span:nth-of-type(3),span:nth-of-type(4) {
            color: white;
            background-color: #009688;
            padding: 2px;
          }
        }

        div:nth-of-type(2) {
          display: flex;
          justify-content: space-between;
          padding: 10px 10px 5px;
          ul{
            width: 30%;
            li{
              font-weight: bold;
              color: #64aba4;
            }
            .li-title{
              font-size: 16px;
              color: black;
              border-bottom: 1px solid #afb0b0;
              margin-bottom: 10px;
            }
            li:hover:not(.li-title){
              cursor: pointer;
              color: #00bb00;
            }
          }
        }
      }
    }
  }
}
</style>
