<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <!--        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">-->
        <div class="container-head-title" style="margin-left: 20px;">
          <b class="noneSpan"><UnorderedListOutlined />&nbsp;&nbsp;组织-项目分类</b>
        </div>

        <div class="ant-btn-group" data-v-a1ccd506="" style="float: right">
<!--          <a-button class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false"
                    @click="openAdd()"><span>新建</span></a-button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="editOpen()"
          ><span>修改</span></button>-->
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="delList()"
          ><span>删除</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="openInto()"
          ><span>引入</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="openAssign()"
          ><span>分配</span></button>
          <!--          <a-button class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false"><span>打印</span>
                    </a-button>
                    <a-button class="ant-btn ant-btn-me"><span>导入</span></a-button>
                    <a-button class="ant-btn ant-btn-me"><span>导出</span></a-button>-->
          <a-button class="ant-btn ant-btn-me" @click="closeCurrent()"><span>退出</span></a-button>
        </div>
      </div>
      <div style="clear: none;"></div>
      <div class="app-container-neck">
        <div style="display: inline-block;float: left;margin-top: .2%;" class="select-div">
          &ensp;<span>所属组织：</span>
          <a-select v-model:value="originId" @change="changeOrigin()" style="width: 250px;margin-top: -10px;margin-left: 10px">
            <a-select-option v-for="item in organizeList" :key="item.uniqueCode">
              {{ item.orgSimpName }}
            </a-select-option>
          </a-select>
        </div>
        <div style="float: left; margin-left: 10px;line-height: 30px;">共 {{cardList.length}} 条记录</div>

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
    </div>
    <div class="app-container">
      <EditPage @save="saveData" @register="registerEditPage"/>
      <Into @save="saveInto" @register="registerIntoPage"/>
      <Assign @save="saveAssign" @register="registerAssignPage"/>

      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <div class="w-1/4 xl:w-1/5" style="width: 300px;min-height: 250px;border: 1px #cccccc solid;margin-right: .2%;margin-bottom: 58px;">
          <ClassTree v-if="isShowTree" @select="handleSelect" v-model="pageParameter" />
        </div>
        <div style="width: calc(100% - 300px); float: right;margin-left: 5px;">
          <BasicTable
            :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
            @row-click="condClick"
            :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
            :dataSource="tableData"
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
  projectClassFindAll
} from '/@/api/record/system/origin-project-class'
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
  UnorderedListOutlined
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
import {getCurrentAccountName,getThisIndexImg} from "/@/api/task-api/tast-bus-api";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {findAllList} from "/@/api/record/system/origin-project";
import {getOrganizeAll} from "/@/api/record/group/im-organize";
import {
  findByOriginIdAndUniqueCode,
  findByUniqueCode, saveAccount
} from "/@/api/record/system/group-project-class-account";

const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option

const {closeCurrent} = useTabs(router);

//查询组织
const organizeList:any = ref([])
const originId:any = ref('')
async function reloadOrigin(){
  organizeList.value = await getOrganizeAll()
  if (organizeList.value.length>0){
    originId.value = organizeList.value[0].uniqueCode
    pageParameter.originId = originId.value
  }
}
//改变组织
async function changeOrigin() {
  pageParameter.originId = originId.value
  await reloadCurrentPage()
}

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

async function handleSelect(keys) {
  // debugger
  loadMark.value = true
  cardList.value = tableDataAll.value.filter(item =>{
    if (keys!=null && keys!='' && keys!='0' && keys!='undefined') {
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
// 这是示例组件
const [registerTable, { reload,setTableData,setColumns,getColumns,getPaginationRef,setPagination }] = useTable({
  columns: columns,
  bordered: true,
  showIndexColumn: false,
  pagination: {
    pageSize: 50,
    showSizeChanger: true,
    pageSizeOptions: ['10', '25', '50', '100'],
    showTotal: t => `总共${t}条数据`
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
  originId: ''
}
function openAdd(){
  openAddPage()
}
const openAddPage = () => {
  val.projectCateCode = pageParameter.itemCode
  val.originId = originId.value
  openEditPage(true, {
    data: val,
    isState: '0'
  })
}
const condClick = (data:any) => {
  /*openEditPage(true, {
    data: data,
    projectCateCode: cateCode.value,
  })*/
}

const del = async(data:any) => {
  await deleteProClass(data)
  // alert('删除成功！')
  createSuccessModal({
    iconType: 'success',
    title: '删除',
    content: '删除成功！'
  })
  await reloadTable()
}

async function saveData(data:any) {
  await saveProClass(data)
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
const onSelectChange = (selectedRowKeys, row) => {
  // console.log('selectedRowKeys changed: ', row);
  state.selectedRowKeys = selectedRowKeys;
  checkRow.value = row
};
const editOpen = () => {
  if (checkRow.value.length == 1) {
    checkRow.value[0].projectCateCode = pageParameter.itemCode
    checkRow.value[0].projectClassCtl = pageParameter.projectClassCtl
    openEditPage(true, {
      data: checkRow.value[0],
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
    for (let i = 0; i < checkRow.value.length; i++) {
      const item = checkRow.value[i]
      const res = await findByUniqueCode(item.uniqueCode)
      if (res.length > 0){
        const list = res.filter(aa=> aa.ctype=='2' && aa.originId==originId.value)
        if (list.length>0) {
          createWarningModal({
            iconType: 'warning',
            title: '删除',
            content: '人员信息已分配不允许删除！'
          })
          return false
        }
      }
    }
    const projectList: any = await findAllList(originId.value)
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
          const item1 = await findByOriginIdAndUniqueCode(originId.value,item.uniqueCode)
          if (item1.length>0) {
            item1[0].flag = '0'
            item1[0].acceptUser = ''
            item1[0].acceptDate = ''
            await saveAccount(item1[0])
          }
          await deleteProClass(item)
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
  await reloadOrigin()
  await reloadTable()
})
const cardList:any = ref([])
async function reloadCurrentPage() {
  loadMark.value = true
  const res: any = await projectClassFindAll(originId.value)
  cardList.value = res
  tableDataAll.value = res
  setTableData(tableData.value)
  await setPagination({total: tableData.value.length})
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
  originId: originId.value
})

//引入
import Into from './popup/into.vue'
const [registerIntoPage, {openModal: openIntoPage}] = useModal()
const openInto = () => {
  openIntoPage(true, {
    originId: originId.value
  })
}

async function saveInto(data) {
  await reloadTable()
}

//分配
import Assign from './popup/assign.vue'
const [registerAssignPage, {openModal: openAssignPage}] = useModal()
const openAssign = () => {
  openAssignPage(true, {
    originId: originId.value
  })
}

async function saveAssign(data) {
}

</script>
<style src="./global-menu-index.less" lang="less" scoped></style>
<style lang="less" scoped>
.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 16px !important;
  padding: 5px 8px !important;
  border-color: #aaaaaa !important;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 4px 8px !important;
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
