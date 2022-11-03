<template>
  <div>
    <div class="app-container">

    <div class="app-container-head">
      <div class="container-head-title">
        <UnorderedListOutlined style="color: #0096c7"/>
        <b class="noneSpan">集团-现金流量项目</b>
      </div>
      <div
        class="ant-btn-group"
        data-v-a1ccd506=""
        style="float: right"
      >
<!--        <button
          type="button"
          class="ant-btn ant-btn-me"
          ant-click-animating-without-extra-node="false"
          @click="openAddPage()"
        ><span>新建</span></button>-->
        <button
          type="button"
          class="ant-btn ant-btn-me "
          ant-click-animating-without-extra-node="false"
        ><span>在线同步</span></button>
        <button
          type="button"
          class="ant-btn ant-btn-me"
          ant-click-animating-without-extra-node="false"
          @click="editOpen()"
        ><span>编辑科目</span></button>
<!--        <button
          type="button"
          class="ant-btn ant-btn-me"
          ant-click-animating-without-extra-node="false"
          @click="delList()"
        ><span>删除</span></button>-->
        <!--        按年度校验科目是否末级-->
        <button
          type="button"
          class="ant-btn ant-btn-me "
          ant-click-animating-without-extra-node="false"
        ><span>校验科目</span></button>

<!--        <button
          type="button"
          class="ant-btn ant-btn-me"
          @click="openExcel()"
        ><span>导入</span></button>-->

        <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent(),router.push('/system/home/welcome')"><span>退出</span></button>
      </div>
    </div>
    <div class="app-container-neck">
      <div style="float: left;font-size: 16px;margin-left: 30px;margin-left: 20px;">
        会计准则：<a-select v-model:value="accStandard" @change="changAccStandard()" placeholder="请选择会计准则" style="width: 230px;">
        <a-select-option v-for="item in accStandardList" :key="item.uniqueAccStandard" :value="item.uniqueAccStandard">
          ({{ item.uniqueAccStandard }}){{ item.accStandardName }}
        </a-select-option>
      </a-select>
      </div>
      <div style="float: right; margin-left: 10px">
        <a-button class="ant-btn-me">
          <SyncOutlined :style="{ fontSize: '14px' }" />
        </a-button>
        <a-popover placement="bottom">
          <template #content>
            <span @click="pageParameter.showRulesSize = 'MAX'"
                  :style="{backgroundColor: (pageParameter.showRulesSize==='MAX')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;&emsp;<SortDescendingOutlined
              :style="{ fontSize: '14px' }"/>&emsp;大号字体&emsp;<CheckOutlined v-if="pageParameter.showRulesSize==='MAX'"
                                                                            :style="{ fontSize: '14px' }"/></span><br/>
            <span @click="pageParameter.showRulesSize = 'MIN'"
                  :style="{backgroundColor: (pageParameter.showRulesSize==='MIN')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;&emsp;<SortAscendingOutlined
              :style="{ fontSize: '14px' }"/>&emsp;小号字体&emsp;<CheckOutlined v-if="pageParameter.showRulesSize==='MIN'"
                                                                            :style="{ fontSize: '14px' }"/></span>
          </template>
          <template #title>
            <b>设置表格字号</b>
          </template>
          <a-button>
            <SettingFilled :style="{ fontSize: '14px' }"/>
          </a-button>
        </a-popover>
        <a-button>
          <UsbOutlined :style="{ fontSize: '14px' }" />
        </a-button>
        <a-button @click="exportExcel()">
          <PrinterOutlined :style="{ fontSize: '14px' }" />
        </a-button>

        <a-button>
          <FilterFilled :style="{ fontSize: '14px' }" />
        </a-button>
      </div>
      <div style="float: right; position: relative">
        <!--        <label style="font-size: 14px">检索条件：</label>-->
        <a-select v-model:value="formItems.selectType" style="width: 120px;font-size: 12px;" class="special_select">
          <a-select-option style="font-size: 12px;" value="projectCode">项目编码</a-select-option>
          <a-select-option style="font-size: 12px;" value="projectName">项目名称</a-select-option>
          <a-select-option style="font-size: 12px;" value="projectType">类别编码</a-select-option>
          <a-select-option style="font-size: 12px;" value="projectTypeName">类别名称</a-select-option>
          <a-select-option style="font-size: 12px;" value="fangxaing">方向</a-select-option>
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
    <Edit
      @save="saveData"
      @register="registerEditPage"
    />
    <Excel
      @save="saveExcel"
      @register="registerExcelPage"
    />

    <PageWrapper dense content-full-height fixed-height content-class="flex" >
      <div class="bg-white" style="display: inline;float: left;margin-top: 0px;">
        <div class="w-1/4 xl:w-1/5" style="width: 250px;height: 100%;border: 1px #cccccc solid;margin-right: .2%;">
          <DeptTree   @select="handleSelect" v-model="accStandard" ref="mychild"/>
        </div>
      </div>

      <div style="display: inline;width: calc(100% - 250px);float: right;padding-left: 6px;">
        <BasicTable
          ref="tableRef"
          :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
          :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
          @row-click="condClick"
          @register="registerTable"
        >
          <template #projectName="{ record }" >
            <span style="float: left" >{{ record.projectName }}</span>
          </template>

          <template #projectType="{ record }"> {{ formatProjectType(record.projectType) }} </template>
          <template #fangxiang="{ record }"> {{ formatFangxiang(record.fangxiang) }} </template>
          <template #jcode="{ record }"> <span style="float: left" >{{ record.jcode }}</span> </template>
          <template #dcode="{ record }"> <span style="float: left" >{{ record.dcode }}</span> </template>

          <template #flag="{ record }">
              <span>
                <a-tag
                  :color="record.flag === '1' ? 'green' : 'volcano'"
                >
                  {{ record.flag === '1' ? '启用' : '停用' }}
                </a-tag>
              </span>
          </template>
          <template #action="{ record }">

            <div>
              <a-popover placement="bottom">
                <a-button style="padding: 0px 4px; height: 20px;">
                  <CaretDownFilled />
                </a-button>
                <template #content>
                  <p style="cursor: pointer" @click="openEdit(record)"><FormOutlined /> 编辑</p>
<!--                  <p v-if="record.flag=='0'" @click="editFlagData(record)" style="cursor: pointer"><CheckCircleOutlined /> 启用</p>
                  <p v-if="record.flag=='1'" @click="editFlagData(record)" style="cursor: pointer"><CloseCircleOutlined /> 停用</p>
                  <p style="cursor: pointer" @click="del(record)"><DeleteOutlined /> 删除</p>-->
                </template>
              </a-popover>
            </div>
          </template>
        </BasicTable>
      </div>

    </PageWrapper>

  </div>
  </div>
</template>
<script setup="props, {emit}" lang="ts">
import {
  getProjectCashList,
  deleteProjectCash,
  saveProjectCash,
  editFlag,
  getProjectCashByAccStandard
} from '/@/api/record/system/group-project-cash'
import { BasicTable, useTable } from '/@/components/Table'
import Edit from './popup/edit.vue'
import Excel from './popup/excel.vue'
import DeptTree from './DeptTree.vue'
import { useModal } from '/@/components/Modal'
import {
  CaretDownFilled,
  FormOutlined,
  DeleteOutlined,
  SettingFilled,
  SyncOutlined,
  FilterFilled,SortDescendingOutlined,CheckOutlined,SortAscendingOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  UsbOutlined,PrinterOutlined,UnorderedListOutlined
} from '@ant-design/icons-vue'
import {onMounted, reactive, ref} from "vue";
import {
  Select as ASelect,
  Input as AInput,
  Popover as APopover,
  Button as AButton,
  Tag as ATag,
  message
} from 'ant-design-vue'
import {getThisAdInfoData} from "/@/api/record/system/financial-settings";
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findAllAcctandardList} from "/@/api/accstandard/accstandard";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {useMessage} from "/@/hooks/web/useMessage";

const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option
const {closeCurrent} = useTabs(router);

const formItems = ref({
  selectType: 'projectCode',
  selectValue: ''
})

const accStandardList:any = ref([])
const accStandard = ref('')
async function reloadAccStandard(){
  // 会计准则list
  const res = await findAllAcctandardList()
  accStandardList.value = res
  if (accStandardList.value.length>0 && (accStandard.value=='' || accStandard.value==null)){
    accStandard.value = accStandardList.value[0].uniqueAccStandard
    //刷新tree
    //刷新子页面list
    mychild.value.fetch(accStandard.value)
    await reload()
  }
}
const mychild =  ref(null)
function changAccStandard(){
  reload()
  //刷新tree
  //刷新子页面list
  mychild.value.fetch(accStandard.value)
}
onMounted(async() => {
  await reloadAccStandard()
})

const CrudApi = {
  list: async () => {
    return await getProjectCashByAccStandard({accStandard:accStandard.value})
  },
  columns: [
    {
      title: 'ID',
      dataIndex: 'id',
      defaultHidden: true,
      ellipsis: true
    },
    {
      title: '项目编码',
      dataIndex: 'projectCode',
      width: 100,
      ellipsis: true
    },
    {
      title: '项目名称',
      dataIndex: 'projectName',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      slots: { customRender: 'projectName' },
      ellipsis: true
    },
    {
      title: '类别编码',
      dataIndex: 'projectType',
      width: 100,
      ellipsis: true,
      // slots: { customRender: 'projectType' }
    },
    {
      title: '类别名称',
      dataIndex: 'projectTypeName',
      width: 180,
      ellipsis: true,
    },
    {
      title: '方向',
      dataIndex: 'fangxiang',
      width: 100,
      ellipsis: true,
      slots: { customRender: 'fangxiang' }
    },
    {
      title: '对应借方科目',
      dataIndex: 'jcode',
      ellipsis: true,
      width: 200,
      slots: { customRender: 'jcode' },
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
    },
    {
      title: '对应贷方科目',
      dataIndex: 'dcode',
      ellipsis: true,
      width: 200,
      slots: { customRender: 'dcode' },
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
    },
    /*{
      title: '状态',
      dataIndex: 'flag',
      width: 100,
      ellipsis: true,
      slots: { customRender: 'flag' }
    }*/
  ],
  editData: {
    id: '',
    projectCode: '',
    projectName: '',
    projectType: '',
    projectTypeName: '',
    fangxiang: '1',
    flag: '1'
  }
}
/*function formatProjectType(projectType:any) {
  let str = projectType
  switch (projectType) {
    case '1':
      str = '经营活动'
      break
    case '2':
      str = '投资活动'
      break
    case '3':
      str = '筹资活动'
      break
    case '4':
      str = '汇率活动'
      break
    case '5':
      str = '现金及现金等价物'
      break
  }
  return str
}*/
function formatFangxiang(fangxiang:any) {
  let str = fangxiang
  switch (fangxiang) {
    case '1':
      str = '流入'
      break
    case '0':
      str = '流出'
      break
  }
  return str
}
// 这是示例组件
const [registerTable, { reload,getColumns,setTableData }] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: false,
  pagination: {
    pageSize: 50,
    showSizeChanger: true,
    pageSizeOptions: ['10', '30', '50', '100'],
    showTotal: t => `总共${t}条数据`
  },
  actionColumn: {
    width: 100,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' }
  }
})
const [registerEditPage, { openModal: openEditPage }] = useModal()
const val = {
  // id: '',
  projectCode: '',
  projectName: '',
  projectType: '',
  projectTypeName: '',
  fangxiang: '1',
  flag: '1'
}
const condClick = (data:any,index:any, e:any) => {
  console.log(index)
  if (e.target.cellIndex == 1) {
    openEditPage(true, {
      data: data,
      accStandard: accStandard.value
    })
  }
}
const openAddPage = () => {
  openEditPage(true, {
    data: val,
    accStandard: accStandard.value
  })
}
const openEdit = (data:any) => {
  openEditPage(true, {
    data: data,
    accStandard: accStandard.value
  })
}
const {
  createWarningModal
} = useMessage()

const editOpen = () => {
  if (checkRow.value.length==1) {
    openEditPage(true, {
      data: checkRow.value[0],
      showEdit: true
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '编辑',
      content: '请选择一条进行编辑！'
    })
  }
}

const del = async(data:any) => {
  await deleteProjectCash(data)
  // alert('删除成功！')
  message.success('删除成功！')
  await reload()
}

async function saveData(data:any) {
  await saveProjectCash(data)
  await reload()
}

async function editFlagData(data:any){
  await editFlag(data)
  await reload()
}

const cardList:any = ref([])
async function reloadCurrentPage() {
  const res:any = await getProjectCashList()
  cardList.value = res.items
}
//搜索
async function onSearch(){
  await reloadCurrentPage()
  let a:any = []
  a = cardList.value.filter(item=> {
    //通过项目编码
    if (formItems.value.selectType=='projectCode' && formItems.value.selectValue!=''){
      return item.projectCode.indexOf(formItems.value.selectValue) != -1
    }
    //通过项目名称
    if (formItems.value.selectType=='projectName' && formItems.value.selectValue!=''){
      return item.projectName.indexOf(formItems.value.selectValue) != -1
    }
    //通过类别编码
    if (formItems.value.selectType=='projectType' && formItems.value.selectValue!=''){
      return item.projectType.indexOf(formItems.value.selectValue) != -1
    }
    //通过类别名称
    if (formItems.value.selectType=='projectTypeName' && formItems.value.selectValue!=''){
      return item.projectTypeName.indexOf(formItems.value.selectValue) != -1
    }
    //通过方向过滤
    if (formItems.value.selectType=='fangxiang' && formItems.value.selectValue!=''){
      if (formItems.value.selectValue=='流入'){
        return item.fangxiang=='1'
      }
      if (formItems.value.selectValue=='流出'){
        return item.fangxiang!='1'
      }
      return item.fangxiang.indexOf(formItems.value.selectValue) != -1
    }
    return item
  })
  // console.log(a)
  // setDataSource(a)
  setTableData([])
  setTableData(a)
}
const pageParameter = reactive({
  showRulesSize: 'MIN',
  companyCode: '100',
  companyName: '北京希格科技',
  ifUnit: false,
})

//文件导入
const [registerExcelPage, { openModal: openExcelPage }] = useModal()
const openExcel = () => {
  openExcelPage(true, {

  })
}

//选中内容
const state = reactive<{
  selectedRowKeys: [];
  loading: boolean;
}>({
  selectedRowKeys: [], // Check here to configure the default column
  loading: false,
});
const checkRow:any = ref([])
const onSelectChange = (selectedRowKeys,row) => {
  // console.log('selectedRowKeys changed: ', row);
  state.selectedRowKeys = selectedRowKeys;
  checkRow.value = row
};
</script>
<style src="./global-menu-index.less" lang="less" scoped></style>

<style lang='less' scoped >
.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 16px !important;
  padding: 5px 8px !important;
}
.bg-white{
  width: 250px ;
  min-height: 250px ;
  height: calc(100% - 228px);
  border: 1px #cccccc solid;
  background:white ;
  margin-top: -0.5%;
}
.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 4px 8px !important;
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
.app-container-head{
  padding-left: 5px;
}
</style>

