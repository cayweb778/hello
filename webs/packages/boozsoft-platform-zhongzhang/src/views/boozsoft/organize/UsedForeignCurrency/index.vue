<template>
  <RecordLayout>
<!--    <div style="margin-left:25px">
      <AccountPicker theme="one" @reloadTable="recordData.dynamicAdReload" />
    </div>-->
    <DefaultView ></DefaultView>
<!--    <CardView v-else></CardView>-->
  </RecordLayout>
</template>
<script setup lang="ts">
import {provide, reactive, ref} from 'vue'

import {
  defineRecrodData,
  useNcAudit,
  useNcLogger,
  useNcTask
} from "/@/utils/boozsoft/record/recordUtils";

import RecordLayout from './record-layout/RecordLayout.vue'
import DefaultView from "./DefaultView.vue";
import CardView from "./other/CardView.vue";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";

import {findAllApiOrg, findByIdApiOrg, saveApiOrg, deleteByIdApiOrg} from '/@/api/boozsoft/group/UsedForeignCurrency'

const recordName = 'UsedForeignCurrency'
const {closeCurrent} = useTabs(router);
const mark = usePlatformsStore().getCurrentPlatformId
// // 日志
// ncLogger.info('进入了日志档案页面')
// // // 任务管理器
// ncTask.info('')
// // // 审计（回滚）
// ncAudit.info('')
const {ncLogger} = useNcLogger({recordName})
const {ncTask} = useNcTask({recordName})
const {ncAudit} = useNcAudit({recordName})

import {
  openEditPageByAddFun,
  openEditPageByEditFun,
  deleteFun,
  printFun,
  excelFun,
  importFun
} from "./crudFun";
import {saveTableAuditApi} from "/@/api/group/TableAudit";
import {saveTaskApi} from "/@/api/record/system/task";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {usePlatformsStore} from "/@/store/modules/platforms";
const pageParameter = ref({
  showRulesSize: 'MIN',
})
provide('pageParameter', pageParameter)
const recordData = ref(defineRecrodData({
  crud: [
    findAllApiOrg,
    findByIdApiOrg,
    (params) => {
      ncAudit.info(params)
      saveApiOrg(params).then((res)=>{
        const {reload} = recordData.value.useTableParams[1]
        reload()
      })
    },
    deleteByIdApiOrg
  ],
  columns: [
/*     {field:'id',label:'undefined'},*/
 {field:'orgUniqueCode',label:'所属组织', slots: {customRender: 'orgUniqueCode'}},
 {field:'beiyong1',label:'所属国家(地区)'},
 {field:'foreignCode',label:'外币国际代码',width: 120},
 {field:'foreignName',label:'外币名称'},
{field:'currencyUnit',label:'货币单位'},
{field:'foreignSimpName',label:'换算规则'},
/* {field:'accountId',label:'账套ID'},*/
/* {field:'beiyong1',label:'undefined'},
 {field:'beiyong2',label:'undefined'},
 {field:'beiyong3',label:'未设置'}*/
  ],
  crudFuns: [
    ['新增', params => {
      ncLogger.info('')
      ncTask.info('')
      openEditPageByAddFun(params, recordData)
    }],
    ['修改', params => {
      ncLogger.info('修改数据')
      ncTask.info('')
      openEditPageByEditFun(params, recordData)
    }],
    ['删除', params => {
      ncLogger.info('删除数据')
      deleteFun(params, recordData)
    }],['退出', params => {
      ncLogger.info('退出常用外币')
      closeCurrent(),router.push(`/${mark == 1003?'system':'origin'}/home/welcome`)
      //
    }]/*,
    ['打印', params => {
      ncLogger.info('打印数据')
      printFun(params, recordData)
    }],
    ['导出', params => {
      ncLogger.info('导出数据')
      excelFun(params, recordData)
    }],
    ['导入', params => {
      ncLogger.info('倒入数据')
      importFun(params, recordData)
    }]*/
  ],
  action: {
    async taskManager(params) {
      // 写入记录
      // 移除记录
    },
    async saveData(params) {
      // 保存记录
       await recordData.value.crud[2](params)
      // 保存回滚记录
     /* const {reload} = recordData.value.useTableParams[1]
      reload()*/
    },
    dynamicAdReload(){

    }
  }
}))


provide('recordData', recordData)
</script>
<style scope src="../../../../assets/styles/generate-code-record-style/record.less" lang="less" scoped></style>
<style scoped>
:deep(.app-container):nth-of-type(1) {
  background-color: #f2f2f2;
  padding: 10px 5px;
  margin: 10px 10px 5px;
}
:deep(.app-container):nth-of-type(2) {
  padding: 0px;
  margin: 5px 10px;
  background: #b4c8e3 !important;
}
</style>
