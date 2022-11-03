<template>
  <div class="p-4">
    <BasicTable
      :row-selection="{ type: 'checkbox' ,fixed: true}"
      title="菜单管理2"
      title-help-message="树形组件不能和序列号列同时存在"
      :columns="columns"
      :data-source="data"
      row-key="id"
      :indent-size="20"
    />
  </div>
</template>
<script lang="ts">
import {defineComponent, onMounted, reactive, toRefs} from 'vue'
import { BasicTable } from '/@/components/Table'
// import { getBasicColumns, getTreeTableData } from '/@/views/demo/table/tableData'
import { getDeptListById } from '/@/api/record/dept'
import { AppRouteModule, AppRouteRecordRaw } from '/@/router/types'
import { LAYOUT } from '/@/router/constant'
import { cloneDeep } from 'lodash-es'
import { LayoutMapKey } from '/@/router/helper/routeHelper'
import {getMenuAll} from "/@/api/admin/menu";
// import { defHttp } from '/@/utils/http/axios'

export function transformObjToRoute<T = AppRouteModule>(routeList: AppRouteModule[]): T[] {

  LayoutMap.set('LAYOUT', LAYOUT)

  routeList.forEach((route) => {
    if (route.component) {
      if ((route.component as string).toUpperCase() === 'LAYOUT') {
        route.component = LayoutMap.get(route.component as LayoutMapKey)
      } else {
        route.children = [cloneDeep(route)]
        route.component = LAYOUT
        route.name = `${route.name}Parent`
        route.path = ''
        const meta = route.meta || {}
        meta.single = true
        meta.affix = false
        route.meta = meta
      }
    }
    route.children && asyncImportRoute(route.children)
  })
  return (routeList as unknown) as T[]
}

export default defineComponent({
  components: { BasicTable },
  setup() {
    console.log(JSON.stringify(null))
    const state= reactive({
      data:[

      ]
    })
    // 2
    let dataFrom=async ()=>{
      state.data.push(...await (getMenuAll()))
    }
    // 3
    onMounted(()=>{
      dataFrom()
    })
    // 目前没有封装 暂时 需要走4步
    return {

      columns: [],
      // 4
      ...toRefs(state)
    }
  }
})
</script>
