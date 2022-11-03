<template>
  <RecordLayout>
    <div style="margin-left:25px">
      <AccountPicker theme="one" @reloadTable="recordData.dynamicAdReload" />
    </div>
    <DefaultView v-if="recordData.activeKey"></DefaultView>
    <CardView v-else></CardView>
  </RecordLayout>
</template>
<script setup lang="ts">
import {provide, ref} from 'vue'

import {defineRecrodData} from "/@/utils/boozsoft/record/recordUtils";

import RecordLayout from './record-layout/RecordLayout.vue'
import DefaultView from "./DefaultView.vue";
import CardView from "./other/CardView.vue";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";

import {findAllApi, findByIdApi, saveApi, deleteByIdApi} from '/@/api/boozsoft/group/$$recordName$$'


function useNcLogger(){
  return {
    ncLogger:{
      info:function (title){
        saveApi({
          type: '0',
          title,
          creatby: '木子桉易洋',
          creattime: '2021-09-20',
          ip: '198.0.2.1',
          agent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36 Edg/93.0.961.38',
          chrome: '/goods/merge/buy',
          method: 'GET',
          commitdata: 'amount=%5B1%5D',
          exectime: '59',
          errorinfo: '',
          appflag: 'pig',

        })
      }
    }
  }
}

function useNcTask(){
  return {
    ncTask:{
      info:function (title){
        saveTaskApi({
          params:{
            caozuoUnique:'333',
            time:'333',
            functionModule:'333',
            recordNum:'333',
            state:'333',
            iyear:'333',
            imonth:'333',
            method:'333',
          }
        },null)
      }
    }
  }
}

function useNcAudit(){
  return {
    ncAudit:{
      info:function (arr){
        // arr.map(it=>{
        //   return {
        //     data:'{id：1,name："张三"}',
        //     operatMode:'修改',
        //     createTime:'2020：1211：12：12：00',
        //     author:'木子桉易洋',
        //     uniqueCode:'1',
        //     tableName:'accvoucher',
        //   }
        // })
        saveTableAuditApi({data:JSON.stringify(arr)})
      }
    }
  }
}


















const {ncLogger}=useNcLogger()
const {ncTask}=useNcTask()
const {ncAudit}=useNcAudit()






// 日志
ncLogger.info('进入了$$recordName$$页面')
// // 任务管理器
ncTask.info('')
// // 审计（回滚）
ncAudit.info('')

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

const recordData = ref(defineRecrodData({
  crud: [
    findAllApi,
    findByIdApi,
    (params) => {
      ncAudit.info(params)
      saveApi(params)
    },
    deleteByIdApi
  ],
  columns: [
    $$recordPropties$$
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
    }],
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
    }]
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
      const {reload} = recordData.value.useTableParams[1]
      reload()
    },
    dynamicAdReload(){

    }
  }
}))


provide('recordData', recordData)
</script>
<style scope src="../../../../assets/styles/generate-code-record-style/record.less" lang="less"></style>