<template>
      <div style="height:100%">
<!--        <A1></A1>-->
        <B1></B1>
      </div>
</template>
<script setup>

import PlatformManagerPage from "../views/PlatformManagerPage.vue"
import {inject, onMounted, provide, ref} from "vue";
import {deleteByIdApi, findAllApi, findByIdApi, saveApi} from "../../../../../../api/group/SysLogger";
import {usePlatformsStoreWidthOut} from "/@/store/modules/platforms";
import {useViewModel} from "../../../../../../utils/boozsoft/viewModel/viewModelUtils";
import MenuManagerPageTable from './MenuManager/Table.vue'
import MenuManagerPageLayout from './MenuManager/Layout.vue'

import A1 from './a.vue'
import B1 from './b.vue'

const platformList = ref([])
const ownPlatforms = ref([])

const viewProvider=inject('viewProvider')

const recordName = 'SystemMenu'

const {ncLogger,ncTask,ncAudit,useViewModelRef}=useViewModel({name:recordName})

onMounted(async ()=>{
  platformList.value=await usePlatformsStoreWidthOut().getPlatformListToNames()
})
const viewModel=useViewModelRef({
  state() {
    return {
      label: '角色平台权限管理',
      // label:'菜单管理',
      settings: {
        disableAccountPicker: true
      },
      showPlatformManagerPage:true,

      openPlatformManager(it) {
        viewModel.value.showPlatformManagerPage = false
        viewModel.value.currentPlatform = it
      },
      closePlatformManager() {
        viewModel.value.showPlatformManagerPage = true
      },
      platformList
    }
  },
  crud: {
    findAllApi,
    findByIdApi,
    saveApi,
    deleteByIdApi
  },
  columns: [
    {field: 'id', label: 'ID'},
    {field: 'errorinfo', label: '错误信息'},
    {field: 'appflag', label: '应用标识'},
    {field: 'type', label: '类型'},
    {field: 'exectime', label: '执行时间'},
    {field: 'ip', label: 'ip'},
    {field: 'creattime', label: '创建时间'},
    {field: 'method', label: '方法类型'},
    {field: 'chrome', label: '浏览器'},
    {field: 'creatby', label: '创建人'},
    {field: 'agent', label: '代理'},
    {field: 'title', label: '标题'},
    {field: 'commitdata', label: '提交数据'}
  ],
  crudFuns: [
    ['新增', params => {
      ncLogger.info('进入新增页面')
      ncTask.info({functionModule: '新增页面'})
      openEditPageByAddFun(params, viewModel)
    }],
    ['修改', params => {
      ncLogger.info('进入修改页面')
      ncTask.info({functionModule: '修改页面'})
      openEditPageByEditFun(params, viewModel)
    }],
    ['删除', params => {
      ncLogger.info('删除数据' + JSON.stringify(viewModel))
      deleteFun(params, viewModel)
    }],
    ['打印', params => {
      ncLogger.info('打印数据')
      printFun(params, viewModel)
    }],
    ['导出', params => {
      ncLogger.info('导出数据')
      excelFun(params, viewModel)
    }],
    ['导入', params => {
      ncLogger.info('倒入数据')
      importFun(params, viewModel)
    }]
  ],
  action: {
    async saveData(params) {
      const {reload} = viewModel.value.useTableParams[1]
      const [data, {editorPreData}] = params

      // 保存日志
      ncLogger.info('保存数据' + JSON.stringify(editorPreData.value))

      // 保存回滚记录
      ncAudit.info({data: [editorPreData.value]})

      // 删除任务记录
      ncTask.delete('SystemLogger')

      // 保存记录
      await viewModel.value.crud.saveApi(data)

      reload()
    },
    dynamicAdReload() {

    }
  }
})



onMounted(async () => {
  // 日志
  ncLogger.info('进入了菜单档案2页面')
})
</script>
