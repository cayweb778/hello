<template>
  <div class="p-4">
    <div v-if="hasPermission('admin')" type="primary" class="mx-4">
      <a-button color="error" class="mx-4">新增</a-button>
      <a-button color="error" class="mx-4">删除</a-button>
      <a-button color="error" class="mx-4">修改</a-button>
      <a-button color="error" class="mx-4">查看</a-button>
      <BasicTable
        :row-selection="{ type: 'checkbox' ,fixed: true}"
        title="机构管理"
        title-help-message="树形组件不能和序列号列同时存在"
        :columns="columns"
        :data-source="data"
        row-key="id"
        :indent-size="20"
      />
    </div>
    <div v-else>
      您无权查看此页面
    </div>
  </div>
</template>
<script lang="ts">
import {defineComponent, onMounted, reactive, toRefs} from 'vue'
import {BasicTable} from '/@/components/Table'
// import {getBasicColumns, getTreeTableData} from '/@/views/demo/table/tableData'
import {getDeptListById} from '/@/api/record/dept'
import {AppRouteModule, AppRouteRecordRaw} from '/@/router/types'
import {LAYOUT} from '/@/router/constant'
import {cloneDeep} from 'lodash-es'
import {LayoutMapKey} from '/@/router/helper/routeHelper'
import {getJigouAll} from '/@/api/admin/jigou'
import {Authority} from '/@/components/Authority'
import {getPermCodeByUserId} from '/@/api/sys/user'
import {usePermissionStoreWidthOut} from '/@/store/modules/permission'
import {PermissionModeEnum} from '/@/enums/appEnum'
import {usePermission} from '/@/hooks/web/usePermission'
import {useProjectStoreWidthOut} from "/@/store/modules/project";

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
  components: {Authority, BasicTable},
  setup() {

    const {hasPermission} = usePermission()

    // !模拟从后台获取权限编码， 该函数可能只需要执行一次，实际项目可以自行放到合适的时机
    async function changePermissionCode(userId: string) {
      const codeList = await getPermCodeByUserId({userId})
      usePermissionStoreWidthOut().commitPermCodeListState(codeList)
    }

    // 默认初始化为1
    changePermissionCode('3')
    // 1
    const state = reactive({
      data: []
    })
    // 2
    const dataFrom = async () => {
      state.data.push(...await getZh())
    }
    // 3
    onMounted(() => {
      dataFrom()
    })
    // 目前没有封装 暂时 需要走4步
    return {
      hasPermission,
      permissionStore:usePermissionStoreWidthOut(),
      changePermissionCode,
      PermissionModeEnum,
      columns: [
        {
          title: 'ID',
          dataIndex: 'id',
          fixed: 'left',
          width: 200
        },
        // {
        //   title: '姓名',
        //   dataIndex: 'name',
        //   width: 200,
        //   filters: [
        //     {text: 'Male', value: 'male'},
        //     {text: 'Female', value: 'female'},
        //   ],
        // },
        {
          title: '机构名称',
          dataIndex: 'deptName',
          width: 200
        },

        {
          title: '机构编码',
          dataIndex: 'deptNum',
          width: 200
        },
        {
          title: '机构等级',
          dataIndex: 'deptOrder',
          width: 200
        },
        {
          title: '机构类型',
          dataIndex: 'deptType',
          width: 200
        }
        // {
        //   title: '开始时间',
        //   width: 120,
        //   sorter: true,
        //   dataIndex: 'beginTime',
        // },
        // {
        //   title: '结束时间',
        //   width: 120,
        //   sorter: true,
        //   dataIndex: 'endTime',
        // },
      ],
      // 4
      ...toRefs(state)
    }
  }
})
</script>
