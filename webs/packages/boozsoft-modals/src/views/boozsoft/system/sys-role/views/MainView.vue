<template>
  <div style="height:100%">
    <div style="height:100%" v-if="showPlatformManagerPage">
      <PlatformManagerPage></PlatformManagerPage>
    </div>
    <div v-else ref="abc" style="display:flex;height:100%;width: 1000px">
      <BasicTable :scroll="{ x: totalColumnWidth,y: windowHeight }"
                  class="a-table-font-size-16"
                  @rowClick="setCurrentRole"
                  style="border-radius: 4px;
    overflow: hidden;background:white;width:100%;height:100%" ref="tableRef"
                  @register="registerTable"

      >
        <template #roleFlag="{ record }">
          <Tag :color="record.roleFlag === '1'?'green':'red'">{{
              record.roleFlag === '1' ? '启用' :'停用'
            }}
          </Tag></template>
      </BasicTable>
      <div style="width:20px;background:#b4c8e3 !important"></div>
      <!--      <div style="min-width:400px;position:relative;border-radius:10px;background:white">-->
      <!--        <div v-if="!showPlatformManager">-->
      <!--          <div-->
      <!--            style="position: absolute;top: 10px;border-bottom: 1px solid #a5a5a5;width: 100%;height: 32px;padding-left: 10px;">-->
      <!--            操作权限-->
      <!--          </div>-->
      <!--          <div style="text-align: right;position: absolute;top: 50px;width: 100%;height: 32px;padding-left: 10px;">-->
      <!--            角色:{{ currentRole.roleName==''?'未选择':currentRole.roleName }}-->
      <!--          </div>-->

      <!--          <div @click="platformManagerFun"-->
      <!--               style="color: white;position: absolute;top: calc( (100% - 100px) / 2 - 100px );left: calc( (100% - 100px) / 2);border-radius: 50%;border: 2px solid rgb(197, 197, 197);width: 100px;height: 100px;margin: 30px auto;text-align: center;line-height: 102px;font-weight: bold;font-size: 16px;background: burlywood;box-shadow: black 0px 0px 9px 1px;cursor: pointer">-->
      <!--            平台管理-->
      <!--          </div>-->
      <!--        </div>-->
      <!--        <div v-else style="height:500px;position: relative;">-->
      <!--          <div-->
      <!--            style="position: absolute;top: 10px;border-bottom: 1px solid #a5a5a5;width: 100%;height: 32px;padding-left: 10px;">-->
      <!--            ({{ currentPlatform.name }})菜单权限-->
      <!--          </div>-->
      <!--          <div style="position: absolute;top: 50px;width: 100%;height: 32px;padding-left: 10px;text-align: right">-->
      <!--            角色:{{ currentRole.roleName==''?'未选择':currentRole.roleName }}-->
      <!--          </div>-->
      <!--          <div style="position: absolute;top:5px;right:10px;">-->
      <!--            <a-button style="margin-right:10px" @click="saveMenu">保存</a-button>-->
      <!--            <a-button style="margin-left: 3px;" @click="showPlatformManagerPage=true">...</a-button>-->
      <!--          </div>-->
      <!--          &lt;!&ndash;          <div style="position: absolute;top:400px;right:5px;z-index:1;width:100%;text-align:center"><a-button>√   保存</a-button></div>&ndash;&gt;-->
      <!--          <div style="position: absolute;top:80px;width:100%;padding:5px 20px">-->

      <!--            <BasicTree-->
      <!--              v-model:value="treeSelectKeys"-->
      <!--              :treeData="treeData"-->
      <!--              :checkable="true"-->
      <!--              defaultExpandAll-->
      <!--            />-->
      <!--          </div>-->

      <!--        </div>-->

      <!--      </div>-->

    </div>
    <Edit @save="saveData" @handleClose="reload()" @register="registerSavePage"/>
  </div>
</template>
<script setup lang="ts">
import {
  delRole,
  findAll, saveRole, saveAudit
} from '/@/api/record/sys-role/data';

import {BasicTree} from '/@/components/Tree'

const abc = ref()
const showPlatformManagerPage = ref(false)
import PlatformManagerPage from './RolePermission/PlatformManagerPage.vue'

import {BasicTable, useTable} from '/@/components/Table';
import {useModal} from '/@/components/Modal';
import {getThisIndexImg} from '/@/api/task-api/tast-bus-api';
import {
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
  FilterFilled,
} from '@ant-design/icons-vue';
import {
  Form as AForm,
  Tabs as ATabs,
  Select as ASelect,
  Input as AInput,
  Popover as APopover,
  Checkbox as ACheckbox,
  Modal as AModal,
  Upload as AUpload,
  Row as ARow,
  Col as ACol,
  Statistic as AStatistic,
  message,
  TreeSelect,Tag
} from 'ant-design-vue';
import {nextTick, onMounted, ref} from 'vue';
import {useUserStoreWidthOut} from '/@/store/modules/user';
import Edit from '../popup/edit.vue';
import {deleteByIdApi, findAllApi, findByIdApi, saveApi} from "/@/api/group/SysLogger";
import {useViewModel} from "/@/utils/boozsoft/viewModel/viewModelUtils";
import {usePlatformsStoreWidthOut} from "/@/store/modules/platforms";
import {getUserMenuTreeByPlatformId2} from "/@/api/sys/user";
import {RoleMenuApi} from "/@/api/boozsoft/group/RouteMenuApi";
import {saveLog} from "/@/api/record/system/group-sys-login-log";

const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const AStatisticCountdown = AStatistic.Countdown;
const ATabPane = ATabs.TabPane;

const username = ref(useUserStoreWidthOut().getUserInfo.username);
const usernum = ref(useUserStoreWidthOut().getUserInfo.username);
const tableRef = ref(null)
const windowWidth = (document.documentElement.clientWidth - 70)
const windowHeight = (window.innerHeight - (350))
const totalColumnWidth = ref(0)
const columns = [
  {
    title: '状态',
    dataIndex: 'roleFlag',
    ellipsis: true,
    slots: {customRender: 'roleFlag'}
    , width: 100
  } ,{
    title: '角色编码',
    dataIndex: 'roleNum',
    ellipsis: true,
    width: 120
  },
  {
    title: '角色名称',
    dataIndex: 'roleName',
    ellipsis: true, width: 300, align: 'left'
  },
/*  {
    title: '操作范围',
    dataIndex: 'roleRangeName',
    ellipsis: true, width: 300
  },
  {
    title: '建立日期',
    dataIndex: 'roleAddTime',
    ellipsis: true, width: 120
  },
  {
    title: '建立人',
    dataIndex: 'roleAddPsn',
    ellipsis: true, width: 120
  }*/
];

const treeData = ref([])

async function getMenuTree(platformId) {
  const menus = (await getUserMenuTreeByPlatformId2({platformId}))
  const arr = []

  function toMenuModel(menu) {
    const obj = {
      ...menu,
      title: menu.meta.target.name,
      value: menu.meta.target.id,
      key: menu.meta.target.id,
    }
    return obj
  }

  menus.forEach(it => {
    const obj = toMenuModel(it)
    arr.push(obj)
    if (it.children != null) {
      obj.children = []
      it.children.forEach(it2 => {
        obj.children.push(toMenuModel(it2))
      })
    }
  })
  return arr
}


// 这是示例组件
const [registerSavePage, {openModal: openSavePage}] = useModal();
const [registerTable, {reload, getDataSource, getSelectRows, getColumns}] = useTable({
  api: findAll,
  columns: columns,
  rowSelection: {type: 'checkbox'},
  isTreeTable: true,
  pagination: {
    pageSize: 200,
    simple: true
  }
});

const showPlatformManager = ref(false)


const openAddPage = (ttype) => {
  if (ttype === 'edit') {
    if (getSelectRows().length === 0 || getSelectRows().length > 1) {
      return message.error('只能选择一条数据修改')
    }
    openSavePage(true, {
      data: getSelectRows()[0],
    });
  } else {
    openSavePage(true, {
      data: '',
    });
  }
}
const saveData = async (data) => {
  await saveRole(data).then(async (res) => {
    await writeLog(data.id == null || data.id == '' ? '新增' : '修改', res, null)
    await reload()
  });
}

const currentPlatform = ref()
const currentRole = ref({roleName: ''})

const delData = async () => {
  console.log(getSelectRows())
  await delRole(getSelectRows()).then(async (res) => {
    if (null != res && res.length > 0) res.forEach(async (r) => await writeLog('删除', r, null))
    await reload()
  })
}

const recordName = 'SystemRole'

const platformList = ref([])
const numberTotal = ref(0)
onMounted(async () => {
  platformList.value = await usePlatformsStoreWidthOut().getPlatformListToNames()
})
const {ncLogger, ncTask, ncAudit, useViewModelRef} = useViewModel({name: recordName})
const viewModel = useViewModelRef({
  state() {
    return {
      currentPlatform: {id: 1003},
      platformList,
      getThisIndexImg,
      openAddPage,
      delData,
      a: 'aaa',
      async openPlatformManager(e) {
        await useMenuTree(e)
        updateMenuTreeSelectKeys(currentRole.value.id)
        // nextTick(()=>{
        // showPlatformManager.value = true
        // showPlatformManagerPage.value = false
        // })
      },getTotalNumber(){
        return numberTotal.value
      }
    }
  },
  crud: {},
  columns: [],
  crudFuns: [],
  action: {}
})
viewModel.value.currentRole = currentRole

async function useMenuTree(platform) {
  currentPlatform.value = platform
  treeData.value = await getMenuTree(platform.id)
}

onMounted(async () => {
  useMenuTree(viewModel.value.currentPlatform)
})

viewModel.value.useMenuTree = useMenuTree
const treeSelectKeys = ref([])

function getMenuIds(treeData) {
  const menuIds = []

  function abc(arr) {
    arr.forEach(it => {
      menuIds.push(it.id)
      if (it.children != null) {
        abc(it.children)
      }
    })
  }

  abc(treeData.value)
  return menuIds
}

async function updateMenuTreeSelectKeys(roleId) {
  const menuIds = getMenuIds(treeData)
  treeSelectKeys.value = (await RoleMenuApi.findAll()).filter(it => it.roleId == roleId).filter(it => menuIds.indexOf(it.menuId) != -1).map(it => it.menuId)
  return menuIds
}

async function setCurrentRole(e) {
  currentRole.value = e
  updateMenuTreeSelectKeys(currentRole.value.id)
}

function platformManagerFun() {
  // if(currentRole.value.roleName==""){
  //   message.error('必须先指定一个角色');
  // }else{
  showPlatformManagerPage.value = !showPlatformManagerPage.value
  // }
}

viewModel.value.platformManagerFun = platformManagerFun
viewModel.value.showPlatformManagerPage = showPlatformManagerPage

function saveMenu() {
  console.warn('接口数据', treeSelectKeys.value)
  RoleMenuApi.saveMenus({roleId: currentRole.value.id, menuIds: treeSelectKeys.value})
  message.warn('未开发接口')

}

function initTableWidth(thisCs) {
  let total = 60
  thisCs.forEach(item => {
    if (item.ifShow == null || item.ifShow)
      total += parseInt(item.width)
  })
  /*  // 去除操作咧宽
    total -= 100*/
  // debugger
  if (total > windowWidth) {
    let f = 0
    totalColumnWidth.value = Number(windowWidth) - f
    tableRef.value.$el.style.setProperty('width', (windowWidth + 70 - f) + 'px')
  } else {
    totalColumnWidth.value = total
    tableRef.value.$el.style.setProperty('width', (total + 70) + 'px')
  }
}

// 20220315 补充日志与审计
async function writeLog(action, a, content) {
  /************** 记录操作日志 ****************/
  if (action == '修改') {
    let old = getDataSource().filter(it => it.id == a.id)[0]
    let keys = (Object.keys(old)).filter(k => old[k] != a[k])
    let text = `操作内容【修改角色】,`+ '角色编码【' + a.roleNum + '】,角色名称【' + a.roleName + '】,'
    for (let i = 0; i < keys.length; i++) {
      const k = keys[i];
      if (k != 'key') {
        let t = columns.filter(t => t.dataIndex == k)[0].title
        text += `${t}【${transformText(k, old[k])},${transformText(k, a[k])}】;`
      }
    }
    content = text.substring(0, text.length - 1)
  }
  let map = {
    loginTime: new Date(+new Date() + 8 * 3600 * 1000).toJSON().substr(0, 19).replace("T", " "),
    userId: useUserStoreWidthOut().getUserInfo.username,
    userName: useUserStoreWidthOut().getUserInfo.realName,
    optModule: 'group',
    optFunction: '角色',
    optRange: '2',
    uniqueCode: '',
    optAction: action,
    optContent: content || `操作内容【${action}角色】` + '角色编码【' + a.roleNum + '】,角色名称【' + a.roleName + '】',
  }
  await saveLog(map)
  await writeAudit(action, old)
}

async function writeAudit(action, a) {
  /************** 记录操作日志 ****************/
  let map = {
    loginTime: new Date(+new Date() + 8 * 3600 * 1000).toJSON().substr(0, 19).replace("T", " "),
    userName: useUserStoreWidthOut().getUserInfo.realName}
  /************** 记录操作日志 End ****************/
  /************** 记录操作审计 Start **************/
  if (action == '修改' || action == '删除') {
    let data = a;
    data.optTime = map.loginTime
    data.optMethod = action == '修改' ? '0' : '1'
    data.optUsername = map.userName
    await saveAudit(data)
  }
  /************** 记录操作审计 End ****************/
}

function transformText(key, value) {
  let text = value
  switch (key) {
    case "roleFlag":
      text = '' == value ? '启用' : '停用'
      break;

  }
  return text
}
</script>
<style lang="less" scoped>
@import '/@/assets/styles/global-group-org-style.less';
</style>
