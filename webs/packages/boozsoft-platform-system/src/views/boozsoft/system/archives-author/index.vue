<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <div class="container-head-title">
          <UnorderedListOutlined style="margin: 0 10px;"/>
          <b class="noneSpan">档案审批授权设置</b>
        </div>
        <div class="ant-btn-group" style="float: right">
          <!--          <button type="button" class="ant-btn ant-btn-me">查询</button>-->
          <button type="button" class="ant-btn ant-btn-me"
                  :style="showOk?{}:{pointerEvents: 'none'}" @click="saveData">保存
          </button>
          <!--          <button type="button" class="ant-btn ant-btn-me" @click="openCopy">复制</button>-->
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent()">退出</button>
        </div>
      </div>
      <div class="app-container-neck">
        <div style="float:left;">
        </div>
        <div style="display: inline-block;float: left;margin-left: 2%;">
          <span>操作员：</span>
          <a-select style="width: 120px;" v-model:value="saveDataModel.userId"
                    @change="operChange">
            <template v-for="item in operList">
              <a-select-option :value="item.id">
                {{ item.realName }}
              </a-select-option>
            </template>
          </a-select>
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
    <div class="app-container" >
      <PageWrapper dense content-class="flex" :style="{minHeight: (windowHeight-200)+'px'}">
        <LeftTree @select="handleSelect" v-if="showLeft"/>
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

            <template #isApprove="{record}">
              <div>
                <a-checkbox v-model:checked="record.isApprove"/>
              </div>
            </template>
            <template #isAuto="{record}">
              <div>
                <a-checkbox v-model:checked="record.isAuto"/>
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
  Col as ACol,
  Popover as APopover,
  Input as AInput,
  Select as ASelect,
  Checkbox as ACheckbox,
  Row as ARow,
  Card as ACard, Popconfirm as APopconfirm,
  List as AList, message, Radio as ARadio, Table as ATable, Tag
} from 'ant-design-vue'
import LeftTree from './popup/LeftTree.vue';

const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
const AListItem = AList.Item
const ACheckboxGroup = ACheckbox.Group
const ARadioButton = ARadio.Button
const ARadioGroup = ARadio.Group
import {
  findAll,
} from '/@/api/caozuoyuan/caozuoyuan';
import {
  saveApproveAuthor,
  findUserApproveAuthor,
} from "/@/api/record/system/group-permission";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {usePlatformsStore} from "/@/store/modules/platforms";
import {assemblyDynamicColumn, initDynamics} from "./data";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {findDbLanMuList, saveLanMuList} from "/@/api/record/system/accvoucher";
import {cloneDeep} from "lodash-es";
import {JsonTool} from "/@/api/task-api/tools/universal-tools";
import {
  getTheMenuOfTheSpecifiedPlatform
} from "/@/views/boozsoft/system/archives-author/menu-data";
import {getOrganizeAll} from "/@/api/record/group/im-organize";

import {getAllAuthorAccountList} from "/@/api/record/system/account";
import fa from "element-plus/packages/locale/lang/fa";

const CrudApi = {
  columns: [
    {
      title: '档案名称',
      dataIndex: 'baseName',
      ellipsis: true, width: 300, align: 'left'
      // slots: {customRender: 'functionName'},
    },
    {
      title: '审批权限',
      dataIndex: 'isApprove',
      ellipsis: true,
      width: 150,
      slots: {customRender: 'isApprove'},
    },
    {
      title: '允许自动生效',
      dataIndex: 'isAuto',
      ellipsis: true,
      width: 150,
      slots: {customRender: 'isAuto'},
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
  userId: '',
  data: ''
})
onMounted(async () => {
  await reloadList()
  // resetDynamicColumnData()
});

async function reloadList() {
  // 查询所以操作员 与 组织与账套
  orgList.value = await getOrganizeAll()
/*  if (orgList.value.length > 0) {
    treeList2.value = orgList.value.map(it => ({
      key: it.uniqueCode,
      name: it.orgCode + ' ' + it.orgName,
    }))
  }*/
  accountList.value = (await getUnitAvailables()).filter(it=>!hasBlank(it.accGroup))
/*  if (accountList.value.length > 0) {
    treeList.value = accountList.value.map(it => ({
      key: it.coCode,
      name: it.coCode + ' ' + it.accNameCn,
    }))
  }*/
  operList.value = ((await findAll()).items)
  showLeft.value = true
  menuList.value = getTheMenuOfTheSpecifiedPlatform()
  if (operList.value.length > 0 && hasBlank(saveDataModel.userId)) {
    saveDataModel.userId = operList.value[1].id
    operChange(operList.value[1].id)
  }
  // 初始化列宽
  initTableWidth(getColumns())
}


const operChange = async (v) => {
  if (!hasBlank(saveDataModel.userId)) {
    loadMark.value = true
    let res = await findUserApproveAuthor({userId: saveDataModel.userId})
    if (null != res && Object.keys(res).length > 0) {
      userDataModel.value = JsonTool.parseProxy(res)
      await modifyIsSuper()
      if (null == theCheckModel.value.type) theCheckModel.value.type = '2'
      lastCheckList.value = [(userDataModel.value[theCheckModel.value.type]).map(it=>theCheckModel.value.type == '2'?it.tenantId:it.originId)[0]]
    } else {
      userDataModel.value = {}
      await modifyIsSuper()
      lastCheckList.value = [treeList.value[0].key]
    }
    loadMark.value = false
  }
}

const modifyIsSuper = async () =>{
  let corps = [...new Set((userDataModel.value['2'] || []).filter(it=>it.supervisor == '1').map(it=>it.tenantId))]
  treeList.value = accountList.value.map(it => ({
    key: it.coCode,
    name: it.coCode + ' ' + it.accNameCn,
    supervisor: corps.indexOf(it.coCode) != -1?true:false
  }))
  let orgs = [...new Set((userDataModel.value['1'] || []).filter(it=>it.supervisor == '1').map(it=>it.originId))]
  treeList2.value = orgList.value.map(it => ({
    key: it.uniqueCode,
    name: it.orgCode + ' ' + it.orgName,
    supervisor: orgs.indexOf(it.uniqueCode) != -1?true:false
  }))
}

async function saveData() {
  showOk.value = false
  if (hasBlank(saveDataModel.userId)) {
    createWarningModal({content: '请选择操作员！'})
    showOk.value = true
  } else {
    let list = getDataSource().filter(it => modificationExists(it)).map(it => {
      it.id =null;
      it.isAuto = it.isAuto?'1':'0';
      it.isApprove = it.isApprove?'1':'0';
      return it;
    })
    if (list.length == 0) {
      createWarningModal({content: '暂未发现任何变化！'})
    } else {
      saveDataModel.data = JsonTool.json(list)
        let res = await saveApproveAuthor(saveDataModel)
        message.success('保存成功！')
        operChange(saveDataModel.userId)
     /* } else {
        message.error('保存失败！')
      }*/
    }
    showOk.value = true
  }
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
  theCheckModel.value = e
  loadMark.value = true
  tableSelectedRowKeys.value = []
  setTableData([])
  userDataList.value = (Object.keys(userDataModel.value).length==0 ? [] : userDataModel.value[e.type]).filter(it1 => e.type == '2' ? e.obj.key == it1.tenantId != -1 : e.obj.key == it1.originId != -1)
  let list = menuList.value
    .map(it => createRow(it, userDataList.value, e))
  setTableData(list)
  tableList.value = list
  pageParameter.searchConditon.value = ''
  loadMark.value = false
}
import {buildUUID} from '/@/utils/uuid';
import {getUnitAvailables, getUnitListNoGroup} from "/@/api/record/group/im-unit";

function createRow(it, dlist, e) {
  let dbt = dlist.filter(d => d.baseName == it.baseName && (( e.type == '2' ? d['tenantId'] :d['originId']) == e.obj.key))
  let s = e.obj.supervisor=='1'?true:false
  if (dbt.length > 0) {
    let a = dbt[0]
    // a = a.map(item=>{item.supervisor =s?'1':'0';item.isAuto=s;it.isApprove = s;return it;})
    a.isAuto = a.isAuto == '1' || s ? true : false
    a.isApprove = a.isApprove == '1' || s ? true : false
    e.type == '2' ? a['tenantId'] = e.obj.key :a['originId'] = e.obj.key
    return a
  } else {
    it['id'] = buildUUID()
    it['userId'] = saveDataModel.userId
    it['tenantId'] = null
    it['originId'] = null
    e.type == '2' ? it['tenantId'] = e.obj.key : it['originId'] = e.obj.key
    it['ctype'] = e.type
    it['isAuto'] = false
    it['isApprove'] = false
    it.supervisor =s?'1':'0';it.isAuto=s;it.isApprove = s;
    return it
  }
}

function checkboxRow(it, v) {
  it.isAuto = v
  it.isApprove = v
  return it
}

function modificationExists(it) {
  return it.isAuto　|| it.isApprove
}


const pageReload = () => {
  // setTableData([])
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
