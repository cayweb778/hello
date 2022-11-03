<template>
  <div>
    <div class="app-container">

      <div class="app-container-head">
<!--        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">-->
<!--        <div class="container-head-title" style="margin-left: 20px;">
          <b class="noneSpan"><UnorderedListOutlined />&nbsp;&nbsp;项目栏目样式</b>
        </div>-->
        <div class="container-head-title" style="float: left;margin-top: 10px;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 35%;text-align: center;margin-top: 30px;">
          <b class="noneSpan" style="font-size: 36px;">项目栏目样式</b>
        </div>
        <div class="ant-btn-group" style="float: right">
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="openAdd()"
          ><span>新增</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
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
          ><span>引入</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
          ><span>导出</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
          ><span>打印</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="closeCurrent()"
          ><span>退出</span></button>
        </div>
      </div>

      <div style="clear: none"/>
      <div style="margin-top: -30px;margin-left: 80px;">
        <div style="display: inline-block;float: left;margin-top: -25px;">
          <AccountPicker theme="three" @reloadTable="dynamicAdReload"/>
        </div>
        <div style="display: inline-block;position:absolute;top:85px;left:110px;font-size: 10px;color:#999999;">共 {{tableDataAll.length}} 条记录</div>
        <div style="float: right; margin-left: 10px;">
          <a-button class="ant-btn-me" @click="reloadTable()">
            <SyncOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
<!--          <a-popover class="ant-btn-default" placement="bottom">
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
          </a-popover>-->
          <!--        <a-popover placement="bottom">
                    <a-button>
                      <SettingFilled :style="{ fontSize: '14px' }" />
                    </a-button>
                  </a-popover>-->
          <a-popover class="ant-btn-default" placement="bottom">
            <template #content>
            <span class="group-btn-span-special2" @click="onChangeSwitch('')"
                  :style="flag==''?{backgroundColor: '#0096c7',color: 'white'}:''">
                <ReadOutlined/>&nbsp;&emsp;全部&emsp;&ensp;<CheckOutlined
              v-if="flag==''"/></span><br/>
              <span class="group-btn-span-special2" @click="onChangeSwitch('1')"
                    :style="flag=='1'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SafetyOutlined/>&nbsp;&emsp;启用&emsp;&ensp;<CheckOutlined
                v-if="flag=='1'"/></span><br/>
              <span class="group-btn-span-special2" @click="onChangeSwitch('0')"
                    :style="flag=='0'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <RestOutlined/>&nbsp;&emsp;停用&emsp;&ensp;<CheckOutlined
                v-if="flag=='0'"/></span>
            </template>
            <a-button>
              <PicLeftOutlined :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>
<!--          <a-button @click="activeKey=!activeKey">
            <PieChartFilled :style="{ fontSize: '14px' }"/>
          </a-button>-->
          <!--        <a-button>
                    <EditFilled :style="{ fontSize: '14px' }" />
                  </a-button>-->
          <!--        <a-popover placement="bottom">
                  </a-popover>

                  <a-button>
                    <EditFilled :style="{ fontSize: '14px' }" />
                  </a-button>

                  <a-button @click="activeKey=!activeKey">
                    <PieChartFilled :style="{ fontSize: '14px' }" />
                  </a-button>
                  <a-button>
                    <FilterFilled :style="{ fontSize: '14px' }" />
                  </a-button>-->
        </div>
<!--        <div style="float: right; position: relative">
          <a-select v-model:value="formItems.selectType" style="width: 120px;font-size: 12px;" class="special_select">
            <a-select-option style="font-size: 12px;" value="projectCateCode">编码</a-select-option>
            <a-select-option style="font-size: 12px;" value="projectCateName">名称</a-select-option>
            <a-select-option style="font-size: 12px;" value="flag">状态</a-select-option>
          </a-select>
          <a-input-search
            v-model:value="formItems.selectValue"
            placeholder=""
            style="width: 200px; border-radius: 4px"
            @search="onSearch"
          />
        </div>-->
      </div>
      <div style="clear:both"/>
    </div>
    <div class="app-container">
      <Edit
        @save="saveData"
        @register="registerEditPage"
      />
      <Add
        @save="saveData"
        @register="registerAddPage"
      />
      <BasicTable
        v-if="activeKey"
        :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
        :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
        @register="registerTable"
      >
<!--      <template #successState="{ record }">
          <span>
            <a-tag
              :color="record.successState === '1' ? 'green' : 'volcano'"
            >
              {{ record.successState === '1' ? '已生效' : '未生效' }}
            </a-tag>
          </span>
      </template>-->
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
                <!--              <p v-if="record.flag=='1'" style="cursor: pointer" @click="openEdit(record)"><FormOutlined /> 编辑</p>-->
                <p v-if="record.flag=='0'" class="p_specifics" @click="editFlagData(record)"
                   style="cursor: pointer">
                  <CheckCircleOutlined/>
                  启用
                </p>
                <p v-if="record.flag=='1'" class="p_specifics" @click="editFlagData(record)"
                   style="cursor: pointer">
                  <CloseCircleOutlined/>
                  停用
                </p>
                <!--              <p style="cursor: pointer" @click="del(record)"><DeleteOutlined /> 删除</p>-->
              </template>
            </a-popover>
          </div>
        </template>

      </BasicTable>
      <div
        v-if="!activeKey"
        :class="cardList"
      >
        <div :class="`${cardList}__content`">
          <!--        <div>ID:{{ item.id }}</div>-->
          <a-list style="height: 700px;overflow-y: scroll;overflow-x: hidden;">
            <a-row :gutter="16">
              <template
                v-for="(item) in cardList"
              >
                <a-col :span="8">
                  <a-list-item style="width: 95%">
                    <a-card
                      :hoverable="true"
                      :class="`${cardList}__card`"
                      style="width: 100%"
                    >
                      <div style="width: 100%;float: right;" class="abc" @click="openShow(item)">
                        <div :class="`${cardList}__card-title`">
                          <Icon
                            v-if="item.icon"
                            class="icon"
                            :icon="item.icon"
                            :color="item.color"
                          />
                          <!--                        {{ index+1 }}-->
                        </div>
                        <div style="float: right; border: 1px solid rgb(1, 143, 251);border-radius: 10px;padding: 0 10px; margin-top: -5px;">
                          {{ item.projectCateCode }}
                        </div>
                        <div style="font-size: 24px; margin-top: 20px;">
                          {{ item.projectCateName }}
                        </div>
                        <div style="float: right;margin-top: -20px;">
                          <a-popover placement="bottom" v-if="item.sysFlag!='1'">
                            <a-button style="padding: 0px 4px; height: 20px;margin-right: 10px;">
                              <EllipsisOutlined />
                            </a-button>
                            <template #content>
                              <p style="cursor: pointer" class="p_specifics" @click="openEdit(item)"><FormOutlined /> 修改</p>
                              <p style="cursor: pointer" class="p_specifics" @click="del(item)"><DeleteOutlined /> 删除</p>
                              <p v-if="item.flag=='0'" class="p_specifics" @click="editFlagData(item)" style="cursor: pointer">
                                <CheckCircleOutlined/>启用
                              </p>
                              <p v-if="item.flag=='1'" class="p_specifics" @click="editFlagData(item)" style="cursor: pointer">
                                <CloseCircleOutlined/>停用
                              </p>
                            </template>
                          </a-popover>
                        </div>
                      </div>
                    </a-card>
                  </a-list-item>
                </a-col>
              </template>
            </a-row>
          </a-list>
        </div>

      </div>
    </div>
  </div>
</template>
<script setup="props, {emit}" lang="ts">
// import {deleteCate, saveCate} from '/@/api/record/system/sys_project_category'

import { BasicTable, useTable } from '/@/components/Table'
import Edit from './popup/edit.vue'
import Add from './popup/add.vue'
import { useModal } from '/@/components/Modal'
import {onMounted, reactive, ref} from 'vue'
import Icon from '/@/components/Icon/index'
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
  SortDescendingOutlined,
  CheckOutlined,
  SortAscendingOutlined,
  ReadOutlined, SafetyOutlined, RestOutlined,
  EllipsisOutlined,
  UnorderedListOutlined,ProfileOutlined
} from '@ant-design/icons-vue'
import {
  cateFindAll,
  cateFindStateFlag,
  editFlag,
  deleteCate,
  saveCate, cateFindAllList
} from '/@/api/project_category/project_category'
import {
  Select as ASelect,
  Input as AInput,
  List as AList,
  Row as ARow,
  Col as ACol,
  Card as ACard,
  Popover as APopover,
  Button as AButton,
  Tag as ATag,
  message
} from 'ant-design-vue'
import {getThisAdInfoData} from "/@/api/record/system/financial-settings";
import {getCurrentAccountName, getThisIndexImg} from "/@/api/task-api/tast-bus-api";
import {useMessage} from "/@/hooks/web/useMessage";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findDataBase} from "/@/api/record/system/account";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker-DATA.vue";
import {deleteProClassByProjectCateCode, saveProClass} from "/@/api/record/system/project_class";
import {getProjectList} from "/@/api/record/system/project";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {findAllProjectItemList} from "/@/api/record/system/project-item";

const AListItem = AList.Item
const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option

const {
  createConfirm,
  createSuccessModal,
  createWarningModal
} = useMessage()

const {closeCurrent} = useTabs(router);

const formItems = ref({
  selectType: 'projectCateCode',
  selectValue: ''
})
const flag = ref('1')
const dynamicTenantId = ref(getCurrentAccountName(true))
const CrudApi = {
  list: useRouteApi(cateFindAll,{schemaName: dynamicTenantId}),
  columns: [
    // { title: '唯一标识', dataIndex: 'uniqueCode', ellipsis: true },
    {
      title: '编码',
      dataIndex: 'projectCateCode',
      width: '20%',
      ellipsis: true
    },
    {
      title: '名称',
      dataIndex: 'projectCateName',
      width: '60%',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return {style: {'text-align': 'left', '-webkit-line-clamp': 2}} // return 想要设置的样式
      },
      ellipsis: true
    },
    /*{
      title: '项目目录表名称',
      dataIndex: 'projectTable',
      ellipsis: true
    },*/
    /*{
      title: '是否生效',
      dataIndex: 'successState',
      ellipsis: true,
      slots: { customRender: 'successState' }
    },*/
    {
      title: '是否停用',
      dataIndex: 'flag',
      width: '10%',
      ellipsis: true,
      slots: {customRender: 'flag'}
    }
  ],
  editData: {
    id: '',
    projectCateCode: '',
    projectCateName: '',
    projectTable: '',
    successState: '1',
    flag: '1'
  }
}
// 这是示例组件
const [registerTable, {  reload,setTableData,setColumns,getColumns,getPaginationRef,setPagination }] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: false,
  actionColumn: {
    width: '10%',
    title: '操作',
    dataIndex: 'action',
    slots: {customRender: 'action'}
  },
  searchInfo: {
    flag: flag.value
  }
})
const [registerEditPage, { openModal: openEditPage }] = useModal()
const [registerAddPage, { openModal: openAddPage }] = useModal()
const val = {
  // id: '',
  projectCateCode: '',
  projectCateName: '',
  projectTable: '',
  successState: '',
  flag: ''
}

const openAdd = () => {
  openAddPage(true, {
    data: val,
    dynamicTenantId: dynamicTenantId.value,
    isState: '0'
  })
}
const openEdit = (data:any) => {
  openEditPage(true, {
    data: data,
    dynamicTenantId: dynamicTenantId.value,
    isState: '1'
  })
}
const openShow = (data:any) => {
  openEditPage(true, {
    data: data,
    dynamicTenantId: dynamicTenantId.value,
    isState: '2'
  })
}
const del = async(data:any) => {
  /*await useRouteApi(deleteCate, {schemaName: dynamicTenantId})(data)
  // await deleteCate(id, data)
  createSuccessModal({
    iconType: 'success',
    title: '删除',
    content: '删除成功！'
  })
  // alert('删除成功！')
  await reload({
    searchInfo: {
      flag: flag.value
    }
  })
  checkRow.value = []
  state.selectedRowKeys = []*/
  const itemList: any = await useRouteApi(findAllProjectItemList, {schemaName: dynamicTenantId})({})
  for (let i = 0; i < itemList.length; i++) {
    const item1=itemList[i]
    if (item1.cateCode==data.projectCateCode){
      createWarningModal({
        iconType: 'warning',
        title: '删除',
        content: '<span style="font-weight: bold;color: red;">[' + data.projectCateName + ']</span>项目栏目样式已在项目大类中使用，不能进行删除操作！'
      })
      return false
    }
  }
  const res: any = await useRouteApi(getProjectList, {schemaName: dynamicTenantId})({projectCateCode: data.projectCateCode})
  if (res.items.length > 0) {
    createWarningModal({
      iconType: 'warning',
      title: '删除',
      content: '<span style="font-weight: bold;color: red;">[' + data.projectCateName + ']</span>项目栏目样式已在项目项目中使用，不能进行删除操作！'
    })
    return false
  }
  createConfirm({
    iconType: 'error',
    title: '警告',
    content: '删除后数据将不能恢复，你确认要删除吗?',
    onOk: async () => {
      await useRouteApi(deleteCate, {schemaName: dynamicTenantId})(data)
      message.success('删除成功！')
      await reloadTable()
    },
    onCancel: () => {
      return false
    }
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
const checkRow: any = ref([])
const onSelectChange = (selectedRowKeys, row) => {
  // console.log('selectedRowKeys changed: ', row);
  state.selectedRowKeys = selectedRowKeys;
  checkRow.value = row
};
const editOpen = () => {
  if (checkRow.value.length == 1) {
    openEditPage(true, {
      data: checkRow.value[0],
      dynamicTenantId: dynamicTenantId.value,
      isState: '2'
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
      const itemList: any = await useRouteApi(findAllProjectItemList, {schemaName: dynamicTenantId})({})
      for (let i = 0; i < itemList.length; i++) {
        const item1=itemList[1]
        if (item1.cateCode==item.projectCateCode){
          createWarningModal({
            iconType: 'warning',
            title: '删除',
            content: '<span style="font-weight: bold;color: red;">[' + item.projectCateName + ']</span>项目栏目样式已在项目大类中使用，不能进行删除操作！'
          })
          return false
        }
      }
      const res: any = await useRouteApi(getProjectList, {schemaName: dynamicTenantId})({projectCateCode: item.projectCateCode})
      if (res.length > 0) {
        createWarningModal({
          iconType: 'warning',
          title: '删除',
          content: '<span style="font-weight: bold;color: red;">[' + item.projectCateName + ']</span>项目栏目样式已在项目项目中使用，不能进行删除操作！'
        })
        return false
      }
    }
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据将不能恢复，你确认要删除吗?',
      onOk: async () => {
        for (let i = 0; i < checkRow.value.length; i++) {
          const item = checkRow.value[i]
          await useRouteApi(deleteCate, {schemaName: dynamicTenantId})(item)
          //删除项目大类下的所有项目分类
          // await useRouteApi(deleteProClassByProjectCateCode, {schemaName: dynamicTenantId})(item.projectCateCode)
        }
        checkRow.value = []
        state.selectedRowKeys = []
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

const activeKey = ref(false)
const cardList: any = ref([])
const tableDataAll:any = ref([])

async function reloadCurrentPage() {
  const res: any = await useRouteApi(cateFindAllList, {schemaName: dynamicTenantId})({})
  tableDataAll.value = res
  if (flag.value=='1'){
    cardList.value = tableDataAll.value.filter(item => item.flag=='1')
  } else if(flag.value=='0'){
    cardList.value = tableDataAll.value.filter(item => item.flag=='0')
  } else {
    cardList.value = tableDataAll.value
  }
  // console.log(cardList.value)
}

async function reloadTable() {
  if (activeKey.value){
    await reload()
  } else {
    await reloadCurrentPage()
  }
  checkRow.value = []
  state.selectedRowKeys = []
}

onMounted(async () => {
  await reloadCurrentPage()
})
async function saveData(data:any) {
  await useRouteApi(saveCate, {schemaName: dynamicTenantId})(data)
  /*if (data.id == null || data.id == "") {
    //添加项目分类
    const item = {
      uniqueCode: '',
      projectClassCode: '1',
      projectClassName: data.projectCateName,
      projectCateCode: data.projectCateCode,
      parentId: '0'
    }
    await useRouteApi(saveProClass, {schemaName: dynamicTenantId})(item)
    const item1 = {
      uniqueCode: '9999',
      projectClassCode: '9999',
      projectClassName: '未分配',
      projectCateCode: data.projectCateCode,
      parentId: '0'
    }
    await useRouteApi(saveProClass, {schemaName: dynamicTenantId})(item1)
  }*/
  await reloadTable()
}

async function editFlagData(data: any) {
  await useRouteApi(editFlag, {schemaName: dynamicTenantId})(data)
  await reloadTable()
}

function onChangeSwitch(str) {
  flag.value = str
  if (flag.value=='1'){
    cardList.value = tableDataAll.value.filter(item => item.flag=='1')
  } else if(flag.value=='0'){
    cardList.value = tableDataAll.value.filter(item => item.flag=='0')
  } else {
    cardList.value = tableDataAll.value
  }
}

function onSearch() {
  let a: any = []
  a = cardList.value.filter(item => {
    //通过项目编码
    if (formItems.value.selectType == 'projectCateCode' && formItems.value.selectValue != '') {
      return item.projectCateCode.indexOf(formItems.value.selectValue) != -1
    }
    //通过项目名称
    if (formItems.value.selectType == 'projectCateName' && formItems.value.selectValue != '') {
      return item.projectCateName.indexOf(formItems.value.selectValue) != -1
    }
    //通过状态过滤
    if (formItems.value.selectType=='flag' && formItems.value.selectValue!=''){
      if (formItems.value.selectValue=='启用' || formItems.value.selectValue=='1'){
        return item.flag=='1'
      }
      if (formItems.value.selectValue=='停用' || formItems.value.selectValue=='0'){
        return item.flag!='1'
      }
      return item.flag.indexOf(formItems.value.selectValue) != -1
    }
    return item
  })
  // console.log(a)
  // setDataSource(a)
  setTableData([])
  setTableData(a)
}

const pageParameter:any = reactive({
  showRulesSize: 'MIN',
  companyCode: '100',
  companyName: '北京希格科技',
  ifUnit: false,
})
const defaultPage = ref(false)
onMounted(async() => {
  // 进页面执行
  getThisAdInfoData({'accId': getCurrentAccountName(false)}).then(async res => {
    if (null != res && res.independent == 0) {
      defaultPage.value = false
      pageParameter.companyName = res.accNameCn
      pageParameter.companyCode = res.coCode
      pageParameter.ifUnit = res.icorp == '1'
    }
  })
})
const dynamicAdReload = async (obj) =>{
  // const dataBase:any = await findDataBase(obj.accId,obj.year)
  const card: any = await useRouteApi(cateFindStateFlag, {schemaName: obj.accountMode})()
  cardList.value = card
  dynamicTenantId.value = obj.accountMode
  if (activeKey.value == true) {
    let data: any = {}
    data.page = getPaginationRef().current
    data.size = getPaginationRef().pageSize
    data.flag = flag.value
    let res: any = await useRouteApi(cateFindAll, {schemaName: obj.accountMode})(data)
    setTableData([]) // 清空可能残留的数据
    setTableData(res.items)
    // 底部分页信息
    dynamicTenantId.value = obj.accountMode
    pageParameter.thisAdInfo = {}
    pageParameter.thisAdInfo = obj
    pageParameter.total = res.total
    setPagination({total: res.total})
  }
}
</script>
<style src="./global-menu-index.less" lang="less" scoped></style>
<style scoped lang="less">
:deep(.ant-card-body) {
  padding: 16px;
  border-left: 2px solid rgb(1, 143, 251);
  box-shadow: rgb(72 113 140) -3px 1px 7px -1px;
}
.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 16px !important;
  padding: 5px 8px !important;
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

.abc:hover {
  color: blue; /*DiV背景颜色*/
}
</style>
