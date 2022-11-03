<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
<!--        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">-->
<!--        <div class="container-head-title" style="margin-left: 20px;">
          <b class="noneSpan"><UnorderedListOutlined />&nbsp;&nbsp;项目分类</b>
        </div>-->
        <div class="container-head-title" style="float: left;margin-top: 10px;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 35%;text-align: center;margin-top: 30px;">
          <b class="noneSpan" style="font-size: 36px;">项目分类</b>
        </div>

        <div class="ant-btn-group" data-v-a1ccd506="" style="float: right">
          <a-button class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false"
                    v-if="defaultPage"
                    @click="openAdd()"><span>新建</span></a-button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            v-if="defaultPage"
            @click="editOpen()"
          ><span>修改</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="delList()"
          ><span>删除</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            v-if="!defaultPage"
            @click="openInto()"
          ><span>引入</span></button>
<!--          <a-button class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false"><span>打印</span>
          </a-button>
          <a-button class="ant-btn ant-btn-me"><span>导入</span></a-button>
          <a-button class="ant-btn ant-btn-me"><span>导出</span></a-button>-->
          <a-button class="ant-btn ant-btn-me" @click="closeCurrent()"><span>退出</span></a-button>
        </div>
      </div>
      <div style="clear: none;"/>
      <div style="margin-top: -30px;margin-left: 80px;">
        <div style="display: inline-block;float: left;margin-top: -40px;">
          <AccountPicker theme="three" @reloadTable="dynamicAdReload"/>
        </div>
        <div style="float: left;font-size: 16px;margin-left: 15px;" v-if="pageParameter.projectClassCtl=='1'">
          <span style="color:#666666;font-size: 14px;font-weight: bold;">项目大类：</span>
          <a-select v-model:value="pageParameter.itemCode" @change="changCate()" placeholder="请选择项目大类"
                    style="min-width: 200px;">
            <a-select-option v-for="item in itemList" :key="item.itemCode" :value="item.itemCode">
              ({{ item.itemCode }}){{ item.itemName }}
            </a-select-option>
          </a-select>
        </div>
        <div style="float: left; margin-left: 20px;margin-bottom: -15px;padding-bottom: -10px;padding-top: 10px;line-height: 30px;font-size: 10px;color:#999999;">共 {{cardList.length}} 条记录</div>

        <div style="float: right; margin-left: 10px">
          <a-button @click="changCate()">
            <SyncOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
          <a-popover class="ant-btn-default" placement="bottom">
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
<!--        <a-popover placement="bottom">
          <a-button>
            <PicLeftOutlined :style="{ fontSize: '14px' }" />
          </a-button>
        </a-popover>

        <a-button>
          <EditFilled :style="{ fontSize: '14px' }" />
        </a-button>

        <a-button>
          <PieChartFilled :style="{ fontSize: '14px' }" />
        </a-button>
        <a-button>
          <FilterFilled :style="{ fontSize: '14px' }" />
        </a-button>-->
        </div>
        <div style="float: right; position: relative">
          <!--        <label style="font-size: 14px">检索条件：</label>-->
          <a-select v-model:value="formItems.selectType" style="width: 120px;font-size: 12px;" class="special_select">
            <a-select-option style="font-size: 12px;" value="projectClassCode">项目分类编码</a-select-option>
            <a-select-option style="font-size: 12px;" value="projectClassName">项目分类名称</a-select-option>
          </a-select>
          <!-- 搜索 -->
          <a-input-search
            v-model:value="formItems.selectValue"
            placeholder=""
            style="width: 200px; border-radius: 4px"
            @search="onSearch"
          />
        </div>
      </div>
      <div style="clear:both"/>
    </div>
    <div class="app-container">
      <EditPage @save="saveData" @register="registerEditPage"/>
      <Into @save="saveInto" @register="registerIntoPage"/>
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <div class="w-1/4 xl:w-1/5" style="width: 250px;min-height: 250px;border: 1px #cccccc solid;margin-right: .2%;margin-bottom: 58px;">
          <ClassTree v-if="isShowTree" class="w-1/4 xl:w-1/5" @select="handleSelect" v-model="pageParameter" />
        </div>
        <div style="width: calc(100% - 250px); float: right;margin-left: 5px;">
          <BasicTable
            :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys,getCheckboxProps:rowSelection.getCheckboxProps, onChange: onSelectChange }"
            @selection-change="selectionChange"
            @row-click="rowClick"
            :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
            :dataSource="tableData"
            :scroll="{ y: windowHeight }"
            :loading="loadMark"
            @register="registerTable">
        <!--        <template #action="{ record }">
                  <div>
                    <a-popover placement="bottom">
                      <a-button style="padding: 0px 4px; height: 20px;">
                        <CaretDownFilled />
                      </a-button>
                      <template #content>
                        <p style="cursor: pointer" @click="condClick(record)">
                          <FormOutlined/>
                          编辑
                        </p>
                        <p style="cursor: pointer" @click="del(record)">
                          <DeleteOutlined/>
                          删除
                        </p>
                      </template>
                    </a-popover>
                  </div>
                </template>-->
          </BasicTable>
          <div class="pagination-text" v-show="showPaginationText">
            共 {{paginationNumber}} 条记录&nbsp;&nbsp; 每页 200 条
          </div>
        </div>
      </PageWrapper>

    </div>
  </div>
</template>
<script setup lang="ts">
import {
  GetProClassTree,
  deleteProClass,
  saveProClass,
  projectClassFindAll, findByUniqueCode
} from '/@/api/record/system/project_class'
import { BasicTable, useTable } from '/@/components/Table'
import { PageWrapper } from '/@/components/Page'
import EditPage from './popup/edit.vue'
import ClassTree from './ClassTree.vue'
import { useModal } from '/@/components/Modal'
import {
  CaretDownFilled,
  FormOutlined,
  DeleteOutlined,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  EditFilled,
  PieChartFilled,
  FilterFilled,
  SortDescendingOutlined,
  CheckOutlined,
  SortAscendingOutlined,
  UnorderedListOutlined,ProfileOutlined
} from '@ant-design/icons-vue'
import {onMounted, reactive, ref} from 'vue'
import {
  Select as ASelect,
  Input as AInput,
  Popover as APopover,
  Button as AButton,
  message
} from 'ant-design-vue'
import {useMessage} from "/@/hooks/web/useMessage";
import {getThisAdInfoData} from "/@/api/record/system/financial-settings";
import {getCurrentAccountName, getThisIndexImg, hasBlank} from "/@/api/task-api/tast-bus-api";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker-DATA.vue";
import {findAllList, getProjectList} from "/@/api/record/system/project";
import {getProjectItemList} from "/@/api/record/system/project-item";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {findByTenantIdAndUniqueCode, save} from "/@/api/record/system/group-project-class-account";

const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option

const windowHeight = (window.innerHeight - (300))

const {closeCurrent} = useTabs(router);

const {
  createConfirm,
  createErrorModal,
  createSuccessModal,
  createWarningModal
} = useMessage()

const formItems = ref({
  selectType: 'projectClassCode',
  selectValue: ''
})

const itemList:any = ref([])
//改变项目大类
async function changCate(){
  await reloadTable()
}

const isShowTree = ref(true)
const loadMark = ref(false)

const tableData:any = ref([]);
const tableDataAll:any = ref([]);

const showPaginationText = ref(false)
const paginationNumber = ref(0)

async function handleSelect(keys) {
  // debugger
  loadMark.value = true
  cardList.value = tableDataAll.value.filter(item =>{
    if (keys!=null && keys!='' && keys!='0') {
      return keys.indexOf(item.id) != -1
    }
    return item
  })
  tableData.value = cardList.value
  await setPagination({total:tableData.value.length})
  loadMark.value = false
}

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    defaultHidden: true,
    ellipsis: true
  },
  {
    title: '项目分类编码',
    dataIndex: 'projectClassCode',
    width: 150,
    align: 'left',
    ellipsis: true
  },
  {
    title: '项目分类名称',
    dataIndex: 'projectClassName',
    align: 'left',
    ellipsis: true
  },
  /*{
    title: '所属项目大类编码',
    dataIndex: 'projectCateCode',
    ellipsis: true
  },*/
  {
    title: '上级ID',
    dataIndex: 'parentId',
    ellipsis: true,
    defaultHidden: true
  }
]
const dynamicTenantId = ref(getCurrentAccountName(true))
// 这是示例组件
const [registerTable, { reload,setTableData,setColumns,getColumns,getPaginationRef,setPagination,deleteSelectRowByKey }] = useTable({
  // api: useRouteApi(GetProClassTree,{schemaName: dynamicTenantId}),
  columns: columns,
  bordered: true,
  showIndexColumn: true,
  indexColumnProps:{ fixed:true },
  pagination: {
    pageSize: 200,
    // showSizeChanger: false,
    simple:true,
    showTotal: t => `共${t}条记录    每页200条`
  },
  /*actionColumn: {
    width: 100,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' }
  },*/
})
const [registerEditPage, { openModal: openEditPage }] = useModal()
const val = {
  id: null,
  uniqueCode: '',
  projectClassCode: '',
  projectClassName: '',
  parentId: '',
  projectCateCode: '',
  projectClassCtl: ''
}
function openAdd(){
  openAddPage()
}
const openAddPage = () => {
  if (pageParameter.projectClassCtl=='1'){
    if (itemList.value==null || itemList.value.length==0){
      createWarningModal({
        iconType: 'warning',
        title: '提示',
        content: '暂无项目大类信息，请先添加项目大类档案！'
      })
      return false
    }
  }
  val.projectCateCode = pageParameter.itemCode
  val.projectClassCtl = pageParameter.projectClassCtl
  openEditPage(true, {
    data: val,
    dynamicTenantId: dynamicTenantId.value,
    isState: '0'
  })
}
const condClick = (data:any) => {
  /*openEditPage(true, {
    data: data,
    projectCateCode: cateCode.value,
    dynamicTenantId: dynamicTenantId.value
  })*/
}

const del = async(data:any) => {
  await useRouteApi(deleteProClass, {schemaName: dynamicTenantId})(data)
  // alert('删除成功！')
  createSuccessModal({
    iconType: 'success',
    title: '删除',
    content: '删除成功！'
  })
  await reloadTable()
}

async function saveData(data:any) {
  await useRouteApi(saveProClass, {schemaName: dynamicTenantId})(data)
  await reloadTable()
}

//选中内容
const state = reactive<{
  selectedRowKeys: [];
  loading: boolean;
}>({
  selectedRowKeys: [], // Check here to configure the default column
  loading: false,
});
const checkRow: any = ref([])
const onSelectChange = (selectedRowKeys,row) => {
  // console.log('selectedRowKeys changed: ', row);
  state.selectedRowKeys = selectedRowKeys;
  checkRow.value = row.filter(item=>item.id!=null)
};

const rowSelection = {
  getCheckboxProps: (record) => ({
    disabled:  record.id==undefined
  }),
};
// 行勾选事件
const editAndDelBtnShow = ref(false);
function selectionChange(a) {
  editAndDelBtnShow.value=a.rows.filter(aa=>aa.flag=='1').length>0?true:false
}

function rowClick(a) {
  if(hasBlank(a.id)){
    deleteSelectRowByKey(a.key)
  }
}

const editOpen = () => {
  if (checkRow.value.length == 1) {
    checkRow.value[0].projectCateCode = pageParameter.itemCode
    checkRow.value[0].projectClassCtl = pageParameter.projectClassCtl
    openEditPage(true, {
      data: checkRow.value[0],
      dynamicTenantId: dynamicTenantId.value,
      isState: '1'
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '编辑',
      content: '请选择一条进行编辑！'
    })
  }
}

async function delList() {
  if (checkRow.value.length > 0) {
    const projectList: any = await useRouteApi(findAllList, {schemaName: dynamicTenantId})({itemCode:pageParameter.itemCode,projectClassCtl:pageParameter.projectClassCtl})
    for (let i = 0; i < checkRow.value.length; i++) {
      const item = checkRow.value[i]
      /*if (item.projectClassCode == '9999') {
        createWarningModal({
          iconType: 'warning',
          title: '删除',
          content: '编码为“9999”的项目分类为系统预制分类不允许删除！'
        })
        return false
      }*/
      for (let a = 0; a < projectList.length; a++) {
        const project = projectList[a]
        if (project.projectClassCode.substring(0,item.projectClassCode.length) == item.projectClassCode) {
          createWarningModal({
            iconType: 'warning',
            title: '删除',
            content: '<span style="font-weight: bold;color: red;">[' + item.projectClassName + ']</span>项目分类中经已存在项目，不能进行删除操作！'
          })
          return false
        }
      }
    }
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据将不能恢复，你确认要删除吗?',
      onOk: async () => {
        for (let i = 0; i < checkRow.value.length; i++) {
          const item = checkRow.value[i]
          const res = await useRouteApi(findByUniqueCode,{schemaName: dynamicTenantId})(item.uniqueCode)
          //如果只有一条修改分配表状态
          if (res.length==1){
            const item1 = await findByTenantIdAndUniqueCode(dynamicTenantId.value,item.uniqueCode)
            if (item1.length>0) {
              item1[0].flag = '0'
              item1[0].acceptUser = ''
              item1[0].acceptDate = ''
              await save(item1[0])
            }
          }
          await useRouteApi(deleteProClass, {schemaName: dynamicTenantId})(item)
        }
        message.success('删除成功！')
        await reloadTable()
      },
      onCancel: () => {
        return false
      }
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '删除',
      content: '请选择需要删除的内容！'
    })
  }
}

async function reloadTable(){
  isShowTree.value = false
  checkRow.value = []
  state.selectedRowKeys = []
  await reloadCurrentPage()
  await reload()
  isShowTree.value = true
}

onMounted(async() => {
  await reloadCurrentPage()
})
const cardList:any = ref([])
async function reloadCurrentPage() {
  loadMark.value = true
  showPaginationText.value = false
  let len = 0
  const res: any = await useRouteApi(projectClassFindAll,{schemaName: dynamicTenantId})({itemCode:pageParameter.itemCode,projectClassCtl:pageParameter.projectClassCtl})
  cardList.value = res
  tableDataAll.value = res
  tableData.value = res
  let num = 50-(tableData.value.length%50)
  for (let i=0;i<num;i++){
    tableData.value.push({})
  }
  len = tableData.value.filter(item=>item.id!=null && item.id!='').length
  paginationNumber.value = len
  showPaginationText.value = true
  loadMark.value = false
}
async function onSearch() {
  // let a: any = []
  tableData.value = cardList.value.filter(item => {
    //通过项目分类编码
    if (formItems.value.selectType == 'projectClassCode' && formItems.value.selectValue != '') {
      return item.projectClassCode.indexOf(formItems.value.selectValue) != -1
    }
    //通过项目分类名称
    if (formItems.value.selectType == 'projectClassName' && formItems.value.selectValue != '') {
      return item.projectClassName.indexOf(formItems.value.selectValue) != -1
    }
    return item
  })
  setTableData([])
  setTableData(tableData.value)
  await setPagination({total: tableData.value.length})
}

const pageParameter:any = reactive({
  showRulesSize: 'MIN',
  companyCode: '100',
  companyName: '北京希格科技',
  accId: '',
  ifUnit: false,
  total: 0,
  projectClassCtl: '',
  itemCode: '',
  thisAdInfo: {}
})
const defaultPage = ref(false)
/*onMounted(async() => {
  // 进页面执行
  getThisAdInfoData({'accId': getCurrentAccountName(false)}).then(res=>{
    if (null != res){
      pageParameter.projectClassCtl = res.projectClassCtl
      if (res.independent == 0) {
        defaultPage.value = false
      }
      defaultPage.value = false
      pageParameter.companyName = res.accNameCn
      pageParameter.companyCode = res.coCode
      pageParameter.ifUnit = res.icorp == '1'
    }
  })
})*/
const dynamicAdReload = async (obj) => {
  // const dataBase:any = await findDataBase(obj.accId,obj.year)
  loadMark.value = true
  dynamicTenantId.value = obj.accountMode
  const account:any = await getThisAdInfoData({'accId': obj.accId})
  if (null != account){
    pageParameter.projectClassCtl = account.projectClassCtl
    if (account.independent == 0) {
      defaultPage.value = false
    } else {
      defaultPage.value = true
    }
    pageParameter.companyName = account.accNameCn
    pageParameter.companyCode = account.coCode
    pageParameter.ifUnit = account.icorp == '1'
  }
  isShowTree.value = false
  if (pageParameter.projectClassCtl=='1'){
    const items: any = await useRouteApi(getProjectItemList,{schemaName: dynamicTenantId})({})
    itemList.value = items
    if (itemList.value==null || itemList.value.length==0){
      isShowTree.value = true
      loadMark.value = false
      pageParameter.thisAdInfo = {}
      pageParameter.thisAdInfo = obj
      pageParameter.total = -1
      createWarningModal({title: '温馨提示', content: '暂无项目大类信息，请先添加项目大类档案！'});
      return false
    } else {
      pageParameter.itemCode = itemList.value[0].itemCode
    }
  }
  let data: any = {}
  data.page = getPaginationRef().current
  data.size = getPaginationRef().pageSize
  isShowTree.value = true
  // let res: any = await useRouteApi(projectClassFindAll, {schemaName: obj.accountMode})(data)
  await reloadCurrentPage()
  // cardList.value = res
  // setTableData([]) // 清空可能残留的数据
  // setTableData(res)
  // 底部分页信息
  pageParameter.accId = obj.accId
  pageParameter.thisAdInfo = {}
  pageParameter.thisAdInfo = obj
  // pageParameter.total = res.total
  // setPagination({total: res.total})
  loadMark.value = false
}

//引用
import Into from './popup/into.vue'
const [registerIntoPage, {openModal: openIntoPage}] = useModal()
const openInto= () => {
  openIntoPage(true, {
    dynamicTenantId: dynamicTenantId.value,
    projectClassCtl: pageParameter.projectClassCtl,
    itemCode: pageParameter.itemCode
  })
}

async function saveInto(data) {
  await reloadTable()
}

</script>
<style src="./global-menu-index.less" lang="less" scoped></style>
<style lang="less" scoped>
:deep(.vben-basic-table) {
  .ant-table-wrapper {
    .ant-table-measure-row{
      td{
        padding: 0!important;
      }
    }
  }
}

.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
  border-color: #aaaaaa !important;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 13px !important;
  padding: 2px 8px !important;
  border-color: #aaaaaa !important;
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
  position: relative;
  :deep(.pagination-text){
    position: absolute;
    bottom: 240px;
    right: 200px;
    font-size: 13px;
    color: black;
    z-index: 99999999;
  }
}

:deep(.ant-table-thead) th{
  text-align: center !important;
  font-weight: bold;
  //background-color: #f2f2f2 !important;
  background-color: #cccccc !important;
  border-color: #aaaaaa !important;
}

:deep(.vben-basic-table) .ant-table-wrapper {
  padding: 0px;
}

:deep(.vben-basic-table) .ant-table {
  width: 100%;
  margin: 0;
  overflow-x: hidden;
  height: calc(100% - 160px);
  min-height: 500px;
}

:deep(.vben-basic-table) .ant-pagination {
  background-color: #cccccc;
  padding-right: 20px;
  padding-top: 5px;
  padding-bottom: 5px;
  margin: 0;
}

:deep(.vben-basic-table){
  min-height: 500px;
  height: calc(100% - 160px);
  margin-bottom: 20px;
}

:deep(.ant-input),:deep(.ant-select),:deep(.ant-btn){
  border: 1px solid #c9c9c9;
}

</style>
