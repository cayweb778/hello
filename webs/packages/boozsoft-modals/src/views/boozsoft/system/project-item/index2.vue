<template>
  <div>
    <div class="app-container">

      <div class="app-container-head">
<!--        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">-->
<!--        <div class="container-head-title" style="margin-left: 20px;">
          <b class="noneSpan"><UnorderedListOutlined />&nbsp;&nbsp;项目大类</b>
        </div>-->
        <div class="container-head-title" style="float: left;margin-top: 10px;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 35%;text-align: center;margin-top: 30px;">
          <b class="noneSpan" style="font-size: 36px;">项目大类</b>
        </div>
        <div class="ant-btn-group" style="float: right">
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="openAdd()"
            v-if="defaultPage"
          ><span>新建</span></button>
<!--          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="editOpen()"
          ><span>修改</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="delList()"
          ><span>删除</span></button>-->
          <button
            type="button"
            class="ant-btn ant-btn-me"
            v-if="!defaultPage"
            @click="openInto()"
          ><span>引入</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            v-if="defaultPage"
            @click="openExcel()"
          ><span>导入</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="exportExcel()"
          ><span>导出</span></button>
<!--          <button
            type="button"
            class="ant-btn ant-btn-me"
          ><span>打印</span></button>-->
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
        <div style="display: inline-block;position:absolute;top:85px;left:110px;font-size: 10px;color:#999999;">共 {{cardList.length}} 条记录</div>
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
          <a-popover class="ant-btn-default" placement="bottom">
            <template #content>
            <span class="group-btn-span-special2" @click="onChangeSwitch('')"
                  :style="flag==''?{backgroundColor: '#0096c7',color: 'white'}:''">
                <ReadOutlined/>&nbsp;&emsp;全部&emsp;&ensp;<CheckOutlined
              v-if="flag==''"/></span><br/>
              <span class="group-btn-span-special2" @click="onChangeSwitch('1')"
                    :style="flag=='1'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SafetyOutlined/>&nbsp;&emsp;启用&emsp;&ensp;<CheckOutlined v-if="flag=='1'"/></span><br/>
              <span class="group-btn-span-special2" @click="onChangeSwitch('0')"
                    :style="flag=='0'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <RestOutlined/>&nbsp;&emsp;停用&emsp;&ensp;<CheckOutlined v-if="flag=='0'"/></span>
            </template>
            <a-button>
              <PicLeftOutlined :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>
          <!--        <a-popover placement="bottom">
                    <a-button>
                      <SettingFilled :style="{ fontSize: '14px' }" />
                    </a-button>
                  </a-popover>-->
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
            <a-select-option style="font-size: 12px;" value="1">项目大类编码</a-select-option>
            <a-select-option style="font-size: 12px;" value="2">项目大类名称</a-select-option>
          </a-select>
          <a-input-search
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
      <Excel
        @save="saveExcel"
        @register="registerExcelPage"
      />
      <Into @save="saveInto" @register="registerIntoPage"/>

      <BasicTable
        v-if="activeKey"
        :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
        :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
        @register="registerTable"
      >
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
                <p v-if="record.flag=='0'" class="p_specifics" @click="editFlagData(record)" style="cursor: pointer">
                  <CheckCircleOutlined/>启用
                </p>
                <p v-if="record.flag=='1'" class="p_specifics" @click="editFlagData(record)" style="cursor: pointer">
                  <CloseCircleOutlined/>停用
                </p>
                <!--              <p v-if="record.flag=='0'" style="cursor: pointer" @click="del(record)"><DeleteOutlined /> 删除</p>-->
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
                        <div
                          style="float: right; border: 1px solid rgb(1, 143, 251);border-radius: 10px;padding: 0 10px; margin-top: -5px;">
                          {{ item.itemCode }}
                        </div>
                        <div style="font-size: 24px;">
                          {{ item.itemName }}
                        </div>
                        <div style="font-size: 14px;">
                          <span style="font-size: 13px;">栏目样式：</span>
                          ({{ item.cateCode }}){{ formatCateName(item.cateCode) }}
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
import { BasicTable, useTable } from '/@/components/Table'
import Edit from './popup/edit.vue'
import Excel from './popup/excel.vue'
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
  ReadOutlined,
  SafetyOutlined,
  RestOutlined,
  EllipsisOutlined,
  UnorderedListOutlined,
  ProfileOutlined
} from '@ant-design/icons-vue'
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
import {
  deleteProjectItem, editByFlag,
  findProjectItemList,
  getProjectItemList,
  saveProjectItem,
  excelProjectItem, findAllProjectItemList
} from "/@/api/record/system/project-item";
import {useMessage} from "/@/hooks/web/useMessage";
//import {aoaToSheetXlsx} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
const aoaToSheetXlsx=null
import {getThisAdInfoData} from "/@/api/record/system/financial-settings";
import {getCurrentAccountName, getThisIndexImg} from "/@/api/task-api/tast-bus-api";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker-DATA.vue";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {findAllList, getProjectList} from "/@/api/record/system/project";
import {deleteProClassByProjectCateCode, saveProClass} from "/@/api/record/system/project_class";
import {cateFindStateFlag} from "/@/api/project_category/project_category";
import {findByTenantIdAndUniqueCode, save} from "/@/api/record/system/group-project-item-account";

const AListItem = AList.Item
const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option

const {
  createSuccessModal,
  createErrorModal,
  createWarningModal,
  createConfirm
} = useMessage()

const {closeCurrent} = useTabs(router);

const formItems = ref({
  selectType: '1'
})
const flag = ref('1')

const dynamicTenantId = ref(getCurrentAccountName(true))
const defaultAdName = ref(useCompanyOperateStoreWidthOut().getSchemaName)
const CrudApi = {
  list: useRouteApi(findProjectItemList, {schemaName: dynamicTenantId}),
  columns: [
    {
      title: '项目大类编码',
      dataIndex: 'itemCode',
      width: 120,
      ellipsis: true
    },
    {
      title: '项目大类名称',
      dataIndex: 'itemName',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      align: 'left',
      ellipsis: true
    },
    {
      title: '状态',
      dataIndex: 'flag',
      width: 100,
      ellipsis: true,
      slots: { customRender: 'flag' }
    }
  ],
  editData: {
    definedCode: '',
    definedName: '',
    flag: '',
    remarks: ''
  }
}
// 这是示例组件
const [registerTable, {
  reload,
  getDataSource,
  setTableData,
  setPagination,
  getPaginationRef
}] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: false,
  actionColumn: {
    width: 120,
    title: '操作',
    dataIndex: 'action',
    slots: {customRender: 'action'}
  }
})

function formatCateName(cateCode){
  let str = ''
  cateList.value.forEach(item=>{
    if (item.projectCateCode==cateCode){
      str = item.projectCateName
    }
  })
  return str
}

const [registerEditPage, { openModal: openEditPage }] = useModal()
const [registerExcelPage, { openModal: openExcelPage }] = useModal()
const val = {
  id: null,
  itemCode: '',
  itemName: '',
  flag: '1',
  cateCode: ''
}

const openAdd = () => {
  if (pageParameter.projectClassCtl=='1') {
    if (cateList.value.length>0) {
      openEditPage(true, {
        data: val,
        dynamicTenantId: dynamicTenantId.value,
        defaultAdName: defaultAdName.value,
        projectClassCtl: pageParameter.projectClassCtl,
        isState: '0'
      })
    } else {
      createWarningModal({
        iconType: 'warning',
        title: '提示',
        content: '暂无项目栏目样式，请先添加项目栏目样式！'
      })
      return false
    }
  } else {
    openEditPage(true, {
      data: val,
      dynamicTenantId: dynamicTenantId.value,
      defaultAdName: defaultAdName.value,
      projectClassCtl: pageParameter.projectClassCtl,
      isState: '0'
    })
  }
}
const openEdit = (data:any) => {
  openEditPage(true, {
    data: data,
    dynamicTenantId: dynamicTenantId.value,
    defaultAdName: defaultAdName.value,
    projectClassCtl: pageParameter.projectClassCtl,
    isState: '1'
  })
}

const openShow = (data:any) => {
  openEditPage(true, {
    data: data,
    dynamicTenantId: dynamicTenantId.value,
    defaultAdName: defaultAdName.value,
    projectClassCtl: pageParameter.projectClassCtl,
    isState: '2'
  })
}

const del = async(data:any) => {
  /*await useRouteApi(deleteProjectItem, {schemaName: dynamicTenantId})(data)
  // await deleteCate(id, data)
  // alert('删除成功！')
  createSuccessModal({
    iconType: 'success',
    title: '删除',
    content: '删除成功！'
  })
  await reload()*/
  if (pageParameter.projectClassCtl=='1'){//项目管控
    //判断大类下的是否有项目
    const res: any = await useRouteApi(findAllList, {schemaName: dynamicTenantId})({itemCode:data.itemCode,projectClassCtl:pageParameter.projectClassCtl})
    // console.log(res.length)
    if (res.length > 0) {
      createWarningModal({
        iconType: 'warning',
        title: '删除',
        content: '<span style="font-weight: bold;color: red;">[' + data.itemName + ']</span>项目大类中经已存在项目，不能进行删除操作！'
      })
      return false
    }
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据将不能恢复，你确认要删除吗?',
      onOk: async () => {
        const item1 = await findByTenantIdAndUniqueCode(dynamicTenantId.value,data.itemCode)
        if (item1.length>0) {
          item1[0].flag = '0'
          item1[0].acceptUser = ''
          item1[0].acceptDate = ''
          await save(item1[0])
        }
        await useRouteApi(deleteProjectItem, {schemaName: dynamicTenantId})(data)
        //删除项目大类下的所有项目分类
        if (pageParameter.projectClassCtl=='1') {
          await useRouteApi(deleteProClassByProjectCateCode, {schemaName: dynamicTenantId})(data.itemCode)
        }
        message.success('删除成功！')
        await reloadTable()
      },
      onCancel: () => {
        return false
      }
    })
  } else {//项目不管控
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据将不能恢复，你确认要删除吗?',
      onOk: async () => {
        const item1 = await findByTenantIdAndUniqueCode(dynamicTenantId.value,data.itemCode)
        if (item1.length>0) {
          item1[0].flag = '0'
          item1[0].acceptUser = ''
          item1[0].acceptDate = ''
          await save(item1[0])
        }
        await useRouteApi(deleteProjectItem, {schemaName: dynamicTenantId})(data)
        message.success('删除成功！')
        await reloadTable()
      },
      onCancel: () => {
        return false
      }
    })
  }
}
const openExcel = () => {
  if (pageParameter.projectClassCtl=='1') {
    if (cateList.value.length>0) {
      openExcelPage(true, {
        data: {},
        dynamicTenantId: dynamicTenantId.value,
        defaultAdName: defaultAdName.value
      })
    } else {
      createWarningModal({
        iconType: 'warning',
        title: '提示',
        content: '暂无项目栏目样式，请先添加项目栏目样式！'
      })
      return false
    }
  } else {
    openExcelPage(true, {
      data: {},
      dynamicTenantId: dynamicTenantId.value,
      defaultAdName: defaultAdName.value
    })
  }
}
//导入Excel
async function saveExcel(data:any) {
  await useRouteApi(excelProjectItem, {schemaName: dynamicTenantId})(data)
  if (pageParameter.projectClassCtl=='1'){
    if (data.id == null || data.id == "") {
      //添加项目分类
      const item = {
        uniqueCode: '',
        projectClassCode: '01',
        projectClassName: data.projectCateName,
        projectCateCode: data.itemCode,
        parentId: '0'
      }
      await useRouteApi(saveProClass, {schemaName: dynamicTenantId})(item)
    }
  }
  await reloadTable()
}

//文件导出
async function exportExcel() {
  // debugger
  const arrHeader = ['大类编码','大类名称'];
  const columnList = ['itemCode','itemName']
  const arrData = tableDataAll.value.map((item) => columnList.map(column=>item[column]));
  // console.log(arrHeader)
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: arrData,
    header: arrHeader,
    filename: '项目大类.xlsx',
  });
}

const activeKey = ref(false)
const cardList:any = ref([])
const tableDataAll:any = ref([])
async function reloadCurrentPage() {
  const res: any = await useRouteApi(findAllProjectItemList, {schemaName: dynamicTenantId})({})
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
onMounted(async() => {
  await reloadCurrentPage()
})
async function saveData(data:any) {
  await useRouteApi(saveProjectItem, {schemaName: dynamicTenantId})(data)
  await reloadTable()
}

async function editFlagData(data: any) {
  await useRouteApi(editByFlag, {schemaName: dynamicTenantId})(data);
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
    openEditPage(true, {
      data: checkRow.value[0],
      dynamicTenantId: dynamicTenantId.value,
      defaultAdName: defaultAdName.value,
      projectClassCtl: pageParameter.projectClassCtl,
      isState: '1'
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '修改',
      content: '请选择一条进行编辑！'
    })
  }
}

async function delList() {
  if (checkRow.value.length > 0) {
    if (pageParameter.projectClassCtl=='1'){//项目管控
      //判断大类下的是否有项目
      for (let i = 0; i < checkRow.value.length; i++) {
        const item = checkRow.value[i]
        const res: any = await useRouteApi(findAllList, {schemaName: dynamicTenantId})({itemCode:item.itemCode,projectClassCtl:pageParameter.projectClassCtl})
        if (res.length > 0) {
          createWarningModal({
            iconType: 'warning',
            title: '删除',
            content: '<span style="font-weight: bold;color: red;">[' + item.itemName + ']</span>项目大类中经已存在项目，不能进行删除操作！'
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
            await useRouteApi(deleteProjectItem, {schemaName: dynamicTenantId})(item)
            //删除项目大类下的所有项目分类
            await useRouteApi(deleteProClassByProjectCateCode, {schemaName: dynamicTenantId})(item.itemCode)
          }
          message.success('删除成功！')
          await reloadTable()
        },
        onCancel: () => {
          return false
        }
      })
    } else {//项目不管控
      createConfirm({
        iconType: 'error',
        title: '警告',
        content: '删除后数据将不能恢复，你确认要删除吗?',
        onOk: async () => {
          for (let i = 0; i < checkRow.value.length; i++) {
            const item = checkRow.value[i]
            await useRouteApi(deleteProjectItem, {schemaName: dynamicTenantId})(item)
          }
          message.success('删除成功！')
          await reloadTable()
        },
        onCancel: () => {
          return false
        }
      })
    }
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '删除',
      content: '请选择需要删除的内容！'
    })
    return false
  }
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

function onSearch() {
}

const pageParameter: any = reactive({
  showRulesSize: 'MIN',
  companyCode: '100',
  companyName: '北京希格科技',
  cateCode: '',
  ifUnit: false,
  projectClassCtl: '0'
})
const defaultPage = ref(false)
/*onMounted(async() => {
  // 进页面执行
  getThisAdInfoData({'accId': getCurrentAccountName(false)}).then(res => {
    if (null != res && res.independent == 0) {
      defaultPage.value = false
      pageParameter.companyName = res.accNameCn
      pageParameter.companyCode = res.coCode
      pageParameter.ifUnit = res.icorp == '1'
    }
  })
})*/

const cateList:any = ref([])
const dynamicAdReload = async (obj) => {
  // const dataBase:any = await findDataBase(obj.accId,obj.year)
  dynamicTenantId.value = obj.accountMode
  defaultAdName.value = obj.accId
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
  cateList.value = await useRouteApi(cateFindStateFlag, {schemaName: obj.accountMode})({})
  await reloadCurrentPage()
  if (activeKey.value == true) {
    let data: any = {}
    data.page = getPaginationRef().current
    data.size = getPaginationRef().pageSize
    let res: any = await useRouteApi(CrudApi.list, {schemaName: obj.accountMode})(data)
    setTableData([]) // 清空可能残留的数据
    setTableData(res.items)
    // 底部分页信息
    pageParameter.thisAdInfo = {}
    pageParameter.thisAdInfo = obj
    pageParameter.total = res.total
    setPagination({total: res.total})
  }
}

//引用
import Into from './popup/into.vue'
const [registerIntoPage, {openModal: openIntoPage}] = useModal()
const openInto= () => {
  openIntoPage(true, {
    dynamicTenantId: dynamicTenantId.value,
    defaultAdName: defaultAdName.value
  })
}

const loadMark = ref(false);
async function saveInto(data) {
  await reloadCurrentPage()
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

.abc:hover {
  color: blue; /*DiV背景颜色*/
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
  background-color: #f2f2f2 !important;
  border-color: #e5e7eb !important;
}
</style>
