<template>
  <div class="p-4">
    {{ getDeptListById }}
    <BasicTable
      :row-selection="{ type: 'checkbox' ,fixed: true}"
      title="api档案"
      title-help-message="树形组件不能和序列号列同时存在"
      :columns="columns"
      :data-source="data"
      row-key="id"
      :indent-size="20"
    />
  </div>
</template>
<script lang="ts">
import { defineComponent, onMounted } from 'vue'
import { BasicTable } from '/@/components/Table'
// import { getBasicColumns, getTreeTableData } from '/@/views/demo/table/tableData'
import { getDeptListById } from '/@/api/record/dept'
import { AppRouteModule, AppRouteRecordRaw } from '/@/router/types'
import { LAYOUT } from '/@/router/constant'
import { cloneDeep } from 'lodash-es'
import { LayoutMapKey } from '/@/router/helper/routeHelper'
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
    // let routeList = (await getDeptListById({ id: '11' })) as AppRouteRecordRaw[];
    //
    // defHttp.request<GetAccountInfoModel>({
    //   url: '/getDeptAll',
    //   method: 'GET',
    // });
    return {
      getDeptListById,
      columns: null,
      data: null
    }
  }
})
</script>
