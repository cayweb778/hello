<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <div class="container-head-title">
          <UnorderedListOutlined style="margin: 0 10px;"/>
          <b class="noneSpan">数据权限控制</b>
        </div>
        <div class="ant-btn-group" style="float: right">
          <button type="button" class="ant-btn ant-btn-me"
                  :style="showOk?{}:{pointerEvents: 'none'}" @click="saveData">保存
          </button>
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent()">退出</button>
        </div>
      </div>
      <div class="app-container-neck">
        <div style="float:left;">
        </div>
        <div style="display: inline-block;float: left;margin-left: 2%;">
        </div>
        <div style="float: right; margin-left: 10px">
          <a-button class="ant-btn-me" @click="pageReload">
            <SyncOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
          <a-popover placement="bottom">
            <template #content>
              <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MAX'"
                    :style="pageParameter.showRulesSize==='MAX'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortDescendingOutlined/>&nbsp;大号字体&ensp;<CheckOutlined
                v-if="pageParameter.showRulesSize==='MAX'"/></span><br/>
              <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MIN'"
                    :style="pageParameter.showRulesSize==='MIN'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortAscendingOutlined/>&nbsp;小号字体&ensp;<CheckOutlined
                v-if="pageParameter.showRulesSize==='MIN'"/></span>
            </template>
            <a-button>
              <SettingFilled :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>
        </div>

        <div class="act-two-d-right">
          <div class="acttd-right-d-search">
            <a-select v-model:value="pageParameter.searchConditon.requirement"
                      class="acttdrd-search-select">
              <a-select-option :value="'functionName'">功能菜单</a-select-option>
            </a-select>

            <a-input-search
              style="width: 180px;"
              v-model:value="pageParameter.searchConditon.value"
              class="acttdrd-search-input"
              @search="onSearch"
            />
          </div>
        </div>
      </div>
    </div>
    <div class="app-container">
      <PageWrapper dense content-class="flex" :style="{minHeight: (windowHeight-200)+'px'}">
        <LeftTree @select="handleSelect"/><!--v-if="showLeft"-->
        <!--        <AuthorCopy @register="registerAuthorCopy"  />-->
        <div style="width: calc(100% - 305px);">
          <BasicTable
            ref="tableRef"
            :row-selection="{ type: 'checkbox',fixed: true, selectedRowKeys: tableSelectedRowKeys, onSelect: onSelectChange,onSelectAll: onSelectChange2 }"
            :rowKey="r=>r.id"
            :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
            :scroll="{ x: totalColumnWidth,y: windowHeight }"
            :loading="loadMark"
            @register="registerTable"
          >

            <template #isCtrl="{record}">
              <div>
                <a-checkbox v-model:checked="record.isCtrl"/>
              </div>
            </template>
            <template #isCtrlYear="{record}">
              <div v-if="record.dataName == '会计科目'">
                <a-checkbox v-model:checked="record.isCtrlYear" @change="(e)=>record.isCtrl = e"/>
              </div>
            </template>
          </BasicTable>
        </div>
      </PageWrapper>
    </div>
  </div>
</template>

<script setup lang="ts">
import {getCurrentAccountName, hasBlank} from '/@/api/task-api/tast-bus-api';
import {PageWrapper} from '/@/components/Page'
import {BasicTable, useTable, TableAction} from '/@/components/Table';
import {onMounted, provide, reactive, ref, unref} from 'vue';

const {closeCurrent} = useTabs(router);
import {useMessage} from '/@/hooks/web/useMessage';
import {
  SettingFilled,
  SyncOutlined,
  SortAscendingOutlined, CheckOutlined, SortDescendingOutlined, UnorderedListOutlined
} from '@ant-design/icons-vue';
import {
  Popover as APopover,
  Input as AInput,
  Select as ASelect,
  Checkbox as ACheckbox,
  List as AList, message, Radio as ARadio
} from 'ant-design-vue'
import LeftTree from './popup/LeftTree.vue';

const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
const AListItem = AList.Item
const ACheckboxGroup = ACheckbox.Group
const ARadioButton = ARadio.Button
const ARadioGroup = ARadio.Group
import {
  saveDataSeeAuthor,
  findDataSeeAuthor,
} from "/@/api/record/system/group-dataSee";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {usePlatformsStore} from "/@/store/modules/platforms";
import {assemblyDynamicColumn, initDynamics} from "./data";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {findDbLanMuList, saveLanMuList} from "/@/api/record/system/accvoucher";
import {cloneDeep} from "lodash-es";
import {JsonTool} from "/@/api/task-api/tools/universal-tools";
import {getTheMenuOfTheSpecifiedPlatform} from "/@/views/boozsoft/system/data-author-control/menu-data";
import {getOrganizeAll} from "/@/api/record/group/im-organize";


const CrudApi = {
  columns: [
    {
      title: '档案名称',
      dataIndex: 'dataName',
      ellipsis: true, width: 300, align: 'left'
    },
    {
      title: '所属模块',
      dataIndex: 'moduleName',
      ellipsis: true, width: 200, align: 'left'
    },
    {
      title: '是否控制',
      dataIndex: 'isCtrl',
      ellipsis: true,
      width: 120,
      slots: {customRender: 'isCtrl'},
    },
    {
      title: '按年度控制',
      dataIndex: 'isCtrlYear',
      ellipsis: true,
      width: 120,
      slots: {customRender: 'isCtrlYear'},
    }
  ]
};
// 这是示例组件
const [registerTable, {setTableData, getColumns, setColumns, reload, getDataSource}] = useTable({
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: true,
  indexColumnProps: {width: 60, fixed: 'left'},
  pagination: false
});

const operList = ref([]);
const orgList = ref([]);
const accountList = ref([]);


const menuList = ref([]);
const userDataModel = ref(null);
const userDataList = ref([]);
const tableList = ref([]);

const treeList = ref([])
const treeList2 = ref([])
const lastCheckList = ref([])


const loadMark = ref(false)
const showOk = ref(true)
const showLeft = ref(false)
const saveDataModel = reactive({
  data: ''
})
onMounted(async () => {
  await reloadList()
  // resetDynamicColumnData()
});

async function reloadList() {
  // 查询所以操作员 与 组织与账套
  orgList.value = await getOrganizeAll()
  if (orgList.value.length > 0) {
    treeList2.value = orgList.value.map(it => ({
      key: it.uniqueCode,
      name: it.orgCode + ' ' + it.orgName,
    }))
  }
  accountList.value = (await getUnitAvailables())/*.filter(it=>!hasBlank(it.accGroup))*/
  if (accountList.value.length > 0) {
    treeList.value = accountList.value.map(it => ({
      key: it.coCode,
      name: it.coCode + ' ' + it.accNameCn,
    }))
  }
  if (treeList.value[0].key != null) {
    lastCheckList.value = [treeList.value[0].key]
  }
  menuList.value = getTheMenuOfTheSpecifiedPlatform()
  // 初始化列宽
  initTableWidth(getColumns())
}


const operChange = async (v,t) => {
  let res = await findDataSeeAuthor({uniqueCode: v,type: t=='2'?'ZT':'ZZ'})
  if (null != res && Object.keys(res).length > 0) {
    userDataModel.value = JsonTool.parseProxy(res)
    if (null == theCheckModel.value.type) theCheckModel.value.type = '2'
  } else {
    userDataModel.value = {}
  }
}

const modifyIsSuper = async () => {
  let corps = [...new Set((userDataModel.value['2'] || []).filter(it => it.supervisor == '1').map(it => it.tenantId))]
  treeList.value = accountList.value.map(it => ({
    key: it.coCode,
    name: it.coCode + ' ' + it.accNameCn,
    supervisor: corps.indexOf(it.coCode) != -1 ? true : false
  }))
  let orgs = [...new Set((userDataModel.value['1'] || []).filter(it => it.supervisor == '1').map(it => it.originId))]
  treeList2.value = orgList.value.map(it => ({
    key: it.uniqueCode,
    name: it.orgCode + ' ' + it.orgName,
    supervisor: orgs.indexOf(it.uniqueCode) != -1 ? true : false
  }))
}

async function saveData() {
  showOk.value = false
  let list = (JsonTool.parseProxy(getDataSource().filter(it => modificationExists(it)))).map(it => {
    it.id = null;
    it.isCtrlYear = it.isCtrlYear ? '1' : '0';
    it.isCtrl = it.isCtrl ? '1' : '0';
    return it;
  })
  if (list.length == 0) {
    createWarningModal({content: '暂未发现任何变化！'})
  } else {
    saveDataModel.data = JsonTool.json(list)
    let res = await saveDataSeeAuthor(saveDataModel)
    message.success('保存成功！')
  }
  showOk.value = true
}

function onSearch() {
  loadMark.value = true
  if (hasBlank(pageParameter.searchConditon.value.trim())) {
    setTableData(tableList.value)
  } else {
    let v = pageParameter.searchConditon.value.trim()
    let k = pageParameter.searchConditon.requirement.trim()
    let searchAfter = tableList.value.filter(item => item[k].indexOf(v) != -1)
    setTableData(searchAfter)
  }
  loadMark.value = false
}

const searchConditonList = ref([])
const theCheckModel = ref({})
// const [registerAuthorCopy, { openModal:openAuthorCopyPage }] = useModal();
// const openCopy = () => {
//   openAuthorCopyPage(true, {userList: operList.value});
// }


const handleSelect = async (e) => {
  loadMark.value = true
  theCheckModel.value = e
  tableSelectedRowKeys.value = []
  setTableData([])
  await operChange(e.obj.key,e.type)
  userDataList.value = (Object.keys(userDataModel.value).length==0 ? [] : userDataModel.value[e.type]).filter(it1 =>  e.obj.key == it1.uniqueCode)
  let list = menuList.value
    .map(it => createRow(it, userDataList.value, e))
  setTableData(list)
  tableList.value = list
  pageParameter.searchConditon.value = ''
  loadMark.value = false
}
import {buildUUID} from '/@/utils/uuid';
import {getUnitAvailables} from "/@/api/record/group/im-unit";

function createRow(it, dlist, e) {
  let dbt = dlist.filter(d => d.dataName == it.dataName && (d['uniqueCode'] == e.obj.key))
  if (dbt.length > 0) {
    let a = dbt[0]
    a.isCtrlYear = a.isCtrlYear == '1' ? true : false
    a.isCtrl = a.isCtrl == '1' ? true : false
    a.ctrlRange = e.type == '2' ? 'ZT' : 'ZZ'
    e.uniqueCode == e.obj.key
    return a
  } else {
    it['id'] = buildUUID()
    it['ctrlRange'] = e.type == '2' ? 'ZT' : 'ZZ'
    it['uniqueCode'] = e.obj.key
    it['isCtrlYear'] = false
    it['isCtrl'] = false
    return it
  }
}

function checkboxRow(it, v) {
  if (it.dataName == '会计科目') it.isCtrlYear = v
  it.isCtrl = v
  return it
}

function modificationExists(it) {
  return it.isCtrlYear || it.isCtrl
}


const pageReload = async () => {
  if (Object.keys(theCheckModel.value).length > 0){
     await handleSelect(theCheckModel.value)
  }
}
const tableSelectedRowKeys = ref([])
const onSelectChange = (record, selected, obj, nativeEvent) => {
  loadMark.value = true
  record = checkboxRow(record, selected)
  tableSelectedRowKeys.value = obj.map(it => it.id)
  loadMark.value = false
}
const onSelectChange2 = (selected, selectedRows, changeRows) => {
  loadMark.value = true
  if (selected) {
    tableSelectedRowKeys.value = selectedRows.map(item => item.id)
    changeRows.map(it => checkboxRow(it, true))
  } else {
    tableSelectedRowKeys.value = []
    changeRows.map(it => checkboxRow(it, false))
  }
  loadMark.value = false
}
/*start栏目设置*/
const pageParameter = reactive({
  showRulesSize: 'MIN',
  searchConditon: {
    requirement: 'functionName',
    value: ''
  },
})
const mark = usePlatformsStore().getCurrentPlatformId
const visible = ref(false);
const windowWidth = (window.innerWidth - (70 /*+ 280*/))
const windowHeight = (window.innerHeight - (265))
const totalColumnWidth = ref(0)
const dynamicColumns = initDynamics().DEFAULT
const dynamicColumnData: any = ref([])
let dynamicColumnDataCopy = []
const editableData = reactive({});
const tableRef: any = ref(null)
const lanMuData = {
  'accId': 'postgres',
  'menuName': '操作权限设置',
  'type': '列表',
  objects: '',
  username: useUserStoreWidthOut().getUserInfo.username
}
const {createConfirm, createWarningModal} = useMessage();
const confirm = (e: MouseEvent) => {
  // 询问
  createConfirm({
    iconType: 'warning',
    title: '栏目同步',
    content: '是否将刚才设置同步数据库!',
    onOk: async () => {
      // 调整数据库 列参数
      lanMuData.accId = getCurrentAccountName(false)
      lanMuData.objects = JSON.stringify(filterModifyData(dynamicColumnData.value, dynamicColumnDataCopy))
      if (lanMuData.objects == '[]') {
        createWarningModal({content: '请先做修改后再进行确认同步数据库！'})
      } else {
        saveLanMuList(lanMuData).then(res => {
          message.success("数据库同步成功！")
        })
        // 重新赋值
        dynamicColumnDataCopy = JSON.parse(JSON.stringify(dynamicColumnData.value))
      }
    }
  });
  // 重新获取数据
  reloadColumns()
}
const cancel = (e: MouseEvent) => {
  // 恢复默认
  dynamicColumnData.value = []
  dynamicColumnData.value = dynamicColumnDataCopy
}
const edit = (key: string) => {
  if (key.toString().indexOf('-') != -1) {
    let arr: any = key.split('-');
    let one = parseInt(arr[0])
    if (arr.length == 2) {
      editableData[key] = cloneDeep(dynamicColumnData.value[one].children.filter(item => key === item.key)[0]);
    } else {
      let two = parseInt(arr[1] - 1)
      editableData[key] = cloneDeep(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0]);
    }
  } else {
    editableData[key] = cloneDeep(dynamicColumnData.value.filter(item => key === item.key)[0]);
  }
}
const save = (key: string, min: number, max: number) => {
  editableData[key].width = editableData[key].width > max ? max : editableData[key].width < min ? min : editableData[key].width
  if (key.toString().indexOf('-') != -1) {
    let arr: any = key.split('-');
    let one = parseInt(arr[0])
    if (arr.length == 2) {
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
    } else {
      let two = parseInt(arr[1] - 1)
      Object.assign(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0], editableData[key]);
    }
  } else {
    Object.assign(dynamicColumnData.value.filter(item => key === item.key)[0], editableData[key]);
  }
  delete editableData[key];
}
const saveName = (key: string) => {
  if (key.toString().indexOf('-') != -1) {
    let arr: any = key.split('-');
    let one = parseInt(arr[0])
    if (arr.length == 2) {
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
    } else {
      let two = parseInt(arr[1] - 1)
      Object.assign(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0], editableData[key]);
    }
  } else {
    Object.assign(dynamicColumnData.value.filter(item => key === item.key)[0], editableData[key]);
  }
  delete editableData[key];
}

function filterModifyData(lanMuList: any, copyList) {
  let a = lanMuList.filter(item => {
    try {
      copyList.forEach(item2 => {
        if (item.key === item2.key && item.name == item2.name) {
          if (item.nameNew != item2.nameNew || item.width != item2.width || item.check != item2.check || item.align != item2.align)
            throw new Error('ok')
        }
      })
      return false
    } catch (e: any) {
      if (e.message == 'ok') {
        return true
      } else {
        return false
      }
    }
  })
  return a;
}

function combineParameters(staticList: any, dbList: any) {
  staticList.forEach(item => {
    dbList.forEach(item2 => {
      if (item.key === item2.key && item.name === item2.name) {
        item.nameNew = item2.nameNew
        item.width = parseInt(item2.width)
        item.check = item2.check == 'true'
        item.align = item2.align
      }
    })
  })
  return staticList
}

const reloadColumns = () => {
  /*let a: any = []
  a = getColumns()
  let last = a.pop()*/
  let newA = JSON.parse(JSON.stringify(CrudApi.columns))
  newA = assemblyDynamicColumn(dynamicColumnData.value, newA)
  // newA.push(last)
  setColumns(newA)
  initTableWidth(newA)
  //searchConditonList.value = newA
}

function resetDynamicColumnData() {
  // 先从数据查询是否已经设置
  lanMuData.accId = getCurrentAccountName(false)
  findDbLanMuList(lanMuData).then(res => {
    // 栏目列
    let dbList = res.items
    if (dbList.length > 0) {
      let statiList = initDynamics()['DATA']
      dbList = combineParameters(statiList, dbList)
      dynamicColumnData.value = dbList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(dbList))
    } else {
      let statiList = initDynamics()['DATA']
      dynamicColumnData.value = statiList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(statiList))
    }
    // 表格列
    reloadColumns()
    //pageReload()
  })
}

function initTableWidth(thisCs) {
  let total = 60
  thisCs.forEach(item => {
    if (item.ifShow == null || item.ifShow)
      total += parseInt(item.width)
  })
  /*  // 去除操作咧宽
    total -= 100*/
  if (total > windowWidth) {
    let f = 0
    if (visible.value) f = 260
    totalColumnWidth.value = Number(windowWidth) - f
    tableRef.value.$el.style.setProperty('width', (windowWidth + 70 - f) + 'px')
  } else {
    if (visible.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
    totalColumnWidth.value = total
    tableRef.value.$el.style.setProperty('width', (total + 70) + 'px')
  }
}

/*栏目设置end*/

provide('treeList', treeList)
provide('treeList2', treeList2)
provide('lastCheckList', lastCheckList)
</script>
<style lang="less" scoped>
@import '/@/assets/styles/global-menu-index.less';
</style>
<style scoped>
.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 16px !important;
  padding: 5px 8px !important;
}

.a-table-font-size-16 :deep(th), .a-table-font-size-12 :deep(th) {
  text-align: center !important;
  font-weight: bold;
  background-color: #f1f1f1;
}

.a-table-font-size-16 :deep(.ant-checkbox-inner), .a-table-font-size-12 :deep(.ant-checkbox-inner) {
  border: 1px solid #c9c9c9;
}

.tableUStyle {
  color: #0798c8;
  cursor: pointer;
  text-decoration: none;
}

.tableUStyle:hover {
  color: #b4c8e3;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
}

.app-container:nth-of-type(1) {
  background-color: #f2f2f2;
  padding: 10px 5px;
  margin: 10px 10px 5px;
}

.app-container:nth-of-type(2) {
  padding: 0px;
  margin: 5px 10px;
  background: #b4c8e3 !important;
}
</style>
