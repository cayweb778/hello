<template>
  <div class="app-container">
    <div class="app-container-top">
      <div class="app-container-head" style="text-align: center;">
        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">
        <div class="container-head-title">
          <b class="noneSpan">数据精度设置</b>
        </div>
        <div class="container-head-account" style="line-height: 40px;">
          <AccountPicker theme="one" @reloadTable="dynamicAdReload" style="width: 100%"/>
        </div>
      </div>
    </div>
    <div class="app-container-bottom" :style="{height: (windowHeight-40)+'px',overflowY: 'auto'}">
      <div class="ac-bottom-left">
        <div class="ant-btn-group">
          <button
            type="button"
            style="background-color: #0096c7;"
            class="ant-btn ant-btn-me"
            @click="closeCurrent(),router.push(`/`)"
          ><span>退出</span></button>
        </div>
        <br/>
        <div class="special-border-div">
          <span>设置说明</span>
          <div>
            1、来源于系统的控制参数不允许删除，可设置相关参数；<br/>
            2、小数点的位数设置区间限定为0到9的正整数，整数位数区间限定为1到15的正整数。
          </div>
        </div>
        <div class="special-border-div">
          <span>精度设置</span>
          <div>
            <a-button class="editable-add-btn" type="primary" size="small" @click="handleCsAdd" style="margin-left: 46%;">+</a-button>
            <Table size="small" bordered :data-source="CsDataSource" :columns="CsColumns" :pagination="false" :scroll="{ y  : tableHeight }">
              <template #paraName="{ text, record }">
                <div class="editable-cell">
                  <div v-if="editableData[record.key] && editableData[record.key].type == 'CS' && editableData[record.key].beiyong2 != '0'" class="editable-cell-input-wrapper">
                    <a-input v-model:value="editableData[record.key].paraName" @pressEnter="save(record.key,'CS')" style="width: 80%" />
                    <check-outlined class="editable-cell-icon-check" @click="save(record.key,'CS')" />
                  </div>
                  <div v-else class="editable-cell-text-wrapper">
                    {{ text || ' ' }}
                  </div>
                </div>
              </template>
              <template #beiyong1="{ text, record }">
                <div class="editable-cell">
                  <div v-if="editableData[record.key]   && editableData[record.key].type == 'CS'" class="editable-cell-input-wrapper">
                    <a-select v-model:value="editableData[record.key].beiyong1" style="width: 80%">
                      <a-select-option v-for="value in (8+1)" :value="value-1">{{value-1}}</a-select-option>
                    </a-select>
                    <check-outlined class="editable-cell-icon-check" @click="save(record.key,'CS')" />
                  </div>
                  <div v-else class="editable-cell-text-wrapper">
                    {{ text || ' ' }}
<!--                    <edit-outlined v-if="record.groupControl == '0'" class="editable-cell-icon" @click="edit(record.key,'CS')" />-->
                  </div>
                </div>
              </template>
              <template #beiyong2="{ text, record }">
                <div class="editable-cell">
<!--                  <div v-if="editableData[record.key]   && editableData[record.key].type == 'CS'" class="editable-cell-input-wrapper">
                    <a-select v-model:value="editableData[record.key].beiyong2" style="width: 80%">
                      <a-select-option v-for="value in (8+1)" :value="value-1">{{value-1}}</a-select-option>
                    </a-select>
                    <check-outlined class="editable-cell-icon-check" @click="save(record.key,'CS')" />
                  </div>
                  <div v-else class="editable-cell-text-wrapper">-->
                    {{ text == '0'?'系统':'自定义' }}
                    <!--                    <edit-outlined v-if="record.groupControl == '0'" class="editable-cell-icon" @click="edit(record.key,'CS')" />-->
<!--                  </div>-->
                </div>
              </template>
              <template #decimalPlaces="{ text, record }">
                <div class="editable-cell">
                  <div v-if="editableData[record.key]   && editableData[record.key].type == 'CS'" class="editable-cell-input-wrapper">
                    <a-select v-model:value="editableData[record.key].decimalPlaces" style="width: 80%">
                      <a-select-option v-for="value in (8+1)" :value="value-1">{{value-1}}</a-select-option>
                    </a-select>
                    <check-outlined class="editable-cell-icon-check" @click="save(record.key,'CS')" />
                  </div>
                  <div v-else class="editable-cell-text-wrapper">
                    {{ text || ' ' }}
<!--                    <edit-outlined v-if="record.groupControl == '0'" class="editable-cell-icon" @click="edit(record.key,'CS')" />-->
                  </div>
                </div>
              </template>
              <template #controlManner="{ text, record }">
                <div class="editable-cell">
                  <div v-if="editableData[record.key] && editableData[record.key].type == 'CS' /*&& record.groupControl == '0'*/" class="editable-cell-input-wrapper">
                    <a-select v-model:value="editableData[record.key].controlManner" style="width: 80%">
                      <a-select-option value="1">严格控制</a-select-option>
                      <a-select-option value="0">不控制</a-select-option>
                      <check-outlined class="editable-cell-icon-check" @click="save(record.key,'CS')" />
                    </a-select>
                  </div>
                  <div v-else class="editable-cell-text-wrapper">
                    {{ (text=='1'?'严格控制':'不控制') || ' ' }}
<!--                    <edit-outlined v-if="record.groupControl == '0'" class="editable-cell-icon" @click="edit(record.key,'CS')" />-->
                  </div>
                </div>
              </template>
<!--                                <template #groupControl="{ text, record }">
                                                     <div class="editable-cell">
                                  <div v-if="editableData[record.key] && editableData[record.key].type == 'CS'" class="editable-cell-input-wrapper">
                                    <a-select v-model:value="editableData[record.key].groupControl" style="width: 80%">
                                      <a-select-option value="1">管控</a-select-option>
                                      <a-select-option value="0">不管控</a-select-option>
                                    </a-select>
                                    <check-outlined class="editable-cell-icon-check" @click="save(record.key,'CS')" />
                                  </div>
                                  <div v-else class="editable-cell-text-wrapper">
                                    {{ (text=='1'?'管控':'不管控') || ' ' }}
                                    <edit-outlined class="editable-cell-icon" @click="edit(record.key,'CS')" />
                                  </div>
                                </div>
                                </template>-->
              <template #operation="{ record }">
                <edit-outlined class="editable-cell-icon" @click="edit(record.key,'CS')" />
                &emsp;
                <Popconfirm
                  v-if="CsDataSource.length"
                  title="你确定删除当前数据精度吗?"
                  @confirm="onDelete(record.key,'CS')"
                >
                  <DeleteOutlined   class="editable-add-btn" v-show="record.beiyong2 != '0'"/>
<!--                  <a-button class="editable-add-btn" type="primary" size="small">-</a-button>-->
                </Popconfirm>
              </template>
            </Table>
          </div>
        </div>
      </div>
      <div class="ac-bottom-right">
<!--        <div class="special-border-div">
          <span>新增操作</span>
          <div>
            新增年度会计期间时，年度启用期间为第一个会计期间， 可修改，期间数量允许修改
          </div>
        </div>
        <div class="special-border-div">
          <span>修改操作</span>
          <div>
            期初余额或凭证表有数据的年度,不允许修改会计期间的启用期间、期间数量和结束日期
            会计期间的结束日期必须介于20日到31日之间，默认使用自然月
            季度期间的开始日期必须大于上-个季度区间的结束日期，默认为自然季度起始日期
          </div>
        </div>
        <div class="special-border-div">
          <span>删除操作</span>
          <div>
            系统只有一个年度的会计期间数据时,不允许删除该年度会计期间
            当年期初余额和凭证表中有数据的年度，不允许删除该年度会计期间
          </div>
        </div>-->
      </div>
    </div>
  </div>
</template>
<script setup="props, {emit}" lang="ts">
import {reactive, ref} from 'vue'
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {
  Select as ASelect,
  Input as AInput,
  Button as AButton,
  message,Popconfirm,
  Table,
} from "ant-design-vue";

const ASelectOption = ASelect.Option
import {CheckOutlined, EditOutlined,DeleteOutlined} from '@ant-design/icons-vue';
const {closeCurrent} = useTabs(router);
const windowHeight = (window.innerHeight)
import {getThisIndexImg, hasBlank} from "/@/api/task-api/tast-bus-api";
import {
  deleteCsData,
  parameterAccuracyDatas,
  saveCsData
} from "/@/api/record/system/financial-settings";
import {useMessage} from "/@/hooks/web/useMessage";
import {cloneDeep} from "lodash-es";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
const {createWarningModal,createSuccessModal,createConfirm} = useMessage();
const dynamicAdReload = (e) => {
  databaseCn.value = e.accId
  database.value = e.accountMode
  databaseYear.value = e.year
  message.loading("查询中请稍候。。。")
  CsDataSource.value = []
  reloadTable(e.target)

}
const editableData = reactive({});
const databaseCn = ref('')
const database = ref('')
const databaseYear = ref('')

const tableHeight = (document.documentElement.clientHeight-540)+'px'


const reloadTable = async (o) => {
  //let dFlag = o.independent == '1'
  CsDataSource.value = []
  let res1 =  await useRouteApi(parameterAccuracyDatas,{schemaName:database.value})({'tenantId':database.value})
  if(null != res1){
    res1.forEach((item,index)=>{
      item.key = `${index}`
      item.serialNumber = `${index+1}`
      //item.controlManner=dFlag?'0':'1'
      //  item.groupControl= true?'1':'0' //ifModify.value
      CsDataSource.value.push(item)
    })
  }
}
const edit = (key: string,type:string) => {
  editableData[key] = cloneDeep(CsDataSource.value.filter(item => key === item.key)[0]);
  editableData[key].type = 'CS'
}

const save = async (key: string,type:string) => {
  let data = editableData[key]
  let handle = null
  Object.assign(CsDataSource.value.filter(item => key === item.key)[0], editableData[key]);
  if (checkCsData(data)){
    //data.foreignName = data.foreignName.replace('↵','').trim()
    let res = await useRouteApi(saveCsData,{schemaName:database.value})(data)
    if (res != null){
      message.success('数据精度更新成功已同步！')
      await reloadTable({}  )
    }else {
      message.error('数据精度更新失败！')
    }
    delete editableData[key];
  }
}
const onDelete = async (key) => {
  let data = CsDataSource.value.filter(item => item.key === key)[0]
  if (data.id == null || data.id == ''){ // 空白行删除
    CsDataSource.value = CsDataSource.value.filter(item => item.key !== key);
  }else {
    let res = await useRouteApi(deleteCsData,{schemaName:database.value})(data)
    if (res == null){
      message.success('数据精度删除成功已同步！')
    }else {
      message.error('数据精度删除失败！')
    }
  }
}
/************************************参数精度************************************/
const CsDataSource =ref(
  [{
    key: '0',
    serialNumber:'1',
    paraNum:'',
    paraName:'',
    beiyong1:'',
    beiyong2:'',
    decimalPlaces:'',
    controlManner:'',
   // groupControl:'',
  }])
const CsColumns =[{
  title: '序号',
  key: 'serialNumber',
  dataIndex: 'serialNumber',
  align: 'center',
  width: '8%'
}, {
  title: '操作',
  dataIndex: 'operation',
  width: '15%',
  align: 'center',
  slots: { customRender: 'operation' },
}, {
  title: '控制参数',
  key: 'paraName',
  dataIndex: 'paraName',
  align: 'center',
  width: '22%',
  slots: { customRender: 'paraName' },
}, {
  title: '整数位数',
  key: 'beiyong1',
  dataIndex: 'beiyong1',
  align: 'center',
  width: '15%',
  slots: { customRender: 'beiyong1' },
},{
  title: '小数点位数',
  key: 'decimalPlaces',
  dataIndex: 'decimalPlaces',
  align: 'center',
  width: '15%',
  slots: { customRender: 'decimalPlaces' },
}, {
  title: '控制方式',
  key: 'controlManner',
  dataIndex: 'controlManner',
  align: 'center',
  width: '15%',
  slots: { customRender: 'controlManner' },
},{
  title: '来源',
  key: 'beiyong2',
  dataIndex: 'beiyong2',
  align: 'center',
  slots: { customRender: 'beiyong2' },
},/* {
    title: '管控',
    key: 'groupControl',
    dataIndex: 'groupControl',
    align: 'center',
    slots: { customRender: 'groupControl' },
  },*/
]

const handleCsAdd = ()=>{
  let a = null == CsDataSource.value?0:CsDataSource.value.length
  const newData = {
    key: `${a}`,
    serialNumber: `${a+1}`,
    paraNum:'',
    paraName:'',
    beiyong1:'',
    beiyong2:'1',
    decimalPlaces:'2',
    controlManner:true?'1':'0',
    accountId: databaseCn.value,
    tenantId: database.value
   // groupControl: dFlag?'0':'1',
  };
  CsDataSource.value.push(newData)
}

const checkCsData = (data)=>{
  try {
    if (data.paraName == ''){
      throw new Error('该控制参数不能为空！')
    }
    if (CsDataSource.value.filter(item => item.paraName.indexOf(data.paraName) != -1).length > 1){
      data.paraName = ''
      throw new Error('该控制参数已存在！')
    }
    return true
  }catch (e) {
    createWarningModal({content: e.message});
    return false
  }
}

/************************************参数精度************************************/
</script>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>
<style scoped="scoped" lang="less">
.app-container {
  padding: 0px;
  margin: 10px 10px 5px;

  .app-container-bottom {
    display: inline-flex;
    justify-content: space-between;
    width: 100%;

    .special-border-div {
      position: relative;
      border: 1px #acabab solid;
      margin: 20px 1%;

      > span {
        position: absolute;
        top: -12px;
        left: 50px;
        font-size: 15px;
        background: white;
        padding: 0 15px;
      }

      > div {
        margin: 15px;

        ul {
          li {
            display: inline-block;
            width: 20%;
            font-weight: bold;
            min-width: 240px;

            span:nth-of-type(1) {
              color: #81817d;
            }

            span:nth-of-type(2) {
              color: black;
              margin-left: 2em;
            }
          }
        }
      }
    }

    .ac-bottom-left {
      width: 60%;
      height: auto;
      padding: 1.2% 2%;

      .ant-btn-group {
        float: right;

        .ant-btn-me {
          margin-right: 10px;
        }
      }

      .ant-input, :deep(.ant-select-selector:not(.ant-input)) {
        border: none;
        border-bottom: solid 1px rgb(191, 191, 191) !important;
      }

    }

    .ac-bottom-right {
      width: 40%;
      height: auto;
      padding: 2.5% 4%;
    }
  }
}
</style>
