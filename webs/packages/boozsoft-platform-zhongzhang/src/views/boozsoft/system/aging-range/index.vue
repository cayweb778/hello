<template>
  <div class="app-financial-info" :style="{height: (windowHeight-125)+'px'}">
    <div class="a-container-head">
      <div class="a-c-head-title">
        <img src="/img/menu/index_comprehensive_reimbursement.png">
        <h2>账龄区间设置</h2>
      </div>
      <div class="a-c-head-btns">
        <div class="ant-btn-group a-c-h-btns-group" style="min-width: 600px">
          <AccountPicker theme="one" @reloadTable="dynamicAdReload" style="width: 100%"/>
        </div>
      </div>
    </div>
    <div class="setting_box"></div>
    <div class="a-container-content">
      <a-tabs type="card" :tabBarGutter="0" v-model:activeKey="activeKey" tabPosition="left">
        <a-tab-pane key="CUSTOMER" :style="activeKey=='CUSTOMER'?{height: windowHeight+'px',watch: '30%',overflowY: 'auto'}:{}"
                    tab="客户账龄区间设置">
          <div class="a-container-content-two">
            <div class="acct-up">
              <span>功能描述</span>
              <div>
                <p>1.账龄区间必须依次按序号对总天数进行设置，且每次增加天数建议不低于30天;</p>
                <p>2.账龄区间可以随时进行设置,只反映在查询账龄分析表上,不对任何数据产生影响。</p>
              </div>
            </div>
            <div class="acct-down">
              <span>区间设置</span>
              <div>
                <a-table size="small" bordered :data-source="CusDataSource" :columns="Columns"
                         :pagination="false" :scroll="{ y  : tableHeight }">
                  <template #startDayNumber="{ text, record }">{{text+(text == ''?'':text.indexOf('天') != -1?'':`天`)}}</template>
                  <template #totalDayNumber="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key]"
                           class="editable-cell-input-wrapper">
                        <a-input v-model:value="editableData[record.key].totalDayNumber"
                                 @pressEnter="rowEdit(record.key,'end')"/>
                        <check-outlined class="editable-cell-icon-check"
                                        @click="rowEdit(record.key,'end')"/>
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ text || ' ' }}
                        <edit-outlined v-if="record.serialNumber !='10'" class="editable-cell-icon" @click="rowEdit(record.key,'start')"/>
                      </div>
                    </div>
                  </template>
                </a-table>
              </div>
            </div>
          </div>
        </a-tab-pane>
        <a-tab-pane key="SUPPLIER" :style="activeKey=='CUSTOMER'?{height: windowHeight+'px',watch: '30%',overflowY: 'auto'}:{}"
                    tab="供应商账龄区间设置">
          <div class="a-container-content-two">
            <div class="acct-up">
              <span>功能描述</span>
              <div>
                <p>1.账龄区间必须依次按序号对总天数进行设置，且每次增加天数建议不低于30天;</p>
                <p>2.账龄区间可以随时进行设置,只反映在查询账龄分析表上,不对任何数据产生影响。</p>
              </div>
            </div>
            <div class="acct-down">
              <span>区间设置</span>
              <div>
                <a-table size="small" bordered :data-source="SupDataSource" :columns="Columns"
                         :pagination="false" :scroll="{ y  : tableHeight }">
                  <template #startDayNumber="{ text, record }">{{text+(text != ''?' 天':'')}}</template>
                  <template #totalDayNumber="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key]"
                           class="editable-cell-input-wrapper">
                        <a-input v-model:value="editableData[record.key].totalDayNumber"
                                 @pressEnter="rowEdit(record.key,'end')"/>
                        <check-outlined class="editable-cell-icon-check"
                                        @click="rowEdit(record.key,'end')"/>
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ text || ' ' }}
                        <edit-outlined v-if="record.serialNumber !='10'" class="editable-cell-icon" @click="rowEdit(record.key,'start')"/>
                      </div>
                    </div>
                  </template>
                </a-table>
              </div>
            </div>
          </div>
        </a-tab-pane>
        <a-tab-pane key="PERSONAL" :style="activeKey=='CUSTOMER'?{height: windowHeight+'px',watch: '30%',overflowY: 'auto'}:{}"
                    tab="个人往来账龄区间设置">
          <div class="a-container-content-two">
            <div class="acct-up">
              <span>功能描述</span>
              <div>
                <p>1.账龄区间必须依次按序号对总天数进行设置，且每次增加天数建议不低于30天;</p>
                <p>2.账龄区间可以随时进行设置,只反映在查询账龄分析表上,不对任何数据产生影响。</p>
              </div>
            </div>
            <div class="acct-down">
              <span>区间设置</span>
              <div>
                <a-table size="small" bordered :data-source="PorDataSource" :columns="Columns"
                         :pagination="false" :scroll="{ y  : tableHeight }">
                  <template #startDayNumber="{ text, record }">{{text+(text != ''?' 天':'')}}</template>
                  <template #totalDayNumber="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key]"
                           class="editable-cell-input-wrapper">
                        <a-input v-model:value="editableData[record.key].totalDayNumber"
                                 @pressEnter="rowEdit(record.key,'end')"/>
                        <check-outlined class="editable-cell-icon-check"
                                        @click="rowEdit(record.key,'end')"/>
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ text || ' ' }}
                        <edit-outlined v-if="record.serialNumber !='10'" class="editable-cell-icon" @click="rowEdit(record.key,'start')"/>
                      </div>
                    </div>
                  </template>
                </a-table>
              </div>
            </div>
          </div>
        </a-tab-pane>
      </a-tabs>
    </div>
  </div>
</template>
<script setup="props, {emit}" lang="ts">
import {CheckOutlined, EditOutlined} from '@ant-design/icons-vue';
import {onMounted, reactive, ref, watch} from "vue";
import {Select as ASelect, Input as AInput, Radio as ARadio, Tabs as ATabs, Checkbox as ACheckbox,
  Button as AButton, message, Table as ATable, DatePicker as ADatePicker, Tree as ATree,
  Popconfirm as APopconfirm} from "ant-design-vue";
const ATabPane = ATabs.TabPane
const ASelectOption = ASelect.Option
const ACheckboxGroup = ACheckbox.Group
const ARadioGroup = ARadio.Group
const ATreeNode = ATree.TreeNode
const AMonthPicker = ADatePicker.MonthPicker
import {useMessage} from "/@/hooks/web/useMessage";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import router from "/@/router";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findAllByAccIdModel, saveModel} from "/@/api/record/system/aging-range";
import {cloneDeep} from "lodash-es";
import {hasBlank} from "/@/api/task-api/tast-bus-api";
import {JsonTool} from "/@/api/task-api/tools/universal-tools";
const {createWarningModal} = useMessage()

const windowHeight = (window.innerHeight)
const tableHeight = (document.documentElement.clientHeight-540)+'px'
const activeKey = ref('')
const dynamicInfo = ref({})
/************************************账龄区间************************************/
const editableData = reactive({});
const CusDataSource = ref([])
const SupDataSource = ref([])
const PorDataSource = ref([])
const Columns = [
  {
    title: '序号',
    key: 'serialNumber',
    dataIndex: 'serialNumber',
    align: 'center',
    width: '10%'
  },
  {
    title: '起止天数',
    key: 'startDayNumber',
    dataIndex: 'startDayNumber',
    align: 'center',
    slots: {customRender: 'startDayNumber'}
  },
  {
    title: '总天数',
    key: 'totalDayNumber',
    dataIndex: 'totalDayNumber',
    align: 'center',
    width: '20%',
    slots: {customRender: 'totalDayNumber'},
  }
]
const createNeatRow = () => {
  return {
    id: null,
    key: '',
    serialNumber: '',
    agingModel: '',
    accId: '',
    startDayNumber: '',
    totalDayNumber: '',
  }
}
async function assemblyRow(list:any) {
  let rowList = []
  if (list.length > 0){
    list.forEach((item,index)=>{
      let row =  createNeatRow()
      row['id'] = item.id
      row['key'] = item.id+''
      row['serialNumber'] = (index+1)+''
      row['agingModel'] = item.agingModel
      row['accId'] = item.accId
      row['startDayNumber'] = item.startDayNumber
      row['totalDayNumber'] = item.totalDayNumber
      rowList.push(row)
    })
  }
  if (list.length < 10){
    for (let i = list.length;i < 10;i++){
      let row =  createNeatRow()
      row['key'] = i+''
      row['serialNumber'] = (i+1) +''
      row['agingModel'] = activeKey.value
      row['accId'] = dynamicInfo.value?.accId
      // 第一次
      if (rowList.length > 0 && i == list.length) row['startDayNumber'] = `${(parseInt(rowList[rowList.length-1].totalDayNumber)+1)}天以上`
      rowList.push(row)
    }
  }
  return rowList
}
const checkAndModifyData =  (key) => {
  try {
    if (hasBlank(editableData[key].totalDayNumber)) throw new Error('总天数为必填项且只能为正整数！')
    let xNum = parseInt(editableData[key].serialNumber)
    if (xNum == 1){
      if (parseInt(editableData[key].totalDayNumber) < 30) throw new Error('每次增加总天数建议不低于30天！')
        editableData[key].startDayNumber = `0-${ editableData[key].totalDayNumber}`
    }else {
      // 获取上一次
      let currList =  currDataSource()
      let previous = currList.value.filter(item => (xNum-1) == item?.serialNumber)[0]
      let prevNum = previous?.totalDayNumber
      if (hasBlank(prevNum))throw new Error(`序号列【${xNum-1}】总天数为必填项且只能为正整数！`)
      if ((parseInt(editableData[key].totalDayNumber) - parseInt(prevNum)) < 30) throw new Error('每次增加总天数建议不低于30天！')
      editableData[key].startDayNumber = `${parseInt(prevNum)+1}-${ editableData[key].totalDayNumber}`
      let nextNum = parseInt(xNum)+1
      // 如果改变的是中间的 重置后面 保留
      if (nextNum != 10){
        let nexts = currList.value.filter(item => nextNum == item?.serialNumber)[0]
        nexts.startDayNumber = (parseInt(editableData[key].totalDayNumber)+1)+'天以上'
      }
    }
    return true
  } catch (e) {
    createWarningModal({content: e.message});
    return false
  }
}
const checkPrevFilledIn = (key,currList) => {
  let xNum = currList.value.filter(item => key == item?.key)[0].serialNumber
  if (xNum != 1){ //上一行是否填写
    let previous = currList.value.filter(item => (xNum-1) == item?.serialNumber)[0]
    if (hasBlank(previous?.totalDayNumber)) return true;
  }
  return false;
}
const rowEdit = async (key,type) => {
  let currList = currDataSource()
  if (type=='start'){
    // 查看上一行是否填写
    if (!checkPrevFilledIn(key,currList)){
      editableData[key] = cloneDeep(currList.value.filter(item => key === item?.key)[0]);
    }else {
      createWarningModal({title: '温馨提示',content: '请先完善上面空白行！'})
    }
  }else {
    editableData[key].totalDayNumber = editableData[key].totalDayNumber.replaceAll(/[^\d]/g,'')
    if (checkAndModifyData(key)){
      let res =  await save(editableData[key])
      if (res != null) {
        editableData[key].key = res?.id
        editableData[key].id = res?.id
        Object.assign(currList.value.filter(item => key === item.key)[0], editableData[key]);
        delete editableData[key];
        message.success('已进行异步更新！')
      }
    }else {
      delete editableData[key];
    }
  }
}
function currDataSource(){
  return activeKey.value === 'CUSTOMER'?CusDataSource:activeKey.value === 'SUPPLIER'?SupDataSource:PorDataSource
}
async function query(type:string) {   // 查询数据库
  return await useRouteApi(findAllByAccIdModel,{schemaName: dynamicInfo.value?.accountMode})({ accId: dynamicInfo.value?.accId,model: type})
}
async function save(data:any) {   // 查询数据库
  return await useRouteApi(saveModel,{schemaName: dynamicInfo.value?.accountMode})(data)
}

watch(() => activeKey.value, async () => {// 监听切换
  let list = await query(activeKey.value)
  if (activeKey.value === 'CUSTOMER') {
    CusDataSource.value = await assemblyRow(list)
  } else if (activeKey.value === 'SUPPLIER') {
    SupDataSource.value = await assemblyRow(list)
  } else if (activeKey.value === 'PERSONAL') {
    PorDataSource.value = await assemblyRow(list)
  }
});
/************************************参数精度************************************/
const dynamicAdReload =async (obj) => {
  dynamicInfo.value = obj
  await query(activeKey.value)
}
onMounted(async () => {
  // 获取当前类型
  let upPath =  router.currentRoute.value.path.split('\/')[3]
  if (upPath.startsWith('gys')){ // 供应商
    activeKey.value = 'SUPPLIER'
  }else if(upPath.startsWith('geren')){ // 客户
    activeKey.value = 'PERSONAL'
  }else {
    activeKey.value = 'CUSTOMER'
  }
})
</script>

<style lang="less" scoped="scoped">
:deep(.ant-tabs-tab:before) {
  background-image: none !important;
}
.app-financial-info {
  background: rgb(241, 239, 239);
  border-radius: 5px;
  margin: 10px;
  padding: 10px 20px;
  height: calc(100% - 20px);
  .a-container-head{
    position: relative;
    .a-c-head-title{
      text-align: left;
      height: 45px;
      border-bottom: 1px solid #d8d4d4;
      img{
        float: left;
        margin: 0 10px;
      }
      h2 {
        font-weight: bold;
        font-size: 20px;
      }
    }
    .a-c-head-btns{
      display: block;
      position: absolute;
      right: 0;
      top: 0;
      .a-c-h-btns-group > button{
        color: blue;
      }
    }
  }

  .a-container-content{
    margin-top: 10px;
    height: calc(100% - 60px);
    background-color: white;
    :deep(.ant-tabs-card){
      height: 100%;
      .ant-tabs-nav{
        width: 180px;
        .ant-tabs-nav-wrap{
          background-color: #e3f3f7;
        }
        .ant-tabs-tab,.ant-tabs-tab-active{
          height: 80px;
          line-height: 80px;
          color: black;
          font-weight: bold;
          background-color: #e3f3f7;
          padding-right: 22px;
        }
        .ant-tabs-tab-active{
          color: blue;
          background-color: white;
        }
        .ant-tabs-tab:before {
          content: "";
          height: 50px;
          width: 50px;
          top: 15%;
          left: 15%;
          display: block;
          background-repeat: no-repeat;
          background-size: 100% 100%;
        }
        .ant-tabs-tab:nth-of-type(1):before {
          background-image: url('/img/menu/financial-info-menu/tab01.png');
        }
        .ant-tabs-tab:nth-of-type(2):before {
          background-image: url('/img/menu/financial-info-menu/tab02.png');
        }
        .ant-tabs-tab:nth-of-type(3):before {
          background-image: url('/img/menu/financial-info-menu/tab03.png');
        }
        .ant-tabs-tab:nth-of-type(4):before {
          background-image: url('/img/menu/financial-info-menu/tab04.png');
        }
        .ant-tabs-tab:nth-of-type(5):before {
          background-image: url('/img/menu/financial-info-menu/tab05.png');
        }
        .ant-tabs-tab:nth-of-type(6):before {
          background-image: url('/img/menu/financial-info-menu/tab06.png');
        }
        .ant-tabs-tab:nth-of-type(7):before {
          background-image: url('/img/menu/financial-info-menu/tab03.png');
        }
        .ant-tabs-tab:nth-of-type(8):before {
          background-image: url('/img/menu/financial-info-menu/tab02.png');
        }
        .ant-tabs-tab:nth-of-type(9):before {
          background-image: url('/img/menu/financial-info-menu/tab01.png');
        }
      }
      .ant-tabs-content{
        height: 100%;
        background-color: white;
        .a-container-content-one{
          overflow-y: auto;
          .acco-btn{
            width: 1060px;
            text-align: right;
            padding: 10px 0 0;
            .ant-btn{
              margin:0 5px;
            }
          }
          .acco-sideline{
            position: relative;
            border: 1px #acabab solid;
            width: 1060px;
            margin: 10px 1% 20px;

            > span{
              position: absolute;
              top: -12px;
              left: 50px;
              background: white;
              padding: 0 15px;
            }
            .ant-checkbox-inner,.ant-radio-inner{
              border-color: #4c4c4c !important;
            }
            .acco-sideline-content-one{
              > div{
                width: 45%;
                display: inline-block;
                padding: 10px 50px;
                .ant-select-selector{
                  border:none;
                  border-bottom: 1px slategrey solid;
                }
                .ant-select-disabled  .ant-select-selector{
                  background: white;
                  color: black;
                }
                .against-input-mark{
                  border:none;
                  border-bottom: 1px slategrey solid;
                  background-color: white;
                  color: black;
                }
              }
            }
            .acco-sideline-content-two{
              width: 100%;
              margin: 20px 50px;
              .ant-row{
                .ant-col{
                  margin: 10px 0;
                  .ant-checkbox-wrapper-disabled{
                    > span{
                      color: black;
                    }
                  }
                }
              }
            }
            .acco-sideline-content-there{
              width: 100%;
              margin: 20px 50px;
              ul{
                li{
                  padding: 5px 0;
                  .ant-radio-group{
                    width: 80%;
                    .ant-radio-wrapper{
                      margin: 0 8%;
                    }
                    .ant-radio-wrapper-disabled{
                      > span{
                        color: black;
                      }
                    }
                  }
                }
              }
            }
          }
        }
        .a-container-content-two{
          margin-top: 50px;
          .acco-btn{
            width: 1060px;
            text-align: right;
            padding: 10px 0 0;
            .ant-btn{
              margin:0 5px;
            }
          }
          .a-table-font-size-14 td,
          .a-table-font-size-14 th {
            font-size: 14px !important;
            padding: 2px 8px !important;
          }
          .ant-select-disabled > .ant-select-selector{
            color: #252525 !important;
            background: white !important;
          }
          .acct-up{
            position: relative;
            border: 1px #acabab solid;
            width: 1060px;
            margin: 10px 1% 20px;

            > span{
              position: absolute;
              top: -12px;
              left: 50px;
              font-size: 15px;
              background: white;
              padding: 0 15px;
            }
            > div{
              margin: 15px;
              color: black;
            }
            .acctd-span{
              margin: 0 10px;
            }
            .ant-select-selector,.ant-input{
              border:none;
              border-bottom: 1px slategrey solid;
              text-align: center;
            }
            .ant-input{
              pointer-events: none;
            }
          }
          .acct-down{
            position: relative;
            border: 1px #acabab solid;
            width: 1060px;
            margin: 10px 1% 20px;

            > span{
              position: absolute;
              top: -12px;
              left: 50px;
              font-size: 15px;
              background: white;
              padding: 0 15px;
            }

            >div{
              text-align: center;
              margin: 10px 20px;
              tr > th{
                text-align: center;
                font-weight: bold;
              }
              .ant-input{
                width: 85%;
              }
            }
            .ant-select-selector,.ant-input{
              border:none;
              border-bottom: 1px slategrey solid;
            }
          }
        }
        .ant-table-align-left{
          text-align: center!important;
        }
      }
    }
  }
}

</style>
